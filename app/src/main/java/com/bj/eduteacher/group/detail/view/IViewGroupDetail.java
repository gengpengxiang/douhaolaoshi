package com.bj.eduteacher.group.detail.view;

import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.group.detail.model.GroupDetail;
import com.bj.eduteacher.presenter.viewinface.MvpView;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public interface IViewGroupDetail extends MvpView{

    void getGroupDetailSuccess(GroupDetail groupDetail);
    void getSignResult(BaseDataInfo baseDataInfo);
}
