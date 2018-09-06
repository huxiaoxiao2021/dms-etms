package com.jd.bluedragon.distribution.packingconsumable.domain;

import java.io.Serializable;

/**
 * Created by hanjiaxing1 on 2018/8/10.
 */
public class PackingConsumableBaseInfo implements Serializable {

    /*耗材编号*/
    private String packingCode;

    /*耗材名称*/
    private String packingName;

    /*耗材类型*/
    private String packingType;

    /*耗材类型名称*/
    private String packingTypeName;

    /*体积*/
    private Double packingVolume;

    /*体积系数*/
    private Double volumeCoefficient;

    /*规格*/
    private String packingSpecification;

    /*单位*/
    private String packingUnit;

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

    public String getPackingTypeName() {
        return packingTypeName;
    }

    public void setPackingTypeName(String packingTypeName) {
        this.packingTypeName = packingTypeName;
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

}
