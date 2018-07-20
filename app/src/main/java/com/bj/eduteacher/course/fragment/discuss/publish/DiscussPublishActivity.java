package com.bj.eduteacher.course.fragment.discuss.publish;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.community.main.view.CustomPopDialog;
import com.bj.eduteacher.community.utils.Base64Util;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

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

public class DiscussPublishActivity extends BaseActivity{

    @BindView(R.id.header_img_back)
    ImageView headerImgBack;
    @BindView(R.id.header_tv_title)
    TextView headerTvTitle;
    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.bt_fabu)
    TextView btFabu;
    private Unbinder unbinder;
    private String id;
    private String unionid;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss_publish);
        unbinder = ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#000000"));
        }
        unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
        phoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, " ");
        id = getIntent().getStringExtra("id");
        initViews();
    }

    private void initViews() {
        headerTvTitle.setVisibility(View.VISIBLE);
        headerImgBack.setVisibility(View.VISIBLE);
        headerTvTitle.setText("发布主题帖");

        InputFilter[] filters = {new InputFilter.LengthFilter(20)};
        etTitle.setFilters(filters);
    }

    @OnClick({R.id.header_img_back, R.id.bt_fabu})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_img_back:
                cancelRemind();
                //finish();
                break;
            case R.id.bt_fabu:
                if (StringUtils.isEmpty(etTitle.getText().toString())) {
                    T.showShort(this, "标题不能为空");
                    return;
                }
                if (StringUtils.isEmpty(etContent.getText().toString())) {
                    T.showShort(this, "内容不能为空");
                    return;
                } else {

                    String title = Base64Util.encode(etTitle.getText().toString());
                    String content = Base64Util.encode(etContent.getText().toString());
                    publish(title, content);
                }
                break;
        }
    }

    private void publish(final String title, final String content) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/douke/kcyantaoadd")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("newstitle", title)
                        .params("newscontent", content)
                        .params("kechengid", id)
                        .params("unionid", unionid)
                        .params("phone",phoneNumber)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("研讨发布结果",str);
                                JSONObject json = JSON.parseObject(str);
                                String ret = (String) json.get("ret");
                                e.onNext(ret);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        if(s.equals("1")){
                            //presenter.getDouBi("fabu", phoneNumber, "getdoubi",unionid);
                            EventBus.getDefault().post(new MsgEvent("discussPublishSuccess"));
                            finish();
                        }else {
                            T.showShort(DiscussPublishActivity.this,"发布失败");
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
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
    

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            cancelRemind();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long currTimeMillin = 0;
    private void cancelRemind() {
        if(StringUtils.isEmpty(etTitle.getText().toString())&&StringUtils.isEmpty(etContent.getText().toString())){
            finish();
        }else {
            if (System.currentTimeMillis() - currTimeMillin > 1000) {
                CustomPopDialog.Builder dialogBuild = new CustomPopDialog.Builder(DiscussPublishActivity.this);
                final CustomPopDialog dialog = dialogBuild.create2(R.layout.dialog_publish_cancel);
                dialog.setCanceledOnTouchOutside(false);
                TextView tv = (TextView) dialog.findViewById(R.id.title_text);
                tv.setText("确认要放弃发布？");
                dialog.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        finish();
                    }
                });
                dialog.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
                currTimeMillin = System.currentTimeMillis();
            }
        }

    }
}
