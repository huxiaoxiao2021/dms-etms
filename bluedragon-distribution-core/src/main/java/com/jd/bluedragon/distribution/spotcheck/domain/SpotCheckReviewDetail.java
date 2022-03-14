package com.jd.bluedragon.distribution.spotcheck.domain;


import com.jd.bluedragon.Constants;

import java.io.Serializable;

/**
 * 抽检复核数据
 *
 * @author hujiping
 * @date 2021/8/10 2:35 下午
 */
public class SpotCheckReviewDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 复核总重量kg
     */
    private Double reviewTotalWeight;
    /**
     * 复核总体积cm³
     */
    private Double reviewTotalVolume;
    /**
     * 复核重量kg
     */
    private Double reviewWeight;
    /**
     * 复核长cm
     */
    private Double reviewLength;
    /**
     * 复核宽cm
     */
    private Double reviewWidth;
    /**
     * 复核高cm
     */
    private Double reviewHeight;
    /**
     * 复核长宽高
     */
    private String reviewLWH;
    /**
     * 复核体积cm³
     */
    private Double reviewVolume;
    /**
     * 复核体积重量
     */
    private Double reviewVolumeWeight;
    /**
     * 复核较大值
     */
    private Double reviewLarge;
    /**
     * 复核区域ID
     */
    private Integer reviewOrgId;
    /**
     * 复核区域名称
     */
    private String reviewOrgName;
    /**
     * 复核站点ID
     */
    private Integer reviewSiteCode;
    /**
     * 复核站点名称
     */
    private String reviewSiteName;
    /**
     * 复核操作人ID
     */
    private Integer reviewUserId;
    /**
     * 复核操作人ERP
     */
    private String reviewUserErp;
    /**
     * 复核操作人名称
     */
    private String reviewUserName;

    /**
     * 设备编码
     */
    private String machineCode;

    public Double getReviewTotalWeight() {
        return reviewTotalWeight == null ? Constants.DOUBLE_ZERO : reviewTotalWeight;
    }

    public void setReviewTotalWeight(Double reviewTotalWeight) {
        this.reviewTotalWeight = reviewTotalWeight;
    }

    public Double getReviewTotalVolume() {
        return reviewTotalVolume == null ? Constants.DOUBLE_ZERO : reviewTotalVolume;
    }

    public void setReviewTotalVolume(Double reviewTotalVolume) {
        this.reviewTotalVolume = reviewTotalVolume;
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

    public Double getReviewVolumeWeight() {
        return reviewVolumeWeight;
    }

    public void setReviewVolumeWeight(Double reviewVolumeWeight) {
        this.reviewVolumeWeight = reviewVolumeWeight;
    }

    public Double getReviewLarge() {
        return reviewLarge;
    }

    public void setReviewLarge(Double reviewLarge) {
        this.reviewLarge = reviewLarge;
    }

    public Integer getReviewOrgId() {
        return reviewOrgId;
    }

    public void setReviewOrgId(Integer reviewOrgId) {
        this.reviewOrgId = reviewOrgId;
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

    public Integer getReviewUserId() {
        return reviewUserId;
    }

    public void setReviewUserId(Integer reviewUserId) {
        this.reviewUserId = reviewUserId;
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
}
