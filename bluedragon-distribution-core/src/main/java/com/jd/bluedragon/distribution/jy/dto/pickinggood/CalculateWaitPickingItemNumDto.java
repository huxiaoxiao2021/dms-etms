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

    public static final String CODE_TYPE_BATCH_CODE = "batch_code";
    public static final String CODE_TYPE_PACKAGE_CODE = "package_code";
    public static final String CODE_TYPE_BOX_CODE = "box_code";

    private String bizId;
    private Long pickingSiteId;
    private Long nextSiteId;
    private String code;
    /**
     * code分两个类型，一种按批次处理【提货任务统计】，一种按包裹处理【发货统计】（发货统计涉及查流向，按批次处理是存在性能问题）
     */
    private String codeType;

    private Integer waitPickingItemNum;
    /**
     * false 计算bizId维度agg
     * true 计算 bizId + nextSiteId 维度agg
     */
    private Boolean calculateNextSiteAggFlag;

    private Long operateTime;
    private Long sysTime;


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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
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

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Long getSysTime() {
        return sysTime;
    }

    public void setSysTime(Long sysTime) {
        this.sysTime = sysTime;
    }
}
