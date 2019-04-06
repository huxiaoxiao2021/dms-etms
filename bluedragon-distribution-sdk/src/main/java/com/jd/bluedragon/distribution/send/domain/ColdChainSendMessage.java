package com.jd.bluedragon.distribution.send.domain;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ColdChainSendMessage
 * @date 2019/4/1
 */
public class ColdChainSendMessage {

    /**
     * 运输计划编号
     */
    private String transPlanCode;

    /**
     * 发货批次号
     */
    private String sendCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 发货类型：1 - 发货 ， 2 - 取消发货
     */
    private Integer sendType;

    /**
     * 操作人名称
     */
    private String operateUserName;

    /**
     * 操作人编号
     */
    private String operateUserErp;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 始发站点编号(七位编码)
     */
    private String createSiteCode;

    /**
     * 目的站点编号(七位编码)
     */
    private String receiveSiteCode;

    public String getTransPlanCode() {
        return transPlanCode;
    }

    public void setTransPlanCode(String transPlanCode) {
        this.transPlanCode = transPlanCode;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getSendType() {
        return sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public String getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(String createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(String receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

}
