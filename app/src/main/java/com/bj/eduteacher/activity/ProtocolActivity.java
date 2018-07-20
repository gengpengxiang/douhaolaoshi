package com.bj.eduteacher.activity;

import android.net.http.SslError;
import android.os.Build;
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
 * 用户协议页面
 */

public class ProtocolActivity extends BaseActivity {

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

        //contentUrl = "http://mp.weixin.qq.com/s/h7fgLrC2209BWolPUcgObw";
        contentUrl = "http://douhaolaoshi.gamepku.com/index.php/wenzhang/index/1148";
        LL.i("协议地址：" + contentUrl);

        initToolBar();
        initView();
        loadContent();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("用户协议");
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
        web_content.getSettings().setJavaScriptEnabled(true);//启用js
        web_content.getSettings().setBlockNetworkImage(false);//解决图片不显示
        web_content.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            web_content.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
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
