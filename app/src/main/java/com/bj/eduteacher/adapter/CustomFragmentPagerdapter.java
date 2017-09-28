package com.bj.eduteacher.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zz379 on 2017/3/31.
 */

public abstract class CustomFragmentPagerdapter extends PagerAdapter {

    private static final String TAG = CustomFragmentPagerdapter.class.getSimpleName();

    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;

    public CustomFragmentPagerdapter(FragmentManager mFragmentManager) {
        this.mFragmentManager = mFragmentManager;
    }

    public abstract Fragment getItem(int position);

    @Override
    public void startUpdate(ViewGroup container) {
        if (container.getId() == View.NO_ID) {
            throw new IllegalStateException("ViewPager with adapter " + this + " requires a view id");
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        Fragment fragment = getItem(position);
        Log.i(TAG, "Adding fragment item #" + position + ": f=" + fragment);
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);
        mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), getItemId(position)));
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;

        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        Log.i(TAG, "Removing fragment #" + position + ": f=" + fragment + " v=" + fragment.getView());
        mCurTransaction.remove(fragment);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (mCurrentPrimaryItem == null) {
            commitUpdate();
        }
        mCurrentPrimaryItem = (Fragment) object;
    }

    public void commitUpdate() {
        if (mCurTransaction != null) {
            mCurTransaction.commitNowAllowingStateLoss();
            mCurTransaction = null;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        commitUpdate();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    protected String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    protected long getItemId(int position) {
        return position;
    }
}
