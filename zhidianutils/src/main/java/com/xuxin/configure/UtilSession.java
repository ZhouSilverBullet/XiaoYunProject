package com.xuxin.configure;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * Created by Administrator on 2018/8/6.
 */

public class UtilSession {
    private static UtilSession session;
    private Context context;
    private String versionCode;

    public static UtilSession getInstance() {
        if (session == null) {
            synchronized (UtilSession.class) {
                if (session == null) {
                    session = new UtilSession();
                }
            }
        }
        return session;
    }

    public void init(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionCode() {
        if (!TextUtils.isEmpty(versionCode)) {
            return versionCode;
        }
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            String versionCode = String.valueOf(pi.versionCode);
            setVersionCode(versionCode);
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return "10001";
        }
    }
}
