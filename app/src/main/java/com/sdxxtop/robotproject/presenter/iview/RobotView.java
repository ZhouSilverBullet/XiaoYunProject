package com.sdxxtop.robotproject.presenter.iview;

/**
 * Created by Administrator on 2018/9/19.
 */

public interface RobotView {
    void onSendRequest(int reqId, String reqType, String reqText, String reqParam);
}
