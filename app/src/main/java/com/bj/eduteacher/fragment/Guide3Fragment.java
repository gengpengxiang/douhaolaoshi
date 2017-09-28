package com.bj.eduteacher.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.LoginActivity;
import com.bj.eduteacher.activity.MainActivity;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.tool.Constants;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_3, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.tv_enter)
    void onClickEnter() {
        intentToHomePage();
    }

    /**
     * 跳转到首页
     */
    private void intentToHomePage() {
        int loginStatus = PreferencesUtils.getInt(getActivity(), MLProperties.PREFER_KEY_LOGIN_STATUS);
        String userPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
        if (loginStatus != 1 || StringUtils.isEmpty(userPhoneNumber)) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return;
        }
        // 判断登录时间
        long lastLoginTime = PreferencesUtils.getLong(getActivity(), MLProperties.PREFER_KEY_LOGIN_Time, 0);
        if (lastLoginTime == 0 || !checkLoginTimeSpan(lastLoginTime)) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return;
        }
        // 判断腾讯云互动直播的相关信息是否需要重新登录
        String sxbSig = getActivity().getSharedPreferences(Constants.USER_INFO, 0).getString(Constants.USER_SIG, "");
        if (StringUtils.isEmpty(sxbSig)) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return;
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

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("way", "登录聊天服务器失败！");
                // 跳转到登录页面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
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
