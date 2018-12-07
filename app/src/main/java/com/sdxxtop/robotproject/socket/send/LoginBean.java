package com.sdxxtop.robotproject.socket.send;

import android.text.TextUtils;

import com.sdxxtop.robotproject.utils.MD5Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginBean extends SendBase {
    public String cmdName = "login";
    public long time = System.currentTimeMillis();
    public String sign = getSign();

    private String getSign() {
        String encode = MD5Utils.encode(device_type + platform_type + time);
        if (TextUtils.isEmpty(encode)) {
            return "";
        }
        return encode;
    }

    public static String getLoginJson() {
        LoginBean loginBean = new LoginBean();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cmdName", loginBean.cmdName);
            jsonObject.put("platform_type", loginBean.platform_type);
            jsonObject.put("device_type", loginBean.device_type);
            jsonObject.put("time", loginBean.time);
            jsonObject.put("sign", loginBean.sign);
            return  jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }
}





















