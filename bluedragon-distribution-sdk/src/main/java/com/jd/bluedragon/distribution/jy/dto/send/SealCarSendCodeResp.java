package com.jd.bluedragon.distribution.jy.dto.send;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 封车批次信息
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
