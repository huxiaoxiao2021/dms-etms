package com.jd.bluedragon.distribution.weightAndVolumeCheck.dto;

import java.math.BigDecimal;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/1/8 17:15
 */
public class CheckExcessParam {

    private BigDecimal sumLWH;

    private Double differenceValue;

    private Double moreBigValue;

    private Double reviewWeight;

    public BigDecimal getSumLWH() {
        return sumLWH;
    }

    public void setSumLWH(BigDecimal sumLWH) {
        this.sumLWH = sumLWH;
    }

    public Double getDifferenceValue() {
        return differenceValue;
    }

    public void setDifferenceValue(Double differenceValue) {
        this.differenceValue = differenceValue;
    }

    public Double getMoreBigValue() {
        return moreBigValue;
    }

    public void setMoreBigValue(Double moreBigValue) {
        this.moreBigValue = moreBigValue;
    }

    public Double getReviewWeight() {
        return reviewWeight;
    }

    public void setReviewWeight(Double reviewWeight) {
        this.reviewWeight = reviewWeight;
    }

}
    
