package com.jd.bluedragon.distribution.discardedPackageStorageTemp.dto;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.io.Serializable;

/**
 * 查询已扫描未提交的记录
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-12-02 17:29:32 周四
 */
public class UnFinishScanDiscardedPackageQo extends BasePagerCondition implements Serializable {
    private static final long serialVersionUID = -3503018670538196586L;

    /**
     * 操作人erp
     */
    private String operatorErp;

    private String waybillCode;

    private String packageCode;

    /**
     * 提交状态
     */
    private Integer unSubmitStatus;

    private Integer pageSize;

    public UnFinishScanDiscardedPackageQo() {
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public UnFinishScanDiscardedPackageQo setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
        return this;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public UnFinishScanDiscardedPackageQo setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
        return this;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public UnFinishScanDiscardedPackageQo setPackageCode(String packageCode) {
        this.packageCode = packageCode;
        return this;
    }

    public Integer getUnSubmitStatus() {
        return unSubmitStatus;
    }

    public UnFinishScanDiscardedPackageQo setUnSubmitStatus(Integer unSubmitStatus) {
        this.unSubmitStatus = unSubmitStatus;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public UnFinishScanDiscardedPackageQo setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public String toString() {
        return "UnFinishScanDiscardedPackageQo{" +
                "operatorErp='" + operatorErp + '\'' +
                ", waybillCode='" + waybillCode + '\'' +
                ", packageCode='" + packageCode + '\'' +
                ", unSubmitStatus=" + unSubmitStatus +
                ", pageSize=" + pageSize +
                '}';
    }
}
