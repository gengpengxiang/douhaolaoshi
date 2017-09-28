package com.bj.eduteacher.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2016/4/6.
 * 禁止左右滑动的ViewPager
 */
public class CustomViewPager extends ViewPager {
    private boolean isPagingEnabled = false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }

    @Override
    public void setCurrentItem(int item) {
        if (Math.abs(this.getCurrentItem() - item) != 1) {
            super.setCurrentItem(item, false);
        } else {
            super.setCurrentItem(item, false);
        }

    }
}
