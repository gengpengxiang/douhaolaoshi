package com.bj.eduteacher.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/6/3 0003.
 */

public class SubTopicCommentInfo {

    /**
     * ret : 1
     * msg :
     * data : {"replaynum":{"countall":"20"},"replaydata":[{"content":"5aSn54+t5pWZ5a2m56Gu5a6e5Lya5pyJ5LiA5Lqb5pON5L2c5LiK55qE5Zuw6Zq+77yM5L2G5piv5Lmf5pyJ5LiA5Lqb5pa55rOV5Y+v5Lul5L2/55So77yM5q+U5aaC5oiR5bCx5Yip55So5YiG57uE55qE5pa55byP77yM6K6p5q+P5Liq5bCP57uE6L+b6KGM57uE5YaF5ri45oiP77yM5oiW6ICF6K6p5YWo54+t5Lul5bCP57uE5Li65Y2V5L2N6L+b6KGMUEvvvIzmr4/kuKrnu4Tov5vooYzmiqLnrZTvvIzov5nmoLflnKjmiJHov5nkuKrnj63ph4zmnIk1MOWkmuS4quWtqeWtkOeahOivvuWgguS4iuacieWPr+S7peW8gOWxleOAgg==","phone":"","name":"","nicheng":"","img":"","img_url":"","tuijianorder":"1","updatetime":"2017-08-09 16:33:12"},{"content":"5oiR6K+V5LiA5LiL\n","phone":"","name":"","nicheng":"夜的第七章祥","img":"","img_url":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLdEdopjNu2c2ib0Kdib82OFqwAiayFAicDk8KBEGwRzMRrHL3WToFcf25d1svGrjbCIPeJMp0icVzVwhQ/132","tuijianorder":"0","updatetime":"2018-06-03 15:54:06"},{"content":"6YeN5aSN5pWw5o2u\n","phone":"13269969063","name":"","nicheng":"柴柴","img":"cd1aeadc465f889634685b629b5bfa30.JPEG","img_url":"http://testdouhao.gamepku.com/files/cd1aeadc465f889634685b629b5bfa30.JPEG","tuijianorder":"0","updatetime":"2018-06-03 15:45:21"},{"content":"8J+HqPCfh7Pwn4eo8J+Hs/Cfh6jwn4ez8J+QlA==\n","phone":"","name":"","nicheng":"","img":"","img_url":"","tuijianorder":"0","updatetime":"2018-06-03 09:13:16"},{"content":"6K+E6K665LiA5LiL5a2Q\n","phone":"","name":"","nicheng":"","img":"","img_url":"","tuijianorder":"0","updatetime":"2018-06-03 09:04:13"},{"content":"5LiA5Liq\n","phone":"","name":"","nicheng":"","img":"","img_url":"","tuijianorder":"0","updatetime":"2018-06-01 19:43:50"},{"content":"6aKd\n","phone":"","name":"","nicheng":"jiawei jin","img":"","img_url":"http://thirdwx.qlogo.cn/mmopen/vi_32/g3MwGKQpZXHhd5nodBT8418Qjkia7QyBtb7ebcyHFKXYIVTpyEUzofYqLggtYonw3IKqLotHkPJLMV7kYZls18g/132","tuijianorder":"0","updatetime":"2018-06-01 17:40:44"},{"content":"5ZOI5ZOI\n","phone":"","name":"","nicheng":"jiawei jin","img":"","img_url":"http://thirdwx.qlogo.cn/mmopen/vi_32/g3MwGKQpZXHhd5nodBT8418Qjkia7QyBtb7ebcyHFKXYIVTpyEUzofYqLggtYonw3IKqLotHkPJLMV7kYZls18g/132","tuijianorder":"0","updatetime":"2018-06-01 17:40:27"},{"content":"5ZOI5ZOI\n","phone":"","name":"","nicheng":"jiawei jin","img":"","img_url":"http://thirdwx.qlogo.cn/mmopen/vi_32/g3MwGKQpZXHhd5nodBT8418Qjkia7QyBtb7ebcyHFKXYIVTpyEUzofYqLggtYonw3IKqLotHkPJLMV7kYZls18g/132","tuijianorder":"0","updatetime":"2018-06-01 17:40:21"},{"content":"55qE56Gu5piv6L+Z5qC3\n","phone":"","name":"","nicheng":"","img":"","img_url":"","tuijianorder":"0","updatetime":"2018-05-22 18:31:03"}]}
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
         * replaynum : {"countall":"20"}
         * replaydata : [{"content":"5aSn54+t5pWZ5a2m56Gu5a6e5Lya5pyJ5LiA5Lqb5pON5L2c5LiK55qE5Zuw6Zq+77yM5L2G5piv5Lmf5pyJ5LiA5Lqb5pa55rOV5Y+v5Lul5L2/55So77yM5q+U5aaC5oiR5bCx5Yip55So5YiG57uE55qE5pa55byP77yM6K6p5q+P5Liq5bCP57uE6L+b6KGM57uE5YaF5ri45oiP77yM5oiW6ICF6K6p5YWo54+t5Lul5bCP57uE5Li65Y2V5L2N6L+b6KGMUEvvvIzmr4/kuKrnu4Tov5vooYzmiqLnrZTvvIzov5nmoLflnKjmiJHov5nkuKrnj63ph4zmnIk1MOWkmuS4quWtqeWtkOeahOivvuWgguS4iuacieWPr+S7peW8gOWxleOAgg==","phone":"","name":"","nicheng":"","img":"","img_url":"","tuijianorder":"1","updatetime":"2017-08-09 16:33:12"},{"content":"5oiR6K+V5LiA5LiL\n","phone":"","name":"","nicheng":"夜的第七章祥","img":"","img_url":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLdEdopjNu2c2ib0Kdib82OFqwAiayFAicDk8KBEGwRzMRrHL3WToFcf25d1svGrjbCIPeJMp0icVzVwhQ/132","tuijianorder":"0","updatetime":"2018-06-03 15:54:06"},{"content":"6YeN5aSN5pWw5o2u\n","phone":"13269969063","name":"","nicheng":"柴柴","img":"cd1aeadc465f889634685b629b5bfa30.JPEG","img_url":"http://testdouhao.gamepku.com/files/cd1aeadc465f889634685b629b5bfa30.JPEG","tuijianorder":"0","updatetime":"2018-06-03 15:45:21"},{"content":"8J+HqPCfh7Pwn4eo8J+Hs/Cfh6jwn4ez8J+QlA==\n","phone":"","name":"","nicheng":"","img":"","img_url":"","tuijianorder":"0","updatetime":"2018-06-03 09:13:16"},{"content":"6K+E6K665LiA5LiL5a2Q\n","phone":"","name":"","nicheng":"","img":"","img_url":"","tuijianorder":"0","updatetime":"2018-06-03 09:04:13"},{"content":"5LiA5Liq\n","phone":"","name":"","nicheng":"","img":"","img_url":"","tuijianorder":"0","updatetime":"2018-06-01 19:43:50"},{"content":"6aKd\n","phone":"","name":"","nicheng":"jiawei jin","img":"","img_url":"http://thirdwx.qlogo.cn/mmopen/vi_32/g3MwGKQpZXHhd5nodBT8418Qjkia7QyBtb7ebcyHFKXYIVTpyEUzofYqLggtYonw3IKqLotHkPJLMV7kYZls18g/132","tuijianorder":"0","updatetime":"2018-06-01 17:40:44"},{"content":"5ZOI5ZOI\n","phone":"","name":"","nicheng":"jiawei jin","img":"","img_url":"http://thirdwx.qlogo.cn/mmopen/vi_32/g3MwGKQpZXHhd5nodBT8418Qjkia7QyBtb7ebcyHFKXYIVTpyEUzofYqLggtYonw3IKqLotHkPJLMV7kYZls18g/132","tuijianorder":"0","updatetime":"2018-06-01 17:40:27"},{"content":"5ZOI5ZOI\n","phone":"","name":"","nicheng":"jiawei jin","img":"","img_url":"http://thirdwx.qlogo.cn/mmopen/vi_32/g3MwGKQpZXHhd5nodBT8418Qjkia7QyBtb7ebcyHFKXYIVTpyEUzofYqLggtYonw3IKqLotHkPJLMV7kYZls18g/132","tuijianorder":"0","updatetime":"2018-06-01 17:40:21"},{"content":"55qE56Gu5piv6L+Z5qC3\n","phone":"","name":"","nicheng":"","img":"","img_url":"","tuijianorder":"0","updatetime":"2018-05-22 18:31:03"}]
         */

        private ReplaynumBean replaynum;
        private List<ReplaydataBean> replaydata;

        public ReplaynumBean getReplaynum() {
            return replaynum;
        }

        public void setReplaynum(ReplaynumBean replaynum) {
            this.replaynum = replaynum;
        }

        public List<ReplaydataBean> getReplaydata() {
            return replaydata;
        }

        public void setReplaydata(List<ReplaydataBean> replaydata) {
            this.replaydata = replaydata;
        }

        public static class ReplaynumBean {
            /**
             * countall : 20
             */

            private String countall;

            public String getCountall() {
                return countall;
            }

            public void setCountall(String countall) {
                this.countall = countall;
            }
        }

        public static class ReplaydataBean {
            /**
             * content : 5aSn54+t5pWZ5a2m56Gu5a6e5Lya5pyJ5LiA5Lqb5pON5L2c5LiK55qE5Zuw6Zq+77yM5L2G5piv5Lmf5pyJ5LiA5Lqb5pa55rOV5Y+v5Lul5L2/55So77yM5q+U5aaC5oiR5bCx5Yip55So5YiG57uE55qE5pa55byP77yM6K6p5q+P5Liq5bCP57uE6L+b6KGM57uE5YaF5ri45oiP77yM5oiW6ICF6K6p5YWo54+t5Lul5bCP57uE5Li65Y2V5L2N6L+b6KGMUEvvvIzmr4/kuKrnu4Tov5vooYzmiqLnrZTvvIzov5nmoLflnKjmiJHov5nkuKrnj63ph4zmnIk1MOWkmuS4quWtqeWtkOeahOivvuWgguS4iuacieWPr+S7peW8gOWxleOAgg==
             * phone :
             * name :
             * nicheng :
             * img :
             * img_url :
             * tuijianorder : 1
             * updatetime : 2017-08-09 16:33:12
             */

            private String content;
            private String phone;
            private String name;
            private String nicheng;
            private String img;
            private String img_url;
            private String tuijianorder;
            private String updatetime;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getNicheng() {
                return nicheng;
            }

            public void setNicheng(String nicheng) {
                this.nicheng = nicheng;
            }

            public String getImg() {
                return img;
            }

            public void setImg(String img) {
                this.img = img;
            }

            public String getImg_url() {
                return img_url;
            }

            public void setImg_url(String img_url) {
                this.img_url = img_url;
            }

            public String getTuijianorder() {
                return tuijianorder;
            }

            public void setTuijianorder(String tuijianorder) {
                this.tuijianorder = tuijianorder;
            }

            public String getUpdatetime() {
                return updatetime;
            }

            public void setUpdatetime(String updatetime) {
                this.updatetime = updatetime;
            }
        }
    }
}
