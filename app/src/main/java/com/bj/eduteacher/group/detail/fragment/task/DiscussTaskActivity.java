package com.bj.eduteacher.group.detail.fragment.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.community.details.model.ArticleDetail;
import com.bj.eduteacher.community.utils.Base64Util;
import com.bj.eduteacher.course.fragment.discuss.comment.CommentAdapter;
import com.bj.eduteacher.course.fragment.discuss.comment.CommentPresenter;
import com.bj.eduteacher.course.fragment.discuss.comment.IViewComment;
import com.bj.eduteacher.course.fragment.discuss.comment.NewCommentInfo;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.integral.model.Doubi;
import com.bj.eduteacher.integral.presenter.IntegralPresenter;
import com.bj.eduteacher.integral.view.IViewintegral;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.wxapi.IViewWXShare;
import com.bj.eduteacher.wxapi.WXSharePresenter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.MyApplication.getContext;
import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.NEWSBYID;


public class DiscussTaskActivity extends BaseActivity implements IViewComment, IViewintegral, IViewWXShare {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.header_tv_title)
    TextView headerTvTitle;
    @BindView(R.id.header_img_back)
    ImageView headerImgBack;

    TextView tvContent, tvAuthorName, tvCreateTime, tvDianzan, tvHuifu, tvTitle;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.bt_agree)
    LinearLayout btAgree;
    @BindView(R.id.bt_share)
    LinearLayout btShare;
    @BindView(R.id.iv_like)
    ImageView ivLike;
    @BindView(R.id.mSmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private Unbinder unbinder;
    private String id, touxiang, name, time, dianzan, huifu, title, content;
    private SimpleDraweeView authorPhoto;

    private CommentAdapter adapter;
    private List<NewCommentInfo.DataBean> list = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private CommentPresenter commentPresenter;
    private IntegralPresenter integralPresenter;

    private int currentPage = 1;
    private View headView;
    private String phoneNumber;
    private String unionid;
    private WXSharePresenter wxSharePresenter;

    private String dianzanstatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss_comment);
        unbinder = ButterKnife.bind(this);
        phoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }

        initDatas();

        initViews();
        commentPresenter = new CommentPresenter(this, this);
        commentPresenter.getCommentList(id, String.valueOf(currentPage));
        integralPresenter = new IntegralPresenter(this, this);
        wxSharePresenter = new WXSharePresenter(this, this);

        getArticleDetail();
    }

    private void initDatas() {
        headerTvTitle.setVisibility(View.VISIBLE);
        headerImgBack.setVisibility(View.VISIBLE);
        headerTvTitle.setText("研讨详情");

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        touxiang = intent.getStringExtra("img");
        name = intent.getStringExtra("nicheng");
        time = intent.getStringExtra("time");
    }

    private void getArticleDetail() {
        Observable.create(new ObservableOnSubscribe<ArticleDetail.DataBean>() {
            @Override
            public void subscribe(final ObservableEmitter<ArticleDetail.DataBean> e) throws Exception {
                OkGo.<String>post(BASE_URL + NEWSBYID)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("newsid", id)
                        .params("usercode", phoneNumber)
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("文章细节返回数据", str);

                                ArticleDetail articledetail = JSON.parseObject(str, new TypeReference<ArticleDetail>() {
                                });

                                ArticleDetail.DataBean data = articledetail.getData();

                                e.onNext(data);
                                e.onComplete();
                            }
                        });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleDetail.DataBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ArticleDetail.DataBean dataBeans) {
//                        touxiang = BASE_RESOURCE_URL + dataBeans.getNews_info().getAuthorimg();
//                        name = dataBeans.getNews_info().getAuthor();
//                        time = dataBeans.getNews_info().getTime();
                        dianzan = dataBeans.getNews_info().getDianzan();
                        huifu = dataBeans.getNews_info().getComment_num();
                        String title0 = dataBeans.getNews_info().getTitle();
                        if (Base64Util.checkBase64(title0)) {
                            title = Base64Util.decode(title0);
                        } else {
                            title = title0;
                        }

//                        content = dataBeans.getNews_info().getContent();
                        dianzanstatus = dataBeans.getNews_dianzanstatus();

                        if (dataBeans.getNews_content() != null) {
                            content = dataBeans.getNews_content().get(0).getContent();
                            tvContent.setText(Base64Util.decode(content));
                        }

                        authorPhoto.setImageURI(touxiang);
                        tvAuthorName.setText(name);
                        tvCreateTime.setText(time);
                        tvDianzan.setText("点赞" + dianzan);
                        tvHuifu.setText("回复" + huifu);
                        tvTitle.setText(title);
//                        tvContent.setText(content);

                        mSmartRefreshLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initViews() {
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(R.layout.recycler_item_comment_info, list);
        headView = getLayoutInflater().inflate(R.layout.recycler_discuss_comment_headview, null);
        adapter.addHeaderView(headView);
        mRecyclerView.setAdapter(adapter);
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        adapter.disableLoadMoreIfNotFullPage(mRecyclerView);

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage++;
                        commentPresenter.getCommentList(id, String.valueOf(currentPage));
                        //                       }
                    }
                }, 500);
            }
        }, mRecyclerView);

        authorPhoto = (SimpleDraweeView) headView.findViewById(R.id.iv_authorPhoto);
        tvContent = (TextView) headView.findViewById(R.id.tv_content);
        tvAuthorName = (TextView) headView.findViewById(R.id.tv_authorName);
        tvCreateTime = (TextView) headView.findViewById(R.id.tv_createTime);
        tvDianzan = (TextView) headView.findViewById(R.id.tv_dianzan);
        tvHuifu = (TextView) headView.findViewById(R.id.tv_huifu);
        tvTitle = (TextView) headView.findViewById(R.id.tv_title);
//        authorPhoto.setImageURI(touxiang);
//        tvAuthorName.setText(name);
//        tvCreateTime.setText(time);
//        tvDianzan.setText("点赞" + dianzan);
//        tvHuifu.setText("回复" + huifu);
//        tvTitle.setText(title);
//        tvContent.setText(content);

        final View rootView = findViewById(R.id.layout_root);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                rootView.getWindowVisibleDisplayFrame(r);

                final int heightDiff = rootView.getRootView().getHeight() - (r.bottom - r.top);
                if (heightDiff > 200) { // if more than 100 pixels, its probably a keyboard...
                    tvSend.setVisibility(View.VISIBLE);
                    btAgree.setVisibility(View.GONE);
                    btShare.setVisibility(View.GONE);
                } else if (heightDiff < 200) {
                    tvSend.setVisibility(View.GONE);
                    btAgree.setVisibility(View.VISIBLE);
                    btShare.setVisibility(View.VISIBLE);
                }
            }
        });


        if (dianzanstatus.equals("1")) {
            ivLike.setImageResource(R.mipmap.ic_liked);
        }
        if (dianzanstatus.equals("0")) {
            ivLike.setImageResource(R.mipmap.ic_like);
        }

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                currentPage = 1;
//                list.clear();
                commentPresenter.getCommentList(id, String.valueOf(currentPage));
                mSmartRefreshLayout.finishRefresh(500);
            }
        });

        mSmartRefreshLayout.setEnableOverScrollDrag(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        commentPresenter.onDestory();
        integralPresenter.onDestory();
    }

    @Override
    public void getCommentSuccess(List<NewCommentInfo.DataBean> commentInfoList) {
        edtContent.setText("");
        if (currentPage == 1) {
            list.clear();
        }
        list.addAll(commentInfoList);
        adapter.setNewData(list);

        if (commentInfoList.size() < 10) {
            adapter.loadMoreEnd(true);
        }

    }

    @Override
    public void getCommentFail() {
        adapter.loadMoreEnd(true);
    }

    @Override
    public void sendCommentSuccess() {
        processSoftKeyBoard(false);
        list.clear();
        currentPage = 1;
        commentPresenter.getCommentList(id, "1");
        integralPresenter.getDouBi("pinglun", phoneNumber, "getdoubi", unionid);
    }

    @Override
    public void sendCommentFail() {

    }


    @Override
    public void agree(BaseDataInfo dataInfo) {

        if (dataInfo.getRet().equals("1")) {
            ivLike.setImageResource(R.mipmap.ic_liked);
            EventBus.getDefault().post(new MsgEvent("dianzanstatus", "1"));
        }
        if ((dataInfo.getRet().equals("3"))) {
            ivLike.setImageResource(R.mipmap.ic_like);
            EventBus.getDefault().post(new MsgEvent("dianzanstatus", "0"));
        }
        EventBus.getDefault().post(new MsgEvent("discussagreesuccess", dataInfo));
    }

    /**
     * 隐藏或者显示软键盘
     *
     * @param isShow true:显示，false:隐藏
     */
    public void processSoftKeyBoard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            imm.showSoftInput(edtContent, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(edtContent.getWindowToken(), 0);
        }
    }

    @Override
    public void getDouBi(Doubi doubi) {

    }

    @OnClick({R.id.header_img_back, R.id.tv_send, R.id.bt_agree, R.id.bt_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_img_back:
                finish();
                break;
            case R.id.tv_send:
                if (!StringUtils.isEmpty(edtContent.getText().toString())) {
                    String etContent = edtContent.getText().toString().trim();
                    commentPresenter.sendCommentContent(id, Base64Util.encode(etContent), phoneNumber, unionid);
                } else {
                    T.showShort(this, "评论内容不能为空");
                }

                break;
            case R.id.bt_agree:
                commentPresenter.agree(id, phoneNumber, "3", unionid);
                break;
            case R.id.bt_share:
                wxSharePresenter.share(phoneNumber, unionid, title, Base64Util.decode(content), "", 0);
                break;
        }
    }

    @Override
    public void share() {
    }

}
