package com.bj.eduteacher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.bj.eduteacher.R;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by zz379 on 2017/8/31.
 */

public class MyJCView extends JCVideoPlayerStandard {

    public MyJCView(Context context) {
        super(context);
    }

    public MyJCView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentScreen == SCREEN_LAYOUT_NORMAL) {
                    if (backListener != null) {
                        backListener.onBackClick();
                    }
                } else {
                    backPress();
                }
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_jc_standard_with_self;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void setUp(String url, int screen, Object... objects) {
        super.setUp(url, screen, objects);
        backButton.setVisibility(View.VISIBLE);
    }
    
    private BackListener backListener;

    public void setBackListener(BackListener backListener) {
        this.backListener = backListener;
    }

    public interface BackListener {
        public void onBackClick();
    }
}
