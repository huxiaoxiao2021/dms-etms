package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;

/**
 * 待提件数量计算
 * @Author zhengchengfa
 * @Date 2024/1/3 21:41
 * @Description
 */
public class CalculateWaitPickingItemNumDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bizId;
    private Long pickingSiteId;
    private Long nextSiteId;
    private String batchCode;
    private Integer waitPickingItemNum;
    /**
     * false 计算bizId维度agg
     * true 计算 bizId + nextSiteId 维度agg
     */
    private Boolean calculateNextSiteAggFlag;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getPickingSiteId() {
        return pickingSiteId;
    }

    public void setPickingSiteId(Long pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
    }

    public Long getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Long nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Integer getWaitPickingItemNum() {
        return waitPickingItemNum;
    }

    public void setWaitPickingItemNum(Integer waitPickingItemNum) {
        this.waitPickingItemNum = waitPickingItemNum;
    }

    public Boolean getCalculateNextSiteAggFlag() {
        return calculateNextSiteAggFlag;
    }

    public void setCalculateNextSiteAggFlag(Boolean calculateNextSiteAggFlag) {
        this.calculateNextSiteAggFlag = calculateNextSiteAggFlag;
    }
}
