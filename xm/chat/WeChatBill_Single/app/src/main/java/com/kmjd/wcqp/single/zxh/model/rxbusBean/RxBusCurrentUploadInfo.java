package com.kmjd.wcqp.single.zxh.model.rxbusBean;

/**
 * Created by androidshuai on 2017/6/26.
 */

public class RxBusCurrentUploadInfo {

    //请求状态
    String state;

    //当前Url
    String currentUrl;

    //上传的内容
    String expectationContent;

    public RxBusCurrentUploadInfo(String state, String currentUrl, String expectationContent) {
        this.state = state;
        this.currentUrl = currentUrl;
        this.expectationContent = expectationContent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCurrentUrl() {
        return currentUrl;
    }

    public void setCurrentUrl(String currentUrl) {
        this.currentUrl = currentUrl;
    }

    public String getExpectationContent() {
        return expectationContent;
    }

    public void setExpectationContent(String expectationContent) {
        this.expectationContent = expectationContent;
    }

    @Override
    public String toString() {
        return "RxBusCurrentUploadInfo{" +
                "state='" + state + '\'' +
                ", currentUrl='" + currentUrl + '\'' +
                ", expectationContent='" + expectationContent + '\'' +
                '}';
    }
}
