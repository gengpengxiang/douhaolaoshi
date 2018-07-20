package com.bj.eduteacher.course.fragment.study;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.ResReviewActivity;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.course.fragment.detail.CourseInfo;
import com.bj.eduteacher.course.view.CourseDetailActivity2;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.community.main.adapter.SpacesItemDecoration;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.videoplayer.view.PlayerActivity;
import com.bj.eduteacher.widget.MyLinearLayoutManager;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.course.fragment.study.CourseRes.DataBean.KcresxxlistBean.FIRST_TYPE;
import static com.bj.eduteacher.course.fragment.study.CourseRes.DataBean.KcresxxlistBean.SECOND_TYPE;
import static com.bj.eduteacher.course.fragment.study.CourseRes.DataBean.KcresxxlistBean.THIRD_TYPE;

/**
 * Created by Administrator on 2018/5/2 0002.
 */

public class StudyFragment extends BaseFragment {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private List<CourseRes.DataBean.KcresxxlistBean> list = new ArrayList<>();
    private StudyAdapter adapter;
    private String courseID;
    private String teacherPhoneNumber;
    private int currentPage = 1;
    private int num;
    private int mCurrentCounter = 0;
    private String unionid;
    private TextView tvPay;
    private CourseDetailActivity2 activity;
    private String beforeResid;
    private String courseBuyStatus;
    private String coursePrice;
    private String courseJiakeStatus = "0";
    private TextView tvNum, tvProgress;

    private int refreshType = 0;

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        activity = (CourseDetailActivity2) getActivity();
        tvPay = (TextView) activity.findViewById(R.id.tv_pay);

        courseID = getActivity().getIntent().getStringExtra("CourseID");
        courseBuyStatus = getActivity().getIntent().getStringExtra("CourseBuyStatus");
        coursePrice = getActivity().getIntent().getStringExtra("CoursePrice");

        unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
        getCourseInfo(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, courseID);

//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setLayoutManager(new MyLinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));

        adapter = new StudyAdapter(R.layout.recycler_item_course_study, list);
        adapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String resID = list.get(position).getResid();

                tvNum = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_num);
                tvProgress = (TextView) adapter.getViewByPosition(mRecyclerView, position, R.id.tv_progress);

                String resName = list.get(position).getTitle();
                String previewUrl = list.get(position).getPreviewurl();
                String downloadUrl = list.get(position).getFileurl();
                String resType = list.get(position).getType();

                if (position != 0 && courseJiakeStatus.equals("0")) {
                    T.showShort(getActivity(), "加入课程后才可学习哦~");
                } else {
//                    if ("2".equals(resType)) {
//                        Intent intent = new Intent(getActivity(), PlayerActivity.class);
//                        intent.putExtra("type", "StudyFragment");
//                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
//                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
//                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
//                        intent.putExtra("kechengid", courseID);
//                        intent.putExtra("currentTime", list.get(position).getCurrentTime());
//                        if (courseJiakeStatus.equals("1")) {
//                            intent.putExtra("jindu", list.get(position).getRes_jindu());
//                        }
//                        intent.putExtra("jiakestatus", courseJiakeStatus);
//                        startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(getActivity(), ResReviewActivity.class);
//                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
//                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
//                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
//                        intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL, downloadUrl);
//                        startActivity(intent);
//                    }

                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra("type", "StudyFragment");
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    intent.putExtra("kechengid", courseID);
                    intent.putExtra("currentTime", list.get(position).getCurrentTime());
                    if (courseJiakeStatus.equals("1")) {
                        intent.putExtra("jindu", list.get(position).getRes_jindu());
                    }
                    intent.putExtra("jiakestatus", courseJiakeStatus);
                    startActivity(intent);
                }
            }
        });

        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mCurrentCounter >= num) {
                            adapter.loadMoreEnd(true);
                        } else {
                            currentPage++;
                            getResourceList(String.valueOf(currentPage), unionid);
                        }
                    }
                }, 500);
            }
        }, mRecyclerView);


        mRecyclerView.setAdapter(adapter);
//        getResourceList(String.valueOf("1"), unionid);
        return view;
    }

    @Override
    protected void bindViews(View view) {
    }

    @Override
    protected void processLogic() {
    }

    @Override
    protected void setListener() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        activity = null;
    }

    private void getResourceList(final String pageIndex, final String unionid) {
        Observable.create(new ObservableOnSubscribe<List<CourseRes.DataBean.KcresxxlistBean>>() {

            @Override
            public void subscribe(final ObservableEmitter<List<CourseRes.DataBean.KcresxxlistBean>> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/kcreslist")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("phone", PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID))
                        .params("kechengid", courseID)
                        .params("limit", "10")
                        .params("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * 10))
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("课程学习", str);

                                CourseRes courseRes = JSON.parseObject(str, new TypeReference<CourseRes>() {
                                });

                                tvPay.setText(courseRes.getData().getKc_jindu());

                                beforeResid = courseRes.getData().getBefore_resid();
                                String beforeResurl = courseRes.getData().getBefore_resurl();
                                String beforeResCurrentTime = courseRes.getData().getBefore_rescurrentTime();
                                //String beforeRestype = courseRes.getData().getBefore_restype();
                                //PreferencesUtils.putString(getActivity(),"beforeRestype",beforeRestype);
                                PreferencesUtils.putString(getActivity(),"beforeResid",beforeResid);
                                PreferencesUtils.putString(getActivity(),"beforeResurl",beforeResurl);
                                PreferencesUtils.putString(getActivity(),"beforeResCurrentTime",beforeResCurrentTime);

                                e.onNext(courseRes.getData().getKcresxxlist());
                                e.onComplete();

                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CourseRes.DataBean.KcresxxlistBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("学习资源列表", "onSubscribe");
                    }

                    @Override
                    public void onNext(List<CourseRes.DataBean.KcresxxlistBean> dataBeans) {

                        if (courseJiakeStatus.equals("1")) {
                            Log.e("第一个方法", "111");
                            for (int i = 0; i < dataBeans.size(); i++) {
                                dataBeans.get(i).setMyType(THIRD_TYPE);
                            }
                        } else {
                            for (int i = 0; i < dataBeans.size(); i++) {
                                if (i == 0) {
                                    Log.e("第二个方法", "444");
                                    dataBeans.get(i).setMyType(FIRST_TYPE);
                                } else {
                                    dataBeans.get(i).setMyType(SECOND_TYPE);
                                }
                            }
                        }

                        if(pageIndex.equals("1")){
//                            list.clear();
//                            list.addAll(dataBeans);
                            if(refreshType==0){
                                list.clear();
                                list.addAll(dataBeans);
                                mCurrentCounter = adapter.getData().size();
                                adapter.notifyDataSetChanged();
                                adapter.loadMoreComplete();
                            }
                            if(refreshType==1){
                                list.clear();
                                list.addAll(dataBeans);
                                mCurrentCounter = adapter.getData().size();
                                adapter.setNewData(list);
//                                adapter.notifyDataSetChanged();
                                adapter.loadMoreComplete();
                            }
                        }else {
                            list.addAll(dataBeans);
                        }


//                        mCurrentCounter = adapter.getData().size();
//                        adapter.notifyDataSetChanged();
//                        adapter.loadMoreComplete();

                        //add
                        String beforeResid = PreferencesUtils.getString(getActivity(), "beforeResid", "");
                        if (courseJiakeStatus.equals("1")) {
                            for (int i = 0; i < dataBeans.size(); i++) {
                                if(dataBeans.get(i).getResid().equals(beforeResid)){
                                    PreferencesUtils.putString(getActivity(),"beforeResjindu",dataBeans.get(i).getRes_jindu());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.loadMoreComplete();
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    public void getCourseInfo(final String phone, final String unionid, final String kechengid) {
        Observable.create(new ObservableOnSubscribe<CourseInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<CourseInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/kecheng")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("phone", phone)
                        .params("kechengid", kechengid)
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("课程信息", str);
                                CourseInfo courseInfo = JSON.parseObject(str, new TypeReference<CourseInfo>() {
                                });
                                e.onNext(courseInfo);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CourseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(CourseInfo courseInfo) {
                        courseJiakeStatus = courseInfo.getData().getJiakestatus();
                        getResourceList(String.valueOf("1"), unionid);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("progressuploadsuccess")) {
            Log.e("上传进度接收", "true");

            refreshType = 1;

            getResourceList("1", unionid);
        }
        if (event.getAction().equals("playfinish")) {
            Log.e("上传进度接收", "true");
        }
        if (event.getAction().equals("groupmembernum")) {
            num = event.getNum();
        }
        if (event.getAction().equals("refreshpage")) {
            currentPage = 1;
            if (event.getNum() == 1) {
                list.clear();
                refreshType = 1;
                getResourceList("1", unionid);
            }
        }
        if (event.getAction().equals("courseStudyBuySuccess")) {
            currentPage = 1;
            list.clear();
            getCourseInfo(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, courseID);
            //getResourceList("1", unionid);
        }
        if (event.getAction().equals("wxloginsuccess")) {
            currentPage = 1;
            list.clear();
            getCourseInfo(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, courseID);
        }
        if (event.getAction().equals("phoneloginsuccess")) {
            currentPage = 1;
            list.clear();
            getCourseInfo(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, courseID);
        }
        if(event.getAction().equals("phonebindsuccess")){
            currentPage = 1;
            list.clear();
            getCourseInfo(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID), unionid, courseID);
        }
    }

}
