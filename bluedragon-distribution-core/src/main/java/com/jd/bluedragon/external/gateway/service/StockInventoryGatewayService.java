package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.stock.StockInventoryResult;
import com.jd.bluedragon.common.dto.stock.StockInventoryScanDto;

/**
 * 库存盘点网关服务
 *
 * @author hujiping
 * @date 2021/6/4 5:13 下午
 */
public interface StockInventoryGatewayService {

    /**
     * 查询库存盘点信息
     *
     * @param createSiteCode
     * @return
     */
    JdCResponse<StockInventoryResult> queryStockInventory(Integer createSiteCode);

    /**
     * 库存盘点扫描
     *
     * @param stockInventoryScanDto
     * @return
     */
    JdCResponse<StockInventoryResult> stockInventoryScan(StockInventoryScanDto stockInventoryScanDto);
}
