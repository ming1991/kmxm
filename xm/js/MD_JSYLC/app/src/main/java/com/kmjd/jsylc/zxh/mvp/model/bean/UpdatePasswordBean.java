package com.kmjd.jsylc.zxh.mvp.model.bean;

/**
 * Created by androidshuai on 2017/12/5.
 */

public class UpdatePasswordBean {

    /**
     * c : 0
     * m : 您的密码已变更，日后请以新密码登入！
     */

    private int c;
    private String m;

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    @Override
    public String toString() {
        return "UpdatePasswordBean{" +
                "c=" + c +
                ", m='" + m + '\'' +
                '}';
    }
}
