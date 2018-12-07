package com.sdxxtop.robotproject.control;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.sdxxtop.robotproject.control.module.ChargeModule;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.global.Constants;
import com.sdxxtop.robotproject.presenter.MessageManagerPresenter;
import com.sdxxtop.robotproject.skill.FaceSkill;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.sdxxtop.robotproject.socket.WebSocketControl;
import com.sdxxtop.robotproject.socket.send.SendQuestionBean;
import com.sdxxtop.robotproject.utils.MessageParser;
import com.xuxin.entry.ChatWordBean;
import com.xuxin.http.IRequestListener;
import com.xuxin.http.Params;
import com.xuxin.http.RequestCallback;
import com.xuxin.http.RequestExe;

import java.util.ArrayList;
import java.util.List;


/**
 * 处理由ModuleCall回调的消息
 *
 * @author Orion
 * @time 2018/9/14
 */
public class MessageManager implements Handler.Callback, WebSocketControl.ConnectListener {

    private final MessageManagerPresenter managerPresenter;
    private final Handler handler;
    private List<MessageCallBack> callbackList;
    private static final String TAG = "MessageManager";

    private MessageManager() {
        callbackList = new ArrayList<>();
        managerPresenter = new MessageManagerPresenter();
        managerPresenter.setTextListener(textListener);
        handler = new Handler(this);
        WebSocketControl.getInstance().setConnectListener(this);
    }

    public static MessageManager getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static final MessageManager INSTANCE = new MessageManager();
    }

    public Handler getHandler() {
        return handler;
    }

    @Override
    public boolean handleMessage(Message message) {
        exeRequest(message);
        return true;
    }

    public void exeRequest(Message msg) {
        Bundle bundle = msg.getData();
        String type = bundle.getString(Constants.REQUEST_TYPE);
        final String param = bundle.getString(Constants.REQUEST_PARAM);
        String answerText = MessageParser.getAnswerText(param);
        String userText = MessageParser.getUserText(param);

        switch (type) {
            case Constants.REQUEST_TYPE_SPEECH:
                if (wakeUpInterruptListener != null) {
                    wakeUpInterruptListener.onWakeUp();
                }

                //唤醒的时候开启messageIsChat
                MainChatControl.getInstance().setMessageIsChat(true);
                MainChatControl.getInstance().setChat(false);

                managerPresenter.requestTypeSpeechWakeup(param);

//                RobotApi.getInstance().wakeUp(Constants.REQUEST_ID_DEFAULT, angle, new ActionListener() {
//                    @Override
//                    public void onResult(int status, String responseString) throws RemoteException {
//                        super.onResult(status, responseString);
//                        Log.e(TAG, "status : " + status + " responseString: " + responseString);
//                        RobotApi.getInstance().startSearchPerson(1, 1, "", 5000, new ActionListener() {
//
//                            @Override
//                            public void onError(int errorCode, String errorString) throws RemoteException {
//                                super.onError(errorCode, errorString);
//                                RobotApi.getInstance().stopSearchPerson(0);
//                                Log.e(TAG, "startSearchPerson onError " + errorCode + ", " + errorString);
//                            }
//
//                            @Override
//                            public void onResult(int status, String responseString) throws RemoteException {
//                                super.onResult(status, responseString);
//                                Log.e(TAG, "startSearchPerson onResult " + status + ", " + responseString);
//                                RobotApi.getInstance().stopSearchPerson(0);
//                                switch (status) {
//                                    case 1:
//                                        stopGetAllPersonInfo();
//                                        wakeUpPersonInfoListener = new PersonInfoListener() {
//                                            @Override
//                                            public void onResult(int status, String responseString) {
//                                                super.onResult(status, responseString);
//                                                Log.e(TAG, "startGetAllPersonInfo status : " + status + " onStatusUpdate: " + responseString);
//                                                stopGetAllPersonInfo();
//                                            }
//
//                                            @Override
//                                            public void onData(int code, List<Person> data) {
//                                                super.onData(code, data);
//                                                for (Person datum : data) {
//                                                    int id = datum.getId();
//                                                    stopGetAllPersonInfo();
//                                                    FaceSkill.getInstance().startFocusFollow(id, new ActionListener() {
//                                                        @Override
//                                                        public void onResult(int status, String responseString) throws RemoteException {
//                                                            stopGetAllPersonInfo();
//                                                            super.onResult(status, responseString);
//                                                            Log.e(TAG, "onResult startFocusFollow" + status + ", " + responseString);
//                                                        }
//
//                                                        @Override
//                                                        public void onError(int errorCode, String errorString) throws RemoteException {
//                                                            stopGetAllPersonInfo();
//                                                            super.onError(errorCode, errorString);
//                                                            Log.e(TAG, "onError startFocusFollow" + errorCode + ", " + errorString);
//                                                        }
//                                                    });
//                                                }
//                                                Log.e(TAG, "1 startGetAllPersonInfo code : " + code + " onStatusUpdate: " + data.toString());
//                                            }
//                                        };
//                                        RobotApi.getInstance().startGetAllPersonInfo(0, wakeUpPersonInfoListener);
//                                        break;
//                                }
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(int errorCode, String errorString) throws RemoteException {
//                        super.onError(errorCode, errorString);
//                        Log.e(TAG, "WakeUp errorCode : " + errorCode + " errorString: " + errorString);
//                    }
//                });

                break;
            case Constants.REQUEST_TYPE_CRUISE:
                // 巡逻
                break;
//            case Constants.REQUEST_TYPE_GUIDE:
                // 导航
//                String destination = MessageParser.getDestination(param);
//                NavigationSkill.getInstance().startNavigation(destination, callback);
//                break;
//            case Constants.REQUEST_TYPE_SET_LOCATION:
                // 设点
//                String location = MessageParser.getLocation(param);
//                NavigationSkill.getInstance().setLocation(location);
//                break;
            case Constants.REQUEST_TYPE_MOVE:
                Log.d(TAG, "REQUEST_TYPE_MOVE: 调用成功！");
                String destination = MessageParser.getMove(param);
                FaceSkill.getInstance().stopFocusFollow();
//                RobotApi.getInstance().stopGetAllPersonInfo(0, null);
                managerPresenter.requestTypeMove(destination);


                break;
            case Constants.REQUEST_TYPE_ASK:
                managerPresenter.registerTypeAsk();
                break;
            case Constants.REQUEST_TYPE_REGISTER:
                FaceSkill.getInstance().stopFocusFollow();
                String personName = MessageParser.parseRegisterName(param);
                Log.e(TAG, "startRegister personName: " + personName);
                RobotApi.getInstance().startRegister(Constants.REQUEST_ID_DEFAULT,
                        personName, 6 * 1000, 2, 1000,
                        new ActionListener() {
                            @Override
                            public void onResult(int status, String responseString) throws RemoteException {
                                Log.e(TAG, "startRegister onResult: " + status + ", " + responseString);
//                                ToastUtil.onResult(getActivity(), status, responseString);
                                RobotApi.getInstance().stopRegister(0);
                                switch (status) {
                                    case 1:
                                        String remoteName = MessageParser.getRegisterRemoteName(responseString);
                                        SpeechSkill.getInstance().playTxt(remoteName, textListener);
                                        break;
                                    default:
                                        SpeechSkill.getInstance().playTxt("录入失败，离我近一点，咱们再来一次", textListener);
                                        break;
                                }
                            }

                            @Override
                            public void onError(int errorCode, String errorString) throws RemoteException {
                                super.onError(errorCode, errorString);
                                RobotApi.getInstance().stopRegister(0);
                                SpeechSkill.getInstance().playTxt("录入失败", textListener);
                                Log.e(TAG, "startRegister onError: " + errorCode + ", " + errorString);
                            }
                        });
                break;
            default:
                if (!MainChatControl.getInstance().isMessageIsChat()) {
                    Log.e(TAG, "payText: isMessageIsChat = " + MainChatControl.getInstance().isMessageIsChat());
                    return;
                }

                if (isPause) {
                    return;
                }

                for (MessageCallBack messageCallBack : callbackList) {
                    messageCallBack.exeRequest(type, param, answerText);
                }

                if (!TextUtils.isEmpty(answerText)) {
                    if ("打开合影".equals(userText)) {
                        return;
                    }
                    loadToSocket(userText);
                }
                break;
        }

//        if (callbackList.size() == 0) {
//            return;
//        }

        if (isPause) {
            return;
        }

//        for (MessageCallBack messageCallBack : callbackList) {
//            messageCallBack.exeRequest(type, param, answerText);
//        }

//        // 文字播报
//        if (!TextUtils.isEmpty(answerText)) {
//
//            if ("打开合影".equals(userText)) {
//                return;
//            }
//
////            loadData(userText);
////            loadToSocket(userText);
//        }
    }

    public synchronized void loadToSocket(String userText) {
        if (WebSocketControl.getInstance().isOpen() && App.getInstance().isZuJianSwitch()) {
            String value = SendQuestionBean.toSendQuestion(userText);
            Log.e(TAG, "loadToSocket : " + value);
            WebSocketControl.getInstance().sendMessage(value);
        } else { //假设长连接失效了
            loadData(userText);
        }
    }

    @Override
    public void onMessage(String answer) {
        Log.e(TAG, " MessageManager WebSocketControl : " + answer);
        if (!TextUtils.isEmpty(answer)) {
            payText(answer);
        }
    }

    public synchronized void loadData(String value) {
        Params params = new Params();
        params.put("it", value);
//        params.put("tp", Constants.TYPE_PROJECT);
        String data = params.getData();
        Log.e(TAG, "params data = " + data);
        RequestExe.createRequest().postChatXiaoYun(data).enqueue(new RequestCallback<>(new IRequestListener<ChatWordBean>() {
            @Override
            public void onSuccess(ChatWordBean chatWordBean) {
//                isRequesting = false;
                ChatWordBean.DataEntry data = chatWordBean.getData();
                Log.e(TAG, "data = " + data);
                if (data != null) {
                    String answer = data.getAnswer();
                    if (!TextUtils.isEmpty(answer)) {
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

    public void payText(String answerText) {
        for (MessageCallBack messageCallBack : callbackList) {
            messageCallBack.onResult(answerText);
        }

        if (!MainChatControl.getInstance().isMessageIsChat()) {
            Log.e(TAG, "payText: isMessageIsChat = " + MainChatControl.getInstance().isMessageIsChat());
            return;
        }
        SpeechSkill.getInstance().playTxt(answerText, new TextListener() {
            @Override
            public void onStop() {
                if (speakingListener != null) {
                    speakingListener.onStop();
                }
            }

            @Override
            public void onStart() {
                if (speakingListener != null) {
                    speakingListener.onStart();
                }
                Log.e(TAG, "onStart: 开始");
            }

            @Override
            public void onError() {
                Log.e(TAG, "onError: 错误");
                if (speakingListener != null) {
                    speakingListener.onStop();
                }
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: 完成");
                if (speakingListener != null) {
                    speakingListener.onStop();
                }

                for (MessageCallBack messageCallBack : callbackList) {
                    messageCallBack.onComplete();
                }
            }
        });
    }

    private boolean isPause;

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public String getString(int stringId) {
        return App.getInstance().getString(stringId);
    }

    public synchronized void addCallBack(MessageCallBack skillCallback) {
        this.callbackList.add(skillCallback);
    }

    public synchronized void removeCallBack(MessageCallBack skillCallback) {
        this.callbackList.remove(skillCallback);
    }

    public void exeCMD(Message message) {
    }

    public void exeHWReport(Message message) {
    }

    public interface MessageCallBack {
        void exeRequest(String type, String param, String answerText);

        void onResult(String value);

        void onComplete();
    }

    private WakeUpInterruptListener wakeUpInterruptListener;

    public void setWakeUpInterruptListener(WakeUpInterruptListener wakeUpInterruptListener) {
        this.wakeUpInterruptListener = wakeUpInterruptListener;
    }

    public interface WakeUpInterruptListener {
        void onWakeUp();
    }

    private FindPersonListener findPersonListener;

    public void setFindPersonListener(FindPersonListener findPersonListener) {
        this.findPersonListener = findPersonListener;
    }

    public interface FindPersonListener {
        void findPeople();
    }

    private SpeakingListener speakingListener;

    public void setSpeakingListener(SpeakingListener speakingListener) {
        this.speakingListener = speakingListener;
    }

    public interface SpeakingListener {
        void onStart();

        void onStop();
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
}
