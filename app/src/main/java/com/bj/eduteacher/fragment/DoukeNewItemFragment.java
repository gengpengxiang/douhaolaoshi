package com.bj.eduteacher.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.DoukeNewDetailActivity;
import com.bj.eduteacher.adapter.DoukeNewAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.widget.manager.SaveGridLayoutManager;
import com.shizhefei.fragment.LazyFragment;

import java.util.ArrayList;
import java.util.List;

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
 * Created by zz379 on 2017/8/16.
 * 新逗课的每一个Tab 页面
 */

public class DoukeNewItemFragment extends LazyFragment {
    private final int PAGE_SIZE = 30;

    public static final String[] NJARRAY = new String[]{
            "一年级", "二年级", "三年级", "四年级", "五年级",
            "六年级", "七年级", "八年级", "九年级", "综合"
    };

    private XRefreshView mXRefreshView;
    private RecyclerView mRecyclerView;

    private List<ArticleInfo> mDataList = new ArrayList<>();
    private String teacherPhoneNumber;
    private DoukeNewAdapter mAdapter;
    private GridLayoutManager layoutManager;

    private String xuekeName;
    // private int currentPage = 1;
    private int currNianjiPosition = 0;
    private int currNJOffset;

    private int columnNum;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        xuekeName = getArguments().getString("XueKe");
        setContentView(R.layout.layout_refresh_view_2);
        columnNum = ScreenUtils.isPadDevice(getActivity()) ? 5 : 3;

        initView();
        initData();
    }

    private void initView() {
        mXRefreshView = (XRefreshView) findViewById(R.id.mXRefreshView);
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new SaveGridLayoutManager(getActivity(), columnNum);
        mAdapter = new DoukeNewAdapter(mDataList);
        mAdapter.setOnMyItemClickListener(new DoukeNewAdapter.OnMyItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                ArticleInfo item = mDataList.get(position);
                Intent intent = new Intent(getActivity(), DoukeNewDetailActivity.class);
                intent.putExtra("Title", item.getTitle());
                intent.putExtra("ID", item.getArticleID());
                startActivity(intent);
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mXRefreshView.setMoveForHorizontal(true);   // 在手指横向移动的时候，让XRefreshView不拦截事件
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);
        mXRefreshView.setEmptyView(R.layout.recycler_item_douhao_course_empty);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                LL.i("刷新数据");
                currNianjiPosition = 0;
                mXRefreshView.setPullLoadEnable(true);
                getDoukeRefreshList();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                // currentPage++;
                getDoukeList();
            }
        });
    }

    private void initData() {
        teacherPhoneNumber = PreferencesUtils.getString(getApplicationContext(), MLProperties.PREFER_KEY_USER_ID, "");

        mXRefreshView.startRefresh();
    }

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
    }

    @Override
    public void onDestroy() {
        LL.i("DoukeNewItemFragment.....onDestroy()");
        disposables.clear();
        super.onDestroy();
    }

    private void getDoukeRefreshList() {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                try {

                    LmsDataService mService = new LmsDataService();
                    List<ArticleInfo> dataList = new ArrayList<>();
                    int pageSize = PAGE_SIZE;
                    for (int i = 0; i < NJARRAY.length; i++) {
                        // 记录当前加载到几年级了
                        currNianjiPosition = i;
                        List<ArticleInfo> njList = mService.getNewDoukeListFromAPI(xuekeName, NJARRAY[i], 0, pageSize);
                        if (njList.size() > 0) {
                            dataList.add(new ArticleInfo(NJARRAY[i], ArticleInfo.SHOW_TYPE_DECORATION));
                            dataList.addAll(njList);
                            pageSize -= njList.size();
                        }
                        if (pageSize > 0) {
                            // 如果下一个年级的数量小于3，则补齐一行
                            if (pageSize < columnNum) {
                                pageSize = columnNum;
                            } else {
                                int last = pageSize % columnNum;
                                if (last > 0) {
                                    pageSize = pageSize + (columnNum - last);
                                }
                            }
                            if (i == NJARRAY.length - 1) {
                                currNJOffset = njList.size();
                            }
                            LL.i("HTTP", "当前年级：" + currNianjiPosition + ":" + NJARRAY[currNianjiPosition] + " -- 当前年级的偏移量：" + currNJOffset);
                            continue;
                        } else {
                            // 如果最后不满一行3个的话就去掉最后一行
                            int reduceNum = njList.size() % columnNum;
                            if (reduceNum > 0) {
                                dataList = dataList.subList(0, dataList.size() - reduceNum);
                            }
                            // 刷新页面，当最后跳出循环时，记录当前年级的偏移量（刷新页面的时候每个年级肯定只加载第一页的数据）
                            currNJOffset = njList.size() - reduceNum;
                            LL.i("HTTP", "当前年级：" + currNianjiPosition + ":" + NJARRAY[currNianjiPosition] + " -- 当前年级的偏移量：" + currNJOffset);
                            break;
                        }
                    }
                    if (!e.isDisposed()) {
                        e.onNext(dataList);
                        e.onComplete();
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {
                        loadRefreshData(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LL.e(e);
                        cleanXRefreshView();
                        if (getActivity() != null) {
                            T.showShort(getActivity(), "服务器开小差了，请重试");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadRefreshData(List<ArticleInfo> result) {
        mXRefreshView.stopRefresh();
        mDataList.clear();
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getAdapterItemCount() == 0) {
                    return columnNum;
                }

                if (mAdapter.getItemViewType(position) == DoukeNewAdapter.ShowType.ITEM_TYPE_DOUKE.ordinal()) {
                    return 1;
                } else {
                    return columnNum;
                }
            }
        });

        // 当加载到最后一个年级的时候，最后一个年级的数量小于18，禁止上拉加载更多的功能
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

        if (getActivity() != null && mXRefreshView.getPullLoadEnable() && null == mAdapter.getCustomLoadMoreView()) {
            mAdapter.setCustomLoadMoreView(new XRefreshViewFooter(getActivity()));
        }
    }

    private void getDoukeList() {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                try {
                    LmsDataService mService = new LmsDataService();
                    List<ArticleInfo> dataList = new ArrayList<>();
                    int pageSize = PAGE_SIZE;
                    for (int i = currNianjiPosition; i < NJARRAY.length; i++) {
                        // 记录当前加载到几年级了
                        List<ArticleInfo> njList = mService.getNewDoukeListFromAPI(xuekeName, NJARRAY[i], currNJOffset, pageSize);

                        if (njList.size() > 0) {
                            if (currNJOffset == 0) {
                                dataList.add(new ArticleInfo(NJARRAY[i], ArticleInfo.SHOW_TYPE_DECORATION));
                            }
                            dataList.addAll(njList);
                            pageSize -= njList.size();
                        }
                        // 保存当前年级
                        currNianjiPosition = i;

                        if (pageSize > 0) {
                            // 如果下一个年级的数量小于3，则补齐一行
                            if (pageSize < columnNum) {
                                pageSize = columnNum;
                            } else {
                                int last = pageSize % columnNum;
                                if (last > 0) {
                                    pageSize = pageSize + (columnNum - last);
                                }
                            }
                            if (i != NJARRAY.length - 1) {
                                // 要进入下一个年级，offset置为0
                                currNJOffset = 0;
                            } else {
                                currNJOffset = currNJOffset + njList.size();
                            }
                            LL.i("HTTP", "当前年级：" + currNianjiPosition + ":" + NJARRAY[currNianjiPosition] + " -- 当前年级的偏移量：" + currNJOffset);
                            continue;
                        } else {
                            // 如果最后不满一行  个的话就去掉最后一行
                            int reduceNum = njList.size() % columnNum;
                            if (reduceNum > 0) {
                                dataList = dataList.subList(0, dataList.size() - reduceNum);
                            }
                            // 当最后跳出循环时，记录当前年级的偏移量
                            currNJOffset = currNJOffset + (njList.size() - reduceNum);
                            LL.i("HTTP", "当前年级：" + currNianjiPosition + ":" + NJARRAY[currNianjiPosition] + " -- 当前年级的偏移量：" + currNJOffset);
                            break;
                        }
                    }
                    if (!e.isDisposed()) {
                        e.onNext(dataList);
                        e.onComplete();
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {
                        loadData(result);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        LL.e(e);
                        cleanXRefreshView();
                        if (getActivity() != null) {
                            T.showShort(getActivity(), "服务器开小差了，请重试");
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadData(List<ArticleInfo> result) {
        mXRefreshView.stopLoadMore();

        // 当加载到最后一个年级的时候，最后一个年级的数量小于18，禁止上拉加载更多的功能
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

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }
}
