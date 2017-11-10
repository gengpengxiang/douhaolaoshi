package com.bj.eduteacher.entity;

/**
 * Created by zz379 on 2017/4/17.
 */

public class TeacherInfo {

    private String teacherPhoneNumber;
    private String teacherName;
    private String teacherImg;
    private String schoolName;
    private String schoolCode;
    private String schoolImg;

    private String teacherNickname; // 用户昵称

    private String errorCode;
    private String message;

    private String sxbStatus;       // 是否有直播的账号
    private String sxbUser;         // 直播用的账号 和 密码
    private String sxbTitle;        // 直播标题
    private String sxbPicture;      // 直播房间图片
    private String sxbPermissions;  // 直播权限 1 : 允许开直播， 0 : 不允许开直播
    private String sxbDanmuPermissions;

    public String getSxbDanmuPermissions() {
        return sxbDanmuPermissions;
    }

    public void setSxbDanmuPermissions(String sxbDanmuPermissions) {
        this.sxbDanmuPermissions = sxbDanmuPermissions;
    }

    public String getTeacherNickname() {
        return teacherNickname;
    }

    public void setTeacherNickname(String teacherNickname) {
        this.teacherNickname = teacherNickname;
    }

    public String getSxbStatus() {
        return sxbStatus;
    }

    public void setSxbStatus(String sxbStatus) {
        this.sxbStatus = sxbStatus;
    }

    public String getSxbUser() {
        return sxbUser;
    }

    public void setSxbUser(String sxbUser) {
        this.sxbUser = sxbUser;
    }

    public String getSxbTitle() {
        return sxbTitle;
    }

    public void setSxbTitle(String sxbTitle) {
        this.sxbTitle = sxbTitle;
    }

    public String getSxbPicture() {
        return sxbPicture;
    }

    public void setSxbPicture(String sxbPicture) {
        this.sxbPicture = sxbPicture;
    }

    public String getSxbPermissions() {
        return sxbPermissions;
    }

    public void setSxbPermissions(String sxbPermissions) {
        this.sxbPermissions = sxbPermissions;
    }

    public String getTeacherPhoneNumber() {
        return teacherPhoneNumber;
    }

    public void setTeacherPhoneNumber(String teacherPhoneNumber) {
        this.teacherPhoneNumber = teacherPhoneNumber;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherImg() {
        return teacherImg;
    }

    public void setTeacherImg(String teacherImg) {
        this.teacherImg = teacherImg;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolCode() {
        return schoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        this.schoolCode = schoolCode;
    }

    public String getSchoolImg() {
        return schoolImg;
    }

    public void setSchoolImg(String schoolImg) {
        this.schoolImg = schoolImg;
    }

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
}
