package com.bj.eduteacher.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.entity.ZiyuanInfo;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.view.ResPreWebView;
import com.bj.eduteacher.zzokhttp.OkHttpUtils;
import com.bj.eduteacher.zzokhttp.callback.FileCallBack;
import com.jaeger.library.StatusBarUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.HttpUtilService.DOWNLOAD_PATH;
import static com.bj.eduteacher.api.Urls.ZIYUANINFO;

/**
 * Created by zz379 on 20/07/2017.
 * 资源预览页面，主要是文档类型资源
 */
@SuppressLint("SetJavaScriptEnabled")
public class ResReviewActivity extends BaseActivity {

    private static final String ARTICLE_AGREE_TYPE_YES = "add";
    private static final String ARTICLE_AGREE_TYPE_NO = "del";
    private static final String ARTICLE_AGREE_TYPE_SEARCH = "status";

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
    ResPreWebView webView;
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
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;

    private String resID;
    private String resName;
    private String previewUrl;
    private String downloadUrl;
    private String fileName;

    private LmsDataService service;
    private String teacherPhoneNumber;

    private PopupWindow popShare;
    private WebSettings setting;

    // 2.创建一个检测器
    GestureDetector detector;
    private String unionid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_review);
        ButterKnife.bind(this);
        service = new LmsDataService();
        detector = new GestureDetector(this, gestureListener);

        initToolBar();
        initView();
        initData();
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        checkDianzanStatus();
    }

    private void checkDianzanStatus() {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {

                OkGo.<String>post(BASE_URL+"index.php/ziyuan/dianzan")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("ziyuanid", resID)
                        .params("unionid", unionid)
                        .params("userphone", teacherPhoneNumber)
                        .params("caozuo", "3")

                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("检查点赞状态", str);
                                BaseDataInfo info = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });

                                e.onNext(info);
                                e.onComplete();
                            }
                        });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo dataInfo) {

                        if (dataInfo.getRet().equals("3")) {
                            if (dataInfo.getData().equals("1")) {//已点赞
                                ivAgree.setImageResource(R.mipmap.ic_liked);
                            }if (dataInfo.getData().equals("0")){
                                ivAgree.setImageResource(R.mipmap.ic_like);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
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

        if(getIntent().getStringExtra("type")!=null){
            shanghcuanjindu();
        }
    }

    @Override
    protected void initView() {
        llBottomBar.setVisibility(View.VISIBLE);
        webView.setClickable(false);
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
                // showLoadingDialog();
            }

        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.setVerticalScrollbarOverlay(true); //指定的垂直滚动条有叠加样式
        // 启用支持JavaScript
        setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setLoadsImagesAutomatically(true);//支持自动加载图片
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setAppCacheEnabled(false);
        setting.setDatabaseEnabled(true);
        setting.setDomStorageEnabled(true); //设置DOM Storage缓存

        if (downloadUrl.endsWith("ppt") || downloadUrl.endsWith("pptx")
                || downloadUrl.endsWith("PPT") || downloadUrl.endsWith("PPTX")) {
            setting.setUseWideViewPort(true);   //设定支持viewport
            setting.setLoadWithOverviewMode(true);
        } else {
            webView.setInitialScale(150);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        setting.setBuiltInZoomControls(true);
        setting.setDisplayZoomControls(false);// 显示放大缩小按钮
        setting.setSupportZoom(true);   //设定支持缩放

        webView.loadUrl(previewUrl);

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void initData() {
        if (!NetUtils.isConnected(this)) {
            T.showShort(this, "无法连接到网络，请检查您的网络设置");
            hideLoadingDialog();
            return;
        }
        // 增加阅读数量
        getResPreviewNumber();
    }

    @OnClick(R.id.ll_commentNumber)
    void clickComment() {
        // 点赞评论需要登录
//        if (LoginStatusUtil.noLogin(this)) {
//            IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
//            return;
//        }

        Intent intent = new Intent(this, ResCommentActivity.class);
        intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, resID);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 300) {
            if (requestCode == 1) {
                Log.e("评论返回", "true");
                getResNumbers();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        MobclickAgent.onPageStart("resource_preview");
        MobclickAgent.onResume(this);
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

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
//        if (isLandscape) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            return;
//        }
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        webView.destroy();
        webView = null;
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
                Log.e("aa","aaa");
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
            Log.e("way", "文件已存在，不需要重新下载");
            sendFileToEmail(oldFile);
            // sendEmail();
            Log.e("aa","aaa11");
        } else {
            Log.e("aa",downloadUrl);
            showLoadingDialog();
            OkHttpUtils.get().url(downloadUrl).tag(fileName).build()
                    .execute(new FileCallBack(DOWNLOAD_PATH, fileName) {

                        @Override
                        public void onError(Call call, Exception e) {

                            hideLoadingDialog();
                            Log.e("aa","fail");
                        }

                        @Override
                        public void onResponse(File response) {
                            Log.e("aa","aaa33");
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

    //获得阅读量
    private void getResPreviewNumber() {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                String[] result = service.addResourcePreviewNumber(resID);
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
                        // 获取点赞数量和评论数量
                        getResNumbers();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取点赞数量和评论数量
    private void getResNumbers() {
        Observable.create(new ObservableOnSubscribe<ArticleInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArticleInfo> e) throws Exception {
                ArticleInfo dataInfo = service.getResInfoByID(resID);
                e.onNext(dataInfo);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ArticleInfo articleInfo) {
                        hideLoadingDialog();
                        tvAgreeNumber.setText(articleInfo.getAgreeNumber());
                        tvCommentNumber.setText(articleInfo.getCommentNumber());
                        tvReadNumber.setText(articleInfo.getReadNumber() + "次阅读");

                        tvTitle.setText(articleInfo.getTitle());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        hideLoadingDialog();
                        llBottomBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @OnClick(R.id.ll_agreeNumber)
    void clickAgree() {
        MobclickAgent.onEvent(this, "article_like");
        // 点赞评论需要登录
        if (LoginStatusUtil.noLogin(this)) {
            IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
            return;
        } else {
            dianzanchaxun();
        }

    }

    private void dianzanchaxun() {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL+"index.php/ziyuan/dianzan")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("ziyuanid", resID)
                        .params("unionid", unionid)
                        .params("userphone", teacherPhoneNumber)
                        .params("caozuo", "3")

                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("检查点赞状态", str);
                                BaseDataInfo info = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });

                                e.onNext(info);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo dataInfo) {

                        if (dataInfo.getRet().equals("3")) {
                            if (dataInfo.getData().equals("1")) {//已点赞
                                dianzan("del");
                            }if (dataInfo.getData().equals("0")){
                                dianzan("add");
                            }
                        }

//                        if(dataInfo.getRet().equals("4")){//已点赞
//                            dianzan("2");
//                        }if(dataInfo.getRet().equals("5")){
//                            dianzan("1");
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void dianzan(final String s) {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL+"index.php/ziyuan/dianzan")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("ziyuanid", resID)
                        .params("unionid", unionid)
                        .params("userphone", teacherPhoneNumber)
                        .params("caozuo", s)

                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("点赞结果", str);
                                BaseDataInfo info = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });

                                e.onNext(info);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo dataInfo) {
                       checkDianzanStatus();

                       getResNumbers();

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private boolean isLandscape = false;    // 是否横屏

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // T.showShort(this, "横屏");
            isLandscape = true;
            screenWidth = ScreenUtils.getScreenWidth(this);
            screenHeight = ScreenUtils.getScreenHeight(this);
            hideHeaderAndBottom();
            ScreenUtils.setFullScreen(this);    // 进入全屏
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.transparent), 0);
            landscapeShowDoc();
            // 修改RelativeLayout中的位置
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) webView.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW, 0);
            lp.addRule(RelativeLayout.ABOVE, 0);
            webView.setLayoutParams(lp);
        } else {
            // T.showShort(this, "竖屏");
            isLandscape = false;
            screenWidth = ScreenUtils.getScreenWidth(this);
            screenHeight = ScreenUtils.getScreenHeight(this);
            showHeaderAndBottom();
            ScreenUtils.quitFullScreen(this);   // 退出全屏
            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary), 0);
            portraitShowDoc();
            // 修改RelativeLayout中的位置
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) webView.getLayoutParams();
            lp.addRule(RelativeLayout.BELOW, toolbar.getId());
            lp.addRule(RelativeLayout.ABOVE, llBottomBar.getId());
            webView.setLayoutParams(lp);
        }
    }

    private boolean isShowHeaderAndBottom;
    private int screenWidth, screenHeight;

    /**
     * 隐藏Toolbar和bottombar
     */
    private void hideHeaderAndBottom() {
        toolbar.setVisibility(View.GONE);
        llBottomBar.setVisibility(View.GONE);
        isShowHeaderAndBottom = false;
    }

    /**
     * 延迟隐藏
     */
    private void delayHide() {
        toolbar.setVisibility(View.VISIBLE);
        llBottomBar.setVisibility(View.VISIBLE);
        isShowHeaderAndBottom = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLandscape) {
                    toolbar.setVisibility(View.GONE);
                    llBottomBar.setVisibility(View.GONE);
                    isShowHeaderAndBottom = false;
                }
            }
        }, 2000);
    }

    /**
     * 显示全屏状态下的Toolbar和bottombar
     */
    private void showHeaderAndBottom() {
        toolbar.setVisibility(View.VISIBLE);
        llBottomBar.setVisibility(View.VISIBLE);
        isShowHeaderAndBottom = true;
    }

    private void portraitShowDoc() {
        if (downloadUrl.endsWith("ppt") || downloadUrl.endsWith("pptx")
                || downloadUrl.endsWith("PPT") || downloadUrl.endsWith("PPTX")) {
            return;
        } else {
            webView.setInitialScale(150);
        }
    }

    private void landscapeShowDoc() {
        if (downloadUrl.endsWith("ppt") || downloadUrl.endsWith("pptx")
                || downloadUrl.endsWith("PPT") || downloadUrl.endsWith("PPTX")) {
            return;
        } else {
            webView.setInitialScale(250);
        }
    }

    GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            int rawY = (int) e.getRawY();
            if (rawY >= screenHeight / 8 * 3 && rawY <= screenHeight / 8 * 5 && !isShowHeaderAndBottom) {
                delayHide();
            }
            return super.onSingleTapConfirmed(e);
        }
    };


    private void shanghcuanjindu() {
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<BaseDataInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/setkcresjindu")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("resid", resID)
                        .params("kechengid", "")
                        .params("restype", "1")
                        .params("endtime", "0")
                        .params("unionid", unionid)
                        .params("phone", teacherPhoneNumber)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                BaseDataInfo baseDataInfo = JSON.parseObject(str, new TypeReference<BaseDataInfo>() {
                                });
                                Log.e("上传进度结果", str);

                                e.onNext(baseDataInfo);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo dataInfo) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
