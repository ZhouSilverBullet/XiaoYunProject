package com.sdxxtop.robotproject.presenter.iview;

/**
 * Created by Administrator on 2018/9/19.
 */

public interface SkillView {
    void onSpeechParResult(String speechMessage);

    void onStartSkill();

    void onStopSkill();

    void onVolumeChange(int volume);

    void onQueryEnded(int query);

    void onSendRequest(String reqType, String reqText, String reqParam);

    void onComplete();

    void onResult(String value);
}
