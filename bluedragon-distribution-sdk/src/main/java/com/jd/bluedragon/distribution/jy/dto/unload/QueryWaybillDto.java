package com.jd.bluedragon.distribution.jy.dto.unload;


import java.io.Serializable;

public class QueryWaybillDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = -3164990371491992850L;
    private String bizId;
    private String waybillCode;
    /**
     * 货物分类
     * com.jd.bluedragon.distribution.jy.enums.GoodsTypeEnum
     */
    private String goodsType;
    /**
     * UnloadBarCodeQueryEntranceEnum
     * 扫描异常类型: 1待扫 2拦截 3多扫
     */
    private Integer expType;
    private Integer pageNo;
    private Integer pageSize;

    private String boardCode;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public Integer getExpType() {
        return expType;
    }

    public void setExpType(Integer expType) {
        this.expType = expType;
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

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
}
