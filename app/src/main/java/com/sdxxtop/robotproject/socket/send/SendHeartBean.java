package com.sdxxtop.robotproject.socket.send;

import com.google.gson.Gson;

public class SendHeartBean {

    /**
     * code : 200
     * data : {"action":"heart","respose":"ok"}
     */

    private int code = 200;
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private String action = "heart";
        private String response = "ok";
    }

    public static String getHeartJson() {
        SendHeartBean sendHeartBean = new SendHeartBean();
        sendHeartBean.setData(new DataBean());
        return new Gson().toJson(sendHeartBean);
    }
}
