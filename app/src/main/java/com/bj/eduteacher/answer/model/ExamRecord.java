package com.bj.eduteacher.answer.model;

import java.util.List;

/**
 * Created by Administrator on 2018/7/10 0010.
 */

public class ExamRecord {


    /**
     * ret : 1
     * msg :
     * data : [{"id":"95","updatetime":"2018-07-13 15:37:02","createtime":"2018-07-13 15:37:02","examid":"1","teacherid":"392","logcode":"2222","status":"1","shiti_right":"1","shiti_num":"1"},{"id":"94","updatetime":"2018-07-13 15:35:22","createtime":"2018-07-13 15:35:22","examid":"1","teacherid":"392","logcode":"2222","status":"1","shiti_right":"1","shiti_num":"0"}]
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
         * id : 95
         * updatetime : 2018-07-13 15:37:02
         * createtime : 2018-07-13 15:37:02
         * examid : 1
         * teacherid : 392
         * logcode : 2222
         * status : 1
         * shiti_right : 1
         * shiti_num : 1
         */

        private String id;
        private String updatetime;
        private String createtime;
        private String examid;
        private String teacherid;
        private String logcode;
        private String status;
        private String shiti_right;
        private String shiti_num;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getExamid() {
            return examid;
        }

        public void setExamid(String examid) {
            this.examid = examid;
        }

        public String getTeacherid() {
            return teacherid;
        }

        public void setTeacherid(String teacherid) {
            this.teacherid = teacherid;
        }

        public String getLogcode() {
            return logcode;
        }

        public void setLogcode(String logcode) {
            this.logcode = logcode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getShiti_right() {
            return shiti_right;
        }

        public void setShiti_right(String shiti_right) {
            this.shiti_right = shiti_right;
        }

        public String getShiti_num() {
            return shiti_num;
        }

        public void setShiti_num(String shiti_num) {
            this.shiti_num = shiti_num;
        }
    }
}
