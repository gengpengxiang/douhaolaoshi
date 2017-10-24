package com.bj.eduteacher.entity;

import java.util.List;

/**
 * Created by zz379 on 2017/4/11.
 */

public class ArticleInfo {

    public static final int SHOW_TYPE_DECORATION = 1;
    public static final int SHOW_TYPE_ZHUANJIA = 2;
    public static final int SHOW_TYPE_DOUKE = 3;
    public static final int SHOW_TYPE_ZHUANJIA_ALL = 4;
    public static final int SHOW_TYPE_ZHUANJIA_RES = 5;
    public static final int SHOW_TYPE_ZHUANJIA_BLACKBOARD_TOP = 6;
    public static final int SHOW_TYPE_ZHUANJIA_BLACKBOARD_MORE = 7;
    public static final int SHOW_TYPE_TEACHER = 8;
    public static final int SHOW_TYPE_LIVE = 9;
    public static final int SHOW_TYPE_LATEST_RES = 10;
    public static final int SHOW_TYPE_COURSE = 11;

    private String articleID;
    private String title;
    private String content;
    private String author;
    private String authImg;
    private String authDesc;
    private String articlePicture;
    private String articlePath;
    private String postTime;
    private String finishTime;

    private String nickname;

    private String readNumber;
    private String agreeNumber;
    private String commentNumber;

    private int showType;

    private String replyCount;
    private List<ArticleInfo> replyList;

    private String previewType;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPreviewType() {
        return previewType;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public void setPreviewType(String previewType) {
        this.previewType = previewType;
    }

    public ArticleInfo() {
    }

    public ArticleInfo(String title, int showType) {
        this.title = title;
        this.showType = showType;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
        this.replyCount = replyCount;
    }

    public List<ArticleInfo> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<ArticleInfo> replyList) {
        this.replyList = replyList;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public String getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(String commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public String getReadNumber() {
        return readNumber;
    }

    public void setReadNumber(String readNumber) {
        this.readNumber = readNumber;
    }

    public String getAgreeNumber() {
        return agreeNumber;
    }

    public void setAgreeNumber(String agreeNumber) {
        this.agreeNumber = agreeNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthImg() {
        return authImg;
    }

    public void setAuthImg(String authImg) {
        this.authImg = authImg;
    }

    public String getAuthDesc() {
        return authDesc;
    }

    public void setAuthDesc(String authDesc) {
        this.authDesc = authDesc;
    }

    public String getArticlePicture() {
        return articlePicture;
    }

    public void setArticlePicture(String articlePicture) {
        this.articlePicture = articlePicture;
    }

    public String getArticlePath() {
        return articlePath;
    }

    public void setArticlePath(String articlePath) {
        this.articlePath = articlePath;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }
}
