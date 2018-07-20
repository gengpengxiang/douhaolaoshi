package com.bj.eduteacher.community.publish.model;

/**
 * Created by Administrator on 2018/4/3 0003.
 */

public class ArticleContent {

    public ArticleContent(String content, String img, String img_s, String type, String ordernum) {
        this.content = content;
        this.img = img;
        this.img_s = img_s;
        this.type = type;
        this.ordernum = ordernum;
    }

    /**
     * content : content1
     * img : img1
     * img_s : imgs1
     * type : 1
     * ordernum : 1
     */



    private String content;
    private String img;
    private String img_s;
    private String type;
    private String ordernum;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg_s() {
        return img_s;
    }

    public void setImg_s(String img_s) {
        this.img_s = img_s;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum;
    }
}
