package com.bj.eduteacher.prize.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.RetrofitService;
import com.bj.eduteacher.presenter.Presenter;
import com.bj.eduteacher.prize.model.Prize;
import com.bj.eduteacher.prize.model.PrizeResult;
import com.bj.eduteacher.prize.view.IViewPrize;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.CHOUJIANG;
import static com.bj.eduteacher.api.Urls.JIANGPIN;

/**
 * Created by Administrator on 2018/4/25 0025.
 */

public class PrizePresenter extends Presenter {

    private Context mContext;
    private IViewPrize iView;

    public PrizePresenter(Context mContext, IViewPrize iView) {
        this.mContext = mContext;
        this.iView = iView;
    }

    public void getPrizeInfo(final String usercode,final String unionid){
        /*RetrofitService.getInstance().getRetrofitApi().getPrizeInfo(usercode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Prize>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Prize prize) {
                        Log.e("奖品结果", prize.toString());
                        iView.getPrizeInfo(prize);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
       /* OkGo.<String>post(BASE_URL+JIANGPIN)
                .params("usercode",usercode)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String str = response.body().toString();
                        Log.e("奖品结果", str);
                        Prize prize = JSON.parseObject(str,new TypeReference<Prize>(){});

                    }
                });*/
        Observable.create(new ObservableOnSubscribe<Prize>() {
            @Override
            public void subscribe(final ObservableEmitter<Prize> e) throws Exception {
                OkGo.<String>post(BASE_URL+JIANGPIN)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("usercode",usercode)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("奖品结果", str);
                                Prize prize = JSON.parseObject(str,new TypeReference<Prize>(){});

                                e.onNext(prize);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Prize>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Prize prize) {
                        iView.getPrizeInfo(prize);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getPrizeResult(final String usercode,final String unionid){
        Observable.create(new ObservableOnSubscribe<PrizeResult>() {
            @Override
            public void subscribe(final ObservableEmitter<PrizeResult> e) throws Exception {
                OkGo.<String>post(BASE_URL+CHOUJIANG)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("usercode",usercode)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("抽奖结果", str);
                                PrizeResult prizeResult = JSON.parseObject(str, new TypeReference<PrizeResult>() {
                                });
                                e.onNext(prizeResult);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PrizeResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PrizeResult prizeResult) {
                        iView.getPrizeResult(prizeResult);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestory() {
        mContext = null;
        iView = null;
    }
}
