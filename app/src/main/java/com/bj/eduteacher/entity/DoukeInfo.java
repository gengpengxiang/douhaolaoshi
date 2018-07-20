package com.bj.eduteacher.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/5/31 0031.
 */

public class DoukeInfo {

    private String ret;
    private String msg;
    private List<DataBean> data;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * url : https://mp.weixin.qq.com/s/5qDs4wKmP2RJVr9fBDAyqQ
         * title : 她竟然用一面墙，扭转了赫赫有名的“问题班”
         * author : 逗号博士
         * img : 71c806639bd1d1de4524664bce9169d0.jpg
         * authorimg : 7a59f7e3f20ba48283c34cbff90daf58.jpg
         * authorjianjie : 科学怪咖，让孩子享受科学快乐的教育
         * jianjie : 0
         * id : 965
         * titlejiami : 0
         * content :
         * time : 2018-03-19 14:43:54
         * type : 1
         * kehuduan : jiaoshi
         * tuisong :
         * dianzan : 5
         * pageview : 93
         * comment_num : 4
         */

        private String url;
        private String title;
        private String author;
        private String img;
        private String authorimg;
        private String authorjianjie;
        private String jianjie;
        private String id;
        private String titlejiami;
        private String content;
        private String time;
        private String type;
        private String kehuduan;
        private String tuisong;
        private String dianzan;
        private String pageview;
        private String comment_num;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getAuthorimg() {
            return authorimg;
        }

        public void setAuthorimg(String authorimg) {
            this.authorimg = authorimg;
        }

        public String getAuthorjianjie() {
            return authorjianjie;
        }

        public void setAuthorjianjie(String authorjianjie) {
            this.authorjianjie = authorjianjie;
        }

        public String getJianjie() {
            return jianjie;
        }

        public void setJianjie(String jianjie) {
            this.jianjie = jianjie;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitlejiami() {
            return titlejiami;
        }

        public void setTitlejiami(String titlejiami) {
            this.titlejiami = titlejiami;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getKehuduan() {
            return kehuduan;
        }

        public void setKehuduan(String kehuduan) {
            this.kehuduan = kehuduan;
        }

        public String getTuisong() {
            return tuisong;
        }

        public void setTuisong(String tuisong) {
            this.tuisong = tuisong;
        }

        public String getDianzan() {
            return dianzan;
        }

        public void setDianzan(String dianzan) {
            this.dianzan = dianzan;
        }

        public String getPageview() {
            return pageview;
        }

        public void setPageview(String pageview) {
            this.pageview = pageview;
        }

        public String getComment_num() {
            return comment_num;
        }

        public void setComment_num(String comment_num) {
            this.comment_num = comment_num;
        }
    }
}
