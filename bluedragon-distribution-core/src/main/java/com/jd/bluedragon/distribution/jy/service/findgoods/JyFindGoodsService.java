package com.jd.bluedragon.distribution.jy.service.findgoods;

import com.jd.bluedragon.common.dto.inventory.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;

public interface JyFindGoodsService {

  InvokeResult findGoodsScan(FindGoodsReq request);

  InvokeResult<InventoryTaskDto> findCurrentInventoryTask(InventoryTaskQueryReq request);

  InvokeResult<InventoryTaskRes> findInventoryTaskByBizId(InventoryTaskQueryReq request);

  InvokeResult<InventoryTaskListQueryRes> findInventoryTaskListPage(InventoryTaskListQueryReq request);

  InvokeResult<InventoryTaskStatisticsRes> inventoryTaskStatistics(InventoryTaskStatisticsReq request);

  InvokeResult<Void> inventoryTaskPhotograph(InventoryTaskPhotographReq request);

  InvokeResult<InventoryDetailQueryRes> findInventoryDetailPage(InventoryDetailQueryReq request);
}
