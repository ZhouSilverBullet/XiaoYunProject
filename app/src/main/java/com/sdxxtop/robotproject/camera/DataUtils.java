package com.sdxxtop.robotproject.camera;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/9/17.
 */

public class DataUtils {
    public static byte[] tempImageData;
    public static int degree;
    public static boolean isBackCamera = true;

    public static List<Bitmap> imgList = new ArrayList<>();

    public static Bitmap getMirrorBitmap(Bitmap bitmap) {
        Bitmap modBm = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        //处理画布  重新绘制图形  形成镜像
        Canvas canvas = new Canvas(modBm);
        Paint paint = new Paint();

        //绘制矩阵  Matrix主要用于对平面进行平移(Translate)，缩放(Scale)，旋转(Rotate)以及斜切(Skew)操作。
        Matrix matrix = new Matrix();
        //镜子效果：
        matrix.setScale(-1, 1);//翻转
        matrix.postTranslate(bitmap.getWidth(), 0);
        canvas.drawBitmap(bitmap, matrix, paint);
        return modBm;
    }
}
