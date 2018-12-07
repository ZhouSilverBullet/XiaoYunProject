package com.sdxxtop.robotproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sdxxtop.robotproject.R;
import com.sdxxtop.robotproject.bean.ChatContentBean;
import com.sdxxtop.robotproject.utils.ChatKeywordUtil;

import java.util.List;

/**
 * Created by Administrator on 2018/9/18.
 */

public class ChatFragmentAdapter extends BaseAdapter {
    private List<ChatContentBean> list;

    public ChatFragmentAdapter(List<ChatContentBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment_chat, parent, false);
            viewHolder.leftLayout = convertView.findViewById(R.id.left_layout);
            viewHolder.leftLayout2 = convertView.findViewById(R.id.left_layout2);
            viewHolder.leftText = convertView.findViewById(R.id.left_text);
            viewHolder.leftText2 = convertView.findViewById(R.id.left_text2);
            viewHolder.leftText3 = convertView.findViewById(R.id.left_text3);

            viewHolder.rightLayout = convertView.findViewById(R.id.right_layout);
            viewHolder.rightText = convertView.findViewById(R.id.right_text);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatContentBean chatContentBean = list.get(position);
        if (chatContentBean.isLeft) {
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout2.setVisibility(View.VISIBLE);
            viewHolder.leftText.setText(chatContentBean.content);


            viewHolder.leftText2.setText("“" + chatContentBean.keyword1 + "”");
            viewHolder.leftText3.setText("“" + chatContentBean.keyword2 + "”");
        } else {
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.leftLayout2.setVisibility(View.GONE);
            viewHolder.rightText.setText(chatContentBean.content);
        }
        return convertView;
    }

    public void clearData() {
        if (getCount() != 0) {
            list.clear();
            notifyDataSetChanged();
        }
    }

    static class ViewHolder {
        View leftLayout;
        View leftLayout2;
        TextView leftText;
        TextView leftText2;
        TextView leftText3;
        View rightLayout;
        TextView rightText;
    }

    public void add(ChatContentBean chatContentBean) {
        if (list != null && chatContentBean != null) {
            list.add(chatContentBean);
            notifyDataSetChanged();
        }
    }

    public void createBeanAndAdd(String content, boolean isLeft) {
        ChatContentBean chatContentBean = new ChatContentBean();
        chatContentBean.content = content;
        List<String> keyword = ChatKeywordUtil.getKeyword();
        chatContentBean.keyword1 = keyword.get(0);
        chatContentBean.keyword2 = keyword.get(1);
        chatContentBean.isLeft = isLeft;
        if (list != null) {
            list.add(chatContentBean);
            notifyDataSetChanged();
        }
    }
}
