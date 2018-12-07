package com.sdxxtop.robotproject.bean;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/9/20.
 */

public class LvJingBean {

    private String lvName;
    private Bitmap img;
    private boolean isSelected;

    public LvJingBean(String lvName, Bitmap img, boolean isSelected) {
        this.lvName = lvName;
        this.img = img;
        this.isSelected = isSelected;
    }

    public String getLvName() {
        return lvName;
    }

    public void setLvName(String lvName) {
        this.lvName = lvName;
    }

    public Bitmap getImg() {
        return img;
    }

    public void setImg(Bitmap img) {
        this.img = img;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
