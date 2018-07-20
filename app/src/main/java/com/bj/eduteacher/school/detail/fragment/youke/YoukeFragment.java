package com.bj.eduteacher.school.detail.fragment.youke;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.andview.refreshview.XRefreshView;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.DoukeNewDetailActivity;
import com.bj.eduteacher.adapter.DoukeNewAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.school.detail.view.SchoolDetailActivity;
import com.bj.eduteacher.school.list.adapter.SchoolAdapter;
import com.bj.eduteacher.school.list.model.School;
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

/**
 * Created by Administrator on 2018/6/15 0015.
 */

public class YoukeFragment extends BaseFragment {
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private String schoolID;
    public static long lastRefreshTime;
    private int currentPage = 1;
    private YoukeAdapter adapter;

    private DoukeNewAdapter mAdapter;

    private List<ArticleInfo> dataList = new ArrayList<>();

    private List<YoukeInfo.DataBean> newList = new ArrayList<>();

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.layout_recycler_white, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        School.DataBean school = (School.DataBean) getActivity().getIntent().getSerializableExtra("school");
        schoolID = school.getId();

        initViews();
        getYoukeList(schoolID, currentPage);
        return view;
    }

    private void initViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 3);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new YoukeAdapter(R.layout.recycler_item_school_youke, newList);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                YoukeInfo.DataBean bean = newList.get(position);

//                Intent intent = new Intent(getActivity(), SchoolDetailActivity.class);
//                intent.putExtra("school",bean);
//                startActivity(intent);

                Intent intent = new Intent(getActivity(), DoukeNewDetailActivity.class);
                intent.putExtra("Title", bean.getName());
                intent.putExtra("ID", bean.getId());
                startActivity(intent);
            }
        });

        mAdapter = new DoukeNewAdapter(dataList);
        mAdapter.setOnMyItemClickListener(new DoukeNewAdapter.OnMyItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                ArticleInfo item = dataList.get(position);
                Intent intent = new Intent(getActivity(), DoukeNewDetailActivity.class);
                intent.putExtra("Title", item.getTitle());
                intent.putExtra("ID", item.getArticleID());
                startActivity(intent);
            }
        });

        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.addItemDecoration(new RecyclerItemDecoration(PxUtils.dpToPx(18,getActivity()),2));
//        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setAdapter(adapter);

    }

    private void getYoukeList(final String id, final int page) {
        Observable.create(new ObservableOnSubscribe<List<YoukeInfo.DataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<YoukeInfo.DataBean>> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/school/youke")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("userclient", "aphone")
                        .params("schoolid", schoolID)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                YoukeInfo youkeInfo = JSON.parseObject(str,new TypeReference<YoukeInfo>(){});
                                e.onNext(youkeInfo.getData());
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<YoukeInfo.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<YoukeInfo.DataBean> dataBeans) {
                        if(page==1){
                            newList.clear();
                            newList.addAll(dataBeans);
                            adapter.setNewData(newList);
                        }else {
                            newList.addAll(dataBeans);
                            adapter.notifyDataSetChanged();
                        }
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
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("refreshschool")) {
            if (event.getNum() == 2) {
                currentPage = 1;
                getYoukeList(schoolID, currentPage);
            }

        }
    }
}
