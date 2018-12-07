package com.sdxxtop.robotproject.skill;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.ainirobot.coreservice.client.ApiListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.ainirobot.coreservice.client.speech.SkillApi;
import com.ainirobot.coreservice.client.speech.SkillCallback;
import com.sdxxtop.robotproject.bean.SkillBean;
import com.sdxxtop.robotproject.global.App;

import java.util.ArrayList;
import java.util.List;


/**
 * 语音功能
 *
 * @author Orion
 * @time 2018/9/11
 */
public class SpeechSkill extends BaseSkill implements Handler.Callback {

    private static final String TAG = "SpeechSkill";

    private SkillApi skillApi;
    private List<OnSpeechCallBack> callbackList;
    private boolean isConnected = false;
    private SkillCallback skillCallback;

    private Handler handler = new Handler(this);

    private SpeechSkill() {
        if (skillApi == null) {
            skillApi = App.getInstance().getSkillApi();
            callbackList = new ArrayList<>();
        }
    }


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SkillBean.SPEECH_PAR_RESULT:
                for (OnSpeechCallBack callback : callbackList) {
                    callback.onSpeechParResult((String) msg.obj);
                }
                break;
            case SkillBean.START:
                for (OnSpeechCallBack callback : callbackList) {
                    callback.onStartSkill();
                }
                break;
            case SkillBean.STOP:
                for (OnSpeechCallBack callback : callbackList) {
                    callback.onStopSkill();
                }
                break;
            case SkillBean.VOLUME_CHANGE:
                for (OnSpeechCallBack callback : callbackList) {
                    callback.onVolumeChange((Integer) msg.obj);
                }
                break;
            case SkillBean.QUERY_ENDED:
                for (OnSpeechCallBack callback : callbackList) {
                    callback.onQueryEnded((Integer) msg.obj);
                }
                break;
        }
        return true;
    }

    public void connectApi() {
        skillCallback = new SkillCallback() {

            @Override
            public void onSpeechParResult(final String s) throws RemoteException {
                //用户说话临时识别结果
                Message message = new Message();
                message.what = SkillBean.SPEECH_PAR_RESULT;
                message.obj = s;
                handler.sendMessage(message);
//                for (OnSpeechCallBack callback : callbackList) {
//                    callback.onSpeechParResult(s);
//                }
            }

            @Override
            public void onStart() throws RemoteException {
                handler.sendEmptyMessage(SkillBean.START);
                //用户开始说话
//                for (OnSpeechCallBack callback : callbackList) {
//                    callback.onStartSkill();
//                }
            }

            @Override
            public void onStop() throws RemoteException {
                handler.sendEmptyMessage(SkillBean.STOP);
                //用户说话结束
//                for (OnSpeechCallBack callback : callbackList) {
//                    callback.onStopSkill();
//                }
            }

            @Override
            public void onVolumeChange(int i) throws RemoteException {
                Message message = new Message();
                message.what = SkillBean.VOLUME_CHANGE;
                message.obj = i;
                handler.sendMessage(message);
                //用户说话声音大小变化
//                for (OnSpeechCallBack callback : callbackList) {
//                    callback.onVolumeChange(i);
//                }
            }

            @Override
            public void onQueryEnded(int i) throws RemoteException {
                Message message = new Message();
                message.what = SkillBean.QUERY_ENDED;
                message.obj = i;
                handler.sendMessage(message);
                Log.d(TAG, "onQueryEnded: ");
//                for (OnSpeechCallBack callback : callbackList) {
//                    callback.onQueryEnded(i);
//                }
            }
        };

        skillApi.connectApi(App.getInstance(), new ApiListener() {
            @Override
            public void handleApiDisabled() {
                isConnected = false;
            }

            @Override
            public void handleApiConnected() {
                //注册语音事件回调
                Log.d(TAG, "handleApiConnected: ");
                isConnected = true;
                skillApi.registerCallBack(skillCallback);
            }

            @Override
            public void handleApiDisconnected() {
                isConnected = false;
                skillApi.connectApi();
            }
        });
    }

    public static SpeechSkill getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static final SpeechSkill INSTANCE = new SpeechSkill();
    }

    public boolean playing;

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public void playTxt(String text) {
        if (isPlaying()) {
           return;
        }
        playing = true;
        skillApi.playText(text, new TextListener() {

            @Override
            public void onComplete() {
                super.onComplete();
                playing = false;
            }

            @Override
            public void onError() {
                super.onError();
                playing = false;
            }

            @Override
            public void onStop() {
                super.onStop();
                playing = false;
            }
        });
    }

    public void playTxt(String text, final TextListener textListener) {
        if (isPlaying()) {
            if (textListener != null) {
                textListener.onError();
            }
            return;
        }

        playing = true;
        skillApi.playText(text, new TextListener() {
            @Override
            public void onComplete() {
                super.onComplete();
                playing = false;
                if (textListener != null) {
                    textListener.onComplete();
                }
            }

            @Override
            public void onError() {
                super.onError();
                playing = false;
                if (textListener != null) {
                    textListener.onError();
                }
            }

            @Override
            public void onStop() {
                super.onStop();
                playing = false;
                if (textListener != null) {
                    textListener.onStop();
                }
            }

            @Override
            public void onStart() {
                super.onStart();
                if (textListener != null) {
                    textListener.onStart();
                }
            }
        });
    }

    public void release() {
        if (isConnected) {
            skillApi.unregisterCallBack(skillCallback);
        }
    }

    public void setSkillApi(SkillApi skillApi) {
        this.skillApi = skillApi;
    }

    public SkillApi getSkillApi() {
        return skillApi;
    }

    public synchronized void addCallBack(OnSpeechCallBack skillCallback) {
        this.callbackList.add(skillCallback);
    }

    public synchronized void removeCallBack(OnSpeechCallBack skillCallback) {
        this.callbackList.remove(skillCallback);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public interface OnSpeechCallBack {

        /*void handleApiDisabled();

        void handleApiConnected();

        void handleApiDisconnected();*/

        void onSpeechParResult(String var1);

        void onStartSkill();

        void onStopSkill();

        void onVolumeChange(int var1);

        void onQueryEnded(int var1);
    }

}
