package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;

public class TaskFlowDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 3984465078328831447L;
    private String bizId;
    private Long endSiteId;
    private String goodsAreaCode;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getGoodsAreaCode() {
        return goodsAreaCode;
    }

    public void setGoodsAreaCode(String goodsAreaCode) {
        this.goodsAreaCode = goodsAreaCode;
    }
}
