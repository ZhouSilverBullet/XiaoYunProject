package com.sdxxtop.robotproject.listener;

/**
 * Created by Administrator on 2018/9/19.
 */

public interface ISkillListener {
    void onSpeechParResult(String speechMessage);

    void onStart();

    void onStop();

    void onVolumeChange(int volume);

    void onQueryEnded(int query);
}
