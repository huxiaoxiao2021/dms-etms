package com.jd.bluedragon.distribution.spotcheck.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 抽检申诉记录对象
 */
public class SpotCheckAppealDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 抽检单号
     */
    private String waybillCode;

    /**
     * 抽检时间
     */
    private Date startTime;

    /**
     * 抽检设备编码
     */
    private String deviceCode;

    /**
     * 抽检人省区编码
     */
    private String startProvinceCode;

    /**
     * 抽检人枢纽编码
     */
    private String startHubCode;

    /**
     * 抽检人场地编码
     */
    private String startSiteCode;

    /**
     * 抽检人erp
     */
    private String startErp;

    /**
     * 申诉人省区编码
     */
    private String dutyProvinceCode;

    /**
     * 申诉人战区编码
     */
    private String dutyWarCode;

    /**
     * 申诉人片区编码
     */
    private String dutyAreaCode;

    /**
     * 申诉人站点编码
     */
    private String dutySiteCode;

    /**
     * 申诉人erp
     */
    private String dutyErp;

    /**
     * 确认人erp
     */
    private String updateUserErp;

    private Date updateTime;

    /**
     * 驳回理由
     */
    private String rejectReason;

    /**
     * 确认状态：0-未确认，1-同意，2-驳回
     */
    private Integer confirmStatus;

    private Date spotStartTime;

    private Date spotEndTime;

    private List<Long> idList;

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

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getStartProvinceCode() {
        return startProvinceCode;
    }

    public void setStartProvinceCode(String startProvinceCode) {
        this.startProvinceCode = startProvinceCode;
    }

    public String getStartHubCode() {
        return startHubCode;
    }

    public void setStartHubCode(String startHubCode) {
        this.startHubCode = startHubCode;
    }

    public String getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(String startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public String getStartErp() {
        return startErp;
    }

    public void setStartErp(String startErp) {
        this.startErp = startErp;
    }

    public String getDutyProvinceCode() {
        return dutyProvinceCode;
    }

    public void setDutyProvinceCode(String dutyProvinceCode) {
        this.dutyProvinceCode = dutyProvinceCode;
    }

    public String getDutyWarCode() {
        return dutyWarCode;
    }

    public void setDutyWarCode(String dutyWarCode) {
        this.dutyWarCode = dutyWarCode;
    }

    public String getDutyAreaCode() {
        return dutyAreaCode;
    }

    public void setDutyAreaCode(String dutyAreaCode) {
        this.dutyAreaCode = dutyAreaCode;
    }

    public String getDutySiteCode() {
        return dutySiteCode;
    }

    public void setDutySiteCode(String dutySiteCode) {
        this.dutySiteCode = dutySiteCode;
    }

    public String getDutyErp() {
        return dutyErp;
    }

    public void setDutyErp(String dutyErp) {
        this.dutyErp = dutyErp;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public Date getSpotStartTime() {
        return spotStartTime;
    }

    public void setSpotStartTime(Date spotStartTime) {
        this.spotStartTime = spotStartTime;
    }

    public Date getSpotEndTime() {
        return spotEndTime;
    }

    public void setSpotEndTime(Date spotEndTime) {
        this.spotEndTime = spotEndTime;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}

