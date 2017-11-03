package com.bj.eduteacher.view;

import android.content.Context;
import android.util.AttributeSet;

import com.bj.eduteacher.zzautolayout.AutoFrameLayout;
import com.bj.eduteacher.zzautolayout.utils.AutoLayoutHelper;
import com.youth.banner.Banner;

/**
 * Created by zz379 on 2017/11/1.
 */

public class MyBanner extends Banner {
    
    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

    public MyBanner(Context context) {
        super(context);
    }

    public MyBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public AutoFrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AutoFrameLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isInEditMode()) {
            mHelper.adjustChildren();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
