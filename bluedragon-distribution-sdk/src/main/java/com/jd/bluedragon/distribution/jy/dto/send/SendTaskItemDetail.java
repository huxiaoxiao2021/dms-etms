package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 发货流向明细
 */
public class SendTaskItemDetail implements Serializable {

    private static final long serialVersionUID = -7065880958568947679L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 业务主键 == 派车明细单号
     */
    private String bizId;

    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    /**
     * 派车明细单号
     */
    private String transWorkItemCode;

    /**
     * 任务状态；0-待发货，1-发货中，2-待封车，3-已封车，4-已作废
     * <see>{@link com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum}</see>
     */
    private Integer vehicleStatus;
    private String vehicleStatusName;

    /**
     * 前一个状态
     */
    private Integer preVehicleStatus;

    /**
     * 始发场地ID
     */
    private Long startSiteId;
    /**
     * 始发场地名称
     */
    private String startSiteName;
    /**
     * 目的场地ID
     */
    private Long endSiteId;
    /**
     * 目的场地名称
     */
    private String endSiteName;
    /**
     * 预计发车时间
     */
    private Date planDepartTime;
    /**
     * 封车时间
     */
    private Date sealCarTime;
    /**
     * 创建人ERP
     */
    private String createUserErp;
    /**
     * 创建人姓名
     */
    private String createUserName;
    /**
     * 更新人ERP
     */
    private String updateUserErp;
    /**
     * 更新人姓名
     */
    private String updateUserName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;

    private Integer excepLabel;

    /**
     * 批次号
     */
    private List<String> batchCodeList;

    /**
     * 待扫包裹数
     */
    private Long toScanPackCount = 0L;

    /**
     * 已扫包裹数
     */
    private Long scannedPackCount = 0L;

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

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
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

    public Integer getPreVehicleStatus() {
        return preVehicleStatus;
    }

    public void setPreVehicleStatus(Integer preVehicleStatus) {
        this.preVehicleStatus = preVehicleStatus;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Date getPlanDepartTime() {
        return planDepartTime;
    }

    public void setPlanDepartTime(Date planDepartTime) {
        this.planDepartTime = planDepartTime;
    }

    public Date getSealCarTime() {
        return sealCarTime;
    }

    public void setSealCarTime(Date sealCarTime) {
        this.sealCarTime = sealCarTime;
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

    public Integer getExcepLabel() {
        return excepLabel;
    }

    public void setExcepLabel(Integer excepLabel) {
        this.excepLabel = excepLabel;
    }

    public List<String> getBatchCodeList() {
        return batchCodeList;
    }

    public void setBatchCodeList(List<String> batchCodeList) {
        this.batchCodeList = batchCodeList;
    }

    public Long getToScanPackCount() {
        return toScanPackCount;
    }

    public void setToScanPackCount(Long toScanPackCount) {
        this.toScanPackCount = toScanPackCount;
    }

    public Long getScannedPackCount() {
        return scannedPackCount;
    }

    public void setScannedPackCount(Long scannedPackCount) {
        this.scannedPackCount = scannedPackCount;
    }
}
