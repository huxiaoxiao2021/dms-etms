package com.jd.bluedragon.distribution.jy.service.findgoods.impl;

import com.jd.bluedragon.common.dto.inventory.*;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryTaskStatusEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dao.findgoods.JyBizTaskFindGoodsDao;
import com.jd.bluedragon.distribution.jy.dao.findgoods.JyBizTaskFindGoodsDetailDao;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoods;
import com.jd.bluedragon.distribution.jy.service.findgoods.JyFindGoodsService;
import org.apache.commons.lang3.StringUtils;
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
    InvokeResult<InventoryTaskDto> res = new InvokeResult<>();
    String workGridKey = this.getWorkGridKeyByPositionCode(request.getPositionCode());
    JyBizTaskFindGoods jyBizTaskFindGoods = jyBizTaskFindGoodsDao.findOngoingTaskByWorkGrid(workGridKey);
    InventoryTaskDto resData = this.convertInventoryTaskDto(jyBizTaskFindGoods);
    res.setData(resData);
    return res;
  }

  private InventoryTaskDto convertInventoryTaskDto(JyBizTaskFindGoods jyBizTaskFindGoods) {
    InventoryTaskDto dto = new InventoryTaskDto();
    dto.setBizId(jyBizTaskFindGoods.getBizId());
//    todo zcf
//    dto.setWaveStartTime(jyBizTaskFindGoods.getWaveStartTime());
//    dto.setWaveEndTime(jyBizTaskFindGoods.getWaveEndTime());
//    dto.setCountdownSeconds();
    dto.setTaskStatus(jyBizTaskFindGoods.getTaskStatus());
    dto.setWaitFindCount(jyBizTaskFindGoods.getWaitFindCount());
    dto.setHaveFindCount(jyBizTaskFindGoods.getHaveFindCount());
    dto.setPhotoStatus(jyBizTaskFindGoods.getPhotoStatus());
    dto.setPhotoCount(StringUtils.isBlank(jyBizTaskFindGoods.getPhotoStatus()) ? 0 : jyBizTaskFindGoods.getPhotoStatus().length());
    dto.setPhotoTotalCount(4);//当前业务场景仅支持4个。直接写死
    if(InventoryTaskStatusEnum.COMPLETE.getCode().equals(jyBizTaskFindGoods.getTaskStatus())) {
      dto.setCompleteTime(jyBizTaskFindGoods.getUpdateTime().getTime());
    }
    return dto;
  }

  /**
   * 根据岗位码查询网格key
   * @param positionCode
   * @return
   */
  private String getWorkGridKeyByPositionCode(String positionCode) {
    //    todo zcf
    return "";
  }

  @Override
  public InvokeResult<InventoryTaskDto> findInventoryTaskByBizId(InventoryTaskQueryReq request) {
    InvokeResult<InventoryTaskDto> res = new InvokeResult<>();
    JyBizTaskFindGoods jyBizTaskFindGoods = jyBizTaskFindGoodsDao.findByBizId(request.getBizId());
    InventoryTaskDto resData = this.convertInventoryTaskDto(jyBizTaskFindGoods);
    res.setData(resData);
    return res;
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
