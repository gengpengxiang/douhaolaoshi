package com.bj.eduteacher.manager;

import android.content.Context;
import android.util.Log;

import com.bj.eduteacher.zzeaseui.controller.EaseUI;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;

/**
 * Created by zz379 on 2017/5/15.
 */

public class IMHelper {

    private static final String TAG = "IMHelper";

    private EaseUI easeUI;

    private static IMHelper instance = null;

    private Context appContext;

    private IMHelper() {

    }

    public synchronized static IMHelper getInstance() {
        if (instance == null) {
            instance = new IMHelper();
        }
        return instance;
    }

    public void init(Context context) {
        EMOptions options = initChatOptions();
        //use default options if options is null
        if (EaseUI.getInstance().init(context, options)) {
            appContext = context;
            //debug mode, you'd better set it to false, if you want release your App officially.
            EMClient.getInstance().setDebugMode(true);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //to set user's profile and avatar
            setEaseUIProviders();
        }
    }

    private EMOptions initChatOptions() {
        Log.d(TAG, "init HuanXin Options");

        EMOptions options = new EMOptions();
        // 设置自动登录，默认为true
        options.setAutoLogin(true);
        // set if accept the invitation automatically
        options.setAcceptInvitationAlways(true);
        // 获取是否自动接受加群邀请
        options.setAutoAcceptGroupInvitation(true);
        // 是否按照server收到时间进行排序 默认是false
        options.setSortMessageByServerTime(false);
        //设置是否需要接受方已读确认 缺省 true 如果设为true，会要求消息的接受方发送已读回执
        options.setRequireAck(true);
        //设置是否需要接受方送达确认,默认false, 如果设为true，会要求消息的接受方发送送达回执。
        options.setRequireDeliveryAck(false);

        return options;
    }

    protected void setEaseUIProviders() {

    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }
}
