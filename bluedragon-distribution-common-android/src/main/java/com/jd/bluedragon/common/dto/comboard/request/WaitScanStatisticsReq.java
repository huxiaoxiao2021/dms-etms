package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

public class WaitScanStatisticsReq extends BaseReq implements Serializable {
    private static final long serialVersionUID = -8190931087075967366L;
    private Integer endSiteId;
    /**
     * 货物类型
     */
    private String goodsType;

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }
}
