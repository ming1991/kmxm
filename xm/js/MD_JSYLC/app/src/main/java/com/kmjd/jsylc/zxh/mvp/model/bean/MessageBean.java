package com.kmjd.jsylc.zxh.mvp.model.bean;

/**
 * Created by ufly on 2017-12-14.
 */

public class MessageBean {

    private int total;

    private String title;

    private String id;

    private int trueMaxId;

    public MessageBean(int total, String title, String id, int trueMaxId) {
        this.total = total;
        this.title = title;
        this.id = id;
        this.trueMaxId = trueMaxId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTrueMaxId() {
        return trueMaxId;
    }

    public void setTrueMaxId(int trueMaxId) {
        this.trueMaxId = trueMaxId;
    }

    @Override
    public String toString() {
        return "MessageBean{" +
                "total=" + total +
                ", title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", trueMaxId=" + trueMaxId +
                '}';
    }
}
