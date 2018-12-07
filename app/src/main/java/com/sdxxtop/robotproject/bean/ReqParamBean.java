package com.sdxxtop.robotproject.bean;

/**
 * Created by Administrator on 2018/9/20.
 */

public class ReqParamBean {

    /**
     * answerText : 我是接待机器人小云，可以前台接待，咨询讲解，跟随引领，视频通话，远程控制，自主充电。
     * intent : tell_me_why&common
     * slots : {"answer":[{"slot_type":"NORMAL","text":"我是接待机器人小云，可以前台接待，咨询讲解，跟随引领，视频通话，远程控制，自主充电。","value":"我是接待机器人小云，可以前台接待，咨询讲解，跟随引领，视频通话，远程控制，自主充电。"}],"question":[{"slot_type":"NORMAL","text":"接待机器人是什么?","value":"接待机器人是什么?"}]}
     * userText : 我想听机器人
     */

    private String answerText;
    private String intent;
    private String slots;
    private String userText;

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getSlots() {
        return slots;
    }

    public void setSlots(String slots) {
        this.slots = slots;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }
}
