package com.bj.eduteacher.school.list.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/6/14 0014.
 */

public class School {

    /**
     * ret : 1
     * msg :
     * data : [{"id":"1","name":"北京市顺义区西辛小学","updatetime":"","schoolcode":"0101","quyu":"1","schoolimg":"school/schoolimg0101.png","huiyuan":"","baogaopay":"1","baogaoopen":"1","baogaoprice":"","baogaopayios":"300","cpidios":"DHJY_1","tuijian":"0","shengfen":"北京","xiaozhangid":"19","jianjie":"北京市顺义区西辛小学位于顺义区西辛小区，顺西路12号，始建于1996年9月，六年一贯制小学，属于区教 委直属单位。","shizi":"2006年，西辛小学学校开设教学班31个。教职工总数90名，专任教师70名，其中小中高专业技术职务1名， 小学高级职务57名，市级骨干教师4名，区学科带头人2人，园丁新星3人，区骨干教师11人。学校占地面积14470 平方米，建筑面积5548平方米。体育场面积6000平方米。","linian":"面对教育改革的新机遇、新挑战，学校坚持\u201c和谐发展，持续创优\u201d的管理思路，坚持\u201c科研兴校、质量立校、 文化铸校、和谐荣校\u201d的基本策略，为实现\u201c理念先进、质量上乘、环境优雅、特色鲜明，具有名牌学科和名牌教 师，家长满意度高、社会认可度强的区级示范性、开放性一流学校\u201d的目标开拓进取，努力奋斗。","xiaozhang_img":"","xiaozhang_name":"","xiaozhang_jianjie":"","pc":"0","xiaochengxu":"0","aphone":"1","iphone":"0"}]
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

    public static class DataBean implements Serializable {

        /**
         * id : 1
         * name : 北京市顺义区西辛小学
         * updatetime :
         * schoolcode : 0101
         * quyu : 1
         * schoolimg : school/schoolimg0101.png
         * huiyuan :
         * baogaopay : 1
         * baogaoopen : 1
         * baogaoprice :
         * baogaopayios : 300
         * cpidios : DHJY_1
         * tuijian : 0
         * shengfen : 北京
         * xiaozhangid : 19
         * jianjie : 北京市顺义区西辛小学位于顺义区西辛小区，顺西路12号，始建于1996年9月，六年一贯制小学，属于区教 委直属单位。
         * shizi : 2006年，西辛小学学校开设教学班31个。教职工总数90名，专任教师70名，其中小中高专业技术职务1名， 小学高级职务57名，市级骨干教师4名，区学科带头人2人，园丁新星3人，区骨干教师11人。学校占地面积14470 平方米，建筑面积5548平方米。体育场面积6000平方米。
         * linian : 面对教育改革的新机遇、新挑战，学校坚持“和谐发展，持续创优”的管理思路，坚持“科研兴校、质量立校、 文化铸校、和谐荣校”的基本策略，为实现“理念先进、质量上乘、环境优雅、特色鲜明，具有名牌学科和名牌教 师，家长满意度高、社会认可度强的区级示范性、开放性一流学校”的目标开拓进取，努力奋斗。
         * xiaozhang_img :
         * xiaozhang_name :
         * xiaozhang_jianjie :
         * pc : 0
         * xiaochengxu : 0
         * aphone : 1
         * iphone : 0
         */

        private String id;
        private String name;
        private String updatetime;
        private String schoolcode;
        private String quyu;
        private String schoolimg;
        private String huiyuan;
        private String baogaopay;
        private String baogaoopen;
        private String baogaoprice;
        private String baogaopayios;
        private String cpidios;
        private String tuijian;
        private String shengfen;
        private String xiaozhangid;
        private String jianjie;
        private String shizi;
        private String linian;
        private String xiaozhang_img;
        private String xiaozhang_name;
        private String xiaozhang_jianjie;
        private String pc;
        private String xiaochengxu;
        private String aphone;
        private String iphone;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public String getSchoolcode() {
            return schoolcode;
        }

        public void setSchoolcode(String schoolcode) {
            this.schoolcode = schoolcode;
        }

        public String getQuyu() {
            return quyu;
        }

        public void setQuyu(String quyu) {
            this.quyu = quyu;
        }

        public String getSchoolimg() {
            return schoolimg;
        }

        public void setSchoolimg(String schoolimg) {
            this.schoolimg = schoolimg;
        }

        public String getHuiyuan() {
            return huiyuan;
        }

        public void setHuiyuan(String huiyuan) {
            this.huiyuan = huiyuan;
        }

        public String getBaogaopay() {
            return baogaopay;
        }

        public void setBaogaopay(String baogaopay) {
            this.baogaopay = baogaopay;
        }

        public String getBaogaoopen() {
            return baogaoopen;
        }

        public void setBaogaoopen(String baogaoopen) {
            this.baogaoopen = baogaoopen;
        }

        public String getBaogaoprice() {
            return baogaoprice;
        }

        public void setBaogaoprice(String baogaoprice) {
            this.baogaoprice = baogaoprice;
        }

        public String getBaogaopayios() {
            return baogaopayios;
        }

        public void setBaogaopayios(String baogaopayios) {
            this.baogaopayios = baogaopayios;
        }

        public String getCpidios() {
            return cpidios;
        }

        public void setCpidios(String cpidios) {
            this.cpidios = cpidios;
        }

        public String getTuijian() {
            return tuijian;
        }

        public void setTuijian(String tuijian) {
            this.tuijian = tuijian;
        }

        public String getShengfen() {
            return shengfen;
        }

        public void setShengfen(String shengfen) {
            this.shengfen = shengfen;
        }

        public String getXiaozhangid() {
            return xiaozhangid;
        }

        public void setXiaozhangid(String xiaozhangid) {
            this.xiaozhangid = xiaozhangid;
        }

        public String getJianjie() {
            return jianjie;
        }

        public void setJianjie(String jianjie) {
            this.jianjie = jianjie;
        }

        public String getShizi() {
            return shizi;
        }

        public void setShizi(String shizi) {
            this.shizi = shizi;
        }

        public String getLinian() {
            return linian;
        }

        public void setLinian(String linian) {
            this.linian = linian;
        }

        public String getXiaozhang_img() {
            return xiaozhang_img;
        }

        public void setXiaozhang_img(String xiaozhang_img) {
            this.xiaozhang_img = xiaozhang_img;
        }

        public String getXiaozhang_name() {
            return xiaozhang_name;
        }

        public void setXiaozhang_name(String xiaozhang_name) {
            this.xiaozhang_name = xiaozhang_name;
        }

        public String getXiaozhang_jianjie() {
            return xiaozhang_jianjie;
        }

        public void setXiaozhang_jianjie(String xiaozhang_jianjie) {
            this.xiaozhang_jianjie = xiaozhang_jianjie;
        }

        public String getPc() {
            return pc;
        }

        public void setPc(String pc) {
            this.pc = pc;
        }

        public String getXiaochengxu() {
            return xiaochengxu;
        }

        public void setXiaochengxu(String xiaochengxu) {
            this.xiaochengxu = xiaochengxu;
        }

        public String getAphone() {
            return aphone;
        }

        public void setAphone(String aphone) {
            this.aphone = aphone;
        }

        public String getIphone() {
            return iphone;
        }

        public void setIphone(String iphone) {
            this.iphone = iphone;
        }
    }
}
