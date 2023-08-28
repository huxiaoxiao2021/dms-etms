package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/7/13 16:56
 * @Description
 */
public class InventoryTaskListQueryRes implements Serializable {
    private static final long serialVersionUID = -6051001368608203945L;

    /**
     * 清场列表
     */
    private List<InventoryTaskDto> inventoryTaskDtoList;

    public List<InventoryTaskDto> getInventoryTaskDtoList() {
        return inventoryTaskDtoList;
    }

    public void setInventoryTaskDtoList(List<InventoryTaskDto> inventoryTaskDtoList) {
        this.inventoryTaskDtoList = inventoryTaskDtoList;
    }
}
