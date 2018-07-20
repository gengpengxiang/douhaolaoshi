/*
package com.bj.eduteacher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.R;
import com.bj.eduteacher.utils.LL;
import com.hpplay.callback.ExecuteResultCallBack;
import com.hpplay.callback.HpplayWindowPlayCallBack;
import com.hpplay.link.HpplayLinkControl;

import cn.jzvd.JZVideoPlayerStandard;

*/
/**
 * Created by zz379 on 2017/8/31.
 *//*


public class MyJZView extends JZVideoPlayerStandard implements HpplayWindowPlayCallBack {

    private static final int PLAY_TV_PORT = 7724;

    public ImageView ivPlayTV;
    private HpplayLinkControl mControl;
    private LinearLayout layoutPlayTV;
    private TextView tvPlayTVState;
    private TextView tvQuitPlayTV;
    private TextView tvExchangeTV;

    public MyJZView(Context context) {
        super(context);
    }

    public MyJZView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        mControl = HpplayLinkControl.getInstance();
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
        layoutPlayTV = (LinearLayout) findViewById(R.id.layout_playTV);
        tvPlayTVState = (TextView) findViewById(R.id.tv_playTVState);
        tvQuitPlayTV = (TextView) findViewById(R.id.tv_stopPlay);
        tvQuitPlayTV.setOnClickListener(this);

        ivPlayTV = (ImageView) findViewById(R.id.playTV);
        ivPlayTV.setOnClickListener(this);

        if (mControl.getMirrorState()) {
            ivPlayTV.setVisibility(View.VISIBLE);
        } else {
            ivPlayTV.setVisibility(View.GONE);
        }
    }

    private void startPlayOnTV() {
        LL.i("准备在电视上播放视频URL");
        // mControl.initHpplayLink(getContext(), MLProperties.LE_BO_KEY);
        // 播放
        mControl.setIsBackgroundPlay(null, PLAY_TV_PORT, false);
        mControl.castStartMediaPlay(new ExecuteResultCallBack() {
            @Override
            public void onResultDate(Object o, int i) {
                LL.i("onResultDate()...播放成功与否：" + (boolean) o + " 端口号：" + i);
            }
        }, PLAY_TV_PORT, getCurrentUrl(), getCurrentPositionWhenPlaying() / 1000, HpplayLinkControl.PUSH_VIDEO);
        // 页面布局改动
        ivPlayTV.setVisibility(View.GONE);
        fullscreenButton.setVisibility(View.GONE);
        layoutPlayTV.setVisibility(View.VISIBLE);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_jz_standard_with_self;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.tv_stopPlay) {
            // 退出播放
            releaseLeboSDK();
            ivPlayTV.setVisibility(View.VISIBLE);
            fullscreenButton.setVisibility(View.VISIBLE);
            layoutPlayTV.setVisibility(View.GONE);
        } else if (i == R.id.playTV) {
            startPlayOnTV();
        }
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

    @Override
    public void onHpplayWindowDismiss() {
        //退出SDK界面
        LL.i("MyJZView.....onHpplayWindowDismiss()..." + "退出SDK界面");
    }

    @Override
    public void onIsConnect(boolean b) {
        //是否成功连接到电视，true为连接成功，false为断开连接
        LL.i("MyJZView.....onIsConnect()..." + "是否成功连接到电视：" + b);
        tvPlayTVState.setText("连接中断");
    }

    @Override
    public void onIsPlaySuccess(boolean b) {
        //是否成功推送到电视，true为推送成功，false为推送失败
        LL.i("MyJZView.....onIsPlaySuccess()..." + "是否成功推送到电视：" + b);
        tvPlayTVState.setText("正在投屏中");
    }

    */
/**
     * 释放整个SDK资源
     *//*

    public void releaseLeboSDK() {
        if (mControl != null) {
            mControl.stopPlay(new ExecuteResultCallBack() {
                @Override
                public void onResultDate(Object o, int i) {
                    LL.i("是否结束成功：" + (boolean) o + "端口号：" + i);
                }
            }, PLAY_TV_PORT);
            // mControl.castDisconnectDevice();        // 释放所有SDK资源
            mControl = null;
        }
    }
}
*/
