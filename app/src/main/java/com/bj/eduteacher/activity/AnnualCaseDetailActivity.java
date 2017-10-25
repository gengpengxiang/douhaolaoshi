package com.bj.eduteacher.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.AnliDetailAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.manager.ShareHelp;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.facebook.drawee.view.SimpleDraweeView;
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
 * 案例详情页面
 */

public class AnnualCaseDetailActivity extends BaseActivity {

    private static final String ARTICLE_AGREE_TYPE_YES = "add";
    private static final String ARTICLE_AGREE_TYPE_NO = "del";
    private static final String ARTICLE_AGREE_TYPE_SEARCH = "";

    @BindView(R.id.header_img_back)
    ImageView imgBack;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_pay)
    TextView tvPay;

    private AnliDetailAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;
    private List<ArticleInfo> mDataList = new ArrayList<>();
    private String anliTitle;
    private String anliID;
    private String anliTPNum;
    private String anliTPStatus;
    private String anliAuthor, authorDiqu;
    private String bannerPath;

    private String teacherPhoneNumber;

    private long currMillis = 0;
    LmsDataService mService = new LmsDataService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual_case_detail);
        ButterKnife.bind(this);

        initToolBar();
        initView();
    }

    private void initToolBar() {
        anliID = getIntent().getExtras().getString("ID", "");
        anliTitle = getIntent().getExtras().getString("Title", "");
        anliAuthor = getIntent().getExtras().getString("Author", "");
        authorDiqu = getIntent().getExtras().getString("Diqu", "");
        anliTPNum = getIntent().getExtras().getString("AnliTPNum", "");
        anliTPStatus = getIntent().getExtras().getString("AnliTPStatus", "");
        bannerPath = getIntent().getExtras().getString("HuodongBanner", "");

        tvTitle.setText(anliTitle);
        if ("0".equals(anliTPStatus)) {
            tvPay.setText("投票（" + anliTPNum + "）");
            tvPay.setBackgroundColor(Color.parseColor("#FC6345"));
        } else {
            tvPay.setText("已投票（" + anliTPNum + "）");
            tvPay.setBackgroundColor(Color.parseColor("#A3A3A3"));
        }
    }

    private void initView() {
        // mXRefreshView.setBackgroundResource(R.color.bg_gray);
        mRecyclerView.setBackground(null);

        // 初始化下拉刷新控件
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set Adatper
        mAdapter = new AnliDetailAdapter(mDataList);
        mAdapter.setOnMyItemClickListener(new AnliDetailAdapter.OnMyItemClickListener() {
            @Override
            public void onZhuanjiaClick(View view, int position) {
                ArticleInfo item = mDataList.get(position);

                MobclickAgent.onEvent(AnnualCaseDetailActivity.this, "doc_look");
                String resID = mDataList.get(position).getArticleID();
                String resName = mDataList.get(position).getTitle();
                String previewUrl = mDataList.get(position).getArticlePath();
                String downloadUrl = mDataList.get(position).getArticlePicture();
                String resType = item.getPreviewType();  // 目前先根据这个类型来判断是否是视频
                if ("2".equals(resType)) {
                    Intent intent = new Intent(AnnualCaseDetailActivity.this, ResPlayActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(AnnualCaseDetailActivity.this, ResReviewActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL, downloadUrl);
                    startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        // set xRefreshView
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(false);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(false);

        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean success) {
                LL.i("刷新数据");
                currentPage = 1;
                mXRefreshView.setAutoLoadMore(true);
                mXRefreshView.setPullLoadEnable(true);
                getMasterDataFromAPI();
            }
        });
    }

    private void initData() {
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

        currentPage = 1;
        getMasterDataFromAPI();
        if (!StringUtils.isEmpty(teacherPhoneNumber)) {
            searchToupiaoStatus();
        }
    }

    @OnClick(R.id.header_ll_left)
    void actionBackClick() {
        this.finish();
    }

    private void getMasterDataFromAPI() {
        if (!NetUtils.isConnected(this)) {
            T.showShort(this, "无法连接到网络，请检查您的网络设置");
            cleanXRefreshView();
            return;
        }
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                // SystemClock.sleep(1000);
                List<ArticleInfo> dataList = mService.getAnliResFromAPI(anliID, teacherPhoneNumber, currentPage);
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
                        hideLoadingDialog();
                        loadData(articleInfos);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LL.e(e);
                        cleanXRefreshView();
                        hideLoadingDialog();
                        T.showShort(AnnualCaseDetailActivity.this, "服务器开小差了，请重试");
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
        mXRefreshView.stopRefresh();
        mDataList.clear();
        // 更新数据
        mDataList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tv_share)
    void showShareDialog() {
        View popViewShare = LayoutInflater.from(this).inflate(R.layout.alert_share_annual_case, null);
        SimpleDraweeView ivPicture = (SimpleDraweeView) popViewShare.findViewById(R.id.iv_picture);
        TextView tvName = (TextView) popViewShare.findViewById(R.id.tv_name);
        TextView tvAuthor = (TextView) popViewShare.findViewById(R.id.tv_author);
        ivPicture.setImageURI(bannerPath);
        tvName.setText("作品名称：" + anliTitle);
        tvAuthor.setText("主讲人：" + anliAuthor + "，" + authorDiqu);
        ShareHelp.getInstance().showShareDialog(this, popViewShare);
    }

    @OnClick(R.id.tv_pay)
    void clickPayCourse() {
        if (StringUtils.isEmpty(teacherPhoneNumber)) {
            IntentManager.toLoginActivity(this, IntentManager.LOGIN_SUCC_ACTION_FINISHSELF);
            return;
        }
        if (System.currentTimeMillis() - currMillis > 1000) {
            currMillis = System.currentTimeMillis();
            if (!"0".equals(anliTPStatus)) {
                confirmOrCancelToupiao(anliID, teacherPhoneNumber, ARTICLE_AGREE_TYPE_NO);
            } else {
                confirmOrCancelToupiao(anliID, teacherPhoneNumber, ARTICLE_AGREE_TYPE_YES);
            }
        }
    }

    /**
     * 搜索投票的状态
     */
    private void searchToupiaoStatus() {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BaseDataInfo> e) throws Exception {
                BaseDataInfo result = mService.searchAnliToupiaoFromAPI(anliID, teacherPhoneNumber);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseDataInfo info) {
                        if (!StringUtils.isEmpty(info.getRet()) && "0".equals(info.getRet())) {
                            // 未投票
                            anliTPStatus = "0";
                            anliTPNum = info.getData();
                            tvPay.setText("投票（" + anliTPNum + "）");
                            tvPay.setBackgroundColor(Color.parseColor("#FC6345"));
                        } else if (!StringUtils.isEmpty(info.getRet()) && "1".equals(info.getRet())) {
                            // 已投票
                            anliTPStatus = "1";
                            anliTPNum = info.getData();
                            tvPay.setText("已投票（" + anliTPNum + "）");
                            tvPay.setBackgroundColor(Color.parseColor("#A3A3A3"));
                        } else {
                            // 数据异常
                            T.showShort(AnnualCaseDetailActivity.this, "数据异常，请稍后重试");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 投票
     */
    private void confirmOrCancelToupiao(final String anliID, final String phoneNumber, final String type) {
        Observable.create(new ObservableOnSubscribe<BaseDataInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<BaseDataInfo> e) throws Exception {
                BaseDataInfo result = mService.getAnliToupiaoFromAPI(anliID, phoneNumber, type);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseDataInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseDataInfo info) {
                        if (info == null || StringUtils.isEmpty(info.getRet())) {
                            return;
                        }
                        if (type.equals(ARTICLE_AGREE_TYPE_YES)) {
                            if ("1".equals(info.getRet())) {
                                T.showShort(AnnualCaseDetailActivity.this, "亲，投票成功！");
                                anliTPNum = StringUtils.isEmpty(info.getData()) ? "0" : info.getData();
                                anliTPStatus = "1";
                                tvPay.setText("已投票（" + anliTPNum + "）");
                                tvPay.setBackgroundColor(Color.parseColor("#A3A3A3"));
                            } else {
                                T.showShort(AnnualCaseDetailActivity.this, info.getMsg());
                            }
                        } else {
                            if ("3".equals(info.getRet())) {
                                anliTPNum = StringUtils.isEmpty(info.getData()) ? "0" : info.getData();
                                anliTPStatus = "0";
                                tvPay.setText("投票（" + anliTPNum + "）");
                                tvPay.setBackgroundColor(Color.parseColor("#FC6345"));
                            } else {
                                T.showShort(AnnualCaseDetailActivity.this, info.getMsg());
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        T.showShort(AnnualCaseDetailActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("anli_detail");
        MobclickAgent.onResume(this);
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("anli_detail");
        MobclickAgent.onPause(this);
    }
}
