package com.bj.eduteacher.entity;

/**
 * Created by Administrator on 2018/6/10 0010.
 */

public class GroupId {

    /**
     * ret : 0
     * msg : 参数错误
     * data : {"groupid_moren":2}
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
         * groupid_moren : 2
         */

        private int groupid_moren;

        public int getGroupid_moren() {
            return groupid_moren;
        }

        public void setGroupid_moren(int groupid_moren) {
            this.groupid_moren = groupid_moren;
        }
    }
}
