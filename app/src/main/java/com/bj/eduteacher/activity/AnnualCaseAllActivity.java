package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.AnnualCaseAllAdapter;
import com.bj.eduteacher.api.HttpUtilService;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.utils.LL;
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

    private static final int PAGE_SIZE = 18;

    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    private SimpleDraweeView ivCaseBanner;

    private List<ArticleInfo> mDataList = new ArrayList<>();
    private AnnualCaseAllAdapter mAdapter;
    private int currentPage = 1;
    private LmsDataService mService = new LmsDataService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual_case_all);
        ButterKnife.bind(this);

        // 初始化页面
        initView();
        initDatas();
    }

    private void initView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new SaveGridLayoutManager(this, 3));
        mAdapter = new AnnualCaseAllAdapter(mDataList);
        ivCaseBanner = (SimpleDraweeView) mAdapter.setHeaderView(R.layout.recycler_item_course_zhengshu, mRecyclerView);
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
        ivCaseBanner.setImageURI(HttpUtilService.BASE_RESOURCE_URL + "46d99fc38ea7e2b6ff35a3b583a1d0f1.png");
    }

    private void initDatas() {
        currentPage = 1;
        getRefreshDataList(currentPage);
    }

    private void actionOnItemClick(ArticleInfo item) {
        Intent intent = new Intent(this, AnnualCaseDetailActivity.class);
        intent.putExtra("Title", "分数的认识");
        intent.putExtra("ID", "0002");
        startActivity(intent);
    }

    @OnClick(R.id.header_ll_left)
    void clickBack() {
        onBackPressed();
    }

    @OnClick(R.id.header_ll_center)
    void clickSearch() {
        Intent intent = new Intent(this, AnnualCaseSearchActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.act_alpha_in, R.anim.act_alpha_out);
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    private void getRefreshDataList(final int pageIndex) {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                List<ArticleInfo> dataList = mService.getFamousTeacherCardsFromAPI(pageIndex, PAGE_SIZE);
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
