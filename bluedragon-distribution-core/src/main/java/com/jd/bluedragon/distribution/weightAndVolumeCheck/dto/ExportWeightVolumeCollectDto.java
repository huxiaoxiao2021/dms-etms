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

    private String reviewDate;
    private String waybillCode;
    private String packageCode;
    /**
     * 是否信任商家 0 - 否， 1 - 是
     */
    private String isTrustBusi;
    private String busiName;
    private String busiCode;
    private String reviewOrgCode;
    private String reviewOrgName;
    private String reviewSiteCode;
    private String reviewSiteName;
    private String reviewSubType;
    private String reviewErp;
    private String reviewWeight;
    private String reviewLWH;
    private String reviewVolume;
    private String billingOrgCode;
    private String billingOrgName;
    private String billingDeptCode;
    private String billingDeptName;
    private String billingErp;
    private String billingWeight;
    private String billingVolume;
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
    private String reviewVolumeWeight;
    private String billingVolumeWeight;
    /**
     * 体积重量是否超标
     */
    private String volumeWeightIsExcess;
    private String billingCompany;
    private String productTypeCode;
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
    private String waybillStatus;
    private String volumeRate;
    private String moreBigWeight;
    private String billingCalcWeight;
    private String billingWeightDifference;
    private String contrastSourceFrom;
    private String contrastLarge;
    private String largeDiff;

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
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

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public String getReviewOrgCode() {
        return reviewOrgCode;
    }

    public void setReviewOrgCode(String reviewOrgCode) {
        this.reviewOrgCode = reviewOrgCode;
    }

    public String getReviewOrgName() {
        return reviewOrgName;
    }

    public void setReviewOrgName(String reviewOrgName) {
        this.reviewOrgName = reviewOrgName;
    }

    public String getReviewSiteCode() {
        return reviewSiteCode;
    }

    public void setReviewSiteCode(String reviewSiteCode) {
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

    public String getReviewWeight() {
        return reviewWeight;
    }

    public void setReviewWeight(String reviewWeight) {
        this.reviewWeight = reviewWeight;
    }

    public String getReviewLWH() {
        return reviewLWH;
    }

    public void setReviewLWH(String reviewLWH) {
        this.reviewLWH = reviewLWH;
    }

    public String getReviewVolume() {
        return reviewVolume;
    }

    public void setReviewVolume(String reviewVolume) {
        this.reviewVolume = reviewVolume;
    }

    public String getBillingOrgCode() {
        return billingOrgCode;
    }

    public void setBillingOrgCode(String billingOrgCode) {
        this.billingOrgCode = billingOrgCode;
    }

    public String getBillingOrgName() {
        return billingOrgName;
    }

    public void setBillingOrgName(String billingOrgName) {
        this.billingOrgName = billingOrgName;
    }

    public String getBillingDeptCode() {
        return billingDeptCode;
    }

    public void setBillingDeptCode(String billingDeptCode) {
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

    public String getBillingWeight() {
        return billingWeight;
    }

    public void setBillingWeight(String billingWeight) {
        this.billingWeight = billingWeight;
    }

    public String getBillingVolume() {
        return billingVolume;
    }

    public void setBillingVolume(String billingVolume) {
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

    public String getReviewVolumeWeight() {
        return reviewVolumeWeight;
    }

    public void setReviewVolumeWeight(String reviewVolumeWeight) {
        this.reviewVolumeWeight = reviewVolumeWeight;
    }

    public String getBillingVolumeWeight() {
        return billingVolumeWeight;
    }

    public void setBillingVolumeWeight(String billingVolumeWeight) {
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

    public String getProductTypeCode() {
        return productTypeCode;
    }

    public void setProductTypeCode(String productTypeCode) {
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

    public String getWaybillStatus() {
        return waybillStatus;
    }

    public void setWaybillStatus(String waybillStatus) {
        this.waybillStatus = waybillStatus;
    }

    public String getVolumeRate() {
        return volumeRate;
    }

    public void setVolumeRate(String volumeRate) {
        this.volumeRate = volumeRate;
    }

    public String getMoreBigWeight() {
        return moreBigWeight;
    }

    public void setMoreBigWeight(String moreBigWeight) {
        this.moreBigWeight = moreBigWeight;
    }

    public String getBillingCalcWeight() {
        return billingCalcWeight;
    }

    public void setBillingCalcWeight(String billingCalcWeight) {
        this.billingCalcWeight = billingCalcWeight;
    }

    public String getBillingWeightDifference() {
        return billingWeightDifference;
    }

    public void setBillingWeightDifference(String billingWeightDifference) {
        this.billingWeightDifference = billingWeightDifference;
    }

    public String getContrastSourceFrom() {
        return contrastSourceFrom;
    }

    public void setContrastSourceFrom(String contrastSourceFrom) {
        this.contrastSourceFrom = contrastSourceFrom;
    }

    public String getContrastLarge() {
        return contrastLarge;
    }

    public void setContrastLarge(String contrastLarge) {
        this.contrastLarge = contrastLarge;
    }

    public String getLargeDiff() {
        return largeDiff;
    }

    public void setLargeDiff(String largeDiff) {
        this.largeDiff = largeDiff;
    }
}
