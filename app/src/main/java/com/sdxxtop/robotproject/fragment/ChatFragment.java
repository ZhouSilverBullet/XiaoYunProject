package com.sdxxtop.robotproject.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ainirobot.coreservice.client.listener.TextListener;
import com.sdxxtop.robotproject.MainActivity;
import com.sdxxtop.robotproject.R;
import com.sdxxtop.robotproject.adapter.ChatFragmentAdapter;
import com.sdxxtop.robotproject.bean.ChatContentBean;
import com.sdxxtop.robotproject.control.MainChatControl;
import com.sdxxtop.robotproject.skill.SpeechSkill;
import com.xuxin.entry.ChatWordBean;
import com.xuxin.http.IRequestListener;
import com.xuxin.http.Params;
import com.xuxin.http.RequestCallback;
import com.xuxin.http.RequestExe;

import java.util.ArrayList;

public class ChatFragment extends BaseFragment {
    public static final String TAG = "ChatFragment";

    private ListView listView;
    private ChatFragmentAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        listView = view.findViewById(R.id.chat_fragment_list);
        mAdapter = new ChatFragmentAdapter(new ArrayList<ChatContentBean>());
        listView.setAdapter(mAdapter);

        showSendData(tempValue);
        showLoadData(tempData);

        view.findViewById(R.id.chat_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof MainActivity) {
                    ((MainActivity) getContext()).popFragment();

                    MainChatControl.getInstance().setMessageIsChat(true);
                    MainChatControl.getInstance().setChat(false);
                }
            }
        });
    }

    private String tempValue;
    private String tempData;
    public void showSendData(String value) {
        if (mAdapter != null && listView != null && !TextUtils.isEmpty(value)) {
            mAdapter.createBeanAndAdd(value, false);
            listView.setSelection(mAdapter.getCount() - 1);
            tempValue = null;
        } else {
            tempValue = value;
        }
    }

    public void showLoadData(String value) {
        if (mAdapter != null && listView != null&& !TextUtils.isEmpty(value)) {
            mAdapter.createBeanAndAdd(value, true);
            listView.setSelection(mAdapter.getCount() - 1);
            tempData = null;
        } else {
            tempData = value;
        }
    }

    //可以进行一次聊天
    public synchronized void toOneChat(String value) {
        if (MainChatControl.getInstance().isChat() && !TextUtils.isEmpty(value) && !SpeechSkill.getInstance().isPlaying()) {
            MainChatControl.getInstance().setChat(false);
            MainChatControl.getInstance().setMessageIsChat(false);
            loadData(value);
        }
    }

    public synchronized void loadData(String value) {
        Params params = new Params();
        params.put("it", value);
//        params.put("tp", Constants.TYPE_PROJECT);
        String data = params.getData();
        Log.e(TAG, "params data = " + data);
        showSendData(value);
        RequestExe.createRequest().postChatXiaoYun(data).enqueue(new RequestCallback<>(new IRequestListener<ChatWordBean>() {
            @Override
            public void onSuccess(ChatWordBean chatWordBean) {
//                isRequesting = false;
                ChatWordBean.DataEntry data = chatWordBean.getData();
                Log.e(TAG, "data = " + data);
                if (data != null) {
                    String answer = data.getAnswer();
                    if (!TextUtils.isEmpty(answer)) {
                        payText(answer);
                        showLoadData(answer);
                    }
                }
            }

            @Override
            public void onFailure(int code, String errorMsg) {
//                isRequesting = false;
                MainChatControl.getInstance().setMessageIsChat(true);
                Log.e(TAG, "code = " + code + " errorMsg = " + errorMsg);
            }
        }));
    }

    private void payText(String answer) {
        SpeechSkill.getInstance().playTxt(answer, new TextListener() {
            @Override
            public void onStart() {
                Log.e(TAG, "onStart");
            }

            @Override
            public void onError() {
//                isPayTextVoice = false;
                Log.e(TAG, "onError");
                MainChatControl.getInstance().setMessageIsChat(true);
            }

            @Override
            public void onComplete() {
                MainChatControl.getInstance().setMessageIsChat(true);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        isPayTextVoice = false;
//                    }
//                });
                Log.e(TAG, "onComplete");
            }
        });
    }

    public void clearData() {
        if (mAdapter != null) {
            mAdapter.clearData();
            mAdapter = null;
        }
    }
}
