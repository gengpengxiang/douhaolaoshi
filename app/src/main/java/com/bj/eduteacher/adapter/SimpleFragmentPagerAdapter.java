package com.bj.eduteacher.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bj.eduteacher.entity.ClassInfo;

import java.util.List;

/**
 * Created by nercdev on 2017/1/17.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mTabs;
    private List<ClassInfo> mTabTitles;

    public SimpleFragmentPagerAdapter(FragmentManager fm, List<Fragment> tabs, List<ClassInfo> tabTitls) {
        super(fm);
        mTabs = tabs;
        mTabTitles = tabTitls;
    }

    @Override
    public Fragment getItem(int position) {
        return mTabs.get(position);
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles.get(position).getClassName();
    }
}
