package com.bj.eduteacher.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.T;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 加载HTML页面
 */
@SuppressLint("SetJavaScriptEnabled")
public class LoginHelpActivity extends BaseActivity {

    @BindView(R.id.header_img_back)
    ImageView ivBack;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.web_content)
    WebView web_content;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.ll_errorContent)
    LinearLayout llErrorContent;

    private WebSettings setting;
    private String contentUrl;

    private long clickMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douke_detail);
        ButterKnife.bind(this);

        contentUrl = "https://www.diaochapai.com/survey2530256";

        initToolBar();
        initView();
        loadContent();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        tvTitle.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
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
                LL.i("5.0以下的系统....shouldOverrideUrlLoading");
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                LL.i("webView ---->>>> onPageFinished ---->>>> title:" + view.getTitle());
                tvTitle.setText(String.valueOf(view.getTitle()));
                // 屏蔽广告
//                view.loadUrl("javascript:(function() { " +
//                        "document.body.innerHTML = " +
//                        "document.body.innerHTML.replace(/Copyright/g,'Google');" +
//                        "})()");
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                LL.i("webView ---->>>> onReceivedError");
                llErrorContent.setVisibility(View.VISIBLE);
                web_content.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                LL.i("webView ---->>>> onReceivedHttpError");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                LL.i("webView ---->>>> onReceivedSslError");
                handler.proceed(); // 接受所有证书
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

        web_content.setVerticalScrollbarOverlay(true); //指定的垂直滚动条有叠加样式
        // 启用支持JavaScript
        setting = web_content.getSettings();
        setting.setJavaScriptEnabled(true);
        // 优先使用缓存
//        setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setUseWideViewPort(true);   //设定支持viewport
        setting.setLoadWithOverviewMode(true);
        setting.setBuiltInZoomControls(false);
        setting.setDisplayZoomControls(false);// 显示放大缩小按钮
        setting.setSupportZoom(false);   //设定支持缩放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
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
        super.onResume();
        MobclickAgent.onPageStart("login_help");
        MobclickAgent.onResume(this);
        web_content.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("login_help");
        MobclickAgent.onPause(this);
        web_content.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        web_content.destroy();
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
        this.finish();
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
}
