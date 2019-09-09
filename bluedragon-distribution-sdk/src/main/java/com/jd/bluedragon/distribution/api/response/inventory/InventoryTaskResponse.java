package com.jd.bluedragon.distribution.api.response.inventory;

import com.jd.bluedragon.distribution.api.domain.SiteEntity;

import java.util.List;

public class InventoryTaskResponse {
    private static final long serialVersionUID = 1L;

    /**
     * 目的卡位列表
     */
    private List<SiteEntity> directionList;

    /**
     * 任务号
     */
    private String inventoryTaskId;

    /**
     * 盘点范围 1：自定义 2：全场 3：异常区
     */
    private Integer inventoryScope;


    /**
     * 提示类型 1：当前操作人有正在进行的任务 2：所选流向有正在进行的任务
     */
    private Integer warnType;

    public List<SiteEntity> getDirectionList() {
        return directionList;
    }

    public void setDirectionList(List<SiteEntity> directionList) {
        this.directionList = directionList;
    }

    public String getInventoryTaskId() {
        return inventoryTaskId;
    }

    public void setInventoryTaskId(String inventoryTaskId) {
        this.inventoryTaskId = inventoryTaskId;
    }

    public Integer getInventoryScope() {
        return inventoryScope;
    }

    public void setInventoryScope(Integer inventoryScope) {
        this.inventoryScope = inventoryScope;
    }

    public Integer getWarnType() {
        return warnType;
    }

    public void setWarnType(Integer warnType) {
        this.warnType = warnType;
    }
}
