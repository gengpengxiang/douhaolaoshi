package com.bj.eduteacher.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.SubjectAllAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.utils.Util;
import com.bj.eduteacher.widget.DecorationForDouke;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
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
 * 专家全部话题
 */

public class SubjectAllActivity extends BaseActivity {

    @BindView(R.id.header_img_back)
    ImageView imgBack;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    private SubjectAllAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;
    private List<ArticleInfo> mDataList = new ArrayList<>();

    private String zhuanjiaID;
    private String userPhotoPath;
    private IWXAPI api;
    private String teacherPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks_detail);
        ButterKnife.bind(this);
        api = WXAPIFactory.createWXAPI(this, MLProperties.APP_DOUHAO_TEACHER_ID);

        initToolBar();
        initView();
        initData();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        imgBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("全部话题");
    }

    @Override
    protected void initView() {
        // 初始化下拉刷新控件
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set Adatper
        mAdapter = new SubjectAllAdapter(mDataList);
        mAdapter.setOnMyItemClickListener(new SubjectAllAdapter.OnMyItemClickListener() {
            @Override
            public void onSubjectClick(View view, String tag, int position) {
                ArticleInfo item = mDataList.get(position);
                if ("Answer".equals(tag)) {
                    // 点击我要回答
                    Intent intent = new Intent(SubjectAllActivity.this, SubjectDetailActivity.class);
                    intent.putExtra("Type", "Answer");
                    intent.putExtra("SubId", item.getArticleID());
                    intent.putExtra("SubTitle", item.getTitle());
                    intent.putExtra("SubContent", item.getContent());
                    startActivity(intent);
                } else if ("Invite".equals(tag)) {
                    // 购买资源前需要登录
                    if (LoginStatusUtil.noLogin(getApplicationContext())) {
                        IntentManager.toLoginSelectActivity(SubjectAllActivity.this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
                        return;
                    }

                    inviteFriendsAnswer(item);
                } else if ("ViewReply".equals(tag)) {
                    // 查看全部回复
                    Intent intent = new Intent(SubjectAllActivity.this, SubjectDetailActivity.class);
                    intent.putExtra("Type", "ViewReply");
                    intent.putExtra("SubId", item.getArticleID());
                    intent.putExtra("SubTitle", item.getTitle());
                    intent.putExtra("SubContent", item.getContent());
                    startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DecorationForDouke(SubjectAllActivity.this, LinearLayoutManager.VERTICAL));

        // set xRefreshView
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);

        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean success) {
                LL.i("刷新数据");
                currentPage = 1;
                mXRefreshView.setAutoLoadMore(true);
                mXRefreshView.setPullLoadEnable(true);
                getMasterDataFromAPI();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                currentPage++;
                getMasterDataFromAPI();
            }
        });
    }

    @Override
    protected void initData() {
        zhuanjiaID = getIntent().getStringExtra("MasterCode");
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        userPhotoPath = PreferencesUtils.getString(SubjectAllActivity.this, MLProperties.BUNDLE_KEY_TEACHER_IMG);

        currentPage = 1;
        getMasterDataFromAPI();
    }

    @OnClick(R.id.header_ll_left)
    void actionBackClick() {
        this.finish();
    }

    private void getMasterDataFromAPI() {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                List<ArticleInfo> dataList = mService.getMasterSubjectsFromAPI(zhuanjiaID, currentPage);
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
                    public void onNext(@NonNull List<ArticleInfo> articleInfos) {
                        loadData(articleInfos);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LL.e(e);
                        cleanXRefreshView();
                        T.showShort(SubjectAllActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
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

        if (list != null && list.size() == 0) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("zhuanjia_subject_all");
        MobclickAgent.onResume(this);
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("zhuanjia_subject_all");
        MobclickAgent.onPause(this);
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
