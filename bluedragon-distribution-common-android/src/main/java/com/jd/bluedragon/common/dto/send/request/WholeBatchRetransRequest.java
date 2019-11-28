package com.jd.bluedragon.common.dto.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

public class WholeBatchRetransRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String oldSendCode;
    private String newSendCode;
    private Integer businessType;
    private CurrentOperate currentOperate;
    private User user;

    public String getOldSendCode() {
        return oldSendCode;
    }

    public void setOldSendCode(String oldSendCode) {
        this.oldSendCode = oldSendCode;
    }

    public String getNewSendCode() {
        return newSendCode;
    }

    public void setNewSendCode(String newSendCode) {
        this.newSendCode = newSendCode;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
