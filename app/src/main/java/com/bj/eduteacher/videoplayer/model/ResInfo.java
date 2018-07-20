package com.bj.eduteacher.videoplayer.model;

/**
 * Created by Administrator on 2018/6/26 0026.
 */

public class ResInfo {

    /**
     * ret : 1
     * msg :
     * data : {"id":"71","img":"","title":"互联网时代，教师一定要有教师资格证吗？","price":"0","buystatus":"","filename":"视频","previewurl":"http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d178d28a7447398155417419141/v.f20.mp4","fileurl":"","createtime":"2018-04-10 16:24:17","updatetime":"2018-06-26 17:23:40","mastercode":"","tuijianorder":"","type":"2","viewnum":"181","comment_num":"4","dianzan":"4","pageview":"","shichang":"5:30","videoid":""}
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
         * id : 71
         * img :
         * title : 互联网时代，教师一定要有教师资格证吗？
         * price : 0
         * buystatus :
         * filename : 视频
         * previewurl : http://1253277879.vod2.myqcloud.com/ddbba89avodtransgzp1253277879/d178d28a7447398155417419141/v.f20.mp4
         * fileurl :
         * createtime : 2018-04-10 16:24:17
         * updatetime : 2018-06-26 17:23:40
         * mastercode :
         * tuijianorder :
         * type : 2
         * viewnum : 181
         * comment_num : 4
         * dianzan : 4
         * pageview :
         * shichang : 5:30
         * videoid :
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
        private String videoid;

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

        public String getVideoid() {
            return videoid;
        }

        public void setVideoid(String videoid) {
            this.videoid = videoid;
        }
    }
}
