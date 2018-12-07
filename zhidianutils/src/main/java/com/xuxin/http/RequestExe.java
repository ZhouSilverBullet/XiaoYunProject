package com.xuxin.http;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：CaiCM
 * 日期：2018/3/22  时间：13:24
 * 邮箱：15010104100@163.com
 * 描述：Retrofit网络请求工具类
 */

public class RequestExe {
    public static final String BASE_URL = "http://app.sdxxtop.com/api/";

    public static Retrofit retrofit;

    public ApiService buildRequest() {
        if (retrofit == null) {
            synchronized (RequestExe.class) {
                if (retrofit == null) {
                    //retrofit的实例化
                    retrofit = new Retrofit.Builder()
                            .baseUrl(ConstantValue.BASE_URL)
                            .client(OkHttpUtils.newOkHttpClient())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();

                }
            }

        }
        return retrofit.create(ApiService.class);
    }

    public static ApiService createRequest() {
        if (retrofit == null) {
            synchronized (RequestExe.class) {
                if (retrofit == null) {
                    //retrofit的实例化
                    retrofit = new Retrofit.Builder()
                            .baseUrl(ConstantValue.BASE_URL)
                            .client(OkHttpUtils.newOkHttpClient())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .build();

                }
            }

        }
        return retrofit.create(ApiService.class);
    }

    public static ApiService createRequest(long time) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.writeTimeout(time, TimeUnit.MILLISECONDS);
        builder.readTimeout(time, TimeUnit.MILLISECONDS);
        builder.connectTimeout(time, TimeUnit.MILLISECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(ApiService.class);
    }
}
