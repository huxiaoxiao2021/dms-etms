package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 异常-破损 entity
 */
public class JyDamageConsumableEntity implements Serializable {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 业务id
     */
    private String damageBizId;

    private String provinceAgencyCode;

    private String provinceAgencyName;

    /**
     * 枢纽
     */
    private String areaHubCode;

    /**
     * 枢纽
     */
    private String areaHubName;
    /**
     * 站点code
     */
    private Integer siteCode;

    /**
     * 站点name
     */
    private String siteName;

    /**
     * 楼层
     */
    private Integer floor;

    /**
     * 网格号
     */
    private String gridNo;

    /**
     * 网码码
     */
    private String gridCode;

    /**
     * 网码名
     */
    private String gridName;

    /**
     * 网格负责人
     */
    private String ownerUserErp;

    /**
     * 作业区
     */
    private String areaCode;

    /**
     * 作业区
     */
    private String areaName;

    /**
     * 异常包裹条码
     */
    private String barCode;

    /**
     * 异常包裹运单号
     */
    private String waybillCode;

    /**
     * 耗材id
     */
    private Integer consumableCode;
    /**
     * 耗材名
     */
    private String consumableName;

    /**
     * 耗材条码
     */
    private String consumableBarcode;

    /**
     * 操作人
     */
    private String operatorErp;

    /**
     * 操作人
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 时间戳
     */
    private Date ts;

    /**
     * 删除标识: 1: 使用 0 删除
     */
    private Integer yn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDamageBizId() {
        return damageBizId;
    }

    public void setDamageBizId(String damageBizId) {
        this.damageBizId = damageBizId;
    }

    public String getProvinceAgencyCode() {
        return provinceAgencyCode;
    }

    public void setProvinceAgencyCode(String provinceAgencyCode) {
        this.provinceAgencyCode = provinceAgencyCode;
    }

    public String getProvinceAgencyName() {
        return provinceAgencyName;
    }

    public void setProvinceAgencyName(String provinceAgencyName) {
        this.provinceAgencyName = provinceAgencyName;
    }

    public String getAreaHubCode() {
        return areaHubCode;
    }

    public void setAreaHubCode(String areaHubCode) {
        this.areaHubCode = areaHubCode;
    }

    public String getAreaHubName() {
        return areaHubName;
    }

    public void setAreaHubName(String areaHubName) {
        this.areaHubName = areaHubName;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getGridNo() {
        return gridNo;
    }

    public void setGridNo(String gridNo) {
        this.gridNo = gridNo;
    }

    public String getGridCode() {
        return gridCode;
    }

    public void setGridCode(String gridCode) {
        this.gridCode = gridCode;
    }

    public String getGridName() {
        return gridName;
    }

    public void setGridName(String gridName) {
        this.gridName = gridName;
    }

    public String getOwnerUserErp() {
        return ownerUserErp;
    }

    public void setOwnerUserErp(String ownerUserErp) {
        this.ownerUserErp = ownerUserErp;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getConsumableCode() {
        return consumableCode;
    }

    public void setConsumableCode(Integer consumableCode) {
        this.consumableCode = consumableCode;
    }

    public String getConsumableName() {
        return consumableName;
    }

    public void setConsumableName(String consumableName) {
        this.consumableName = consumableName;
    }

    public String getConsumableBarcode() {
        return consumableBarcode;
    }

    public void setConsumableBarcode(String consumableBarcode) {
        this.consumableBarcode = consumableBarcode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
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

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
