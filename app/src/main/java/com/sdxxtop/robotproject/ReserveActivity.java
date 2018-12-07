package com.sdxxtop.robotproject;

import android.content.Intent;
import android.os.Bundle;

import com.sdxxtop.robotproject.bean.ReserveData;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.xuxin.entry.ReserveBean;

import java.util.List;

public class ReserveActivity extends KeyboardActivity {
    public static final int RESERVE_BACK = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeechSkill.getInstance().playTxt("请输入5位数的预约码");
    }

    @Override
    public void handleData(List<ReserveBean.DataBean> data) {
        ReserveData.getInstance().add(data);
        ReserveBean.DataBean dataBean = ReserveData.getInstance().getFirst();
        Intent intent = new Intent();
        intent.putExtra("reserve_data", dataBean);
        setResult(RESERVE_BACK, intent);
        finish();
    }
}
