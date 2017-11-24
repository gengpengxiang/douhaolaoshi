package com.bj.eduteacher.media.controller;

import android.view.View;

import com.bj.eduteacher.media.StateChangeListener;
import com.bj.eduteacher.media.videoview.IVideoView;


/**
 * Created on 2017/8/10 上午10:38.
 * leo linxiaotao1993@vip.qq.com
 */

public interface IMediaController extends StateChangeListener {

    View makeControllerView();

    void setVideoView(IVideoView videoView);

    void setEnabled(boolean enable);
}
