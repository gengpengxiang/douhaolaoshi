package com.bj.eduteacher.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.TeacherInfo;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.presenter.viewinface.LoginView;
import com.bj.eduteacher.presenter.viewinface.LogoutView;
import com.bj.eduteacher.tool.SxbLog;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.tencent.TIMManager;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 登录的数据处理类
 */
public class LoginHelper extends Presenter {

    private Context mContext;
    private static final String TAG = LoginHelper.class.getSimpleName();
    private LoginView mLoginView;
    private LogoutView mLogoutView;
    private LmsDataService mService = new LmsDataService();

    public LoginHelper(Context context) {
        mContext = context;
    }

    public LoginHelper(Context context, LoginView loginView) {
        mContext = context;
        mLoginView = loginView;
    }

    public LoginHelper(Context context, LogoutView logoutView) {
        mContext = context;
        mLogoutView = logoutView;
    }

    /**
     * 我们自己的登录功能
     *
     * @param phone 手机号
     * @param code  验证码
     */
    public void pkuLogin(final String phone, final String code) {
        Observable.create(new ObservableOnSubscribe<TeacherInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<TeacherInfo> e) throws Exception {
                TeacherInfo result = mService.loginFromAPI2(phone, code);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TeacherInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TeacherInfo result) {
                        if (!StringUtils.isEmpty(result.getErrorCode()) && result.getErrorCode().equals("1")) {
                            // 保存该用户是否有直播的权限
                            PreferencesUtils.putString(mContext, MLProperties.PREFER_KEY_USER_SXB_PERMISSIONS, result.getSxbPermissions());
                            PreferencesUtils.putString(mContext, MLProperties.PREFER_KEY_USER_SXB_Title, result.getSxbTitle());
                            PreferencesUtils.putString(mContext, MLProperties.PREFER_KEY_USER_SXB_Picture, result.getSxbPicture());
                            // 登录成功后，根据直播状态判断, 获取教师的个人信息
                            getTeacherInfo(phone, result.getSxbStatus());
                        } else {
                            mLoginView.loginFail(result.getErrorCode(), 0, result.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mLoginView.loginFail("", 0, "验证手机号和验证码是否匹配出现问题");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getTeacherInfo(final String phoneNumber, final String sxbStatus) {
        Observable.create(new ObservableOnSubscribe<TeacherInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<TeacherInfo> e) throws Exception {
                TeacherInfo teacherInfo = mService.getTeacherInfoFromAPI2(phoneNumber);
                e.onNext(teacherInfo);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TeacherInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TeacherInfo teacherInfo) {
                        String nickname = teacherInfo.getTeacherNickname();
                        String teacherImg = teacherInfo.getTeacherImg();

                        PreferencesUtils.putString(mContext, MLProperties.BUNDLE_KEY_TEACHER_NICK, teacherInfo.getTeacherNickname());
                        PreferencesUtils.putString(mContext, MLProperties.BUNDLE_KEY_TEACHER_IMG, teacherInfo.getTeacherImg());
                        PreferencesUtils.putString(mContext, MLProperties.BUNDLE_KEY_CLASS_NAME, teacherInfo.getTeacherName());
                        PreferencesUtils.putString(mContext, MLProperties.PREFER_KEY_USER_ID, teacherInfo.getTeacherPhoneNumber());
                        PreferencesUtils.putString(mContext, MLProperties.BUNDLE_KEY_SCHOOL_NAME, teacherInfo.getSchoolName());
                        PreferencesUtils.putString(mContext, MLProperties.BUNDLE_KEY_SCHOOL_CODE, teacherInfo.getSchoolCode());
                        PreferencesUtils.putString(mContext, MLProperties.BUNDLE_KEY_SCHOOL_IMG, teacherInfo.getSchoolImg());
                        // 直播的相关信息
                        PreferencesUtils.putString(mContext, MLProperties.PREFER_KEY_USER_SXB_User, "sxb" + teacherInfo.getTeacherPhoneNumber());
                        if (StringUtils.isEmpty(nickname) || StringUtils.isEmpty(teacherImg)) {
                            mLoginView.completeInfo(sxbStatus);
                        } else {
                            MySelfInfo.getInstance().setAvatar(teacherInfo.getTeacherImg());
                            MySelfInfo.getInstance().setNickName(teacherInfo.getTeacherNickname());
                            MySelfInfo.getInstance().writeToCache(mContext);
                            // 判断后续相关初始化操作,根据直播状态判断下一步的动作
                            checkSxbLiveStatus(sxbStatus, phoneNumber);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        mLoginView.loginFail("", 0, "获取教师个人信息接口出现异常");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 登录成功后，根据直播状态判断下一步的动作
     */
    public void checkSxbLiveStatus(String sxbStatus, String phoneNum) {
        // 如果 sxbstatus 为0，需要注册腾讯云的托管账号，为1，需要进行登录
        if (!StringUtils.isEmpty(sxbStatus) && "1".equals(sxbStatus)) {
            LL.i("已存在腾讯云托管账号，直接登录");
            standardLogin("sxb" + phoneNum, "sxb" + phoneNum);
        } else {
            LL.i("先注册腾讯云托管账号");
            standardRegister("sxb" + phoneNum, "sxb" + phoneNum);
        }
    }


    //登录模式登录
    private StandardLoginTask loginTask;

    class StandardLoginTask extends AsyncTask<String, Integer, UserServerHelper.RequestBackInfo> {

        @Override
        protected UserServerHelper.RequestBackInfo doInBackground(String... strings) {

            return UserServerHelper.getInstance().loginId(strings[0], strings[1]);
        }

        @Override
        protected void onPostExecute(UserServerHelper.RequestBackInfo result) {

            if (result != null) {
                if (result.getErrorCode() == 0) {
                    MySelfInfo.getInstance().writeToCache(mContext);
                    //登录
                    iLiveLogin(MySelfInfo.getInstance().getId(), MySelfInfo.getInstance().getUserSig());
                } else {
                    mLoginView.loginFail("Module_TLSSDK", result.getErrorCode(), result.getErrorInfo());
                }
            }

        }
    }


    public void iLiveLogin(String id, String sig) {
        Log.i(TAG, "sig:" + sig + "--id:" + id);
        //登录
        ILiveLoginManager.getInstance().iLiveLogin(id, sig, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                Log.d("way", "iLiveLogin->env: " + TIMManager.getInstance().getEnv());
                if (mLoginView != null)
                    mLoginView.loginSucc();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Log.e("way", "IMLogin fail ：" + module + "|" + errCode + " msg " + errMsg);
                if (mLoginView != null)
                    mLoginView.loginFail(module, errCode, errMsg);
            }
        });
    }


    /**
     * 退出imsdk <p> 退出成功会调用退出AVSDK
     */
    public void iLiveLogout() {
        //TODO 新方式登出ILiveSDK
        ILiveLoginManager.getInstance().iLiveLogout(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.i(TAG, "IMLogout succ !");
                //清除本地缓存
                MySelfInfo.getInstance().clearCache(mContext);
                mLogoutView.logoutSucc();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "IMLogout fail ：" + module + "|" + errCode + " msg " + errMsg);
            }
        });
    }

    /**
     * 独立模式 登录
     */
    public void standardLogin(String id, String password) {
        loginTask = new StandardLoginTask();
        loginTask.execute(id, password);

    }


    /**
     * 独立模式 注册
     */
    public void standardRegister(final String id, final String psw) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final UserServerHelper.RequestBackInfo result = UserServerHelper.getInstance().registerId(id, psw);
                if (null != mContext) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        public void run() {

                            if (result != null && result.getErrorCode() == 0) {
                                standardLogin(id, psw);
                            } else if (result != null) {
                                //
                                Toast.makeText(mContext, "  " + result.getErrorCode() + " : " + result.getErrorInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }).start();
    }


    /**
     * 独立模式 登出
     */
    public void standardLogout(final String id) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserServerHelper.RequestBackInfo result = UserServerHelper.getInstance().logoutId(id);
                if (result != null && (result.getErrorCode() == 0 || result.getErrorCode() == 10008)) {
                }
            }
        }).start();
        iLiveLogout();
    }


    @Override
    public void onDestory() {
        mLoginView = null;
        mLogoutView = null;
        mContext = null;
    }
}
