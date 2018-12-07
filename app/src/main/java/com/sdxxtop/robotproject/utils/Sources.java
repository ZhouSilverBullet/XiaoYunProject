package com.sdxxtop.robotproject.utils;

import android.content.Context;

import com.sdxxtop.robotproject.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Orion
 * @time 2018/9/11
 */
public class Sources {

    public static List<String> getPatterns(Context context) {
        List<String> patternList = new ArrayList<>();
        String[] patterns = context.getResources().getStringArray(R.array.module_pattern);
        for(String pattern : patterns){
            patternList.add(pattern);
        }
        return patternList;
    }
}
