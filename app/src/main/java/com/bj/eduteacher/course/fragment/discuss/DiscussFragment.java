package com.bj.eduteacher.course.fragment.discuss;

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
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.course.fragment.detail.CourseInfo;
import com.bj.eduteacher.course.fragment.discuss.comment.DiscussCommentActivity;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.community.details.model.ArticleDetail;
import com.bj.eduteacher.community.main.adapter.ArticleInfoAdapter;
import com.bj.eduteacher.community.main.adapter.SpacesItemDecoration;
import com.bj.eduteacher.community.main.model.ArticleInfo;
import com.bj.eduteacher.community.main.presenter.ArticlePresenter;
import com.bj.eduteacher.community.main.view.IArticleInfoView;
import com.bj.eduteacher.community.utils.Base64Util;
import com.bj.eduteacher.integral.model.Doubi;
import com.bj.eduteacher.integral.presenter.IntegralPresenter;
import com.bj.eduteacher.integral.view.IViewintegral;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.wxapi.IViewWXShare;
import com.bj.eduteacher.wxapi.WXSharePresenter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
 * Created by Administrator on 2018/5/2 0002.
 */

public class DiscussFragment extends BaseFragment implements IArticleInfoView, IViewintegral, IViewWXShare {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private ArticleInfoAdapter adapter;
    private List<ArticleInfo.DataBean> list = new ArrayList<>();
    private String courseID;

    private ArticlePresenter articlePresenter;
    private IntegralPresenter integralPresenter;
    private WXSharePresenter wxSharePresenter;
    private int pos = 10000;
    private int currentPage = 1;
    private ImageView iv_like_icon;
    private TextView tv_like_num, tv_comment_num;
    private String newsID;
    private int listSize = 0;
    private String unionid;
    private String dianzanstatus = "";
    private String courseJiakeStatus = "";

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

        courseID = getActivity().getIntent().getStringExtra("CourseID");
        courseJiakeStatus = getActivity().getIntent().getStringExtra("CourseJiakeStatus");

        unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));
        adapter = new ArticleInfoAdapter(list);
//        adapter.setEmptyView(R.layout.recycler_item_comment_empty, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                newsID = list.get(position).getId();
                Intent intent = new Intent();
                intent.setClass(getActivity(), DiscussCommentActivity.class);
                intent.putExtra("newsid", newsID);
                intent.putExtra("type", "DiscussFragment");

                //add
                ArticleInfo.DataBean bean = list.get(position);
                //add
                intent.putExtra("courseID", courseID);

                if (StringUtils.isEmpty(dianzanstatus)) {
                    intent.putExtra("dianzanstatus", bean.getNews_dianzanstatus());
                } else {
                    intent.putExtra("dianzanstatus", dianzanstatus);
                }
                intent.putExtra("jiakestatus", courseJiakeStatus);

                intent.putExtra("id", bean.getId());
                intent.putExtra("touxiang", bean.getAuthorimg_url());
                intent.putExtra("name", bean.getAuthor());
                intent.putExtra("time", bean.getCreatetime());
                intent.putExtra("dianzan", bean.getDianzan());
                intent.putExtra("huifu", bean.getComment_num());
                //intent.putExtra("title", Base64Util.decode(bean.getTitle()));

                if (Base64Util.checkBase64(bean.getTitle())) {
                    intent.putExtra("title", Base64Util.decode(bean.getTitle()));
                } else {
                    intent.putExtra("title", bean.getTitle());
                }

//                intent.putExtra("title", bean.getTitle());
                intent.putExtra("content", Base64Util.decode(bean.getOne_content().get(0).getContent()));

                startActivity(intent);

                iv_like_icon = (ImageView) adapter.getViewByPosition(mRecyclerView, position, R.id.iv_like);
                tv_like_num = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_like);
                tv_comment_num = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_comment);
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {

            @Override
            public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
                pos = position;


                    switch (view.getId()) {
                        case R.id.layout_like:
                            if (LoginStatusUtil.noLogin(getActivity())) {
                                IntentManager.toLoginSelectActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                                return;
                            }else {
                                if (courseJiakeStatus.equals("1")) {
                                    iv_like_icon = (ImageView) adapter.getViewByPosition(mRecyclerView, position, R.id.iv_like);
                                    tv_like_num = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_like);
                                    articlePresenter.agree(list.get(position).getId(), PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), "3", unionid);
                                }else {
                                    T.showShort(getActivity(),"您还未加入课程");
                                }

                            }

                            break;
                        case R.id.layout_comment:
                            tv_comment_num = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_comment);
                            newsID = list.get(position).getId();
                            Intent intent = new Intent(getActivity(), DiscussCommentActivity.class);
                            intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, list.get(position).getId());
                            intent.putExtra("type", "DiscussFragment");
                            //add
                            ArticleInfo.DataBean bean = list.get(position);

                            //add
                            intent.putExtra("courseID", courseID);

                            if (StringUtils.isEmpty(dianzanstatus)) {
                                intent.putExtra("dianzanstatus", bean.getNews_dianzanstatus());
                            } else {
                                intent.putExtra("dianzanstatus", dianzanstatus);
                            }

                            if (Base64Util.checkBase64(bean.getTitle())) {
                                intent.putExtra("title", Base64Util.decode(bean.getTitle()));
                            } else {
                                intent.putExtra("title", bean.getTitle());
                            }

                            intent.putExtra("id", bean.getId());
                            intent.putExtra("touxiang", bean.getAuthorimg_url());
                            intent.putExtra("name", bean.getAuthor());
                            intent.putExtra("time", bean.getCreatetime());
                            intent.putExtra("dianzan", bean.getDianzan());
                            intent.putExtra("huifu", bean.getComment_num());
                            //intent.putExtra("title", bean.getTitle());
                            intent.putExtra("content", Base64Util.decode(bean.getOne_content().get(0).getContent()));

                            startActivity(intent);
                            break;
                        case R.id.layout_money:
                            if (LoginStatusUtil.noLogin(getActivity())) {
                                IntentManager.toLoginSelectActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                                return;
                            }else {
                                if (courseJiakeStatus.equals("1")) {
                                    String phoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
                                    String fromunionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                                    String tounionid = list.get(position).getUnionid();
                                    articlePresenter.reward(phoneNumber, list.get(position).getUsercode(), fromunionid, tounionid);

                                }else {
                                    T.showShort(getActivity(),"您还未加入课程");
                                }
                                 }
                            break;
                        case R.id.layout_share:
                            String title = "";
                            String img = "";
                            if(Base64Util.checkBase64(list.get(position).getTitle())){
                                title = Base64Util.decode(list.get(position).getTitle());
                            }else {
                                title = list.get(position).getTitle();
                            }

                            unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");

                            String content = Base64Util.decode(list.get(position).getOne_content().get(0).getContent());
                            if (list.get(position).getThree_content() != null) {
                                img = list.get(position).getThree_content().get(0).getImg();
                                wxSharePresenter.share(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, title, content, img,0);
                            } else {
                                img = "";
                                wxSharePresenter.share(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, title, content, img,0);
                            }
                            break;
                    }



            }
        });
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (listSize < 10) {
                            adapter.loadMoreEnd(true);
                        } else {
                            currentPage++;
                            getDiscussList(String.valueOf(currentPage), unionid);
                        }

                    }
                }, 500);
            }
        }, mRecyclerView);
        adapter.disableLoadMoreIfNotFullPage(mRecyclerView);

        articlePresenter = new ArticlePresenter(getActivity(), this);
        integralPresenter = new IntegralPresenter(getActivity(), this);
        wxSharePresenter = new WXSharePresenter(getActivity(), this);

        getDiscussList("1", unionid);
        getCourseInfo(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, courseID);

        return view;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        integralPresenter.onDestory();
        articlePresenter.onDestory();
        wxSharePresenter.onDestory();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void getArticleInfoSuccess(List<ArticleInfo.DataBean> dataBeans) {
        list.clear();
        list.addAll(dataBeans);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getArticleInfoFail() {
        T.showShort(getActivity(), "没有更多数据了哦");
    }

    @Override
    public void refresh(List<ArticleInfo.DataBean> dataBeans) {
        Log.e("刷新", "true");
        list.clear();
        list.addAll(dataBeans);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void refreshFail() {
//        mXRefreshView.setAutoLoadMore(false);
        Log.e("刷新失败", "false");

    }

    @Override
    public void loadMore(List<ArticleInfo.DataBean> dataBeans) {
        Log.e("加载更多", "true");
        list.addAll(dataBeans);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void agree(BaseDataInfo dataInfo) {

        if (dataInfo.getRet().equals("1")) {
            Glide.with(mContext).load(R.mipmap.ic_like2).into(iv_like_icon);
            tv_like_num.setText(String.valueOf(dataInfo.getData()));

            dianzanstatus = "1";
        }
        if (dataInfo.getRet().equals("3")) {
            Glide.with(mContext).load(R.mipmap.ic_like).into(iv_like_icon);
            tv_like_num.setText(String.valueOf(dataInfo.getData()));

            dianzanstatus = "0";
        }
    }

    @Override
    public void reward(String result) {
        T.showShort(getActivity(), result);
    }

    @Override
    public void getDouBi(Doubi doubi) {
        EventBus.getDefault().post(new MsgEvent("getdoubisuccess", doubi.getData().getUser_doubinum_sum()));
    }

    @Override
    public void share() {
        //T.showShort(getActivity(),"分享了哦");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("discussagreesuccess")) {
            Log.e("细节页面已点赞", event.getBaseDataInfo().getData());
            BaseDataInfo dataInfo = event.getBaseDataInfo();
            if (dataInfo.getRet().equals("1")) {
                //Glide.with(getActivity()).load(R.mipmap.ic_like2).into(iv_like_icon);
                iv_like_icon.setImageResource(R.mipmap.ic_like2);

                tv_like_num.setText(String.valueOf(dataInfo.getData()));
            }
            if (dataInfo.getRet().equals("3")) {
                //Glide.with(getActivity()).load(R.mipmap.ic_like).into(iv_like_icon);
                iv_like_icon.setImageResource(R.mipmap.ic_like);
                tv_like_num.setText(String.valueOf(dataInfo.getData()));
            }
        }
        if (event.getAction().equals("discusspinglunsuccess")) {
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(final ObservableEmitter<String> e) throws Exception {
                    OkGo.<String>post(BASE_URL + NEWSBYID)
                            .params("appkey", MLConfig.HTTP_APP_KEY)
                            .params("newsid", newsID)
                            .params("usercode", PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID))
                            .params("unionid", unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID))
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    String str = response.body().toString();
                                    Log.e("DiscussFragment评论返回数据", str);
                                    ArticleDetail articledetail = JSON.parseObject(str, new TypeReference<ArticleDetail>() {
                                    });
                                    ArticleDetail.DataBean data = articledetail.getData();
                                    if (articledetail.getRet().equals("1")) {
                                        e.onNext(data.getNews_info().getComment_num());
                                        e.onComplete();
                                    }

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
        if (event.getAction().equals("refreshpage")) {
            unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
            currentPage = 1;
            if (event.getNum() == 2) {
                list.clear();
                dianzanstatus = "";
                getDiscussList("1", unionid);
            }
        }
        if (event.getAction().equals("dianzanstatus")) {
            dianzanstatus = event.getDoubisum();
        }
        if (event.getAction().equals("courseStudyBuySuccess")) {
            getCourseInfo(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, courseID);
        }
        if (event.getAction().equals("discussPublishSuccess")) {
            list.clear();
            getDiscussList("1", unionid);
            String phoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, " ");
            integralPresenter.getDouBi("fabu", phoneNumber, "getdoubi", unionid);
        }
    }

    private void getDiscussList(final String pageIndex, final String unionid) {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo.DataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<ArticleInfo.DataBean>> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/kechenghuati")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("limit", "10")
                        .params("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * 10))
                        .params("usercode", PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID))
                        .params("kechengid", courseID)
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("课程研讨", str);
                                ArticleInfo articleInfo = JSON.parseObject(str, new TypeReference<ArticleInfo>() {
                                });
                                //list = articleInfo.getData();
                                e.onNext(articleInfo.getData());
                                e.onComplete();
                            }

                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ArticleInfo.DataBean> dataBeans) {
                        for (int i = 0; i < dataBeans.size(); i++) {
                            if (dataBeans.get(i).getThree_content() == null) {
                                dataBeans.get(i).setType(1);
                            } else {
                                if (dataBeans.get(i).getThree_content().size() < 3) {
                                    dataBeans.get(i).setType(2);
                                }
                                if (dataBeans.get(i).getThree_content().size() > 2) {
                                    dataBeans.get(i).setType(3);
                                }

                            }

                        }
                        listSize = dataBeans.size();
                        list.addAll(dataBeans);
                        adapter.setNewData(list);
                        adapter.loadMoreComplete();

                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.loadMoreComplete();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getCourseInfo(final String phone, final String unionid, final String kechengid) {
        Observable.create(new ObservableOnSubscribe<CourseInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<CourseInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/kecheng")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("phone", phone)
                        .params("kechengid", kechengid)
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                //Log.e("课程信息", str);
                                CourseInfo courseInfo = JSON.parseObject(str, new TypeReference<CourseInfo>() {
                                });
                                e.onNext(courseInfo);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CourseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CourseInfo courseInfo) {
                        courseJiakeStatus = courseInfo.getData().getJiakestatus();

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
