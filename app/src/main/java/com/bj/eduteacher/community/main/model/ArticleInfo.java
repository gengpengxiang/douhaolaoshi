package com.bj.eduteacher.community.main.model;

import java.util.List;

/**
 * Created by Administrator on 2018/4/3 0003.
 */

public class ArticleInfo {

    /**
     * ret : 1
     * msg :
     * data : [{"id":"897","title":"6L+Z5L2N5riF5Y","author":"明月几时有","authorimg":"9e639f5b12f6ab431d3925a1a0bd847e.jpeg","createtime":"17小时前","dianzan":"2","pageview":"0","usercode":"11111111002","comment_num":"0","news_dianzanstatus":"0","one_content":[{"content":"6L+R5q615pe26Ze077yM5"}],"three_content":[{"img":"102ddeb1c1f164a7fca1d831a7238462.jpeg"},{"img":"bebedf963794bfbfdfef7d86bfc5d3e8.jpeg"},{"img":"7287bcccb65f1b40eb88529c226104f1.jpeg"}]}]
     */

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



    public static class DataBean  {

        public static final int FIRST_TYPE = 1;
        public static final int SECOND_TYPE = 2;
        public static final int THREE_TYPE = 3;

        //添加类型变量
        public int type;
        /**
         * id : 897
         * title : 6L+Z5L2N5riF5Y
         * author : 明月几时有
         * authorimg : 9e639f5b12f6ab431d3925a1a0bd847e.jpeg
         * authorimg_url": ""
         * createtime : 17小时前
         * dianzan : 2
         * pageview : 0
         * usercode : 11111111002
         * comment_num : 0
         * news_dianzanstatus : 0
         * one_content : [{"content":"6L+R5q615pe26Ze077yM5"}]
         * three_content : [{"img":"102ddeb1c1f164a7fca1d831a7238462.jpeg"},{"img":"bebedf963794bfbfdfef7d86bfc5d3e8.jpeg"},{"img":"7287bcccb65f1b40eb88529c226104f1.jpeg"}]
         */
//        public static final int IMG_NO = 1;
//        public static final int IMG_ONE = 2;
//        public static final int IMG_THREE = 3;
//
//        private int itemType;
//
//        public DataBean(int itemType) {
//            this.itemType = itemType;
//        }
//
//        @Override
//        public int getItemType() {
//            return itemType;
//        }

        private String id;
        private String title;
        private String author;
        private String authorimg;
        private String authorimg_url;
        private String unionid;
        private String createtime;
        private String dianzan;
        private String pageview;
        private String usercode;
        private String comment_num;
        private String news_dianzanstatus;
        private List<OneContentBean> one_content;
        private List<ThreeContentBean> three_content;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getAuthorimg() {
            return authorimg;
        }

        public void setAuthorimg(String authorimg) {
            this.authorimg = authorimg;
        }

        public String getAuthorimg_url() {
            return authorimg_url;
        }

        public void setAuthorimg_url(String authorimg_url) {
            this.authorimg_url = authorimg_url;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
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

        public String getUsercode() {
            return usercode;
        }

        public void setUsercode(String usercode) {
            this.usercode = usercode;
        }

        public String getComment_num() {
            return comment_num;
        }

        public void setComment_num(String comment_num) {
            this.comment_num = comment_num;
        }

        public String getNews_dianzanstatus() {
            return news_dianzanstatus;
        }

        public void setNews_dianzanstatus(String news_dianzanstatus) {
            this.news_dianzanstatus = news_dianzanstatus;
        }

        public List<OneContentBean> getOne_content() {
            return one_content;
        }

        public void setOne_content(List<OneContentBean> one_content) {
            this.one_content = one_content;
        }

        public List<ThreeContentBean> getThree_content() {
            return three_content;
        }

        public void setThree_content(List<ThreeContentBean> three_content) {
            this.three_content = three_content;
        }

        public static class OneContentBean {
            /**
             * content : 6L+R5q615pe26Ze077yM5
             */

            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }

        public static class ThreeContentBean {
            /**
             * img : 102ddeb1c1f164a7fca1d831a7238462.jpeg
             */

            private String img;

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (this == null && obj == null)
                return true;
            if (this == null || obj == null)
                return false;
            if (obj.getClass() != this.getClass())
                return false;
            ArticleInfo.DataBean a = (ArticleInfo.DataBean) obj;
            if (this.id.equals(a.id)&&this.comment_num.equals(a.comment_num)&&this.dianzan.equals(a.dianzan))
                return true;
            return false;
        }
    }
}
