package com.sdxxtop.robotproject.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdxxtop.robotproject.R;
import com.sdxxtop.robotproject.bean.MainListBean;

import java.util.List;

public class MainListAdapter extends BaseAdapter {
    private List<MainListBean> list;

    public MainListAdapter(List<MainListBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = View.inflate(parent.getContext(), R.layout.item_main_list, null);
            viewHolder.tvTitle = convertView.findViewById(R.id.tv_title);
            viewHolder.tvContent = convertView.findViewById(R.id.tv_content);
            viewHolder.ivIcon = convertView.findViewById(R.id.iv_icon);
            viewHolder.vLine = convertView.findViewById(R.id.v_line);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MainListBean listBean = list.get(position);
        viewHolder.tvContent.setText(listBean.content);
        viewHolder.tvTitle.setText(listBean.title);
        viewHolder.ivIcon.setImageResource(listBean.leftIcon);
        viewHolder.vLine.setScaleY(0.2f);

        return convertView;
    }

    private static class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;
        TextView tvContent;
        TextView vLine;
    }
}
