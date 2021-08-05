package com.jd.bluedragon.distribution.stock;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.stock.domain.StockInventory;
import com.jd.bluedragon.distribution.stock.domain.InventoryQuery;

import java.util.List;


/**
 * 清场库存盘点对外jsf接口
 *
 * @author hujiping
 * @date 2021/6/7 4:42 下午
 */
public interface StockInventoryJSFService {

    /**
     * 根据条件查询已盘点的包裹
     * @param queryCondition
     * @return
     */
    Response<List<StockInventory>> queryInventoryUnSendPacks(InventoryQuery queryCondition);

    /**
     * 获取已盘点数量
     *
     * @param queryCondition
     * @return
     */
    Response<Long> queryInventoryUnSendNum(InventoryQuery queryCondition);

}
