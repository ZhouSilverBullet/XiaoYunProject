package com.sdxxtop.robotproject.utils;

import android.text.TextUtils;

import java.util.Calendar;

/**
 * Created by Administrator on 2018/10/12.
 */

public class NameUtils {
    /**
     * 你是XX
     * 我认识你，你叫XXX
     * 你是XXX，我很聪明的。
     * 当然认识你，你是XX
     *
     * @param name
     * @return
     */
    public static String getGetName(String name) {
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        int x = (int) (Math.random() * 100) + 1;
        int i = x % 5;
        name = changeName(name);
        String value = "";
        switch (i) {
            case 0:
                value = "你是" + name;
                break;
            case 1:
                value = "我认识你，你是" + name;
                break;
            case 2:
                value = "你是" + name + "，我很聪明的。";
                break;
            default:
                value = "当然认识你，你是" + name;
                break;
        }
        return value;
    }


    public static String getWakeUpName(String name) {
        if (TextUtils.isEmpty(name)) {
            return "";
        }
        int x = (int) (Math.random() * 100) + 1;
        int i = x % 5;
        name = changeName(name);
        String value = "";
        switch (i) {
            case 0:
                value = name + "你好，见到你很高兴";
                break;
            case 1:
                value = name + "，" + getDuringDay() + "好，有什么可以帮到你的";
                break;
            case 2:
                value = name + "你好，有什么能帮到你的";
                break;
            default:
                value = name + "你好，欢迎莅临旭兴科技。";
                break;
        }
        return value;
    }


    public static String getDuringDay() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 1 && hour < 7) {
            return "早上";
        } else if (hour >= 7 && hour < 11) {
            return "上午";
        }
        if (hour >= 11 && hour <= 13) {
            return "中午";
        }
        if (hour >= 14 && hour <= 18) {
            return "下午";
        } else {
            return "晚上";
        }
    }

    public static String changeName(String name) {
        String value = "";
        switch (name) {
            case "王玉君":
                value = "王玉君书记";
                break;
            case "李明涛":
                value = "李鸣涛院长";
                break;
            case "张世敏":
                value = "姜仕礼部长";
                break;
            case "朝阳峰":
            case "朝阳风":
                value = "曹仰锋院长";
                break;
            case "刘九儒":
                value = "刘九如司长";
                break;
            case "陆丰":
                value = "陆峰所长";
                break;
            case "聂林海":
                value = "聂林海司长";
                break;
            case "童小明":
            case "陈小明":
                value = "童晓民司长";
                break;
            case "李建华":
                value = "李建华秘书长";
                break;
            case "文丹枫":
                value = "文丹枫老师";
                break;
            case "高永胜":
                value = "高永胜书记";
                break;
            case "孟庆斌":
                value = "孟庆斌市长";
                break;

        }

        if (TextUtils.isEmpty(value)) {
            value = name;
        }
        return value;
    }
}
