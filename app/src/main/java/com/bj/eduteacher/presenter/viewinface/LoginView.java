package com.bj.eduteacher.presenter.viewinface;


/**
 * 登录回调
 */
public interface LoginView extends MvpView {

    void loginSucc();

    void completeInfo(String sxbStatus);

    void loginFail(String module, int errCode, String errMsg);
}
