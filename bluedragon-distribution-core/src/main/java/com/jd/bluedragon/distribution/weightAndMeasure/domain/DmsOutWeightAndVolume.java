package com.jd.bluedragon.distribution.weightAndMeasure.domain;

import java.util.Date;

public class DmsOutWeightAndVolume {
    /**
     * 操作类型常量1：静态 2：动态
     */
    public static final Integer OPERATE_TYPE_STATIC = 1;
    public static final Integer OPERATE_TYPE_DYNAMIC = 2;

    /**
     * barCode类型1：包裹号 2：箱号
     */
    public static final Integer BARCODE_TYPE_PACKAGECODE = 1;
    public static final Integer BARCODE_TYPE_BOXCODE = 2;

    /**
     * 全局唯一id
     */
    private Long id;
    /**
     * 扫描的包裹号/箱号/板号
     */
    private String barCode;
    /**
     * 扫描到的号码类型1：包裹号 2：箱号 3：板号
     */
    private Integer barCodeType;
    /**
     * 重量 单位kg
     */
    private Double weight;
    /**
     * 体积 单位cm³
     */
    private Double volume;

    /**
     * 长 单位cm
     */
    private Double length;
    /**
     * 宽 单位cm
     */
    private Double width;
    /**
     * 高 单位cm
     */
    private Double height;

    /**
     * 操作类型 1：静态量方 2：动态量方
     */
    private Integer operateType;
    /**
     * 操作站点编码
     */
    private Integer createSiteCode;
    /**
     * 称重人编码
     */
    private Integer weightUserCode;

    /**
     * 称重人姓名
     */
    private String weightUserName;
    /**
     * 称重时间
     */
    private Date weightTime;
    /**
     * 量方人编码
     */
    private Integer measureUserCode;

    /**
     * 量方人姓名
     */
    private String measureUserName;

    /**
     * 量方时间
     */
    private Date measureTime;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 有效标识
     */
    private Integer isDelete;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getBarCodeType() {
        return barCodeType;
    }

    public void setBarCodeType(Integer barCodeType) {
        this.barCodeType = barCodeType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
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

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getWeightUserCode() {
        return weightUserCode;
    }

    public void setWeightUserCode(Integer weightUserCode) {
        this.weightUserCode = weightUserCode;
    }

    public String getWeightUserName() {
        return weightUserName;
    }

    public void setWeightUserName(String weightUserName) {
        this.weightUserName = weightUserName;
    }

    public Date getWeightTime() {
        return weightTime;
    }

    public void setWeightTime(Date weightTime) {
        this.weightTime = weightTime;
    }

    public Integer getMeasureUserCode() {
        return measureUserCode;
    }

    public void setMeasureUserCode(Integer measureUserCode) {
        this.measureUserCode = measureUserCode;
    }

    public String getMeasureUserName() {
        return measureUserName;
    }

    public void setMeasureUserName(String measureUserName) {
        this.measureUserName = measureUserName;
    }

    public Date getMeasureTime() {
        return measureTime;
    }

    public void setMeasureTime(Date measureTime) {
        this.measureTime = measureTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }
}
