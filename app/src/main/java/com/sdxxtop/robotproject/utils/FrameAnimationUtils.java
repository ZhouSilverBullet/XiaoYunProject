package com.sdxxtop.robotproject.utils;

import android.graphics.drawable.AnimationDrawable;
import android.util.Log;

import com.sdxxtop.robotproject.R;
import com.sdxxtop.robotproject.global.App;

import java.util.concurrent.ConcurrentHashMap;

public class FrameAnimationUtils {
    private static FrameAnimationUtils frameAnimationUtils;
    private ConcurrentHashMap<Integer, AnimationDrawable> map;

    public static FrameAnimationUtils getInstance() {
        if (frameAnimationUtils == null) {
            synchronized (FrameAnimationUtils.class) {
                if (frameAnimationUtils == null) {
                    frameAnimationUtils = new FrameAnimationUtils();
                }
            }
        }
        return frameAnimationUtils;
    }

    private FrameAnimationUtils() {
        map = new ConcurrentHashMap<>();
        map.put(1, (AnimationDrawable) App.getInstance().getResources().getDrawable(R.drawable.gif_blink));
        map.put(2, (AnimationDrawable) App.getInstance().getResources().getDrawable(R.drawable.gif_sayone));
        map.put(3, (AnimationDrawable) App.getInstance().getResources().getDrawable(R.drawable.gif_walk));
    }

    public AnimationDrawable getDrawable(int index) {
        if (map.containsKey(index)) {
            return map.get(index);
        }
        return null;
    }

    /**
     * 开始播放
     */
    public void start(int index) {
        hasKey(index);
        AnimationDrawable frameAnim = map.get(index);
        if (frameAnim != null && !frameAnim.isRunning()) {
            frameAnim.start();
//            Toast.makeText(ToXMLActivity.this, "开始播放", 0).show();
            Log.i("main", "index 开始播放" + index);
            Log.i("main", "当前AnimationDrawable一共有" + frameAnim.getNumberOfFrames() + "帧");
        }
    }

    /**
     * 停止播放
     */
    public void stop(int index) {
        hasKey(index);
        AnimationDrawable frameAnim = map.get(index);
        if (frameAnim != null && frameAnim.isRunning()) {
            frameAnim.stop();
//            Toast.makeText(ToXMLActivity.this, "停止播放", 0).show();
        }
    }

    public void hasKey(int index) {
        if (!map.containsKey(index)) {
            throw new IndexOutOfBoundsException("");
        }
    }
}