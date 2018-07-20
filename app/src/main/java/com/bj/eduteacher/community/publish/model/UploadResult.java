package com.bj.eduteacher.community.publish.model;

/**
 * Created by Administrator on 2018/4/19 0019.
 */

public class UploadResult {

    /**
     * ret : 1
     * msg : 更新sucess
     * data : {"img":"c714e9067844adef4f8c6ee2f1df00b3.jpg"}
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
         * img : c714e9067844adef4f8c6ee2f1df00b3.jpg
         */

        private String img;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}
