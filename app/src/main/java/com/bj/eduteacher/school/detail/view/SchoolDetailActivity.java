package com.bj.eduteacher.school.detail.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.school.detail.fragment.dongtai.DongtaiFragment;
import com.bj.eduteacher.school.detail.fragment.jianjie.IntroduceFragment;
import com.bj.eduteacher.school.detail.fragment.youke.YoukeFragment;
import com.bj.eduteacher.school.list.model.School;
import com.bj.eduteacher.utils.StatusBarCompat;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;

public class SchoolDetailActivity extends BaseActivity {

    @BindView(R.id.header_bg)
    ImageView headerBg;
    @BindView(R.id.header_name)
    TextView headerName;
    //    @BindView(R.id.header_area)
//    TextView headerArea;
    @BindView(R.id.spaceView)
    View spaceView;
    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.coordinatorlayout)
    CoordinatorLayout coordinatorlayout;
    @BindView(R.id.mSmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.header_bg0)
    SimpleDraweeView headerBg0;

    private int h;

    private School.DataBean school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_detail);
        ButterKnife.bind(this);

        StatusBarCompat.fullScreen(this);
        h = StatusBarCompat.getStatusBarHeight(this);
        //改变底部导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }

        initViews();
    }

    private void initViews() {
        AutoUtils.auto(collapsingToolbar);
        AutoUtils.auto(appbar);
        Intent intent = getIntent();
        school = (School.DataBean) intent.getSerializableExtra("school");

        headerName.setText(school.getName());
        //headerArea.setText(school.getShengfen());
        //Glide.with(this).load(BASE_RESOURCE_URL + school.getSchoolimg()).into(headerBg);

        headerBg0.setImageURI(BASE_RESOURCE_URL + school.getSchoolimg());

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        viewpager.setOffscreenPageLimit(3);
        toolbar.setTitle("");
        setupViewPager(viewpager);
        tabs.setupWithViewPager(viewpager);
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.btn_red));
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offset = Math.abs(verticalOffset);
                if ((appbar.getHeight() - offset) == toolbar.getHeight()) {
                    collapsingToolbar.setContentScrimColor(Color.parseColor("#FE5433"));
                    toolbarTitle.setText(school.getName());
                    toolbarTitle.setVisibility(View.VISIBLE);
                } else {
                    collapsingToolbar.setContentScrimColor(Color.parseColor("#01000000"));
                    toolbarTitle.setText("");
                    toolbarTitle.setVisibility(View.GONE);
                }
            }
        });

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                int currenItem = viewpager.getCurrentItem();
                EventBus.getDefault().post(new MsgEvent("refreshschool", currenItem));
                mSmartRefreshLayout.finishRefresh(500);
            }
        });

        viewpager.setOffscreenPageLimit(3);
    }

    @OnClick(R.id.toolbar_back)
    public void onClick() {
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        InfoAdapter adapter = new InfoAdapter(getSupportFragmentManager());
        adapter.addFragment(new IntroduceFragment(), "简介");
        adapter.addFragment(new DongtaiFragment(), "动态");
        adapter.addFragment(new YoukeFragment(), "优课");
        viewPager.setAdapter(adapter);
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
