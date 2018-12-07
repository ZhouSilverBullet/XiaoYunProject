package com.sdxxtop.robotproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdxxtop.robotproject.adapter.PhoneNumAdapter;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.xuxin.entry.ReserveBean;
import com.xuxin.http.BaseModel;
import com.xuxin.http.IRequestListener;
import com.xuxin.http.Params;
import com.xuxin.http.RequestCallback;
import com.xuxin.http.RequestExe;

import java.util.List;

public abstract class KeyboardActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnClickListener, Handler.Callback {

    public static final int RESERVE_COUNT = 5;

    private GridView gridview;
    private TextView tvNum;
    private TextView tvBig;
    private TextView tvNum0;
    private ImageView ivDelect;
    private RelativeLayout rlNumContainor;
    private LinearLayout llBackContainor;
    private Handler handler;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        gridview = findViewById(R.id.gridview);
        PhoneNumAdapter phoneNumAdapter = new PhoneNumAdapter(this);
        gridview.setAdapter(phoneNumAdapter);
        gridview.setOnItemClickListener(this);
        handler = new Handler(this);
        initView();
    }

    private void initView() {
        tvNum = findViewById(R.id.tv_num);
        tvBig = findViewById(R.id.tv_big);
        tvNum0 = findViewById(R.id.tv_num_0);
        ivDelect = findViewById(R.id.iv_delect);
        rlNumContainor = findViewById(R.id.rl_num_containor);
        llBackContainor = findViewById(R.id.ll_back_containor);
        ivDelect.setOnClickListener(this);
        tvNum0.setOnClickListener(this);
        llBackContainor.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (count == RESERVE_COUNT) {
            return;
        } else {
            inputNum(position + 1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_delect:
                String text = tvNum.getText().toString().trim();
                if (!TextUtils.isEmpty(text)) {
                    deleteNum(text);
                } else {
                    return;
                }
                break;
            case R.id.tv_num_0:
                if (count == RESERVE_COUNT) {
                    return;
                } else {
                    inputNum(0);
                }
                break;
            case R.id.ll_back_containor:
                finish();
                break;
        }
    }

    /**
     * 号码删除
     */
    private void deleteNum(String text) {
        String tempText = text.substring(0, text.length() - 1).trim();
        if (TextUtils.isEmpty(tempText)) {
            rlNumContainor.setVisibility(View.GONE);
//            tvBig.setVisibility(View.VISIBLE);
            if (changeListener != null) {
                changeListener.onChange(false);
            }
        }
        tvNum.setText(tempText);
        count--;
    }

    /**
     * 输入号码
     *
     * @param num 号码
     */
    public void inputNum(int num) {
        String text = tvNum.getText().toString();
        rlNumContainor.setVisibility(View.VISIBLE);
//        tvBig.setVisibility(View.GONE);
        if (count == 0) {
            tvNum.setText(text + num);
        } else {
            tvNum.setText(text + "  " + num);
        }
        count++;
        if (count == RESERVE_COUNT) {
            loadSubscribeCheck();
            handler.sendEmptyMessageDelayed(100, 500);
        }
        if (changeListener != null) {
            String s = tvNum.getText().toString();
            if (!TextUtils.isEmpty(s) && s.length() == 1) {
                changeListener.onChange(true);
            }
        }
    }

    private void loadSubscribeCheck() {
        String numCode = tvNum.getText().toString().trim();
        String code = numCode.replace(" ", "");
        Params params = new Params();
        params.put("cd", code);
        RequestExe.createRequest().postSubscribeCheck(params.getData()).enqueue(new RequestCallback<>(new IRequestListener<ReserveBean>() {
            @Override
            public void onSuccess(ReserveBean reserveBean) {
                List<ReserveBean.DataBean> data = reserveBean.getData();
                if (data != null && data.size() > 0 ) {
                    handleData(data);
                } else {
                    SpeechSkill.getInstance().playTxt("地址获取失败");
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                if (!TextUtils.isEmpty(errorMsg)) {
                    SpeechSkill.getInstance().playTxt(errorMsg);
                } else {
                    SpeechSkill.getInstance().playTxt("输入预约码错误");
                }
            }
        }));
    }

    public abstract void handleData(List<ReserveBean.DataBean> data);

    private TextChangeListener changeListener = new TextChangeListener() {
        @Override
        public void onChange(boolean hasValue) {
            if (hasValue) {
                ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(tvBig, "translationY", 0, -160f);
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(tvBig, "scaleX", 1f, 0.5f);
                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(tvBig, "scaleY", 1f, 0.5f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(translationAnimator, scaleXAnimator, scaleYAnimator);
                animatorSet.setDuration(300);
                animatorSet.start();
            } else {
                ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(tvBig, "translationY", -160f, 0);
                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(tvBig, "scaleX", 0.5f, 1f);
                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(tvBig, "scaleY", 0.5f, 1f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(translationAnimator, scaleXAnimator, scaleYAnimator);
                animatorSet.setDuration(300);
                animatorSet.start();
            }

        }
    };

    public interface TextChangeListener {
        void onChange(boolean hasValue);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                tvNum.setTextColor(Color.parseColor("#f44336"));
                Animation snake = AnimationUtils.loadAnimation(this, R.anim.shake);
                snake.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        tvNum.setTextColor(Color.parseColor("#ffffff"));
                        tvNum.setText("");
                        rlNumContainor.setVisibility(View.GONE);
//                        tvBig.setVisibility(View.VISIBLE);
                        if (changeListener != null) {
                            changeListener.onChange(false);
                        }
                        count = 0;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                tvNum.startAnimation(snake);
                break;
        }
        return true;
    }
}
