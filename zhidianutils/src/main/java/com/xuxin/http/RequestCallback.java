package com.xuxin.http;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.conn.ConnectTimeoutException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2018/5/7.
 */

public class RequestCallback<D extends BaseModel> implements Callback<D> {
    private static final String TAG = "RequestCallback";
    private IRequestListener<D> listener;
    private Context context;

    public RequestCallback(Context context, IRequestListener<D> listener) {
        this.listener = listener;
        this.context = context;
    }

    public RequestCallback(IRequestListener<D> listener) {
        this.listener = listener;
    }

    @Override
    public void onResponse(Call<D> call, Response<D> response) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()) { //activity已经结束了就不在进行任何回调
                return;
            }
        }

        Log.e(TAG, "onResponse: " + call.request().url() + "");
        Log.e(TAG, "onResponse: " + response.body() + "");

        int code = response.code();
        D body = response.body();

        if (code != 200) {
            if (body != null) {
                if (!TextUtils.isEmpty(body.msg)) {
                    onFail(code, body.msg);
                } else {
                    onFail(code, "未知错误");
                }
            } else {
                onFail(code, "未知错误");
            }
            return;
        }

        if (body == null) {
            //返回值为200 但是数据为空
            onFail(-102, "未知错误");
            return;
        }

        code = body.code;

        if (body.code != 200) {
            if (!TextUtils.isEmpty(body.msg)) {
                onFail(code, body.msg);
            } else {
                onFail(code, "未知错误");
            }
            return;
        }

        if (listener != null) {
            String msg = body.msg;
            if (TextUtils.isEmpty(msg)) {
                //后期如果要改可以改，我是打算如果后台为空，直接给个成功的值反正是200，应该没毛病
                body.msg = "成功";
            }
            listener.onSuccess(body);
        }


    }

    private void onFail(int code, String errorMsg) {
        if (listener != null) { //返回不是200
//            listener.onFailure(code, errorMsg + "(" + code + ")");
            listener.onFailure(code, errorMsg);
        }
    }

    @Override
    public void onFailure(Call<D> call, Throwable t) {
        Log.e(TAG, "onFailure: " + call.request().url() + "");

        String msg = getOkHttpExceptionMsg(t, "");
        if (listener != null) {
            listener.onFailure(-99, TextUtils.isEmpty(msg) ? "" : msg);
        }
    }

    public static String getOkHttpExceptionMsg(Throwable exception, String errorMsg) {
        String defaultMsg = "未知错误";
        if (exception != null) {
            Log.e(TAG, "Request Exception:" + exception.getMessage());
            if (exception instanceof UnknownHostException) {
                defaultMsg = "您的网络可能有问题,请确认连接上有效网络后重试";
            } else if (exception instanceof ConnectTimeoutException) {
                defaultMsg = "连接超时,您的网络可能有问题,请确认连接上有效网络后重试";
            } else if (exception instanceof SocketTimeoutException) {
                defaultMsg = "请求超时,您的网络可能有问题,请确认连接上有效网络后重试";
            } else {
                defaultMsg = "未知的网络错误, 请重试";
            }
        } else {
            if (!TextUtils.isEmpty(errorMsg)) {
                Log.e(TAG, "Request Exception errorMsg: " + errorMsg);
                String lowerMsg = errorMsg.toLowerCase(Locale.ENGLISH);
                if (lowerMsg.contains("java")
                        || lowerMsg.contains("exception")
                        || lowerMsg.contains(".net")
                        || lowerMsg.contains("java")) {
                    defaultMsg = "未知错误, 请重试";
                } else {
                    defaultMsg = "未知错误, 请重试";
                }
            }
        }
        return defaultMsg;
    }
}
