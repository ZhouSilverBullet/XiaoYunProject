package com.sdxxtop.robotproject.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.sdxxtop.robotproject.R;
import com.sdxxtop.robotproject.control.MessageManager;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.skill.NavigationSkill;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.sdxxtop.robotproject.strip.DevConfigActivity;
import com.sdxxtop.robotproject.strip.DevConfigListener;

public class LocationDialog {
    private Context context;
    private AlertDialog dialog;

    public LocationDialog(Context context) {
        this.context = context;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_location, null);
        builder.setView(view);
        dialog = builder.show();

        initView(view);
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    private void initView(View view) {
        view.findViewById(R.id.hand_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                RobotApi.getInstance().finishModuleParser(0, false);
//                sendNewRequest(,"start_manual_reposition", "start_manual_reposition", (String) null, 0);
                NavigationSkill.getInstance().getLocation("充电桩");
            }
        });
        view.findViewById(R.id.charge_motion_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationSkill.getInstance().setLocation("充电桩");
//                sendNewRequest(,"set_place", "这里是充电桩", (String) null, 0);
            }
        });

        view.findViewById(R.id.relocation_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NavigationSkill.getInstance().setLocation("充电桩");

                RobotApi.getInstance().naviRelocation(0, new CommandListener() {
                    @Override
                    public void onResult(int result, String message) {
                        super.onResult(result, message);
                        SpeechSkill.getInstance().playTxt(result + "，结果：" + message);
                    }
                });
//                sendNewRequest(,"set_place", "这里是充电桩", (String) null, 0);
            }
        });
        view.findViewById(R.id.skip_map_tool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipSystemUtils.skip(v.getContext(), SkipSystemUtils.TYPE_NAME_MAP_TOOL);
            }
        });
        view.findViewById(R.id.skip_core_tool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipSystemUtils.skip(v.getContext(), SkipSystemUtils.TYPE_NAME_CORE);
            }
        });
        view.findViewById(R.id.skip_speech_tool).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipSystemUtils.skip(v.getContext(), SkipSystemUtils.TYPE_NAME_ARS_SPEECH);
            }
        });

        view.findViewById(R.id.skip_system_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkipSystemUtils.skip(v.getContext(), SkipSystemUtils.TYPE_NAME_SYSTEM_SETTINGS);
            }
        });

        view.findViewById(R.id.zu_jian_control).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean zuJianSwitch = !App.getInstance().isZuJianSwitch();
                if (zuJianSwitch) {
                    Toast.makeText(context, "开启小程序连接", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "关闭小程序连接", Toast.LENGTH_SHORT).show();
                }
                App.getInstance().setZuJianSwitch(zuJianSwitch);
            }
        });

        view.findViewById(R.id.strip_control).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.getContext().startActivity(new Intent(v.getContext(), DevConfigActivity.class));
            }
        });

    }

    private void sendNewRequest(String bundleType, String bundleIntent, String bundleText, String bundleParam, int bundleId) {
//        Bundle bundle = new Bundle();
//        bundle.putString("bundle_request_type", bundleType);
//        bundle.putString("bundle_intent", bundleIntent);
//        bundle.putString("bundle_text", bundleText);
//        bundle.putString("bundle_param", bundleParam);
//        bundle.putInt("bundle_id", bundleId);
//        Message message = Message.obtain();
//        message.what = 101;
//        message.setData(bundle);
//        MessageManager.getInstance().getHandler().sendMessage(message);
    }
}
