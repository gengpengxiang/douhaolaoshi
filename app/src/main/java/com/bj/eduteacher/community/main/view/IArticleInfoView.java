package com.bj.eduteacher.community.main.view;

import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.community.main.model.ArticleInfo;
import com.bj.eduteacher.presenter.viewinface.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2018/4/15 0015.
 */

public interface IArticleInfoView extends MvpView {

    void getArticleInfoSuccess(List<ArticleInfo.DataBean> dataBeans);
    void getArticleInfoFail();
//    void agree(int statusCode);
    void agree(BaseDataInfo dataInfo);
    void reward(String result);
    void refresh(List<ArticleInfo.DataBean> dataBeans);
    void refreshFail();
    void loadMore(List<ArticleInfo.DataBean> dataBeans);
}
