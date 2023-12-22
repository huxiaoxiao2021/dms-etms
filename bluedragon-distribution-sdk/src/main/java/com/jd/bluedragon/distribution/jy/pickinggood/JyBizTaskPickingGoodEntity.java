package com.jd.bluedragon.distribution.jy.pickinggood;

import java.io.Serializable;
import java.util.Date;

/**
 * 空提提货任务表
 */
public class JyBizTaskPickingGoodEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    //PinkingGoodTask -- PGT
    public static final String BIZ_PREFIX = "PGT";
    public static final String BIZ_PREFIX_NOTASK = "NPGT";

    public static final Integer INTERCEPT_FLAG = 1;

    private Long id;

    private String bizId;
    /**
     * 始发场地id
     */
    private Long startSiteId;
    /**
     * 流向场地ID（待提货场地）
     */
    private Long nextSiteId;
    /**
     * 空铁班次流水号
     */
    private String businessNumber;
    /**
     * 班次号：航班号/车次号
     */
    private String serviceNumber;
    /**
     * 始发机场/车站编码
     */
    private String beginNodeCode;
    /**
     * 始发机场/车站名称
     */
    private String beginNodeName;
    /**
     * 目的机场/车站编码
     */
    private String endNodeCode;
    /**
     * 目的机场/车站名称
     */
    private String endNodeName;
    /**
     * 计划起飞时间/出发时间
     */
    private Date nodePlanStartTime;
    /**
     * 实际起飞时间/出发时间
     */
    private Date nodeRealStartTime;
    /**
     * 预计降落时间/预计到达时间
     */
    private Date nodePlanArriveTime;
    /**
     * 实际降落时间/实际到达时间
     */
    private Date nodeRealArriveTime;
    /**
     * 操作类型
     */
    private Integer operateNodeType;
    /**
     * 上游登记货物数量
     */
    private Integer cargoNumber;
    /**
     * 上游登记货物重量
     */
    private Double cargoWeight;
    /**
     * 提货状态（0-待提，1-提货中，2-提货完成）
     * com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.PickingGoodStatusEnum
     */
    private Integer status;
    /**
     * 完成节点[1-完成按钮点击，2-待提0系统默认完成，3-异常完成]，4 自建任务定时完成
     */
    private Integer completeNode;
    /**
     * 提货开始时间
     */
    private Date pickingStartTime;
    /**
     * 提货完成时间
     */
    private Date pickingCompleteTime;
    /**
     * 拦截标识（0：不拦截【默认】1：拦截）
     */
    private Integer intercept;
    /**
     * 拦截时间
     */
    private Date interceptTime;
    /**
     * 提货任务类型（1-航空提货，2-铁路提货）
     */
    private Integer taskType;
    /**
     * 是否自建任务；（0-否【默认】 1-是）
     */
    private Integer manualCreatedFlag;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private Integer yn;

    private Date ts;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public Long getNextSiteId() {
        return nextSiteId;
    }

    public void setNextSiteId(Long nextSiteId) {
        this.nextSiteId = nextSiteId;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public String getBeginNodeCode() {
        return beginNodeCode;
    }

    public void setBeginNodeCode(String beginNodeCode) {
        this.beginNodeCode = beginNodeCode;
    }

    public String getBeginNodeName() {
        return beginNodeName;
    }

    public void setBeginNodeName(String beginNodeName) {
        this.beginNodeName = beginNodeName;
    }

    public String getEndNodeCode() {
        return endNodeCode;
    }

    public void setEndNodeCode(String endNodeCode) {
        this.endNodeCode = endNodeCode;
    }

    public String getEndNodeName() {
        return endNodeName;
    }

    public void setEndNodeName(String endNodeName) {
        this.endNodeName = endNodeName;
    }

    public Date getNodePlanStartTime() {
        return nodePlanStartTime;
    }

    public void setNodePlanStartTime(Date nodePlanStartTime) {
        this.nodePlanStartTime = nodePlanStartTime;
    }

    public Date getNodeRealStartTime() {
        return nodeRealStartTime;
    }

    public void setNodeRealStartTime(Date nodeRealStartTime) {
        this.nodeRealStartTime = nodeRealStartTime;
    }

    public Date getNodePlanArriveTime() {
        return nodePlanArriveTime;
    }

    public void setNodePlanArriveTime(Date nodePlanArriveTime) {
        this.nodePlanArriveTime = nodePlanArriveTime;
    }

    public Date getNodeRealArriveTime() {
        return nodeRealArriveTime;
    }

    public void setNodeRealArriveTime(Date nodeRealArriveTime) {
        this.nodeRealArriveTime = nodeRealArriveTime;
    }

    public Integer getOperateNodeType() {
        return operateNodeType;
    }

    public void setOperateNodeType(Integer operateNodeType) {
        this.operateNodeType = operateNodeType;
    }

    public Integer getCargoNumber() {
        return cargoNumber;
    }

    public void setCargoNumber(Integer cargoNumber) {
        this.cargoNumber = cargoNumber;
    }

    public Double getCargoWeight() {
        return cargoWeight;
    }

    public void setCargoWeight(Double cargoWeight) {
        this.cargoWeight = cargoWeight;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getCompleteNode() {
        return completeNode;
    }

    public void setCompleteNode(Integer completeNode) {
        this.completeNode = completeNode;
    }

    public Date getPickingStartTime() {
        return pickingStartTime;
    }

    public void setPickingStartTime(Date pickingStartTime) {
        this.pickingStartTime = pickingStartTime;
    }

    public Date getPickingCompleteTime() {
        return pickingCompleteTime;
    }

    public void setPickingCompleteTime(Date pickingCompleteTime) {
        this.pickingCompleteTime = pickingCompleteTime;
    }

    public Integer getIntercept() {
        return intercept;
    }

    public void setIntercept(Integer intercept) {
        this.intercept = intercept;
    }

    public Date getInterceptTime() {
        return interceptTime;
    }

    public void setInterceptTime(Date interceptTime) {
        this.interceptTime = interceptTime;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public Integer getManualCreatedFlag() {
        return manualCreatedFlag;
    }

    public void setManualCreatedFlag(Integer manualCreatedFlag) {
        this.manualCreatedFlag = manualCreatedFlag;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }
}