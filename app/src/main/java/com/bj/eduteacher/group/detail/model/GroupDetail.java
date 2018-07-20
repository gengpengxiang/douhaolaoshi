package com.bj.eduteacher.group.detail.model;

/**
 * Created by Administrator on 2018/5/4 0004.
 */

public class GroupDetail {


    /**
     * ret : 1
     * msg : 查询成功
     * data : {"group_chengyuan_num":6,"group_huati_num":4,"group_chengyuan":1,"group_qiandao":0,"group_qiandao_days":0,"group_info":{"id":"5","updatetime":"2018-05-14 16:22:31","name":"游戏化学习课程组","logo":"glogo0.jpg","jianjie":"一切为了提升老师生活质量","yaoqingma":"004","createtime":"2018-03-07 10:18:49","ht_zl_updatetime":"2018-05-14 16:22:31","moren":"0","kechengid":"1","bgimg":"gbgimg1.jpg","renwu_show":"0"}}
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
         * group_chengyuan_num : 6
         * group_huati_num : 4
         * group_chengyuan : 1
         * group_qiandao : 0
         * group_qiandao_days : 0
         * group_info : {"id":"5","updatetime":"2018-05-14 16:22:31","name":"游戏化学习课程组","logo":"glogo0.jpg","jianjie":"一切为了提升老师生活质量","yaoqingma":"004","createtime":"2018-03-07 10:18:49","ht_zl_updatetime":"2018-05-14 16:22:31","moren":"0","kechengid":"1","bgimg":"gbgimg1.jpg","renwu_show":"0"}
         */

        private int group_chengyuan_num;
        private int group_huati_num;
        private int group_chengyuan;
        private int group_qiandao;
        private int group_qiandao_days;
        private GroupInfoBean group_info;

        public int getGroup_chengyuan_num() {
            return group_chengyuan_num;
        }

        public void setGroup_chengyuan_num(int group_chengyuan_num) {
            this.group_chengyuan_num = group_chengyuan_num;
        }

        public int getGroup_huati_num() {
            return group_huati_num;
        }

        public void setGroup_huati_num(int group_huati_num) {
            this.group_huati_num = group_huati_num;
        }

        public int getGroup_chengyuan() {
            return group_chengyuan;
        }

        public void setGroup_chengyuan(int group_chengyuan) {
            this.group_chengyuan = group_chengyuan;
        }

        public int getGroup_qiandao() {
            return group_qiandao;
        }

        public void setGroup_qiandao(int group_qiandao) {
            this.group_qiandao = group_qiandao;
        }

        public int getGroup_qiandao_days() {
            return group_qiandao_days;
        }

        public void setGroup_qiandao_days(int group_qiandao_days) {
            this.group_qiandao_days = group_qiandao_days;
        }

        public GroupInfoBean getGroup_info() {
            return group_info;
        }

        public void setGroup_info(GroupInfoBean group_info) {
            this.group_info = group_info;
        }

        public static class GroupInfoBean {
            /**
             * id : 5
             * updatetime : 2018-05-14 16:22:31
             * name : 游戏化学习课程组
             * logo : glogo0.jpg
             * jianjie : 一切为了提升老师生活质量
             * yaoqingma : 004
             * createtime : 2018-03-07 10:18:49
             * ht_zl_updatetime : 2018-05-14 16:22:31
             * moren : 0
             * kechengid : 1
             * bgimg : gbgimg1.jpg
             * renwu_show : 0
             */

            private String id;
            private String updatetime;
            private String name;
            private String logo;
            private String jianjie;
            private String yaoqingma;
            private String createtime;
            private String ht_zl_updatetime;
            private String moren;
            private String kechengid;
            private String bgimg;
            private String renwu_show;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getLogo() {
                return logo;
            }

            public void setLogo(String logo) {
                this.logo = logo;
            }

            public String getJianjie() {
                return jianjie;
            }

            public void setJianjie(String jianjie) {
                this.jianjie = jianjie;
            }

            public String getYaoqingma() {
                return yaoqingma;
            }

            public void setYaoqingma(String yaoqingma) {
                this.yaoqingma = yaoqingma;
            }

            public String getCreatetime() {
                return createtime;
            }

            public void setCreatetime(String createtime) {
                this.createtime = createtime;
            }

            public String getHt_zl_updatetime() {
                return ht_zl_updatetime;
            }

            public void setHt_zl_updatetime(String ht_zl_updatetime) {
                this.ht_zl_updatetime = ht_zl_updatetime;
            }

            public String getMoren() {
                return moren;
            }

            public void setMoren(String moren) {
                this.moren = moren;
            }

            public String getKechengid() {
                return kechengid;
            }

            public void setKechengid(String kechengid) {
                this.kechengid = kechengid;
            }

            public String getBgimg() {
                return bgimg;
            }

            public void setBgimg(String bgimg) {
                this.bgimg = bgimg;
            }

            public String getRenwu_show() {
                return renwu_show;
            }

            public void setRenwu_show(String renwu_show) {
                this.renwu_show = renwu_show;
            }
        }
    }
}
