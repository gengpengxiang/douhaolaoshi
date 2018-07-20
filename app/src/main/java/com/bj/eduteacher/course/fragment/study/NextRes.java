package com.bj.eduteacher.course.fragment.study;

/**
 * Created by Administrator on 2018/5/31 0031.
 */

public class NextRes {


    /**
     * ret : 1
     * msg : 查询成功
     * data : {"next_resid":"72","next_currentTime":"0","next_previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d19bf8107447398155417437480/v.f20.mp4"}
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
         * next_resid : 72
         * next_currentTime : 0
         * next_previewurl : http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d19bf8107447398155417437480/v.f20.mp4
         */

        private String next_resid;
        private String next_currentTime;
        private String next_previewurl;

        public String getNext_resid() {
            return next_resid;
        }

        public void setNext_resid(String next_resid) {
            this.next_resid = next_resid;
        }

        public String getNext_currentTime() {
            return next_currentTime;
        }

        public void setNext_currentTime(String next_currentTime) {
            this.next_currentTime = next_currentTime;
        }

        public String getNext_previewurl() {
            return next_previewurl;
        }

        public void setNext_previewurl(String next_previewurl) {
            this.next_previewurl = next_previewurl;
        }
    }
}
