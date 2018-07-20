package com.bj.eduteacher.community.details.view;

import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.community.details.model.ArticleDetail;
import com.bj.eduteacher.presenter.viewinface.MvpView;


/**
 * Created by Administrator on 2018/4/18 0018.
 */

public interface IArticleDetailView extends MvpView {
    void getArticleDetailSuccess(ArticleDetail.DataBean dataBeans);
    void getArticleDetailFail();
    void reward(String result);
    void agree(BaseDataInfo dataInfo);
}
