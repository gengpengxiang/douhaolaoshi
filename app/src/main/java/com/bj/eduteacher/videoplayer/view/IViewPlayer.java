package com.bj.eduteacher.videoplayer.view;

import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.presenter.viewinface.MvpView;
import com.bj.eduteacher.videoplayer.model.ResInfo;

/**
 * Created by Administrator on 2018/6/26 0026.
 */

public interface IViewPlayer extends MvpView {

    void getResInfo(ResInfo.DataBean resInfo);
    void addBrowseNumSuccess();
    void getAgreeInfo(BaseDataInfo info,String type);
}
