package com.kmjd.jsylc.zxh.mvp.model.event;

/**
 * Created by androidshuai on 2018/1/5.
 */

public class NetEvent {
    /**
     * 网络是否可用
     */
    private boolean isAvailableNet;

    public NetEvent(boolean isAvailableNet) {
        this.isAvailableNet = isAvailableNet;
    }

    public boolean isAvailableNet() {
        return isAvailableNet;
    }

    public void setAvailableNet(boolean availableNet) {
        isAvailableNet = availableNet;
    }

    @Override
    public String toString() {
        return "NetEvent{" +
                "isAvailableNet=" + isAvailableNet +
                '}';
    }
}
