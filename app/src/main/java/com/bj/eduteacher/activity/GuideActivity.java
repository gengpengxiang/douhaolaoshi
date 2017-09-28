package com.bj.eduteacher.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.GuideFragmentPagerAdapter;
import com.bj.eduteacher.fragment.Guide1Fragment;
import com.bj.eduteacher.fragment.Guide2Fragment;
import com.bj.eduteacher.fragment.Guide3Fragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zz379 on 2017/8/11.
 */

public class GuideActivity extends BaseActivity {

    @BindView(R.id.mViewPager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {

        List<Fragment> dataList = new ArrayList<>();
        dataList.add(new Guide1Fragment());
        dataList.add(new Guide2Fragment());
        dataList.add(new Guide3Fragment());

        GuideFragmentPagerAdapter mAdapter = new GuideFragmentPagerAdapter(getSupportFragmentManager(), dataList);

        mViewPager.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
