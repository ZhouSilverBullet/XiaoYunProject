/*
 *
 * Copyright (C) 2017 OrionStar Technology Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.sdxxtop.robotproject.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.IBinder;
import android.util.Log;

import com.ainirobot.coreservice.client.ApiListener;
import com.ainirobot.coreservice.client.RobotApi;
import com.sdxxtop.robotproject.control.PersonInfo;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.sdxxtop.robotproject.utils.Sources;


/**
 * 连接核心服务
 *
 * @author 高晓峰
 * @time 2018/9/20
 */

public class ModuleService extends Service {

    private static final String TAG = "ModuleService";

    private static final int NOTIFICATION_ID = 1001;

    private ModuleCallback mModuleCallback;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        Context context = getApplicationContext();
        mModuleCallback = new ModuleCallback(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand this result=" + result);

        Notification notification = new Notification();
        startForeground(NOTIFICATION_ID, notification);

        ConnectivityManager network = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        network.requestNetwork(
                new NetworkRequest.Builder().build(),
                new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        initRobotApi();
                    }
                });
        return START_NOT_STICKY;
    }

    public void initRobotApi() {
        Log.d(TAG, "initRobotApi");
        RobotApi.getInstance().connectServer(this, new ApiListener() {
            @Override
            public void handleApiDisabled() {
            }

            @Override
            public void handleApiConnected() {
                addApiCallBack();
                SpeechSkill.getInstance().connectApi();
                PersonInfo.getInstance().startSearchPerson();
            }

            @Override
            public void handleApiDisconnected() {
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void addApiCallBack() {
        Log.d(TAG, "CoreService connected ");
        RobotApi.getInstance().registerModule("default", Sources.getPatterns(this), mModuleCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SpeechSkill.getInstance().release();
    }
}
