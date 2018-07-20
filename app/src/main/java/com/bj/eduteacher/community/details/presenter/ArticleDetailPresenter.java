package com.bj.eduteacher.community.details.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.community.details.model.ArticleDetail;
import com.bj.eduteacher.community.details.view.IArticleDetailView;
import com.bj.eduteacher.presenter.Presenter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.DASHANGDOUBI;
import static com.bj.eduteacher.api.Urls.NEWSBYID;
import static com.bj.eduteacher.api.Urls.NEWSDIANZAN;


/**
 * Created by Administrator on 2018/4/18 0018.
 */

public class ArticleDetailPresenter extends Presenter {

    private Context mContext;
    private IArticleDetailView iArticleDetailView;
    private ArticleDetail.DataBean dataBean;
    private int status,newStatus,statusCode;

    public ArticleDetailPresenter(Context mContext, IArticleDetailView iArticleDetailView) {
        this.mContext = mContext;
        this.iArticleDetailView = iArticleDetailView;
    }

    public void getArticleDetail(final String newsid,final String phoneNumber,final String unionid){
        Observable.create(new ObservableOnSubscribe<ArticleDetail.DataBean>() {
            @Override
            public void subscribe(final ObservableEmitter<ArticleDetail.DataBean> e) throws Exception {
                OkGo.<String>post(BASE_URL + NEWSBYID)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("newsid", newsid)
                        .params("usercode", phoneNumber)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("文章细节返回数据", str);

                                ArticleDetail articledetail = JSON.parseObject(str,new TypeReference<ArticleDetail>(){});

                                ArticleDetail.DataBean data = articledetail.getData();

                                e.onNext(data);
                                e.onComplete();
                            }
                        });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleDetail.DataBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArticleDetail.DataBean dataBeans) {

                        iArticleDetailView.getArticleDetailSuccess(dataBeans);
                    }

                    @Override
                    public void onError(Throwable e) {
                        iArticleDetailView.getArticleDetailFail();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    public void agree(final String newsid, final String userPhoneNumber, final String type,final String unionid) {

        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + NEWSDIANZAN)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("newsid", newsid)
                        .params("userphone", userPhoneNumber)
                        .params("dianzanadd", type)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("点赞返回数据", str);

                                Gson gson = new Gson();
                                BaseDataInfo datainfo = gson.fromJson(str, BaseDataInfo.class);

                                e.onNext(datainfo);
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
                    public void onNext(BaseDataInfo info) {
                        iArticleDetailView.agree(info);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void reward(final String fromusercode, final String tousercode,final String fromunionid,final String tounionid) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL + DASHANGDOUBI)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("fromusercode", fromusercode)
                        .params("tousercode", tousercode)
                        .params("fromunionid",fromunionid)
                        .params("tounionid",tounionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();

                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    String msg = jsonObject.optString("msg");
                                    e.onNext(msg);
                                    e.onComplete();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String string) {
                        iArticleDetailView.reward(string);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("打赏","失败了");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestory() {
        mContext = null;
        iArticleDetailView = null;
    }
}
