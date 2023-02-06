package com.jd.bluedragon.distribution.jy.dto.calibrate;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * dws设备校准mq
 *
 * @author hujiping
 * @date 2022/12/8 10:48 AM
 */
public class DwsMachineCalibrateMQ implements Serializable {

    private static final long serialVersionUID = 1L;

    // 消息唯一码
    private String businessId;
    // 设备编码
    private String machineCode;
    // 砝码编码
    private String farmarCode;
    // 校验类型
    private Integer calibrateType;
    // 砝码重量
    private BigDecimal farmarWeight;
    // 砝码长
    private BigDecimal farmarLength;
    // 砝码宽
    private BigDecimal farmarWidth;
    // 砝码高
    private BigDecimal farmarHigh;
    // 实际重量
    private BigDecimal actualWeight;
    // 实际长
    private BigDecimal actualLength;
    // 实际宽
    private BigDecimal actualWidth;
    // 实际高
    private BigDecimal actualHigh;
    // 重量校准状态
    private Integer weightCalibrateStatus;
    // 体积校准状态
    private Integer volumeCalibrateStatus;
    // 校准时间
    private Long calibrateTime;
    // 设备状态
    private Integer machineStatus;
    // 上次设备状态
    private Integer previousMachineStatus;
    // 上次校准时间
    private Long previousCalibrateTime;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getFarmarCode() {
        return farmarCode;
    }

    public void setFarmarCode(String farmarCode) {
        this.farmarCode = farmarCode;
    }

    public Integer getCalibrateType() {
        return calibrateType;
    }

    public void setCalibrateType(Integer calibrateType) {
        this.calibrateType = calibrateType;
    }

    public BigDecimal getFarmarWeight() {
        return farmarWeight;
    }

    public void setFarmarWeight(BigDecimal farmarWeight) {
        this.farmarWeight = farmarWeight;
    }

    public BigDecimal getFarmarLength() {
        return farmarLength;
    }

    public void setFarmarLength(BigDecimal farmarLength) {
        this.farmarLength = farmarLength;
    }

    public BigDecimal getFarmarWidth() {
        return farmarWidth;
    }

    public void setFarmarWidth(BigDecimal farmarWidth) {
        this.farmarWidth = farmarWidth;
    }

    public BigDecimal getFarmarHigh() {
        return farmarHigh;
    }

    public void setFarmarHigh(BigDecimal farmarHigh) {
        this.farmarHigh = farmarHigh;
    }

    public BigDecimal getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(BigDecimal actualWeight) {
        this.actualWeight = actualWeight;
    }

    public BigDecimal getActualLength() {
        return actualLength;
    }

    public void setActualLength(BigDecimal actualLength) {
        this.actualLength = actualLength;
    }

    public BigDecimal getActualWidth() {
        return actualWidth;
    }

    public void setActualWidth(BigDecimal actualWidth) {
        this.actualWidth = actualWidth;
    }

    public BigDecimal getActualHigh() {
        return actualHigh;
    }

    public void setActualHigh(BigDecimal actualHigh) {
        this.actualHigh = actualHigh;
    }

    public Integer getWeightCalibrateStatus() {
        return weightCalibrateStatus;
    }

    public Integer getVolumeCalibrateStatus() {
        return volumeCalibrateStatus;
    }

    public void setVolumeCalibrateStatus(Integer volumeCalibrateStatus) {
        this.volumeCalibrateStatus = volumeCalibrateStatus;
    }

    public void setWeightCalibrateStatus(Integer weightCalibrateStatus) {
        this.weightCalibrateStatus = weightCalibrateStatus;
    }

    public Long getCalibrateTime() {
        return calibrateTime;
    }

    public void setCalibrateTime(Long calibrateTime) {
        this.calibrateTime = calibrateTime;
    }

    public Integer getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(Integer machineStatus) {
        this.machineStatus = machineStatus;
    }

    public Integer getPreviousMachineStatus() {
        return previousMachineStatus;
    }

    public void setPreviousMachineStatus(Integer previousMachineStatus) {
        this.previousMachineStatus = previousMachineStatus;
    }

    public Long getPreviousCalibrateTime() {
        return previousCalibrateTime;
    }

    public void setPreviousCalibrateTime(Long previousCalibrateTime) {
        this.previousCalibrateTime = previousCalibrateTime;
    }
}
