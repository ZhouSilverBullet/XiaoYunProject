package com.sdxxtop.robotproject.utils;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

/**
 * @author Orion
 * @time 2018/9/13
 */
public class ToastUtil {

    public static void onResult(final Context context, int code, String msg) {
        String text = String.format("result:[code:%s, msg:%s]", code, msg);
        toast(context, text);
    }

    public static void onUpdate(Context context, int code, String msg) {
        String text = String.format("update:[code:%s, msg:%s]", code, msg);
        toast(context, text);
    }

    public static void onError(Context context, int code, String msg) {
        String text = String.format("error:[code:%s, msg:%s]", code, msg);
        toast(context, text);
    }

    private static void toast(final Context context, final String text){
        FragmentActivity activity = (FragmentActivity) context;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
