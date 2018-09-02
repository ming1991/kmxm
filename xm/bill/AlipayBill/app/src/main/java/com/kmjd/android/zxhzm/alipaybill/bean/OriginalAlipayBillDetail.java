package com.kmjd.android.zxhzm.alipaybill.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by androidshuai on 2018/4/26.
 */

public class OriginalAlipayBillDetail implements Parcelable {

    //昵称
    private String nickName = "";

    //金额
    private String amount = "";

    //余额
    private String surplus = "";

    //收入还是支出(收入type = 1; 支出type = 2)
    private String type = "";

    //交易状态
    private String tradeState = "";

    //行为描述(商品说明<只考虑二维码收款> 转账备注 提现说明 转出说明<暂不考虑> 充值说明<暂不考虑>)
    private String actionDescription = "";
    //行为描述备注
    private String remark = "";

    //交易类型(翻译行为描述：收款、转账(其他都算转账)、提现、服务费)
    private String transactionType = "";

    //账单分类
    private String billClassification = "";

    //创建时间
    private String creationTime = "";

    //订单号
    private String orderNumber = "";

    //付款方式
    private String expenditureMethod = "";

    //收款方式
    private String inComeMethod = "";

    //对方账户
    private String reciprocalAccount = "";

    //提现到
    private String withdrawCashTo = "";

    //收支详情类型
    private String szxqType = "";

    public OriginalAlipayBillDetail() {
    }

    public OriginalAlipayBillDetail(String nickName, String amount, String surplus, String type, String tradeState, String actionDescription, String remark, String transactionType, String billClassification, String creationTime, String orderNumber, String expenditureMethod, String inComeMethod, String reciprocalAccount, String withdrawCashTo, String szxqType) {
        this.nickName = nickName;
        this.amount = amount;
        this.surplus = surplus;
        this.type = type;
        this.tradeState = tradeState;
        this.actionDescription = actionDescription;
        this.remark = remark;
        this.transactionType = transactionType;
        this.billClassification = billClassification;
        this.creationTime = creationTime;
        this.orderNumber = orderNumber;
        this.expenditureMethod = expenditureMethod;
        this.inComeMethod = inComeMethod;
        this.reciprocalAccount = reciprocalAccount;
        this.withdrawCashTo = withdrawCashTo;
        this.szxqType = szxqType;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSurplus() {
        return surplus;
    }

    public void setSurplus(String surplus) {
        this.surplus = surplus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTradeState() {
        return tradeState;
    }

    public void setTradeState(String tradeState) {
        this.tradeState = tradeState;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public void setActionDescription(String actionDescription) {
        this.actionDescription = actionDescription;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getBillClassification() {
        return billClassification;
    }

    public void setBillClassification(String billClassification) {
        this.billClassification = billClassification;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getExpenditureMethod() {
        return expenditureMethod;
    }

    public void setExpenditureMethod(String expenditureMethod) {
        this.expenditureMethod = expenditureMethod;
    }

    public String getInComeMethod() {
        return inComeMethod;
    }

    public void setInComeMethod(String inComeMethod) {
        this.inComeMethod = inComeMethod;
    }

    public String getReciprocalAccount() {
        return reciprocalAccount;
    }

    public void setReciprocalAccount(String reciprocalAccount) {
        this.reciprocalAccount = reciprocalAccount;
    }

    public String getWithdrawCashTo() {
        return withdrawCashTo;
    }

    public void setWithdrawCashTo(String withdrawCashTo) {
        this.withdrawCashTo = withdrawCashTo;
    }

    public String getSzxqType() {
        return szxqType;
    }

    public void setSzxqType(String szxqType) {
        this.szxqType = szxqType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OriginalAlipayBillDetail that = (OriginalAlipayBillDetail) o;

        if (nickName != null ? !nickName.equals(that.nickName) : that.nickName != null)
            return false;
        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (surplus != null ? !surplus.equals(that.surplus) : that.surplus != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (tradeState != null ? !tradeState.equals(that.tradeState) : that.tradeState != null)
            return false;
        if (actionDescription != null ? !actionDescription.equals(that.actionDescription) : that.actionDescription != null)
            return false;
        if (remark != null ? !remark.equals(that.remark) : that.remark != null) return false;
        if (transactionType != null ? !transactionType.equals(that.transactionType) : that.transactionType != null)
            return false;
        if (billClassification != null ? !billClassification.equals(that.billClassification) : that.billClassification != null)
            return false;
        if (creationTime != null ? !creationTime.equals(that.creationTime) : that.creationTime != null)
            return false;
        if (orderNumber != null ? !orderNumber.equals(that.orderNumber) : that.orderNumber != null)
            return false;
        if (expenditureMethod != null ? !expenditureMethod.equals(that.expenditureMethod) : that.expenditureMethod != null)
            return false;
        if (inComeMethod != null ? !inComeMethod.equals(that.inComeMethod) : that.inComeMethod != null)
            return false;
        if (reciprocalAccount != null ? !reciprocalAccount.equals(that.reciprocalAccount) : that.reciprocalAccount != null)
            return false;
        if (withdrawCashTo != null ? !withdrawCashTo.equals(that.withdrawCashTo) : that.withdrawCashTo != null)
            return false;
        return szxqType != null ? szxqType.equals(that.szxqType) : that.szxqType == null;
    }

    @Override
    public int hashCode() {
        int result = nickName != null ? nickName.hashCode() : 0;
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (surplus != null ? surplus.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (tradeState != null ? tradeState.hashCode() : 0);
        result = 31 * result + (actionDescription != null ? actionDescription.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (transactionType != null ? transactionType.hashCode() : 0);
        result = 31 * result + (billClassification != null ? billClassification.hashCode() : 0);
        result = 31 * result + (creationTime != null ? creationTime.hashCode() : 0);
        result = 31 * result + (orderNumber != null ? orderNumber.hashCode() : 0);
        result = 31 * result + (expenditureMethod != null ? expenditureMethod.hashCode() : 0);
        result = 31 * result + (inComeMethod != null ? inComeMethod.hashCode() : 0);
        result = 31 * result + (reciprocalAccount != null ? reciprocalAccount.hashCode() : 0);
        result = 31 * result + (withdrawCashTo != null ? withdrawCashTo.hashCode() : 0);
        result = 31 * result + (szxqType != null ? szxqType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OriginalAlipayBillDetail{" +
                "nickName='" + nickName + '\'' +
                ", amount='" + amount + '\'' +
                ", surplus='" + surplus + '\'' +
                ", type='" + type + '\'' +
                ", tradeState='" + tradeState + '\'' +
                ", actionDescription='" + actionDescription + '\'' +
                ", remark='" + remark + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", billClassification='" + billClassification + '\'' +
                ", creationTime='" + creationTime + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", expenditureMethod='" + expenditureMethod + '\'' +
                ", inComeMethod='" + inComeMethod + '\'' +
                ", reciprocalAccount='" + reciprocalAccount + '\'' +
                ", withdrawCashTo='" + withdrawCashTo + '\'' +
                ", szxqType='" + szxqType + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.nickName);
        dest.writeString(this.amount);
        dest.writeString(this.surplus);
        dest.writeString(this.type);
        dest.writeString(this.tradeState);
        dest.writeString(this.actionDescription);
        dest.writeString(this.remark);
        dest.writeString(this.transactionType);
        dest.writeString(this.billClassification);
        dest.writeString(this.creationTime);
        dest.writeString(this.orderNumber);
        dest.writeString(this.expenditureMethod);
        dest.writeString(this.inComeMethod);
        dest.writeString(this.reciprocalAccount);
        dest.writeString(this.withdrawCashTo);
        dest.writeString(this.szxqType);
    }

    protected OriginalAlipayBillDetail(Parcel in) {
        this.nickName = in.readString();
        this.amount = in.readString();
        this.surplus = in.readString();
        this.type = in.readString();
        this.tradeState = in.readString();
        this.actionDescription = in.readString();
        this.remark = in.readString();
        this.transactionType = in.readString();
        this.billClassification = in.readString();
        this.creationTime = in.readString();
        this.orderNumber = in.readString();
        this.expenditureMethod = in.readString();
        this.inComeMethod = in.readString();
        this.reciprocalAccount = in.readString();
        this.withdrawCashTo = in.readString();
        this.szxqType = in.readString();
    }

    public static final Creator<OriginalAlipayBillDetail> CREATOR = new Creator<OriginalAlipayBillDetail>() {
        @Override
        public OriginalAlipayBillDetail createFromParcel(Parcel source) {
            return new OriginalAlipayBillDetail(source);
        }

        @Override
        public OriginalAlipayBillDetail[] newArray(int size) {
            return new OriginalAlipayBillDetail[size];
        }
    };
}
