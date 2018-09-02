package com.kmjd.jsylc.zxh.mvp.model.bean;

/**
 * Created by ufly on 2017-12-11.
 */

public class IsOpenPlatfrom {
    private int postion;
    private int type;

    public IsOpenPlatfrom(int postion, int type) {
        this.postion = postion;
        this.type = type;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "IsOpenPlatfrom{" +
                "postion=" + postion +
                ", type=" + type +
                '}';
    }
}
