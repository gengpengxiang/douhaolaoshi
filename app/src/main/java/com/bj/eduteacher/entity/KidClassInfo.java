package com.bj.eduteacher.entity;

/**
 * Created by zz379 on 2016/12/25.
 */
public class KidClassInfo {

    private Long id;
    private String kidId;
    private String kidName;
    private String kidImg;
    private String schoolId;
    private String schoolName;
    private String classId;
    private String className;
    private String teacherImg;

    public String getTeacherImg() {
        return teacherImg;
    }

    public void setTeacherImg(String teacherImg) {
        this.teacherImg = teacherImg;
    }

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

    public KidClassInfo(Long id, String kidId, String kidName, String kidImg, String schoolId,
            String schoolName, String classId, String className, String teacherImg) {
        this.id = id;
        this.kidId = kidId;
        this.kidName = kidName;
        this.kidImg = kidImg;
        this.schoolId = schoolId;
        this.schoolName = schoolName;
        this.classId = classId;
        this.className = className;
        this.teacherImg = teacherImg;
    }

    public KidClassInfo() {
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

    public String getKidName() {
        return this.kidName;
    }

    public void setKidName(String kidName) {
        this.kidName = kidName;
    }

    public String getKidImg() {
        return this.kidImg;
    }

    public void setKidImg(String kidImg) {
        this.kidImg = kidImg;
    }

    public String getSchoolId() {
        return this.schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getClassId() {
        return this.classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


}
