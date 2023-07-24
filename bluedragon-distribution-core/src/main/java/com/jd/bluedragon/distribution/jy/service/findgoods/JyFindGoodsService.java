package com.jd.bluedragon.distribution.jy.service.findgoods;

import com.jd.bluedragon.common.dto.inventory.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.findgoods.DistributPackageDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.FindGoodsTaskDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.FindGoodsTaskQueryDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.UpdateWaitFindPackageStatusDto;

public interface JyFindGoodsService {

  InvokeResult findGoodsScan(FindGoodsReq request);

  InvokeResult<InventoryTaskDto> findCurrentInventoryTask(InventoryTaskQueryReq request);

  InvokeResult<InventoryTaskDto> findInventoryTaskByBizId(InventoryTaskQueryReq request);

  InvokeResult<InventoryTaskListQueryRes> findInventoryTaskListPage(InventoryTaskListQueryReq request);

  InvokeResult<InventoryTaskStatisticsRes> inventoryTaskStatistics(InventoryTaskStatisticsReq request);

  InvokeResult<Void> inventoryTaskPhotograph(InventoryTaskPhotographReq request);

  InvokeResult<InventoryDetailQueryRes> findInventoryDetailPage(InventoryDetailQueryReq request);

  /**
   * 查询某天某波次的找货任务
   * @param dto
   * @return
   */
  InvokeResult<FindGoodsTaskDto> findWaveTask(FindGoodsTaskQueryDto dto);

  int saveFindGoodsTask(FindGoodsTaskDto findGoodsTaskDto);

  FindGoodsTaskDto findTaskByBizId(String findGoodsTaskBizId);

  boolean distributWaitFindPackage(DistributPackageDto dto,FindGoodsTaskDto findGoodsTaskDto);

  boolean updateTaskStatistics(FindGoodsTaskDto findGoodsTaskDto);

  boolean updateWaitFindPackage(UpdateWaitFindPackageStatusDto dto, FindGoodsTaskDto findGoodsTaskDto);
}
