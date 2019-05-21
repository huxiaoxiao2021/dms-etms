package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.domain.SendCodeSummary;

/**
 * <P>
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/22
 */
public class SendCodeExceptionResponse {

    /** 装箱的包裹数（总） **/
    private Integer packageInBoxSum;

    /** 未装箱的包裹数（总） **/
    private Integer packageOutBoxSum;

    /** 总包裹数 **/
    private Integer packageSum;

    /** 已操作的装箱的包裹数 **/
    private Integer operatedPackageInBoxCount;

    /** 已操作的未装箱的包裹数 **/
    private Integer operatedPackageOutBoxCount;

    /** 已操作的总包裹数 **/
    private Integer operatedPackageCount;

    /** 未操作的装箱包裹数 **/
    private Integer unoperatedPackageInBoxCount;

    /** 未操作的未装箱的包裹数 **/
    private Integer unoperatedPackageOutBoxCount;

    /** 未操作的包裹总部 **/
    private Integer unoperatedPackageCount;

    /**
     * 总统计
     */
    private SendCodeSummary summary;

    /**
     * 已操作的统计
     */
    private SendCodeSummary operatedSummary;

    /**
     * 未操作统计
     */
    private SendCodeSummary unoperatedSummary;

    public Integer getPackageInBoxSum() {
        return packageInBoxSum;
    }

    public void setPackageInBoxSum(Integer packageInBoxSum) {
        this.packageInBoxSum = packageInBoxSum;
    }

    public Integer getPackageOutBoxSum() {
        return packageOutBoxSum;
    }

    public void setPackageOutBoxSum(Integer packageOutBoxSum) {
        this.packageOutBoxSum = packageOutBoxSum;
    }

    public Integer getPackageSum() {
        return packageSum;
    }

    public void setPackageSum(Integer packageSum) {
        this.packageSum = packageSum;
    }

    public Integer getOperatedPackageInBoxCount() {
        return operatedPackageInBoxCount;
    }

    public void setOperatedPackageInBoxCount(Integer operatedPackageInBoxCount) {
        this.operatedPackageInBoxCount = operatedPackageInBoxCount;
    }

    public Integer getOperatedPackageOutBoxCount() {
        return operatedPackageOutBoxCount;
    }

    public void setOperatedPackageOutBoxCount(Integer operatedPackageOutBoxCount) {
        this.operatedPackageOutBoxCount = operatedPackageOutBoxCount;
    }

    public Integer getOperatedPackageCount() {
        return operatedPackageCount;
    }

    public void setOperatedPackageCount(Integer operatedPackageCount) {
        this.operatedPackageCount = operatedPackageCount;
    }

    public Integer getUnoperatedPackageInBoxCount() {
        return unoperatedPackageInBoxCount;
    }

    public void setUnoperatedPackageInBoxCount(Integer unoperatedPackageInBoxCount) {
        this.unoperatedPackageInBoxCount = unoperatedPackageInBoxCount;
    }

    public Integer getUnoperatedPackageOutBoxCount() {
        return unoperatedPackageOutBoxCount;
    }

    public void setUnoperatedPackageOutBoxCount(Integer unoperatedPackageOutBoxCount) {
        this.unoperatedPackageOutBoxCount = unoperatedPackageOutBoxCount;
    }

    public Integer getUnoperatedPackageCount() {
        return unoperatedPackageCount;
    }

    public void setUnoperatedPackageCount(Integer unoperatedPackageCount) {
        this.unoperatedPackageCount = unoperatedPackageCount;
    }

    public SendCodeSummary getSummary() {
        return summary;
    }

    public void setSummary(SendCodeSummary summary) {
        this.summary = summary;
    }

    public SendCodeSummary getOperatedSummary() {
        return operatedSummary;
    }

    public void setOperatedSummary(SendCodeSummary operatedSummary) {
        this.operatedSummary = operatedSummary;
    }

    public SendCodeSummary getUnoperatedSummary() {
        return unoperatedSummary;
    }

    public void setUnoperatedSummary(SendCodeSummary unoperatedSummary) {
        this.unoperatedSummary = unoperatedSummary;
    }
}
