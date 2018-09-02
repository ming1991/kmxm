package com.kmjd.jsylc.zxh.mvp.model.bean;

/**
 * Created by androidshuai on 2017/11/23.
 *
 * 获取RSA公钥的返回数据
 */

public class RSABean {


    /**
     * c : 0
     * msg : success
     * data :
     */

    private int c;
    private String msg;
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "RSABean{" +
                "c=" + c +
                ", msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
