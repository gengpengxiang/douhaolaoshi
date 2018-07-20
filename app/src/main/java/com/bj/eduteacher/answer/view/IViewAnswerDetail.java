package com.bj.eduteacher.answer.view;

import com.bj.eduteacher.answer.model.Question;
import com.bj.eduteacher.presenter.viewinface.MvpView;

/**
 * Created by Administrator on 2018/7/11 0011.
 */

public interface IViewAnswerDetail extends MvpView{

    void getAnswerList(Question question);
}
