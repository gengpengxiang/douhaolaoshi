package com.bj.eduteacher.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.BitmapCallBack;
import com.bj.eduteacher.glide.ProgressTarget;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.utils.Util;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

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

/**
 * 加载HTML页面
 * 逗课文章详情页面
 */
@SuppressLint("SetJavaScriptEnabled")
public class DoukeDetailActivity extends BaseActivity {

    private static final String ARTICLE_AGREE_TYPE_YES = "1";
    private static final String ARTICLE_AGREE_TYPE_NO = "2";
    private static final String ARTICLE_AGREE_TYPE_SEARCH = "3";

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
    WebView web_content;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.ll_errorContent)
    LinearLayout llErrorContent;
    @BindView(R.id.tv_readNumber)
    TextView tvReadNumber;
    @BindView(R.id.tv_agreeNumber)
    TextView tvAgreeNumber;
    @BindView(R.id.iv_agree)
    ImageView ivAgree;
    @BindView(R.id.ll_bottomBar)
    LinearLayout llBottomBar;
    @BindView(R.id.tv_commentNumber)
    TextView tvCommentNumber;

    private WebSettings setting;
    private String contentUrl;
    private String newsID;
    private String userPhoneNumber;
    private PopupWindow popShare;
    private IWXAPI api;
    private long clickMillis = 0;
    private String cacheFirstLoadImageUrl;
    private String msgDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douke_detail);
        ButterKnife.bind(this);
        api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID);
        userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        newsID = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_DOUKE_ID);
        contentUrl = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_DOUKE_URL);
        LL.i("文章地址：" + contentUrl + "\n文章ID：" + newsID);

        initToolBar();
        initView();
        loadContent();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        tvTitle.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        llHeaderRight.setVisibility(View.VISIBLE);
        tvShare.setVisibility(View.VISIBLE);
        initPopViewShare();
    }

    /**
     * 初始化WebView
     */
    @Override
    protected void initView() {
        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        // 主要处理解析，渲染网页等浏览器做的事情 WebViewClient就是帮助WebView处理各种通知、请求事件的。
        web_content.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 页面没有加载完成之前，禁止分享功能
                llHeaderRight.setEnabled(true);
                LL.i("webView ---->>>> onPageFinished ---->>>> title:" + view.getTitle());
                tvTitle.setText(String.valueOf(view.getTitle()));
                //页面加载完毕后，判断是否为微信的公众平台文章
                if (url.startsWith("http://mp.weixin.qq.com") || url.startsWith("https://mp.weixin.qq.com")) {
                    //读取分享的信息，将数据以json的格式返回,方便日后扩展
                    view.loadUrl("javascript:window.codeboy.callme(JSON.stringify({" +
                            "\"msg_title\":msg_title.toString()," +
                            "\"msg_desc\":msg_desc.toString()," +
                            "\"msg_link\":msg_link.toString()," +
                            "\"msg_cdn_url\":msg_cdn_url.toString()" +
                            "}))");
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                LL.i("webView ---->>>> onReceivedError");
                llErrorContent.setVisibility(View.VISIBLE);
                web_content.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                LL.i("webView ---->>>> onReceivedHttpError");
                // llErrorContent.setVisibility(View.VISIBLE);
                // web_content.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
//                LL.i("webView ---->>>> onReceivedSslError");
//                llErrorContent.setVisibility(View.VISIBLE);
//                web_content.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                cacheFirstLoadImageUrl = null;
                llHeaderRight.setEnabled(false);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                //读取加载中的资源里的图片
//                if (cacheFirstLoadImageUrl == null && !TextUtils.isEmpty(url)) {
//                    if (url.contains(".png") || url.contains(".jpg") || url.contains(".jpeg")) {
//                        cacheFirstLoadImageUrl = url;
//                    }
//                }
            }
        });
        // setWebChromeClient：辅助WebView处理Javascript的对话框，网站图标，网站title，加载进度等
        web_content.setWebChromeClient(new WebChromeClient() {
            // 网页加载进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress == 100) {
                    // 页面加载完成
                    progressBar.setVisibility(View.GONE);
                } else {
                    // 页面加载中
                    if (progressBar.getVisibility() != View.VISIBLE) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
            }

        });

        // 启用支持JavaScript
        setting = web_content.getSettings();
        setting.setJavaScriptEnabled(true);
        //自适应屏幕
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

//        setting.setDomStorageEnabled(true);
//        setting.setPluginState(WebSettings.PluginState.ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        // 优先使用缓存
        setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setUseWideViewPort(true);   //设定支持viewport
        setting.setLoadWithOverviewMode(true);
        setting.setBuiltInZoomControls(false);
        setting.setDisplayZoomControls(false);// 显示放大缩小按钮
        setting.setSupportZoom(false);   //设定支持缩放
        //添加一个允许javascript访问方法，如 window.codeboy.callme("hello codeboy");
        web_content.addJavascriptInterface(new CodeBoyJsInterface(), "codeboy");
    }

    /**
     * 实现Parcelable接口，主要是避免因为混淆而导致方法名变更
     */
    @SuppressLint({"JavascriptInterface", "ParcelCreator"})
    public class CodeBoyJsInterface implements Parcelable {

        public CodeBoyJsInterface() {
        }

        //解决throws Uncaught Error: Error calling method on NPObject on Android
        @JavascriptInterface
        public void callme(final String str) {
            //这里是javascript里回调的，注意回调是通过非UI线程
            try {
                JSONObject json = new JSONObject(str);
                String msg_title = getJsonStr(json, "msg_title");
                String msg_desc = getJsonStr(json, "msg_desc");
                String msg_link = getJsonStr(json, "msg_link");
                String msg_cdn_url = getJsonStr(json, "msg_cdn_url");

                msgDesc = msg_desc;
                cacheFirstLoadImageUrl = msg_cdn_url;

                Log.d("way", "msg_title:" + msg_title + " msg_desc:" + msg_desc
                        + " msg_link:" + msg_link + " msg_cdn_url:" + msg_cdn_url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String getJsonStr(JSONObject json, String key) {
            try {
                String value = json.getString(key);
                //对数据进行转义
                return Html.fromHtml(value).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int describeContents() {
            //ignore
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            //ignore
        }
    }

    /**
     * 加载内容
     */
    private void loadContent() {
        if (NetUtils.isConnected(this)) {
            llErrorContent.setVisibility(View.GONE);
            web_content.setVisibility(View.VISIBLE);
            web_content.loadUrl(contentUrl);
            getArticleReadNumber();
            getArticleAgreeNumber(ARTICLE_AGREE_TYPE_SEARCH);
            getArticleCommentNumber();
        } else {
            web_content.setVisibility(View.GONE);
            llErrorContent.setVisibility(View.VISIBLE);
            if (System.currentTimeMillis() - clickMillis > 3000) {
                T.showShort(this, "请检查您的网络连接是否正常");
                clickMillis = System.currentTimeMillis();
            }
        }

    }

    @Override
    protected void onResume() {
        web_content.onResume();
        super.onResume();
        MobclickAgent.onPageStart("article");
        MobclickAgent.onResume(this);
        userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        getArticleAgreeNumber(ARTICLE_AGREE_TYPE_SEARCH);
        getArticleCommentNumber();
    }

    @Override
    protected void onPause() {
        web_content.onPause();
        super.onPause();
        MobclickAgent.onPageEnd("article");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        web_content.destroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 后退事件的处理
     */
    public void onBack() {
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }
        if (web_content.canGoBack()) {
            web_content.goBack();
        } else {
            clickBack();
        }
    }

    @OnClick(R.id.header_ll_left)
    void clickBack() {
        if (MainActivity.getInstance() == null) {
            Intent intent = new Intent(DoukeDetailActivity.this, MainActivity.class);
            startActivity(intent);
            this.finish();
            overridePendingTransition(R.anim.left_right_in, R.anim.left_right_out);
        } else {
            this.finish();
            overridePendingTransition(R.anim.left_right_in, R.anim.left_right_out);
        }
    }

    private boolean isLandscape = false;    // 是否横屏

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            T.showShort(this, "横屏");
            // toolbar.setVisibility(View.GONE);
            isLandscape = true;
            ScreenUtils.setFullScreen(this);    // 进入全屏
            setting.setBuiltInZoomControls(false);     // 取消缩放
        } else {
//            T.showShort(this, "竖屏");
            // toolbar.setVisibility(View.VISIBLE);
            isLandscape = false;
            ScreenUtils.quitFullScreen(this);   // 退出全屏
            setting.setBuiltInZoomControls(true);     // 打开缩放
        }
    }

    @OnClick(R.id.ll_errorContent)
    void clickRefreshView() {
        // 重新加载页面
        loadContent();
    }

    boolean isAgree = false;

    @OnClick(R.id.ll_agreeNumber)
    void clickAgree() {
        // 点赞评论需要登录
        if (StringUtils.isEmpty(userPhoneNumber)) {
            IntentManager.toLoginActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
            return;
        }

        MobclickAgent.onEvent(this, "article_like");
        if (isAgree) {
            getArticleAgreeNumber(ARTICLE_AGREE_TYPE_NO);
        } else {
            getArticleAgreeNumber(ARTICLE_AGREE_TYPE_YES);
        }
    }

    @OnClick(R.id.ll_commentNumber)
    void clickComment() {
        // 点赞评论需要登录
        if (StringUtils.isEmpty(userPhoneNumber)) {
            IntentManager.toLoginActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
            return;
        }

        Intent intent = new Intent(DoukeDetailActivity.this, DoukeCommentActivity.class);
        intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, newsID);
        startActivity(intent);
    }

    private void getArticleCommentNumber() {
        Observable.create(new ObservableOnSubscribe<ArticleInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArticleInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                ArticleInfo dataInfo = mService.getArticleInfoByID(newsID);
                e.onNext(dataInfo);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArticleInfo articleInfo) {
                        String commentNumber = articleInfo.getCommentNumber();
                        tvCommentNumber.setText(StringUtils.isEmpty(commentNumber) ? "0" : commentNumber);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 文章单次浏览记录
     */
    private void getArticleReadNumber() {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BaseDataInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                BaseDataInfo dataInfo = mService.getArticleReadNumber(newsID, userPhoneNumber);
                e.onNext(dataInfo);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo info) {
                        if (!StringUtils.isEmpty(info.getRet()) && info.getRet().equals("1")) {
                            tvReadNumber.setText(info.getData() + "次阅读");
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

    /**
     * 为逗课内容点赞或取消点赞、或查询是否点过赞
     */
    private void getArticleAgreeNumber(final String type) {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BaseDataInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                BaseDataInfo dataInfo = mService.getArticleAgreeNumber(newsID, userPhoneNumber, type);
                e.onNext(dataInfo);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(BaseDataInfo info) {
                        if (info == null || StringUtils.isEmpty(info.getRet())) {
                            return;
                        }
                        if (type.equals(ARTICLE_AGREE_TYPE_SEARCH)) {
                            if (info.getRet().equals("4")) {
                                ivAgree.setImageResource(R.mipmap.ic_liked);
                                isAgree = true;
                            } else if (info.getRet().equals("5")) {
                                ivAgree.setImageResource(R.mipmap.ic_like);
                                isAgree = false;
                            }
                            tvAgreeNumber.setText(info.getData());
                            llBottomBar.setVisibility(View.VISIBLE);
                        } else {
                            if (info.getRet().equals("1")) {
                                ivAgree.setImageResource(R.mipmap.ic_liked);
                                tvAgreeNumber.setText(info.getData());
                                isAgree = true;
                            } else if (info.getRet().equals("3")) {
                                ivAgree.setImageResource(R.mipmap.ic_like);
                                tvAgreeNumber.setText(info.getData());
                                isAgree = false;
                            } else {

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

    @OnClick(R.id.header_ll_right)
    void onClickShare() {
        llHeaderRight.setEnabled(false);
        llHeaderLeft.setEnabled(false);
        showPopViewShare();
    }

    private void initPopViewShare() {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_share_report, null);
        popShare = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popShare.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        popShare.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popShare.setFocusable(false);
        popShare.setOutsideTouchable(true);
        popShare.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(DoukeDetailActivity.this, 1f);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llHeaderLeft.setEnabled(true);
                        llHeaderRight.setEnabled(true);
                    }
                }, 100);
            }
        });
        ImageView ivShareSession = (ImageView) popView.findViewById(R.id.iv_shareSession);
        ivShareSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isEmpty(cacheFirstLoadImageUrl)) {
                    getThumData(cacheFirstLoadImageUrl, new BitmapCallBack<Bitmap>() {
                        @Override
                        public void onBitmapLoadSuccess(Bitmap bitmap) {
                            shareToSession(bitmap);
                        }
                    });
                } else {
                    shareToSession(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                }
            }
        });
        ImageView ivShareTimeline = (ImageView) popView.findViewById(R.id.iv_shareTimeline);
        ivShareTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isEmpty(cacheFirstLoadImageUrl)) {
                    getThumData(cacheFirstLoadImageUrl, new BitmapCallBack<Bitmap>() {
                        @Override
                        public void onBitmapLoadSuccess(Bitmap bitmap) {
                            shareToTimeline(bitmap);
                        }
                    });
                } else {
                    shareToTimeline(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
                }
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
     * 分享到微信
     */
    public void shareToSession(Bitmap shareBmp) {
        MobclickAgent.onEvent(this, "article_share");
        if (!isWeixinAvilible(this)) {
            Toast.makeText(this, "抱歉！您还没有安装微信", Toast.LENGTH_SHORT).show();
            return;
        }
        if (popShare.isShowing()) {
            popShare.dismiss();

            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = contentUrl;

            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = web_content.getTitle();
            msg.description = msgDesc;
            // 设置缩略图
            if (shareBmp != null && !shareBmp.isRecycled()) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(shareBmp, shareBmp.getWidth() / 10,
                        shareBmp.getHeight() / 10, true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                shareBmp.recycle();
            }
            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            // transaction 字段用于唯一标示一个请求
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;

            // 调用api接口发送数据到微信
            api.sendReq(req);
        }
    }

    /**
     * 获取缩略图
     *
     * @param url
     * @param callBack
     */
    private void getThumData(String url, final BitmapCallBack<Bitmap> callBack) {
        Glide.with(this).load(url).downloadOnly(new ProgressTarget<String, File>(url, null) {
            @Override
            public void onLoadStarted(Drawable placeholder) {
                super.onLoadStarted(placeholder);
                showLoadingDialog();
            }

            @Override
            public void onProgress(long bytesRead, long expectedLength) {
            }

            @Override
            public void onResourceReady(File resource, GlideAnimation<? super File> animation) {
                super.onResourceReady(resource, animation);
                hideLoadingDialog();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(resource.getAbsolutePath(), options);
                LL.i("bitmap.size = " + bitmap.getWidth() + " -- " + bitmap.getHeight());
                callBack.onBitmapLoadSuccess(bitmap);
            }

            @Override
            public void getSize(SizeReadyCallback cb) {
                cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
            }
        });
    }

    /**
     * 分享到朋友圈
     */
    public void shareToTimeline(Bitmap shareBmp) {
        MobclickAgent.onEvent(this, "article_share");
        if (!isWeixinAvilible(this)) {
            Toast.makeText(this, "抱歉！您还没有安装微信", Toast.LENGTH_SHORT).show();
            return;
        }
        if (popShare.isShowing()) {
            popShare.dismiss();

            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = contentUrl;

            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = web_content.getTitle();
            msg.description = msgDesc;
            // 设置缩略图
            if (shareBmp != null && !shareBmp.isRecycled()) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(shareBmp, shareBmp.getWidth() / 10,
                        shareBmp.getHeight() / 10, true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                shareBmp.recycle();
            }
            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            // transaction 字段用于唯一标示一个请求
            req.transaction = buildTransaction("webpage");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneTimeline;

            // 调用api接口发送数据到微信
            api.sendReq(req);
        }
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /***
     * 检查是否安装了微信
     * @param context
     * @return
     */
    private boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
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
}
