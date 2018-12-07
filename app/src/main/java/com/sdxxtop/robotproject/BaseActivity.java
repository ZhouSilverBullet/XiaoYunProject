package com.sdxxtop.robotproject;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.sdxxtop.robotproject.control.MessageManager;
import com.sdxxtop.robotproject.utils.LocationDialog;

import me.yokeyword.fragmentation.SupportActivity;

public class BaseActivity extends SupportActivity {

    private int flags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏状态栏
//        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
//        layoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        super.onCreate(savedInstanceState);
    }

    public void longClick() {
        LocationDialog locationDialog = new LocationDialog(this);
        locationDialog.show();
        locationDialog.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                getWindow().getDecorView().setSystemUiVisibility(flags);
            }
        });
    }

    private void sendNewRequest(String bundleType, String bundleIntent, String bundleText, String bundleParam, int bundleId) {
        Bundle bundle = new Bundle();
        bundle.putString("bundle_request_type", bundleType);
        bundle.putString("bundle_intent", bundleIntent);
        bundle.putString("bundle_text", bundleText);
        bundle.putString("bundle_param", bundleParam);
        bundle.putInt("bundle_id", bundleId);
        Message message = Message.obtain();
        message.what = 101;
        message.setData(bundle);
        MessageManager.getInstance().getHandler().sendMessage(message);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getWindow().getDecorView().setSystemUiVisibility(flags);
    }
}
