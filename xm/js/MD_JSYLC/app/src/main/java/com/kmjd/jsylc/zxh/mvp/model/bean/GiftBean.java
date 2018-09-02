package com.kmjd.jsylc.zxh.mvp.model.bean;

/**
 * Created by androidshuai on 2017/12/7.
 */

public class GiftBean {

    /**
     * c : 0
     * msg : success
     * data : {"show":1,"birthday":1}
     */

    private int c;
    private String msg;
    private DataBean data;

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
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
         * show : 1
         * birthday : 1
         */

        private int show;
        private int birthday;

        public int getShow() {
            return show;
        }

        public void setShow(int show) {
            this.show = show;
        }

        public int getBirthday() {
            return birthday;
        }

        public void setBirthday(int birthday) {
            this.birthday = birthday;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "show=" + show +
                    ", birthday=" + birthday +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "GiftBean{" +
                "c=" + c +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
