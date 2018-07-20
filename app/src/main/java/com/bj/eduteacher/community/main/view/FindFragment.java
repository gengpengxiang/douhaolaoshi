package com.bj.eduteacher.community.main.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.andview.refreshview.XRefreshView;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.DoukeCommentActivity;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.community.details.model.ArticleDetail;
import com.bj.eduteacher.community.details.view.ArticleDetailActivity;
import com.bj.eduteacher.community.main.adapter.ArticleInfoAdapter;
import com.bj.eduteacher.community.main.adapter.SpacesItemDecoration;
import com.bj.eduteacher.community.main.model.ArticleInfo;
import com.bj.eduteacher.community.main.presenter.ArticlePresenter;
import com.bj.eduteacher.community.publish.view.PublishActivity;
import com.bj.eduteacher.community.utils.Base64Util;
import com.bj.eduteacher.integral.presenter.IntegralPresenter;
import com.bj.eduteacher.integral.view.IViewintegral;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.wxapi.IViewWXShare;
import com.bj.eduteacher.wxapi.WXSharePresenter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.NEWSBYID;

/**
 * Created by gpx on 2018/4/8 0008.
 */

public class FindFragment extends BaseFragment implements IArticleInfoView, IViewintegral, IViewWXShare {

    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    Unbinder unbinder;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    public static long lastRefreshTime;
    private IWXAPI api;

    private LinearLayoutManager layoutManager;
    private String teacherPhoneNumber, unionid;
    private ArticleInfoAdapter articleInfoAdapter;
    private ArticlePresenter articlePresenter;
    private IntegralPresenter integralPresenter;
    private WXSharePresenter wxSharePresenter;
    private ArrayList<ArticleInfo.DataBean> articlesInfoList = new ArrayList<>();
    private int pos = 10000;
    private int currentPage = 1;
    private ImageView iv_like_icon;
    private TextView tv_like_num, tv_comment_num;
    private String newsID;


    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_find, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        api = WXAPIFactory.createWXAPI(getActivity(), MLProperties.APP_DOUHAO_TEACHER_ID);
        articlePresenter = new ArticlePresenter(getActivity(), this);
        integralPresenter = new IntegralPresenter(getActivity(), this);
        wxSharePresenter = new WXSharePresenter(getActivity(), this);
        initDatas();
        initToolbar();
        initViews();
        unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        articlePresenter.getArticleInfo(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, ""), unionid,String.valueOf(currentPage));
        return view;

    }

    private void initDatas() {
        // 绑定别名
        teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, " ");
    }

    private void initViews() {

        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        articleInfoAdapter = new ArticleInfoAdapter(articlesInfoList);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(20));
        // mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mRecyclerView.setAdapter(articleInfoAdapter);
        articleInfoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                iv_like_icon = (ImageView) adapter.getViewByPosition(mRecyclerView, position, R.id.iv_like);
                tv_like_num = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_like);
                tv_comment_num = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_comment);
                newsID = articlesInfoList.get(position).getId();
                Intent intent = new Intent();
                intent.setClass(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("newsid", newsID);
                intent.putExtra("type", "FindFragment");
                intent.putExtra("authorName",articlesInfoList.get(position).getAuthor());
                startActivity(intent);


            }
        });
        articleInfoAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {

            @Override
            public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
                pos = position;
                unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");

                switch (view.getId()) {
                    case R.id.layout_like:
                        if (LoginStatusUtil.noLogin(getActivity())) {
                            IntentManager.toLoginSelectActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                            return;
                        }else {
                            iv_like_icon = (ImageView) adapter.getViewByPosition(mRecyclerView, position, R.id.iv_like);
                            tv_like_num = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_like);
                            articlePresenter.agree(articlesInfoList.get(position).getId(), teacherPhoneNumber, "3", unionid);
                        }
                        break;
                    case R.id.layout_comment:
                        tv_comment_num = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_comment);
                        newsID = articlesInfoList.get(position).getId();
                        Intent intent = new Intent(getActivity(), DoukeCommentActivity.class);
                        intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, articlesInfoList.get(position).getId());
                        intent.putExtra("type", "FindFragment");
                        startActivity(intent);
                        break;
                    case R.id.layout_money:
                        if (LoginStatusUtil.noLogin(getActivity())) {
                            IntentManager.toLoginSelectActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                            return;
                        }else {
                            String phoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
                            String fromunionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                            String tounionid = articlesInfoList.get(position).getUnionid();
                            articlePresenter.reward(phoneNumber, articlesInfoList.get(position).getUsercode(), fromunionid, tounionid);
                        }
                        break;
                    case R.id.layout_share:
                        String title;
                        if (Base64Util.checkBase64(articlesInfoList.get(position).getTitle())) {
                            title = Base64Util.decode(articlesInfoList.get(position).getTitle());
                        } else {
                            title = articlesInfoList.get(position).getTitle();
                        }
                        String unionid2 = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                        String content = Base64Util.decode(articlesInfoList.get(position).getOne_content().get(0).getContent());
                        if (articlesInfoList.get(position).getThree_content() != null) {
                            String img = articlesInfoList.get(position).getThree_content().get(0).getImg();
                            wxSharePresenter.share(teacherPhoneNumber, unionid2, title, content, img,0);
                        } else {
                            String img = "";
                            wxSharePresenter.share(teacherPhoneNumber, unionid2, title, content, img,0);
                        }

                        break;
                }
            }
        });
        mXRefreshView.setMoveForHorizontal(true);   // 在手指横向移动的时候，让XRefreshView不拦截事件
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(false);
        mXRefreshView.setEmptyView(R.layout.recycler_item_douke_empty);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                LL.i("刷新数据");
                lastRefreshTime = mXRefreshView.getLastRefreshTime();
                //cleanXRefreshView();

                currentPage = 1;
//                mXRefreshView.setPullLoadEnable(true);
                //articlePresenter.getArticleInfo(teacherPhoneNumber, String.valueOf(currentPage));
                teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
                unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                articlePresenter.refresh(teacherPhoneNumber, unionid);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                currentPage++;
                unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                articlePresenter.getArticleInfo(teacherPhoneNumber, unionid, String.valueOf(currentPage));
            }
        });

    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    @Override
    protected void bindViews(View view) {
    }

    @Override
    protected void processLogic() {
    }

    @Override
    protected void setListener() {
    }

    private void initToolbar() {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(R.string.bottom_tab_4));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onResume() {
        super.onResume();
        teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
        //articlePresenter.refresh(teacherPhoneNumber);
        Log.e("FindFragment", "onResume" + teacherPhoneNumber);
    }

    @Override
    public void getArticleInfoSuccess(List<ArticleInfo.DataBean> dataBeans) {
        cleanXRefreshView();
        articlesInfoList.clear();
        articlesInfoList.addAll(dataBeans);
        articleInfoAdapter.notifyDataSetChanged();
    }

    @Override
    public void getArticleInfoFail() {
        cleanXRefreshView();
        T.showShort(getActivity(), "没有更多数据了哦");
    }

    @Override
    public void refresh(List<ArticleInfo.DataBean> dataBeans) {
        Log.e("刷新", "true");
        cleanXRefreshView();
        articlesInfoList.clear();
        articlesInfoList.addAll(dataBeans);
        articleInfoAdapter.notifyDataSetChanged();

    }

    @Override
    public void refreshFail() {
//        mXRefreshView.setAutoLoadMore(false);
        Log.e("刷新失败", "false");
        cleanXRefreshView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("publishsuccess")) {
            if (event.getDoubisum().equals("find")) {
                currentPage = 1;
                String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                articlePresenter.getArticleInfo(teacherPhoneNumber, unionid,"1");
                integralPresenter.getDouBi("fabu", teacherPhoneNumber, "getdoubi", unionid);
            }
        }
        if (event.getAction().equals("detailagreesuccess")) {
            Log.e("细节页面已点赞", event.getBaseDataInfo().getData());
            BaseDataInfo dataInfo = event.getBaseDataInfo();
            if (dataInfo.getRet().equals("1")) {
                iv_like_icon.setImageResource(R.mipmap.ic_like2);
                tv_like_num.setText(String.valueOf(dataInfo.getData()));
            }
            if (dataInfo.getRet().equals("3")) {
                iv_like_icon.setImageResource(R.mipmap.ic_like);
                tv_like_num.setText(String.valueOf(dataInfo.getData()));
            }
        }
        if (event.getAction().equals("pinglunsuccess")) {

            if (event.getDoubisum().equals("FindFragment")) {
                Log.e("findFragment获得评论成功", "true");
                Log.e("newsid==", newsID + "usercode==" + teacherPhoneNumber);
                Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(final ObservableEmitter<String> e) throws Exception {
                        OkGo.<String>post(BASE_URL + NEWSBYID)
                                .params("appkey", MLConfig.HTTP_APP_KEY)
                                .params("newsid", newsID)
                                .params("usercode", teacherPhoneNumber)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        String str = response.body().toString();
                                        Log.e("find评论返回数据", str);

                                        ArticleDetail articledetail = JSON.parseObject(str, new TypeReference<ArticleDetail>() {
                                        });
                                        ArticleDetail.DataBean data = articledetail.getData();
                                        String num = data.getNews_info().getComment_num();
                                        Log.e("数量", num + "===");

                                        e.onNext(num);
                                        e.onComplete();


                                    }
                                });
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(String s) {
                                tv_comment_num.setText(s);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        });
            }
        }
        if (event.getAction().equals("loginout")) {
            currentPage = 1;
            teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
            unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
            articlePresenter.refresh("", unionid);
        }
        if (event.getAction().equals("phoneloginsuccess")) {
            currentPage = 1;
            teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
            unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
            articlePresenter.refresh(teacherPhoneNumber, unionid);
        }
        if (event.getAction().equals("wxloginsuccess")) {
            currentPage = 1;
            teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
            unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
            articlePresenter.refresh(teacherPhoneNumber, unionid);
        }
        if (event.getAction().equals("groupupdate")) {

            currentPage = 1;
            articlePresenter.refresh(teacherPhoneNumber, unionid);
        }
    }

    @Override
    public void loadMore(List<ArticleInfo.DataBean> dataBeans) {
        Log.e("加载更多", "true");
        cleanXRefreshView();
        articlesInfoList.addAll(dataBeans);
        articleInfoAdapter.notifyDataSetChanged();
    }

    @Override
    public void agree(BaseDataInfo dataInfo) {

        if (dataInfo.getRet().equals("1")) {
            Glide.with(mContext).load(R.mipmap.ic_like2).into(iv_like_icon);
            tv_like_num.setText(String.valueOf(dataInfo.getData()));
        }
        if (dataInfo.getRet().equals("3")) {
            Glide.with(mContext).load(R.mipmap.ic_like).into(iv_like_icon);
            tv_like_num.setText(String.valueOf(dataInfo.getData()));
        }
    }

    @Override
    public void reward(String result) {
        T.showShort(getActivity(), result);
    }

    @OnClick(R.id.bt_find_edit)
    public void onClick() {

        if (LoginStatusUtil.noLogin(getActivity())) {
            IntentManager.toLoginSelectActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
            return;
        } else {
            Intent intent = new Intent(getActivity(), PublishActivity.class);
            intent.putExtra("type", "find");
            startActivity(intent);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("FindFragmentonDestroy()", "onDestroy()");
        EventBus.getDefault().unregister(this);

        integralPresenter.onDestory();
        articlePresenter.onDestory();
        wxSharePresenter.onDestory();
        unbinder.unbind();

    }

   /* @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUI(MsgEvent event) {
        if (event.getAction().equals("publishsuccess")) {
            if (event.getDoubisum().equals("find")) {
                currentPage = 1;
                articlePresenter.getArticleInfo(teacherPhoneNumber, unionid,"1");
                String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID,"");

                integralPresenter.getDouBi("fabu", teacherPhoneNumber, "getdoubi",unionid);
            }
        }
    }*/

    @Override
    public void getDouBi(com.bj.eduteacher.integral.model.Doubi doubi) {
        EventBus.getDefault().post(new MsgEvent("getdoubisuccess", doubi.getData().getUser_doubinum_sum()));
    }

    @Override
    public void share() {
        //T.showShort(getActivity(),"分享了哦");
    }


}
