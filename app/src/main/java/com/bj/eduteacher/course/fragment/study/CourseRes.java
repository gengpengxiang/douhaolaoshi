package com.bj.eduteacher.course.fragment.study;

import java.util.List;

/**
 * Created by Administrator on 2018/5/23 0023.
 */

public class CourseRes {

    /**
     * ret : 1
     * msg : 授权微信用户
     * data : {"kc_jindu":"￥0.01 立即加入学习","before_resid":"70","before_resurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/db3880e17447398155417876859/v.f20.mp4","before_restype":"2","before_rescurrentTime":"0","kcresxxlist":[{"resid":"70","res_jindu":"","shichang":"5:30","title":"BAT进村了,互联网+教育革命悄悄打枪了","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/db3880e17447398155417876859/v.f20.mp4","fileurl":"","type":"2","viewnum":"60","ordernum":"1","currentTime":"0"},{"resid":"71","res_jindu":"","shichang":"5:30","title":"互联网时代，教师一定要有教师资格证吗？","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d178d28a7447398155417419141/v.f20.mp4","fileurl":"","type":"2","viewnum":"9","ordernum":"2","currentTime":"0"},{"resid":"72","res_jindu":"","shichang":"5:30","title":"互联网时代，教师不会失业，但是不得不转业","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d19bf8107447398155417437480/v.f20.mp4","fileurl":"","type":"2","viewnum":"6","ordernum":"3","currentTime":"0"},{"resid":"73","res_jindu":"","shichang":"5:30","title":"一场梦，想清楚幼儿园上3年，小学6年的原因","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d187322a7447398155417422840/v.f20.mp4","fileurl":"","type":"2","viewnum":"0","ordernum":"4","currentTime":"0"},{"resid":"74","res_jindu":"","shichang":"5:30","title":"为什么你没学过量子力学？因为它来晚了\u2026\u2026","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d1aa34ec7447398155417440329/v.f20.mp4","fileurl":"","type":"2","viewnum":"0","ordernum":"5","currentTime":"0"},{"resid":"75","res_jindu":"","shichang":"5:30","title":"互联网时代你要成为超级教师","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/a56bbb4d7447398155420544376/v.f20.mp4","fileurl":"","type":"2","viewnum":"1","ordernum":"6","currentTime":"0"},{"resid":"76","res_jindu":"","shichang":"5:30","title":"从MOOC谈到\u201c二中理论\u201d","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/150b43897447398155418007308/v.f20.mp4","fileurl":"","type":"2","viewnum":"0","ordernum":"7","currentTime":"0"},{"resid":"77","res_jindu":"","shichang":"5:30","title":"一切为了S.H.E.","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d3ed38467447398155417530026/v.f20.mp4","fileurl":"","type":"2","viewnum":"0","ordernum":"8","currentTime":"0"},{"resid":"78","res_jindu":"","shichang":"5:30","title":"除了上帝，任何人必须要用数据说话","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d43982df7447398155417577499/v.f20.mp4","fileurl":"","type":"2","viewnum":"0","ordernum":"9","currentTime":"0"},{"resid":"79","res_jindu":"","shichang":"5:30","title":"互联网+教育回顾初心，一个都不能少","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/17826ba47447398155418123304/v.f20.mp4","fileurl":"","type":"2","viewnum":"31","ordernum":"10","currentTime":"0"}]}
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
         * kc_jindu : ￥0.01 立即加入学习
         * before_resid : 70
         * before_resurl : http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/db3880e17447398155417876859/v.f20.mp4
         * before_restype : 2
         * before_rescurrentTime : 0
         * kcresxxlist : [{"resid":"70","res_jindu":"","shichang":"5:30","title":"BAT进村了,互联网+教育革命悄悄打枪了","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/db3880e17447398155417876859/v.f20.mp4","fileurl":"","type":"2","viewnum":"60","ordernum":"1","currentTime":"0"},{"resid":"71","res_jindu":"","shichang":"5:30","title":"互联网时代，教师一定要有教师资格证吗？","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d178d28a7447398155417419141/v.f20.mp4","fileurl":"","type":"2","viewnum":"9","ordernum":"2","currentTime":"0"},{"resid":"72","res_jindu":"","shichang":"5:30","title":"互联网时代，教师不会失业，但是不得不转业","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d19bf8107447398155417437480/v.f20.mp4","fileurl":"","type":"2","viewnum":"6","ordernum":"3","currentTime":"0"},{"resid":"73","res_jindu":"","shichang":"5:30","title":"一场梦，想清楚幼儿园上3年，小学6年的原因","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d187322a7447398155417422840/v.f20.mp4","fileurl":"","type":"2","viewnum":"0","ordernum":"4","currentTime":"0"},{"resid":"74","res_jindu":"","shichang":"5:30","title":"为什么你没学过量子力学？因为它来晚了\u2026\u2026","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d1aa34ec7447398155417440329/v.f20.mp4","fileurl":"","type":"2","viewnum":"0","ordernum":"5","currentTime":"0"},{"resid":"75","res_jindu":"","shichang":"5:30","title":"互联网时代你要成为超级教师","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/a56bbb4d7447398155420544376/v.f20.mp4","fileurl":"","type":"2","viewnum":"1","ordernum":"6","currentTime":"0"},{"resid":"76","res_jindu":"","shichang":"5:30","title":"从MOOC谈到\u201c二中理论\u201d","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/150b43897447398155418007308/v.f20.mp4","fileurl":"","type":"2","viewnum":"0","ordernum":"7","currentTime":"0"},{"resid":"77","res_jindu":"","shichang":"5:30","title":"一切为了S.H.E.","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d3ed38467447398155417530026/v.f20.mp4","fileurl":"","type":"2","viewnum":"0","ordernum":"8","currentTime":"0"},{"resid":"78","res_jindu":"","shichang":"5:30","title":"除了上帝，任何人必须要用数据说话","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d43982df7447398155417577499/v.f20.mp4","fileurl":"","type":"2","viewnum":"0","ordernum":"9","currentTime":"0"},{"resid":"79","res_jindu":"","shichang":"5:30","title":"互联网+教育回顾初心，一个都不能少","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/17826ba47447398155418123304/v.f20.mp4","fileurl":"","type":"2","viewnum":"31","ordernum":"10","currentTime":"0"}]
         */

        private String kc_jindu;
        private String before_resid;
        private String before_resurl;
        private String before_restype;
        private String before_rescurrentTime;
        private List<KcresxxlistBean> kcresxxlist;

        public String getKc_jindu() {
            return kc_jindu;
        }

        public void setKc_jindu(String kc_jindu) {
            this.kc_jindu = kc_jindu;
        }

        public String getBefore_resid() {
            return before_resid;
        }

        public void setBefore_resid(String before_resid) {
            this.before_resid = before_resid;
        }

        public String getBefore_resurl() {
            return before_resurl;
        }

        public void setBefore_resurl(String before_resurl) {
            this.before_resurl = before_resurl;
        }

        public String getBefore_restype() {
            return before_restype;
        }

        public void setBefore_restype(String before_restype) {
            this.before_restype = before_restype;
        }

        public String getBefore_rescurrentTime() {
            return before_rescurrentTime;
        }

        public void setBefore_rescurrentTime(String before_rescurrentTime) {
            this.before_rescurrentTime = before_rescurrentTime;
        }

        public List<KcresxxlistBean> getKcresxxlist() {
            return kcresxxlist;
        }

        public void setKcresxxlist(List<KcresxxlistBean> kcresxxlist) {
            this.kcresxxlist = kcresxxlist;
        }

        public static class KcresxxlistBean {
            /**
             * resid : 70
             * res_jindu :
             * shichang : 5:30
             * title : BAT进村了,互联网+教育革命悄悄打枪了
             * filename : 视频
             * previewurl : http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/db3880e17447398155417876859/v.f20.mp4
             * fileurl :
             * type : 2
             * viewnum : 60
             * ordernum : 1
             * currentTime : 0
             * "before_res_status": 0
             */

            public static final int FIRST_TYPE = 1;//第一条
            public static final int SECOND_TYPE = 2;//未解锁
            public static final int THIRD_TYPE = 3;//解锁
            public static final int SELECTED_TYPE = 4;//选中

            //添加类型变量
            public int myType;

            private String resid;
            private String res_jindu;
            private String shichang;
            private String title;
            private String filename;
            private String previewurl;
            private String fileurl;
            private String type;
            private String viewnum;
            private String ordernum;
            private String currentTime;
            private int before_res_status;

            public int getBefore_res_status() {
                return before_res_status;
            }

            public void setBefore_res_status(int before_res_status) {
                this.before_res_status = before_res_status;
            }

            public int getMyType() {
                return myType;
            }

            public void setMyType(int myType) {
                this.myType = myType;
            }

            public String getResid() {
                return resid;
            }

            public void setResid(String resid) {
                this.resid = resid;
            }

            public String getRes_jindu() {
                return res_jindu;
            }

            public void setRes_jindu(String res_jindu) {
                this.res_jindu = res_jindu;
            }

            public String getShichang() {
                return shichang;
            }

            public void setShichang(String shichang) {
                this.shichang = shichang;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getFilename() {
                return filename;
            }

            public void setFilename(String filename) {
                this.filename = filename;
            }

            public String getPreviewurl() {
                return previewurl;
            }

            public void setPreviewurl(String previewurl) {
                this.previewurl = previewurl;
            }

            public String getFileurl() {
                return fileurl;
            }

            public void setFileurl(String fileurl) {
                this.fileurl = fileurl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getViewnum() {
                return viewnum;
            }

            public void setViewnum(String viewnum) {
                this.viewnum = viewnum;
            }

            public String getOrdernum() {
                return ordernum;
            }

            public void setOrdernum(String ordernum) {
                this.ordernum = ordernum;
            }

            public String getCurrentTime() {
                return currentTime;
            }

            public void setCurrentTime(String currentTime) {
                this.currentTime = currentTime;
            }
        }
    }
}
