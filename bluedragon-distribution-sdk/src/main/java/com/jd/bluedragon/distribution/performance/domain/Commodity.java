package com.jd.bluedragon.distribution.performance.domain;

import java.io.Serializable;

/**
 * @ClassName: Commodity
 * @Description: 商品信息
 * @author: hujiping
 * @date: 2018/8/24 17:07
 */
public class Commodity implements Serializable {

    private String skuId;
    private String skuName;
    private String poNo;
    private Integer skuNum;
    private String boxCode;
    private String waybillCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public Integer getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(Integer skuNum) {
        this.skuNum = skuNum;
    }

    @Override
    public String toString() {
        return "Commodity{" +
                "skuId='" + skuId + '\'' +
                ", skuName='" + skuName + '\'' +
                ", poNo='" + poNo + '\'' +
                ", skuNum=" + skuNum +
                ", boxCode='" + boxCode + '\'' +
                ", waybillCode='" + waybillCode + '\'' +
                '}';
    }
}
