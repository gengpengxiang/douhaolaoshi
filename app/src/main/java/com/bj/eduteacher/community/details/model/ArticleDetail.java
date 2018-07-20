package com.bj.eduteacher.community.details.model;

import java.util.List;

/**
 * Created by Administrator on 2018/4/18 0018.
 */

public class ArticleDetail {
    /**
     * ret : 1
     * msg :
     * data : {"news_dianzanstatus":"0","news_info":{"id":"455","xiaochengxu":"0","url":"0","status":"0","titlejiami":"0","groupid":"0","title":"5Lik5byg5Zu+54mH","content":"","author":"","time":"0","updatetime":"2018-04-12 12:07:18","type":"0","img":"","authorimg":"0","authorjianjie":"0","kehuduan":"0","tuisong":"0","dianzan":"0","pageview":"0","comment_num":"0","jianjie":"0","usercode":"13520031276","createtime":"2018-04-12 12:07:18"},"news_content":[{"id":"774","newsid":"455","content":"你们好","img":"","img_s":"","uptimestamp":"2018-04-12 12:07:18","type":"1","ordernum":"1"},{"id":"775","newsid":"455","content":"","img":"6ecfddf4bb22503e899856a258ef3752.jpg","img_s":"c4de8cb7ba6f0598367a5caa5f55c678.jpg","uptimestamp":"2018-04-12 12:07:18","type":"2","ordernum":"2"},{"id":"776","newsid":"455","content":"","img":"6ecfddf4bb22503e899856a258ef3752.jpg","img_s":"c4de8cb7ba6f0598367a5caa5f55c678.jpg","uptimestamp":"2018-04-12 12:07:18","type":"2","ordernum":"3"}]}
     */

    private String ret;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * news_dianzanstatus : 0
         * news_info : {"id":"455","xiaochengxu":"0","url":"0","status":"0","titlejiami":"0","groupid":"0","title":"5Lik5byg5Zu+54mH","content":"","author":"","time":"0","updatetime":"2018-04-12 12:07:18","type":"0","img":"","authorimg":"0","authorjianjie":"0","kehuduan":"0","tuisong":"0","dianzan":"0","pageview":"0","comment_num":"0","jianjie":"0","usercode":"13520031276","createtime":"2018-04-12 12:07:18"}
         * news_content : [{"id":"774","newsid":"455","content":"你们好","img":"","img_s":"","uptimestamp":"2018-04-12 12:07:18","type":"1","ordernum":"1"},{"id":"775","newsid":"455","content":"","img":"6ecfddf4bb22503e899856a258ef3752.jpg","img_s":"c4de8cb7ba6f0598367a5caa5f55c678.jpg","uptimestamp":"2018-04-12 12:07:18","type":"2","ordernum":"2"},{"id":"776","newsid":"455","content":"","img":"6ecfddf4bb22503e899856a258ef3752.jpg","img_s":"c4de8cb7ba6f0598367a5caa5f55c678.jpg","uptimestamp":"2018-04-12 12:07:18","type":"2","ordernum":"3"}]
         */

        private String news_dianzanstatus;
        private NewsInfoBean news_info;
        private List<NewsContentBean> news_content;

        public String getNews_dianzanstatus() {
            return news_dianzanstatus;
        }

        public void setNews_dianzanstatus(String news_dianzanstatus) {
            this.news_dianzanstatus = news_dianzanstatus;
        }

        public NewsInfoBean getNews_info() {
            return news_info;
        }

        public void setNews_info(NewsInfoBean news_info) {
            this.news_info = news_info;
        }

        public List<NewsContentBean> getNews_content() {
            return news_content;
        }

        public void setNews_content(List<NewsContentBean> news_content) {
            this.news_content = news_content;
        }

        public static class NewsInfoBean {
            /**
             * id : 455
             * xiaochengxu : 0
             * url : 0
             * status : 0
             * titlejiami : 0
             * groupid : 0
             * title : 5Lik5byg5Zu+54mH
             * content :
             * author :
             * time : 0
             * updatetime : 2018-04-12 12:07:18
             * type : 0
             * img :
             * authorimg : 0
             * authorjianjie : 0
             * kehuduan : 0
             * tuisong : 0
             * dianzan : 0
             * pageview : 0
             * comment_num : 0
             * jianjie : 0
             * usercode : 13520031276
             * createtime : 2018-04-12 12:07:18
             */

            private String id;
            private String xiaochengxu;
            private String url;
            private String status;
            private String titlejiami;
            private String groupid;
            private String title;
            private String content;
            private String author;
            private String time;
            private String updatetime;
            private String type;
            private String img;
            private String authorimg;
            private String authorjianjie;
            private String kehuduan;
            private String tuisong;
            private String dianzan;
            private String pageview;
            private String comment_num;
            private String jianjie;
            private String usercode;
            private String createtime;
            private String weixin_unionid;

            public String getWeixin_unionid() {
                return weixin_unionid;
            }

            public void setWeixin_unionid(String weixin_unionid) {
                this.weixin_unionid = weixin_unionid;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getXiaochengxu() {
                return xiaochengxu;
            }

            public void setXiaochengxu(String xiaochengxu) {
                this.xiaochengxu = xiaochengxu;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getTitlejiami() {
                return titlejiami;
            }

            public void setTitlejiami(String titlejiami) {
                this.titlejiami = titlejiami;
            }

            public String getGroupid() {
                return groupid;
            }

            public void setGroupid(String groupid) {
                this.groupid = groupid;
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

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
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

            public String getJianjie() {
                return jianjie;
            }

            public void setJianjie(String jianjie) {
                this.jianjie = jianjie;
            }

            public String getUsercode() {
                return usercode;
            }

            public void setUsercode(String usercode) {
                this.usercode = usercode;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }
        }

        public static class NewsContentBean {
            /**
             * id : 774
             * newsid : 455
             * content : 你们好
             * img :
             * img_s :
             * uptimestamp : 2018-04-12 12:07:18
             * type : 1
             * ordernum : 1
             */

            private String id;
            private String newsid;
            private String content;
            private String img;
            private String img_s;
            private String uptimestamp;
            private String type;
            private String ordernum;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getNewsid() {
                return newsid;
            }

            public void setNewsid(String newsid) {
                this.newsid = newsid;
            }

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

            public String getUptimestamp() {
                return uptimestamp;
            }

            public void setUptimestamp(String uptimestamp) {
                this.uptimestamp = uptimestamp;
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
    }
}
