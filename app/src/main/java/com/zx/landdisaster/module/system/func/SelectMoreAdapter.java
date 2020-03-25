package com.zx.landdisaster.module.system.func;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.zx.landdisaster.R;

import org.json.JSONArray;

import java.util.List;


/**
 * @name 多选
 */
public class SelectMoreAdapter extends BaseAdapter {
    private Context context;
    private JSONArray datalist;
    private List<Boolean> selects;
    private String key;

    public SelectMoreAdapter(Context context, JSONArray datalist, List<Boolean> selects, String key) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.datalist = datalist;
        this.selects = selects;
        this.key = key;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return datalist.length();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return datalist.opt(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public void setDatalist(JSONArray list, List<Boolean> select) {
        datalist = list;
        selects = select;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        convertView = LayoutInflater.from(context).inflate(R.layout.item_select_more, null);
        TextView text=convertView.findViewById(R.id.text);
        ImageView img=convertView.findViewById(R.id.img);

        text.setText(datalist.optJSONObject(position).optString(key));
        img.setImageResource(selects.get(position) ? R.drawable.icon_select_1 : R.drawable.icon_select_2);
        text.setTextColor(ContextCompat.getColor(context, selects.get(position) ? R.color.blue : R.color.black));
        return convertView;
    }
}
