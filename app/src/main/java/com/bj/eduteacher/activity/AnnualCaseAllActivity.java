package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.AnnualCaseAllAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.view.OnRecyclerItemClickListener;
import com.bj.eduteacher.widget.manager.SaveGridLayoutManager;
import com.facebook.drawee.view.SimpleDraweeView;

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
 * Created by zz379 on 2017/10/18.
 * 年会案例集合
 */

public class AnnualCaseAllActivity extends BaseActivity {

    private static final int PAGE_SIZE = 30;

    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;

    private SimpleDraweeView ivCaseBanner;
    private View headerView;

    private List<ArticleInfo> mDataList = new ArrayList<>();
    private AnnualCaseAllAdapter mAdapter;
    private int currentPage = 1;
    private LmsDataService mService = new LmsDataService();

    private String huodongID;
    private String teacherPhoneNumber;
    private String bannerPath = "";

    private int columnNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual_case_all);
        ButterKnife.bind(this);
        columnNum = ScreenUtils.isPadDevice(this) ? 5 : 3;
        
        initStatus();
        // 初始化页面
        initToolBar();
        initView();
        initData();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        tvTitle.setText("案例评审");
        huodongID = getIntent().getStringExtra("huodongID");
    }

    @Override
    protected void initView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new SaveGridLayoutManager(this, columnNum));
        mAdapter = new AnnualCaseAllAdapter(mDataList);
        headerView = mAdapter.setHeaderView(R.layout.recycler_item_annual_case_header, mRecyclerView);
        initHeaderView();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                if (position > 0) {
                    // 包括header
                    ArticleInfo item = mDataList.get(position - 1);
                    actionOnItemClick(item);
                }
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder holder, int position) {

            }
        });

        mXRefreshView.setMoveForHorizontal(true);   // 在手指横向移动的时候，让XRefreshView不拦截事件
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                LL.i("刷新数据");
                currentPage = 1;
                getHuodongInfo(huodongID);
                getRefreshDataList(currentPage);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                currentPage++;
                getRefreshDataList(currentPage);
            }
        });
    }

    private void initHeaderView() {
        ivCaseBanner = (SimpleDraweeView) headerView.findViewById(R.id.iv_zhengshu);
    }

    @Override
    protected void initData() {
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

        currentPage = 1;
        getHuodongInfo(huodongID);
        getRefreshDataList(currentPage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
    }

    private void actionOnItemClick(ArticleInfo item) {
        Intent intent = new Intent(this, AnnualCaseDetailActivity.class);
        intent.putExtra("ID", item.getArticleID());
        intent.putExtra("Title", item.getTitle());
        intent.putExtra("Author", item.getAuthor());
        intent.putExtra("Diqu", item.getContent());
        intent.putExtra("AnliTPNum", item.getAgreeNumber());
        intent.putExtra("AnliTPStatus", item.getCommentNumber());
        intent.putExtra("HuodongBanner", bannerPath);
        startActivity(intent);
    }

    @OnClick(R.id.header_ll_left)
    void clickBack() {
        onBackPressed();
    }

    @OnClick(R.id.header_ll_bottom)
    void clickSearch() {
        Intent intent = new Intent(this, AnnualCaseSearchActivity.class);
        intent.putExtra("HuodongID", huodongID);
        intent.putExtra("HuodongBanner", bannerPath);
        startActivity(intent);
        overridePendingTransition(R.anim.act_alpha_in, R.anim.act_alpha_out);
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    /**
     * 获取活动的相关信息
     *
     * @param huodongID
     */
    private void getHuodongInfo(final String huodongID) {
        Observable.create(new ObservableOnSubscribe<ArticleInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArticleInfo> e) throws Exception {
                ArticleInfo info = mService.getHuodongInfoFromAPI(huodongID);
                e.onNext(info);
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
                        bannerPath = info.getArticlePicture();
                        ivCaseBanner.setImageURI(info.getArticlePicture());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getRefreshDataList(final int pageIndex) {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                if (pageIndex == 1) {
                    // 刷新数据的时候需要调接口，对所有案例重新进行排序
                    mService.updateAnliPaixu(huodongID);
                }
                List<ArticleInfo> dataList = mService.getAnliListFromAPI(huodongID, teacherPhoneNumber, "", pageIndex, PAGE_SIZE);
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
                        cleanXRefreshView();
                        T.showShort(AnnualCaseAllActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadData(List<ArticleInfo> list) {
        if (currentPage == 1) {
            mDataList.clear();
            mXRefreshView.stopRefresh();
            mXRefreshView.setPullLoadEnable(true);
            mXRefreshView.setAutoLoadMore(true);
        } else {
            mXRefreshView.stopLoadMore();
        }
        if (list == null || list.size() < PAGE_SIZE) {
            mXRefreshView.setPullLoadEnable(false);
            mXRefreshView.setAutoLoadMore(false);
        }
        // 更新数据
        mDataList.addAll(list);
        mAdapter.notifyDataSetChanged();
        if (mDataList.size() >= 10 && null == mAdapter.getCustomLoadMoreView()) {
            mAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        }
    }
}
