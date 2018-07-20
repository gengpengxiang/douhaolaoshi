package com.bj.eduteacher.answer.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.answer.model.ExamInfo;
import com.bj.eduteacher.answer.model.ExamRecord;
import com.bj.eduteacher.answer.view.IViewAnswerHome;
import com.bj.eduteacher.api.MLConfig;
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

/**
 * Created by Administrator on 2018/7/11 0011.
 */

public class AnswerHomePresenter extends Presenter {


    private Context context;
    private IViewAnswerHome iView;

    public AnswerHomePresenter(Context context, IViewAnswerHome iView) {
        this.context = context;
        this.iView = iView;
    }

    public void getExamInfo(final String id,final String phone, final String unionid) {
        Observable.create(new ObservableOnSubscribe<ExamInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<ExamInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL+"index.php/grenwu/examinfo")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("examid",id)
                        .params("unionid",unionid)
                        .params("phone",phone)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                ExamInfo examInfo = JSON.parseObject(str, new TypeReference<ExamInfo>() {
                                });
                                e.onNext(examInfo);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ExamInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ExamInfo examInfo) {
                        iView.getExamInfo(examInfo);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getRecordList(final String id,final String phone, final String unionid){
        Observable.create(new ObservableOnSubscribe<ExamRecord>() {
            @Override
            public void subscribe(final ObservableEmitter<ExamRecord> e) throws Exception {
                OkGo.<String>post(BASE_URL+"index.php/grenwu/datiloglist")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("examid",id)
                        .params("unionid",unionid)
                        .params("phone",phone)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("考试记录",str);
                                ExamRecord examRecord = JSON.parseObject(str,new TypeReference<ExamRecord>(){});
                                e.onNext(examRecord);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ExamRecord>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ExamRecord examRecord) {
                        if(examRecord.getRet().equals("1")){
                            if(examRecord.getData()!=null){
                                iView.getExamRecord(examRecord);
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void startExam( final String id,final String logcode,final String phone, final String unionid) {

    }


    @Override
    public void onDestory() {
        context = null;
        iView = null;
    }
}
