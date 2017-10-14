package com.bj.eduteacher.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.bj.eduteacher.fragment.ChangeBottomTabListener;
import com.bj.eduteacher.fragment.ConversationListFragment;
import com.bj.eduteacher.fragment.DoukeFragment;
import com.bj.eduteacher.fragment.DoukeNewFragment;
import com.bj.eduteacher.fragment.HomeFragment;
import com.bj.eduteacher.fragment.UserFragment;
import com.bj.eduteacher.manager.IntentManager;
import com.bj.eduteacher.manager.UMPushManager;
import com.bj.eduteacher.model.CurLiveInfo;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.presenter.UserServerHelper;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.tool.ShowNameUtil;
import com.bj.eduteacher.utils.DensityUtils;
import com.bj.eduteacher.utils.IMMLeaks;
import com.bj.eduteacher.utils.KeyBoardUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.LeakedUtils;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.widget.CustomViewPager;
import com.bj.eduteacher.widget.dialog.NotifyDialog;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.livesdk.ILVLiveManager;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

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
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements ChangeBottomTabListener {

    private static final int TAB_NUMBER = 5;

    @BindView(R.id.mViewPager)
    CustomViewPager mViewPager;
    @BindView(R.id.iv_notification)
    View ivNotification;
    @BindView(R.id.ll_bottomBar)
    LinearLayout llBottomBar;
    @BindViews({R.id.iv_tab1, R.id.iv_tab2, R.id.iv_tab3, R.id.iv_tab4, R.id.iv_tab5})
    ImageView[] ivTabs;
    @BindViews({R.id.tv_tab1, R.id.tv_tab2, R.id.tv_tab3, R.id.tv_tab4, R.id.tv_tab5})
    TextView[] tvTabs;
    int[] resTabImageSelect = {R.mipmap.ic_tab_home_selected, R.mipmap.ic_tab_read_selected, R.mipmap.ic_tab_class_teacher, R.drawable.ic_tab_communication, R.mipmap.ic_tab_person_selected};
    int[] resTabImageUnSelect = {R.mipmap.ic_tab_home, R.mipmap.ic_tab_read, R.mipmap.ic_tab_class_unselected, R.drawable.ic_tab_communication_unselected, R.mipmap.ic_tab_person};
    private Fragment[] mTabFragments;
    private long exitTime = 0;

    private static MainActivity instance = null;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver broadcastReceiver;

    private int currentPageIndex = 0;
    private PopupWindow popupWindow;
    private boolean living;
    private String teacherPhoneNumber;

    private MyFragmentPagerAdapter mAdapter;

    private DoukeFragment doukeFragment;
    private DoukeNewFragment doukeNewFragment;
    private HomeFragment homeFragment;
    private ConversationListFragment conversationListFragment;
    private UserFragment userFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        instance = this;
        // 恢复数据
        if (savedInstanceState != null && 0 != savedInstanceState.getInt("CurrPageIndex")) {
            currentPageIndex = savedInstanceState.getInt("CurrPageIndex");
        }
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        living = pref.getBoolean("living", false);

        initView();
        initDonation();

        // 检查上次是否是异常推出直播
        checkLiveException();
    }

    private void initView() {
        currentPageIndex = getIntent().getIntExtra(MLProperties.BUNDLE_KEY_MAIN_PAGEINDEX, currentPageIndex);

        mTabFragments = new Fragment[TAB_NUMBER];

        doukeFragment = new DoukeFragment();
        doukeFragment.setBottomTabListener(this);
        doukeNewFragment = new DoukeNewFragment();
        homeFragment = new HomeFragment();
        conversationListFragment = new ConversationListFragment();
        userFragment = new UserFragment();
        userFragment.setBottomTabListener(this);

        mTabFragments[0] = doukeFragment;
        mTabFragments[1] = doukeNewFragment;
        mTabFragments[2] = homeFragment;
        mTabFragments[3] = conversationListFragment;
        mTabFragments[4] = userFragment;

        mAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mViewPager.setCurrentItem(currentPageIndex);
        mViewPager.setOffscreenPageLimit(4);
        actionTabItemSelect(currentPageIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        initData();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!MainActivity.this.isFinishing()) {
                    RxPermissions rxPermissions = new RxPermissions(MainActivity.this);
                    rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE)
                            .subscribe();
                }
            }
        }, 1000);
    }

    private void initData() {
        teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        updateUnreadLabel();
        EMClient.getInstance().chatManager().addMessageListener(messageListener);
        // 添加标签
        String schoolID = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_SCHOOL_CODE, "");
        if (!StringUtils.isEmpty(schoolID)) {
            UMPushManager.getInstance().addTag(schoolID);
        }
        // 获取捐助功能是否显示
        getDonationStatus();

        if (!StringUtils.isEmpty(teacherPhoneNumber)) {
            // 初始化友盟
            UMPushManager manager = UMPushManager.getInstance();
            manager.setPushAlias(teacherPhoneNumber);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // PopupWindow必须在某个事件中显示或者是开启一个新线程去调用，不能直接在onCreate方法中显示一个Popupwindow
        // 根据接口来控制开关
        // llBottomBar.post(() -> showDonationEntry());
    }

    @OnClick(R.id.ll_tab1)
    void clickTab1() {
        actionTabItemSelect(0);
    }

    @OnClick(R.id.ll_tab2)
    void clickTab2() {
        actionTabItemSelect(1);
    }

    @OnClick(R.id.ll_tab3)
    void clickTab3() {
        actionTabItemSelect(2);
    }

    @OnClick(R.id.ll_tab4)
    void clickTab4() {
        actionTabItemSelect(3);
        ivNotification.setVisibility(View.GONE);
    }

    @OnClick(R.id.ll_tab5)
    void clickTab5() {
        if (StringUtils.isEmpty(teacherPhoneNumber)) {
            IntentManager.toLoginActivity(this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
            return;
        }
        actionTabItemSelect(4);
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

        if (instance != null) instance = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        EMClient.getInstance().chatManager().removeMessageListener(messageListener);
        super.onStop();
    }

    EMMessageListener messageListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            refreshUIWithMessage();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //red packet code : 处理红包回执透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {

        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
        }
    };

    private void refreshUIWithMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // refresh unread count
                updateUnreadLabel();
                // refresh conversation list
                if (mTabFragments[1] != null) {
                    ((ConversationListFragment) mTabFragments[1]).refresh();
                }
            }
        });
    }

    /**
     * update unread message count
     */
    private void updateUnreadLabel() {
        int count = getUnreadMsgCountTotal();
        if (currentPageIndex != 1 && count > 0) {
            ivNotification.setVisibility(View.VISIBLE);
        } else {
            ivNotification.setVisibility(View.GONE);
        }
    }

    /**
     * get unread message count
     *
     * @return
     */
    public int getUnreadMsgCountTotal() {
        int unreadMsgCountTotal = 0;
        int chatroomUnreadMsgCount = 0;
        unreadMsgCountTotal = EMClient.getInstance().chatManager().getUnreadMessageCount();
        for (EMConversation conversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
            if (conversation.getType() == EMConversation.EMConversationType.ChatRoom)
                chatroomUnreadMsgCount = chatroomUnreadMsgCount + conversation.getUnreadMsgCount();
        }
        return unreadMsgCountTotal - chatroomUnreadMsgCount;
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
                if (StringUtils.isEmpty(teacherPhoneNumber)) {
                    IntentManager.toLoginActivity(MainActivity.this, IntentManager.LOGIN_SUCC_ACTION_MAINACTIVITY);
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
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String[]>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String[] strings) {
                        if (!StringUtils.isEmpty(strings[0]) && strings[0].equals("1")
                                && "open".equals(strings[2])) {
                            llBottomBar.post(new Runnable() {
                                @Override
                                public void run() {
                                    showDonationEntry();
                                }
                            });
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
        if (StringUtils.isEmpty(teacherPhoneNumber)) {
            // 退出登录的时候需要手动刷新Homefragment的数据
            doukeFragment.cleanNotice();
            homeFragment.resetDataByHand();
        }
    }

    private void checkLiveException() {
        if (living) {
            NotifyDialog dialog = new NotifyDialog();
            dialog.show(getString(R.string.title_living), getSupportFragmentManager(), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, LiveActivity.class);
                    MySelfInfo.getInstance().setIdStatus(Constants.HOST);
                    MySelfInfo.getInstance().setJoinRoomWay(true);
                    CurLiveInfo.setHostID(MySelfInfo.getInstance().getId());
                    CurLiveInfo.setRoomNum(MySelfInfo.getInstance().getMyRoomNum());
                    String sxbTitle = PreferencesUtils.getString(MainActivity.this, MLProperties.PREFER_KEY_USER_SXB_Title, "");
                    String sxbPic = PreferencesUtils.getString(MainActivity.this, MLProperties.PREFER_KEY_USER_SXB_Picture, "");
                    CurLiveInfo.setTitle(sxbTitle);
                    if (!StringUtils.isEmpty(sxbPic)) {
                        CurLiveInfo.setCoverurl(sxbPic.substring(sxbPic.lastIndexOf("/") + 1));
                    }
                    intent.putExtra("HostComeBack", true);
                    startActivity(intent);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
                    editor.putBoolean("living", false);
                    editor.apply();
                    // 需要把直播关掉, 通知server 我不开了
                    exitRoom();
                }
            });
        }
    }

    private void exitRoom() {
        ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        ILVLiveManager.getInstance().quitRoom(null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserServerHelper.getInstance().notifyCloseLive();
                UserServerHelper.getInstance().reportMe(MySelfInfo.getInstance().getIdStatus(), 1);//通知server 我下线了
            }
        }).start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LL.i("MainActivity -- onNewIntent()");
        // 设置昵称
        String userName = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_CLASS_NAME);
        String nickname = PreferencesUtils.getString(this, MLProperties.BUNDLE_KEY_TEACHER_NICK);
        String phone = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
        if (!StringUtils.isEmpty(phone) && phone.length() > 10) {
            phone = phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
        TIMFriendshipManager.getInstance().setNickName(ShowNameUtil.getFirstNotNullParams(nickname, userName, phone), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
            }

            @Override
            public void onSuccess() {

            }
        });
        super.onNewIntent(intent);
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
}
