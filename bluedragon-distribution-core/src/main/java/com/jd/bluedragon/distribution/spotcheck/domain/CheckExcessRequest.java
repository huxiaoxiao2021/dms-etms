package com.jd.bluedragon.distribution.spotcheck.domain;

import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;

/**
 * 校验超标请求参数
 *
 * @author hujiping
 * @date 2021/8/23 2:51 下午
 */
public class CheckExcessRequest {

    /**
     * 抽检来源类型
     * @see SpotCheckSourceFromEnum
     */
    private String spotCheckSourceFrom;
    /**
     * 是否多包裹
     */
    private Boolean isMorePack;
    /**
     * 复核重量
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
     * 复核体积
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
     * 核对重量
     */
    private Double contrastWeight;
    /**
     * 核对体积
     */
    private Double contrastVolume;
    /**
     * 核对较大值
     */
    private Double contrastLarge;
    /**
     * 较大值差异
     */
    private Double largeDiff;

    public String getSpotCheckSourceFrom() {
        return spotCheckSourceFrom;
    }

    public void setSpotCheckSourceFrom(String spotCheckSourceFrom) {
        this.spotCheckSourceFrom = spotCheckSourceFrom;
    }

    public Boolean getIsMorePack() {
        return isMorePack;
    }

    public void setIsMorePack(Boolean morePack) {
        isMorePack = morePack;
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

    public Double getContrastLarge() {
        return contrastLarge;
    }

    public void setContrastLarge(Double contrastLarge) {
        this.contrastLarge = contrastLarge;
    }

    public Double getLargeDiff() {
        return largeDiff;
    }

    public void setLargeDiff(Double largeDiff) {
        this.largeDiff = largeDiff;
    }
}
