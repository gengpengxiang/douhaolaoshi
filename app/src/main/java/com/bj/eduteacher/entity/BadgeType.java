package com.bj.eduteacher.entity;

import java.util.List;

/**
 * Created by zz379 on 2017/3/9.
 */

public class BadgeType {
    private String badgeTypeID;
    private String name;
    private int number;
    private List<SubjectInfo> xuekeList;

    public BadgeType() {
    }

    public BadgeType(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBadgeTypeID() {
        return badgeTypeID;
    }

    public void setBadgeTypeID(String badgeTypeID) {
        this.badgeTypeID = badgeTypeID;
    }

    public List<SubjectInfo> getXuekeList() {
        return xuekeList;
    }

    public void setXuekeList(List<SubjectInfo> xuekeList) {
        this.xuekeList = xuekeList;
    }
}
