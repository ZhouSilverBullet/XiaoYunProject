package com.sdxxtop.robotproject.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdxxtop.robotproject.R;
import com.sdxxtop.robotproject.bean.LvJingBean;

import java.util.List;

/**
 * Created by Administrator on 2018/9/20.
 */

public class ImageAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<LvJingBean> mData;
    private View rootView;
    private boolean isFirst = true;
    private OnLvJingClickListener mListener;

    public ImageAdapter(Context context, List<LvJingBean> data, OnLvJingClickListener listener) {
        this.mContext = context;
        this.mData = data;
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        rootView = LayoutInflater.from(mContext).inflate(R.layout.item_img, parent, false);
        ImgHolder imgHolder = new ImgHolder(rootView);
        return imgHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final LvJingBean item = mData.get(position);
        ((ImgHolder) holder).tvImgName.setText(item.getLvName());
        if (position == mData.size() - 1) {
            ((ImgHolder) holder).viewDivice.setVisibility(View.VISIBLE);
        } else {
            ((ImgHolder) holder).viewDivice.setVisibility(View.GONE);
        }
        ((ImgHolder) holder).ivImg.setImageBitmap(item.getImg());
        if (isFirst && position == 0) {
            ((ImgHolder) holder).rlContainor.setSelected(true);
        } else {
            if (item.isSelected()) {
                ((ImgHolder) holder).rlContainor.setSelected(true);
            } else {
                ((ImgHolder) holder).rlContainor.setSelected(false);
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirst = false;
                for (LvJingBean mDatum : mData) {
                    mDatum.setSelected(false);
                }
                item.setSelected(true);
                notifyDataSetChanged();
                mListener.onLJClick(position);
            }
        });
    }

    public interface OnLvJingClickListener {
        void onLJClick(int position);
    }

    public void setFirstSelect(){
        isFirst = true;
        for (LvJingBean mDatum : mData) {
            mDatum.setSelected(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    class ImgHolder extends RecyclerView.ViewHolder {
        ImageView ivImg;
        TextView tvImgName;
        View viewDivice;
        RelativeLayout rlContainor;

        public ImgHolder(View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.iv_img);
            tvImgName = itemView.findViewById(R.id.tv_img_name);
            viewDivice = itemView.findViewById(R.id.view_divice);
            rlContainor = itemView.findViewById(R.id.rl_containor);
        }
    }
}
