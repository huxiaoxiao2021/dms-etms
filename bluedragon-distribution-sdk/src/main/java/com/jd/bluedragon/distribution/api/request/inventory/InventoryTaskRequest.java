package com.jd.bluedragon.distribution.api.request.inventory;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.domain.SiteEntity;

import java.util.List;

public class InventoryTaskRequest extends JdRequest {
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
     * 协作类型 1：创建者  2：协作者
     */
    private Integer cooperateType;

    /**
     * 盘点范围 1：自定义 2：全场 3：异常区
     */
    private Integer inventoryScope;

    /**
     * 操作人erp
     */
    private String userErp;

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

    public Integer getCooperateType() {
        return cooperateType;
    }

    public void setCooperateType(Integer cooperateType) {
        this.cooperateType = cooperateType;
    }

    public Integer getInventoryScope() {
        return inventoryScope;
    }

    public void setInventoryScope(Integer inventoryScope) {
        this.inventoryScope = inventoryScope;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }
}
