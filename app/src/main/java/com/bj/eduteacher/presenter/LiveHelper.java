package com.bj.eduteacher.presenter;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bj.eduteacher.R;
import com.bj.eduteacher.api.HttpUtilService;
import com.bj.eduteacher.api.LmsDataService;
import com.bj.eduteacher.api.MLProperties;
import com.bj.eduteacher.entity.TeacherInfo;
import com.bj.eduteacher.model.CurLiveInfo;
import com.bj.eduteacher.model.MemberID;
import com.bj.eduteacher.model.MySelfInfo;
import com.bj.eduteacher.presenter.viewinface.LiveView;
import com.bj.eduteacher.tool.Constants;
import com.bj.eduteacher.tool.LogConstants;
import com.bj.eduteacher.tool.SxbLog;
import com.bj.eduteacher.utils.LL;
import com.bj.eduteacher.utils.PreferencesUtils;
import com.bj.eduteacher.utils.StringUtils;
import com.bj.eduteacher.zzokhttp.utils.Exceptions;
import com.tencent.TIMConversationType;
import com.tencent.TIMCustomElem;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMMessage;
import com.tencent.av.sdk.AVRoomMulti;
import com.tencent.av.sdk.AVVideoCtrl;
import com.tencent.av.sdk.AVView;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.ILiveSDK;
import com.tencent.ilivesdk.core.ILiveLog;
import com.tencent.ilivesdk.core.ILivePushOption;
import com.tencent.ilivesdk.core.ILiveRecordOption;
import com.tencent.ilivesdk.core.ILiveRoomManager;
import com.tencent.ilivesdk.core.ILiveRoomOption;
import com.tencent.ilivesdk.data.ILivePushRes;
import com.tencent.ilivesdk.data.ILivePushUrl;
import com.tencent.livesdk.ILVChangeRoleRes;
import com.tencent.livesdk.ILVCustomCmd;
import com.tencent.livesdk.ILVLiveConstants;
import com.tencent.livesdk.ILVLiveManager;
import com.tencent.livesdk.ILVLiveRoomOption;
import com.tencent.livesdk.ILVText;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bj.eduteacher.model.CurLiveInfo.hostID;


/**
 * 直播控制类
 */
public class LiveHelper extends Presenter implements ILiveRoomOption.onRoomDisconnectListener, Observer {
    private final String TAG = "LiveHelper";
    private LiveView mLiveView;
    public Context mContext;
    private boolean bCameraOn = false;
    private boolean bMicOn = false;
    private boolean flashLgihtStatus = false;
    private long streamChannelID;
    private ApplyCreateRoom createRoomProcess;
    private LmsDataService service = new LmsDataService();
    //获取
    private GetMemberListTask mGetMemListTask;
    private final String teacherPhoneNumber;

    class GetMemberListTask extends AsyncTask<String, Integer, ArrayList<MemberID>> {

        @Override
        protected ArrayList<MemberID> doInBackground(String... strings) {
            //1上报成员
            UserServerHelper.getInstance().reportMe(MySelfInfo.getInstance().getIdStatus(), 0);
            //2 拉取成员列表
            return UserServerHelper.getInstance().getMemberList();
        }

        @Override
        protected void onPostExecute(ArrayList<MemberID> result) {
            if (mLiveView != null)
                mLiveView.refreshMember(result);
        }
    }


    class ApplyCreateRoom extends AsyncTask<String, Integer, UserServerHelper.RequestBackInfo> {

        @Override
        protected UserServerHelper.RequestBackInfo doInBackground(String... strings) {

            return UserServerHelper.getInstance().applyCreateRoom(); //获取后台
        }

        @Override
        protected void onPostExecute(UserServerHelper.RequestBackInfo result) {
            if (result != null && result.getErrorCode() == 0) {
                Log.i("way", MySelfInfo.getInstance().toString());
                MySelfInfo.getInstance().writeToCache(mContext);
                createRoom();
            } else {
                Log.i(TAG, "ApplyCreateRoom onPostExecute: " + (null != result ? result.getErrorInfo() : "empty"));
            }
        }
    }

    /**
     * 申请房间
     */
    private void startCreateRoom() {
        createRoomProcess = new ApplyCreateRoom(); //申请房间
        createRoomProcess.execute();

    }


    /**
     * 拉取成员
     */
    public void pullMemberList() {
        mGetMemListTask = new GetMemberListTask(); //拉取成员
        mGetMemListTask.execute();
    }

    /**
     * 上报房间
     */
    private void NotifyServerLiveTask() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserServerHelper.getInstance().notifyCloseLive();
            }
        }).start();

    }


    public LiveHelper(Context context, LiveView liveview) {
        mContext = context;
        mLiveView = liveview;
        teacherPhoneNumber = PreferencesUtils.getString(mContext, MLProperties.PREFER_KEY_USER_ID, "");
        MessageEvent.getInstance().addObserver(this);
    }

    @Override
    public void onDestory() {
        mLiveView = null;
        mContext = null;
        MessageEvent.getInstance().deleteObserver(this);
        ILVLiveManager.getInstance().quitRoom(null);
    }

    /**
     * 检查弹幕的权限
     */
    public void checkPermission(final String phone) {
        io.reactivex.Observable.create(new ObservableOnSubscribe<TeacherInfo>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<TeacherInfo> e) throws Exception {
                TeacherInfo info = service.getTeacherLiveDanmuPermissionsFromAPI(phone);
                if (!e.isDisposed()) {
                    e.onNext(info);
                    e.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<TeacherInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TeacherInfo teacherInfo) {
                        mLiveView.permisstionsResult("1", "", teacherInfo);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        mLiveView.permisstionsResult("0", "服务器开小差了，请稍后重试", null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 点赞或者查询点赞数量
     *
     * @param hostID
     * @param operate
     */
    public void operateRoomGoodNum(final String roomID, final String operate) {
        io.reactivex.Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                String[] result = service.operateLiveRoomGoodNumber(roomID, operate);
                if (!e.isDisposed()) {
                    e.onNext(result);
                    e.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<String[]>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String[] result) {
                        if ("add".equals(operate)) {
                            mLiveView.addLiveRoomGoodNumberResult(result);
                        } else {
                            mLiveView.searchLiveRoomGoodNumberResult(result);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 进入房间
     */
    public void startEnterRoom() {
        if (MySelfInfo.getInstance().isCreateRoom() == true) {
            startCreateRoom();
        } else {
            joinRoom();
        }
    }

    public void comeBackRoom() {
        createRoom();
    }

    public void switchRoom() {
        ILVLiveRoomOption memberOption = new ILVLiveRoomOption(CurLiveInfo.getHostID())
                .autoCamera(false)
                .autoFocus(true)
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(Constants.NORMAL_MEMBER_ROLE)
                .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)
                .autoMic(false);
        ILVLiveManager.getInstance().switchRoom(CurLiveInfo.getRoomNum(), memberOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveLog.d(TAG, "ILVB-Suixinbo|switchRoom->join room sucess");
                if (null != mLiveView) {
                    mLiveView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ILiveLog.d(TAG, "ILVB-Suixinbo|switchRoom->join room failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView) {
                    mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }
        });
        SxbLog.i(TAG, "switchRoom startEnterRoom ");
    }

    private void showToast(String strMsg) {
        if (null != mContext) {
            Toast.makeText(mContext, strMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void showUserToast(String account, int resId) {
        if (null != mContext) {
            Toast.makeText(mContext, account + mContext.getString(resId), Toast.LENGTH_SHORT).show();
        }
    }

    private void quitLiveRoom() {
        ILVLiveManager.getInstance().quitRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveLog.d(TAG, "ILVB-SXB|quitRoom->success");
                CurLiveInfo.setCurrentRequestCount(0);
                //通知结束
                NotifyServerLiveTask();
                if (null != mLiveView) {
                    mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ILiveLog.d(TAG, "ILVB-SXB|quitRoom->failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView) {
                    mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }
        });
    }

    public void startExitRoom() {
        ILiveSDK.getInstance().getAvVideoCtrl().setLocalVideoPreProcessCallback(null);
        quitLiveRoom();
    }

    /**
     * 发送信令
     */

    public int sendGroupCmd(int cmd, String param) {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eGroupMsg);
        return sendCmd(customCmd);
    }

    public int sendC2CCmd(final int cmd, String param, String destId) {
        ILVCustomCmd customCmd = new ILVCustomCmd();
        customCmd.setDestId(destId);
        customCmd.setCmd(cmd);
        customCmd.setParam(param);
        customCmd.setType(ILVText.ILVTextType.eC2CMsg);
        return sendCmd(customCmd);
    }

    /**
     * 打开闪光灯
     */
    public boolean toggleFlashLight() {
        AVVideoCtrl videoCtrl = ILiveSDK.getInstance().getAvVideoCtrl();
        if (null == videoCtrl) {
            return false;
        }

        final Object cam = videoCtrl.getCamera();
        if ((cam == null) || (!(cam instanceof Camera))) {
            return false;
        }
        final Camera.Parameters camParam = ((Camera) cam).getParameters();
        if (null == camParam) {
            return false;
        }

        Object camHandler = videoCtrl.getCameraHandler();
        if ((camHandler == null) || (!(camHandler instanceof Handler))) {
            return false;
        }

        //对摄像头的操作放在摄像头线程
        if (flashLgihtStatus == false) {
            ((Handler) camHandler).post(new Runnable() {
                public void run() {
                    try {
                        camParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        ((Camera) cam).setParameters(camParam);
                        flashLgihtStatus = true;
                    } catch (RuntimeException e) {
                        SxbLog.d("setParameters", "RuntimeException");
                    }
                }
            });
        } else {
            ((Handler) camHandler).post(new Runnable() {
                public void run() {
                    try {
                        camParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        ((Camera) cam).setParameters(camParam);
                        flashLgihtStatus = false;
                    } catch (RuntimeException e) {
                        SxbLog.d("setParameters", "RuntimeException");
                    }

                }
            });
        }
        return true;
    }

    public void startRecord(ILiveRecordOption option) {
        ILiveRoomManager.getInstance().startRecordVideo(option, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.i(TAG, "start record success ");
                if (null != mLiveView)
                    mLiveView.startRecordCallback(true);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "start record error " + errCode + "  " + errMsg);
                if (null != mLiveView)
                    mLiveView.startRecordCallback(false);
            }
        });
    }

    public void stopRecord() {
        ILiveRoomManager.getInstance().stopRecordVideo(new ILiveCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> data) {
                SxbLog.d(TAG, "stopRecord->success");
                for (String url : data) {
                    SxbLog.d(TAG, "stopRecord->url:" + url);
                }
                if (null != mLiveView)
                    mLiveView.stopRecordCallback(true, data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "stopRecord->failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView)
                    mLiveView.stopRecordCallback(false, null);
            }
        });
    }

    public void startPush(ILivePushOption option) {
        ILiveRoomManager.getInstance().startPushStream(option, new ILiveCallBack<ILivePushRes>() {
            @Override
            public void onSuccess(ILivePushRes data) {
                List<ILivePushUrl> liveUrls = data.getUrls();
                streamChannelID = data.getChnlId();
                if (null != mLiveView)
                    mLiveView.pushStreamSucc(data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "url error " + errCode + " : " + errMsg);
                showToast("start stream error,try again " + errCode + " : " + errMsg);
            }
        });
    }

    public void stopPush() {
        ILiveRoomManager.getInstance().stopPushStream(streamChannelID, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.e(TAG, "stopPush->success");
                if (null != mLiveView)
                    mLiveView.stopStreamSucc();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "stopPush->failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }


    @Override
    public void onRoomDisconnect(int errCode, String errMsg) {
        if (null != mLiveView) {
            mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
        }
    }

    // 解析文本消息
    private void processTextMsg(MessageEvent.SxbMsgInfo info) {
        if (null == info.data || !(info.data instanceof ILVText)) {
            SxbLog.w(TAG, "processTextMsg->wrong object:" + info.data);
            return;
        }
        ILVText text = (ILVText) info.data;
        if (text.getType() == ILVText.ILVTextType.eGroupMsg
                && !CurLiveInfo.getChatRoomId().equals(text.getDestId())) {
            SxbLog.d(TAG, "processTextMsg->ingore message from: " + text.getDestId() + "/" + CurLiveInfo.getChatRoomId());
            return;
        }

        String name = info.senderId;
        if (null != info.profile && !TextUtils.isEmpty(info.profile.getNickName())) {
            name = info.profile.getNickName();
        }

        if (null != mLiveView)
            mLiveView.refreshText(text.getText(), name);
    }

    // 解析自定义信令
    private void processCmdMsg(MessageEvent.SxbMsgInfo info) {
        if (null == info.data || !(info.data instanceof ILVCustomCmd)) {
            SxbLog.w(TAG, "processCmdMsg->wrong object:" + info.data);
            return;
        }
        ILVCustomCmd cmd = (ILVCustomCmd) info.data;
        if (cmd.getType() == ILVText.ILVTextType.eGroupMsg
                && !CurLiveInfo.getChatRoomId().equals(cmd.getDestId())) {
            SxbLog.d(TAG, "processCmdMsg->ingore message from: " + cmd.getDestId() + "/" + CurLiveInfo.getChatRoomId());
            return;
        }

        String name = info.senderId;
        if (null != info.profile && !TextUtils.isEmpty(info.profile.getNickName())) {
            name = info.profile.getNickName();
        }

        handleCustomMsg(cmd.getCmd(), cmd.getParam(), info.senderId, name);
    }

    private void processOtherMsg(MessageEvent.SxbMsgInfo info) {
        if (null == info.data || !(info.data instanceof TIMMessage)) {
            SxbLog.w(TAG, "processOtherMsg->wrong object:" + info.data);
            return;
        }
        TIMMessage currMsg = (TIMMessage) info.data;

        // 过滤非当前群组消息
        if (currMsg.getConversation() != null && currMsg.getConversation().getPeer() != null) {
            if (currMsg.getConversation().getType() == TIMConversationType.Group
                    && !CurLiveInfo.getChatRoomId().equals(currMsg.getConversation().getPeer())) {
                return;
            }
        }

        for (int j = 0; j < currMsg.getElementCount(); j++) {
            if (currMsg.getElement(j) == null)
                continue;
            TIMElem elem = currMsg.getElement(j);
            TIMElemType type = elem.getType();

            SxbLog.d(TAG, "LiveHelper->otherMsg type:" + type);

            //系统消息
            if (type == TIMElemType.GroupSystem) {  // 群组解散消息
                if (TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE == ((TIMGroupSystemElem) elem).getSubtype()) {
                    if (null != mLiveView)
                        mLiveView.hostLeave("host", null);
                }
            } else if (type == TIMElemType.Custom) {
                try {
                    final String strMagic = "__ACTION__";
                    String customText = new String(((TIMCustomElem) elem).getData(), "UTF-8");
                    if (!customText.startsWith(strMagic))   // 检测前缀
                        continue;
                    JSONTokener jsonParser = new JSONTokener(customText.substring(strMagic.length() + 1));
                    JSONObject json = (JSONObject) jsonParser.nextValue();
                    String action = json.optString("action", "");
                    if (action.equals("force_exit_room") || action.equals("force_disband_room")) {
                        JSONObject objData = json.getJSONObject("data");
                        String strRoomNum = objData.optString("room_num", "");
                        SxbLog.d(TAG, "processOtherMsg->action:" + action + ", room_num:" + strRoomNum);
                        if (strRoomNum.equals(String.valueOf(ILiveRoomManager.getInstance().getRoomId()))) {
                            if (null != mLiveView) {
                                mLiveView.forceQuitRoom(mContext.getString(R.string.str_tips_force_exit));
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void update(Observable observable, Object o) {
        MessageEvent.SxbMsgInfo info = (MessageEvent.SxbMsgInfo) o;
        switch (info.msgType) {
            case MessageEvent.MSGTYPE_TEXT:
                processTextMsg(info);
                break;
            case MessageEvent.MSGTYPE_CMD:
                processCmdMsg(info);
                break;
            case MessageEvent.MSGTYPE_OTHER:
                processOtherMsg(info);
                break;
        }
    }

    public void notifyNewRoomInfoFromAPI() {
        io.reactivex.Observable.create(new ObservableOnSubscribe<String[]>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String[]> e) throws Exception {
                String[] result = service.notifyNewRoomInfoFromAPI(teacherPhoneNumber, MySelfInfo.getInstance().getMyRoomNum(),
                        CurLiveInfo.getTitle(), CurLiveInfo.getCoverurl());
                e.onNext(result);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.Observer<String[]>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String[] result) {
                        if (!StringUtils.isEmpty(result[0]) && "1".equals(result[0])) {
                            PreferencesUtils.putString(mContext, MLProperties.PREFER_KEY_USER_SXB_Title, CurLiveInfo.getTitle());
                            PreferencesUtils.putString(mContext, MLProperties.PREFER_KEY_USER_SXB_Picture, HttpUtilService.BASE_RESOURCE_URL + CurLiveInfo.getCoverurl());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 上报房间信息
     */
    public void notifyNewRoomInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject liveInfo = null;
                try {
                    liveInfo = new JSONObject();
                    liveInfo.put("token", MySelfInfo.getInstance().getToken());

                    JSONObject room = new JSONObject();
                    room.put("title", CurLiveInfo.getTitle());
                    room.put("roomnum", MySelfInfo.getInstance().getMyRoomNum());
                    room.put("type", "live");
                    room.put("groupid", "" + CurLiveInfo.getRoomNum());
                    room.put("cover", CurLiveInfo.getCoverurl());
                    room.put("appid", Constants.SDK_APPID);
                    room.put("device", 1);
                    room.put("videotype", 0);
                    liveInfo.put("room", room);

                    JSONObject lbs = new JSONObject();
                    lbs.put("longitude", CurLiveInfo.getLong1());
                    lbs.put("latitude", CurLiveInfo.getLat1());
                    lbs.put("address", CurLiveInfo.getAddress());
                    liveInfo.put("lbs", lbs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (liveInfo != null) {
                    UserServerHelper.getInstance().reporNewtRoomInfo(liveInfo.toString());
                }

            }
        }).start();
    }

    /**
     * 上报录制信息
     */
    public void notifyNewRecordInfo(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject recordInfo = null;
                try {
                    recordInfo = new JSONObject();
                    recordInfo.put("token", MySelfInfo.getInstance().getToken());
                    recordInfo.put("roomnum", CurLiveInfo.getRoomNum());
                    recordInfo.put("uid", MySelfInfo.getInstance().getId());
                    recordInfo.put("name", name);
                    recordInfo.put("type", 0);
                    recordInfo.put("cover", CurLiveInfo.getCoverurl());
                    Thread.sleep(2000);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (recordInfo != null) {
                    UserServerHelper.getInstance().reporNewtRecordInfo(recordInfo.toString());
                }

            }
        }).start();
    }

    public void toggleCamera() {
        bCameraOn = !bCameraOn;
        SxbLog.d(TAG, "toggleCamera->change camera:" + bCameraOn);
        ILiveRoomManager.getInstance().enableCamera(ILiveRoomManager.getInstance().getCurCameraId(), bCameraOn);
    }

    public void enableCamera() {
        bCameraOn = true;
        ILiveRoomManager.getInstance().enableCamera(ILiveRoomManager.getInstance().getCurCameraId(), bCameraOn);
    }

    public void disableCamera() {
        bCameraOn = false;
        ILiveRoomManager.getInstance().enableCamera(ILiveRoomManager.getInstance().getCurCameraId(), bCameraOn);
    }

    public void toggleMic() {
        bMicOn = !bMicOn;
        SxbLog.d(TAG, "toggleMic->change mic:" + bMicOn);
        ILiveRoomManager.getInstance().enableMic(bMicOn);
    }

    public void enableMic() {
        bMicOn = true;
        ILiveRoomManager.getInstance().enableMic(bMicOn);
    }

    public void disableMic() {
        bMicOn = false;
        ILiveRoomManager.getInstance().enableMic(bMicOn);
    }

    public boolean isMicOn() {
        return bMicOn;
    }

    public void upMemberVideo() {
        if (!ILiveRoomManager.getInstance().isEnterRoom()) {
            SxbLog.e(TAG, "upMemberVideo->with not in room");
        }
        ILVLiveManager.getInstance().upToVideoMember(Constants.VIDEO_MEMBER_ROLE, true, true, new ILiveCallBack<ILVChangeRoleRes>() {
            @Override
            public void onSuccess(ILVChangeRoleRes data) {
                SxbLog.d(TAG, "upToVideoMember->success");
                MySelfInfo.getInstance().setIdStatus(Constants.VIDEO_MEMBER);
                bMicOn = true;
                bCameraOn = true;
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "upToVideoMember->failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }

    public void downMemberVideo() {
        if (!ILiveRoomManager.getInstance().isEnterRoom()) {
            SxbLog.e(TAG, "downMemberVideo->with not in room");
        }
        ILVLiveManager.getInstance().downToNorMember(Constants.NORMAL_MEMBER_ROLE, new ILiveCallBack<ILVChangeRoleRes>() {
            @Override
            public void onSuccess(ILVChangeRoleRes data) {
                MySelfInfo.getInstance().setIdStatus(Constants.MEMBER);
                bMicOn = false;
                bCameraOn = false;
                SxbLog.e(TAG, "downMemberVideo->onSuccess");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SxbLog.e(TAG, "downMemberVideo->failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }

    private void checkEnterReturn(int iRet) {
        if (ILiveConstants.NO_ERR != iRet) {
            ILiveLog.d(TAG, "ILVB-Suixinbo|checkEnterReturn->enter room failed:" + iRet);
            if (ILiveConstants.ERR_ALREADY_IN_ROOM == iRet) {     // 上次房间未退出处理做退出处理
                ILiveRoomManager.getInstance().quitRoom(new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (null != mLiveView) {
                            mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                        }
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        if (null != mLiveView) {
                            mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                        }
                    }
                });
            } else {
                if (null != mLiveView) {
                    mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }
        }
    }


    private void createRoom() {
        ILVLiveRoomOption hostOption = new ILVLiveRoomOption(MySelfInfo.getInstance().getId())
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(CurLiveInfo.getCurRole())
                .autoFocus(true)    // 开启自动对焦
                .authBits(AVRoomMulti.AUTH_BITS_DEFAULT)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO);

        int ret = ILVLiveManager.getInstance().createRoom(MySelfInfo.getInstance().getMyRoomNum(), hostOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveLog.d(TAG, "ILVB-SXB|startEnterRoom->create room sucess" + "|roomid:" + MySelfInfo.getInstance().getMyRoomNum());
                bCameraOn = true;
                bMicOn = true;
                if (null != mLiveView)
                    mLiveView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
                notifyNewRoomInfoFromAPI();
                notifyNewRoomInfo();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ILiveLog.d(TAG, "ILVB-SXB|createRoom->create room failed:" + module + "|" + errCode + "|" + errMsg + "|roomid:" + MySelfInfo.getInstance().getMyRoomNum());
                // showToast("sendCmd->failed:" + module + "|" + errCode + "|" + errMsg);
                if (null != mLiveView) {
                    mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                }
            }
        });
        checkEnterReturn(ret);
    }

    private void joinRoom() {
        LL.i("进入房间信息：hostid:" + CurLiveInfo.getHostID());
        LL.i("进入房间信息：guestRole:" + MySelfInfo.getInstance().getGuestRole());
        LL.i("进入房间信息：roomNum:" + CurLiveInfo.getRoomNum());

        ILVLiveRoomOption memberOption = new ILVLiveRoomOption(CurLiveInfo.getHostID())
                .autoCamera(false)
                .roomDisconnectListener(this)
                .videoMode(ILiveConstants.VIDEOMODE_BSUPPORT)
                .controlRole(MySelfInfo.getInstance().getGuestRole())
                .authBits(AVRoomMulti.AUTH_BITS_JOIN_ROOM | AVRoomMulti.AUTH_BITS_RECV_AUDIO | AVRoomMulti.AUTH_BITS_RECV_CAMERA_VIDEO | AVRoomMulti.AUTH_BITS_RECV_SCREEN_VIDEO)
                .videoRecvMode(AVRoomMulti.VIDEO_RECV_MODE_SEMI_AUTO_RECV_CAMERA_VIDEO)
                .autoMic(false);

        int ret = ILVLiveManager.getInstance().joinRoom(CurLiveInfo.getRoomNum(), memberOption, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                ILiveLog.d(TAG, "ILVB-Suixinbo|startEnterRoom->join room sucess");
                if (null != mLiveView)
                    mLiveView.enterRoomComplete(MySelfInfo.getInstance().getIdStatus(), true);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ILiveLog.d(TAG, "ILVB-Suixinbo|startEnterRoom->join room failed:" + module + "|" + errCode + "|" + errMsg);
                ILiveLog.d(TAG, "ILVB-SXB|createRoom->create room failed:" + module + "|" + errCode + "|" + errMsg);
                if (errCode == 10010) {
                    //this group does not exist
                    Toast.makeText(mContext, "该主播暂未开播！", Toast.LENGTH_SHORT).show();
                } else if (errCode == 8002) {
                    // 内存被回收造成的
                    // Toast.makeText(mContext, "您离开太久了", Toast.LENGTH_SHORT).show();
                    if (null != mLiveView) {
                        mLiveView.exitErrorRoom();
                    }
                } else if (errCode == 6017) {
                    // 内存被回收造成的
                    if (null != mLiveView) {
                        mLiveView.exitErrorRoom();
                    }
                } else {
                    Toast.makeText(mContext, "进入房间失败，请重试！", Toast.LENGTH_SHORT).show();
                    if (null != mLiveView) {
                        mLiveView.quiteRoomComplete(MySelfInfo.getInstance().getIdStatus(), true, null);
                    }
                }
            }
        });
        checkEnterReturn(ret);
        SxbLog.i(TAG, "joinLiveRoom startEnterRoom ");
    }

    private int sendCmd(final ILVCustomCmd cmd) {
        return ILVLiveManager.getInstance().sendCustomCmd(cmd, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                SxbLog.i(TAG, "sendCmd->success:" + cmd.getCmd() + "|" + cmd.getParam());
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
//                Toast.makeText(mContext, "sendCmd->failed:" + module + "|" + errCode + "|" + errMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCustomMsg(int action, String param, String identifier, String nickname) {
        SxbLog.d(TAG, "handleCustomMsg->action: " + action);
        if (null == mLiveView) {
            return;
        }
        switch (action) {
            case Constants.AVIMCMD_MUlTI_HOST_INVITE:
                SxbLog.d(TAG, LogConstants.ACTION_VIEWER_SHOW + LogConstants.DIV + MySelfInfo.getInstance().getId() + LogConstants.DIV + "receive invite message" +
                        LogConstants.DIV + "id " + identifier);
                mLiveView.showInviteDialog();
                break;
            case Constants.AVIMCMD_MUlTI_JOIN:
                SxbLog.i(TAG, "handleCustomMsg " + identifier);
                mLiveView.cancelInviteView(identifier);
                break;
            case Constants.AVIMCMD_MUlTI_REFUSE:
                mLiveView.cancelInviteView(identifier);
                showToast(identifier + " refuse !");
                break;
            case Constants.AVIMCMD_PRAISE:
                mLiveView.refreshThumbUp();
                break;
            case Constants.AVIMCMD_ENTERLIVE:
                mLiveView.memberJoin(identifier, nickname);
                break;

            case Constants.AVIMCMD_MULTI_CANCEL_INTERACT://主播关闭摄像头命令
                //如果是自己关闭Camera和Mic
                if (param.equals(MySelfInfo.getInstance().getId())) {//是自己
                    //TODO 被动下麦 下麦 下麦
                    downMemberVideo();
                }
                //其他人关闭小窗口
                ILiveRoomManager.getInstance().getRoomView().closeUserView(param, AVView.VIDEO_SRC_TYPE_CAMERA, true);
                mLiveView.hideInviteDialog();
                mLiveView.changeCtrlView(false);
                break;
            case Constants.AVIMCMD_MULTI_HOST_CANCELINVITE:
                mLiveView.hideInviteDialog();
                break;
            case Constants.AVIMCMD_EXITLIVE:
                //startExitRoom();
                mLiveView.forceQuitRoom(mContext.getString(R.string.str_room_discuss));
                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_REQ:     // 跨房邀请
                mLiveView.linkRoomReq(identifier, nickname);
                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_ACCEPT:  // 接听
                mLiveView.linkRoomAccept(identifier, param);
                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_REFUSE:  // 拒绝
                showUserToast(identifier, R.string.str_link_refuse_tips);
                break;
            case ILVLiveConstants.ILVLIVE_CMD_LINKROOM_LIMIT:   // 达到上限
                showUserToast(identifier, R.string.str_link_limit);
                break;
            case Constants.AVIMCMD_HOST_BACK:
                mLiveView.hostBack(identifier, nickname);

            default:
                break;
        }
    }

    public void changeRole(final String role) {
        ILiveRoomManager.getInstance().changeRole(role, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                showToast("change " + role + " succ !!");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                showToast("change " + role + "   failed  : " + errCode + " msg " + errMsg);
            }
        });
    }

    public void sendLinkReq(final String dstId) {
        ILVLiveManager.getInstance().linkRoomRequest(dstId, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                showToast("sendLinkReq " + dstId + " succ !!");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                showToast("sendLinkReq " + dstId + " failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }

    public void unlinkRoom() {
        ILVLiveManager.getInstance().unlinkRoom(new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                showToast("unlinkRoom succ !!");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                showToast("unlinkRoom failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }

    public void acceptLink(String id) {
        ILVLiveManager.getInstance().acceptLinkRoom(id, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                showToast("acceptLinkRoom succ !!");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                showToast("unlinkRoom failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }

    public void refuseLink(String id) {
        ILVLiveManager.getInstance().refuseLinkRoom(id, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                showToast("refuseLinkRoom succ !!");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                showToast("refuseLinkRoom failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }

    public void linkRoom(String id, String room, String sign) {
        ILVLiveManager.getInstance().linkRoom(Integer.valueOf(room), id, sign, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                showToast("linkRoom success!!");
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                showToast("linkRoom failed:" + module + "|" + errCode + "|" + errMsg);
            }
        });
    }
}
