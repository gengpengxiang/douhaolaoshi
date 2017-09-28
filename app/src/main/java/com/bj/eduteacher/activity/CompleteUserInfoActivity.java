package com.bj.eduteacher.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.zzimgselector.view.ImageSelectorActivity;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import static com.bj.eduteacher.utils.BitmapUtils.changeFileSize;

/**
 * Created by zz379 on 2017/9/27.
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

    private Handler mHandler = new Handler();

    private String userPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_user_info);
        ButterKnife.bind(this);

        initToolbar();
        intiView();
    }

    private void initToolbar() {
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setText("完善个人资料");
    }

    private void intiView() {
        edtNickname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeScrollView();
                return false;
            }
        });
    }

    /**
     * 使ScrollView指向底部
     */
    private void changeScrollView() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, tvComplete.getBottom() - mScrollView.getHeight());
            }
        }, 300);
    }

    @OnClick(R.id.header_ll_left)
    void clickBack() {
        onBackPressed();
    }

    @OnClick(R.id.ll_content)
    void clickBlankSpace() {
        KeyBoardUtils.closeKeybord(this.getCurrentFocus().getWindowToken(), this);
    }

    @OnClick(R.id.tv_complete)
    void clickComplete() {
        T.showShort(this, "完成");
        KeyBoardUtils.closeKeybord(edtNickname, this);
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
                String kidPicturePath = changeFileSize(filePath);
                File newFile = new File(kidPicturePath);
                Log.i("way", "newFileSize:" + newFile.length() / 1024 + "KB");
                Uri uri = Uri.parse("file://" + kidPicturePath);
                imgUserPhoto.setImageURI(uri);
            }
        }
    }

    private class UpdateUserPhotoTask extends AsyncTask<String, Integer, String[]> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog();
        }

        @Override
        protected String[] doInBackground(String... params) {
            String kidPicturePath = params[0];
            LmsDataService mService = new LmsDataService();
            String[] result;
            try {
                result = mService.uploadKidPhoto("", kidPicturePath);
            } catch (Exception e) {
                e.printStackTrace();
                LL.e(e);
                result = new String[3];
                result[0] = "0";
                result[1] = "服务器开小差了，请待会重试";
                result[2] = kidPicturePath;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            hideLoadingDialog();
            if (StringUtils.isEmpty(result[0]) || result[0].equals("0")) {
                T.showShort(CompleteUserInfoActivity.this, StringUtils.isEmpty(result[1]) ? "服务器开小差了，请待会重试" : result[1]);
            } else {
                T.showShort(CompleteUserInfoActivity.this, "头像更新成功");
                userPhotoPath = result[1];
                PreferencesUtils.putString(CompleteUserInfoActivity.this, MLProperties.BUNDLE_KEY_TEACHER_IMG, userPhotoPath);
                if (!StringUtils.isEmpty(userPhotoPath)) {
                    imgUserPhoto.setImageURI(Uri.parse(userPhotoPath));
                }
            }
        }
    }
}
