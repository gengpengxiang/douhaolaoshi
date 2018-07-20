package com.bj.eduteacher.group.detail.fragment.task;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.ResReviewActivity;
import com.bj.eduteacher.answer.view.AnswerHomeActivity;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.community.main.adapter.SpacesItemDecoration;
import com.bj.eduteacher.course.view.CourseDetailActivity2;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.videoplayer.view.PlayerActivity;
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

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;
import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

/**
 * Created by Administrator on 2018/6/22 0022.
 */

public class TaskFragment extends BaseFragment {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private String groupid;
    private String unionid;
    private String phone;
    private TaskAdapter adapter;
    private List<TaskInfo.DataBean.RenwuBean> dataList = new ArrayList<>();
    private int currentPage = 1;
    private int refreshType = 0;

    private LinearLayoutManager linearLayoutManager;

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (getActivity().getIntent().getStringExtra("groupid") != null) {
            groupid = getActivity().getIntent().getStringExtra("groupid");
        } else {
            groupid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID, "");
        }

        initViews();

        return view;
    }

    private void initViews() {

        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));
        adapter = new TaskAdapter(R.layout.recycler_item_task, dataList);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (LoginStatusUtil.noLogin(getActivity())) {
                    IntentManager.toLoginSelectActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);

                } else {
                    TaskInfo.DataBean.RenwuBean bean = dataList.get(position);
                    String type = bean.getType();
                    if (type.equals("1")) {
                        Intent intent = new Intent(getActivity(), DiscussTaskActivity.class);
                        intent.putExtra("id", dataList.get(position).getCaozuoid());
                        intent.putExtra("img",dataList.get(position).getAuthorimg_url());
                        intent.putExtra("nicheng",dataList.get(position).getNicheng());
                        intent.putExtra("time",dataList.get(position).getTime());
                        startActivity(intent);
                    }
                    if (type.equals("2")) {
                        if (bean.getRestype().equals("1")) {//PPT
                            Intent intent = new Intent(getActivity(), ResReviewActivity.class);
                            intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, bean.getCaozuoid());
                            intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, "");
                            intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, bean.getPreviewurl());
                            intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL, BASE_RESOURCE_URL+bean.getFileurl());
                            //add
                            intent.putExtra("type", "TaskFragment");
                            startActivity(intent);
                        }
                        if (bean.getRestype().equals("2")) {//Video
                            Intent intent = new Intent(getActivity(), PlayerActivity.class);
                            intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, bean.getCaozuoid());

                            intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, bean.getPreviewurl());
                            intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_VID, bean.getVideoid_ali());
                            intent.putExtra("type", "TaskFragment");
                            startActivity(intent);
                        }
                    }
                    if (type.equals("3")) {
                        Intent intent = new Intent(getActivity(), CourseDetailActivity2.class);
                        Bundle args = new Bundle();
                        args.putString("CourseID", bean.getCaozuoid());
                        args.putString("CourseTitle", "");
                        args.putString("CourseLearnNum", "");
                        args.putString("CourseResNum", "");
                        args.putString("CoursePicture", "");
                        args.putString("CoursePrice", "");
                        args.putString("CourseBuyStatus", "");

                        //add
                        args.putString("CourseJiakeStatus", "");
                        args.putString("CourseDesc", "");
                        args.putString("CourseZhengshu", "");
                        args.putString("CourseShuoming", "");

                        intent.putExtras(args);
                        startActivity(intent);
                    }
                    if (type.equals("4")) {
                        Intent intent = new Intent(getActivity(), AnswerHomeActivity.class);
                        intent.putExtra("examid",bean.getCaozuoid());
                        intent.putExtra("jiezhitime",bean.getJiezhitime());
                        intent.putExtra("jiezhistatus",bean.getJiezhi_status());
                        intent.putExtra("wanchengstatus",bean.getStatus());
                        startActivity(intent);
                    }
                }
            }
        });
        adapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getTaskList(currentPage);
            }
        }, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
    }

    private void moveToPosition(LinearLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        manager.setStackFromEnd(true);
    }

    private void getTaskList(final int page) {
        unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        phone = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/Grenwu/renwulist")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("groupid", groupid)
                        .params("unionid", unionid)
                        .params("usercode", phone)
                        .params("limit", "10")
                        .params("offset", String.valueOf((page - 1) * 10))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {

                                String str = response.body().toString();
                                e.onNext(str);
                                e.onComplete();
                                Log.e("小组任务", str);
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {

                        try{
                            TaskInfo taskInfo = JSON.parseObject(s, new TypeReference<TaskInfo>() {
                            });

                            if (taskInfo.getData().getRenwu() == null) {
                                adapter.loadMoreComplete();
                                adapter.loadMoreEnd(true);

                            } else {
                                if (page == 1) {
                                    if(refreshType==1){
                                        dataList.clear();
                                        dataList.addAll(taskInfo.getData().getRenwu());
                                        adapter.setNewData(dataList);
                                        adapter.loadMoreComplete();
                                    }
                                    if(refreshType==0){
                                        dataList.clear();
                                        dataList.addAll(taskInfo.getData().getRenwu());
                                        adapter.notifyDataSetChanged();
                                        adapter.loadMoreComplete();
                                    }

                                } else {
                                    dataList.addAll(taskInfo.getData().getRenwu());
                                    adapter.notifyDataSetChanged();
                                    adapter.loadMoreComplete();
                                }
                            }
                        }catch (Exception e){

                        }

//                        TaskInfo taskInfo = JSON.parseObject(s, new TypeReference<TaskInfo>() {
//                        });
//
//                        if (taskInfo.getData().getRenwu() == null) {
//                            adapter.loadMoreComplete();
//                            adapter.loadMoreEnd(true);
//
//                        } else {
//                            if (page == 1) {
//                                dataList.clear();
//                                dataList.addAll(taskInfo.getData().getRenwu());
//                                adapter.notifyDataSetChanged();
//                                adapter.loadMoreComplete();
//                            } else {
//                                dataList.addAll(taskInfo.getData().getRenwu());
//                                adapter.notifyDataSetChanged();
//                                adapter.loadMoreComplete();
//                            }
//                        }
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
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshType = 1;
        currentPage = 1;
        getTaskList(1);

        //moveToPosition(linearLayoutManager,10);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("refreshPage")) {

                refreshType = 1;
                dataList.clear();
                currentPage = 1;
                getTaskList(1);

        }
    }
}
