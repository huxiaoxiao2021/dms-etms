package com.jd.bluedragon.distribution.jy.send;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/11 17:16
 * @Description: 发货明细
 */
public class JySendGoodsAggsEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long id;

    private String bizId;

    private String sendVehicleBizId;
    // 不写入数据库
    private String sendCode;
    // 不写入数据库
    private boolean sendCodeRemoveFlag;

    private String transWorkItemCode;

    private String vehicleNumber;

    private Integer vehicleStatus;

    private Integer operateSiteId;

    private Integer receiveSiteId;

    private Integer shouldScanCount;

    private Integer actualScanCount;

    private Integer actualScanPackageCodeCount;

    private Integer actualScanBoxCodeCount;

    private Double actualScanWeight;

    private Double actualScanVolume;

    private Integer interceptScanCount;

    private Integer forceSendCount;

    private Integer totalShouldScanCount;

    private Integer totalScannedCount;

    private Integer totalInterceptCount;

    private Integer totalForceSendCount;

    private Double totalScannedWeight;

    private Double totalScannedVolume;

    private Integer totalScannedPackageCodeCount;

    private Integer totalScannedBoxCodeCount;

    private Double vehicleWeight;

    private Double vehicleVolume;

    private Date createTime;

    private Long bizCreateTime;

    private Integer yn;

    private Long ts;
    private Long sealCarTime;

    private String flowRandKey;
    private String sendCodeRandKey;

    private String beforeSendDay;

    // 按流向keyBy
    public String getFlowKey() {
        return operateSiteId + "|" + receiveSiteId;
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

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public boolean isSendCodeRemoveFlag() {
        return sendCodeRemoveFlag;
    }

    public void setSendCodeRemoveFlag(boolean sendCodeRemoveFlag) {
        this.sendCodeRemoveFlag = sendCodeRemoveFlag;
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

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public Integer getReceiveSiteId() {
        return receiveSiteId;
    }

    public void setReceiveSiteId(Integer receiveSiteId) {
        this.receiveSiteId = receiveSiteId;
    }

    public Integer getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Integer shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public Integer getActualScanCount() {
        return actualScanCount;
    }

    public void setActualScanCount(Integer actualScanCount) {
        this.actualScanCount = actualScanCount;
    }

    public Integer getActualScanPackageCodeCount() {
        return actualScanPackageCodeCount;
    }

    public void setActualScanPackageCodeCount(Integer actualScanPackageCodeCount) {
        this.actualScanPackageCodeCount = actualScanPackageCodeCount;
    }

    public Integer getActualScanBoxCodeCount() {
        return actualScanBoxCodeCount;
    }

    public void setActualScanBoxCodeCount(Integer actualScanBoxCodeCount) {
        this.actualScanBoxCodeCount = actualScanBoxCodeCount;
    }

    public Double getActualScanWeight() {
        return actualScanWeight;
    }

    public void setActualScanWeight(Double actualScanWeight) {
        this.actualScanWeight = actualScanWeight;
    }

    public Double getActualScanVolume() {
        return actualScanVolume;
    }

    public void setActualScanVolume(Double actualScanVolume) {
        this.actualScanVolume = actualScanVolume;
    }

    public Integer getInterceptScanCount() {
        return interceptScanCount;
    }

    public void setInterceptScanCount(Integer interceptScanCount) {
        this.interceptScanCount = interceptScanCount;
    }

    public Integer getForceSendCount() {
        return forceSendCount;
    }

    public void setForceSendCount(Integer forceSendCount) {
        this.forceSendCount = forceSendCount;
    }

    public Integer getTotalShouldScanCount() {
        return totalShouldScanCount;
    }

    public void setTotalShouldScanCount(Integer totalShouldScanCount) {
        this.totalShouldScanCount = totalShouldScanCount;
    }

    public Integer getTotalScannedCount() {
        return totalScannedCount;
    }

    public void setTotalScannedCount(Integer totalScannedCount) {
        this.totalScannedCount = totalScannedCount;
    }

    public Integer getTotalInterceptCount() {
        return totalInterceptCount;
    }

    public void setTotalInterceptCount(Integer totalInterceptCount) {
        this.totalInterceptCount = totalInterceptCount;
    }

    public Integer getTotalForceSendCount() {
        return totalForceSendCount;
    }

    public void setTotalForceSendCount(Integer totalForceSendCount) {
        this.totalForceSendCount = totalForceSendCount;
    }

    public Double getTotalScannedWeight() {
        return totalScannedWeight;
    }

    public void setTotalScannedWeight(Double totalScannedWeight) {
        this.totalScannedWeight = totalScannedWeight;
    }

    public Double getTotalScannedVolume() {
        return totalScannedVolume;
    }

    public void setTotalScannedVolume(Double totalScannedVolume) {
        this.totalScannedVolume = totalScannedVolume;
    }

    public Integer getTotalScannedPackageCodeCount() {
        return totalScannedPackageCodeCount;
    }

    public void setTotalScannedPackageCodeCount(Integer totalScannedPackageCodeCount) {
        this.totalScannedPackageCodeCount = totalScannedPackageCodeCount;
    }

    public Integer getTotalScannedBoxCodeCount() {
        return totalScannedBoxCodeCount;
    }

    public void setTotalScannedBoxCodeCount(Integer totalScannedBoxCodeCount) {
        this.totalScannedBoxCodeCount = totalScannedBoxCodeCount;
    }

    public Double getVehicleWeight() {
        return vehicleWeight;
    }

    public void setVehicleWeight(Double vehicleWeight) {
        this.vehicleWeight = vehicleWeight;
    }

    public Double getVehicleVolume() {
        return vehicleVolume;
    }

    public void setVehicleVolume(Double vehicleVolume) {
        this.vehicleVolume = vehicleVolume;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getBizCreateTime() {
        return bizCreateTime;
    }

    public void setBizCreateTime(Long bizCreateTime) {
        this.bizCreateTime = bizCreateTime;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Long getSealCarTime() {
        return sealCarTime;
    }

    public void setSealCarTime(Long sealCarTime) {
        this.sealCarTime = sealCarTime;
    }

    public String getFlowRandKey() {
        return flowRandKey;
    }

    public void setFlowRandKey(String flowRandKey) {
        this.flowRandKey = flowRandKey;
    }

    public String getSendCodeRandKey() {
        return sendCodeRandKey;
    }

    public void setSendCodeRandKey(String sendCodeRandKey) {
        this.sendCodeRandKey = sendCodeRandKey;
    }

    public String getBeforeSendDay() {
        return beforeSendDay;
    }

    public void setBeforeSendDay(String beforeSendDay) {
        this.beforeSendDay = beforeSendDay;
    }
}
