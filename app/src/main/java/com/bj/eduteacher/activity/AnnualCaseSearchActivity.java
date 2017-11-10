package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.AnnualCaseAllAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.view.OnRecyclerItemClickListener;
import com.bj.eduteacher.widget.manager.SaveGridLayoutManager;

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
 */

public class AnnualCaseSearchActivity extends BaseActivity {

    private static final int PAGE_SIZE = 30;

    @BindView(R.id.header_edt_search)
    EditText edtSearch;

    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    private List<ArticleInfo> mDataList = new ArrayList<>();
    private AnnualCaseAllAdapter mAdapter;
    private int currentPage = 1;
    private LmsDataService mService = new LmsDataService();

    private String searchContent;
    private String huodongID, bannerPath;
    private String teacherPhoneNumber;

    private int columnNum;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual_case_search);
        ButterKnife.bind(this);
        columnNum = ScreenUtils.isPadDevice(this) ? 5 : 3;

        // 初始化页面
        initToolBar();
        initView();
        initData();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        huodongID = getIntent().getStringExtra("HuodongID");
        bannerPath = getIntent().getStringExtra("HuodongBanner");

        edtSearch.setSingleLine();
        edtSearch.setHint("输入案例提交者姓名");
        edtSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    searchContent = edtSearch.getText().toString().trim();
                    searchData(searchContent);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void initView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new SaveGridLayoutManager(this, columnNum));
        mAdapter = new AnnualCaseAllAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                ArticleInfo item = mDataList.get(position);
                actionOnItemClick(item);
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder holder, int position) {

            }
        });

        mXRefreshView.setMoveForHorizontal(true);   // 在手指横向移动的时候，让XRefreshView不拦截事件
        mXRefreshView.setPullRefreshEnable(false);
        mXRefreshView.setPullLoadEnable(false);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(false);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {

            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                currentPage++;
                getRefreshDataList(searchContent, currentPage);
            }
        });
    }

    @Override
    protected void initData() {
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // edtSearch.requestFocus();
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

    @OnClick(R.id.header_ll_right)
    void clickCancelSearch() {
        onBackPressed();
        overridePendingTransition(R.anim.act_alpha_in, R.anim.act_alpha_out);
    }

    @Override
    public void onBackPressed() {
        KeyBoardUtils.closeKeybord(edtSearch, this);
        super.onBackPressed();
    }

    private void searchData(String content) {
        KeyBoardUtils.closeKeybord(edtSearch, this);
        // 搜索相关内容
        currentPage = 1;
        getRefreshDataList(content, currentPage);
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    private void getRefreshDataList(final String content, final int pageIndex) {
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                List<ArticleInfo> dataList = mService.getAnliListFromAPI(huodongID, teacherPhoneNumber, content, pageIndex, PAGE_SIZE);
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
                        T.showShort(AnnualCaseSearchActivity.this, "服务器开小差了，请重试");
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
            if (list.size() == 0) {
                T.showShort(this, "没有找到他的相关案例");
            }
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
