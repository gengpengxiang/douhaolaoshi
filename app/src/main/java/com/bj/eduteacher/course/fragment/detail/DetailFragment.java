package com.bj.eduteacher.course.fragment.detail;

import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
 * Created by Administrator on 2018/5/23 0023.
 */

public class DetailFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_jianjie)
    TextView tvJianjie;
    @BindView(R.id.tv_shuoming)
    TextView tvShuoming;
    @BindView(R.id.iv_zhengshu)
    ImageView ivZhengshu;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.mNestedScrollView)
    NestedScrollView mNestedScrollView;
    @BindView(R.id.layout_detail)
    LinearLayout layoutDetail;
    private String courseID;
    private String unionid;
    private String phoneNumber;

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.fragment_course_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        courseID = getActivity().getIntent().getStringExtra("CourseID");
        unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");

        phoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
        Log.e("课程页的unionid", unionid + "id=" + courseID + "phone=" + phoneNumber);
        getCourseInfo();

        return view;
    }

    private void getCourseInfo() {
        Observable.create(new ObservableOnSubscribe<CourseInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<CourseInfo> e) throws Exception {
                OkGo.<String>post(BASE_URL + "index.php/kecheng/kecheng")
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("phone", phoneNumber)
                        .params("kechengid", courseID)
                        .params("unionid", unionid)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("课程信息", str);
                                CourseInfo courseInfo = JSON.parseObject(str, new TypeReference<CourseInfo>() {
                                });
                                e.onNext(courseInfo);
                                e.onComplete();
                            }
                        });
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<CourseInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(CourseInfo courseInfo) {
                        tvJianjie.setText(courseInfo.getData().getJianjie());
                        tvShuoming.setText(courseInfo.getData().getShuoming());
                        Glide.with(getActivity()).load(BASE_RESOURCE_URL + courseInfo.getData().getZhengshu()).into(ivZhengshu);

                        webView.loadDataWithBaseURL(null, courseInfo.getData().getDagang(), "text/html", "UTF-8", null);

                        layoutDetail.setVisibility(View.VISIBLE);
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
        if (event.getAction().equals("refreshpage")) {
            if (event.getNum() == 0) {
                getCourseInfo();
            }
        }
        if (event.getAction().equals("scrolltotop")) {
            mNestedScrollView.requestFocus();
            mNestedScrollView.scrollTo(0, 0);
        }
    }
}
