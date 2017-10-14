package com.bj.eduteacher.activity;

import android.content.Context;
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

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loginHelper = new LoginHelper(this);
        // 禁止默认的页面统计方式，这样将不会再自动统计Activity
        MobclickAgent.openActivityDurationTrack(false);
        initView();
        registWx();

        initData();
    }

    private void initData() {
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
            // 判断登录时间，看是否需要重新登录
            long lastLoginTime = PreferencesUtils.getLong(SplashActivity.this, MLProperties.PREFER_KEY_LOGIN_Time, 0);
            if (lastLoginTime == 0 || isLoginAgain(lastLoginTime)) {
                LL.i("登录超时，需要重新登录");
                // 清空所有Preferences数据
                cleanAllPreferencesData();
                // 跳转到首页
                intentToMainActivity();
            } else {
                intentToHomePage();
            }
        }
    }

    private void cleanAllPreferencesData() {
        // 清除所有app内的数据
        PreferencesUtils.cleanAllData(this);
        // 清除直播设置数据
        getSharedPreferences("data", Context.MODE_PRIVATE).edit().clear().commit();
        // 清除直播个人数据
        getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE).edit().clear().commit();
        // 清除环信数据
        getSharedPreferences("EM_SP_AT_MESSAGE", Context.MODE_PRIVATE).edit().clear().commit();
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
        String userPhoneNumber = PreferencesUtils.getString(SplashActivity.this, MLProperties.PREFER_KEY_USER_ID, "");
        int loginStatus = PreferencesUtils.getInt(SplashActivity.this, MLProperties.PREFER_KEY_LOGIN_STATUS);
        if (loginStatus != 1 || StringUtils.isEmpty(userPhoneNumber)) {
            cleanAllPreferencesData();
            intentToMainActivity();
            return;
        }
        // 判断腾讯云互动直播的相关信息是否需要重新登录
        final String sxbSig = getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_SIG, "");
        final String sxbUserID = PreferencesUtils.getString(SplashActivity.this, MLProperties.PREFER_KEY_USER_SXB_User, "");
        LL.i("sxbSig：" + sxbSig);
        if (StringUtils.isEmpty(sxbSig) || StringUtils.isEmpty(sxbUserID)) {
            cleanAllPreferencesData();
            intentToMainActivity();
            return;
        } else {
            // 开一个子线程，登录直播功能
            new Thread(new Runnable() {
                @Override
                public void run() {
                    loginHelper.iLiveLogin(sxbUserID, sxbSig);
                }
            }).start();
        }
        // 环信登录状态被取消
        login2Ease(userPhoneNumber);
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

                intentToMainActivity();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("way", "登录聊天服务器失败！");
                // 跳转到登录页面
                intentToMainActivity();
            }
        });
    }

    /**
     * 1500毫秒后跳转到首页
     */
    private void intentToMainActivity() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
