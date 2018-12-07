/*
 * Copyright 2014 Flavio Faria
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sdxxtop.robotproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ainirobot.coreservice.client.listener.TextListener;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flaviofaria.kenburnsview.Transition;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.presenter.iview.SkillView;


public class SingleImageActivity extends BaseActivity implements Handler.Callback, SkillView {

    int[] imgList = {R.drawable.img2, R.drawable.img4, R.drawable.img8,
            R.drawable.img9, R.drawable.img11, R.drawable.img12, R.drawable.img13, R.drawable.img15};
    String[] titleList = {"织布主题民宿", "青旅主题民宿", "花生榻榻米", "枯木大床房", "木作主题民宿", "花生榻榻木", "蓝染标准间", "创客公寓"};
    String[] decsList = {"由传统工匠手工打磨制作，能容纳4-8人住宿", "本民宿与木作主题民宿连为一体，是青年人休闲和交流的场所"
            , "取于木作主题民宿榻榻米房间，上下铺形式，适合4位客人居住", "民宿院落内一个大床房间，房间温馨舒适，适合一家人居住",
            "利用本地桃木等木材，就地加工，带动传统木工工艺加工产业发展", "取于木作主题民宿榻榻米房间，上下铺形式，适合4位客人居住",
            "屋内格局以原木为主体，极简的实木家具，呈现出独居民族特色的居室", "创客公寓位于公寓三楼，房间内独立卫生间并且有一个视野非常开阔的阳台"};

    private KenBurnsView mImg;
    public static final int HANDLER_NUM = 0;
    int count = 0;
    private TextView tvTitle;
    private TextView tvDesc;

    Handler handler = new Handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏状态栏

        App.getInstance().addView(this);
        setContentView(R.layout.single_image);
        initView();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvDesc = findViewById(R.id.tv_desc);
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle.setText("朱家林田园客厅");
        tvDesc.setText("田园客厅在一楼分设了开放式小礼堂，烘焙坊以及农产品DIY体验区");
        mImg = (KenBurnsView) findViewById(R.id.img);
        mImg.setImageResource(R.drawable.img1);

        mImg.setTransitionListener(new KenBurnsView.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                handler.sendEmptyMessageDelayed(HANDLER_NUM, 6000);
            }

            @Override
            public void onTransitionEnd(Transition transition) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (mImg != null) {
            mImg.pause();
        }
        App.getInstance().removeView(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HANDLER_NUM:
                Log.e("position", "" + count);
                Log.e("总数", "" + imgList.length);

                if (count < imgList.length) {
                    mImg.setImageResource(imgList[count]);
                    tvTitle.setText(titleList[count]);
                    tvDesc.setText(decsList[count]);
                    count++;

                } else {
                    count = 0;
                    mImg.setImageResource(R.drawable.img1);
                    tvTitle.setText("朱家林田园客厅");
                    tvDesc.setText("田园客厅在一楼分设了开放式小礼堂，烘焙坊以及农产品DIY体验区");
                }
                break;
        }
        return true;
    }

    @Override
    public void onSpeechParResult(String speechMessage) {
        if (getString(R.string.robot_quit).equals(speechMessage)) {
//            SpeechSkill.getInstance().removeCallBack(this);
            App.getInstance().removeView(this);
            App.getInstance().getSkillApi().playText(getString(R.string.robot_name) + "已为您退出当前页面", new TextListener() {
                @Override
                public void onComplete() {
                    super.onComplete();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onStartSkill() {

    }

    @Override
    public void onStopSkill() {

    }

    @Override
    public void onVolumeChange(int volume) {

    }

    @Override
    public void onQueryEnded(int query) {

    }

    @Override
    public void onSendRequest(String reqType, String reqText, String reqParam) {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onResult(String value) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}