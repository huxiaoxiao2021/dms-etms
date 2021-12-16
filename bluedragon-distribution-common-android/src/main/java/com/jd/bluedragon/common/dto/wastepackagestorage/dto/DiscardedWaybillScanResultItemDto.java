package com.jd.bluedragon.common.dto.wastepackagestorage.dto;

import java.io.Serializable;

/**
 * 弃件扫描结果单行结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-01 21:27:56 周三
 */
public class DiscardedWaybillScanResultItemDto implements Serializable {
    private static final long serialVersionUID = 8763317540772958472L;

    private Long id;

    private String waybillCode;

    private Integer status;

    private String operatorErp;

    private String operatorName;

    private Integer siteCode;
    private String siteName;
    private Integer operateType;
    private Integer waybillType;
    private Integer siteDepartType;
    private Integer submitStatus;
    /**
     * 系统包裹数
     */
    private Integer packageSysTotal;

    /**
     * 已验未发个数
     */
    private Integer inspectionNotSendTotal;

    /**
     * 已扫总数
     */
    private Integer packageScanTotal;

    /**
     * 未扫总数
     */
    private Integer packageNotScanTotal;

    public DiscardedWaybillScanResultItemDto() {
    }

    public Long getId() {
        return id;
    }

    public DiscardedWaybillScanResultItemDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public DiscardedWaybillScanResultItemDto setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public DiscardedWaybillScanResultItemDto setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public DiscardedWaybillScanResultItemDto setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
        return this;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public DiscardedWaybillScanResultItemDto setOperatorName(String operatorName) {
        this.operatorName = operatorName;
        return this;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public DiscardedWaybillScanResultItemDto setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
        return this;
    }

    public String getSiteName() {
        return siteName;
    }

    public DiscardedWaybillScanResultItemDto setSiteName(String siteName) {
        this.siteName = siteName;
        return this;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public DiscardedWaybillScanResultItemDto setOperateType(Integer operateType) {
        this.operateType = operateType;
        return this;
    }

    public Integer getWaybillType() {
        return waybillType;
    }

    public DiscardedWaybillScanResultItemDto setWaybillType(Integer waybillType) {
        this.waybillType = waybillType;
        return this;
    }

    public Integer getSiteDepartType() {
        return siteDepartType;
    }

    public DiscardedWaybillScanResultItemDto setSiteDepartType(Integer siteDepartType) {
        this.siteDepartType = siteDepartType;
        return this;
    }

    public Integer getSubmitStatus() {
        return submitStatus;
    }

    public DiscardedWaybillScanResultItemDto setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
        return this;
    }

    public Integer getPackageSysTotal() {
        return packageSysTotal;
    }

    public DiscardedWaybillScanResultItemDto setPackageSysTotal(Integer packageSysTotal) {
        this.packageSysTotal = packageSysTotal;
        return this;
    }

    public Integer getInspectionNotSendTotal() {
        return inspectionNotSendTotal;
    }

    public DiscardedWaybillScanResultItemDto setInspectionNotSendTotal(Integer inspectionNotSendTotal) {
        this.inspectionNotSendTotal = inspectionNotSendTotal;
        return this;
    }

    public Integer getPackageScanTotal() {
        return packageScanTotal;
    }

    public DiscardedWaybillScanResultItemDto setPackageScanTotal(Integer packageScanTotal) {
        this.packageScanTotal = packageScanTotal;
        return this;
    }

    public Integer getPackageNotScanTotal() {
        return packageNotScanTotal;
    }

    public DiscardedWaybillScanResultItemDto setPackageNotScanTotal(Integer packageNotScanTotal) {
        this.packageNotScanTotal = packageNotScanTotal;
        return this;
    }

    @Override
    public String toString() {
        return "DiscardedPackageScanResultItemDto{" +
                "id=" + id +
                ", waybillCode='" + waybillCode + '\'' +
                ", status=" + status +
                ", operatorErp='" + operatorErp + '\'' +
                ", operatorName='" + operatorName + '\'' +
                ", siteCode=" + siteCode +
                ", siteName='" + siteName + '\'' +
                ", operateType=" + operateType +
                ", waybillType=" + waybillType +
                ", siteDepartType=" + siteDepartType +
                ", submitStatus=" + submitStatus +
                ", packageSysTotal=" + packageSysTotal +
                ", inspectionNotSendTotal=" + inspectionNotSendTotal +
                ", packageScanTotal=" + packageScanTotal +
                ", packageNotScanTotal=" + packageNotScanTotal +
                '}';
    }
}
