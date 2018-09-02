package com.kmjd.android.zxhzm.alipaybill.bean.event;

/**
 * Created by androidshuai on 2018/7/23.
 *
 * 上传结果事件
 */

public class UploadResultEvent {

    //上传状态
    private String uploadState;

    //上传链接
    private String uploadUrl;

    public UploadResultEvent(String uploadState, String uploadUrl) {
        this.uploadState = uploadState;
        this.uploadUrl = uploadUrl;
    }

    public String getUploadState() {
        return uploadState;
    }

    public void setUploadState(String uploadState) {
        this.uploadState = uploadState;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    @Override
    public String toString() {
        return "UploadResultEvent{" +
                "uploadState='" + uploadState + '\'' +
                ", uploadUrl='" + uploadUrl + '\'' +
                '}';
    }
}
