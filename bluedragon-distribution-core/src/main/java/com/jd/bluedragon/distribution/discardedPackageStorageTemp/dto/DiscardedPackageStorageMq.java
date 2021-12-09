package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto;

import java.io.Serializable;

/**
 * 弃件暂存包裹纬度mq消息类
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-09 11:01:10 周四
 */
public class DiscardedPackageStorageMq implements Serializable {
    private static final long serialVersionUID = 3806849569802732760L;

    private String barCode;

    private Integer siteDepartType;

    private Integer waybillType;

    private Integer storageStatus;

    private String operateUserErp;

    private String operateUserName;

    private Integer operateSiteCode;

    private String operateSiteName;

    private Long operateTimeMillSeconds;

    public DiscardedPackageStorageMq() {
    }

    public String getBarCode() {
        return barCode;
    }

    public DiscardedPackageStorageMq setBarCode(String barCode) {
        this.barCode = barCode;
        return this;
    }

    public Integer getSiteDepartType() {
        return siteDepartType;
    }

    public DiscardedPackageStorageMq setSiteDepartType(Integer siteDepartType) {
        this.siteDepartType = siteDepartType;
        return this;
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public DiscardedPackageStorageMq setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
        return this;
    }

    public Integer getStorageStatus() {
        return storageStatus;
    }

    public DiscardedPackageStorageMq setStorageStatus(Integer storageStatus) {
        this.storageStatus = storageStatus;
        return this;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public DiscardedPackageStorageMq setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
        return this;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public DiscardedPackageStorageMq setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
        return this;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public DiscardedPackageStorageMq setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
        return this;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public DiscardedPackageStorageMq setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
        return this;
    }

    public Long getOperateTimeMillSeconds() {
        return operateTimeMillSeconds;
    }

    public DiscardedPackageStorageMq setOperateTimeMillSeconds(Long operateTimeMillSeconds) {
        this.operateTimeMillSeconds = operateTimeMillSeconds;
        return this;
    }

    @Override
    public String toString() {
        return "DiscardedPackageStorageMq{" +
                "barCode='" + barCode + '\'' +
                ", siteDepartType=" + siteDepartType +
                ", waybillType=" + waybillType +
                ", storageStatus=" + storageStatus +
                ", operateUserErp='" + operateUserErp + '\'' +
                ", operateUserName='" + operateUserName + '\'' +
                ", siteCode=" + operateSiteCode +
                ", siteName='" + operateSiteName + '\'' +
                ", operateTimeMillSeconds=" + operateTimeMillSeconds +
                '}';
    }
}
