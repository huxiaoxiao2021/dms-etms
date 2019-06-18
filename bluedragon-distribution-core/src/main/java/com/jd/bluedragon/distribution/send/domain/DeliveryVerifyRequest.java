package com.jd.bluedragon.distribution.send.domain;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName DeliveryVerifyRequest
 * @date 2019/6/13
 */
public class DeliveryVerifyRequest {

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 上次箱号运输类型
     */
    private Integer lastBoxTransportType;

    /**
     * 操作站点编号
     */
    private Integer operateSiteCode;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getLastBoxTransportType() {
        return lastBoxTransportType;
    }

    public void setLastBoxTransportType(Integer lastBoxTransportType) {
        this.lastBoxTransportType = lastBoxTransportType;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }
}
