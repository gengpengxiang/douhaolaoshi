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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.zzimgselector.view.ImageSelectorActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;

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

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.utils.BitmapUtils.changeFileSize;
import static com.taobao.accs.ACCSManager.mContext;

/**
 * Created by zz379 on 2017/9/27.
 * 用户信息完善页面，昵称，头像设置
 */

public class CompleteUserInfoActivity extends BaseActivity {

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
    private String currPhotoPath,userPhotoPath;
    private String currNickName,userNickName;
    private LmsDataService mService;
    private String unionid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_user_info);
        ButterKnife.bind(this);

        initToolBar();
        initView();
        initData();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
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
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID,"");
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
        if (StringUtils.isEmpty(currPhotoPath)) {
            T.showShort(this, "请选择一张图片作为您的头像！");
            return;
        }
        if (StringUtils.isEmpty(currNickName)) {
            T.showShort(this, "昵称不能为空！");
            return;
        }

        // 开始完善用户的头像和昵称信息
        showLoadingDialog();
        updateUserPhoto();
    }

    private void updateUserPhoto() {
        final String phoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        final String unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                String[] result = mService.uploadKidPhoto(phoneNumber,unionid, currPhotoPath);
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
                           updateUserNickName();
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

    private void updateUserNickName() {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                String[] result = mService.updateUserNickName(teacherPhoneNumber,unionid, currNickName);
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
                            userNickName = currNickName;
                            Log.e("昵称设置成功",userNickName);
                            PreferencesUtils.putString(CompleteUserInfoActivity.this, MLProperties.BUNDLE_KEY_TEACHER_NICK, userNickName);
                            // 上传昵称成功后登录
                            login();
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

    private void login() {
        EventBus.getDefault().post(new MsgEvent("phoneloginsuccess"));
        hideLoadingDialog();
        Intent intent = new Intent(CompleteUserInfoActivity.this, MainActivity.class);
        startActivity(intent);
        CompleteUserInfoActivity.this.finish();
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
