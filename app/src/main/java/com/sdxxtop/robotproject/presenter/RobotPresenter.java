package com.sdxxtop.robotproject.presenter;

import android.os.Handler;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.coreservice.client.module.ModuleCallbackApi;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.global.RobotController;
import com.sdxxtop.robotproject.presenter.iview.RobotView;
import com.sdxxtop.robotproject.skill.FaceSkill;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/9/19.
 */

public class RobotPresenter implements IPresenter<RobotView> {
    public static final String TAG = "RobotPresenter";
    private RobotView robotView;

    @Override
    public void addView(RobotView robotView) {
        this.robotView = robotView;
    }

    @Override
    public void removeView() {
        if (robotView != null) {
            robotView = null;
        }
    }

    public void registerModule() {
        ArrayList<String> arrayList = new ArrayList<>();
        getRobotApi().registerModule("default", arrayList, new ModuleCallbackApi() {
            @Override
            public boolean onSendRequest(final int reqId, final String reqType, final String reqText, final String reqParam) throws RemoteException {
                Log.e(TAG, "onSendRequest reqId:" + reqId + " reqType: " + reqType + " reqText: " + reqText + " reqParam: " + reqParam);
                runMainThread(new Runnable() {
                    @Override
                    public void run() {
                        robotView.onSendRequest(reqId, reqType, reqText, reqParam);
                    }
                });
//                stopFlow();
                startFlow();
                return super.onSendRequest(reqId, reqType, reqText, reqParam);
            }

            @Override
            public void onHWReport(int hwFunction, String cmdType, String hwReport) throws RemoteException {
                Log.e(TAG, "onHWReport hwFunction:" + hwFunction + " cmdType: " + cmdType + " hwReport: " + hwReport);
                super.onHWReport(hwFunction, cmdType, hwReport);
            }

            @Override
            public void onCmdResponse(int cmdId, String cmdType, String cmdResponse) throws RemoteException {
                Log.e(TAG, "onCmdResponse cmdId:" + cmdId + " cmdType: " + cmdType + " onCmdResponse: " + cmdResponse);
                super.onCmdResponse(cmdId, cmdType, cmdResponse);
            }

            @Override
            public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
                Log.e(TAG, "onTransact code: " + code);
                return super.onTransact(code, data, reply, flags);
            }
        });

    }

    private void stopFlow() {
        FaceSkill.getInstance().stopFocusFollow();
    }

    public void startFlow() {
        FaceSkill.getInstance().startFocusFollow(0, new ActionListener());
    }

    public void startSearchPerson() {
        getRobotApi().startSearchPerson(Definition.ACTION_SEARCH_TARGET, Definition.FOLLOW_FACE_POLICY, "", Definition.DAY, new ActionListener());
    }

    public void registerPeople(String name) {
//        String dir = Environment.getExternalStorageDirectory() + File.separator + "RobotFace";
//        File file = new File(dir);
//        if (!file.exists() && !file.mkdirs()) {
//            skill("创建文件失败");
//            return;
//        }
//        String path = dir + File.separator + System.currentTimeMillis() + ".jpg";
        stopFlow();
        getRobotApi().startRegister(Definition.ACTION_REMOTE_REGISTER, name, 10000, 0, 0, new ActionListener() {
            @Override
            public void onStatusUpdate(int status, String data) throws RemoteException {
                super.onStatusUpdate(status, data);
                Log.e(TAG, "registerPeople data " + data);
            }

            @Override
            public void onResult(int status, String responseString) throws RemoteException {
                super.onResult(status, responseString);
                Log.e(TAG, "registerPeople responseString " + responseString);
            }

            @Override
            public void onError(int errorCode, String errorString) throws RemoteException {
                super.onError(errorCode, errorString);
                Log.e(TAG, "registerPeople errorCode : " + errorCode + " onError " + errorString);
            }
        });
    }

    public void searchPeople(String name) {
//        stopFlow();
//        getRobotApi().startGetAllPersonInfo(Definition.ACTION_HEAD_GET_ALL_PERSON_INFOS, new PersonInfoListener() {
//
//            @Override
//            public void onResult(int status, String responseString) {
//                super.onResult(status, responseString);
//                Log.e(TAG, "searchPeople message " + responseString);
//            }
//
//            @Override
//            public void onData(int code, List<Person> data) {
//                super.onData(code, data);
//                Log.e(TAG, "searchPeople onData " + data);
//                if (data != null) {
//                    for (Person datum : data) {
//                        String name1 = datum.getName();
//                        Log.e(TAG, "searchPeople " + name1);
//                    }
//                }
//            }
//        });
        getRobotApi().getPersonInfoByName(Definition.ACTION_HEAD_GET_PERSON_INFO_BY_NAME, name, "", new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                super.onResult(result, message);
                Log.e(TAG, "searchPeople message " + message);
            }

            @Override
            public void onStatusUpdate(int status, String data) {
                super.onStatusUpdate(status, data);
                Log.e(TAG, "searchPeople data " + data);
            }
        });
//        getRobotApi().searchPersonByName(Definition.ACTION_HEAD_SEARCH_PERSON_BY_NAME, "all", name,100000, new CommandListener(){
//            @Override
//            public void onResult(int result, String message) {
//                super.onResult(result, message);
//                Log.e(TAG, "searchPeople message " + message);
//            }
//
//            @Override
//            public void onStatusUpdate(int status, String data) {
//                super.onStatusUpdate(status, data);
//                Log.e(TAG, "searchPeople data " + data);
//            }
//        });
    }

    public void startNavigation(String positionName) {
        getRobotApi().setMapInfo(176, "09200721", new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                super.onResult(result, message);
                Log.e(TAG, "message: " + message);
            }

            @Override
            public void onStatusUpdate(int status, String data) {
                super.onStatusUpdate(status, data);
                Log.e(TAG, "data: " + data);
            }
        });

        getRobotApi().startNavigation(Definition.ACTION_NAVIGATION, positionName, 0.5f, 20000, new ActionListener() {
            @Override
            public void onError(int errorCode, String errorString) throws RemoteException {
                super.onError(errorCode, errorString);
                if (errorString != null) {
                    skill(errorString);
                }
            }
        });


//        getRobotApi().getMapName(170, new CommandListener() {
//            @Override
//            public void onResult(int result, String message) {
//                super.onResult(result, message);
//            }
//
//            @Override
//            public void onStatusUpdate(int status, String data) {
//                super.onStatusUpdate(status, data);
//            }
//        });
    }

    public void skill(String text) {
        getRobotApi().textToSpeech(text);
    }


    public RobotApi getRobotApi() {
        return RobotController.getInstance().getRobotApi();
    }


    public Handler getHandler() {
        return App.getInstance().getHandler();
    }

    /**
     * 让binder进程回到自己app的主线程
     */
    public void runMainThread(Runnable runnable) {
        if (runnable != null) {
            getHandler().post(runnable);
        }
    }
}
