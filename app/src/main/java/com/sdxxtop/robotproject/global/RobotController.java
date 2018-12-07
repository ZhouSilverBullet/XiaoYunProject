package com.sdxxtop.robotproject.global;

import android.content.Context;

import com.ainirobot.coreservice.client.ApiListener;
import com.ainirobot.coreservice.client.RobotApi;

/**
 * Created by Administrator on 2018/9/19.
 */

public class RobotController {
    public static final String TAG = "RobotController";
    private static RobotController robotController;
    private RobotApi robotApi;

    public static RobotController getInstance() {
        if (robotController == null) {
            robotController = new RobotController();
        }
        return robotController;
    }

    public void initRobotApi(Context context, ApiListener listener) {
        if (robotApi == null) {
            robotApi = RobotApi.getInstance();
        }
        robotApi.connectServer(context, listener);
    }

    public RobotApi getRobotApi() {
        return robotApi;
    }
}
