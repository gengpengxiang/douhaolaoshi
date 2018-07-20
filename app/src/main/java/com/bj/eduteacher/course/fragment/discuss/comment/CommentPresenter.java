package com.bj.eduteacher.course.fragment.discuss.comment;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.course.fragment.discuss.comment.IViewComment;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.CommentInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.presenter.Presenter;
import com.bj.eduteacher.utils.StringUtils;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
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
import static com.bj.eduteacher.api.Urls.NEWSDIANZAN;

/**
 * Created by Administrator on 2018/5/24 0024.
 */

public class CommentPresenter extends Presenter {

    private Context context;
    private IViewComment iView;
    private int status;
    private int newStatus;

    public CommentPresenter(Context context, IViewComment iView) {
        this.context = context;
        this.iView = iView;
    }

    public void getCommentList(final String id, final String page) {
        Observable.create(new ObservableOnSubscribe<List<NewCommentInfo.DataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<NewCommentInfo.DataBean>> e) throws Exception {

                OkGo.<String>post(BASE_URL + "index.php/douke")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("newsid", id)
                        .params("limit", "10")
                        .params("offset", String.valueOf((Integer.parseInt(page) - 1) * 10))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("评论",str);
                                NewCommentInfo info = JSON.parseObject(str, new TypeReference<NewCommentInfo>() {
                                });

                                e.onNext(info.getData());
                                e.onComplete();
                            }
                        });

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<NewCommentInfo.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<NewCommentInfo.DataBean> dataBeans) {
                        iView.getCommentSuccess(dataBeans);
                    }

                    @Override
                    public void onError(Throwable e) {
                        iView.getCommentFail();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void sendCommentContent(final String id, final String content, final String phone, final String unionid) {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL+"index.php/douke/setcomment")
                        .params("appkey",MLConfig.HTTP_APP_KEY)
                        .params("newsid",id)
                        .params("content",content)
                        .params("usertype","jiaoshi")
                        .params("userphone",phone)
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                BaseDataInfo info = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });

                                e.onNext(info);
                                e.onComplete();
                            }
                        });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo dataInfo) {
                        if(dataInfo.getRet().equals("1")){
                            iView.sendCommentSuccess();
                            EventBus.getDefault().post(new MsgEvent("discusspinglunsuccess"));
                        }else {
                            iView.sendCommentFail();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        /*Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {

                LmsDataService mService = new LmsDataService();
                String[] result = mService.postDoukeCommentFromAPI(id, phone,
                        MLConfig.KEY_DOUKE_COMMENT_JIAOSHI, content,unionid);
                e.onNext(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String[] result) {
                        if (!StringUtils.isEmpty(result[0]) && "1".equals(result[0])) {
                            // 发布成功
                            iView.sendCommentSuccess();
                            EventBus.getDefault().post(new MsgEvent("discusspinglunsuccess"));
                            //presenter.getDouBi("pinglun",userPhoneNumber,"getdoubi",unionid);
                        } else {
                            // 发布失败
                            iView.sendCommentFail();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/
    }

    public void agree(final String newsid, final String userPhoneNumber, final String type, final String unionid) {

        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + NEWSDIANZAN)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("newsid", newsid)
                        .params("userphone", userPhoneNumber)
                        .params("dianzanadd", type)
                        .params("unionid", unionid)
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
                        if (status == 4) {
                            newStatus = 2;
                        }
                        if (status == 5) {
                            newStatus = 1;
                        }
                        OkGo.<String>post(BASE_URL + NEWSDIANZAN)
                                .params("appkey", MLConfig.HTTP_APP_KEY)
                                .params("newsid", newsid)
                                .params("userphone", userPhoneNumber)
                                .params("dianzanadd", newStatus)
                                .params("unionid", unionid)
                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        String str = response.body().toString();
                                        Log.e("点赞结果返回数据", str);

                                        Gson gson = new Gson();
                                        BaseDataInfo datainfo = gson.fromJson(str, BaseDataInfo.class);

                                        iView.agree(datainfo);
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


    @Override
    public void onDestory() {
        context = null;
        iView = null;
    }
}
