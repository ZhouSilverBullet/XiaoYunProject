package com.sdxxtop.robotproject.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdxxtop.robotproject.R;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/9/17.
 */

public class MyAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> mData;

    public MyAdapter(Context context, List<String> data) {
        mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String item = mData.get(position);
        TextView textView = new TextView(mContext);
        textView.setTextColor(Color.parseColor("#000000"));
        textView.setText(item);
        container.addView(textView);
        return textView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // super.destroyItem(container,position,object); 这一句要删除，否则报错
        View view = (View) object;
        container.removeView(view);
        view = null;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setData() {
        notifyDataSetChanged();
    }
}
