package com.bj.eduteacher.login.model;

/**
 * Created by Administrator on 2018/5/16 0016.
 */

public class UserInfo {


    /**
     * ret : 1
     * msg :
     * data : {"teacherphone":"15612770087","teacherimg":"6a37c80e9bbba89f17e60fdec4208e09.JPEG","nicheng":"测试昵称","weixin_unionid":"on7vH0woEaF_DuXPSnc0ZAcPi7U0","teacherimg_url":"http://testdouhao.gamepku.com/files/6a37c80e9bbba89f17e60fdec4208e09.JPEG","weixin_nicheng":"夜的第七章祥","teacher_groupid":"2"}
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
         * teacherphone : 15612770087
         * teacherimg : 6a37c80e9bbba89f17e60fdec4208e09.JPEG
         * nicheng : 测试昵称
         * weixin_unionid : on7vH0woEaF_DuXPSnc0ZAcPi7U0
         * teacherimg_url : http://testdouhao.gamepku.com/files/6a37c80e9bbba89f17e60fdec4208e09.JPEG
         * weixin_nicheng : 夜的第七章祥
         * teacher_groupid : 2
         */
        private String teacherphone;
        private String teacherimg;
        private String nicheng;
        private String weixin_unionid;
        private String teacherimg_url;
        private String weixin_nicheng;
        private String teacher_groupid;

        public String getTeacherphone() {
            return teacherphone;
        }

        public void setTeacherphone(String teacherphone) {
            this.teacherphone = teacherphone;
        }

        public String getTeacherimg() {
            return teacherimg;
        }

        public void setTeacherimg(String teacherimg) {
            this.teacherimg = teacherimg;
        }

        public String getNicheng() {
            return nicheng;
        }

        public void setNicheng(String nicheng) {
            this.nicheng = nicheng;
        }

        public String getWeixin_unionid() {
            return weixin_unionid;
        }

        public void setWeixin_unionid(String weixin_unionid) {
            this.weixin_unionid = weixin_unionid;
        }

        public String getTeacherimg_url() {
            return teacherimg_url;
        }

        public void setTeacherimg_url(String teacherimg_url) {
            this.teacherimg_url = teacherimg_url;
        }

        public String getWeixin_nicheng() {
            return weixin_nicheng;
        }

        public void setWeixin_nicheng(String weixin_nicheng) {
            this.weixin_nicheng = weixin_nicheng;
        }

        public String getTeacher_groupid() {
            return teacher_groupid;
        }

        public void setTeacher_groupid(String teacher_groupid) {
            this.teacher_groupid = teacher_groupid;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "teacherphone='" + teacherphone + '\'' +
                    ", teacherimg='" + teacherimg + '\'' +
                    ", nicheng='" + nicheng + '\'' +
                    ", weixin_unionid='" + weixin_unionid + '\'' +
                    ", teacherimg_url='" + teacherimg_url + '\'' +
                    ", weixin_nicheng='" + weixin_nicheng + '\'' +
                    ", teacher_groupid='" + teacher_groupid + '\'' +
                    '}';
        }
    }
}
