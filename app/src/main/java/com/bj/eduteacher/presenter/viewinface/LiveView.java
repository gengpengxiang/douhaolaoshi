package com.bj.eduteacher.presenter.viewinface;


import com.bj.eduteacher.entity.TeacherInfo;
import com.bj.eduteacher.model.LiveInfoJson;
import com.bj.eduteacher.model.MemberID;
import com.tencent.ilivesdk.data.ILivePushRes;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播界面回调
 */
public interface LiveView extends MvpView {

    void enterRoomComplete(int id_status, boolean succ);

    void quiteRoomComplete(int id_status, boolean succ, LiveInfoJson liveinfo);

    void showInviteDialog();

    void refreshText(String text, String name);

    void refreshThumbUp();

    void changeCtrlView(boolean bVideoMember);

    boolean showInviteView(String id);

    void cancelInviteView(String id);

    void cancelMemberView(String id);

    void memberJoin(String id, String name);

    void hideInviteDialog();

    void pushStreamSucc(ILivePushRes streamRes);

    void stopStreamSucc();

    void startRecordCallback(boolean isSucc);

    void stopRecordCallback(boolean isSucc, List<String> files);

    void hostLeave(String id, String name);

    void hostBack(String id, String name);

    void refreshMember(ArrayList<MemberID> memlist);

    void linkRoomReq(String id, String name);

    void linkRoomAccept(String id, String strRoomId);

    void forceQuitRoom(String strMessage);

    void exitErrorRoom();

    void permisstionsResult(String errorCode, String errorMsg, TeacherInfo data);

    void addLiveRoomGoodNumberResult(String[] result);

    void searchLiveRoomGoodNumberResult(String[] result);
}
