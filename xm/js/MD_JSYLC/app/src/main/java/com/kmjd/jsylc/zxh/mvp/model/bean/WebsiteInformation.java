package com.kmjd.jsylc.zxh.mvp.model.bean;

/**
 * Created by androidshuai on 2018/3/20.
 */

public class WebsiteInformation {

    private String website;

    private float speed;

    public WebsiteInformation(String website, float speed) {
        this.website = website;
        this.speed = speed;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "WebsiteInformation{" +
                "website='" + website + '\'' +
                ", speed=" + speed +
                '}';
    }
}
