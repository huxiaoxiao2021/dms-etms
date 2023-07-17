package com.jd.bluedragon.distribution.jy.service.findgoods.impl;

import com.jd.bluedragon.common.dto.inventory.*;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryTaskStatusEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dao.findgoods.JyBizTaskFindGoodsDao;
import com.jd.bluedragon.distribution.jy.dao.findgoods.JyBizTaskFindGoodsDetailDao;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoods;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoodsQueryDto;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoodsStatisticsDto;
import com.jd.bluedragon.distribution.jy.service.findgoods.JyFindGoodsService;
import com.jd.bluedragon.utils.DateHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    //todo zcf 确认起止时间返回格式
    dto.setWaveStartTime(jyBizTaskFindGoods.getWaveStartTime());
    dto.setWaveEndTime(jyBizTaskFindGoods.getWaveEndTime());
    //todo zcf 考虑这个秒数怎么计算
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
    res.success();

    JyBizTaskFindGoods jyBizTaskFindGoods = jyBizTaskFindGoodsDao.findByBizId(request.getBizId());
    InventoryTaskDto resData = this.convertInventoryTaskDto(jyBizTaskFindGoods);
    res.setData(resData);
    return res;
  }

  @Override
  public InvokeResult<InventoryTaskListQueryRes> findInventoryTaskListPage(InventoryTaskListQueryReq request) {
    InvokeResult<InventoryTaskListQueryRes> res = new InvokeResult<>();
    res.success();

    String workGridKey = this.getWorkGridKeyByPositionCode(request.getPositionCode());

    JyBizTaskFindGoodsQueryDto dbQuery = new JyBizTaskFindGoodsQueryDto();
    dbQuery.setWorkGridKey(workGridKey);
    dbQuery.setCreateTimeBegin(DateHelper.getZeroFromDay(new Date(), request.getQueryDays()));
    dbQuery.setPageNo(request.getPageNo());
    dbQuery.setPageSize(request.getPageSize());
    Integer offset = (request.getPageNo() - 1) * request.getPageSize();
    dbQuery.setOffset(offset);

    List<JyBizTaskFindGoods> jyBizTaskFindGoodsList = jyBizTaskFindGoodsDao.pageFindTaskListByCreateTime(dbQuery);
    if(CollectionUtils.isEmpty(jyBizTaskFindGoodsList)) {
      res.setMessage("查询为空");
      return res;
    }

    List<InventoryTaskDto> inventoryTaskDtoList = new ArrayList<>();
    jyBizTaskFindGoodsList.forEach(pojo -> {
      inventoryTaskDtoList.add(this.convertInventoryTaskDto(pojo));
    });
    InventoryTaskListQueryRes resData = new InventoryTaskListQueryRes();
    resData.setInventoryTaskDtoList(inventoryTaskDtoList);
    return res;
  }

  @Override
  public InvokeResult<InventoryTaskStatisticsRes> inventoryTaskStatistics(InventoryTaskStatisticsReq request) {
    InvokeResult<InventoryTaskStatisticsRes> res = new InvokeResult<>();
    res.success();

    String workGridKey = this.getWorkGridKeyByPositionCode(request.getPositionCode());
    JyBizTaskFindGoodsQueryDto dbQuery = new JyBizTaskFindGoodsQueryDto();
    dbQuery.setWorkGridKey(workGridKey);
    dbQuery.setCreateTimeBegin(DateHelper.getZeroFromDay(new Date(), request.getStatisticsDays()));

    JyBizTaskFindGoodsStatisticsDto statisticsDto = jyBizTaskFindGoodsDao.taskStatistics(dbQuery);

    InventoryTaskStatisticsRes resData = new InventoryTaskStatisticsRes();
    resData.setTotalTaskNum(statisticsDto.getTotalTaskNum());
    resData.setTotalPackageNum(statisticsDto.getTotalPackageNum());

    res.setData(resData);
    return res;
  }

  @Override
  public InvokeResult<Void> inventoryTaskPhotograph(InventoryTaskPhotographReq request) {
    InvokeResult<Void> res = new InvokeResult<>();
    res.success();

    //todo zcf 照片存储逻辑



    return res;
  }

  @Override
  public InvokeResult<InventoryDetailQueryRes> findInventoryDetailPage(InventoryDetailQueryReq request) {
    return null;
  }
}
