package com.bj.eduteacher.manager;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.DoukeDetailActivity;
import com.bj.eduteacher.activity.RankListActivity;
import com.bj.eduteacher.activity.SplashActivity;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.common.inter.ITagManager;
import com.umeng.message.entity.UMessage;
import com.umeng.message.tag.TagManager;

import java.util.List;
import java.util.Map;

/**
 * Created by zz379 on 2017/3/27.
 */

public class UMPushManager {

    private static final String UM_MESSAGE_TYPE_NORMAL = "Normal";
    private static final String UM_MESSAGE_TYPE_RANK = "RankList";
    private static final String UM_MESSAGE_TYPE_DOUKE = "DouKe";
    private static final String ALIAS_TYPE_PARENT = "jiaoshi";

    private static UMPushManager instance;
    private PushAgent mPushAgent;

    public static UMPushManager getInstance() {
        if (instance == null) {
            instance = new UMPushManager();
        }
        return instance;
    }

    private UMPushManager() {
    }

    public PushAgent getmPushAgent() {
        return mPushAgent;
    }

    public void initUmeng(PushAgent pushAgent) {
        mPushAgent = pushAgent;
        // 设置开发模式
        mPushAgent.setDebugMode(true);
        // 应用前台时是否显示通知
        mPushAgent.setNotificaitonOnForeground(true);
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String s) {
                //注册成功会返回device token
                Log.i("way", "UM_token:" + s);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
        mPushAgent.setMessageHandler(messageHandler);
        mPushAgent.setNoDisturbMode(23, 0, 7, 0);
        mPushAgent.setMuteDurationSeconds(1);
        mPushAgent.setDisplayNotificationNumber(5);
    }

    /**
     * 绑定别名
     *
     * @param userPhoneNumber
     */
    public void setPushAlias(String userPhoneNumber) {
        if (mPushAgent != null && !StringUtils.isEmpty(mPushAgent.getRegistrationId())) {
            mPushAgent.addAlias(userPhoneNumber, ALIAS_TYPE_PARENT, new CallBackAddAlias(userPhoneNumber));
        }
    }

    /**
     * 删除别名
     *
     * @param userPhoneNumber
     */
    public void removePushAlias(String userPhoneNumber) {
        if (mPushAgent != null && !StringUtils.isEmpty(mPushAgent.getRegistrationId())) {
            mPushAgent.removeAlias(userPhoneNumber, ALIAS_TYPE_PARENT, new UTrack.ICallBack() {
                @Override
                public void onMessage(boolean b, String s) {

                }
            });
        }
    }

    /**
     * 友盟首页添加标签
     *
     * @param schoolId
     */
    public void addTag(final String schoolId) {
        if (mPushAgent != null) {
            mPushAgent.getTagManager().list(new TagManager.TagListCallBack() {
                @Override
                public void onMessage(boolean isSuccess, List<String> list) {
                    if (isSuccess) {
                        // 获取标签成功
                        if (list.contains(schoolId)) {
                            // 已经包含了该标签
                            LL.i("该标签已经添加过了..." + schoolId);
                        } else {
                            // 没有包含该标签
                            mPushAgent.getTagManager().add(new TagManager.TCallBack() {
                                @Override
                                public void onMessage(boolean b, ITagManager.Result result) {
                                    if (b) {
                                        // 添加标签成功
                                        LL.i("添加标签成功..." + schoolId);
                                    }
                                }
                            }, schoolId);
                        }
                    } else {
                        // 获取标签失败
                        mPushAgent.getTagManager().update(new TagManager.TCallBack() {
                            @Override
                            public void onMessage(boolean b, ITagManager.Result result) {
                                if (b) {
                                    LL.i("更新用户标签成功..." + schoolId);
                                }
                            }
                        }, schoolId);
                    }
                }
            });
        }
    }

    /**
     * 友盟推出账号时清空所有标签
     *
     * @param schoolID
     */
    public void removeAllTag(final String schoolID) {
        if (mPushAgent != null) {
            mPushAgent.getTagManager().reset(new TagManager.TCallBack() {
                @Override
                public void onMessage(boolean isSuccess, ITagManager.Result result) {
                    if (isSuccess) {
                        LL.i("清除用户所有标签成功");
                    } else {
                        LL.i("清除用户所有标签失败");
                        // 如果清空所有标签失败的情况
                        mPushAgent.getTagManager().delete(new TagManager.TCallBack() {
                            @Override
                            public void onMessage(boolean b, ITagManager.Result result) {

                            }
                        }, schoolID);
                    }
                }
            });
        }
    }

    class CallBackAddAlias implements UTrack.ICallBack {

        String userPhoneNumber;

        public CallBackAddAlias(String userPhoneNumber) {
            this.userPhoneNumber = userPhoneNumber;
        }

        @Override
        public void onMessage(boolean isSuccess, String s) {
            if (isSuccess) {
                Log.i("way", "绑定Alias成功：" + s + " devices_token:" + mPushAgent.getRegistrationId());
            } else {
                Log.i("way", "绑定Alias失败：" + s);
                // 先删除原来的别名
                mPushAgent.removeAlias(userPhoneNumber, ALIAS_TYPE_PARENT, new CallBackRemoveAlias(userPhoneNumber));
            }
        }
    }

    class CallBackRemoveAlias implements UTrack.ICallBack {

        String userPhoneNumber;

        public CallBackRemoveAlias(String userPhoneNumber) {
            this.userPhoneNumber = userPhoneNumber;
        }

        @Override
        public void onMessage(boolean isSuccess, String s) {
            if (isSuccess) {
                Log.i("way", "删除Alias成功：" + s);
                // 在添加新的别名
                mPushAgent.addAlias(userPhoneNumber, ALIAS_TYPE_PARENT, new UTrack.ICallBack() {
                    @Override
                    public void onMessage(boolean isSuccess, String s) {
                        if (isSuccess) {
                            Log.i("way", "绑定Alias成功：" + s);
                        } else {
                            Log.i("way", "绑定Alias失败：" + s);
                        }
                    }
                });
            } else {
                Log.i("way", "删除Alias失败：" + s);
            }
        }
    }

    UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
        @Override
        public void dealWithCustomAction(Context context, UMessage uMessage) {
            int loginStatus = PreferencesUtils.getInt(context, MLProperties.PREFER_KEY_LOGIN_STATUS, 0);
            if (loginStatus != 0) {
                if (uMessage.custom != null && uMessage.custom.equals(UM_MESSAGE_TYPE_RANK)) {
                    Intent intent = new Intent(context, RankListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if (uMessage.custom != null && uMessage.custom.equals(UM_MESSAGE_TYPE_DOUKE)) {
                    Intent intent = new Intent(context, DoukeDetailActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    // 设置文章的链接
                    for (Map.Entry<String, String> entry : uMessage.extra.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if (key.equals("link")) {
                            intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_URL, value);
                        } else if (key.equals("newsid")) {
                            intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, value);
                        }
                    }
                    context.startActivity(intent);
                } else {
                    super.dealWithCustomAction(context, uMessage);
                }
            } else {
                T.showShort(context, "请先登录在进行查看！");
                Intent intent = new Intent(context, SplashActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        }
    };

    UmengMessageHandler messageHandler = new UmengMessageHandler() {
        @Override
        public Notification getNotification(Context context, UMessage uMessage) {

            // 自定义通知的展示形式
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle(uMessage.title)
                    .setContentText(uMessage.text)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setTicker(uMessage.ticker)
                    .setAutoCancel(true);
            return builder.build();
        }
    };
}
