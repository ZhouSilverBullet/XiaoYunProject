package com.sdxxtop.robotproject;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.sdxxtop.robotproject.camera.CustomCameraActivity;
import com.sdxxtop.robotproject.control.MainChatControl;
import com.sdxxtop.robotproject.control.MessageManager;
import com.sdxxtop.robotproject.control.RobotPersonInfo;
import com.sdxxtop.robotproject.fragment.ChatFragment;
import com.sdxxtop.robotproject.fragment.MainFragment;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.global.Constants;
import com.sdxxtop.robotproject.presenter.iview.SkillView;
import com.sdxxtop.robotproject.skill.FaceSkill;
import com.sdxxtop.robotproject.skill.FollowSkill;
import com.sdxxtop.robotproject.skill.NavigationSkill;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.sdxxtop.robotproject.strip.StripController;
import com.sdxxtop.robotproject.utils.FrameAnimationUtils;
import com.sdxxtop.robotproject.utils.FuzzyUtils;
import com.sdxxtop.robotproject.utils.MessageParser;
import com.sdxxtop.robotproject.utils.ReportMediaPlayer;
import com.sdxxtop.robotproject.utils.SkipSystemUtils;
import com.sdxxtop.robotproject.widget.SiriView;
import com.xuxin.entry.ChatWordBean;
import com.xuxin.entry.ReserveBean;
import com.xuxin.http.BaseModel;
import com.xuxin.http.IRequestListener;
import com.xuxin.http.Params;
import com.xuxin.http.RequestCallback;
import com.xuxin.http.RequestExe;

public class MainActivity extends BaseActivity implements SkillView, Handler.Callback {

    public static final String TAG = "MainActivity";

    public static final int CONTROL_SLEEP_VISIBILITY = 101;
    //控制开启识别人脸唤醒
    public static final int START_SEARCH_PEOPLE = 105;

    public static final int SLEEP_CURRENT_TIME = 10000;

    //    private GridView gridview;
//    private GridAdapter adapter;
    //    private MainPresenter mainPresenter;
    private View sleepLayout;
    private ImageView sleepImage;
    private View speakingLayout;
    private ImageView speakingImage;
    private SiriView siriNoSpeakingView;
    private SiriView siriSpeakingView;
    private TextView rainbowTextView;
    private MainActivity mContext;
    private Handler handler;

    private boolean isResume;
    private ChatFragment chatFragment;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
//        if (!SystemUtils.isServiceRunning(ModuleService.class, getApplicationContext())) {
//            Log.d(TAG, "onCreate: ");
//            Intent intent = new Intent(this, ModuleService.class);
//            startService(intent);
//        }
        handler = new Handler(this);
//        handler.sendEmptyMessage(100);

        Log.e(TAG, " message height" + getResources().getDisplayMetrics().heightPixels + " width = " + getResources().getDisplayMetrics().widthPixels);
        initVariables();
        initView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == ReserveActivity.RESERVE_BACK && data != null) {
            ReserveBean.DataBean dataBean = (ReserveBean.DataBean) data.getSerializableExtra("reserve_data");
            if (dataBean != null && !TextUtils.isEmpty(dataBean.getPlace())) {
                FollowSkill.getInstance().prepareLead(dataBean);
            }
        }
    }

    private void initView() {
        if (chatFragment == null) {
            chatFragment = new ChatFragment();
        }
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance();
        }
        loadRootFragment(R.id.main_fragment_container, mainFragment, false, false);
//        gridview = findViewById(R.id.gridview);
//        adapter = new GridAdapter();
//        gridview.setAdapter(adapter);
//
//        adapter.setItemClickListener(new GridAdapter.ItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                Intent intent = null;
//                switch (position) {
//                    case 0:
//                        intent = new Intent(MainActivity.this, ChatActivity.class);
//                        startActivity(intent);
////                        ChargeModule.getInstance().queryChargeEstimate();
//                        break;
//                    case 1:
//                        intent = new Intent(MainActivity.this, MoveActivity.class);
//                        startActivity(intent);
//                        break;
//                    case 2:
////                        intent = new Intent(MainActivity.this, CustomCameraActivity.class);
//                        intent = new Intent(MainActivity.this, ReserveActivity.class);
//                        startActivityForResult(intent, 1);
//                        break;
//                    case 3:
////                        intent = new Intent(MainActivity.this, SingleImageActivity.class);
//                        intent = new Intent(MainActivity.this, ReserveActivity.class);
//                        startActivityForResult(intent, 1);
//                        break;
//                }
//            }
//        });


        sleepLayout = findViewById(R.id.sleep_layout);
        sleepImage = findViewById(R.id.sleep_image);

        speakingLayout = findViewById(R.id.speaking_layout);
        speakingImage = findViewById(R.id.speaking_image);

        siriNoSpeakingView = findViewById(R.id.siri_no_speaking_view);
        siriSpeakingView = findViewById(R.id.siri_speaking_view);
        rainbowTextView = findViewById(R.id.chat_text);

//        Glide.with(this).load(R.drawable.main_normal).into(sleepImage);
//        Glide.with(this).load(R.drawable.main_spking).into(speakingImage);

        AnimationDrawable speakingAnimationDrawable = FrameAnimationUtils.getInstance().getDrawable(1);
        sleepImage.setImageDrawable(speakingAnimationDrawable);
        AnimationDrawable speakingAnimationDrawable2 = FrameAnimationUtils.getInstance().getDrawable(2);
        speakingImage.setImageDrawable(speakingAnimationDrawable2);

        MessageManager.getInstance().setWakeUpInterruptListener(new MessageManager.WakeUpInterruptListener() {
            @Override
            public void onWakeUp() {
                //只要导航被唤醒打断都不显示动嘴的笑脸
                speakingLayoutStatus(false, true);
            }
        });

        MessageManager.getInstance().setSpeakingListener(new MessageManager.SpeakingListener() {
            @Override
            public void onStart() {
//                speakingLayoutStatus(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.removeMessages(100);
                        sleepLayout.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onStop() {
//                speakingLayoutStatus(false);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MainChatControl.getInstance().setChat(true);

                        handler.sendEmptyMessageDelayed(100, SLEEP_CURRENT_TIME);
                    }
                });
            }
        });

        RobotPersonInfo.getInstance().setSpeakingListener(new RobotPersonInfo.SpeakingListener() {
            @Override
            public void onStart() {
                speakingLayoutStatus(true, true);
            }

            @Override
            public void onStop() {
                speakingLayoutStatus(false, true);
            }
        });

        FollowSkill.getInstance().setSpeakingListener(new FollowSkill.SpeakingListener() {
            @Override
            public void onStart() {
                speakingLayoutStatus(true);
            }

            @Override
            public void onStop() {
                speakingLayoutStatus(false);
            }
        });

        ReportMediaPlayer.getInstance().setReportListener(new ReportMediaPlayer.ReportListener() {
            @Override
            public void onStart() {
                speakingLayoutStatus(true);
            }

            @Override
            public void onStop() {
                speakingLayoutStatus(false);
            }
        });

        sleepLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeMessages(100);
                handler.sendEmptyMessageDelayed(100, SLEEP_CURRENT_TIME);
                sleepLayout.setVisibility(View.INVISIBLE);
                normalFrameAnimationStatus(false);
            }
        });

        findViewById(R.id.main_root_layout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                longClick();
                return false;
            }
        });
    }

    private void initVariables() {

//        mainPresenter = new MainPresenter();

        //skill 是否连接成功
        if (App.getInstance().isRobotInitSuccess) {
            register();
        } else {
            App.getInstance().setRobotRunnable(new Runnable() {
                @Override
                public void run() {
                    register();
                }
            });
        }

        RobotPersonInfo.getInstance().setRobotWakeUpListener(new RobotPersonInfo.RobotWakeUpListener() {
            @Override
            public void wakeUp() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sleepLayout.setVisibility(View.GONE);
                        handler.removeMessages(100);
                        handler.removeMessages(START_SEARCH_PEOPLE);
                        handler.sendEmptyMessage(CONTROL_SLEEP_VISIBILITY);
                        handler.sendEmptyMessageDelayed(100, SLEEP_CURRENT_TIME);
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        handler.sendEmptyMessageDelayed(100, 1000);
        App.getInstance().addView(this);
        MessageManager.getInstance().setPause(false);
//        mainPresenter.addView(this);
    }

    @Override
    protected void onPause() {
        handler.removeMessages(100);
        super.onPause();
        isResume = false;
        App.getInstance().removeView(this);
        MessageManager.getInstance().setPause(true);
//        mainPresenter.removeView();
    }

    private void register() {
//        robotPresenter.registerModule();
        RobotApi.getInstance().resetEstimate(0, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                super.onResult(result, message);
                Log.d(TAG, "resetEstimate " + result + " , " + message);
            }
        });

        RobotApi.getInstance().isRobotEstimate(0, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                super.onResult(result, message);
                Log.d(TAG, "isRobotEstimate " + result + " , " + message);
            }
        });
    }

    private void speaking(boolean speaking) {
        if (speaking) {
            siriSpeakingView.setVisibility(View.VISIBLE);
            siriNoSpeakingView.setVisibility(View.INVISIBLE);
        } else {
            siriSpeakingView.setVisibility(View.INVISIBLE);
            siriNoSpeakingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSpeechParResult(final String speechMessage) {
//        Log.e(TAG, "onSpeechParResult speechMessage " + speechMessage);
        if (!TextUtils.isEmpty(speechMessage)) {
            Intent intent = null;
            if ("打开问答".equals(speechMessage) || "开启问答".equals(speechMessage)) {
                intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            } else if ("打开带路".equals(speechMessage) || "开启带路".equals(speechMessage)) {
                intent = new Intent(MainActivity.this, MoveActivity.class);
                startActivity(intent);
            } else if ("打开合影".equals(speechMessage) || "开启合影".equals(speechMessage)) {
                intent = new Intent(MainActivity.this, CustomCameraActivity.class);
                startActivity(intent);
            } else if ("打开美图".equals(speechMessage) || "开启美图".equals(speechMessage)) {
                intent = new Intent(MainActivity.this, SingleImageActivity.class);
                startActivity(intent);
            } else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.openStripContains(speechMessage)) {
                StripController.getInstance().stripOpen(speechMessage);
            } else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.closeStripContains(speechMessage)) {
                StripController.getInstance().stripClose(speechMessage);
            }
//            else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.contains2(speechMessage)) {
//                String answerText = getString(R.string.report_luanshen);
//                SpeechSkill.getInstance().playTxt(answerText);
//            } else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.contains6(speechMessage)) {
//                String answerText = getString(R.string.report_luanshen_jieshu);
//                SpeechSkill.getInstance().playTxt(answerText);
//            } else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.contains8(speechMessage)) {
//                String answerText = getString(R.string.report_hui_yi_neirong);
//                SpeechSkill.getInstance().playTxt(answerText);
//            } else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.contains5(speechMessage)) {
//                String answerText = getString(R.string.report_jieri);
//                SpeechSkill.getInstance().playTxt(answerText);
//            } else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.contains9(speechMessage)) {
//                String answerText = getString(R.string.report_hui_yi_place);
//                SpeechSkill.getInstance().playTxt(answerText);
//            } else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.contains10(speechMessage)) {
//                String answerText = getString(R.string.report_hui_yi_keqi);
//                SpeechSkill.getInstance().playTxt(answerText);
//            }
//            else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.contains3(speechMessage)) {
//                String answerText = getString(R.string.report_go_position);
//                SpeechSkill.getInstance().playTxt(answerText);
//            } else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.contains4(speechMessage)) {
//                String answerText = getString(R.string.report_say_bye);
//                SpeechSkill.getInstance().playTxt(answerText);
//            } else if (!TextUtils.isEmpty(speechMessage) && FuzzyUtils.contains7(speechMessage)) {
//                String answerText = getString(R.string.report_laoban);
//                SpeechSkill.getInstance().playTxt(answerText);
//            }
            else if (speechMessage.contains("带我去展厅") || speechMessage.contains("去展厅")) {
                boolean turnBack;
                if (speechMessage.contains("带我去展厅")) {
                    turnBack = true;
                } else {
                    turnBack = false;
                }
                navigation("展厅", turnBack);
            }
            else if (speechMessage.contains("退出导航")) {
                handler.removeMessages(START_SEARCH_PEOPLE);
                RobotApi.getInstance().stopNavigation(0);
                SpeechSkill.getInstance().playTxt("退出导航成功", new TextListener() {
                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }
                });
            }

////            //大门、展厅、电梯口
//            else if (speechMessage.contains("带我去大门")|| speechMessage.contains("去大门")) {
//                navigation("大门");
//            } else if (speechMessage.contains("带我去展厅") || speechMessage.contains("去展厅")) {
//                navigation("展厅");
//            } else if (speechMessage.contains("带我去电梯口")|| speechMessage.contains("去电梯口")) {
//                navigation("电梯口");
//            } else if (speechMessage.contains("退出导航")) {
//                SpeechSkill.getInstance().playTxt("退出导航成功", new TextListener() {
//                    @Override
//                    public void onComplete() {
//                        super.onComplete();
//                        RobotApi.getInstance().stopNavigation(0);
//                    }
//                });
//            }

            if (isResume) {
                SkipSystemUtils.skip(this, speechMessage);
            }

        }

//        if (sleepLayout.getVisibility() != View.VISIBLE) {
        if (speechMessage.contains("小豹")) {
            String tempSpeechMessage = speechMessage.replace("小豹", "小云");
            rainbowTextView.setText(tempSpeechMessage);
        } else {
            rainbowTextView.setText(speechMessage);
        }
//        }
    }

    public synchronized void loadKeywordData(final String value) {
        Params params = new Params();
        params.put("it", value);
//        params.put("tp", Constants.TYPE_PROJECT);
        String data = params.getData();
        Log.e(TAG, "params data = " + data);
        RequestExe.createRequest().postChatKeyword(data).enqueue(new RequestCallback<>(new IRequestListener<ChatWordBean>() {
            @Override
            public void onSuccess(ChatWordBean chatWordBean) {
//                isRequesting = false;
                ChatWordBean.DataEntry data = chatWordBean.getData();
                Log.e(TAG, "data = " + data);
                if (data != null) {
                    String answer = data.getAnswer();
                    if (!TextUtils.isEmpty(answer)) {
                        if (!chatFragment.isAdded()) {
                            loadRootFragment(R.id.main_fragment_container, chatFragment, false, true);
                        }
                        handler.removeMessages(100);
                        handler.sendEmptyMessageDelayed(100, SLEEP_CURRENT_TIME);

                        chatFragment.showSendData(value);

                        payText(answer);
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
//                isRequesting = false;
                Log.e(TAG, "code = " + code + " errorMsg = " + errorMsg);
            }
        }));
    }

    /**
     * 控制视频播放
     *
     * @param controlType
     */
    public synchronized void loadChatVideoControl(int controlType) {
        Params params = new Params();
//        params.put("tp", Constants.TYPE_PROJECT);
        //(1:开始播放 2:暂停/停止播放 3:重新播放 4:全屏播放)
        params.put("ct", controlType);
        String data = params.getData();
        Log.e(TAG, "params data = " + data);
        RequestExe.createRequest().postChatVideo(data).enqueue(new RequestCallback<>(new IRequestListener<BaseModel>() {
            @Override
            public void onSuccess(BaseModel baseModel) {
//                isRequesting = false;
//                ChatWordBean.DataEntry data = chatWordBean.getData();
                Log.e(TAG, "data = " + data);

//                if (data != null) {
//                    String answer = data.getAnswer();
//                    if (!TextUtils.isEmpty(answer)) {
//                        payText(answer);
//                    }
//                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
//                isRequesting = false;
                Log.e(TAG, "code = " + code + " errorMsg = " + errorMsg);
                SpeechSkill.getInstance().playTxt("视频控制失败" + errorMsg);
            }
        }));
    }

    private void payText(String answerText) {
        chatFragment.showLoadData(answerText);
        SpeechSkill.getInstance().playTxt(answerText, new TextListener() {
            @Override
            public void onStart() {
//                speakingLayoutStatus(true);
                Log.e(TAG, "onStart: 开始");
            }

            @Override
            public void onStop() {
//                speakingLayoutStatus(false);
                Log.e(TAG, "onStop: 结束");
            }

            @Override
            public void onError() {
//                speakingLayoutStatus(false);
                Log.e(TAG, "onError: 错误");
            }

            @Override
            public void onComplete() {
//                speakingLayoutStatus(false);
                Log.e(TAG, "onComplete: 完成");
            }
        });
    }

    public void speakingLayoutStatus(final boolean visible) {
        speakingLayoutStatus(visible, false);
    }

    public void speakingLayoutStatus(boolean visible, boolean isWakeUp) {
        final int visibility = visible ? View.VISIBLE : View.INVISIBLE;
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            speakingLayout.setVisibility(visibility);
            frameAnimationStatus(visible);
            Log.e(TAG, "speakingLayoutStatus Looper visible : " + visible);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "speakingLayoutStatus run visible : " + visible);
                    speakingLayout.setVisibility(visibility);
                    frameAnimationStatus(visible);
                }
            });
        }

        if (!isWakeUp) {
            popFragment();
        }
    }

    private void frameAnimationStatus(boolean visible) {
        if (visible) {

            FrameAnimationUtils.getInstance().start(2);
        } else {
            FrameAnimationUtils.getInstance().stop(2);
        }
    }

    private void normalFrameAnimationStatus(boolean visible) {
        if (visible) {
            RobotPersonInfo.getInstance().setRespleep(false);
            handler.removeMessages(START_SEARCH_PEOPLE);
            FrameAnimationUtils.getInstance().start(1);
        } else {
            FrameAnimationUtils.getInstance().stop(1);
        }
    }

    @Override
    public void onStartSkill() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speaking(true);
            }
        });

//        Log.e(TAG, "onStartSkill thread " + Thread.currentThread());
    }

    //当使用stop的时候会出现两次的回复
    private String tempSpeechMessage;

    @Override
    public void onStopSkill() {
//        Log.e(TAG, "onStopSkill thread " + Thread.currentThread());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speaking(false);
                String speechMessage = rainbowTextView.getText().toString();

                if (speechMessage.contains("开始播报") || speechMessage.contains("开始播放")) {
//                    Log.e(TAG, "Constants.content 开始播报: " + Constants.content.length());
//                ReportMediaPlayer.getInstance().play();
                    rainbowTextView.setText("");
                    loadChatVideoControl(1);
                } else if (speechMessage.contains("暂停播报") || speechMessage.contains("暂停播放")
                        || speechMessage.contains("停止播报") || speechMessage.contains("停止播放")
                        ) {
//                    Log.e(TAG, "Constants.content 停止播报: " + Constants.content.length());
                    rainbowTextView.setText("");
                    loadChatVideoControl(2);
//                ReportMediaPlayer.getInstance().pause();
                } else if (speechMessage.contains("停止播报") || speechMessage.contains("继续播放")) {
//                    Log.e(TAG, "Constants.content 停止播报: " + Constants.content.length());
//                ReportMediaPlayer.getInstance().play();
                    rainbowTextView.setText("");
                    loadChatVideoControl(1);
                } else if (speechMessage.contains("重新播报") || speechMessage.contains("重新播放")) {
//                    Log.e(TAG, "Constants.content 重新播报: " + Constants.content.length());
//                ReportMediaPlayer.getInstance().reset();
                    rainbowTextView.setText("");
                    loadChatVideoControl(3);
                } else if (speechMessage.contains("全屏播报") || speechMessage.contains("全屏播放")) {
//                    Log.e(TAG, "Constants.content 全屏播报: " + Constants.content.length());
//                ReportMediaPlayer.getInstance().reset();
                    rainbowTextView.setText("");
                    loadChatVideoControl(4);
                } else if (!TextUtils.isEmpty(speechMessage)) {
                    if (!speechMessage.equals(tempSpeechMessage)) {
                        loadKeywordData(speechMessage);
                    }
                    tempSpeechMessage = speechMessage;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (chatFragment != null) {
                                chatFragment.toOneChat(tempSpeechMessage);
                            }
                            tempSpeechMessage = "";
                        }
                    }, 2000);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handler.removeMessages(100);
                        handler.sendEmptyMessageDelayed(100, SLEEP_CURRENT_TIME);
                        rainbowTextView.setText("");
                    }
                }, 10000);
            }
        });
    }

    private void showSleepView() {
        String s = rainbowTextView.getText().toString();
        handler.removeMessages(START_SEARCH_PEOPLE);
//        if (!FaceSkill.getInstance().isFocus()) {
//        }
        if (TextUtils.isEmpty(s)) {
            sleepLayout.setVisibility(View.VISIBLE);
            normalFrameAnimationStatus(true);
//            speakingLayout.setVisibility(View.INVISIBLE);
            if (isResume) {
                handler.sendEmptyMessageDelayed(START_SEARCH_PEOPLE, 10000);
            }
        } else {
            popFragment();
//            sleepLayout.setVisibility(View.VISIBLE);
            normalFrameAnimationStatus(true);
            sleepLayout.setVisibility(View.GONE);
        }
    }

    public void popFragment() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            popChatFragment();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    popChatFragment();
                }
            });
        }

    }

    private void popChatFragment() {
        if (chatFragment != null) {
            chatFragment.clearData();
        }
        loadRootFragment(R.id.main_fragment_container, mainFragment, false, false);
    }


    @Override
    public void onVolumeChange(int volume) {
//        Log.e(TAG, "onVolumeChange thread " + Thread.currentThread());
    }

    @Override
    public void onQueryEnded(int query) {
//        Log.e(TAG, "onQueryEnded thread " + Thread.currentThread() + " query: " + query);
    }

    @Override
    public void onSendRequest(String reqType, String reqText, String reqParam) {
        String userText = MessageParser.getUserText(reqText);

        if (!TextUtils.isEmpty(userText) && "打开合影".equals(userText)) {
            Intent intent = new Intent(MainActivity.this, CustomCameraActivity.class);
            startActivity(intent);
            return;
        } else if ("visit&interview".equals(reqType)) {
            Intent intent = new Intent(this, ReserveActivity.class);
            startActivityForResult(intent, 1);
            return;
        } else if ("visit&visit".equals(reqType)) {
            Intent intent = new Intent(this, ReserveActivity.class);
            startActivityForResult(intent, 1);
            return;
        }

        if (!chatFragment.isAdded()) {
            loadRootFragment(R.id.main_fragment_container, chatFragment, false, true);
        }

        handler.removeMessages(100);
        handler.sendEmptyMessageDelayed(100, SLEEP_CURRENT_TIME);

        chatFragment.showSendData(userText);

        Log.e(TAG, "onSendRequest default ");

        sleepLayout.setVisibility(View.GONE);
        switch (reqType) {
            case Definition.REQ_SPEECH_WAKEUP:
//                if (sleepLayout.getVisibility() == View.INVISIBLE) {
//                    gridview.setAdapter(adapter);
//                }
//                sleepLayout.setVisibility(View.GONE);
                break;
            case Definition.REQ_SPEECH_SLEEP:

                break;
            case Definition.REQ_CHAT:
                Log.e(TAG, "onSendRequest" + Definition.REQ_CHAT);
                break;
            case Definition.REQ_TELL_ME_WHY:

                break;
            case Constants.REQUEST_TYPE_GUIDE:
                // 导航
                String destination = MessageParser.getDestination(reqText);
                if (!TextUtils.isEmpty(destination)) {
                    navigation(destination, true);
                }
                break;
            case Constants.REQUEST_TYPE_GO_DESTINATION:
                String destinationBack = MessageParser.getLocation(reqText);
                if (!TextUtils.isEmpty(destinationBack)) {
                    navigation(destinationBack, false);
                }
                break;
            default:

                break;
        }
    }

    public void navigation(String destinationName, boolean turnBack) {
        NavigationSkill.getInstance().prepareStartNavigation(destinationName, turnBack, new NavigationSkill.NavigationCallback() {
            @Override
            public void onNavigationStart() {
                speakingLayoutStatus(true);
            }

            @Override
            public void onNavigationEnd() {
                speakingLayoutStatus(false);
            }

            @Override
            public void onNavigationSuccessStart() {
                speakingLayoutStatus(true);
            }

            @Override
            public void onNavigationSuccessEnd() {
                speakingLayoutStatus(false);
            }
        });
    }

    @Override
    public void onComplete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rainbowTextView.setText("");
            }
        });
    }

    @Override
    public void onResult(String value) {
        if (chatFragment != null && chatFragment.isAdded()) {
            chatFragment.showLoadData(value);
        }
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 100:
                showSleepView();
                handler.sendEmptyMessageDelayed(100, SLEEP_CURRENT_TIME);
                break;
            case CONTROL_SLEEP_VISIBILITY:
                sleepLayout.setVisibility(View.GONE);
                break;
            case START_SEARCH_PEOPLE:
                RobotPersonInfo.getInstance().setRespleep(true);
                FaceSkill.getInstance().stopFocusFollow();
                RobotApi.getInstance().resetHead(0, new CommandListener());
                RobotPersonInfo.getInstance().startSearchPerson();
                break;
        }
        return true;
    }
}
