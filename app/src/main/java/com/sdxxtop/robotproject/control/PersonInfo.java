package com.sdxxtop.robotproject.control;

import android.os.RemoteException;
import android.util.Log;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.Person;
import com.ainirobot.coreservice.client.listener.PersonInfoListener;
import com.sdxxtop.robotproject.global.Constants;
import com.sdxxtop.robotproject.skill.FaceSkill;
import com.sdxxtop.robotproject.utils.MessageParser;

import java.util.ArrayList;
import java.util.List;


/**
 * 连接服务后，开始获取然连信息，以唤醒机器
 *
 * @author 高晓峰
 * @time 2018/9/20
 */
public class PersonInfo {

    private static final String TAG = "PersonInfo";

    public enum States {
        IDLE, WAKINGUP, NONE
    }

    private static final float RECOGNIZE_AVAILABLE_DISTANCE = 1f;

    private List<Person> personList;
    private Person mCurrentPerson;
    private Person mTrackingPerson;
    private States mCurrentStates = States.IDLE;
    private MyPersonInfoListener personInfoListener = new MyPersonInfoListener();

    private PersonInfo() {
        personList = new ArrayList<>();
    }

    public static PersonInfo getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static final PersonInfo INSTANCE = new PersonInfo();
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
                    if (mCurrentStates == States.IDLE) {
                        Log.d(TAG, "onData: wakeup id " + mCurrentPerson.getId());
                        // 唤醒
                        RobotApi.getInstance().wakeUp(Constants.REQUEST_ID_DEFAULT,
                                mCurrentPerson.getAngle(), new ActionListener() {
                                    @Override
                                    public void onResult(int status, String responseString) throws RemoteException {
                                        Log.d(TAG, "onData: wakeup status " + status);
                                        if (status == Definition.RESULT_OK) {
                                            mCurrentStates = States.WAKINGUP;
                                            mTrackingPerson = mCurrentPerson;
                                            // 开启焦点跟随
                                            FaceSkill.getInstance().stopFocusFollow();
                                            FaceSkill.getInstance().startFocusFollow(mTrackingPerson.getId(), null);
                                        }
                                    }

                                    @Override
                                    public void onError(int errorCode, String errorString) throws RemoteException {
                                        mCurrentStates = States.IDLE;
                                    }
                                });
                    } else {
//                        if (mTrackingPerson.getId()
//                                != mCurrentPerson.getId()) {
                        Log.d(TAG, "onData: change focus id " + mCurrentPerson.getId());
                        // 停止焦点跟随
                        FaceSkill.getInstance().stopFocusFollow();
                        FaceSkill.getInstance().startFocusFollow(mCurrentPerson.getId(), null);
                        mTrackingPerson = mCurrentPerson;
//                        }
                    }
                }
            } else {
                Log.d(TAG, "onData: " + code + ", no person found.");
                mCurrentStates = States.IDLE;
//                RobotApi.getInstance().stopFocusFollow(Constants.REQUEST_ID_DEFAULT);
//                RobotApi.getInstance().resetHead(Constants.REQUEST_ID_DEFAULT, null);
            }
        }
    }

    /**
     * 开启人脸检测，以唤醒机器
     */
    public void startSearchPerson() {
        Log.d(TAG, "start seach person.");
        RobotApi.getInstance().stopGetAllPersonInfo(Constants.REQUEST_ID_DEFAULT, null);
        RobotApi.getInstance().startGetAllPersonInfo(Constants.REQUEST_ID_DEFAULT, personInfoListener);
    }

}
