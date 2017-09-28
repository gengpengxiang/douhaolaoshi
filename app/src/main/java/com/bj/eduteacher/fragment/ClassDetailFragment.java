package com.bj.eduteacher.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.XRefreshViewFooter;
import com.bj.eduteacher.BaseFragment2;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.BadgeDetailActivity;
import com.bj.eduteacher.activity.BadgeProDetailActivity;
import com.bj.eduteacher.activity.CommendDetailActivity;
import com.bj.eduteacher.activity.RankListActivity;
import com.bj.eduteacher.activity.StudentAllActivity;
import com.bj.eduteacher.activity.StudentDetailActivity;
import com.bj.eduteacher.activity.StudentGradeActivity;
import com.bj.eduteacher.activity.ThanksDetailActivity;
import com.bj.eduteacher.adapter.LatestNewsHomeAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ClassItemInfo;
import com.bj.eduteacher.entity.ClassNewsInfo;
import com.bj.eduteacher.entity.TeacherInfo;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
 * Created by zz379 on 2017/3/7.
 */
public class ClassDetailFragment extends BaseFragment2 {

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private boolean isCached = false;
    private boolean isLazyLoad = true;

    private String classId;
    private String className;
    private String userPhoneNumber;

    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;

    private LatestNewsHomeAdapter mAdapter;
    private int currentPage = 1;
    public static long lastRefreshTime;
    private List<ClassNewsInfo> mDataList = new ArrayList<>();

    private FrameLayout flAllStudent;
    private View headerView;

    private TextView tvClasScore, tvClasBadge, tvClasRank, tvClasName;
    private TextView tvClasGradeNum1, tvClasGradeNum2, tvClasGradeNum3, tvClasGradeNum4, tvClasGradeNum5;
    private TextView tvClasGradeName1, tvClasGradeName2, tvClasGradeName3, tvClasGradeName4, tvClasGradeName5;
    RelativeLayout rlSchoolRank;

    private TextView tvClasXueke;
    private TextView tvClasSpecialBadge;
    private LmsDataService mService;

    private SimpleDraweeView ivSchoolImg;
    private String schoolImg, schoolID;
    private long currMillis = 0;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public static ClassDetailFragment newInstance(String classId, String className, boolean isLazyLoad) {
        ClassDetailFragment fragment = new ClassDetailFragment();
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_CLASS_ID, classId);
        args.putString(MLConfig.KEY_CLASS_NAME, className);
        fragment.setArguments(args);
        fragment.isLazyLoad = isLazyLoad;
        return fragment;
    }

    public ClassDetailFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        classId = args.getString(MLConfig.KEY_CLASS_ID);
        className = args.getString(MLConfig.KEY_CLASS_NAME);
        userPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LL.i("onCreateView...");
        View view = inflater.inflate(R.layout.layout_refresh_view, container, false);
        ButterKnife.bind(this, view);
        mService = new LmsDataService();
        initView();

        isPrepared = true;
        lazyLoad();
        return view;
    }

    @Override
    protected void lazyLoad() {
        if (!isLazyLoad) {
            initData();
            return;
        }

        if (!isPrepared || !isVisible || isCached) {
            return;
        }
        //填充各控件的数据
        initData();
    }

    private void initView() {
        mRecyclerView.setHasFixedSize(true);

        // look as listview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // set Adatper
        mAdapter = new LatestNewsHomeAdapter(mDataList);
        mAdapter.setOnMyItemClickListener(new LatestNewsHomeAdapter.OnMyItemClickListener() {
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
        headerView = mAdapter.setHeaderView(R.layout.recycler_header_class_detail, mRecyclerView);
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
                currentPage = 1;
                mXRefreshView.setAutoLoadMore(true);
                mXRefreshView.setPullLoadEnable(true);
                getData();
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                LL.i("加载更多数据");
                currentPage++;
                getClassAllNews(String.valueOf(currentPage));
            }
        });
    }

    private void initData() {
        tvClasName.setText(className);
        schoolImg = PreferencesUtils.getString(getActivity(), MLProperties.BUNDLE_KEY_SCHOOL_IMG, "");
        schoolID = PreferencesUtils.getString(getActivity(), MLProperties.BUNDLE_KEY_SCHOOL_CODE, "");

        mDataList.clear();
        mXRefreshView.startRefresh();

        String commendReasons = PreferencesUtils.getString(getActivity(), "CommendReason");
        if (StringUtils.isEmpty(commendReasons) || commendReasons.equals("[]")) {
            getCommendReason();
        }
    }

    private void getData() {
        getClassData();
        getClassAllNews(String.valueOf(currentPage));
        // 获取活力榜是否显示
        getSchoolRankListStatus();

        if (!StringUtils.isEmpty(schoolImg)) {
            ivSchoolImg.setImageURI(Uri.parse(schoolImg));
        } else {
            getTeacherInfo(userPhoneNumber);
        }
    }

    private void initHeaderView() {
        flAllStudent = (FrameLayout) headerView.findViewById(R.id.itemContainer);
        flAllStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2ClassStudentAllList(1);
            }
        });
        // 班级信息
        tvClasScore = (TextView) headerView.findViewById(R.id.tv_clasScore);
        tvClasBadge = (TextView) headerView.findViewById(R.id.tv_clasBadge);
        tvClasSpecialBadge = (TextView) headerView.findViewById(R.id.tv_specialBadge);
        tvClasRank = (TextView) headerView.findViewById(R.id.tv_clasRank);
        tvClasName = (TextView) headerView.findViewById(R.id.tv_className);
        tvClasXueke = (TextView) headerView.findViewById(R.id.tv_dengji);
        ivSchoolImg = (SimpleDraweeView) headerView.findViewById(R.id.img_classBg);

        tvClasGradeNum1 = (TextView) headerView.findViewById(R.id.tv_clasGradeNum1);
        tvClasGradeNum2 = (TextView) headerView.findViewById(R.id.tv_clasGradeNum2);
        tvClasGradeNum3 = (TextView) headerView.findViewById(R.id.tv_clasGradeNum3);
        tvClasGradeNum4 = (TextView) headerView.findViewById(R.id.tv_clasGradeNum4);
        tvClasGradeNum5 = (TextView) headerView.findViewById(R.id.tv_clasGradeNum5);

        tvClasGradeName1 = (TextView) headerView.findViewById(R.id.tv_clasGradeName1);
        tvClasGradeName2 = (TextView) headerView.findViewById(R.id.tv_clasGradeName2);
        tvClasGradeName3 = (TextView) headerView.findViewById(R.id.tv_clasGradeName3);
        tvClasGradeName4 = (TextView) headerView.findViewById(R.id.tv_clasGradeName4);
        tvClasGradeName5 = (TextView) headerView.findViewById(R.id.tv_clasGradeName5);

        initHeaderClassInfo();
    }

    private void intent2ClassStudentAllList(int pageIndex) {
        Intent intent = new Intent(getActivity(), StudentAllActivity.class);
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_CLASS_ID, classId);
        args.putInt(MLProperties.BUNDLE_KEY_VIEWPAGER_INDEX, pageIndex);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void intent2ClassStudentGradeList(String gradeType, String gradeName) {
        Intent intent = new Intent(getActivity(), StudentGradeActivity.class);
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_CLASS_ID, classId);
        args.putString(MLConfig.KEY_GRADE_ID, gradeType);
        args.putString(MLConfig.KEY_GRADE_NAME, gradeName);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void initHeaderClassInfo() {
        LinearLayout flBadge = ButterKnife.findById(headerView, R.id.ll_kid_badge);
        LinearLayout flSpecialBadge = ButterKnife.findById(headerView, R.id.ll_special_badge);
        LinearLayout flCommend = ButterKnife.findById(headerView, R.id.ll_kid_commend);
        LinearLayout flThanks = ButterKnife.findById(headerView, R.id.ll_kid_thanks);
        TextView tvCommendStudent = ButterKnife.findById(headerView, R.id.tv_commend_for_student);

        flBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBadgeClick();
            }
        });
        flSpecialBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionBadgeProClick();
            }
        });
        flCommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionCommendClick();
            }
        });
        flThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionThanksClick();
            }
        });
        tvCommendStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2ClassStudentAllList(0);
            }
        });

        LinearLayout llGradeType1 = ButterKnife.findById(headerView, R.id.ll_grade_type_1);
        LinearLayout llGradeType2 = ButterKnife.findById(headerView, R.id.ll_grade_type_2);
        LinearLayout llGradeType3 = ButterKnife.findById(headerView, R.id.ll_grade_type_3);
        LinearLayout llGradeType4 = ButterKnife.findById(headerView, R.id.ll_grade_type_4);
        LinearLayout llGradeType5 = ButterKnife.findById(headerView, R.id.ll_grade_type_5);

        llGradeType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2ClassStudentGradeList("1", "全部" + tvClasGradeName1.getText().toString());
            }
        });
        llGradeType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2ClassStudentGradeList("2", "全部" + tvClasGradeName2.getText().toString());
            }
        });
        llGradeType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2ClassStudentGradeList("3", "全部" + tvClasGradeName3.getText().toString());
            }
        });
        llGradeType4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2ClassStudentGradeList("4", "全部" + tvClasGradeName4.getText().toString());
            }
        });
        llGradeType5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent2ClassStudentGradeList("5", "全部" + tvClasGradeName5.getText().toString());
            }
        });

        rlSchoolRank = (RelativeLayout) headerView.findViewById(R.id.rl_school_rank);
        rlSchoolRank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSchoolRankClick();
            }
        });
    }

    private void actionSchoolRankClick() {
        Intent intent = new Intent(getActivity(), RankListActivity.class);
        startActivity(intent);
    }

    private void actionThanksClick() {
        Intent intent = new Intent(getActivity(), ThanksDetailActivity.class);
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_CLASS_ID, classId);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void actionCommendClick() {
        Intent intent = new Intent(getActivity(), CommendDetailActivity.class);
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_CLASS_ID, classId);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void actionBadgeProClick() {
        Intent intent = new Intent(getActivity(), BadgeProDetailActivity.class);
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_CLASS_ID, classId);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void actionBadgeClick() {
        Intent intent = new Intent(getActivity(), BadgeDetailActivity.class);
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_CLASS_ID, classId);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void intent2StudentDetailActivity(String studentId, String studentImg) {
        Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
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

    private void getClassData() {
        Observable.create(new ObservableOnSubscribe<ClassItemInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ClassItemInfo> emitter) throws Exception {
                if (emitter.isDisposed()) return;
                ClassItemInfo clas = mService.getClassDataFromAPI(classId, userPhoneNumber);
                emitter.onNext(clas);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ClassItemInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(ClassItemInfo result) {
                        isCached = true;
                        if (!StringUtils.isEmpty(result.getErrorCode()) && result.getErrorCode().equals("1")) {
                            // 更新数据
                            upDataClassDataInfo(result);
                        } else {
                            T.showShort(getActivity(), result.getMessage());
                        }
                        cleanXRefreshView();
                    }

                    @Override
                    public void onError(Throwable e) {
                        cleanXRefreshView();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void upDataClassDataInfo(ClassItemInfo result) {
        if (!StringUtils.isEmpty(result.getClasImg())) {
            PreferencesUtils.putString(getActivity(), MLProperties.BUNDLE_KEY_TEACHER_IMG, result.getClasImg());
        }
        tvClasScore.setText(String.format("点赞%s", (StringUtils.isEmpty(result.getClasScoreNum()) ||
                result.getClasScoreNum().equals("0")) ? "" : " " + result.getClasScoreNum()));
        tvClasBadge.setText(String.format("徽章%s", (StringUtils.isEmpty(result.getClasBadgeNum()) ||
                result.getClasBadgeNum().equals("0")) ? "" : " " + result.getClasBadgeNum()));
        tvClasSpecialBadge.setText(String.format("专项%s", (StringUtils.isEmpty(result.getClasBadgeProNum()) ||
                result.getClasBadgeProNum().equals("0")) ? "" : " " + result.getClasBadgeProNum()));
        tvClasRank.setText(String.format("感谢%s", (StringUtils.isEmpty(result.getClasGanXieNum()) ||
                result.getClasGanXieNum().equals("0")) ? "" : " " + result.getClasGanXieNum()));
        tvClasXueke.setText(StringUtils.isEmpty(result.getClasXueke()) ? "语文" : result.getClasXueke());

        tvClasGradeNum1.setText(result.getClasGradeNum1());
        tvClasGradeNum2.setText(result.getClasGradeNum2());
        tvClasGradeNum3.setText(result.getClasGradeNum3());
        tvClasGradeNum4.setText(result.getClasGradeNum4());
        tvClasGradeNum5.setText(result.getClasGradeNum5());

        tvClasGradeName1.setText(result.getClasGradeName1());
        tvClasGradeName2.setText(result.getClasGradeName2());
        tvClasGradeName3.setText(result.getClasGradeName3());
        tvClasGradeName4.setText(result.getClasGradeName4());
        tvClasGradeName5.setText(result.getClasGradeName5());
    }

    private void getClassAllNews(final String pageIndex) {
        Observable.create(new ObservableOnSubscribe<List<ClassNewsInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ClassNewsInfo>> emitter) throws Exception {
                List<ClassNewsInfo> infoList = mService.getClassAllNewsFromAPI(classId, pageIndex);
                emitter.onNext(infoList);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ClassNewsInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // disposables.add(d);
                    }

                    @Override
                    public void onNext(List<ClassNewsInfo> result) {
                        loadData(result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        cleanXRefreshView();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
            mAdapter.setCustomLoadMoreView(new XRefreshViewFooter(getActivity()));
        }

        if (list != null && list.size() == 0) {

        }
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    private void getCommendReason() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                if (emitter.isDisposed()) return;
                String result = mService.getCommendReasonFromAPI(userPhoneNumber);
                PreferencesUtils.putString(getActivity(), "CommendReason", result);
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(String s) {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        MobclickAgent.onPageStart("home_class");
    }

    @Override
    protected void onInvisible() {
        super.onInvisible();
        MobclickAgent.onPageEnd("home_class");
    }

    @Override
    public void onPause() {
        super.onPause();
        LL.i("ClassDetailFragment -- onPause()");
        disposables.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LL.i("ClassDetailFragment -- onDestroy()");
    }

    /**
     * 获取是否显示全校排行榜数据的入口
     */
    private void getSchoolRankListStatus() {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> emitter) throws Exception {
                if (emitter.isDisposed()) return;
                String[] result = mService.getSchoolRankListStatusFromAPI(schoolID);
                emitter.onNext(result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(String[] strings) {
                        if (!StringUtils.isEmpty(strings[0]) && strings[0].equals("1")
                                && strings[2].equals("1")) {
                            rlSchoolRank.setVisibility(View.VISIBLE);
                        } else {
                            rlSchoolRank.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        cleanXRefreshView();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getTeacherInfo(final String phoneNumber) {
        Observable.create(new ObservableOnSubscribe<TeacherInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<TeacherInfo> emitter) throws Exception {
                if (emitter.isDisposed()) return;
                TeacherInfo teacherInfo = mService.getTeacherInfoFromAPI2(phoneNumber);
                emitter.onNext(teacherInfo);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TeacherInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(TeacherInfo teacherInfo) {
                        schoolImg = teacherInfo.getSchoolImg();
                        PreferencesUtils.putString(getActivity(), MLProperties.BUNDLE_KEY_CLASS_NAME, teacherInfo.getTeacherName());
                        PreferencesUtils.putString(getActivity(), MLProperties.PREFER_KEY_USER_ID, teacherInfo.getTeacherPhoneNumber());
                        PreferencesUtils.putString(getActivity(), MLProperties.BUNDLE_KEY_TEACHER_IMG, teacherInfo.getTeacherImg());
                        PreferencesUtils.putString(getActivity(), MLProperties.BUNDLE_KEY_SCHOOL_NAME, teacherInfo.getSchoolName());
                        PreferencesUtils.putString(getActivity(), MLProperties.BUNDLE_KEY_SCHOOL_CODE, teacherInfo.getSchoolCode());
                        PreferencesUtils.putString(getActivity(), MLProperties.BUNDLE_KEY_SCHOOL_IMG, teacherInfo.getSchoolImg());
                        if (!StringUtils.isEmpty(schoolImg)) {
                            ivSchoolImg.setImageURI(Uri.parse(schoolImg));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        cleanXRefreshView();
                        T.showShort(getActivity(), "服务器开小差了，请待会重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
