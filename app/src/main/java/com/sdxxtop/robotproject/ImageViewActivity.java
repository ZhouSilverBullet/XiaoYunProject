package com.sdxxtop.robotproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sdxxtop.robotproject.adapter.ImageAdapter;
import com.sdxxtop.robotproject.adapter.MyPagerAdapter;
import com.sdxxtop.robotproject.bean.LvJingBean;
import com.sdxxtop.robotproject.camera.DataUtils;
import com.sdxxtop.robotproject.global.App;
import com.sdxxtop.robotproject.presenter.ImgPresenter;
import com.sdxxtop.robotproject.presenter.iview.SkillView;
import com.sdxxtop.robotproject.utils.Filter;
import com.sdxxtop.robotproject.utils.ImgUtil;
import com.xuxin.entry.UpLodeImgBean;
import com.xuxin.http.IRequestListener;
import com.xuxin.http.ImageParams;
import com.xuxin.http.RequestCallback;
import com.xuxin.http.RequestExe;
import com.zyao89.view.zloading.ZLoadingView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.RequestBody;


/**
 * Created by Administrator on 2018/9/17.
 */

public class ImageViewActivity extends BaseActivity implements SkillView, View.OnClickListener, ImageAdapter.OnLvJingClickListener {
    public static final int REFRESH = 010;
    private String TAG = "ImageViewActivity";
    private ViewPager viewPager;
    private RecyclerView recyclerView;
    private LinearLayout llContainor, llRcContainor, llBackPhoto, llDeleteContainor, llConfirmContainor;
    private MyPagerAdapter adapter;
    private int mCurrentPosition;
    private List<LvJingBean> imgList = new ArrayList<>();
    private AlertDialog dayinDialog;
    private ImageView ivQrCode;
    private ImageView ivImgforlj;
    private AlertDialog qrDialog;
    private ZLoadingView zLoading;
    private Bitmap currentBitmap = DataUtils.imgList.get(0);
    private ImageAdapter imageAdapter;
    private Boolean isLvJing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgview);
        initView();
        initVariables();
        initData();
        initDialog();
    }

    private void initData() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.huge);
        imgList.add(new LvJingBean("原图", bitmap, true));
        imgList.add(new LvJingBean("宝丽来", Filter.changeToGray(bitmap, 0), false));
        imgList.add(new LvJingBean("怀旧", Filter.changeToGray(bitmap, 1), false));
        imgList.add(new LvJingBean("泛红", Filter.changeToGray(bitmap, 2), false));
        imgList.add(new LvJingBean("荧光绿", Filter.changeToGray(bitmap, 3), false));
        imgList.add(new LvJingBean("宝石蓝", Filter.changeToGray(bitmap, 4), false));
        imgList.add(new LvJingBean("泛黄", Filter.changeToGray(bitmap, 5), false));
    }

    /**
     * 初始化控件
     */
    private void initView() {
        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(30);
        recyclerView = findViewById(R.id.recyclerView);
        ivImgforlj = findViewById(R.id.iv_imgforlj);
        llContainor = findViewById(R.id.ll_containor);       // 包裹底部4个tab的线性布局
        llRcContainor = findViewById(R.id.ll_rc_containor);  // 包裹recyclerview的线性布局
        llBackPhoto = findViewById(R.id.ll_back_photo);      // 包裹没有照片返回的线性布局
        llDeleteContainor = findViewById(R.id.ll_delete_containor);      // 包裹滤镜返回的线性布局
        llConfirmContainor = findViewById(R.id.ll_confirm_containor);      // 包裹滤镜确认的线性布局
        findViewById(R.id.tv_dayin).setOnClickListener(this);
        findViewById(R.id.tv_delect).setOnClickListener(this);
        findViewById(R.id.tv_upLode).setOnClickListener(this);
        findViewById(R.id.tv_lvjing).setOnClickListener(this);
        llBackPhoto.setOnClickListener(this);
        llDeleteContainor.setOnClickListener(this);
        llConfirmContainor.setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(this, imgList, this);
        recyclerView.setAdapter(imageAdapter);

        if (DataUtils.imgList == null) {
            Log.e("数据列表为空==", "'false");
            return;
        }
        adapter = new MyPagerAdapter(this, DataUtils.imgList);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("onPageScrolled---move==", "true");
                // 把当前显示的position传递出去
                mCurrentPosition = position;
                currentBitmap = DataUtils.imgList.get(mCurrentPosition);
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("onPageSelected---move==", "true");
                // 把当前显示的position传递出去
                mCurrentPosition = position;
                currentBitmap = DataUtils.imgList.get(mCurrentPosition);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initVariables() {
        ImgPresenter.getInstance().addView(this);
    }

    /**
     * 将图片上传至网络
     */
    private void upDataImg() {
        File file = null;
        if (isLvJing) {
            ivImgforlj.setDrawingCacheEnabled(true);
            file = ImgUtil.compressImage(Bitmap.createBitmap(ivImgforlj.getDrawingCache()));
            ivImgforlj.setDrawingCacheEnabled(false);
        } else {
            file = ImgUtil.compressImage(DataUtils.imgList.get(mCurrentPosition));
        }
        Log.e("文件大小==", "" + file.length());
        ImageParams params = new ImageParams();
        params.addImagePath("img", file);
        HashMap<String, RequestBody> imgData = params.getImgData();
        Log.e(TAG, "params data = " + imgData);
        RequestExe.createRequest().upDataImg(imgData).enqueue(new RequestCallback<>(new IRequestListener<UpLodeImgBean>() {
            @Override
            public void onSuccess(UpLodeImgBean upLodeImgBean) {
                UpLodeImgBean.DataBean data = upLodeImgBean.getData();
                if (data != null) {
                    if (qrDialog != null) {
                        zLoading.setVisibility(View.GONE);
                        String qr_code = data.getQr_code();
                        Glide.with(ImageViewActivity.this).load(qr_code).into(ivQrCode);
                        ivQrCode.setVisibility(View.VISIBLE);
                        isLvJing = false;
                    }
                }
                Log.e("-----------", "成功");
            }

            @Override
            public void onFailure(int code, String errorMsg) {
                Log.e(TAG, "code = " + code + " errorMsg = " + errorMsg);
                if (qrDialog != null) {
                    qrDialog.dismiss();
                }
                isLvJing = false;
            }
        }));
    }

    @Override
    public void onSpeechParResult(String speechMessage) {

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
    protected void onDestroy() {
        ImgPresenter.getInstance().removeView();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_lvjing:  // 滤镜
                if (DataUtils.imgList.size() == 0) {
                    Toast.makeText(this, "没有照片请返回拍照", Toast.LENGTH_SHORT).show();
                    return;
                }
                recyclerView.scrollToPosition(0);
                viewPager.setVisibility(View.GONE);
                llContainor.setVisibility(View.GONE);
                llRcContainor.setVisibility(View.VISIBLE);
                ivImgforlj.setVisibility(View.VISIBLE);
                ivImgforlj.setImageBitmap(currentBitmap);
                break;
            case R.id.tv_upLode:  // 上传
                if (DataUtils.imgList.size() == 0) {
                    Toast.makeText(this, "没有照片请返回拍照", Toast.LENGTH_SHORT).show();
                    return;
                }
                zLoading.setVisibility(View.VISIBLE);
                ivQrCode.setVisibility(View.GONE);
                qrDialog.show();
                upDataImg();
                break;
            case R.id.tv_dayin:   // 打印
//                daYinDialog();
                break;
            case R.id.tv_delect:  // 删除
                if (DataUtils.imgList.size() == 0) {
                    Toast.makeText(this, "没有照片请返回拍照", Toast.LENGTH_SHORT).show();
                    return;
                }
                delectDialog();
                break;
            case R.id.ll_back_photo:  // 返回拍照
                App.getInstance().getHandler().sendEmptyMessage(REFRESH);
                finish();
                break;
            case R.id.ll_delete_containor:  // 退出滤镜
                imageAdapter.setFirstSelect();
                viewPager.setVisibility(View.VISIBLE);
                llContainor.setVisibility(View.VISIBLE);
                llRcContainor.setVisibility(View.GONE);
                ivImgforlj.setVisibility(View.GONE);
                break;
            case R.id.ll_confirm_containor:  // 确定选择
                isLvJing = true;
                zLoading.setVisibility(View.VISIBLE);
                ivQrCode.setVisibility(View.GONE);
                qrDialog.show();
                upDataImg();
                break;
        }
    }

    /**
     * 删除对话框
     */
    private void delectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_delect, null);
        final AlertDialog delectDialog = builder.create();
        Window window = delectDialog.getWindow();
        delectDialog.setView(view);
        delectDialog.show();
        window.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams p = window.getAttributes();
        p.width = getResources().getDisplayMetrics().widthPixels; //设置dialog的宽度为当前手机屏幕的宽度
        window.setAttributes(p);
        window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));  // 设置背景为透明
        view.findViewById(R.id.tv_delect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataUtils.imgList.size() > 0) {
                    DataUtils.imgList.remove(mCurrentPosition);
                    adapter.setData(DataUtils.imgList);
                }
                if (DataUtils.imgList.size() == 0) {
                    llBackPhoto.setVisibility(View.VISIBLE);
                }
                delectDialog.dismiss();
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delectDialog.dismiss();
            }
        });
    }

    /**
     * 打印弹出确认对话框
     */
    private void daYinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_dayin, null);
        dayinDialog = builder.create();
        Window window = dayinDialog.getWindow();
        dayinDialog.setView(view);
        dayinDialog.show();
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        view.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDataImg();
            }
        });
        view.findViewById(R.id.tv_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayinDialog.dismiss();
            }
        });
    }

    private void initDialog() {
        /** 上传图片到手机弹出对话框 **/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.qr_code, null);
        ivQrCode = view.findViewById(R.id.iv_qr_code);
        zLoading = view.findViewById(R.id.z_loading);
        qrDialog = builder.create();
        Window window = qrDialog.getWindow();
        qrDialog.setView(view);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
    }

    @Override
    public void onLJClick(int position) {
        if (position == 0) {
            ivImgforlj.setImageBitmap(currentBitmap);
        } else {
            Bitmap bitmap = Filter.changeToGray(currentBitmap, position - 1);
            ivImgforlj.setImageBitmap(bitmap);
        }
    }
}
