package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.base.request.BaseReq;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/7/13 16:28
 * @Description  找货任务
 */
public class InventoryDetailQueryRes implements Serializable {
    private static final long serialVersionUID = -6051001368608203945L;

    private List<InventoryDetailDto> inventoryDetailDtoList;
    /**
     * 货物总数
     */
    private Integer totalNum;

    public List<InventoryDetailDto> getInventoryDetailDtoList() {
        return inventoryDetailDtoList;
    }

    public void setInventoryDetailDtoList(List<InventoryDetailDto> inventoryDetailDtoList) {
        this.inventoryDetailDtoList = inventoryDetailDtoList;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }
}
