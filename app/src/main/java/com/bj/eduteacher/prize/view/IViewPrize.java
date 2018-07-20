package com.bj.eduteacher.prize.view;

import com.bj.eduteacher.presenter.viewinface.MvpView;
import com.bj.eduteacher.prize.model.Prize;
import com.bj.eduteacher.prize.model.PrizeResult;

/**
 * Created by Administrator on 2018/4/25 0025.
 */

public interface IViewPrize extends MvpView{

    void getPrizeInfo(Prize prize);

    void getPrizeResult(PrizeResult prizeResult);
}
