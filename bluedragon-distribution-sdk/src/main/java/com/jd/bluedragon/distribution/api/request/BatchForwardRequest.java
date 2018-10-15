package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * Created by hujiping on 2018/8/1.
 */
public class BatchForwardRequest extends JdRequest {

    /**
     *  旧批次号
     * */
    private String oldSendCode;
    /**
     * 新批次号
     * */
    private String newSendCode;

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
}
