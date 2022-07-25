package com.jd.bluedragon.distribution.consumable.domain;

import java.math.BigDecimal;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.consumable.domain
 * @ClassName: PackingConsumableFinanceMessageDto
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/5/11 19:15
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class PackingConsumableFinanceMessageDto {

    private String waybillCode;

    private String supplierCode;

    private String packingTime;

    private Integer siteCode;

    private BigDecimal packingVolume;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getPackingTime() {
        return packingTime;
    }

    public void setPackingTime(String packingTime) {
        this.packingTime = packingTime;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public BigDecimal getPackingVolume() {
        return packingVolume;
    }

    public void setPackingVolume(BigDecimal packingVolume) {
        this.packingVolume = packingVolume;
    }
}
