package com.xuxin.http;

/**
 * Created by Administrator on 2018/5/7.
 */

public interface IRequestListener<T extends BaseModel> {
    void onSuccess(T t);
    void onFailure(int code, String errorMsg);
}
