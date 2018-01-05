package com.bj.eduteacher.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.presenter.LoginHelper;
import com.bj.eduteacher.presenter.viewinface.LoginView;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.zzimgselector.view.ImageSelectorActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.utils.BitmapUtils.changeFileSize;
import static com.taobao.accs.ACCSManager.mContext;

/**
 * Created by zz379 on 2017/9/27.
 */

public class CompleteUserInfoActivity extends BaseActivity implements LoginView {

    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.header_img_back)
    ImageView ivBack;
    @BindView(R.id.iv_userPhoto)
    SimpleDraweeView imgUserPhoto;
    @BindView(R.id.edt_nickname)
    EditText edtNickname;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.scrollview)
    ScrollView mScrollView;
    @BindView(R.id.tv_withProblem)
    TextView tvWithProblem;

    private Handler mHandler = new Handler();

    private String teacherPhoneNumber;
    private String userPhotoPath;
    private String userNickName;
    private String currPhotoPath;
    private String currNickName;

    private String sxbStatus;

    private LmsDataService mService;
    private LoginHelper sxbLoginHelper;

    private String loginSuccAction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_user_info);
        ButterKnife.bind(this);
        sxbLoginHelper = new LoginHelper(this, this);

        initToolBar();
        initView();
        initData();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        loginSuccAction = getIntent().getExtras().getString("LoginSuccAction", "0");
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("完善个人资料");
    }

    @Override
    protected void initView() {
        edtNickname.setFilters(new InputFilter[]{emojiFilter, specialCharFilter, new InputFilter.LengthFilter(8)});
        edtNickname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeScrollView();
                return false;
            }
        });
    }

    @Override
    protected void initData() {
        mService = new LmsDataService();
        sxbStatus = getIntent().getStringExtra("SxbStatus");
        userPhotoPath = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_TEACHER_IMG, "");
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        userNickName = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_TEACHER_NICK, "");

        // 用户头像
        if (!StringUtils.isEmpty(userPhotoPath)) {
            imgUserPhoto.setImageURI(Uri.parse(userPhotoPath));
        }
        // 用户昵称
        if (!StringUtils.isEmpty(userNickName)) {
            edtNickname.setText(userNickName);
        }
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

    @OnClick(R.id.header_ll_left)
    void clickBack() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        // 退出之前需要先把所有数据都清空
        cleanAllPreferencesData();
        super.onBackPressed();
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

    @OnClick(R.id.ll_content)
    void clickBlankSpace() {
        KeyBoardUtils.closeKeybord(this.getCurrentFocus().getWindowToken(), this);
    }

    @OnClick(R.id.tv_complete)
    void clickComplete() {
        currNickName = edtNickname.getText().toString().trim();
        KeyBoardUtils.closeKeybord(edtNickname, this);
        if (StringUtils.isEmpty(userPhotoPath) && StringUtils.isEmpty(currPhotoPath)) {
            T.showShort(this, "请选择一张图片作为您的头像！");
            return;
        }
        if (StringUtils.isEmpty(userNickName) && StringUtils.isEmpty(currNickName)) {
            T.showShort(this, "昵称不能为空！");
            return;
        }

        // 开始晚上用户的头像和昵称信息
        showLoadingDialog();
        if (!StringUtils.isEmpty(currPhotoPath)) {
            updateUserPhoto(currPhotoPath);
        } else {
            if (!StringUtils.isEmpty(currNickName) && !currNickName.equals(userNickName)) {
                updateUserNickname(currNickName);
            } else {
                loginLiveAndEase();
            }
        }
    }

    @OnClick(R.id.iv_userPhoto)
    void clickUserPhoto() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Boolean success) {
                        if (success) {
                            actionSelectKidPhoto();
                        } else {
                            T.showShort(CompleteUserInfoActivity.this, "未获取到相机权限");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void actionSelectKidPhoto() {
        ImageSelectorActivity.start(CompleteUserInfoActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, true, true, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            // do something
            String filePath = images.get(0);
            Log.i("way", "FilePath:" + filePath);
            if (filePath != null && !filePath.equals("")) {
                File file = new File(filePath);
                Log.i("way", "FileSize:" + file.length() / 1024 + "KB");
                currPhotoPath = changeFileSize(filePath);
                File newFile = new File(currPhotoPath);
                Log.i("way", "newFileSize:" + newFile.length() / 1024 + "KB");
                Uri uri = Uri.parse("file://" + currPhotoPath);
                imgUserPhoto.setImageURI(uri);
            }
        }
    }

    /**
     * 上传用户头像
     *
     * @param filePath
     */
    private void updateUserPhoto(final String filePath) {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                String[] result = mService.uploadKidPhoto(teacherPhoneNumber, filePath);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull String[] result) {
                        if (StringUtils.isEmpty(result[0]) || result[0].equals("0")) {
                            T.showShort(CompleteUserInfoActivity.this, StringUtils.isEmpty(result[1]) ? "服务器开小差了，请待会重试" : result[1]);
                        } else {
                            userPhotoPath = result[1];
                            PreferencesUtils.putString(CompleteUserInfoActivity.this, MLProperties.BUNDLE_KEY_TEACHER_IMG, userPhotoPath);
                            // 上传头像成功后，如果需要就上传昵称，否则结束页面
                            if (StringUtils.isEmpty(userNickName)) {
                                updateUserNickname(currNickName);
                            } else {
                                loginLiveAndEase();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoadingDialog();
                        T.showShort(CompleteUserInfoActivity.this, "服务器开小差了，请稍后重试！");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 登录直播和环信功能
     */
    private void loginLiveAndEase() {
        MySelfInfo.getInstance().setAvatar(userPhotoPath);
        MySelfInfo.getInstance().setNickName(userNickName);
        MySelfInfo.getInstance().writeToCache(mContext);
        // 判断后续相关初始化操作,根据直播状态判断下一步的动作
        sxbLoginHelper.checkSxbLiveStatus(sxbStatus, teacherPhoneNumber);
    }

    /**
     * 上传用户昵称
     *
     * @param nickname
     */
    private void updateUserNickname(final String nickname) {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                String[] result = mService.updateUserNickName(teacherPhoneNumber, nickname);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String[] result) {
                        if (StringUtils.isEmpty(result[0]) || result[0].equals("0")) {
                            T.showShort(CompleteUserInfoActivity.this, StringUtils.isEmpty(result[1]) ? "服务器开小差了，请待会重试" : result[1]);
                        } else {
                            userNickName = nickname;
                            PreferencesUtils.putString(CompleteUserInfoActivity.this, MLProperties.BUNDLE_KEY_TEACHER_NICK, userNickName);
                            // 上传昵称成功后登录直播和环信
                            loginLiveAndEase();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoadingDialog();
                        T.showShort(CompleteUserInfoActivity.this, "服务器开小差了，请稍后重试！");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void loginSucc() {
        // 检查环信是否登录
        // checkIsLoginEase();
        // 去掉环信功能，所以需要跳过环信的登录检测
        loginSuccess();
    }

    @Override
    public void completeInfo(String sxbStatus) {

    }

    @Override
    public void loginFail(String module, int errCode, String errMsg) {
        T.showShort(this, "网络连接异常，请稍后重试！");
        Log.i("way", "modole: " + module + "-- errCode: " + errCode + " -- errMsg: " + errMsg);
    }

    /**
     * 判断环信是否已经登录过，登录过就推出
     */
    private void checkIsLoginEase() {
        // 登录环信，成功后 获取教师信息
        if (EMClient.getInstance().isLoggedInBefore()) {
            EMClient.getInstance().logout(true, new EMCallBack() {
                @Override
                public void onSuccess() {
                    login2Ease(teacherPhoneNumber);
                }

                @Override
                public void onError(int code, String error) {

                }

                @Override
                public void onProgress(int progress, String status) {

                }
            });
        } else {
            login2Ease(teacherPhoneNumber);
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
                        Log.d("way", userEaseID + "登录失败，请重新发送验证码" + " " + code);
                        if (code == 200) {
                            // 加载环信相关数据
                            EMClient.getInstance().groupManager().loadAllGroups();
                            EMClient.getInstance().chatManager().loadAllConversations();
                            Log.d("way", "登录聊天服务器成功！");
                            // 登录成功, 跳转到首页
                            loginSuccess();
                        } else {
                            T.showShort(CompleteUserInfoActivity.this, "登录失败，请重新发送验证码" + " " + code);
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
        hideLoadingDialog();
        PreferencesUtils.putLong(this, MLProperties.PREFER_KEY_LOGIN_Time, System.currentTimeMillis());
        PreferencesUtils.putInt(this, MLProperties.PREFER_KEY_LOGIN_STATUS, 1);
        if (loginSuccAction.equals(IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY)) {
            Intent intent = new Intent(CompleteUserInfoActivity.this, MainActivity.class);
            startActivity(intent);
        }
        CompleteUserInfoActivity.this.finish();
    }

    InputFilter emojiFilter = new InputFilter() {
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                Toast.makeText(CompleteUserInfoActivity.this, "不支持输入表情", Toast.LENGTH_SHORT).show();
                return "";
            }
            return null;
        }
    };

    InputFilter specialCharFilter = new InputFilter() {
        String regEx = "[\\s~·`!！～@#￥$%^……&*（()）\\-——\\-_=+【\\[\\]】｛{}｝\\|、\\\\；;：:‘'“”\"，,《<。.》>、/？?]";
        Pattern specialPattern = Pattern.compile(regEx, Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher specialMatcher = specialPattern.matcher(source);
            if (specialMatcher.find()) {
                Toast.makeText(CompleteUserInfoActivity.this, "仅支持输入中英字母、数字", Toast.LENGTH_SHORT).show();
                return "";
            }
            return null;
        }
    };
}
