package com.bj.eduteacher.zzeaseui.model;

import com.hyphenate.chat.EMConversation;

/**
 * Created by zz379 on 2017/5/22.
 */

public class EaseConversation {
    private EMConversation emConversation;
    private String userEaseID;
    private String userPhoto;
    private String userNick;
    private String className;
    private String relation;

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public EMConversation getEmConversation() {
        return emConversation;
    }

    public void setEmConversation(EMConversation emConversation) {
        this.emConversation = emConversation;
    }

    public String getUserEaseID() {
        return userEaseID;
    }

    public void setUserEaseID(String userEaseID) {
        this.userEaseID = userEaseID;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }
}
