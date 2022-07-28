package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class UnloadMasterTaskDto implements Serializable {

    private static final long serialVersionUID = 2419641078080000602L;
    /**
     *
     */
    private String bizId;
    /**
     *
     */
    private String sealCarCode;
    /**
     *
     */
    private String vehicleNumber;
    /**
     *
     */
    private String startSiteName;
    private Long startSiteId;
    /**
     *
     */
    private String endSiteName;
    private Long endSiteId;
    /**
     *
     */
    private String railwayPfNo;
    /**
     *
     */
    private Date desealCarTime;
    /**
     *
     */
    private Date unloadFinishTime;
    /**
     *
     */
    private Date unloadStartTime;
    /**
     *
     */
    private Integer vehicleStatus;
    /**
     *
     */
    private Integer manualCreatedFlag;

    /**
     *
     */
    private Integer unloadType;

    private String createUserErp;
    private String createUserName;
    private String updateUserErp;
    private String updateUserName;
    private Date createTime;
    private Date updateTime;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getRailwayPfNo() {
        return railwayPfNo;
    }

    public void setRailwayPfNo(String railwayPfNo) {
        this.railwayPfNo = railwayPfNo;
    }

    public Date getDesealCarTime() {
        return desealCarTime;
    }

    public void setDesealCarTime(Date desealCarTime) {
        this.desealCarTime = desealCarTime;
    }

    public Date getUnloadFinishTime() {
        return unloadFinishTime;
    }

    public void setUnloadFinishTime(Date unloadFinishTime) {
        this.unloadFinishTime = unloadFinishTime;
    }

    public Date getUnloadStartTime() {
        return unloadStartTime;
    }

    public void setUnloadStartTime(Date unloadStartTime) {
        this.unloadStartTime = unloadStartTime;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Integer getManualCreatedFlag() {
        return manualCreatedFlag;
    }

    public void setManualCreatedFlag(Integer manualCreatedFlag) {
        this.manualCreatedFlag = manualCreatedFlag;
    }

    public Integer getUnloadType() {
        return unloadType;
    }

    public void setUnloadType(Integer unloadType) {
        this.unloadType = unloadType;
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
}
