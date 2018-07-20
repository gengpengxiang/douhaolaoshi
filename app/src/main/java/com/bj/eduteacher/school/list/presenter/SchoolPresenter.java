package com.bj.eduteacher.school.list.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.presenter.Presenter;
import com.bj.eduteacher.school.list.model.Province;
import com.bj.eduteacher.school.list.model.School;
import com.bj.eduteacher.school.list.view.IViewSchool;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class SchoolPresenter extends Presenter {

    private Context context;
    private IViewSchool iView;

    public SchoolPresenter(Context context, IViewSchool iView) {
        this.context = context;
        this.iView = iView;
    }

    public void getProvinces() {
        Observable.create(new ObservableOnSubscribe<List<Province.DataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<Province.DataBean>> e) throws Exception {
                OkGo.<String>post(BASE_URL+"index.php/school/shengfen")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("userclient","aphone")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("省份返回",str);
                                Province province = JSON.parseObject(str, new TypeReference<Province>() {
                                });

                                e.onNext(province.getData());
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Province.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Province.DataBean> provinceList) {
                        iView.getProvincesSuccess(provinceList);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getSchools(final String shengfen,final int pageIndex) {
        Observable.create(new ObservableOnSubscribe<List<School.DataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<School.DataBean>> e) throws Exception {
                OkGo.<String>post(BASE_URL+"index.php/school/index")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("userclient","aphone")
                        .params("limit","10")
                        .params("offset",String.valueOf((pageIndex - 1) * 10))
                        .params("shengfen",shengfen)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("学校列表返回",str);
                                School school = JSON.parseObject(str, new TypeReference<School>() {
                                });

                                e.onNext(school.getData());
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<School.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<School.DataBean> schoolList) {
                        iView.getSchoolsSuccess(schoolList);
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
