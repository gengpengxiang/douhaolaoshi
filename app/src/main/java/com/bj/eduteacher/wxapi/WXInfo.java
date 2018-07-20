package com.bj.eduteacher.wxapi;

/**
 * Created by Administrator on 2018/5/16 0016.
 */

public class WXInfo {

    /**
     * ret : 2
     * msg : 微信请求错误
     * data : {"appid":"wx7e56305cbb24d576","secret":"04f6981e2893af98e5748a10ca94a6e5"}
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
         * appid : wx7e56305cbb24d576
         * secret : 04f6981e2893af98e5748a10ca94a6e5
         */

        private String appid;
        private String secret;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
