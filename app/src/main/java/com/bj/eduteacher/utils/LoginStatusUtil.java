package com.bj.eduteacher.utils;

import android.content.Context;

import com.bj.eduteacher.api.MLProperties;

/**
 * Created by Administrator on 2018/5/18 0018.
 */

public class LoginStatusUtil {

    public static boolean noLogin(Context context) {
        if (PreferencesUtils.getString(context, MLProperties.PREFER_KEY_USER_ID) == null && PreferencesUtils.getString(context, MLProperties.PREFER_KEY_WECHAT_UNIONID) == null) {
            return true;
        }else {
            return false;
        }
    }
}
