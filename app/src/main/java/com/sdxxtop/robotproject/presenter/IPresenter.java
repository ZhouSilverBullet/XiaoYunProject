package com.sdxxtop.robotproject.presenter;

/**
 * Created by Administrator on 2018/9/19.
 */

public interface IPresenter<V> {
    void addView(V view);
    void removeView();
}
