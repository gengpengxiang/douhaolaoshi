package com.bj.eduteacher.activity;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.SubjectDetailAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.utils.Util;
import com.facebook.drawee.view.SimpleDraweeView;
import com.jakewharton.rxbinding2.view.RxView;
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

/**
 * Created by zz379 on 2017/8/3.
 * 专家黑板报功能-话题详情页面
 */
public class SubjectDetailActivity extends BaseActivity {

    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.tv_send)
    TextView tvSend;

    private SubjectDetailAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;

    private List<ArticleInfo> mDataList = new ArrayList<>();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douke_comment);
        ButterKnife.bind(this);
        api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID);

        // 初始化页面
        initToolBar();
        initView();
        initData();
    }

    private void initToolBar() {
        userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID);
        subjectID = getIntent().getStringExtra("SubId");
        viewType = getIntent().getStringExtra("Type");
        subTitle = getIntent().getStringExtra("SubTitle");
        subContent = getIntent().getStringExtra("SubContent");

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

    private void initView() {
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        // set Adatper
        mAdapter = new SubjectDetailAdapter(mDataList);
        headerView = mAdapter.setHeaderView(R.layout.recycler_header_subject_detail, mRecyclerView);
        initHeaderView();

        mRecyclerView.setAdapter(mAdapter);

        // set xRefreshView
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);
        // mXRefreshView.setEmptyView(R.layout.recycler_item_comment_empty);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isRefresh) {
                LL.i("刷新数据");
                currentPage = 1;
                mXRefreshView.setPullLoadEnable(true);
                getDoukeCommentFromAPI(currentPage);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                currentPage++;
                getDoukeCommentFromAPI(currentPage);
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

    private void initData() {
        userPhotoPath = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_TEACHER_IMG);

        currentPage = 1;
        mXRefreshView.setPullLoadEnable(true);
        getDoukeCommentFromAPI(currentPage);

        if ("Answer".equals(viewType)) {
            edtContent.post(new Runnable() {
                @Override
                public void run() {
                    edtContent.requestFocus();
                    KeyBoardUtils.openKeybord(edtContent, SubjectDetailActivity.this);
                }
            });
        } else {

        }
    }

    private void loadData(List<ArticleInfo> list) {
        lastRefreshTime = mXRefreshView.getLastRefreshTime();
        if (mXRefreshView.mPullRefreshing) {
            mDataList.clear();
            mXRefreshView.stopRefresh();
        }
        if (list == null || list.size() < 10) {
            mXRefreshView.setPullLoadEnable(false);
        }
        mXRefreshView.stopLoadMore();
        // 更新数据
        mDataList.addAll(list);
        mAdapter.notifyDataSetChanged();
        if (mDataList.size() >= 10 && null == mAdapter.getCustomLoadMoreView()) {
            mAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        }
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    @OnClick(R.id.tv_send)
    void actionSendClick() {
        String content = edtContent.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            T.showShort(this, "评论内容不能为空！");
            return;
        }
        tvSend.setEnabled(false);
        edtContent.setText("");
        KeyBoardUtils.closeKeybord(this.getCurrentFocus().getWindowToken(), this);

        sendCommentContent(content);
        // mDataList.add(0, new ArticleInfo());
        // mAdapter.notifyDataSetChanged();
    }

    private void getDoukeCommentFromAPI(final int currentPage) {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                List<ArticleInfo> dataList = mService.getMasterSubjectReplyListFromAPI(subjectID, currentPage);
                e.onNext(dataList);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<ArticleInfo> classItemInfos) {
                        loadData(classItemInfos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        cleanXRefreshView();
                        T.showShort(SubjectDetailActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void sendCommentContent(final String content) {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                String[] result = mService.postSubjectReplyFromAPI(subjectID, userPhoneNumber, content);
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
                        tvSend.setEnabled(true);
                        edtContent.clearFocus();

                        if (!StringUtils.isEmpty(result[0]) && "1".equals(result[0])) {
                            // 发布成功
                            T.showShort(SubjectDetailActivity.this, "评论成功");
                            mXRefreshView.startRefresh();
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("subjectDetail");
        MobclickAgent.onResume(this);
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
}