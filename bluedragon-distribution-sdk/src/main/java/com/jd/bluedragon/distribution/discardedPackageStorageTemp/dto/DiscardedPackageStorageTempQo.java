package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 快递弃件暂存<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 * 
 * @author fanggang7
 * @time 2021-03-31 11:32:59 周三
 */
public class DiscardedPackageStorageTempQo extends BasePagerCondition implements Serializable {

    private static final long serialVersionUID = -8295174102368328510L;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 运单号
     */
    private String packageCode;

    /**
     * 区域
     */
    private Integer orgCode;

    /**
     * 分拣中心
     */
    private Integer siteCode;

    /**
     * 商家名称
     */
    private String businessName;

    /**
     * 是否是COD
     */
    private Integer isCod;

    /**
     * 存储天数，录入起、止天数
     */
    private Integer storageDaysFrom;

    /**
     * 存储天数，录入起、止天数
     */
    private Integer storageDaysTo;

    /**
     * 扫描开始时间
     */
    private Date scanTimeFrom;
    /**
     * 扫描结束时间
     */
    private Date scanTimeTo;

    private Integer yn;

    private String currentUserErp;

    private Integer isAsyncExport;

    private Integer pageSize;
    /**
     * 操作类型-1-弃件暂存 2-弃件废弃
     */
    private Integer operateType;
    /**
     * 运单类型 1-包裹类 2-信件类 
     */
    private Integer waybillType;

    /**
     * 提交状态
     */
    private Integer submitStatus;

    /**
     * 场地部门类型
     */
    private Integer siteDepartType;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(Integer orgCode) {
        this.orgCode = orgCode;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public Integer getIsCod() {
        return isCod;
    }

    public void setIsCod(Integer isCod) {
        this.isCod = isCod;
    }

    public Integer getStorageDaysFrom() {
        return storageDaysFrom;
    }

    public void setStorageDaysFrom(Integer storageDaysFrom) {
        this.storageDaysFrom = storageDaysFrom;
    }

    public Integer getStorageDaysTo() {
        return storageDaysTo;
    }

    public void setStorageDaysTo(Integer storageDaysTo) {
        this.storageDaysTo = storageDaysTo;
    }

    public Date getScanTimeFrom() {
        return scanTimeFrom;
    }

    public void setScanTimeFrom(Date scanTimeFrom) {
        this.scanTimeFrom = scanTimeFrom;
    }

    public Date getScanTimeTo() {
        return scanTimeTo;
    }

    public void setScanTimeTo(Date scanTimeTo) {
        this.scanTimeTo = scanTimeTo;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
    public String getCurrentUserErp() {
        return currentUserErp;
    }

    public void setCurrentUserErp(String currentUserErp) {
        this.currentUserErp = currentUserErp;
    }

    public Integer getIsAsyncExport() {
        return isAsyncExport;
    }

    public void setIsAsyncExport(Integer isAsyncExport) {
        this.isAsyncExport = isAsyncExport;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        this.setLimit(pageSize);
    }

	public Integer getOperateType() {
		return operateType;
	}

	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}

	public Integer getWaybillType() {
		return waybillType;
	}

	public void setWaybillType(Integer waybillType) {
		this.waybillType = waybillType;
	}

    public Integer getSubmitStatus() {
        return submitStatus;
    }

    public DiscardedPackageStorageTempQo setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
        return this;
    }

    public Integer getSiteDepartType() {
        return siteDepartType;
    }

    public DiscardedPackageStorageTempQo setSiteDepartType(Integer siteDepartType) {
        this.siteDepartType = siteDepartType;
        return this;
    }
}