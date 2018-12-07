package com.sdxxtop.robotproject.skill;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.actionbean.LeadingParams;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.coreservice.client.listener.PersonInfoListener;
import com.sdxxtop.robotproject.global.Constants;

import org.json.JSONObject;


/**
 * 视觉相关
 *
 * @author Orion
 * @time 2018/9/12
 */
public class FaceSkill extends BaseSkill {

    private static final String TAG = "FaceSkill";
    private volatile boolean focus = false;

    private FaceSkill() {
    }

    public static FaceSkill getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static final FaceSkill INSTANCE = new FaceSkill();
    }

    public void register(final String name) {
        RobotApi.getInstance().startRegister(0, name, 6 * 1000, 3, 1000, new ActionListener() {
            @Override
            public void onResult(int status, String response) throws RemoteException {
                switch (status) {
                    case Definition.RESULT_OK: {
                        handlerResult(response, name, true);
                        break;
                    }
                    case Definition.RESULT_FAILURE: {
                        handlerResult(response, name, false);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        });
    }

    private void handlerResult(String response, String name, boolean isSuccess) {
        if (isSuccess) {
            try {
                if (!TextUtils.isEmpty(response)) {
                    JSONObject json = new JSONObject(response);
                    String remoteType = json.optString(Definition.REGISTER_REMOTE_TYPE);
                    String remoteName = json.optString(Definition.REGISTER_REMOTE_NAME);
                    Log.d(TAG, "handlerResult remoteType = " + remoteType + ", remoteName = " + remoteName);
                    if (Definition.REGISTER_REMOTE_SERVER_EXIST.equals(remoteType) && !TextUtils.isEmpty(remoteName)) {

                    } else if (Definition.REGISTER_REMOTE_SERVER_NEW.equals(remoteType) && !TextUtils.isEmpty(remoteName)) {

                    } else if (Definition.REGISTER_DETECT_EXIST.equals(remoteType) && !TextUtils.isEmpty(remoteName)) {

                    } else {
                        RobotApi.getInstance().stopRegister(0);
                    }
                }
            } catch (Throwable e) {
                Log.e(TAG, "handleResultOK e : " + e.getLocalizedMessage());
            }
        }
    }

    public void wakeUp(float angle) {
        RobotApi.getInstance().wakeUp(Constants.REQUEST_ID_DEFAULT, angle, new ActionListener() {
            @Override
            public void onResult(int status, String responseString) throws RemoteException {

            }

            @Override
            public void onError(int errorCode, String errorString) throws RemoteException {

            }
        });
    }

    public void startLead(int reqId, LeadingParams params, ActionListener listener) {
        RobotApi.getInstance().startLead(reqId, params, listener);
    }

    public void stopLead(int reqId, boolean isResetHW) {
        RobotApi.getInstance().stopLead(reqId, isResetHW);
    }

    public void startFocusFollow(int personId, final ActionListener listener) {
        RobotApi.getInstance().startFocusFollow(0, personId,  10000, 2, new ActionListener(){
            @Override
            public void onResult(int status, String responseString) throws RemoteException {
                super.onResult(status, responseString);
                switch (status) {
                    case 1:
                        focus = true;
                        break;
                }
                if (listener != null) {
                    listener.onResult(status, responseString);
                }
            }

            @Override
            public void onStatusUpdate(int status, String data) throws RemoteException {
                super.onStatusUpdate(status, data);
                if (listener != null) {
                    listener.onStatusUpdate(status, data);
                }
            }

            @Override
            public void onError(int errorCode, String errorString) throws RemoteException {
                super.onError(errorCode, errorString);
                if (listener != null) {
                    listener.onError(errorCode, errorString);
                }
            }
        });
    }

    public void stopFocusFollow() {
        RobotApi.getInstance().stopFocusFollow(0);
        focus = false;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setTrackTarget(int reqId, String name, int id,
                               Definition.TrackMode mode, CommandListener listener) {
        RobotApi.getInstance().setTrackTarget(reqId, name, id, mode, listener);
    }

    public void startSearchPerson(int reqId, int cameraType, String personName,
                                  long timeout, ActionListener listener) {
        RobotApi.getInstance().startSearchPerson(reqId, cameraType, personName, timeout, listener);
    }

    public void stopSearchPerson(int reqId) {
        RobotApi.getInstance().stopSearchPerson(reqId);
    }

    public void startGetAllPersonInfo(int reqId, PersonInfoListener listener) {
        RobotApi.getInstance().startGetAllPersonInfo(reqId, listener);
    }

    public void stopGetAllPersonInfo(int reqId, PersonInfoListener listener) {
        RobotApi.getInstance().stopGetAllPersonInfo(reqId, listener);
    }

}
