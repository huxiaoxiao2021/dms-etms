package com.jd.bluedragon.distribution.consumable.domain;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.consumable.domain
 * @ClassName: WaybillConsumableDetailMessageDto
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/5/15 21:20
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class WaybillConsumableDetailMessageDto {

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包装耗材编码
     */
    private String consumableCode;

    /**
     * 包装耗材名称
     */
    private String consumableName;

    /**
     * 包装耗材类型
     */
    private String consumableType;

    /**
     * 包装耗材类型名称
     */
    private String consumableTypeName;

    /**
     * 包装耗材体积(立方厘米)
     */
    private Double volume;

    /**
     * 包装耗材体积系数
     */
    private Double volumeCoefficient;

    /**
     * 包装耗材规格
     */
    private String specification;

    /**
     * 包装耗材单位
     */
    private String unit;

    /**
     * 包装耗材价格
     */
    private Double packingCharge;

    /**
     * 揽收数量
     */
    private Double collectionNumber;

    /**
     * 确认数量
     */
    private Double confirmNumber;

    /**
     * 打包人erp
     */
    private String packingErp;

    /**
     * 打包人ID
     */
    private String packingUserId;

    /**
     * 打包后包装耗材体积(立方米)
     */
    private Double packingVolume;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getConsumableCode() {
        return consumableCode;
    }

    public void setConsumableCode(String consumableCode) {
        this.consumableCode = consumableCode;
    }

    public String getConsumableName() {
        return consumableName;
    }

    public void setConsumableName(String consumableName) {
        this.consumableName = consumableName;
    }

    public String getConsumableType() {
        return consumableType;
    }

    public void setConsumableType(String consumableType) {
        this.consumableType = consumableType;
    }

    public String getConsumableTypeName() {
        return consumableTypeName;
    }

    public void setConsumableTypeName(String consumableTypeName) {
        this.consumableTypeName = consumableTypeName;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Double getVolumeCoefficient() {
        return volumeCoefficient;
    }

    public void setVolumeCoefficient(Double volumeCoefficient) {
        this.volumeCoefficient = volumeCoefficient;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getPackingCharge() {
        return packingCharge;
    }

    public void setPackingCharge(Double packingCharge) {
        this.packingCharge = packingCharge;
    }

    public Double getCollectionNumber() {
        return collectionNumber;
    }

    public void setCollectionNumber(Double collectionNumber) {
        this.collectionNumber = collectionNumber;
    }

    public Double getConfirmNumber() {
        return confirmNumber;
    }

    public void setConfirmNumber(Double confirmNumber) {
        this.confirmNumber = confirmNumber;
    }

    public String getPackingErp() {
        return packingErp;
    }

    public void setPackingErp(String packingErp) {
        this.packingErp = packingErp;
    }

    public String getPackingUserId() {
        return packingUserId;
    }

    public void setPackingUserId(String packingUserId) {
        this.packingUserId = packingUserId;
    }

    public Double getPackingVolume() {
        return packingVolume;
    }

    public void setPackingVolume(Double packingVolume) {
        this.packingVolume = packingVolume;
    }
}
