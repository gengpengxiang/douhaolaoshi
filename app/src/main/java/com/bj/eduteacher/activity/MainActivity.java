package com.bj.eduteacher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLConfig;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.MsgEvent;
import com.bj.eduteacher.community.main.view.CustomPopDialog;
import com.bj.eduteacher.community.main.view.FindFragment;
import com.bj.eduteacher.fragment.ChangeBottomTabListener;
import com.bj.eduteacher.fragment.DoukeFragment;
import com.bj.eduteacher.fragment.DoukeNewFragment;
import com.bj.eduteacher.fragment.StudyFragment;
import com.bj.eduteacher.fragment.UserFragment;
import com.bj.eduteacher.integral.model.Doubi;
import com.bj.eduteacher.integral.presenter.IntegralPresenter;
import com.bj.eduteacher.integral.view.IViewintegral;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.manager.UMPushManager;
import com.bj.eduteacher.utils.DensityUtils;
import com.bj.eduteacher.utils.IMMLeaks;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.LeakedUtils;
import com.bj.eduteacher.utils.LoginStatusUtil;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.widget.CustomViewPager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.BindViews;
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
import io.reactivex.schedulers.Schedulers;

//import com.tencent.TIMCallBack;
//import com.tencent.TIMFriendshipManager;
//import com.tencent.ilivesdk.ILiveSDK;
//import com.tencent.livesdk.ILVLiveManager;

/**
 * 首页
 */
public class MainActivity extends BaseActivity implements ChangeBottomTabListener, IViewintegral {

    private static final int TAB_NUMBER = 5;

    @BindView(R.id.mViewPager)
    CustomViewPager mViewPager;
    @BindView(R.id.ll_bottomBar)
    LinearLayout llBottomBar;
    @BindViews({R.id.iv_tab1, R.id.iv_tab2, R.id.iv_tab3, R.id.iv_tab4, R.id.iv_tab5})
    ImageView[] ivTabs;
    @BindViews({R.id.tv_tab1, R.id.tv_tab2, R.id.tv_tab3, R.id.tv_tab4, R.id.tv_tab5})
    TextView[] tvTabs;
    int[] resTabImageSelect = {R.mipmap.ic_tab_faxian_selected, R.mipmap.ic_tab_study_selected, R.mipmap.ic_tab_youke_selected, R.mipmap.ic_tab_shequ_selected, R.mipmap.ic_tab_wode_selected};
    int[] resTabImageUnSelect = {R.mipmap.ic_tab_faxian, R.mipmap.ic_tab_study, R.mipmap.ic_tab_youke, R.mipmap.ic_tab_shequ, R.mipmap.ic_tab_wode};
    @BindView(R.id.ll_tab4)
    LinearLayout llTab4;
    private Fragment[] mTabFragments;
    private long exitTime = 0;

    private static MainActivity instance = null;

    private int currentPageIndex = 0;
    private PopupWindow popupWindow;
    private boolean living;
    private String teacherPhoneNumber;

    private MyFragmentPagerAdapter mAdapter;

    private DoukeFragment doukeFragment;
    private DoukeNewFragment doukeNewFragment;
    private UserFragment userFragment;
    private FindFragment findFragment;
    private StudyFragment studyFragment;

    private CompositeDisposable mDisposables = new CompositeDisposable();

    private IntegralPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        instance = this;
        // 恢复数据
        if (savedInstanceState != null && 0 != savedInstanceState.getInt("CurrPageIndex")) {
            currentPageIndex = savedInstanceState.getInt("CurrPageIndex");
        }

        initToolBar();
        initView();

        loginRemindDialog();
    }

    private void loginRemindDialog() {
        Log.e("登陆提醒", teacherPhoneNumber + "xxx");
        if (PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID) == null
                && PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID) == null
                && PreferencesUtils.getInt(this, "remindLoginStatus", 1) == 0) {
            PreferencesUtils.putInt(this, "remindLoginStatus", 1);
            CustomPopDialog.Builder dialogBuild = new CustomPopDialog.Builder(MainActivity.this);
            final CustomPopDialog dialog = dialogBuild.create2(R.layout.dialog_login_remind);
            dialog.setCanceledOnTouchOutside(false);
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
                    IntentManager.toLoginSelectActivity(MainActivity.this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                }
            });
            dialog.show();
        }
    }

    @Override
    protected void initToolBar() {
        super.initToolBar();
    }

    @Override
    protected void initView() {
        currentPageIndex = getIntent().getIntExtra(MLProperties.BUNDLE_KEY_MAIN_PAGEINDEX, currentPageIndex);

        mTabFragments = new Fragment[TAB_NUMBER];

        doukeFragment = new DoukeFragment();
        doukeFragment.setBottomTabListener(this);
        //add by gpx
        findFragment = new FindFragment();
        studyFragment = new StudyFragment();
        //findFragment.setBottomTabListener(this);
        doukeNewFragment = new DoukeNewFragment();
        userFragment = new UserFragment();
        userFragment.setBottomTabListener(this);


        mTabFragments[0] = doukeFragment;
        mTabFragments[1] = studyFragment;
        mTabFragments[2] = doukeNewFragment;
        mTabFragments[3] = findFragment;
        mTabFragments[4] = userFragment;


        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
//        mViewPager.setCurrentItem(currentPageIndex);
        mViewPager.setOffscreenPageLimit(5);

        if(LoginStatusUtil.noLogin(this)){
            currentPageIndex = 0;
        }else {
            currentPageIndex = 1;
        }
        mViewPager.setCurrentItem(currentPageIndex);
        actionTabItemSelect(currentPageIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initData();
    }

    @Override
    protected void initData() {
        // 绑定别名
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID);
        if (!StringUtils.isEmpty(teacherPhoneNumber)) {
            UMPushManager.getInstance().setPushAlias(teacherPhoneNumber);
        }
        // 添加标签
        String schoolID = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_SCHOOL_CODE, "");
        if (!StringUtils.isEmpty(schoolID)) {
            UMPushManager.getInstance().addTag(schoolID);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // PopupWindow必须在某个事件中显示或者是开启一个新线程去调用，不能直接在onCreate方法中显示一个Popupwindow
        // 根据接口来控制开关
        // llBottomBar.post(() -> showDonationEntry());
        // 获取捐助功能是否显示
        getDonationStatus();
    }

    @OnClick({R.id.ll_tab1, R.id.ll_tab2, R.id.ll_tab3, R.id.ll_tab4, R.id.ll_tab5})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_tab1:
                actionTabItemSelect(0);
                break;
            case R.id.ll_tab2:
                actionTabItemSelect(1);
                break;
            case R.id.ll_tab3:
                actionTabItemSelect(2);
                break;
            case R.id.ll_tab4:
                actionTabItemSelect(3);
                break;
            case R.id.ll_tab5:
                if (LoginStatusUtil.noLogin(this)) {
                    IntentManager.toLoginSelectActivity(this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                    return;
                }
                actionTabItemSelect(4);
                break;
        }
    }

    private void actionTabItemSelect(int position) {
        llBottomBar.requestFocus();
        KeyBoardUtils.closeKeybord(llBottomBar.getWindowToken(), this);
        for (int i = 0; i < TAB_NUMBER; i++) {
            if (i == position) {
                ivTabs[i].setImageResource(resTabImageSelect[i]);
                tvTabs[i].setTextColor(ContextCompat.getColor(this, R.color.text_tab_selected));
            } else {
                ivTabs[i].setImageResource(resTabImageUnSelect[i]);
                tvTabs[i].setTextColor(ContextCompat.getColor(this, R.color.text_tab_unselected));
            }
        }
        if (currentPageIndex != position) {
            currentPageIndex = position;
            mViewPager.setCurrentItem(position);
        }
    }


    public static MainActivity getInstance() {
        return instance;
    }

    public static void finishSelf() {
        if (instance != null && !instance.isFinishing()) {
            instance.finish();
            instance = null;
        }
    }

    @Override
    protected void onDestroy() {
        LeakedUtils.fixTextLineCacheLeak();
        IMMLeaks.fixFocusedViewLeak(getApplication());
        // 退出的时候，释放Lebo占用的资源
        //HpplayLinkControl.getInstance().castDisconnectDevice();
        if (instance != null) instance = null;
        //改动
        EventBus.getDefault().post(new MsgEvent("quit"));

        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        mDisposables.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CurrPageIndex", currentPageIndex);
    }

    /**
     * 初始化捐助入口
     */
    private void initDonation() {
        View popView = LayoutInflater.from(this).inflate(R.layout.pop_donation_us_entry, null);
        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, false);
        // 设置点击返回键使其消失，且不影响背景，此时setOutsideTouchable函数即使设置为false
        // 点击PopupWindow 外的屏幕，PopupWindow依然会消失；相反，如果不设置BackgroundDrawable
        // 则点击返回键PopupWindow不会消失，同时，即时setOutsideTouchable设置为true
        // 点击PopupWindow 外的屏幕，PopupWindow依然不会消失
        // popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        popView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LoginStatusUtil.noLogin(getApplicationContext())) {
                    IntentManager.toLoginSelectActivity(MainActivity.this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
                    return;
                }
                Intent intent = new Intent(MainActivity.this, DonationActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 显示入口
     */
    private void showDonationEntry() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            // popupWindow.showAtLocation(llBottomBar, Gravity.BOTTOM | Gravity.RIGHT, 0, 0);
            popupWindow.showAsDropDown(llBottomBar, llBottomBar.getWidth() - DensityUtils.dp2px(this, 64),
                    -DensityUtils.dp2px(this, 130));
        }
    }

    private void hideDonationEntry() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 是否显示捐助的入口
     */
    private void getDonationStatus() {
        Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                LmsDataService mService = new LmsDataService();
                String[] result = mService.getDonationStatusFromAPI(MLConfig.KEY_DONATION_SEARCH_TYPE_JIAOSHI);
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(String[] strings) {
                        if (!StringUtils.isEmpty(strings[0]) && strings[0].equals("1")
                                && "open".equals(strings[2])) {
                            //showDonationEntry();
                        } else {
                            hideDonationEntry();
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
    public void onTabChange(int position) {
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        actionTabItemSelect(position);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        LL.i("MainActivity -- onNewIntent() 再次进入MainActivity");
        // 设置昵称
        String userName = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_CLASS_NAME);
        String nickname = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_TEACHER_NICK);
        String phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        if (!StringUtils.isEmpty(phone) && phone.length() > 10) {
            phone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        super.onNewIntent(intent);
    }

    @Override
    public void getDouBi(Doubi doubi) {
        EventBus.getDefault().post(new MsgEvent("getdoubisuccess", doubi.getData().getUser_doubinum_sum()));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateUI(MsgEvent event) {
        if (event.getAction().equals("phoneloginsuccess")) {
            teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

            presenter = new IntegralPresenter(this, this);
            String unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
            presenter.getDouBi("login", teacherPhoneNumber, "getdoubi", unionid);

            //add
            currentPageIndex = 1;
            mViewPager.setCurrentItem(currentPageIndex);
            actionTabItemSelect(currentPageIndex);
        }
        if (event.getAction().equals("wxloginsuccess")) {
            teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");

            presenter = new IntegralPresenter(this, this);
            String unionid = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_WECHAT_UNIONID, "");
            presenter.getDouBi("login", teacherPhoneNumber, "getdoubi", unionid);

            //add
            currentPageIndex = 1;
            mViewPager.setCurrentItem(currentPageIndex);
            actionTabItemSelect(currentPageIndex);
        }
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mTabFragments[position];
        }

        @Override
        public int getCount() {
            return TAB_NUMBER;
        }

        @Override
        public long getItemId(int position) {
            // 获取当前数据的hashCode  
            int hashCode = mTabFragments[position].hashCode();
            return hashCode;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) <= 2000) {
                // 退出APP
                finishSelf();
                // getActivity().exitApp();
            } else {
                Toast.makeText(this, getString(R.string.toast_home_exit_system), Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
