package com.jd.bluedragon.distribution.jy.service.findgoods.impl;

import com.jd.bluedragon.common.dto.inventory.*;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dao.findgoods.JyBizTaskFindGoodsDao;
import com.jd.bluedragon.distribution.jy.dao.findgoods.JyBizTaskFindGoodsDetailDao;
import com.jd.bluedragon.distribution.jy.service.findgoods.JyFindGoodsService;
import org.springframework.beans.factory.annotation.Autowired;

public class JyFindGoodsServiceImpl implements JyFindGoodsService {


  @Autowired
  private JyBizTaskFindGoodsDao jyBizTaskFindGoodsDao;
  @Autowired
  private JyBizTaskFindGoodsDetailDao jyBizTaskFindGoodsDetailDao;

  @Override
  public InvokeResult findGoodsScan(FindGoodsReq request) {
    return null;
  }

  @Override
  public InvokeResult<InventoryTaskDto> findCurrentInventoryTask(InventoryTaskQueryReq request) {
    return null;
  }

  @Override
  public InvokeResult<InventoryTaskDto> findInventoryTaskByBizId(InventoryTaskQueryReq request) {
    return null;
  }

  @Override
  public InvokeResult<InventoryTaskListQueryRes> findInventoryTaskListPage(InventoryTaskListQueryReq request) {
    return null;
  }

  @Override
  public InvokeResult<InventoryTaskStatisticsRes> inventoryTaskStatistics(InventoryTaskStatisticsReq request) {
    return null;
  }

  @Override
  public InvokeResult<Void> inventoryTaskPhotograph(InventoryTaskPhotographReq request) {
    return null;
  }

  @Override
  public InvokeResult<InventoryDetailQueryRes> findInventoryDetailPage(InventoryDetailQueryReq request) {
    return null;
  }
}
