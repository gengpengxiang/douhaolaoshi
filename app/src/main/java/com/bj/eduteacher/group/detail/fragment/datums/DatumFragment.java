package com.bj.eduteacher.group.detail.fragment.datums;

import android.content.Intent;
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
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.community.main.adapter.SpacesItemDecoration;
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

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.GROUPZILIAO;

/**
 * Created by Administrator on 2018/5/2 0002.
 */

public class DatumFragment extends BaseFragment {

    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private String teacherPhoneNumber, groupid;
    private DatumAdapter adapter;
    private List<Datums.DataBean.GroupZiliaolistBean> list = new ArrayList<>();
    private int currentPage = 1;
    private int listSize = 0;
    private String unionid;

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if(getActivity().getIntent().getStringExtra("groupid")!=null){
            groupid = getActivity().getIntent().getStringExtra("groupid");
        }else {
            groupid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID,"");
        }
//        groupid = getActivity().getIntent().getStringExtra("groupid");

        unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID,"");
        teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
        unbinder = ButterKnife.bind(this, view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));
        adapter = new DatumAdapter(R.layout.recycler_item_group_datum, list);
        //adapter.setEmptyView(R.layout.recycler_item_comment_empty,mRecyclerView);
        mRecyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                String path = list.get(position).getPreviewurl();
                String id = list.get(position).getId();

                String resID = list.get(position).getId();
                String resName = list.get(position).getTitle();
                String previewUrl = list.get(position).getPreviewurl();
                String downloadUrl = list.get(position).getFileurl();
                String vid = list.get(position).getVideoid_ali();
                //视频
                if (list.get(position).getType().equals("2")) {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);

                    intent.putExtra("type","DatumFragment");
                    startActivity(intent);
                } else {
                    //PPT或文档
//                    T.showShort(getActivity(), "页面不存在");
                    Intent intent = new Intent(getActivity(), ResReviewActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL, downloadUrl);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_VID, vid);
                    startActivity(intent);
                }
            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String resID = list.get(position).getId();
                String resName = list.get(position).getTitle();
                String previewUrl = list.get(position).getPreviewurl();
                String downloadUrl = list.get(position).getFileurl();
                //视频
                if (list.get(position).getType().equals("2")) {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class);

                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    intent.putExtra("type","DatumFragment");
                    startActivity(intent);
                } else {
                    //PPT或文档
//                    T.showShort(getActivity(), "页面不存在");
                    Intent intent = new Intent(getActivity(), ResReviewActivity.class);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_ID, resID);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_NAME, resName);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_PREVIEW_URL, previewUrl);
                    intent.putExtra(MLProperties.BUNDLE_KEY_MASTER_RES_DOWNLOAD_URL, downloadUrl);
                    startActivity(intent);
                }
            }
        });
        adapter.disableLoadMoreIfNotFullPage(mRecyclerView);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        if (listSize < 10) {
//                            adapter.loadMoreEnd(true);
//                        } else {
//                            currentPage++;
//                            getDatumsList(groupid, String.valueOf(currentPage),unionid);
//                        }
                            currentPage++;
                            getDatumsList(groupid, String.valueOf(currentPage),unionid);
                    }
                }, 500);
            }
        }, mRecyclerView);

//        adapter.setEnableLoadMore(false);
        getDatumsList(groupid, String.valueOf(currentPage),unionid);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("refreshPage")) {
            currentPage = 1;
                list.clear();
                getDatumsList(groupid, String.valueOf(currentPage),unionid);
        }
        if (event.getAction().equals("groupid")) {
            currentPage = 1;
            groupid = event.getDoubisum();
            list.clear();
            getDatumsList(groupid, String.valueOf(currentPage),unionid);
        }
    }

    private void getDatumsList(final String id, final String pageIndex,final String unionid) {
        Observable.create(new ObservableOnSubscribe<List<Datums.DataBean.GroupZiliaolistBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<Datums.DataBean.GroupZiliaolistBean>> e) throws Exception {
                OkGo.<String>post(BASE_URL + GROUPZILIAO)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("usercode", teacherPhoneNumber)
                        .params("groupid", id)
                        .params("limit", "10")
                        .params("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * 10))
                        .params("unionid",unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("小组资料", str);

                                Datums datums = JSON.parseObject(str, new TypeReference<Datums>() {
                                });
                                e.onNext(datums.getData().getGroup_ziliaolist());
                                e.onComplete();

                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Datums.DataBean.GroupZiliaolistBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<Datums.DataBean.GroupZiliaolistBean> articleInfos) {

                        //listSize = articleInfos.size();
                        list.addAll(articleInfos);
                        adapter.setNewData(list);
                        adapter.loadMoreComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        adapter.loadMoreComplete();
                        adapter.loadMoreEnd(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
