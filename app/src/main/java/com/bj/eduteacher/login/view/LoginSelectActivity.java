package com.bj.eduteacher.login.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.ProtocolActivity;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StatusBarCompat;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoginSelectActivity extends BaseActivity {

    private Unbinder unbinder;
    private String userPhoneNumber;
    private String unionid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        StatusBarCompat.fullScreen(this);
        //改变底部导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID,"");
        setContentView(R.layout.activity_login_select);
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uodate(MsgEvent event){
        if(event.getAction().equals("phoneloginsuccess")){
            finish();
        }if(event.getAction().equals("wxloginsuccess")){
            finish();
        }
    }

    @OnClick({R.id.tv_close, R.id.layoutweixin, R.id.tv_phonelogin, R.id.tv_look})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_close:
                finish();
                break;
            case R.id.layoutweixin:
                wxLogin();
                break;
            case R.id.tv_phonelogin:
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("laiyuan","login");
                startActivity(intent);
                break;
            case R.id.tv_look:
                Intent intent2 = new Intent(this, ProtocolActivity.class);
                startActivity(intent2);
                break;
        }
    }

    private void wxLogin() {
        IWXAPI api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "login";
        api.sendReq(req);
    }
}
