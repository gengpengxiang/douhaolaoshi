package com.bj.eduteacher;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewTreeObserver;

import com.bj.eduteacher.dialog.UpdateAPPAlertDialog;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.utils.ConnectionChangeReceiver;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.widget.LoadingDialog;
import com.bj.eduteacher.zzautolayout.AutoLayoutActivity;
import com.jaeger.library.StatusBarUtil;
import com.tencent.TIMFriendshipManager;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.umeng.message.PushAgent;

/**
 * Created by he on 2016/11/9.
 */

public class BaseActivity extends AutoLayoutActivity {

    private LoadingDialog loadingDialog;
    private UpdateAPPAlertDialog updateAPPDialog;

    private BroadcastReceiver recv;
    private ConnectionChangeReceiver netWorkStateReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 统计应用启动数据
        PushAgent.getInstance(this).onAppStart();
        loadingDialog = new LoadingDialog(this);
        initLive();
    }

    protected void initToolBar() {
        initStatus();
    }

    protected void initView() {
    }

    protected void initData() {
    }

    protected void initStatus() {
        // 设置顶部状态栏颜色
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary), 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            // 如果存在虚拟按键，则设置虚拟按键的背景色
            if (ScreenUtils.isNavigationBarShow(this)) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, android.R.color.black));
            }
        }
    }

    private void initLive() {
        TIMFriendshipManager manager = TIMFriendshipManager.getInstance();
        manager.setNickName("", null);
        ILiveLoginManager.getInstance().setUserStatusListener(new ILiveLoginManager.TILVBStatusListener() {
            @Override
            public void onForceOffline(int error, String message) {
                switch (error) {
                    case ILiveConstants.ERR_KICK_OUT:
                        processOffline(getString(R.string.str_offline_msg));
                        break;
                    case ILiveConstants.ERR_EXPIRE:
                        processOffline("onUserSigExpired|" + message);
                        break;
                }
            }
        });
        recv = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BD_EXIT_APP)) {
                    IntentManager.toLoginActivity(context, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                    finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.BD_EXIT_APP);
        registerReceiver(recv, filter);

        //监听网络变化
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new ConnectionChangeReceiver();
        }
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netWorkStateReceiver, filter2);
    }

    /**
     * 1、获取main在窗体的可视区域
     * 2、获取main在窗体的不可视区域高度
     * 3、判断不可视区域高度
     * 1、大于100：键盘显示  获取Scroll的窗体坐标
     * 算出main需要滚动的高度，使scroll显示。
     * 2、小于100：键盘隐藏
     *
     * @param main   根布局
     * @param scroll 需要显示的最下方View
     */
    public void addLayoutListener(final View main, final View scroll) {
        main.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                main.getWindowVisibleDisplayFrame(rect);
                int mainInvisibleHeight = main.getRootView().getHeight() - rect.bottom;
                if (mainInvisibleHeight > 100) {
                    int[] location = new int[2];
                    scroll.getLocationInWindow(location);
                    int srollHeight = (location[1] + scroll.getHeight()) - rect.bottom;
                    main.scrollTo(0, srollHeight);
                } else {
                    main.scrollTo(0, 0);
                }
            }
        });
    }

    public static final String TAG_EXIT = "exitApp";

    /**
     * 显示加载对话框
     */
    public void showLoadingDialog() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    /**
     * 隐藏加载对话框
     */
    public void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public void createUpdateAppDialog(String title, String content, UpdateAPPAlertDialog.OnSweetClickListener confirmListener,
                                      UpdateAPPAlertDialog.OnSweetClickListener cancelListener) {
        UpdateAPPAlertDialog dialog = new UpdateAPPAlertDialog(this);
        dialog.setTitleText(title);
        dialog.setContentText(content);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setConfirmClickListener(confirmListener);
        dialog.setCancelClickListener(cancelListener);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(recv);
            unregisterReceiver(netWorkStateReceiver);
        } catch (Exception e) {
        }
        super.onDestroy();
    }

    private void processOffline(String message) {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_tips_title)
                .setMessage(message)
                .setPositiveButton(R.string.btn_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putBoolean("living", false);
                editor.apply();
                MySelfInfo.getInstance().clearCache(getBaseContext());
                getBaseContext().sendBroadcast(new Intent(Constants.BD_EXIT_APP));
            }
        });
        alertDialog.show();
    }
}
