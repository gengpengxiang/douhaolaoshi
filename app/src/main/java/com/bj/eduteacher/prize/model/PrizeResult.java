package com.bj.eduteacher.prize.model;

import java.util.List;

/**
 * Created by Administrator on 2018/4/25 0025.
 */

public class PrizeResult {

    /**
     * ret : 1
     * msg : 抽奖成功
     * data : {"choujianglogid":555,"usercode":"18977777777","weixin_unionid":"","get_randnum":1245,"qujian_from":2,"qujian_to":2002,"get_price":"st2","get_price_code":"2","get_price_content":"图书：数之乐","doubinum_add":1,"user_doubinum_sum":"154","user_sychoujiang_num":498,"user_doubinum_sum_jiangpin":0,"jiangpins_qujian_array":[],"get_price_type":"1"}
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
         * choujianglogid : 555
         * usercode : 18977777777
         * weixin_unionid :
         * get_randnum : 1245
         * qujian_from : 2
         * qujian_to : 2002
         * get_price : st2
         * get_price_code : 2
         * get_price_content : 图书：数之乐
         * doubinum_add : 1
         * user_doubinum_sum : 154
         * user_sychoujiang_num : 498
         * user_doubinum_sum_jiangpin : 0
         * jiangpins_qujian_array : []
         * get_price_type : 1
         */

        private int choujianglogid;
        private String usercode;
        private String weixin_unionid;
        private int get_randnum;
        private int qujian_from;
        private int qujian_to;
        private String get_price;
        private String get_price_code;
        private String get_price_content;
        private int doubinum_add;
        private String user_doubinum_sum;
        private int user_sychoujiang_num;
        private int user_doubinum_sum_jiangpin;
        private String get_price_type;
        private List<?> jiangpins_qujian_array;

        public int getChoujianglogid() {
            return choujianglogid;
        }

        public void setChoujianglogid(int choujianglogid) {
            this.choujianglogid = choujianglogid;
        }

        public String getUsercode() {
            return usercode;
        }

        public void setUsercode(String usercode) {
            this.usercode = usercode;
        }

        public String getWeixin_unionid() {
            return weixin_unionid;
        }

        public void setWeixin_unionid(String weixin_unionid) {
            this.weixin_unionid = weixin_unionid;
        }

        public int getGet_randnum() {
            return get_randnum;
        }

        public void setGet_randnum(int get_randnum) {
            this.get_randnum = get_randnum;
        }

        public int getQujian_from() {
            return qujian_from;
        }

        public void setQujian_from(int qujian_from) {
            this.qujian_from = qujian_from;
        }

        public int getQujian_to() {
            return qujian_to;
        }

        public void setQujian_to(int qujian_to) {
            this.qujian_to = qujian_to;
        }

        public String getGet_price() {
            return get_price;
        }

        public void setGet_price(String get_price) {
            this.get_price = get_price;
        }

        public String getGet_price_code() {
            return get_price_code;
        }

        public void setGet_price_code(String get_price_code) {
            this.get_price_code = get_price_code;
        }

        public String getGet_price_content() {
            return get_price_content;
        }

        public void setGet_price_content(String get_price_content) {
            this.get_price_content = get_price_content;
        }

        public int getDoubinum_add() {
            return doubinum_add;
        }

        public void setDoubinum_add(int doubinum_add) {
            this.doubinum_add = doubinum_add;
        }

        public String getUser_doubinum_sum() {
            return user_doubinum_sum;
        }

        public void setUser_doubinum_sum(String user_doubinum_sum) {
            this.user_doubinum_sum = user_doubinum_sum;
        }

        public int getUser_sychoujiang_num() {
            return user_sychoujiang_num;
        }

        public void setUser_sychoujiang_num(int user_sychoujiang_num) {
            this.user_sychoujiang_num = user_sychoujiang_num;
        }

        public int getUser_doubinum_sum_jiangpin() {
            return user_doubinum_sum_jiangpin;
        }

        public void setUser_doubinum_sum_jiangpin(int user_doubinum_sum_jiangpin) {
            this.user_doubinum_sum_jiangpin = user_doubinum_sum_jiangpin;
        }

        public String getGet_price_type() {
            return get_price_type;
        }

        public void setGet_price_type(String get_price_type) {
            this.get_price_type = get_price_type;
        }

        public List<?> getJiangpins_qujian_array() {
            return jiangpins_qujian_array;
        }

        public void setJiangpins_qujian_array(List<?> jiangpins_qujian_array) {
            this.jiangpins_qujian_array = jiangpins_qujian_array;
        }
    }
}
