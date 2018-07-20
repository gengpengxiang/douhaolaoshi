package com.bj.eduteacher.answer.model;

import java.util.List;

/**
 * Created by Administrator on 2018/7/9 0009.
 */

public class Question {


    /**
     * ret : 1
     * msg :
     * data : {"shiti_info":{"id":"2","title":"第二题的题干也很长的话也是要进行换行的，显示效果是什么样子的呢？","updatetime":"2018-07-13 12:09:24","createtime":"2018-07-10 17:31:50","type":"2","fenshu":"5","daan":"2,3"},"shiti_xuanxiang":[{"id":"5","title":"第二题答案1如果非常长的话是会换行的然而换行之后的显示是怎么样的呢？","updatetime":"2018-07-12 12:00:20","createtime":"2018-07-10 17:32:55","shiti_id":"2","ordernum":"1"},{"id":"6","title":"第二题答案2","updatetime":"2018-07-11 17:02:18","createtime":"2018-07-10 17:32:55","shiti_id":"2","ordernum":"2"},{"id":"7","title":"第二题答案3","updatetime":"2018-07-11 17:02:20","createtime":"2018-07-10 17:32:55","shiti_id":"2","ordernum":"3"}]}
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
         * shiti_info : {"id":"2","title":"第二题的题干也很长的话也是要进行换行的，显示效果是什么样子的呢？","updatetime":"2018-07-13 12:09:24","createtime":"2018-07-10 17:31:50","type":"2","fenshu":"5","daan":"2,3"}
         * shiti_xuanxiang : [{"id":"5","title":"第二题答案1如果非常长的话是会换行的然而换行之后的显示是怎么样的呢？","updatetime":"2018-07-12 12:00:20","createtime":"2018-07-10 17:32:55","shiti_id":"2","ordernum":"1"},{"id":"6","title":"第二题答案2","updatetime":"2018-07-11 17:02:18","createtime":"2018-07-10 17:32:55","shiti_id":"2","ordernum":"2"},{"id":"7","title":"第二题答案3","updatetime":"2018-07-11 17:02:20","createtime":"2018-07-10 17:32:55","shiti_id":"2","ordernum":"3"}]
         */

        private ShitiInfoBean shiti_info;
        private List<ShitiXuanxiangBean> shiti_xuanxiang;

        public ShitiInfoBean getShiti_info() {
            return shiti_info;
        }

        public void setShiti_info(ShitiInfoBean shiti_info) {
            this.shiti_info = shiti_info;
        }

        public List<ShitiXuanxiangBean> getShiti_xuanxiang() {
            return shiti_xuanxiang;
        }

        public void setShiti_xuanxiang(List<ShitiXuanxiangBean> shiti_xuanxiang) {
            this.shiti_xuanxiang = shiti_xuanxiang;
        }

        public static class ShitiInfoBean {
            /**
             * id : 2
             * title : 第二题的题干也很长的话也是要进行换行的，显示效果是什么样子的呢？
             * updatetime : 2018-07-13 12:09:24
             * createtime : 2018-07-10 17:31:50
             * type : 2
             * fenshu : 5
             * daan : 2,3
             */

            private String id;
            private String title;
            private String updatetime;
            private String createtime;
            private String type;
            private String fenshu;
            private String daan;

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

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getFenshu() {
                return fenshu;
            }

            public void setFenshu(String fenshu) {
                this.fenshu = fenshu;
            }

            public String getDaan() {
                return daan;
            }

            public void setDaan(String daan) {
                this.daan = daan;
            }
        }

        public static class ShitiXuanxiangBean {
            /**
             * id : 5
             * title : 第二题答案1如果非常长的话是会换行的然而换行之后的显示是怎么样的呢？
             * updatetime : 2018-07-12 12:00:20
             * createtime : 2018-07-10 17:32:55
             * shiti_id : 2
             * ordernum : 1
             */

            private String id;
            private String title;
            private String updatetime;
            private String createtime;
            private String shiti_id;
            private String ordernum;

            public boolean isSelect;

            public boolean isSelect() {
                return isSelect;
            }

            public void setSelect(boolean isSelect) {
                this.isSelect = isSelect;
            }

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

            public String getShiti_id() {
                return shiti_id;
            }

            public void setShiti_id(String shiti_id) {
                this.shiti_id = shiti_id;
            }

            public String getOrdernum() {
                return ordernum;
            }

            public void setOrdernum(String ordernum) {
                this.ordernum = ordernum;
            }
        }
    }
}
