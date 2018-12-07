package com.sdxxtop.robotproject.presenter;

import android.util.Log;

import com.sdxxtop.robotproject.bean.SkillBean;
import com.sdxxtop.robotproject.presenter.iview.SkillView;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Administrator on 2018/9/19.
 */

public class ChatSkillPresenter implements Observer, IPresenter<SkillView> {
    public static final String TAG = "ChatSkillPresenter";
    private SkillView skillView;

    private volatile static ChatSkillPresenter instance;

    private ChatSkillPresenter() {

    }

    public static ChatSkillPresenter getInstance() {
        if (instance == null) {
            synchronized (ChatSkillPresenter.class) {
                if (instance == null) {
                    instance = new ChatSkillPresenter();
                }
            }
        }
        return instance;
    }

    public void addView(SkillView skillView) {
        this.skillView = skillView;
    }

    @Override
    public void removeView() {
        if (skillView != null) {
            skillView = null;
        }
    }

    @Override
    public void update(Observable observable, final Object o) {
        Log.e(TAG, "Object " + o + " thread " + Thread.currentThread());
//        App.getInstance().getHandler().post(new Runnable() {
//            @Override
//            public void run() {
//                EventBus.getDefault().post(o);
//            }
//        });

        if (skillView == null || !(o instanceof SkillBean)) {
            return;
        }

        final SkillBean skillBean = (SkillBean) o;
        switch (skillBean.type) {
            case SkillBean.SPEECH_PAR_RESULT:
                skillView.onSpeechParResult(skillBean.message);
                break;
            case SkillBean.START:
                skillView.onStartSkill();
                break;
            case SkillBean.STOP:
                skillView.onStopSkill();
                break;
            case SkillBean.VOLUME_CHANGE:
                skillView.onVolumeChange(skillBean.value);
                break;
            case SkillBean.QUERY_ENDED:
                skillView.onQueryEnded(skillBean.value);
                break;
        }
    }
}
