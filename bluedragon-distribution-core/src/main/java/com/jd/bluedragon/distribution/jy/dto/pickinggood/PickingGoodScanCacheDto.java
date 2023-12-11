package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/12/11 20:45
 * @Description
 */
public class PickingGoodScanCacheDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String barCode;
    //提货人
    private String operatorErp;
    //提货时间
    private Long operateTime;
    //提货任务
    private String bizId;
    //是否同时发货
    private Boolean sendFlag;
    //创建场地
    private Integer createSiteId;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Boolean getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(Boolean sendFlag) {
        this.sendFlag = sendFlag;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getCreateSiteId() {
        return createSiteId;
    }

    public void setCreateSiteId(Integer createSiteId) {
        this.createSiteId = createSiteId;
    }
}
