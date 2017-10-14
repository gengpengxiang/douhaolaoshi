package com.bj.eduteacher.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.LatestNewsAdapter2;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.dialog.CommendReasonAlertDialog;
import com.bj.eduteacher.dialog.CommendReasonInfo;
import com.bj.eduteacher.entity.ClassNewsInfo;
import com.bj.eduteacher.entity.KidDataInfo;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.facebook.drawee.view.SimpleDraweeView;
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

public class StudentDetailActivity extends BaseActivity implements View.OnClickListener {

    private XRefreshView mXRefreshView;
    private RecyclerView mRecyclerView;
    private LatestNewsAdapter2 mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;
    private List<ClassNewsInfo> mDataList = new ArrayList<>();
    private MyAsyncTask myAsyncTask;
    private String classId;
    private String className;
    private TextView tvTitle;
    private String userPhoneNumber;

    private LinearLayout llCommendStudent;
    private View headerView;

    private SimpleDraweeView imgStudPhoto;
    private TextView tvStudPingyu;
    private TextView tvStudScore;
    private TextView tvStudBadge;
    private TextView tvStudGrade;
    private TextView tvStudGradePro;
    private String studId;
    private String studName;
    private String studPhoto;
    private String studScore;
    private String studBadge;
    private String studBadgePro;
    private String studGrade;
    private String studPingyu;

    List<CommendReasonInfo> reasonList = new ArrayList<>();
    private TextView tvAddScore;
    private SimpleDraweeView ivSchoolPic;
    private String schoolImg;

    private long currMillis = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);
        // 初始化页面
        initToolBar();
        initView();
        initData();
    }

    private void initToolBar() {
        TextView tvTitle = (TextView) this.findViewById(R.id.header_tv_title);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("学生主页");

        LinearLayout llHeaderLeft = (LinearLayout) this.findViewById(R.id.header_ll_left);
        llHeaderLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentDetailActivity.this.finish();
            }
        });

        ImageView imgBack = (ImageView) this.findViewById(R.id.header_img_back);
        imgBack.setVisibility(View.VISIBLE);
    }

    private void initView() {
        llCommendStudent = (LinearLayout) this.findViewById(R.id.ll_qrCode);
        llCommendStudent.setOnClickListener(this);
        mXRefreshView = (XRefreshView) this.findViewById(R.id.mXRefreshView);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        // look as listview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // set Adatper
        mAdapter = new LatestNewsAdapter2(mDataList);

        headerView = mAdapter.setHeaderView(R.layout.recycler_header_student_detail_2, mRecyclerView);
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
            public void onRefresh(boolean isPull) {
                LL.i("刷新数据");
                if (myAsyncTask != null && !myAsyncTask.isCancelled()) {
                    myAsyncTask.cancel(true);
                }

                currentPage = 1;
                mXRefreshView.setPullLoadEnable(true);
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute(studId, String.valueOf(currentPage));
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                if (myAsyncTask != null && !myAsyncTask.isCancelled()) {
                    myAsyncTask.cancel(true);
                }

                currentPage++;
                myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute(studId, String.valueOf(currentPage));
            }
        });
    }

    private void initHeaderView() {
        imgStudPhoto = (SimpleDraweeView) headerView.findViewById(R.id.img_kidPhoto);
        ivSchoolPic = (SimpleDraweeView) headerView.findViewById(R.id.img_classBg);
        tvStudPingyu = (TextView) headerView.findViewById(R.id.tv_kid_pingyu);
        tvStudScore = (TextView) headerView.findViewById(R.id.tv_jifen);
        tvStudBadge = (TextView) headerView.findViewById(R.id.tv_huizhang);
        tvStudGrade = (TextView) headerView.findViewById(R.id.tv_dengji);
        tvAddScore = (TextView) headerView.findViewById(R.id.tv_addJifen);
        tvStudGradePro = (TextView) headerView.findViewById(R.id.tv_zhuanxiang);
    }

    private void updateHeaderView() {
        if (!StringUtils.isEmpty(schoolImg)) {
            ivSchoolPic.setImageURI(Uri.parse(schoolImg));
        }
        if (!StringUtils.isEmpty(studPhoto)) {
            imgStudPhoto.setImageURI(Uri.parse(studPhoto));
        }
        // tvStudPingyu.setText(studPingyu);
        // tvStudScore.setText("点赞 " + studScore);
        // tvStudBadge.setText("徽章 " + studBadge);
        tvStudGrade.setText(studGrade + "等级");
    }

    private void initData() {
        userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        schoolImg = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_SCHOOL_IMG, "");

        Bundle args = getIntent().getExtras();
        studId = args.getString(MLConfig.KEY_STUDENT_ID);
        studName = args.getString(MLConfig.KEY_STUDENT_NAME);
        studPhoto = args.getString(MLConfig.KEY_STUDENT_PHOTO);
        studScore = args.getString(MLConfig.KEY_STUDENT_SCORE);
        studBadge = args.getString(MLConfig.KEY_STUDENT_BADGE);
        studGrade = args.getString(MLConfig.KEY_STUDENT_GRADE);
        studPingyu = args.getString(MLConfig.KEY_STUDENT_PINGYU);

        updateHeaderView();

        currentPage = 1;
        mXRefreshView.setPullLoadEnable(true);
        myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute(studId, String.valueOf(currentPage));

        MyGetKidDataTask myGetKidDataTask = new MyGetKidDataTask();
        myGetKidDataTask.execute(studId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("student");
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
        MobclickAgent.onPageEnd("student");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ll_qrCode) {
            MobclickAgent.onEvent(StudentDetailActivity.this, "student_thumb");
            if (System.currentTimeMillis() - currMillis > 1000) {
                CommendReasonAlertDialog dialog = new CommendReasonAlertDialog(this)
                        .setContentRecyclerData(reasonList)
                        .setmItemClickListener(new CommendReasonAlertDialog.onSweetContentItemClickListener() {
                            @Override
                            public void onClick(CommendReasonAlertDialog sweetAlertDialog, int position) {
                                sweetAlertDialog.dismiss();
                                if ("-1".equals(reasonList.get(position).getReasonID())) {
                                    Intent intent = new Intent(StudentDetailActivity.this, ReasonEditActivity.class);
                                    startActivity(intent);
                                } else {
                                    MyCommentTask task = new MyCommentTask();
                                    task.execute(studId, reasonList.get(position).getReasonID(), userPhoneNumber);
                                }
                            }
                        });
                dialog.show();
                currMillis = System.currentTimeMillis();
            }
        }
    }


    private class MyAsyncTask extends AsyncTask<String, Integer, List<ClassNewsInfo>> {

        @Override
        protected List<ClassNewsInfo> doInBackground(String... params) {
            String studId = params[0];
            String pageIndex = params[1];
            LmsDataService mService = new LmsDataService();
            List<ClassNewsInfo> infoList;
            try {
                infoList = mService.getStudentAllNewsFromAPI(studId, pageIndex, studPhoto);
            } catch (Exception e) {
                e.printStackTrace();
                LL.e(e);
                infoList = null;
            }
            return infoList;
        }

        @Override
        protected void onPostExecute(List<ClassNewsInfo> result) {
            if (result == null) {
                cleanXRefreshView();
                T.showShort(StudentDetailActivity.this, "服务器开小差了，请重试");
            } else {
                loadData(result);
            }
        }
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
        if (null == mAdapter.getCustomLoadMoreView()) {
            mAdapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        }
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    private class MyCommentTask extends AsyncTask<String, Integer, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            String studentId = params[0];
            String reasonId = params[1];
            String teacherId = params[2];

            LmsDataService mService = new LmsDataService();
            String[] result;
            try {
                result = mService.commendStudentFromAPI(teacherId, studentId, reasonId, "0");
            } catch (Exception e) {
                e.printStackTrace();
                LL.e(e);
                result = new String[2];
                result[0] = "0";
                result[1] = "服务器开小差了，请待会重试";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String[] result) {
            T.showShort(StudentDetailActivity.this, result[1]);
            if (!StringUtils.isEmpty(result[0]) && result[0].equals("1")) {
                actionAddScore("1");
            }
        }
    }

    private Handler mHandler = new Handler();

    private void actionAddScore(final String value) {
        tvAddScore.setText("+" + value);
        animationTextView(tvAddScore, value);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                studScore = String.valueOf(Integer.valueOf(StringUtils.isEmpty(studScore) ? "0" : studScore) + Integer.valueOf(value));
                tvStudScore.setText("点赞 " + (StringUtils.isEmpty(studScore) ? "0" : studScore));
            }
        }, 900);

    }

    private void animationTextView(final TextView tv, String value) {
        tv.setVisibility(View.VISIBLE);
        AnimationSet animationSet = new AnimationSet(true);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0f);
        alphaAnimation.setDuration(1000);
        animationSet.addAnimation(alphaAnimation);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, -0.3f,
                Animation.RELATIVE_TO_PARENT, -1.0f);
        translateAnimation.setDuration(1000);
        animationSet.addAnimation(translateAnimation);

        tv.startAnimation(animationSet);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setVisibility(View.GONE);
            }
        }, 800);
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

    private class MyGetKidDataTask extends AsyncTask<String, Integer, KidDataInfo> {

        @Override
        protected KidDataInfo doInBackground(String... params) {
            LmsDataService mService = new LmsDataService();
            KidDataInfo kidDataInfo;
            try {
                kidDataInfo = mService.getStudentDataFromAPI(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                LL.e(e);
                kidDataInfo = new KidDataInfo();
                kidDataInfo.setErrorCode("0");
                kidDataInfo.setMessage("服务器开小差了，请待会重试");
            }
            return kidDataInfo;
        }

        @Override
        protected void onPostExecute(KidDataInfo result) {

            String errorCode = result.getErrorCode();
            String message = result.getMessage();
            if (errorCode.equals("0") || errorCode.equals("2")) {
                T.showShort(StudentDetailActivity.this, message);
            } else {
                String kidScore = result.getScore();
                tvStudScore.setText("点赞" + (StringUtils.isEmpty(kidScore) || kidScore.equals("0") ? "" : " " + kidScore));
                studScore = kidScore;

                String kidBadge = result.getBadge();
                tvStudBadge.setText("徽章" + (StringUtils.isEmpty(kidBadge) || kidBadge.equals("0") ? "" : " " + kidBadge));
                studBadge = kidBadge;

                String kidBadgePro = result.getBadgePro();
                tvStudGradePro.setText("专项" + (StringUtils.isEmpty(kidBadgePro) || kidBadgePro.equals("0") ? "" : " " + kidBadgePro));
                studBadgePro = kidBadgePro;

                String kidGrade = result.getGrade();
                tvStudGrade.setText((StringUtils.isEmpty(kidGrade) ? "" : kidGrade) + "等级");
                studGrade = kidGrade;

                String kidPingYu = (StringUtils.isEmpty(result.getPingyu()) ? "你准备好了吗？" : result.getPingyu());
                tvStudPingyu.setText(kidPingYu);
                studPingyu = kidPingYu;
            }
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
                    PreferencesUtils.putString(StudentDetailActivity.this, "CommendReason", result);
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
