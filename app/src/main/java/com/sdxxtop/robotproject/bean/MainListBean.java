package com.sdxxtop.robotproject.bean;

public class MainListBean {
    public static final int TYPE_ASK_WAY = 1;
    public static final int TYPE_ANSWER = 2;
    public static final int TYPE_CHAT = 3;
    public static final int TYPE_WEATHER = 4;
    public static final int TYPE_INTRODUCE = 5;

    public int type;
    public int leftIcon;
    public String title;
    public String content;

    public MainListBean(int type, int leftIcon, String title, String content) {
        this.type = type;
        this.leftIcon = leftIcon;
        this.title = title;
        this.content = content;
    }
}
