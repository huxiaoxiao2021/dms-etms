package com.jd.bluedragon.distribution.half.domain;

import java.io.Serializable;

/**
 * 包裹半收协商再投审核之包裹明细
 */
public class PackageHalfApproveDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运单包裹号
     */
    private String packageCode;

    /**
     * 运单包裹状态（妥投150拒收160再投待审核550待再投570待拒收580待报废590）
     */
    private Integer packageState;

    /**
     * 操作描述
     */
    private String remark;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getPackageState() {
        return packageState;
    }

    public void setPackageState(Integer packageState) {
        this.packageState = packageState;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
