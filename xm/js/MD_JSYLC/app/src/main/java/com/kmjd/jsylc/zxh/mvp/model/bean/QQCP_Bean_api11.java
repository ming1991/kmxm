package com.kmjd.jsylc.zxh.mvp.model.bean;

/**
 * Created by Android-d on 2017/12/6.
 */

public class QQCP_Bean_api11 {
    /**
     * c : -1
     * info : {"lastDevice":"手机版","lastLoginTime":"2018-06-29 13:45:23","loginTime":"2018-06-29 13:57:23","user":"TB1123"}
     * msg : M-01
     * tip : 您被强迫下线，您的帐户在其他位置登入！
     */

    private int c;
    private InfoBean info;
    private String msg;
    private String tip;
    private String id;
    private int sta;
    private String amt;
    private String mamt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSta() {
        return sta;
    }

    public void setSta(int sta) {
        this.sta = sta;
    }

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    public String getMamt() {
        return mamt;
    }

    public void setMamt(String mamt) {
        this.mamt = mamt;
    }

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
         * lastLoginTime : 2018-06-29 13:45:23
         * loginTime : 2018-06-29 13:57:23
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
        return "QQCP_Bean_api11{" +
                "c=" + c +
                ", info=" + info +
                ", msg='" + msg + '\'' +
                ", tip='" + tip + '\'' +
                ", id='" + id + '\'' +
                ", sta=" + sta +
                ", amt='" + amt + '\'' +
                ", mamt='" + mamt + '\'' +
                '}';
    }

    //    /**
//     * id : g1
//     * sta : 0
//     * amt : 0
//     * mamt : 0
//     * tip :
//     */
//
//    private String id;
//    private int sta;
//    private String amt;
//    private String mamt;
//    private String tip;
//
//    public String getId() {
//        return id;
//    }
//
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public int getSta() {
//        return sta;
//    }
//
//    public void setSta(int sta) {
//        this.sta = sta;
//    }
//
//    public String getAmt() {
//        return amt;
//    }
//
//    public void setAmt(String amt) {
//        this.amt = amt;
//    }
//
//    public String getMamt() {
//        return mamt;
//    }
//
//    public void setMamt(String mamt) {
//        this.mamt = mamt;
//    }
//
//    public String getTip() {
//        return tip;
//    }
//
//    public void setTip(String tip) {
//        this.tip = tip;
//    }
//
//    @Override
//    public String toString() {
//        return "QQCP_Bean_api11{" +
//                "id='" + id + '\'' +
//                ", sta=" + sta +
//                ", amt='" + amt + '\'' +
//                ", mamt='" + mamt + '\'' +
//                ", tip='" + tip + '\'' +
//                '}';
//    }

}
