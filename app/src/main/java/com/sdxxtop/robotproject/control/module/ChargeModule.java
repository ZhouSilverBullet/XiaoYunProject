package com.sdxxtop.robotproject.control.module;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.sdxxtop.robotproject.skill.NavigationSkill;
import com.sdxxtop.robotproject.skill.SpeechSkill;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class ChargeModule {
    public static final String TAG = "ChargeModule";

    private static ChargeModule chargeModule;
    private BatteryChangedReceiver batteryChangedReceiver;
    private volatile boolean isAutoCharging;
    private volatile boolean isCharging;
    private AtomicInteger mFailCount;
    private Handler handler;

    public static ChargeModule getInstance() {
        if (chargeModule == null) {
            synchronized (ChargeModule.class) {
                if (chargeModule == null) {
                    chargeModule = new ChargeModule();
                }
            }
        }
        return chargeModule;
    }

    private ChargeModule() {
        mFailCount = new AtomicInteger(0);
        handler = new Handler();
    }

    public void init(Context context) {
        batteryChangedReceiver = new BatteryChangedReceiver();
        IntentFilter intentFilter = getFilter();
        context.registerReceiver(batteryChangedReceiver, intentFilter);
    }

    public void onDestroy(Context context) {
        if (batteryChangedReceiver != null && context != null) {
            context.unregisterReceiver(batteryChangedReceiver);
            batteryChangedReceiver = null;
        }
    }

    public boolean isAutoCharging() {
        return isAutoCharging;
    }

    ///获取IntentFilter对象
    private IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_BATTERY_LOW);
        filter.addAction(Intent.ACTION_BATTERY_OKAY);
        return filter;
    }

    public class BatteryChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            final String action = intent.getAction();
            if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_CHANGED)) {
                // 电池当前的电量, 它介于0和 EXTRA_SCALE之间
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                String curLevel = String.valueOf(level);
                Log.e(TAG, "当前的电量： " + curLevel);
//                // 电池电量的最大值
//                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//                Log.e(TAG, "电量的最大值： " + String.valueOf(scale));
                // 当前手机使用的是哪里的电源
                int pluged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                switch (pluged) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        // 电源是AC charger.[应该是指充电器]
                        Log.e(TAG, "电源是AC charger.");
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        // 电源是USB port
                        Log.e(TAG, "电源是USB port");
                        break;
                    default:
                        break;
                }

                handleLevel(level);

            } else if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_LOW)) {
                // 表示当前电池电量低
            } else if (action.equalsIgnoreCase(Intent.ACTION_BATTERY_OKAY)) {
                // 表示当前电池已经从电量低恢复为正常
                System.out.println(
                        "BatteryChangedReceiver ACTION_BATTERY_OKAY---");
            }
        }

    }

    private volatile boolean isLevelCanCharge;

    private void handleLevel(int level) {
        if (level <= 20 && !isLevelCanCharge && NavigationSkill.getInstance().getNavigationStatus() == NavigationSkill.NavigationStatus.Idle) {
            isLevelCanCharge = true;
            Log.e(TAG, "该充电了。。。");
            queryChargeEstimate();
        } else {
//            isLevelCanCharge = false;
//            SpeechSkill.getInstance().playTxt("正在交流");
        }
    }

    public void setCharging() {
        if (isCharging) {
            return;
        }
        //先等于true
        isCharging = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isCharging = false;
            }
        }, 10000);
    }

    private RobotApi checkRobotApiConnection() {
        RobotApi robotApi = RobotApi.getInstance();
        if (robotApi != null && robotApi.isApiConnectedService()) {
            Log.e(TAG, "checkRobotApiConnection OK");
            return robotApi;
        } else {
            Log.e(TAG, "checkRobotApiConnection NULL , robotApi : " + robotApi);
            return null;
        }
    }

    public void startAutoChargeAction() {
        if (!isCharging && !isAutoCharging) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    isLevelCanCharge = false;
                }
            }, 30000);
            SpeechSkill.getInstance().getSkillApi().playText("本宝宝快没电了，要去充电了", new TextListener() {
                @Override
                public void onComplete() {
                    super.onComplete();
                    startNavigation();
                }
            });
        } else {
//            SpeechSkill.getInstance().playTxt("正在交流");
        }
    }

    private void startNavigation() {
        NavigationSkill.getInstance().startNavigation("充电桩", false, false, new NavigationSkill.NavigationCallback() {
            @Override
            public void onNavigationStart() {
            }

            @Override
            public void onNavigationEnd() {
            }

            @Override
            public void onNavigationSuccessStart() {
            }

            @Override
            public void onNavigationSuccessEnd() {
                autoChargeAction();
            }
        });
    }

    public void autoChargeAction() {
        RobotApi robotApi = this.checkRobotApiConnection();
        if (robotApi != null) {
            isAutoCharging = true;
            robotApi.startNaviToAutoChargeAction(0, 30000L, new ActionListener() {
                public void onError(int errorCode, String errorString) throws RemoteException {
                    super.onError(errorCode, errorString);
                    isAutoCharging = false;
                    SpeechSkill.getInstance().playTxt("onError: " + errorCode + ", msg: " + errorString);
                    Log.e(TAG, "startNaviToAutoCharge onError code = " + errorCode + ",msg = " + errorString);
                }

                public void onResult(int status, String responseString) throws RemoteException {
                    super.onResult(status, responseString);
                    Log.e(TAG, "startNaviToAutoCharge onResult status = " + status + ", msg = " + responseString);
                    isAutoCharging = false;

                    Log.e(TAG, "startNaviToAutoCharge onResult status = " + status + ", msg = " + responseString);
                    if (status == 1) {
                        isAutoCharging = true;
                        Log.e(TAG, "auto charge success ------------");
                        ChargeModule.this.finishAutoCharge(1);
                    } else {
                        if (status == 2) {
                            mFailCount.incrementAndGet();
                            if (ChargeModule.this.mFailCount.get() < 3) {
                                Log.e(TAG, "auto charge fail , need to startAutoChargeAction again");
                                ChargeModule.this.isAutoCharging = false;
//                                ChargeModule.this.mCurState = ChargeModule.State.IDLE;
                                autoChargeAction();
                                return;
                            }

                            Log.d(TAG, "auto charge fail after 3 count");
                            SpeechSkill.getInstance().playTxt("已经连续回充三次,失败,请将我缓慢推回充电桩");
                            ChargeModule.this.finishAutoCharge(2);
                            return;
                        }

                        if (status == 3) {
                            SpeechSkill.getInstance().playTxt("onResult: " + status + ", msg" + responseString);
                            ChargeModule.this.isAutoCharging = false;
//                            UIController.getsInstance().play(EMOJI_TYPE.TYPE_SMILE);
//                            ChargeModule.this.stopModule(status);
                        }
                    }
                }

            });
        }
    }

    public void stopAutoChargeAction(Runnable runnable) {
        RobotApi robotApi = checkRobotApiConnection();
        if (robotApi != null) {
            if (isAutoCharging) {
                Log.e(TAG, "stopChargingNormal  goForward ");
                robotApi.goForward(0, 0.2F, 0.1F, new CommandListener() {
                    public void onResult(int var1, String var2) {
                        super.onResult(var1, var2);
                        Log.e(TAG, "stopChargingNormal motionArc status = " + var1 + " , msg = " + var2);
//                            ChargeModule.this.stopModule(ChargeModule.this.mCurResult);
                        isAutoCharging = false;
                        stopAutoChargeAction();
                        if (runnable != null) {
                            runnable.run();
                        }
                    }
                });
            } else {
                stopAutoChargeAction();
                if (runnable != null) {
                    runnable.run();
                }
            }
            isAutoCharging = false;
        }
    }

    public void stopAutoChargeAction() {
        RobotApi robotApi = checkRobotApiConnection();
        if (robotApi != null) {
            robotApi.stopAutoChargeAction(0, false);
        }
    }

    private void finishAutoCharge(int var1) {
        Log.e(TAG, "finishAutoCharge  status = " + var1);

        mFailCount.set(0);
        if (1 == var1) {
//            this.mCurState = ChargeModule.State.IS_CHARGE;
//            UIController.getsInstance().play(EMOJI_TYPE.TYPE_SMILE);
            SpeechSkill.getInstance().playTxt("自动充电成功");
            isAutoCharging = true;
//            this.mCurResult = var1;
        } else if (2 == var1) {
            isAutoCharging = false;
//            this.stopModule(var1);
//            this.sendSetChargePileRequest(2);
        } else {
            isAutoCharging = false;
        }
    }

    public void queryChargeEstimate() {
        RobotApi var1 = this.checkRobotApiConnection();
        if (var1 != null) {
            var1.isRobotEstimate(0, new CommandListener() {
                public void onResult(int var1, String var2) {
                    super.onResult(var1, var2);
                    Log.e(TAG, "startChargeAction result : " + var1 + ", isRobotEstimate :" + var2);
                    switch (var1) {
                        case 0:
                        case 2:
                            SpeechSkill.getInstance().playTxt("定位失败");
                            ChargeModule.this.finishAutoCharge(101);
                            Log.e(TAG, "isRobotEstimate error ");
                            return;
                        case 1:
                            if ("true".equals(var2)) {
                                ChargeModule.this.startAutoChargeAction();
                                return;
                            }

                            SpeechSkill.getInstance().playTxt("定位失败");
                            ChargeModule.this.finishAutoCharge(101);
                            return;
                        default:
                    }
                }
            });
        }
    }

}
