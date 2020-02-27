package com.jd.bluedragon.distribution.weightVolume.domain;

import java.io.Serializable;

/**
 * 称重量方失败返回实体
 *
 * @author: hujiping
 * @date: 2020/1/13 21:23
 */
public class WeightAndVolumeFail implements Serializable {
    private static final long serialVersionUID = 1L;

    private String strCode;

    private String failMessage;

    public String getStrCode() {
        return strCode;
    }

    public void setStrCode(String strCode) {
        this.strCode = strCode;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public void setFailMessage(String failMessage) {
        this.failMessage = failMessage;
    }
}
