package com.kmjd.android.zxhzm.alipaybill.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kmjd.android.zxhzm.alipaybill.R;

import java.util.List;


/**
 * Created by yuandl on 2016-11-01.
 */

public class DetialAdpter extends RecyclerView.Adapter<DetialAdpter.DetialViewHolder> {
    private Context context;
    private List<String> mDatas;
    private String[] classifyText;

    public DetialAdpter(Context context, List<String> mList) {
        this.context = context;
        this.mDatas = mList;
        classifyText = context.getResources().getStringArray(R.array.detial_classify);
    }

    @Override
    public DetialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.detial_item, parent, false);
        return new DetialViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(DetialViewHolder holder, int position) {
        holder.classify.setText(classifyText[position]);
        holder.detial.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return classifyText == null ? 0 : classifyText.length;
    }


    class DetialViewHolder extends RecyclerView.ViewHolder {
        private TextView classify;
        private TextView detial;

        DetialViewHolder(View itemView) {
            super(itemView);
            classify = (TextView) itemView.findViewById(R.id.classify);
            detial = (TextView) itemView.findViewById(R.id.detial);
        }
    }


}
