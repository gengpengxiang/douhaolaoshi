package com.bj.eduteacher.group.detail.fragment.datums;

import java.util.List;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public class Datums {


    /**
     * ret : 1
     * msg : 查询成功
     * data : {"group_chengyuan":0,"group_ziliaolist":[{"id":"2","createtime":"2017-07-24 00:38:14","resid":"2","groupid":"2","img":"d66ae2561618929878913ccefac98ebb.JPEG","title":"快速记忆乘法口诀表","price":"0","buystatus":"","filename":"word文档","previewurl":"https://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/newdouke/shuxue/allshuxue/res/3pql.pdf","videoid_ali":"","videoid":"","fileurl":"levelpicture/1.docx","updatetime":"2018-07-03 18:15:18","mastercode":"0001","tuijianorder":"2","type":"1","viewnum":"2605","comment_num":"22","dianzan":"12","pageview":"","shichang":"0"},{"id":"1","createtime":"2017-07-23 23:19:12","resid":"1","groupid":"2","img":"d66ae2561618929878913ccefac98ebb.JPEG","title":"十以内加减法一一一一一一一一一一一一","price":"1","buystatus":"","filename":"ppt课件","previewurl":"","videoid_ali":"","videoid":"","fileurl":"levelpicture/1.docx","updatetime":"2018-07-06 11:44:38","mastercode":"0001","tuijianorder":"3","type":"1","viewnum":"1810","comment_num":"5","dianzan":"2","pageview":"","shichang":"0"}]}
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
         * group_chengyuan : 0
         * group_ziliaolist : [{"id":"2","createtime":"2017-07-24 00:38:14","resid":"2","groupid":"2","img":"d66ae2561618929878913ccefac98ebb.JPEG","title":"快速记忆乘法口诀表","price":"0","buystatus":"","filename":"word文档","previewurl":"https://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/newdouke/shuxue/allshuxue/res/3pql.pdf","videoid_ali":"","videoid":"","fileurl":"levelpicture/1.docx","updatetime":"2018-07-03 18:15:18","mastercode":"0001","tuijianorder":"2","type":"1","viewnum":"2605","comment_num":"22","dianzan":"12","pageview":"","shichang":"0"},{"id":"1","createtime":"2017-07-23 23:19:12","resid":"1","groupid":"2","img":"d66ae2561618929878913ccefac98ebb.JPEG","title":"十以内加减法一一一一一一一一一一一一","price":"1","buystatus":"","filename":"ppt课件","previewurl":"","videoid_ali":"","videoid":"","fileurl":"levelpicture/1.docx","updatetime":"2018-07-06 11:44:38","mastercode":"0001","tuijianorder":"3","type":"1","viewnum":"1810","comment_num":"5","dianzan":"2","pageview":"","shichang":"0"}]
         */

        private int group_chengyuan;
        private List<GroupZiliaolistBean> group_ziliaolist;

        public int getGroup_chengyuan() {
            return group_chengyuan;
        }

        public void setGroup_chengyuan(int group_chengyuan) {
            this.group_chengyuan = group_chengyuan;
        }

        public List<GroupZiliaolistBean> getGroup_ziliaolist() {
            return group_ziliaolist;
        }

        public void setGroup_ziliaolist(List<GroupZiliaolistBean> group_ziliaolist) {
            this.group_ziliaolist = group_ziliaolist;
        }

        public static class GroupZiliaolistBean {
            /**
             * id : 2
             * createtime : 2017-07-24 00:38:14
             * resid : 2
             * groupid : 2
             * img : d66ae2561618929878913ccefac98ebb.JPEG
             * title : 快速记忆乘法口诀表
             * price : 0
             * buystatus :
             * filename : word文档
             * previewurl : https://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/newdouke/shuxue/allshuxue/res/3pql.pdf
             * videoid_ali :
             * videoid :
             * fileurl : levelpicture/1.docx
             * updatetime : 2018-07-03 18:15:18
             * mastercode : 0001
             * tuijianorder : 2
             * type : 1
             * viewnum : 2605
             * comment_num : 22
             * dianzan : 12
             * pageview :
             * shichang : 0
             */

            private String id;
            private String createtime;
            private String resid;
            private String groupid;
            private String img;
            private String title;
            private String price;
            private String buystatus;
            private String filename;
            private String previewurl;
            private String videoid_ali;
            private String videoid;
            private String fileurl;
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

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public String getResid() {
                return resid;
            }

            public void setResid(String resid) {
                this.resid = resid;
            }

            public String getGroupid() {
                return groupid;
            }

            public void setGroupid(String groupid) {
                this.groupid = groupid;
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

            public String getVideoid_ali() {
                return videoid_ali;
            }

            public void setVideoid_ali(String videoid_ali) {
                this.videoid_ali = videoid_ali;
            }

            public String getVideoid() {
                return videoid;
            }

            public void setVideoid(String videoid) {
                this.videoid = videoid;
            }

            public String getFileurl() {
                return fileurl;
            }

            public void setFileurl(String fileurl) {
                this.fileurl = fileurl;
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
}
