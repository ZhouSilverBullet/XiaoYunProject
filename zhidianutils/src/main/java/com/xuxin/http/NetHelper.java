package com.xuxin.http;

/**
 * 作者：CaiCM
 * 日期：2018/3/22  时间：15:24
 * 邮箱：15010104100@163.com
 * 描述：下面注释
 */

import android.util.Base64;
import android.util.Log;

import com.xuxin.configure.UtilSession;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

/**
 * 1. map ({(a,1),(b,2),(c,3)}) 转成字符串 str (a=1&b=2&c=3)
 * 2. str = str + appkey
 * 3. 计算str的md5
 * 4. 将 md5存入map: map.put("sn", md5)
 * 5. map 转成 json
 * 6. 对json字符串进行base64加密
 */
public class NetHelper {

    public static String getBase64(String str) {
        String result = "";
        if (str != null) {
            try {
                result = new String(Base64.encode(str.getBytes("utf-8"), Base64.NO_WRAP), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    public static String getBase64Data(Map<String, String> map) {
        if (map == null)
            return "";
        map.put("v", UtilSession.getInstance().getVersionCode());
        map.put("ts", System.currentTimeMillis() + "");//把时间转成时间戳？
        StringBuilder sb = new StringBuilder();
        Object[] keyArr = map.keySet().toArray();
        Arrays.sort(keyArr);
        for (int i = 0; i < keyArr.length; i++) {
            if (i != 0) {
                sb.append(ConstantValue.STR_SPLICE_SYMBOL);
            }
            Object key = keyArr[i];
            sb.append(key).append(ConstantValue.STR_EQUAL_OPERATION).append(map.get(key));
        }
        map.put("sn", md5(sb.toString() + ConstantValue.APP_KEY).toUpperCase());
        for (String key : map.keySet()) {
            Log.e("LOG", "key  " + key + "  value  " + map.get(key));
        }
        JSONObject json = new JSONObject(map);
        String jsonStr = json.toString();
        return getBase64(jsonStr);

    }

    //MD5加密算法
    public static String md5(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }


}
