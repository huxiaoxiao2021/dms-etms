package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.inventory.FindGoodsReq;
import com.jd.bluedragon.common.dto.inventory.FindGoodsResp;
import com.jd.bluedragon.common.dto.inventory.*;

/**
 * 拣运找货网关服务
 */
public interface JyFindGoodsGatewayService {

  /**
   * 找货扫描
   */
  JdCResponse<FindGoodsResp> findGoodsScan(FindGoodsReq request);


  /**
   * 查询当前时刻开始中的盘点任务
   * @param request
   * @return
   */
  JdCResponse<InventoryTaskDto> findCurrentInventoryTask(InventoryTaskQueryReq request);

  /**
   * 根据bizId查询找货任务
   * @param request
   * @return
   */
  JdCResponse<InventoryTaskDto> findInventoryTaskByBizId(InventoryTaskQueryReq request);
  /**
   * 查询任务列表（分页）
   * @return
   */
  JdCResponse<InventoryTaskListQueryRes> findInventoryTaskListPage(InventoryTaskListQueryReq request);
  /**
   * 任务统计
   * @param request
   * @return
   */
  JdCResponse<InventoryTaskStatisticsRes> inventoryTaskStatistics(InventoryTaskStatisticsReq request);
  /**
   * 拍照
   * @param request
   * @return
   */
  JdCResponse<Void> inventoryTaskPhotograph(InventoryTaskPhotographReq request);
  /**
   * 查询任务明细（分页）
   * @param request
   * @return
   */
  JdCResponse<InventoryDetailQueryRes> findInventoryDetailPage(InventoryDetailQueryReq request);


}
