package com.bj.eduteacher.activity;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.T;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zz379 on 2017/5/4.
 */

public class WebviewActivity extends BaseActivity {

    @BindView(R.id.header_img_back)
    ImageView imgBack;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.web_content)
    WebView web_content;
    @BindView(R.id.progress)
    ProgressBar progressBar;
    @BindView(R.id.ll_errorContent)
    LinearLayout llErrorContent;

    private WebSettings setting;
    private String title, contentUrl;
    private long clickMillis = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        ButterKnife.bind(this);

        initToolBar();
        initView();
        loadContent();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        title = getIntent().getExtras().getString(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, "");
        contentUrl = getIntent().getExtras().getString(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, "");

        imgBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }

    @OnClick(R.id.header_ll_left)
    void clickBack() {
        this.finish();
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
                LL.i("webView ---->>>> onPageFinished ---->>>> title:" + view.getTitle());
                // tvTitle.setText(String.valueOf(view.getTitle()));
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
                super.onReceivedSslError(view, handler, error);
                LL.i("webView ---->>>> onReceivedSslError");
                // llErrorContent.setVisibility(View.VISIBLE);
                // web_content.setVisibility(View.GONE);
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
        // 优先使用缓存
//        setting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        setting.setUseWideViewPort(true);   //设定支持viewport
        setting.setLoadWithOverviewMode(true);
        setting.setBuiltInZoomControls(false);
        setting.setDisplayZoomControls(false);// 显示放大缩小按钮
        setting.setSupportZoom(false);   //设定支持缩放
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
        MobclickAgent.onPageStart("user_protocol");
        MobclickAgent.onResume(this);
        web_content.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("user_protocol");
        MobclickAgent.onPause(this);
        web_content.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        web_content.destroy();
    }

    @OnClick(R.id.ll_errorContent)
    void clickRefreshView() {
        // 重新加载页面
        loadContent();
    }
}
