package com.jd.bluedragon.distribution.discardedPackageStorageTemp.vo;

import java.io.Serializable;

import com.jd.bluedragon.distribution.discardedPackageStorageTemp.model.DiscardedPackageStorageTemp;

/**
 * Description: 快递弃件暂存<br>
 * Copyright: Copyright (c) 2020<br>
 * Company: jd.com 京东物流JDL<br>
 *
 * @author fanggang7
 * @time 2021-03-31 11:32:59 周三
 */
public class DiscardedPackageStorageTempVo extends DiscardedPackageStorageTemp implements Serializable {

    private static final long serialVersionUID = -7351141695878504065L;

    /**
     * 首次扫描日期
     */
    private String firstScanTimeFormative;

    /**
     * 是否cod
     */
    private String isCodStr;

    /**
     * 状态
     */
    private String statusStr;

    /**
     * 最后操作时间
     */
    private String lastOperateTimeFormative;

    /**
     * 存已储天数
     */
    private Integer storageDays;
    /**
     * 操作类型-描述
     */
    private String operateTypeDesc;
    /**
     * 运单类型-描述
     */
    private String waybillTypeDesc;

    /**
     * 场地部门类型
     */
    private String siteDepartTypeDesc;

    /**
     * 系统包裹总数
     */
    private Integer packageSysTotal;
    /**
     * 已扫包裹数
     */
    private Integer packageScanTotal;

    /**
     * 提交状态
     */
    private Integer submitStatus;

    public String getFirstScanTimeFormative() {
        return firstScanTimeFormative;
    }

    public void setFirstScanTimeFormative(String firstScanTimeFormative) {
        this.firstScanTimeFormative = firstScanTimeFormative;
    }

    public String getIsCodStr() {
        return isCodStr;
    }

    public void setIsCodStr(String isCodStr) {
        this.isCodStr = isCodStr;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getLastOperateTimeFormative() {
        return lastOperateTimeFormative;
    }

    public void setLastOperateTimeFormative(String lastOperateTimeFormative) {
        this.lastOperateTimeFormative = lastOperateTimeFormative;
    }

    public Integer getStorageDays() {
        return storageDays;
    }

    public void setStorageDays(Integer storageDays) {
        this.storageDays = storageDays;
    }

    public String getOperateTypeDesc() {
        return operateTypeDesc;
    }

    public void setOperateTypeDesc(String operateTypeDesc) {
        this.operateTypeDesc = operateTypeDesc;
    }

    public String getWaybillTypeDesc() {
        return waybillTypeDesc;
    }

    public void setWaybillTypeDesc(String waybillTypeDesc) {
        this.waybillTypeDesc = waybillTypeDesc;
    }

    public String getSiteDepartTypeDesc() {
        return siteDepartTypeDesc;
    }

    public DiscardedPackageStorageTempVo setSiteDepartTypeDesc(String siteDepartTypeDesc) {
        this.siteDepartTypeDesc = siteDepartTypeDesc;
        return this;
    }

    public Integer getPackageSysTotal() {
        return packageSysTotal;
    }

    public DiscardedPackageStorageTempVo setPackageSysTotal(Integer packageSysTotal) {
        this.packageSysTotal = packageSysTotal;
        return this;
    }

    public Integer getPackageScanTotal() {
        return packageScanTotal;
    }

    public DiscardedPackageStorageTempVo setPackageScanTotal(Integer packageScanTotal) {
        this.packageScanTotal = packageScanTotal;
        return this;
    }

    public Integer getSubmitStatus() {
        return submitStatus;
    }

    public DiscardedPackageStorageTempVo setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
        return this;
    }

    @Override
    public String toString() {
        return "DiscardedPackageStorageTempVo{" +
                "firstScanTimeFormative='" + firstScanTimeFormative + '\'' +
                ", isCodStr='" + isCodStr + '\'' +
                ", statusStr='" + statusStr + '\'' +
                ", lastOperateTimeFormative='" + lastOperateTimeFormative + '\'' +
                ", storageDays=" + storageDays +
                ", operateTypeDesc='" + operateTypeDesc + '\'' +
                ", waybillTypeDesc='" + waybillTypeDesc + '\'' +
                ", siteDepartTypeDesc='" + siteDepartTypeDesc + '\'' +
                ", packageSysTotal=" + packageSysTotal +
                ", packageScanTotal=" + packageScanTotal +
                ", submitStatus=" + submitStatus +
                '}';
    }
}
