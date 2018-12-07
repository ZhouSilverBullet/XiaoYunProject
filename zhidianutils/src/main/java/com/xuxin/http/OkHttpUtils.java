package com.xuxin.http;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 作者：CaiCM
 * 日期：2018/3/22  时间：13:24
 * 邮箱：15010104100@163.com
 * 描述：OK单例工具类
 */

public class OkHttpUtils {

    public static OkHttpUtils okHttpUtils;
    public static OkHttpClient okHttpClient;

    private OkHttpUtils() {
    }

    //创建工具类单例模式
    public static OkHttpUtils newInstance() {
        if (okHttpUtils == null) {
            synchronized (OkHttpUtils.class) {
                if (okHttpUtils == null) {
                    okHttpUtils = new OkHttpUtils();
                }
            }
        }
        return okHttpUtils;
    }

    //创建OkHttpClient单例模式
    public static OkHttpClient newOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpUtils.class) {
                if (okHttpClient == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
                    okHttpClient = new OkHttpClient().newBuilder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .writeTimeout(15, TimeUnit.SECONDS)
//                            .addInterceptor(new Interceptor() {
//                                @Override
//                                public Response intercept(Chain chain) throws IOException {
//                                    Request request = chain.request();
//                                    request.newBuilder().addHeader("Cookie", "aaaa");
//                                    Response response = null;
//                                    try {
//                                        response = chain.proceed(request);
//                                        ResponseBody responseBody = response.body();
//                                        BufferedSource source = responseBody.source();
//                                        source.request(Long.MAX_VALUE);
//                                        Buffer buffer = source.buffer();
//                                        LogUtils.e("LOG", request.method()+ "请求 url:" + request.url());
//                                        LogUtils.e("LOG", "内容:" + buffer.clone().readString(Charset.forName("UTF-8")));
//                                        LogUtils.e("LOG", "request-body:" + request.body());
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    return response;
//                                }
//                            })
                            .addInterceptor(new LoggingInterceptor())
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    //创建OkHttpClient单例模式
    public static OkHttpClient newOkHttpClient(Context context) {
        if (okHttpClient == null) {
            synchronized (OkHttpUtils.class) {
                if (okHttpClient == null) {
                    //设置缓存目录和大小
                    int cacheSize = 10 << 20; // 10 MiB
                    Cache cache = new Cache(context.getCacheDir(), cacheSize);
                    okHttpClient = new OkHttpClient().newBuilder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .writeTimeout(15, TimeUnit.SECONDS)
                            .cache(cache) //设置缓存
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    public static RequestBody getRequestBody(String filePath) {
        return RequestBody.create(
                MediaType.parse("multipart/form-data"),
                new File(filePath));
    }

    public static MultipartBody.Part getMultipartBodyPart(String fileName, String filePath) {
        return MultipartBody.Part.createFormData("images", fileName, getRequestBody(filePath));
    }

    //根据请求参数的键值对和上传的文件生成MultipartBody
    public static MultipartBody getMultipartBody(HashMap<String, String> map, List<String> fileNames) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (HashMap.Entry<String, String> entry : map.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        for (int i = 0; i < fileNames.size(); i++) {
            File file = new File(fileNames.get(i));
            String fileType = getMimeType(file.getName());
            builder.addFormDataPart("image", file.getName(),
                    RequestBody.create(MediaType.parse(fileType), file));
        }
        return builder.build();
    }

    //根据文件后缀名，来获得MIME类型
    public static String getMimeType(String filename) {
        FileNameMap filenameMap = URLConnection.getFileNameMap();
        String contentTypeFor = filenameMap.getContentTypeFor(filename);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    static class LoggingInterceptor implements Interceptor {

        private Logger logger = Logger.global;

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            logger.info(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            logger.info(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));
            return response;
        }
    }
}
