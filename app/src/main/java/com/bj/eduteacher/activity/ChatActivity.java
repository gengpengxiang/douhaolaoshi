package com.bj.eduteacher.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.bj.eduteacher.R;
import com.bj.eduteacher.api.Constant;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.zzeaseui.ui.EaseBaseActivity;
import com.bj.eduteacher.zzeaseui.ui.EaseChatFragment;
import com.hyphenate.util.EasyUtils;
import com.jaeger.library.StatusBarUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;

/**
 * chat activity，EaseChatFragment was used {@link EaseChatFragment}
 */
public class ChatActivity extends EaseBaseActivity implements EaseChatFragment.OnBackClickListener {
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    String toChatUsername;
    String currUserPhoto;
    String currUserNick;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        // 如果存在虚拟按键，则设置虚拟按键的背景色
        if (ScreenUtils.checkDeviceHasNavigationBar(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, android.R.color.black));
            }
        }
        setContentView(R.layout.em_activity_chat);
        // 设置状态栏颜色
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary));
        activityInstance = this;
        Bundle bundle = getIntent().getExtras();
        currUserPhoto = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_TEACHER_IMG);
        currUserNick = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_CLASS_NAME);
        bundle.putString("currUserPhoto", currUserPhoto);
        bundle.putString("currUserNick", currUserNick);

        //get user id or group id
        toChatUsername = bundle.getString(Constant.EXTRA_USER_ID);

        //use EaseChatFratFragment
        chatFragment = new EaseChatFragment();
        chatFragment.setBackClickListener(this);
        //pass parameters to chat fragment
        chatFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

        // 录音、拍照的权限
        requredPermission();
    }

    private void requredPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)
                .subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityInstance = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            onBackClick();
        }
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    @Override
    public void onBackClick() {
        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
        intent.putExtra(MLProperties.BUNDLE_KEY_MAIN_PAGEINDEX, 1);
        startActivity(intent);
        this.finish();
        overridePendingTransition(R.anim.left_right_in, R.anim.left_right_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
