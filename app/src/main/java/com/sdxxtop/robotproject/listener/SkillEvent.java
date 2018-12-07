package com.sdxxtop.robotproject.listener;

import com.sdxxtop.robotproject.bean.SkillBean;

import java.util.Observable;

/**
 * Created by Administrator on 2018/9/19.
 */

public class SkillEvent extends Observable implements ISkillListener {
    private volatile static SkillEvent instance;

    private SkillEvent() {

    }

    public static SkillEvent getInstance() {
        if (instance == null) {
            synchronized (SkillEvent.class) {
                if (instance == null) {
                    instance = new SkillEvent();
                }
            }
        }
        return instance;
    }

    @Override
    public void onSpeechParResult(String speechMessage) {
        setChanged();
        SkillBean skillBean = new SkillBean();
        skillBean.message = speechMessage;
        skillBean.type = SkillBean.SPEECH_PAR_RESULT;
        notifyObservers(skillBean);
    }

    @Override
    public void onStart() {
        setChanged();
        SkillBean skillBean = new SkillBean();
        skillBean.type = SkillBean.START;
        notifyObservers(skillBean);
    }

    @Override
    public void onStop() {
        setChanged();
        SkillBean skillBean = new SkillBean();
        skillBean.type = SkillBean.STOP;
        notifyObservers(skillBean);
    }

    @Override
    public void onVolumeChange(int volume) {
        setChanged();
        SkillBean skillBean = new SkillBean();
        skillBean.type = SkillBean.VOLUME_CHANGE;
        skillBean.value = volume;
        notifyObservers(skillBean);
    }

    @Override
    public void onQueryEnded(int query) {
        setChanged();
        SkillBean skillBean = new SkillBean();
        skillBean.type = SkillBean.QUERY_ENDED;
        skillBean.value = query;
        notifyObservers(skillBean);
    }
}
