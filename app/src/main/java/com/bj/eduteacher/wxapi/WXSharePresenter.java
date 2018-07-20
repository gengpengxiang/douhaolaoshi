package com.bj.eduteacher.wxapi;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.presenter.Presenter;
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
import static com.bj.eduteacher.api.Urls.FENXIANGRANDOMNUM;

/**
 * Created by Administrator on 2018/4/24 0024.
 */

public class WXSharePresenter extends Presenter{

    private Context mContext;
    private IViewWXShare iViewWXShare;

    public WXSharePresenter(Context mContext, IViewWXShare iViewWXShare) {
        this.mContext = mContext;
        this.iViewWXShare = iViewWXShare;
    }

    public void share(final String userCode,final String unionid,final String title,final String content,final String img,final int type){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL + FENXIANGRANDOMNUM)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("usercode", userCode)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("获取随机数返回数据", str);

                                BaseDataInfo dataInfo = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });
                                e.onNext(dataInfo.getData());
                                e.onComplete();
                            }
                        });
            }
        }).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        String url = BASE_URL+"index.php/Dhlaoshi/getdoubibyfenxiang?usercode="+userCode+"&randnum="+s;

                        Log.e("分享网址",url);
                        WXUtil.share(mContext,type,url,title,content,img);
                        iViewWXShare.share();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void invite(final String userCode,final String unionid){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL + FENXIANGRANDOMNUM)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("usercode", userCode)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("获取随机数返回数据", str);

                                BaseDataInfo dataInfo = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });
                                e.onNext(dataInfo.getData());
                                e.onComplete();
                            }
                        });
            }
        }).observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        String url = BASE_URL+"index.php/Dhlaoshi/getdoubibyfenxiang?usercode="+userCode+"&randnum="+s;

                        Log.e("分享网址",url);
                        WXUtil.invite(mContext,0,url);
                        iViewWXShare.share();
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
    }
}
