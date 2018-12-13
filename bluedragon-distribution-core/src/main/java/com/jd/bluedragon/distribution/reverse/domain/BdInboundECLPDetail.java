package com.jd.bluedragon.distribution.reverse.domain;

import java.io.Serializable;

/**
 * @author liuduo
 */
public class BdInboundECLPDetail implements Serializable {
    private static final long serialVersionUID = -2735515368599528845L;
    private String goodsNo;
    private String goodsName;
    private Integer num;

    public String getGoodsNo() {
        return goodsNo;
    }

    public void setGoodsNo(String goodsNo) {
        this.goodsNo = goodsNo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "BdInboundECLPDetail{" +
                "goodsNo='" + goodsNo + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", num=" + num +
                '}';
    }
}
