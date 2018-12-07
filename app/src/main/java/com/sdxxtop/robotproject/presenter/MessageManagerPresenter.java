package com.sdxxtop.robotproject.presenter;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.coreservice.client.listener.Person;
import com.ainirobot.coreservice.client.listener.PersonInfoListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.sdxxtop.robotproject.control.PersonInfo;
import com.sdxxtop.robotproject.control.RobotPersonInfo;
import com.sdxxtop.robotproject.control.module.ChargeModule;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.global.Constants;
import com.sdxxtop.robotproject.skill.FaceSkill;
import com.sdxxtop.robotproject.skill.MoveSkill;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.sdxxtop.robotproject.utils.MessageParser;
import com.sdxxtop.robotproject.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MessageManagerPresenter {
    public static final String TAG = "MessageManagerPresenter";
    private boolean isSpeaking;
    private boolean isStart;
    private TextListener textListener;

    public void setTextListener(TextListener textListener) {
        this.textListener = textListener;
    }

    private PersonInfoListener askPersonInfoListener;
    private PersonInfoListener wakeUpPersonInfoListener;

    /**
     * 注册的type申请的
     */
    public void registerTypeAsk() {
        FaceSkill.getInstance().stopFocusFollow();
        stopGetAllPersonInfo();
        isSpeaking = false;
        askPersonInfoListener = new PersonInfoListener() {
            @Override
            public void onResult(int status, String responseString) {
                super.onResult(status, responseString);
                stopGetAllPersonInfo();
                Log.e(TAG, "startGetAllPersonInfo status : " + status + " onStatusUpdate: " + responseString);
            }

            @Override
            public void onData(int code, List<Person> data) {
                super.onData(code, data);
                ArrayList<String> list = new ArrayList<>();
                for (Person datum : data) {
                    list.add(datum.toGson());
                    final int id = datum.getId();
                    RobotApi.getInstance().getPictureById(0, id, 1, new CommandListener() {
                        @Override
                        public void onResult(int result, String message) {
                            super.onResult(result, message);
                            Log.e(TAG, "getPictureById result: " + result + " message: " + message);
                            App.getInstance().getHandler().removeCallbacks(delayMessageRunnable);
                            isStart = false;
                            switch (result) {
                                case 1:
                                    stopGetAllPersonInfo();
                                    List<String> pictures = MessageParser.getPictures(message);
                                    RobotApi.getInstance().remoteDetect(0, "" + id, pictures, new CommandListener() {
                                        @Override
                                        public void onResult(int result, String message) {
                                            super.onResult(result, message);
                                            Log.e(TAG, "remoteDetect result: " + result + " message: " + message);
                                            String personName = MessageParser.getPersonName(message);
                                            boolean b = !TextUtils.isEmpty(personName);
                                            FaceSkill.getInstance().startFocusFollow(id, new ActionListener() {
                                                @Override
                                                public void onResult(int status, String responseString) throws RemoteException {
                                                    super.onResult(status, responseString);
                                                    Log.e(TAG, " REQUEST_TYPE_ASK onResult startFocusFollow " + status + ", " + responseString);
                                                }

                                                @Override
                                                public void onError(int errorCode, String errorString) throws RemoteException {
                                                    super.onError(errorCode, errorString);
                                                    Log.e(TAG, "REQUEST_TYPE_ASK onError startFocusFollow" + errorCode + ", " + errorString);
                                                }
                                            });
                                            if (b) {
                                                if (!isSpeaking) {
                                                    isSpeaking = true;
                                                    SpeechSkill.getInstance().getSkillApi().playText(NameUtils.getGetName(personName), textListener);
                                                }
                                            } else {
                                                int personError = MessageParser.getPersonError(message);
                                                if (personError == 1) {
                                                    SpeechSkill.getInstance().getSkillApi().playText("我还不认识你了，你能告诉我吗", textListener);
                                                } else {
                                                    SpeechSkill.getInstance().getSkillApi().playText("您离我近一点，再来一次", textListener);
                                                }
                                            }
                                        }
                                    });
                                    break;
                                default:
                                    SpeechSkill.getInstance().getSkillApi().playText("您离我近一点，再来一次", textListener);
                                    break;
                            }
                        }
                    });
                }

                if (data.size() == 0 && !isStart) {
                    isStart = true;
                    App.getInstance().getHandler().postDelayed(delayMessageRunnable, 3000);
                }

                Log.e(TAG, "2 startGetAllPersonInfo code : " + code + " onStatusUpdate: " + list.toString());
            }
        };
        RobotApi.getInstance().startGetAllPersonInfo(0, askPersonInfoListener);
    }

    /**
     * 当识别人请求超时的时候，延迟3s进行回复，"您离我近一点，我才能认识你"
     */
    private Runnable delayMessageRunnable = new Runnable() {
        @Override
        public void run() {
            SpeechSkill.getInstance().getSkillApi().playText("您离我近一点，我才能认识你", textListener);
        }
    };


    /**
     * 统一停止getAllPerson搜索
     */
    private void stopGetAllPersonInfo() {
        if (askPersonInfoListener != null || wakeUpPersonInfoListener != null) {
            if (askPersonInfoListener != null) {
                RobotApi.getInstance().stopGetAllPersonInfo(0, askPersonInfoListener);
                askPersonInfoListener = null;
            }

            if (wakeUpPersonInfoListener != null) {
                RobotApi.getInstance().stopGetAllPersonInfo(0, wakeUpPersonInfoListener);
                wakeUpPersonInfoListener = null;
            }
        } else {
            RobotApi.getInstance().stopGetAllPersonInfo(0, null);
        }
    }

    public void requestTypeSpeechWakeup(String param) {
        if (ChargeModule.getInstance().isAutoCharging()) {
            ChargeModule.getInstance().stopAutoChargeAction(new Runnable() {
                @Override
                public void run() {
                    speechWakeup(param);
                }
            });
        } else {
            ChargeModule.getInstance().stopAutoChargeAction();
            speechWakeup(param);
        }
    }

    private void speechWakeup(String param) {

        ChargeModule.getInstance().setCharging();

        SpeechSkill.getInstance().setPlaying(false);

        //你们请注意我们的实现逻辑   当喊他的时候  先头转  如果找到人   交点跟随  ，然后身体转动
        // FIXME: 2018/9/20 需要地盘同步转动  //relative absolute
        FaceSkill.getInstance().stopFocusFollow();
        RobotApi.getInstance().stopSearchPerson(0);
        RobotApi.getInstance().stopWakeUp(0);
        stopGetAllPersonInfo();
        RobotApi.getInstance().stopMove(0, new CommandListener());
        RobotApi.getInstance().stopCharge(0);
        RobotApi.getInstance().stopNavigation(0);

        final Integer angle = Integer.valueOf(param);
        Log.e(TAG, " exeRequest angle :" + angle);
        RobotApi.getInstance().wakeUp(Constants.REQUEST_ID_DEFAULT, angle, new ActionListener() {
            @Override
            public void onResult(int status, String responseString) throws RemoteException {
                super.onResult(status, responseString);
                switch (status) {
                    case 1:
                        RobotApi.getInstance().stopWakeUp(0);
                        PersonInfo.getInstance().startSearchPerson();
//                        RobotPersonInfo.getInstance().startSearchPerson();
                        break;
                }
            }

            @Override
            public void onError(int errorCode, String errorString) throws RemoteException {
                super.onError(errorCode, errorString);

                Log.e(TAG, "wakeUp onError errorCode :" + errorCode + ", " + errorString);
            }
        });
    }

    public void requestTypeMove(String value) {
        Log.e(TAG, " value : " + value);
        switch (value) {
            case "后退":
                MoveSkill.getInstance().goBackward(0.4f, 1f, new CommandListener() {
                    @Override
                    public void onResult(int result, String message) {
                        super.onResult(result, message);
                        Log.d(TAG, "onResult: " + result + " , " + message);
                    }
                });
                break;
            case "前进":
                MoveSkill.getInstance().goForward(0.4f, 1f, new CommandListener() {
                    @Override
                    public void onResult(int result, String message) {
                        super.onResult(result, message);
                        Log.d(TAG, "onResult: " + result + " , " + message);
                    }
                });
                break;
            case "左":
                MoveSkill.getInstance().turnLeft(30f, 60f, new CommandListener() {
                    @Override
                    public void onResult(int result, String message) {
                        super.onResult(result, message);
                        Log.d(TAG, "onResult: " + result + " , " + message);
                    }
                });
                break;
            case "右":
                MoveSkill.getInstance().turnRight(30f, 60f, new CommandListener() {
                    @Override
                    public void onResult(int result, String message) {
                        super.onResult(result, message);
                        Log.d(TAG, "onResult: " + result + " , " + message);
                    }
                });
                break;
            default:
//                MoveSkill.getInstance().motionArc();
                break;
        }

    }
}
