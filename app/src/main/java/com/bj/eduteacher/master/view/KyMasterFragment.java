package com.bj.eduteacher.master.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.andview.refreshview.XRefreshView;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.ZhuanjiaDetailActivity;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.master.adapter.KyMasterAdapter;
import com.bj.eduteacher.master.model.KyMasterInfo;
import com.bj.eduteacher.view.OnRecyclerItemClickListener;
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

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;
import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

/**
 * Created by Administrator on 2018/6/22 0022.
 */

public class KyMasterFragment extends BaseFragment {


    Unbinder unbinder;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    //private String type;
    public static long lastRefreshTime = 0;
    private KyMasterAdapter adapter;
    private List<KyMasterInfo.DataBean.MasterDataBean> mDataList = new ArrayList<>();
    private int currentPage = 1;

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_zhuanjia, container, false);
        unbinder = ButterKnife.bind(this, view);
        //type = getArguments().getString("type");

        initViews();
        getMasterList(currentPage);
        return view;
    }

    private void initViews() {

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new KyMasterAdapter(R.layout.recycler_item_zhuanjia, mDataList);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                Intent intent = new Intent(getActivity(), ZhuanjiaDetailActivity.class);
                intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_ID, mDataList.get(position).getMastercode());
                intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_NAME, mDataList.get(position).getName());
                intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_TITLE, mDataList.get(position).getTitle());
                intent.putExtra(MLProperties.BUNDLE_KEY_ZHUANJIA_IMG, BASE_RESOURCE_URL+mDataList.get(position).getImg());
                startActivity(intent);
            }

            @Override
            public void onLongClick(RecyclerView.ViewHolder holder, int position) {

            }
        });
        adapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                currentPage++;
                getMasterList(currentPage);
            }
        }, mRecyclerView);
        mRecyclerView.setAdapter(adapter);

        mXRefreshView.setMoveForHorizontal(true);   // 在手指横向移动的时候，让XRefreshView不拦截事件
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(false);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(false);


        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                mDataList.clear();
                lastRefreshTime = mXRefreshView.getLastRefreshTime();
                currentPage = 1;
                getMasterList(currentPage);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
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

    private void getMasterList(final int page) {
        Observable.create(new ObservableOnSubscribe<KyMasterInfo.DataBean>() {
            @Override
            public void subscribe(final ObservableEmitter<KyMasterInfo.DataBean> e) throws Exception {
                String url = "index.php/jsmaster/mastercard";
//                if (type.equals("科研专家")) {
//                    url = "index.php/jsmaster/mastercard";
//                }
//                if (type.equals("教研专家")) {
//                    url = "index.php/jsmaster/jymastercard";
//                }
                OkGo.<String>post(BASE_URL + url)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("userclient", "aphone")
                        .params("limit", "10")
                        .params("offset", String.valueOf((page - 1) * 10))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                KyMasterInfo masterInfo = JSON.parseObject(str, new TypeReference<KyMasterInfo>() {
                                });
                                e.onNext(masterInfo.getData());
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<KyMasterInfo.DataBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(KyMasterInfo.DataBean dataBean) {
                        mDataList.addAll(dataBean.getMaster_data());
                        adapter.notifyDataSetChanged();

                        cleanXRefreshView();
                        adapter.loadMoreComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.loadMoreEnd(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

   /* private void getMasterDataFromAPI() {
        Log.e("data", "aaa" + currentPage);
        Observable.create(new ObservableOnSubscribe<List<ArticleInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ArticleInfo>> e) throws Exception {

                if (type.equals("专家1")) {
                    LmsDataService mService = new LmsDataService();
                    List<ArticleInfo> dataList = mService.getMasterCardsFromAPI2(currentPage, 5);
                    e.onNext(dataList);
                    e.onComplete();
                }
                if (type.equals("专家2")) {
                    LmsDataService mService = new LmsDataService();
                    List<ArticleInfo> dataList = mService.getMasterCardsFromAPI3(currentPage, 10);
                    e.onNext(dataList);
                    e.onComplete();
                }

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ArticleInfo>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull List<ArticleInfo> articleInfos) {

                        mDataList.addAll(articleInfos);
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {


                        T.showShort(getActivity(), "服务器开小差了，请重试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }*/

}
