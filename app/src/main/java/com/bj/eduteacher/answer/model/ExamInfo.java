package com.bj.eduteacher.answer.model;

/**
 * Created by Administrator on 2018/7/11 0011.
 */

public class ExamInfo {

    /**
     * ret : 1
     * msg :
     * data : {"id":"1","title":"第一周测验","updatetime":"2018-07-10 16:08:12","createtime":"2018-07-10 16:08:12","content":"共有1个单选题，2个多选题。","shiti_num":"0"}
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
         * id : 1
         * title : 第一周测验
         * updatetime : 2018-07-10 16:08:12
         * createtime : 2018-07-10 16:08:12
         * content : 共有1个单选题，2个多选题。
         * shiti_num : 0
         */

        private String id;
        private String title;
        private String updatetime;
        private String createtime;
        private String content;
        private String shiti_num;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
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

        public String getShiti_num() {
            return shiti_num;
        }

        public void setShiti_num(String shiti_num) {
            this.shiti_num = shiti_num;
        }
    }
}
