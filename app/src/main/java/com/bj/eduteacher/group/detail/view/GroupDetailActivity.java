package com.bj.eduteacher.group.detail.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.community.publish.view.PublishActivity;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.group.detail.fragment.datums.DatumFragment;
import com.bj.eduteacher.group.detail.fragment.member.MemberFragment;
import com.bj.eduteacher.group.detail.fragment.task.TaskFragment;
import com.bj.eduteacher.group.detail.fragment.topic.TopicFragment;
import com.bj.eduteacher.group.detail.model.GroupDetail;
import com.bj.eduteacher.group.detail.presenter.GroupDetailPresenter;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StatusBarCompat;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

public class GroupDetailActivity extends BaseActivity implements IViewGroupDetail {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.header_bg)
    ImageView headerBg;
    @BindView(R.id.header_name)
    TextView headerName;
    @BindView(R.id.header_tongji)
    TextView headerTongji;
    //    @BindView(R.id.header_jianjie)
//    TextView headerJianjie;
    @BindView(R.id.toolbar_signin)
    TextView toolbarSignin;
    @BindView(R.id.header_logo)
    SimpleDraweeView headerLogo;
    @BindView(R.id.mSmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.bt_group_edit)
    Button btGroupEdit;
    @BindView(R.id.spaceView)
    View spaceView;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.header_bg0)
    SimpleDraweeView headerBg0;
    @BindView(R.id.lineView)
    View lineView;

    private Unbinder unbinder;
    private String groupid;
    private String grouptitle;

    private int h;
    private GroupDetailPresenter groupDetailPresenter;
    private int membernum;
    public static long lastRefreshTime;
    private String unionid;

    private String taskShow = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        groupDetailPresenter = new GroupDetailPresenter(this, this);
        Intent intent = getIntent();
        groupid = intent.getStringExtra("groupid");
        grouptitle = intent.getStringExtra("grouptitle");
        groupDetailPresenter.getGroupDetail(PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID), groupid, unionid);

        StatusBarCompat.fullScreen(this);
        h = StatusBarCompat.getStatusBarHeight(this);
        //改变底部导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }

        initViews();
        initDatas();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        viewpager.setOffscreenPageLimit(3);


        AutoUtils.auto(collapsingToolbar);
        AutoUtils.auto(appbar);


        toolbar.setTitle("");
//        setupViewPager(viewpager);
        tabs.setupWithViewPager(viewpager);
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.btn_red));

        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offset = Math.abs(verticalOffset);

                if ((appbar.getHeight() - offset) == toolbar.getHeight()) {
                    collapsingToolbar.setContentScrimColor(Color.parseColor("#FE5433"));
                    toolbarTitle.setText(grouptitle);
                    toolbarTitle.setVisibility(View.VISIBLE);
                } else {
                    collapsingToolbar.setContentScrimColor(Color.parseColor("#01000000"));
                    toolbarTitle.setText("");
                    toolbarTitle.setVisibility(View.GONE);
                }
            }
        });

        EventBus.getDefault().post(new MsgEvent("entergroup"));

    }


    private void initViews() {

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                int currenItem = viewpager.getCurrentItem();
                EventBus.getDefault().post(new MsgEvent("refreshPage", currenItem));
                mSmartRefreshLayout.finishRefresh(500);
            }
        });

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    if (positionOffset != 0) {
                        btGroupEdit.setVisibility(View.GONE);
                    } else {
                        btGroupEdit.setVisibility(View.VISIBLE);
                    }

                } else {
                    btGroupEdit.setVisibility(View.GONE);
                }

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    btGroupEdit.setVisibility(View.VISIBLE);
                } else {
                    btGroupEdit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initDatas() {

    }

    /**
     * 设置item
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        InfoAdapter adapter = new InfoAdapter(getSupportFragmentManager());
        adapter.addFragment(new TopicFragment(), "研讨");
        if (taskShow.equals("1")) {
            adapter.addFragment(new TaskFragment(), "任务");
        }
        adapter.addFragment(new DatumFragment(), "资料");
        adapter.addFragment(new MemberFragment(), "成员");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        //setResult(3);
        EventBus.getDefault().post(new MsgEvent("groupupdate"));
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUI(MsgEvent event) {
        if (event.getAction().equals("phoneloginsuccess")) {
            groupDetailPresenter.getGroupDetail(PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID), groupid, unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID));
            int currenItem = viewpager.getCurrentItem();
            EventBus.getDefault().post(new MsgEvent("refreshPage", currenItem));
        }
        if (event.getAction().equals("wxloginsuccess")) {
            groupDetailPresenter.getGroupDetail(PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID), groupid, unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID));
            int currenItem = viewpager.getCurrentItem();
            EventBus.getDefault().post(new MsgEvent("refreshPage", currenItem));
        }
    }


    @Override
    public void getGroupDetailSuccess(GroupDetail groupDetail) {

        lineView.setVisibility(View.VISIBLE);
        taskShow = groupDetail.getData().getGroup_info().getRenwu_show();
        setupViewPager(viewpager);

        GroupDetail.DataBean.GroupInfoBean info = groupDetail.getData().getGroup_info();
        toolbarTitle.setText(info.getName());
//        Glide.with(this).load(BASE_RESOURCE_URL + info.getBgimg()).into(headerBg);

        headerBg0.setImageURI(BASE_RESOURCE_URL + info.getBgimg());

        headerLogo.setImageURI(BASE_RESOURCE_URL + info.getLogo());
        headerName.setText(info.getName());
        headerTongji.setText("成员 " + groupDetail.getData().getGroup_chengyuan_num() + " | " + "话题 " + groupDetail.getData().getGroup_huati_num());
        //headerJianjie.setText(info.getJianjie());

        if (groupDetail.getData().getGroup_qiandao() == 0) {
            //toolbarSignin.setBackgroundResource(R.drawable.shape_btn_signin);
            //toolbarSignin.setTextColor(this.getResources().getColor(R.color.colorTextBlack));
            toolbarSignin.setBackgroundResource(R.drawable.shape_btn_signin);
            toolbarSignin.setText("签到");
        } else {
            //toolbarSignin.setBackgroundColor(Color.TRANSPARENT);
            //toolbarSignin.setTextColor(this.getResources().getColor(R.color.colorTextWhite));
            toolbarSignin.setBackgroundResource(R.drawable.shape_btn_signin);
            toolbarSignin.setText("已签到" + groupDetail.getData().getGroup_qiandao_days() + "天");
        }

        membernum = groupDetail.getData().getGroup_chengyuan_num();
        Log.e("细节界面个数", groupDetail.getData().getGroup_chengyuan_num() + "");
        EventBus.getDefault().postSticky(new MsgEvent("groupmembernum", groupDetail.getData().getGroup_chengyuan_num()));
    }

    @Override
    public void getSignResult(BaseDataInfo baseDataInfo) {
        T.showShort(this, baseDataInfo.getMsg());
        if (baseDataInfo.getRet().equals("1")) {
            //toolbarSignin.setBackgroundColor(Color.TRANSPARENT);
            //toolbarSignin.setTextColor(this.getResources().getColor(R.color.colorTextWhite));
            toolbarSignin.setBackgroundResource(R.drawable.shape_btn_signin);
            toolbarSignin.setText("已签到" + baseDataInfo.getData() + "天");

            EventBus.getDefault().post(new MsgEvent("signSuccess"));
        }
    }

    @OnClick({R.id.toolbar_back, R.id.toolbar_signin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.toolbar_signin:
                if (LoginStatusUtil.noLogin(this)) {
                    IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                    return;
                } else {
                    String unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                    groupDetailPresenter.signIn(PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, ""), groupid, unionid);
                }
                break;
        }
    }

    @OnClick(R.id.bt_group_edit)
    public void onClick() {
        if (LoginStatusUtil.noLogin(this)) {
            IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
            return;
        } else {
            Intent intent = new Intent(GroupDetailActivity.this, PublishActivity.class);
            intent.putExtra("type", groupid);
            startActivity(intent);
        }
    }

    class InfoAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public InfoAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}
