package com.sdxxtop.robotproject.control;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.coreservice.client.listener.Person;
import com.ainirobot.coreservice.client.listener.PersonInfoListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.sdxxtop.robotproject.global.Constants;
import com.sdxxtop.robotproject.skill.FaceSkill;
import com.sdxxtop.robotproject.skill.FollowSkill;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.sdxxtop.robotproject.utils.MessageParser;
import com.sdxxtop.robotproject.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 连接服务后，开始获取然连信息，以唤醒机器
 *
 * @author 高晓峰
 * @time 2018/9/20
 */
public class RobotPersonInfo {

    private static final String TAG = "sdxxtop_RobotPersonInfo";

    public enum States {
        IDLE, WAKINGUP, NONE
    }

    private static final float RECOGNIZE_AVAILABLE_DISTANCE = 1f;

    private List<Person> personList;
    private Person mCurrentPerson;
    private Person mTrackingPerson;
    private boolean isRespleep; //重新进入睡眠后
    private States mCurrentStates = States.IDLE;
    private MyPersonInfoListener personInfoListener = new MyPersonInfoListener();

    private RobotPersonInfo() {
        personList = new ArrayList<>();
    }

    public static RobotPersonInfo getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static final RobotPersonInfo INSTANCE = new RobotPersonInfo();
    }

    public void setRespleep(boolean respleep) {
        isRespleep = respleep;
    }

    public Person getCurrentPerson() {
        return mCurrentPerson;
    }

    public void setCurrentPerson(Person mCurrentPerson) {
        this.mCurrentPerson = mCurrentPerson;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
        mCurrentPerson = MessageParser.getOnePerson(mTrackingPerson, personList, RECOGNIZE_AVAILABLE_DISTANCE);
    }

    public Person getTrackingPerson() {
        return mTrackingPerson;
    }

    @Override
    public String toString() {
        return "PersonInfo{" +
                "mCurrentPerson=" + mCurrentPerson +
                '}';
    }

    class MyPersonInfoListener extends PersonInfoListener {

        private int reqId;

        public void setReqId(int reqId) {
            this.reqId = reqId;
        }

        @Override
        public void onResult(int status, String responseString) {
            Log.d(TAG, "onResult: " + status + ", " + responseString);
        }

        @Override
        public void onData(int code, List<Person> personList) {
            if (personList != null && personList.size() > 0) {
                // 解析人脸信息
                setPersonList(personList);
                if (mCurrentPerson != null) {
                    Log.d(TAG, "onData: code = " + code + ", size = " + personList.size() + ", id = " + mCurrentPerson.getId());
                    RobotApi.getInstance().stopGetAllPersonInfo(Constants.REQUEST_ID_DEFAULT, personInfoListener);
                    isStarting = false;
                    if (mCurrentStates == States.IDLE) {
                        Log.d(TAG, "onData: wakeup id " + mCurrentPerson.getId());
                        // 唤醒
//                        MoveSkill.getInstance().motionArc(0, 0, mCurrentPerson.getAngle(), new CommandListener() {
//                            @Override
//                            public void onResult(int result, String message) {
//                                super.onResult(result, message);
//
//                            }
//                        });

                        RobotApi.getInstance().stopWakeUp(0);
                        RobotApi.getInstance().wakeUp(Constants.REQUEST_ID_DEFAULT,
                                mCurrentPerson.getAngle(), new ActionListener() {
                                    @Override
                                    public void onResult(int status, String responseString) throws RemoteException {
                                        Log.d(TAG, "onData: wakeup status " + status);
                                        RobotApi.getInstance().stopWakeUp(0);
                                        if (status == Definition.RESULT_OK) {
                                            isSpeaking = false;

                                            mCurrentStates = States.WAKINGUP;
                                            mTrackingPerson = mCurrentPerson;
                                            // 开启焦点跟随
                                            FaceSkill.getInstance().stopFocusFollow();
                                            FaceSkill.getInstance().startFocusFollow(mTrackingPerson.getId(), null);

                                            if (!isRespleep) { //不是重新睡眠，唤醒就不在搜索人
                                                return;
                                            }

                                            //不在睡眠期间
                                            isRespleep = false;

                                            if (robotWakeUpListener != null && !isFinding && mCurrentPerson != null) {
                                                isFinding = true;
                                                findPeople(mCurrentPerson.getId());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onError(int errorCode, String errorString) throws RemoteException {
                                        RobotApi.getInstance().stopWakeUp(0);
                                        mCurrentStates = States.IDLE;
                                    }
                                });

                    } else {
                        Log.d(TAG, "onData: change focus id " + mCurrentPerson.getId());
                        // 停止焦点跟随
                        FaceSkill.getInstance().stopFocusFollow();
//                        RobotApi.getInstance().startFocusFollow(Constants.REQUEST_ID_DEFAULT,
//                                mCurrentPerson.getId(), 2000, 2, null);
                        mTrackingPerson = mCurrentPerson;
                    }
                }
            } else {
                Log.d(TAG, "onData: " + code + ", no person found.");
                mCurrentStates = States.IDLE;
                isStarting = true;
                FaceSkill.getInstance().stopFocusFollow();
                RobotApi.getInstance().resetHead(Constants.REQUEST_ID_DEFAULT, null);
            }
        }
    }

    private boolean isSpeaking;
    private boolean isFinding;

    private void findPeople(final int id) {
//        FaceSkill.getInstance().stopFocusFollow();
        RobotApi.getInstance().getPictureById(0, id, 1, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                super.onResult(result, message);
                Log.e(TAG, "getPictureById result: " + result + " message: " + message);
                switch (result) {
                    case 1:
                        List<String> pictures = MessageParser.getPictures(message);
                        RobotApi.getInstance().remoteDetect(0, "" + id, pictures, new CommandListener() {
                            @Override
                            public void onResult(int result, String message) {
                                super.onResult(result, message);
                                Log.e(TAG, "remoteDetect result: " + result + " message: " + message);
                                String personName = MessageParser.getPersonName(message);
                                boolean b = !TextUtils.isEmpty(personName);
                                if (b) {
                                    if (!isSpeaking) {
                                        isSpeaking = true;
                                        SpeechSkill.getInstance().playTxt(NameUtils.getWakeUpName(personName), textListener);
//                                        RobotApi.getInstance().stopTrack(0 , new CommandListener());
//                                        RobotApi.getInstance().setTrackTarget(0, personName, id, Definition.TrackMode.FACE, new CommandListener(){
//                                            @Override
//                                            public void onResult(int result, String message) {
//                                                super.onResult(result, message);
//                                                Log.e(TAG, "setTrackTarget onResult result=" + result + " message=" + message);
//                                            }
//
//                                            @Override
//                                            public void onStatusUpdate(int status, String data) {
//                                                super.onStatusUpdate(status, data);
//                                                Log.e(TAG, "setTrackTarget onStatusUpdate status=" + status + " data=" + data);
//                                            }
//                                        });

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
                                    }
                                } else {
                                    SpeechSkill.getInstance().playTxt("有什么可以帮到您的", textListener);
                                }

                                isFinding = false;
                            }
                        });
                        break;
                    default:
                        SpeechSkill.getInstance().playTxt("有什么可以帮到您的", textListener);
                        isFinding = false;
                        break;
                }
                if (robotWakeUpListener != null) {
                    robotWakeUpListener.wakeUp();
                }
            }
        });
    }

    private boolean isStarting;

    /**
     * 开启人脸检测，以唤醒机器
     */
    public void startSearchPerson() {
        Log.d(TAG, "start seach person.");
        RobotApi.getInstance().stopGetAllPersonInfo(Constants.REQUEST_ID_DEFAULT, null);
        RobotApi.getInstance().startGetAllPersonInfo(Constants.REQUEST_ID_DEFAULT, personInfoListener);
    }

    /**
     * 开启人脸检测，以唤醒机器
     */
    public void stopSearchPerson() {
        Log.d(TAG, "stop search person.");
        if (!isStarting) {
            RobotApi.getInstance().stopGetAllPersonInfo(Constants.REQUEST_ID_DEFAULT, personInfoListener);
        }
    }

    private boolean initSuccess;

    public boolean isInitSuccess() {
        return initSuccess;
    }

    public void setInitSuccess(boolean initSuccess) {
        this.initSuccess = initSuccess;
    }


    private RobotWakeUpListener robotWakeUpListener;

    public void setRobotWakeUpListener(RobotWakeUpListener robotWakeUpListener) {
        this.robotWakeUpListener = robotWakeUpListener;
    }

    public interface RobotWakeUpListener {
        void wakeUp();
    }

    private TextListener textListener = new TextListener() {
        @Override
        public void onStart() {
            if (speakingListener != null) {
                speakingListener.onStart();
            }
        }

        @Override
        public void onStop() {
            if (speakingListener != null) {
                speakingListener.onStop();
            }
        }


        @Override
        public void onError() {
            if (speakingListener != null) {
                speakingListener.onStop();
            }
        }

        @Override
        public void onComplete() {
            if (speakingListener != null) {
                speakingListener.onStop();
            }
        }
    };

    private SpeakingListener speakingListener;

    public void setSpeakingListener(SpeakingListener speakingListener) {
        this.speakingListener = speakingListener;
    }

    public interface SpeakingListener {
        void onStart();

        void onStop();
    }
}
