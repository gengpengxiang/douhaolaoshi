package com.bj.eduteacher.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.community.publish.view.PublishActivity;
import com.bj.eduteacher.entity.BaseDataInfo;
import com.bj.eduteacher.entity.GroupId;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.group.detail.fragment.datums.DatumFragment;
import com.bj.eduteacher.group.detail.fragment.member.MemberFragment;
import com.bj.eduteacher.group.detail.fragment.task.TaskFragment;
import com.bj.eduteacher.group.detail.fragment.topic.TopicFragment;
import com.bj.eduteacher.group.detail.model.GroupDetail;
import com.bj.eduteacher.group.detail.presenter.GroupDetailPresenter;
import com.bj.eduteacher.group.detail.view.IViewGroupDetail;
import com.bj.eduteacher.group.list.view.GroupAllActivity;
import com.bj.eduteacher.login.model.UserInfo;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.zzautolayout.utils.AutoUtils;
import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
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
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.api.HttpUtilService.BASE_RESOURCE_URL;
import static com.bj.eduteacher.api.HttpUtilService.BASE_URL;
import static com.bj.eduteacher.api.Urls.GETTEACHERINFO;

/**
 * Created by Administrator on 2018/6/8 0008.
 */

public class StudyFragment extends BaseFragment implements IViewGroupDetail {

    Unbinder unbinder;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.viewpager)
    ViewPager viewpager;
    @BindView(R.id.coordinatorlayout)
    CoordinatorLayout coordinatorlayout;
    @BindView(R.id.mSmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.header_bg)
    ImageView headerBg;
    @BindView(R.id.header_logo)
    SimpleDraweeView headerLogo;
    @BindView(R.id.header_name)
    TextView headerName;
    @BindView(R.id.header_tongji)
    TextView headerTongji;
    @BindView(R.id.bt_signin)
    TextView btSignin;
    @BindView(R.id.bt_study_edit)
    Button btStudyEdit;
    @BindView(R.id.layout_root)
    LinearLayout layoutRoot;
    @BindView(R.id.header_bg0)
    SimpleDraweeView headerBg0;


    private String groupid;
    private GroupDetailPresenter groupDetailPresenter;
    private InfoAdapter adapter;

    private String taskShow = "0";

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_main_study, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        groupid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID, "");
        groupDetailPresenter = new GroupDetailPresenter(getActivity(), this);
        String phone = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
        String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID);

        getUserInfo(phone, unionid);
        return view;
    }

    private void initViews() {
        AutoUtils.auto(collapsingToolbar);
        AutoUtils.auto(appbar);

        //viewpager.setOffscreenPageLimit(3);
        toolbar.setTitle("");
        setupViewPager(viewpager);
        tabs.setupWithViewPager(viewpager);
        tabs.setSelectedTabIndicatorColor(getResources().getColor(R.color.btn_red));
        appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            }
        });

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    if (positionOffset != 0) {
                        btStudyEdit.setVisibility(View.GONE);
                    } else {
                        btStudyEdit.setVisibility(View.VISIBLE);
                    }
                } else {
                    btStudyEdit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    btStudyEdit.setVisibility(View.VISIBLE);
                } else {
                    btStudyEdit.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewpager.setOffscreenPageLimit(3);
        viewpager.setCurrentItem(0);
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                int currenItem = viewpager.getCurrentItem();
                EventBus.getDefault().post(new MsgEvent("refreshPage", currenItem));
                mSmartRefreshLayout.finishRefresh(500);
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
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    private void setupViewPager(ViewPager viewPager) {

        adapter = new InfoAdapter(getChildFragmentManager());
        adapter.addFragment(new TopicFragment(), "研讨");

        if (taskShow.equals("1")) {
            adapter.addFragment(new TaskFragment(), "任务");
        }

        adapter.addFragment(new DatumFragment(), "资料");

        adapter.addFragment(new MemberFragment(), "成员");
        viewPager.setAdapter(adapter);

    }

    @Override
    public void getGroupDetailSuccess(GroupDetail groupDetail) {


        taskShow = groupDetail.getData().getGroup_info().getRenwu_show();

        mSmartRefreshLayout.setVisibility(View.VISIBLE);

        initViews();
        GroupDetail.DataBean.GroupInfoBean info = groupDetail.getData().getGroup_info();
//        Glide.with(this).load(BASE_RESOURCE_URL + info.getBgimg()).into(headerBg);

        headerBg0.setImageURI(BASE_RESOURCE_URL + info.getBgimg());

        headerLogo.setImageURI(BASE_RESOURCE_URL + info.getLogo());
        headerName.setText(info.getName());
        headerTongji.setText("成员 " + groupDetail.getData().getGroup_chengyuan_num() + " | " + "话题 " + groupDetail.getData().getGroup_huati_num());

        if (groupDetail.getData().getGroup_qiandao() == 0) {
            btSignin.setBackgroundResource(R.drawable.shape_btn_signin);
            btSignin.setText("签到");
        } else {
            btSignin.setBackgroundResource(R.drawable.shape_btn_signin);
            btSignin.setText("已签到" + groupDetail.getData().getGroup_qiandao_days() + "天");
        }

        EventBus.getDefault().postSticky(new MsgEvent("groupmembernum", groupDetail.getData().getGroup_chengyuan_num()));
    }

    @Override
    public void getSignResult(BaseDataInfo baseDataInfo) {
        T.showShort(getActivity(), baseDataInfo.getMsg());
        if (baseDataInfo.getRet().equals("1")) {
            btSignin.setBackgroundResource(R.drawable.shape_btn_signin);
            btSignin.setText("已签到" + baseDataInfo.getData() + "天");
        }
    }

    @OnClick({R.id.bt_signin, R.id.bt_changegroup, R.id.bt_study_edit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_signin:
                if (LoginStatusUtil.noLogin(getActivity())) {
                    IntentManager.toLoginSelectActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                    return;
                } else {
                    String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
                    groupDetailPresenter.signIn(PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, ""), groupid, unionid);
                }
                break;
            case R.id.bt_changegroup:
                Intent intent = new Intent(getActivity(), GroupAllActivity.class);
                intent.putExtra("type", "StudyFragment");
                startActivity(intent);
                break;
            case R.id.bt_study_edit:
                if (LoginStatusUtil.noLogin(getActivity())) {
                    IntentManager.toLoginSelectActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                    return;
                } else {
                    Intent intent2 = new Intent(getActivity(), PublishActivity.class);
                    intent2.putExtra("type", groupid);
                    startActivity(intent2);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUI(MsgEvent event) {
        if (event.getAction().equals("groupid")) {
            adapter.deleteFragment();

            viewpager.removeAllViewsInLayout();

            groupid = event.getDoubisum();
            String phone = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
            String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID);
            groupDetailPresenter.getGroupDetail(phone, groupid, unionid);
        }
        if (event.getAction().equals("wxloginsuccess")) {

            String phone = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
            String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID);
            groupid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID, "");
            groupDetailPresenter.getGroupDetail(phone, groupid, unionid);
        }
        if (event.getAction().equals("phoneloginsuccess")) {
            String phone = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
            String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID);
            groupid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID, "");
            groupDetailPresenter.getGroupDetail(phone, groupid, unionid);
        }
        if (event.getAction().equals("loginout")) {
            String phone = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
            String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID);
            getUserInfo(phone, unionid);
        }
        if (event.getAction().equals("signSuccess")) {
            String phone = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
            String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID);
            getUserInfo(phone, unionid);
        }
        if (event.getAction().equals("entergroup")) {
            String phone = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID);
            String unionid = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID);
            getUserInfo(phone, unionid);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    class InfoAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments = new ArrayList<>();
        private List<String> mFragmentTitles = new ArrayList<>();

        public InfoAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
            notifyDataSetChanged();
        }

        public void deleteFragment() {
            mFragments.clear();
            mFragmentTitles.clear();
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    public void getUserInfo(final String phone, final String unionid) {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> e) throws Exception {
                OkGo.<String>post(BASE_URL + GETTEACHERINFO)
                        .params("appkey", MLConfig.HTTP_APP_KEY)
                        .params("teacherphone", phone)
                        .params("unionid", unionid)
                        .params("type", "weixin")
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                String str = response.body().toString();
                                Log.e("StudyFragment获取用户数据", str);
                                e.onNext(str);
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
                        JSONObject json = JSON.parseObject(s);
                        String ret = (String) json.get("ret");
                        if (ret.equals("0")) {
                            GroupId result = JSON.parseObject(s, new TypeReference<GroupId>() {
                            });
                            groupid = String.valueOf(result.getData().getGroupid_moren());

                            PreferencesUtils.putString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID, groupid);
                            groupDetailPresenter.getGroupDetail(phone, groupid, unionid);
                        }
                        if (ret.equals("1")) {
                            UserInfo userInfo = JSON.parseObject(s, new TypeReference<UserInfo>() {
                            });
                            Log.e("手机号登录成功后获取用户数据", userInfo.getData().toString());
                            UserInfo.DataBean bean = userInfo.getData();
                            PreferencesUtils.putString(getActivity(), MLProperties.PREFER_KEY_USER_ID, bean.getTeacherphone());
                            PreferencesUtils.putString(getActivity(), MLProperties.BUNDLE_KEY_TEACHER_NICK, bean.getNicheng());
                            PreferencesUtils.putString(getActivity(), MLProperties.BUNDLE_KEY_TEACHER_IMG, bean.getTeacherimg_url());
                            PreferencesUtils.putString(getActivity(), MLProperties.PREFER_KEY_WECHAT_UNIONID, bean.getWeixin_unionid());
                            PreferencesUtils.putString(getActivity(), MLProperties.PREFER_KEY_WECHAT_NICHENG, bean.getWeixin_nicheng());

                            groupid = bean.getTeacher_groupid();

                            Log.e("StudyFragment", bean.getTeacher_groupid());

                            if (StringUtils.isEmpty(bean.getTeacher_groupid())) {
                                Log.e("StudyFragment", "1111");
                                PreferencesUtils.putString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID, "2");
                                groupDetailPresenter.getGroupDetail(phone, "2", unionid);
                            } else {
                                PreferencesUtils.putString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID, groupid);
                                Log.e("StudyFragment", "2222");
                                groupDetailPresenter.getGroupDetail(phone, groupid, unionid);
                            }

//                            PreferencesUtils.putString(getActivity(), MLProperties.PREFER_KEY_TEACHER_GROUPID, groupid);
//                            groupDetailPresenter.getGroupDetail(phone, groupid, unionid);
                        }
                        //add
                        //groupDetailPresenter.getGroupDetail(phone, groupid, unionid);
//                        initViews();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
