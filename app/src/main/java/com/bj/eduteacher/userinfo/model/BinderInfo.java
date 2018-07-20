package com.bj.eduteacher.userinfo.model;

/**
 * Created by Administrator on 2018/5/19 0019.
 */

public class BinderInfo {

    /**
     * ret : 1
     * msg :
     * data : {"unionid":"on7vH09HjjAnt_cGGw1J8U_BaRZc","phone":"13520031276","laiyuan":"weixin"}
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
         * unionid : on7vH09HjjAnt_cGGw1J8U_BaRZc
         * phone : 13520031276
         * laiyuan : weixin
         */

        private String unionid;
        private String phone;
        private String laiyuan;

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getLaiyuan() {
            return laiyuan;
        }

        public void setLaiyuan(String laiyuan) {
            this.laiyuan = laiyuan;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "unionid='" + unionid + '\'' +
                    ", phone='" + phone + '\'' +
                    ", laiyuan='" + laiyuan + '\'' +
                    '}';
        }
    }
}
