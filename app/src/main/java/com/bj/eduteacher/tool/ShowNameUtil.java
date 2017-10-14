package com.bj.eduteacher.tool;

import android.widget.TextView;

import com.bj.eduteacher.utils.StringUtils;

/**
 * Created by zz379 on 2017/10/12.
 */

public class ShowNameUtil {

    public static boolean showNameLogic(TextView tv, String nickname, String realname, String phoneNumber) {
        if (!StringUtils.isEmpty(nickname)) {
            tv.setText(nickname);
            return true;
        } else if (!StringUtils.isEmpty(realname)) {
            tv.setText(realname);
            return true;
        } else if (!StringUtils.isEmpty(phoneNumber)) {
            tv.setText(phoneNumber);
            return true;
        } else {
            tv.setText("");
            return false;
        }
    }

    public static String getFirstNotNullParams(String... params) {
        for (String s : params) {
            if (!StringUtils.isEmpty(s)) {
                return s;
            }
        }
        return "";
    }
}
