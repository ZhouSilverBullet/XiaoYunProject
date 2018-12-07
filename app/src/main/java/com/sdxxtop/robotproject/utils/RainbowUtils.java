package com.sdxxtop.robotproject.utils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.sdxxtop.robotproject.R;
import com.sdxxtop.robotproject.global.App;

/**
 * Created by Administrator on 2018/9/20.
 */

public class RainbowUtils {
    private static int[] colors = {R.color.rainbow_red, R.color.rainbow_orange, R.color.rainbow_yellow,
            R.color.rainbow_green, R.color.rainbow_cyan, R.color.rainbow_blue, R.color.rainbow_purple};

    public static CharSequence colorText(String textValue) {
        if (TextUtils.isEmpty(textValue)) {
            return "";
        }
        char[] chars = textValue.toCharArray();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < chars.length; i++) {
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(
                    App.getInstance().getResources().getColor(colors[i % 7]));
            int length = stringBuilder.length();
            stringBuilder.append(chars[i]);
            stringBuilder.setSpan(colorSpan, length, length + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return stringBuilder;
    }
}
