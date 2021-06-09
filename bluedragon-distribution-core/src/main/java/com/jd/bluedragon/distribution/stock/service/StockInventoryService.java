package com.jd.bluedragon.distribution.stock.service;

import com.jd.bluedragon.common.dto.stock.StockInventoryResult;
import com.jd.bluedragon.common.dto.stock.StockInventoryScanDto;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.stock.domain.InventoryQuery;
import com.jd.bluedragon.distribution.stock.domain.StockInventory;

import java.util.List;

/**
 * 库存盘点接口
 *
 * @author hujiping
 * @date 2021/6/4 2:14 下午
 */
public interface StockInventoryService {

    /**
     * 库存盘点扫描
     *
     * @param stockInventoryScanDto
     * @return
     */
    InvokeResult<StockInventoryResult> stockInventoryScan(StockInventoryScanDto stockInventoryScanDto);

    /**
     * 查询当前站点的库存盘点数据
     *
     * @param createSiteCode
     * @return
     */
    InvokeResult<StockInventoryResult> queryStockInventory(Integer createSiteCode);

    /**
     * 更新盘点数据
     *
     * @param stockInventoryScanDto
     * @return
     */
    void updateInventory(StockInventoryScanDto stockInventoryScanDto);

    /**
     * 根据条件查询已盘点包裹数据
     *
     * @param inventoryQuery
     * @return
     */
    InvokeResult<List<StockInventory>> queryInventoryUnSendPacks(InventoryQuery inventoryQuery);

    /**
     * 查询盘点数量
     *
     * @param inventoryQuery
     * @return
     */
    InvokeResult<Long> queryInventoryUnSendNum(InventoryQuery inventoryQuery);
}
