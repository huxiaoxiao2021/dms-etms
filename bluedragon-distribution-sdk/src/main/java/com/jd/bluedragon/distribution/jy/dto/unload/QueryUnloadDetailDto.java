package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

public class QueryUnloadDetailDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = -4224506737591286259L;
    private String bizId;

    /**
     * 异常标识：false 待扫 ,true(拦截、多扫)
     */
    private Boolean expFlag;
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

    public Boolean getExpFlag() {
        return expFlag;
    }

    public void setExpFlag(Boolean expFlag) {
        this.expFlag = expFlag;
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

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
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
