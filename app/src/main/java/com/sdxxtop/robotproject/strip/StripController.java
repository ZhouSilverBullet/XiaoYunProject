package com.sdxxtop.robotproject.strip;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.sdxxtop.robotproject.global.App;
import com.xuxin.utils.PreferenceUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.account.BLAccount;
import cn.com.broadlink.base.BLConfigParam;
import cn.com.broadlink.family.BLFamily;
import cn.com.broadlink.ircode.BLIRCode;
import cn.com.broadlink.sdk.BLLet;
import cn.com.broadlink.sdk.data.controller.BLDNADevice;
import cn.com.broadlink.sdk.interfaces.controller.BLDeviceScanListener;
import cn.com.broadlink.sdk.result.controller.BLDownloadScriptResult;

public class StripController {
    private static final String TAG = "StripController";

    private static volatile StripController controller;
    private static final int PWR_ON = 1;
    private static final int PWR_OFF = 0;
    private Map<String, BLDNADevice> localMap = new HashMap<>();

//    private BLDNADevice bldnaDevice;

    public StripController() {
        init();
    }

    public static StripController getInstance() {
        if (controller == null) {
            synchronized (StripController.class) {
                if (controller == null) {
                    controller = new StripController();
                }
            }
        }
        return controller;
    }

    private void init() {
        BLLet.Controller.startProbe(3000);
        BLLet.Controller.setOnDeviceScanListener(new BLDeviceScanListener() {

            @Override
            public boolean shouldAdd(BLDNADevice bldnaDevice) {
                return false;
            }

            @Override
            public void onDeviceUpdate(BLDNADevice bldnaDevice, boolean b) {
                addDeviceInList(bldnaDevice);
            }
        });
    }

    /**
     * APPSDK 初始化函数
     */
    public static void initStripSdk(Context context) {

        // 初始化核心库
        BLConfigParam blConfigParam = new BLConfigParam();
        // 1. 设置日志级别，默认为 4 全部打印
        blConfigParam.put(BLConfigParam.CONTROLLER_LOG_LEVEL, "4");
        // 2. 设置底层打印日志级别，默认为 4 全部打印
        blConfigParam.put(BLConfigParam.CONTROLLER_JNI_LOG_LEVEL, "4");
        // 3. 设置脚本保存目录, 默认在 ../let/ 目录下
        // blConfigParam.put(BLConfigParam.SDK_FILE_PATH, "");
        // 4. 设置本地控制超时时间，默认 3000ms
        blConfigParam.put(BLConfigParam.CONTROLLER_LOCAL_TIMEOUT, "3000");
        // 5. 设置远程控制超时时间，默认 5000ms
        blConfigParam.put(BLConfigParam.CONTROLLER_REMOTE_TIMEOUT, "5000");
        // 6. 设置控制重试次数，默认 1
        blConfigParam.put(BLConfigParam.CONTROLLER_SEND_COUNT, "1");
        // 7. 设置设备控制支持的网络模式，默认 -1 都支持。  0 - 局域网控制，非0 - 局域网/远程都支持。
        blConfigParam.put(BLConfigParam.CONTROLLER_NETMODE, "-1");
        // 8. 设置脚本和UI文件下载资源平台。 默认 0 老平台。  1 - 新平台
        blConfigParam.put(BLConfigParam.CONTROLLER_SCRIPT_DOWNLOAD_VERSION, "1");
        // 9. 批量查询设备在线状态最小设备数
        blConfigParam.put(BLConfigParam.CONTROLLER_QUERY_COUNT, "8");

        BLLet.init(context, blConfigParam);

        // 初始化之后，获取 lid 和 companyId ，用于其他类库的初始化
        String lid = BLLet.getLicenseId();
        String companyId = BLLet.getCompanyid();

        // 初始化家庭库
        BLFamily.init(companyId, lid);
        // 初始化账户库
        BLAccount.init(companyId, lid);
        // 初始化红外码库
        BLIRCode.init(lid, blConfigParam);

        // 添加登录成功回调函数
        BLAccount.addLoginListener(BLLet.Controller.getLoginListener());
        BLAccount.addLoginListener(BLFamily.getLoginListener());
        BLAccount.addLoginListener(BLIRCode.getLoginListener());
    }

    private void addDeviceInList(BLDNADevice bldnaDevice) {
        if (bldnaDevice != null) {
            String did = bldnaDevice.getDid();
            if (!localMap.containsKey(did)) {
                localMap.put(did, bldnaDevice);
                downloadScript(bldnaDevice);

                if (devListListener != null) {
                    devListListener.onDevList(localMap);
                }
            }
        }
    }

    private void downloadScript(BLDNADevice bldnaDevice) {
//        if (this.bldnaDevice == null || (bldnaDevice != null && !bldnaDevice.getDid().equals(this.bldnaDevice.getDid()))) {
//            this.bldnaDevice = bldnaDevice;
        new Thread() {
            @Override
            public void run() {
                BLLet.Controller.addDevice(bldnaDevice);
                String pid = bldnaDevice.getPid();
                BLDownloadScriptResult result = BLLet.Controller.downloadScript(pid);
                Log.e(TAG, "pid: " + pid + "run: " + result.getSavePath() + " state " + bldnaDevice.getState());
            }
        }.start();
//        }
    }

    public void stripOpen(String stripName) {
        control(stripName, PWR_ON);
//        if (bldnaDevice != null) {
//            SPControlModelImpl.getInstance().controlDevPwr(bldnaDevice.getDid(), PWR_ON);
//        }
    }

    public void open(String did) {
        SPControlModelImpl.getInstance().controlDevPwr(did, PWR_ON);
    }

    public void close(String did) {
        SPControlModelImpl.getInstance().controlDevPwr(did, PWR_OFF);
    }

    public void stripClose(String stripName) {
        control(stripName, PWR_OFF);
//        if (bldnaDevice != null) {
//            SPControlModelImpl.getInstance().controlDevPwr(bldnaDevice.getDid(), PWR_OFF);
//        }
    }

    public Map<String, BLDNADevice> getLocalMap() {
        return localMap;
    }

    public void setStripName() {
//        PreferenceUtils.getInstance(App.getInstance()).saveParam();
    }

    private void control(String stripName, int state) {
        for (String s : localMap.keySet()) {
            BLDNADevice bldnaDevice = localMap.get(s);
            String name = PreferenceUtils.getInstance(App.getInstance()).getStringParam(s);
            switch (state) {
                case PWR_ON:
                    String open = null;
                    if (stripName.contains("打开")) {
                        String[] opens = stripName.split("打开");
                        if (opens.length > 1) {
                            open = opens[1];
                        }
                    } else if (stripName.contains("开启")) {
                        String[] opens = stripName.split("开启");
                        if (opens.length > 1) {
                            open = opens[1];
                        }
                    }

                    if (!TextUtils.isEmpty(name) && name.equals(open)) {
                        SPControlModelImpl.getInstance().controlDevPwr(bldnaDevice.getDid(), state);
                    }
                    break;
                case PWR_OFF:
                    String close = null;
                    if (stripName.contains("关闭")) {
                        String[] closes = stripName.split("关闭");
                        if (closes.length > 1) {
                            close = closes[1];
                        }
                    }

                    if (!TextUtils.isEmpty(name) && name.equals(close)) {
                        SPControlModelImpl.getInstance().controlDevPwr(bldnaDevice.getDid(), state);
                    }
                    break;
            }
        }
    }

    private DevListListener devListListener;

    public void setDevListListener(DevListListener devListListener) {
        this.devListListener = devListListener;
    }

    public interface DevListListener {
        void onDevList(Map<String, BLDNADevice> localMap);
    }
}
