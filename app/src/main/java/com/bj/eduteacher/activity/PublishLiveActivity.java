package com.bj.eduteacher.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.HttpUtilService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.model.CurLiveInfo;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.presenter.UploadHelper;
import com.bj.eduteacher.presenter.viewinface.UploadView;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.tool.SxbLog;
import com.bj.eduteacher.tool.UIUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.widget.LineControllerView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by zz379 on 2017/9/5.
 * 发布直播类
 */

public class PublishLiveActivity extends BaseActivity implements View.OnClickListener, UploadView {

    private static final String TAG = PublishLiveActivity.class.getSimpleName();

    private UploadHelper mPublishLivePresenter;

    private TextView BtnBack, BtnPublish;
    private LinearLayout llBack;
    private Dialog mPicChsDialog;
    private Dialog mRoleChsDialog;
    private SimpleDraweeView cover;
    private Uri fileUri, cropUri;
    private TextView tvPicTip;
    private EditText edtTitle;
    private LineControllerView lcvRole;

    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    private static final int CROP_CHOOSE = 10;
    private boolean bUploading = false;
    private boolean bPermission = false;
    private int uploadPercent = 0;
    private ImageView ivAdd;
    private String sxbTitle;
    private String sxbPicture;
    private String selectPicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_publish);
        mPublishLivePresenter = new UploadHelper(this, this);
        initToolBar();
        initView();

        initPhotoDialog();
        initRolesDialog();

        // 提前更新sig -- 上传图片用
        // mPublishLivePresenter.updateSig();

        checkPublishPermission();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
    }

    @Override
    protected void initView() {
        sxbTitle = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_SXB_Title, "");
        sxbPicture = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_SXB_Picture, "");

        edtTitle = (EditText) findViewById(R.id.live_title);
        BtnBack = (TextView) findViewById(R.id.btn_cancel);
        tvPicTip = (TextView) findViewById(R.id.tv_pic_tip);
        ivAdd = (ImageView) findViewById(R.id.iv_add);
        BtnPublish = (TextView) findViewById(R.id.btn_publish);
        cover = (SimpleDraweeView) findViewById(R.id.cover);
        lcvRole = (LineControllerView) findViewById(R.id.lcv_role);
        llBack = (LinearLayout) findViewById(R.id.header_ll_left);
        cover.setOnClickListener(this);
        BtnBack.setOnClickListener(this);
        BtnPublish.setOnClickListener(this);
        lcvRole.setOnClickListener(this);
        llBack.setOnClickListener(this);
        // 获取上次的设置
        lcvRole.setContent(getRoleShow(CurLiveInfo.getCurRole()));

        if (!StringUtils.isEmpty(sxbTitle)) {
            edtTitle.setText(sxbTitle);
        }
        if (!StringUtils.isEmpty(sxbPicture)) {
            // 设置图片
            loadImageToCover(sxbPicture);
        }
    }

    @Override
    protected void onDestroy() {
        mPublishLivePresenter.onDestory();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel || i == R.id.header_ll_left) {
            finish();
        } else if (i == R.id.btn_publish) {
            publishLive();
        } else if (i == R.id.cover) {
            if (mPicChsDialog != null && !mPicChsDialog.isShowing()) mPicChsDialog.show();
        } else if (i == R.id.lcv_role) {
            if (mRoleChsDialog != null && !mRoleChsDialog.isShowing()) mRoleChsDialog.show();
        }
    }

    private void publishLive() {
        String title = edtTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            T.showShort(this, "请输入直播标题");
            return;
        }
        if (StringUtils.isEmpty(sxbPicture) && StringUtils.isEmpty(selectPicPath)) {
            T.showShort(this, "直播封面不能为空");
            return;
        }
        if (bUploading) {
            Toast.makeText(this, getString(R.string.publish_wait_uploading), Toast.LENGTH_SHORT).show();
            return;
        }

        // 跳转到直播的页面
//        Intent intent = new Intent(this, LiveActivity.class);
//        MySelfInfo.getInstance().setIdStatus(Constants.HOST);
//        MySelfInfo.getInstance().setJoinRoomWay(true);
//        CurLiveInfo.setTitle(title);
//        CurLiveInfo.setHostID(MySelfInfo.getInstance().getId());
//        CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
//        if (StringUtils.isEmpty(selectPicPath)) {
//            CurLiveInfo.setCoverurl(sxbPicture.substring(sxbPicture.lastIndexOf("/") + 1));
//        }
//        startActivity(intent);
//        SxbLog.i(TAG, "PerformanceTest  publish Live " + SxbLog.getTime());
//        Log.i("way", MySelfInfo.getInstance().toString());
//        this.finish();
    }

    /**
     * 图片选择对话框
     */
    private void initPhotoDialog() {
        mPicChsDialog = new Dialog(this, R.style.floag_dialog);
        mPicChsDialog.setContentView(R.layout.pic_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = mPicChsDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int) (display.getWidth()); //设置宽度

        mPicChsDialog.getWindow().setAttributes(lp);

        TextView camera = (TextView) mPicChsDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) mPicChsDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) mPicChsDialog.findViewById(R.id.btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                mPicChsDialog.dismiss();
            }
        });
        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicFrom(IMAGE_STORE);
                mPicChsDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicChsDialog.dismiss();
            }
        });
    }

    private void initRolesDialog() {
        final String[] values = new String[]{Constants.HD_ROLE, Constants.SD_ROLE, Constants.LD_ROLE};

        mRoleChsDialog = new Dialog(this, R.style.floag_dialog);
        mRoleChsDialog.setContentView(R.layout.role_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = mRoleChsDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int) (display.getWidth()); //设置宽度

        mRoleChsDialog.getWindow().setAttributes(lp);

        TextView tvHD = (TextView) mRoleChsDialog.findViewById(R.id.tvHD);
        TextView tvSD = (TextView) mRoleChsDialog.findViewById(R.id.tvSD);
        TextView tvLD = (TextView) mRoleChsDialog.findViewById(R.id.tvLD);
        TextView cancel = (TextView) mRoleChsDialog.findViewById(R.id.btn_cancel);

        tvHD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SxbLog.d(TAG, "initRoleDialog->onClick item:" + 0);
                CurLiveInfo.setCurRole(values[0]);
                lcvRole.setContent(getRoleShow(CurLiveInfo.getCurRole()));
                mRoleChsDialog.dismiss();
            }
        });
        tvSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SxbLog.d(TAG, "initRoleDialog->onClick item:" + 1);
                CurLiveInfo.setCurRole(values[1]);
                lcvRole.setContent(getRoleShow(CurLiveInfo.getCurRole()));
                mRoleChsDialog.dismiss();
            }
        });
        tvLD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SxbLog.d(TAG, "initRoleDialog->onClick item:" + 2);
                CurLiveInfo.setCurRole(values[2]);
                lcvRole.setContent(getRoleShow(CurLiveInfo.getCurRole()));
                mRoleChsDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRoleChsDialog.dismiss();
            }
        });
    }

    private String getRoleShow(String role) {
        if (role.equals(Constants.HD_ROLE)) {
            return getString(R.string.str_dt_hd);
        } else if (role.equals(Constants.SD_ROLE)) {
            return getString(R.string.str_dt_sd);
        } else {
            return getString(R.string.str_dt_ld);
        }
    }

    /**
     * 获取图片资源
     *
     * @param type
     */
    private void getPicFrom(int type) {
        if (!bPermission) {
            Toast.makeText(this, getString(R.string.tip_no_permission), Toast.LENGTH_SHORT).show();
            return;
        }

        switch (type) {
            case CAPTURE_IMAGE_CAMERA:
                fileUri = createCoverUri("", false);
                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            case IMAGE_STORE:
                fileUri = createCoverUri("_select", false);
                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
                intent_album.setType("image/*");
                startActivityForResult(intent_album, IMAGE_STORE);
                break;

        }
    }

    private void checkPublishPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Boolean success) {
                        bPermission = success;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Uri createCoverUri(String type, boolean bCrop) {
        String filename = MySelfInfo.getInstance().getId() + System.currentTimeMillis() + type + ".jpg";
        File outputImage = new File(Environment.getExternalStorageDirectory(), filename);
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bCrop) {
            return Uri.fromFile(outputImage);
        } else {
            return UIUtils.getUriFromFile(this, outputImage);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAPTURE_IMAGE_CAMERA:
                    startPhotoZoom(fileUri);
                    break;
                case IMAGE_STORE:
                    String path = UIUtils.getPath(this, data.getData());
                    if (null != path) {
                        SxbLog.d(TAG, "startPhotoZoom->path:" + path);
                        File file = new File(path);
                        startPhotoZoom(UIUtils.getUriFromFile(this, file));
                    }
                    break;
                case CROP_CHOOSE:
                    // tvPicTip.setVisibility(View.GONE);
                    // ivAdd.setVisibility(View.GONE);

                    LL.i("图片地址：" + cropUri.getPath());
                    // cover.setImageBitmap(null);
                    // cover.setImageURI(cropUri);
                    bUploading = true;
                    // mPublishLivePresenter.uploadCover(cropUri.getPath());
                    mPublishLivePresenter.uploadPicture(cropUri.getPath());
                    break;
            }
        }

    }

    public void startPhotoZoom(Uri uri) {
        cropUri = createCoverUri("_crop", true);

        Intent intent = new Intent("com.android.camera.action.CROP");
        /* 这句要记得写：这是申请权限，之前因为没有添加这个，打开裁剪页面时，一直提示“无法修改低于50*50像素的图片”，
      开始还以为是图片的问题呢，结果发现是因为没有添加FLAG_GRANT_READ_URI_PERMISSION。
      如果关联了源码，点开FileProvider的getUriForFile()看看（下面有），注释就写着需要添加权限。
      */
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 360);
        intent.putExtra("aspectY", 240);
        intent.putExtra("outputX", 360);
        intent.putExtra("outputY", 240);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_CHOOSE);
    }

    private void loadImageToCover(String url) {
        LL.i("图片地址：" + url);
        tvPicTip.setVisibility(View.GONE);
        ivAdd.setVisibility(View.GONE);
        cover.setImageURI(url);
    }

    @Override
    public void onUploadResult(int code, String url) {
        if (1 == code) {
            selectPicPath = url;
            CurLiveInfo.setCoverurl(url);
            // 设置封面
            loadImageToCover(HttpUtilService.BASE_RESOURCE_URL + url);
            Toast.makeText(this, getString(R.string.publish_upload_success), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.publish_upload_cover_failed) + "|" + code + "|" + url, Toast.LENGTH_SHORT).show();
        }
        bUploading = false;
    }

    @Override
    public void onUploadProcess(int percent) {

    }
}
