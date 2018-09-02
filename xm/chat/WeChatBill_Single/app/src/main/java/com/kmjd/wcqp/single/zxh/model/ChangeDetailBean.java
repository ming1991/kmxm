package com.kmjd.wcqp.single.zxh.model;

/**
 * Created by zym on 2017/3/17.
 * 微信零钱明细原始数据
 */

public class ChangeDetailBean {

    /**
     * balance : 1181
     * balance_source : 收入
     * bill_type :
     * bkid : 6b650cb2c0959fb305d2c87d4103fa7f
     * conmum :
     * createtime : 2017-01-29 17:45:36
     * explain :
     * fcon : 0
     * list_sign :
     * modifytime : 2017-01-29 17:45:36
     * paynum : 188
     * trans_state_name : 微信红包
     * transid : 1000039401170129003208308600005895635966
     * type : 1
     */

    private String balance;
    private String balance_source;
    private String bill_type;
    private String bkid;
    private String conmum;
    private String createtime;
    private String explain;
    private String fcon;
    private String list_sign;
    private String modifytime;
    private String paynum;
    private String trans_state_name;
    private String transid;
    private String type;

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getBalance_source() {
        return balance_source;
    }

    public void setBalance_source(String balance_source) {
        this.balance_source = balance_source;
    }

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }

    public String getBkid() {
        return bkid;
    }

    public void setBkid(String bkid) {
        this.bkid = bkid;
    }

    public String getConmum() {
        return conmum;
    }

    public void setConmum(String conmum) {
        this.conmum = conmum;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getFcon() {
        return fcon;
    }

    public void setFcon(String fcon) {
        this.fcon = fcon;
    }

    public String getList_sign() {
        return list_sign;
    }

    public void setList_sign(String list_sign) {
        this.list_sign = list_sign;
    }

    public String getModifytime() {
        return modifytime;
    }

    public void setModifytime(String modifytime) {
        this.modifytime = modifytime;
    }

    public String getPaynum() {
        return paynum;
    }

    public void setPaynum(String paynum) {
        this.paynum = paynum;
    }

    public String getTrans_state_name() {
        return trans_state_name;
    }

    public void setTrans_state_name(String trans_state_name) {
        this.trans_state_name = trans_state_name;
    }

    public String getTransid() {
        return transid;
    }

    public void setTransid(String transid) {
        this.transid = transid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChangeDetailBean{" +
                "balance='" + balance + '\'' +
                ", balance_source='" + balance_source + '\'' +
                ", bill_type='" + bill_type + '\'' +
                ", bkid='" + bkid + '\'' +
                ", conmum='" + conmum + '\'' +
                ", createtime='" + createtime + '\'' +
                ", explain='" + explain + '\'' +
                ", fcon='" + fcon + '\'' +
                ", list_sign='" + list_sign + '\'' +
                ", modifytime='" + modifytime + '\'' +
                ", paynum='" + paynum + '\'' +
                ", trans_state_name='" + trans_state_name + '\'' +
                ", transid='" + transid + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
