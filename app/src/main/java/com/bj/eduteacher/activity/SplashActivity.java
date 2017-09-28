package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.manager.ShareHelp;
import com.bj.eduteacher.presenter.LoginHelper;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.utils.AppUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.umeng.analytics.MobclickAgent;

/**
 * APP启动页
 */
public class SplashActivity extends BaseActivity {

    private IWXAPI api;
    private LoginHelper loginHelper;
    private String userPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loginHelper = new LoginHelper(this);
        // 禁止默认的页面统计方式，这样将不会再自动统计Activity
        MobclickAgent.openActivityDurationTrack(false);
        initView();
        registWx();

        // test
        // test();

        if (isShowGuide()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }, 1500);
        } else {
            intentToHomePage();
        }
    }

    private void test() {
        Intent intent = new Intent(this, CompleteUserInfoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        loginHelper.onDestory();
        super.onDestroy();
    }

    private void registWx() {
        ShareHelp.getInstance().init(getApplicationContext());
    }

    private void initView() {
        //可以将一下代码加到你的MainActivity中，或者在任意一个需要调用分享功能的activity当中
        ImageView imgSplash = (ImageView) this.findViewById(R.id.img_splash);
        Animation animation = new AlphaAnimation(0, 1.0f);
        animation.setDuration(1500);

        animation.setFillAfter(true);
        imgSplash.startAnimation(animation);
    }

    /**
     * 判断是否显示启动页
     *
     * @return
     */
    private boolean isShowGuide() {
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
     * 跳转到首页
     */
    private void intentToHomePage() {
        userPhoneNumber = PreferencesUtils.getString(SplashActivity.this, MLProperties.PREFER_KEY_USER_ID, "");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int loginStatus = PreferencesUtils.getInt(SplashActivity.this, MLProperties.PREFER_KEY_LOGIN_STATUS);
                if (loginStatus != 1 || StringUtils.isEmpty(userPhoneNumber)) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                    return;
                }
                // 判断登录时间
                long lastLoginTime = PreferencesUtils.getLong(SplashActivity.this, MLProperties.PREFER_KEY_LOGIN_Time, 0);
                if (lastLoginTime == 0 || !checkLoginTimeSpan(lastLoginTime)) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                    return;
                }
                // 判断腾讯云互动直播的相关信息是否需要重新登录
                String sxbSig = getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_SIG, "");
                String sxbUserID = PreferencesUtils.getString(SplashActivity.this, MLProperties.PREFER_KEY_USER_SXB_User, "");
                LL.i("sxbSig：" + sxbSig);
                if (StringUtils.isEmpty(sxbSig) || StringUtils.isEmpty(sxbUserID)) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                    return;
                }

                // 环信登录状态被取消
                login2Ease(userPhoneNumber);
            }
        }, 1500);
    }

    private void login2Ease(String userPhoneNumber) {
        String userEaseID = MLConfig.EASE_TEACHER_ID_PREFIX + userPhoneNumber;
        EMClient.getInstance().login(userEaseID, "123456", new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                // 加载环信相关数据
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("way", "登录聊天服务器成功！");

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("way", "登录聊天服务器失败！");
                // 跳转到登录页面
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        });
    }

    private boolean checkLoginTimeSpan(long lastLoginTime) {
        long currTime = System.currentTimeMillis();
        int span = (int) (currTime - lastLoginTime) / 1000 / 60 / 60 / 24;

        if (span <= MLConfig.KEEP_LOGIN_TIME_LENGTH) {
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
