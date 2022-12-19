package com.jd.bluedragon.distribution.integral.domain;


import java.math.BigDecimal;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/14
 */
public class JyQuotaCoefficientDetailDTO {
    private String quotaNo;
    private String quotaName;
    private BigDecimal coefficient;

    public String getQuotaNo() {
        return quotaNo;
    }

    public void setQuotaNo(String quotaNo) {
        this.quotaNo = quotaNo;
    }

    public String getQuotaName() {
        return quotaName;
    }

    public void setQuotaName(String quotaName) {
        this.quotaName = quotaName;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }
}
