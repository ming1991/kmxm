package com.kmjd.jsylc.zxh.mvp.model.bean;

/**
 * Created by androidshuai on 2018/8/8.
 */

public class TransferAccountBean {

    /**
     * 平台转账正常响应
     * c : 0
     * msg : success
     * data : {"code":"M-08","message":"平台转帐成功！"}
     */

    /**
     * 平台转账请求提示被登出的响应
     * c : -1
     * info : {"lastDevice":"手机版","lastLoginTime":"2018-06-29 14:49:32","loginTime":"2018-06-29 15:04:44","user":"TB1123"}
     * msg : M-01
     * tip : 您被强迫下线，您的帐户在其他位置登入！
     */

    private InfoBean info;
    private String tip;
    private int c;
    private String msg;
    private DataBean data;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

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
         * code : M-08
         * message : 平台转帐成功！
         */

        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
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
        return "TransferAccountBean{" +
                "info=" + info +
                ", tip='" + tip + '\'' +
                ", c=" + c +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
