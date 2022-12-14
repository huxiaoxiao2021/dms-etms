package com.jd.bluedragon.distribution.open.entity;

import java.math.BigDecimal;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open.entity
 * @ClassName: WeightVolume
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 15:53
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class WeightVolumeOperateInfo {

    /**
     * 称重量方单号
     */
    private String barcode;

    /**
     * 重量KG
     */
    private BigDecimal weight;

    /**
     * 长度cm
     */
    private BigDecimal length;

    /**
     * 宽度cm
     */
    private BigDecimal width;

    /**
     * 高度cm
     */
    private BigDecimal height;

    /**
     * 体积：立方厘米
     */
    private BigDecimal volume;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getLength() {
        return length;
    }

    public void setLength(BigDecimal length) {
        this.length = length;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal width) {
        this.width = width;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal height) {
        this.height = height;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public void setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
    }
}
