package com.bj.eduteacher.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.SubTopicCommentAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.community.main.adapter.SpacesItemDecoration;
import com.bj.eduteacher.entity.SubTopicCommentInfo;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.utils.Util;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding2.view.RxView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
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
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.MyApplication.getContext;
import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

/**
 * Created by zz379 on 2017/8/3.
 * 专家黑板报功能-话题详情页面
 */
public class SubjectDetailActivity extends BaseActivity {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.tv_send)
    TextView tvSend;
    @BindView(R.id.mSmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private SubTopicCommentAdapter adapter;
    private int currentPage = 1;
    public static long lastRefreshTime;

    private List<SubTopicCommentInfo.DataBean.ReplaydataBean> list = new ArrayList<>();
    private String subjectID;
    private String userPhoneNumber;
    private LinearLayoutManager layoutManager;
    private View headerView;

    private String viewType;
    private String subTitle, subContent;
    private TextView tvSubTitle;
    private TextView tvSubContent;
    private TextView tvSubInvite;

    private IWXAPI api;
    private String userPhotoPath;
    private Disposable shareDisposable;
    private String unionid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_comment);
        ButterKnife.bind(this);
        api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID);

        // 初始化页面
        initToolBar();
        initView();

        getCommentList(currentPage, "refresh");
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        subjectID = getIntent().getStringExtra("SubId");
        viewType = getIntent().getStringExtra("Type");
        subTitle = getIntent().getStringExtra("SubTitle");
        subContent = getIntent().getStringExtra("SubContent");
        Log.e("话题id", subjectID);

        TextView tvTitle = (TextView) this.findViewById(R.id.header_tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("话题详情");

        LinearLayout llHeaderLeft = (LinearLayout) this.findViewById(R.id.header_ll_left);
        llHeaderLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ImageView imgBack = (ImageView) this.findViewById(R.id.header_img_back);
        imgBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initView() {
        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);


        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        // set Adatper
        adapter = new SubTopicCommentAdapter(R.layout.recycler_item_subject_detail, list);
        headerView = getLayoutInflater().inflate(R.layout.recycler_header_subject_detail, null);
        adapter.addHeaderView(headerView);
        initHeaderView();
        adapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        currentPage++;
                        getCommentList(currentPage, "loadmore");
                    }
                }, 500);
            }
        }, mRecyclerView);
        mRecyclerView.setAdapter(adapter);

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                currentPage = 1;
                getCommentList(currentPage, "refresh");
                mSmartRefreshLayout.finishRefresh(500);
            }
        });

        mSmartRefreshLayout.setEnableOverScrollDrag(false);

    }

    private void getCommentList(final int i, final String type) {
        Observable.create(new ObservableOnSubscribe<List<SubTopicCommentInfo.DataBean.ReplaydataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<SubTopicCommentInfo.DataBean.ReplaydataBean>> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/jsmaster/topicreply")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("topicid", subjectID)
                        .params("limit", "10")
                        .params("offset", String.valueOf((i - 1) * 10))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                SubTopicCommentInfo info = JSON.parseObject(str, new TypeReference<SubTopicCommentInfo>() {
                                });
                                e.onNext(info.getData().getReplaydata());
                                e.onComplete();
                            }
                        });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<SubTopicCommentInfo.DataBean.ReplaydataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<SubTopicCommentInfo.DataBean.ReplaydataBean> replaydataBeans) {

                        if (type.equals("refresh")) {
                            list.clear();
                        }

                        list.addAll(replaydataBeans);
                        adapter.setNewData(list);

                        adapter.loadMoreComplete();

                        if (replaydataBeans.size() < 10) {
                            adapter.loadMoreEnd(true);
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

    @Override
    public void onBackPressed() {
        KeyBoardUtils.closeKeybord(edtContent, this);
        super.onBackPressed();
    }

    private void initHeaderView() {
        tvSubTitle = (TextView) headerView.findViewById(R.id.tv_subjectName);
        tvSubContent = (TextView) headerView.findViewById(R.id.tv_subjectContent);
        tvSubInvite = (TextView) headerView.findViewById(R.id.tv_invite);

        tvSubTitle.setText(subTitle);
        tvSubContent.setText(subContent);

        shareDisposable = RxView.clicks(tvSubInvite)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        if (LoginStatusUtil.noLogin(getApplicationContext())) {
                            IntentManager.toLoginSelectActivity(SubjectDetailActivity.this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
                            return;
                        }
                        inviteFriendsAnswer(subTitle);
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

    @OnClick(R.id.tv_send)
    void actionSendClick() {
        // 评论之前需要先进行登录
        if (LoginStatusUtil.noLogin(this)) {
            IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
            return;
        }

        String content = edtContent.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            T.showShort(this, "评论内容不能为空！");
            return;
        }
        tvSend.setEnabled(false);
        edtContent.setText("");
        KeyBoardUtils.closeKeybord(this.getCurrentFocus().getWindowToken(), this);

        sendCommentContent(content, unionid);

    }

    private void sendCommentContent(final String content, final String unionid) {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                String[] result = mService.postSubjectReplyFromAPI(subjectID, userPhoneNumber, content, unionid);
                e.onNext(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String[] result) {

                        Log.e("次数", "true");
                        tvSend.setEnabled(true);
                        edtContent.clearFocus();

                        if (!StringUtils.isEmpty(result[0]) && "1".equals(result[0])) {
                            // 发布成功
                            //T.showShort(SubjectDetailActivity.this, "评论成功");
                            processSoftKeyBoard(false);
                            list.clear();
                            getCommentList(1, "refresh");


                        } else if (!StringUtils.isEmpty(result[0]) && "3".equals(result[0])) {
                            T.showShort(SubjectDetailActivity.this, "您已被拉黑，暂时不能评论");
                        } else {
                            // 发布失败
                            T.showShort(SubjectDetailActivity.this, "发布评论失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        tvSend.setEnabled(true);
                        T.showShort(SubjectDetailActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void processSoftKeyBoard(boolean isShow) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isShow) {
            imm.showSoftInput(edtContent, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(edtContent.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("subjectDetail");
        MobclickAgent.onResume(this);
        userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyBoardUtils.closeKeybord(edtContent, this);
        edtContent.clearFocus();
        MobclickAgent.onPageEnd("subjectDetail");
        MobclickAgent.onPause(this);
    }

    /**
     * 邀请好友回答
     *
     * @param subTitle
     */
    private void inviteFriendsAnswer(String subTitle) {
        userPhotoPath = PreferencesUtils.getString(SubjectDetailActivity.this, MLProperties.BUNDLE_KEY_TEACHER_IMG);
        View popView = LayoutInflater.from(this).inflate(R.layout.alert_share_subject, null);
        SimpleDraweeView ivUserPhoto = (SimpleDraweeView) popView.findViewById(R.id.img_kidPhoto);
        // TextView tvAuthorName = (TextView) popView.findViewById(R.id.tv_authorName);
        TextView tvAuthorDesc = (TextView) popView.findViewById(R.id.tv_authorDesc);
        ivUserPhoto.setImageURI(userPhotoPath);
        // tvAuthorName.setText(zhuanjiaName);
        tvAuthorDesc.setText(subTitle);
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