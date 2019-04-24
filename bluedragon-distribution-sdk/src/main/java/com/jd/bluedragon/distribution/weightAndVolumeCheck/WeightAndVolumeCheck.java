package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * @ClassName: WeightAndVolumeCheck
 * @Description: 重量体积校验-实体类
 * @author: hujiping
 * @date: 2019/04/22 10:48
 */
public class WeightAndVolumeCheck extends DbEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     * */
    private Long id;
    /** 复核日期 */
    private Date reviewDate;
    /** 运单号 */
    private String waybillCode;
    /** 包裹号 */
    private String packageCode;
    /** 商家名称 */
    private String busiName;
    /** 是否是信任商家 */
    private Integer isTrustBusiName;
    /** 复核区域 */
    private Integer reviewOrgCode;
    private String reviewOrg;
    /** 复核分拣 */
    private Integer reviewCreateSiteCode;
    private String reviewCreateSiteName;
    /** 机构类型(1:分拣中心 0:转运中心) */
    private Integer mechanismType;
    /** 复核人erp */
    private String reviewErp;
    /** 复核重量kg */
    private Double reviewWeight;
    /** 复核长cm */
    private Double reviewLength;
    /** 复核宽cm */
    private Double reviewWidth;
    /** 复核高cm */
    private Double reviewHeight;
    /** 复核长宽高cm */
    private String reviewLwh;
    /** 复核体积cm */
    private Double reviewVolume;
    /** 计费操作区域 */
    private String billingOperateOrg;
    /** 计费操作机构 */
    private String billingOperateDepartment;
    /** 计费操作人ERP */
    private String billingOperateErp;
    /** 计费重量kg */
    private Double billingWeight;
    /** 计费体积cm */
    private Double billingVolume;
    /** 重量差异 */
    private String weightDiff;
    /** 体积重量差异 */
    private String volumeWeightDiff;
    /** 误差标准值 */
    private String diffStandard;
    /** 是否超标 */
    private Integer isExcess;
    /** 是否有效 1：有效 0：无效 */
    private Integer isDelete;
    /** 默认时间 */
    private Date ts;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public String getBusiName() {
        return busiName;
    }

    public void setBusiName(String busiName) {
        this.busiName = busiName;
    }

    public Integer getIsTrustBusiName() {
        return isTrustBusiName;
    }

    public void setIsTrustBusiName(Integer isTrustBusiName) {
        this.isTrustBusiName = isTrustBusiName;
    }

    public Integer getReviewOrgCode() {
        return reviewOrgCode;
    }

    public void setReviewOrgCode(Integer reviewOrgCode) {
        this.reviewOrgCode = reviewOrgCode;
    }

    public String getReviewOrg() {
        return reviewOrg;
    }

    public void setReviewOrg(String reviewOrg) {
        this.reviewOrg = reviewOrg;
    }

    public Integer getReviewCreateSiteCode() {
        return reviewCreateSiteCode;
    }

    public void setReviewCreateSiteCode(Integer reviewCreateSiteCode) {
        this.reviewCreateSiteCode = reviewCreateSiteCode;
    }

    public String getReviewCreateSiteName() {
        return reviewCreateSiteName;
    }

    public void setReviewCreateSiteName(String reviewCreateSiteName) {
        this.reviewCreateSiteName = reviewCreateSiteName;
    }

    public Integer getMechanismType() {
        return mechanismType;
    }

    public void setMechanismType(Integer mechanismType) {
        this.mechanismType = mechanismType;
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

    public String getReviewLwh() {
        return reviewLwh;
    }

    public void setReviewLwh(String reviewLwh) {
        this.reviewLwh = reviewLwh;
    }

    public Double getReviewVolume() {
        return reviewVolume;
    }

    public void setReviewVolume(Double reviewVolume) {
        this.reviewVolume = reviewVolume;
    }

    public String getBillingOperateOrg() {
        return billingOperateOrg;
    }

    public void setBillingOperateOrg(String billingOperateOrg) {
        this.billingOperateOrg = billingOperateOrg;
    }

    public String getBillingOperateDepartment() {
        return billingOperateDepartment;
    }

    public void setBillingOperateDepartment(String billingOperateDepartment) {
        this.billingOperateDepartment = billingOperateDepartment;
    }

    public String getBillingOperateErp() {
        return billingOperateErp;
    }

    public void setBillingOperateErp(String billingOperateErp) {
        this.billingOperateErp = billingOperateErp;
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

    public Integer getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(Integer isExcess) {
        this.isExcess = isExcess;
    }

    @Override
    public Integer getIsDelete() {
        return isDelete;
    }

    @Override
    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    @Override
    public Date getTs() {
        return ts;
    }

    @Override
    public void setTs(Date ts) {
        this.ts = ts;
    }
}
