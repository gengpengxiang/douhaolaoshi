package com.bj.eduteacher.group.list.model;

import java.util.List;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public class GroupInfo {

    /**
     * ret : 1
     * msg : 查询成功
     * data : [{"updatestatus":"0","name":"教学法课程组","logo":"glogo0.jpg","jianjie":"一切为了提升老师生活质量","ht_zl_updatetime":"0","viewupdatetime":"0","id":"4"},{"updatestatus":"0","name":"游戏化学习课程组","logo":"glogo0.jpg","jianjie":"一切为了提升老师生活质量","ht_zl_updatetime":"0","viewupdatetime":"0","id":"5"},{"updatestatus":"1","name":"不简单老师组","logo":"glogo0.jpg","jianjie":"一切为了提升老师生活质量","ht_zl_updatetime":"2018-03-11 10:57:50","viewupdatetime":"2018-03-07 18:43:59","id":"2"}]
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

    public static class DataBean {
        /**
         * updatestatus : 0
         * name : 教学法课程组
         * logo : glogo0.jpg
         * jianjie : 一切为了提升老师生活质量
         * ht_zl_updatetime : 0
         * viewupdatetime : 0
         * id : 4
         */

        private String updatestatus;
        private String name;
        private String logo;
        private String jianjie;
        private String ht_zl_updatetime;
        private String viewupdatetime;
        private String id;

        public String getUpdatestatus() {
            return updatestatus;
        }

        public void setUpdatestatus(String updatestatus) {
            this.updatestatus = updatestatus;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        public String getJianjie() {
            return jianjie;
        }

        public void setJianjie(String jianjie) {
            this.jianjie = jianjie;
        }

        public String getHt_zl_updatetime() {
            return ht_zl_updatetime;
        }

        public void setHt_zl_updatetime(String ht_zl_updatetime) {
            this.ht_zl_updatetime = ht_zl_updatetime;
        }

        public String getViewupdatetime() {
            return viewupdatetime;
        }

        public void setViewupdatetime(String viewupdatetime) {
            this.viewupdatetime = viewupdatetime;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
