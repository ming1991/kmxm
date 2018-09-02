package com.kmjd.wcqp.single.zxh.model.rxbusBean;

/**
 * Created by androidshuai on 2017/6/26.
 */

public class RxBusPersonInfo {

    //微信账号
    String wechatAccount;

    //零钱
    String balance;

    public RxBusPersonInfo(String wechatAccount, String balance) {
        this.wechatAccount = wechatAccount;
        this.balance = balance;
    }

    public String getWechatAccount() {
        return wechatAccount;
    }

    public void setWechatAccount(String wechatAccount) {
        this.wechatAccount = wechatAccount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "RxBusPersonInfo{" +
                "wechatAccount='" + wechatAccount + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }
}
