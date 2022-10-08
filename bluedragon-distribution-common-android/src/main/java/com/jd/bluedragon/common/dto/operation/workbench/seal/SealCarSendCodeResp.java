package com.jd.bluedragon.common.dto.operation.workbench.seal;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 封车批次信息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-09-27 14:21:22 周二
 */
public class SealCarSendCodeResp implements Serializable {
    private static final long serialVersionUID = 4073173708374568073L;

    private String batchCode;

    private BigDecimal packageWeightTotal;

    private BigDecimal packageVolumeTotal;

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public BigDecimal getPackageWeightTotal() {
        return packageWeightTotal;
    }

    public void setPackageWeightTotal(BigDecimal packageWeightTotal) {
        this.packageWeightTotal = packageWeightTotal;
    }

    public BigDecimal getPackageVolumeTotal() {
        return packageVolumeTotal;
    }

    public void setPackageVolumeTotal(BigDecimal packageVolumeTotal) {
        this.packageVolumeTotal = packageVolumeTotal;
    }
}
