package com.bj.eduteacher.entity;

/**
 * Created by Administrator on 2018/4/20 0020.
 */

public class MsgEvent {
    ///public int action;
    public String action;
    public String doubisum;
    public int num;
    public long duration;
    public BaseDataInfo baseDataInfo;

    public MsgEvent(String action) {
        this.action = action;
    }

    public MsgEvent(String action, String doubisum) {
        this.action = action;
        this.doubisum = doubisum;
    }

    public MsgEvent(String action, int num) {
        this.action = action;
        this.num = num;
    }

    public MsgEvent(String action, long duration) {
        this.action = action;
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public MsgEvent(String action, BaseDataInfo baseDataInfo) {
        this.action = action;
        this.baseDataInfo = baseDataInfo;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDoubisum() {
        return doubisum;
    }

    public void setDoubisum(String doubisum) {
        this.doubisum = doubisum;
    }

    public BaseDataInfo getBaseDataInfo() {
        return baseDataInfo;
    }

    public void setBaseDataInfo(BaseDataInfo baseDataInfo) {
        this.baseDataInfo = baseDataInfo;
    }
}
