package com.md.jsyxzs_cn.zym_xs.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.RepairListInfo;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.QueryRepairResModel;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by androidshuai on 2016/12/14.
 */

public class RepairListAdapter extends RecyclerView.Adapter<RepairListAdapter.RepairListViewHolder>{
    private List<QueryRepairResModel> repairList;
    private QueryRepairResModel repairModelBean;

    private Context mContext;
    private boolean[] mBooleen;

    public RepairListAdapter(Context context, List<QueryRepairResModel> repairList) {
        this.mContext=context;
        this.repairList =repairList;
        if (repairList!=null) mBooleen = new boolean[repairList.size()];
    }

    @Override
    public RepairListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repair_list_item, parent, false);
        return new RepairListViewHolder(mContext,view);
    }

    @Override
    public void onBindViewHolder(RepairListViewHolder holder, int position) {

        setLayout(holder,position);

        repairModelBean = repairList.get(position);
        holder.setReapirItemTime(repairModelBean.getF_date());
        holder.repair_item_repairno_id.setText(repairModelBean.getF_id()+"");
        holder.repair_item_network_address.setText(repairModelBean.getF_webSite());
        holder.setProblemType(repairModelBean.getF_type());
        holder.setDealState(repairModelBean.getF_state());
        holder.repair_item_content.setText(repairModelBean.getF_content());
        if (!TextUtils.isEmpty(repairModelBean.getF_replyContent()))
            holder.repair_item_replycontent.setText(repairModelBean.getF_replyContent());

        holder.reapir_item_zhankai.setTag(position);
        holder.reapir_item_zhankai.setOnClickListener(mOnClickListener);
        holder.ll_repair_list_item.setTag(position);
        holder.ll_repair_list_item.setOnClickListener(mOnClickListener);
    }

    private void setLayout(RepairListViewHolder holder, int position) {
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0,
                mBooleen[position] ? ViewGroup.LayoutParams.MATCH_PARENT : ViewGroup.LayoutParams.WRAP_CONTENT,3);
        holder.rl_repair_item_time.setLayoutParams(layoutParams1);

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0,
                mBooleen[position] ? ViewGroup.LayoutParams.WRAP_CONTENT : ViewGroup.LayoutParams.MATCH_PARENT,10);
        holder.rl_repair_item_little_title.setLayoutParams(layoutParams2);

        holder.ll_repair_item_content.setVisibility( mBooleen[position] ? View.VISIBLE : View.GONE);

        holder.reapir_item_zhankai.setImageResource(mBooleen[position] ? R.drawable.shouqi : R.drawable.zhankai);
    }

    @Override
    public int getItemCount() {
        return repairList==null?0:repairList.size();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag!=null && tag instanceof Integer){
                int position = (int) tag;
                //if (mBooleen[position] == true && v.getId() == R.id.ll_repair_list_item) return;//只有点击上拉按钮才可以收回
                for (int i = 0; i < mBooleen.length; i++) {
                    if (i == position) {
                        mBooleen[i] = !mBooleen[i];
                    }else{
                        mBooleen[i] = false;
                    }
                }
                notifyDataSetChanged();
            }
        }
    };

    static class RepairListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_repair_list_item)
        LinearLayout ll_repair_list_item;
        @BindView(R.id.repair_item_repairno_id)
        TextView repair_item_repairno_id;
        @BindView(R.id.repair_item_network_address)
        TextView repair_item_network_address;
        @BindView(R.id.repair_item_problem_type)
        TextView repair_item_problem_type;
        @BindView(R.id.repair_item_deal_state)
        TextView repair_item_deal_state;
        @BindView(R.id.repair_item_content)
        TextView repair_item_content;
        @BindView(R.id.repair_item_replycontent)
        TextView repair_item_replycontent;
        @BindView(R.id.reapir_item_time_year)
        TextView reapir_item_time_year;
        @BindView(R.id.reapir_item_time_yueri)
        TextView reapir_item_time_yueri;
        @BindView(R.id.reapir_item_time_second)
        TextView reapir_item_time_second;
        @BindView(R.id.reapir_item_zhankai)
        ImageView reapir_item_zhankai;

        @BindView(R.id.ll_repair_item_content)
        LinearLayout ll_repair_item_content;
        @BindView(R.id.rl_repair_item_little_title)
        RelativeLayout rl_repair_item_little_title;
        @BindView(R.id.rl_repair_item_time)
        RelativeLayout rl_repair_item_time;
        private WeakReference<Context> mContext;
        private String[] states;

        public RepairListViewHolder(Context context, View itemView) {
            super(itemView);
            this.mContext = new WeakReference<Context>(context);
            ButterKnife.bind(this, itemView);
            intiData();
        }

        private void intiData() {
            states=new String[]{mContext.get().getString(R.string.undisposed),
                    mContext.get().getString(R.string.resolved),
                    mContext.get().getString(R.string.dealing),
                    mContext.get().getString(R.string.inextricability)};
        }

        public void setReapirItemTime(String time){
            if (!TextUtils.isEmpty(time)){
                String[] split = time.split(" ");
                String[] split1 = split[0].split("-");
                reapir_item_time_year.setText(split1[0]);
                reapir_item_time_yueri.setText(split1[1]+"/"+split1[2]);
                reapir_item_time_second.setText(split[1].substring(0,split[1].lastIndexOf(":")));
            }
        }

        int[] ints = {1,2,3,14,8,9,10,11,12,13};
        public void setProblemType(int type){
            for (int i = 0; i < ints.length; i++) {
                if (type == ints[i]) {
                    repair_item_problem_type.setText(mContext.get().getResources().getStringArray(R.array.repair_problem_type)[i]);
                    break;
                }
            }
        }

        public void setDealState(int state){
            repair_item_deal_state.setText(states[state]);
            repair_item_deal_state.setTextColor(mContext.get().getResources().getColor(R.color.repair_list_state_text_color));
        }
    }
}
