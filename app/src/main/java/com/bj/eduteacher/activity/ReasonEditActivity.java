package com.bj.eduteacher.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.ReasonEditAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.dialog.AddReasonAlertDialog;
import com.bj.eduteacher.dialog.CancelConfirmAlertDialog3;
import com.bj.eduteacher.dialog.CommendReasonInfo;
import com.bj.eduteacher.utils.DensityUtils;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.widget.SpacesItemDecoration;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by zz379 on 2017/5/5.
 * 编辑理由页面
 */

public class ReasonEditActivity extends BaseActivity {

    @BindView(R.id.header_img_back)
    ImageView imgBack;
    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;

    private List<CommendReasonInfo> mDataList = new ArrayList<>();

    private ReasonEditAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private String teacherPhoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reason_edit);
        ButterKnife.bind(this);

        initToolBar();
        initView();
        initData();
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("编辑理由");
        imgBack.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initView() {
        int padding = DensityUtils.dp2px(this, 4);
        int itemPadding = DensityUtils.dp2px(this, 4);
        // 下拉刷新控件
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setPadding(padding, padding * 2, padding, padding);
        // look as listview
        layoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(layoutManager);
        // set Adatper
        mAdapter = new ReasonEditAdapter(mDataList);
        mAdapter.setOnMyItemClickListener(new ReasonEditAdapter.OnMyItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (mDataList.size() > 1) {
                    showDeleteReasonDialog(position);
                } else {
                    T.showShort(ReasonEditActivity.this, "点赞理由不能为空");
                }
            }

            @Override
            public void onDeleteClick(View view, int position) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
        SpacesItemDecoration decoration = new SpacesItemDecoration(itemPadding);
        mRecyclerView.addItemDecoration(decoration);

        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(false);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(false);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                super.onRefresh(isPullDown);
                getCommendReason();
            }
        });
    }

    @Override
    protected void initData() {
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        // 获取缓存的点赞理由
        String commendReasons = PreferencesUtils.getString(this, "CommendReason");
        if (StringUtils.isEmpty(commendReasons) || commendReasons.equals("[]")) {
            // 获取最新点赞理由
            getCommendReason();
        } else {
            try {
                loadData(parseCommendReasons(commendReasons));
            } catch (Exception e) {
                e.printStackTrace();
                // 理由解析出现问题的时候
                mXRefreshView.startRefresh();
            }
        }
    }

    @OnClick(R.id.tv_addReason)
    void clickAddReason() {
        showAddReasonDialog();
        MobclickAgent.onEvent(this, "thumb_add");
    }

    @OnClick(R.id.header_ll_left)
    void clickBack() {
        this.finish();
        // overridePendingTransition(R.anim.left_right_in, R.anim.left_right_out);
    }

    @Override
    public void onBackPressed() {
        clickBack();
    }

    /**
     * 获取最新理由后，刷新页面
     */
    private void getCommendReason() {
        Observable.create(new ObservableOnSubscribe<List<CommendReasonInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CommendReasonInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                String result = mService.getCommendReasonFromAPI(teacherPhoneNumber);
                PreferencesUtils.putString(ReasonEditActivity.this, "CommendReason", result);
                List<CommendReasonInfo> resultList = parseCommendReasons(result);
                e.onNext(resultList);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CommendReasonInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<CommendReasonInfo> list) {
                        loadData(list);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 只缓存最新理由，不刷新页面
     */
    private void refreshCommendReason() {
        Observable.create(new ObservableOnSubscribe<List<CommendReasonInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<CommendReasonInfo>> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                String result = mService.getCommendReasonFromAPI(teacherPhoneNumber);
                PreferencesUtils.putString(ReasonEditActivity.this, "CommendReason", result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CommendReasonInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<CommendReasonInfo> list) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void loadData(List<CommendReasonInfo> list) {
        mXRefreshView.stopRefresh();
        mDataList.clear();
        mDataList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    private List<CommendReasonInfo> parseCommendReasons(String result) throws Exception {
        List<CommendReasonInfo> reasonList = new ArrayList<>();
        if (!StringUtils.isEmpty(result)) {
            JSONArray resultArray = new JSONArray(result);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject reasonObj = resultArray.optJSONObject(i);
                String reasonID = reasonObj.optString("id");
                String reasonTitle = reasonObj.optString("title");
                reasonList.add(new CommendReasonInfo(reasonID, reasonTitle));
            }
        }
        return reasonList;
    }

    private void showDeleteReasonDialog(final int position) {
        CancelConfirmAlertDialog3 dialog = new CancelConfirmAlertDialog3(this);
        dialog.setTitleText("删除理由");
        dialog.setContentText("是否确认删除理由？");
        dialog.setCancelClickListener(new CancelConfirmAlertDialog3.OnSweetClickListener() {
            @Override
            public void onClick(CancelConfirmAlertDialog3 sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });
        dialog.setConfirmClickListener(new CancelConfirmAlertDialog3.OnSweetClickListener() {
            @Override
            public void onClick(CancelConfirmAlertDialog3 sweetAlertDialog) {
                // 确认删除
                sweetAlertDialog.dismiss();
                MobclickAgent.onEvent(ReasonEditActivity.this, "thumb_delete");
                deleteReason(mDataList.get(position).getReasonName(), position);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 删除理由
     *
     * @param reasonName
     * @param position
     */
    private void deleteReason(final String reasonName, final int position) {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                String[] result = mService.deleteTeacherCommendReasonFromAPI(teacherPhoneNumber, reasonName);
                e.onNext(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String[] result) {
                        if (!StringUtils.isEmpty(result[0]) && "1".equals(result[0])) {
                            // 删除成功
                            mDataList.remove(position);
                            mAdapter.notifyDataSetChanged();
                            refreshCommendReason();
                        } else {
                            T.showShort(ReasonEditActivity.this, "删除失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        T.showShort(ReasonEditActivity.this, "服务器开小差了，请稍后再试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showAddReasonDialog() {
        AddReasonAlertDialog dialog = new AddReasonAlertDialog(this);
        dialog.setCancelClickListener(new AddReasonAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(AddReasonAlertDialog sweetAlertDialog, EditText mContentEdt) {
                KeyBoardUtils.closeKeybord(mContentEdt, ReasonEditActivity.this);
                sweetAlertDialog.dismiss();
            }
        });
        dialog.setConfirmClickListener(new AddReasonAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(AddReasonAlertDialog sweetAlertDialog, EditText mContentEdt) {
                String reasonName = mContentEdt.getText().toString();
                KeyBoardUtils.closeKeybord(mContentEdt, ReasonEditActivity.this);
                // 判断理由是否已经存在
                if (!checkReasonRepeat(reasonName)) {
                    addNewReason(reasonName);
                    sweetAlertDialog.dismiss();
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 检查点赞理由是否为空，或者时候已经存在
     *
     * @param reasonName
     * @return
     */
    private boolean checkReasonRepeat(String reasonName) {
        if (StringUtils.isEmpty(reasonName)) {
            T.showShort(this, "点赞理由不能为空");
            return true;
        }
        for (CommendReasonInfo info : mDataList) {
            if (info.getReasonName().equals(reasonName)) {
                T.showShort(this, "该点赞理由已存在");
                return true;
            }
        }
        // 没有重复
        return false;
    }

    private void addNewReason(final String reasonName) {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                String[] result = mService.addTeacherCommendReasonFromAPI(teacherPhoneNumber, reasonName);
                e.onNext(result);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String[] result) {
                        if (!StringUtils.isEmpty(result[0]) && ("1".equals(result[0])) ||
                                "2".equals(result[0])) {
                            // 添加成功
                            mXRefreshView.startRefresh();
                        } else {
                            T.showShort(ReasonEditActivity.this, "添加失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        T.showShort(ReasonEditActivity.this, "服务器开小差了，请稍后再试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("thumbEdit");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("thumbEdit");
        MobclickAgent.onPause(this);
    }
}
