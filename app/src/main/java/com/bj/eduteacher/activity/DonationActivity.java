package com.bj.eduteacher.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.OrderInfo;
import com.bj.eduteacher.entity.TradeInfo;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.LeakedUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.utils.Util;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

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
 * Created by zz379 on 2017/6/13.
 * 赞助页面
 */

public class DonationActivity extends BaseActivity {

    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.header_img_back)
    ImageView imgBack;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.edt_moneyNumber)
    EditText edtMoney;
    @BindView(R.id.tv_support)
    TextView tvSupport;
    @BindView(R.id.tv_donationProtocol)
    TextView tvProtocol;
    @BindView(R.id.iv_supportBG)
    SimpleDraweeView ivSupportBg;

    private IWXAPI api;
    private int currMoneyNumber;    // 当前支持的金钱
    private String currTradeID = "";     // 当前商户订单号
    private String userPhoneNumber;
    private AlertDialog shareDialog;

    // 分享
    protected ScrollView mScrollView;
    private String userPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation);
        ButterKnife.bind(this);
        api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID);
        // 初始化页面
        initToolBar();
        initView();
        initData();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("支持下我们");
        imgBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initView() {
        userPhotoPath = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_TEACHER_IMG, "");
        LL.i("分享用户的头像：" + userPhotoPath);
        userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

        edtMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null || s.length() == 0) {
                    tvSupport.setEnabled(false);
                } else {
                    int money = Integer.valueOf(s.toString());
                    if (money == 0) {
                        tvSupport.setEnabled(false);
                    } else {
                        tvSupport.setEnabled(true);
                    }

                }
            }
        });
    }

    @Override
    protected void initData() {
        edtMoney.post(new Runnable() {
            @Override
            public void run() {
                edtMoney.setSelection(edtMoney.getText().toString().length());
            }
        });
    }

    @Override
    protected void onDestroy() {
        LeakedUtils.fixTextLineCacheLeak();
        super.onDestroy();
    }

    @OnClick(R.id.header_ll_left)
    void actionBackClick() {
        this.finish();
    }

    @OnClick(R.id.rl_inputMoney)
    void actionRlInputMoneyClick() {
        KeyBoardUtils.openKeybord(edtMoney, this);
    }

    @OnClick(R.id.tv_support)
    void actionSupportClick() {
        // 设置按钮不可重复点击
        tvSupport.setEnabled(false);
        edtMoney.setEnabled(false);

        KeyBoardUtils.closeKeybord(this.getCurrentFocus().getWindowToken(), this);
        currMoneyNumber = Integer.valueOf(edtMoney.getText().toString());
        if (currMoneyNumber > 0) {
            getTheOrderFromAPI(String.valueOf(currMoneyNumber * 100));
//            getTheOrderFromAPI(String.valueOf(currMoneyNumber));
        }
    }

    @OnClick(R.id.tv_donationProtocol)
    void actionProtocolClick() {
        KeyBoardUtils.closeKeybord(this.getCurrentFocus().getWindowToken(), this);
        Intent intent = new Intent(this, DonationProtocolActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.ll_content)
    void actionBlankClick() {   // 空白处点击,隐藏输入法
        KeyBoardUtils.closeKeybord(this.getCurrentFocus().getWindowToken(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("donate");
        MobclickAgent.onPause(this);
        KeyBoardUtils.closeKeybord(this.getCurrentFocus().getWindowToken(), this);
    }

    private void showShareDialog() {
        View popView = LayoutInflater.from(this).inflate(R.layout.alert_share_donation, null);
        SimpleDraweeView ivUserPhoto = (SimpleDraweeView) popView.findViewById(R.id.img_kidPhoto);
        ivUserPhoto.setImageURI(userPhotoPath);
        TextView tvThanksMoney = (TextView) popView.findViewById(R.id.tv_thanksDonation);
        tvThanksMoney.setText("“感谢您为让中国孩子更快乐科学的学习贡献了" + currMoneyNumber + "元钱”");
        mScrollView = (ScrollView) popView.findViewById(R.id.mScrollView);

        shareDialog = new AlertDialog.Builder(this).create();
        shareDialog.setCanceledOnTouchOutside(false);
        shareDialog.show();
        shareDialog.getWindow().setContentView(popView);
        shareDialog.getWindow().findViewById(R.id.iv_shareSession).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToSession();
            }
        });
        shareDialog.getWindow().findViewById(R.id.iv_shareTimeline).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToTimeline();
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
    public void shareToSession() {
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
    public void shareToTimeline() {
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

    /**
     * 生成订单
     */
    private void getTheOrderFromAPI(final String price) {
        Observable.create(new ObservableOnSubscribe<OrderInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<OrderInfo> emitter) throws Exception {
                LmsDataService mService = new LmsDataService();
                OrderInfo info = mService.getTheOrderInfoFromAPI(price, userPhoneNumber, "zanzhu");
                emitter.onNext(info);
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
                            T.showShort(DonationActivity.this, "订单生成失败");
                        }
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                        tvSupport.setEnabled(true);
                        edtMoney.setEnabled(true);
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
        if (api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
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
        MobclickAgent.onPageStart("donate");
        MobclickAgent.onResume(this);
        if (!StringUtils.isEmpty(currTradeID)) {
            queryTheTradeStateFromAPI(currTradeID);
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
                        tvSupport.setEnabled(true);
                        edtMoney.setEnabled(true);
                        if ("SUCCESS".equals(tradeInfo.getResult_code())) {
                            if ("SUCCESS".equals(tradeInfo.getTrade_state())) {
                                showShareDialog();
                                T.showShort(DonationActivity.this, "支付成功");
                            } else if ("NOTPAY".equals(tradeInfo.getTrade_state())) {
                                T.showShort(DonationActivity.this, "未完成支付");
                            }
                        } else {
                            T.showShort(DonationActivity.this, "支付失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        tvSupport.setEnabled(true);
                        edtMoney.setEnabled(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
