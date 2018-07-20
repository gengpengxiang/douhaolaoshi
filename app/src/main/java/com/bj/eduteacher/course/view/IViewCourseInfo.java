package com.bj.eduteacher.course.view;

import com.bj.eduteacher.course.fragment.detail.CourseInfo;
import com.bj.eduteacher.presenter.viewinface.MvpView;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public interface IViewCourseInfo extends MvpView{

    void getCourseInfoSuccess(CourseInfo courseInfo);

    void getCourseInfoFail();

}
