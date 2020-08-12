package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * @ClassName: ReviewWeightSpotCheck
 * @Description: 重量体积抽验统计-实体类
 * @author: hujiping
 * @date: 2019/2/27 13:48
 */
public class ReviewWeightSpotCheck extends DbEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     * */
    private Long id;
    /** 复核日期 */
    private Date reviewDate;
    /** 复核区域 */
    private String reviewOrgName;
    /** 机构类型 */
    private Integer reviewMechanismType;
    /** 机构编码 */
    private Integer reviewSiteCode;
    /** 机构名称 */
    private String reviewSiteName;
    /** 普通应抽查包裹数 */
    private Integer normalPackageNum;
    /** 普通实际抽查包裹数 */
    private Integer normalPackageNumOfActual;
    /** 普通抽查率 */
    private String normalCheckRate;
    /** 普通抽查差异包裹数 */
    private Integer normalPackageNumOfDiff;
    /** 普通抽查差异率 */
    private String normalCheckRateOfDiff;
    /** 信任商家应抽查包裹数 */
    private Integer trustPackageNum;
    /** 信任商家实际抽查包裹数 */
    private Integer trustPackageNumOfActual;
    /** 信任商家抽查率 */
    private String trustCheckRate;
    /** 信任商家抽查差异包裹数 */
    private Integer trustPackageNumOfDiff;
    /** 信任商家抽查差异率 */
    private String trustCheckRateOfDiff;

    /** 普通应抽查运单数 */
    private Integer normalWaybillNum;
    /** 普通实际抽查运单数 */
    private Integer normalWaybillNumOfActual;
    /** 普通抽查差异运单数 */
    private Integer normalWaybillNumOfDiff;
    /** 信任商家应抽查运单数 */
    private Integer trustWaybillNum;
    /** 信任商家实际抽查运单数 */
    private Integer trustWaybillNumOfActual;
    /** 信任商家抽查差异运单数 */
    private Integer trustWaybillNumOfDiff;

    /** 总抽查率 */
    private String totalCheckRate;
    /** 业务类型 */
    private Integer spotCheckType;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getReviewSiteCode() {
        return reviewSiteCode;
    }

    public void setReviewSiteCode(Integer reviewSiteCode) {
        this.reviewSiteCode = reviewSiteCode;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getReviewOrgName() {
        return reviewOrgName;
    }

    public void setReviewOrgName(String reviewOrgName) {
        this.reviewOrgName = reviewOrgName;
    }

    public Integer getReviewMechanismType() {
        return reviewMechanismType;
    }

    public void setReviewMechanismType(Integer reviewMechanismType) {
        this.reviewMechanismType = reviewMechanismType;
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

    public Integer getSpotCheckType() {
        return spotCheckType;
    }

    public void setSpotCheckType(Integer spotCheckType) {
        this.spotCheckType = spotCheckType;
    }

    public Integer getNormalWaybillNum() {
        return normalWaybillNum;
    }

    public void setNormalWaybillNum(Integer normalWaybillNum) {
        this.normalWaybillNum = normalWaybillNum;
    }

    public Integer getNormalWaybillNumOfActual() {
        return normalWaybillNumOfActual;
    }

    public void setNormalWaybillNumOfActual(Integer normalWaybillNumOfActual) {
        this.normalWaybillNumOfActual = normalWaybillNumOfActual;
    }

    public Integer getNormalWaybillNumOfDiff() {
        return normalWaybillNumOfDiff;
    }

    public void setNormalWaybillNumOfDiff(Integer normalWaybillNumOfDiff) {
        this.normalWaybillNumOfDiff = normalWaybillNumOfDiff;
    }

    public Integer getTrustWaybillNum() {
        return trustWaybillNum;
    }

    public void setTrustWaybillNum(Integer trustWaybillNum) {
        this.trustWaybillNum = trustWaybillNum;
    }

    public Integer getTrustWaybillNumOfActual() {
        return trustWaybillNumOfActual;
    }

    public void setTrustWaybillNumOfActual(Integer trustWaybillNumOfActual) {
        this.trustWaybillNumOfActual = trustWaybillNumOfActual;
    }

    public Integer getTrustWaybillNumOfDiff() {
        return trustWaybillNumOfDiff;
    }

    public void setTrustWaybillNumOfDiff(Integer trustWaybillNumOfDiff) {
        this.trustWaybillNumOfDiff = trustWaybillNumOfDiff;
    }
}
