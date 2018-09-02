package com.kmjd.jsylc.zxh.mvp.model.bean;

public class VelocityEntity {

    private String url;

    private long lastTime;

    public VelocityEntity(String url, long lastTime) {
        this.url = url;
        this.lastTime = lastTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
}
