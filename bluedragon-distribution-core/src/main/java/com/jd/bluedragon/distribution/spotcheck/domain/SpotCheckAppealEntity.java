package com.jd.bluedragon.distribution.spotcheck.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 抽检申诉记录对象
 */
public class SpotCheckAppealEntity implements Serializable {

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
     * 抽检人省区名称
     */
    private String startProvinceName;

    /**
     * 抽检人枢纽编码
     */
    private String startHubCode;

    /**
     * 抽检人枢纽名称
     */
    private String startHubName;

    /**
     * 抽检人场地编码
     */
    private String startSiteCode;

    /**
     * 抽检人场地名称
     */
    private String startSiteName;

    /**
     * 抽检人erp
     */
    private String startErp;

    /**
     * 责任类型
     */
    private Integer dutyType;

    /**
     * 申诉人省区编码
     */
    private String dutyProvinceCode;

    /**
     * 申诉人省区名称
     */
    private String dutyProvinceName;

    /**
     * 申诉人枢纽编码
     */
    private String dutyHubCode;

    /**
     * 申诉人枢纽名称
     */
    private String dutyHubName;

    /**
     * 申诉人战区编码
     */
    private String dutyWarCode;

    /**
     * 申诉人战区名称
     */
    private String dutyWarName;

    /**
     * 申诉人片区编码
     */
    private String dutyAreaCode;

    /**
     * 申诉人片区名称
     */
    private String dutyAreaName;

    /**
     * 申诉人站点编码
     */
    private String dutySiteCode;

    /**
     * 申诉人站点名称
     */
    private String dutySiteName;

    /**
     * 申诉人erp
     */
    private String dutyErp;

    /**
     * 抽检重量,单位为kg
     */
    private String confirmWeight;

    /**
     * 抽检体积,单位为cm3
     */
    private String confirmVolume;

    /**
     * 核对重量,单位为kg
     */
    private String reConfirmWeight;

    /**
     * 核对体积,单位为cm3
     */
    private String reConfirmVolume;

    /**
     * 重量差异
     */
    private String diffWeight;

    /**
     * 误差标准值
     */
    private String standerDiff;

    /**
     * 抽检前设备重量校准：0-不合格，1-合格
     */
    private Integer beforeWeightStatus;

    /**
     * 抽检前设备体积校准：0-不合格，1-合格
     */
    private Integer beforeVolumeStatus;

    /**
     * 抽检后设备重量校准：0-不合格，1-合格
     */
    private Integer afterWeightStatus;

    /**
     * 抽检后设备体积校准：0-不合格，1-合格
     */
    private Integer afterVolumeStatus;

    /**
     * 申诉时设备重量校准：0-不合格，1-合格
     */
    private Integer appealWeightStatus;

    /**
     * 申诉时设备体积校准：0-不合格，1-合格
     */
    private Integer appealVolumeStatus;

    /**
     * 确认人erp
     */
    private String updateUserErp;

    /**
     * 驳回理由
     */
    private String rejectReason;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 确认时间
     */
    private Date updateTime;

    /**
     * 确认状态：0-未确认，1-同意，2-驳回
     */
    private Integer confirmStatus;

    /**
     * 是否自动确认：0-否，1-是
     */
    private Integer autoStatus;

    /**
     * 是否有效：0-否，1-是
     */
    private Integer yn;

    /**
     * 数据库时间戳
     */
    private Date ts;

    private Date spotStartTime;

    private Date spotEndTime;

    private List<Long> idList;

    private Integer offset;

    private Integer pageSize;

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

    public String getStartProvinceName() {
        return startProvinceName;
    }

    public void setStartProvinceName(String startProvinceName) {
        this.startProvinceName = startProvinceName;
    }

    public String getStartHubCode() {
        return startHubCode;
    }

    public void setStartHubCode(String startHubCode) {
        this.startHubCode = startHubCode;
    }

    public String getStartHubName() {
        return startHubName;
    }

    public void setStartHubName(String startHubName) {
        this.startHubName = startHubName;
    }

    public String getStartSiteCode() {
        return startSiteCode;
    }

    public void setStartSiteCode(String startSiteCode) {
        this.startSiteCode = startSiteCode;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public String getStartErp() {
        return startErp;
    }

    public void setStartErp(String startErp) {
        this.startErp = startErp;
    }

    public Integer getDutyType() {
        return dutyType;
    }

    public void setDutyType(Integer dutyType) {
        this.dutyType = dutyType;
    }

    public String getDutyProvinceCode() {
        return dutyProvinceCode;
    }

    public void setDutyProvinceCode(String dutyProvinceCode) {
        this.dutyProvinceCode = dutyProvinceCode;
    }

    public String getDutyProvinceName() {
        return dutyProvinceName;
    }

    public void setDutyProvinceName(String dutyProvinceName) {
        this.dutyProvinceName = dutyProvinceName;
    }

    public String getDutyHubCode() {
        return dutyHubCode;
    }

    public void setDutyHubCode(String dutyHubCode) {
        this.dutyHubCode = dutyHubCode;
    }

    public String getDutyHubName() {
        return dutyHubName;
    }

    public void setDutyHubName(String dutyHubName) {
        this.dutyHubName = dutyHubName;
    }

    public String getDutyWarCode() {
        return dutyWarCode;
    }

    public void setDutyWarCode(String dutyWarCode) {
        this.dutyWarCode = dutyWarCode;
    }

    public String getDutyWarName() {
        return dutyWarName;
    }

    public void setDutyWarName(String dutyWarName) {
        this.dutyWarName = dutyWarName;
    }

    public String getDutyAreaCode() {
        return dutyAreaCode;
    }

    public void setDutyAreaCode(String dutyAreaCode) {
        this.dutyAreaCode = dutyAreaCode;
    }

    public String getDutyAreaName() {
        return dutyAreaName;
    }

    public void setDutyAreaName(String dutyAreaName) {
        this.dutyAreaName = dutyAreaName;
    }

    public String getDutySiteCode() {
        return dutySiteCode;
    }

    public void setDutySiteCode(String dutySiteCode) {
        this.dutySiteCode = dutySiteCode;
    }

    public String getDutySiteName() {
        return dutySiteName;
    }

    public void setDutySiteName(String dutySiteName) {
        this.dutySiteName = dutySiteName;
    }

    public String getDutyErp() {
        return dutyErp;
    }

    public void setDutyErp(String dutyErp) {
        this.dutyErp = dutyErp;
    }

    public String getConfirmWeight() {
        return confirmWeight;
    }

    public void setConfirmWeight(String confirmWeight) {
        this.confirmWeight = confirmWeight;
    }

    public String getConfirmVolume() {
        return confirmVolume;
    }

    public void setConfirmVolume(String confirmVolume) {
        this.confirmVolume = confirmVolume;
    }

    public String getReConfirmWeight() {
        return reConfirmWeight;
    }

    public void setReConfirmWeight(String reConfirmWeight) {
        this.reConfirmWeight = reConfirmWeight;
    }

    public String getReConfirmVolume() {
        return reConfirmVolume;
    }

    public void setReConfirmVolume(String reConfirmVolume) {
        this.reConfirmVolume = reConfirmVolume;
    }

    public String getDiffWeight() {
        return diffWeight;
    }

    public void setDiffWeight(String diffWeight) {
        this.diffWeight = diffWeight;
    }

    public String getStanderDiff() {
        return standerDiff;
    }

    public void setStanderDiff(String standerDiff) {
        this.standerDiff = standerDiff;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public Integer getAutoStatus() {
        return autoStatus;
    }

    public void setAutoStatus(Integer autoStatus) {
        this.autoStatus = autoStatus;
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

    public Integer getBeforeWeightStatus() {
        return beforeWeightStatus;
    }

    public void setBeforeWeightStatus(Integer beforeWeightStatus) {
        this.beforeWeightStatus = beforeWeightStatus;
    }

    public Integer getBeforeVolumeStatus() {
        return beforeVolumeStatus;
    }

    public void setBeforeVolumeStatus(Integer beforeVolumeStatus) {
        this.beforeVolumeStatus = beforeVolumeStatus;
    }

    public Integer getAfterWeightStatus() {
        return afterWeightStatus;
    }

    public void setAfterWeightStatus(Integer afterWeightStatus) {
        this.afterWeightStatus = afterWeightStatus;
    }

    public Integer getAfterVolumeStatus() {
        return afterVolumeStatus;
    }

    public void setAfterVolumeStatus(Integer afterVolumeStatus) {
        this.afterVolumeStatus = afterVolumeStatus;
    }

    public Integer getAppealWeightStatus() {
        return appealWeightStatus;
    }

    public void setAppealWeightStatus(Integer appealWeightStatus) {
        this.appealWeightStatus = appealWeightStatus;
    }

    public Integer getAppealVolumeStatus() {
        return appealVolumeStatus;
    }

    public void setAppealVolumeStatus(Integer appealVolumeStatus) {
        this.appealVolumeStatus = appealVolumeStatus;
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
