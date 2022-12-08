package com.jd.bluedragon.common.dto.operation.workbench.calibrate;

import java.io.Serializable;
import java.math.BigDecimal;

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
    private BigDecimal farmarWeight;

    /**
     * 砝码长：cm
     */
    private BigDecimal farmarLength;

    /**
     * 砝码宽：cm
     */
    private BigDecimal farmarWidth;

    /**
     * 砝码高：cm
     */
    private BigDecimal farmarHigh;

    /**
     * 砝码长宽高
     */
    private String farmarLwh;

    /**
     * 实际重量
     */
    private BigDecimal actualWeight;

    /**
     * 实际长：cm
     */
    private BigDecimal actualLength;

    /**
     * 实际宽：cm
     */
    private BigDecimal actualWidth;

    /**
     * 实际高：cm
     */
    private BigDecimal actualHigh;

    /**
     * 实际长宽高
     */
    private String actualLwh;

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

    public String getFarmarLwh() {
        return farmarLwh;
    }

    public void setFarmarLwh(String farmarLwh) {
        this.farmarLwh = farmarLwh;
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

    public String getActualLwh() {
        return actualLwh;
    }

    public void setActualLwh(String actualLwh) {
        this.actualLwh = actualLwh;
    }
}
