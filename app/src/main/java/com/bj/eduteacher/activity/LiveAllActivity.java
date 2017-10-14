package com.bj.eduteacher.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.LiveAllAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.dialog.TipsAlertDialog2;
import com.bj.eduteacher.dialog.TipsAlertDialog3;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.OrderInfo;
import com.bj.eduteacher.entity.TradeInfo;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.model.CurLiveInfo;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.presenter.UserServerHelper;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.tool.ShowNameUtil;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.view.OnRecyclerItemClickListener;
import com.bj.eduteacher.widget.dialog.NotifyDialog;
import com.bj.eduteacher.widget.dialog.RadioGroupDialog;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveManager;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zz379 on 2017/3/8.
 * 全部直播列表
 */

public class LiveAllActivity extends BaseActivity {

    public static final String[] NJARRAY = new String[]{"1", "2"};
    public static final String[] NAMEARRAY = new String[]{"正在直播", "已结束直播"};
    public static final int PAGE_SIZE = 20;

    @BindView(R.id.header_img_back)
    ImageView imgBack;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_startLive)
    TextView tvStartLive;

    private LiveAllAdapter mAdapter;
    public static long lastRefreshTime;
    private List<ArticleInfo> mDataList = new ArrayList<>();

    private int currNianjiPosition = 0;
    private int currNJOffset;
    private String teacherPhoneNumber;

    private PopupWindow popPayDetail;
    private IWXAPI api;
    private String currTradeID = "";     // 当前商户订单号
    private boolean living;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_all);
        ButterKnife.bind(this);
        api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID);

        initToolBar();
        initView();
        initData();

        // 检查上次是否是异常推出直播
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        living = pref.getBoolean("living", false);
        checkLiveException();
    }

    private void initToolBar() {
        imgBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("全部直播");
    }

    private void initView() {
        // 初始化下拉刷新控件
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set Adatper
        mAdapter = new LiveAllAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                ArticleInfo item = mDataList.get(position);
                if (item.getShowType() != ArticleInfo.SHOW_TYPE_DECORATION) {
                    if (StringUtils.isEmpty(teacherPhoneNumber)) {
                        IntentManager.toLoginActivity(LiveAllActivity.this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
                        return;
                    }
                    joinLive(item);
                }
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder holder, int position) {

            }
        });

        // set xRefreshView
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);

        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                LL.i("刷新数据");
                currNianjiPosition = 0;
                mXRefreshView.setPullLoadEnable(true);
                getDoukeRefreshList();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                getDoukeList();
            }
        });
    }

    private void joinLive(ArticleInfo item) {
        if ("1".equals(item.getPreviewType())) {
            //如果是自己
            if (item.getAuthDesc().equals(MySelfInfo.getInstance().getId())) {
                returnBackRoom();
                return;
            }

            String price = item.getAgreeNumber();
            String buyType = item.getCommentNumber();

            if (!StringUtils.isEmpty(price) && !"0".equals(price) && "0".equals(buyType)) {
                initPopViewPayDetailForLive(item.getArticleID(), item.getAuthDesc(), item.getAgreeNumber());
            } else {
                MySelfInfo.getInstance().setIdStatus(Constants.MEMBER);
                MySelfInfo.getInstance().setJoinRoomWay(false);
                CurLiveInfo.setHostID(item.getAuthDesc());

                String phone;
                if (!StringUtils.isEmpty(item.getAuthDesc())) {
                    phone = item.getAuthDesc().substring(3);
                    if (!StringUtils.isEmpty(phone) && phone.length() > 10) {
                        phone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                    }
                } else {
                    phone = "";
                }
                CurLiveInfo.setHostName(ShowNameUtil.getFirstNotNullParams(item.getNickname(), item.getAuthor(), phone));
                CurLiveInfo.setHostAvator(item.getAuthImg());
                CurLiveInfo.setRoomNum(Integer.valueOf(item.getArticleID()));
                CurLiveInfo.setTitle(item.getTitle());
                CurLiveInfo.setCoverurl(item.getArticlePicture());
                checkJoinLive();
            }
        } else {
            // 已经结束的直播不进入直播间
            TipsAlertDialog2 dialog = new TipsAlertDialog2(LiveAllActivity.this);
            dialog.setContentText("本直播已结束，暂不开放直播回看，敬请期待后续开放!");
            dialog.show();
        }
    }

    private void initData() {
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        String sxbPermissions = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_SXB_PERMISSIONS, "0");
        if ("1".equals(sxbPermissions)) {
            tvStartLive.setVisibility(View.VISIBLE);
        } else {
            tvStartLive.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.tv_startLive)
    void actionStartLiveClick() {
        Intent intent = new Intent(this, PublishLiveActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.header_ll_left)
    void actionBackClick() {
        this.finish();
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
        hideLoadingDialog();
    }

    private void getDoukeRefreshList() {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                List<ArticleInfo> dataList = new ArrayList<>();
                int pageSize = PAGE_SIZE;
                for (int i = 0; i < NJARRAY.length; i++) {
                    // 记录当前加载到几年级了
                    currNianjiPosition = i;
                    List<ArticleInfo> njList = mService.getLiveListFromAPI(teacherPhoneNumber, NJARRAY[i], pageSize, 0);
                    if (njList.size() > 0) {
                        dataList.add(new ArticleInfo(NAMEARRAY[i], ArticleInfo.SHOW_TYPE_DECORATION));
                        dataList.addAll(njList);
                        pageSize -= njList.size();
                    }
                    if (pageSize > 0) {
                        continue;
                    } else {
                        // 刷新页面，当最后跳出循环时，记录当前年级的偏移量（刷新页面的时候每个年级肯定只加载第一页的数据）
                        currNJOffset = njList.size();
                        break;
                    }
                }
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {
                        cleanXRefreshView();
                        loadRefreshData(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LL.e(e);
                        cleanXRefreshView();
                        T.showShort(LiveAllActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadRefreshData(List<ArticleInfo> result) {
        mXRefreshView.stopRefresh();
        mDataList.clear();

        // 当加载到最后一个年级的时候，最后一个年级的数量小于，禁止上拉加载更多的功能
        int size = 0;
        if (currNianjiPosition == NJARRAY.length - 1) {
            for (ArticleInfo item : result) {
                mDataList.add(item);
                if (item.getShowType() != ArticleInfo.SHOW_TYPE_DECORATION) {
                    size++;
                }
            }
            if (size < PAGE_SIZE) {
                mXRefreshView.setPullLoadEnable(false);
            }
        } else {
            mDataList.addAll(result);
        }
        mAdapter.notifyDataSetChanged();

        if (mXRefreshView.getPullLoadEnable() && null == mAdapter.getCustomLoadMoreView()) {
            mAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        }
    }

    private void getDoukeList() {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                List<ArticleInfo> dataList = new ArrayList<>();
                int pageSize = PAGE_SIZE;
                for (int i = currNianjiPosition; i < NJARRAY.length; i++) {
                    // 记录当前加载到几年级了
                    List<ArticleInfo> njList = mService.getLiveListFromAPI(teacherPhoneNumber, NJARRAY[i], pageSize, currNJOffset);
                    if (njList.size() > 0) {
                        if (currNJOffset == 0) {
                            dataList.add(new ArticleInfo(NAMEARRAY[i], ArticleInfo.SHOW_TYPE_DECORATION));
                        }
                        dataList.addAll(njList);
                        pageSize -= njList.size();
                    }
                    // 保存当前年级
                    currNianjiPosition = i;

                    if (pageSize > 0) {
                        currNJOffset = 0;
                        continue;
                    } else {
                        // 当最后跳出循环时，记录当前年级的偏移量
                        currNJOffset = currNJOffset + njList.size();
                        break;
                    }
                }

                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {
                        cleanXRefreshView();
                        loadData(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LL.e(e);
                        cleanXRefreshView();
                        T.showShort(LiveAllActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadData(List<ArticleInfo> result) {
        mXRefreshView.stopLoadMore();

        // 当加载到最后一个年级的时候，最后一个年级的数量小于，禁止上拉加载更多的功能
        int size = 0;
        if (currNianjiPosition == NJARRAY.length - 1) {
            for (ArticleInfo item : result) {
                mDataList.add(item);
                if (item.getShowType() != ArticleInfo.SHOW_TYPE_DECORATION) {
                    size++;
                }
            }
            if (size < PAGE_SIZE) {
                mXRefreshView.setPullLoadEnable(false);
            }
        } else {
            mDataList.addAll(result);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("live_all");
        MobclickAgent.onResume(this);
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        // 查询订单
        if (!StringUtils.isEmpty(currTradeID)) {
            queryTheTradeStateFromAPI(currTradeID);
            return;
        }

        currNianjiPosition = 0;
        mXRefreshView.setPullLoadEnable(true);
        getDoukeRefreshList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("live_all");
        MobclickAgent.onPause(this);
    }

    /****************************** 支付 ***********************************/
    private void initPopViewPayDetailForLive(final String masterid, final String sxbroomuser, final String realPrice) {
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
                setBackgroundAlpha(LiveAllActivity.this, 1f);
            }
        });
        TextView tvRealPay = (TextView) popView.findViewById(R.id.tv_realPay);
        tvRealPay.setText("¥ " + (Double.parseDouble(realPrice)) / 100);
        TextView tvReportProtocol = (TextView) popView.findViewById(R.id.tv_report_protocol);
        tvReportProtocol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiveAllActivity.this, PayProtocolActivity.class);
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
                getTheOrderFromAPIForLive(masterid, sxbroomuser, String.valueOf(realPrice), "suixinbo");
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

    private void getTheOrderFromAPIForLive(final String masterresid, final String sxbroomuser, final String price, final String payType) {
        Observable.create(new ObservableOnSubscribe<OrderInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<OrderInfo> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                OrderInfo info = mService.getTheOrderInfoFromAPIForLive(masterresid, sxbroomuser, price, teacherPhoneNumber, payType);
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
                            T.showShort(LiveAllActivity.this, "订单生成失败");
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
            T.showShort(LiveAllActivity.this, "当前手机暂不支持该功能");
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
                                showAlertDialog("支付成功", "您现在就可以去查看完整资料啦");
                            } else if ("NOTPAY".equals(tradeInfo.getTrade_state())) {
                                showAlertDialog("支付失败", "支付遇到问题，请重试");
                            }
                        } else {
                            showAlertDialog("支付失败", "支付遇到问题，请重试");
                        }
                        // 刷新页面
                        showLoadingDialog();

                        currNianjiPosition = 0;
                        mXRefreshView.setPullLoadEnable(true);
                        getDoukeRefreshList();
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

    private void checkJoinLive() {
        if (TextUtils.isEmpty(MySelfInfo.getInstance().getGuestRole())) {
            final String[] roles = new String[]{getString(R.string.str_video_sd), getString(R.string.str_video_ld)};
            final String[] values = new String[]{Constants.SD_GUEST, Constants.LD_GUEST};

            RadioGroupDialog roleDialog = new RadioGroupDialog(this, roles);

            roleDialog.setTitle(R.string.str_video_qulity);
            roleDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    MySelfInfo.getInstance().setGuestRole(values[position]);
                    MySelfInfo.getInstance().writeToCache(LiveAllActivity.this);
                    Intent intent = new Intent(LiveAllActivity.this, LiveActivity.class);
                    startActivity(intent);
                }
            });
            roleDialog.show();
        } else {
            Intent intent = new Intent(LiveAllActivity.this, LiveActivity.class);
            startActivity(intent);
        }
    }

    private void checkLiveException() {
        if (living) {
            NotifyDialog dialog = new NotifyDialog();
            dialog.show(getString(R.string.title_living), getSupportFragmentManager(), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    returnBackRoom();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("living", false);
                    editor.apply();
                    // 需要把直播关掉, 通知server 我不开了
                    exitRoom();
                }
            });
        }
    }

    private void returnBackRoom() {
        Intent intent = new Intent(LiveAllActivity.this, LiveActivity.class);
        MySelfInfo.getInstance().setIdStatus(Constants.HOST);
        MySelfInfo.getInstance().setJoinRoomWay(true);
        CurLiveInfo.setHostID(MySelfInfo.getInstance().getId());
        CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
        String sxbTitle = PreferencesUtils.getString(LiveAllActivity.this, MLProperties.PREFER_KEY_USER_SXB_Title, "");
        String sxbPic = PreferencesUtils.getString(LiveAllActivity.this, MLProperties.PREFER_KEY_USER_SXB_Picture, "");
        CurLiveInfo.setTitle(sxbTitle);
        if (!StringUtils.isEmpty(sxbPic)) {
            CurLiveInfo.setCoverurl(sxbPic.substring(sxbPic.lastIndexOf("/") + 1));
        }
        intent.putExtra("HostComeBack", true);
        startActivity(intent);
    }

    private void exitRoom() {
        ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        ILVLiveManager.getInstance().quitRoom(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserServerHelper.getInstance().notifyCloseLive();
                UserServerHelper.getInstance().reportMe(MySelfInfo.getInstance().getIdStatus(), 1);//通知server 我下线了
            }
        }).start();
    }
}
