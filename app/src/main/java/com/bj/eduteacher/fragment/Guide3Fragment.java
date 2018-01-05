package com.bj.eduteacher.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.MainActivity;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.presenter.LoginHelper;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zz379 on 2017/8/11.
 */

public class Guide3Fragment extends Fragment {

    private Handler mHandler = new Handler();
    private LoginHelper loginHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_3, container, false);
        ButterKnife.bind(this, view);
        loginHelper = new LoginHelper(getActivity());
        return view;
    }

    @OnClick(R.id.tv_enter)
    void onClickEnter() {
        // intentToHomePage();
        toMainActivity();
    }

    private void toMainActivity() {
        // 判断登录时间，看是否需要重新登录
        long lastLoginTime = PreferencesUtils.getLong(getActivity(), MLProperties.PREFER_KEY_LOGIN_Time, 0);
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

    /**
     * 1500毫秒后跳转到首页
     */
    private void intentToMainActivity() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }, 1500);
    }

    /**
     * 跳转到首页
     */
    private void intentToHomePage() {
        String userPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
        int loginStatus = PreferencesUtils.getInt(getActivity(), MLProperties.PREFER_KEY_LOGIN_STATUS);
        if (loginStatus != 1 || StringUtils.isEmpty(userPhoneNumber)) {
            cleanAllPreferencesData();
            intentToMainActivity();
            return;
        }
        // 判断腾讯云互动直播的相关信息是否需要重新登录
        final String sxbSig = getActivity().getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_SIG, "");
        final String sxbUserID = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_SXB_User, "");
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

        intentToMainActivity();
        // 跳过环信登录检测
        // login2Ease(userPhoneNumber);
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

    private boolean isLoginAgain(long lastLoginTime) {
        long currTime = System.currentTimeMillis();
        int span = (int) (currTime - lastLoginTime) / 1000 / 60 / 60 / 24;

        if (span >= MLConfig.KEEP_LOGIN_TIME_LENGTH) {
            return true;
        }

        return false;
    }

    private void cleanAllPreferencesData() {
        // 清除所有app内的数据
        PreferencesUtils.cleanAllData(getActivity());
        // 清除直播设置数据
        getActivity().getSharedPreferences("data", Context.MODE_PRIVATE).edit().clear().commit();
        // 清除直播个人数据
        getActivity().getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE).edit().clear().commit();
        // 清除环信数据
        getActivity().getSharedPreferences("EM_SP_AT_MESSAGE", Context.MODE_PRIVATE).edit().clear().commit();
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("guide3");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("guide3");
    }
}
