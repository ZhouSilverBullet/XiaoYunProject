package com.xuxin.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 作者: ZhangHD
 * 日期: 2018/3/21 时间: 13:37
 */

public class StringUtil {
    /**
     * 判断字符串是否为空，null，空串均为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        if (s == null || "".equals(s)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为空，null，空串，空白符（空格）均为空
     *
     * @param s
     * @return
     */
    public static boolean isEmptyWithTrim(String s) {
        if (s == null || "".equals(s.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为空，null，"null",空串，空白符（空格）均为空
     */
    public static boolean isNull(String s) {
        if (s == null || "".equals(s.trim()) || s.toLowerCase().trim().equals("null")) {
            return true;
        }
        return false;
    }

    private static final boolean DEBUG = false;

    public static void toastDebug(Context context, String msg) {
        //by lee 测试期间toast方法
        //会将接口请求返回适当打印
        //后期上线将DEBUG设置false即可

        if (!DEBUG)
            return;
        if (isEmpty(msg)) {
            Toast.makeText(context, "Toast传入参数为空，请检查接口数据", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void toastInfo(Context context, String msg) {
        //by lee 测试期间toast方法
        //会将接口请求返回适当打印
        //后期上线将DEBUG设置false即可

        if (isEmpty(msg)) {
            Toast.makeText(context, "Toast传入参数为空，请检查接口数据", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static String stringNotNull(String value) {
        String temp = "";
        if (!TextUtils.isEmpty(value)) {
            temp = value;
        }
        return temp;
    }


    /**
     * 截取名字
     *
     * @param value
     * @return
     */
    public static String stringSubName(String value) {
        String temp = "";
        if (!TextUtils.isEmpty(value)) {
            if (value.length() >= 2) {
                temp = value.substring(value.length() - 2, value.length());
            } else {
                temp = value;
            }
        }
        return temp;
    }

    /**
     * 截取部门前面两个字
     *
     * @param value
     * @return
     */
    public static String stringSubStartName(String value) {
        String temp = "";
        if (!TextUtils.isEmpty(value)) {
            if (value.length() >= 2) {
                temp = value.substring(0, 2);
            } else {
                temp = value;
            }
        }
        return temp;
    }


    /**
     * textView展示手机格式
     *
     * @param value
     * @return
     */
    public static String textPhoneValue(String value) {
        String temp = "";
        if (!TextUtils.isEmpty(value)) {
            StringBuilder sb = new StringBuilder();
            sb.append(value).insert(7, " ").insert(3, " ");
            temp = sb.toString();
        }
        return temp;
    }

    public static String getParentIconName(String parentName) {
        if (TextUtils.isEmpty(parentName)) {
            return parentName;
        }
        String tempString = "";
        if (!TextUtils.isEmpty(parentName) && parentName.length() > 0
                && parentName.contains("(") && parentName.contains(")")
                && parentName.indexOf("(") < parentName.indexOf(")")) {
            tempString = parentName.substring(0, parentName.length() - 1);
            if (!TextUtils.isEmpty(tempString)) {
                String[] split = tempString.split("\\(");
                if (split.length > 1) {
                    tempString = split[1];
                }
            }
        }
        return tempString;
    }
}
