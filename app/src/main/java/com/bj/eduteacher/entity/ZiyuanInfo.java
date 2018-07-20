package com.bj.eduteacher.entity;

/**
 * Created by Administrator on 2018/5/21 0021.
 */

public class ZiyuanInfo {

    /**
     * ret : 1
     * msg :
     * data : {"id":"2","img":"d66ae2561618929878913ccefac98ebb.JPEG","title":"快速记忆乘法口诀表","price":"0","buystatus":"","filename":"word文档","previewurl":"http://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/newdouke/shuxue/allshuxue/res/3pql.pdf","fileurl":"levelpicture/1.docx","createtime":"2017-07-24 00:38:14","updatetime":"2018-05-21 15:09:57","mastercode":"0001","tuijianorder":"2","type":"1","viewnum":"618","comment_num":"13","dianzan":"8","pageview":"","shichang":"0"}
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
         * id : 2
         * img : d66ae2561618929878913ccefac98ebb.JPEG
         * title : 快速记忆乘法口诀表
         * price : 0
         * buystatus :
         * filename : word文档
         * previewurl : http://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/newdouke/shuxue/allshuxue/res/3pql.pdf
         * fileurl : levelpicture/1.docx
         * createtime : 2017-07-24 00:38:14
         * updatetime : 2018-05-21 15:09:57
         * mastercode : 0001
         * tuijianorder : 2
         * type : 1
         * viewnum : 618
         * comment_num : 13
         * dianzan : 8
         * pageview :
         * shichang : 0
         */

        private String id;
        private String img;
        private String title;
        private String price;
        private String buystatus;
        private String filename;
        private String previewurl;
        private String fileurl;
        private String createtime;
        private String updatetime;
        private String mastercode;
        private String tuijianorder;
        private String type;
        private String viewnum;
        private String comment_num;
        private String dianzan;
        private String pageview;
        private String shichang;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getBuystatus() {
            return buystatus;
        }

        public void setBuystatus(String buystatus) {
            this.buystatus = buystatus;
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

        public String getMastercode() {
            return mastercode;
        }

        public void setMastercode(String mastercode) {
            this.mastercode = mastercode;
        }

        public String getTuijianorder() {
            return tuijianorder;
        }

        public void setTuijianorder(String tuijianorder) {
            this.tuijianorder = tuijianorder;
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

        public String getComment_num() {
            return comment_num;
        }

        public void setComment_num(String comment_num) {
            this.comment_num = comment_num;
        }

        public String getDianzan() {
            return dianzan;
        }

        public void setDianzan(String dianzan) {
            this.dianzan = dianzan;
        }

        public String getPageview() {
            return pageview;
        }

        public void setPageview(String pageview) {
            this.pageview = pageview;
        }

        public String getShichang() {
            return shichang;
        }

        public void setShichang(String shichang) {
            this.shichang = shichang;
        }
    }
}
