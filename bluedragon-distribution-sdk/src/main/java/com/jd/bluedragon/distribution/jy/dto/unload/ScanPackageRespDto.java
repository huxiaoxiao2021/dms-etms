package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class ScanPackageRespDto implements Serializable {
    private static final long serialVersionUID = -6963372061306635997L;
    private String bizId;
    /**
     * 货区编码
     */
    private String goodsAreaCode;
    /**
     * 板号
     */
    private String boardCode;
    private String endSiteName;
    private Long endSiteId;
    private Integer comBoardCount;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getGoodsAreaCode() {
        return goodsAreaCode;
    }

    public void setGoodsAreaCode(String goodsAreaCode) {
        this.goodsAreaCode = goodsAreaCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
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

    public Integer getComBoardCount() {
        return comBoardCount;
    }

    public void setComBoardCount(Integer comBoardCount) {
        this.comBoardCount = comBoardCount;
    }
}
