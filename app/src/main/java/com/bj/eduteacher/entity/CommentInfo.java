package com.bj.eduteacher.entity;

/**
 * Created by zz379 on 2017/6/16.
 */

public class CommentInfo {
    private String commID;
    private String commCreaterName;
    private String commCreaterDesc;
    private String commCreaterPhoto;
    private String commCreateTime;
    private String commContent;
    private String commCreatePhone;

    public CommentInfo() {
    }

    public CommentInfo(String commContent) {
        this.commContent = commContent;
    }

    public String getCommCreatePhone() {
        return commCreatePhone;
    }

    public void setCommCreatePhone(String commCreatePhone) {
        this.commCreatePhone = commCreatePhone;
    }

    public String getCommID() {
        return commID;
    }

    public void setCommID(String commID) {
        this.commID = commID;
    }

    public String getCommCreaterName() {
        return commCreaterName;
    }

    public void setCommCreaterName(String commCreaterName) {
        this.commCreaterName = commCreaterName;
    }

    public String getCommCreaterDesc() {
        return commCreaterDesc;
    }

    public void setCommCreaterDesc(String commCreaterDesc) {
        this.commCreaterDesc = commCreaterDesc;
    }

    public String getCommCreaterPhoto() {
        return commCreaterPhoto;
    }

    public void setCommCreaterPhoto(String commCreaterPhoto) {
        this.commCreaterPhoto = commCreaterPhoto;
    }

    public String getCommCreateTime() {
        return commCreateTime;
    }

    public void setCommCreateTime(String commCreateTime) {
        this.commCreateTime = commCreateTime;
    }

    public String getCommContent() {
        return commContent;
    }

    public void setCommContent(String commContent) {
        this.commContent = commContent;
    }
}
