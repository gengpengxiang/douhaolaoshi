package com.bj.eduteacher.community.main.model;

/**
 * Created by Administrator on 2018/4/18 0018.
 */

public class Reward {

    /**
     * ret : 1
     * msg : 打赏成功
     * data : {"fromusercode":"18977777777","tousercode":"18988888888","dashang_doubinum":"1","fromuser_doubisumnum":"6","touser_doubisumnum":"48"}
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
         * fromusercode : 18977777777
         * tousercode : 18988888888
         * dashang_doubinum : 1
         * fromuser_doubisumnum : 6
         * touser_doubisumnum : 48
         */

        private String fromusercode;
        private String tousercode;
        private String dashang_doubinum;
        private String fromuser_doubisumnum;
        private String touser_doubisumnum;

        public String getFromusercode() {
            return fromusercode;
        }

        public void setFromusercode(String fromusercode) {
            this.fromusercode = fromusercode;
        }

        public String getTousercode() {
            return tousercode;
        }

        public void setTousercode(String tousercode) {
            this.tousercode = tousercode;
        }

        public String getDashang_doubinum() {
            return dashang_doubinum;
        }

        public void setDashang_doubinum(String dashang_doubinum) {
            this.dashang_doubinum = dashang_doubinum;
        }

        public String getFromuser_doubisumnum() {
            return fromuser_doubisumnum;
        }

        public void setFromuser_doubisumnum(String fromuser_doubisumnum) {
            this.fromuser_doubisumnum = fromuser_doubisumnum;
        }

        public String getTouser_doubisumnum() {
            return touser_doubisumnum;
        }

        public void setTouser_doubisumnum(String touser_doubisumnum) {
            this.touser_doubisumnum = touser_doubisumnum;
        }
    }
}
