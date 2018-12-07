package com.sdxxtop.robotproject.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2018/9/22.
 */

public class UrlToBitmap {
    public static byte[] getImage(String website) throws Exception {
        URL url = new URL(website);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            byte[] bytes = read(inputStream);
            return bytes;
        }
        return "读取网络数据失败".getBytes();
    }

    public static byte[] read(InputStream inputStream) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        baos.close();
        return buf;
    }
}
