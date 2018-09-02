package com.kmjd.wcqp.single.zxh.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kmjd.wcqp.single.zxh.R;
import com.kmjd.wcqp.single.zxh.database.PersonInfom;

import java.util.List;

/**
 *
 * 显示不同类型的条目，
 */
public class PersonInformAdapter extends BaseAdapter {
    private List<PersonInfom> mList;
    public PersonInformAdapter(List<PersonInfom> chatMsgList) {
        this.mList=chatMsgList;
    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public int getViewTypeCount() {
        return 2;
    }
    @Override
    public int getItemViewType(int position) {
       return mList.get(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        PersonViewHolder holder=null;
        if (type==PersonInfom.TYPE_ONE){
            holder = PersonViewHolder.createSELF(convertView, R.layout.person_inform1, parent);
        }else{
            holder = PersonViewHolder.createSELF(convertView, R.layout.person_inform2, parent);
        }
        TextView tv_desc = holder.getTv(R.id.tv_desc);
        TextView tv_content = holder.getTv(R.id.tv_content);
        ImageView iv_icon = holder.getIv(R.id.inform_image);
        PersonInfom inform = mList.get(position);
        if (type==PersonInfom.TYPE_TWO){
            iv_icon.setImageResource(inform.getImage());
            tv_desc.setText(inform.getLeftContent());
            tv_content.setText(inform.getRightContent());
        }
        iv_icon.setImageResource(inform.getImage());
        tv_desc.setText(inform.getLeftContent());
        tv_content.setText(inform.getRightContent());

        return holder.convertView;
    }
}
