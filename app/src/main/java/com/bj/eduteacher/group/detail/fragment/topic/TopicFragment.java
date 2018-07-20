package com.bj.eduteacher.group.detail.fragment.topic;

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
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
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
import com.bj.eduteacher.community.main.view.IArticleInfoView;
import com.bj.eduteacher.community.utils.Base64Util;
import com.bj.eduteacher.integral.model.Doubi;
import com.bj.eduteacher.integral.presenter.IntegralPresenter;
import com.bj.eduteacher.integral.view.IViewintegral;
import com.bj.eduteacher.manager.IntentManager;
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
import static com.bj.eduteacher.api.Urls.GROUPHUATI;
import static com.bj.eduteacher.api.Urls.NEWSBYID;

/**
 * Created by Administrator on 2018/5/2 0002.
 */

public class TopicFragment extends BaseFragment implements IArticleInfoView, IViewintegral, IViewWXShare {


    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    /*@BindView(R.id.bt_group_edit)
    Button btGroupEdit;*/
    //private String teacherPhoneNumber;
    private ArticleInfoAdapter adapter;
    private List<ArticleInfo.DataBean> list = new ArrayList<>();
    private String groupid;

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

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);
        unbinder = ButterKnife.bind(this, view);

//        groupid = getActivity().getIntent().getStringExtra("groupid");
        if (getActivity().getIntent().getStringExtra("groupid") != null) {
            groupid = getActivity().getIntent().getStringExtra("groupid");
        } else {
            groupid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID, "");
        }
        //teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
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
                intent.setClass(getActivity(), ArticleDetailActivity.class);
                intent.putExtra("newsid", newsID);
                intent.putExtra("type", "TopicFragment");
                intent.putExtra("authorName",list.get(position).getAuthor());
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
                        } else {
                            iv_like_icon = (ImageView) adapter.getViewByPosition(mRecyclerView, position, R.id.iv_like);
                            tv_like_num = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_like);
                            articlePresenter.agree(list.get(position).getId(), PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), "3", unionid);
                        }
                        break;
                    case R.id.layout_comment:
                        tv_comment_num = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_comment);
                        newsID = list.get(position).getId();
                        Intent intent = new Intent(getActivity(), DoukeCommentActivity.class);
                        intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, list.get(position).getId());
                        intent.putExtra("type", "TopicFragment");
                        startActivity(intent);
                        break;
                    case R.id.layout_money:
                        if (LoginStatusUtil.noLogin(getActivity())) {
                            IntentManager.toLoginSelectActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                            return;
                        } else {
                            String phoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
                            String fromunionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                            String tounionid = list.get(position).getUnionid();
                            articlePresenter.reward(phoneNumber, list.get(position).getUsercode(), fromunionid, tounionid);

                        }

                        break;
                    case R.id.layout_share:
                        String title = "";
                        String img = "";

                        if (Base64Util.checkBase64(list.get(position).getTitle())) {
                            title = Base64Util.decode(list.get(position).getTitle());
                        } else {
                            title = list.get(position).getTitle();
                        }

                        unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                        String content = Base64Util.decode(list.get(position).getOne_content().get(0).getContent());
                        if (list.get(position).getThree_content() != null) {
                            img = list.get(position).getThree_content().get(0).getImg();
                            wxSharePresenter.share(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, title, content, img, 0);
                        } else {
                            img = "";
                            wxSharePresenter.share(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, title, content, img, 0);
                        }
                        break;
                }

            }
        });
        adapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
//                mRecyclerView.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (listSize < 10) {
//                            adapter.loadMoreEnd(true);
//                        } else {
//                            currentPage++;
//                            getTopicList(String.valueOf(currentPage), unionid, groupid);
//                        }
//                    }
//                }, 500);

                if (listSize < 10) {
                    adapter.loadMoreEnd(true);
                } else {
                    currentPage++;
                    getTopicList(String.valueOf(currentPage), unionid, groupid);
                }
            }
        }, mRecyclerView);



        articlePresenter = new ArticlePresenter(getActivity(), this);
        integralPresenter = new IntegralPresenter(getActivity(), this);
        wxSharePresenter = new WXSharePresenter(getActivity(), this);

        getTopicList("1", unionid, groupid);

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
        int num = Integer.parseInt(list.get(pos).getDianzan());

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
            if (event.getDoubisum().equals("TopicFragment")) {
                Log.e("topFragment获得评论成功", "true");

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
                                        Log.e("TopicFragment评论返回数据", str);
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

        }
        if (event.getAction().equals("publishsuccess")) {
            if (!event.getDoubisum().equals("find")) {
                list.clear();
                currentPage = 1;
                getTopicList("1", unionid, groupid);
                String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                integralPresenter.getDouBi("fabu", PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), "getdoubi", PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID));
            }
        }
        if (event.getAction().equals("refreshPage")) {
            unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
            currentPage = 1;

                list.clear();
                getTopicList("1", unionid, groupid);

        }
        if (event.getAction().equals("groupid")) {
            groupid = event.getDoubisum();
            list.clear();
            getTopicList("1", unionid, groupid);
        }
//        if (event.getAction().equals("wxloginsuccess")) {
//            currentPage = 1;
//            list.clear();
//            groupid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID, "");
//            getTopicList("1", unionid, groupid);
//        }
    }

    private void getTopicList(final String pageIndex, final String unionid, final String id) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL + GROUPHUATI)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("limit", "10")
                        .params("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * 10))
                        .params("usercode", PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID))
                        .params("groupid", id)
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("小组话题", str);
//                                ArticleInfo articleInfo = JSON.parseObject(str, new TypeReference<ArticleInfo>() {
//                                });
                                //list = articleInfo.getData();
                                e.onNext(str);
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

                        JSONObject jsonObject = JSON.parseObject(s);
                        String ret = (String) jsonObject.get("ret");

                        if(ret.equals("2")){
                            adapter.loadMoreComplete();
                            adapter.loadMoreEnd(true);
                        }
                        if(ret.equals("1")){
                            ArticleInfo articleInfo = JSON.parseObject(s, new TypeReference<ArticleInfo>() {});
                            List<ArticleInfo.DataBean> dataBeans = articleInfo.getData();

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
                    }
                    @Override
                    public void onError(Throwable e) {
                        adapter.loadMoreComplete();
                    }

                    @Override
                    public void onComplete() {
                        adapter.loadMoreComplete();
                    }
                });
    }

}
