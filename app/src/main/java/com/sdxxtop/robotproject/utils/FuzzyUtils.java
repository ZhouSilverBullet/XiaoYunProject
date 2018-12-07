package com.sdxxtop.robotproject.utils;

/**
 * Created by Administrator on 2018/9/22.
 */

public class FuzzyUtils {

    /**
     * 给大家打招呼
     *
     * @param message
     * @return
     */
    public static boolean contains1(String message) {
        return (message.contains("跟大家打个招呼")
                || message.contains("大家打个招呼")
                || (message.contains("介绍") && message.contains("自己"))
                || message.contains("打个招呼"));

    }


    /**
     * 你有哪些功能
     *
     * @param message
     * @return
     */
    public static boolean contains2(String message) {
        return message.contains("你有哪些功能")
                || message.contains("有哪些功能")
                || message.contains("哪些功能")
                || (message.contains("你有") && message.contains("功能"));

    }

    /**
     * 跟大家介绍一下旭兴科技公司
     *
     * @param message
     * @return
     */
    public static boolean contains3(String message) {
        return message.contains("跟大家介绍一下旭兴科技公司")
                || (message.contains("介绍") && message.contains("公司"));

    }

    /**
     * 再介绍一下知点云
     *
     * @param message
     * @return
     */
    public static boolean contains4(String message) {
        return message.contains("介绍一下知点云")
                || (message.contains("介绍") && message.contains("知点云"))
                || (message.contains("介绍") && message.contains("指点"))
                || (message.contains("介绍") && message.contains("支点"));

    }

    /**
     * 你知道今天是什么日子吗
     * 今天有什么会议
     *
     * @param message
     * @return
     */
    public static boolean contains5(String message) {
        return message.contains("今天是什么日子")
                || message.contains("今天有什么会议")
                || (message.contains("今天") && message.contains("日子"))
                || (message.contains("今天") && message.contains("会议"));

    }

    /**
     * 数字孪生技术
     *
     * @param message
     * @return
     */
    public static boolean contains6(String message) {
        return message.contains("介绍一下数字孪生技术")
                || message.contains("数字孪生技术")
                || (message.contains("介绍") && message.contains("数字孪生技术"));

    }

    /**
     * 李国琛是谁
     *
     * @param message
     * @return
     */
    public static boolean contains7(String message) {
        return message.contains("李国琛是谁")
                || message.contains("李国春是谁")
                || (message.contains("李国") && message.contains("是谁"));

    }
    /**
     * 你知道今天会议的内容吗
     *
     * @param message
     * @return
     */
    public static boolean contains8(String message) {
        return message.contains("今天会议的内容")
                || (message.contains("今天会议") && message.contains("内容"))
                || (message.contains("今天交流会") && message.contains("内容"))
                || (message.contains("今天交流会") && message.contains("主题"))
                ;

    }

    /**
     * 交流会在几楼举行
     * 会议在哪开
     *
     * @param message
     * @return
     */
    public static boolean contains9(String message) {
        return message.contains("交流会在几楼举行")
                || (message.contains("交流会") && message.contains("几楼举行"))
                || (message.contains("交流会") && message.contains("几楼"))
                || (message.contains("会议") && message.contains("几楼举行"))
                || (message.contains("会议") && message.contains("几楼"))
                || (message.contains("会议") && message.contains("几楼召开"))
                || (message.contains("会议") && message.contains("哪开"));

    }

    /**
     * 谢谢啦
     *
     * @param message
     * @return
     */
    public static boolean contains10(String message) {
        return message.contains("谢谢啦")
                || (message.contains("谢谢"));

    }

    /**
     * 跟领导们说再见
     *
     * @param message
     * @return
     */
    public static boolean contains11(String message) {
        return message.contains("跟领导们说再见")
                || (message.contains("领导") && message.contains("再见"));

    }

    /**
     * 打开 xx
     * @param message
     * @return
     */
    public static boolean openStripContains(String message) {
        return message.contains("打开")
                || (message.contains("开启"));

    }

    /**
     * 关闭 xx
     * @param message
     * @return
     */
    public static boolean closeStripContains(String message) {
        return message.contains("关闭");

    }


}
