package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/9/19 10:58
 */
public class WeightVolumeCheckOfB2bPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String loginErp;
    private Integer createSiteCode;
    private Integer isWaybill;
    private String packageCode;
    private Double weight;
    private Double length;
    private Double width;
    private Double height;
    private Integer isExcess;
    private Integer upLoadNum;
    private Double totalWeight;
    private Double totalVolume;

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Double getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(Double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public String getLoginErp() {
        return loginErp;
    }

    public void setLoginErp(String loginErp) {
        this.loginErp = loginErp;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getIsWaybill() {
        return isWaybill;
    }

    public void setIsWaybill(Integer isWaybill) {
        this.isWaybill = isWaybill;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Integer getIsExcess() {
        return isExcess;
    }

    public void setIsExcess(Integer isExcess) {
        this.isExcess = isExcess;
    }

    public Integer getUpLoadNum() {
        return upLoadNum;
    }

    public void setUpLoadNum(Integer upLoadNum) {
        this.upLoadNum = upLoadNum;
    }
}
