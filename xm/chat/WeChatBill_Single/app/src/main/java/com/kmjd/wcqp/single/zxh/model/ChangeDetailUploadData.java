package com.kmjd.wcqp.single.zxh.model;

/**
 * Created by androidshuai on 2017/6/27.
 * 抽取的对象
 */

public class ChangeDetailUploadData {

    //请求头部信息，使用时需要再md5
    String userAgent;

    //需要上传的内容
    String uploadJsonString;

    //余额
    String alreadyGetBalance;

    //微信号
    String wechatAccount;

    //捕获的微信零钱明细数据
    String captureData;

    public ChangeDetailUploadData(String userAgent, String uploadJsonString, String alreadyGetBalance, String wechatAccount, String captureData) {
        this.userAgent = userAgent;
        this.uploadJsonString = uploadJsonString;
        this.alreadyGetBalance = alreadyGetBalance;
        this.wechatAccount = wechatAccount;
        this.captureData = captureData;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUploadJsonString() {
        return uploadJsonString;
    }

    public void setUploadJsonString(String uploadJsonString) {
        this.uploadJsonString = uploadJsonString;
    }

    public String getAlreadyGetBalance() {
        return alreadyGetBalance;
    }

    public void setAlreadyGetBalance(String alreadyGetBalance) {
        this.alreadyGetBalance = alreadyGetBalance;
    }

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }

    public String getCaptureData() {
        return captureData;
    }

    public void setCaptureData(String captureData) {
        this.captureData = captureData;
    }

    @Override
    public String toString() {
        return "ChangeDetailUploadData{" +
                "userAgent='" + userAgent + '\'' +
                ", uploadJsonString='" + uploadJsonString + '\'' +
                ", alreadyGetBalance='" + alreadyGetBalance + '\'' +
                ", wechatAccount='" + wechatAccount + '\'' +
                ", captureData='" + captureData + '\'' +
                '}';
    }
}
