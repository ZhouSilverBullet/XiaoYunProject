package com.sdxxtop.robotproject.strip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.TypedArray;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdxxtop.robotproject.BaseActivity;
import com.sdxxtop.robotproject.R;
import com.xuxin.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.broadlink.sdk.data.controller.BLDNADevice;

public class DevListActivity extends Activity {

    private ListView listView;
    private DevAdapter devAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_list);

        initView();
    }

    public void initView() {
        listView = findViewById(R.id.dev_list_view);
        StripController.getInstance().setDevListListener(new StripController.DevListListener() {
            @Override
            public void onDevList(Map<String, BLDNADevice> localMap) {
                if (localMap != null && localMap.size() > 0) {
                    List<BLDNADevice> list = new ArrayList<>();
                    for (String s : localMap.keySet()) {
                        BLDNADevice e = localMap.get(s);
                        list.add(e);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (devAdapter == null) {
                                devAdapter = new DevAdapter(list);
                                listView.setAdapter(devAdapter);
                            } else {
                                devAdapter.clearAndAddData(list);
                            }
                        }
                    });
                }
            }
        });

        Map<String, BLDNADevice> localMap = StripController.getInstance().getLocalMap();
        if (localMap != null && localMap.size() > 0) {
            List<BLDNADevice> list = new ArrayList<>();
            for (String s : localMap.keySet()) {
                BLDNADevice e = localMap.get(s);
                list.add(e);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (devAdapter == null) {
                        devAdapter = new DevAdapter(list);
                        listView.setAdapter(devAdapter);
                    } else {
                        devAdapter.clearAndAddData(list);
                    }
                }
            });
        }
    }

    private class DevAdapter extends BaseAdapter {
        private List<BLDNADevice> list;

        public DevAdapter(List<BLDNADevice> list) {
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
                convertView = View.inflate(parent.getContext(), R.layout.item_dev_list, null);
                convertView.setTag(viewHolder);
                viewHolder.nameText = convertView.findViewById(R.id.dev_name);
                viewHolder.macText = convertView.findViewById(R.id.dev_mac);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final BLDNADevice bldnaDevice = list.get(position);
            String devName = PreferenceUtils.getInstance(parent.getContext()).getStringParam(bldnaDevice.getDid());
            viewHolder.nameText.setText(devName + "(" + bldnaDevice.getDid() + ")");
            viewHolder.macText.setText(bldnaDevice.getName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    LinearLayout linearLayout = new LinearLayout(v.getContext());

                    TextView textView = new TextView(v.getContext());
                    textView.setText("控制");
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    linearLayout.addView(textView);

                    Button openButton = new Button(v.getContext());
                    openButton.setText("打开");
                    linearLayout.addView(openButton);

                    Button closeButton = new Button(v.getContext());
                    closeButton.setText("关闭");
                    linearLayout.addView(closeButton);
                    linearLayout.setGravity(Gravity.CENTER);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    builder.setView(linearLayout);
                    builder.show();

                    openButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StripController.getInstance().open(bldnaDevice.getDid());
                        }
                    });

                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StripController.getInstance().close(bldnaDevice.getDid());
                        }
                    });

                }
            });
            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    LinearLayout linearLayout = new LinearLayout(v.getContext());
                    EditText editText = new EditText(v.getContext());
                    linearLayout.addView(editText, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    TextView textView = new TextView(v.getContext());
                    textView.setText("请填写控制名字");
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    linearLayout.addView(textView);

                    Button button = new Button(v.getContext());
                    button.setText("确定");
                    linearLayout.addView(button);
                    linearLayout.setGravity(Gravity.CENTER);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    builder.setView(linearLayout);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String name = editText.getText().toString().trim();
                            if (!TextUtils.isEmpty(name)) {
                                PreferenceUtils.getInstance(parent.getContext()).saveParam(bldnaDevice.getDid(), name);
                                Toast.makeText(DevListActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(DevListActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.show();

                    return false;
                }
            });

            return convertView;
        }

        public void clearAndAddData(List<BLDNADevice> list) {
            if (this.list != null && list != null) {
                this.list.clear();
                this.list.addAll(list);
                notifyDataSetChanged();
            }
        }

        class ViewHolder {
            TextView nameText;
            TextView macText;
        }
    }
}
