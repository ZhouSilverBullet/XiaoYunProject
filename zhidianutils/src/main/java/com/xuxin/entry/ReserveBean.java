package com.xuxin.entry;

import com.xuxin.http.BaseModel;

import java.io.Serializable;
import java.util.List;

public class ReserveBean extends BaseModel<List<ReserveBean.DataBean>> implements Serializable {

    public static class DataBean implements Serializable {
        private String place;
        private String begin_word;
        private String end_word;

        public String getPlace() {
            return place;
        }

        public void setPlace(String place) {
            this.place = place;
        }

        public String getBegin_word() {
            return begin_word;
        }

        public void setBegin_word(String begin_word) {
            this.begin_word = begin_word;
        }

        public String getEnd_word() {
            return end_word;
        }

        public void setEnd_word(String end_word) {
            this.end_word = end_word;
        }
    }
}
