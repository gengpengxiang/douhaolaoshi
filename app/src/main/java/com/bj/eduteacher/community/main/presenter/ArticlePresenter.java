package com.bj.eduteacher.community.main.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.community.main.model.ArticleInfo;
import com.bj.eduteacher.community.main.view.IArticleInfoView;
import com.bj.eduteacher.presenter.Presenter;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
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
import static com.bj.eduteacher.api.Urls.DOUKELIST;
import static com.bj.eduteacher.api.Urls.NEWSDIANZAN;
/**
 * Created by Administrator on 2018/4/15 0015.
 */

public class ArticlePresenter extends Presenter {


    private Context mContext;
    private IArticleInfoView iArticleInfoView;
    private List<ArticleInfo.DataBean> list;
    private int status;
    private int newStatus;

    public ArticlePresenter(Context context) {
        mContext = context;
    }

    public ArticlePresenter(Context context, IArticleInfoView iArticleInfoView) {
        mContext = context;
        this.iArticleInfoView = iArticleInfoView;

    }

    public void getArticleInfo(final String userPhoneNumber, final String unionid,final String pageIndex) {

        Observable.create(new ObservableOnSubscribe<List<ArticleInfo.DataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<ArticleInfo.DataBean>> e) throws Exception {
                    OkGo.<String>post(BASE_URL + DOUKELIST)
                            .params("appkey", MLConfig.HTTP_APP_KEY)
                            .params("limit", "10")
                            .params("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * 10))
                            .params("usercode", userPhoneNumber)
                            .params("unionid",unionid)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    String str = response.body().toString();
                                    Log.e("文章信息返回数据", str);
                                    ArticleInfo articleInfo = JSON.parseObject(str, new TypeReference<ArticleInfo>() {
                                    });
                                    list = articleInfo.getData();
                                    e.onNext(list);
                                    e.onComplete();
                                }

                            });

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("onSubscribe","onSubscribe");
                    }

                    @Override
                    public void onNext(List<ArticleInfo.DataBean> dataBeans) {

                        for (int i = 0; i < dataBeans.size(); i++) {
                            if (dataBeans.get(i).getThree_content() == null) {
                                dataBeans.get(i).setType(1);
                            } else {
                                if (dataBeans.get(i).getThree_content().size() < 3) {
                                    dataBeans.get(i).setType(2);
                                }
                                if (dataBeans.get(i).getThree_content().size() > 2) {
                                    dataBeans.get(i).setType(3);
                                }

                            }

                        }
                        if(pageIndex.equals("1")){
                            iArticleInfoView.getArticleInfoSuccess(dataBeans);
                        }else {
                            iArticleInfoView.loadMore(dataBeans);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("onError","onError");
                        iArticleInfoView.getArticleInfoFail();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("onComplete","onComplete");
                    }
                });

    }

    public void refresh(final String userPhoneNumber,final String unionid) {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo.DataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<ArticleInfo.DataBean>> e) throws Exception {
                OkGo.<String>post(BASE_URL + DOUKELIST)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("limit", "10")
                        .params("offset", 0)
                        .params("usercode", userPhoneNumber)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("文章列表页刷新数据", str);
                                ArticleInfo articleInfo = JSON.parseObject(str, new TypeReference<ArticleInfo>() {
                                });
                                list = articleInfo.getData();

                                e.onNext(list);
                                e.onComplete();
                            }

                            @Override
                            public void onError(Response<String> response) {
                                Log.e("失败1","true");
                                //iArticleInfoView.refreshFail();
                            }
                        });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(List<ArticleInfo.DataBean> dataBeans) {

                        for (int i = 0; i < dataBeans.size(); i++) {
                            if (dataBeans.get(i).getThree_content() == null) {
                                dataBeans.get(i).setType(1);
                            } else {
                                if (dataBeans.get(i).getThree_content().size() < 3) {
                                    dataBeans.get(i).setType(2);
                                }
                                if (dataBeans.get(i).getThree_content().size() > 2) {
                                    dataBeans.get(i).setType(3);
                                }
                            }
                        }
                        iArticleInfoView.refresh(dataBeans);
                    }
                    @Override
                    public void onError(Throwable e) {
                        iArticleInfoView.refreshFail();
                    }
                    @Override
                    public void onComplete() {
                        Log.e("失败2","true");
                    }
                });
    }

    public void agree(final String newsid, final String userPhoneNumber, final String type,final String unionid) {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL+ NEWSDIANZAN)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("newsid", newsid)
                        .params("userphone", userPhoneNumber)
                        .params("dianzanadd", type)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("点赞查询返回数据", str);
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
                        status = Integer.parseInt(info.getRet());
                        if(status==4){
                            newStatus =2;
                        }
                        if(status==5){
                            newStatus =1;
                        }
                        OkGo.<String>post(BASE_URL + NEWSDIANZAN)
                                .params("appkey", MLConfig.HTTP_APP_KEY)
                                .params("newsid", newsid)
                                .params("userphone", userPhoneNumber)
                                .params("dianzanadd", newStatus)
                                .params("unionid",unionid)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        String str = response.body().toString();
                                        Log.e("点赞结果返回数据", str);
                                        Gson gson = new Gson();
                                        BaseDataInfo datainfo = gson.fromJson(str, BaseDataInfo.class);

                                       iArticleInfoView.agree(datainfo);
                                    }
                                });
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
        Log.e("打赏时的手机号2","from"+fromusercode+"to"+tousercode+"fromunionid"+fromunionid+"tounionid"+tounionid);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL+ DASHANGDOUBI)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("fromusercode", fromusercode)
                        .params("tousercode", tousercode)
                        .params("fromunionid",fromunionid)
                        .params("tounionid",tounionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("打赏结果",str);
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
                        iArticleInfoView.reward(string);
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
        iArticleInfoView = null;
    }
}
