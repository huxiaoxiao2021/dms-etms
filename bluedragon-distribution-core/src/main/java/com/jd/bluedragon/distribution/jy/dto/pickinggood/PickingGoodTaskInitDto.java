package com.jd.bluedragon.distribution.jy.dto.pickinggood;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/12/7 22:10
 * @Description
 */
public class PickingGoodTaskInitDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String bizId;

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
     * 计划起飞时间/计划出发时间
     */
    private Date nodePlanStartTime;
    /**
     * 预计降落时间/预计到达时间
     */
    private Date nodePlanArriveTime;
    /**
     * 实际起飞时间/实际出发时间
     */
    private Date nodeRealStartTime;
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
     * 提货任务类型（1-航空提货，2-铁路提货）
     */
    private Integer taskType;

    private String createUserErp;

    private String createUserName;

    private String updateUserErp;

    private String updateUserName;

    private Long operateTime;


    List<PickingGoodTaskExtendInitDto> extendInitDtoList;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
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

    public Date getNodePlanArriveTime() {
        return nodePlanArriveTime;
    }

    public void setNodePlanArriveTime(Date nodePlanArriveTime) {
        this.nodePlanArriveTime = nodePlanArriveTime;
    }

    public Date getNodeRealStartTime() {
        return nodeRealStartTime;
    }

    public void setNodeRealStartTime(Date nodeRealStartTime) {
        this.nodeRealStartTime = nodeRealStartTime;
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

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
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

    public List<PickingGoodTaskExtendInitDto> getExtendInitDtoList() {
        return extendInitDtoList;
    }

    public void setExtendInitDtoList(List<PickingGoodTaskExtendInitDto> extendInitDtoList) {
        this.extendInitDtoList = extendInitDtoList;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }
}
