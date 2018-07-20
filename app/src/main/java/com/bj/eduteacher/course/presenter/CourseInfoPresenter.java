package com.bj.eduteacher.course.presenter;

import android.content.Context;
import android.text.Html;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.course.fragment.detail.CourseInfo;
import com.bj.eduteacher.course.view.IViewCourseInfo;
import com.bj.eduteacher.presenter.Presenter;
import com.bumptech.glide.Glide;
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

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;
import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class CourseInfoPresenter extends Presenter{

    private Context context;
    private IViewCourseInfo iView;

    public CourseInfoPresenter(Context context, IViewCourseInfo iView) {
        this.context = context;
        this.iView = iView;
    }

    public void getCourseInfo(final String phone,final String unionid,final String kechengid){
        Observable.create(new ObservableOnSubscribe<CourseInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<CourseInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/kecheng")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("phone", phone)
                        .params("kechengid", kechengid)
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("课程信息", str);
                                CourseInfo courseInfo = JSON.parseObject(str, new TypeReference<CourseInfo>() {
                                });
                                e.onNext(courseInfo);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CourseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CourseInfo courseInfo) {
                       iView.getCourseInfoSuccess(courseInfo);
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
        context=null;
        iView = null;
    }
}
