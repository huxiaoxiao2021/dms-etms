package com.jd.bluedragon.distribution.weightAndVolumeCheck;

import java.io.Serializable;

/**
 * 类描述信息
 *
 * @author: hujiping
 * @date: 2019/10/10 17:35
 */
public class WeightVolumeCheckOfB2bWaybill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     * */
    private String waybillCode;
    /**
     * 包裹数
     * */
    private Integer packNum;
    /**
     * 总重量
     * */
    private Double waybillWeight;
    /**
     * 总体积
     * */
    private Double waybillVolume;
    /**
     * 是否超标
     * */
    private Integer isExcess;
    /**
     * 上传图片数量
     * */
    private Integer upLoadNum;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getPackNum() {
        return packNum;
    }

    public void setPackNum(Integer packNum) {
        this.packNum = packNum;
    }

    public Double getWaybillWeight() {
        return waybillWeight;
    }

    public void setWaybillWeight(Double waybillWeight) {
        this.waybillWeight = waybillWeight;
    }

    public Double getWaybillVolume() {
        return waybillVolume;
    }

    public void setWaybillVolume(Double waybillVolume) {
        this.waybillVolume = waybillVolume;
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
