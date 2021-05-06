package com.jd.bluedragon.distribution.sealVehicle.domain;

import java.io.Serializable;

/**
 * 封车集齐监控报表返回参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-04-29 14:22:48 周日
 */
public class SealCarNotCollectedDto implements Serializable {

    private static final long serialVersionUID = -5424540721703078519L;

    /**
     * 分拣中心ID
     */
    private Long siteId;

    /**
     * 封车号
     */
    private String sealCarCode;

    /**
     * 封车车牌号
     */
    private String vehicleNumber;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 系统包裹总数
     */
    private Integer packageNumSys;

    /**
     * 封车包裹数
     */
    private Integer packageNumSeal;

    public Long getSiteId() {
        return siteId;
    }

    public SealCarNotCollectedDto setSiteId(Long siteId) {
        this.siteId = siteId;
        return this;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public SealCarNotCollectedDto setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
        return this;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public SealCarNotCollectedDto setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
        return this;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public SealCarNotCollectedDto setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
        return this;
    }

    public Integer getPackageNumSys() {
        return packageNumSys;
    }

    public SealCarNotCollectedDto setPackageNumSys(Integer packageNumSys) {
        this.packageNumSys = packageNumSys;
        return this;
    }

    public Integer getPackageNumSeal() {
        return packageNumSeal;
    }

    public SealCarNotCollectedDto setPackageNumSeal(Integer packageNumSeal) {
        this.packageNumSeal = packageNumSeal;
        return this;
    }
}
