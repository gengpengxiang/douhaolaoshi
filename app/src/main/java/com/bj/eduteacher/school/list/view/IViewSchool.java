package com.bj.eduteacher.school.list.view;

import com.bj.eduteacher.presenter.viewinface.MvpView;
import com.bj.eduteacher.school.list.model.Province;
import com.bj.eduteacher.school.list.model.School;

import java.util.List;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public interface IViewSchool extends MvpView{

    void getProvincesSuccess(List<Province.DataBean> provinceList);
    void getSchoolsSuccess(List<School.DataBean> schoolList);
}
