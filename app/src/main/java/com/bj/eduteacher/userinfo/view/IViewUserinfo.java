package com.bj.eduteacher.userinfo.view;

import com.bj.eduteacher.login.model.UserInfo;
import com.bj.eduteacher.presenter.viewinface.MvpView;
import com.bj.eduteacher.userinfo.model.BinderInfo;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public interface IViewUserinfo extends MvpView{

    void getBindInfoSuccess(BinderInfo binderInfo);
    void getBindInfoFail(String result);

    void getUserInfoSuccess(UserInfo userInfo);
    void getUserInfoFail();

}
