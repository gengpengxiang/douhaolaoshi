package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
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

public class StudentGradeActivity extends BaseActivity {

    private XRefreshView mXRefreshView;
    private RecyclerView mRecyclerView;
    private AllStudentAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;
    private List<ClassItemInfo> mDataList = new ArrayList<>();
    private MyAsyncTask myAsyncTask;
    private String classId;
    private String className;
    private TextView tvTitle;
    private String userPhoneNumber;

    List<CommendReasonInfo> reasonList = new ArrayList<>();
    private String gradeName;
    private String gradeTypeID;
    private long currMillis = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);
        // 初始化页面
        initToolBar();
        initView();
        initData();
    }

    private void initToolBar() {
        tvTitle = (TextView) this.findViewById(R.id.header_tv_title);
        tvTitle.setVisibility(View.VISIBLE);

        LinearLayout llHeaderLeft = (LinearLayout) this.findViewById(R.id.header_ll_left);
        llHeaderLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentGradeActivity.this.finish();
            }
        });
        LinearLayout llHeaderRight = (LinearLayout) this.findViewById(R.id.header_ll_right);
        llHeaderRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentGradeActivity.this, LevelDetailActivity.class);
                startActivity(intent);
            }
        });

        ImageView imgBack = (ImageView) this.findViewById(R.id.header_img_back);
        imgBack.setVisibility(View.VISIBLE);
        ImageView ivQuestion = (ImageView) this.findViewById(R.id.header_img_question);
        ivQuestion.setVisibility(View.VISIBLE);
    }

    private void initView() {
        mXRefreshView = (XRefreshView) this.findViewById(R.id.mXRefreshView);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set Adatper
        mAdapter = new AllStudentAdapter(mDataList);
        mAdapter.setOnMyItemClickListener(new AllStudentAdapter.OnMyItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                intent2StudentDetailActivity(mDataList.get(position));
            }

            @Override
            public void onCommendClick(View view, int position) {
                MobclickAgent.onEvent(StudentGradeActivity.this, "class_thumb");
                if (System.currentTimeMillis() - currMillis > 1000) {
                    actionCommendStudentReason(mDataList.get(position).getStudId(), String.valueOf(position));
                    currMillis = System.currentTimeMillis();
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        // set xRefreshView
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);
        mXRefreshView.setEmptyView(R.layout.recycler_item_news_empty_3);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean success) {
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

    private void initData() {
        userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        classId = getIntent().getExtras().getString(MLConfig.KEY_CLASS_ID);
        gradeTypeID = getIntent().getExtras().getString(MLConfig.KEY_GRADE_ID);
        gradeName = getIntent().getExtras().getString(MLConfig.KEY_GRADE_NAME);
        // 设置页面标题
        tvTitle.setText(gradeName);

        currentPage = 1;
        mXRefreshView.setPullLoadEnable(true);
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(classId, gradeTypeID, String.valueOf(currentPage));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("studentGrade");
        MobclickAgent.onResume(this);
        String commendReasons = PreferencesUtils.getString(this, "CommendReason");
        if (StringUtils.isEmpty(commendReasons) || commendReasons.equals("[]")) {
            getCommendReason();
        } else {
            parseCommendReasons(commendReasons);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("studentGrade");
        MobclickAgent.onPause(this);
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
                infoList = mService.getClassAllStudentFromAPI(classId, type, pageIndex, "qt");
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
                T.showShort(StudentGradeActivity.this, "服务器开小差了，请重试");
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
            mAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        }
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    private void intent2StudentDetailActivity(ClassItemInfo student) {
        Intent intent = new Intent(this, StudentDetailActivity.class);
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
        CommendReasonAlertDialog dialog = new CommendReasonAlertDialog(this)
                .setContentRecyclerData(reasonList)
                .setmItemClickListener(new CommendReasonAlertDialog.onSweetContentItemClickListener() {
                    @Override
                    public void onClick(CommendReasonAlertDialog sweetAlertDialog, int position) {
                        sweetAlertDialog.dismiss();
                        if ("-1".equals(reasonList.get(position).getReasonID())) {
                            Intent intent = new Intent(StudentGradeActivity.this, ReasonEditActivity.class);
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
            T.showShort(StudentGradeActivity.this, result[1]);
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
            if (!StringUtils.isEmpty(result)) {
                JSONArray resultArray = new JSONArray(result);
                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject reasonObj = resultArray.optJSONObject(i);
                    String reasonID = reasonObj.optString("id");
                    String reasonTitle = reasonObj.optString("title");
                    reasonList.add(new CommendReasonInfo(reasonID, reasonTitle));
                }
                // 添加编辑理由的功能
                reasonList.add(new CommendReasonInfo("-1", "编辑理由"));
            }
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
                    PreferencesUtils.putString(StudentGradeActivity.this, "CommendReason", result);
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
