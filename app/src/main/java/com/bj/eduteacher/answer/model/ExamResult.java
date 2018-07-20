package com.bj.eduteacher.answer.model;

import java.util.List;

/**
 * Created by Administrator on 2018/7/13 0013.
 */

public class ExamResult {

    /**
     * ret : 1
     * msg :
     * data : {"log_exam":{"id":"9","updatetime":"2018-07-12 15:34:01","createtime":"2018-07-12 15:34:01","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","status":"0","shiti_right":"0","shiti_num":"0","exam_title":"第一周测验"},"log_shiti":[{"id":"17","updatetime":"2018-07-12 15:34:27","createtime":"2018-07-12 15:34:27","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","shiti_id":"1","status":"0","daan_teacher":"1,2","shiti_ordernum":"0","shiti_title":"第一题"},{"id":"30","updatetime":"2018-07-12 17:36:27","createtime":"2018-07-12 17:36:27","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","shiti_id":"2","status":"0","daan_teacher":"2","shiti_ordernum":"0","shiti_title":"第二题的题干也很长的话也是要进行换行的，显示效果是什么样子的呢？"},{"id":"31","updatetime":"2018-07-12 17:36:27","createtime":"2018-07-12 17:36:27","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","shiti_id":"0","status":"0","daan_teacher":"[","shiti_ordernum":"0","shiti_title":""},{"id":"32","updatetime":"2018-07-13 11:02:55","createtime":"2018-07-12 17:36:30","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","shiti_id":"2","status":"1","daan_teacher":"2","shiti_ordernum":"0","shiti_title":"第二题的题干也很长的话也是要进行换行的，显示效果是什么样子的呢？"},{"id":"33","updatetime":"2018-07-12 17:36:30","createtime":"2018-07-12 17:36:30","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","shiti_id":"0","status":"0","daan_teacher":"[","shiti_ordernum":"0","shiti_title":""}]}
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
         * log_exam : {"id":"9","updatetime":"2018-07-12 15:34:01","createtime":"2018-07-12 15:34:01","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","status":"0","shiti_right":"0","shiti_num":"0","exam_title":"第一周测验"}
         * log_shiti : [{"id":"17","updatetime":"2018-07-12 15:34:27","createtime":"2018-07-12 15:34:27","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","shiti_id":"1","status":"0","daan_teacher":"1,2","shiti_ordernum":"0","shiti_title":"第一题"},{"id":"30","updatetime":"2018-07-12 17:36:27","createtime":"2018-07-12 17:36:27","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","shiti_id":"2","status":"0","daan_teacher":"2","shiti_ordernum":"0","shiti_title":"第二题的题干也很长的话也是要进行换行的，显示效果是什么样子的呢？"},{"id":"31","updatetime":"2018-07-12 17:36:27","createtime":"2018-07-12 17:36:27","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","shiti_id":"0","status":"0","daan_teacher":"[","shiti_ordernum":"0","shiti_title":""},{"id":"32","updatetime":"2018-07-13 11:02:55","createtime":"2018-07-12 17:36:30","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","shiti_id":"2","status":"1","daan_teacher":"2","shiti_ordernum":"0","shiti_title":"第二题的题干也很长的话也是要进行换行的，显示效果是什么样子的呢？"},{"id":"33","updatetime":"2018-07-12 17:36:30","createtime":"2018-07-12 17:36:30","examid":"1","teacherid":"392","logcode":"132699690631531365243403524","shiti_id":"0","status":"0","daan_teacher":"[","shiti_ordernum":"0","shiti_title":""}]
         */

        private LogExamBean log_exam;
        private List<LogShitiBean> log_shiti;

        public LogExamBean getLog_exam() {
            return log_exam;
        }

        public void setLog_exam(LogExamBean log_exam) {
            this.log_exam = log_exam;
        }

        public List<LogShitiBean> getLog_shiti() {
            return log_shiti;
        }

        public void setLog_shiti(List<LogShitiBean> log_shiti) {
            this.log_shiti = log_shiti;
        }

        public static class LogExamBean {
            /**
             * id : 9
             * updatetime : 2018-07-12 15:34:01
             * createtime : 2018-07-12 15:34:01
             * examid : 1
             * teacherid : 392
             * logcode : 132699690631531365243403524
             * status : 0
             * shiti_right : 0
             * shiti_num : 0
             * exam_title : 第一周测验
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
            private String exam_title;

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

            public String getExam_title() {
                return exam_title;
            }

            public void setExam_title(String exam_title) {
                this.exam_title = exam_title;
            }
        }

        public static class LogShitiBean {
            /**
             * id : 17
             * updatetime : 2018-07-12 15:34:27
             * createtime : 2018-07-12 15:34:27
             * examid : 1
             * teacherid : 392
             * logcode : 132699690631531365243403524
             * shiti_id : 1
             * status : 0
             * daan_teacher : 1,2
             * shiti_ordernum : 0
             * shiti_title : 第一题
             */

            private String id;
            private String updatetime;
            private String createtime;
            private String examid;
            private String teacherid;
            private String logcode;
            private String shiti_id;
            private String status;
            private String daan_teacher;
            private String shiti_ordernum;
            private String shiti_title;

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

            public String getShiti_id() {
                return shiti_id;
            }

            public void setShiti_id(String shiti_id) {
                this.shiti_id = shiti_id;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getDaan_teacher() {
                return daan_teacher;
            }

            public void setDaan_teacher(String daan_teacher) {
                this.daan_teacher = daan_teacher;
            }

            public String getShiti_ordernum() {
                return shiti_ordernum;
            }

            public void setShiti_ordernum(String shiti_ordernum) {
                this.shiti_ordernum = shiti_ordernum;
            }

            public String getShiti_title() {
                return shiti_title;
            }

            public void setShiti_title(String shiti_title) {
                this.shiti_title = shiti_title;
            }
        }
    }
}
