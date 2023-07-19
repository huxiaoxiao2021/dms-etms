package com.jd.bluedragon.distribution.jy.service.findgoods.impl;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.PACKAGE_HASBEEN_SCAN;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.PACKAGE_HASBEEN_SCAN_MESSAGE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.inventory.*;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryDetailStatusEnum;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryListTypeEnum;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryTaskStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.attachment.JyAttachmentDetailDao;
import com.jd.bluedragon.distribution.jy.dao.findgoods.JyBizTaskFindGoodsDao;
import com.jd.bluedragon.distribution.jy.dao.findgoods.JyBizTaskFindGoodsDetailDao;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.findgoods.*;
import com.jd.bluedragon.distribution.jy.service.findgoods.JyFindGoodsCacheService;
import com.jd.bluedragon.distribution.jy.service.findgoods.JyFindGoodsService;
import com.jd.bluedragon.distribution.jy.service.findgoods.constants.FindGoodsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.common.utils.ObjectHelper;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class JyFindGoodsServiceImpl implements JyFindGoodsService {


  @Autowired
  private JyBizTaskFindGoodsDao jyBizTaskFindGoodsDao;
  @Autowired
  private JyBizTaskFindGoodsDetailDao jyBizTaskFindGoodsDetailDao;
  @Autowired
  private JyAttachmentDetailDao jyAttachmentDetailDao;
  @Autowired
  private JyFindGoodsCacheService jyFindGoodsCacheService;
  @Autowired
  private PositionManager positionManager;

  @Override
  public InvokeResult findGoodsScan(FindGoodsReq request) {
    JyBizTaskFindGoods jyBizTaskFindGoods =jyBizTaskFindGoodsDao.findByBizId(request.getBizId());
    if (ObjectHelper.isEmpty(jyBizTaskFindGoods)){
      throw new JyBizException("未找到对应的找货任务！");
    }
    JyBizTaskFindGoodsDetail query = assembleFindGoodsDetailQuery(request);
    JyBizTaskFindGoodsDetail jyBizTaskFindGoodsDetail =jyBizTaskFindGoodsDetailDao.findPackage(query);
    if (ObjectHelper.isEmpty(jyBizTaskFindGoodsDetail)){
      throw new JyBizException("未找到对应的找货任务的待找包裹数据！");
    }

    if (ObjectHelper.isNotNull(jyBizTaskFindGoodsDetail.getFindStatus()) &&
        InventoryDetailStatusEnum.EXCEPTION.getCode() == jyBizTaskFindGoodsDetail.getFindStatus()){
      JyBizTaskFindGoodsDetail detail =new JyBizTaskFindGoodsDetail();
      detail.setId(jyBizTaskFindGoodsDetail.getId());
      detail.setFindStatus(InventoryDetailStatusEnum.FIND_GOOD.getCode());
      detail.setFindUserErp(request.getUser().getUserErp());
      detail.setFindUserName(request.getUser().getUserName());
      detail.setUpdateUserErp(request.getUser().getUserErp());
      detail.setUpdateUserName(request.getUser().getUserName());
      detail.setUpdateTime(new Date());
      jyBizTaskFindGoodsDetailDao.updateByPrimaryKeySelective(detail);
      return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE);
    }
    return new InvokeResult(PACKAGE_HASBEEN_SCAN,PACKAGE_HASBEEN_SCAN_MESSAGE);
  }

  private JyBizTaskFindGoodsDetail assembleFindGoodsDetailQuery(FindGoodsReq request) {
    JyBizTaskFindGoodsDetail jyBizTaskFindGoodsDetail =new JyBizTaskFindGoodsDetailQueryDto();
    jyBizTaskFindGoodsDetail.setFindGoodsTaskBizId(request.getBizId());
    jyBizTaskFindGoodsDetail.setPackageCode(request.getBarCode());
    return jyBizTaskFindGoodsDetail;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsServiceImpl.findCurrentInventoryTask", mState = {JProEnum.TP})
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
    Result<String> res = positionManager.queryWorkGridKeyByPositionCode(positionCode);
    if(!res.isSuccess()) {
      log.error("根据岗位码查询网格key异常,网格码={}，response={}", positionCode, JsonHelper.toJson(res));
      throw new JyBizException("根据岗位码查询网格key异常");
    }
    if(StringUtils.isBlank(res.getData())) {
      throw new JyBizException("根据岗位码" + positionCode +"查询网格码为空");
    }
    return res.getData();
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsServiceImpl.findInventoryTaskByBizId", mState = {JProEnum.TP})
  public InvokeResult<InventoryTaskDto> findInventoryTaskByBizId(InventoryTaskQueryReq request) {
    InvokeResult<InventoryTaskDto> res = new InvokeResult<>();
    res.success();

    JyBizTaskFindGoods jyBizTaskFindGoods = jyBizTaskFindGoodsDao.findByBizId(request.getBizId());
    InventoryTaskDto resData = this.convertInventoryTaskDto(jyBizTaskFindGoods);
    res.setData(resData);
    return res;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsServiceImpl.findInventoryTaskListPage", mState = {JProEnum.TP})
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
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsServiceImpl.inventoryTaskStatistics", mState = {JProEnum.TP})
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
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsServiceImpl.inventoryTaskPhotograph", mState = {JProEnum.TP})
  public InvokeResult<Void> inventoryTaskPhotograph(InventoryTaskPhotographReq request) {
    String methodDesc = "JyFindGoodsServiceImpl.inventoryTaskPhotograph:找货上传照片服务：";
    InvokeResult<Void> res = new InvokeResult<>();
    res.success();

    if(!jyFindGoodsCacheService.lockTaskByBizId(request.getBizId())) {
      res.error("操作繁忙，稍后重试");
      return res;
    }
    try {
      JyBizTaskFindGoods findGoods = jyBizTaskFindGoodsDao.findByBizId(request.getBizId());
      if (Objects.isNull(findGoods)) {
        res.error("没有找到操作任务信息");
        return res;
      }
      if (InventoryTaskStatusEnum.COMPLETE.getCode().equals(findGoods.getTaskStatus())) {
        res.error("该任务已结束");
        return res;
      }
      //图片数量校验
      res = this.checkPhotoNum(request);
      if (!res.codeSuccess()) {
        return res;
      }
      //照片存储逻辑
      this.savePhoto(request);

      if (StringUtils.isBlank(findGoods.getPhotoStatus()) || !findGoods.getPhotoStatus().contains(request.getPhotoPosition().toString())) {
        JyBizTaskFindGoods dbUpdate = new JyBizTaskFindGoods();
        dbUpdate.setUpdateTime(new Date());
        dbUpdate.setBizId(request.getBizId());
        dbUpdate.setPhotoStatus(findGoods.getPhotoStatus().concat(request.getPhotoPosition().toString()));
        jyBizTaskFindGoodsDao.updatePhotoStatus(dbUpdate);
      }
    }catch (Exception ex) {
      log.error("{}服务异常；req={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
      jyFindGoodsCacheService.unlockTaskByBizId(request.getBizId());
      throw new JyBizException("找货上传照片服务异常");
    }
    return res;
  }

  /**
   * 保存图片
   * @param request
   */
  private void savePhoto(InventoryTaskPhotographReq request) {
    List<JyAttachmentDetailEntity> entityList = new ArrayList<>();
    request.getPhotoUrls().forEach(url -> {
      JyAttachmentDetailEntity entity = new JyAttachmentDetailEntity();
      entity.setBizId(request.getBizId());
      entity.setBizType(FindGoodsConstants.PHOTOGRAPH_TYPE);
      entity.setBizSubType(request.getPhotoPosition().toString());//方位
      entity.setSiteCode(request.getCurrentOperate().getSiteCode());
      entity.setAttachmentType(JyAttachmentTypeEnum.PICTURE.getCode());
      entity.setAttachmentUrl(url);
      entity.setCreateUserErp(request.getUser().getUserErp());
      entity.setUpdateUserErp(request.getUser().getUserErp());
      entityList.add(entity);
    });
    jyAttachmentDetailDao.batchInsert(entityList);
  }

  /**
   * 校验图片数量
   * @param request
   * @return
   */
  private InvokeResult<Void> checkPhotoNum(InventoryTaskPhotographReq request) {
    InvokeResult<Void> res = new InvokeResult<>();
    res.success();

    JyAttachmentDetailQuery condition = new JyAttachmentDetailQuery();
    condition.setBizId(request.getBizId());
    condition.setSiteCode(request.getCurrentOperate().getSiteCode());
    condition.setBizType(FindGoodsConstants.PHOTOGRAPH_TYPE);
    condition.setBizSubType(request.getPhotoPosition().toString());
    Integer count = jyAttachmentDetailDao.countByCondition(condition);
    if(count + request.getPhotoUrls().size() > FindGoodsConstants.PHOTOGRAPH_MAX_NUM) {
      res.error(String.format("该位置上传图片最大支持%s个，已上传%s个，请确认当前上传数量", FindGoodsConstants.PHOTOGRAPH_MAX_NUM, count));
      return res;
    }
    return res;
  }



  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JyFindGoodsServiceImpl.findInventoryDetailPage", mState = {JProEnum.TP})
  public InvokeResult<InventoryDetailQueryRes> findInventoryDetailPage(InventoryDetailQueryReq request) {
    InvokeResult<InventoryDetailQueryRes> res = new InvokeResult<>();
    res.success();
    InventoryDetailQueryRes resData = new InventoryDetailQueryRes();
    res.setData(resData);

    JyBizTaskFindGoods findGoods = jyBizTaskFindGoodsDao.findByBizId(request.getBizId());
    if (Objects.isNull(findGoods)) {
      res.error("没有找到操作任务信息");
      return res;
    }

    JyBizTaskFindGoodsDetailQueryDto queryDto = new JyBizTaskFindGoodsDetailQueryDto();
    queryDto.setFindGoodsTaskBizId(request.getBizId());
    queryDto.setFindType(request.getInventoryDetailType());
    queryDto.setPageSize(request.getPageSize());
    Integer offset = (request.getPageNo() - 1) * request.getPageSize();
    queryDto.setOffset(offset);
    if(InventoryListTypeEnum.FOUND.getCode() == request.getInventoryListType()) {
      List<Integer> statusList = Arrays.asList(InventoryDetailStatusEnum.FIND_GOOD.getCode(), InventoryDetailStatusEnum.PDA_REAL_OPERATE.getCode());
      queryDto.setStatusList(statusList);
    }

    Integer count = jyBizTaskFindGoodsDetailDao.countInventoryDetail(queryDto);
    resData.setTotalNum(count);
    if(count <= 0) {
      res.setMessage("查询为空");
      return res;
    }

    List<JyBizTaskFindGoodsDetail> detailList = jyBizTaskFindGoodsDetailDao.findInventoryDetail(queryDto);
    if(CollectionUtils.isEmpty(detailList)) {
      res.setMessage("查询为空");
      return res;
    }
    List<InventoryDetailDto> inventoryDetailDtoList = new ArrayList<>();
    detailList.forEach(detail -> {
      InventoryDetailDto detailDto = new InventoryDetailDto();
      detailDto.setPackageCode(detail.getPackageCode());
      if(InventoryListTypeEnum.FOUND.getCode() == request.getInventoryListType()) {
        detailDto.setFindStatus(detail.getFindStatus());
      }
    });
    resData.setInventoryDetailDtoList(inventoryDetailDtoList);

    return res;
  }
}
