package com.kmjd.jsylc.zxh.mvp.model.bean;

/**
 * Created by androidshuai on 2018/6/29.
 *
 * 下线的时候接口返回对象
 */

public class OfflineBean {

    /**
     * c : -1
     * info : {"lastDevice":"手机版","lastLoginTime":"2018-06-29 14:49:32","loginTime":"2018-06-29 15:04:44","user":"TB1123"}
     * msg : M-01
     * tip : 您被强迫下线，您的帐户在其他位置登入！
     */

    private int c;
    private InfoBean info;
    private String msg;
    private String tip;

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public static class InfoBean {
        /**
         * lastDevice : 手机版
         * lastLoginTime : 2018-06-29 14:49:32
         * loginTime : 2018-06-29 15:04:44
         * user : TB1123
         */

        private String lastDevice;
        private String lastLoginTime;
        private String loginTime;
        private String user;

        public String getLastDevice() {
            return lastDevice;
        }

        public void setLastDevice(String lastDevice) {
            this.lastDevice = lastDevice;
        }

        public String getLastLoginTime() {
            return lastLoginTime;
        }

        public void setLastLoginTime(String lastLoginTime) {
            this.lastLoginTime = lastLoginTime;
        }

        public String getLoginTime() {
            return loginTime;
        }

        public void setLoginTime(String loginTime) {
            this.loginTime = loginTime;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "InfoBean{" +
                    "lastDevice='" + lastDevice + '\'' +
                    ", lastLoginTime='" + lastLoginTime + '\'' +
                    ", loginTime='" + loginTime + '\'' +
                    ", user='" + user + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OfflineBean{" +
                "c=" + c +
                ", info=" + info +
                ", msg='" + msg + '\'' +
                ", tip='" + tip + '\'' +
                '}';
    }
}
