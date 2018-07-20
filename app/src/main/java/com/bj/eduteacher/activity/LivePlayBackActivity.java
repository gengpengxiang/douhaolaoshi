/*
package com.bj.eduteacher.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.view.MyJZView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerManager;
import cn.jzvd.JZVideoPlayerStandard;

*/
/**
 * Created by zz379 on 2017/8/30.
 * 直播回放功能页面
 *//*


public class LivePlayBackActivity extends BaseActivity {

    private static final String ARTICLE_AGREE_TYPE_YES = "add";
    private static final String ARTICLE_AGREE_TYPE_NO = "del";
    private static final String ARTICLE_AGREE_TYPE_SEARCH = "status";

    @BindView(R.id.tv_readNumber)
    TextView tvReadNumber;
    @BindView(R.id.tv_commentNumber)
    TextView tvCommentNumber;
    @BindView(R.id.tv_agreeNumber)
    TextView tvAgreeNumber;
    @BindView(R.id.iv_agree)
    ImageView ivAgree;
    @BindView(R.id.ll_bottomBar)
    LinearLayout llBottomBar;

    private MyJZView mPlayer;

    // private String resID;
    private String resUrl;
    private String resName;
    private LmsDataService service;
    // private String teacherPhoneNumber;

    // JZVideoPlayer.JZAutoFullscreenListener mSensorEventListener;
    AutoFullScreenListener mSensorEventListener;
    SensorManager mSensorManager;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
        }
        setContentView(R.layout.layout_live_playback);
        ButterKnife.bind(this);
        service = new LmsDataService();
        initStatus();
        initView();
        initData();
    }

    @Override
    protected void initStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            // 如果存在虚拟按键，则设置虚拟按键的背景色
            if (ScreenUtils.isNavigationBarShow(this)) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, android.R.color.black));
            }
        }
    }

    @Override
    protected void initView() {
        // resID = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID);
        resName = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME);
        resUrl = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL);

        mPlayer = (MyJZView) findViewById(R.id.mPlayer);
        mPlayer.setBackListener(new MyJZView.BackListener() {
            @Override
            public void onBackClick() {
                onBackPressed();
            }
        });
        mPlayer.setUp(resUrl, JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, resName);
        // 设置全屏前后的屏幕方向
        JZVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JZVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        JZVideoPlayer.SAVE_PROGRESS = false;

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // mSensorEventListener = new JZVideoPlayer.JZAutoFullscreenListener();
        mSensorEventListener = new AutoFullScreenListener();

        mPlayer.startButton.performClick();
    }

    @Override
    protected void initData() {
        if (!NetUtils.isConnected(this)) {
            T.showShort(this, "无法连接到网络，请检查您的网络设置");
            hideLoadingDialog();
            return;
        }
        // 增加阅读数量
        // getResPreviewNumber();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
//        if (!StringUtils.isEmpty(teacherPhoneNumber)) {
//            // 查询是否点赞
//            getArticleAgreeNumber(ARTICLE_AGREE_TYPE_SEARCH);
//        }

        if (mPlayer.currentState != JZVideoPlayer.CURRENT_STATE_PLAYING && mPlayer.currentState != JZVideoPlayer.CURRENT_STATE_ERROR) {
            mPlayer.startButton.performClick();
        }
        // 注册重力监听
        Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @OnClick(R.id.ll_commentNumber)
    void clickComment() {
        // 点赞评论需要登录
//        if (StringUtils.isEmpty(teacherPhoneNumber)) {
//            IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
//            return;
//        }

//        Intent intent = new Intent(this, ResCommentActivity.class);
//        intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, resID);
//        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    boolean isAgree = false;

    @OnClick(R.id.ll_agreeNumber)
    void clickAgree() {
//        MobclickAgent.onEvent(this, "article_like");
//        // 点赞评论需要登录
//        if (StringUtils.isEmpty(teacherPhoneNumber)) {
//            IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
//            return;
//        }
//
//        if (isAgree) {
//            getArticleAgreeNumber(ARTICLE_AGREE_TYPE_NO);
//        } else {
//            getArticleAgreeNumber(ARTICLE_AGREE_TYPE_YES);
//        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // 防止 AudioManager 出现内存泄漏
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }

    public static class AutoFullScreenListener implements SensorEventListener {
        private static final int _DATA_X = 0;
        private static final int _DATA_Y = 1;
        private static final int _DATA_Z = 2;

        public static final int ORIENTATION_UNKNOWN = -1;

        public AutoFullScreenListener() {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] values = event.values;
            int orientation = ORIENTATION_UNKNOWN;
            float X = -values[_DATA_X];
            float Y = -values[_DATA_Y];
            float Z = -values[_DATA_Z];
            float magnitude = X * X + Y * Y;
            // Don't trust the angle if the magnitude is small compared to the y
            // value
            if (magnitude * 4 >= Z * Z) {
                // 屏幕旋转时
                float OneEightyOverPi = 57.29577957855f;
                float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
                orientation = 90 - Math.round(angle);
                // normalize to 0 - 359 range
                while (orientation >= 360) {
                    orientation -= 360;
                }
                while (orientation < 0) {
                    orientation += 360;
                }
            }

            */
/**
             * 根据手机屏幕的朝向角度，来设置内容的横竖屏，并且记录状态
             *//*

            if (orientation > 45 && orientation < 135) {
                LL.i("··········反向横屏··········");
                return;
            } else if (orientation > 135 && orientation < 225) {
                LL.i("··········反向竖屏··········");
                return;
            } else if (orientation > 225 && orientation < 315) {
                LL.i("··········横屏··········");
                if (JZVideoPlayerManager.getCurrentJzvd() != null && System.currentTimeMillis() - JZVideoPlayer.lastAutoFullscreenTime > 2000L) {
                    JZVideoPlayerManager.getCurrentJzvd().autoFullscreen(X);
                    JZVideoPlayer.lastAutoFullscreenTime = System.currentTimeMillis();
                }
            } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                LL.i("··········竖屏··········");
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().autoQuitFullscreen();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
*/
