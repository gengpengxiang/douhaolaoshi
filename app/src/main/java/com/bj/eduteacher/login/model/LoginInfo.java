package com.bj.eduteacher.login.model;

/**
 * Created by Administrator on 2018/5/16 0016.
 */

public class LoginInfo {

    /**
     * ret : 1
     * msg : 登录成功
     * data : {"sxbstatus":"1","sxbuser":"sxb18977777777","suixinbotitle":"博雅游戏化教学（河北）名师培养","suixinbocover":"76619c3ce34f392462f6a487a3e7a940.jpeg","zhiboquan":"1"}
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
         * sxbstatus : 1
         * sxbuser : sxb18977777777
         * suixinbotitle : 博雅游戏化教学（河北）名师培养
         * suixinbocover : 76619c3ce34f392462f6a487a3e7a940.jpeg
         * zhiboquan : 1
         */

        private String sxbstatus;
        private String sxbuser;
        private String suixinbotitle;
        private String suixinbocover;
        private String zhiboquan;

        public String getSxbstatus() {
            return sxbstatus;
        }

        public void setSxbstatus(String sxbstatus) {
            this.sxbstatus = sxbstatus;
        }

        public String getSxbuser() {
            return sxbuser;
        }

        public void setSxbuser(String sxbuser) {
            this.sxbuser = sxbuser;
        }

        public String getSuixinbotitle() {
            return suixinbotitle;
        }

        public void setSuixinbotitle(String suixinbotitle) {
            this.suixinbotitle = suixinbotitle;
        }

        public String getSuixinbocover() {
            return suixinbocover;
        }

        public void setSuixinbocover(String suixinbocover) {
            this.suixinbocover = suixinbocover;
        }

        public String getZhiboquan() {
            return zhiboquan;
        }

        public void setZhiboquan(String zhiboquan) {
            this.zhiboquan = zhiboquan;
        }
    }
}
