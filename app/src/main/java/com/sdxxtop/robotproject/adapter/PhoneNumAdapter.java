package com.sdxxtop.robotproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sdxxtop.robotproject.R;


/**
 * Created by Administrator on 2018/9/25.
 */

public class PhoneNumAdapter extends BaseAdapter {

    private int[] numList = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
    private Context mContext;

    public PhoneNumAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return numList == null ? 0 : numList.length;
    }

    @Override
    public String getItem(int position) {
        return String.valueOf(numList[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.num_item, parent, false);
            viewHolder.tvNum = convertView.findViewById(R.id.tv_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String item = getItem(position);
        viewHolder.tvNum.setText(item);
        return convertView;
    }

    class ViewHolder {
        TextView tvNum;
    }
}
