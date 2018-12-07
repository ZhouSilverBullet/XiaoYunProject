package com.sdxxtop.robotproject.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.sdxxtop.robotproject.control.MessageManager;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.presenter.iview.SkillView;
import com.sdxxtop.robotproject.skill.SpeechSkill;

/**
 * Created by Administrator on 2018/9/19.
 */

public class MainPresenter implements SpeechSkill.OnSpeechCallBack, MessageManager.MessageCallBack, IPresenter<SkillView> {
    public static final String TAG = "MainPresenter";
    private SkillView skillView;

    public MainPresenter() {

    }

    @Override
    public void addView(SkillView skillView) {
        this.skillView = skillView;
        SpeechSkill.getInstance().addCallBack(this);
        MessageManager.getInstance().addCallBack(this);
    }

    @Override
    public void removeView() {
        skillView = null;
        SpeechSkill.getInstance().removeCallBack(this);
        MessageManager.getInstance().removeCallBack(this);
    }

    @Override
    public void onSpeechParResult(String speechMessage) {
        if (skillView != null) {
            skillView.onSpeechParResult(speechMessage);
        }
    }

    @Override
    public void onStartSkill() {
        if (skillView != null) {
            skillView.onStartSkill();
        }
    }

    @Override
    public void onStopSkill() {
        if (skillView != null) {
            skillView.onStopSkill();
        }
    }

    @Override
    public void onVolumeChange(int volumeChange) {
        if (skillView != null) {
            skillView.onVolumeChange(volumeChange);
        }
    }

    @Override
    public void onQueryEnded(int queryEnded) {
        if (skillView != null) {
            skillView.onQueryEnded(queryEnded);
        }
    }

    @Override
    public void exeRequest(final String type, final String param, final String answerText) {
//        skillView.onSendRequest(type, param, answerText);

        Log.e(TAG, " answerText: " + answerText + " thread : " + Thread.currentThread());

        if (TextUtils.isEmpty(type)) {
            Log.e(TAG, "onSendRequest reqType 为空");
            return;
        }

        if (skillView != null) {
            onMainThread(new Runnable() {
                @Override
                public void run() {
                    skillView.onSendRequest(type, param, answerText);
                }
            });
        }
    }

    @Override
    public void onResult(String value) {

    }

    @Override
    public void onComplete() {
        if (skillView != null) {
            skillView.onComplete();
        }
    }

    private void onMainThread(Runnable runnable) {
        App.getInstance().getHandler().post(runnable);
    }
}
