package com.bj.eduteacher.answer.view;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.answer.adapter.ExamResultAdapter;
import com.bj.eduteacher.answer.model.ExamResult;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.community.main.adapter.SpacesItemDecoration;
import com.bj.eduteacher.utils.PreferencesUtils;
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

public class ExamResultActivity extends BaseActivity {

    @BindView(R.id.header_img_back)
    ImageView ivBack;
    @BindView(R.id.tv_title_left)
    TextView tvTitleLeft;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.header_ll_left)
    LinearLayout headerLlLeft;
    private Unbinder unbinder;
    private ExamResultAdapter adapter;
    private List<ExamResult.DataBean.LogShitiBean> resultList = new ArrayList<>();

    private String examid, logcode, phone, unionid;
    private TextView tvTitle, tvContent;
    private View headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_record);
        unbinder = ButterKnife.bind(this);
        phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, " ");
        initViews();

    }

    private void initViews() {
        tvTitleLeft.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvTitleLeft.setText("单元测试成绩");

        examid = getIntent().getStringExtra("examid");
        logcode = getIntent().getStringExtra("logcode");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(2));
        adapter = new ExamResultAdapter(R.layout.recycler_item_question, resultList);
        mRecyclerView.setAdapter(adapter);

        headView = getLayoutInflater().inflate(R.layout.recycler_headview_exam_result, null);

        tvTitle = (TextView) headView.findViewById(R.id.tv_title);
        tvContent = (TextView) headView.findViewById(R.id.tv_content);
        adapter.setHeaderView(headView);

        getResult(examid, logcode, phone, unionid);
    }

    private void getResult(final String examid, final String logcode, final String phone, final String unionid) {
        Observable.create(new ObservableOnSubscribe<ExamResult>() {
            @Override
            public void subscribe(final ObservableEmitter<ExamResult> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/grenwu/datilog")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("examid", examid)
                        .params("logcode", logcode)
                        .params("phone", phone)
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                ExamResult examResult = JSON.parseObject(str, new TypeReference<ExamResult>() {
                                });
                                e.onNext(examResult);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ExamResult>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ExamResult examResult) {
                        if (examResult.getRet().equals("1")) {
                            headView.setVisibility(View.VISIBLE);
                            resultList.clear();
                            resultList.addAll(examResult.getData().getLog_shiti());
                            adapter.notifyDataSetChanged();

                            tvTitle.setText(examResult.getData().getLog_exam().getExam_title());
                            tvContent.setText("本次测验的提交时间为：" + examResult.getData().getLog_exam().getUpdatetime().substring(0, 16));
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
    protected void onResume() {
        super.onResume();
        phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, " ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }



    @OnClick(R.id.header_ll_left)
    public void onClick() {
        finish();
    }
}
