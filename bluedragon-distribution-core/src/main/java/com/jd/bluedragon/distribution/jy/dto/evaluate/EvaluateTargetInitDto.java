package com.jd.bluedragon.distribution.jy.dto.evaluate;

import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateDimensionReq;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 装车评价报表加工初始化消息实体
 */
public class EvaluateTargetInitDto implements Serializable {

    private static final long serialVersionUID = 8821734157832978341L;

    /**
     * 评价目标bizId
     */
    private String targetBizId;

    /**
     * 评价目标站点ID
     */
    private Integer targetSiteCode;

    /**
     * 评价目标站点名称
     */
    private String targetSiteName;

    /**
     * 评价来源bizId
     */
    private String sourceBizId;

    /**
     * 评价来源站点ID
     */
    private Integer sourceSiteCode;

    /**
     * 评价来源站点名称
     */
    private String sourceSiteName;

    /**
     * 派车单号
     */
    private String transWorkItemCode;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 封车时间
     */
    private Date sealTime;

    /**
     * 解封车时间
     */
    private Date unsealTime;

    /**
     * 操作人erp
     */
    private String operateUserErp;

    /**
     * 操作人姓名
     */
    private String operateUserName;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 是否首次评价
     */
    private boolean firstEvaluate;

    /**
     * 本次评价详情列表（每次都是增量，不是历史汇总）
     */
    private List<EvaluateDimensionReq> dimensionList;

    public String getTargetBizId() {
        return targetBizId;
    }

    public void setTargetBizId(String targetBizId) {
        this.targetBizId = targetBizId;
    }

    public String getSourceBizId() {
        return sourceBizId;
    }

    public void setSourceBizId(String sourceBizId) {
        this.sourceBizId = sourceBizId;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public boolean isFirstEvaluate() {
        return firstEvaluate;
    }

    public void setFirstEvaluate(boolean firstEvaluate) {
        this.firstEvaluate = firstEvaluate;
    }

    public Integer getTargetSiteCode() {
        return targetSiteCode;
    }

    public void setTargetSiteCode(Integer targetSiteCode) {
        this.targetSiteCode = targetSiteCode;
    }

    public String getTargetSiteName() {
        return targetSiteName;
    }

    public void setTargetSiteName(String targetSiteName) {
        this.targetSiteName = targetSiteName;
    }

    public Integer getSourceSiteCode() {
        return sourceSiteCode;
    }

    public void setSourceSiteCode(Integer sourceSiteCode) {
        this.sourceSiteCode = sourceSiteCode;
    }

    public String getSourceSiteName() {
        return sourceSiteName;
    }

    public void setSourceSiteName(String sourceSiteName) {
        this.sourceSiteName = sourceSiteName;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Date getSealTime() {
        return sealTime;
    }

    public void setSealTime(Date sealTime) {
        this.sealTime = sealTime;
    }

    public Date getUnsealTime() {
        return unsealTime;
    }

    public void setUnsealTime(Date unsealTime) {
        this.unsealTime = unsealTime;
    }

    public List<EvaluateDimensionReq> getDimensionList() {
        return dimensionList;
    }

    public void setDimensionList(List<EvaluateDimensionReq> dimensionList) {
        this.dimensionList = dimensionList;
    }
}