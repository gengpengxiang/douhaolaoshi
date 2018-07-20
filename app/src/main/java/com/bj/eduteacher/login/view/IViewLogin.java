package com.bj.eduteacher.login.view;

import com.bj.eduteacher.login.model.LoginInfo;
import com.bj.eduteacher.presenter.viewinface.MvpView;

/**
 * Created by Administrator on 2018/5/16 0016.
 */

public interface IViewLogin extends MvpView{

    void loginSuccess();

    void gotoCompleteUserInfo();

    void loginFail(String errMessage);

    void bindPhoneSuccess(String phone);

}
