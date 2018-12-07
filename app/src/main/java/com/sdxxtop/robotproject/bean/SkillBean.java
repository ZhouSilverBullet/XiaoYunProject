package com.sdxxtop.robotproject.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/9/19.
 */

public class SkillBean implements Serializable {
    public static final int SPEECH_PAR_RESULT = 1;
    public static final int START = 2;
    public static final int STOP = 3;
    public static final int VOLUME_CHANGE = 4;
    public static final int QUERY_ENDED = 5;

    public int type; //填写上面的5个type
    public int value; //填写onVolumeChange  onQueryEnded 的参数值
    public String message; //成功的message

    @Override
    public String toString() {
        return "SkillBean{" +
                "type=" + type +
                ", value=" + value +
                ", message='" + message + '\'' +
                '}';
    }
}
