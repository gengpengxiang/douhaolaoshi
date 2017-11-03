package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.ThanksListAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.entity.ClassNewsInfo;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.T;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zz379 on 2017/3/8.
 * 感谢二级页面
 */

public class ThanksDetailActivity extends BaseActivity {

    @BindView(R.id.header_img_back)
    ImageView imgBack;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    private ThanksListAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;
    private List<ClassNewsInfo> mDataList = new ArrayList<>();

    private String classId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanks_detail);
        ButterKnife.bind(this);

        initToolBar();
        initView();
        initData();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        imgBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("感谢");
    }

    @Override
    protected void initView() {
        classId = getIntent().getExtras().getString(MLConfig.KEY_CLASS_ID);
        // 初始化下拉刷新控件
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set Adatper
        mAdapter = new ThanksListAdapter(mDataList);
        mAdapter.setOnMyItemClickListener(new ThanksListAdapter.OnMyItemClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onUserPhotoClick(View view, int position) {
//                String studentId = mDataList.get(position).getStudentId();
//                String studentImg = mDataList.get(position).getStudentPic();
//                intent2StudentDetailActivity(studentId, studentImg);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

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

                MyClassAllNewsAsyncTask task3 = new MyClassAllNewsAsyncTask();
                task3.execute(classId, String.valueOf(currentPage));
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                currentPage++;
                MyClassAllNewsAsyncTask task3 = new MyClassAllNewsAsyncTask();
                task3.execute(classId, String.valueOf(currentPage));
            }
        });
    }

    @Override
    protected void initData() {
        MyClassAllNewsAsyncTask task3 = new MyClassAllNewsAsyncTask();
        task3.execute(classId, String.valueOf(currentPage));
    }

    @OnClick(R.id.header_ll_left)
    void actionBackClick() {
        this.finish();
    }

    private void intent2StudentDetailActivity(String studentId, String studentImg) {
        Intent intent = new Intent(this, StudentDetailActivity.class);
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_STUDENT_ID, studentId);
        args.putString(MLConfig.KEY_STUDENT_NAME, "");
        args.putString(MLConfig.KEY_STUDENT_PHOTO, studentImg);
        args.putString(MLConfig.KEY_STUDENT_SCORE, "");
        args.putString(MLConfig.KEY_STUDENT_BADGE, "");
        args.putString(MLConfig.KEY_STUDENT_GRADE, "");
        args.putString(MLConfig.KEY_STUDENT_PINGYU, "");
        intent.putExtras(args);
        startActivity(intent);
    }

    private class MyClassAllNewsAsyncTask extends AsyncTask<String, Integer, List<ClassNewsInfo>> {
        @Override
        protected List<ClassNewsInfo> doInBackground(String... params) {
            String classId = params[0];
            String pageIndex = params[1];
            LmsDataService mService = new LmsDataService();
            List<ClassNewsInfo> infoList;
            try {
                infoList = mService.getClassThanksListFromAPI(classId, pageIndex);
            } catch (Exception e) {
                e.printStackTrace();
                LL.e(e);
                infoList = null;
            }
            return infoList;
        }

        @Override
        protected void onPostExecute(List<ClassNewsInfo> result) {
            hideLoadingDialog();

            if (result == null) {
                cleanXRefreshView();
                T.showShort(ThanksDetailActivity.this, "服务器开小差了，请重试");
            } else {
                loadData(result);
            }
        }
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    private void loadData(List<ClassNewsInfo> list) {
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
        MobclickAgent.onPageStart("thanks");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("thanks");
        MobclickAgent.onPause(this);
    }
}
