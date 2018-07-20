package com.bj.eduteacher.group.detail.fragment.member;

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
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.community.main.adapter.SpacesItemDecoration;
import com.bj.eduteacher.utils.PreferencesUtils;
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
import static com.bj.eduteacher.api.Urls.GROUPCHENGYUAN;

/**
 * Created by Administrator on 2018/5/2 0002.
 */

public class MemberFragment extends BaseFragment {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;
    private List<Member.DataBean> list = new ArrayList<>();
    private MemberAdapter adapter;
    private String groupid;
    private String teacherPhoneNumber;
    private int currentPage = 1;
    private int num;
    private int mCurrentCounter = 0;
    private String unionid;

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        //groupid = getActivity().getIntent().getStringExtra("groupid");
        if(getActivity().getIntent().getStringExtra("groupid")!=null){
            groupid = getActivity().getIntent().getStringExtra("groupid");
        }else {
            groupid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID,"");
        }


        unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID,"");
        teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));
        adapter = new MemberAdapter(R.layout.recycler_item_group_member, list);
        //adapter.setEmptyView(R.layout.recycler_item_comment_empty,mRecyclerView);
        //adapter.disableLoadMoreIfNotFullPage();
        adapter.disableLoadMoreIfNotFullPage(mRecyclerView);

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
//                            getMemberList(String.valueOf(currentPage));
                            getMemberList(String.valueOf(currentPage),unionid);
                        }
                    }
                }, 500);
            }
        }, mRecyclerView);

        mRecyclerView.setAdapter(adapter);

        getMemberList(String.valueOf("1"),unionid);

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
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }


    private void getMemberList(final String pageIndex,final String unionid) {
        Observable.create(new ObservableOnSubscribe<Member>() {
            @Override
            public void subscribe(final ObservableEmitter<Member> e) throws Exception {
                OkGo.<String>post(BASE_URL + GROUPCHENGYUAN)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("usercode", teacherPhoneNumber)
                        .params("groupid", groupid)
                        .params("limit", "10")
                        .params("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * 10))
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("小组成员", str);

                                Member member = JSON.parseObject(str, new TypeReference<Member>() {
                                });
                                e.onNext(member);
                                e.onComplete();

                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Member>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.e("小组成员", "onSubscribe");
                    }

                    @Override
                    public void onNext(Member dataBeans) {

                        if(dataBeans.getRet().equals("1")){
                            if(dataBeans.getData()!=null){
                                list.addAll(dataBeans.getData());
                                mCurrentCounter = adapter.getData().size();
                                adapter.setNewData(list);
                                //adapter.notifyDataSetChanged();
                                adapter.loadMoreComplete();
                            }else {
                                adapter.loadMoreEnd(true);
                            }
                        }
//                        list.addAll(dataBeans);
//
//                        mCurrentCounter = adapter.getData().size();
//
//                        adapter.setNewData(list);
//                        //adapter.notifyDataSetChanged();
//                        adapter.loadMoreComplete();

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("groupmembernum")) {
            num = event.getNum();
        }
        if (event.getAction().equals("refreshPage")) {
            currentPage = 1;

                list.clear();
                getMemberList("1",unionid);

        }
        if (event.getAction().equals("groupid")) {
            currentPage = 1;
            groupid = event.getDoubisum();
            list.clear();
            getMemberList("1",unionid);
        }
    }
}
