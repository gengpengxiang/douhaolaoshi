package com.bj.eduteacher.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.CourseAdapter;
import com.bj.eduteacher.adapter.DoukeListAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.course.view.CourseDetailActivity2;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.view.OnRecyclerItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 课程界面
 */

public class CourseAllActivity extends BaseActivity {

    @BindView(R.id.header_tv_title)
    TextView headerTvTitle;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.header_img_back)
    ImageView headerImgBack;
    private Unbinder unbinder;
    public static long lastRefreshTime;
    private int currentPage = 1;
    private String teacherPhoneNumber;
    private CourseAdapter mAdapter;
    private List<ArticleInfo> mDataList = new ArrayList<>();
    private String unionid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_all);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        //改变底部导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }

        initViews();
        initDatas();
        getCourseList(currentPage);
    }

    private void initViews() {
        headerTvTitle.setVisibility(View.VISIBLE);
        headerTvTitle.setText("课程");
        headerImgBack.setVisibility(View.VISIBLE);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CourseAdapter(R.layout.recycler_item_course,mDataList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
//                Intent intent = new Intent(CourseAllActivity.this, CourseDetailActivity.class);
                Intent intent = new Intent(CourseAllActivity.this, CourseDetailActivity2.class);
                Bundle args = new Bundle();
                args.putString("CourseID", mDataList.get(position).getArticleID());
                args.putString("CourseTitle", mDataList.get(position).getTitle());
                args.putString("CourseLearnNum", mDataList.get(position).getReplyCount());
                args.putString("CourseResNum", mDataList.get(position).getReadNumber());
                args.putString("CoursePicture", mDataList.get(position).getArticlePicture());

                args.putString("CoursePrice", mDataList.get(position).getAgreeNumber());
                args.putString("CourseBuyStatus", mDataList.get(position).getCommentNumber());

                //add
                args.putString("CourseJiakeStatus", mDataList.get(position).getJiakeStatus());

                Log.e("jiakestatus11111","11111=="+mDataList.get(position).getJiakeStatus());

                args.putString("CourseDesc", mDataList.get(position).getAuthDesc());
                args.putString("CourseZhengshu", mDataList.get(position).getAuthImg());
                args.putString("CourseShuoming", mDataList.get(position).getContent());

                intent.putExtras(args);
                startActivity(intent);
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder holder, int position) {

            }
        });

        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(false);
        mXRefreshView.setEmptyView(R.layout.recycler_item_douke_empty);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                lastRefreshTime = mXRefreshView.getLastRefreshTime();
                currentPage = 1;
                getCourseList(currentPage);

            }

            @Override
            public void onLoadMore(boolean isSilence) {
                currentPage++;
                getCourseList(currentPage);
            }
        });
    }

    private void getCourseList(final int pageIndex) {

        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");

        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {
                try {
                    LmsDataService mService = new LmsDataService();
                    //List<ArticleInfo> dataList = mService.getDouKeListFromAPI(currentPage, teacherPhoneNumber);
                    List<ArticleInfo> dataList = mService.getHomePageCourseList2(teacherPhoneNumber, unionid, String.valueOf(pageIndex));
                    e.onNext(dataList);
                    e.onComplete();
                } catch (InterruptedIOException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                } catch (InterruptedException ex) {
                    if (!e.isDisposed()) {
                        e.onError(ex);
                        return;
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> result) {

                        //loadData(result);
                        cleanXRefreshView();
                        if(currentPage==1){
                            mDataList.clear();
                            mDataList.addAll(result);
                            mAdapter.notifyDataSetChanged();
                        }else {
                            mDataList.addAll(result);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        cleanXRefreshView();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    private void initDatas() {
        teacherPhoneNumber = PreferencesUtils.getString(CourseAllActivity.this, MLProperties.PREFER_KEY_USER_ID, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.header_img_back)
    public void onClick() {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("courseStudyBuySuccess")) {
            mDataList.clear();
            getCourseList(1);
        }
        if (event.getAction().equals("phoneloginsuccess")) {
            mDataList.clear();
            getCourseList(1);
        }
        if (event.getAction().equals("wxloginsuccess")) {
            mDataList.clear();
            getCourseList(1);
        }
    }

}
