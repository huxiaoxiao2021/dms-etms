package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/13 10:02
 */
public class ReviewWeightSpotCheckExportDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 复核日期 */
    private String reviewDate;
    /** 复核区域 */
    private String reviewOrgName;
    /** 机构类型：1-分拣中心;0-转运中心 */
    private String reviewMechanismType;
    /** 机构编码 */
    private Integer reviewSiteCode;
    /** 机构名称 */
    private String reviewSiteName;
    /** 普通应抽查运单数 */
    private Integer normalPackageNum;
    /** 普通实际抽查运单数 */
    private Integer normalPackageNumOfActual;
    /** 普通抽查率 */
    private String normalCheckRate;
    /** 普通抽查差异运单数 */
    private Integer normalPackageNumOfDiff;
    /** 普通抽查差异率 */
    private String normalCheckRateOfDiff;
    /** 信任商家应抽查运单数 */
    private Integer trustPackageNum;
    /** 信任商家实际抽查运单数 */
    private Integer trustPackageNumOfActual;
    /** 信任商家抽查率 */
    private String trustCheckRate;
    /** 信任商家抽查差异运单数 */
    private Integer trustPackageNumOfDiff;
    /** 信任商家抽查差异率 */
    private String trustCheckRateOfDiff;
    /** 总抽查率 */
    private String totalCheckRate;
    /** 业务类型 */
    private String spotCheckType;


    public String getReviewMechanismType() {
        return reviewMechanismType;
    }

    public void setReviewMechanismType(String reviewMechanismType) {
        this.reviewMechanismType = reviewMechanismType;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewOrgName() {
        return reviewOrgName;
    }

    public void setReviewOrgName(String reviewOrgName) {
        this.reviewOrgName = reviewOrgName;
    }


    public Integer getReviewSiteCode() {
        return reviewSiteCode;
    }

    public void setReviewSiteCode(Integer reviewSiteCode) {
        this.reviewSiteCode = reviewSiteCode;
    }

    public String getReviewSiteName() {
        return reviewSiteName;
    }

    public void setReviewSiteName(String reviewSiteName) {
        this.reviewSiteName = reviewSiteName;
    }

    public Integer getNormalPackageNum() {
        return normalPackageNum;
    }

    public void setNormalPackageNum(Integer normalPackageNum) {
        this.normalPackageNum = normalPackageNum;
    }

    public Integer getNormalPackageNumOfActual() {
        return normalPackageNumOfActual;
    }

    public void setNormalPackageNumOfActual(Integer normalPackageNumOfActual) {
        this.normalPackageNumOfActual = normalPackageNumOfActual;
    }

    public String getNormalCheckRate() {
        return normalCheckRate;
    }

    public void setNormalCheckRate(String normalCheckRate) {
        this.normalCheckRate = normalCheckRate;
    }

    public Integer getNormalPackageNumOfDiff() {
        return normalPackageNumOfDiff;
    }

    public void setNormalPackageNumOfDiff(Integer normalPackageNumOfDiff) {
        this.normalPackageNumOfDiff = normalPackageNumOfDiff;
    }

    public String getNormalCheckRateOfDiff() {
        return normalCheckRateOfDiff;
    }

    public void setNormalCheckRateOfDiff(String normalCheckRateOfDiff) {
        this.normalCheckRateOfDiff = normalCheckRateOfDiff;
    }

    public Integer getTrustPackageNum() {
        return trustPackageNum;
    }

    public void setTrustPackageNum(Integer trustPackageNum) {
        this.trustPackageNum = trustPackageNum;
    }

    public Integer getTrustPackageNumOfActual() {
        return trustPackageNumOfActual;
    }

    public void setTrustPackageNumOfActual(Integer trustPackageNumOfActual) {
        this.trustPackageNumOfActual = trustPackageNumOfActual;
    }

    public String getTrustCheckRate() {
        return trustCheckRate;
    }

    public void setTrustCheckRate(String trustCheckRate) {
        this.trustCheckRate = trustCheckRate;
    }

    public Integer getTrustPackageNumOfDiff() {
        return trustPackageNumOfDiff;
    }

    public void setTrustPackageNumOfDiff(Integer trustPackageNumOfDiff) {
        this.trustPackageNumOfDiff = trustPackageNumOfDiff;
    }

    public String getTrustCheckRateOfDiff() {
        return trustCheckRateOfDiff;
    }

    public void setTrustCheckRateOfDiff(String trustCheckRateOfDiff) {
        this.trustCheckRateOfDiff = trustCheckRateOfDiff;
    }

    public String getTotalCheckRate() {
        return totalCheckRate;
    }

    public void setTotalCheckRate(String totalCheckRate) {
        this.totalCheckRate = totalCheckRate;
    }

    public String getSpotCheckType() {
        return spotCheckType;
    }

    public void setSpotCheckType(String spotCheckType) {
        this.spotCheckType = spotCheckType;
    }
}
    
