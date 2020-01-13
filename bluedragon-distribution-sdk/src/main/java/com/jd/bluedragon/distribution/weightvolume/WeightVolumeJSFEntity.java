package com.jd.bluedragon.distribution.weightvolume;

import java.util.Date;

/**
 * <p>
 *     分拣称重量方的JSF实体
 *
 * @author wuzuxiang
 * @since 2020/1/10
 **/
public class WeightVolumeJSFEntity {

    /**
     * 操作单号
     */
    private String barCode;

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
     * 业务类型枚举，要求：枚举值 大写 值来源于客户单定义 具有可识别性可读性
     * 含义：该流水的业务来源
     * @see WeightVolumeBusinessTypeEnum
     */
    private WeightVolumeBusinessTypeEnum businessType;

    /**
     * 系统识别编号， 要求：枚举值大写，值来自于调用方系统定义，约定俗成
     * @see FromSourceEnum
     */
    private FromSourceEnum sourceCode;

    /**
     * 操作站点编号
     */
    private Integer operateSiteCode;

    /**
     * 操作站点名称
     */
    private String operateSiteName;

    /**
     * 操作人编号
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
     * 操作时间
     */
    private Date operateTime;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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

    public WeightVolumeBusinessTypeEnum getBusinessType() {
        return businessType;
    }

    public void setBusinessType(WeightVolumeBusinessTypeEnum businessType) {
        this.businessType = businessType;
    }

    public FromSourceEnum getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(FromSourceEnum sourceCode) {
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

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
