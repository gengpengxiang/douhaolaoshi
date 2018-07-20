package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.login.view.LoginActivity;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.GROUPJOIN;

/**
 * Created by zz379 on 2017/1/5.
 * 关于页面
 */

public class SettingActivity extends BaseActivity {

    @BindView(R.id.layout_inviteCode)
    LinearLayout layoutInviteCode;
    @BindView(R.id.scrollview)
    ScrollView scrollview;
    @BindView(R.id.edit)
    EditText edit;
    private Intent intent;
    private String symbol;
    private Unbinder unbinder;
    //private String teacherPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        unbinder = ButterKnife.bind(this);
        //teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

        intent = getIntent();
        symbol = intent.getStringExtra("symbol");
        initToolBar();
        initView();

        findViewById(R.id.bttest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IWXAPI api = WXAPIFactory.createWXAPI(SettingActivity.this, MLProperties.APP_DOUHAO_TEACHER_ID);
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = System.currentTimeMillis() + "";
                api.sendReq(req);
            }
        });
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        LinearLayout llLeft = (LinearLayout) this.findViewById(R.id.header_ll_left);
        llLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });
        ImageView imgBack = (ImageView) this.findViewById(R.id.header_img_back);
        imgBack.setVisibility(View.VISIBLE);
        TextView tvTitle = (TextView) this.findViewById(R.id.header_tv_title);
        tvTitle.setVisibility(View.VISIBLE);

        //add by gpx
        if (symbol.equals("UserFragment")) {
            tvTitle.setText("关于逗号老师");
            scrollview.setVisibility(View.VISIBLE);
        }
        if (symbol.equals("GroupAllActivity")) {
            tvTitle.setText("邀请码");
            layoutInviteCode.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void initView() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("aboutCompany");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("aboutCompany");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.bt_confirm)
    public void onClick() {

        Log.e("手机号",PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID)+"ddd");

        if (LoginStatusUtil.noLogin(this)) {
            IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
            return;
        }
        if(PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID) == null
                || PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID).equals("0")
                || StringUtils.isEmpty(PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID))){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("laiyuan","bind_first");
            startActivity(intent);
            return;
        }
        else {

            if (TextUtils.isEmpty(edit.getText().toString())) {
                T.showShort(SettingActivity.this, "输入内容为空");
            } else {
                final String unionid = PreferencesUtils.getString(SettingActivity.this, MLProperties.PREFER_KEY_WECHAT_UNIONID,"");
                final String phone = PreferencesUtils.getString(SettingActivity.this, MLProperties.PREFER_KEY_USER_ID," ");
              // Log.e("数据","unionid=="+unionid+"phone=="+phone+"yqm=="+edit.getText().toString());
                Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
                    @Override
                    public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                        OkGo.<String>post(BASE_URL + GROUPJOIN)
                                .params("appkey", MLConfig.HTTP_APP_KEY)
                                .params("unionid",unionid)
                                .params("usercode", phone)
                                .params("yqm", edit.getText().toString())

                                .execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        String str = response.body().toString();
                                        Log.e("邀请码返回数据", str);
                                        BaseDataInfo groupInfo = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                        });
                                        e.onNext(groupInfo);
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
                                T.showShort(SettingActivity.this, dataInfo.getMsg());
                                if (dataInfo.getRet().equals("1")) {
                                    //setResult(2);
                                    EventBus.getDefault().post(new MsgEvent("groupupdate"));
                                    finish();
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

        }
    }

}
