package com.sdxxtop.robotproject.skill;

import android.app.Activity;
import android.util.Log;

import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.sdxxtop.robotproject.global.Constants;
import com.sdxxtop.robotproject.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 云台及地盘操作相关
 *
 * @author Orion
 * @time 2018/9/12
 */
public class MoveSkill extends BaseSkill {

    public static final String TAG = "MoveSkill";

    private MoveSkill() {
    }

    public static MoveSkill getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static final MoveSkill INSTANCE = new MoveSkill();
    }

    public void goForward(float speed, float distance, CommandListener listener) {
        RobotApi.getInstance().goForward(Constants.REQUEST_ID_DEFAULT, speed, distance, listener);
    }

    public void goBackward(float speed, float distance, CommandListener listener) {
        RobotApi.getInstance().goBackward(Constants.REQUEST_ID_DEFAULT, speed, distance, listener);
    }

    public void turnLeft(float speed, float distance, CommandListener listener) {
        RobotApi.getInstance().turnLeft(Constants.REQUEST_ID_DEFAULT, 30f, 60f, listener);
    }

    public void turnRight(float speed, float distance, CommandListener listener) {
        RobotApi.getInstance().turnRight(Constants.REQUEST_ID_DEFAULT, 30, 60f, listener);
    }

    public void motionArc(int reqId, float lineSpeed, float angularSpeed, CommandListener listener) {

    }

    public void moveHead(int reqId, String hmode, String vmode, int hangle, int
            vangle, CommandListener listener) {

    }

    public void goPosition(final Activity activity, String positionName) {
//        NavigationSkill.getInstance().startNavigation(positionName);
        RobotApi.getInstance().getLocation(Constants.REQUEST_ID_DEFAULT,
                positionName, new CommandListener() {
                    @Override
                    public void onResult(int result, final String message) {
                        ToastUtil.onResult(activity, result, message);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                goLocation(activity, message);

                            }
                        });
                    }
                });
    }

    private void goLocation(final Activity activity, String message) {
        RobotApi.getInstance().goPosition(Constants.REQUEST_ID_DEFAULT,
                getPoint(message), new CommandListener() {
                    @Override
                    public void onResult(final int result, final String message) {
                        if ("success".equals(message)) {
                            RobotApi.getInstance().stopGoPosition(0);
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.onResult(activity, result, message);
                            }
                        });
                    }

                    @Override
                    public void onStatusUpdate(int status, String data) {
                        super.onStatusUpdate(status, data);
                        Log.e(TAG, "onStatusUpdate: " + status + ", " + data);
                        switch (status) {
                            case 1006:
                                SpeechSkill.getInstance().playTxt("卡住了");
                                break;
                        }
                    }
                });
    }

    /**
     * 格式化坐标形式
     *
     * @param text
     * @return
     */
    private String getPoint(String text) {
        String point = "";
        try {
            JSONObject jsonObject = new JSONObject(text);
            JSONObject pointJson = new JSONObject();
            pointJson.putOpt("x", jsonObject.optString("px"));
            pointJson.putOpt("y", jsonObject.optString("py"));
            pointJson.putOpt("theta", jsonObject.optString("theta"));
            point = pointJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return point;
    }

}
