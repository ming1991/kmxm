package com.kmjd.android.zxhzm.alipaybill.bean.event;

import com.kmjd.android.zxhzm.alipaybill.bean.OriginalAlipayBillDetail;

import java.util.ArrayList;

/**
 * Created by androidshuai on 2018/5/14.
 */

public class ShowBillDetailEvent {

    private ArrayList<OriginalAlipayBillDetail> mOriginalAlipayBillDetails;

    public ShowBillDetailEvent(ArrayList<OriginalAlipayBillDetail> originalAlipayBillDetails) {
        mOriginalAlipayBillDetails = originalAlipayBillDetails;
    }

    public ArrayList<OriginalAlipayBillDetail> getOriginalAlipayBillDetails() {
        return mOriginalAlipayBillDetails;
    }

    public void setOriginalAlipayBillDetails(ArrayList<OriginalAlipayBillDetail> originalAlipayBillDetails) {
        mOriginalAlipayBillDetails = originalAlipayBillDetails;
    }

    @Override
    public String toString() {
        return "ShowBillDetailEvent{" +
                "mOriginalAlipayBillDetails=" + mOriginalAlipayBillDetails +
                '}';
    }
}
