package com.bj.eduteacher.answer.presenter;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.answer.model.Question;
import com.bj.eduteacher.answer.view.IViewAnswerDetail;
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

public class AnswerDetailPresenter extends Presenter {

    private Context context;
    private IViewAnswerDetail iView;

    public AnswerDetailPresenter(Context context, IViewAnswerDetail iView) {
        this.context = context;
        this.iView = iView;
    }

    public void getAnswerList(final String id, final String ordernum) {
        Observable.create(new ObservableOnSubscribe<Question>() {
            @Override
            public void subscribe(final ObservableEmitter<Question> e) throws Exception {
                OkGo.<String>post(BASE_URL+"index.php/grenwu/shiti")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("examid",id)
                        .params("shiti_ordernum",ordernum)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Question question = JSON.parseObject(str,new TypeReference<Question>(){});
                                e.onNext(question);
                                e.onComplete();
                            }
                        });

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Question>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Question question) {

                        if(question.getRet().equals("1")){
                            iView.getAnswerList(question);
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

    @Override
    public void onDestory() {
        context = null;
        iView = null;
    }
}
