package com.jd.bluedragon.distribution.jy.dto.evaluate;

import com.jd.bluedragon.common.dto.operation.workbench.evaluate.request.EvaluateDimensionReq;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 装车评价报表最终结果消息实体
 */
public class EvaluateTargetResultDto implements Serializable {

    private static final long serialVersionUID = 8821734157832978341L;

    private Integer targetAreaCode;

    private String targetAreaName;

    private Integer targetSiteCode;

    private String targetSiteName;

    private String targetTaskId;

    private String targetBizId;

    private Date targetStartTime;

    private Date targetFinishTime;

    private String transWorkItemCode;

    private String vehicleNumber;

    private Date sealTime;

    private String helperErp;

    private Integer sourceAreaCode;

    private String sourceAreaName;

    private Integer sourceSiteCode;

    private String sourceSiteName;

    private String sourceTaskId;

    private String sourceBizId;

    private Date unsealTime;

    private Integer evaluateType;

    private Integer status;

    private String dimensionCode;

    private Integer imgCount;

    private String evaluateUserErp;

    private String remark;

    private String operateUserErp;

    private String operateUserName;

    private Long operateTime;

    private boolean firstEvaluate;

    /**
     * 评价维度详情列表
     */
    private List<EvaluateDimensionReq> dimensionList;

    public Integer getTargetAreaCode() {
        return targetAreaCode;
    }

    public void setTargetAreaCode(Integer targetAreaCode) {
        this.targetAreaCode = targetAreaCode;
    }

    public String getTargetAreaName() {
        return targetAreaName;
    }

    public void setTargetAreaName(String targetAreaName) {
        this.targetAreaName = targetAreaName;
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

    public String getTargetTaskId() {
        return targetTaskId;
    }

    public void setTargetTaskId(String targetTaskId) {
        this.targetTaskId = targetTaskId;
    }

    public String getTargetBizId() {
        return targetBizId;
    }

    public void setTargetBizId(String targetBizId) {
        this.targetBizId = targetBizId;
    }

    public Date getTargetStartTime() {
        return targetStartTime;
    }

    public void setTargetStartTime(Date targetStartTime) {
        this.targetStartTime = targetStartTime;
    }

    public Date getTargetFinishTime() {
        return targetFinishTime;
    }

    public void setTargetFinishTime(Date targetFinishTime) {
        this.targetFinishTime = targetFinishTime;
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

    public String getHelperErp() {
        return helperErp;
    }

    public void setHelperErp(String helperErp) {
        this.helperErp = helperErp;
    }

    public Integer getSourceAreaCode() {
        return sourceAreaCode;
    }

    public void setSourceAreaCode(Integer sourceAreaCode) {
        this.sourceAreaCode = sourceAreaCode;
    }

    public String getSourceAreaName() {
        return sourceAreaName;
    }

    public void setSourceAreaName(String sourceAreaName) {
        this.sourceAreaName = sourceAreaName;
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

    public String getSourceTaskId() {
        return sourceTaskId;
    }

    public void setSourceTaskId(String sourceTaskId) {
        this.sourceTaskId = sourceTaskId;
    }

    public String getSourceBizId() {
        return sourceBizId;
    }

    public void setSourceBizId(String sourceBizId) {
        this.sourceBizId = sourceBizId;
    }

    public Date getUnsealTime() {
        return unsealTime;
    }

    public void setUnsealTime(Date unsealTime) {
        this.unsealTime = unsealTime;
    }

    public Integer getEvaluateType() {
        return evaluateType;
    }

    public void setEvaluateType(Integer evaluateType) {
        this.evaluateType = evaluateType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getDimensionCode() {
        return dimensionCode;
    }

    public void setDimensionCode(String dimensionCode) {
        this.dimensionCode = dimensionCode;
    }

    public Integer getImgCount() {
        return imgCount;
    }

    public void setImgCount(Integer imgCount) {
        this.imgCount = imgCount;
    }

    public String getEvaluateUserErp() {
        return evaluateUserErp;
    }

    public void setEvaluateUserErp(String evaluateUserErp) {
        this.evaluateUserErp = evaluateUserErp;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public List<EvaluateDimensionReq> getDimensionList() {
        return dimensionList;
    }

    public void setDimensionList(List<EvaluateDimensionReq> dimensionList) {
        this.dimensionList = dimensionList;
    }
}