package com.jd.bluedragon.distribution.spotcheck.domain;

import com.jd.bluedragon.distribution.spotcheck.enums.ExcessStatusEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckDimensionEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckBusinessTypeEnum;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

import java.io.Serializable;
import java.util.Date;

/**
 * 抽检上下文
 *
 * @author hujiping
 * @date 2021/8/10 2:35 下午
 */
public class SpotCheckContext implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 抽检来源
     * @see SpotCheckSourceFromEnum
     */
    private String spotCheckSourceFrom;
    /**
     * 抽检业务类型
     * @see SpotCheckBusinessTypeEnum
     */
    private Integer spotCheckBusinessType;
    /**
     * 抽检维度类型
     * @see SpotCheckDimensionEnum
     */
    private Integer spotCheckDimensionType;
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 运单对象
     */
    private Waybill waybill;
    /**
     * 运单包裹数量
     */
    private Integer packNum;
    /**
     * 是否一单多件
     */
    private boolean isMultiPack = false;
    /**
     * 是否集齐
     */
    private boolean isGatherTogether = false;
    /**
     * 操作时间
     */
    private Date operateTime;
    /**
     * 是否信任商家
     *  1：信任
     *  2；非信任
     */
    private Boolean isTrustMerchant;
    /**
     * 商家编码
     */
    private String merchantCode;
    /**
     * 商家名称
     */
    private String merchantName;
    /**
     * 操作站点站点
     */
    private Integer reviewSiteCode;
    /**
     * 站点
     */
    private BaseStaffSiteOrgDto reviewSite;
    /**
     * 产品标识编码
     */
    private Integer productTypeCode;
    /**
     * 产品标识
     */
    private String productTypeName;
    /**
     * 计泡系数
     */
    private Integer volumeRate;
    /**
     * 复核对象
     */
    private SpotCheckReviewDetail spotCheckReviewDetail;
    /**
     * 核对对象
     */
    private SpotCheckContrastDetail spotCheckContrastDetail;

    /**
     * 是否超标
     * @see ExcessStatusEnum
     */
    private Integer excessStatus;
    /**
     * 超标原因
     */
    private String excessReason;
    /**
     * 误差标准值
     */
    private String diffStandard;
    /**
     * 是否有图片
     * 1: 有
     * 0: 无
     */
    private Integer isHasPicture;
    /**
     * 图片链接
     */
    private String pictureAddress;

    public String getSpotCheckSourceFrom() {
        return spotCheckSourceFrom;
    }

    public void setSpotCheckSourceFrom(String spotCheckSourceFrom) {
        this.spotCheckSourceFrom = spotCheckSourceFrom;
    }

    public Integer getSpotCheckBusinessType() {
        return spotCheckBusinessType;
    }

    public void setSpotCheckBusinessType(Integer spotCheckBusinessType) {
        this.spotCheckBusinessType = spotCheckBusinessType;
    }

    public Integer getSpotCheckDimensionType() {
        return spotCheckDimensionType;
    }

    public void setSpotCheckDimensionType(Integer spotCheckDimensionType) {
        this.spotCheckDimensionType = spotCheckDimensionType;
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

    public Waybill getWaybill() {
        return waybill;
    }

    public void setWaybill(Waybill waybill) {
        this.waybill = waybill;
    }

    public Integer getPackNum() {
        return packNum;
    }

    public void setPackNum(Integer packNum) {
        this.packNum = packNum;
    }

    public boolean getIsMultiPack() {
        return isMultiPack;
    }

    public void setIsMultiPack(boolean multiPack) {
        this.isMultiPack = multiPack;
    }

    public boolean getIsGatherTogether() {
        return isGatherTogether;
    }

    public void setIsGatherTogether(boolean gatherTogether) {
        isGatherTogether = gatherTogether;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Boolean getIsTrustMerchant() {
        return isTrustMerchant;
    }

    public void setIsTrustMerchant(Boolean trustMerchant) {
        isTrustMerchant = trustMerchant;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Integer getReviewSiteCode() {
        return reviewSiteCode;
    }

    public void setReviewSiteCode(Integer reviewSiteCode) {
        this.reviewSiteCode = reviewSiteCode;
    }

    public BaseStaffSiteOrgDto getReviewSite() {
        return reviewSite;
    }

    public void setReviewSite(BaseStaffSiteOrgDto reviewSite) {
        this.reviewSite = reviewSite;
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

    public Integer getVolumeRate() {
        return volumeRate;
    }

    public void setVolumeRate(Integer volumeRate) {
        this.volumeRate = volumeRate;
    }

    public SpotCheckReviewDetail getSpotCheckReviewDetail() {
        return spotCheckReviewDetail;
    }

    public void setSpotCheckReviewDetail(SpotCheckReviewDetail spotCheckReviewDetail) {
        this.spotCheckReviewDetail = spotCheckReviewDetail == null ? new SpotCheckReviewDetail() : spotCheckReviewDetail;
    }

    public SpotCheckContrastDetail getSpotCheckContrastDetail() {
        return spotCheckContrastDetail == null ? new SpotCheckContrastDetail() : spotCheckContrastDetail;
    }

    public void setSpotCheckContrastDetail(SpotCheckContrastDetail spotCheckContrastDetail) {
        this.spotCheckContrastDetail = spotCheckContrastDetail;
    }

    public Integer getExcessStatus() {
        return excessStatus;
    }

    public void setExcessStatus(Integer excessStatus) {
        this.excessStatus = excessStatus;
    }

    public String getExcessReason() {
        return excessReason;
    }

    public void setExcessReason(String excessReason) {
        this.excessReason = excessReason;
    }

    public String getDiffStandard() {
        return diffStandard;
    }

    public void setDiffStandard(String diffStandard) {
        this.diffStandard = diffStandard;
    }

    public Integer getIsHasPicture() {
        return isHasPicture;
    }

    public void setIsHasPicture(Integer isHasPicture) {
        this.isHasPicture = isHasPicture;
    }

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }
}
