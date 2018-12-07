package com.sdxxtop.robotproject.service;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.ainirobot.coreservice.client.module.ModuleCallbackApi;
import com.sdxxtop.robotproject.control.MessageManager;
import com.sdxxtop.robotproject.global.Constants;


/**
 * 用户行为意图回调
 *
 * @author 高晓峰
 * @time 2018/9/20
 */
public class ModuleCallback extends ModuleCallbackApi {

    private static final String TAG = "ModuleCallback";

    private Context mContext;

    public ModuleCallback(Context context) {
        mContext = context;
    }

    @Override
    public boolean onSendRequest(int reqId, String reqType, String reqText, String reqParam) {
        Log.e(TAG, "New request: " + "reqId: " + reqId + " type is:" + reqType + " text is:" + reqText + " reqParam = " + reqParam);
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ID, reqId);
        bundle.putString(Constants.REQUEST_TYPE, reqType);
        bundle.putString(Constants.REQUEST_TEXT, reqText);
        bundle.putString(Constants.REQUEST_PARAM, reqParam);
        msg.setData(bundle);
        MessageManager.getInstance().getHandler().sendMessage(msg);
        return true;
    }

    @Override
    public void onCmdResponse(int cmdId, String command, String cmdStatus) {
        Log.e(TAG, "Command Execute Finish cmdId: " + cmdId + " command:" + command +
                " status:" + cmdStatus);
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ID, cmdId);
        bundle.putString(Constants.CMD_COMMAND, command);
        bundle.putString(Constants.CMD_STATUS, cmdStatus);
        msg.setData(bundle);
        MessageManager.getInstance().exeCMD(msg);
    }

    /**
     * Handle message received from HW service
     *
     * @param hwFunction
     * @param hwReport
     * @throws RemoteException
     */
    @Override
    public void onHWReport(int hwFunction, String command, String hwReport) {
        Log.e(TAG, "HW report: " + " hwFunction is:" + hwFunction + " command:" + command +
                " hwReport is:" + hwReport);
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ID, hwFunction);
        bundle.putString(Constants.CMD_COMMAND, command);
        bundle.putString(Constants.CMD_STATUS, hwReport);
        msg.setData(bundle);
        MessageManager.getInstance().exeHWReport(msg);
    }

}
