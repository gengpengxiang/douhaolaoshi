package com.bj.eduteacher.login.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.api.RetrofitService;
import com.bj.eduteacher.login.model.LoginInfo;
import com.bj.eduteacher.login.model.UserInfo;
import com.bj.eduteacher.login.view.IViewLogin;
import com.bj.eduteacher.presenter.Presenter;
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
 * Created by Administrator on 2018/5/16 0016.
 */

public class LoginPresenter extends Presenter {

    private Context context;
    private IViewLogin iView;

    public LoginPresenter(Context context, IViewLogin iView) {
        this.context = context;
        this.iView = iView;
    }

    public void login(final String phone, String yzm, final String laiyuan) {
        RetrofitService.getRetrofitApi().getLoginInfo(MLConfig.HTTP_APP_KEY, phone, yzm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LoginInfo loginInfo) {

                        if(laiyuan.equals("login")){
                            Log.e("手机号登录获取信息",loginInfo.getMsg().toString());
                        }if(laiyuan.equals("bind")){
                            Log.e("手机号绑定获取信息",loginInfo.getMsg().toString());
                        }

//                        Log.e("登录信息", loginInfo.getMsg().toString());
                        if (loginInfo.getRet().equals("1")) {
                            getUserInfo(phone, "", laiyuan);
                        } else {
                            iView.loginFail(loginInfo.getMsg());
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

    public void getUserInfo(final String phone, final String unionid, final String laiyuan) {

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL + GETTEACHERINFO)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("teacherphone", phone)
                        .params("unionid", unionid)
                        .params("type", "weixin")
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
                        JSONObject json = JSON.parseObject(s);
                        String ret = (String) json.get("ret");
                        if(ret.equals("0")){
                            String data = (String) json.get("data");
                            JSONObject json2 = JSON.parseObject(data);
                            String groupid = (String) json2.get("groupid_moren");
                            PreferencesUtils.putString(context, MLProperties.PREFER_KEY_TEACHER_GROUPID, groupid);
                        }if(ret.equals("1")){
                            UserInfo userInfo = JSON.parseObject(s, new TypeReference<UserInfo>() {});
//                            Log.e("手机号登录成功后获取用户数据",userInfo.getData().toString());
                            UserInfo.DataBean bean = userInfo.getData();
                            String nickname = bean.getNicheng();
                            String teacherImg = bean.getTeacherimg_url();
                            PreferencesUtils.putString(context, MLProperties.PREFER_KEY_USER_ID, bean.getTeacherphone());
                            PreferencesUtils.putString(context, MLProperties.PREFER_KEY_WECHAT_NICHENG, bean.getWeixin_nicheng());
                            PreferencesUtils.putString(context, MLProperties.PREFER_KEY_TEACHER_GROUPID, bean.getTeacher_groupid());
                            //首次登录时unionid为0，所以不可在此put
                            //PreferencesUtils.putString(context, MLProperties.PREFER_KEY_WECHAT_UNIONID, bean.getWeixin_unionid());

                            if(laiyuan.equals("login")){
                                Log.e("手机号登录成功后获取用户数据",userInfo.getData().toString());
                                if (nickname.equals("0") || teacherImg.equals("0")||StringUtils.isEmpty(nickname)||StringUtils.isEmpty(teacherImg)) {
                                    iView.gotoCompleteUserInfo();
                                } else {
                                    PreferencesUtils.putString(context, MLProperties.BUNDLE_KEY_TEACHER_NICK, bean.getNicheng());
                                    PreferencesUtils.putString(context, MLProperties.BUNDLE_KEY_TEACHER_IMG, bean.getTeacherimg_url());

                                    PreferencesUtils.putString(context, MLProperties.PREFER_KEY_USER_ID, bean.getTeacherphone());

                                    PreferencesUtils.putString(context, MLProperties.PREFER_KEY_WECHAT_UNIONID, bean.getWeixin_unionid());


                                    iView.loginSuccess();
                                }
                            }if(laiyuan.equals("bind")){
                                Log.e("手机号绑定成功后获取用户数据",userInfo.getData().toString());
                                if (StringUtils.isEmpty(nickname) || StringUtils.isEmpty(teacherImg)) {
                                    iView.bindPhoneSuccess(phone);
                                }
                                if(nickname.equals("0")||teacherImg.equals("0")){
                                    Log.e("绑定手机号时昵称头像均为0","111");
                                    iView.bindPhoneSuccess(phone);
                                }
                                else {
                                    PreferencesUtils.putString(context, MLProperties.BUNDLE_KEY_TEACHER_NICK, bean.getNicheng());
                                    PreferencesUtils.putString(context, MLProperties.BUNDLE_KEY_TEACHER_IMG, bean.getTeacherimg_url());
                                    iView.bindPhoneSuccess(phone);
                                }
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

    @Override
    public void onDestory() {
        context = null;
        iView = null;
    }
}
