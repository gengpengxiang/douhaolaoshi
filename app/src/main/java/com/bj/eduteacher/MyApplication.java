package com.bj.eduteacher;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDexApplication;

import com.bj.eduteacher.manager.IMHelper;
import com.bj.eduteacher.manager.UMPushManager;
import com.bj.eduteacher.presenter.InitBusinessHelper;
import com.bj.eduteacher.tool.SxbLogImpl;
import com.bj.eduteacher.utils.FrescoImagePipelineConfigFactory;
import com.bj.eduteacher.utils.FrescoUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.zzautolayout.config.AutoLayoutConifg;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.umeng.message.PushAgent;

import java.util.List;

/**
 * Created by zz379 on 2017/9/6.
 */

public class MyApplication extends MultiDexApplication {

    private static MyApplication app;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        context = getApplicationContext();

        initFresco();
        // 默认使用的高度是设备的可用高度，也就是不包括状态栏和底部的操作栏的，如果你希望拿设备的物理高度进行百分比化：
        AutoLayoutConifg.getInstance().useDeviceSize();
        // 初始化友盟的相关操作
        initUMPush();
        // 初始化EaseUI
        IMHelper.getInstance().init(this);

        if (shouldInit()) {
            SxbLogImpl.init(getApplicationContext());
            //初始化APP
            InitBusinessHelper.initApp(context);
        }
    }

    private void initUMPush() {
        // 初始化友盟推送相关信息
        LL.i("制造商：" + Build.MANUFACTURER);
        if (!Build.MANUFACTURER.equals("Xiaomi")) {
            LL.i("初始化友盟推送");
        } else {
            // MIUI系统
            LL.i("初始化小米推送");
        }

        UMPushManager manager = UMPushManager.getInstance();
        PushAgent pushAgent = PushAgent.getInstance(this);
        manager.initUmeng(pushAgent);
    }

    /**
     * 初始化FaceBook的Fresco框架
     */
    private void initFresco() {
        Fresco.initialize(this, FrescoImagePipelineConfigFactory.getOkHttpImagePipelineConfig(this));
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();

        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static Context getContext() {
        return context;
    }

    public static MyApplication getInstance() {
        return app;
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        FrescoUtils.TrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        FrescoUtils.clearAllMemoryCaches();
    }
}
