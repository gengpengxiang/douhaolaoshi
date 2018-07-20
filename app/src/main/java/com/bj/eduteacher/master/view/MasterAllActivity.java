package com.bj.eduteacher.master.view;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MasterAllActivity extends BaseActivity {

    @BindView(R.id.header_img_back)
    ImageView headerImgBack;
    @BindView(R.id.header_tv_title)
    TextView headerTvTitle;
    @BindView(R.id.lineView)
    View lineView;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    private Unbinder unbinder;
    private InfoAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_all);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        lineView.setVisibility(View.VISIBLE);
        headerImgBack.setVisibility(View.VISIBLE);
        headerTvTitle.setVisibility(View.VISIBLE);
        headerTvTitle.setText("驻场专家");

//        dataList.add("科研专家");
//        dataList.add("教研专家");
        setupViewPager(viewpager);
        tabs.setupWithViewPager(viewpager);
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.btn_red));

    }

    private void setupViewPager(ViewPager viewPager) {
//        InfoAdapter adapter = new InfoAdapter(getSupportFragmentManager());
        adapter = new InfoAdapter(getSupportFragmentManager());
        //adapter.deleteFragment();
        adapter.addFragment(new KyMasterFragment(), "科研专家");
        adapter.addFragment(new JyMasterFragment(), "教研专家");

        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        viewpager.setOffscreenPageLimit(3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.header_img_back)
    public void onClick() {
        finish();
    }

    class InfoAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mFragmentTitles = new ArrayList<>();

        public InfoAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
            notifyDataSetChanged();
        }

        public void deleteFragment() {
            mFragments.clear();
            mFragmentTitles.clear();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
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
