package com.jd.bluedragon.distribution.consumable.domain;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.consumable.domain
 * @ClassName: BoxChargeDetail
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/2/7 11:46
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class BoxChargeDetail implements Serializable {

    /**
     * 包装耗材编码
     */
    private String barCode;

    /**
     * 包装耗材名称
     */
    private String boxName;

    /**
     * 包装耗材数量
     */
    private Integer boxNumber;

    /**
     * 包装耗材总计(B网)
     */
    private BigDecimal materialAmount;

    /**
     * 包装箱数量
     */
    @Deprecated
    private Integer boxCount;

    /**
     * 包裝箱单价
     */
    @Deprecated
    private BigDecimal boxMoney;

    /**
     * 包装耗材单价
     */
    private Double price;

    /**
     * 包装类型 1-高毛利 2-低毛利
     */
    @Deprecated
    private Integer boxType;

    /**
     * B网包装耗材编码
     */
    @Deprecated
    private String materialCode;

    /**
     * B网包装耗材名称
     */
    @Deprecated
    private String materialName;

    /**
     * 包装耗材类型编码(B网)
     */
    private String materialTypeCode;

    /**
     * 包装耗材类型(B网)
     */
    private String materialTypeName;

    /**
     * 包装耗材体积(B网)
     */
    private Double materialVolume;

    /**
     * 包装耗材体积系数(B网)
     */
    private Double volumeCoefficient;

    /**
     * 包装耗材包装规格(B网)
     */
    private String materialSpecification;

    /**
     * 包装耗材单位(B网)
     */
    private String materialUnit;

    /**
     * 包装耗材数量(B网)
     * initPackingNumber*volumeCoefficient
     */
    private BigDecimal materialNumber;

    /**
     * 包装耗材录入数量(B网)
     */
    private Double initPackingNumber;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public Integer getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(Integer boxNumber) {
        this.boxNumber = boxNumber;
    }

    public BigDecimal getMaterialAmount() {
        return materialAmount;
    }

    public void setMaterialAmount(BigDecimal materialAmount) {
        this.materialAmount = materialAmount;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getMaterialTypeCode() {
        return materialTypeCode;
    }

    public void setMaterialTypeCode(String materialTypeCode) {
        this.materialTypeCode = materialTypeCode;
    }

    public String getMaterialTypeName() {
        return materialTypeName;
    }

    public void setMaterialTypeName(String materialTypeName) {
        this.materialTypeName = materialTypeName;
    }

    public Double getMaterialVolume() {
        return materialVolume;
    }

    public void setMaterialVolume(Double materialVolume) {
        this.materialVolume = materialVolume;
    }

    public Double getVolumeCoefficient() {
        return volumeCoefficient;
    }

    public void setVolumeCoefficient(Double volumeCoefficient) {
        this.volumeCoefficient = volumeCoefficient;
    }

    public String getMaterialSpecification() {
        return materialSpecification;
    }

    public void setMaterialSpecification(String materialSpecification) {
        this.materialSpecification = materialSpecification;
    }

    public String getMaterialUnit() {
        return materialUnit;
    }

    public void setMaterialUnit(String materialUnit) {
        this.materialUnit = materialUnit;
    }

    public BigDecimal getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(BigDecimal materialNumber) {
        this.materialNumber = materialNumber;
    }

    public Double getInitPackingNumber() {
        return initPackingNumber;
    }

    public void setInitPackingNumber(Double initPackingNumber) {
        this.initPackingNumber = initPackingNumber;
    }

    public Integer getBoxCount() {
        return boxCount;
    }

    public void setBoxCount(Integer boxCount) {
        this.boxCount = boxCount;
    }

    public BigDecimal getBoxMoney() {
        return boxMoney;
    }

    public void setBoxMoney(BigDecimal boxMoney) {
        this.boxMoney = boxMoney;
    }

    public Integer getBoxType() {
        return boxType;
    }

    public void setBoxType(Integer boxType) {
        this.boxType = boxType;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}
