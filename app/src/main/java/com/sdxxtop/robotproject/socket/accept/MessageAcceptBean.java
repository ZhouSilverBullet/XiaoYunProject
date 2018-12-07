package com.sdxxtop.robotproject.socket.accept;

import android.text.TextUtils;

import com.sdxxtop.robotproject.socket.SocketConstantValue;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageAcceptBean {
    /**
     * {
     * "code": 200,
     * "data": {
     * "action":"sendAnswer",
     * "device_type":1,//机器人类型(1:小伴 2:小云)
     * "answer":"问题答案"
     * }
     * }
     */

    public static String messageAcceptParse(String value) {
        return handleMessage(value);
    }

    private static String handleMessage(String message) {
        String value = "";
        if (!TextUtils.isEmpty(message)) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.has("data")) {
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (data != null && "sendAnswer".equals(data.optString("action"))) {
                        String device_type = data.optString("device_type");
                        switch (device_type) {
                            case SocketConstantValue.DEVICE_TYPE:
                                value = data.optString("answer");
                                break;
                        }
                        return value;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
