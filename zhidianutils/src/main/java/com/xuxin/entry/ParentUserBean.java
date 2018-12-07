package com.xuxin.entry;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/8/1.
 */

public class ParentUserBean implements Serializable {
    public Integer userId;
    public Integer typeId;

    public String getParentUserId() {
        return typeId + "|" + userId;
    }
}
