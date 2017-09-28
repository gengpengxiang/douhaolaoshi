package com.bj.eduteacher.presenter.viewinface;


import com.bj.eduteacher.model.RoomInfoJson;
import com.bj.eduteacher.presenter.UserServerHelper;

import java.util.ArrayList;


/**
 *  列表页面回调
 */
public interface LiveListView extends MvpView{


    void showRoomList(UserServerHelper.RequestBackInfo result, ArrayList<RoomInfoJson> roomlist);
}
