package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: liming522
 * @Description: 分拣抽检数据推送京牛MQ
 * @Date: create in 2020/9/14 18:12
 */
public class AbnormalResultMqToJN implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     */
    private String billCode;


    /**
     * 责任类型
     * @See com.jd.etms.finance.enums.DutyTypeEnum
     */
    private Integer dutyType;


    /**
     * 计费重量
     */
    private BigDecimal weight;
    /**
     * 计费体积
     */
    private BigDecimal volume;

    /**
     * 复核重量
     * */
    private Double reviewWeight;

    /**
     * 复核体积cm3
     * */
    private Double reviewVolume;
    /**
     * 复核日期
     * */
    private Date reviewDate;

    /**
     * 来源系统
     * 1分拣抽检
     * 2终端抽检
     * 3计费
     */
    private String from;

    /**
     * 图片链接
     * */
    private String pictureAddress;
    /**
     * 责任人erp
     */
    private String dutyErp;


    /**
     * 责任三级id
     */
    private String threeLevelId;

    /**
     * 复核责任人erp
     * */
    private String reviewErp;

    /**
     * 复核责任二级id
     * 1配送中心id
     * 2分拣中心id
     * 3片区id
     * 4、5为空
     * */
    private Integer reviewSecondLevelId;
    /**
     * 复核长cm
     * */
    private Double reviewLength;
    /**
     * 复核宽cm
     * */
    private Double reviewWidth;
    /**
     * 复核高cm
     * */
    private Double reviewHeight;

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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getBillCode() {
        return billCode;
    }

    public void setBillCode(String billCode) {
        this.billCode = billCode;
    }

    public Integer getDutyType() {
        return dutyType;
    }

    public void setDutyType(Integer dutyType) {
        this.dutyType = dutyType;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public Double getReviewWeight() {
        return reviewWeight;
    }

    public void setReviewWeight(Double reviewWeight) {
        this.reviewWeight = reviewWeight;
    }

    public Double getReviewVolume() {
        return reviewVolume;
    }

    public void setReviewVolume(Double reviewVolume) {
        this.reviewVolume = reviewVolume;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getPictureAddress() {
        return pictureAddress;
    }

    public void setPictureAddress(String pictureAddress) {
        this.pictureAddress = pictureAddress;
    }

    public String getDutyErp() {
        return dutyErp;
    }

    public void setDutyErp(String dutyErp) {
        this.dutyErp = dutyErp;
    }

    public String getThreeLevelId() {
        return threeLevelId;
    }

    public void setThreeLevelId(String threeLevelId) {
        this.threeLevelId = threeLevelId;
    }

    public String getReviewErp() {
        return reviewErp;
    }

    public void setReviewErp(String reviewErp) {
        this.reviewErp = reviewErp;
    }

    public Integer getReviewSecondLevelId() {
        return reviewSecondLevelId;
    }

    public void setReviewSecondLevelId(Integer reviewSecondLevelId) {
        this.reviewSecondLevelId = reviewSecondLevelId;
    }
}
    
