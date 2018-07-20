package com.aliyun.vodplayerview.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/*
 * Copyright (C) 2010-2018 Alibaba Group Holding Limited.
 */

/**
 * 屏幕相关的操作类
 */
public class ScreenUtils {
    /**
     * 获取宽度
     *
     * @param activity 上下文
     * @return 宽度值，px
     */
    public static int getWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取高度
     *
     * @param activity 上下文
     * @return 高度值，px
     */
    public static int getHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 是否在屏幕右侧
     *
     * @param activity 上下文
     * @param xPos     位置的x坐标值
     * @return true：是。
     */
    public static boolean isInRight(Activity activity, int xPos) {
        return (xPos > getWidth(activity) / 2);
    }

    /**
     * 是否在屏幕左侧
     *
     * @param activity 上下文
     * @param xPos     位置的x坐标值
     * @return true：是。
     */
    public static boolean isInLeft(Activity activity, int xPos) {
        return (xPos < getWidth(activity) / 2);
    }

}
