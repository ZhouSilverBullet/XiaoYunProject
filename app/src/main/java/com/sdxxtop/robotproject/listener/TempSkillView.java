package com.sdxxtop.robotproject.listener;

import com.sdxxtop.robotproject.presenter.IPresenter;
import com.sdxxtop.robotproject.presenter.iview.SkillView;

/**
 * Created by Administrator on 2018/9/21.
 */

public class TempSkillView implements SkillView, IPresenter<SkillView> {

    private SkillView skillView;
    private SkillView skillView2;

    @Override
    public void onSpeechParResult(String speechMessage) {
        if (skillView != null) {
            skillView.onSpeechParResult(speechMessage);
        }

        if (skillView2 != null) {
            skillView2.onSpeechParResult(speechMessage);
        }
    }

    @Override
    public void onStartSkill() {
        if (skillView != null) {
            skillView.onStartSkill();
        }
        if (skillView2 != null) {
            skillView2.onStartSkill();
        }
    }

    @Override
    public void onStopSkill() {
        if (skillView != null) {
            skillView.onStopSkill();
        }
        if (skillView2!=  null) {
            skillView2.onStopSkill();
        }
    }

    @Override
    public void onVolumeChange(int volume) {
        if (skillView != null) {
            skillView.onVolumeChange(volume);
        }
        if (skillView2 != null) {
            skillView2.onVolumeChange(volume);
        }
    }

    @Override
    public void onQueryEnded(int query) {
        if (skillView != null) {
            skillView.onQueryEnded(query);
        }
        if (skillView2 != null) {
            skillView2.onQueryEnded(query);
        }
    }

    @Override
    public void onSendRequest(String reqType, String reqText, String reqParam) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onResult(String value) {

    }

    @Override
    public void addView(SkillView view) {
        skillView = view;
    }

    public void addView2(SkillView view) {
        skillView2 = view;
    }

    @Override
    public void removeView() {
        if (skillView != null) {
            skillView = null;
        }
        if (skillView2 != null) {
            skillView2 = null;
        }
    }
}
