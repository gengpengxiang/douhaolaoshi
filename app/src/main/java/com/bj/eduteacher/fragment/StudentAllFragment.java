package com.bj.eduteacher.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.ReasonEditActivity;
import com.bj.eduteacher.activity.StudentDetailActivity;
import com.bj.eduteacher.adapter.AllStudentAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.dialog.CommendReasonAlertDialog;
import com.bj.eduteacher.dialog.CommendReasonInfo;
import com.bj.eduteacher.entity.ClassItemInfo;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
 */

public class StudentAllFragment extends BaseFragment {

    XRefreshView mXRefreshView;
    RecyclerView mRecyclerView;

    private AllStudentAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;
    private List<ClassItemInfo> mDataList = new ArrayList<>();
    private MyAsyncTask myAsyncTask;
    private String classId;
    private String userPhoneNumber;
    private String orderby;

    List<CommendReasonInfo> reasonList = new ArrayList<>();
    // 全班学生
    private String gradeTypeID = "0";
    private long currMillis = 0;

    public StudentAllFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.layout_refresh_view, container, false);
    }

    @Override
    protected void bindViews(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.mRecyclerView);
        mXRefreshView = (XRefreshView) view.findViewById(R.id.mXRefreshView);

        mRecyclerView.setHasFixedSize(true);
        // look as listview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // set Adatper
        mAdapter = new AllStudentAdapter(mDataList);
        mRecyclerView.setAdapter(mAdapter);

        // set xRefreshView
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);
        mXRefreshView.setEmptyView(R.layout.recycler_item_news_empty_3);
    }

    @Override
    protected void setListener() {
        mAdapter.setOnMyItemClickListener(new AllStudentAdapter.OnMyItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                intent2StudentDetailActivity(mDataList.get(position));
            }

            @Override
            public void onCommendClick(View view, int position) {
                MobclickAgent.onEvent(getActivity(), "class_thumb");
                if (System.currentTimeMillis() - currMillis > 1000) {
                    actionCommendStudentReason(mDataList.get(position).getStudId(), String.valueOf(position));
                    currMillis = System.currentTimeMillis();
                }
            }
        });
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh() {
                LL.i("刷新数据");
                if (myAsyncTask != null && !myAsyncTask.isCancelled()) {
                    myAsyncTask.cancel(true);
                }

                currentPage = 1;
                mXRefreshView.setPullLoadEnable(true);
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute(classId, gradeTypeID, String.valueOf(currentPage));
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                if (myAsyncTask != null && !myAsyncTask.isCancelled()) {
                    myAsyncTask.cancel(true);
                }

                currentPage++;
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute(classId, gradeTypeID, String.valueOf(currentPage));
            }
        });
    }

    @Override
    protected void processLogic() {
        userPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
        classId = getArguments().getString(MLConfig.KEY_CLASS_ID, "");
        orderby = getArguments().getString(MLConfig.KEY_CLASS_STUDENTS_ORDERBY, "");
        // 设置页面标题

        currentPage = 1;
        mXRefreshView.setPullLoadEnable(true);
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(classId, gradeTypeID, String.valueOf(currentPage));
    }

    @Override
    public void onResume() {
        super.onResume();
        String commendReasons = PreferencesUtils.getString(getActivity(), "CommendReason");
        if (StringUtils.isEmpty(commendReasons) || commendReasons.equals("[]")) {
            getCommendReason();
        } else {
            parseCommendReasons(commendReasons);
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        MobclickAgent.onPageStart("allStudent");
    }

    @Override
    protected void onInVisible() {
        super.onInVisible();
        MobclickAgent.onPageEnd("allStudent");
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, List<ClassItemInfo>> {

        @Override
        protected List<ClassItemInfo> doInBackground(String... params) {
            String classId = params[0];
            String type = params[1];
            String pageIndex = params[2];
            LmsDataService mService = new LmsDataService();
            List<ClassItemInfo> infoList;
            try {
                infoList = mService.getClassAllStudentFromAPI(classId, type, pageIndex, orderby);
            } catch (Exception e) {
                e.printStackTrace();
                LL.e(e);
                infoList = null;
            }
            return infoList;
        }

        @Override
        protected void onPostExecute(List<ClassItemInfo> result) {
            if (result == null) {
                cleanXRefreshView();
                T.showShort(getActivity(), "服务器开小差了，请重试");
            } else {
                loadData(result);
            }
        }
    }

    private void loadData(List<ClassItemInfo> list) {
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
            mAdapter.setCustomLoadMoreView(new XRefreshViewFooter(getActivity()));
        }
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    private void intent2StudentDetailActivity(ClassItemInfo student) {
        Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_STUDENT_ID, student.getStudId());
        args.putString(MLConfig.KEY_STUDENT_NAME, student.getStudName());
        args.putString(MLConfig.KEY_STUDENT_PHOTO, student.getStudImg());
        args.putString(MLConfig.KEY_STUDENT_SCORE, student.getStudScore());
        args.putString(MLConfig.KEY_STUDENT_BADGE, student.getStudBadge());
        args.putString(MLConfig.KEY_STUDENT_GRADE, student.getStudGrade());
        args.putString(MLConfig.KEY_STUDENT_PINGYU, student.getStudPingyu());
        intent.putExtras(args);
        startActivity(intent);
    }

    private void actionCommendStudentReason(final String studentID, final String pos) {
        CommendReasonAlertDialog dialog = new CommendReasonAlertDialog(getActivity())
                .setContentRecyclerData(reasonList)
                .setmItemClickListener(new CommendReasonAlertDialog.onSweetContentItemClickListener() {
                    @Override
                    public void onClick(CommendReasonAlertDialog sweetAlertDialog, int position) {
                        sweetAlertDialog.dismiss();
                        if ("-1".equals(reasonList.get(position).getReasonID())) {
                            Intent intent = new Intent(getActivity(), ReasonEditActivity.class);
                            startActivity(intent);
                        } else {
                            MyCommentTask task = new MyCommentTask();
                            task.execute(studentID, reasonList.get(position).getReasonID(), userPhoneNumber, pos);
                        }
                    }
                });
        dialog.show();
    }

    private class MyCommentTask extends AsyncTask<String, Integer, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            String studentId = params[0];
            String reasonId = params[1];
            String teacherId = params[2];
            String position = params[3];

            LmsDataService mService = new LmsDataService();
            String[] result;
            try {
                result = mService.commendStudentFromAPI(teacherId, studentId, reasonId, position);
            } catch (Exception e) {
                e.printStackTrace();
                LL.e(e);
                result = new String[2];
                result[0] = "0";
                result[1] = "服务器开小差了，请待会重试";
                result[2] = position;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            T.showShort(getActivity(), result[1]);
            if (!StringUtils.isEmpty(result[0]) && result[0].equals("1")) {
                int position = Integer.valueOf(result[2]);
                mDataList.get(position).setStudScore(String.valueOf(Integer.valueOf(mDataList.get(position).getStudScore()) + 1));
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void parseCommendReasons(String result) {
        reasonList.clear();
        try {
            JSONArray resultArray = new JSONArray(result);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject reasonObj = resultArray.optJSONObject(i);
                String reasonID = reasonObj.optString("id");
                String reasonTitle = reasonObj.optString("title");
                reasonList.add(new CommendReasonInfo(reasonID, reasonTitle));
            }
            // 添加编辑理由的功能
            reasonList.add(new CommendReasonInfo("-1", "编辑理由"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCommendReason() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                LmsDataService mService = new LmsDataService();
                String result;
                try {
                    result = mService.getCommendReasonFromAPI(userPhoneNumber);
                    PreferencesUtils.putString(getActivity(), "CommendReason", result);
                } catch (Exception e) {
                    e.printStackTrace();
                    LL.e(e);
                    result = "";
                    emitter.onError(e);
                }
                emitter.onNext(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String s) {
                        parseCommendReasons(s);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
