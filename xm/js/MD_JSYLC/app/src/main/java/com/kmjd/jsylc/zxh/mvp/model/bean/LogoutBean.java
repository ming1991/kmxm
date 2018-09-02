package com.kmjd.jsylc.zxh.mvp.model.bean;


public class LogoutBean  {

    /**
     * c : 0
     * msg : success
     */

    private int c;
    private String msg;

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

    @Override
    public String toString() {
        return "LogoutBean{" +
                "c=" + c +
                ", msg='" + msg + '\'' +
                '}';
    }
}
