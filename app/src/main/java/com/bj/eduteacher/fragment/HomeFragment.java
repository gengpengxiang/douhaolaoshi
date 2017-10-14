package com.bj.eduteacher.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatSpinner;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bj.eduteacher.BaseFragment;
import com.bj.eduteacher.R;
import com.bj.eduteacher.activity.LoginHelpActivity;
import com.bj.eduteacher.adapter.SimpleFragmentPagerAdapter;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.dialog.UpdateAPPAlertDialog;
import com.bj.eduteacher.entity.AppVersionInfo;
import com.bj.eduteacher.entity.ClassInfo;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.service.DownloadAppService;
import com.bj.eduteacher.utils.AppUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.NetUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.zzokhttp.OkHttpUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.umeng.analytics.MobclickAgent;

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
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zz379 on 2017/4/13.
 */

public class HomeFragment extends BaseFragment {

    @BindView(R.id.header_tv_title)
    TextView tvTitle;
    @BindView(R.id.mTabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;
    @BindView(R.id.header_spinner)
    AppCompatSpinner mSpinnerClass;
    @BindView(R.id.mFrameLayout)
    FrameLayout mFrameLayout;
    @BindView(R.id.ll_content)
    LinearLayout llContent;
    @BindView(R.id.ll_errorContent)
    LinearLayout llErrorContent;
    @BindView(R.id.sv_noClassLinked)
    ScrollView svNoClassLinked;
    @BindView(R.id.tv_apply)
    TextView tvApply;
    @BindView(R.id.ll_withoutLogin)
    LinearLayout llWithoutLogin;

    private LmsDataService mService;
    private FragmentManager fm;

    private List<ClassInfo> teacherClassList = new ArrayList<>();
    private List<Fragment> mTabs = new ArrayList<>();

    private String teacherPhoneNumber;

    private int minTabSize;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_teacher_classes, container, false);

        ButterKnife.bind(this, view);
        mService = new LmsDataService();

        DisplayMetrics display = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(display);
        minTabSize = (display.widthPixels / (display.densityDpi / 160)) / 90 - 1;
        minTabSize = minTabSize > 0 ? minTabSize : 3;
        LL.i("页面Tab开始滑动的阀值：" + minTabSize);

        initToolBar();
        initView();
        initDatas(true);

        fm = getChildFragmentManager();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected View loadViewLayout(LayoutInflater inflater, ViewGroup container) {
        return null;
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

    private void initToolBar() {

    }

    private void initView() {

    }

    private void initDatas(boolean isCheck) {
        // 检查新版本 如果是Wi-Fi环境下才进行检查
        if (NetUtils.isConnected(getActivity()) && NetUtils.isWifi(getActivity())) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkAppNewVersion();
                }
            }, 3000);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
        if (StringUtils.isEmpty(teacherPhoneNumber)) {
            updatePage4();
        } else {
            llWithoutLogin.setVisibility(View.GONE);
            // 获取教师关联班级数量
            if (teacherClassList.size() == 0) {
                getTeacherLinkClass();
            }
            // 先清空上次缓存的理由
            PreferencesUtils.putString(getActivity(), "CommendReason", "");
            // 缓存点赞理由
            getCommendReason();
        }
    }

    @Override
    protected void onVisible() {
        super.onVisible();
        MobclickAgent.onPageStart("home");
        LL.i("HomeFragment ------- " + this.toString() + "HashCode: ------------------ : " + this.hashCode());
        if (getActivity() != null) {
            teacherPhoneNumber = PreferencesUtils.getString(getActivity(), MLProperties.PREFER_KEY_USER_ID, "");
            if (StringUtils.isEmpty(teacherPhoneNumber)) {
                updatePage4();
            } else {
                llWithoutLogin.setVisibility(View.GONE);
                // 获取教师关联班级数量
                if (teacherClassList.size() == 0) {
                    getTeacherLinkClass();
                }
                // 先清空上次缓存的理由
                PreferencesUtils.putString(getActivity(), "CommendReason", "");
                // 缓存点赞理由
                getCommendReason();
            }
        }
    }

    public void resetDataByHand() {
        teacherClassList.clear();
        FragmentTransaction ft = fm.beginTransaction();
        for (Fragment fragment : mTabs) {
            ft.remove(fragment);
        }
        ft.commit();
        ft = null;
        fm.executePendingTransactions();
        LL.i("清空HomeFragment的缓存数据：" + fm.getBackStackEntryCount());
    }

    @Override
    protected void onInVisible() {
        super.onInVisible();
        MobclickAgent.onPageEnd("home");
    }

    @Override
    public void onPause() {
        super.onPause();
        disposables.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.tv_login)
    void clickLogin() {
        IntentManager.toLoginActivity(getActivity(), IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
    }

    private void checkAppNewVersion() {
        Observable.create(new ObservableOnSubscribe<AppVersionInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<AppVersionInfo> emitter) throws Exception {
                if (emitter.isDisposed()) return;
                AppVersionInfo info;
                String versionName = AppUtils.getVersionName(getActivity());
                String qudao = AppUtils.getMetaDataFromApplication(getActivity(), MLConfig.KEY_CHANNEL_NAME);
                try {
                    info = mService.checkNewVersion(versionName, qudao);
                } catch (Exception e) {
                    e.printStackTrace();
                    LL.e(e);
                    info = new AppVersionInfo();
                    info.setErrorCode("0");
                }
                emitter.onNext(info);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AppVersionInfo>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(AppVersionInfo info) {
                        if (StringUtils.isEmpty(info.getErrorCode()) || info.getErrorCode().equals("0")) {
                            return;
                        }
                        if (info.getErrorCode().equals("1")) {
                            showNewVersionDialog(info.getTitle(), info.getContent(), info.getDownloadUrl());
                        } else if (info.getErrorCode().equals("2")) {
                            // T.showShort(getActivity(), info.getMessage());
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

    private void showNewVersionDialog(String title, String content, final String downloadUrl) {
        createUpdateAppDialog(title, content, new UpdateAPPAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(final UpdateAPPAlertDialog sweetAlertDialog) {
                // confirm
                RxPermissions rxPermissions = new RxPermissions(getActivity());
                rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(@NonNull Boolean success) throws Exception {
                                if (success) {
                                    startDownloadAppService(downloadUrl);
                                    sweetAlertDialog.startDownload();
                                } else {
                                    sweetAlertDialog.dismiss();
                                }
                            }
                        });
            }
        }, new UpdateAPPAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(UpdateAPPAlertDialog sweetAlertDialog) {
                // cancel
                sweetAlertDialog.dismiss();
            }
        }, new UpdateAPPAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(UpdateAPPAlertDialog sweetAlertDialog) {
                // cancel Download
                sweetAlertDialog.dismiss();
                stopDownloadAppService(downloadUrl);
            }
        });
    }

    private void startDownloadAppService(String downloadUrl) {
        Intent intent = new Intent(getActivity(), DownloadAppService.class);
        Bundle args = new Bundle();
        args.putString(MLConfig.KEY_BUNDLE_DOWNLOAD_URL, downloadUrl);
        intent.putExtras(args);
        getActivity().startService(intent);
    }

    private void stopDownloadAppService(String downloadUrl) {
        OkHttpUtils.getInstance().cancelTag(DownloadAppService.FILENAME);
    }

    private void getCommendReason() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Exception {
                if (emitter.isDisposed()) return;
                String result;
                result = mService.getCommendReasonFromAPI(teacherPhoneNumber);
                PreferencesUtils.putString(getActivity(), "CommendReason", result);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getTeacherLinkClass() {
        Observable.create(new ObservableOnSubscribe<List<ClassInfo>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<ClassInfo>> emitter) throws Exception {
                if (emitter.isDisposed()) return;
                List<ClassInfo> resultList = mService.getTeacherLinksClassFromAPI(teacherPhoneNumber);
                emitter.onNext(resultList);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ClassInfo>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposables.add(d);
                    }

                    @Override
                    public void onNext(List<ClassInfo> classInfos) {
                        if (classInfos.size() != 0) {
                            PreferencesUtils.putString(getActivity(), MLProperties.BUNDLE_KEY_CLASS_LINKED, "1");
                            updatePage1(classInfos);
                        } else {
                            PreferencesUtils.putString(getActivity(), MLProperties.BUNDLE_KEY_CLASS_LINKED, "0");
                            updatePage3();
                        }
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        updatePage2();
                        hideLoadingDialog();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void updatePage4() {
        llContent.setVisibility(View.GONE);
        llErrorContent.setVisibility(View.GONE);
        svNoClassLinked.setVisibility(View.GONE);
        mSpinnerClass.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.bottom_tab_0);

        llWithoutLogin.setVisibility(View.VISIBLE);
    }

    private void updatePage3() {
        llContent.setVisibility(View.GONE);
        llErrorContent.setVisibility(View.GONE);
        llWithoutLogin.setVisibility(View.GONE);
        svNoClassLinked.setVisibility(View.VISIBLE);
        mSpinnerClass.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.bottom_tab_0);
    }

    @OnClick(R.id.tv_apply)
    void onClickApplyForClass() {
        MobclickAgent.onEvent(getActivity(), "new_class_apply");
        Intent intent = new Intent(getActivity(), LoginHelpActivity.class);
        startActivity(intent);
    }

    private void updatePage2() {
        llContent.setVisibility(View.GONE);
        llWithoutLogin.setVisibility(View.GONE);
        llErrorContent.setVisibility(View.VISIBLE);
        svNoClassLinked.setVisibility(View.GONE);
        mSpinnerClass.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.bottom_tab_0);
    }

    private void updatePage1(List<ClassInfo> classInfos) {
        llContent.setVisibility(View.VISIBLE);
        llErrorContent.setVisibility(View.GONE);
        svNoClassLinked.setVisibility(View.GONE);
        llWithoutLogin.setVisibility(View.GONE);

        teacherClassList.clear();
        mTabs.clear();
        final ArrayList<String> classNameList = new ArrayList<>();
        // 初始化班级信息
        for (ClassInfo item : classInfos) {
            teacherClassList.add(new ClassInfo(item.getClassID(), item.getClassName()));
            classNameList.add(item.getClassName());
        }

        if (teacherClassList.size() == 1) {
            mTabLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            mFrameLayout.setVisibility(View.GONE);
        } else if (teacherClassList.size() <= minTabSize) {
            mTabLayout.setVisibility(View.VISIBLE);
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            mTabLayout.setVisibility(View.VISIBLE);
            mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        if (teacherClassList.size() < 9) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(R.string.bottom_tab_0);
            mViewPager.setVisibility(View.VISIBLE);
            mFrameLayout.setVisibility(View.GONE);
            mSpinnerClass.setVisibility(View.GONE);

            for (ClassInfo info : teacherClassList) {
                mTabs.add(ClassDetailFragment.newInstance(info.getClassID(), info.getClassName(), true));
            }
            SimpleFragmentPagerAdapter mAdapter = new SimpleFragmentPagerAdapter(fm,
                    mTabs, teacherClassList);
            mViewPager.setAdapter(mAdapter);
            mViewPager.setOffscreenPageLimit(teacherClassList.size());
            mTabLayout.setupWithViewPager(mViewPager);
        } else {
            mTabLayout.setVisibility(View.GONE);
            mViewPager.setVisibility(View.GONE);
            tvTitle.setVisibility(View.GONE);
            mFrameLayout.setVisibility(View.VISIBLE);
            mSpinnerClass.setVisibility(View.VISIBLE);

            mSpinnerClass.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.header_item_spinner, classNameList) {
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    TextView view = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.header_item_spinner_dropdown, null);
                    view.setText(classNameList.get(position));
                    return view;
                }
            });

            mSpinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FragmentTransaction ft = fm.beginTransaction();
                    ClassInfo item = teacherClassList.get(position);
                    Fragment fragment = ClassDetailFragment.newInstance(item.getClassID(), item.getClassName(), false);
                    if (mCurrFragment != null) {
                        ft.remove(mCurrFragment);
                    }
                    mCurrFragment = fragment;
                    ft.add(R.id.mFrameLayout, fragment, "123");
                    ft.commit();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private Fragment mCurrFragment = null;

    @OnClick(R.id.tv_reload)
    void clickReload() {
        llContent.setVisibility(View.VISIBLE);
        llErrorContent.setVisibility(View.GONE);
        svNoClassLinked.setVisibility(View.GONE);
        // showLoadingDialog();
        getTeacherLinkClass();
    }
}
