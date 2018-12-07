package com.sdxxtop.robotproject.presenter;

import com.sdxxtop.robotproject.presenter.iview.SkillView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Administrator on 2018/9/20.
 */

public class ImgPresenter implements Observer, IPresenter<SkillView> {
    public static final String TAG = "ImgPresenter";
    private SkillView skillView;
    private volatile static ImgPresenter instance;

    private ImgPresenter() {

    }

    public static ImgPresenter getInstance() {
        if (instance == null) {
            synchronized (ImgPresenter.class) {
                if (instance == null) {
                    instance = new ImgPresenter();
                }
            }
        }
        return instance;
    }

    @Override
    public void addView(SkillView view) {
        this.skillView = view;
    }

    @Override
    public void removeView() {
        if (skillView != null) {
            skillView = null;
        }
    }

    @Override
    public void update(Observable o, Object arg) {

    }

}
