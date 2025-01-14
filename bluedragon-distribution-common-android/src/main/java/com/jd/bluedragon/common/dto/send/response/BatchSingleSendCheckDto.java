package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;

/**
 * BatchSingleSendCheckDto
 * 批次校验返回
 * @author jiaowenqiang
 * @date 2019/6/14
 */
public class BatchSingleSendCheckDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 目的地站点编码
     */
    private Integer receiveSiteCode;

    @Override
    public String toString() {
        return "BatchSingleSendCheckDto{" +
                "sendCode='" + sendCode + '\'' +
                ", receiveSiteCode=" + receiveSiteCode +
                '}';
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }
}
