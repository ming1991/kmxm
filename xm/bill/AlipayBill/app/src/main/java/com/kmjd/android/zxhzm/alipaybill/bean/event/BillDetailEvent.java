package com.kmjd.android.zxhzm.alipaybill.bean.event;

import com.kmjd.android.zxhzm.alipaybill.bean.OriginalAlipayBillDetail;

import java.util.ArrayList;

/**
 * Created by androidshuai on 2018/4/28.
 */

public class BillDetailEvent {

    public ArrayList<OriginalAlipayBillDetail> getmOriginalAlipayBillDetails() {
        return mOriginalAlipayBillDetails;
    }

    public void setmOriginalAlipayBillDetails(ArrayList<OriginalAlipayBillDetail> mOriginalAlipayBillDetails) {
        this.mOriginalAlipayBillDetails = mOriginalAlipayBillDetails;
    }

    private ArrayList<OriginalAlipayBillDetail> mOriginalAlipayBillDetails;

    public BillDetailEvent(ArrayList<OriginalAlipayBillDetail> originalAlipayBillDetails) {
        mOriginalAlipayBillDetails = originalAlipayBillDetails;
    }


    @Override
    public String toString() {
        return "BillDetailEvent{" +
                "mOriginalAlipayBillDetails=" + mOriginalAlipayBillDetails +
                '}';
    }
}
