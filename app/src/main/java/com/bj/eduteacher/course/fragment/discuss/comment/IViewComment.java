package com.bj.eduteacher.course.fragment.discuss.comment;


import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.CommentInfo;
import com.bj.eduteacher.presenter.viewinface.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public interface IViewComment extends MvpView{

    void getCommentSuccess(List<NewCommentInfo.DataBean> commentInfoList);
    void getCommentFail();

    void sendCommentSuccess();
    void sendCommentFail();
    void agree(BaseDataInfo dataInfo);
}
