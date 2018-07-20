package com.bj.eduteacher.community.details.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.DoukeCommentActivity;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.community.details.adapter.ArticleDetailAdapter;
import com.bj.eduteacher.community.details.model.ArticleDetail;
import com.bj.eduteacher.community.details.presenter.ArticleDetailPresenter;
import com.bj.eduteacher.community.utils.Base64Util;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.widget.MyDividerItemDecoration;
import com.bj.eduteacher.wxapi.IViewWXShare;
import com.bj.eduteacher.wxapi.WXSharePresenter;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ArticleDetailActivity extends BaseActivity implements IArticleDetailView, IViewWXShare {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.header_tv_title)
    TextView headerTvTitle;
    @BindView(R.id.header_img_back)
    ImageView headerImgBack;
    @BindView(R.id.header_ll_left)
    LinearLayout headerLlLeft;
    @BindView(R.id.iv_like)
    ImageView ivLike;
    @BindView(R.id.tv_like)
    TextView tvLike;
    @BindView(R.id.tv_comment)
    TextView tvComment;
    @BindView(R.id.layout_bottom)
    LinearLayout layoutBottom;
    private ArticleDetailAdapter adapter;
    private LinearLayoutManager layoutManager;
    private List<ArticleDetail.DataBean.NewsContentBean> list = new ArrayList<>();
    private ArticleDetailPresenter presenter;
    private WXSharePresenter wxSharePresenter;
    private View headView;
    private TextView tvhead,tvheadtime,tvheadname;
    private String newsid = "";
    private String authorPhoneNumber, dianzanStatus, dianzanNum;
    private int status = 0;
    private Unbinder unbind;
    private String type, unionid;
    private String title, content, img;

    private String tounionid;
    private String authorName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        unbind = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        //teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        //Log.e("文章细节onCreate（）",teacherPhoneNumber+"");
        initViews();
        Intent intent = getIntent();
        newsid = intent.getStringExtra("newsid");
        type = intent.getStringExtra("type");
        authorName = intent.getStringExtra("authorName");

        Log.e("传递过来的id", newsid);
        presenter = new ArticleDetailPresenter(this, this);
        wxSharePresenter = new WXSharePresenter(this, this);
        presenter.getArticleDetail(newsid, PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID), unionid);
        headView = getLayoutInflater().inflate(R.layout.layout_recycler_headview, null);
        tvhead = (TextView) headView.findViewById(R.id.tv_head);
        tvheadtime = (TextView) headView.findViewById(R.id.tv_head_time);
        tvheadname = (TextView) headView.findViewById(R.id.tv_head_name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }
    }

    private void initViews() {
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new ArticleDetailAdapter(R.layout.recycler_item_article_detail, list);
        mRecyclerView.setAdapter(adapter);

        headerTvTitle.setVisibility(View.VISIBLE);
        headerTvTitle.setText("文章详情");
        headerImgBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "onDestroy");
        presenter.onDestory();
        wxSharePresenter.onDestory();
        unbind.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUI(MsgEvent event) {
        if (event.getAction().equals("phoneloginsuccess")) {
            presenter.getArticleDetail(newsid, PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID), unionid);
        }
    }

    @Override
    public void getArticleDetailSuccess(ArticleDetail.DataBean dataBeans) {

        tounionid = dataBeans.getNews_info().getWeixin_unionid();

        if (Base64Util.checkBase64(dataBeans.getNews_info().getTitle())) {
            title = Base64Util.decode(dataBeans.getNews_info().getTitle());
        } else {
            title = dataBeans.getNews_info().getTitle();
        }

        if (dataBeans.getNews_content() != null) {
            content = Base64Util.decode(dataBeans.getNews_content().get(0).getContent());
        }
//        content = Base64Util.decode(dataBeans.getNews_content().get(0).getContent());
        if (dataBeans.getNews_content() != null){
            img = dataBeans.getNews_content().get(0).getImg();
        }
//        img = dataBeans.getNews_content().get(0).getImg();
        Log.e("点赞状态", dataBeans.getNews_dianzanstatus());
        dianzanStatus = dataBeans.getNews_dianzanstatus();
        if (status == 0) {
            //首次进入
            if (Base64Util.checkBase64(dataBeans.getNews_info().getTitle())) {
                tvhead.setText(Base64Util.decode(dataBeans.getNews_info().getTitle()));
                //add
                tvheadtime.setText(dataBeans.getNews_info().getCreatetime().substring(0,10));
//                tvheadname.setText(dataBeans.getNews_info().getAuthor());
                tvheadname.setText(authorName);
            } else {
                tvhead.setText(dataBeans.getNews_info().getTitle());
                //add
                tvheadtime.setText(dataBeans.getNews_info().getCreatetime().substring(0,10));
//                tvheadname.setText(dataBeans.getNews_info().getAuthor());
                tvheadname.setText(authorName);
            }

            if (adapter.getHeaderLayout() != null) {
                adapter.removeHeaderView(headView);
            }
            if (!dataBeans.getNews_info().getTitle().equals("")) {
                adapter.addHeaderView(headView);
            }
            //adapter.addHeaderView(headView);
            //add
            if (dataBeans.getNews_content() != null) {
                list.addAll(dataBeans.getNews_content());
            }
//            list.addAll(dataBeans.getNews_content());
            adapter.notifyDataSetChanged();
            if (dataBeans.getNews_dianzanstatus().equals("0")) {
                Glide.with(this).load(R.mipmap.ic_like).into(ivLike);
            }
            if (dataBeans.getNews_dianzanstatus().equals("1")) {
                Glide.with(this).load(R.mipmap.ic_like2).into(ivLike);
            }
            dianzanNum = dataBeans.getNews_info().getDianzan();
            tvLike.setText(dianzanNum);
            tvComment.setText(dataBeans.getNews_info().getComment_num());
            newsid = dataBeans.getNews_info().getId();
            layoutBottom.setVisibility(View.VISIBLE);
            authorPhoneNumber = dataBeans.getNews_info().getUsercode();
        }
        if (status == 1) {
            //点赞
            if (dataBeans.getNews_dianzanstatus().equals("0")) {
                Glide.with(this).load(R.mipmap.ic_like).into(ivLike);
            }
            if (dataBeans.getNews_dianzanstatus().equals("1")) {
                Glide.with(this).load(R.mipmap.ic_like2).into(ivLike);
            }
            tvLike.setText(dataBeans.getNews_info().getDianzan());
        }
        if (status == 2) {
            //评论
            tvComment.setText(dataBeans.getNews_info().getComment_num());
            Log.e("评论已刷新", "true");

        }
    }

    @Override
    public void getArticleDetailFail() {
        T.showShort(this, "未获取到文章信息");
    }

    @Override
    public void reward(String result) {

        T.showShort(this, result);
        //presenter.getArticleDDetail(newsid);
    }

    @Override
    public void agree(BaseDataInfo dataInfo) {
        status = 1;
        presenter.getArticleDetail(newsid, PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID), unionid);
        EventBus.getDefault().post(new MsgEvent("detailagreesuccess", dataInfo));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            if (requestCode == 1) {
                Log.e("评论返回", "true");
                status = 2;
                presenter.getArticleDetail(newsid, PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID), unionid);
            }
        }
    }

    @OnClick({R.id.header_ll_left, R.id.layout_like, R.id.layout_comment, R.id.layout_money, R.id.layout_share})
    public void onClick(View view) {
        if (view.getId() == R.id.header_ll_left) {
            finish();
        } else {
            unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
            switch (view.getId()) {
                case R.id.layout_like:
                    if (LoginStatusUtil.noLogin(this)) {
                        IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);

                    } else {
                        if (dianzanStatus.equals("0")) {//未点赞
                            presenter.agree(newsid, PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID), "1", unionid);
                        }
                        if (dianzanStatus.equals("1")) {//已点赞
                            presenter.agree(newsid, PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID), "2", unionid);
                        }
                    }
                    break;
                case R.id.layout_comment:
                    Intent intent = new Intent(this, DoukeCommentActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, newsid);
                    intent.putExtra("type", type);
                    startActivityForResult(intent, 1);
                    break;
                case R.id.layout_money:
                    if (LoginStatusUtil.noLogin(this)) {
                        IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);

                    } else {
                        String phoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
                        String fromunionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                        presenter.reward(phoneNumber, authorPhoneNumber, fromunionid, tounionid);
                    }

                    break;
                case R.id.layout_share:
                    wxSharePresenter.share(PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID), unionid, title, content, img, 0);
                    break;
            }

        }

    }

    @Override
    public void share() {

    }
}
