package com.jd.bluedragon.distribution.weightAndVolumeCheck.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 抽检导出类对象
 *
 * @author hujiping
 * @date 2021/3/30 5:23 下午
 */
public class ExportWeightVolumeCollectDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date reviewDate;
    private String waybillCode;
    private String packageCode;
    /**
     * 是否信任商家 0 - 否， 1 - 是
     */
    private String isTrustBusi;
    private String busiName;
    private Integer busiCode;
    private Integer reviewOrgCode;
    private String reviewOrgName;
    private Integer reviewSiteCode;
    private String reviewSiteName;
    private String reviewSubType;
    private String reviewErp;
    private Double reviewWeight;
    private String reviewLWH;
    private Double reviewVolume;
    private Integer billingOrgCode;
    private String billingOrgName;
    private Integer billingDeptCode;
    private String billingDeptName;
    private String billingErp;
    private Double billingWeight;
    private Double billingVolume;
    private String weightDiff;
    private String volumeWeightDiff;
    private String diffStandard;
    /**
     * 是否超标，1-超标，0-不超标
     */
    private String isExcess;
    /**
     * 有无图片，1-有，0-无
     */
    private String isHasPicture;
    private String pictureAddress;
    private Double reviewVolumeWeight;
    private Double billingVolumeWeight;
    /**
     * 体积重量是否超标
     */
    private String volumeWeightIsExcess;
    private String billingCompany;
    private Integer productTypeCode;
    private String productTypeName;
    /**
     * 抽检类型
     */
    private String spotCheckType;
    /**
     * 是否运单维度抽检
     */
    private String isWaybillSpotCheck;
    private String billingDeptCodeStr;
    /**
     * 数据来源
     */
    private String fromSource;
    private String merchantCode;
    private Integer waybillStatus;
    private Integer volumeRate;
    private Double moreBigWeight;
    private Double billingCalcWeight;
    private Double billingWeightDifference;

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getIsTrustBusi() {
        return isTrustBusi;
    }

    public void setIsTrustBusi(String isTrustBusi) {
        this.isTrustBusi = isTrustBusi;
    }

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public Integer getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(Integer busiCode) {
        this.busiCode = busiCode;
    }

    public Integer getReviewOrgCode() {
        return reviewOrgCode;
    }

    public void setReviewOrgCode(Integer reviewOrgCode) {
        this.reviewOrgCode = reviewOrgCode;
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

    public String getReviewSubType() {
        return reviewSubType;
    }

    public void setReviewSubType(String reviewSubType) {
        this.reviewSubType = reviewSubType;
    }

    public String getReviewErp() {
        return reviewErp;
    }

    public void setReviewErp(String reviewErp) {
        this.reviewErp = reviewErp;
    }

    public Double getReviewWeight() {
        return reviewWeight;
    }

    public void setReviewWeight(Double reviewWeight) {
        this.reviewWeight = reviewWeight;
    }

    public String getReviewLWH() {
        return reviewLWH;
    }

    public void setReviewLWH(String reviewLWH) {
        this.reviewLWH = reviewLWH;
    }

    public Double getReviewVolume() {
        return reviewVolume;
    }

    public void setReviewVolume(Double reviewVolume) {
        this.reviewVolume = reviewVolume;
    }

    public Integer getBillingOrgCode() {
        return billingOrgCode;
    }

    public void setBillingOrgCode(Integer billingOrgCode) {
        this.billingOrgCode = billingOrgCode;
    }

    public String getBillingOrgName() {
        return billingOrgName;
    }

    public void setBillingOrgName(String billingOrgName) {
        this.billingOrgName = billingOrgName;
    }

    public Integer getBillingDeptCode() {
        return billingDeptCode;
    }

    public void setBillingDeptCode(Integer billingDeptCode) {
        this.billingDeptCode = billingDeptCode;
    }

    public String getBillingDeptName() {
        return billingDeptName;
    }

    public void setBillingDeptName(String billingDeptName) {
        this.billingDeptName = billingDeptName;
    }

    public String getBillingErp() {
        return billingErp;
    }

    public void setBillingErp(String billingErp) {
        this.billingErp = billingErp;
    }

    public Double getBillingWeight() {
        return billingWeight;
    }

    public void setBillingWeight(Double billingWeight) {
        this.billingWeight = billingWeight;
    }

    public Double getBillingVolume() {
        return billingVolume;
    }

    public void setBillingVolume(Double billingVolume) {
        this.billingVolume = billingVolume;
    }

    public String getWeightDiff() {
        return weightDiff;
    }

    public void setWeightDiff(String weightDiff) {
        this.weightDiff = weightDiff;
    }

    public String getVolumeWeightDiff() {
        return volumeWeightDiff;
    }

    public void setVolumeWeightDiff(String volumeWeightDiff) {
        this.volumeWeightDiff = volumeWeightDiff;
    }

    public String getDiffStandard() {
        return diffStandard;
    }

    public void setDiffStandard(String diffStandard) {
        this.diffStandard = diffStandard;
    }

    public String getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(String isExcess) {
        this.isExcess = isExcess;
    }

    public String getIsHasPicture() {
        return isHasPicture;
    }

    public void setIsHasPicture(String isHasPicture) {
        this.isHasPicture = isHasPicture;
    }

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    public Double getReviewVolumeWeight() {
        return reviewVolumeWeight;
    }

    public void setReviewVolumeWeight(Double reviewVolumeWeight) {
        this.reviewVolumeWeight = reviewVolumeWeight;
    }

    public Double getBillingVolumeWeight() {
        return billingVolumeWeight;
    }

    public void setBillingVolumeWeight(Double billingVolumeWeight) {
        this.billingVolumeWeight = billingVolumeWeight;
    }

    public String getVolumeWeightIsExcess() {
        return volumeWeightIsExcess;
    }

    public void setVolumeWeightIsExcess(String volumeWeightIsExcess) {
        this.volumeWeightIsExcess = volumeWeightIsExcess;
    }

    public String getBillingCompany() {
        return billingCompany;
    }

    public void setBillingCompany(String billingCompany) {
        this.billingCompany = billingCompany;
    }

    public Integer getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(Integer productTypeCode) {
        this.productTypeCode = productTypeCode;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getSpotCheckType() {
        return spotCheckType;
    }

    public void setSpotCheckType(String spotCheckType) {
        this.spotCheckType = spotCheckType;
    }

    public String getIsWaybillSpotCheck() {
        return isWaybillSpotCheck;
    }

    public void setIsWaybillSpotCheck(String isWaybillSpotCheck) {
        this.isWaybillSpotCheck = isWaybillSpotCheck;
    }

    public String getBillingDeptCodeStr() {
        return billingDeptCodeStr;
    }

    public void setBillingDeptCodeStr(String billingDeptCodeStr) {
        this.billingDeptCodeStr = billingDeptCodeStr;
    }

    public String getFromSource() {
        return fromSource;
    }

    public void setFromSource(String fromSource) {
        this.fromSource = fromSource;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public Integer getWaybillStatus() {
        return waybillStatus;
    }

    public void setWaybillStatus(Integer waybillStatus) {
        this.waybillStatus = waybillStatus;
    }

    public Integer getVolumeRate() {
        return volumeRate;
    }

    public void setVolumeRate(Integer volumeRate) {
        this.volumeRate = volumeRate;
    }

    public Double getMoreBigWeight() {
        return moreBigWeight;
    }

    public void setMoreBigWeight(Double moreBigWeight) {
        this.moreBigWeight = moreBigWeight;
    }

    public Double getBillingCalcWeight() {
        return billingCalcWeight;
    }

    public void setBillingCalcWeight(Double billingCalcWeight) {
        this.billingCalcWeight = billingCalcWeight;
    }

    public Double getBillingWeightDifference() {
        return billingWeightDifference;
    }

    public void setBillingWeightDifference(Double billingWeightDifference) {
        this.billingWeightDifference = billingWeightDifference;
    }
}
