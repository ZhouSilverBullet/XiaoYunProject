package com.sdxxtop.robotproject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.sdxxtop.robotproject.adapter.ChatAdapter;
import com.sdxxtop.robotproject.bean.ChatContentBean;
import com.sdxxtop.robotproject.control.MessageManager;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.presenter.iview.SkillView;
import com.sdxxtop.robotproject.skill.MoveSkill;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.xuxin.entry.ChatWordBean;
import com.xuxin.http.IRequestListener;
import com.xuxin.http.Params;
import com.xuxin.http.RequestCallback;
import com.xuxin.http.RequestExe;

import java.util.ArrayList;

import retrofit2.http.PATCH;

public class ChatActivity extends SecondBaseActivity {
    private String TAG = "ChatActivity";
    private ListView chatList;
    private LinearLayout chatBackLayout;
    private TextView chatVoiceText;
    private View sendTextImg;

    private ChatAdapter mAdapter;

    private ChatActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mContext = this;
        initVariables();
        initView();
        initEvent();
    }

    private void initVariables() {
        App.getInstance().addView(this);
        SpeechSkill.getInstance().setPlaying(false);
        MessageManager.getInstance().setWakeUpInterruptListener(new MessageManager.WakeUpInterruptListener() {
            @Override
            public void onWakeUp() {
                isPayTextVoice = false;
                isSkillCallback = false;
            }
        });
//        ChatSkillPresenter.getInstance().addView(this);
//        App.getInstance().addSkillView(this);
//        SpeechSkill.getInstance().addCallBack(this);
//        SpeechSkill.getInstance().getSkillApi().setRecognizeMode(true);
//        SpeechSkill.getInstance().getSkillApi().setRecognizable(true);
    }

    private void initEvent() {
        chatBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sendTextImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initView() {
        chatBackLayout = findViewById(R.id.chat_back);
        chatList = findViewById(R.id.chat_list);
        chatVoiceText = findViewById(R.id.chat_voice_text);
        sendTextImg = findViewById(R.id.chat_send_text_img);

        mAdapter = new ChatAdapter(new ArrayList<ChatContentBean>());
        chatList.setAdapter(mAdapter);
    }

    private boolean isRequesting;

    private void sendRequest(String value) {
        chatVoiceText.setText("");
        //如果超过20的这个长度就不在发送
        if (TextUtils.isEmpty(value) || value.length() > 20) {
            return;
        }

        if (isRequesting) {
            return;
        }

        if (isPayTextVoice || isSkillCallback) {
            return;
        }

        isRequesting = true;

        mAdapter.createBeanAndAdd(value, false);
        chatList.setSelection(mAdapter.getCount() - 1);
        Log.e(TAG, "value = " + value);
        Params params = new Params();
        params.put("it", value);
        params.put("tp", 2);
        String data = params.getData();
        Log.e(TAG, "params data = " + data);
        RequestExe.createRequest().postChatXiaoYun(data).enqueue(new RequestCallback<>(new IRequestListener<ChatWordBean>() {
            @Override
            public void onSuccess(ChatWordBean chatWordBean) {
                isRequesting = false;
                ChatWordBean.DataEntry data = chatWordBean.getData();
                Log.e(TAG, "data = " + data);
                if (data != null) {
                    String answer = data.getAnswer();
                    if (!TextUtils.isEmpty(answer)) {
                        payText(answer);
                        mAdapter.createBeanAndAdd(answer, true);
                        chatList.setSelection(mAdapter.getCount() - 1);
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                isRequesting = false;
                Log.e(TAG, "code = " + code + " errorMsg = " + errorMsg);
            }
        }));
    }

    private boolean isPayTextVoice;
    private boolean isSkillCallback;

    private void payText(String text) {
        if (isPayTextVoice || isSkillCallback) {
            return;
        }

        isPayTextVoice = true;
        isSkillCallback = true;
        SpeechSkill.getInstance().playTxt(text, new TextListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "onStart");
            }

            @Override
            public void onError() {
                isPayTextVoice = false;
                Log.e(TAG, "onError");
            }

            @Override
            public void onComplete() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isPayTextVoice = false;
                    }
                });
                Log.e(TAG, "onComplete");
            }
        });
    }

    @Override
    protected void onDestroy() {
        App.getInstance().removeView(this);
        SpeechSkill.getInstance().setPlaying(false);
//        SpeechSkill.getInstance().getSkillApi().setRecognizeMode(false);
//        SpeechSkill.getInstance().getSkillApi().setRecognizable(false);
//        SpeechSkill.getInstance().removeCallBack(this);
        super.onDestroy();
    }

    @Override
    public void onSpeechParResult(final String speechMessage) {
        Log.e(TAG, "mSkillCallback onSpeechParResult speechMessage = " + speechMessage + " thread : " + Thread.currentThread());
        super.onSpeechParResult(speechMessage);

//        if (speechMessage.contains("带我去大门")) {
//            SpeechSkill.getInstance().playTxt("正在去往大门，跟我来吧！", new TextListener() {
//                @Override
//                public void onComplete() {
//                    super.onComplete();
//                    MoveSkill.getInstance().goPosition(mContext, "大门");
//                }
//            });
//        } else if (speechMessage.contains("带我去讲解台")) {
//            SpeechSkill.getInstance().playTxt("正在去往讲解台，跟我来吧！", new TextListener() {
//                @Override
//                public void onComplete() {
//                    super.onComplete();
//                    MoveSkill.getInstance().goPosition(mContext, "讲解台");
//                }
//            });
//        } else if (speechMessage.contains("带我去展厅")) {
//            SpeechSkill.getInstance().playTxt("正在去往讲展厅，跟我来吧！", new TextListener() {
//                @Override
//                public void onComplete() {
//                    super.onComplete();
//                    MoveSkill.getInstance().goPosition(mContext, "展厅");
//                }
//            });
//        } else

        if (speechMessage.contains("退出导航")) {
            SpeechSkill.getInstance().playTxt("退出导航成功", new TextListener() {
                @Override
                public void onComplete() {
                    super.onComplete();
                    RobotApi.getInstance().stopGoPosition(0);
                }
            });
        }

        chatVoiceText.setText(speechMessage);
    }

    @Override
    public void onStartSkill() {
        Log.e(TAG, "mSkillCallback onStartSkill");
    }

    @Override
    public void onStopSkill() {
        Log.e(TAG, "mSkillCallback onStop");
        isSkillCallback = false;
        String value = chatVoiceText.getText().toString();
        if (getString(R.string.robot_quit).equals(value)) {
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
        } else {
            sendRequest(value);
        }
    }

    @Override
    public void onVolumeChange(int volume) {
        Log.e(TAG, "mSkillCallback onVolumeChange i = " + volume);
    }

    @Override
    public void onQueryEnded(int query) {
        Log.e(TAG, "mSkillCallback onQueryEnded i = " + query);
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
}
