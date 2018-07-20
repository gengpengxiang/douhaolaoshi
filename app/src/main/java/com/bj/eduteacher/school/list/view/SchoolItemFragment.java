package com.bj.eduteacher.school.list.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.andview.refreshview.XRefreshView;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.school.detail.view.SchoolDetailActivity;
import com.bj.eduteacher.school.list.adapter.SchoolAdapter;
import com.bj.eduteacher.school.list.model.School;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

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
 * Created by Administrator on 2018/6/14 0014.
 */

public class SchoolItemFragment extends BaseFragment {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    Unbinder unbinder;
    private String province;
    private SchoolAdapter adapter;
    private List<School.DataBean> dataList = new ArrayList<>();
    public static long lastRefreshTime;
    private int currentPage = 1;

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_school_all, container, false);
        unbinder = ButterKnife.bind(this, view);

        province = getArguments().getString("Province");

        getSchools(province,currentPage);
        initViews();
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private void initViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new SchoolAdapter(R.layout.recycler_item_school,dataList);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                School.DataBean bean = dataList.get(position);
                Intent intent = new Intent(getActivity(), SchoolDetailActivity.class);
                intent.putExtra("school",bean);
                startActivity(intent);
            }
        });

        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.addItemDecoration(new RecyclerItemDecoration(PxUtils.dpToPx(18,getActivity()),2));
        mRecyclerView.setAdapter(adapter);

        mXRefreshView.setMoveForHorizontal(true);   // 在手指横向移动的时候，让XRefreshView不拦截事件
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(false);

        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {

                lastRefreshTime = mXRefreshView.getLastRefreshTime();
                currentPage = 1;
                getSchools(province,currentPage);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                currentPage++;
                getSchools(province,currentPage);
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

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void getSchools(final String shengfen,final int pageIndex) {
        Observable.create(new ObservableOnSubscribe<List<School.DataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<School.DataBean>> e) throws Exception {
                OkGo.<String>post(BASE_URL+"index.php/school/index")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("userclient","aphone")
                        .params("limit","10")
                        .params("offset",String.valueOf((pageIndex - 1) * 10))
                        .params("shengfen",shengfen)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("学校列表返回",str);
                                School school = JSON.parseObject(str, new TypeReference<School>() {
                                });

                                e.onNext(school.getData());
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<School.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<School.DataBean> schoolList) {
                        cleanXRefreshView();
                        if(pageIndex==1){
                            dataList.clear();
                            dataList.addAll(schoolList);
                            adapter.notifyDataSetChanged();
                        }else {
                            dataList.addAll(schoolList);
                            adapter.notifyDataSetChanged();
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
}
