package com.sdxxtop.robotproject.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChatKeywordUtil {
    public static Map<Integer, String> map = new HashMap<>();
    public static List<Integer> numList = new ArrayList<>();

    static {
        map.put(1, "介绍一下旭兴科技公司");
        map.put(2, "介绍一下知点云");
        map.put(3, "你有哪些功能");
        map.put(4, "什么是数字李生");
        map.put(5, "什么是人脸识别技术");
        map.put(6, "什么是情绪识别技术");
        map.put(7, "带我去展厅");
        map.put(8, "带我去电梯口");
        map.put(9, "洗手间在哪里");
        map.put(0, "展厅在哪里");
    }

    public static List<String> getKeyword() {

        List<String> list = new ArrayList<>();
        Random random = new Random();
        int num = random.nextInt(10);
        list.add(map.get(num));
        list.add(map.get(num == 9 ? 0 : num + 1));

        return list;
    }
}
