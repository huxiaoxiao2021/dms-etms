package com.jd.bluedragon.distribution.jy.dto.pickinggood;

/**
 * @Author zhengchengfa
 * @Date 2023/12/7 22:10
 * @Description
 */
public class PickingGoodTaskDetailInitDto {
    private static final long serialVersionUID = 1L;
    private String businessId;
    /**
     * 空铁班次流水号
     */
    private String businessNumber;
    /**
     * 班次号：航班号/车次号
     */
    private String serviceNumber;

    private String bizId;
    /**
     * 提货场地id
     */
    private Long pickingSiteId;
    /**
     * 提货机场编码/提货车站编码
     */
    private String pickingNodeCode;

    private String sealCarCode;

    private String batchCode;

    private Long startSiteId;
    //startSiteId 场地类型
    private Integer startSiteType;

    private Integer taskType;
    /**
     * 待提包裹号
     */
    private String packageCode;
    /**
     * 待提包裹所属箱号
     */
    private String boxCode;
    /**
     * true: 按箱扫描类型
     */
    private Boolean scanIsBoxType;

    private Long operateTime;

    private Long sysTime;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
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

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getPickingSiteId() {
        return pickingSiteId;
    }

    public void setPickingSiteId(Long pickingSiteId) {
        this.pickingSiteId = pickingSiteId;
    }

    public String getPickingNodeCode() {
        return pickingNodeCode;
    }

    public void setPickingNodeCode(String pickingNodeCode) {
        this.pickingNodeCode = pickingNodeCode;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public Integer getStartSiteType() {
        return startSiteType;
    }

    public void setStartSiteType(Integer startSiteType) {
        this.startSiteType = startSiteType;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Boolean getScanIsBoxType() {
        return scanIsBoxType;
    }

    public void setScanIsBoxType(Boolean scanIsBoxType) {
        this.scanIsBoxType = scanIsBoxType;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Long getSysTime() {
        return sysTime;
    }

    public void setSysTime(Long sysTime) {
        this.sysTime = sysTime;
    }
}
