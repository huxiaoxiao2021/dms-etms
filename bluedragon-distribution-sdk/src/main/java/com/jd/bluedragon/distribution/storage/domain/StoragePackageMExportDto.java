package com.jd.bluedragon.distribution.storage.domain;

import java.io.Serializable;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/12 15:37
 */
public class StoragePackageMExportDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 履约单号 */
    private String performanceCode;

    /** 储位号 */
    private String storageCode;

    /** 运单号 */
    private String waybillCode;

    /** 系统包裹数 */
    private Long packageSum;

    /** 上架包裹数 */
    private Long putawayPackageSum;

    /** 状态（1-已上架，2-集齐可发货，3-强制可发货，4-已发货） */
    private String status;

    /** 强制发货原因 */
    private String forceSendReason;

    /** 计划配送时间 */
    private String planDeliveryTime;

    /** 所属分拣中心 */
    private Long createSiteCode;

    /** 所属分拣中心 */
    private String createSiteName;

    /** 发货时间 */
    private String sendTime;

    /** 上架时间 */
    private String putawayTime;

    /** 创建人 */
    private String createUser;

    /** 更新人 */
    private String updateUser;

    /** 来源 1、金鹏暂存 2、快运暂存 */
    private String source;

    /** 全部上架完成时间 */
    private String putAwayCompleteTime;

    /** 全部下架完成时间 */
    private String  downAwayCompleteTime;

    /** 下架时间 */
    private String downAwayTime;

    public String getPerformanceCode() {
        return performanceCode;
    }

    public void setPerformanceCode(String performanceCode) {
        this.performanceCode = performanceCode;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Long getPackageSum() {
        return packageSum;
    }

    public void setPackageSum(Long packageSum) {
        this.packageSum = packageSum;
    }

    public Long getPutawayPackageSum() {
        return putawayPackageSum;
    }

    public void setPutawayPackageSum(Long putawayPackageSum) {
        this.putawayPackageSum = putawayPackageSum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getForceSendReason() {
        return forceSendReason;
    }

    public void setForceSendReason(String forceSendReason) {
        this.forceSendReason = forceSendReason;
    }

    public String getPlanDeliveryTime() {
        return planDeliveryTime;
    }

    public void setPlanDeliveryTime(String planDeliveryTime) {
        this.planDeliveryTime = planDeliveryTime;
    }

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getPutawayTime() {
        return putawayTime;
    }

    public void setPutawayTime(String putawayTime) {
        this.putawayTime = putawayTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPutAwayCompleteTime() {
        return putAwayCompleteTime;
    }

    public void setPutAwayCompleteTime(String putAwayCompleteTime) {
        this.putAwayCompleteTime = putAwayCompleteTime;
    }

    public String getDownAwayCompleteTime() {
        return downAwayCompleteTime;
    }

    public void setDownAwayCompleteTime(String downAwayCompleteTime) {
        this.downAwayCompleteTime = downAwayCompleteTime;
    }

    public String getDownAwayTime() {
        return downAwayTime;
    }

    public void setDownAwayTime(String downAwayTime) {
        this.downAwayTime = downAwayTime;
    }
}
    
