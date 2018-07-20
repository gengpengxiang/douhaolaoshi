package com.bj.eduteacher.answer.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.answer.adapter.ExamRecordAdapter;
import com.bj.eduteacher.answer.model.ExamInfo;
import com.bj.eduteacher.answer.model.ExamRecord;
import com.bj.eduteacher.answer.presenter.AnswerHomePresenter;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.widget.SpacesItemDecoration;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AnswerHomeActivity extends BaseActivity implements IViewAnswerHome {

    @BindView(R.id.header_img_back)
    ImageView ivBack;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.mSmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    TextView tvContent;
    TextView tvNum;
    TextView tvTime;
    TextView tvExamtitle;
    Button btStart;
    @BindView(R.id.header_ll_left)
    LinearLayout headerLlLeft;
    private Unbinder unbinder;

    private ExamRecordAdapter adapter;
    private AnswerHomePresenter presenter;
    private String phone;
    private String unionid;

    private String questionNum, examid, jiezhiTime, recordid, wanchengstatus,jiezhiststus;
    private List<ExamRecord.DataBean> recordList = new ArrayList<>();
    private View headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_answer);
        unbinder = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");

        initViews();
    }

    private void initViews() {
        tvTitle.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle.setText("单元测试");

        examid = getIntent().getStringExtra("examid");
        jiezhiTime = getIntent().getStringExtra("jiezhitime");
        wanchengstatus = getIntent().getStringExtra("wanchengstatus");
        jiezhiststus = getIntent().getStringExtra("jiezhistatus");


        presenter = new AnswerHomePresenter(this, this);

        presenter.getExamInfo(examid, phone, unionid);
        presenter.getRecordList(examid, phone, unionid);

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                presenter.getExamInfo(examid, phone, unionid);
                presenter.getRecordList(examid, phone, unionid);
                mSmartRefreshLayout.finishRefresh(500);
            }
        });


        adapter = new ExamRecordAdapter(R.layout.recycler_item_exam_record, recordList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(2));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(AnswerHomeActivity.this, ExamResultActivity.class);
                intent.putExtra("examid", examid);
                intent.putExtra("logcode", recordList.get(position).getLogcode());
                startActivity(intent);
            }
        });

        headView = getLayoutInflater().inflate(R.layout.recycler_headview_examinfo, null);
        adapter.setHeaderView(headView);
        tvContent = (TextView) headView.findViewById(R.id.tv_content);
        tvNum = (TextView) headView.findViewById(R.id.tv_num);
        tvTime = (TextView) headView.findViewById(R.id.tv_time);
        tvExamtitle = (TextView) headView.findViewById(R.id.tv_examtitle);
        btStart = (Button) headView.findViewById(R.id.bt_start);

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AnswerHomeActivity.this, AnswerActivity.class);
                intent.putExtra("questionNum", questionNum);
                intent.putExtra("examid", examid);
                startActivity(intent);
            }
        });

        btStart.setText("开始答题");

        if (wanchengstatus.equals("1")) {//1代表已经截止
            btStart.setText("任务已完成");
            btStart.setEnabled(false);
            btStart.setTextColor(Color.GRAY);
            btStart.setBackgroundResource(R.drawable.btn_shape_corner_gray);
        }
        if (jiezhiststus.equals("1")) {//1代表已经截止
            btStart.setText("已截止提交");
            btStart.setEnabled(false);
            btStart.setTextColor(Color.GRAY);
            btStart.setBackgroundResource(R.drawable.btn_shape_corner_gray);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        presenter.onDestory();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void udateUI(MsgEvent event) {
        if (event.getAction().equals("submitsuccess")) {
            presenter.getExamInfo(examid, phone, unionid);
            presenter.getRecordList(examid, phone, unionid);
        }
    }

    @Override
    public void getExamInfo(ExamInfo examInfo) {

        questionNum = examInfo.getData().getShiti_num();
        examid = examInfo.getData().getId();

        mSmartRefreshLayout.setVisibility(View.VISIBLE);
        tvExamtitle.setText(examInfo.getData().getTitle());
        tvContent.setText(examInfo.getData().getContent());
        tvNum.setText(examInfo.getData().getShiti_num() + "道题");
        tvTime.setText(jiezhiTime);
    }

    @Override
    public void getExamRecord(ExamRecord examRecord) {
        recordList.clear();
        recordList.addAll(examRecord.getData());
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.header_ll_left)
    public void onClick() {
        finish();
    }
}
