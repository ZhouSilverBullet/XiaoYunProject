package com.sdxxtop.robotproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

import com.sdxxtop.robotproject.R;

/**
 * Created by Administrator on 2018/9/20.
 */
public class RainbowTextView extends AppCompatTextView {
    private String TAG = "MyTextView";
    private Paint mPaint;
    private Context mContext;

    private int[] colors = {R.color.rainbow_red, R.color.rainbow_orange, R.color.rainbow_yellow,
            R.color.rainbow_green, R.color.rainbow_cyan, R.color.rainbow_blue, R.color.rainbow_purple};
    private Rect rect;

    public RainbowTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        Log.d(TAG, "onDraw");
        char[] chars = getText().toString().toCharArray();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(DensityUtil.dip2px(getContext(), 18));
        int width = 0;
        for (int i = 0; i < chars.length; i++) {
            mPaint.setColor(getResources().getColor(colors[i % 7]));
            rect = new Rect();
            canvas.drawText(chars[i] + "", width + 10, 200, mPaint);
            mPaint.getTextBounds(chars[i] + "", 0, 1, rect);
            width += rect.width() + 10;
        }
    }
}