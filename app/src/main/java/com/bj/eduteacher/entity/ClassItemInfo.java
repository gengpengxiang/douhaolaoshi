package com.bj.eduteacher.entity;

/**
 * Created by zz379 on 2017/1/6.
 * 教师端班级信息
 */
public class ClassItemInfo {

    public static final int SHOWTYPE_CLASS = 0;
    public static final int SHOWTYPE_STUDENT = 1;

    private Long id;
    private String clasId;
    private String clasName;
    private String clasImg;
    private String clasScoreNum;   // 点赞数量
    private String clasBadgeNum;    // 徽章数量
    private String clasBadgeProNum; // 专项徽章的数量
    private String clasRankNum;     // 全校排名
    private String clasGanXieNum;   // 感谢数量
    private String clasXueke;       // 学科

    private String clasGradeNum1;   // 童生的人数
    private String clasGradeNum2;   // 秀才的人数
    private String clasGradeNum3;   // 举人的人数
    private String clasGradeNum4;   // 贡生的人数
    private String clasGradeNum5;   // 进士的人数

    private String clasGradeName1;
    private String clasGradeName2;
    private String clasGradeName3;
    private String clasGradeName4;
    private String clasGradeName5;

    private String studId;
    private String studName;
    private String studImg;
    private String studScore;   // 积分
    private String studBadge;   // 徽章
    private String studBadgePro;    // 专项
    private String studGrade;   // 等级
    private String studPingyu;

    private int showType;   // 页面展示的类型  0：班级    1：学生

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

    public ClassItemInfo(Long id, String clasId, String clasName, String clasImg, String clasScoreNum, String clasBadgeNum, String clasBadgeProNum, String clasRankNum,
            String clasGanXieNum, String clasXueke, String clasGradeNum1, String clasGradeNum2, String clasGradeNum3, String clasGradeNum4, String clasGradeNum5,
            String clasGradeName1, String clasGradeName2, String clasGradeName3, String clasGradeName4, String clasGradeName5, String studId, String studName, String studImg,
            String studScore, String studBadge, String studBadgePro, String studGrade, String studPingyu, int showType) {
        this.id = id;
        this.clasId = clasId;
        this.clasName = clasName;
        this.clasImg = clasImg;
        this.clasScoreNum = clasScoreNum;
        this.clasBadgeNum = clasBadgeNum;
        this.clasBadgeProNum = clasBadgeProNum;
        this.clasRankNum = clasRankNum;
        this.clasGanXieNum = clasGanXieNum;
        this.clasXueke = clasXueke;
        this.clasGradeNum1 = clasGradeNum1;
        this.clasGradeNum2 = clasGradeNum2;
        this.clasGradeNum3 = clasGradeNum3;
        this.clasGradeNum4 = clasGradeNum4;
        this.clasGradeNum5 = clasGradeNum5;
        this.clasGradeName1 = clasGradeName1;
        this.clasGradeName2 = clasGradeName2;
        this.clasGradeName3 = clasGradeName3;
        this.clasGradeName4 = clasGradeName4;
        this.clasGradeName5 = clasGradeName5;
        this.studId = studId;
        this.studName = studName;
        this.studImg = studImg;
        this.studScore = studScore;
        this.studBadge = studBadge;
        this.studBadgePro = studBadgePro;
        this.studGrade = studGrade;
        this.studPingyu = studPingyu;
        this.showType = showType;
    }

    public ClassItemInfo() {
    }


    public String getClasBadgeProNum() {
        return clasBadgeProNum;
    }

    public void setClasBadgeProNum(String clasBadgeProNum) {
        this.clasBadgeProNum = clasBadgeProNum;
    }

    public String getStudPingyu() {
        return studPingyu;
    }

    public void setStudPingyu(String studPingyu) {
        this.studPingyu = studPingyu;
    }

    public String getClasGanXieNum() {
        return clasGanXieNum;
    }

    public void setClasGanXieNum(String clasGanXieNum) {
        this.clasGanXieNum = clasGanXieNum;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClasId() {
        return this.clasId;
    }

    public void setClasId(String clasId) {
        this.clasId = clasId;
    }

    public String getClasName() {
        return this.clasName;
    }

    public void setClasName(String clasName) {
        this.clasName = clasName;
    }

    public String getClasImg() {
        return this.clasImg;
    }

    public void setClasImg(String clasImg) {
        this.clasImg = clasImg;
    }

    public String getClasScoreNum() {
        return this.clasScoreNum;
    }

    public void setClasScoreNum(String clasScoreNum) {
        this.clasScoreNum = clasScoreNum;
    }

    public String getClasBadgeNum() {
        return this.clasBadgeNum;
    }

    public void setClasBadgeNum(String clasBadgeNum) {
        this.clasBadgeNum = clasBadgeNum;
    }

    public String getClasRankNum() {
        return this.clasRankNum;
    }

    public void setClasRankNum(String clasRankNum) {
        this.clasRankNum = clasRankNum;
    }

    public String getClasGradeNum1() {
        return this.clasGradeNum1;
    }

    public void setClasGradeNum1(String clasGradeNum1) {
        this.clasGradeNum1 = clasGradeNum1;
    }

    public String getClasGradeNum2() {
        return this.clasGradeNum2;
    }

    public void setClasGradeNum2(String clasGradeNum2) {
        this.clasGradeNum2 = clasGradeNum2;
    }

    public String getClasGradeNum3() {
        return this.clasGradeNum3;
    }

    public void setClasGradeNum3(String clasGradeNum3) {
        this.clasGradeNum3 = clasGradeNum3;
    }

    public String getClasGradeNum4() {
        return this.clasGradeNum4;
    }

    public void setClasGradeNum4(String clasGradeNum4) {
        this.clasGradeNum4 = clasGradeNum4;
    }

    public String getStudId() {
        return this.studId;
    }

    public void setStudId(String studId) {
        this.studId = studId;
    }

    public String getStudName() {
        return this.studName;
    }

    public void setStudName(String studName) {
        this.studName = studName;
    }

    public String getStudImg() {
        return this.studImg;
    }

    public void setStudImg(String studImg) {
        this.studImg = studImg;
    }

    public String getStudScore() {
        return this.studScore;
    }

    public void setStudScore(String studScore) {
        this.studScore = studScore;
    }

    public String getStudBadge() {
        return this.studBadge;
    }

    public void setStudBadge(String studBadge) {
        this.studBadge = studBadge;
    }

    public String getStudGrade() {
        return this.studGrade;
    }

    public void setStudGrade(String studGrade) {
        this.studGrade = studGrade;
    }

    public int getShowType() {
        return this.showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public String getClasGradeNum5() {
        return this.clasGradeNum5;
    }

    public void setClasGradeNum5(String clasGradeNum5) {
        this.clasGradeNum5 = clasGradeNum5;
    }

    public String getClasGradeName1() {
        return this.clasGradeName1;
    }

    public void setClasGradeName1(String clasGradeName1) {
        this.clasGradeName1 = clasGradeName1;
    }

    public String getClasGradeName2() {
        return this.clasGradeName2;
    }

    public void setClasGradeName2(String clasGradeName2) {
        this.clasGradeName2 = clasGradeName2;
    }

    public String getClasGradeName3() {
        return this.clasGradeName3;
    }

    public void setClasGradeName3(String clasGradeName3) {
        this.clasGradeName3 = clasGradeName3;
    }

    public String getClasGradeName4() {
        return this.clasGradeName4;
    }

    public void setClasGradeName4(String clasGradeName4) {
        this.clasGradeName4 = clasGradeName4;
    }

    public String getClasGradeName5() {
        return this.clasGradeName5;
    }

    public void setClasGradeName5(String clasGradeName5) {
        this.clasGradeName5 = clasGradeName5;
    }

    public String getClasXueke() {
        return clasXueke;
    }

    public void setClasXueke(String clasXueke) {
        this.clasXueke = clasXueke;
    }

    public String getStudBadgePro() {
        return studBadgePro;
    }

    public void setStudBadgePro(String studBadgePro) {
        this.studBadgePro = studBadgePro;
    }
}
