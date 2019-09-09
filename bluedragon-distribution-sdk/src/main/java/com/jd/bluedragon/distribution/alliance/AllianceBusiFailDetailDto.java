package com.jd.bluedragon.distribution.alliance;

import java.io.Serializable;

/**
 * 加盟商失败返回实体
 */
public class AllianceBusiFailDetailDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private String opeCode;

    private String failMessage;

    public String getOpeCode() {
        return opeCode;
    }

    public void setOpeCode(String opeCode) {
        this.opeCode = opeCode;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }
}
