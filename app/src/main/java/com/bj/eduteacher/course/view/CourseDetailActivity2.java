package com.bj.eduteacher.course.view;

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
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.PayProtocolActivity;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.course.fragment.detail.CourseInfo;
import com.bj.eduteacher.course.fragment.detail.DetailFragment;
import com.bj.eduteacher.course.fragment.discuss.DiscussFragment;
import com.bj.eduteacher.course.fragment.discuss.publish.DiscussPublishActivity;
import com.bj.eduteacher.course.fragment.study.StudyFragment;
import com.bj.eduteacher.course.presenter.CourseInfoPresenter;
import com.bj.eduteacher.dialog.TipsAlertDialog3;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.entity.OrderInfo;
import com.bj.eduteacher.entity.TradeInfo;
import com.bj.eduteacher.login.view.LoginActivity;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.manager.ShareHelp;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StatusBarCompat;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.utils.Util;
import com.bj.eduteacher.videoplayer.view.PlayerActivity;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;
import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

public class CourseDetailActivity2 extends BaseActivity implements IViewCourseInfo {

    @BindView(R.id.header_bg)
    ImageView headerBg;
    @BindView(R.id.header_title)
    TextView headerTitle;
    @BindView(R.id.header_keshi)
    TextView headerKeshi;
    @BindView(R.id.header_peopleNum)
    TextView headerPeopleNum;
    @BindView(R.id.spaceView)
    View spaceView;
    @BindView(R.id.toolbar_back)
    ImageView toolbarBack;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.coordinatorlayout)
    CoordinatorLayout coordinatorlayout;
    @BindView(R.id.mSmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.bt_yantao_edit)
    Button btYantaoEdit;
    @BindView(R.id.tv_pay)
    TextView tvPay;
    @BindView(R.id.header_bg0)
    SimpleDraweeView headerBg0;
    private Unbinder unbinder;
    private int h;
    private String courseID;
    private String coursePicture;
    private String courseTitle;
    private String courseLearnNum;
    private String courseZhengshu;
    private String courseShuoming;
    private String courseResNum;

    private CourseInfoPresenter presenter;
    private String unionid;
    private String phoneNumber;
    private String courseBuyStatus;
    private PopupWindow popPayDetail;
    private String coursePrice;
    private IWXAPI api;
    private String currTradeID = "";     // 当前商户订单号
    public static String price;
    private String courseJiakeStatus;

    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail2);
        unbinder = ButterKnife.bind(this);
        api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID);
        StatusBarCompat.fullScreen(this);
        h = StatusBarCompat.getStatusBarHeight(this);
        //改变底部导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }

        presenter = new CourseInfoPresenter(this, this);
        initDatas();
        initViews();
        //根据课程id获取课程的基本信息，包括购买状态、用户加入课程的状态
        //presenter.getCourseInfo(phoneNumber, unionid, courseID);
    }

    private void initDatas() {
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        phoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

        Bundle args = getIntent().getExtras();
        courseID = args.getString("CourseID", "");
        coursePicture = args.getString("CoursePicture", "");
        courseTitle = args.getString("CourseTitle", "");
        courseLearnNum = args.getString("CourseLearnNum", "");
        courseResNum = args.getString("CourseResNum", "");
        courseZhengshu = args.getString("CourseZhengshu", "");
        courseShuoming = args.getString("CourseShuoming", "");

        courseBuyStatus = args.getString("CourseBuyStatus", "");
        coursePrice = args.getString("CoursePrice", "");
        courseJiakeStatus = args.getString("CourseJiakeStatus", "");

        headerTitle.setText(courseTitle);
        headerKeshi.setText(courseResNum + "课时");
        headerPeopleNum.setText(courseLearnNum + "人参加");
        //Glide.with(this).load(coursePicture).into(headerBg);
        headerBg0.setImageURI(coursePicture);

    }

    private void initViews() {

        AutoUtils.auto(collapsingToolbar);
        AutoUtils.auto(appbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        viewpager.setOffscreenPageLimit(3);
        toolbar.setTitle("");
        setupViewPager(viewpager);
        tabs.setupWithViewPager(viewpager);
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.btn_red));
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int offset = Math.abs(verticalOffset);
                if ((appbar.getHeight() - offset) == toolbar.getHeight()) {
                    collapsingToolbar.setContentScrimColor(Color.parseColor("#FE5433"));
                    toolbarTitle.setText(courseTitle);
                    toolbarTitle.setVisibility(View.VISIBLE);
                } else {
                    collapsingToolbar.setContentScrimColor(Color.parseColor("#01000000"));
                    toolbarTitle.setText("");
                    toolbarTitle.setVisibility(View.GONE);
                }
            }
        });

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                int currenItem = viewpager.getCurrentItem();
                EventBus.getDefault().post(new MsgEvent("refreshpage", currenItem));
                mSmartRefreshLayout.finishRefresh(500);
            }
        });
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if(courseJiakeStatus.equals("1")){
                if (position == 2) {
                    if (positionOffset != 0) {
                        btYantaoEdit.setVisibility(View.GONE);
                    } else {
                        btYantaoEdit.setVisibility(View.VISIBLE);
                    }
                } else {
                    btYantaoEdit.setVisibility(View.GONE);
                    // }
                }
            }

            @Override
            public void onPageSelected(int position) {
//                if (courseJiakeStatus.equals("1")) {
                if (position == 2) {
                    btYantaoEdit.setVisibility(View.VISIBLE);
                } else {
                    btYantaoEdit.setVisibility(View.GONE);
                }
                //}
                if (position == 0) {
                    EventBus.getDefault().post(new MsgEvent("scrolltotop"));
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewpager.setOffscreenPageLimit(3);

        if (courseJiakeStatus.equals("1")) {
            viewpager.setCurrentItem(1);
        } else {
            viewpager.setCurrentItem(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        phoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        getCourseInfo();

        if (!StringUtils.isEmpty(currTradeID)) {
            queryTheTradeStateFromAPI(currTradeID);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        presenter.onDestory();
    }

    private void setupViewPager(ViewPager viewPager) {
        InfoAdapter adapter = new InfoAdapter(getSupportFragmentManager());
        adapter.addFragment(new DetailFragment(), "详情");
        adapter.addFragment(new StudyFragment(), "学习");
        adapter.addFragment(new DiscussFragment(), "研讨");
        viewPager.setAdapter(adapter);
    }

    @OnClick({R.id.toolbar_back, R.id.tv_share, R.id.tv_pay, R.id.bt_yantao_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.tv_share:
                showShareDialog();
                break;
            case R.id.tv_pay:
                pay();
                break;
            case R.id.bt_yantao_edit:
                if (LoginStatusUtil.noLogin(this)) {
                    IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                } else {
                    if (courseJiakeStatus.equals("1")) {
                        Intent intent = new Intent(this, DiscussPublishActivity.class);
                        intent.putExtra("id", courseID);
                        startActivity(intent);
                    } else {
                        T.showShort(this, "加入课程后才可发布");
                    }
                }

                break;
        }
    }

    private void pay() {
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        phoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        if (StringUtils.isEmpty(courseJiakeStatus) || courseJiakeStatus.equals("0")) {
            if (LoginStatusUtil.noLogin(this)) {
                IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
                return;
            }
            if (PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID) == null
                    || PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID).equals("0")
                    || StringUtils.isEmpty(PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID))) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("laiyuan", "bind_first");
                startActivity(intent);
                return;
            }

            if (StringUtils.isEmpty(coursePrice) || "0".equals(coursePrice)) {
                // 调用加入课程的接口
                joinCourse(courseID, phoneNumber, "1", "free");
                //T.showShort(this, "调用加入课程的接口免费");
            } else {
                if ("0".equals(courseBuyStatus)) {
                    initPopViewPayDetail(courseID, coursePrice);
                } else {
                    // 已支付，但是加入课程时出现问题，直接加入课程
                    if (StringUtils.isEmpty(courseJiakeStatus) || "0".equals(courseJiakeStatus)) {
                        joinCourse(courseID, phoneNumber, "1", "reAdd");
                        T.showShort(this, "已支付，但是加入课程时出现问题，直接加入课程");
                    } else {
                        T.showShort(this, "已加入课程");
                    }
                }
            }
        } else {
//            if (StringUtils.isEmpty(coursePrice) || coursePrice.equals("0")) {
//                T.showShort(this, "已加入，快去学习吧~");
//            } else {
//                T.showShort(this, "已购买，快去学习吧~");
//
//            }
            String beforeResid = PreferencesUtils.getString(this, "beforeResid", "");
            String beforeResurl = PreferencesUtils.getString(this, "beforeResurl", "");
            String beforeResCurrentTime = PreferencesUtils.getString(this, "beforeResCurrentTime", "");
            String beforeResjindu = PreferencesUtils.getString(this, "beforeResjindu", "");

            //EventBus.getDefault().post("startPlayVideo");
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("type", "StudyFragment");
            intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, beforeResid);
            intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, "");
            intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, beforeResurl);
            intent.putExtra("kechengid", courseID);
            intent.putExtra("currentTime", beforeResCurrentTime);
            intent.putExtra("jiakestatus", courseJiakeStatus);
            if (courseJiakeStatus.equals("1")) {
                intent.putExtra("jindu", beforeResjindu);
            }
            startActivity(intent);

        }

    }

    @Override
    public void getCourseInfoSuccess(CourseInfo courseInfo) {
        headerTitle.setText(courseInfo.getData().getTitle());
        headerKeshi.setText(courseInfo.getData().getKeshi() + "课时");
        headerPeopleNum.setText(courseInfo.getData().getStudentnum() + "人参加");
        Glide.with(this).load(BASE_RESOURCE_URL + courseInfo.getData().getImg()).into(headerBg);

        coursePicture = BASE_RESOURCE_URL + courseInfo.getData().getImg();

        courseBuyStatus = courseInfo.getData().getBuystatus();
        coursePrice = courseInfo.getData().getPrice();
        courseJiakeStatus = courseInfo.getData().getJiakestatus();


    }

    @Override
    public void getCourseInfoFail() {

    }

    private void showShareDialog() {
        View popViewShare = LayoutInflater.from(this).inflate(R.layout.alert_share_course, null);
        SimpleDraweeView ivPicture = (SimpleDraweeView) popViewShare.findViewById(R.id.iv_coursePicture);
        TextView tvTitle = (TextView) popViewShare.findViewById(R.id.tv_courseTitle);
        ivPicture.setImageURI(coursePicture);
        tvTitle.setText(courseTitle);
        //ShareHelp.getInstance().showShareDialog(this, popViewShare);
        final ScrollView mScrollView = (ScrollView) popViewShare.findViewById(R.id.mScrollView);
        final AlertDialog shareDialog = new AlertDialog.Builder(this).create();
        shareDialog.setCanceledOnTouchOutside(false);
        shareDialog.show();
        shareDialog.getWindow().setContentView(popViewShare);
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

    class InfoAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public InfoAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

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
                setBackgroundAlpha(CourseDetailActivity2.this, 1f);
            }
        });
        TextView tvRealPay = (TextView) popView.findViewById(R.id.tv_realPay);
        tvRealPay.setText("¥ " + (Double.parseDouble(realPrice)) / 100);
        TextView tvReportProtocol = (TextView) popView.findViewById(R.id.tv_report_protocol);
        tvReportProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseDetailActivity2.this, PayProtocolActivity.class);
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
            }
        });
        // 显示页面
        showPopViewPayDetail();
    }

    private void showPopViewPayDetail() {
        if (popPayDetail != null && !popPayDetail.isShowing()) {
            setBackgroundAlpha(this, 0.5f);
            popPayDetail.showAtLocation(viewpager, Gravity.BOTTOM, 0, popPayDetail.getHeight());
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
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        phoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        Observable.create(new ObservableOnSubscribe<OrderInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<OrderInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                OrderInfo info = mService.getTheOrderInfoFromAPIForCourse(masterresid, price, phoneNumber, unionid, payType);
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

                        Log.e("订单信息", orderInfo.toString());
                        // 获取订单成功后 发起微信支付
                        if ("SUCCESS".equals(orderInfo.getResult_code())
                                && "SUCCESS".equals(orderInfo.getReturn_code())
                                && "OK".equals(orderInfo.getReturn_msg())) {
                            startPay(orderInfo);
                        } else {
                            // 获取订单失败
                            T.showShort(CourseDetailActivity2.this, "订单生成失败");
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
                                //showAlertDialog("支付成功", "您现在就可以去查看完整资料啦");

                                //add
                                joinCourse(courseID, phoneNumber, "1", "pay");

                            } else if ("NOTPAY".equals(tradeInfo.getTrade_state())) {
                                showAlertDialog("支付失败", "支付遇到问题，请重试");
                            }
                        } else {
                            showAlertDialog("支付失败", "支付遇到问题，请重试");
                        }
                        // 刷新页面
                        //showLoadingDialog();
                        //getRefreshDataList(zhuanjiaID);


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
     * 加入课程
     *
     * @param courseID
     * @param phoneNumber
     * @param payStatus
     */
    private void joinCourse(final String courseID, final String phoneNumber, final String payStatus, final String addWay) {
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        //phoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                String[] result = mService.joinCourseFromAPI(courseID, phoneNumber, unionid, payStatus);
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
                                T.showShort(CourseDetailActivity2.this, "你赚到了，免费学习哈~");
                            } else {
                                T.showShort(CourseDetailActivity2.this, "加入成功，赶快开始学习吧");
                            }
                            EventBus.getDefault().post(new MsgEvent("courseStudyBuySuccess", "1"));

                        } else {
                            if ("pay".equals(addWay)) {
                                // 加入课程失败
                                T.showShort(CourseDetailActivity2.this, "支付成功，点击加入学习");
                                tvPay.setText("已支付，点击加入学习");
                            } else {
                                T.showShort(CourseDetailActivity2.this, "服务器开小差了，请重试");
                            }
                        }
                        // 刷新页面数据
                        //getCourseInfoFromAPI();
                        presenter.getCourseInfo(phoneNumber, unionid, courseID);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        T.showShort(CourseDetailActivity2.this, "服务器开小差了，请重试");
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

    private void getCourseInfo() {
        Observable.create(new ObservableOnSubscribe<CourseInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<CourseInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/kecheng")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("phone", phoneNumber)
                        .params("kechengid", courseID)
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("课程信息", str);
                                CourseInfo courseInfo = JSON.parseObject(str, new TypeReference<CourseInfo>() {
                                });
                                e.onNext(courseInfo);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CourseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CourseInfo courseInfo) {

                        courseJiakeStatus = courseInfo.getData().getJiakestatus();
                        courseBuyStatus = courseInfo.getData().getBuystatus();

                        headerTitle.setText(courseInfo.getData().getTitle());
                        headerKeshi.setText(courseInfo.getData().getKeshi() + "课时");
                        headerPeopleNum.setText(courseInfo.getData().getStudentnum() + "人参加");
                        //Glide.with(CourseDetailActivity2.this).load(BASE_RESOURCE_URL + courseInfo.getData().getImg()).into(headerBg);

                        headerBg0.setImageURI(BASE_RESOURCE_URL + courseInfo.getData().getImg());

                        coursePicture = BASE_RESOURCE_URL + courseInfo.getData().getImg();



                        if(count==0){
                            if (courseJiakeStatus.equals("1")) {
                                viewpager.setCurrentItem(1);
                            } else {
                                viewpager.setCurrentItem(0);
                            }
                        }
                        count++;
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
                // msg.thumbData = WXUtil.bmpToByteArray(thumbBmp, true);
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
}
