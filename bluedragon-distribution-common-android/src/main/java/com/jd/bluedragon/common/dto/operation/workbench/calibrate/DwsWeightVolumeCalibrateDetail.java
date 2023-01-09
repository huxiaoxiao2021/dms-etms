package com.jd.bluedragon.common.dto.operation.workbench.calibrate;

import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineCalibrateTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineVolumeCalibrateStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizTaskMachineWeightCalibrateStatusEnum;

import java.io.Serializable;

/**
 * 设备称重量方校准明细
 *
 * @author hujiping
 * @date 2022/12/7 8:39 PM
 */
public class DwsWeightVolumeCalibrateDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 校准类型
     * @see JyBizTaskMachineCalibrateTypeEnum
     */
    private Integer calibrateType;

    /**
     * 设备编码
     */
    private String machineCode;

    /**
     * 砝码编码
     */
    private String farmarCode;

    /**
     * 砝码重量
     */
    private String farmarWeight;

    /**
     * 砝码长：cm
     */
    private String farmarLength;

    /**
     * 砝码宽：cm
     */
    private String farmarWidth;

    /**
     * 砝码高：cm
     */
    private String farmarHigh;

    /**
     * 实际重量
     */
    private String actualWeight;

    /**
     * 实际长：cm
     */
    private String actualLength;

    /**
     * 实际宽：cm
     */
    private String actualWidth;

    /**
     * 实际高：cm
     */
    private String actualHigh;

    /**
     * 校准状态
     * @see JyBizTaskMachineVolumeCalibrateStatusEnum
     * @see JyBizTaskMachineWeightCalibrateStatusEnum
     */
    private Integer calibrateStatus;

    /**
     * 校准时间
     */
    private Long calibrateTime;

    /**
     * 误差范围
     */
    private String errorRange;

    public Integer getCalibrateType() {
        return calibrateType;
    }

    public void setCalibrateType(Integer calibrateType) {
        this.calibrateType = calibrateType;
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

    public String getFarmarWeight() {
        return farmarWeight;
    }

    public void setFarmarWeight(String farmarWeight) {
        this.farmarWeight = farmarWeight;
    }

    public String getFarmarLength() {
        return farmarLength;
    }

    public void setFarmarLength(String farmarLength) {
        this.farmarLength = farmarLength;
    }

    public String getFarmarWidth() {
        return farmarWidth;
    }

    public void setFarmarWidth(String farmarWidth) {
        this.farmarWidth = farmarWidth;
    }

    public String getFarmarHigh() {
        return farmarHigh;
    }

    public void setFarmarHigh(String farmarHigh) {
        this.farmarHigh = farmarHigh;
    }

    public String getActualWeight() {
        return actualWeight;
    }

    public void setActualWeight(String actualWeight) {
        this.actualWeight = actualWeight;
    }

    public String getActualLength() {
        return actualLength;
    }

    public void setActualLength(String actualLength) {
        this.actualLength = actualLength;
    }

    public String getActualWidth() {
        return actualWidth;
    }

    public void setActualWidth(String actualWidth) {
        this.actualWidth = actualWidth;
    }

    public String getActualHigh() {
        return actualHigh;
    }

    public void setActualHigh(String actualHigh) {
        this.actualHigh = actualHigh;
    }

    public Integer getCalibrateStatus() {
        return calibrateStatus;
    }

    public void setCalibrateStatus(Integer calibrateStatus) {
        this.calibrateStatus = calibrateStatus;
    }

    public Long getCalibrateTime() {
        return calibrateTime;
    }

    public void setCalibrateTime(Long calibrateTime) {
        this.calibrateTime = calibrateTime;
    }

    public String getErrorRange() {
        return errorRange;
    }

    public void setErrorRange(String errorRange) {
        this.errorRange = errorRange;
    }
}
