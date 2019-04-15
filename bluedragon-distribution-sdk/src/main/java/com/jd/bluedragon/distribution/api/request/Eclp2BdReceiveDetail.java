package com.jd.bluedragon.distribution.api.request;

/**
 * @ClassName: Eclp2BdReceiveDetail
 * @Description: 商品明细对象-ECLP
 * @author: hujiping
 * @date: 2019/2/25 14:12
 */
public class Eclp2BdReceiveDetail {
     /**
      * 商品编号
      * */
    private String goodsNo;
    /**
     * 商品名称
     */
    private String goodsName;
    /**
     * 批次号（备件条码）
     */
    private String batchNo;
    /**
     * 数量
     */
    private Integer quantity;

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

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
