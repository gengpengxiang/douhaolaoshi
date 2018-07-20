package com.bj.eduteacher.videoplayer.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alivc.player.VcPlayerLog;
import com.aliyun.vodplayer.media.AliyunLocalSource;
import com.aliyun.vodplayer.media.AliyunVidSts;
import com.aliyun.vodplayer.media.IAliyunVodPlayer;
import com.aliyun.vodplayerview.utils.OrientationWatchDog;
import com.aliyun.vodplayerview.view.control.ControlView;
import com.aliyun.vodplayerview.widget.AliyunScreenMode;
import com.aliyun.vodplayerview.widget.AliyunVodPlayerView;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.ResCommentActivity;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.course.fragment.study.NextRes;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.videoplayer.model.AliyunVideoInfo;
import com.bj.eduteacher.videoplayer.model.ResInfo;
import com.bj.eduteacher.videoplayer.presenter.PlayerPresenter;
import com.bj.eduteacher.videoplayer.util.ScreenStatusController;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

public class PlayerActivity extends BaseActivity implements IViewPlayer {

    @BindView(R.id.tv_readNumber)
    TextView tvReadNumber;
    @BindView(R.id.tv_agreeNumber)
    TextView tvAgreeNumber;
    @BindView(R.id.tv_commentNumber)
    TextView tvCommentNumber;
    Unbinder unbinder;
    @BindView(R.id.iv_agree)
    ImageView ivAgree;
    @BindView(R.id.ll_bottomBar)
    LinearLayout llBottomBar;
    private AliyunVodPlayerView mAliyunVodPlayerView = null;

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS");
    private List<String> logStrs = new ArrayList<>();

    private ScreenStatusController mScreenStatusController = null;
    private String unionid, phone;
    private String dianzanStatus;
    private String type;
    private String kechengid;
    private String currentTime;
    private String jiakestatus;
    private String jindu;
    private String videoUrl;

    private int mProgress = 0;
    private int mDuration = 0;

    private boolean isStrangePhone() {
        boolean strangePhone = Build.DEVICE.equalsIgnoreCase("mx5")
                || Build.DEVICE.equalsIgnoreCase("Redmi Note2")
                || Build.DEVICE.equalsIgnoreCase("Z00A_1")
                || Build.DEVICE.equalsIgnoreCase("hwH60-L02")
                || Build.DEVICE.equalsIgnoreCase("hermes")
                || (Build.DEVICE.equalsIgnoreCase("V4") && Build.MANUFACTURER.equalsIgnoreCase("Meitu"))
                || (Build.DEVICE.equalsIgnoreCase("m1metal") && Build.MANUFACTURER.equalsIgnoreCase("Meizu"));

        VcPlayerLog.e("lfj1115 ", " Build.Device = " + Build.DEVICE + " , isStrange = " + strangePhone);
        return strangePhone;

    }

    //add
    private PlayerPresenter playerPresenter;
    private String resId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
        }

        setContentView(R.layout.activity_skin);
        unbinder = ButterKnife.bind(this);

        mAliyunVodPlayerView = (AliyunVodPlayerView) findViewById(R.id.video_view);
        mAliyunVodPlayerView.setKeepScreenOn(true);//保持屏幕敞亮

        String sdDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test_save_cache";
        //mAliyunVodPlayerView.setPlayingCache(true, sdDir, 60 * 60 /*时长, s */, 300 /*大小，MB*/);//边播边存
        mAliyunVodPlayerView.setTheme(AliyunVodPlayerView.Theme.Orange);
        mAliyunVodPlayerView.setCirclePlay(false);//循环播放

        mAliyunVodPlayerView.setOnPreparedListener(new MyPrepareListener(this));
        mAliyunVodPlayerView.setOnCompletionListener(new MyCompletionListener(this));
        mAliyunVodPlayerView.setOnFirstFrameStartListener(new IAliyunVodPlayer.OnFirstFrameStartListener() {
            @Override
            public void onFirstFrameStart() {

                mDuration = mAliyunVodPlayerView.getDuration();
            }
        });
//        mAliyunVodPlayerView.setOnChangeQualityListener(new MyChangeQualityListener(this));
//        mAliyunVodPlayerView.setOnStoppedListener(new MyStoppedListener(this));

        mAliyunVodPlayerView.mOrientationWatchDog.setOnOrientationListener(new AliyunVodPlayerView.InnerOrientationListener(mAliyunVodPlayerView) {
            @Override
            public void changedToLandScape(boolean fromPort) {

                Log.e("转换","aaa");

                mAliyunVodPlayerView.changedToLandScape(fromPort);
                llBottomBar.setVisibility(View.GONE);
            }

            @Override
            public void changedToPortrait(boolean fromLand) {

                Log.e("转换","bbb");
                mAliyunVodPlayerView.changedToPortrait(fromLand);
                //llBottomBar.setVisibility(View.VISIBLE);
            }
        });


        mAliyunVodPlayerView.enableNativeLog();

        mAliyunVodPlayerView.setAutoPlay(true);//自动播放

        setPlaySource();
        mAliyunVodPlayerView.showProgressDialog(true);

        mScreenStatusController = new ScreenStatusController(this);
        mScreenStatusController.setScreenStatusListener(new ScreenStatusController.ScreenStatusListener() {
            @Override
            public void onScreenOn() {
            }

            @Override
            public void onScreenOff() {

            }
        });
        mScreenStatusController.startListen();

        //add
        resId = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID);
        playerPresenter = new PlayerPresenter(this, this);
//        playerPresenter.getResInfo(resId);

        playerPresenter.addBrowseNum(resId);//查询浏览数量
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        playerPresenter.queryOrAgree(resId, "status", phone, unionid);//查询点赞状态


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("ScreenMode");

        MyBroadcastReceiver receiver = new MyBroadcastReceiver();
        registerReceiver(receiver, intentFilter);

    }


    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("guangbo","aaa");

            String model = intent.getStringExtra("model");
            if(model.equals("full")){
                llBottomBar.setVisibility(View.GONE);
            }else {
                llBottomBar.setVisibility(View.VISIBLE);
            }

        }

    }


    @Override
    public void getResInfo(ResInfo.DataBean resInfo) {
        tvAgreeNumber.setText(StringUtils.isEmpty(resInfo.getDianzan()) ? "0" : resInfo.getDianzan());
        tvCommentNumber.setText(StringUtils.isEmpty(resInfo.getComment_num()) ? "0" : resInfo.getComment_num());
        tvReadNumber.setText(resInfo.getViewnum() + "次浏览");

        mAliyunVodPlayerView.mControlView.setTitle(resInfo.getTitle());

        videoUrl = resInfo.getPreviewurl();

        llBottomBar.setVisibility(View.VISIBLE);

    }

    @Override
    public void addBrowseNumSuccess() {
        playerPresenter.getResInfo(resId);
    }

    @Override
    public void getAgreeInfo(BaseDataInfo info, String type) {
        if (type.equals("status")) {
            if (info.getRet().equals("3")) {
                if (info.getData().equals("1")) {
                    ivAgree.setImageResource(R.mipmap.ic_liked);
                    dianzanStatus = "1";
                } else {
                    ivAgree.setImageResource(R.mipmap.ic_like);
                    dianzanStatus = "0";
                }
            }
            playerPresenter.getResInfo(resId);
        }
        if (type.equals("add")) {
            if (info.getRet().equals("1")) {
                playerPresenter.queryOrAgree(resId, "status", phone, unionid);
                playerPresenter.getResInfo(resId);
            }
        }
        if (type.equals("del")) {
            if (info.getRet().equals("2")) {
                playerPresenter.queryOrAgree(resId, "status", phone, unionid);
                playerPresenter.getResInfo(resId);
            }
        }
    }

    private void missionComplete() {
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/setkcresjindu")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("resid", resId)
                        .params("kechengid", "")
                        .params("restype", "1")
                        .params("endtime", "0")
                        .params("unionid", unionid)
                        .params("phone", phone)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                BaseDataInfo baseDataInfo = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });
                                Log.e("观看视频任务完成", str);

                                e.onNext(baseDataInfo);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo dataInfo) {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 300) {
            if (requestCode == 1) {
                playerPresenter.getResInfo(resId);
            }
        }
    }

    @OnClick({R.id.ll_agreeNumber, R.id.ll_commentNumber})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_agreeNumber:
                if (LoginStatusUtil.noLogin(this)) {
                    IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
                    return;
                }
                if (dianzanStatus.equals("0")) {
                    playerPresenter.queryOrAgree(resId, "add", phone, unionid);
                }
                if (dianzanStatus.equals("1")) {
                    playerPresenter.queryOrAgree(resId, "del", phone, unionid);
                }
                break;
            case R.id.ll_commentNumber:
                Intent intent = new Intent(this, ResCommentActivity.class);
                intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, resId);
                startActivityForResult(intent, 1);
                break;
        }
    }

    private class MyPrepareListener implements IAliyunVodPlayer.OnPreparedListener {
        private WeakReference<PlayerActivity> activityWeakReference;

        public MyPrepareListener(PlayerActivity skinActivity) {
            activityWeakReference = new WeakReference<PlayerActivity>(skinActivity);
        }

        @Override
        public void onPrepared() {
            PlayerActivity activity = activityWeakReference.get();
            if (activity != null) {
                mAliyunVodPlayerView.seekTo(mProgress);
            }
//            mAliyunVodPlayerView.showProgressDialog(true);
        }
    }

    private class MyCompletionListener implements IAliyunVodPlayer.OnCompletionListener {
        private WeakReference<PlayerActivity> activityWeakReference;

        public MyCompletionListener(PlayerActivity skinActivity) {
            activityWeakReference = new WeakReference<PlayerActivity>(skinActivity);
        }

        @Override
        public void onCompletion() {
            PlayerActivity activity = activityWeakReference.get();
            if (activity != null) {
                if (type.equals("TaskFragment")) {
                    missionComplete();
                }
                if (type.equals("StudyFragment")) {

                    if (jiakestatus.equals("1")) {

                        if (jindu != null && !jindu.equals("已学完")) {
                            String endtime = String.valueOf(System.currentTimeMillis());
                            shanghcuanjindu(mDuration, mDuration, endtime);
                        }
                        playNext(resId, kechengid);
                    }
                }
                if (!type.equals("StudyFragment")) {
                    mAliyunVodPlayerView.showDialog(true);
                }
            }
        }
    }

    private void playNext(final String resid, final String kechengid) {
        Observable.create(new ObservableOnSubscribe<NextRes>() {
            @Override
            public void subscribe(final ObservableEmitter<NextRes> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/getnextres")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("resid", resid)
                        .params("kechengid", kechengid)
                        .params("unionid", unionid)
                        .params("phone", phone)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("获取下一个视频结果", str);
                                NextRes nextRes = JSON.parseObject(str, new TypeReference<NextRes>() {
                                });
                                e.onNext(nextRes);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NextRes>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(NextRes nextRes) {
                        if (nextRes.getRet().equals("1")) {
                            resId = nextRes.getData().getNext_resid();

                            //chaxunAgreeStatus("status");
                            //getResPreviewNumber();
                            //mIjkVideoView.setVideoPath(nextRes.getData().getNext_previewurl());
                            playerPresenter.queryOrAgree(resId, "status", phone, unionid);//查询点赞状态
                            playerPresenter.addBrowseNum(resId);

                            if (type.equals("StudyFragment")) {
                                if (jiakestatus.equals("1")) {

                                    jindu = "test";
                                    currentTime = nextRes.getData().getNext_currentTime();
                                    String url = nextRes.getData().getNext_previewurl();
                                    AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
                                    alsb.setSource(url);
                                    AliyunLocalSource localSource = alsb.build();
                                    mProgress = Integer.valueOf(currentTime) * 1000;
                                    mAliyunVodPlayerView.setLocalSource(localSource);

                                }
                            }

                        }
                        if (nextRes.getRet().equals("2")) {
                            T.showShort(PlayerActivity.this, "已是最后一个视频");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void setPlaySource() {

        type = getIntent().getStringExtra("type");
        if (TextUtils.isEmpty(type)) {
            return;
        }
        if (type.equals("StudyFragment")) {
            kechengid = getIntent().getStringExtra("kechengid");
            currentTime = getIntent().getStringExtra("currentTime");
            jiakestatus = getIntent().getStringExtra("jiakestatus");
            jindu = getIntent().getStringExtra("jindu");
            if (currentTime != null) {
                mProgress = Integer.valueOf(currentTime) * 1000;
            }

            String url = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL);
            AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
            alsb.setSource(url);
            AliyunLocalSource localSource = alsb.build();
            mAliyunVodPlayerView.setLocalSource(localSource);

            mDuration = mAliyunVodPlayerView.getDuration();
        }
//        if (type.equals("DoukeFragment")||type.equals("DatumFragment")) {
        else {
//            String url = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL);
//            AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
//            alsb.setSource(url);
//            AliyunLocalSource localSource = alsb.build();
//            mAliyunVodPlayerView.setLocalSource(localSource);
            play();

        }
       /* if (type.equals("TaskFragment_url")) {
            String url = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL);
            AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
            alsb.setSource(url);
            AliyunLocalSource localSource = alsb.build();
            mAliyunVodPlayerView.setLocalSource(localSource);
        }*/
        if (type.equals("TaskFragment")) {
            if (StringUtils.isEmpty(getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL))) {
                OkGo.<String>get("http://testg2c.gamepku.com/userlib/aliyun/vodsts.php")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String s = response.body().toString();
                                Gson gson = new Gson();
                                final AliyunVideoInfo info = gson.fromJson(s, AliyunVideoInfo.class);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String vid = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_VID);
                                        AliyunVidSts vidSts = new AliyunVidSts();
                                        vidSts.setVid(vid);//2869102e80b74ecd8f5c43edbfde4ac4
                                        vidSts.setAcId(info.getCredentials().getAccessKeyId());
                                        vidSts.setAkSceret(info.getCredentials().getAccessKeySecret());
                                        vidSts.setSecurityToken(info.getCredentials().getSecurityToken());
                                        mAliyunVodPlayerView.setVidSts(vidSts);
                                    }
                                });
                            }
                        });
            } else {
                String url = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL);
                AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
                alsb.setSource(url);
                AliyunLocalSource localSource = alsb.build();
                mAliyunVodPlayerView.setLocalSource(localSource);
            }

        }
    }


    private void play() {
        if (StringUtils.isEmpty(getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL))) {
            OkGo.<String>get("http://testg2c.gamepku.com/userlib/aliyun/vodsts.php")
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String s = response.body().toString();
                            Gson gson = new Gson();
                            final AliyunVideoInfo info = gson.fromJson(s, AliyunVideoInfo.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String vid = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_VID);
                                    AliyunVidSts vidSts = new AliyunVidSts();
                                    vidSts.setVid(vid);//2869102e80b74ecd8f5c43edbfde4ac4
                                    vidSts.setAcId(info.getCredentials().getAccessKeyId());
                                    vidSts.setAkSceret(info.getCredentials().getAccessKeySecret());
                                    vidSts.setSecurityToken(info.getCredentials().getSecurityToken());
                                    mAliyunVodPlayerView.setVidSts(vidSts);
                                }
                            });
                        }
                    });
        } else {
            String url = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL);
            AliyunLocalSource.AliyunLocalSourceBuilder alsb = new AliyunLocalSource.AliyunLocalSourceBuilder();
            alsb.setSource(url);
            AliyunLocalSource localSource = alsb.build();
            mAliyunVodPlayerView.setLocalSource(localSource);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        playerPresenter.queryOrAgree(resId, "status", phone, unionid);//查询点赞状态
        updatePlayerViewMode();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onResume();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onStop();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }

    private void updatePlayerViewMode() {
        if (mAliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {//转为竖屏了。
                this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                //设置view的布局，宽高之类
//                LinearLayout.LayoutParams aliVcVideoViewLayoutParams = (LinearLayout.LayoutParams) mAliyunVodPlayerView.getLayoutParams();
//                aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
//                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView.getLayoutParams();
//                aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {//转到横屏了。
                //隐藏状态栏
                if (!isStrangePhone()) {
                    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    mAliyunVodPlayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
                //设置view的布局，宽高
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView.getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            }
        }
    }

    @Override
    protected void onDestroy() {

        if (type != null && type.equals("StudyFragment")) {
            uploadProgress();
        }

        if (mAliyunVodPlayerView != null) {
            mAliyunVodPlayerView.onDestroy();
            mAliyunVodPlayerView = null;
        }
        mScreenStatusController.stopListen();
        super.onDestroy();
        unbinder.unbind();
    }

    private void uploadProgress() {

        long duration = mAliyunVodPlayerView.getDuration();
        long progress = mAliyunVodPlayerView.getCurrentPosition();
        long time = System.currentTimeMillis();//获取系统时间的10位的时间戳
        String endtime = String.valueOf(time);
        Log.e("进度上传", progress / 1000 + "=====" + "总时长==" + duration / 1000);
        if (type.equals("StudyFragment")) {
            if (jindu != null && !jindu.equals("已学完") && jiakestatus.equals("1")) {

                if (Integer.valueOf(currentTime) < (progress / 1000)) {
                    shanghcuanjindu(progress, duration, endtime);
                }
                if (Integer.valueOf(currentTime) > (progress / 1000)) {
                    Log.e("测试的currentTime", currentTime);
                    shanghcuanjindu(Long.valueOf(currentTime) * 1000, duration, endtime);
                    Log.e("测试的currentTime2", Long.valueOf(currentTime) + "");
                }
            }
            if (jindu != null && jindu.equals("已学完") && jiakestatus.equals("1")) {
                shanghcuanjindu(duration, duration, endtime);
            }
        }
    }

    private void shanghcuanjindu(final long progress, final long duration, final String endTime) {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/setkcresjindu")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("resid", resId)
                        .params("kechengid", kechengid)
                        .params("restype", "2")
                        .params("endtime", endTime)
                        .params("currentTime", String.valueOf(progress / 1000))
                        .params("duration", String.valueOf(duration / 1000))
                        .params("unionid", unionid)
                        .params("phone", phone)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                BaseDataInfo baseDataInfo = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });
                                Log.e("上传进度结果new", str);

                                e.onNext(baseDataInfo);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo dataInfo) {
                        Log.e("上传进度发送", "true");
                        EventBus.getDefault().post(new MsgEvent("progressuploadsuccess"));
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAliyunVodPlayerView != null) {
            boolean handler = mAliyunVodPlayerView.onKeyDown(keyCode, event);
            if (!handler) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //解决某些手机上锁屏之后会出现标题栏的问题。
        VcPlayerLog.d("lfj1030", "onWindowFocusChanged = " + hasFocus);
        updatePlayerViewMode();
    }
}
