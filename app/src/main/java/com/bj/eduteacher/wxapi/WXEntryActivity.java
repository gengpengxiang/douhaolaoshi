package com.bj.eduteacher.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.api.RetrofitService;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.login.model.UserInfo;
import com.bj.eduteacher.userinfo.model.BinderInfo;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
//import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.APPUPDATEWEIXINUSERINFO;


public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID, false);

        try {
            api.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        Log.e("onNewIntent", "onNewIntent");
    }

    @Override
    public void onReq(BaseReq req) {
        Log.e("onReq", "onReq");
        this.finish();
        Log.e("分享成功222","success");
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e("onResp", "onResp");
        int result = 0;
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //如果errcode为零，表示用户同意授权
                switch (resp.getType()) {
                    case ConstantsAPI.COMMAND_SENDAUTH:
                        //登录成功的回调
                        result = R.string.errcode_login_success;
                        String code = ((SendAuth.Resp) resp).code;
                        String state = ((SendAuth.Resp) resp).state;
                        Log.e("微信登录返回code==", ((SendAuth.Resp) resp).code);
                        Log.e("微信登录返回status==", state);
                        getToken(code, state);
                        break;
                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
                        //分享成功的回调
                        Log.e("分享成功","success");
                        break;
                }
                // 得到所需的code
                //此处报错检查下载的sdk是否正确
                //result = R.string.errcode_success;
                this.finish();
                Log.e("ERR_OK:", "ERR_OK:");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                result = R.string.errcode_cancel;
                this.finish();
                Log.e("ERR_USER_CANCEL:", "ERR_USER_CANCEL");

//                switch (resp.getType()) {
//                    case ConstantsAPI.COMMAND_SENDAUTH:
//                        //登录取消的回调
//                        //result = "登录取消";
//                        break;
//                    case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
//                        Log.i("JavaUnity", "分享取消");
//                        //分享取消的回调
//
//                        break;
//        }

                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                this.finish();
                Log.e("ERR_AUTH_DENIED:", "ERR_AUTH_DENIED");
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                this.finish();
                Log.e("ERR_UNSUPPORT:", "ERR_UNSUPPORT");
                break;
            default:
                result = R.string.errcode_unknown;
                this.finish();
                Log.e("返回", "返回");
                break;
        }
        // Toast.makeText(this, result, Toast.LENGTH_LONG).show();
//        this.finish();
        overridePendingTransition(0, 0);
    }

    private void getToken(final String code, final String status) {

        String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + "wx7e56305cbb24d576"
                + "&secret="
                + "04f6981e2893af98e5748a10ca94a6e5"
                + "&code="
                + code
                + "&grant_type=authorization_code";
        Log.e("请求微信信息网址", path);
        RetrofitService.getRetrofitApi().getWXcode("wx7e56305cbb24d576", "04f6981e2893af98e5748a10ca94a6e5", code, "authorization_code")
                .flatMap(new Function<WXToken, ObservableSource<WXUserInfo>>() {
                    @Override
                    public ObservableSource<WXUserInfo> apply(WXToken wxToken) throws Exception {
                        Log.e("微信返回的code,用来获取token", wxToken.getAccess_token());
                        return RetrofitService.getRetrofitApi().getWXUserInfo(wxToken.getAccess_token(), wxToken.getOpenid());
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WXUserInfo>() {
                    @Override
                    public void accept(WXUserInfo wxUserInfo) throws Exception {
                        Log.e("用户的数据", wxUserInfo.getHeadimgurl());
                        Log.e("微信用户信息", wxUserInfo.toString());

                        //区分登录和绑定

                        //add

                        //微信登录后台验证
                        if(status.equals("login")){
                            weChatLogin(wxUserInfo,status);
                        }if(status.equals("bind")){
                            weChatBind(wxUserInfo,status);
                        }

                    }
                });
    }

    private void weChatBind(final WXUserInfo wxUserInfo,final String status) {
        String phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

        RetrofitService.getRetrofitApi().getBindInfo(MLConfig.HTTP_APP_KEY, wxUserInfo.getUnionid(), phone, "appphone")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BinderInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BinderInfo binderInfo) {
                        switch (binderInfo.getRet()) {
                            case "1":
                                //PreferencesUtils.putString(context, MLProperties.PREFER_KEY_USER_ID, binderInfo.getData().getPhone());
                                //iView.getBindInfoSuccess(binderInfo.getData().getPhone());
                                weChatLogin(wxUserInfo,status);
                                break;
                            case "2":
                                //iView.getBindInfoFail(binderInfo.getMsg());
                                break;
                            case "3":
                                //iView.getBindInfoFail(binderInfo.getMsg());
                                break;
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

    private void weChatLogin(final WXUserInfo wxUserInfo,final String status) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL + APPUPDATEWEIXINUSERINFO)
                        .params("headimgUrl", wxUserInfo.getHeadimgurl())
                        .params("unionid", wxUserInfo.getUnionid())
                        .params("nickname", wxUserInfo.getNickname())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("app更新微信用户信息", str);
                                try {
                                    JSONObject jsonObject = new JSONObject(str);
                                    String ret = jsonObject.optString("ret");
                                    e.onNext(ret);
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
                    public void onNext(String s) {
                        if (s.equals("1")) {
                            getUserInfo(wxUserInfo,status);
                        } else {
                            T.showShort(WXEntryActivity.this, "微信登录失败");
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

    //登录后获取用户信息
    private void getUserInfo(WXUserInfo wxUserInfo,final String status) {
        RetrofitService.getRetrofitApi().getUserInfo(MLConfig.HTTP_APP_KEY, "", wxUserInfo.getUnionid(), "weixin")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onNext(UserInfo userInfo) {
                        Log.e("微信登录后获取用户数据返回", userInfo.getData().toString());
                        UserInfo.DataBean bean = userInfo.getData();

                        //保存手机号
                        if (!StringUtils.isEmpty(bean.getTeacherphone())) {
                            PreferencesUtils.putString(getApplicationContext(), MLProperties.PREFER_KEY_USER_ID, bean.getTeacherphone());
                        }
                        PreferencesUtils.putString(getApplicationContext(), MLProperties.BUNDLE_KEY_TEACHER_NICK, bean.getNicheng());
                        PreferencesUtils.putString(getApplicationContext(), MLProperties.BUNDLE_KEY_TEACHER_IMG, bean.getTeacherimg_url());
                        PreferencesUtils.putString(getApplicationContext(), MLProperties.PREFER_KEY_WECHAT_UNIONID, bean.getWeixin_unionid());

                        PreferencesUtils.putString(getApplicationContext(),MLProperties.PREFER_KEY_WECHAT_NICHENG,bean.getWeixin_nicheng());
                        PreferencesUtils.putString(getApplicationContext(), MLProperties.PREFER_KEY_TEACHER_GROUPID, bean.getTeacher_groupid());
                       if(status.equals("login")){
                           EventBus.getDefault().post(new MsgEvent("wxloginsuccess"));
                       }if(status.equals("bind")){
                            EventBus.getDefault().post(new MsgEvent("wxbindsuccess"));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("获取失败", "");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}