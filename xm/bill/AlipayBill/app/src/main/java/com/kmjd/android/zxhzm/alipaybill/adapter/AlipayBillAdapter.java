package com.kmjd.android.zxhzm.alipaybill.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kmjd.android.zxhzm.alipaybill.R;
import com.kmjd.android.zxhzm.alipaybill.bean.OriginalAlipayBillDetail;

import java.util.List;

public class AlipayBillAdapter extends RecyclerView.Adapter<AlipayBillAdapter.BillViewHolder> implements View.OnClickListener {
    private Context context;
    private List<OriginalAlipayBillDetail> mDetails;

    public AlipayBillAdapter(Context context, List<OriginalAlipayBillDetail> details) {
        this.context = context;
        this.mDetails = details;
    }

    public void setDetails(List<OriginalAlipayBillDetail> details) {
        mDetails = details;
    }

    @Override
    public AlipayBillAdapter.BillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.bill_item, parent, false);
        AlipayBillAdapter.BillViewHolder viewHolder = new AlipayBillAdapter.BillViewHolder(contentView);
        contentView.setOnClickListener(this);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AlipayBillAdapter.BillViewHolder holder, int position) {
        String mc=context.getResources().getString(R.string.mc);
        OriginalAlipayBillDetail billDetail = mDetails.get(position);
        if (billDetail.getAmount().contains("+")){
            holder.detial.setTextColor(Color.parseColor("#ff0000"));
        }else {
            holder.detial.setTextColor(Color.parseColor("#000000"));
        }
        holder.detial.setText(billDetail.getAmount());
        holder.name.setText(mc+billDetail.getNickName());
        holder.classify.setText("["+billDetail.getBillClassification()+"]");
        holder.time.setText(billDetail.getCreationTime());
        holder.money.setText(context.getResources().getString(R.string.cuurent_money)+billDetail.getSurplus());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDetails == null ? 0 : mDetails.size();
    }

    @Override
    public void onClick(View view) {
        if (mItemClickListener != null) {
            mItemClickListener.onItemClick(view, (Integer) view.getTag());
        }
    }

    class BillViewHolder extends RecyclerView.ViewHolder {
        private TextView classify;
        private TextView detial;
        private TextView name;
        private TextView time;
        private TextView money;

        BillViewHolder(View itemView) {
            super(itemView);
            classify = (TextView) itemView.findViewById(R.id.classify);
            detial = (TextView) itemView.findViewById(R.id.detial);
            name = (TextView) itemView.findViewById(R.id.name);
            time = (TextView) itemView.findViewById(R.id.time);
            money = (TextView) itemView.findViewById(R.id.money);
        }
    }

    public void setmItemClickListener(AlipayBillAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    private AlipayBillAdapter.OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
