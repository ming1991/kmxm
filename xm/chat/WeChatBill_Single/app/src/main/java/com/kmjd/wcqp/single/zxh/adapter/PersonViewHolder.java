package com.kmjd.wcqp.single.zxh.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ufly on 2017/3/24.
 */

public class PersonViewHolder {

    public final View convertView;
    private Map<Integer, View> views = new HashMap<Integer, View>();

    public PersonViewHolder(View convertView) {
        this.convertView = convertView;
    }

    public static PersonViewHolder createSELF(View convertView, int idRes, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(idRes, parent, false);
            PersonViewHolder holder = new PersonViewHolder(convertView);
            convertView.setTag(holder);
        }
        return (PersonViewHolder) convertView.getTag();
    }
    public void putView(int id, View view) {
        views.put(id, view);
    }
    public View getView(int id) {
        if (views.get(id) == null) {
            views.put(id, convertView.findViewById(id));
        }
        return views.get(id);
    }
    public TextView getTv(int id) {
        return (TextView) getView(id);
    }
    public ImageView getIv(int id) {
        return (ImageView) getView(id);
    }
}
