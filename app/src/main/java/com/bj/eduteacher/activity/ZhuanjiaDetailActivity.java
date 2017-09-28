package com.bj.eduteacher.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.ZhuanjiaDetailAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.dialog.TipsAlertDialog3;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.OrderInfo;
import com.bj.eduteacher.entity.TradeInfo;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.utils.Util;
import com.bj.eduteacher.view.FullyLinearLayoutManager;
import com.bj.eduteacher.widget.PullZoomView;
import com.bj.eduteacher.widget.RoundProgressBar;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jaeger.library.StatusBarUtil;
import com.jakewharton.rxbinding2.view.RxView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zz379 on 2017/7/30.
 */

public class ZhuanjiaDetailActivity extends BaseActivity {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.title_layout)
    RelativeLayout rlTitleLayout;
    @BindView(R.id.title_center_layout)
    LinearLayout llCenterLayout;
    @BindView(R.id.title_uc_title)
    TextView tvTitle;
    @BindView(R.id.zoomView)
    RelativeLayout rlZoomView;
    @BindView(R.id.uc_progressbar)
    RoundProgressBar progressBar;

    SimpleDraweeView ivZhuanjiaPhoto;
    TextView tvZhuanjiaName;
    TextView tvZhuanjiaTitle;
    TextView tvZhuanjiaDesc;

    private String teacherPhoneNumber, userPhotoPath;
    private ZhuanjiaDetailAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;
    private List<ArticleInfo> mDataList = new ArrayList<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    // 分享
    private IWXAPI api;
    private PopupWindow popPayDetail;
    private boolean isUserPaySuccess = false;  // 是否支付成功
    private String currTradeID = "";     // 当前商户订单号

    private String zhuanjiaID;
    private String zhuanjiaName;
    private String zhuanjiaTitle;
    private String zhuanjiaImg;
    private View headerView;
    private int headerHeight = 0;
    private Disposable shareDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuanjia_detail);
        ButterKnife.bind(this);
        api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID);

        initStatus();
        initToolbar();
        initView();
        initData();
    }

    private void initToolbar() {
        zhuanjiaID = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_ID);
        zhuanjiaName = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_NAME);
        zhuanjiaTitle = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_TITLE);
        zhuanjiaImg = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_IMG);
    }

    private void initView() {
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(ZhuanjiaDetailActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        // set Adatper
        mAdapter = new ZhuanjiaDetailAdapter(mDataList);
        mAdapter.setOnMyItemClickListener(new ZhuanjiaDetailAdapter.OnMyItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                String path = mDataList.get(position).getArticlePath();
                String id = mDataList.get(position).getArticleID();
                if (!StringUtils.isEmpty(path)) {
                    Intent intent = new Intent(ZhuanjiaDetailActivity.this, DoukeDetailActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, id);
                    intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_URL, path);
                    startActivity(intent);
                } else {
                    T.showShort(ZhuanjiaDetailActivity.this, "页面不存在");
                }
            }

            @Override
            public void onZhuanjiaClick(View view, int position) {
                ArticleInfo item = mDataList.get(position);
                String price = item.getAgreeNumber();
                String buyType = item.getCommentNumber();
                if (!"0".equals(price) && "0".equals(buyType)) {
                    MobclickAgent.onEvent(ZhuanjiaDetailActivity.this, "doc_buy");
                    initPopViewPayDetail(item.getArticleID(), item.getAgreeNumber());
                } else {
                    MobclickAgent.onEvent(ZhuanjiaDetailActivity.this, "doc_look");
                    String resID = mDataList.get(position).getArticleID();
                    String resName = mDataList.get(position).getTitle();
                    String previewUrl = mDataList.get(position).getArticlePath();
                    String downloadUrl = mDataList.get(position).getArticlePicture();
                    String resType = item.getPreviewType();  // 目前先根据这个类型来判断是否是视频
                    if ("2".equals(resType)) {
                        Intent intent = new Intent(ZhuanjiaDetailActivity.this, ResPlayActivity.class);
                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(ZhuanjiaDetailActivity.this, ResReviewActivity.class);
                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL, downloadUrl);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onSubjectClick(View view, String tag, int position) {
                ArticleInfo item = mDataList.get(position);
                if ("Answer".equals(tag)) {
                    // 点击我要回答
                    Intent intent = new Intent(ZhuanjiaDetailActivity.this, SubjectDetailActivity.class);
                    intent.putExtra("Type", "Answer");
                    intent.putExtra("SubId", item.getArticleID());
                    intent.putExtra("SubTitle", item.getTitle());
                    intent.putExtra("SubContent", item.getContent());
                    startActivity(intent);
                } else if ("Invite".equals(tag)) {
                    inviteFriendsAnswer(item);
                } else if ("ViewReply".equals(tag)) {
                    // 查看全部回复
                    Intent intent = new Intent(ZhuanjiaDetailActivity.this, SubjectDetailActivity.class);
                    intent.putExtra("Type", "ViewReply");
                    intent.putExtra("SubId", item.getArticleID());
                    intent.putExtra("SubTitle", item.getTitle());
                    intent.putExtra("SubContent", item.getContent());
                    startActivity(intent);
                } else if ("SubMore1".equals(tag)) {
                    ArticleInfo subItem = item.getReplyList().get(0);
                    Intent intent = new Intent(ZhuanjiaDetailActivity.this, SubjectDetailActivity.class);
                    intent.putExtra("Type", "ViewReply");
                    intent.putExtra("SubId", subItem.getArticleID());
                    intent.putExtra("SubTitle", subItem.getTitle());
                    intent.putExtra("SubContent", subItem.getContent());
                    startActivity(intent);
                } else if ("SubMore2".equals(tag)) {
                    ArticleInfo subItem = item.getReplyList().get(1);
                    Intent intent = new Intent(ZhuanjiaDetailActivity.this, SubjectDetailActivity.class);
                    intent.putExtra("Type", "ViewReply");
                    intent.putExtra("SubId", subItem.getArticleID());
                    intent.putExtra("SubTitle", subItem.getTitle());
                    intent.putExtra("SubContent", subItem.getContent());
                    startActivity(intent);
                } else if ("SubAll".equals(tag)) {
                    Intent intent = new Intent(ZhuanjiaDetailActivity.this, SubjectAllActivity.class);
                    intent.putExtra("MasterCode", zhuanjiaID);
                    startActivity(intent);
                }
            }
        });
        headerView = mAdapter.setHeaderView(R.layout.layout_header_zhuanjia_detail, mRecyclerView);
        initHeaderView();
        mRecyclerView.setAdapter(mAdapter);

        PullZoomView pzv = (PullZoomView) findViewById(R.id.pzv);
        pzv.setIsParallax(true);
        pzv.setIsZoomEnable(true);
        pzv.setSensitive(1.5f);
        pzv.setZoomTime(500);
        pzv.setOnScrollListener(new PullZoomView.OnScrollListener() {
            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                if (Math.abs(t) > 0 && Math.abs(t) <= headerHeight) {
                    float percent = Float.valueOf(Math.abs(t)) / Float.valueOf(headerHeight);
                    llCenterLayout.setAlpha(percent);
                    StatusBarUtil.setColor(ZhuanjiaDetailActivity.this, ContextCompat.getColor(ZhuanjiaDetailActivity.this, R.color.colorPrimary), 0);
                } else if (Math.abs(t) == 0) {
                    llCenterLayout.setAlpha(0f);
                    StatusBarUtil.setColor(ZhuanjiaDetailActivity.this, ContextCompat.getColor(ZhuanjiaDetailActivity.this, android.R.color.transparent), 0);
                } else {
                    llCenterLayout.setAlpha(1f);
                    StatusBarUtil.setColor(ZhuanjiaDetailActivity.this, ContextCompat.getColor(ZhuanjiaDetailActivity.this, R.color.colorPrimary), 0);
                }
            }

            @Override
            public void onHeaderScroll(int currentY, int maxY) {
            }

            @Override
            public void onContentScroll(int l, int t, int oldl, int oldt) {
            }
        });
        pzv.setOnPullZoomListener(new PullZoomView.OnPullZoomListener() {
            @Override
            public void onPullZoom(int originHeight, int currentHeight) {
                double progress = Double.valueOf(currentHeight - originHeight) / Double.valueOf(originHeight / 2);
                if (!progressBar.isSpinning) {
                    progressBar.setProgress((int) (progress * 360));
                }
            }

            @Override
            public void onUpToZoom() {
                if (!progressBar.isSpinning) {
                    // 刷新viewpager里的fragment
                    startRefreshChildView();
                }
            }

            @Override
            public void onZoomFinish() {

            }
        });
    }

    /**
     * 开始刷新
     */
    public void startRefreshChildView() {
        progressBar.spin();
        currentPage = 1;
        // 为了显示刷新效果
//        mDataList.clear();
//        mAdapter.notifyDataSetChanged();
        getRefreshDataList(zhuanjiaID);
    }

    /**
     * 停止刷新
     */
    public void stopRefreshChildView() {
        if (progressBar.isSpinning) {
            progressBar.stopSpinning();
        }
    }

    private void initHeaderView() {
        ivZhuanjiaPhoto = (SimpleDraweeView) headerView.findViewById(R.id.iv_authorPhoto);
        tvZhuanjiaName = (TextView) headerView.findViewById(R.id.tv_authorName);
        tvZhuanjiaTitle = (TextView) headerView.findViewById(R.id.tv_authorTitle);
        tvZhuanjiaDesc = (TextView) headerView.findViewById(R.id.tv_authorDesc);

        if (!StringUtils.isEmpty(zhuanjiaName)) {
            tvZhuanjiaName.setText(zhuanjiaName);
            tvTitle.setText(zhuanjiaName);
        }
        if (!StringUtils.isEmpty(zhuanjiaTitle)) {
            tvZhuanjiaTitle.setText(zhuanjiaTitle);
        }
        if (!StringUtils.isEmpty(zhuanjiaImg)) {
            ivZhuanjiaPhoto.setImageURI(zhuanjiaImg);
        }

        TextView tvShare = (TextView) headerView.findViewById(R.id.tv_shareZhuanjia);
        shareDisposable = RxView.clicks(tvShare)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        showShareDialog();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (shareDisposable != null && !shareDisposable.isDisposed()) {
            shareDisposable.dispose();
        }
    }

    /**
     * 初始化状态栏位置
     */
    private void initStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以下不支持状态栏变色
            //注意了，这里使用了第三方库 StatusBarUtil，目的是改变状态栏的alpha
            StatusBarUtil.setTransparentForImageView(ZhuanjiaDetailActivity.this, null);
            //这里是重设我们的title布局的topMargin，StatusBarUtil提供了重设的方法，但是我们这里有两个布局
            //TODO 关于为什么不把Toolbar和@layout/layout_uc_head_title放到一起，是因为需要Toolbar来占位，防止AppBarLayout折叠时将title顶出视野范围
            int statusBarHeight = getStatusBarHeight(ZhuanjiaDetailActivity.this);
            llCenterLayout.setAlpha(0f);
            FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) rlTitleLayout.getLayoutParams();
            lp1.topMargin = statusBarHeight;
            rlTitleLayout.setLayoutParams(lp1);
        }
    }

    /**
     * 获取状态栏高度
     * ！！这个方法来自StatusBarUtil,因为作者将之设为private，所以直接copy出来
     *
     * @param context context
     * @return 状态栏高度
     */
    private int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private void initData() {
        teacherPhoneNumber = PreferencesUtils.getString(ZhuanjiaDetailActivity.this, MLProperties.PREFER_KEY_USER_ID);
        userPhotoPath = PreferencesUtils.getString(ZhuanjiaDetailActivity.this, MLProperties.BUNDLE_KEY_TEACHER_IMG);

        currentPage = 1;
        mDataList.clear();

        getZhuanjiaInfoFromAPI();
        getRefreshDataList(zhuanjiaID);
    }

    private void getZhuanjiaInfoFromAPI() {
        Observable.create(new ObservableOnSubscribe<ArticleInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArticleInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                ArticleInfo masterInfo = mService.getMasterCardsFromAPI(zhuanjiaID);
                e.onNext(masterInfo);
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
                        zhuanjiaName = articleInfo.getAuthor();
                        zhuanjiaTitle = articleInfo.getTitle();
                        zhuanjiaImg = articleInfo.getAuthImg();

                        tvZhuanjiaName.setText(articleInfo.getAuthor());
                        tvZhuanjiaTitle.setText(articleInfo.getTitle());
                        tvZhuanjiaDesc.setText(articleInfo.getAuthDesc());
                        ivZhuanjiaPhoto.setImageURI(articleInfo.getAuthImg());
                        tvTitle.setText(zhuanjiaName);

                        final ViewTreeObserver viewTreeObserver = tvZhuanjiaDesc.getViewTreeObserver();
                        viewTreeObserver.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                            @Override
                            public void onDraw() {
                                headerHeight = headerView.getHeight() + rlZoomView.getHeight();
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getRefreshDataList(final String masterID) {
        if (!NetUtils.isConnected(this)) {
            T.showShort(this, "无法连接到网络，请检查您的网络设置");
            hideLoadingDialog();
            stopRefreshChildView();
            return;
        }

        // 获取专家资源
        Observable observable1 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                // SystemClock.sleep(1000);
                List<ArticleInfo> dataList = mService.getMasterResFromAPI(masterID, teacherPhoneNumber, currentPage);
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
        // 获取专家相关精选文章列表
        Observable observable2 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                // SystemClock.sleep(1000);
                List<ArticleInfo> dataList = mService.getMasterDouKeListFromAPI(masterID, currentPage);
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
        // 获取专家黑板报信息
        Observable observable3 = Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                List<ArticleInfo> data = mService.getMasterSubjectTopFromAPI(masterID, currentPage);
                e.onNext(data);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, observable3, new Function3<List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>, List<ArticleInfo>>() {

            @Override
            public List<ArticleInfo> apply(@NonNull List<ArticleInfo> data1, @NonNull List<ArticleInfo> data2, @NonNull List<ArticleInfo> data3) throws Exception {
                List<ArticleInfo> dataList = new ArrayList<>();
                if (data1.size() > 0) {
                    dataList.add(new ArticleInfo("学习资料", ArticleInfo.SHOW_TYPE_DECORATION));
                    dataList.addAll(data1);
                }
                if (data3 != null && data3.size() > 0) {
                    // 置顶话题
                    dataList.add(new ArticleInfo("专家黑板报", ArticleInfo.SHOW_TYPE_DECORATION));
                    ArticleInfo subjectTop = data3.get(0);
                    subjectTop.setShowType(ArticleInfo.SHOW_TYPE_ZHUANJIA_BLACKBOARD_TOP);
                    dataList.add(subjectTop);
                    // 更多话题
                    if (data3.size() > 1) {
                        ArticleInfo subjectMore = new ArticleInfo();
                        subjectMore.setShowType(ArticleInfo.SHOW_TYPE_ZHUANJIA_BLACKBOARD_MORE);
                        List<ArticleInfo> subjectList = new ArrayList<>();
                        subjectList.addAll(data3.subList(1, data3.size()));
                        subjectMore.setReplyList(subjectList);
                        dataList.add(subjectMore);
                    }
                }
                if (data2.size() > 0) {
                    dataList.add(new ArticleInfo("相关精选", ArticleInfo.SHOW_TYPE_DECORATION));
                    dataList.addAll(data2);
                }
                return dataList;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {
                        loadRefreshData(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LL.e(e);
                        hideLoadingDialog();
                        stopRefreshChildView();
                        T.showShort(ZhuanjiaDetailActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadRefreshData(List<ArticleInfo> result) {
        hideLoadingDialog();
        stopRefreshChildView();
        mDataList.clear();
        mDataList.addAll(result);
        mAdapter.notifyDataSetChanged();
    }

    private void getZhuanjiaDouke(String masterID) {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                List<ArticleInfo> dataList = mService.getDouKeListFromAPI(currentPage);
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {
                        loadData(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        T.showShort(ZhuanjiaDetailActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadData(List<ArticleInfo> list) {
        // 更新数据
        mDataList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    /********************************* 分享功能 ***********************************************/
    void showShareDialog() {
        MobclickAgent.onEvent(ZhuanjiaDetailActivity.this, "elect_expert");
        View popView = LayoutInflater.from(this).inflate(R.layout.alert_share_zhuanjia, null);
        SimpleDraweeView ivUserPhoto = (SimpleDraweeView) popView.findViewById(R.id.img_kidPhoto);
        TextView tvAuthorName = (TextView) popView.findViewById(R.id.tv_authorName);
        TextView tvAuthorDesc = (TextView) popView.findViewById(R.id.tv_authorDesc);
        ivUserPhoto.setImageURI(zhuanjiaImg);
        tvAuthorName.setText(zhuanjiaName);
        tvAuthorDesc.setText(zhuanjiaTitle);
        final ScrollView mScrollView = (ScrollView) popView.findViewById(R.id.mScrollView);

        final AlertDialog shareDialog = new AlertDialog.Builder(this).create();
        shareDialog.setCanceledOnTouchOutside(false);
        shareDialog.show();
        shareDialog.getWindow().setContentView(popView);
        shareDialog.getWindow().findViewById(R.id.iv_shareSession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToSession(shareDialog, mScrollView);
            }
        });
        shareDialog.getWindow().findViewById(R.id.iv_shareTimeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToTimeline(shareDialog, mScrollView);
            }
        });
        shareDialog.getWindow().findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });
    }

    /**
     * 分享到微信
     */
    public void shareToSession(AlertDialog shareDialog, ScrollView mScrollView) {
        if (!isWeixinAvilible(this)) {
            Toast.makeText(this, "抱歉！您还没有安装微信", Toast.LENGTH_SHORT).show();
            return;
        }
        if (shareDialog.isShowing() && mScrollView != null) {
            Bitmap shareBmp = ScreenUtils.compressImage(ScreenUtils.getBitmapByView(mScrollView));
            shareDialog.dismiss();

            WXImageObject imgObject = new WXImageObject(shareBmp);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObject;
            // 设置缩略图
            if (shareBmp != null && !shareBmp.isRecycled()) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(shareBmp, shareBmp.getWidth() / 10,
                        shareBmp.getHeight() / 10, true);
                // msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                shareBmp.recycle();
            }
            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            // transaction 字段用于唯一标示一个请求
            req.transaction = buildTransaction("img");
            req.message = msg;
            req.scene = SendMessageToWX.Req.WXSceneSession;

            // 调用api接口发送数据到微信
            api.sendReq(req);
        }
    }

    /**
     * 分享到朋友圈
     */
    public void shareToTimeline(AlertDialog shareDialog, ScrollView mScrollView) {
        if (!isWeixinAvilible(this)) {
            Toast.makeText(this, "抱歉！您还没有安装微信", Toast.LENGTH_SHORT).show();
            return;
        }
        if (shareDialog.isShowing() && mScrollView != null) {
            Bitmap shareBmp = ScreenUtils.compressImage(ScreenUtils.getBitmapByView(mScrollView));
            shareDialog.dismiss();

            WXImageObject imgObject = new WXImageObject(shareBmp);
            WXMediaMessage msg = new WXMediaMessage();
            msg.mediaObject = imgObject;
            // 设置缩略图
            if (shareBmp != null && !shareBmp.isRecycled()) {
                Bitmap thumbBmp = Bitmap.createScaledBitmap(shareBmp, 150,
                        shareBmp.getHeight() * 150 / shareBmp.getWidth(), true);
                msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
                shareBmp.recycle();
            }
            // 构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            // transaction 字段用于唯一标示一个请求
            req.transaction = buildTransaction("img");
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

    /*************************** 支付 **************************************/
    private void initPopViewPayDetail(final String masterid, final String realPrice) {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_pay_masterres_detail, null);
        popPayDetail = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popPayDetail.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        popPayDetail.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popPayDetail.setFocusable(true);
        popPayDetail.setOutsideTouchable(true);
        popPayDetail.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(ZhuanjiaDetailActivity.this, 1f);
            }
        });
        TextView tvRealPay = (TextView) popView.findViewById(R.id.tv_realPay);
        tvRealPay.setText("¥ " + (Double.parseDouble(realPrice)) / 100);
        TextView tvReportProtocol = (TextView) popView.findViewById(R.id.tv_report_protocol);
        tvReportProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ZhuanjiaDetailActivity.this, PayProtocolActivity.class);
                startActivity(intent);
            }
        });
        TextView tvPay = (TextView) popView.findViewById(R.id.tv_payReport);
        tvPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 开始支付,隐藏支付窗口
                hidePopViewPayDetail();
                // 获取订单号
                getTheOrderFromAPI(masterid, String.valueOf(realPrice), "masterres");
                MobclickAgent.onEvent(ZhuanjiaDetailActivity.this, "masterres_pay");
            }
        });
        // 显示页面
        showPopViewPayDetail();
    }

    private void showPopViewPayDetail() {
        if (popPayDetail != null && !popPayDetail.isShowing()) {
            setBackgroundAlpha(this, 0.5f);
            popPayDetail.showAtLocation(mRecyclerView, Gravity.BOTTOM, 0, popPayDetail.getHeight());
        }
    }

    private void hidePopViewPayDetail() {
        if (popPayDetail != null && popPayDetail.isShowing()) {
            popPayDetail.dismiss();
            popPayDetail = null;
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
     * 生成订单
     */
    private void getTheOrderFromAPI(final String masterresid, final String price, final String payType) {
        Observable.create(new ObservableOnSubscribe<OrderInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<OrderInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                OrderInfo info = mService.getTheOrderInfoFromAPI(masterresid, price, teacherPhoneNumber, payType);
                e.onNext(info);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<OrderInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showLoadingDialog();
                    }

                    @Override
                    public void onNext(OrderInfo orderInfo) {
                        // 获取订单成功后 发起微信支付
                        if ("SUCCESS".equals(orderInfo.getResult_code())
                                && "SUCCESS".equals(orderInfo.getReturn_code())
                                && "OK".equals(orderInfo.getReturn_msg())) {
                            startPay(orderInfo);
                        } else {
                            // 获取订单失败
                            T.showShort(ZhuanjiaDetailActivity.this, "订单生成失败");
                        }
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 开始支付
     *
     * @param orderInfo
     */
    private void startPay(OrderInfo orderInfo) {
        if (api.getWXAppSupportAPI() >= com.tencent.mm.opensdk.constants.Build.PAY_SUPPORTED_SDK_INT) {
            currTradeID = orderInfo.getOut_trade_no();  // 获取当前商户订单号
            PayReq request = new PayReq();
            request.appId = orderInfo.getAppid();
            request.partnerId = orderInfo.getMch_id();   // 商户号
            request.prepayId = orderInfo.getPrepay_id();
            request.packageValue = "Sign=WXPay";
            request.nonceStr = orderInfo.getNonce_str();
            request.timeStamp = orderInfo.getTimeStamp();
            request.extData = "app data";
            request.sign = orderInfo.getSign();
            api.sendReq(request);
        } else {
            T.showShort(this, "当前手机暂不支持该功能");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("zhuanjia_detail");
        MobclickAgent.onResume(this);
        if (!StringUtils.isEmpty(currTradeID)) {
            queryTheTradeStateFromAPI(currTradeID);
        } else {
            getRefreshDataList(zhuanjiaID);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("zhuanjia_detail");
        MobclickAgent.onPause(this);
    }

    /**
     * 查询订单支付状态
     *
     * @param tradeID
     */
    private void queryTheTradeStateFromAPI(final String tradeID) {
        Observable.create(new ObservableOnSubscribe<TradeInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<TradeInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                TradeInfo info = mService.getTheTradeInfoFromAPI(tradeID);
                e.onNext(info);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TradeInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(TradeInfo tradeInfo) {
                        currTradeID = "";
                        if ("SUCCESS".equals(tradeInfo.getResult_code())) {
                            if ("SUCCESS".equals(tradeInfo.getTrade_state())) {
                                showAlertDialog("支付成功", "您现在就可以去查看完整资料啦");
                            } else if ("NOTPAY".equals(tradeInfo.getTrade_state())) {
                                showAlertDialog("支付失败", "支付遇到问题，请重试");
                            }
                        } else {
                            showAlertDialog("支付失败", "支付遇到问题，请重试");
                        }
                        // 刷新页面
                        showLoadingDialog();
                        getRefreshDataList(zhuanjiaID);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void showAlertDialog(String title, String content) {
        TipsAlertDialog3 dialog = new TipsAlertDialog3(this);
        dialog.setTitleText(title);
        dialog.setContentText(content);
        dialog.setConfirmText("好的");
        dialog.setConfirmClickListener(new TipsAlertDialog3.OnSweetClickListener() {
            @Override
            public void onClick(TipsAlertDialog3 sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });
        dialog.show();
    }

    @OnClick(R.id.uc_setting_iv)
    void clickHeaderback() {
        onBackPressed();
    }

    /**
     * 邀请好友回答
     *
     * @param item
     */
    private void inviteFriendsAnswer(ArticleInfo item) {
        View popView = LayoutInflater.from(this).inflate(R.layout.alert_share_subject, null);
        SimpleDraweeView ivUserPhoto = (SimpleDraweeView) popView.findViewById(R.id.img_kidPhoto);
        // TextView tvAuthorName = (TextView) popView.findViewById(R.id.tv_authorName);
        TextView tvAuthorDesc = (TextView) popView.findViewById(R.id.tv_authorDesc);
        ivUserPhoto.setImageURI(userPhotoPath);
        // tvAuthorName.setText(zhuanjiaName);
        tvAuthorDesc.setText(item.getTitle());
        final ScrollView mScrollView = (ScrollView) popView.findViewById(R.id.mScrollView);

        final AlertDialog inviteDialog = new AlertDialog.Builder(this).create();
        inviteDialog.setCanceledOnTouchOutside(false);
        inviteDialog.show();
        inviteDialog.getWindow().setContentView(popView);
        inviteDialog.getWindow().findViewById(R.id.iv_shareSession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToSession(inviteDialog, mScrollView);
            }
        });
        inviteDialog.getWindow().findViewById(R.id.iv_shareTimeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToTimeline(inviteDialog, mScrollView);
            }
        });
        inviteDialog.getWindow().findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteDialog.dismiss();
            }
        });
    }
}
