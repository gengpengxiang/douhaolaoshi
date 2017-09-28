package com.bj.eduteacher.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.view.MyJCView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zz379 on 2017/8/30.
 */

public class ResPlayActivity extends BaseActivity {

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

    private MyJCView mPlayer;

    private String resID;
    private String resUrl;
    private String resName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.black));
        }
        setContentView(R.layout.layout_jc_player);
        ButterKnife.bind(this);

        initView();
        initData();

        test();
    }

    private void test() {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                llBottomBar.setVisibility(View.VISIBLE);
                tvReadNumber.setText("12" + "次阅读");
                tvAgreeNumber.setText("20");
                tvCommentNumber.setText("43");
            }
        }, 2000);
    }

    private void initView() {
        resID = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID);
        resName = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME);
        resUrl = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL);

        mPlayer = (MyJCView) findViewById(R.id.mPlayer);
        mPlayer.setBackListener(new MyJCView.BackListener() {
            @Override
            public void onBackClick() {
                onBackPressed();
            }
        });
        mPlayer.setUp(resUrl, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, resName);
        // 设置全屏前后的屏幕方向
        JCVideoPlayer.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        JCVideoPlayer.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        mPlayer.startButton.performClick();
    }

    private void initData() {
        // 增加阅读数量
        getResPreviewNumber();
    }

    @OnClick(R.id.ll_agreeNumber)
    void clickAgree() {
        T.showShort(this, "点赞！");
    }

    @OnClick(R.id.ll_commentNumber)
    void clickComment() {
        Intent intent = new Intent(this, ResCommentActivity.class);
        intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, "0");
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    private void getResPreviewNumber() {
        if (!NetUtils.isConnected(this)) {
            T.showShort(this, "无法连接到网络，请检查您的网络设置");
            hideLoadingDialog();
            return;
        }
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                LmsDataService service = new LmsDataService();
                service.addResourcePreviewNumber(resID);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
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
}
