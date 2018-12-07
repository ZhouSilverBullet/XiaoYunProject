package com.sdxxtop.robotproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.sdxxtop.robotproject.ChatActivity;
import com.sdxxtop.robotproject.MainActivity;
import com.sdxxtop.robotproject.MoveActivity;
import com.sdxxtop.robotproject.R;
import com.sdxxtop.robotproject.ReserveActivity;
import com.sdxxtop.robotproject.adapter.GridAdapter;
import com.sdxxtop.robotproject.adapter.MainListAdapter;
import com.sdxxtop.robotproject.bean.MainListBean;

import java.util.ArrayList;

public class MainFragment extends BaseFragment {
    private GridView gridview;
    private GridAdapter adapter;
    private ListView listView;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        gridview = view.findViewById(R.id.gridview);
        adapter = new GridAdapter();
        gridview.setAdapter(adapter);

        adapter.setItemClickListener(new GridAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = null;
                switch (position) {
//                    case 0:
//                        intent = new Intent(_mActivity, ChatActivity.class);
//                        startActivity(intent);
//                        break;
//                    case 1:
//                        intent = new Intent(_mActivity, MoveActivity.class);
//                        startActivityForResult(intent, 100);
//                        break;
                    case 0:
                        intent = new Intent(_mActivity, ReserveActivity.class);
                        _mActivity.startActivityForResult(intent, 1);
                        break;
                    case 1:
                        intent = new Intent(_mActivity, ReserveActivity.class);
                        _mActivity.startActivityForResult(intent, 1);
                        break;
                }
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).longClick();
                }
                return false;
            }
        });

        listView = view.findViewById(R.id.listview);
        final ArrayList<MainListBean> list = getDataList();

        listView.setAdapter(new MainListAdapter(list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainListBean listBean = list.get(position);
                Intent intent = null;
                switch (listBean.type) {
                    case MainListBean.TYPE_ASK_WAY:
                        intent = new Intent(_mActivity, MoveActivity.class);
                        startActivityForResult(intent, 100);
                        break;
                    case MainListBean.TYPE_ANSWER:
                        intent = new Intent(_mActivity, ChatActivity.class);
                        _mActivity.startActivity(intent);
                        break;
                    case MainListBean.TYPE_CHAT:
                        intent = new Intent(_mActivity, ChatActivity.class);
                        _mActivity.startActivity(intent);
                        break;
                    case MainListBean.TYPE_WEATHER:
                        break;
                    case MainListBean.TYPE_INTRODUCE:
                        break;
                }
            }
        });
    }

    private ArrayList<MainListBean> getDataList() {
        ArrayList<MainListBean> list = new ArrayList<>();
        list.add(new MainListBean(MainListBean.TYPE_ASK_WAY, R.drawable.ask_way_icon, "问路", "“带我去...”"));
        list.add(new MainListBean(MainListBean.TYPE_ANSWER, R.drawable.answer_icon, "问答", "“小云有什么功能”"));
        list.add(new MainListBean(MainListBean.TYPE_CHAT, R.drawable.chat_icon, "聊天", "“你会干什么”"));
        list.add(new MainListBean(MainListBean.TYPE_WEATHER, R.drawable.weather_icon, "天气", "“今天有雨吗”"));
        list.add(new MainListBean(MainListBean.TYPE_INTRODUCE, R.drawable.introduce_icon, "介绍", "“介绍旭兴科技公司”"));
        return list;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == 101 && data != null) {
            String location = data.getStringExtra("location");
            if (getActivity() instanceof MainActivity && !TextUtils.isEmpty(location)) {
                ((MainActivity) getActivity()).navigation(location, false);
            }
        }
    }
}
