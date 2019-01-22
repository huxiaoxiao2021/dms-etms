package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

/**
 * 包裹信息
 * Created by wangtingwei on 2015/12/23.
 */
public class PrintPackage implements Serializable {

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 包裹重量
     */
    private Double weight;

    /**
     * 包裹是否已打印
     */
    public Boolean isPrintPack;

    /**
     * 包裹序号
     */
    public String packSerial;

    /**
     * 包裹重量
     */
    public String packageWeight;

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

    public Boolean getIsPrintPack() {
        return isPrintPack;
    }

    public void setIsPrintPack(Boolean isPrintPack) {
        this.isPrintPack = isPrintPack;
    }

    public String getPackSerial() {
        return packSerial;
    }

    public void setPackSerial(String packSerial) {
        this.packSerial = packSerial;
    }

    public String getPackageWeight() {
        return packageWeight;
    }

    public void setPackageWeight(String packageWeight) {
        this.packageWeight = packageWeight;
    }
}
