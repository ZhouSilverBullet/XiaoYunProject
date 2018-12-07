package com.xuxin.entry;

import com.xuxin.http.BaseModel;

/**
 * Created by Administrator on 2018/9/18.
 */

public class ChatWordBean extends BaseModel<ChatWordBean.DataEntry> {

    public static class DataEntry {
        private String answer;

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
