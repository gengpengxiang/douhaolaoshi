package com.bj.eduteacher.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.media.IjkVideoManager;
import com.bj.eduteacher.media.controller.MediaController;
import com.bj.eduteacher.media.videoview.IjkVideoView;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.hpplay.callback.ExecuteResultCallBack;
import com.hpplay.link.HpplayLinkControl;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zz379 on 2017/8/30.
 * 资源播放页面 该页面使用的是bilibili开源播放器，目前正在使用
 */

public class ResPlayActivity extends BaseActivity implements MediaController.OnTopBackButtonClickListener, MediaController.OnFullScreenChangeListener {

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

    private String resID;
    private String resUrl;
    private String resName;
    private LmsDataService service;
    private String teacherPhoneNumber;

    private IjkVideoView mIjkVideoView;
    private MediaController mediaController;
    private static final int LEBO_CONTROLLER_VOLUME_UP_PORT = 43;
    private static final int LEBO_CONTROLLER_VOLUME_DOWN_PORT = 44;
    private static final int LEBO_CONTROLLER_VOLUME_MUTE_PORT = 45;
    private static final int LEBO_STOP_PLAY_PORT = 50;

    // AutoFullScreenListener mSensorEventListener;
    // SensorManager mSensorManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
        }
        setContentView(R.layout.activity_res_play_bilibili);
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
        resID = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID);
        resName = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME);
        resUrl = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL);

        // mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // mSensorEventListener = new AutoFullScreenListener();

        mIjkVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mediaController = new MediaController(this);
        mediaController.setVideoName(resName);    // 设置标题
        mediaController.setShowThumb(true);
        mediaController.setPreparedPlay(true);
        mediaController.setTopBackButtonClickListener(this);    // 设置返回按钮监听
        mediaController.setFullScreenChangeListener(this);
        mIjkVideoView.setMediaController(mediaController);

        mIjkVideoView.setVideoPath(resUrl);
        // 自动播放
        mediaController.mBtnPlay.post(new Runnable() {
            @Override
            public void run() {
                mediaController.mBtnPlay.performClick();
            }
        });
    }

    @Override
    protected void initData() {
        if (!NetUtils.isConnected(this)) {
            T.showShort(this, "无法连接到网络，请检查您的网络设置");
            hideLoadingDialog();
            return;
        }
        // 增加阅读数量
        getResPreviewNumber();
    }

    @Override
    protected void onResume() {
        super.onResume();
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        if (!StringUtils.isEmpty(teacherPhoneNumber)) {
            // 查询是否点赞
            getArticleAgreeNumber(ARTICLE_AGREE_TYPE_SEARCH);
        }
        // 注册重力监听
        // Sensor accelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // mSensorManager.registerListener(mSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @OnClick(R.id.ll_commentNumber)
    void clickComment() {
        // 点赞评论需要登录
        if (StringUtils.isEmpty(teacherPhoneNumber)) {
            IntentManager.toLoginActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
            return;
        }

        Intent intent = new Intent(this, ResCommentActivity.class);
        intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, resID);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaController.isPlayingOnTV) {
            mediaController.isPlayingOnTV = false;
            HpplayLinkControl.getInstance().stopPlay(new ExecuteResultCallBack() {
                @Override
                public void onResultDate(Object o, int i) {
                    Log.i("way", "结束播放：" + (Boolean) o + "...端口号：" + i);
                }
            }, LEBO_STOP_PLAY_PORT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        IjkVideoManager.getInstance().pause();
        // mSensorManager.unregisterListener(mSensorEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IjkVideoManager.getInstance().release();
        // 释放整个SDK
        HpplayLinkControl.getInstance().castDisconnectDevice();
    }

    private void getResPreviewNumber() {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                String[] result = service.addResourcePreviewNumber(resID);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String[] result) {
                        // 获取点赞数量和评论数量
                        getResNumbers();
                        if ("1".equals(result[0])) {

                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getResNumbers() {
        Observable.create(new ObservableOnSubscribe<ArticleInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArticleInfo> e) throws Exception {
                ArticleInfo dataInfo = service.getResInfoByID(resID);
                e.onNext(dataInfo);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ArticleInfo articleInfo) {
                        hideLoadingDialog();
                        llBottomBar.setVisibility(View.VISIBLE);
                        tvAgreeNumber.setText(articleInfo.getAgreeNumber());
                        tvCommentNumber.setText(articleInfo.getCommentNumber());
                        tvReadNumber.setText(articleInfo.getReadNumber() + "次阅读");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoadingDialog();
                        llBottomBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    boolean isAgree = false;

    @OnClick(R.id.ll_agreeNumber)
    void clickAgree() {
        MobclickAgent.onEvent(this, "article_like");
        // 点赞评论需要登录
        if (StringUtils.isEmpty(teacherPhoneNumber)) {
            IntentManager.toLoginActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
            return;
        }

        if (isAgree) {
            getArticleAgreeNumber(ARTICLE_AGREE_TYPE_NO);
        } else {
            getArticleAgreeNumber(ARTICLE_AGREE_TYPE_YES);
        }
    }

    private void getArticleAgreeNumber(final String type) {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BaseDataInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                BaseDataInfo dataInfo = mService.getResourceAgreeStatus(resID, teacherPhoneNumber, type);
                e.onNext(dataInfo);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo info) {
                        if (info == null || StringUtils.isEmpty(info.getRet())) {
                            return;
                        }

                        if (info.getRet().equals("3")) {
                            if (info.getData().equals("1")) {
                                ivAgree.setImageResource(R.mipmap.ic_liked);
                                isAgree = true;
                            } else {
                                ivAgree.setImageResource(R.mipmap.ic_like);
                                isAgree = false;
                            }
                        } else if (info.getRet().equals("2")) {
                            ivAgree.setImageResource(R.mipmap.ic_like);
                            isAgree = false;
                        } else if (info.getRet().equals("1")) {
                            ivAgree.setImageResource(R.mipmap.ic_liked);
                            isAgree = true;
                        } else {

                        }
                        // 获取res 各种数据
                        getResNumbers();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

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

    @Override
    public void onFullScreenChange(boolean fullscreen) {
        
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // T.showShort(this, "横屏");
            int options = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            getWindow().getDecorView().setSystemUiVisibility(options);
            llBottomBar.setVisibility(View.GONE);
        } else {
            // T.showShort(this, "竖屏");
            getWindow().getDecorView().setSystemUiVisibility(0);
            llBottomBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTopBackClick() {
        onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("way", "Activity...onKeyDown()...按键监听：" + keyCode);
        // 处理电视音量
        if (mediaController.isPlayingOnTV && keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            HpplayLinkControl.getInstance().castDeviceVolume(new ExecuteResultCallBack() {
                @Override
                public void onResultDate(Object o, int i) {
                    Log.i("way", "增加音量：" + (Boolean) o + "...端口号：" + i);
                }
            }, LEBO_CONTROLLER_VOLUME_UP_PORT, true);
            return true;
        } else if (mediaController.isPlayingOnTV && keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            HpplayLinkControl.getInstance().castDeviceVolume(new ExecuteResultCallBack() {
                @Override
                public void onResultDate(Object o, int i) {
                    Log.i("way", "减少音量：" + (Boolean) o + "...端口号：" + i);
                }
            }, LEBO_CONTROLLER_VOLUME_DOWN_PORT, false);
            return true;
        } else if (mediaController.isPlayingOnTV && keyCode == KeyEvent.KEYCODE_VOLUME_MUTE) {
            HpplayLinkControl.getInstance().castDeviceVolume(new ExecuteResultCallBack() {
                @Override
                public void onResultDate(Object o, int i) {
                    Log.i("way", "静音：" + (Boolean) o + "...端口号：" + i);
                }
            }, LEBO_CONTROLLER_VOLUME_MUTE_PORT, 0);
        }

        return super.onKeyDown(keyCode, event);
    }

    public class AutoFullScreenListener implements SensorEventListener {
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

            /**
             * 根据手机屏幕的朝向角度，来设置内容的横竖屏，并且记录状态
             */
            if (orientation > 45 && orientation < 135) {
                LL.i("··········反向横屏··········");
                return;
            } else if (orientation > 135 && orientation < 225) {
                LL.i("··········反向竖屏··········");
                return;
            } else if (orientation > 225 && orientation < 315) {
                LL.i("··········横屏··········");
                mediaController.startFullScreen();
            } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                LL.i("··········竖屏··········");
                mediaController.quitFullScreen();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }
}
