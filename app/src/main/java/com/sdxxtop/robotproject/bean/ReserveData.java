package com.sdxxtop.robotproject.bean;

import com.xuxin.entry.ReserveBean;

import java.util.ArrayList;
import java.util.List;

public class ReserveData {
    private static ReserveData reserveData;

    public static ReserveData getInstance() {
        if (reserveData == null) {
            reserveData = new ReserveData();
        }
        return reserveData;
    }

    public List<ReserveBean.DataBean> data;

    public void add(List<ReserveBean.DataBean> data) {
        this.data = new ArrayList<>(data);
    }

    public void removeFirst() {
        if (data == null || data.size() == 0) {
            return;
        }
        data.remove(0);
    }

    public ReserveBean.DataBean getFirst() {
        if (data == null || data.size() == 0) {
            return null;
        }

        return data.get(0);
    }

    public ReserveBean.DataBean pop() {
        if (data == null || data.size() == 0) {
            return null;
        }

        ReserveBean.DataBean dataBean = data.get(0);
        removeFirst();
        return dataBean;

    }

}
