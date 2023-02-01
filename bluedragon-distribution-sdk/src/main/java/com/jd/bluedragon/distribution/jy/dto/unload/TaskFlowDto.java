package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;

public class TaskFlowDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 3984465078328831447L;
    private String bizId;
    private Long endSiteId;
    private String goodsAreaCode;


    /**
     * 页码
     */
    private Integer pageNo;
    /**
     * 页容量
     */
    private Integer pageSize;

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

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
