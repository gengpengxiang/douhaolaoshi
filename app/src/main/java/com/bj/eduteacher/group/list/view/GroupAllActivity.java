package com.bj.eduteacher.group.list.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.SettingActivity;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.community.main.adapter.SpacesItemDecoration;
import com.bj.eduteacher.group.detail.view.GroupDetailActivity;
import com.bj.eduteacher.group.list.adapter.GroupAdapter;
import com.bj.eduteacher.group.list.model.GroupInfo;
import com.bj.eduteacher.group.list.presenter.GroupPresenter;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 小组界面
 */
public class GroupAllActivity extends BaseActivity implements IViewGroup {

    @BindView(R.id.tv_share)
    TextView tvShare;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mXRefreshView)
    XRefreshView mXRefreshView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.header_img_back)
    ImageView headerImgBack;
    @BindView(R.id.header_ll_right)
    LinearLayout headerLlRight;
    private Unbinder unbinder;
    private GroupPresenter groupPresenter;
    private String teacherPhoneNumber, unionid;
    private GroupAdapter groupAdapter;
    private List<GroupInfo.DataBean> dataList = new ArrayList<>();
    public static long lastRefreshTime;
    private int currentPage = 1;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_all);
        EventBus.getDefault().register(this);
        //改变底部导航栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }
        if (getIntent() != null) {
            type = getIntent().getStringExtra("type");
        }

        unbinder = ButterKnife.bind(this);
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        groupPresenter = new GroupPresenter(this, this);
        initViews();
        groupPresenter.getGroupList(teacherPhoneNumber, String.valueOf(currentPage), unionid);
    }

    private void initViews() {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("学习小组");
        headerImgBack.setVisibility(View.VISIBLE);
        tvShare.setText("+邀请码");
        tvShare.setVisibility(View.VISIBLE);
        headerLlRight.setVisibility(View.VISIBLE);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(3));
        // mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        groupAdapter = new GroupAdapter(R.layout.recycler_item_group, dataList);
        groupAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (type.equals("DoukeFragment")) {
                    Intent intent = new Intent(GroupAllActivity.this, GroupDetailActivity.class);
                    intent.putExtra("groupid", dataList.get(position).getId());
                    intent.putExtra("grouptitle", dataList.get(position).getName());
                    startActivityForResult(intent, 100);
                    //EventBus.getDefault().post(new MsgEvent("groupid",dataList.get(position).getId()));
                }
                if (type.equals("StudyFragment")) {
                    PreferencesUtils.putString(GroupAllActivity.this, MLProperties.PREFER_KEY_TEACHER_GROUPID, dataList.get(position).getId());
                    EventBus.getDefault().post(new MsgEvent("groupid", dataList.get(position).getId()));

                    finish();
                }
            }
        });
        mRecyclerView.setAdapter(groupAdapter);
        mXRefreshView.setMoveForHorizontal(true);
        mXRefreshView.setPullRefreshEnable(true);
        mXRefreshView.setPullLoadEnable(false);
        mXRefreshView.restoreLastRefreshTime(lastRefreshTime);
        mXRefreshView.setAutoRefresh(false);
        mXRefreshView.setAutoLoadMore(false);
        mXRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {
            @Override
            public void onRefresh(boolean isPullDown) {
                teacherPhoneNumber = PreferencesUtils.getString(GroupAllActivity.this, MLProperties.PREFER_KEY_USER_ID, "");
                unionid = PreferencesUtils.getString(GroupAllActivity.this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                lastRefreshTime = mXRefreshView.getLastRefreshTime();
                currentPage = 1;
                groupPresenter.getGroupList(teacherPhoneNumber, String.valueOf(currentPage), unionid);
            }

            @Override
            public void onLoadMore(boolean isSilence) {
                teacherPhoneNumber = PreferencesUtils.getString(GroupAllActivity.this, MLProperties.PREFER_KEY_USER_ID, "");
                unionid = PreferencesUtils.getString(GroupAllActivity.this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                currentPage++;
                groupPresenter.getGroupList(teacherPhoneNumber, String.valueOf(currentPage), unionid);
            }
        });
    }

    private void cleanXRefreshView() {
        mXRefreshView.stopRefresh();
        mXRefreshView.stopLoadMore();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        groupPresenter.onDestory();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }

    @OnClick({R.id.header_img_back, R.id.tv_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_img_back:
                finish();
                break;
            case R.id.tv_share:
                Intent intent = new Intent(GroupAllActivity.this, SettingActivity.class);
                intent.putExtra("symbol", "GroupAllActivity");
                startActivityForResult(intent, 1);
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUI(MsgEvent event) {
        if (event.getAction().equals("groupupdate")) {
            teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
            unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
            currentPage = 1;
            groupPresenter.getGroupList(teacherPhoneNumber, "1", unionid);
        }
//        if (event.getAction().equals("wxloginsuccess")) {
//            teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
//            unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
//            currentPage = 1;
//            groupPresenter.getGroupList(teacherPhoneNumber, "1", unionid);
//        }
//        if (event.getAction().equals("phoneloginsuccess")) {
//            teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
//            unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
//            currentPage = 1;
//            groupPresenter.getGroupList(teacherPhoneNumber, "1", unionid);
//
//        }
    }

    @Override
    public void getGroupListSuccess(List<GroupInfo.DataBean> groupInfoList) {
        cleanXRefreshView();
        if(currentPage==1){
            dataList.clear();
            dataList.addAll(groupInfoList);
            groupAdapter.notifyDataSetChanged();
        }else {
            dataList.addAll(groupInfoList);
            groupAdapter.notifyDataSetChanged();
        }
//        dataList.addAll(groupInfoList);
//        groupAdapter.notifyDataSetChanged();
    }

    @Override
    public void refresh(List<GroupInfo.DataBean> groupInfoList) {
        cleanXRefreshView();
        dataList.clear();
        dataList.addAll(groupInfoList);
        groupAdapter.notifyDataSetChanged();
    }

    @Override
    public void getGroupListFail() {
        cleanXRefreshView();
    }
}
