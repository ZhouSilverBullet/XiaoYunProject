package com.sdxxtop.robotproject.socket.send;

import com.google.gson.Gson;

public class SendQuestionBean extends SendBase {
    /**
     * {
     *     "cmdName":"sendQuestion",    //请求的接口名称
     *     "platform_type":"1", //1机器人 2:微信
     *     "device_type":"1",  //机器人类型(1:小伴 2:小云)
     *     "question":"问题",
     * }
     */
    private String cmdName = "sendQuestion";
    private String question;

    public static String toSendQuestion(String question) {
        SendQuestionBean sendQuestionBean = new SendQuestionBean();
        sendQuestionBean.question = question;
        return new Gson().toJson(sendQuestionBean);
    }
}
