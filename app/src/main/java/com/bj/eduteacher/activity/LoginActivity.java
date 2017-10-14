package com.bj.eduteacher.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.dialog.HelpAlertDialog;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.presenter.LoginHelper;
import com.bj.eduteacher.presenter.viewinface.LoginView;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by he on 2016/12/19.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener, LoginView {

    private EditText edtPhoneNumber, edtCode;
    private SimpleDraweeView btnLogin;
    private TextView tvGetCode, tvWithProblem;
    private int timeRemaining; //剩余时间
    private ScrollView mScrollView;
    private ProgressDialog mProgressDialog;
    private LinearLayout llDouhaoProtocol;

    private LoginHelper sxbLoginHelper;

    private String loginSuccAction;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 如果存在虚拟按键，则设置虚拟按键的背景色
        if (ScreenUtils.checkDeviceHasNavigationBar(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, android.R.color.black));
            }
        }

        setContentView(R.layout.activity_login);
        sxbLoginHelper = new LoginHelper(this, this);
        // 初始化页面
        initToolBar();
        initView();
        initDatas();

        // 恢复数据
        if (savedInstanceState != null && !StringUtils.isEmpty(savedInstanceState.getString("PhoneNumber"))) {
            edtPhoneNumber.post(new Runnable() {
                @Override
                public void run() {
                    edtPhoneNumber.setText(savedInstanceState.getString("PhoneNumber"));
                }
            });
        }
    }

    private void initToolBar() {
        loginSuccAction = getIntent().getExtras().getString("LoginSuccAction", "0");
        ImageView ivBack = (ImageView) this.findViewById(R.id.header_img_back);
        ivBack.setVisibility(View.VISIBLE);
        TextView tvTitle = (TextView) this.findViewById(R.id.header_tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.app_name);
        LinearLayout llLeft = (LinearLayout) this.findViewById(R.id.header_ll_left);
        llLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        mScrollView = (ScrollView) this.findViewById(R.id.scrollview);
        LinearLayout llContent = (LinearLayout) this.findViewById(R.id.ll_content);
        llContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardUtils.closeKeybord(LoginActivity.this.getCurrentFocus().getWindowToken()
                        , LoginActivity.this);
            }
        });
        edtPhoneNumber = (EditText) this.findViewById(R.id.edt_phoneNumber);
        edtCode = (EditText) this.findViewById(R.id.edt_verificationCode);
        btnLogin = (SimpleDraweeView) this.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        tvGetCode = (TextView) this.findViewById(R.id.tv_getCode);
        tvGetCode.setOnClickListener(this);
        tvWithProblem = (TextView) this.findViewById(R.id.tv_withProblem);
        tvWithProblem.setOnClickListener(this);
        llDouhaoProtocol = (LinearLayout) this.findViewById(R.id.ll_douhaoProtocol);

        edtPhoneNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeScrollView();
                return false;
            }
        });
        edtCode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeScrollView();
                return false;
            }
        });
        // addLayoutListener(llContent, tvWithProblem);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(android.R.style.Theme_Material_Dialog_Alert);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("数据加载中...");
        mProgressDialog.setCancelable(false);

        llDouhaoProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis() - currTimeMillin >= 1000) {
                    currTimeMillin = System.currentTimeMillis();
                    Intent intent = new Intent(LoginActivity.this, ProtocolActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void initDatas() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                actionLogin();
                break;
            case R.id.tv_getCode:
                MobclickAgent.onEvent(LoginActivity.this, "login_code");
                actionGetCode();
                break;
            case R.id.tv_withProblem:
                KeyBoardUtils.closeKeybord(LoginActivity.this.getCurrentFocus().getWindowToken()
                        , LoginActivity.this);
                actionForHelp();
                break;
        }
    }

    private void actionForHelp() {
        HelpAlertDialog dialog = new HelpAlertDialog(LoginActivity.this);
        dialog.setContentText("使用逗号老师过程中遇到任何问题或您有什么建议，请联系逗号小助手。\n微信号：pkugame\n电话：15201635868\n邮箱：douhaojiaoyu@163.com");
        dialog.show();
    }

    private long currTimeMillin;

    /**
     * 点击login按钮
     */
    private void actionLogin() {
        if (System.currentTimeMillis() - currTimeMillin < 1000) {
            currTimeMillin = System.currentTimeMillis();
        } else {
            currTimeMillin = System.currentTimeMillis();

            KeyBoardUtils.closeKeybord(LoginActivity.this.getCurrentFocus().getWindowToken()
                    , LoginActivity.this);
            String phoneNum = edtPhoneNumber.getText().toString().trim();
            String codeNum = edtCode.getText().toString().trim();
            if (StringUtils.isEmpty(phoneNum)) {
                T.showShort(this, "请输入手机号");
                return;
            }
            if (!StringUtils.checkPhoneNumber(phoneNum)) {
                T.showShort(this, "手机号输入有误");
                return;
            }
            if (StringUtils.isEmpty(codeNum)) {
                T.showShort(this, "请输入验证码");
                return;
            }

            // 判断手机号和验证码是否匹配
            btnLogin.setClickable(false);
            mProgressDialog.show();
            sxbLoginHelper.pkuLogin(phoneNum, codeNum);
        }
    }

    private void actionGetCode() {
        if (System.currentTimeMillis() - currTimeMillin < 1000) {
            currTimeMillin = System.currentTimeMillis();
        } else {
            currTimeMillin = System.currentTimeMillis();
            KeyBoardUtils.closeKeybord(LoginActivity.this.getCurrentFocus().getWindowToken()
                    , LoginActivity.this);
            String phoneNum = edtPhoneNumber.getText().toString().trim();
            if (StringUtils.isEmpty(phoneNum)) {
                T.showShort(this, "请输入手机号");
                return;
            }
            if (!StringUtils.checkPhoneNumber(phoneNum)) {
                T.showShort(this, "手机号输入有误");
                return;
            }
            // 获取验证码，并开始倒计时

            MyGetCodeTask myGetCodeTask = new MyGetCodeTask();
            myGetCodeTask.execute(phoneNum);
        }
    }

    private void startTimerSchedule() {
        timeRemaining = 61;
        tvGetCode.setTextColor(ContextCompat.getColor(this, R.color.text_gray));
        tvGetCode.setClickable(false);

        mHandler.post(timerRunnable);
    }

    Handler mHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timeRemaining--;
            tvGetCode.setText(String.format("%d秒后可重发", timeRemaining));
            if (timeRemaining < 0) {
                tvGetCode.setText("获取验证码");
                tvGetCode.setTextColor(ContextCompat.getColor(LoginActivity.this,
                        R.color.colorPrimary));
                tvGetCode.setClickable(true);
            } else {
                mHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        sxbLoginHelper.onDestory();
        mHandler.removeCallbacks(timerRunnable);
        super.onDestroy();
    }

    /**
     * 使ScrollView指向底部
     */
    private void changeScrollView() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, tvWithProblem.getBottom() - mScrollView.getHeight());
            }
        }, 300);
    }

    private class MyGetCodeTask extends AsyncTask<String, Integer, String[]> {
        @Override
        protected void onPreExecute() {
            tvGetCode.setClickable(false);
            startTimerSchedule();   // 开始计时
        }

        @Override
        protected String[] doInBackground(String... params) {
            LmsDataService mService = new LmsDataService();
            String[] result;
            try {
                result = mService.getCodeFromAPI2(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                LL.e(e);
                result = new String[2];
                result[0] = "0";
                result[1] = "服务器开小差了，请待会重试";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (!StringUtils.isEmpty(result[0]) && result[0].equals("1")) {
                // startTimerSchedule();   // 开始计时
                T.showShort(LoginActivity.this, "获取验证码成功");
            } else {
                tvGetCode.setClickable(true);
                T.showShort(LoginActivity.this, StringUtils.isEmpty(result[1]) ? "发送验证码失败" : result[1]);
            }
        }
    }

    @Override
    public void loginSucc() {
        // 检查环信是否登录
        checkIsLoginEase();
    }

    @Override
    public void completeInfo(String sxbStatus) {
        // 需要去完善用户的信息
        Intent intent = new Intent(this, CompleteUserInfoActivity.class);
        intent.putExtra("SxbStatus", sxbStatus);
        intent.putExtra("LoginSuccAction", loginSuccAction);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    @Override
    public void loginFail(String module, int errCode, String errMsg) {
        if (!StringUtils.isEmpty(module)) {
            T.showShort(this, errMsg);
        } else {
            T.showShort(this, "网络连接异常，请稍后重试！");
        }
        Log.i("way", "modole: " + module + "-- errCode: " + errCode + " -- errMsg: " + errMsg);
        btnLogin.setClickable(true);
        mProgressDialog.dismiss();
    }

    /**
     * 判断环信是否已经登录过，登录过就推出
     */
    private void checkIsLoginEase() {
        final String phoneNum = edtPhoneNumber.getText().toString().trim();
        // 登录环信，成功后 获取教师信息
        if (EMClient.getInstance().isLoggedInBefore()) {
            EMClient.getInstance().logout(true, new EMCallBack() {
                @Override
                public void onSuccess() {
                    login2Ease(phoneNum);
                }

                @Override
                public void onError(int code, String error) {

                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        } else {
            login2Ease(phoneNum);
        }
    }

    /**
     * 登录到环信
     *
     * @param userPhoneNumber
     */
    private void login2Ease(final String userPhoneNumber) {
        final String userEaseID = MLConfig.EASE_TEACHER_ID_PREFIX + userPhoneNumber;
        EMClient.getInstance().login(userEaseID, "123456", new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                // 加载环信相关数据
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("way", userEaseID + "登录聊天服务器成功！");
                // 登录成功, 跳转到首页
                loginSuccess();
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(final int code, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnLogin.setClickable(true);
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        Log.d("way", userEaseID + "登录失败，请重新发送验证码" + " " + code);
                        if (code == 200) {
                            // 加载环信相关数据
                            EMClient.getInstance().groupManager().loadAllGroups();
                            EMClient.getInstance().chatManager().loadAllConversations();
                            Log.d("way", "登录聊天服务器成功！");
                            // 登录成功, 跳转到首页
                            loginSuccess();
                        } else {
                            T.showShort(LoginActivity.this, "登录失败，请重新发送验证码" + " " + code);
                        }
                    }
                });
            }
        });
    }

    /**
     * 登录成功后跳转到首页
     */
    private void loginSuccess() {
        PreferencesUtils.putLong(this, MLProperties.PREFER_KEY_LOGIN_Time, System.currentTimeMillis());
        PreferencesUtils.putInt(this, MLProperties.PREFER_KEY_LOGIN_STATUS, 1);
        if (loginSuccAction.equals(IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
        LoginActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        cleanAllPreferencesData();
        this.finish();
        overridePendingTransition(R.anim.act_alpha_in, R.anim.act_top_bottom_out);
    }

    private void cleanAllPreferencesData() {
        // 清除所有app内的数据
        PreferencesUtils.cleanAllData(this);
        // 清除直播设置数据
        getSharedPreferences("data", Context.MODE_PRIVATE).edit().clear().commit();
        // 清除直播个人数据
        getSharedPreferences(Constants.USER_INFO, Context.MODE_PRIVATE).edit().clear().commit();
        // 清除环信数据
        getSharedPreferences("EM_SP_AT_MESSAGE", Context.MODE_PRIVATE).edit().clear().commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("login");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("login");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("PhoneNumber", edtPhoneNumber.getText().toString());
    }
}
