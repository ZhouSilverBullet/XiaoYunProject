package com.sdxxtop.robotproject.skill;

import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.global.Constants;


/**
 * 充电相关
 *
 * @author Orion
 * @time 2018/9/12
 */
public class ChargeSkill extends BaseSkill {

    private static final String TAG = "ChargeSkill";

    private ChargeSkill() {
    }

    public static ChargeSkill getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static final ChargeSkill INSTANCE = new ChargeSkill();
    }

    public void setStartChargePoseAction() {
        RobotApi.getInstance().setStartChargePoseAction(Constants.REQUEST_ID_DEFAULT, 0, new ActionListener() {
            @Override
            public void onResult(final int status, final String responseString) throws RemoteException {
                super.onResult(status, responseString);
                Log.d(TAG, "setStartChargePoseAction status = " + status + " , response = " + responseString);
                if (Definition.RESULT_OK == status) {
                    SpeechSkill.getInstance().playTxt("设置充电桩成功");
                    Settings.Global.putInt(App.getInstance().getContentResolver(), "充电桩", 1);// for settings use
                    //　get place pose and report to server the pose .
                    RobotApi.getInstance().postSetPlaceToServer(Constants.REQUEST_ID_DEFAULT, Definition.START_BACK_CHARGE_POSE);
                }
            }

            @Override
            public void onError(int errorCode, String errorString) throws RemoteException {
                super.onError(errorCode, errorString);
                Log.d(TAG, "setStartChargePoseAction onError code = " + errorCode + ", msg = " + errorString);
                SpeechSkill.getInstance().playTxt("设置充电桩失败");
            }
        });
    }

    public void startNaviToAutoChargeAction() {
        RobotApi.getInstance().startNaviToAutoChargeAction(Constants.REQUEST_ID_DEFAULT, 3 * 1000, new ActionListener() {
            @Override
            public void onResult(int status, String responseString) throws RemoteException {
                Log.d(TAG, "onResult: " + status + ", " + responseString);
                SpeechSkill.getInstance().playTxt("自动充电成功");
            }

            @Override
            public void onError(int errorCode, String errorString) throws RemoteException {
                Log.e(TAG, "onError: " + errorString + ", " + errorString);
                SpeechSkill.getInstance().playTxt("自动充电失败");
            }
        });
    }

}
