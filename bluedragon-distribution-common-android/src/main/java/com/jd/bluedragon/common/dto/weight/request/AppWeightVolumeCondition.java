package com.jd.bluedragon.common.dto.weight.request;

import java.util.Date;
import java.util.List;

/**
 * @program: ql-dms-distribution
 * @description:
 * @author: caozhixing3
 * @date: 2024-03-25 14:13
 **/
public class AppWeightVolumeCondition {

    /**
     * 操作单号
     */
    private String barCode;
    /**
     * 处理类型
     */
    private String businessType;
    /**
     * 业务来源
     */
    private String sourceCode;

    /**
     * 操作站点编号
     */
    private Integer operateSiteCode;
    /**
     * 操作站点名称
     */
    private String operateSiteName;
    /**
     * 操作人ERP编号
     */
    private String operatorCode;
    /**
     * 操作人ID
     */
    private Integer operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;
    /**
     * 重量 单位KG
     */
    private Double weight;
    /**
     * 长度 单位CM
     */
    private Double length;
    /**
     * 宽 单位CM
     */
    private Double width;
    /**
     * 高 单位CM
     */
    private Double height;
    /**
     * 体积 单位cm³
     */
    private Double volume;
    /**
     * 操作时间 13位时间戳
     */
    private Date operateTime;
    /**
     * 长包裹 0:普通包裹 1:长包裹
     * */
    private Integer longPackage;
    /**
     * 设备编码
     */
    private String machineCode;
    /**
     * 按总体积量方
     */
    private Boolean totalVolumeFlag  = Boolean.FALSE;
    /**
     * 超重服务-确认标识
     */
    private Boolean overLengthAndWeightConfirmFlag  = Boolean.FALSE;
    /**
     * 超重服务标识
     */
    private Boolean overLengthAndWeightEnable;
    /**
     * 超重服务类型
     */
    private List<String> overLengthAndWeightTypes;
    //======================================================

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getLongPackage() {
        return longPackage;
    }

    public void setLongPackage(Integer longPackage) {
        this.longPackage = longPackage;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public Boolean getTotalVolumeFlag() {
        return totalVolumeFlag;
    }

    public void setTotalVolumeFlag(Boolean totalVolumeFlag) {
        this.totalVolumeFlag = totalVolumeFlag;
    }

    public Boolean getOverLengthAndWeightConfirmFlag() {
        return overLengthAndWeightConfirmFlag;
    }

    public void setOverLengthAndWeightConfirmFlag(Boolean overLengthAndWeightConfirmFlag) {
        this.overLengthAndWeightConfirmFlag = overLengthAndWeightConfirmFlag;
    }

    public Boolean getOverLengthAndWeightEnable() {
        return overLengthAndWeightEnable;
    }

    public void setOverLengthAndWeightEnable(Boolean overLengthAndWeightEnable) {
        this.overLengthAndWeightEnable = overLengthAndWeightEnable;
    }

    public List<String> getOverLengthAndWeightTypes() {
        return overLengthAndWeightTypes;
    }

    public void setOverLengthAndWeightTypes(List<String> overLengthAndWeightTypes) {
        this.overLengthAndWeightTypes = overLengthAndWeightTypes;
    }
}
