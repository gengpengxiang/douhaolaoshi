package com.bj.eduteacher.group.list.presenter;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.group.list.model.GroupInfo;
import com.bj.eduteacher.group.list.view.IViewGroup;
import com.bj.eduteacher.presenter.Presenter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.GROUPLIST;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public class GroupPresenter extends Presenter{

    private Context context;
    private IViewGroup iView;

    public GroupPresenter(Context context, IViewGroup iView) {
        context = context;
        this.iView = iView;
    }

    public void getGroupList(final String userPhoneNumber, final String pageIndex,final String unionid){

        Observable.create(new ObservableOnSubscribe<List<GroupInfo.DataBean>>() {
            @Override
            public void subscribe(final ObservableEmitter<List<GroupInfo.DataBean>> e) throws Exception {
                    OkGo.<String>post(BASE_URL + GROUPLIST)
                            .params("appkey", MLConfig.HTTP_APP_KEY)
                            .params("limit", "10")
                            .params("offset", String.valueOf((Integer.parseInt(pageIndex) - 1) * 10))
                            .params("usercode", userPhoneNumber)
                            .params("unionid",unionid)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    String str = response.body().toString();
                                    Log.e("小组列表返回数据", str);
                                    GroupInfo groupInfo = JSON.parseObject(str, new TypeReference<GroupInfo>() {
                                    });
                                    List<GroupInfo.DataBean> list = groupInfo.getData();

                                    e.onNext(list);
                                    e.onComplete();
                                }

                            });
//                }

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<GroupInfo.DataBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<GroupInfo.DataBean> dataBeans) {
                        if(pageIndex.equals("1")){
                            iView.refresh(dataBeans);
                        }else {
                            iView.getGroupListSuccess(dataBeans);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        iView.getGroupListFail();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onDestory() {
        context = null;
        iView = null;
    }
}
