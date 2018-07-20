package com.bj.eduteacher.videoplayer.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.presenter.Presenter;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.videoplayer.model.ResInfo;
import com.bj.eduteacher.videoplayer.view.IViewPlayer;
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
 * Created by Administrator on 2018/6/26 0026.
 */

public class PlayerPresenter extends Presenter {

    private IViewPlayer iView;
    private Context context;

    public PlayerPresenter(IViewPlayer iView, Context context) {
        this.iView = iView;
        this.context = context;
    }

    /**
     * 获取资源详细信息
     *
     * @param id
     */
    public void getResInfo(final String id) {
        Observable.create(new ObservableOnSubscribe<ResInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<ResInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/ziyuan/getbyid")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("ziyuanid", id)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("资源信息",str);
                                ResInfo resInfo = JSON.parseObject(str, new TypeReference<ResInfo>() {
                                });
                                e.onNext(resInfo);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResInfo resInfo) {
                        iView.getResInfo(resInfo.getData());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 增加浏览量
     */
    public void addBrowseNum(final String id) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/jsmaster/resvnumadd")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("masterresid", id)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                e.onNext(str);
                                e.onComplete();
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
                    public void onNext(String s) {
                        JSONObject jsonObject = JSON.parseObject(s);
                        String ret = (String) jsonObject.get("ret");

                        if(ret.equals("1")){
                            iView.addBrowseNumSuccess();
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
    /**
     * 查询点赞状态及点赞操作
     */
    public void queryOrAgree(final String id, final String type, final String phone, final String unionid){

        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "ziyuan/dianzan")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("ziyuanid", id)
                        .params("caozuo", type)
                        .params("userphone", phone)
                        .params("unionid", unionid)
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
                    public void onNext(BaseDataInfo info) {

                        if (type.equals("status")) {

                            iView.getAgreeInfo(info,"status");
                          /*  if (info.getRet().equals("3")) {
                                if (info.getData().equals("1")) {
                                    ivAgree.setImageResource(R.mipmap.ic_liked);

                                    agreeStatus = 1;
                                } else {
                                    ivAgree.setImageResource(R.mipmap.ic_like);
                                    agreeStatus = 0;
                                }
                            }*/
                        }
                        if (type.equals("add")) {
                            iView.getAgreeInfo(info,"add");
//                            if (info.getRet().equals("1")) {
//                                chaxunAgreeStatus("status");
//                            }
                        }
                        if (type.equals("del")) {
                            iView.getAgreeInfo(info,"del");
//                            if (info.getRet().equals("2")) {
//                                chaxunAgreeStatus("status");
//                            }
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
        iView = null;
        context = null;
    }
}
