package com.bj.eduteacher.presenter;

/**
 * 页面展示逻辑基类
 */
public abstract class Presenter {

    //销去持有外部的mContext;
    public abstract void onDestory();
}