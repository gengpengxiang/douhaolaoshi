package com.bj.eduteacher.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by nercdev on 2017/1/17.
 */

public class SimpleFragmentPagerAdapter2 extends FragmentPagerAdapter {

    private List<Fragment> mTabs;

    public SimpleFragmentPagerAdapter2(FragmentManager fm, List<Fragment> tabs) {
        super(fm);
        mTabs = tabs;
    }

    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

}
