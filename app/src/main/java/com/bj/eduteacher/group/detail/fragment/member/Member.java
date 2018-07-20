package com.bj.eduteacher.group.detail.fragment.member;

import java.util.List;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public class Member {


    /**
     * ret : 1
     * msg : 查询成功
     * data : [{"groupid":"3","phone":"","name":"","nicheng":"","img":"","img_url":""},{"groupid":"3","phone":"","name":"","nicheng":"","img":"","img_url":""}]
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
         * groupid : 3
         * phone :
         * name :
         * nicheng :
         * img :
         * img_url :
         */

        private String groupid;
        private String phone;
        private String name;
        private String nicheng;
        private String img;
        private String img_url;

        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNicheng() {
            return nicheng;
        }

        public void setNicheng(String nicheng) {
            this.nicheng = nicheng;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }
    }
}
