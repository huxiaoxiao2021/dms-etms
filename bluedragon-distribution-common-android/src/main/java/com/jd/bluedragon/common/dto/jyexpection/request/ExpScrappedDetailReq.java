package com.jd.bluedragon.common.dto.jyexpection.request;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/10 16:29
 * @Description: 报废
 */
public class ExpScrappedDetailReq extends  ExpBaseReq{

    // 业务主键id
    private String bizId;

    // 存储类型 0暂存 1提交
    private Integer saveType;

    /**
     * 报废类型code 值
     */
    private Integer scrappedTypCode;

    /**
     * 物品照片 多个逗号分割
     */
    private String goodsImageUrl;

    /**
     * 证明照片 多个逗号分割
     */
    private String certifyImageUrl;




    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getSaveType() {
        return saveType;
    }

    public void setSaveType(Integer saveType) {
        this.saveType = saveType;
    }

    public Integer getScrappedTypCode() {
        return scrappedTypCode;
    }

    public void setScrappedTypCode(Integer scrappedTypCode) {
        this.scrappedTypCode = scrappedTypCode;
    }

    public String getGoodsImageUrl() {
        return goodsImageUrl;
    }

    public void setGoodsImageUrl(String goodsImageUrl) {
        this.goodsImageUrl = goodsImageUrl;
    }

    public String getCertifyImageUrl() {
        return certifyImageUrl;
    }

    public void setCertifyImageUrl(String certifyImageUrl) {
        this.certifyImageUrl = certifyImageUrl;
    }
}
