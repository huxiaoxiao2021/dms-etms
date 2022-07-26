package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;
public class UnloadTaskFlowDto implements Serializable {
    private static final long serialVersionUID = 7911106472951275134L;
    /**
     * 货区编码
     */
    private String goodsAreaCode;
    /**
     * 下游场地名称
     */
    private String endSiteName;
    private Long endSiteId;
    /**
     * 该流向下组板数量
     */
    private Integer comBoardCount;

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

    public Integer getComBoardCount() {
        return comBoardCount;
    }

    public void setComBoardCount(Integer comBoardCount) {
        this.comBoardCount = comBoardCount;
    }
}
