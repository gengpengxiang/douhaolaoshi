package com.bj.eduteacher.answer.view;

import com.bj.eduteacher.answer.model.ExamInfo;
import com.bj.eduteacher.answer.model.ExamRecord;
import com.bj.eduteacher.presenter.viewinface.MvpView;

/**
 * Created by Administrator on 2018/7/11 0011.
 */

public interface IViewAnswerHome extends MvpView{

    void getExamInfo(ExamInfo examInfo);
    void  getExamRecord(ExamRecord examRecord);
}
