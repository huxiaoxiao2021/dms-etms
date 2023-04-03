package com.jd.bluedragon.distribution.spotcheck;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * 抽检报表查询条件
 *
 * @author hujiping
 * @date 2021/12/6 4:24 下午
 */
public class SpotCheckReportQueryCondition extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 复核区域ID
     */
    private Long reviewOrgCode;

    /**
     * 复核站点ID
     */
    private Long reviewSiteCode;

    /**
     * 复核日期开始时间
     */
    private String reviewStartTime;

    /**
     * 复核日期开始时间
     */
    private String reviewEndTime;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 复核人ERP
     */
    private String reviewErp;

    /**
     * 复核来源
     */
    private Integer reviewSource;

    /**
     * 核对人ERP
     */
    private String contrastErp;

    /**
     * 业务类型
     */
    private Byte businessType;

    /**
     * 是否信任商家
     */
    private Byte isTrustMerchant;

    /**
     * 是否集齐
     */
    private Byte isGatherTogether;

    /**
     * 是否超标
     */
    private Byte isExcess;

    /**
     * 是否下发
     */
    private Byte isIssueDownstream;

    private Integer excessType;
    
    private String pictureAIDistinguishReason;

    public Long getReviewOrgCode() {
        return reviewOrgCode;
    }

    public void setReviewOrgCode(Long reviewOrgCode) {
        this.reviewOrgCode = reviewOrgCode;
    }

    public Long getReviewSiteCode() {
        return reviewSiteCode;
    }

    public void setReviewSiteCode(Long reviewSiteCode) {
        this.reviewSiteCode = reviewSiteCode;
    }

    public String getReviewStartTime() {
        return reviewStartTime;
    }

    public void setReviewStartTime(String reviewStartTime) {
        this.reviewStartTime = reviewStartTime;
    }

    public String getReviewEndTime() {
        return reviewEndTime;
    }

    public void setReviewEndTime(String reviewEndTime) {
        this.reviewEndTime = reviewEndTime;
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

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getReviewErp() {
        return reviewErp;
    }

    public void setReviewErp(String reviewErp) {
        this.reviewErp = reviewErp;
    }

    public Integer getReviewSource() {
        return reviewSource;
    }

    public void setReviewSource(Integer reviewSource) {
        this.reviewSource = reviewSource;
    }

    public String getContrastErp() {
        return contrastErp;
    }

    public void setContrastErp(String contrastErp) {
        this.contrastErp = contrastErp;
    }

    public Byte getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Byte businessType) {
        this.businessType = businessType;
    }

    public Byte getIsTrustMerchant() {
        return isTrustMerchant;
    }

    public void setIsTrustMerchant(Byte isTrustMerchant) {
        this.isTrustMerchant = isTrustMerchant;
    }

    public Byte getIsGatherTogether() {
        return isGatherTogether;
    }

    public void setIsGatherTogether(Byte isGatherTogether) {
        this.isGatherTogether = isGatherTogether;
    }

    public Byte getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(Byte isExcess) {
        this.isExcess = isExcess;
    }

    public Byte getIsIssueDownstream() {
        return isIssueDownstream;
    }

    public void setIsIssueDownstream(Byte isIssueDownstream) {
        this.isIssueDownstream = isIssueDownstream;
    }

    public Integer getExcessType() {
        return excessType;
    }

    public void setExcessType(Integer excessType) {
        this.excessType = excessType;
    }

    public String getPictureAIDistinguishReason() {
        return pictureAIDistinguishReason;
    }

    public void setPictureAIDistinguishReason(String pictureAIDistinguishReason) {
        this.pictureAIDistinguishReason = pictureAIDistinguishReason;
    }
}
