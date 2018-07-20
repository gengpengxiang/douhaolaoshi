package com.bj.eduteacher.prize.model;

import java.util.List;

/**
 * Created by Administrator on 2018/4/25 0025.
 */

public class Prize {

    @Override
    public String toString() {
        return "Prize{" +
                "ret='" + ret + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    /**
     * ret : 1
     * msg :
     * data : {"jiangpin_array":[{"Id":"1","type":"1","title":"st1","img":"jiangpin_anmoyi.png","code":"1","ordernum":"1","status":"1","chance":"1","content":"价值500元的按摩椅","createtime":"2018-02-05 21:21:02","updatetime":"2018-02-10 15:31:33"},{"Id":"2","type":"1","title":"st2","img":"jiangpin_tushu.png","code":"2","ordernum":"2","status":"1","chance":"2000","content":"图书：数之乐","createtime":"2018-02-05 21:21:22","updatetime":"2018-04-12 14:37:14"},{"Id":"3","type":"1","title":"st3","img":"jiangpin_shuqian.png","code":"3","ordernum":"3","status":"1","chance":"30","content":"北京大学纪念书签","createtime":"2018-02-05 21:21:57","updatetime":"2018-02-10 15:31:52"},{"Id":"4","type":"1","title":"st4","img":"jiangpin_bijiben.png","code":"4","ordernum":"4","status":"1","chance":"50","content":"北京大学纪念笔记本","createtime":"2018-02-05 21:23:25","updatetime":"2018-02-10 15:32:05"},{"Id":"5","type":"1","title":"st5","img":"jiangpin_mianbaoji.png","code":"5","ordernum":"5","status":"1","chance":"10","content":"贴心烤面包机","createtime":"2018-02-05 22:04:08","updatetime":"2018-02-10 15:32:15"},{"Id":"6","type":"2","title":"jiangpin10","img":"jiangpin_haoyunlianlian.png","code":"6","ordernum":"6","status":"1","chance":"100","content":"逗币10个","createtime":"2018-02-06 11:39:09","updatetime":"2018-04-09 18:47:05"},{"Id":"7","type":"3","title":"xx7","img":"jiangpin_haoyunlianlian.png","code":"7","ordernum":"7","status":"1","chance":"500","content":"祝您：好运连连","createtime":"2018-02-06 11:39:13","updatetime":"2018-02-10 15:32:34"},{"Id":"8","type":"0","title":"xx8","img":"jiangpin_haoyunlianlian.png","code":"8","ordernum":"8","status":"1","chance":"289","content":"谢谢参与","createtime":"2018-02-06 11:39:17","updatetime":"2018-04-09 18:46:40"}],"user_doubinum_add":"1","user_doubinum_sum":0,"user_syxianzhi_num":0,"fenxiang_getdoubi_num":"1"}
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
         * jiangpin_array : [{"Id":"1","type":"1","title":"st1","img":"jiangpin_anmoyi.png","code":"1","ordernum":"1","status":"1","chance":"1","content":"价值500元的按摩椅","createtime":"2018-02-05 21:21:02","updatetime":"2018-02-10 15:31:33"},{"Id":"2","type":"1","title":"st2","img":"jiangpin_tushu.png","code":"2","ordernum":"2","status":"1","chance":"2000","content":"图书：数之乐","createtime":"2018-02-05 21:21:22","updatetime":"2018-04-12 14:37:14"},{"Id":"3","type":"1","title":"st3","img":"jiangpin_shuqian.png","code":"3","ordernum":"3","status":"1","chance":"30","content":"北京大学纪念书签","createtime":"2018-02-05 21:21:57","updatetime":"2018-02-10 15:31:52"},{"Id":"4","type":"1","title":"st4","img":"jiangpin_bijiben.png","code":"4","ordernum":"4","status":"1","chance":"50","content":"北京大学纪念笔记本","createtime":"2018-02-05 21:23:25","updatetime":"2018-02-10 15:32:05"},{"Id":"5","type":"1","title":"st5","img":"jiangpin_mianbaoji.png","code":"5","ordernum":"5","status":"1","chance":"10","content":"贴心烤面包机","createtime":"2018-02-05 22:04:08","updatetime":"2018-02-10 15:32:15"},{"Id":"6","type":"2","title":"jiangpin10","img":"jiangpin_haoyunlianlian.png","code":"6","ordernum":"6","status":"1","chance":"100","content":"逗币10个","createtime":"2018-02-06 11:39:09","updatetime":"2018-04-09 18:47:05"},{"Id":"7","type":"3","title":"xx7","img":"jiangpin_haoyunlianlian.png","code":"7","ordernum":"7","status":"1","chance":"500","content":"祝您：好运连连","createtime":"2018-02-06 11:39:13","updatetime":"2018-02-10 15:32:34"},{"Id":"8","type":"0","title":"xx8","img":"jiangpin_haoyunlianlian.png","code":"8","ordernum":"8","status":"1","chance":"289","content":"谢谢参与","createtime":"2018-02-06 11:39:17","updatetime":"2018-04-09 18:46:40"}]
         * user_doubinum_add : 1
         * user_doubinum_sum : 0
         * user_syxianzhi_num : 0
         * fenxiang_getdoubi_num : 1
         */

        private String user_doubinum_add;
        private int user_doubinum_sum;
        private int user_syxianzhi_num;
        private String fenxiang_getdoubi_num;
        private List<JiangpinArrayBean> jiangpin_array;

        public String getUser_doubinum_add() {
            return user_doubinum_add;
        }

        public void setUser_doubinum_add(String user_doubinum_add) {
            this.user_doubinum_add = user_doubinum_add;
        }

        public int getUser_doubinum_sum() {
            return user_doubinum_sum;
        }

        public void setUser_doubinum_sum(int user_doubinum_sum) {
            this.user_doubinum_sum = user_doubinum_sum;
        }

        public int getUser_syxianzhi_num() {
            return user_syxianzhi_num;
        }

        public void setUser_syxianzhi_num(int user_syxianzhi_num) {
            this.user_syxianzhi_num = user_syxianzhi_num;
        }

        public String getFenxiang_getdoubi_num() {
            return fenxiang_getdoubi_num;
        }

        public void setFenxiang_getdoubi_num(String fenxiang_getdoubi_num) {
            this.fenxiang_getdoubi_num = fenxiang_getdoubi_num;
        }

        public List<JiangpinArrayBean> getJiangpin_array() {
            return jiangpin_array;
        }

        public void setJiangpin_array(List<JiangpinArrayBean> jiangpin_array) {
            this.jiangpin_array = jiangpin_array;
        }

        public static class JiangpinArrayBean {
            /**
             * Id : 1
             * type : 1
             * title : st1
             * img : jiangpin_anmoyi.png
             * code : 1
             * ordernum : 1
             * status : 1
             * chance : 1
             * content : 价值500元的按摩椅
             * createtime : 2018-02-05 21:21:02
             * updatetime : 2018-02-10 15:31:33
             */

            private String Id;
            private String type;
            private String title;
            private String img;
            private String code;
            private String ordernum;
            private String status;
            private String chance;
            private String content;
            private String createtime;
            private String updatetime;

            public String getId() {
                return Id;
            }

            public void setId(String Id) {
                this.Id = Id;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getOrdernum() {
                return ordernum;
            }

            public void setOrdernum(String ordernum) {
                this.ordernum = ordernum;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getChance() {
                return chance;
            }

            public void setChance(String chance) {
                this.chance = chance;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }
        }
    }
}
