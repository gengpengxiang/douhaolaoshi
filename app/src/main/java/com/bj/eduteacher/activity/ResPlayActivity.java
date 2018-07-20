/*
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
import android.widget.VideoView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.course.fragment.study.NextRes;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.media.IjkVideoManager;
import com.bj.eduteacher.media.controller.MediaController;
import com.bj.eduteacher.media.videoview.IjkVideoView;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import tv.danmaku.ijk.media.player.IMediaPlayer;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

*/
/**
 * Created by zz379 on 2017/8/30.
 * 资源播放页面 该页面使用的是bilibili开源播放器，目前正在使用
 *//*


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
    private String unionid;
    private String kechengid, currentTime;
    private String type;
    private String jiakestatus = "";
    private String jindu = "";
    private boolean isPlayComplete = false;
    private boolean isNext = false;
    private int agreeStatus = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
        }
        setContentView(R.layout.activity_res_play_bilibili);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        service = new LmsDataService();
        initStatus();
        initView();

        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID);
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID);
        //查询点赞状态
        chaxunAgreeStatus("status");

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
        //add
        type = getIntent().getStringExtra("type");

        resID = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID);
        resName = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME);
        resUrl = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL);

        Log.e("resid111", resID);

        if (type!=null&&!type.equals("free")) {
            kechengid = getIntent().getStringExtra("kechengid");
            currentTime = getIntent().getStringExtra("currentTime");
            jiakestatus = getIntent().getStringExtra("jiakestatus");
            jindu = getIntent().getStringExtra("jindu");
        }

        mIjkVideoView = (IjkVideoView) findViewById(R.id.video_view);
        mediaController = new MediaController(this);
        mediaController.setVideoName(resName);    // 设置标题
        mediaController.setShowThumb(true);
        mediaController.setPreparedPlay(true);
        mediaController.setTopBackButtonClickListener(this);    // 设置返回按钮监听
        mediaController.setFullScreenChangeListener(this);
        mIjkVideoView.setMediaController(mediaController);

        mIjkVideoView.setVideoPath(resUrl);

        if (!type.equals("free")) {
            if (jiakestatus.equals("1")) {
                IjkVideoManager.getInstance().seekTo(Integer.valueOf(currentTime) * 1000);
            }
        }
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
        Log.e("resume", ",mmk");
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID);
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID);
        //查询点赞状态
        chaxunAgreeStatus("status");
    }

    @OnClick(R.id.ll_commentNumber)
    void clickComment() {
        Intent intent = new Intent(this, ResCommentActivity.class);
        intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, resID);
        startActivityForResult(intent, 1);
    }


    @OnClick(R.id.ll_agreeNumber)
    void clickAgree() {

        MobclickAgent.onEvent(this, "article_like");
        // 点赞评论需要登录
        if (LoginStatusUtil.noLogin(this)) {
            IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
            return;
        }
        if (agreeStatus == 1) {
            chaxunAgreeStatus("del");
        }
        if (agreeStatus == 0) {
            chaxunAgreeStatus("add");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaController.isPlayingOnTV) {
            mediaController.isPlayingOnTV = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        IjkVideoManager.getInstance().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        long duration = IjkVideoManager.getInstance().getDuration();
        long progress = IjkVideoManager.getInstance().getCurrentPlayPosition();

        long time = System.currentTimeMillis();//获取系统时间的10位的时间戳

        String endtime = String.valueOf(time);

        Log.e("进度上传", progress / 1000 + "=====" + "总时长==" + duration / 1000);

        if (!type.equals("free")) {
            if (jindu != null && !jindu.equals("已学完") && jiakestatus.equals("1")) {

//                if (jindu.equals("test")) {
//                    shanghcuanjindu(progress, duration, endtime);
//                }
                if (Integer.valueOf(currentTime) < (progress / 1000)){
                    shanghcuanjindu(progress, duration, endtime);
                }
                if (Integer.valueOf(currentTime) > (progress / 1000)){
                    Log.e("测试的currentTime",currentTime);
                    shanghcuanjindu(Long.valueOf(currentTime)*1000, duration, endtime);
                    Log.e("测试的currentTime2",Long.valueOf(currentTime)+"");
                }
//                if (Integer.valueOf(currentTime) < (progress / 1000))
//                    if (!isPlayComplete) {
//                        shanghcuanjindu(progress, duration, endtime);
//                    }
            }
            if (jindu != null && jindu.equals("已学完") && jiakestatus.equals("1")) {
                shanghcuanjindu(duration, duration, endtime);
            }
//            if (jindu != null && jindu.equals("未学完") && jiakestatus.equals("1")) {
//                if (Integer.valueOf(currentTime) < (progress / 1000))
//                    if (!isPlayComplete) {
//                        shanghcuanjindu(progress, duration, endtime);
//                    }
//            }
        }
        EventBus.getDefault().post(new MsgEvent("playfinish"));

        IjkVideoManager.getInstance().release();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("playComplete")) {
            if (!type.equals("free")) {
                isPlayComplete = true;
                long duration = event.getDuration();
                if (jiakestatus.equals("1")) {
                    String endtime = String.valueOf(System.currentTimeMillis());
                    shanghcuanjindu(duration, duration, endtime);
                    playNext(resID, kechengid);
                }
//                playNext(resID, kechengid);
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
                        .params("phone", teacherPhoneNumber)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("结果", str);
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
                            resID = nextRes.getData().getNext_resid();

                            chaxunAgreeStatus("status");
                            isNext = true;

                            getResPreviewNumber();

                            mIjkVideoView.setVideoPath(nextRes.getData().getNext_previewurl());
                            if (!type.equals("free")) {
                                if (jiakestatus.equals("1")) {
                                    jindu = "test";
                                    currentTime = nextRes.getData().getNext_currentTime();
                                    IjkVideoManager.getInstance().seekTo(Integer.valueOf(nextRes.getData().getNext_currentTime()) * 1000);
                                }
                            }
                            // 自动播放
//                            mediaController.mBtnPlay.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mediaController.mBtnPlay.performClick();
//                                }
//                            });

                            //T.showShort(ResPlayActivity.this,"下一个视频id="+nextRes.getData().getNext_resid());
                        }
                        if (nextRes.getRet().equals("2")) {
                            T.showShort(ResPlayActivity.this, "已是最后一个视频");
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

    private void shanghcuanjindu(final long progress, final long duration, final String endTime) {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/setkcresjindu")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("resid", resID)
                        .params("kechengid", kechengid)
                        .params("restype", "2")
                        .params("endtime", endTime)
                        .params("currentTime", String.valueOf(progress / 1000))
                        .params("duration", String.valueOf(duration / 1000))
                        .params("unionid", unionid)
                        .params("phone", teacherPhoneNumber)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                BaseDataInfo baseDataInfo = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });
                                Log.e("上传进度结果", str);

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
                        tvReadNumber.setText(articleInfo.getReadNumber() + "次播放");

                        //add
//                        if (isNext) {
//                            Log.e("Title", articleInfo.getTitle());
//                            mediaController.changeTitle(articleInfo.getTitle());
//                        }
                        mediaController.changeTitle(articleInfo.getTitle());


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

    private void chaxunAgreeStatus(final String type) {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "ziyuan/dianzan")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("ziyuanid", resID)
                        .params("caozuo", type)
                        .params("userphone", teacherPhoneNumber)
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                BaseDataInfo info = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });

                                e.onNext(info);
                                e.onComplete();
                            }
                        });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(BaseDataInfo info) {
                        if (type.equals("status")) {
                            if (info.getRet().equals("3")) {
                                if (info.getData().equals("1")) {
                                    ivAgree.setImageResource(R.mipmap.ic_liked);

                                    agreeStatus = 1;
                                } else {
                                    ivAgree.setImageResource(R.mipmap.ic_like);
                                    agreeStatus = 0;
                                }
                            }
                        }
                        if (type.equals("add")) {
                            if (info.getRet().equals("1")) {
                                agreeStatus = 1;
                                chaxunAgreeStatus("status");
                            }

                        }
                        if (type.equals("del")) {
                            if (info.getRet().equals("2")) {
                                //ivAgree.setImageResource(R.mipmap.ic_liked);
                                agreeStatus = 0;
                                chaxunAgreeStatus("status");
                            }

                        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 300) {
            if (requestCode == 1) {
                Log.e("评论返回", "true");
                getResNumbers();
            }
        }
    }

}
*/
