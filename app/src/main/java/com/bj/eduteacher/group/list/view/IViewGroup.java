package com.bj.eduteacher.group.list.view;

import com.bj.eduteacher.group.list.model.GroupInfo;
import com.bj.eduteacher.presenter.viewinface.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public interface IViewGroup extends MvpView{
    void getGroupListSuccess(List<GroupInfo.DataBean> groupInfoList);
    void getGroupListFail();
    void refresh(List<GroupInfo.DataBean> groupInfoList);
}
