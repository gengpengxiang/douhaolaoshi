package com.bj.eduteacher.integral.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.community.main.view.CustomPopDialog;
import com.bj.eduteacher.integral.model.Doubi;
import com.bj.eduteacher.integral.view.IViewintegral;
import com.bj.eduteacher.presenter.Presenter;
import com.bj.eduteacher.prize.view.PrizeActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.DOUBI;

/**
 * Created by Administrator on 2018/4/23 0023.
 */

public class IntegralPresenter extends Presenter {
    private Context mContext;
    private IViewintegral iView;

    public IntegralPresenter(Context mContext, IViewintegral iView) {
        this.mContext = mContext;
        this.iView = iView;
    }

    public void getDouBi(final String laiyuan, final String userCode, final String type,final String unionid) {
            Observable.create(new ObservableOnSubscribe<Doubi>() {
                @Override
                public void subscribe(@NonNull final ObservableEmitter<Doubi> e) throws Exception {
                    OkGo.<String>post(BASE_URL + DOUBI)
                            .params("appkey", MLConfig.HTTP_APP_KEY)
                            .params("laiyuan", laiyuan)
                            .params("usercode", userCode)
                            .params("type", type)
                            .params("unionid",unionid)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    String str = response.body().toString();
                                    Log.e("获取积分返回数据", str);

                                    JSONObject json = JSON.parseObject(str);
                                    String ret = (String) json.get("ret");
                                    Log.e("ret",ret+"+++aaa");
                                    if(!ret.equals("0")){//查询成功

                                        Log.e("123","123");
                                        Doubi dataBean = JSON.parseObject(str, new TypeReference<Doubi>() {
                                        });

                                        Log.e("321","321");
                                        e.onNext(dataBean);
                                        e.onComplete();
                                    }

//                                    Doubi dataBean = JSON.parseObject(str, new TypeReference<Doubi>() {
//                                    });
//                                    e.onNext(dataBean);
//                                    e.onComplete();

                                }
                            });
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Doubi>() {
                        public int layoutId;

                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Doubi doubi) {

                            Log.e("000","000");
                            iView.getDouBi(doubi);
                            if (laiyuan.equals("chaxun")) {
                                return;
                            } else {
                                switch (laiyuan) {
                                    case "fabu":
                                        layoutId = R.layout.dialog_publish_success;
                                        Log.e("111","111");
                                        break;
                                    case "pinglun":
                                        layoutId = R.layout.dialog_comment_success;
                                        break;
                                    case "login":
                                        layoutId = R.layout.dialog_login_success;
                                        break;
                                }
                                Log.e("222","222");
                                CustomPopDialog.Builder dialogBuild = new CustomPopDialog.Builder(mContext);
                                final CustomPopDialog dialog = dialogBuild.create2(layoutId);
                                dialog.setCanceledOnTouchOutside(false);
                                TextView tvDouBi = (TextView) dialog.findViewById(R.id.tv_doubi);
                                tvDouBi.setText("+" + doubi.getData().getUser_doubinum_add());
                                TextView tvDouBiZS = (TextView) dialog.findViewById(R.id.tv_doubizongshu);
                                tvDouBiZS.setText(doubi.getData().getUser_doubinum_sum());
                                dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (dialog.isShowing())
                                            dialog.dismiss();
                                    }
                                });
                                dialog.findViewById(R.id.bt_choujiang).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (dialog.isShowing())
                                            dialog.dismiss();
                                        Intent intent = new Intent();
                                        intent.setClass(mContext, PrizeActivity.class);
                                        mContext.startActivity(intent);
                                    }
                                });
                                //首次登录才弹出
                                if(!doubi.getRet().equals("3")){
                                    Log.e("333","333");
                                    dialog.show();
                                }

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
    public void onDestory() {
        mContext = null;
        iView = null;
    }
}
