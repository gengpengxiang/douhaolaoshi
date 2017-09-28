package com.bj.eduteacher;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bj.eduteacher.dialog.UpdateAPPAlertDialog;
import com.bj.eduteacher.widget.LoadingDialog;


/**
 * Created by zz379 on 2017/3/31.
 */

public abstract class BaseFragment extends Fragment {

    protected Activity mContext;
    protected boolean isFirst = true;
    protected View rootView;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new LoadingDialog(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return loadViewLayout(inflater, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        rootView = view;
        initView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // MyApplication.getRefWatcher(getActivity()).watch(this);
    }

    /**
     * 获取控件
     *
     * @param id  控件的id
     * @param <E>
     * @return
     */
    protected <E extends View> E get(int id) {
        return (E) rootView.findViewById(id);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            onVisible();
        } else {
            onInVisible();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
        } else {
            onInVisible();
        }
    }

    /**
     * 当界面可见时的操作
     */
    protected void onVisible() {
        if (isFirst) {
            lazyLoad();
            isFirst = false;
        }
    }

    /**
     * 数据懒加载
     */
    protected void lazyLoad() {

    }

    /**
     * 当界面不可见时的操作
     */
    protected void onInVisible() {

    }

    /**
     * 初始化界面
     *
     * @param view
     */
    private void initView(View view) {
        bindViews(view);
        setListener();
        processLogic();
    }

    /**
     * 加载布局
     */
    protected abstract View loadViewLayout(LayoutInflater inflater, ViewGroup container);

    /**
     * find控件
     *
     * @param view
     */
    protected abstract void bindViews(View view);

    /**
     * 处理数据
     */
    protected abstract void processLogic();

    /**
     * 设置监听
     */
    protected abstract void setListener();


    /**
     * 显示加载对话框
     */
    public void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void createUpdateAppDialog(String title, String content, UpdateAPPAlertDialog.OnSweetClickListener confirmListener,
                                      UpdateAPPAlertDialog.OnSweetClickListener cancelListener,
                                      UpdateAPPAlertDialog.OnSweetClickListener cancelDownloadListener) {
        UpdateAPPAlertDialog dialog = new UpdateAPPAlertDialog(getActivity());
        dialog.setTitleText(title);
        dialog.setContentText(content);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setConfirmClickListener(confirmListener);
        dialog.setCancelClickListener(cancelListener);
        dialog.setCancelDownloadListener(cancelDownloadListener);
        dialog.show();
    }
}
