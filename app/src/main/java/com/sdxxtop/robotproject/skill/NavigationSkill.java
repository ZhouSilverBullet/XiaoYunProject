package com.sdxxtop.robotproject.skill;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.actionbean.Pose;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.sdxxtop.robotproject.bean.ReserveData;
import com.sdxxtop.robotproject.global.Constants;
import com.xuxin.entry.ReserveBean;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 导航相关
 *
 * @author Orion
 * @time 2018/9/12
 */
public class NavigationSkill extends BaseSkill {

    private static final String TAG = "NavigationSkill";
    private Pose mChargingPose;
    private NavigationStatus navigationStatus;

    private NavigationSkill() {
        navigationStatus = NavigationStatus.Idle;
    }

    public static NavigationSkill getInstance() {
        return SingleHolder.INSTANCE;
    }

    private static class SingleHolder {
        public static final NavigationSkill INSTANCE = new NavigationSkill();
    }

    public enum NavigationStatus {
        Navigationing, Idle;
    }

    public NavigationStatus getNavigationStatus() {
        return navigationStatus;
    }

    public void startNavigation(final String destination, boolean turnBack, boolean isNavigation/*是否是导航*/, final NavigationCallback callback) {
        if (navigationStatus == NavigationStatus.Navigationing) {
            playTTS("我正在导航中，可以说，退出导航结束这次导航");
            return;
        }
        navigationStatus = NavigationStatus.Navigationing;
        FaceSkill.getInstance().stopFocusFollow();
        RobotApi.getInstance().resetHead(0, new CommandListener());
        RobotApi.getInstance().startNavigation(Constants.REQUEST_ID_DEFAULT, destination,
                Constants.COORDINATE_DEVIATION, Constants.START_NAVIGATION_TIME_OUT,
                new ActionListener() {
                    @Override
                    public void onError(int errorCode, String errorString) throws RemoteException {
                        Log.e(TAG, "onError: " + errorCode + ", " + errorString);
                        switch (errorCode) {
                            case -116:
                                Log.w(TAG, "startNavigation reqId:" + 0 + " error, not estimate!");
                                playTTS("请先定位");
//                                release(var2, 101, (Bundle)null);
                                break;
                            case -113:
                                Log.w(TAG, "error in destination, reqId: " + 0 + " errorString: " + errorString);
                                if (isNavigation) {
                                    playTTS("这里就是" + destination);
                                }
                                if (callback != null) {
                                    callback.onNavigationSuccessEnd();
                                }
                                break;
                            case -109:
                                Log.w(TAG, "error destination can't arrive, reqId: " + 0 + " errorString: " + errorString);
                                playTTS(destination + "无法到达");
                                break;
                            case -108:
                                Log.w(TAG, "error destination not exist, reqId: " + 0 + " errorString: " + errorString);
                                playTTS("没有设定" + destination);
                                break;
                            case -101:
                                Log.w(TAG, "error resource locked, reqId: " + 0 + " errorString: " + errorString);
                                playTTS("还未连接到底盘，请稍后再试");
                                break;
                            case -6:
                            case -1:
                                Log.w(TAG, "maybe other module is in navigation, reqId: " + 0 + " errorString: " + errorString);
                                playTTS("我的腿动不了了, 请检查当前模式");
                                break;
                            default:
                                Log.e(TAG, "startNavigation::onError() reqId: " + 0 + " unknown errorCode: " + errorCode + "  errorString: " + errorString);
                        }

                        stopNavigation(turnBack);
                    }

                    @Override
                    public void onResult(int status, String responseString) throws RemoteException {
                        Log.e(TAG, "onResult: " + status + ", " + responseString);
                        switch (status) {
                            case Definition.RESULT_OK:
                                if (callback != null) {
                                    callback.onNavigationSuccessStart();
                                }

                                if (isNavigation) {
                                    ReserveBean.DataBean dataBean = ReserveData.getInstance().pop();
                                    if (dataBean != null && !TextUtils.isEmpty(destination)
                                            && destination.equals(dataBean.getPlace()) && !TextUtils.isEmpty(dataBean.getEnd_word())) {
                                        SpeechSkill.getInstance().getSkillApi().playText(dataBean.getEnd_word(), new TextListener() {
                                            @Override
                                            public void onComplete() {
                                                super.onComplete();
                                                if (callback != null) {
                                                    callback.onNavigationSuccessEnd();
                                                }
                                            }
                                        });
                                    } else {
                                        if (turnBack) {
                                            SpeechSkill.getInstance().getSkillApi().playText("已经到达了" + destination + ",我回接待点啦", new TextListener() {
                                                @Override
                                                public void onComplete() {
                                                    super.onComplete();
                                                    if (callback != null) {
                                                        callback.onNavigationSuccessEnd();
                                                    }
                                                    stopNavigation(true);
                                                }
                                            });
                                        } else {
                                            SpeechSkill.getInstance().getSkillApi().playText("已经到达了" + destination, new TextListener() {
                                                @Override
                                                public void onComplete() {
                                                    super.onComplete();
                                                    if (callback != null) {
                                                        callback.onNavigationSuccessEnd();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    if (callback != null) {
                                        callback.onNavigationSuccessEnd();
                                    }
                                }
                                RobotApi.getInstance().resetHead(Constants.REQUEST_ID_DEFAULT, null);
                                if (!turnBack) {
                                    stopNavigation(turnBack);
                                }
                                break;
                            case Definition.ACTION_RESPONSE_STOP_SUCCESS:
                                stopNavigation(turnBack);
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onStatusUpdate(int status, String data) throws RemoteException {
                        Log.e(TAG, "onResult: " + status + ", " + data);
                        switch (status) {
                            /**
                             * 1015 ：Definition.STATUS_START_CRUISE 开始巡航
                             * 1016 ：Definition.STATUS_GOAL_OCCLUDED 目标点被占用
                             * 1017 ：Definition.STATUS_GOAL_OCCLUDED_END 目标点被占用结束
                             * 1018 ：Definition.STATUS_NAVI_AVOID 障碍物堵住行进路线
                             * 1019 ：Definition.STATUS_NAVI_AVOID_END 障碍物移除
                             * 1021 ：Definition.STATUS_CRUISE_REACH_POINT 到达某一目标点，message为目标点下标
                             */
                            case Definition.STATUS_START_CRUISE:
//                                speechPlayText("请让一下");
                                break;
                            case Definition.STATUS_GOAL_OCCLUDED:
//                                speechPlayText("请让一下");
                                break;
                            case Definition.STATUS_GOAL_OCCLUDED_END:
//                                speechPlayText("请让一下");
                                break;
                            case Definition.STATUS_NAVI_AVOID:
                                speechPlayText("请让一下");
                                break;
                            case Definition.STATUS_NAVI_AVOID_END:
//                                speechPlayText("请让一下");
                                break;
                            case Definition.STATUS_CRUISE_REACH_POINT:
//                                speechPlayText("请让一下");
                                break;
//                            default:
//                                speechPlayText("请让一下66");
//                                break;
                        }
                    }
                });
    }

    public void playTTS(String value) {
        SpeechSkill.getInstance().playTxt(value);
    }

    public void prepareStartNavigation(final String destination, boolean turnBack, final NavigationCallback callback) {
        if (callback != null) {
            callback.onNavigationStart();
        }
        SpeechSkill.getInstance().getSkillApi().playText("好的，正在前往" + destination + "，请跟我来吧！", new TextListener() {
            @Override
            public void onComplete() {
                super.onComplete();
                if (callback != null) {
                    callback.onNavigationEnd();
                }
                startNavigation(destination, turnBack, true, callback);
            }
        });
    }

    public void stopNavigation(boolean turnBack) {
        navigationStatus = NavigationStatus.Idle;
        RobotApi.getInstance().stopNavigation(Constants.REQUEST_ID_DEFAULT);
        if (turnBack) {
            startNavigation("接待点", false, true, null);
        }
    }

    public void setLocation(String param) {
        RobotApi.getInstance().setLocation(Constants.REQUEST_ID_DEFAULT,
                param, new CommandListener() {
                    @Override
                    public void onResult(int result, String message) {
                        Log.d(TAG, "onResult: " + result + ", " + message);
                        if (result == Definition.RESULT_OK) {
                            SpeechSkill.getInstance().playTxt("设置成功");
                            RobotApi.getInstance().postSetPlaceToServer(0, param);
                        } else {
                            SpeechSkill.getInstance().playTxt("设置失败");
                        }
                    }
                });
    }

    public void getLocation(String param) {
        RobotApi.getInstance().getLocation(Constants.REQUEST_ID_DEFAULT, param, new CommandListener() {
            public void onResult(int var1x, String var2) {
                super.onResult(var1x, var2);
                Log.i(TAG, "getLocation result:" + var1x + " message:" + var2);
                switch (var1x) {
                    case 0:
                    case 2:
                        SpeechSkill.getInstance().playTxt("没有设置充电桩位置");
                        return;
                    case 1:
                        try {
                            JSONObject var3 = new JSONObject(var2);
                            if (mChargingPose == null) {
                                mChargingPose = new Pose();
                            }

                            if (!var3.optBoolean("siteexist", false)) {
                                SpeechSkill.getInstance().playTxt("没有设置充电桩位置");
                                return;
                            }

                            mChargingPose.setX((float) var3.optDouble("px", 0.0D));
                            mChargingPose.setY((float) var3.optDouble("py", 0.0D));
                            mChargingPose.setTheta((float) var3.optDouble("theta", 0.0D));
                            startRobotEstimate();
                            return;
                        } catch (JSONException var5) {
                            var5.printStackTrace();
                            SpeechSkill.getInstance().playTxt("没有设置充电桩位置");
                            return;
                        }
                    default:
                }
            }
        });
    }

    public void goPosition(String position, CommandListener listener) {
        RobotApi.getInstance().goPosition(Constants.REQUEST_ID_DEFAULT, position, listener);
    }

    private void speechPlayText(String value) {
        SpeechSkill.getInstance().playTxt(value);
    }

    public void startRobotEstimate() {
        RobotApi.getInstance().isRobotEstimate(Constants.REQUEST_ID_DEFAULT, new CommandListener() {
            public void onResult(int var1, String var2) {
                super.onResult(var1, var2);
                Log.i(TAG, "isRobotEstimate result:" + var1 + " message:" + var2);
                switch (var1) {
                    case 0:
                    case 2:
                        speechPlayText("请让机器人背部朝向充电桩");
//                        RepositionModule.this.showEstimateView(3);
                        return;
                    case 1:
                        if (!TextUtils.isEmpty(var2) && "true".equals(var2)) {
                            try {
                                speechPlayText("定位状态正常，无需重定位");
                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return;
                            }
                        }

                        speechPlayText("请让机器人背部朝向充电桩");
//                        RepositionModule.this.showEstimateView(3);
                        return;
                    default:
                        speechPlayText("请让机器人背部朝向充电桩");
//                        RepositionModule.this.showEstimateView(3);
                }
            }
        });
    }

    public void setPoseLocation(String param, CommandListener listener) {
        RobotApi.getInstance().setPoseLocation(Constants.REQUEST_ID_DEFAULT, param, listener);
    }

    public void setPoseEstimate(String param, CommandListener listener) {
        RobotApi.getInstance().setPoseEstimate(Constants.REQUEST_ID_DEFAULT, param, listener);
    }

    public void saveRobotEstimate(CommandListener listener) {
        RobotApi.getInstance().saveRobotEstimate(Constants.REQUEST_ID_DEFAULT, listener);
    }

    public void getPlaceName(String param, CommandListener listener) {
        RobotApi.getInstance().getPlace(Constants.REQUEST_ID_DEFAULT, param, listener);
    }

    public void getPlaceList(CommandListener listener) {
        RobotApi.getInstance().getPlaceList(Constants.REQUEST_ID_DEFAULT, listener);
    }

    public void isRobotInlocations(String param, CommandListener listener) {
        RobotApi.getInstance().isRobotInlocations(Constants.REQUEST_ID_DEFAULT, param, listener);
    }

    public void isRobotEstimate(CommandListener listener) {
        RobotApi.getInstance().isRobotEstimate(Constants.REQUEST_ID_DEFAULT, listener);
    }

    public interface NavigationCallback {
        void onNavigationStart();

        void onNavigationEnd();

        void onNavigationSuccessStart();

        void onNavigationSuccessEnd();
    }

}
