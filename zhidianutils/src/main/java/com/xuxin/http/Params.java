package com.xuxin.http;

import android.content.Context;
import android.text.TextUtils;

import com.xuxin.configure.UtilSession;
import com.xuxin.utils.PreferenceUtils;
import com.xuxin.utils.StringUtil;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/5/7.
 */

public class Params {
    protected Context context;
    protected HashMap<String, String> map;

    public Params() {
        this.context = UtilSession.getInstance().getContext();
        map = new HashMap<>();
        defaultValue();
    }

    private void defaultValue() {
        map.put("tp", ConstantValue.REQUEST_TYPE);
//        map.put("ci", PreferenceUtils.getInstance(context).getStringParam(ConstantValue.COMPANY_ID));
//        map.put("ui", PreferenceUtils.getInstance(context).getStringParam(ConstantValue.USER_ID));
    }

    public String getUserId() {
        String ui = map.get("ui");
        if (TextUtils.isEmpty(ui)) {
            ui = PreferenceUtils.getInstance(context).getStringParam(ConstantValue.USER_ID);
        }
        return ui;
    }

    public String getCompanyId() {
        String ci = map.get("ci");
        if (TextUtils.isEmpty(ci)) {
            ci = PreferenceUtils.getInstance(context).getStringParam(ConstantValue.COMPANY_ID);
        }
        return ci;
    }

    public void removeKey(String key) {
        if (map.containsKey(key)) {
            map.remove(key);
        }
    }

    public void put(String key, String value) {
        map.put(key, StringUtil.stringNotNull(value));
    }

    public void put(String key, long value) {
        map.put(key, value + "");
    }

    public void put(String key, int value) {
        map.put(key, value + "");
    }

    public String getData() {
        return NetHelper.getBase64Data(map);
    }
}
