package com.kmjd.jsylc.zxh.mvp.model.bean;

import java.util.List;

/**
 * Created by androidshuai on 2017/11/28.
 */

public class RollBean {

    private int bbs;

    private List<ImgsBean> imgs;

    public int getBbs() {
        return bbs;
    }

    public void setBbs(int bbs) {
        this.bbs = bbs;
    }

    public List<ImgsBean> getImgs() {
        return imgs;
    }

    public void setImgs(List<ImgsBean> imgs) {
        this.imgs = imgs;
    }

    public static class ImgsBean {
        /**
         * img : https://ju111.net/pic/youhui1.jpg
         * href :
         * tip : 请先加入正式会员或登入会员帐号
         * target : 1
         */

        private String img;
        private String href;
        private String tip;
        private int target;

        public ImgsBean(String img, String href, String tip, int target) {
            this.img = img;
            this.href = href;
            this.tip = tip;
            this.target = target;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getTip() {
            return tip;
        }

        public void setTip(String tip) {
            this.tip = tip;
        }

        public int getTarget() {
            return target;
        }

        public void setTarget(int target) {
            this.target = target;
        }

        @Override
        public String toString() {
            return "ImgsBean{" +
                    "img='" + img + '\'' +
                    ", href='" + href + '\'' +
                    ", tip='" + tip + '\'' +
                    ", target=" + target +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RollBean{" +
                "bbs=" + bbs +
                ", imgs=" + imgs +
                '}';
    }
}
