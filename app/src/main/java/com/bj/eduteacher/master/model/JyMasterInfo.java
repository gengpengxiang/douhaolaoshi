package com.bj.eduteacher.master.model;

import java.util.List;

/**
 * Created by Administrator on 2018/6/22 0022.
 */

public class JyMasterInfo {

    /**
     * ret : 1
     * msg :
     * data : {"getmore":"0","master_data":[{"mastercode":"0001","img":"e29e5b24e90522063fa187e92bef2153.jpeg","img_pc":"e29e5b24e90522063fa187e92bef2153.jpeg","name":"逗号博士","tuijianorder":"1","newupdate":"","xuekeshengfen":"数学，北京"},{"mastercode":"0002","img":"c8b524111a43c6fc188a0115b8709f5a.JPEG","img_pc":"c8b524111a43c6fc188a0115b8709f5a.JPEG","name":"释冰已","tuijianorder":"8","newupdate":"1","xuekeshengfen":"体育，北京"}],"masternum":2}
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
         * getmore : 0
         * master_data : [{"mastercode":"0001","img":"e29e5b24e90522063fa187e92bef2153.jpeg","img_pc":"e29e5b24e90522063fa187e92bef2153.jpeg","name":"逗号博士","tuijianorder":"1","newupdate":"","xuekeshengfen":"数学，北京"},{"mastercode":"0002","img":"c8b524111a43c6fc188a0115b8709f5a.JPEG","img_pc":"c8b524111a43c6fc188a0115b8709f5a.JPEG","name":"释冰已","tuijianorder":"8","newupdate":"1","xuekeshengfen":"体育，北京"}]
         * masternum : 2
         */

        private String getmore;
        private int masternum;
        private List<MasterDataBean> master_data;

        public String getGetmore() {
            return getmore;
        }

        public void setGetmore(String getmore) {
            this.getmore = getmore;
        }

        public int getMasternum() {
            return masternum;
        }

        public void setMasternum(int masternum) {
            this.masternum = masternum;
        }

        public List<MasterDataBean> getMaster_data() {
            return master_data;
        }

        public void setMaster_data(List<MasterDataBean> master_data) {
            this.master_data = master_data;
        }

        public static class MasterDataBean {
            /**
             * mastercode : 0001
             * img : e29e5b24e90522063fa187e92bef2153.jpeg
             * img_pc : e29e5b24e90522063fa187e92bef2153.jpeg
             * name : 逗号博士
             * tuijianorder : 1
             * newupdate :
             * xuekeshengfen : 数学，北京
             */

            private String mastercode;
            private String img;
            private String img_pc;
            private String name;
            private String tuijianorder;
            private String newupdate;
            private String xuekeshengfen;

            public String getMastercode() {
                return mastercode;
            }

            public void setMastercode(String mastercode) {
                this.mastercode = mastercode;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getImg_pc() {
                return img_pc;
            }

            public void setImg_pc(String img_pc) {
                this.img_pc = img_pc;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getTuijianorder() {
                return tuijianorder;
            }

            public void setTuijianorder(String tuijianorder) {
                this.tuijianorder = tuijianorder;
            }

            public String getNewupdate() {
                return newupdate;
            }

            public void setNewupdate(String newupdate) {
                this.newupdate = newupdate;
            }

            public String getXuekeshengfen() {
                return xuekeshengfen;
            }

            public void setXuekeshengfen(String xuekeshengfen) {
                this.xuekeshengfen = xuekeshengfen;
            }
        }
    }
}
