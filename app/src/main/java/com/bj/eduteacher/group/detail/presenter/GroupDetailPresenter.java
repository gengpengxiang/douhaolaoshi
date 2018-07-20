package com.bj.eduteacher.group.detail.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.group.detail.model.GroupDetail;
import com.bj.eduteacher.group.detail.view.IViewGroupDetail;
import com.bj.eduteacher.integral.model.Doubi;
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
import static com.bj.eduteacher.api.Urls.DOUBI;
import static com.bj.eduteacher.api.Urls.GROUPINFO;
import static com.bj.eduteacher.api.Urls.GROUPQIANDAO;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public class GroupDetailPresenter extends Presenter {

    private Context context;
    private IViewGroupDetail iView;

    public GroupDetailPresenter(Context context, IViewGroupDetail iView) {
        this.context = context;
        this.iView = iView;
    }

    public void getGroupDetail(final String usercode, final String id,final String unionid) {
        Observable.create(new ObservableOnSubscribe<GroupDetail>() {
            @Override
            public void subscribe(final ObservableEmitter<GroupDetail> e) throws Exception {
                OkGo.<String>post(BASE_URL + GROUPINFO)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("usercode", usercode)
                        .params("groupid", id)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("小组详情", str);

                                GroupDetail groupDetail = JSON.parseObject(str, new TypeReference<GroupDetail>() {
                                });
                                e.onNext(groupDetail);
                                e.onComplete();

                            }
                        });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GroupDetail>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GroupDetail groupDetail) {
                        iView.getGroupDetailSuccess(groupDetail);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void signIn(final String usercode, final String id,final String unionid) {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + GROUPQIANDAO)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("usercode", usercode)
                        .params("groupid", id)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("签到详情", str);

                                BaseDataInfo baseDataInfo = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });
                                e.onNext(baseDataInfo);
                                e.onComplete();

                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo dataInfo) {
                        iView.getSignResult(dataInfo);
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
        context = null;
        iView = null;
    }
}
