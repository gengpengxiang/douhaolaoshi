package com.bj.eduteacher.entity;

/**
 * Created by zz379 on 2017/3/7.
 * 教师名下的班级
 */

public class SubjectInfo {
    private String subID;
    private String subName;
    private int subBadgeCount;

    public SubjectInfo() {
    }

    public SubjectInfo(String subID, String subName, int subBadgeCount) {
        this.subID = subID;
        this.subName = subName;
        this.subBadgeCount = subBadgeCount;
    }

    public int getSubBadgeCount() {
        return subBadgeCount;
    }

    public void setSubBadgeCount(int subBadgeCount) {
        this.subBadgeCount = subBadgeCount;
    }

    public String getSubID() {
        return subID;
    }

    public void setSubID(String subID) {
        this.subID = subID;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }
}
