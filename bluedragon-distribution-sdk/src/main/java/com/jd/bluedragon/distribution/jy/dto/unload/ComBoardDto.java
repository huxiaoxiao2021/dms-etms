package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class ComBoardDto implements Serializable {
    private static final long serialVersionUID = 8723860956859377453L;
    private String bizId;
    /**
     * 板号
     */
    private String boardCode;
    /**
     * 货区编码
     */
    private String goodsAreaCode;
    private String endSiteName;
    private Long endSiteId;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getGoodsAreaCode() {
        return goodsAreaCode;
    }

    public void setGoodsAreaCode(String goodsAreaCode) {
        this.goodsAreaCode = goodsAreaCode;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }
}
