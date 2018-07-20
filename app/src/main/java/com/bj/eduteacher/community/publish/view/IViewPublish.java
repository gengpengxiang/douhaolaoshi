package com.bj.eduteacher.community.publish.view;

import com.bj.eduteacher.presenter.viewinface.MvpView;

/**
 * Created by Administrator on 2018/4/27 0027.
 */

public interface IViewPublish extends MvpView{

    void uploadPicSuccess();
    void uploadPicFail();
    void publishSuccess();
    void publishFail();

}
