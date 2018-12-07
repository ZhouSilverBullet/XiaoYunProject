package com.sdxxtop.robotproject;

import android.os.Bundle;

import com.ainirobot.coreservice.client.listener.TextListener;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.presenter.iview.SkillView;

public abstract class SecondBaseActivity extends BaseActivity implements SkillView {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSpeechParResult(String speechMessage) {
        if (getString(R.string.robot_quit).equals(speechMessage)) {
//            SpeechSkill.getInstance().removeCallBack(this);
            App.getInstance().removeView(this);
            App.getInstance().getSkillApi().playText(getString(R.string.robot_name) + "已为您退出当前页面", new TextListener() {
                @Override
                public void onComplete() {
                    super.onComplete();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            });
        }
    }
}
