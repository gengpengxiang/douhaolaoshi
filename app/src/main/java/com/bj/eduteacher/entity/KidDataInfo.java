package com.bj.eduteacher.entity;

/**
 * Created by zz379 on 2016/12/25.
 */
public class KidDataInfo {

    private Long id;
    private String kidId;
    private String score;   // 积分
    private String badge;   // 徽章
    private String grade;   // 等级
    private String pingyu;  // 评语
    private String badgePro;    // 专项
    private String updateTime;
    private String errorCode;
    private String message;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public KidDataInfo(Long id, String kidId, String score, String badge,
            String grade, String pingyu, String badgePro, String updateTime) {
        this.id = id;
        this.kidId = kidId;
        this.score = score;
        this.badge = badge;
        this.grade = grade;
        this.pingyu = pingyu;
        this.badgePro = badgePro;
        this.updateTime = updateTime;
    }

    public KidDataInfo() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKidId() {
        return this.kidId;
    }

    public void setKidId(String kidId) {
        this.kidId = kidId;
    }

    public String getScore() {
        return this.score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getBadge() {
        return this.badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getGrade() {
        return this.grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPingyu() {
        return this.pingyu;
    }

    public void setPingyu(String pingyu) {
        this.pingyu = pingyu;
    }

    public String getBadgePro() {
        return badgePro;
    }

    public void setBadgePro(String badgePro) {
        this.badgePro = badgePro;
    }
}
