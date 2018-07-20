package com.bj.eduteacher.course.fragment.discuss.comment;

import java.util.List;

/**
 * Created by Administrator on 2018/5/30 0030.
 */

public class NewCommentInfo {

    /**
     * ret : 1
     * msg :
     * data : [{"user_img":null,"user_img_url":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLdEdopjNu2c2ib0Kdib82OFqwAiayFAicDk8KBEGwRzMRrHL3WToFcf25d1svGrjbCIPeJMp0icVzVwhQ/132","user_title":"匿名***","createtime":"7分钟前","content":"5LiN6ZSZ\n","userphone":"13520031276","weixin_unionid":"on7vH0woEaF_DuXPSnc0ZAcPi7U0","comment_id":"534","nicheng":"夜的第七章祥","tmp_0":"421","tmp_1":{"id":"421","doubinum":"6","name":"","nicheng":null,"weixin_unionid":"on7vH0woEaF_DuXPSnc0ZAcPi7U0","phone":"13520031276","img":null,"weixin_nicheng":"夜的第七章祥","weixin_img":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLdEdopjNu2c2ib0Kdib82OFqwAiayFAicDk8KBEGwRzMRrHL3WToFcf25d1svGrjbCIPeJMp0icVzVwhQ/132"},"tmp_2":null}]
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
         * user_img : null
         * user_img_url : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLdEdopjNu2c2ib0Kdib82OFqwAiayFAicDk8KBEGwRzMRrHL3WToFcf25d1svGrjbCIPeJMp0icVzVwhQ/132
         * user_title : 匿名***
         * createtime : 7分钟前
         * content : 5LiN6ZSZ

         * userphone : 13520031276
         * weixin_unionid : on7vH0woEaF_DuXPSnc0ZAcPi7U0
         * comment_id : 534
         * nicheng : 夜的第七章祥
         * tmp_0 : 421
         * tmp_1 : {"id":"421","doubinum":"6","name":"","nicheng":null,"weixin_unionid":"on7vH0woEaF_DuXPSnc0ZAcPi7U0","phone":"13520031276","img":null,"weixin_nicheng":"夜的第七章祥","weixin_img":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLdEdopjNu2c2ib0Kdib82OFqwAiayFAicDk8KBEGwRzMRrHL3WToFcf25d1svGrjbCIPeJMp0icVzVwhQ/132"}
         * tmp_2 : null
         */

        private Object user_img;
        private String user_img_url;
        private String user_title;
        private String createtime;
        private String content;
        private String userphone;
        private String weixin_unionid;
        private String comment_id;
        private String nicheng;
        private String tmp_0;
        private Tmp1Bean tmp_1;
        private Object tmp_2;

        public Object getUser_img() {
            return user_img;
        }

        public void setUser_img(Object user_img) {
            this.user_img = user_img;
        }

        public String getUser_img_url() {
            return user_img_url;
        }

        public void setUser_img_url(String user_img_url) {
            this.user_img_url = user_img_url;
        }

        public String getUser_title() {
            return user_title;
        }

        public void setUser_title(String user_title) {
            this.user_title = user_title;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUserphone() {
            return userphone;
        }

        public void setUserphone(String userphone) {
            this.userphone = userphone;
        }

        public String getWeixin_unionid() {
            return weixin_unionid;
        }

        public void setWeixin_unionid(String weixin_unionid) {
            this.weixin_unionid = weixin_unionid;
        }

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getNicheng() {
            return nicheng;
        }

        public void setNicheng(String nicheng) {
            this.nicheng = nicheng;
        }

        public String getTmp_0() {
            return tmp_0;
        }

        public void setTmp_0(String tmp_0) {
            this.tmp_0 = tmp_0;
        }

        public Tmp1Bean getTmp_1() {
            return tmp_1;
        }

        public void setTmp_1(Tmp1Bean tmp_1) {
            this.tmp_1 = tmp_1;
        }

        public Object getTmp_2() {
            return tmp_2;
        }

        public void setTmp_2(Object tmp_2) {
            this.tmp_2 = tmp_2;
        }

        public static class Tmp1Bean {
            /**
             * id : 421
             * doubinum : 6
             * name :
             * nicheng : null
             * weixin_unionid : on7vH0woEaF_DuXPSnc0ZAcPi7U0
             * phone : 13520031276
             * img : null
             * weixin_nicheng : 夜的第七章祥
             * weixin_img : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLdEdopjNu2c2ib0Kdib82OFqwAiayFAicDk8KBEGwRzMRrHL3WToFcf25d1svGrjbCIPeJMp0icVzVwhQ/132
             */

            private String id;
            private String doubinum;
            private String name;
            private Object nicheng;
            private String weixin_unionid;
            private String phone;
            private Object img;
            private String weixin_nicheng;
            private String weixin_img;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getDoubinum() {
                return doubinum;
            }

            public void setDoubinum(String doubinum) {
                this.doubinum = doubinum;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Object getNicheng() {
                return nicheng;
            }

            public void setNicheng(Object nicheng) {
                this.nicheng = nicheng;
            }

            public String getWeixin_unionid() {
                return weixin_unionid;
            }

            public void setWeixin_unionid(String weixin_unionid) {
                this.weixin_unionid = weixin_unionid;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public Object getImg() {
                return img;
            }

            public void setImg(Object img) {
                this.img = img;
            }

            public String getWeixin_nicheng() {
                return weixin_nicheng;
            }

            public void setWeixin_nicheng(String weixin_nicheng) {
                this.weixin_nicheng = weixin_nicheng;
            }

            public String getWeixin_img() {
                return weixin_img;
            }

            public void setWeixin_img(String weixin_img) {
                this.weixin_img = weixin_img;
            }
        }
    }
}
