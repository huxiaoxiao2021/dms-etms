package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.util.List;

public class CancelSendWaybillDto implements Serializable {

    private static final long serialVersionUID = 3813878446640603317L;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 运单取消的包裹总数
     */
    private Integer cancelPackageCount;

    /**
     * 运单包裹总数
     */
    private Integer totalPackageCount;

    /**
     * 取消的包裹号列表
     */
    private List<String> packageCodes;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getCancelPackageCount() {
        return cancelPackageCount;
    }

    public void setCancelPackageCount(Integer cancelPackageCount) {
        this.cancelPackageCount = cancelPackageCount;
    }

    public Integer getTotalPackageCount() {
        return totalPackageCount;
    }

    public void setTotalPackageCount(Integer totalPackageCount) {
        this.totalPackageCount = totalPackageCount;
    }

    public List<String> getPackageCodes() {
        return packageCodes;
    }

    public void setPackageCodes(List<String> packageCodes) {
        this.packageCodes = packageCodes;
    }
}
