package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Date 2023/7/13 16:28
 * @Description  找货任务
 */
public class InventoryDetailQueryReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -6051001368608203945L;

    private String bizId;

    /**
     * 任务列表类型
     * com.jd.bluedragon.common.dto.inventory.enums.InventoryListTypeEnum
     */
    private Integer inventoryListType;
    /**
     * 明细分类
     * com.jd.bluedragon.common.dto.inventory.enums.InventoryDetailTypeEnum
     */
    private Integer inventoryDetailType;

    /**
     * 页码
     */
    private Integer pageNo;
    private Integer pageSize;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getInventoryListType() {
        return inventoryListType;
    }

    public void setInventoryListType(Integer inventoryListType) {
        this.inventoryListType = inventoryListType;
    }

    public Integer getInventoryDetailType() {
        return inventoryDetailType;
    }

    public void setInventoryDetailType(Integer inventoryDetailType) {
        this.inventoryDetailType = inventoryDetailType;
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
