package com.sdxxtop.robotproject.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.sdxxtop.robotproject.R;

public class AutoPlayView extends View {


    Drawable mDrawableLeft, mDrawableRight;
    int drawableWidth, drawableHeight;
    ValueAnimator valueAnimator;

    public AutoPlayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mDrawableLeft = getResources().getDrawable(R.drawable.my_banner1);
        drawableWidth = mDrawableLeft.getMinimumWidth() / 2;
        drawableHeight = mDrawableLeft.getMinimumHeight() / 2;
        mDrawableLeft.setBounds(0, 0, drawableWidth, drawableHeight);

        mDrawableRight = getResources().getDrawable(R.drawable.my_banner1);
        mDrawableRight.setBounds(drawableWidth, 0, drawableWidth * 2, drawableHeight);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wdMode = MeasureSpec.getMode(widthMeasureSpec);
        int hdMode = MeasureSpec.getMode(heightMeasureSpec);
        //测量布局大小，默认为屏幕的宽，图片的高
        if (wdMode == MeasureSpec.EXACTLY && hdMode == MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int wd = MeasureSpec.makeMeasureSpec(getResources().getDisplayMetrics().widthPixels, MeasureSpec.EXACTLY);
            int hd = MeasureSpec.makeMeasureSpec(drawableHeight, MeasureSpec.EXACTLY);
            setMeasuredDimension(wd, hd);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //思路：左边一张图，右边隐藏一张图，不停的scrollto
        mDrawableLeft.draw(canvas);
        mDrawableRight.draw(canvas);
    }

    public void startPlay() {
        //一张图片的宽的移动距离，即可视觉上达到不停的在滚动
        valueAnimator = ValueAnimator.ofInt(0, drawableWidth);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(-1);
        valueAnimator.setDuration(10000);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                scrollTo(value, 0);
            }
        });
        valueAnimator.start();
    }

    public void stopPlay() {
        if (valueAnimator != null && valueAnimator.isRunning() && valueAnimator.isStarted()) {
            valueAnimator.cancel();
        }
    }
}

