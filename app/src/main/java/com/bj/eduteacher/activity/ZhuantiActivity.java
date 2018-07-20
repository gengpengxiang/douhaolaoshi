package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.andview.refreshview.XRefreshView;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.DoukeAdapter;
import com.bj.eduteacher.adapter.DoukeListAdapter;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.ArticleInfo;
import com.bj.eduteacher.entity.DoukeInfo;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;

public class ZhuantiActivity extends BaseActivity {

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
    List<DoukeInfo.DataBean> dataList = new ArrayList<>();
    private DoukeAdapter adapter;
    private int currentPage = 1;
    private String id,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuanti);
        unbinder = ButterKnife.bind(this);

        initViews();
        getDoukeList(currentPage);
    }

    private void initViews() {

        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");

        headerTvTitle.setVisibility(View.VISIBLE);
        headerTvTitle.setText(title);
        headerImgBack.setVisibility(View.VISIBLE);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DoukeAdapter(R.layout.recycler_item_douke, dataList);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DoukeInfo.DataBean bean = dataList.get(position);
                // 跳转到逗课页面
                Intent intent = new Intent(ZhuantiActivity.this, DoukeDetailActivity.class);
                intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_ID, bean.getId());
                intent.putExtra(MLProperties.BUNDLE_KEY_DOUKE_URL, bean.getUrl());
                intent.putExtra("title", bean.getTitle());
                intent.putExtra("content", bean.getContent());
                intent.putExtra("imgurl", bean.getImg());
                intent.putExtra("commentnum", bean.getComment_num());
                startActivity(intent);
            }
        });

        mXRefreshView.setMoveForHorizontal(true);   // 在手指横向移动的时候，让XRefreshView不拦截事件
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(true);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(true);
        mXRefreshView.setEmptyView(R.layout.recycler_item_douke_empty);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                lastRefreshTime = mXRefreshView.getLastRefreshTime();
                currentPage = 1;
                dataList.clear();
                getDoukeList(currentPage);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                currentPage++;
                getDoukeList(currentPage);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.header_img_back)
    public void onClick() {
        finish();
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    private void getDoukeList(final int currentPage) {
        Observable.create(new ObservableOnSubscribe<List<DoukeInfo.DataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<DoukeInfo.DataBean>> e) throws Exception {
                OkGo.<String>post(BASE_URL+"index.php/js/doukelist")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("zhuanti",id)
                        .params("limit","10")
                        .params("offset",String.valueOf((currentPage - 1) * 10))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("专题",str);
                                DoukeInfo doukeInfo = JSON.parseObject(str, new TypeReference<DoukeInfo>() {});

                                e.onNext(doukeInfo.getData());
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<DoukeInfo.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<DoukeInfo.DataBean> articleInfos) {
                        cleanXRefreshView();
                        dataList.addAll(articleInfos);
                        adapter.notifyDataSetChanged();
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
