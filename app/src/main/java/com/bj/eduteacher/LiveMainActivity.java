package com.bj.eduteacher;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.bj.eduteacher.activity.LiveActivity;
import com.bj.eduteacher.activity.PublishLiveActivity;
import com.bj.eduteacher.model.CurLiveInfo;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.presenter.LoginHelper;
import com.bj.eduteacher.presenter.viewinface.LoginView;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.widget.dialog.NotifyDialog;

public class LiveMainActivity extends AppCompatActivity implements View.OnClickListener, LoginView {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private TextView tvMaster;
    private TextView tvGuester;

    private LoginHelper loginHelper;
    private TextView tvSelfInfo;
    private boolean living;

    // id : zz379 , zz397   password : zz123456

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_main);
        // 检查权限
        checkRxPermissions();
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        living = pref.getBoolean("living", false);

        loginHelper = new LoginHelper(this, this);
        initView();
        userLogin();
    }

    private void initView() {
        tvMaster = (TextView) findViewById(R.id.tvMaster);
        tvGuester = (TextView) findViewById(R.id.tvGuester);
        tvSelfInfo = (TextView) findViewById(R.id.tvSelfInfo);
        tvMaster.setOnClickListener(this);
        tvGuester.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvMaster:
                intentToMaster();
                break;
            case R.id.tvGuester:
                intentToGuester();
                break;
            default:
                break;
        }
    }

    private void intentToMaster() {
        Intent intent = new Intent(this, PublishLiveActivity.class);
        startActivity(intent);
    }

    private void intentToGuester() {
        Intent intent = new Intent(this, LiveActivity.class);
        MySelfInfo.getInstance().setIdStatus(Constants.MEMBER);
        MySelfInfo.getInstance().setJoinRoomWay(false);
        MySelfInfo.getInstance().setGuestRole(Constants.SD_GUEST);// 观看模式：清晰
        MySelfInfo.getInstance().writeToCache(getApplicationContext());
        CurLiveInfo.setHostName("zz397");
        CurLiveInfo.setHostID("zz397");
        CurLiveInfo.setHostAvator("");
        CurLiveInfo.setRoomNum(94283);
        CurLiveInfo.setMembers(2); // 添加自己
        CurLiveInfo.setAdmires(100);

        startActivity(intent);
    }

    /**
     * 检查权限
     */
    private void checkRxPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CAMERA).subscribe();
    }

    private void userLogin() {
        if ("Xiaomi".equals(Build.BRAND)) {
            loginHelper.standardLogin("zz397", "zz123456");
        } else {
            loginHelper.standardLogin("zz379", "zz123456");
        }

        // 手动设置房间号
        MySelfInfo.getInstance().setMyRoomNum(94283);
    }

    @Override
    public void loginSucc() {
        tvSelfInfo.setText("");
        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        tvSelfInfo.setText(MySelfInfo.getInstance().toString());
        tvSelfInfo.append("\n手机厂商: " + Build.BRAND);
        Log.i("way", MySelfInfo.getInstance().toString());
        // 检查上次是否是异常推出直播
        checkLiveException();
    }

    @Override
    public void loginFail(String module, int errCode, String errMsg) {
        Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
        tvSelfInfo.setText("");
    }

    @Override
    protected void onDestroy() {
        loginHelper.onDestory();
        super.onDestroy();
    }

    private void checkLiveException() {
        if (living) {
            NotifyDialog dialog = new NotifyDialog();
            dialog.show(getString(R.string.title_living), getSupportFragmentManager(), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(LiveMainActivity.this, LiveActivity.class);
                    MySelfInfo.getInstance().setIdStatus(Constants.HOST);
                    MySelfInfo.getInstance().setJoinRoomWay(true);
                    CurLiveInfo.setHostID(MySelfInfo.getInstance().getId());
                    CurLiveInfo.setHostName(MySelfInfo.getInstance().getId());
                    CurLiveInfo.setHostAvator("");
                    CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
                    startActivity(intent);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("living", false);
                    editor.apply();
                }
            });
        }
    }
}
