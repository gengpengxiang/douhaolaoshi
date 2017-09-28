package com.bj.eduteacher.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.zzokhttp.OkHttpUtils;
import com.bj.eduteacher.zzokhttp.callback.FileCallBack;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

import static com.bj.eduteacher.api.HttpUtilService.DOWNLOAD_PATH;

/**
 * Created by zz379 on 20/07/2017.
 */

public class ResReviewActivity extends BaseActivity {

    @BindView(R.id.header_img_back)
    ImageView ivBack;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.header_ll_left)
    LinearLayout llHeaderLeft;
    @BindView(R.id.header_ll_right)
    LinearLayout llHeaderRight;
    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.web_content)
    WebView webView;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.tv_readNumber)
    TextView tvReadNumber;
    @BindView(R.id.tv_commentNumber)
    TextView tvCommentNumber;
    @BindView(R.id.tv_agreeNumber)
    TextView tvAgreeNumber;
    @BindView(R.id.iv_agree)
    ImageView ivAgree;
    @BindView(R.id.ll_bottomBar)
    LinearLayout llBottomBar;

    private String resID;
    private String resName;
    private String previewUrl;
    private String downloadUrl;
    private String fileName;

    private PopupWindow popShare;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_review);
        ButterKnife.bind(this);

        initToolbar();
        initView();
        initData();

        test();
    }

    private void test() {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                llBottomBar.setVisibility(View.VISIBLE);
                tvReadNumber.setText("12" + "次阅读");
                tvAgreeNumber.setText("20");
                tvCommentNumber.setText("43");
            }
        }, 2000);
    }

    private void initToolbar() {
        resID = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID);
        resName = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME);
        previewUrl = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL);
        downloadUrl = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL);
        fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
        LL.i("resID: " + resID + "\npreviewUrl: " + previewUrl + "\ndownloadUrl: " + downloadUrl);

        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(resName);
        ivBack.setVisibility(View.VISIBLE);
        llHeaderRight.setVisibility(View.VISIBLE);
        tvShare.setVisibility(View.VISIBLE);
        tvShare.setText("发送");
        initPopViewShare();
    }

    private void initView() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                hideLoadingDialog();
                super.onPageFinished(view, url);
                // 页面没有加载完成之前，禁止分享功能
                llHeaderRight.setEnabled(true);
                LL.i("webView ---->>>> onPageFinished ---->>>> title:" + view.getTitle());
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                llHeaderRight.setEnabled(false);
                showLoadingDialog();
            }

        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.setVerticalScrollbarOverlay(true); //指定的垂直滚动条有叠加样式
        // 启用支持JavaScript
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        setting.setUseWideViewPort(true);   //设定支持viewport
        setting.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

//        if (downloadUrl.endsWith(".ppt") || previewUrl.endsWith(".pptx")) {
//            setting.setBuiltInZoomControls(false);
//            setting.setDisplayZoomControls(false);// 显示放大缩小按钮
//            setting.setSupportZoom(false);   //设定支持缩放
//        } else {
//            setting.setBuiltInZoomControls(true);
//            setting.setDisplayZoomControls(false);// 显示放大缩小按钮
//            setting.setSupportZoom(true);   //设定支持缩放
//            webView.setInitialScale(190);
//        }
        setting.setBuiltInZoomControls(false);
        setting.setDisplayZoomControls(false);// 显示放大缩小按钮
        setting.setSupportZoom(false);   //设定支持缩放

        webView.loadUrl(previewUrl);
    }

    private void initData() {
        // 增加阅读数量
        getResPreviewNumber();
    }

    @OnClick(R.id.ll_agreeNumber)
    void clickAgree() {
        T.showShort(this, "点赞！");
    }

    @OnClick(R.id.ll_commentNumber)
    void clickComment() {
        Intent intent = new Intent(this, ResCommentActivity.class);
        intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, "0");
        startActivity(intent);
    }

    private void getResPreviewNumber() {
        if (!NetUtils.isConnected(this)) {
            T.showShort(this, "无法连接到网络，请检查您的网络设置");
            hideLoadingDialog();
            return;
        }
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                LmsDataService service = new LmsDataService();
                service.addResourcePreviewNumber(resID);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        MobclickAgent.onPageStart("resource_preview");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
        MobclickAgent.onPageEnd("resource_preview");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.header_ll_left)
    void onClickBack() {
        onBackPressed();
    }

    @OnClick(R.id.header_ll_right)
    void onClickShare() {
        showPopViewShare();
    }

    private void initPopViewShare() {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_send_res, null);
        popShare = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popShare.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        popShare.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popShare.setFocusable(true);
        popShare.setOutsideTouchable(true);
        popShare.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(ResReviewActivity.this, 1f);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llHeaderLeft.setEnabled(true);
                        llHeaderRight.setEnabled(true);
                    }
                }, 100);
            }
        });
        TextView tvSendEmail = (TextView) popView.findViewById(R.id.tv_sendEmail);
        tvSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopViewShare();
                // 开始下载，然后发送到邮箱
                startSendOffice();
            }
        });
        TextView tvSendCancel = (TextView) popView.findViewById(R.id.tv_cancel);
        tvSendCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePopViewShare();
            }
        });
    }

    private void showPopViewShare() {
        if (popShare != null && !popShare.isShowing()) {
            setBackgroundAlpha(this, 0.5f);
            popShare.showAtLocation(tvTitle, Gravity.BOTTOM, 0, popShare.getHeight());
        }
    }

    private void hidePopViewShare() {
        if (popShare != null && popShare.isShowing()) {
            popShare.dismiss();
        }
    }

    /**
     * 设置背景透明度
     *
     * @param activity
     * @param bgAlpha
     */
    public void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 开始发送
     */
    private void startSendOffice() {
        if (!NetUtils.isConnected(this)) {
            T.showShort(this, "无法连接到网络，请检查您的网络设置");
            hideLoadingDialog();
            return;
        }

        File oldFile = new File(DOWNLOAD_PATH, fileName);
        if (oldFile.exists()) {
            LL.i("way", "文件已存在，不需要重新下载");
            sendFileToEmail(oldFile);
            // sendEmail();
        } else {
            showLoadingDialog();
            OkHttpUtils.get().url(downloadUrl).tag(fileName).build()
                    .execute(new FileCallBack(DOWNLOAD_PATH, fileName) {

                        @Override
                        public void onError(Call call, Exception e) {
                            hideLoadingDialog();
                        }

                        @Override
                        public void onResponse(File response) {
                            hideLoadingDialog();
                            LL.i("下载完成：" + response.length() / 1024 + "kb");
                            sendFileToEmail(response);
                            // sendEmail();
                        }

                        @Override
                        public void inProgress(float progress, long total) {

                        }
                    });
        }
    }

    private void sendFileToEmail(File file) {
        MobclickAgent.onEvent(ResReviewActivity.this, "send_doc_email");
        // 调用系统的邮件软件来发送 发送到邮箱
        Intent email = new Intent(Intent.ACTION_SEND);
        //邮件发送类型：带附件的邮件
        email.setType("application/octet-stream");
        //设置邮件地址
        // email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
        //设置邮件标题
        email.putExtra(Intent.EXTRA_SUBJECT, resName);
        //设置发送的内容
        // email.putExtra(android.content.Intent.EXTRA_TEXT, emailContent);
        //附件
        email.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        //调用系统的邮件系统
        startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
    }

    private void sendEmail() {
        Uri uri = Uri.parse("mailto:1104197791@qq.com");
        String[] email = {};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        intent.putExtra(Intent.EXTRA_SUBJECT, ""); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, ""); // 正文
        startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }
}
