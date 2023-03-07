package com.jd.bluedragon.common.dto.operation.workbench.unseal.response;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName SealTaskInfo
 * @Description 封车任务明细
 * @Author wyh
 * @Date 2022/3/5 10:37
 **/
public class SealTaskInfo implements Serializable {

    private static final long serialVersionUID = -8821284160059283069L;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    private String sealCarCode;

    /**
     * 车型
     */
    private String carModel;

    /**
     * 车辆状态
     */
    private Integer vehicleStatus;

    /**
     * 车辆状态描述
     */
    private String vehicleStatusName;

    /**
     * 始发站点ID
     */
    private Integer startSiteId;

    /**
     * 始发站点名称
     */
    private String startSiteName;

    /**
     * 线路类型
     */
    private Integer lineType;

    /**
     * 线路类型描述
     */
    private String lineTypeName;

    /**
     * 运力编码
     */
    private String transportCode;

    /**
     * 委托书号
     */
    private String transBookCode;

    /**
     * 批次列表
     */
    private List<String> batchCodeList;

    /**
     * 总件数
     */
    private Integer totalCount;

    /**
     * 总件数-本地
     */
    private Integer localCount;

    /**
     * 总件数-外埠
     */
    private Integer externalCount;

    /**
     * 已卸件数
     */
    private Integer unloadCount;

    /**
     * 封车时间
     */
    private Date sealCarTime;

    /**
     * 预计到达时间
     */
    private Date predictionArriveTime;

    /**
     * 实际到达时间
     */
    private Date actualArriveTime;

    /**
     * 任务排序
     */
    private Integer orderIndex;

    /**
     * 错误解封车顺序提示语
     */
    private String wrongOrderMessage;

    /**
     * 任务号
     */
    private String billCode;

    /**
     * 封签号集合
     */
    private List<String> sealCodes;

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getVehicleStatusName() {
        return vehicleStatusName;
    }

    public void setVehicleStatusName(String vehicleStatusName) {
        this.vehicleStatusName = vehicleStatusName;
    }

    public Integer getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Integer startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Integer getLineType() {
        return lineType;
    }

    public void setLineType(Integer lineType) {
        this.lineType = lineType;
    }

    public String getLineTypeName() {
        return lineTypeName;
    }

    public void setLineTypeName(String lineTypeName) {
        this.lineTypeName = lineTypeName;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public String getTransBookCode() {
        return transBookCode;
    }

    public void setTransBookCode(String transBookCode) {
        this.transBookCode = transBookCode;
    }

    public List<String> getBatchCodeList() {
        return batchCodeList;
    }

    public void setBatchCodeList(List<String> batchCodeList) {
        this.batchCodeList = batchCodeList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getLocalCount() {
        return localCount;
    }

    public void setLocalCount(Integer localCount) {
        this.localCount = localCount;
    }

    public Integer getExternalCount() {
        return externalCount;
    }

    public void setExternalCount(Integer externalCount) {
        this.externalCount = externalCount;
    }

    public Integer getUnloadCount() {
        return unloadCount;
    }

    public void setUnloadCount(Integer unloadCount) {
        this.unloadCount = unloadCount;
    }

    public Date getSealCarTime() {
        return sealCarTime;
    }

    public void setSealCarTime(Date sealCarTime) {
        this.sealCarTime = sealCarTime;
    }

    public Date getPredictionArriveTime() {
        return predictionArriveTime;
    }

    public void setPredictionArriveTime(Date predictionArriveTime) {
        this.predictionArriveTime = predictionArriveTime;
    }

    public Date getActualArriveTime() {
        return actualArriveTime;
    }

    public void setActualArriveTime(Date actualArriveTime) {
        this.actualArriveTime = actualArriveTime;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getWrongOrderMessage() {
        return wrongOrderMessage;
    }

    public void setWrongOrderMessage(String wrongOrderMessage) {
        this.wrongOrderMessage = wrongOrderMessage;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public List<String> getSealCodes() {
        return sealCodes;
    }

    public void setSealCodes(List<String> sealCodes) {
        this.sealCodes = sealCodes;
    }
}
