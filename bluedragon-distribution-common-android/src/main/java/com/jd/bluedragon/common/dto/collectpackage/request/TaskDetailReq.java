package com.jd.bluedragon.common.dto.collectpackage.request;

import java.io.Serializable;

public class TaskDetailReq implements Serializable {
    private static final long serialVersionUID = -6548692946108546373L;
    
    private String bizId;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
