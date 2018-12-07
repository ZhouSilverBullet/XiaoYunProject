package com.sdxxtop.robotproject.global;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.ainirobot.coreservice.client.ApiListener;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.speech.SkillApi;
import com.ainirobot.coreservice.client.speech.SkillCallback;
import com.sdxxtop.robotproject.bean.SkillBean;
import com.sdxxtop.robotproject.control.MessageManager;
import com.sdxxtop.robotproject.control.module.ChargeModule;
import com.sdxxtop.robotproject.presenter.iview.SkillView;
import com.sdxxtop.robotproject.service.ModuleCallback;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.sdxxtop.robotproject.socket.WebSocketControl;
import com.sdxxtop.robotproject.strip.StripController;
import com.sdxxtop.robotproject.utils.Sources;
import com.xuxin.configure.UtilSession;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.fragmentation.Fragmentation;
import me.yokeyword.fragmentation.helper.ExceptionHandler;

import static com.sdxxtop.robotproject.ImageViewActivity.REFRESH;


/**
 * Created by Administrator on 2018/9/16.
 */

public class App extends MultiDexApplication implements Handler.Callback, MessageManager.MessageCallBack {
    public static final String TAG = "App";
    private static App instance;
    private Handler handler;
    //skill初始化是否成功
    public boolean isRobotInitSuccess;
    public Runnable runnable;
    public Runnable robotRunnable;
    public Runnable imgRefreshRunnable;

    //竹间的长连接的控制开关
    private boolean isZuJianSwitch;

    public static final int SKILL_INI_SUCCESS = 10;
    public static final int ROBOT_INI_SUCCESS = 11;
    private SkillApi skillApi;
    private MySkillCallback mSkillCallback = new MySkillCallback();
    private List<SkillView> skillViewList = new ArrayList<>(0);
    private ModuleCallback mModuleCallback;
    private ApiListener apiListener;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new Handler(this);
        UtilSession.getInstance().init(this);
        initSkill();
        SpeechSkill.getInstance().setSkillApi(skillApi);
        initRobot();
        MessageManager.getInstance().addCallBack(this);
        mModuleCallback = new ModuleCallback(this);
        WebSocketControl.getInstance();
//        Fragmentation.builder()
//                .stackViewMode(Fragmentation.BUBBLE)
//                .debug(true) // 实际场景建议.debug(BuildConfig.DEBUG)
//                .handleException(new ExceptionHandler() {
//                    @Override
//                    public void onException(Exception e) {
//                    }
//                })
//                .install();
        registerGlobal();
        StripController.initStripSdk(this);
    }

    private void registerGlobal() {
        ChargeModule.getInstance().init(instance);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        WebSocketControl.getInstance().disConnect();
    }

    private void initSkill() {
        skillApi = new SkillApi();
        skillApi.connectApi(this, new ApiListener() {
            @Override
            public void handleApiDisabled() {
            }

            @Override
            public void handleApiConnected() {
                skillApi.setRecognizable(true);
                skillApi.setRecognizeMode(true);
                skillApi.registerCallBack(mSkillCallback);
            }

            @Override
            public void handleApiDisconnected() {
            }
        });
    }

    public SkillApi getSkillApi() {
        return skillApi;
    }

    private void initRobot() {
        apiListener = new ApiListener() {
            @Override
            public void handleApiDisabled() {
                Log.e(TAG, " RobotController handleApiDisabled ");
            }

            @Override
            public void handleApiConnected() {
                Log.e(TAG, " RobotController handleApiConnected ");
                isRobotInitSuccess = true;
                handler.sendEmptyMessage(ROBOT_INI_SUCCESS);
                RobotApi.getInstance().registerModule("default", Sources.getPatterns(instance), mModuleCallback);
//                WebSocketControl.getInstance();
            }

            @Override
            public void handleApiDisconnected() {
                Log.e(TAG, " RobotController handleApiDisconnected ");
//                RobotController.getInstance().initRobotApi(App.this, apiListener);
            }
        };
        RobotController.getInstance().initRobotApi(this, apiListener);
    }

    public Handler getHandler() {
        return handler;
    }

    public static App getInstance() {
        return instance;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public void setRobotRunnable(Runnable robotRunnable) {
        this.robotRunnable = robotRunnable;
    }

    public void setimgRefreshRunnable(Runnable imgRefreshRunnable) {
        this.imgRefreshRunnable = imgRefreshRunnable;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case SKILL_INI_SUCCESS:
                if (runnable != null) {
                    runnable.run();
                }
                break;
            case ROBOT_INI_SUCCESS:
                if (robotRunnable != null) {
                    robotRunnable.run();
                }
                break;
            case REFRESH:
                if (imgRefreshRunnable != null) {
                    imgRefreshRunnable.run();
                }
                break;
            case SkillBean.SPEECH_PAR_RESULT:
                for (SkillView callback : skillViewList) {
                    callback.onSpeechParResult((String) message.obj);
                }
                break;
            case SkillBean.START:
                for (SkillView callback : skillViewList) {
                    callback.onStartSkill();
                }
                break;
            case SkillBean.STOP:
                for (SkillView callback : skillViewList) {
                    callback.onStopSkill();
                }
                break;
            case SkillBean.VOLUME_CHANGE:
                for (SkillView callback : skillViewList) {
                    callback.onVolumeChange((Integer) message.obj);
                }
                break;
            case SkillBean.QUERY_ENDED:
                for (SkillView callback : skillViewList) {
                    callback.onQueryEnded((Integer) message.obj);
                }
                break;
        }
        return true;
    }

    public void addView(SkillView skillView) {
        skillViewList.add(skillView);
    }

    public void removeView(SkillView skillView) {
        if (skillViewList.contains(skillView)) {
            skillViewList.remove(skillView);
        }
    }

    @Override
    public void exeRequest(final String type, final String param, final String answerText) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (SkillView callback : skillViewList) {
                    callback.onSendRequest(type, param, answerText);
                }
            }
        });
    }

    @Override
    public void onResult(String value) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (SkillView callback : skillViewList) {
                    callback.onResult(value);
                }
            }
        });
    }

    @Override
    public void onComplete() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (SkillView callback : skillViewList) {
                    callback.onComplete();
                }
            }
        });
    }

    private class MySkillCallback extends SkillCallback {

        @Override
        public void onSpeechParResult(final String s) throws RemoteException {
            //用户说话临时识别结果
            Message message = new Message();
            message.what = SkillBean.SPEECH_PAR_RESULT;
            message.obj = s;
            handler.sendMessage(message);
        }

        @Override
        public void onStart() throws RemoteException {
            handler.sendEmptyMessage(SkillBean.START);
            //用户开始说话
        }

        @Override
        public void onStop() throws RemoteException {
            handler.sendEmptyMessage(SkillBean.STOP);
            //用户说话结束
        }

        @Override
        public void onVolumeChange(int i) throws RemoteException {
            Message message = new Message();
            message.what = SkillBean.VOLUME_CHANGE;
            message.obj = i;
            handler.sendMessage(message);
            //用户说话声音大小变化
        }

        @Override
        public void onQueryEnded(int i) throws RemoteException {
            Message message = new Message();
            message.what = SkillBean.QUERY_ENDED;
            message.obj = i;
            handler.sendMessage(message);
            Log.d(TAG, "onQueryEnded: ");
        }
    }

    public void setZuJianSwitch(boolean zuJianSwitch) {
        isZuJianSwitch = zuJianSwitch;
    }

    public boolean isZuJianSwitch() {
        return isZuJianSwitch;
    }
}
