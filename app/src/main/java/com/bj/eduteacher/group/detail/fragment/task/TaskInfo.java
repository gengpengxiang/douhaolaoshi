package com.bj.eduteacher.group.detail.fragment.task;

import java.util.List;

/**
 * Created by Administrator on 2018/6/25 0025.
 */

public class TaskInfo {

    /**
     * ret : 1
     * msg : 查询成功
     * data : {"userdata":"1","renwu":[{"renwuid":"1","teacherid":"20033","status":"","title":"互联网+时代下学校管理怎么做？","type":"1","caozuoid":"1092","jiezhitime":"2018.12.31","restype":"","videoid_ali":"","previewurl":"","fileurl":"","time":"2018-05-07 18:19:44","nicheng":"匿名","authorimg":"","authorimg_url":""},{"renwuid":"2","teacherid":"20033","status":"1","title":"游戏的内在动机理论","type":"2","caozuoid":"1","jiezhitime":"2018.12.31","restype":"1","videoid_ali":"","previewurl":"https://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/zhuanjia/shangjunjie/res/res1.pdf","fileurl":"zhuanjia/shangjunjie/res/res1.pdf","time":"","nicheng":"匿名","authorimg":"","authorimg_url":""},{"renwuid":"3","teacherid":"20033","status":"1","title":"游戏化学习的价值及未来发展趋势","type":"2","caozuoid":"2","jiezhitime":"2018.12.31","restype":"1","videoid_ali":"","previewurl":"https://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/zhuanjia/shangjunjie/res/res2.pdf","fileurl":"zhuanjia/shangjunjie/res/res2.pdf","time":"","nicheng":"匿名","authorimg":"","authorimg_url":""},{"renwuid":"4","teacherid":"20033","status":"","title":"游戏化设计工具箱","type":"2","caozuoid":"11","jiezhitime":"2018.12.31","restype":"1","videoid_ali":"","previewurl":"https://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/zhuanjia/xiaohaiming/res/gongjuxiang.pptx","fileurl":"zhuanjia/xiaohaiming/res/gongjuxiang.pptx","time":"","nicheng":"匿名","authorimg":"","authorimg_url":""},{"renwuid":"5","teacherid":"20033","status":"","title":"加密视频","type":"2","caozuoid":"541","jiezhitime":"2018.12.31","restype":"2","videoid_ali":"6bdb41c4126648bfba777055b0944181","previewurl":"","fileurl":"","time":"","nicheng":"匿名","authorimg":"","authorimg_url":""},{"renwuid":"6","teacherid":"20033","status":"","title":"北大尚俊杰教授讲解游戏化教学的概念和价值","type":"3","caozuoid":"5","jiezhitime":"2018.12.31","restype":"","videoid_ali":"","previewurl":"","fileurl":"","time":"","nicheng":"匿名","authorimg":"","authorimg_url":""}]}
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
         * userdata : 1
         * renwu : [{"renwuid":"1","teacherid":"20033","status":"","title":"互联网+时代下学校管理怎么做？","type":"1","caozuoid":"1092","jiezhitime":"2018.12.31","restype":"","videoid_ali":"","previewurl":"","fileurl":"","time":"2018-05-07 18:19:44","nicheng":"匿名","authorimg":"","authorimg_url":""},{"renwuid":"2","teacherid":"20033","status":"1","title":"游戏的内在动机理论","type":"2","caozuoid":"1","jiezhitime":"2018.12.31","restype":"1","videoid_ali":"","previewurl":"https://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/zhuanjia/shangjunjie/res/res1.pdf","fileurl":"zhuanjia/shangjunjie/res/res1.pdf","time":"","nicheng":"匿名","authorimg":"","authorimg_url":""},{"renwuid":"3","teacherid":"20033","status":"1","title":"游戏化学习的价值及未来发展趋势","type":"2","caozuoid":"2","jiezhitime":"2018.12.31","restype":"1","videoid_ali":"","previewurl":"https://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/zhuanjia/shangjunjie/res/res2.pdf","fileurl":"zhuanjia/shangjunjie/res/res2.pdf","time":"","nicheng":"匿名","authorimg":"","authorimg_url":""},{"renwuid":"4","teacherid":"20033","status":"","title":"游戏化设计工具箱","type":"2","caozuoid":"11","jiezhitime":"2018.12.31","restype":"1","videoid_ali":"","previewurl":"https://ow365.cn/?i=13214&furl=http://douhao.gamepku.com/files/zhuanjia/xiaohaiming/res/gongjuxiang.pptx","fileurl":"zhuanjia/xiaohaiming/res/gongjuxiang.pptx","time":"","nicheng":"匿名","authorimg":"","authorimg_url":""},{"renwuid":"5","teacherid":"20033","status":"","title":"加密视频","type":"2","caozuoid":"541","jiezhitime":"2018.12.31","restype":"2","videoid_ali":"6bdb41c4126648bfba777055b0944181","previewurl":"","fileurl":"","time":"","nicheng":"匿名","authorimg":"","authorimg_url":""},{"renwuid":"6","teacherid":"20033","status":"","title":"北大尚俊杰教授讲解游戏化教学的概念和价值","type":"3","caozuoid":"5","jiezhitime":"2018.12.31","restype":"","videoid_ali":"","previewurl":"","fileurl":"","time":"","nicheng":"匿名","authorimg":"","authorimg_url":""}]
         */

        private String userdata;
        private List<RenwuBean> renwu;

        public String getUserdata() {
            return userdata;
        }

        public void setUserdata(String userdata) {
            this.userdata = userdata;
        }

        public List<RenwuBean> getRenwu() {
            return renwu;
        }

        public void setRenwu(List<RenwuBean> renwu) {
            this.renwu = renwu;
        }

        public static class RenwuBean {


            /**
             * renwuid : 1
             * teacherid : 20033
             * status :
             * title : 互联网+时代下学校管理怎么做？
             * type : 1
             * caozuoid : 1092
             * jiezhitime : 2018.12.31
             * restype :
             * videoid_ali :
             * previewurl :
             * fileurl :
             * time : 2018-05-07 18:19:44
             * nicheng : 匿名
             * authorimg :
             * authorimg_url :
             */



            private String renwuid;
            private String teacherid;
            private String status;
            private String title;
            private String type;
            private String caozuoid;
            private String jiezhitime;
            private String restype;
            private String videoid_ali;
            private String previewurl;
            private String fileurl;
            private String time;
            private String nicheng;
            private String authorimg;
            private String authorimg_url;
            private String jiezhi_status;

            public String getJiezhi_status() {
                return jiezhi_status;
            }

            public void setJiezhi_status(String jiezhi_status) {
                this.jiezhi_status = jiezhi_status;
            }

            public String getRenwuid() {
                return renwuid;
            }

            public void setRenwuid(String renwuid) {
                this.renwuid = renwuid;
            }

            public String getTeacherid() {
                return teacherid;
            }

            public void setTeacherid(String teacherid) {
                this.teacherid = teacherid;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getCaozuoid() {
                return caozuoid;
            }

            public void setCaozuoid(String caozuoid) {
                this.caozuoid = caozuoid;
            }

            public String getJiezhitime() {
                return jiezhitime;
            }

            public void setJiezhitime(String jiezhitime) {
                this.jiezhitime = jiezhitime;
            }

            public String getRestype() {
                return restype;
            }

            public void setRestype(String restype) {
                this.restype = restype;
            }

            public String getVideoid_ali() {
                return videoid_ali;
            }

            public void setVideoid_ali(String videoid_ali) {
                this.videoid_ali = videoid_ali;
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

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getNicheng() {
                return nicheng;
            }

            public void setNicheng(String nicheng) {
                this.nicheng = nicheng;
            }

            public String getAuthorimg() {
                return authorimg;
            }

            public void setAuthorimg(String authorimg) {
                this.authorimg = authorimg;
            }

            public String getAuthorimg_url() {
                return authorimg_url;
            }

            public void setAuthorimg_url(String authorimg_url) {
                this.authorimg_url = authorimg_url;
            }
        }
    }
}
