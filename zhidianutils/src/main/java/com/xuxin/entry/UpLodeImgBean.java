package com.xuxin.entry;

import com.xuxin.http.BaseModel;

/**
 * Created by Administrator on 2018/9/20.
 */

public class UpLodeImgBean extends BaseModel<UpLodeImgBean.DataBean> {

    public static class DataBean {
        private String img;
        private String qr_code;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getQr_code() {
            return qr_code;
        }

        public void setQr_code(String qr_code) {
            this.qr_code = qr_code;
        }
    }

}
