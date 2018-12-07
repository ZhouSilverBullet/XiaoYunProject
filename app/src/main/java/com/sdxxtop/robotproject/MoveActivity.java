package com.sdxxtop.robotproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.coreservice.client.listener.TextListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sdxxtop.robotproject.bean.LocationBean;
import com.sdxxtop.robotproject.control.MessageManager;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.presenter.iview.SkillView;
import com.sdxxtop.robotproject.skill.MoveSkill;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.sdxxtop.robotproject.utils.FrameAnimationUtils;
import com.sdxxtop.robotproject.widget.AutoPlayView;

import java.lang.reflect.Type;
import java.util.List;

public class MoveActivity extends SecondBaseActivity {

    public static final String TAG = "MoveActivity";
    private MoveActivity mContext;
    private ListView moveList;
    private ImageView peopleImage;
    private AutoPlayView moveAutoPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move);
        mContext = this;

        SpeechSkill.getInstance().playTxt("需要我带你去哪");

        App.getInstance().addView(this);
        initView();
        RobotApi.getInstance().getPlaceListWithName(0, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                super.onResult(result, message);
                Log.e(TAG, "onResult: result : " + result + " message: " + message);
                switch (result) {
                    case 1:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!TextUtils.isEmpty(message)) {
                                    handlerPlaceList(message);
                                }
                            }
                        });
                        break;
                }
            }
        });
    }

    private void initView() {
        moveList = findViewById(R.id.move_list);
        peopleImage = findViewById(R.id.move_people_image);

        AnimationDrawable drawable = FrameAnimationUtils.getInstance().getDrawable(3);
        peopleImage.setImageDrawable(drawable);
        FrameAnimationUtils.getInstance().start(3);

        moveAutoPlay = findViewById(R.id.move_auto_play);
//        moveAutoPlay.startPlay();

        findViewById(R.id.chat_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void handlerPlaceList(String message) {
        List<LocationBean> locationBeanList = new Gson().fromJson(message, new TypeToken<List<LocationBean>>() {
        }.getType());
        if (locationBeanList != null && locationBeanList.size() > 0) {
            moveList.setAdapter(new MoveAdapter(locationBeanList));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().removeView(this);
        MessageManager.getInstance().setPause(false);
    }

    @Override
    public void onSpeechParResult(String speechMessage) {
        if (TextUtils.isEmpty(speechMessage)) {
            return;
        }
        super.onSpeechParResult(speechMessage);
        if (speechMessage.contains("带我去大门")) {
            SpeechSkill.getInstance().playTxt("正在去往大门，跟我来吧！", new TextListener() {
                @Override
                public void onComplete() {
                    super.onComplete();
                    MoveSkill.getInstance().goPosition(mContext, "大门");
                }
            });
        } else if (speechMessage.contains("带我去讲解台")) {
            SpeechSkill.getInstance().playTxt("正在去往讲解台，跟我来吧！", new TextListener() {
                @Override
                public void onComplete() {
                    super.onComplete();
                    MoveSkill.getInstance().goPosition(mContext, "讲解台");
                }
            });
        } else if (speechMessage.contains("带我去展厅")) {
            SpeechSkill.getInstance().playTxt("正在去往讲展厅，跟我来吧！", new TextListener() {
                @Override
                public void onComplete() {
                    super.onComplete();
                    MoveSkill.getInstance().goPosition(mContext, "展厅");
                }
            });
        } else if (speechMessage.contains("退出导航")) {
            SpeechSkill.getInstance().playTxt("退出导航成功", new TextListener() {
                @Override
                public void onComplete() {
                    super.onComplete();
                    RobotApi.getInstance().stopGoPosition(0);
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

    private class MoveAdapter extends BaseAdapter {
        private List<LocationBean> locationBeans;

        public MoveAdapter(List<LocationBean> locationBeans) {
            this.locationBeans = locationBeans;
        }

        @Override
        public int getCount() {
            return locationBeans == null ? 0 : locationBeans.size();
        }

        @Override
        public Object getItem(int position) {
            return locationBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final LocationBean locationBean = locationBeans.get(position);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = View.inflate(parent.getContext(), R.layout.move_item, null);
                viewHolder.textView = convertView.findViewById(R.id.item_move_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String name = locationBean.getName();
            viewHolder.textView.setText(name);
            GradientDrawable background = (GradientDrawable) viewHolder.textView.getBackground();
            if (!TextUtils.isEmpty(name) && name.equals("接待点")) {
                viewHolder.textView.setCompoundDrawables(getResources().getDrawable(R.drawable.move_user),null,null,null);
                background.setColor(Color.parseColor("#ff00ff"));
            } else {
                viewHolder.textView.setCompoundDrawables(getResources().getDrawable(R.drawable.move_house),null,null,null);
                background.setColor(Color.parseColor("#00ffff"));
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (locationBean != null) {
                        handleResult(locationBean.getName());
                    }
                }
            });
            return convertView;
        }

        private void handleResult(String name) {
            Intent intent = new Intent();
            intent.putExtra("location", name);
            setResult(101, intent);
            finish();
        }

        private class ViewHolder {
            TextView textView;
        }
    }
}
