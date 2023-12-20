package com.jd.bluedragon.distribution.spotcheck.domain;

import java.io.Serializable;

/**
 * 抽检实体
 *
 * @author hujiping
 * @date 2021/12/2 9:55 下午
 */
public class SpotCheckIssueDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 复核日期
     */
    private Long reviewDate;
    /**
     * 运单号
     */
    private String waybillCode;
    /**
     * 包裹号
     */
    private String packageCode;
    /**
     * 商家配送编码
     */
    private String merchantCode;
    /**
     * 商家名称
     */
    private String merchantName;
    /**
     * 是否是信任商家：1：信任 0：非信任
     */
    private Integer isTrustMerchant;

    @Deprecated
    private Integer volumeRate;
    /**
     * 计泡系数
     */
    private String volumeRateStr;
    /**
     * 产品类型标识
     */
    private String productTypeName;
    /**
     * 场地类型：1：分拣 2：转运
     */
    private Integer siteTypeName;
    /**
     * 复核区域ID
     */
    private Integer reviewOrgCode;
    /**
     * 复核区域名称
     */
    private String reviewOrgName;
    /**
     * 复核省区编码
     */
    private String reviewProvinceAgencyCode;
    /**
     * 复核省区名称
     */
    private String reviewProvinceAgencyName;
    /**
     * 复核枢纽编码
     */
    private String reviewAreaHubCode;
    /**
     * 复核枢纽名称
     */
    private String reviewAreaHubName;
    /**
     * 复核站点ID
     */
    private Integer reviewSiteCode;
    /**
     * 复核站点名称
     */
    private String reviewSiteName;
    /**
     * 复核人ERP
     */
    private String reviewUserErp;
    /**
     * 复核人名称
     */
    private String reviewUserName;
    /**
     * 设备编码
     */
    private String machineCode;
    /**
     * 设备状态
     */
    private Integer machineStatus;
    /**
     * 复核重量：kg
     */
    private Double reviewWeight;
    /**
     * 复核长
     */
    private Double reviewLength;
    /**
     * 复核宽
     */
    private Double reviewWidth;
    /**
     * 复核高
     */
    private Double reviewHeight;
    /**
     * 复核体积：cm³
     */
    private Double reviewVolume;
    /**
     * 复核长宽高
     */
    private String reviewLWH;
    /**
     * 复核来源
     */
    private Integer reviewSource;
    /**
     * 核对来源
     */
    private Integer contrastSource;
    /**
     * 核对区域编码
     */
    private Integer contrastOrgCode;
    /**
     * 核对区域名称
     */
    private String contrastOrgName;
    /**
     * 核对战区编码
     */
    private String contrastWarZoneCode;
    /**
     * 核对战区名称
     */
    private String contrastWarZoneName;
    /**
     * 核对片区编码
     */
    private String contrastAreaCode;
    /**
     * 核对片区名称
     */
    private String contrastAreaName;
    /**
     * 核对省区编码
     */
    private String contrastProvinceAgencyCode;
    /**
     * 核对省区名称
     */
    private String contrastProvinceAgencyName;
    /**
     * 核对站点ID
     */
    private Integer contrastSiteCode;
    /**
     * 核对站点名称
     */
    private String contrastSiteName;
    /**
     * 核对责任人账号 erp 或 pin
     */
    private String contrastStaffAccount;
    /**
     * 核对责任人名称
     */
    private String contrastStaffName;
    /**
     * 核对责任人账号类型 1：自营 2：三方
     */
    private Integer contrastStaffType;
    /**
     * 核对责任类型
     */
    private Integer contrastDutyType;
    /**
     * 核对重量：kg
     */
    private Double contrastWeight;
    /**
     * 核对体积：cm³
     */
    private Double contrastVolume;
    /**
     * 重量差异
     */
    private Double diffWeight;
    /**
     * 差异标准值
     */
    private String diffStandard;
    /**
     * 是否多包裹：1: 是 0: 否
     */
    private Integer isMultiPack;
    /**
     * 是否有图片：1：有 0：无
     */
    private Integer isHasPicture;
    /**
     * 图片链接
     */
    private String pictureAddress;
    /**
     * 图片是否合格：1：合格 0：不合格
     */
    private Integer picIsQualify;
    /**
     * AI图片识别异常原因
     */
    private String pictureAIDistinguishReason;
    /**
     * 是否有视频：1：有 0：无
     */
    private Integer isHasVideo;
    /**
     * 视频链接
     */
    private String videoPicture;
    /**
     * 业务类型：1：B网 0：C网
     */
    private Integer businessType;
    /**
     * 维度类型：1：运单维度抽检 0：包裹维度抽检
     */
    private Integer dimensionType;
    /**
     * 记录类型: 1：汇总记录，0：单个明细记录
     */
    private Integer recordType;
    /**
     * 是否集齐: 1：集齐 0：未集齐
     */
    private Integer isGatherTogether;
    /**
     * 是否超标: 1：超标 0：未超标 2：集齐待计算
     */
    private Integer isExcess;
    /**
     * 超标类型（由超标计算系统提供）
     */
    private Integer excessType;
    /**
     * 是否下发: 1：已下发 0：未下发
     */
    private Integer isIssueDownstream;
    /**
     * 超标状态: 1：待核实 2：认责 3：超时认责 4：系统认责 5：升级判责 6：判责有效 7：判责无效 8：处理完成
     */
    private Integer spotCheckStatus;
    /**
     * 手动上传称重
     */
    private Integer manualUploadWeight;
    /**
     * 更新时间戳
     */
    private Long updateTime;
    /**
     * 时间戳
     */
    private Long ts;
    /**
     * 是否有效
     */
    private Integer yn;
    /**
     * 二级超标类型（由超标计算系统提供）：1-计费超标，2-非计费超标
     */
    private Integer excessSubType;
    /**
     * 扩展字段 <a href="https://joyspace.jd.com/pages/ILE90taNyoY9AYFQ8cWy">...</a>
     */
    private String extendMap;

    public Long getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Long reviewDate) {
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

    public Integer getIsTrustMerchant() {
        return isTrustMerchant;
    }

    public void setIsTrustMerchant(Integer isTrustMerchant) {
        this.isTrustMerchant = isTrustMerchant;
    }

    public Integer getVolumeRate() {
        return volumeRate;
    }

    public void setVolumeRate(Integer volumeRate) {
        this.volumeRate = volumeRate;
    }

    public String getVolumeRateStr() {
        return volumeRateStr;
    }

    public void setVolumeRateStr(String volumeRateStr) {
        this.volumeRateStr = volumeRateStr;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public Integer getSiteTypeName() {
        return siteTypeName;
    }

    public void setSiteTypeName(Integer siteTypeName) {
        this.siteTypeName = siteTypeName;
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

    public String getReviewProvinceAgencyCode() {
        return reviewProvinceAgencyCode;
    }

    public void setReviewProvinceAgencyCode(String reviewProvinceAgencyCode) {
        this.reviewProvinceAgencyCode = reviewProvinceAgencyCode;
    }

    public String getReviewProvinceAgencyName() {
        return reviewProvinceAgencyName;
    }

    public void setReviewProvinceAgencyName(String reviewProvinceAgencyName) {
        this.reviewProvinceAgencyName = reviewProvinceAgencyName;
    }

    public String getReviewAreaHubCode() {
        return reviewAreaHubCode;
    }

    public void setReviewAreaHubCode(String reviewAreaHubCode) {
        this.reviewAreaHubCode = reviewAreaHubCode;
    }

    public String getReviewAreaHubName() {
        return reviewAreaHubName;
    }

    public void setReviewAreaHubName(String reviewAreaHubName) {
        this.reviewAreaHubName = reviewAreaHubName;
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

    public String getReviewUserErp() {
        return reviewUserErp;
    }

    public void setReviewUserErp(String reviewUserErp) {
        this.reviewUserErp = reviewUserErp;
    }

    public String getReviewUserName() {
        return reviewUserName;
    }

    public void setReviewUserName(String reviewUserName) {
        this.reviewUserName = reviewUserName;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public Integer getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(Integer machineStatus) {
        this.machineStatus = machineStatus;
    }

    public Double getReviewWeight() {
        return reviewWeight;
    }

    public void setReviewWeight(Double reviewWeight) {
        this.reviewWeight = reviewWeight;
    }

    public Double getReviewLength() {
        return reviewLength;
    }

    public void setReviewLength(Double reviewLength) {
        this.reviewLength = reviewLength;
    }

    public Double getReviewWidth() {
        return reviewWidth;
    }

    public void setReviewWidth(Double reviewWidth) {
        this.reviewWidth = reviewWidth;
    }

    public Double getReviewHeight() {
        return reviewHeight;
    }

    public void setReviewHeight(Double reviewHeight) {
        this.reviewHeight = reviewHeight;
    }

    public Double getReviewVolume() {
        return reviewVolume;
    }

    public void setReviewVolume(Double reviewVolume) {
        this.reviewVolume = reviewVolume;
    }

    public String getReviewLWH() {
        return reviewLWH;
    }

    public void setReviewLWH(String reviewLWH) {
        this.reviewLWH = reviewLWH;
    }

    public Integer getReviewSource() {
        return reviewSource;
    }

    public void setReviewSource(Integer reviewSource) {
        this.reviewSource = reviewSource;
    }

    public Integer getContrastSource() {
        return contrastSource;
    }

    public void setContrastSource(Integer contrastSource) {
        this.contrastSource = contrastSource;
    }

    public Integer getContrastOrgCode() {
        return contrastOrgCode;
    }

    public void setContrastOrgCode(Integer contrastOrgCode) {
        this.contrastOrgCode = contrastOrgCode;
    }

    public String getContrastOrgName() {
        return contrastOrgName;
    }

    public void setContrastOrgName(String contrastOrgName) {
        this.contrastOrgName = contrastOrgName;
    }

    public String getContrastWarZoneCode() {
        return contrastWarZoneCode;
    }

    public void setContrastWarZoneCode(String contrastWarZoneCode) {
        this.contrastWarZoneCode = contrastWarZoneCode;
    }

    public String getContrastWarZoneName() {
        return contrastWarZoneName;
    }

    public void setContrastWarZoneName(String contrastWarZoneName) {
        this.contrastWarZoneName = contrastWarZoneName;
    }

    public String getContrastAreaCode() {
        return contrastAreaCode;
    }

    public void setContrastAreaCode(String contrastAreaCode) {
        this.contrastAreaCode = contrastAreaCode;
    }

    public String getContrastAreaName() {
        return contrastAreaName;
    }

    public void setContrastAreaName(String contrastAreaName) {
        this.contrastAreaName = contrastAreaName;
    }

    public String getContrastProvinceAgencyCode() {
        return contrastProvinceAgencyCode;
    }

    public void setContrastProvinceAgencyCode(String contrastProvinceAgencyCode) {
        this.contrastProvinceAgencyCode = contrastProvinceAgencyCode;
    }

    public String getContrastProvinceAgencyName() {
        return contrastProvinceAgencyName;
    }

    public void setContrastProvinceAgencyName(String contrastProvinceAgencyName) {
        this.contrastProvinceAgencyName = contrastProvinceAgencyName;
    }

    public Integer getContrastSiteCode() {
        return contrastSiteCode;
    }

    public void setContrastSiteCode(Integer contrastSiteCode) {
        this.contrastSiteCode = contrastSiteCode;
    }

    public String getContrastSiteName() {
        return contrastSiteName;
    }

    public void setContrastSiteName(String contrastSiteName) {
        this.contrastSiteName = contrastSiteName;
    }

    public String getContrastStaffAccount() {
        return contrastStaffAccount;
    }

    public void setContrastStaffAccount(String contrastStaffAccount) {
        this.contrastStaffAccount = contrastStaffAccount;
    }

    public String getContrastStaffName() {
        return contrastStaffName;
    }

    public void setContrastStaffName(String contrastStaffName) {
        this.contrastStaffName = contrastStaffName;
    }

    public Integer getContrastStaffType() {
        return contrastStaffType;
    }

    public void setContrastStaffType(Integer contrastStaffType) {
        this.contrastStaffType = contrastStaffType;
    }

    public Integer getContrastDutyType() {
        return contrastDutyType;
    }

    public void setContrastDutyType(Integer contrastDutyType) {
        this.contrastDutyType = contrastDutyType;
    }

    public Double getContrastWeight() {
        return contrastWeight;
    }

    public void setContrastWeight(Double contrastWeight) {
        this.contrastWeight = contrastWeight;
    }

    public Double getContrastVolume() {
        return contrastVolume;
    }

    public void setContrastVolume(Double contrastVolume) {
        this.contrastVolume = contrastVolume;
    }

    public Double getDiffWeight() {
        return diffWeight;
    }

    public void setDiffWeight(Double diffWeight) {
        this.diffWeight = diffWeight;
    }

    public String getDiffStandard() {
        return diffStandard;
    }

    public void setDiffStandard(String diffStandard) {
        this.diffStandard = diffStandard;
    }

    public Integer getIsMultiPack() {
        return isMultiPack;
    }

    public void setIsMultiPack(Integer isMultiPack) {
        this.isMultiPack = isMultiPack;
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

    public Integer getPicIsQualify() {
        return picIsQualify;
    }

    public void setPicIsQualify(Integer picIsQualify) {
        this.picIsQualify = picIsQualify;
    }

    public String getPictureAIDistinguishReason() {
        return pictureAIDistinguishReason;
    }

    public void setPictureAIDistinguishReason(String pictureAIDistinguishReason) {
        this.pictureAIDistinguishReason = pictureAIDistinguishReason;
    }

    public Integer getIsHasVideo() {
        return isHasVideo;
    }

    public void setIsHasVideo(Integer isHasVideo) {
        this.isHasVideo = isHasVideo;
    }

    public String getVideoPicture() {
        return videoPicture;
    }

    public void setVideoPicture(String videoPicture) {
        this.videoPicture = videoPicture;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getDimensionType() {
        return dimensionType;
    }

    public void setDimensionType(Integer dimensionType) {
        this.dimensionType = dimensionType;
    }

    public Integer getRecordType() {
        return recordType;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }

    public Integer getIsGatherTogether() {
        return isGatherTogether;
    }

    public void setIsGatherTogether(Integer isGatherTogether) {
        this.isGatherTogether = isGatherTogether;
    }

    public Integer getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(Integer isExcess) {
        this.isExcess = isExcess;
    }

    public Integer getExcessType() {
        return excessType;
    }

    public void setExcessType(Integer excessType) {
        this.excessType = excessType;
    }

    public Integer getIsIssueDownstream() {
        return isIssueDownstream;
    }

    public void setIsIssueDownstream(Integer isIssueDownstream) {
        this.isIssueDownstream = isIssueDownstream;
    }

    public Integer getSpotCheckStatus() {
        return spotCheckStatus;
    }

    public void setSpotCheckStatus(Integer spotCheckStatus) {
        this.spotCheckStatus = spotCheckStatus;
    }

    public Integer getManualUploadWeight() {
        return manualUploadWeight;
    }

    public void setManualUploadWeight(Integer manualUploadWeight) {
        this.manualUploadWeight = manualUploadWeight;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public Integer getExcessSubType() {
        return excessSubType;
    }

    public void setExcessSubType(Integer excessSubType) {
        this.excessSubType = excessSubType;
    }

    public String getExtendMap() {
        return extendMap;
    }

    public void setExtendMap(String extendMap) {
        this.extendMap = extendMap;
    }
}
