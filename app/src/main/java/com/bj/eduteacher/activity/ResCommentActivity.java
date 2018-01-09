package com.bj.eduteacher.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.DoukeCommentAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.CommentInfo;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.widget.DecorationForDouke;
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
 * Created by zz379 on 2017/2/3.
 * 资源评论页面
 */

public class ResCommentActivity extends BaseActivity {

    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.edt_content)
    EditText edtContent;
    @BindView(R.id.tv_send)
    TextView tvSend;

    private DoukeCommentAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;

    private List<CommentInfo> mDataList = new ArrayList<>();
    private String newsID;
    private String userPhoneNumber;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_douke_comment);
        ButterKnife.bind(this);
        // 初始化页面
        initToolBar();
        initView();
        initData();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        TextView tvTitle = (TextView) this.findViewById(R.id.header_tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("评论");

        LinearLayout llHeaderLeft = (LinearLayout) this.findViewById(R.id.header_ll_left);
        llHeaderLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResCommentActivity.this.finish();
            }
        });

        ImageView imgBack = (ImageView) this.findViewById(R.id.header_img_back);
        imgBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initView() {
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        // set Adatper
        mAdapter = new DoukeCommentAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DecorationForDouke(this, LinearLayoutManager.VERTICAL, 3));

        // set xRefreshView
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);
        mXRefreshView.setEmptyView(R.layout.recycler_item_comment_empty);
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
    protected void initData() {
        userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        newsID = getIntent().getStringExtra(MLProperties.BUNDLE_KEY_DOUKE_ID);

        currentPage = 1;
        mXRefreshView.setPullLoadEnable(true);
        getDoukeCommentFromAPI(currentPage);
    }

    private void loadData(List<CommentInfo> list) {
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

        edtContent.setText("");
        KeyBoardUtils.closeKeybord(this.getCurrentFocus().getWindowToken(), this);
        tvSend.setEnabled(false);
        sendCommentContent(content);
    }

    private void getDoukeCommentFromAPI(final int currentPage) {
        Observable.create(new ObservableOnSubscribe<List<CommentInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CommentInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                List<CommentInfo> dataList = mService.getResourceAllCommentFromAPI(newsID, String.valueOf(currentPage));
                e.onNext(dataList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CommentInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<CommentInfo> classItemInfos) {
                        loadData(classItemInfos);
                    }

                    @Override
                    public void onError(Throwable e) {
                        cleanXRefreshView();
                        T.showShort(ResCommentActivity.this, "服务器开小差了，请重试");
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
                String[] result = mService.postResourceCommentFromAPI(newsID, userPhoneNumber,
                        MLConfig.KEY_DOUKE_COMMENT_JIAOSHI, content);
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
                        if (!StringUtils.isEmpty(result[0]) && "1".equals(result[0])) {
                            // 发布成功
                            mXRefreshView.startRefresh();
                        } else {
                            // 发布失败
                            T.showShort(ResCommentActivity.this, "发布评论失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        tvSend.setEnabled(true);
                        T.showShort(ResCommentActivity.this, "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("comment");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("comment");
        MobclickAgent.onPause(this);
    }
}
