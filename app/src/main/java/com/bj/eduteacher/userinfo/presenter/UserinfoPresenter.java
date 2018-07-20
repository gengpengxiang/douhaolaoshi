package com.bj.eduteacher.userinfo.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.api.RetrofitService;
import com.bj.eduteacher.login.model.UserInfo;
import com.bj.eduteacher.presenter.Presenter;
import com.bj.eduteacher.userinfo.model.BinderInfo;
import com.bj.eduteacher.userinfo.view.IViewUserinfo;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
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
import static com.bj.eduteacher.api.Urls.GETTEACHERINFO;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class UserinfoPresenter extends Presenter {

    private Context context;
    private IViewUserinfo iView;

    public UserinfoPresenter(Context context, IViewUserinfo iView) {
        this.context = context;
        this.iView = iView;
    }

    public void getUserInfo(final String phone,String unionid){
        RetrofitService.getRetrofitApi().getUserInfo(MLConfig.HTTP_APP_KEY,phone,unionid,"weixin")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        if(userInfo.getRet().equals("1")){
                            UserInfo.DataBean bean = userInfo.getData();
                            PreferencesUtils.putString(context, MLProperties.PREFER_KEY_USER_ID, bean.getTeacherphone());

                            PreferencesUtils.putString(context, MLProperties.BUNDLE_KEY_TEACHER_NICK, bean.getNicheng());
                            PreferencesUtils.putString(context, MLProperties.BUNDLE_KEY_TEACHER_IMG, bean.getTeacherimg_url());
                            PreferencesUtils.putString(context, MLProperties.PREFER_KEY_WECHAT_UNIONID, bean.getWeixin_unionid());

                            PreferencesUtils.putString(context,MLProperties.PREFER_KEY_WECHAT_NICHENG,bean.getWeixin_nicheng());


                            iView.getUserInfoSuccess(userInfo);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("获取失败","");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getBindInfo(String unionid, String phone, String laiyuan) {
        RetrofitService.getRetrofitApi().getBindInfo(MLConfig.HTTP_APP_KEY, unionid, phone, laiyuan)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BinderInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BinderInfo binderInfo) {

                        //Log.e("绑定信息返回",binderInfo.getMsg());
                        switch (binderInfo.getRet()) {
                            case "1":
                                PreferencesUtils.putString(context, MLProperties.PREFER_KEY_WECHAT_UNIONID, binderInfo.getData().getUnionid());
                                PreferencesUtils.putString(context, MLProperties.PREFER_KEY_USER_ID, binderInfo.getData().getPhone());
                                iView.getBindInfoSuccess(binderInfo);
                                break;
                            case "2":
                                iView.getBindInfoFail(binderInfo.getMsg());
                                break;
                            case "3":
                                iView.getBindInfoFail(binderInfo.getMsg());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        iView.getBindInfoFail("失败了");
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
