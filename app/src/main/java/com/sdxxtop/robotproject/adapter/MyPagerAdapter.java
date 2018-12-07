package com.sdxxtop.robotproject.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sdxxtop.robotproject.R;
import com.sdxxtop.robotproject.camera.DataUtils;
import com.sdxxtop.robotproject.utils.ImgUtil;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/9/17.
 */

public class MyPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Bitmap> mData;
    private Object currentBitmap;
    private ImageView ivImg;

    public MyPagerAdapter(Context context, List<Bitmap> list) {
        mContext = context;
        mData = list;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Bitmap item = mData.get(position);
        View view = View.inflate(mContext, R.layout.item_pager, null);
        ivImg = (ImageView) view.findViewById(R.id.iv_img);
//        item = DataUtils.getMirrorBitmap(item);
        ivImg.setImageBitmap(item);
        container.addView(view);
        return view;
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

    public void setData(List<Bitmap> list){
        this.mData = list;
        notifyDataSetChanged();
    }
}
