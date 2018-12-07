package com.xuxin.utils;


import android.content.Intent;

import com.xuxin.entry.ParentUserBean;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Administrator on 2018/8/2.
 */

public class CollectUtil {

    public static String getIntegerSetToString(HashSet<Integer> set) {
        if (set == null) {
            return "";
        }

        String value = "";
        for (Integer integer : set) {
            value = value + integer + ",";
        }

        if (value.length() > 0) {
            value = value.substring(0, value.length() - 1);
        }

        return value;
    }

    public static String getParentMapToString(HashMap<String, ParentUserBean> map) {
        if (map == null) {
            return "";
        }

        String value = "";
        for (String integer : map.keySet()) {
            value = value + map.get(integer).getParentUserId() + ",";
        }

        if (value.length() > 0) {
            value = value.substring(0, value.length() - 1);
        }

        return value;
    }


    public static HashSet<Integer> replaceIntegerHashSet(HashSet<Integer> userListSet, Intent data, String name) {
        if (userListSet != null) {
            HashSet<Integer> tempSelectUserSet = userListSet;
            userListSet = (HashSet<Integer>) data.getSerializableExtra(name);
            userListSet.addAll(tempSelectUserSet);
        } else {
            userListSet = (HashSet<Integer>) data.getSerializableExtra(name);
        }
        return userListSet;

    }

    public static HashMap<String, ParentUserBean> replaceParentUserBeanHashMap(HashMap<String, ParentUserBean> parentMap, Intent data, String name) {
        if (parentMap != null && parentMap.size() > 0) {
            HashMap<String, ParentUserBean> tempParentMap = parentMap;
            parentMap = (HashMap<String, ParentUserBean>) data.getSerializableExtra(name);
            HashMap<String, ParentUserBean> centerParentMap = new HashMap<>(parentMap);
            for (String integer : tempParentMap.keySet()) {
                if (centerParentMap.containsKey(integer)) {
                    parentMap.remove(integer);
                }
            }

            parentMap.putAll(tempParentMap);
        } else {
            parentMap = (HashMap<String, ParentUserBean>) data.getSerializableExtra(name);
        }

        return parentMap;

    }

}
