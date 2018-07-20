package com.bj.eduteacher.integral.model;

/**
 * Created by Administrator on 2018/4/22 0022.
 */

public class Doubi {

    /**
     * ret : 1
     * msg : 积分获取成功
     * data : {"user_doubinum_add":"3","user_doubinum_sum":"103","user_syxianzhi_num":"4"}
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
         * user_doubinum_add : 3
         * user_doubinum_sum : 103
         * user_syxianzhi_num : 4
         */

        private String user_doubinum_add;
        private String user_doubinum_sum;
        private String user_syxianzhi_num;

        public String getUser_doubinum_add() {
            return user_doubinum_add;
        }

        public void setUser_doubinum_add(String user_doubinum_add) {
            this.user_doubinum_add = user_doubinum_add;
        }

        public String getUser_doubinum_sum() {
            return user_doubinum_sum;
        }

        public void setUser_doubinum_sum(String user_doubinum_sum) {
            this.user_doubinum_sum = user_doubinum_sum;
        }

        public String getUser_syxianzhi_num() {
            return user_syxianzhi_num;
        }

        public void setUser_syxianzhi_num(String user_syxianzhi_num) {
            this.user_syxianzhi_num = user_syxianzhi_num;
        }
    }
}
