package com.kmjd.wcqp.single.zxh.model;

/**
 * Created by zym on 2017/3/17.
 * 要上传到服务器的微信明细
 */

public class WeChatUploadBean {

    /// 跟盘表id
    public String f_id  = "";

    /// 充值序号
    public String f_recordid = "";

    /// 银行帐本id 或者 支付宝等级
    public String f_bankid = "";

    /// 银行卡号 或者 支付宝账号
    public String f_account = "";

    /// 收入金额
    public String f_extraction = "";

    /// 支出金额
    public String f_income = "";

    /// 余额
    public String f_surplus = "";

    /// 转账类型
    public String f_type = "";

    /// 转账类型显示的文本不入库
    public String f_type_str = "";

    /// 备注
    public String f_remark = "";

    /// 转账交易时间
    public String f_time = "";

    /// 汇款银行 或者 支付宝手机号
    public String f_remitting_bank = "";

    /// 汇款人
    public String f_remitter = "";

    /// 汇款帐号 或者 支付宝邮箱
    public String f_remittance_account = "";

    /// 管理员
    public String f_Admin = "";

    /// 帐本名称
    public String f_BankID_zdd = "";

    /// 操作时间
    public String f_operateTime = "";

    /// 交易号
    public String f_tradeNum = "";

    /// 账本类型 0=储值 1=兑银 2=支付宝储值 3=支付宝兑银 4=微信储值 5=微信兑银
    public String f_DetailedType = "";

    /// 请求APP次数
    public String PostAppCount = "";

    /// APP返回状态
    public String ReturnIndex = "";

    /// 最后请求APP时间
    public String LastTime = "";

    public String getF_id() {
        return f_id;
    }

    public void setF_id(String f_id) {
        this.f_id = f_id;
    }

    public String getF_recordid() {
        return f_recordid;
    }

    public void setF_recordid(String f_recordid) {
        this.f_recordid = f_recordid;
    }

    public String getF_bankid() {
        return f_bankid;
    }

    public void setF_bankid(String f_bankid) {
        this.f_bankid = f_bankid;
    }

    public String getF_account() {
        return f_account;
    }

    public void setF_account(String f_account) {
        this.f_account = f_account;
    }

    public String getF_extraction() {
        return f_extraction;
    }

    public void setF_extraction(String f_extraction) {
        this.f_extraction = f_extraction;
    }

    public String getF_income() {
        return f_income;
    }

    public void setF_income(String f_income) {
        this.f_income = f_income;
    }

    public String getF_surplus() {
        return f_surplus;
    }

    public void setF_surplus(String f_surplus) {
        this.f_surplus = f_surplus;
    }

    public String getF_type() {
        return f_type;
    }

    public void setF_type(String f_type) {
        this.f_type = f_type;
    }

    public String getF_type_str() {
        return f_type_str;
    }

    public void setF_type_str(String f_type_str) {
        this.f_type_str = f_type_str;
    }

    public String getF_remark() {
        return f_remark;
    }

    public void setF_remark(String f_remark) {
        this.f_remark = f_remark;
    }

    public String getF_time() {
        return f_time;
    }

    public void setF_time(String f_time) {
        this.f_time = f_time;
    }

    public String getF_remitting_bank() {
        return f_remitting_bank;
    }

    public void setF_remitting_bank(String f_remitting_bank) {
        this.f_remitting_bank = f_remitting_bank;
    }

    public String getF_remitter() {
        return f_remitter;
    }

    public void setF_remitter(String f_remitter) {
        this.f_remitter = f_remitter;
    }

    public String getF_remittance_account() {
        return f_remittance_account;
    }

    public void setF_remittance_account(String f_remittance_account) {
        this.f_remittance_account = f_remittance_account;
    }

    public String getF_Admin() {
        return f_Admin;
    }

    public void setF_Admin(String f_Admin) {
        this.f_Admin = f_Admin;
    }

    public String getF_BankID_zdd() {
        return f_BankID_zdd;
    }

    public void setF_BankID_zdd(String f_BankID_zdd) {
        this.f_BankID_zdd = f_BankID_zdd;
    }

    public String getF_operateTime() {
        return f_operateTime;
    }

    public void setF_operateTime(String f_operateTime) {
        this.f_operateTime = f_operateTime;
    }

    public String getF_tradeNum() {
        return f_tradeNum;
    }

    public void setF_tradeNum(String f_tradeNum) {
        this.f_tradeNum = f_tradeNum;
    }

    public String getF_DetailedType() {
        return f_DetailedType;
    }

    public void setF_DetailedType(String f_DetailedType) {
        this.f_DetailedType = f_DetailedType;
    }

    public String getPostAppCount() {
        return PostAppCount;
    }

    public void setPostAppCount(String postAppCount) {
        PostAppCount = postAppCount;
    }

    public String getReturnIndex() {
        return ReturnIndex;
    }

    public void setReturnIndex(String returnIndex) {
        ReturnIndex = returnIndex;
    }

    public String getLastTime() {
        return LastTime;
    }

    public void setLastTime(String lastTime) {
        LastTime = lastTime;
    }
}
