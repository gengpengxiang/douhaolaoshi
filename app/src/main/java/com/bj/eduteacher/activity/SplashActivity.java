package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.login.view.LoginSelectActivity;
import com.bj.eduteacher.manager.ShareHelp;
import com.bj.eduteacher.utils.AppUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.umeng.analytics.MobclickAgent;

/**
 * APP启动页
 */
public class SplashActivity extends BaseActivity {

    private IWXAPI api;

    private Handler mHandler = new Handler();
    private boolean isShowGuideByAdmin = false;     // 管理员直接决定是否要显示导航页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 禁止默认的页面统计方式，这样将不会再自动统计Activity
        MobclickAgent.openActivityDurationTrack(false);

        initToolBar();
        initView();
        registWx();

        initData();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
    }

    @Override
    protected void initData() {
        // 首先判断是否要进入导航页
        if (isShowGuide()) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }, 1500);
        } else {
            intentToMainActivity();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void registWx() {
        ShareHelp.getInstance().init(getApplicationContext());
    }

    @Override
    protected void initView() {
        ImageView imgSplash = (ImageView) this.findViewById(R.id.img_splash);
        TextView tvTitle = (TextView) this.findViewById(R.id.tv_title);
        Animation animation = new AlphaAnimation(0, 1.0f);
        animation.setStartTime(0);
        animation.setDuration(1500);
        animation.setFillAfter(true);
        imgSplash.startAnimation(animation);

        Animation animation2 = new AlphaAnimation(0, 1.0f);
        animation2.setStartTime(500);
        animation2.setDuration(1000);
        animation2.setFillAfter(true);
        tvTitle.startAnimation(animation2);
    }

    /**
     * 判断是否显示启动页
     *
     * @return
     */
    private boolean isShowGuide() {
        // 如果管理员决定当前版本不再显示导航页，则直接跳过后面的检查
        if (!isShowGuideByAdmin) {
            return isShowGuideByAdmin;
        }
        String oldVersion = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_VERSION_CODE, "");
        String currVersion = AppUtils.getVersionName(this);
        if (StringUtils.isEmpty(oldVersion) || !oldVersion.equals(currVersion)) {
            // 保存当前版本号 
            PreferencesUtils.putString(this, MLProperties.PREFER_KEY_VERSION_CODE, currVersion);
            return true;
        } else {
            return false;
        }
    }
    /**
     * 1500毫秒后跳转到首页
     */
    private void intentToMainActivity() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                if(PreferencesUtils.getString(SplashActivity.this, MLProperties.PREFER_KEY_USER_ID)!=null){
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }else {
//                    Intent intent = new Intent(SplashActivity.this, LoginSelectActivity.class);
//                    startActivity(intent);
//                }
                //改动
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 1500);
    }

    private boolean isLoginAgain(long lastLoginTime) {
        long currTime = System.currentTimeMillis();
        int span = (int) (currTime - lastLoginTime) / 1000 / 60 / 60 / 24;

        if (span >= MLConfig.KEEP_LOGIN_TIME_LENGTH) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("qidongye");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("qidongye");
        MobclickAgent.onPause(this);
    }
}
