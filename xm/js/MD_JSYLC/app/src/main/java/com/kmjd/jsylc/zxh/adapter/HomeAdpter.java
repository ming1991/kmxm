package com.kmjd.jsylc.zxh.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.mvp.model.bean.IsOpenPlatfrom;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;

import java.util.ArrayList;
import java.util.List;


public class HomeAdpter extends RecyclerView.Adapter<HomeAdpter.MyViewHolder> implements View.OnClickListener {

    public static final int TYPE_ITEM_ONE = 0;
    public static final int TYPE_ITEM_TWO = 1;
    public static final int TYPE_ITEM_THREE = 2;
    private LayoutInflater mLayoutInflater;
    private Context context;
    private List<IsOpenPlatfrom> mOpenPlatfromList;//是否维护
    private int screenWidthDp;
    private List<String> mTitles;

    public HomeAdpter(Context context, List<IsOpenPlatfrom> mList, int screenWidthDp) {
        this.context = context;
        mTitles = new ArrayList<>();
        mLayoutInflater = LayoutInflater.from(context);
        this.mOpenPlatfromList = mList;
        this.screenWidthDp = screenWidthDp;

    }

    public void setOpenPlatfromList(List<IsOpenPlatfrom> openPlatfromList) {
        mOpenPlatfromList = openPlatfromList;
    }

    public void setTitles(List<String> titles) {
        this.mTitles = titles;
    }

    @Override
    public HomeAdpter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView;
        if (viewType == TYPE_ITEM_ONE) {
            contentView = mLayoutInflater.inflate(R.layout.home_item_type1, parent, false);
        } else if (viewType == TYPE_ITEM_TWO) {
            contentView = mLayoutInflater.inflate(R.layout.home_item_type2, parent, false);
        } else {
            contentView = mLayoutInflater.inflate(R.layout.home_item_type3, parent, false);
        }
        contentView.setOnClickListener(this);
        return new MyViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(HomeAdpter.MyViewHolder holder, int position) {
        holder.itemView.setTag(position);
        int[] images = {R.mipmap.icon_home_sport, R.mipmap.icon_home_people, R.mipmap.icon_home_jszr, R.mipmap.icon_home_game, R.mipmap.icon_home_ball, R.mipmap.icon_home_studio, R.mipmap.icon_home_live, R.mipmap.icon_home_gift, R.mipmap.icon_home_discuss};
        String[] stringArray = context.getResources().getStringArray(R.array.home_item);
        if (position < 5) {
            if (!mTitles.isEmpty()) {
                holder.tv_center.setText(mTitles.get(position));
            }
        } else {
            holder.tv_center.setText(stringArray[position-5]);
        }
        holder.iv_left.setImageResource(images[position]);
        holder.itemView.setTag(position);
        Drawable d = context.getResources().getDrawable(R.color.foreground);
        Drawable dd = context.getResources().getDrawable(R.color.foreground2);
        if (mOpenPlatfromList.size() > 0) {
            for (int i = 0; i < mOpenPlatfromList.size(); i++) {
                if (mOpenPlatfromList.get(i).getPostion() == position) {
                    switch (mOpenPlatfromList.get(i).getType()) {
                        case 0:
                            //维护
                            holder.card_view.setForeground(d);
                            holder.fly.setVisibility(View.VISIBLE);
                            holder.isweifu.setText(R.string.wei_hu);
                            break;
                        case 1:
                            //期待
                            holder.card_view.setForeground(d);
                            holder.fly.setVisibility(View.VISIBLE);
                            holder.isweifu.setText(R.string.forworld);
                            break;
                        default:
                            holder.card_view.setForeground(dd);
                            holder.fly.setVisibility(View.GONE);
                            break;
                    }
                    break;
                } else {
                    holder.card_view.setForeground(dd);
                    holder.fly.setVisibility(View.GONE);
                }
            }
        } else {
            holder.card_view.setForeground(dd);
            holder.fly.setVisibility(View.GONE);
        }


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int type = getItemViewType(position);
                    switch (type) {
                        case TYPE_ITEM_ONE://一列，返回2
                            return 2;
                        case TYPE_ITEM_TWO:
                        case TYPE_ITEM_THREE: //两列，返回1
                            return 1;
                        default:
                            return 2;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ITEM_ONE;
        } else if (position > 0 && position < 5) {
            return TYPE_ITEM_TWO;
        } else {
            return TYPE_ITEM_THREE;
        }
    }


    @Override
    public int getItemCount() {
        return 9;
    }

    @Override
    public void onClick(View view) {
        long currentTimeMillis = System.currentTimeMillis();
        if (mItemClickListener != null) {
            if ((currentTimeMillis - lastTimeMillis) > MIN_CLICK_INTERVAL) {
                lastTimeMillis = currentTimeMillis;
                mItemClickListener.OnHomeItemClick(view, (Integer) view.getTag());
            }
        }
    }

    private static long lastTimeMillis;
    private static final long MIN_CLICK_INTERVAL = 200;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_left;
        private TextView tv_center;
        private TextView isweifu;
        private FrameLayout fly;
        private FrameLayout card_view;

        MyViewHolder(View itemView) {
            super(itemView);
            iv_left = itemView.findViewById(R.id.iv_left);
            tv_center = itemView.findViewById(R.id.tv_center);
            isweifu = itemView.findViewById(R.id.isweifu);
            fly = itemView.findViewById(R.id.fly);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    private OnItemClickListener mItemClickListener;

    public interface OnItemClickListener {
        void OnHomeItemClick(View view, int position);
    }
}
