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
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.T;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zz379 on 2017/5/4.
 * 支付协议页面
 */

public class PayProtocolActivity extends BaseActivity {

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
    private String contentUrl;
    private long clickMillis = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protocol);
        ButterKnife.bind(this);

        contentUrl = "https://mp.weixin.qq.com/s?__biz=MzI2NzU3MTg0OA==&mid=2247483861&idx=2&sn=798061c9d1978714076b64311c9ca2e4&chksm=eafd8632dd8a0f2461effb6da7752968bee745d4aebb171cd70d4d022c0ed093f80a8749dd76&scene=0&key=d366dccab315fdbf284fb3d7ba8b9ab6124ea3920ff4a7350d777bf0cda79a1ed19a27b6b6b2c9101a59f406bae11c7c27f5d61dee15a3f7a5b18a88f599490d8524783eb3f87f5431b79378226f4e86&ascene=0&uin=MTI4MzUxMjEzNw%3D%3D&devicetype=iMac+MacBookPro13%2C2+OSX+OSX+10.12.5+build(16F73)&version=12020810&nettype=WIFI&fontScale=100&pass_ticket=DmPEeD%2Bi86qiRxNu%2FjCpMp2Ce5Ap9BWQWeDcxVPUnEjBZF06t7OV3Cxv%2FL14qO3R";
        LL.i("协议地址：" + contentUrl);

        initToolBar();
        initView();
        loadContent();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("订阅服务协议");
        imgBack.setVisibility(View.VISIBLE);
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
        MobclickAgent.onPageStart("baogao_protocol");
        MobclickAgent.onResume(this);
        web_content.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("baogao_protocol");
        MobclickAgent.onPause(this);
        web_content.onPause();
    }

    @Override
    protected void onDestroy() {
        web_content.destroy();
        super.onDestroy();
    }

    @OnClick(R.id.ll_errorContent)
    void clickRefreshView() {
        // 重新加载页面
        loadContent();
    }
}
