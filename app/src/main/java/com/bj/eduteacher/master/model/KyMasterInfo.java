package com.bj.eduteacher.master.model;

import java.util.List;

/**
 * Created by Administrator on 2018/6/22 0022.
 */

public class KyMasterInfo {

    /**
     * ret : 1
     * msg :
     * data : {"getmore":"0","master_data":[{"mastercode":"0004","img":"d66ae2561618929878913ccefac98ebb.JPEG","name":"龙涛鼎","title":"北京大学教育学院副教授","sjianjie":"让学习更科学更快乐更有效让学习更科学更快乐更有效","news":"老师为什么是\u201c老师\u201d？你不能不知道自己尊称的由来啊","tuijianorder":"4"}],"masternum":2}
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
         * getmore : 0
         * master_data : [{"mastercode":"0004","img":"d66ae2561618929878913ccefac98ebb.JPEG","name":"龙涛鼎","title":"北京大学教育学院副教授","sjianjie":"让学习更科学更快乐更有效让学习更科学更快乐更有效","news":"老师为什么是\u201c老师\u201d？你不能不知道自己尊称的由来啊","tuijianorder":"4"}]
         * masternum : 2
         */

        private String getmore;
        private int masternum;
        private List<MasterDataBean> master_data;

        public String getGetmore() {
            return getmore;
        }

        public void setGetmore(String getmore) {
            this.getmore = getmore;
        }

        public int getMasternum() {
            return masternum;
        }

        public void setMasternum(int masternum) {
            this.masternum = masternum;
        }

        public List<MasterDataBean> getMaster_data() {
            return master_data;
        }

        public void setMaster_data(List<MasterDataBean> master_data) {
            this.master_data = master_data;
        }

        public static class MasterDataBean {
            /**
             * mastercode : 0004
             * img : d66ae2561618929878913ccefac98ebb.JPEG
             * name : 龙涛鼎
             * title : 北京大学教育学院副教授
             * sjianjie : 让学习更科学更快乐更有效让学习更科学更快乐更有效
             * news : 老师为什么是“老师”？你不能不知道自己尊称的由来啊
             * tuijianorder : 4
             */

            private String mastercode;
            private String img;
            private String name;
            private String title;
            private String sjianjie;
            private String news;
            private String tuijianorder;

            public String getMastercode() {
                return mastercode;
            }

            public void setMastercode(String mastercode) {
                this.mastercode = mastercode;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getSjianjie() {
                return sjianjie;
            }

            public void setSjianjie(String sjianjie) {
                this.sjianjie = sjianjie;
            }

            public String getNews() {
                return news;
            }

            public void setNews(String news) {
                this.news = news;
            }

            public String getTuijianorder() {
                return tuijianorder;
            }

            public void setTuijianorder(String tuijianorder) {
                this.tuijianorder = tuijianorder;
            }
        }
    }
}
