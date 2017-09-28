package com.bj.eduteacher.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.bj.eduteacher.BaseFragment2;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.RankListAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ClassItemInfo;
import com.bj.eduteacher.utils.LL;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zz379 on 2017/3/29.
 */

public class SchoolRankFragment extends BaseFragment2 {

    private String typeID;
    private String schoolID;

    public static SchoolRankFragment newInstance(String typeID, String schoolID) {

        Bundle args = new Bundle();

        SchoolRankFragment fragment = new SchoolRankFragment();
        args.putString(MLProperties.BUNDLE_KEY_SCHOOL_RANK_TYPE_ID, typeID);
        args.putString(MLProperties.BUNDLE_KEY_SCHOOL_CODE, schoolID);
        fragment.setArguments(args);
        return fragment;
    }

    public SchoolRankFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeID = getArguments().getString(MLProperties.BUNDLE_KEY_SCHOOL_RANK_TYPE_ID);
        schoolID = getArguments().getString(MLProperties.BUNDLE_KEY_SCHOOL_CODE);
    }

    @Override
    protected void lazyLoad() {

    }

    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    private List<ClassItemInfo> mDataList = new ArrayList<>();
    private RankListAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_refresh_view, container, false);
        ButterKnife.bind(this, view);

        initView();
        return view;
    }

    private void initView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new RankListAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);

        mXRefreshView.setAutoLoadMore(false);
        mXRefreshView.setPullLoadEnable(false);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setPullRefreshEnable(false);

        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                LL.i("是否手动下拉刷新：" + isPullDown);
                refreshData();
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshData();
    }

    private void refreshData() {
        MyGetStudentAllNewsTask task = new MyGetStudentAllNewsTask();
        task.execute(typeID);
    }

    private class MyGetStudentAllNewsTask extends AsyncTask<String, Integer, List<ClassItemInfo>> {

        @Override
        protected List<ClassItemInfo> doInBackground(String... params) {
            LmsDataService mService = new LmsDataService();
            String currBadgeType = params[0];
            List<ClassItemInfo> dataList;
            try {
                dataList = mService.getSchoolRankListByTypeFromAPI(currBadgeType, schoolID);
            } catch (Exception e) {
                e.printStackTrace();
                LL.e(e);
                dataList = null;
            }
            return dataList;
        }

        @Override
        protected void onPostExecute(List<ClassItemInfo> result) {
            if (result != null) {
                loadData(result);
            }
        }
    }

    private void loadData(List<ClassItemInfo> result) {
        mXRefreshView.stopRefresh();
        mDataList.clear();
        mDataList.addAll(result);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        MobclickAgent.onPageStart("schoolBoard");
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        MobclickAgent.onPageEnd("schoolBoard");
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
