package com.xuxin.http;

import com.xuxin.entry.ChatWordBean;
import com.xuxin.entry.ReserveBean;
import com.xuxin.entry.UpLodeImgBean;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * Created by Administrator on 2018/8/6.
 */

public interface ApiService {

    @Multipart
    @POST("camera/photo")
    Call<UpLodeImgBean> upDataImg(@PartMap HashMap<String, RequestBody> map);


    @FormUrlEncoded
    @POST("chat/word")
    Call<ChatWordBean> postChatWord(@Field("data") String data);

    @FormUrlEncoded
    @POST("chat/xiaoyun")
    Call<ChatWordBean> postChatXiaoYun(@Field("data") String data);

    @FormUrlEncoded
    @POST("chat/keyword")
    Call<ChatWordBean> postChatKeyword(@Field("data") String data);

    @FormUrlEncoded
    @POST("chat/video")
    Call<BaseModel> postChatVideo(@Field("data") String data);

    @FormUrlEncoded
    @POST("subscribe/check")
    Call<ReserveBean> postSubscribeCheck(@Field("data") String data);

}
