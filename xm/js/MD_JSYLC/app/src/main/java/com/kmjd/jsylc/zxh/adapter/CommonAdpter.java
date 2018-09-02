package com.kmjd.jsylc.zxh.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIconTitleBean;
import com.kmjd.jsylc.zxh.utils.SPUtils;
import com.kmjd.jsylc.zxh.widget.LabelView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommonAdpter extends RecyclerView.Adapter<CommonAdpter.MyViewHolder> implements View.OnClickListener {
    private Context context;
    private List<PlatfromIconTitleBean.DataBean> mList;

    /**
     * stateCode:
     * 0:正常
     * 1.维护中
     * 2.敬请期待
     */
    private List<Integer> mStateCodes;

    public List<Integer> getStateCodes() {
        return mStateCodes;
    }

    public void setList(List<PlatfromIconTitleBean.DataBean> list) {
        mList = list;
    }

    //设置状态码
    public void setStateCodes(List<Integer> stateCodes) {
        mStateCodes = stateCodes;
    }

    public CommonAdpter(Context context, List<PlatfromIconTitleBean.DataBean> list, List<Integer> stateCodes) {
        this.context = context;
        mList = list;
        mStateCodes = stateCodes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.common_item_list, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(contentView);
        contentView.setOnClickListener(this);
        return viewHolder;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Drawable d = context.getResources().getDrawable(R.color.foreground);
        Drawable dd = context.getResources().getDrawable(R.color.foreground2);
        String iconUrl = SPUtils.getString(MainApplication.applicationContext, SPUtils.SP_KEY.FAST_DOMAIN.toString());
        PlatfromIconTitleBean.DataBean dataBean = mList.get(position);
        holder.title.setText(dataBean.getT());
        Glide.with(context).asBitmap().load(iconUrl + dataBean.getI()).into(holder.imageView);
        holder.itemView.setTag(position);
        if (dataBean.getD() != null && dataBean.getD().equals("hot")) {
            holder.hot.setVisibility(View.VISIBLE);
            holder.hot_text.setText(context.getResources().getString(R.string.hot_show));
            holder.hot_text.setBgColor(Color.parseColor("#148CFB"));
        } else if (dataBean.getD() != null && dataBean.getD().equals("new")) {
            holder.hot.setVisibility(View.VISIBLE);
            holder.hot_text.setText(context.getResources().getString(R.string.new_last));
            holder.hot_text.setBgColor(Color.parseColor("#FF9600"));
        }  else {
            holder.hot.setVisibility(View.GONE);
        }
        if (null != mStateCodes && mStateCodes.size() > 0) {
            Integer stateCode = mStateCodes.get(position);
            switch (stateCode) {
                case 0://正常
                    holder.cradview_bg.setForeground(dd);
                    holder.hot.setForeground(dd);
                    holder.fly_bg.setVisibility(View.GONE);
                    holder.imageView.setCircleBackgroundColor(Color.parseColor("#363636"));
                    holder.downIcon.setBackground(dd);
                    break;
                case 1://维护中
                    holder.weifu.setText(R.string.wei_hu);
                    holder.cradview_bg.setForeground(d);
                    holder.hot.setForeground(d);
                    holder.fly_bg.setVisibility(View.VISIBLE);
                    holder.imageView.setCircleBackgroundColor(Color.parseColor("#444444"));
                    break;
                case 2://敬请期待
                    holder.weifu.setText(R.string.forworld);
                    holder.cradview_bg.setForeground(d);
                    holder.hot.setForeground(d);
                    holder.fly_bg.setVisibility(View.VISIBLE);
                    holder.imageView.setCircleBackgroundColor(Color.parseColor("#444444"));
                    break;
            }
        }
    }


    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private static long lastTimeMillis;
    private static final long MIN_CLICK_INTERVAL = 200;

    @Override
    public void onClick(View view) {
        long currentTimeMillis = System.currentTimeMillis();
        if (mOnItemClickListener != null) {
            if ((currentTimeMillis - lastTimeMillis) > MIN_CLICK_INTERVAL) {
                lastTimeMillis = currentTimeMillis;
                mOnItemClickListener.onItemClick(view, (Integer) view.getTag());
            }
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView title;
        private FrameLayout hot;
        private TextView weifu;
        private ImageView downIcon;
        private FrameLayout cradview_bg;
        private FrameLayout fly_bg;
        private LabelView hot_text;


        MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_icon);
            cradview_bg = itemView.findViewById(R.id.cradview_bg);
            title = itemView.findViewById(R.id.title);
            hot = itemView.findViewById(R.id.hot);
            weifu = itemView.findViewById(R.id.weifu);
            downIcon = itemView.findViewById(R.id.down);
            fly_bg = itemView.findViewById(R.id.fly_bg);
            hot_text = itemView.findViewById(R.id.hot_text);
        }
    }


    public void setOnItemClickListener(setOnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    private setOnItemClickListener mOnItemClickListener;

    public interface setOnItemClickListener {
        void onItemClick(View view, int position);
    }


}
