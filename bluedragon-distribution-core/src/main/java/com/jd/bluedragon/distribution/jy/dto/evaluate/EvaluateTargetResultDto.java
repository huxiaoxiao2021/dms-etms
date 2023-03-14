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

    /**
     * 评价目标区域ID
     */
    private Integer targetAreaCode;

    /**
     * 评价目标区域名称
     */
    private String targetAreaName;

    /**
     * 评价目标站点ID
     */
    private Integer targetSiteCode;

    /**
     * 评价目标站点名称
     */
    private String targetSiteName;

    /**
     * 评价目标任务ID
     */
    private String targetTaskId;

    /**
     * 评价目标bizId
     */
    private String targetBizId;

    /**
     * 评价目标任务开始时间
     */
    private Date targetStartTime;

    /**
     * 评价目标任务结束时间
     */
    private Date targetFinishTime;

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
     * 协助人erp集合，多个逗号分隔
     */
    private String helperErp;

    /**
     * 评价来源区域ID
     */
    private Integer sourceAreaCode;

    /**
     * 评价来源区域名称
     */
    private String sourceAreaName;

    /**
     * 评价来源站点ID
     */
    private Integer sourceSiteCode;

    /**
     * 评价来源站点名称
     */
    private String sourceSiteName;

    /**
     * 评价来源任务ID
     */
    private String sourceTaskId;

    /**
     * 评价来源bizId
     */
    private String sourceBizId;

    /**
     * 解封车时间
     */
    private Date unsealTime;

    /**
     * 评价类型：1-装车评价
     */
    private Integer evaluateType;

    /**
     * 评价状态：0-不满意，1-满意
     */
    private Integer status;

    /**
     * 评价维度编码集合，多个逗号隔开
     */
    private String dimensionCode;

    /**
     * 图片数量
     */
    private Integer imgCount;

    /**
     * 评价人erp集合，多个逗号隔开
     */
    private String evaluateUserErp;

    /**
     * 备注集合，多个换行符隔开
     */
    private String remark;

    /**
     * 本次评价人erp
     */
    private String operateUserErp;

    /**
     * 本次评价人姓名
     */
    private String operateUserName;

    /**
     * 本次评价操作时间
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