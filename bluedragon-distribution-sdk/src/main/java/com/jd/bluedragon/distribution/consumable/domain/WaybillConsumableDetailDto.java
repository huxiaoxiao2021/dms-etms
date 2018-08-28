package com.jd.bluedragon.distribution.consumable.domain;

import java.io.Serializable;

/**
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2018年08月22日 10时:37分
 */
public class WaybillConsumableDetailDto   implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 包装耗材编号
     */
    private String packingCode;

    /**
     * 包装耗材名称
     */
    private String packingName;

    /**
     * 包装耗材类型
     */
    private String packingType;

    /**
     * 包装耗材体积
     */
    private Double packingVolume;

    /**
     * 包装耗材体积系数
     */
    private Double volumeCoefficient;

    /**
     * 包装规格
     */
    private String packingSpecification;

    /**
     * 包装耗材单位
     */
    private String packingUnit;

    /**
     * 包装耗材数量
     */
    private Double packingNumber;

    /**
     * 包装耗材价格
     */
    private Double packingCharge;

    public String getPackingCode() {
        return packingCode;
    }

    public void setPackingCode(String packingCode) {
        this.packingCode = packingCode;
    }

    public String getPackingName() {
        return packingName;
    }

    public void setPackingName(String packingName) {
        this.packingName = packingName;
    }

    public String getPackingType() {
        return packingType;
    }

    public void setPackingType(String packingType) {
        this.packingType = packingType;
    }

    public Double getPackingVolume() {
        return packingVolume;
    }

    public void setPackingVolume(Double packingVolume) {
        this.packingVolume = packingVolume;
    }

    public Double getVolumeCoefficient() {
        return volumeCoefficient;
    }

    public void setVolumeCoefficient(Double volumeCoefficient) {
        this.volumeCoefficient = volumeCoefficient;
    }

    public String getPackingSpecification() {
        return packingSpecification;
    }

    public void setPackingSpecification(String packingSpecification) {
        this.packingSpecification = packingSpecification;
    }

    public String getPackingUnit() {
        return packingUnit;
    }

    public void setPackingUnit(String packingUnit) {
        this.packingUnit = packingUnit;
    }

    public Double getPackingNumber() {
        return packingNumber;
    }

    public void setPackingNumber(Double packingNumber) {
        this.packingNumber = packingNumber;
    }

    public Double getPackingCharge() {
        return packingCharge;
    }

    public void setPackingCharge(Double packingCharge) {
        this.packingCharge = packingCharge;
    }
}
