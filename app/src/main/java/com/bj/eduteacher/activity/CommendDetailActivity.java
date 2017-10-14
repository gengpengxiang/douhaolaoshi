package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.LatestNewsCommendAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ClassNewsInfo;
import com.bj.eduteacher.entity.SubjectInfo;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.widget.flowlayout.FlowLayout;
import com.bj.eduteacher.widget.flowlayout.TagAdapter;
import com.bj.eduteacher.widget.flowlayout.TagFlowLayout;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zz379 on 2017/3/7.
 * 点赞二级页面
 */

public class CommendDetailActivity extends BaseActivity {

    @BindView(R.id.header_img_back)
    ImageView imgBack;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.mTagFlowLayout)
    TagFlowLayout mTagFlowLayout;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    private List<SubjectInfo> mSubjectList = new ArrayList<>();
    private LayoutInflater mInflater;
    private TagAdapter<SubjectInfo> mTagAdapter;

    private LatestNewsCommendAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;
    private List<ClassNewsInfo> mDataList = new ArrayList<>();
    private View headerView;

    private String classId;
    private String reasonTypeID = "0";
    private String userPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commend_detail);
        ButterKnife.bind(this);

        initToolBar();
        initView();
        initData();
    }

    private void initToolBar() {
        imgBack.setVisibility(View.VISIBLE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("点赞");
    }

    private void initView() {
        mInflater = LayoutInflater.from(this);
        classId = getIntent().getExtras().getString(MLConfig.KEY_CLASS_ID);
        // 初始化流式布局
        mTagAdapter = new TagAdapter<SubjectInfo>(mSubjectList) {
            @Override
            public View getView(FlowLayout parent, int position, SubjectInfo subjectInfo) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tv_subject_tag, mTagFlowLayout, false);
                tv.setText(subjectInfo.getSubName() + " " + subjectInfo.getSubBadgeCount());
                return tv;
            }
        };
        mTagFlowLayout.setAdapter(mTagAdapter);
        mTagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                mTagAdapter.setSelectedList(position);
                reasonTypeID = mSubjectList.get(position).getSubID();

                mXRefreshView.startRefresh();
                return true;
            }
        });

        // 初始化下拉刷新控件
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set Adatper
        mAdapter = new LatestNewsCommendAdapter(mDataList);
        mAdapter.setOnMyItemClickListener(new LatestNewsCommendAdapter.OnMyItemClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onUserPhotoClick(View view, int position) {
                String studentId = mDataList.get(position - 1).getStudentId();
                String studentImg = mDataList.get(position - 1).getStudentPic();
                intent2StudentDetailActivity(studentId, studentImg);
            }
        });
        headerView = mAdapter.setHeaderView(R.layout.recycler_header_line_textview_line, mRecyclerView);
        initHeaderView();

        mRecyclerView.setAdapter(mAdapter);

        // set xRefreshView
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);

        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isRefresh) {
                LL.i("刷新数据");
                currentPage = 1;
//                 mXRefreshView.setAutoLoadMore(true);
//                 mXRefreshView.setPullLoadEnable(true);

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

    private void initHeaderView() {
        TextView tv = ButterKnife.findById(headerView, R.id.tv_latest_news);
        tv.setText("点赞记录");
    }

    private void initData() {
        userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

        MyClassCommendTypeAsyncTask task = new MyClassCommendTypeAsyncTask();
        task.execute();

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

    private class MyClassCommendTypeAsyncTask extends AsyncTask<String, Integer, List<SubjectInfo>> {

        @Override
        protected List<SubjectInfo> doInBackground(String... params) {
            LmsDataService mService = new LmsDataService();
            List<SubjectInfo> newList;
            try {
                newList = mService.getClassCommendTypeFromAPI(classId, userPhoneNumber);
            } catch (Exception e) {
                e.printStackTrace();
                newList = new ArrayList<>();
            }
            return newList;
        }

        @Override
        protected void onPostExecute(List<SubjectInfo> subjectInfos) {
            if (subjectInfos == null) {
                T.showShort(CommendDetailActivity.this, "服务器开小差了，请待会重试");
            } else if (subjectInfos.size() == 0) {
                T.showShort(CommendDetailActivity.this, "数据异常");
            } else {
                updateCommendType(subjectInfos);
            }
        }
    }

    private void updateCommendType(List<SubjectInfo> subjectInfos) {
        mSubjectList.clear();
        mSubjectList.addAll(subjectInfos);

        if (mSubjectList.size() > 0) {
            mTagAdapter.setSelectedList(0);
        }
        mTagAdapter.notifyDataChanged();
    }

    private class MyClassAllNewsAsyncTask extends AsyncTask<String, Integer, List<ClassNewsInfo>> {
        @Override
        protected List<ClassNewsInfo> doInBackground(String... params) {
            String classId = params[0];
            String pageIndex = params[1];
            LmsDataService mService = new LmsDataService();
            List<ClassNewsInfo> infoList;
            try {
                infoList = mService.getClassCommendNewsFromAPI(classId, userPhoneNumber, reasonTypeID, pageIndex);
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
                T.showShort(CommendDetailActivity.this, "服务器开小差了，请重试");
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
            mXRefreshView.setAutoLoadMore(true);
            mXRefreshView.setPullLoadEnable(true);
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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("thumb");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("thumb");
        MobclickAgent.onPause(this);
    }
}
