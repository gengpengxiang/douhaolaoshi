package com.bj.eduteacher.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bj.eduteacher.BaseActivity;
import com.bj.eduteacher.R;
import com.bj.eduteacher.adapter.ChatMsgListAdapter;
import com.bj.eduteacher.api.HttpUtilService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.TeacherInfo;
import com.bj.eduteacher.manager.ShareHelp;
import com.bj.eduteacher.model.ChatEntity;
import com.bj.eduteacher.model.CurLiveInfo;
import com.bj.eduteacher.model.LiveInfoJson;
import com.bj.eduteacher.model.MemberID;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.presenter.LiveHelper;
import com.bj.eduteacher.presenter.UserServerHelper;
import com.bj.eduteacher.presenter.viewinface.LiveView;
import com.bj.eduteacher.presenter.viewinface.ProfileView;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.tool.LogConstants;
import com.bj.eduteacher.tool.SxbLog;
import com.bj.eduteacher.tool.UIUtils;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.ScreenUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.utils.T;
import com.bj.eduteacher.widget.HeartLayout;
import com.bj.eduteacher.widget.dialog.InputTextMsgDialog;
import com.bj.eduteacher.widget.dialog.MembersDialog;
import com.bj.eduteacher.widget.dialog.RadioGroupDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.TIMMessage;
import com.tencent.TIMUserProfile;
import com.tencent.av.TIMAvManager;
import com.tencent.av.extra.effect.AVVideoEffect;
import com.tencent.av.opengl.ui.GLView;
import com.tencent.av.sdk.AVAudioCtrl;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivefilter.TILFilter;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILivePushOption;
import com.tencent.ilivesdk.core.ILiveRecordOption;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.data.ILivePushRes;
import com.tencent.ilivesdk.data.ILivePushUrl;
import com.tencent.ilivesdk.tools.quality.ILiveQualityData;
import com.tencent.ilivesdk.tools.quality.LiveInfo;
import com.tencent.ilivesdk.view.AVRootView;
import com.tencent.ilivesdk.view.AVVideoView;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zz379 on 2017/9/5.
 * 直播间类：游客和主播
 */

public class LiveActivity extends BaseActivity implements LiveView, View.OnClickListener, ProfileView {
    private static final String TAG = LiveActivity.class.getSimpleName();
    private static final int GETPROFILE_JOIN = 0x200;

    private LiveHelper mLiveHelper;

    private ArrayList<ChatEntity> mArrayListChatEntity;
    private ChatMsgListAdapter mChatMsgListAdapter;
    private static final int MINFRESHINTERVAL = 500;
    private static final int UPDAT_WALL_TIME_TIMER_TASK = 1;
    private static final int TIMEOUT_INVITE = 2;
    private boolean mBoolRefreshLock = false;
    private boolean mBoolNeedRefresh = false;
    private final Timer mTimer = new Timer();
    private ArrayList<ChatEntity> mTmpChatList = new ArrayList<ChatEntity>();//缓冲队列
    private TimerTask mTimerTask = null;
    private static final int REFRESH_LISTVIEW = 5;
    private Dialog mMemberDg, inviteDg;
    private HeartLayout mHeartLayout;
    private HeartBeatTask mHeartBeatTask;//心跳
    private SimpleDraweeView mHeadIcon;
    private TextView mHostNameTv;
    private LinearLayout mHostLayout, mHostLeaveLayout;
    // private final int REQUEST_PHONE_PERMISSIONS = 0;
    private long mSecond = 0;
    private String formatTime;
    private Timer mHearBeatTimer, mVideoTimer;
    private VideoTimerTask mVideoTimerTask;//计时器
    private TextView mVideoTime;
    private ObjectAnimator mObjAnim;
    private ImageView mRecordBall;
    private ImageView mQualityCircle;
    private TextView mQualityText;
    // private TextView roomId;
    private int thumbUp = 0;
    private long admireTime = 0;
    private int watchCount = 0;
    private boolean bCleanMode = false;
    private boolean bInAvRoom = false, bSlideUp = false, bDelayQuit = false;
    private boolean bReadyToChange = false;
    private boolean bHLSPush = false;
    private boolean bVideoMember = false;       // 是否上麦观众

    private String backGroundId;

    private TextView tvMembers;
    private TextView tvAdmires;
    private AVRootView mRootView;

    private Dialog mDetailDialog;

    private TILFilter mUDFilter; //美颜处理器
    private String teacherPhoneNumber;
    private TextView tvMsgInput;
    private View msgInputInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   // 不锁屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        checkPermission();

        mLiveHelper = new LiveHelper(this, this);
        initStatus();
        initView();
        backGroundId = CurLiveInfo.getHostID();
        //进入房间流程
        boolean isHostComeBack = getIntent().getBooleanExtra("HostComeBack", false);
        if (isHostComeBack) {
            mLiveHelper.comeBackRoom();
        } else {
            mLiveHelper.startEnterRoom();
        }
        if (MySelfInfo.getInstance().getIdStatus() != Constants.HOST) {
            // 如果是观众需要查看是否有发送弹幕的权限
            teacherPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
            mLiveHelper.checkPermission(teacherPhoneNumber);
            // 如果是观众需要查看房间的点赞数量
            mLiveHelper.operateRoomGoodNum(String.valueOf(CurLiveInfo.getRoomNum()), "search");
        }
    }

    @Override
    protected void initStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            // 如果存在虚拟按键，则设置虚拟按键的背景色
            if (ScreenUtils.isNavigationBarShow(this)) {
                getWindow().setNavigationBarColor(ContextCompat.getColor(this, android.R.color.black));
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDAT_WALL_TIME_TIMER_TASK:
                    updateWallTime();
                    break;
                case REFRESH_LISTVIEW:
                    doRefreshListView();
                    break;
            }
            return false;
        }
    });

    /**
     * 时间格式化
     */
    private void updateWallTime() {
        String hs, ms, ss;

        long h, m, s;
        h = mSecond / 3600;
        m = (mSecond % 3600) / 60;
        s = (mSecond % 3600) % 60;
        if (h < 10) {
            hs = "0" + h;
        } else {
            hs = "" + h;
        }

        if (m < 10) {
            ms = "0" + m;
        } else {
            ms = "" + m;
        }

        if (s < 10) {
            ss = "0" + s;
        } else {
            ss = "" + s;
        }
        if (hs.equals("00")) {
            formatTime = ms + ":" + ss;
        } else {
            formatTime = hs + ":" + ms + ":" + ss;
        }

        if (Constants.HOST == MySelfInfo.getInstance().getIdStatus() && null != mVideoTime) {
            SxbLog.i(TAG, " refresh time ");
            mVideoTime.setText(formatTime);
        }
    }

    /**
     * 初始化UI
     */
    private TextView BtnBack, BtnMic, BtnNormal, mBeautyConfirm;
    private TextView btnChageVoice, btnFlash, btnChangeRole, btnFilter;
    private ListView mListViewMsgItems;
    private LinearLayout mHostCtrView, mCtrViewMore, mNomalMemberCtrView, mBeautySettings;
    private FrameLayout mFullControllerUi;
    private SeekBar mBeautyBar, mWhiteBar;
    private int mBeautyRate, mWhiteRate;
    private TextView pushBtn, recordBtn;
    private TextView tvHostShare, tvMemberShare;

    private void showHeadIcon(SimpleDraweeView view, String avatar) {
//        if (TextUtils.isEmpty(avatar)) {
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar);
//            Bitmap cirBitMap = UIUtils.createCircleImage(bitmap, 0);
//            view.setImageBitmap(cirBitMap);
//        } else {
//            SxbLog.d(TAG, "load icon: " + avatar);
//            RequestManager req = Glide.with(this);
//            req.load(avatar).transform(new GlideCircleTransform(this)).into(view);
//        }
        view.setImageURI(avatar);
    }

    private void initILiveBeauty() {
        if (null == mUDFilter) {
            SxbLog.d(TAG, "FILTER->created");
            mUDFilter = new TILFilter(this);
            mUDFilter.setFilter(1);
            mUDFilter.setBeauty(0);
            mUDFilter.setWhite(0);
            ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(new AVVideoCtrl.LocalVideoPreProcessCallback() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void onFrameReceive(AVVideoCtrl.VideoFrame var1) {
                    mUDFilter.processData(var1.data, var1.dataLen, var1.width, var1.height, var1.srcType);
                }
            });
        }
    }

    /**
     * 初始化界面
     */
    @Override
    protected void initView() {
        mHostCtrView = (LinearLayout) findViewById(R.id.host_bottom_layout);
        mCtrViewMore = (LinearLayout) findViewById(R.id.host_bottom_layout_more);
        mNomalMemberCtrView = (LinearLayout) findViewById(R.id.member_bottom_layout);
        mHostLeaveLayout = (LinearLayout) findViewById(R.id.ll_host_leave);

        mHeartLayout = (HeartLayout) findViewById(R.id.heart_layout);
        mVideoTime = (TextView) findViewById(R.id.broadcasting_time);
        mHeadIcon = (SimpleDraweeView) findViewById(R.id.head_icon);
        mHostNameTv = (TextView) findViewById(R.id.host_name);
        tvMembers = (TextView) findViewById(R.id.member_counts);
        tvAdmires = (TextView) findViewById(R.id.heart_counts);
        mQualityText = (TextView) findViewById(R.id.quality_text);
        mQualityCircle = (ImageView) findViewById(R.id.quality_circle);

        tvHostShare = (TextView) findViewById(R.id.host_share);
        tvMemberShare = (TextView) findViewById(R.id.member_share);

        // 通用对话框初始化
        initVoiceTypeDialog();
        initFilterDialog();
        initRoleDialog();

        // 通用按钮初始化
        findViewById(R.id.back_primary).setOnClickListener(this);

        btnChageVoice = (TextView) findViewById(R.id.change_voice);
        btnChangeRole = (TextView) findViewById(R.id.change_role);
        btnFlash = (TextView) findViewById(R.id.flash_btn);
        btnFilter = (TextView) findViewById(R.id.tv_filter);

        btnChageVoice.setOnClickListener(this);
        btnChangeRole.setOnClickListener(this);
        btnFlash.setOnClickListener(this);
        btnFilter.setOnClickListener(this);

        mBeautySettings = (LinearLayout) findViewById(R.id.qav_beauty_setting);
        mBeautyConfirm = (TextView) findViewById(R.id.qav_beauty_setting_finish);
        mBeautyConfirm.setOnClickListener(this);
        mBeautyBar = (SeekBar) (findViewById(R.id.qav_beauty_progress));
        mBeautyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SxbLog.d("SeekBar", "onStopTrackingTouch");
                // Toast.makeText(LiveActivity.this, "beauty " + mBeautyRate + "%", Toast.LENGTH_SHORT).show();//美颜度
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                SxbLog.d("SeekBar", "onStartTrackingTouch");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Log.i(TAG, "onProgressChanged " + progress);
                mBeautyRate = progress;
                if (MySelfInfo.getInstance().getBeautyType() == 1) {
                    initILiveBeauty();
                    mUDFilter.setBeauty(progress * 7 / 100);
                } else {//美颜
                    ILiveRoomManager.getInstance().enableBeauty(getBeautyProgress(progress));
                }
            }
        });
        mWhiteBar = (SeekBar) (findViewById(R.id.qav_white_progress));
        mWhiteBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SxbLog.d("SeekBar", "onStopTrackingTouch");
                // Toast.makeText(LiveActivity.this, "white " + mWhiteRate + "%", Toast.LENGTH_SHORT).show();//美白度
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                SxbLog.d("SeekBar", "onStartTrackingTouch");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                Log.i(TAG, "onProgressChanged " + progress);
                mWhiteRate = progress;
                if (MySelfInfo.getInstance().getBeautyType() == 1) {
                    initILiveBeauty();
                    mUDFilter.setWhite(progress * 9 / 100);
                } else {//美白
                    ILiveRoomManager.getInstance().enableWhite(getBeautyProgress(progress));
                }
            }
        });

        //for 测试用
        tvTipsMsg = (TextView) findViewById(R.id.qav_tips_msg);
        tvTipsMsg.setTextColor(Color.BLACK);
        paramTimer.schedule(task, 1000, 1000);

        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            // 初始化主播控件
            mHostCtrView.setVisibility(View.VISIBLE);
            mNomalMemberCtrView.setVisibility(View.GONE);
            mRecordBall = (ImageView) findViewById(R.id.record_ball);
            BtnMic = (TextView) findViewById(R.id.host_mic_btn);
            // 推流
            pushBtn = (TextView) findViewById(R.id.push_btn);
            pushBtn.setVisibility(View.GONE);
            pushBtn.setOnClickListener(this);
            // 录制
            recordBtn = (TextView) findViewById(R.id.record_btn);
            recordBtn.setVisibility(View.GONE);
            recordBtn.setOnClickListener(this);

            tvHostShare.setVisibility(View.VISIBLE);
            tvHostShare.setOnClickListener(this);
            tvMemberShare.setVisibility(View.GONE);

            findViewById(R.id.host_message_input).setOnClickListener(this);
            findViewById(R.id.host_fullscreen_btn).setOnClickListener(this);
            findViewById(R.id.host_switch_cam).setOnClickListener(this);
            findViewById(R.id.host_beauty_btn).setOnClickListener(this);
            findViewById(R.id.host_menu_more).setOnClickListener(this);

            tvAdmires.setVisibility(View.VISIBLE);

            initBackDialog();
            initDetailDailog();

            mMemberDg = new MembersDialog(this, R.style.floag_dialog, this);
            startRecordAnimation();
            showHeadIcon(mHeadIcon, MySelfInfo.getInstance().getAvatar());
        } else {
            // 初始化观众控件
            mHostCtrView.setVisibility(View.GONE);
            changeCtrlView(bVideoMember);

            BtnMic = (TextView) findViewById(R.id.vmember_mic_btn);

            findViewById(R.id.record_tip).setVisibility(View.GONE);
            mHostNameTv.setVisibility(View.VISIBLE);
            tvAdmires.setVisibility(View.VISIBLE);

            findViewById(R.id.member_fullscreen_btn).setOnClickListener(this);
            findViewById(R.id.member_send_good).setOnClickListener(this);
            msgInputInterval = findViewById(R.id.member_message_interval);
            tvMsgInput = (TextView) findViewById(R.id.member_message_input);
            tvMsgInput.setOnClickListener(this);

            List<String> ids = new ArrayList<>();
            ids.add(CurLiveInfo.getHostID());
            showHeadIcon(mHeadIcon, CurLiveInfo.getHostAvator());
            // mHostNameTv.setText(UIUtils.getLimitString(CurLiveInfo.getHostID(), 10));
            mHostNameTv.setText(UIUtils.getLimitString(CurLiveInfo.getHostName(), 5));

            mHostLayout = (LinearLayout) findViewById(R.id.head_up_layout);
            // mHostLayout.setOnClickListener(this);

            tvHostShare.setVisibility(View.GONE);
            tvMemberShare.setVisibility(View.VISIBLE);
            tvMemberShare.setOnClickListener(this);
        }
        BtnMic.setOnClickListener(this);

        BtnNormal = (TextView) findViewById(R.id.normal_btn);
        BtnNormal.setOnClickListener(this);
        mFullControllerUi = (FrameLayout) findViewById(R.id.controll_ui);

        initPushDialog();

        BtnBack = (TextView) findViewById(R.id.btn_back);
        BtnBack.setOnClickListener(this);

        mListViewMsgItems = (ListView) findViewById(R.id.im_msg_listview);
        mArrayListChatEntity = new ArrayList<ChatEntity>();
        mChatMsgListAdapter = new ChatMsgListAdapter(this, mListViewMsgItems, mArrayListChatEntity);
        mListViewMsgItems.setAdapter(mChatMsgListAdapter);

        tvMembers.setText("" + CurLiveInfo.getMembers());
        // tvAdmires.setText("" + CurLiveInfo.getAdmires());

        //TODO 获取渲染层
        mRootView = (AVRootView) findViewById(R.id.av_root_view);
        //TODO 设置渲染层
        ILVLiveManager.getInstance().setAvVideoView(mRootView);

        mRootView.setBackground(R.mipmap.renderback);
        mRootView.setGravity(AVRootView.LAYOUT_GRAVITY_RIGHT);
        mRootView.setSubMarginY(getResources().getDimensionPixelSize(R.dimen.small_area_margin_top));
        mRootView.setSubMarginX(getResources().getDimensionPixelSize(R.dimen.small_area_marginright));
        mRootView.setSubPadding(getResources().getDimensionPixelSize(R.dimen.small_area_marginbetween));
        mRootView.setSubWidth(getResources().getDimensionPixelSize(R.dimen.small_area_width));
        mRootView.setSubHeight(getResources().getDimensionPixelSize(R.dimen.small_area_height));
        mRootView.setSubCreatedListener(new AVRootView.onSubViewCreatedListener() {
            @Override
            public void onSubViewCreated() {
                for (int i = 1; i < ILiveConstants.MAX_AV_VIDEO_NUM; i++) {
                    final int index = i;
                    AVVideoView avVideoView = mRootView.getViewByIndex(index);
                    avVideoView.setRotate(false);
                    avVideoView.setGestureListener(new GestureDetector.SimpleOnGestureListener() {
                        @Override
                        public boolean onSingleTapConfirmed(MotionEvent e) {
                            mRootView.swapVideoView(0, index);
                            backGroundId = mRootView.getViewByIndex(0).getIdentifier();
                            return super.onSingleTapConfirmed(e);
                        }
                    });
                }

                mRootView.getViewByIndex(0).setRotate(false);
                mRootView.getViewByIndex(0).setBackground(R.mipmap.renderback);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            mLiveHelper.enableCamera();
            mLiveHelper.enableMic();
        }
        ILiveRoomManager.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            mLiveHelper.disableCamera();
            mLiveHelper.disableMic();
        }
        ILiveRoomManager.getInstance().onPause();
    }


    /**
     * 直播心跳
     */
    private class HeartBeatTask extends TimerTask {
        @Override
        public void run() {
            String host = CurLiveInfo.getHostID();
            SxbLog.i(TAG, "HeartBeatTask " + host);
            if (!TextUtils.isEmpty(MySelfInfo.getInstance().getId()) && MySelfInfo.getInstance().getId().equals(CurLiveInfo.getHostID()))
                UserServerHelper.getInstance().heartBeater(1);
            else
                UserServerHelper.getInstance().heartBeater(MySelfInfo.getInstance().getIdStatus());
            mLiveHelper.pullMemberList();
        }
    }

    /**
     * 记时器
     */
    private class VideoTimerTask extends TimerTask {
        public void run() {
            SxbLog.i(TAG, "timeTask ");
            ++mSecond;
            if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST)
                mHandler.sendEmptyMessage(UPDAT_WALL_TIME_TIMER_TASK);
        }
    }

    @Override
    protected void onDestroy() {
        watchCount = 0;
        super.onDestroy();
        if (null != mHearBeatTimer) {
            mHearBeatTimer.cancel();
            mHearBeatTimer = null;
        }
        if (null != mVideoTimer) {
            mVideoTimer.cancel();
            mVideoTimer = null;
        }
        if (null != paramTimer) {
            paramTimer.cancel();
            paramTimer = null;
        }


        inviteViewCount = 0;
        thumbUp = 0;
        CurLiveInfo.setMembers(0);
        CurLiveInfo.setAdmires(0);
        CurLiveInfo.setCurrentRequestCount(0);
        mLiveHelper.onDestory();
    }


    /**
     * 点击Back键
     */
    @Override
    public void onBackPressed() {
        // more 按钮的点击情况
        if (mCtrViewMore.getVisibility() == View.VISIBLE && mHostCtrView.getVisibility() != View.VISIBLE) {
            mCtrViewMore.setVisibility(View.INVISIBLE);
            mHostCtrView.setVisibility(View.VISIBLE);
            return;
        }
        // 美颜调整框显示的情况
        if (mBeautySettings.getVisibility() == View.VISIBLE && mFullControllerUi.getVisibility() != View.VISIBLE) {
            mBeautySettings.setVisibility(View.GONE);
            mFullControllerUi.setVisibility(View.VISIBLE);
            return;
        }
        // 全屏显示的情况
        if (BtnNormal.getVisibility() == View.VISIBLE && mFullControllerUi.getVisibility() != View.VISIBLE) {
            bCleanMode = false;
            BtnNormal.setVisibility(View.GONE);
            mFullControllerUi.setVisibility(View.VISIBLE);
            return;
        }

        if (bInAvRoom) {
            bDelayQuit = false;
            quiteLiveByPurpose();
        } else {
            clearOldData();
            finish();
        }
    }

    @Override
    public void forceQuitRoom(String strMessage) {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        ILiveRoomManager.getInstance().onPause();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.str_tips_title)
                .setMessage(strMessage)
                .setPositiveButton(R.string.btn_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                callExitRoom();
            }
        });
        alertDialog.show();
    }


    /**
     * 主动退出直播
     */
    private void quiteLiveByPurpose() {
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            if (backDialog.isShowing() == false)
                backDialog.show();
        } else {
            callExitRoom();
        }
    }

    private void callExitRoom() {
        mLiveHelper.startExitRoom();
    }

    private Dialog backDialog;

    private void initBackDialog() {
        backDialog = new Dialog(this, R.style.dialog);
        backDialog.setContentView(R.layout.dialog_end_live);
        TextView tvSure = (TextView) backDialog.findViewById(R.id.btn_sure);
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ILVCustomCmd cmd = new ILVCustomCmd();
                cmd.setCmd(Constants.AVIMCMD_EXITLIVE);
                cmd.setType(ILVText.ILVTextType.eGroupMsg);
                ILVLiveManager.getInstance().sendCustomCmd(cmd, new ILiveCallBack<TIMMessage>() {
                    @Override
                    public void onSuccess(TIMMessage data) {
                        //如果是直播，发消息
                        if (null != mLiveHelper) {
                            // 取消录制
                            if (mRecord) {
                                stopRecordLive();
                            }
                            // 退出房间
                            callExitRoom();
                            // 取消推流
                            if (isPushed) {
                                mLiveHelper.stopPush();
                            }
                        }
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                    }

                });
                // 取消跨房连麦
                ILVLiveManager.getInstance().unlinkRoom(null);
                backDialog.dismiss();
            }
        });
        TextView tvCancel = (TextView) backDialog.findViewById(R.id.btn_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backDialog.cancel();
            }
        });
    }

    // 变声对话框
    private RadioGroupDialog voiceTypeDialog;
    private int curVoice = 0;

    private void initVoiceTypeDialog() {
        final String[] roles = new String[]{"原声", "萝莉", "大叔", "空灵", "幼稚园", "重机器",
                "擎天柱", "困兽", "土掉渣/歪果仁/方言", "金属机器人", "死肥仔"};
        voiceTypeDialog = new RadioGroupDialog(this, roles);
        voiceTypeDialog.setTitle(R.string.str_dt_voice);
        voiceTypeDialog.setSelected(curVoice);
        voiceTypeDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SxbLog.d(TAG, "initRoleDialog->onClick item:" + position);
                curRole = position;
                ILiveSDK.getInstance().getAvAudioCtrl().setVoiceType(curRole);
            }
        });
    }

    // 角色对话框
    private RadioGroupDialog roleDialog;
    private int curRole = 0;

    private void initRoleDialog() {
        // final String[] roles = new String[]{"高清(960*540,25fps)", "标清(640*368,20fps)", "流畅(640*368,15fps)"};
        final String[] roles = new String[]{"标清(640*368,20fps)", "流畅(640*368,15fps)"};
        final String[] values = new String[]{Constants.SD_ROLE, Constants.LD_ROLE};
        // final String[] values = new String[]{Constants.HD_ROLE, Constants.SD_ROLE, Constants.LD_ROLE};
        final String[] guestValues = new String[]{Constants.HD_GUEST_ROLE, Constants.SD_GUEST_ROLE, Constants.LD_GUEST_ROLE};
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            if (CurLiveInfo.getCurRole().equals(Constants.SD_ROLE)) {
                curRole = 1;
            } else if (CurLiveInfo.getCurRole().equals(Constants.LD_ROLE)) {
                curRole = 2;
            }
        }
        roleDialog = new RadioGroupDialog(this, roles);

        roleDialog.setTitle(R.string.str_dt_change_role);
        roleDialog.setSelected(curRole);
        roleDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SxbLog.d(TAG, "initRoleDialog->onClick item:" + position);
                curRole = position;
                if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
                    mLiveHelper.changeRole(values[curRole]);
                } else {
                    mLiveHelper.changeRole(guestValues[curRole]);
                }
            }
        });
    }

    // 滤镜对话框
    private RadioGroupDialog filterDialog;
    private int curFilter = 0;

    private void initFilterDialog() {
        final String filterRootPath = "assets://qaveffect/filter/";
        final String[] filters = new String[]{"清空滤镜", "漫画(COMIC)",
                "盛夏(GESE)", "暖阳(BRIGHTFIRE)",
                "月光(SKYLINE)", "蔷薇(G1)",
                "幽兰(ORCHID)", "圣代(SHENGDAI)",
                "薄荷(AMARO)", "浪漫(FENBI)"};
        final String[] values = new String[]{null, filterRootPath + "COMIC",
                filterRootPath + "GESE", filterRootPath + "BRIGHTFIRE",
                filterRootPath + "SKYLINE", filterRootPath + "G1",
                filterRootPath + "ORCHID", filterRootPath + "SHENGDAI",
                filterRootPath + "AMARO", filterRootPath + "FENBI"};
        filterDialog = new RadioGroupDialog(this, filters);
        filterDialog.setTitle(R.string.str_dt_filter);
        filterDialog.setSelected(curFilter);
        filterDialog.setOnItemClickListener(new RadioGroupDialog.onItemClickListener() {
            @Override
            public void onItemClick(int position) {
                SxbLog.d(TAG, "initFilterDialog->onClick item:" + position);
                curFilter = position;
                AVVideoEffect.getInstance(LiveActivity.this).setFilter(values[position]);
            }
        });
    }

    private void initPtuEnv() {
        AVVideoEffect mEffect = AVVideoEffect.getInstance(LiveActivity.this);

        AVVideoCtrl avVideoCtrl = ILiveSDK.getInstance().getAvVideoCtrl();
        if (null != avVideoCtrl)
            avVideoCtrl.setEffect(mEffect);
    }

    /**
     * 完成进出房间流程
     */
    @Override
    public void enterRoomComplete(int id_status, boolean isSucc) {
        // 录制Dialog:房间号发生了变化
        initRecordDialog();
        // 重置滤镜
        AVVideoEffect.getInstance(this).setFilter(null);
        // 重置脸萌
        AVVideoEffect.getInstance(this).setPendant(null);
        // 重置美颜
        ILiveRoomManager.getInstance().enableBeauty(0);
        ILiveRoomManager.getInstance().enableWhite(0);

        mRootView.getViewByIndex(0).setVisibility(GLView.VISIBLE);

        bInAvRoom = true;
        bDelayQuit = true;
        bReadyToChange = true;
        if (isSucc == true) {
            //主播心跳
            mHearBeatTimer = new Timer(true);
            mHeartBeatTask = new HeartBeatTask();
            mHearBeatTimer.schedule(mHeartBeatTask, 100, 5 * 1000); //5秒重复上报心跳 拉取房间列表

            //直播时间
            mVideoTimer = new Timer(true);
            mVideoTimerTask = new VideoTimerTask();
            mVideoTimer.schedule(mVideoTimerTask, 1000, 1000);

            initPtuEnv();
            //IM初始化
            if (id_status == Constants.HOST) {//主播方式加入房间成功
                // mHostNameTv.setText(MySelfInfo.getInstance().getId());
                String userPhoneNumber = PreferencesUtils.getString(this, MLProperties.PREFER_KEY_USER_ID, "");
                String name = MySelfInfo.getInstance().getNickName();
                if (StringUtils.isEmpty(name)) {
                    mHostNameTv.setText(userPhoneNumber);
                } else {
                    mHostNameTv.setText(name);
                }

                //注册一个音频回调为变声用
                ILiveSDK.getInstance().getAvAudioCtrl().registAudioDataCallbackWithByteBuffer(AVAudioCtrl.AudioDataSourceType.AUDIO_DATA_SOURCE_VOICEDISPOSE, new AVAudioCtrl.RegistAudioDataCompleteCallbackWithByteBuffer() {
                    @Override
                    public int onComplete(AVAudioCtrl.AudioFrameWithByteBuffer audioFrameWithByteBuffer, int i) {
                        return 0;
                    }
                });

                //开启摄像头渲染画面
                SxbLog.i(TAG, "createlive enterRoomComplete isSucc" + isSucc);
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                editor.putBoolean("living", true);
                editor.apply();

                // 打开直播录制
                startRecordLive();
            } else {
                // 更新控制栏
                changeCtrlView(false);
                //发消息通知上线
                mLiveHelper.sendGroupCmd(Constants.AVIMCMD_ENTERLIVE, "");
            }
        }
    }


    @Override
    public void exitErrorRoom() {
        // 重新启动
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.act_right_left_in, R.anim.act_right_left_out);
        // this.finish();
    }

    @Override
    public void quiteRoomComplete(int id_status, boolean succ, LiveInfoJson liveinfo) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                UserServerHelper.getInstance().reportMe(MySelfInfo.getInstance().getIdStatus(), 1);//通知server 我下线了
            }
        }.start();

        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
//            if ((getBaseContext() != null) && (null != mDetailDialog) && (mDetailDialog.isShowing() == false)) {
//                SxbLog.d(TAG, LogConstants.ACTION_HOST_QUIT_ROOM + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + "quite room callback"
//                        + LogConstants.DIV + LogConstants.STATUS.SUCCEED + LogConstants.DIV + "id status " + id_status);
//                mDetailTime.setText(formatTime);
//                mDetailAdmires.setText("" + CurLiveInfo.getAdmires());
//                mDetailWatchCount.setText("" + watchCount);
//                mDetailDialog.show();
//            }
            SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putBoolean("living", false);
            editor.apply();
            clearOldData();
            // 上报录制房间信息
            mLiveHelper.notifyNewRecordInfo(CurLiveInfo.getTitle());
            finish();
        } else {
            clearOldData();
            finish();
        }

        if (null != mUDFilter && MySelfInfo.getInstance().getBeautyType() == 1) {
            SxbLog.d(TAG, "FILTER->destory");
            mUDFilter.setFilter(-1);
            mUDFilter.destroyFilter();
            mUDFilter = null;
        }

        //发送
        bInAvRoom = false;
    }


    private TextView mDetailTime, mDetailAdmires, mDetailWatchCount;

    private void initDetailDailog() {
        mDetailDialog = new Dialog(this, R.style.dialog);
        mDetailDialog.setContentView(R.layout.dialog_live_detail);
        mDetailTime = (TextView) mDetailDialog.findViewById(R.id.tv_time);
        mDetailAdmires = (TextView) mDetailDialog.findViewById(R.id.tv_admires);
        mDetailWatchCount = (TextView) mDetailDialog.findViewById(R.id.tv_members);

        mDetailDialog.setCancelable(false);

        TextView tvCancel = (TextView) mDetailDialog.findViewById(R.id.btn_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDetailDialog.dismiss();
                finish();
            }
        });
    }

    /**
     * 成员状态变更
     */
    @Override
    public void memberJoin(String id, String name) {
        SxbLog.d(TAG, LogConstants.ACTION_VIEWER_ENTER_ROOM + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + "on member join" +
                LogConstants.DIV + "join room " + id);
        watchCount++;
        refreshTextListView(TextUtils.isEmpty(name) ? id : name, "加入房间", Constants.MEMBER_ENTER);
    }


    @Override
    public void hostLeave(String id, String name) {
        refreshTextListView("主播", "离开了房间", Constants.HOST_LEAVE);
    }

    @Override
    public void hostBack(String id, String name) {
        // refreshTextListView(TextUtils.isEmpty(name) ? id : name, "回来了", Constants.HOST_BACK);
        refreshTextListView("主播", "又回来了", Constants.HOST_BACK);
    }

    @Override
    public void refreshMember(ArrayList<MemberID> memlist) {
        if (memlist != null && tvMembers != null)
            tvMembers.setText("" + memlist.size());
    }

    @Override
    public void linkRoomReq(final String id, String name) {
    }

    @Override
    public void linkRoomAccept(final String id, final String strRoomId) {
    }

    /**
     * 红点动画
     */
    private void startRecordAnimation() {
        mObjAnim = ObjectAnimator.ofFloat(mRecordBall, "alpha", 1f, 0f, 1f);
        mObjAnim.setDuration(1000);
        mObjAnim.setRepeatCount(-1);
        mObjAnim.start();
    }

    private float getBeautyProgress(int progress) {
        SxbLog.d("shixu", "progress: " + progress);
        return (9.0f * progress / 100.0f);
    }


    @Override
    public void showInviteDialog() {
        if ((inviteDg != null) && (getBaseContext() != null) && (inviteDg.isShowing() != true)) {
            inviteDg.show();
        }
    }

    @Override
    public void hideInviteDialog() {
        if ((inviteDg != null) && (inviteDg.isShowing() == true)) {
            inviteDg.dismiss();
        }
    }


    @Override
    public void refreshText(String text, String name) {
        if (text != null) {
            refreshTextListView(name, text, Constants.TEXT_TYPE);
        }
    }

    @Override
    public void refreshThumbUp() {
        CurLiveInfo.setAdmires(CurLiveInfo.getAdmires() + 1);
        if (!bCleanMode) {      // 纯净模式下不播放飘星动画
            mHeartLayout.addFavor();
        }
        tvAdmires.setText("" + CurLiveInfo.getAdmires());
    }

    private int inviteViewCount = 0;

    @Override
    public boolean showInviteView(String id) {
        return true;
    }

    @Override
    public void cancelInviteView(String id) {
    }

    @Override
    public void cancelMemberView(String id) {
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            mLiveHelper.sendGroupCmd(Constants.AVIMCMD_MULTI_CANCEL_INTERACT, id);
            mRootView.closeUserView(id, AVView.VIDEO_SRC_TYPE_CAMERA, true);
        } else {
            //TODO 主动下麦 下麦；
            SxbLog.d(TAG, LogConstants.ACTION_VIEWER_UNSHOW + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + "start unShow" +
                    LogConstants.DIV + "id " + id);
            mLiveHelper.downMemberVideo();
            changeCtrlView(false);
        }
    }


    private void showReportDialog() {
        final Dialog reportDialog = new Dialog(this, R.style.report_dlg);
        reportDialog.setContentView(R.layout.dialog_live_report);

        TextView tvReportDirty = (TextView) reportDialog.findViewById(R.id.btn_dirty);
        TextView tvReportFalse = (TextView) reportDialog.findViewById(R.id.btn_false);
        TextView tvReportVirus = (TextView) reportDialog.findViewById(R.id.btn_virus);
        TextView tvReportIllegal = (TextView) reportDialog.findViewById(R.id.btn_illegal);
        TextView tvReportYellow = (TextView) reportDialog.findViewById(R.id.btn_yellow);
        TextView tvReportCancel = (TextView) reportDialog.findViewById(R.id.btn_cancel);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    default:
                        reportDialog.cancel();
                        break;
                }
            }
        };

        tvReportDirty.setOnClickListener(listener);
        tvReportFalse.setOnClickListener(listener);
        tvReportVirus.setOnClickListener(listener);
        tvReportIllegal.setOnClickListener(listener);
        tvReportYellow.setOnClickListener(listener);
        tvReportCancel.setOnClickListener(listener);

        reportDialog.setCanceledOnTouchOutside(true);
        reportDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.host_message_input:
            case R.id.member_message_input:
                inputMsgDialog();
                break;
            case R.id.member_send_good:
                mHeartLayout.addFavor();
                if (checkInterval()) {
                    mLiveHelper.sendGroupCmd(Constants.AVIMCMD_PRAISE, "");
                    mLiveHelper.operateRoomGoodNum(String.valueOf(CurLiveInfo.getRoomNum()), "add");
                    CurLiveInfo.setAdmires(CurLiveInfo.getAdmires() + 1);
                    tvAdmires.setText("" + CurLiveInfo.getAdmires());
                }
                break;
            case R.id.host_switch_cam:
                ILiveRoomManager.getInstance().switchCamera(1 - ILiveRoomManager.getInstance().getCurCameraId());
                break;
            case R.id.host_mic_btn:
                if (mLiveHelper.isMicOn()) {
                    BtnMic.setBackgroundResource(R.drawable.icon_mic_close);
                } else {
                    BtnMic.setBackgroundResource(R.drawable.icon_mic_open);
                }
                mLiveHelper.toggleMic();
                break;
            case R.id.host_beauty_btn:
                Log.i(TAG, "onClick->beauty:" + mBeautyRate + ", whote:" + mWhiteRate);
                if (mBeautySettings != null) {
                    if (mBeautySettings.getVisibility() == View.GONE) {
                        mBeautySettings.setVisibility(View.VISIBLE);
                        mFullControllerUi.setVisibility(View.INVISIBLE);
                        mBeautyBar.setProgress(mBeautyRate);
                        mWhiteBar.setProgress(mWhiteRate);
                    } else {
                        mBeautySettings.setVisibility(View.GONE);
                        mFullControllerUi.setVisibility(View.VISIBLE);
                    }
                } else {
                    SxbLog.i(TAG, "beauty_btn mTopBar  is null ");
                }
                break;
            case R.id.host_menu_more:
                mHostCtrView.setVisibility(View.INVISIBLE);
                mNomalMemberCtrView.setVisibility(View.INVISIBLE);
                mCtrViewMore.setVisibility(View.VISIBLE);
                break;
            case R.id.host_fullscreen_btn:
            case R.id.member_fullscreen_btn:
                bCleanMode = true;
                mFullControllerUi.setVisibility(View.INVISIBLE);
                BtnNormal.setVisibility(View.VISIBLE);
                break;
            case R.id.normal_btn:
                bCleanMode = false;
                mFullControllerUi.setVisibility(View.VISIBLE);
                BtnNormal.setVisibility(View.GONE);
                break;
            case R.id.head_up_layout:
                // showHostDetail();
                break;
            case R.id.qav_beauty_setting_finish:
                mBeautySettings.setVisibility(View.GONE);
                mFullControllerUi.setVisibility(View.VISIBLE);
                break;
            case R.id.back_primary:
                mCtrViewMore.setVisibility(View.INVISIBLE);
                if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) { // 主播
                    mHostCtrView.setVisibility(View.VISIBLE);
                } else {
                    if (bVideoMember) { // 上麦观众

                    } else {    // 普通观众
                        mNomalMemberCtrView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.flash_btn:     // 闪光
                if (!mLiveHelper.toggleFlashLight()) {
                    Toast.makeText(LiveActivity.this, "toggle flash light failed!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_back:
                quiteLiveByPurpose();
                break;
            case R.id.push_btn:
                // pushStream();
                break;
            case R.id.change_voice:       // 变声
                if (voiceTypeDialog != null) voiceTypeDialog.show();
                break;
            case R.id.change_role:
                if (roleDialog != null) roleDialog.show();
                break;
            case R.id.tv_filter:        // 滤镜
                if (filterDialog != null) filterDialog.show();
                break;
            case R.id.record_btn:
                if (!mRecord) {
                    if (recordDialog != null)
                        recordDialog.show();
                } else {
                    mLiveHelper.stopRecord();
                }
                break;
            case R.id.host_share:
            case R.id.member_share:
                showShareDialog();
                break;
        }
    }

    private void showShareDialog() {
        View popView = LayoutInflater.from(this).inflate(R.layout.alert_share_live, null);
        SimpleDraweeView ivLivePicture = (SimpleDraweeView) popView.findViewById(R.id.iv_livePicture);
        TextView tvLiveTitle = (TextView) popView.findViewById(R.id.tv_liveTitle);
        if (CurLiveInfo.getCoverurl().startsWith("http")) {
            ivLivePicture.setImageURI(CurLiveInfo.getCoverurl());
        } else {
            ivLivePicture.setImageURI(HttpUtilService.BASE_RESOURCE_URL + CurLiveInfo.getCoverurl());
        }
        tvLiveTitle.setText(CurLiveInfo.getTitle());
        ShareHelp.getInstance().showShareDialog(this, popView);
    }

    private boolean checkInterval() {
        if (0 == admireTime) {
            admireTime = System.currentTimeMillis();
            return true;
        }
        long newTime = System.currentTimeMillis();
        if (newTime >= admireTime + 1000) {
            admireTime = newTime;
            return true;
        }
        return false;
    }

    private String getParams(String src, String title, String key) {
        int pos = src.indexOf(key);
        if (-1 != pos) {
            pos += key.length() + 2;
            int endPos = src.indexOf(",", pos);
            return title + ": " + src.substring(pos, endPos) + "\n";
        }

        return "";
    }

    @Override
    public void changeCtrlView(boolean videoMember) {
        if (MySelfInfo.getInstance().getIdStatus() == Constants.HOST) {
            // 主播不存在切换
            return;
        }
        bVideoMember = videoMember;
        mCtrViewMore.setVisibility(View.INVISIBLE);
        if (bVideoMember) {
            mNomalMemberCtrView.setVisibility(View.GONE);

            btnChageVoice.setVisibility(View.VISIBLE);
            btnChangeRole.setVisibility(View.VISIBLE);
            btnFlash.setVisibility(View.VISIBLE);
            btnFilter.setVisibility(View.VISIBLE);
        } else {
            mNomalMemberCtrView.setVisibility(View.VISIBLE);
            btnChageVoice.setVisibility(View.INVISIBLE);
            btnChangeRole.setVisibility(View.INVISIBLE);
            btnFlash.setVisibility(View.INVISIBLE);
            btnFilter.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 发消息弹出框
     */
    private void inputMsgDialog() {
        InputTextMsgDialog inputMsgDialog = new InputTextMsgDialog(this, R.style.inputdialog, this);
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = inputMsgDialog.getWindow().getAttributes();

        lp.width = (int) (display.getWidth()); //设置宽度
        inputMsgDialog.getWindow().setAttributes(lp);
        inputMsgDialog.setCancelable(true);
        inputMsgDialog.show();
    }

    /**
     * 消息刷新显示
     *
     * @param name    发送者
     * @param context 内容
     * @param type    类型 （上线线消息和 聊天消息）
     */
    public void refreshTextListView(String name, String context, int type) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName(name);
        entity.setContext(context);
        entity.setType(type);
        //mArrayListChatEntity.add(entity);
        notifyRefreshListView(entity);
        //mChatMsgListAdapter.notifyDataSetChanged();

        mListViewMsgItems.setVisibility(View.VISIBLE);
        SxbLog.d(TAG, "refreshTextListView height " + mListViewMsgItems.getHeight());

        if (mListViewMsgItems.getCount() > 1) {
            if (true)
                mListViewMsgItems.setSelection(0);
            else
                mListViewMsgItems.setSelection(mListViewMsgItems.getCount() - 1);
        }
    }


    /**
     * 通知刷新消息ListView
     */
    private void notifyRefreshListView(ChatEntity entity) {
        mBoolNeedRefresh = true;
        mTmpChatList.add(entity);
        if (mBoolRefreshLock) {
            return;
        } else {
            doRefreshListView();
        }
    }


    /**
     * 刷新ListView并重置状态
     */
    private void doRefreshListView() {
        if (mBoolNeedRefresh) {
            mBoolRefreshLock = true;
            mBoolNeedRefresh = false;
            mArrayListChatEntity.addAll(mTmpChatList);
            mTmpChatList.clear();
            mChatMsgListAdapter.notifyDataSetChanged();

            if (null != mTimerTask) {
                mTimerTask.cancel();
            }
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    SxbLog.v(TAG, "doRefreshListView->task enter with need:" + mBoolNeedRefresh);
                    mHandler.sendEmptyMessage(REFRESH_LISTVIEW);
                }
            };
            //mTimer.cancel();
            mTimer.schedule(mTimerTask, MINFRESHINTERVAL);
        } else {
            mBoolRefreshLock = false;
        }
    }

    @Override
    public void updateProfileInfo(TIMUserProfile profile) {

    }

    @Override
    public void updateUserInfo(int requestCode, List<TIMUserProfile> profiles) {
        if (null != profiles) {
            switch (requestCode) {
                case GETPROFILE_JOIN:
                    for (TIMUserProfile user : profiles) {
                        tvMembers.setText("" + CurLiveInfo.getMembers());
                        SxbLog.w(TAG, "get nick name:" + user.getNickName());
                        SxbLog.w(TAG, "get remark name:" + user.getRemark());
                        SxbLog.w(TAG, "get avatar:" + user.getFaceUrl());
                        if (!TextUtils.isEmpty(user.getNickName())) {
                            refreshTextListView(user.getNickName(), "加入房间", Constants.MEMBER_ENTER);
                        } else {
                            refreshTextListView(user.getIdentifier(), "加入房间", Constants.MEMBER_ENTER);
                        }
                    }
                    break;
            }

        }
    }

    //旁路直播
    private static boolean isPushed = false;

    /**
     * 旁路直播 退出房间时必须退出推流。否则会占用后台channel。
     */
    public void pushStream() {
        if (!isPushed) {
            bHLSPush = false;
            if (mPushDialog != null)
                mPushDialog.show();
        } else {
            mLiveHelper.stopPush();
        }
    }

    private Dialog mPushDialog;

    private void initPushDialog() {
        mPushDialog = new Dialog(this, R.style.dialog);
        mPushDialog.setContentView(R.layout.push_dialog_layout);
        final EditText pushfileNameInput = (EditText) mPushDialog.findViewById(R.id.push_filename);
        final RadioGroup radgroup = (RadioGroup) mPushDialog.findViewById(R.id.push_type);


        Button recordOk = (Button) mPushDialog.findViewById(R.id.btn_record_ok);
        recordOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILivePushOption option = new ILivePushOption();
                if (pushfileNameInput.getText().toString().equals("")) { // 推流名字为空
                    Toast.makeText(LiveActivity.this, "name can't be empty", Toast.LENGTH_SHORT);
                    return;
                } else {
                    option.channelName(pushfileNameInput.getText().toString());
                }

                if (radgroup.getCheckedRadioButtonId() == R.id.hls) {//默认格式
                    option.encode(ILivePushOption.Encode.HLS);
                    bHLSPush = true;
                } else {
                    option.encode(ILivePushOption.Encode.RTMP);
                }
                mLiveHelper.startPush(option);//开启推流
                mPushDialog.dismiss();
            }
        });


        Button recordCancel = (Button) mPushDialog.findViewById(R.id.btn_record_cancel);
        recordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPushDialog.dismiss();
            }
        });

        Window dialogWindow = mPushDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        mPushDialog.setCanceledOnTouchOutside(false);
    }

    private void showPushUrl(final String url) {
        ILiveLog.d("ILVBX", "showPushUrl->entered:" + url);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.str_push_title)
                .setMessage(url)
                .setPositiveButton(getString(R.string.str_push_copy), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager cmb = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text", url);
                        cmb.setPrimaryClip(clipData);
                        Toast.makeText(getApplicationContext(), "Copy Success", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.btn_cancel), null);
        if (bHLSPush) {
            builder.setNeutralButton(getString(R.string.str_push_share), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // showShareDlg(url);
                }
            });
        }
        builder.show();
    }

    /**
     * 推流成功
     */
    @Override
    public void pushStreamSucc(ILivePushRes streamRes) {
        List<ILivePushUrl> liveUrls = streamRes.getUrls();
        isPushed = true;
        pushBtn.setText(R.string.live_btn_stop_push);
        int length = liveUrls.size();
        String url = null;
        String url2 = null;
        if (length == 1) {
            ILivePushUrl avUrl = liveUrls.get(0);
            url = avUrl.getUrl();
        } else if (length == 2) {
            ILivePushUrl avUrl = liveUrls.get(0);
            url = avUrl.getUrl();
            ILivePushUrl avUrl2 = liveUrls.get(1);
            url2 = avUrl2.getUrl();
        }

        showPushUrl(url);
    }

    private Dialog recordDialog;
    private String filename = "";
    private boolean mRecord = false;
    private EditText filenameEditText;

    private void initRecordDialog() {
        recordDialog = new Dialog(this, R.style.dialog);
        recordDialog.setContentView(R.layout.record_layout);

        filenameEditText = (EditText) recordDialog.findViewById(R.id.record_filename);

        if (filename.length() > 0) {
            filenameEditText.setText(filename);
        }
        filenameEditText.setText("" + CurLiveInfo.getRoomNum());

        Button videoRecord = (Button) recordDialog.findViewById(R.id.btn_record_video);
        videoRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILiveRecordOption option = new ILiveRecordOption();
                filename = filenameEditText.getText().toString();
                option.fileName("sxb_" + ILiveLoginManager.getInstance().getMyUserId() + "_" + filename);

                option.recordType(TIMAvManager.RecordType.VIDEO);
                mLiveHelper.startRecord(option);
                mLiveHelper.notifyNewRecordInfo(filename);
                recordDialog.dismiss();
            }
        });
        Button audioRecord = (Button) recordDialog.findViewById(R.id.btn_record_audio);
        audioRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ILiveRecordOption option = new ILiveRecordOption();
                filename = filenameEditText.getText().toString();
                option.fileName("sxb_" + ILiveLoginManager.getInstance().getMyUserId() + "_" + filename);

                option.recordType(TIMAvManager.RecordType.AUDIO);
                mLiveHelper.startRecord(option);
                recordDialog.dismiss();
            }
        });
        Window dialogWindow = recordDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setAttributes(lp);
        recordDialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 停止推流成功
     */
    @Override
    public void stopStreamSucc() {
        isPushed = false;
        pushBtn.setText(R.string.live_btn_push);
    }

    /**
     * 准备录制视频
     */
    private void startRecordLive() {
        LL.i("打开直播录制..." + CurLiveInfo.getTitle());
        // 如果是主播则开启直播录制的功能
        ILiveRecordOption option = new ILiveRecordOption();
        option.fileName(CurLiveInfo.getTitle());
        option.classId(0);
        option.recordType(TIMAvManager.RecordType.VIDEO);
        mLiveHelper.startRecord(option);
        // mLiveHelper.notifyNewRecordInfo(CurLiveInfo.getTitle());
    }

    /**
     * 停止录制视频
     */
    private void stopRecordLive() {
        LL.i("结束直播录制...");
        mLiveHelper.stopRecord();
    }

    @Override
    public void startRecordCallback(boolean isSucc) {
        LL.i("开始直播录制成功..." + isSucc);
        mRecord = true;
        // recordBtn.setText(R.string.live_btn_stop_record);
    }

    @Override
    public void stopRecordCallback(boolean isSucc, List<String> files) {
        if (isSucc == true) {
            mRecord = false;
            LL.i("结束直播录制成功..." + isSucc);
            // recordBtn.setText(R.string.live_btn_record);
        }
    }

    void checkPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WAKE_LOCK, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                .subscribe();
    }

    // 清除老房间数据
    private void clearOldData() {
        mArrayListChatEntity.clear();
        mBoolNeedRefresh = true;
        if (mBoolRefreshLock) {
            return;
        } else {
            doRefreshListView();
        }
        mRootView.clearUserView();
    }

    private static String getValue(String src, String param, String sep) {
        int idx = src.indexOf(param);
        if (-1 != idx) {
            idx += param.length() + 1;
            if (-1 != sep.indexOf(src.charAt(idx))) {
                idx++;
            }
            for (int i = idx; i < src.length(); i++) {
                if (-1 != sep.indexOf(src.charAt(i))) {
                    return src.substring(idx, i).trim();
                }
            }
        }
        return "";
    }

    @Override
    public void permisstionsResult(String errorCode, String errorMsg, TeacherInfo data) {
        // 检查用户是否有发弹幕的权限
        if ("1".equals(errorCode)) {
            if ("1".equals(data.getSxbDanmuPermissions())) {
                tvMsgInput.setVisibility(View.VISIBLE);
                msgInputInterval.setVisibility(View.VISIBLE);
            } else {
                tvMsgInput.setVisibility(View.GONE);
                msgInputInterval.setVisibility(View.GONE);
            }
        } else {
            T.showShort(this, errorMsg);
        }
    }

    @Override
    public void addLiveRoomGoodNumberResult(String[] result) {
        if (!StringUtils.isEmpty(result[0]) && "1".equals(result)) {
            // 添加点赞的时候啥都不算
        }
    }

    @Override
    public void searchLiveRoomGoodNumberResult(String[] result) {
        if (!StringUtils.isEmpty(result[0]) && "1".equals(result[0])) {
            // 进来的时候手动查询当前直播的点赞数量
            CurLiveInfo.setAdmires(Integer.parseInt(result[2]));
            tvAdmires.setText("" + CurLiveInfo.getAdmires());
        } else {
            tvAdmires.setText("" + CurLiveInfo.getAdmires());
        }
    }

    /***************************************************************************************/

    //for 测试获取测试参数
    private boolean showTips = false;
    private TextView tvTipsMsg;
    Timer paramTimer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            runOnUiThread(new Runnable() {
                public void run() {
                    if (showTips) {
                        mQualityCircle.setVisibility(View.VISIBLE);
                        mQualityText.setVisibility(View.VISIBLE);
                        if (tvTipsMsg != null && ILiveSDK.getInstance().getAVContext() != null &&
                                ILiveSDK.getInstance().getAVContext().getRoom() != null) {
                            //String tips =getQualityTips();
                            String tips = "";
                            ILiveQualityData qData = ILiveRoomManager.getInstance().getQualityData();
                            if (null != qData) {
                                tips += "FPS:\t" + qData.getUpFPS() + "\n";
                                tips += "Send:\t" + qData.getSendKbps() + "Kbps\t";
                                tips += "Recv:\t" + qData.getRecvKbps() + "Kbps\n";
                                tips += "SendLossRate:\t" + qData.getSendLossRate() + "%\t";
                                tips += "RecvLossRate:\t" + qData.getRecvLossRate() + "%\n";
                                tips += "AppCPURate:\t" + qData.getAppCPURate() + "%\t";
                                tips += "SysCPURate:\t" + qData.getSysCPURate() + "%\n";
                                Map<String, LiveInfo> userMaps = qData.getLives();
                                for (Map.Entry<String, LiveInfo> entry : userMaps.entrySet()) {
                                    tips += "\t" + entry.getKey() + "-" + entry.getValue().getWidth() + "*" + entry.getValue().getHeight() + "\n";
                                }
                            }

                            //tips = expandTips(tips);
                            tips += '\n';
                            tips += getQualityTips(ILiveSDK.getInstance().getAVContext().getRoom().getQualityTips());
                            tvTipsMsg.getBackground().setAlpha(125);
                            tvTipsMsg.setText(tips);
                            tvTipsMsg.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvTipsMsg.setText("");
                        tvTipsMsg.setVisibility(View.INVISIBLE);
                        mQualityCircle.setVisibility(View.GONE);
                        mQualityText.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    public String getQualityTips(String qualityTips) {
        String strTips = "";
        String sep = "[](),\n";

        strTips += "AVSDK版本号: " + getValue(qualityTips, "sdk_version", sep) + "\n";
        strTips += "房间号: " + getValue(qualityTips, "RoomID", sep) + "\n";
        strTips += "角色: " + getValue(qualityTips, "ControlRole", sep) + "\n";
        strTips += "权限: " + getValue(qualityTips, "Authority", sep) + "\n";
        String tmpStr = getValue(qualityTips, "视频采集", "\n");
        if (!TextUtils.isEmpty(tmpStr))
            strTips += "采集信息: " + getValue(qualityTips, "视频采集", "\n") + "\n";
        strTips += "麦克风: " + getValue(qualityTips, "Mic", sep) + "\n";
        strTips += "扬声器: " + getValue(qualityTips, "Spk", sep) + "\n";

        return strTips;
    }
}
