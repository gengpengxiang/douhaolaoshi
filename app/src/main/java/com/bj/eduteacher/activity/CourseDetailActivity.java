package com.bj.eduteacher.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.CourseDetailAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.dialog.TipsAlertDialog3;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.OrderInfo;
import com.bj.eduteacher.entity.TradeInfo;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.manager.ShareHelp;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.view.FullyLinearLayoutManager;
import com.bj.eduteacher.view.OnRecyclerItemClickListener;
import com.bj.eduteacher.widget.PullZoomView;
import com.bj.eduteacher.widget.RoundProgressBar;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jaeger.library.StatusBarUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zz379 on 2017/7/30.
 * 名师详情页面
 */

public class CourseDetailActivity extends BaseActivity {

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
    @BindView(R.id.iv_bg)
    SimpleDraweeView ivCoursePicture;
    @BindView(R.id.uc_progressbar)
    RoundProgressBar progressBar;
    @BindView(R.id.tv_pay)
    TextView tvpay;

    TextView tvCourseTitle;
    TextView tvCourseDesc;
    TextView tvCourseLearnNum, tvCourseResNum;

    private String teacherPhoneNumber;
    private CourseDetailAdapter mAdapter;
    public static long lastRefreshTime;
    private List<ArticleInfo> mDataList = new ArrayList<>();
    private final CompositeDisposable disposables = new CompositeDisposable();
    // 分享
    private IWXAPI api;
    private PopupWindow popPayDetail;
    private boolean isUserPaySuccess = false;  // 是否支付成功
    private String currTradeID = "";     // 当前商户订单号

    private String courseID;
    private String coursePicture;
    private String courseTitle, courseDesc, courseLearnNum, courseResNum;
    private String coursePrice;
    private String courseBuyStatus;
    private String courseJiaru;
    private String courseZhengshu, courseShuoming;
    private View headerView;
    private int headerHeight = 0;
    LmsDataService mService = new LmsDataService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        ButterKnife.bind(this);
        init();

        initToolBar();
        initView();
        initData();
    }

    private void init() {
        api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID);
        ViewGroup.LayoutParams lp = rlZoomView.getLayoutParams();
        lp.height = (int) (ScreenUtils.getScreenWidth(this) / 2.08f);
        rlZoomView.setLayoutParams(lp);
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        Bundle args = getIntent().getExtras();
        courseID = args.getString("CourseID", "");
        coursePicture = args.getString("CoursePicture", "");
        ivCoursePicture.setImageURI(coursePicture, "");
        courseTitle = args.getString("CourseTitle", "");
        tvTitle.setText(courseTitle);
        courseDesc = args.getString("CourseDesc", "");
        courseLearnNum = args.getString("CourseLearnNum", "");
        courseResNum = args.getString("CourseResNum", "");
        courseZhengshu = args.getString("CourseZhengshu", "");
        courseShuoming = args.getString("CourseShuoming", "");
    }

    @Override
    protected void initView() {
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        FullyLinearLayoutManager layoutManager = new FullyLinearLayoutManager(CourseDetailActivity.this);
        mRecyclerView.setLayoutManager(layoutManager);
        // set Adatper
        mAdapter = new CourseDetailAdapter(mDataList);
        mAdapter.setPayStatus(CourseDetailAdapter.PAY_STATUS_UNPAY);

        headerView = mAdapter.setHeaderView(R.layout.layout_header_course_detail, mRecyclerView);
        initHeaderView();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                if (position > 0) {
                    ArticleInfo item = mDataList.get(position - 1);
                    actionOnItemClick(item, position - 1);
                }
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder holder, int position) {

            }
        });

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
                    StatusBarUtil.setColor(CourseDetailActivity.this, ContextCompat.getColor(CourseDetailActivity.this, R.color.colorPrimary), 0);
                } else if (Math.abs(t) == 0) {
                    llCenterLayout.setAlpha(0f);
                    StatusBarUtil.setColor(CourseDetailActivity.this, ContextCompat.getColor(CourseDetailActivity.this, android.R.color.transparent), 0);
                } else {
                    llCenterLayout.setAlpha(1f);
                    StatusBarUtil.setColor(CourseDetailActivity.this, ContextCompat.getColor(CourseDetailActivity.this, R.color.colorPrimary), 0);
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

    private void initHeaderView() {
        tvCourseTitle = (TextView) headerView.findViewById(R.id.tv_courseTitle);
        tvCourseDesc = (TextView) headerView.findViewById(R.id.tv_courseDesc);
        tvCourseLearnNum = (TextView) headerView.findViewById(R.id.tv_learnNum);
        tvCourseResNum = (TextView) headerView.findViewById(R.id.tv_resNum);

        tvCourseTitle.setText(courseTitle);
        tvCourseDesc.setText(courseDesc);
        tvCourseLearnNum.setText(courseLearnNum);
        tvCourseResNum.setText(courseResNum);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    /**
     * 初始化状态栏位置
     */
    @Override
    protected void initStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4以下不支持状态栏变色
            //注意了，这里使用了第三方库 StatusBarUtil，目的是改变状态栏的alpha
            StatusBarUtil.setTransparentForImageView(CourseDetailActivity.this, null);
            //这里是重设我们的title布局的topMargin，StatusBarUtil提供了重设的方法，但是我们这里有两个布局
            //TODO 关于为什么不把Toolbar和@layout/layout_uc_head_title放到一起，是因为需要Toolbar来占位，防止AppBarLayout折叠时将title顶出视野范围
            int statusBarHeight = getStatusBarHeight(CourseDetailActivity.this);
            llCenterLayout.setAlpha(0f);
            RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) rlTitleLayout.getLayoutParams();
            lp1.topMargin = statusBarHeight;
            rlTitleLayout.setLayoutParams(lp1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            // 如果存在虚拟按键，则设置虚拟按键的背景色
            if (ScreenUtils.isNavigationBarShow(this)) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, android.R.color.black));
            }
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

    @Override
    protected void initData() {
        teacherPhoneNumber = PreferencesUtils.getString(CourseDetailActivity.this, MLProperties.PREFER_KEY_USER_ID, "");

        mDataList.clear();
        getCourseInfoFromAPI();
    }

    private void actionOnItemClick(ArticleInfo item, int position) {
        if (item.getShowType() == ArticleInfo.SHOW_TYPE_ZHUANJIA_RES) {
            if (position != 1 && (StringUtils.isEmpty(courseJiaru) || courseJiaru.equals("0"))) {
                if (StringUtils.isEmpty(coursePrice) || coursePrice.equals("0") || !"0".equals(courseBuyStatus)) {
                    T.showShort(this, "加入课程后才可学习哦~");
                } else {
                    T.showShort(this, "购买后才可学习哦~");
                }
            } else {
                // 查看资源详情
                MobclickAgent.onEvent(this, "doc_look");
                String resID = item.getArticleID();
                String resName = item.getTitle();
                String previewUrl = item.getArticlePath();
                String downloadUrl = item.getArticlePicture();
                String resType = item.getPreviewType();  // 目前先根据这个类型来判断是否是视频
                if ("2".equals(resType)) {
                    Intent intent = new Intent(this, ResPlayActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(this, ResReviewActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL, downloadUrl);
                    startActivity(intent);
                }
            }
        }
    }

    private void getCourseInfoFromAPI() {
        Observable.create(new ObservableOnSubscribe<ArticleInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArticleInfo> e) throws Exception {
                ArticleInfo article = mService.getCourseDetailInfoFromAPI(courseID, teacherPhoneNumber);
                e.onNext(article);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ArticleInfo info) {
                        String desc = info.getAuthDesc();
                        if (!courseDesc.equals(desc)) {
                            courseDesc = desc;
                            tvCourseDesc.setText(desc);
                        }
                        final ViewTreeObserver viewTreeObserver = tvCourseDesc.getViewTreeObserver();
                        viewTreeObserver.addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
                            @Override
                            public void onDraw() {
                                headerHeight = headerView.getHeight() + rlZoomView.getHeight();
                            }
                        });

                        String learnNum = info.getReplyCount();
                        if (!courseLearnNum.equals(learnNum)) {
                            courseLearnNum = learnNum;
                            tvCourseLearnNum.setText(learnNum);
                        }
                        String resNum = info.getReadNumber();
                        if (!courseResNum.equals(resNum)) {
                            courseResNum = resNum;
                            tvCourseResNum.setText(resNum);
                        }
                        String zhengshu = info.getAuthImg();
                        if (!courseZhengshu.equals(zhengshu)) {
                            courseZhengshu = zhengshu;
                        }
                        String shuoming = info.getContent();
                        if (!courseShuoming.equals(shuoming)) {
                            courseShuoming = shuoming;
                        }
                        // 修改底部加入按钮的显示状态
                        courseJiaru = info.getNickname();
                        coursePrice = info.getAgreeNumber();
                        courseBuyStatus = info.getCommentNumber();
                        if (StringUtils.isEmpty(courseJiaru) || "0".equals(courseJiaru)) {
                            if (StringUtils.isEmpty(coursePrice) || "0".equals(coursePrice)) {
                                tvpay.setText("免费加入学习");
                            } else {
                                if ("0".equals(courseBuyStatus)) {
                                    tvpay.setText("¥ " + (Double.parseDouble(coursePrice)) / 100 + "立即加入学习");
                                } else {
                                    tvpay.setText("已支付，点击加入学习");
                                }
                            }
                            mAdapter.setPayStatus(CourseDetailAdapter.PAY_STATUS_UNPAY);
                        } else {
                            if ("0".equals(coursePrice)) {
                                tvpay.setText("已加入，快去学习");
                            } else {
                                tvpay.setText("已购买，快去学习");
                            }
                            mAdapter.setPayStatus(CourseDetailAdapter.PAY_STATUS_PAYED);
                        }
                        // 刷新资源列表
                        getRefreshDataList(courseID);
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
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                List<ArticleInfo> dataList = mService.getCourseResFromAPI(masterID);
                if (dataList.size() > 0) {
                    dataList.add(0, new ArticleInfo("课程内容", ArticleInfo.SHOW_TYPE_DECORATION));
                }
                dataList.add(new ArticleInfo("课程证书", ArticleInfo.SHOW_TYPE_DECORATION));
                dataList.add(new ArticleInfo(courseZhengshu, ArticleInfo.SHOW_TYPE_ZHUANJIA_BLACKBOARD_TOP));
                dataList.add(new ArticleInfo("课程说明", ArticleInfo.SHOW_TYPE_DECORATION));
                dataList.add(new ArticleInfo(courseShuoming, ArticleInfo.SHOW_TYPE_ZHUANJIA_BLACKBOARD_MORE));
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
                        loadRefreshData(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LL.e(e);
                        hideLoadingDialog();
                        stopRefreshChildView();
                        T.showShort(CourseDetailActivity.this, "服务器开小差了，请重试");
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

    /**
     * 开始刷新
     */
    public void startRefreshChildView() {
        progressBar.spin();
        // 为了显示刷新效果
        getCourseInfoFromAPI();
    }

    /**
     * 停止刷新
     */
    public void stopRefreshChildView() {
        if (progressBar.isSpinning) {
            progressBar.stopSpinning();
        }
    }

    /********************************* 分享功能 ***********************************************/
    @OnClick(R.id.tv_share)
    void showShareDialog() {
        MobclickAgent.onEvent(CourseDetailActivity.this, "elect_expert");
        View popViewShare = LayoutInflater.from(this).inflate(R.layout.alert_share_course, null);
        SimpleDraweeView ivPicture = (SimpleDraweeView) popViewShare.findViewById(R.id.iv_coursePicture);
        TextView tvTitle = (TextView) popViewShare.findViewById(R.id.tv_courseTitle);
        ivPicture.setImageURI(coursePicture);
        tvTitle.setText(courseTitle);
        ShareHelp.getInstance().showShareDialog(this, popViewShare);
    }

    @OnClick(R.id.uc_setting_iv)
    void clickHeaderback() {
        onBackPressed();
    }

    @OnClick(R.id.tv_pay)
    void clickPayCourse() {
        if (StringUtils.isEmpty(courseJiaru) || courseJiaru.equals("0")) {
            if (StringUtils.isEmpty(teacherPhoneNumber)) {
                IntentManager.toLoginActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
                return;
            }

            if (StringUtils.isEmpty(coursePrice) || "0".equals(coursePrice)) {
                // 调用加入课程的接口
                joinCourse(courseID, teacherPhoneNumber, "1", "free");
            } else {
                if ("0".equals(courseBuyStatus)) {
                    initPopViewPayDetail(courseID, coursePrice);
                } else {
                    // 已支付，但是加入课程时出现问题，直接加入课程
                    joinCourse(courseID, teacherPhoneNumber, "1", "reAdd");
                }
            }
        } else {
            if (StringUtils.isEmpty(coursePrice) || coursePrice.equals("0")) {
                T.showShort(this, "已加入，快去学习吧~");
            } else {
                T.showShort(this, "已购买，快去学习吧~");
            }
        }
    }

    /*************************** 支付 **************************************/
    private void initPopViewPayDetail(final String masterid, final String realPrice) {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_pay_course_detail, null);
        popPayDetail = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popPayDetail.setAnimationStyle(R.style.MyPopupWindow_anim_style);
        popPayDetail.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popPayDetail.setFocusable(true);
        popPayDetail.setOutsideTouchable(true);
        popPayDetail.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(CourseDetailActivity.this, 1f);
            }
        });
        TextView tvRealPay = (TextView) popView.findViewById(R.id.tv_realPay);
        tvRealPay.setText("¥ " + (Double.parseDouble(realPrice)) / 100);
        TextView tvReportProtocol = (TextView) popView.findViewById(R.id.tv_report_protocol);
        tvReportProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetailActivity.this, PayProtocolActivity.class);
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
                getTheOrderFromAPI(masterid, String.valueOf(realPrice), "kecheng");
                MobclickAgent.onEvent(CourseDetailActivity.this, "masterres_pay");
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
                OrderInfo info = mService.getTheOrderInfoFromAPIForCourse(masterresid, price, teacherPhoneNumber, payType);
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
                            T.showShort(CourseDetailActivity.this, "订单生成失败");
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
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

        if (!StringUtils.isEmpty(currTradeID)) {
            queryTheTradeStateFromAPI(currTradeID);
        } else {
            getCourseInfoFromAPI();
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
                                // 支付成功后自动加入课程
                                joinCourse(courseID, teacherPhoneNumber, "1", "pay");
                            } else if ("NOTPAY".equals(tradeInfo.getTrade_state())) {
                                showAlertDialog("支付失败", "支付遇到问题，请重试");
                            }
                        } else {
                            showAlertDialog("支付失败", "支付遇到问题，请重试");
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

    /**
     * 加入课程
     *
     * @param courseID
     * @param phoneNumber
     * @param payStatus
     */
    private void joinCourse(final String courseID, final String phoneNumber, final String payStatus, final String addWay) {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                String[] result = mService.joinCourseFromAPI(courseID, phoneNumber, payStatus);
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
                        if (!StringUtils.isEmpty(result[0]) && "1".equals(result[0])) {
                            if ("pay".equals(addWay)) {
                                // 弹框
                                showAlertDialog("支付成功", "您现在就可以去查看完整课程啦");
                            } else if ("free".equals(addWay)) {
                                T.showShort(CourseDetailActivity.this, "你赚到了，免费学习哈~");
                            } else {
                                T.showShort(CourseDetailActivity.this, "加入成功，赶快开始学习吧");
                            }
                        } else {
                            if ("pay".equals(addWay)) {
                                // 加入课程失败
                                T.showShort(CourseDetailActivity.this, "支付成功，点击加入学习");
                                tvpay.setText("已支付，点击加入学习");
                            } else {
                                T.showShort(CourseDetailActivity.this, "服务器开小差了，请重试");
                            }
                        }
                        // 刷新页面数据
                        getCourseInfoFromAPI();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        T.showShort(CourseDetailActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
