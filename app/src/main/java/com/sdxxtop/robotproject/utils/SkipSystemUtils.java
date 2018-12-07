package com.sdxxtop.robotproject.utils;

import android.content.Context;
import android.content.Intent;

public class SkipSystemUtils {
    public static final String TYPE_NAME_MAP_TOOL = "打开地图工具";
    public static final String TYPE_NAME_CORE = "打开代码工具";
    public static final String TYPE_NAME_ARS_SPEECH = "打开语音工具";
    public static final String TYPE_NAME_SYSTEM_SETTINGS = "打开系统设置";

    public static void skip(Context context, String speechMessage) {
        Intent intent = null;
        if (speechMessage.contains(TYPE_NAME_MAP_TOOL)) {
            intent = new Intent();
            intent.setClassName("com.ainirobot.maptool",
                    "com.ainirobot.maptool.ui.MainActivity");
        } else if (speechMessage.contains(TYPE_NAME_CORE)) {
            intent = new Intent();
            intent.setClassName("com.ainirobot.coreservice",
                    "com.ainirobot.coreservice.test.TestActivity");
        } else if (speechMessage.contains(TYPE_NAME_ARS_SPEECH)) {
            intent = new Intent();
            intent.setClassName("com.ainirobot.speechasrservice",
                    "com.ainirobot.speechasrservice.MainActivity");
        } else if (speechMessage.contains(TYPE_NAME_SYSTEM_SETTINGS)) {
            intent = new Intent();
            intent.setClassName("com.android.settings",
                    "com.android.settings.Settings");
        }
        if (intent != null && context != null) {
            context.startActivity(intent);
        }
    }
}
