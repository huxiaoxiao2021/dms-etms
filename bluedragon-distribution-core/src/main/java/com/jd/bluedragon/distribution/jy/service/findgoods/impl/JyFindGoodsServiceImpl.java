package com.jd.bluedragon.distribution.jy.service.findgoods.impl;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.NO_FINDGOODS_TASK_DATA_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.NO_FINDGOODS_TASK_DATA_MESSAGE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.PACKAGE_HASBEEN_SCAN;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.PACKAGE_HASBEEN_SCAN_MESSAGE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.RESULT_SUCCESS_MESSAGE;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.inventory.*;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryDetailStatusEnum;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryListTypeEnum;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryTaskStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.config.dto.ClientAutoRefreshConfig;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyAttachmentTypeEnum;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.jsf.position.PositionManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailEntity;
import com.jd.bluedragon.distribution.jy.attachment.JyAttachmentDetailQuery;
import com.jd.bluedragon.distribution.jy.dao.attachment.JyAttachmentDetailDao;
import com.jd.bluedragon.distribution.jy.dao.findgoods.JyBizTaskFindGoodsDao;
import com.jd.bluedragon.distribution.jy.dao.findgoods.JyBizTaskFindGoodsDetailDao;
import com.jd.bluedragon.distribution.jy.dto.findgoods.DistributPackageDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.FindGoodsTaskDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.FindGoodsTaskQueryDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.UpdateWaitFindPackageStatusDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.findgoods.*;
import com.jd.bluedragon.distribution.jy.service.findgoods.JyFindGoodsCacheService;
import com.jd.bluedragon.distribution.jy.service.findgoods.JyFindGoodsService;
import com.jd.bluedragon.distribution.jy.service.findgoods.constants.FindGoodsConstants;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.distribution.jy.service.task.autoRefresh.enums.ClientAutoRefreshBusinessTypeEnum;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.common.utils.ObjectHelper;
import com.jdl.basic.common.utils.Result;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;

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
  @Autowired
  private UccPropertyConfiguration uccConfig;
  @Autowired
  JimDbLock jimDbLock;


  @Override
  @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public InvokeResult findGoodsScan(FindGoodsReq request) {
    FindGoodsTaskDto findGoodsTaskDto =findTaskByBizId(request.getBizId());
    if (ObjectHelper.isEmpty(findGoodsTaskDto)){
      throw new JyBizException("未找到对应的找货任务！");
    }
    if(InventoryTaskStatusEnum.COMPLETE.getCode().equals(findGoodsTaskDto.getTaskStatus())) {
      throw new JyBizException("任务已完成！");
    }
    JyBizTaskFindGoodsDetail query = assembleFindGoodsDetailQuery(request);
    JyBizTaskFindGoodsDetail jyBizTaskFindGoodsDetail =jyBizTaskFindGoodsDetailDao.findPackage(query);
    if (ObjectHelper.isEmpty(jyBizTaskFindGoodsDetail)){
      throw new JyBizException("未找到对应的找货任务的待找包裹数据！");
    }
    if (ObjectHelper.isNotNull(jyBizTaskFindGoodsDetail.getFindStatus()) && InventoryDetailStatusEnum.EXCEPTION.getCode() == jyBizTaskFindGoodsDetail.getFindStatus()){
      String findGoodsTaskLock = String.format(Constants.JY_FINDGOODS_TASK_LOCK_PREFIX, request.getBizId());
      if (!jimDbLock.lock(findGoodsTaskLock, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
        throw new JyBizException("请求繁忙,请稍后再试！");
      }
      try {
        JyBizTaskFindGoodsDetail detail =new JyBizTaskFindGoodsDetail();
        detail.setId(jyBizTaskFindGoodsDetail.getId());
        detail.setFindStatus(InventoryDetailStatusEnum.FIND_GOOD.getCode());
        detail.setFindUserErp(request.getUser().getUserErp());
        detail.setFindUserName(request.getUser().getUserName());
        detail.setUpdateUserErp(request.getUser().getUserErp());
        detail.setUpdateUserName(request.getUser().getUserName());
        detail.setUpdateTime(new Date());
        jyBizTaskFindGoodsDetailDao.updateByPrimaryKeySelective(detail);
        findGoodsTaskDto.setUpdateUserErp(request.getUser().getUserErp());
        findGoodsTaskDto.setUpdateUserName(request.getUser().getUserName());
        updateTaskStatistics(findGoodsTaskDto);
      } finally {
        jimDbLock.releaseLock(findGoodsTaskLock,request.getRequestId());
      }
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
    if(Objects.isNull(jyBizTaskFindGoods)) {
      res.setMessage(InvokeResult.RESULT_NULL_MESSAGE);
      return res;
    }
    InventoryTaskDto resData = this.convertInventoryTaskDto(jyBizTaskFindGoods);
    res.setData(resData);
    return res;
  }

  private InventoryTaskDto convertInventoryTaskDto(JyBizTaskFindGoods jyBizTaskFindGoods) {
    InventoryTaskDto dto = new InventoryTaskDto();
    dto.setBizId(jyBizTaskFindGoods.getBizId());
    dto.setWaveStartTime(getPdaShowTime(jyBizTaskFindGoods.getWaveStartTime()));
    dto.setWaveEndTime(getPdaShowTime(jyBizTaskFindGoods.getWaveEndTime()));
    dto.setCountdownSeconds(this.getCountdownSeconds(jyBizTaskFindGoods.getBizId(), jyBizTaskFindGoods.getWaveEndTime()));
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
   * 入参： 12:13:14  出参：12:13
   * @param hourMinuteSecondStr
   * @return
   */
  private String getPdaShowTime(String hourMinuteSecondStr) {
    if(StringUtils.isBlank(hourMinuteSecondStr)) {
      return null;
    }
    String[] timeArr = hourMinuteSecondStr.split(Constants.SEPARATOR_COLON);
    if(timeArr.length != 3) {
      return null;
    }
    StringBuffer sb = new StringBuffer();
    sb.append(timeArr[0]).append(Constants.SEPARATOR_COLON).append(timeArr[1]);
    return sb.toString();

  }

  /**
   * 获取进行中任务倒计时
   * @param bizId
   * @param waveEndTime
   * @return
   */
  private Long getCountdownSeconds(String bizId, String waveEndTime) {
    if(StringUtils.isBlank(waveEndTime)) {
      return 0l;
    }
    String[] str = waveEndTime.split(":");
    Integer hour = Integer.valueOf(str[0]);
    Integer minute = Integer.valueOf(str[1]);
    Integer second = Integer.valueOf(str[2]);

    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    c.set(Calendar.HOUR_OF_DAY, hour);
    c.set(Calendar.MINUTE, minute);
    c.set(Calendar.SECOND, second);
    c.set(Calendar.MILLISECOND, 0);
    //计划班次结束后30分钟找货任务开始，持续60分钟关闭
    Long taskPlanEndTime = c.getTime().getTime()
            + FindGoodsConstants.PLAN_INVENTORY_TASK_START_INTERVAL_MINUTES * 60 * 1000l
            + FindGoodsConstants.PLAN_INVENTORY_TASK_DURATION_MINUTES * 60l * 1000l;
    Long currentTime = System.currentTimeMillis();
    Long countdown = taskPlanEndTime - currentTime;
    Long oneDayStamps = 24l * 3600l * 1000l;
    Long res = 0l;

    if(countdown > 0) {
      if(countdown > oneDayStamps) {
        res = (countdown - oneDayStamps) / 1000;
      }
      res = countdown / 1000;
    }
    if (log.isInfoEnabled()) {
      log.info("找货任务倒计时，bizId={},批次结束时间为{}，当前时间={}。倒计时秒={}", bizId, waveEndTime, currentTime, res);
    }
   return res;
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
  public InvokeResult<InventoryTaskRes> findInventoryTaskByBizId(InventoryTaskQueryReq request) {
    InvokeResult<InventoryTaskRes> res = new InvokeResult<>();
    res.success();
    InventoryTaskRes resData = new InventoryTaskRes();
    resData.setClientAutoRefreshConfig(this.getClientAutoRefreshConfig());

    JyBizTaskFindGoods jyBizTaskFindGoods = jyBizTaskFindGoodsDao.findByBizId(request.getBizId());
    if(Objects.isNull(jyBizTaskFindGoods)) {
      res.setMessage(InvokeResult.RESULT_NULL_MESSAGE);
      return res;
    }
    InventoryTaskDto inventoryTaskDto = this.convertInventoryTaskDto(jyBizTaskFindGoods);
    resData.setInventoryTaskDto(inventoryTaskDto);
    res.setData(resData);
    return res;
  }

  private ClientAutoRefreshConfig getClientAutoRefreshConfig() {
    try {
      return uccConfig.getJyWorkAppAutoRefreshConfigByBusinessType(ClientAutoRefreshBusinessTypeEnum.FIND_GOODS_TASK_PROGRESS.name());
    }catch (Exception ex) {
      log.error("找货刷新间隔获取错误，errMsg={}", ex.getMessage(), ex);
    }
    return null;
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
    if(Boolean.TRUE.equals(request.getOnlyHistoryComplete())) {
      dbQuery.setTaskStatus(InventoryTaskStatusEnum.COMPLETE.getCode());
    }

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
    res.setData(resData);
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
    dbQuery.setTaskStatus(InventoryTaskStatusEnum.COMPLETE.getCode());
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
      //图片删除
      this.deleteByBizIdAndSiteCode(request);
      //照片存储逻辑
      this.savePhoto(request);

      if (StringUtils.isBlank(findGoods.getPhotoStatus()) || !findGoods.getPhotoStatus().contains(request.getPhotoPosition().toString())) {
        JyBizTaskFindGoods dbUpdate = new JyBizTaskFindGoods();
        dbUpdate.setUpdateTime(new Date());
        dbUpdate.setBizId(request.getBizId());
        if(StringUtils.isBlank(findGoods.getPhotoStatus())) {
          dbUpdate.setPhotoStatus(request.getPhotoPosition().toString());
        }else {
          dbUpdate.setPhotoStatus(findGoods.getPhotoStatus().concat(request.getPhotoPosition().toString()));
        }
        jyBizTaskFindGoodsDao.updatePhotoStatus(dbUpdate);
      }
    }catch (Exception ex) {
      log.error("{}服务异常；req={},errMsg={}", methodDesc, JsonHelper.toJson(request), ex.getMessage(), ex);
      throw new JyBizException("找货上传照片服务异常");
    }finally {
      jyFindGoodsCacheService.unlockTaskByBizId(request.getBizId());
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
  private void deleteByBizIdAndSiteCode(InventoryTaskPhotographReq request) {
    JyAttachmentDetailEntity condition = new JyAttachmentDetailEntity();
    condition.setBizId(request.getBizId());
    condition.setSiteCode(request.getCurrentOperate().getSiteCode());
    condition.setBizType(FindGoodsConstants.PHOTOGRAPH_TYPE);
    condition.setBizSubType(request.getPhotoPosition().toString());
    condition.setUpdateUserErp(request.getUser().getUserErp());
    jyAttachmentDetailDao.delete(condition);
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
      inventoryDetailDtoList.add(detailDto);
    });
    resData.setInventoryDetailDtoList(inventoryDetailDtoList);

    return res;
  }

  @Override
  public InvokeResult<FindGoodsTaskDto> findWaveTask(FindGoodsTaskQueryDto dto) {
    JyBizTaskFindGoods jyBizTaskFindGoods = jyBizTaskFindGoodsDao.findWaveTask(dto);
    if (ObjectHelper.isNotNull(jyBizTaskFindGoods)){
      FindGoodsTaskDto findGoodsTaskDto = BeanUtils.copy(jyBizTaskFindGoods,FindGoodsTaskDto.class);
      return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,findGoodsTaskDto);
    }
    return new InvokeResult(NO_FINDGOODS_TASK_DATA_CODE,NO_FINDGOODS_TASK_DATA_MESSAGE);
  }

  @Override
  public int saveFindGoodsTask(FindGoodsTaskDto findGoodsTaskDto) {
    JyBizTaskFindGoods jyBizTaskFindGoods =BeanUtils.copy(findGoodsTaskDto,JyBizTaskFindGoods.class);
    return jyBizTaskFindGoodsDao.insertSelective(jyBizTaskFindGoods);
  }

  @Override
  public FindGoodsTaskDto findTaskByBizId(String findGoodsTaskBizId) {
    JyBizTaskFindGoods jyBizTaskFindGoods= jyBizTaskFindGoodsDao.findByBizId(findGoodsTaskBizId);
    if (ObjectHelper.isNotNull(jyBizTaskFindGoods)){
      return BeanUtils.copy(jyBizTaskFindGoods,FindGoodsTaskDto.class);
    }
    return null;
  }


  @Override
  public boolean distributWaitFindPackage(DistributPackageDto dto,FindGoodsTaskDto findGoodsTaskDto) {
    List<JyBizTaskFindGoodsDetail> findGoodsDetailList =assembleFindGoodsDetailList(dto,findGoodsTaskDto);
    List<JyBizTaskFindGoodsDetail> hasBeenDistributed =jyBizTaskFindGoodsDetailDao.listFindGoodsDetail(findGoodsDetailList);
    if (CollectionUtils.isNotEmpty(hasBeenDistributed)){
      findGoodsDetailList =findGoodsDetailList.stream().filter(jyBizTaskFindGoodsDetail ->  !checkHasBeenDistributed(jyBizTaskFindGoodsDetail,hasBeenDistributed)).collect(Collectors.toList());
    }
    return jyBizTaskFindGoodsDetailDao.batchInsert(findGoodsDetailList) >0 ;
  }

  private List<JyBizTaskFindGoodsDetail> assembleFindGoodsDetailList(DistributPackageDto dto,FindGoodsTaskDto findGoodsTaskDto) {
    Date now =new Date();
    return dto.getWaitFindPackageDtoList().stream().map(waitFindPackageDto -> {
      JyBizTaskFindGoodsDetail jyBizTaskFindGoodsDetail =new JyBizTaskFindGoodsDetail();
      jyBizTaskFindGoodsDetail.setFindGoodsTaskBizId(dto.getFindGoodsTaskBizId());
      jyBizTaskFindGoodsDetail.setPackageCode(waitFindPackageDto.getPackageCode());
      jyBizTaskFindGoodsDetail.setFindStatus(InventoryDetailStatusEnum.EXCEPTION.getCode());
      jyBizTaskFindGoodsDetail.setFindType(waitFindPackageDto.getFindType());
      jyBizTaskFindGoodsDetail.setSiteCode(Long.valueOf(dto.getCurrentOperate().getSiteCode()));
      jyBizTaskFindGoodsDetail.setWaveStartTime(findGoodsTaskDto.getWaveStartTime());
      jyBizTaskFindGoodsDetail.setWaveEndTime(findGoodsTaskDto.getWaveEndTime());
      jyBizTaskFindGoodsDetail.setCreateUserErp("sys");
      jyBizTaskFindGoodsDetail.setCreateUserName("系统分配");
      jyBizTaskFindGoodsDetail.setUpdateUserErp("sys");
      jyBizTaskFindGoodsDetail.setUpdateUserName("系统分配");
      jyBizTaskFindGoodsDetail.setCreateTime(now);
      jyBizTaskFindGoodsDetail.setUpdateTime(now);
      return jyBizTaskFindGoodsDetail;
    }).collect(Collectors.toList());
  }

  private boolean checkHasBeenDistributed(JyBizTaskFindGoodsDetail jyBizTaskFindGoodsDetail, List<JyBizTaskFindGoodsDetail> hasBeenDistributed) {
    for (JyBizTaskFindGoodsDetail distributedGoodsDetail :hasBeenDistributed){
      if (jyBizTaskFindGoodsDetail.getFindGoodsTaskBizId().equals(distributedGoodsDetail.getFindGoodsTaskBizId())
          && jyBizTaskFindGoodsDetail.getPackageCode().equals(distributedGoodsDetail.getPackageCode())) {
      return true;
      }
    }
    return false;
  }

  @Override
  public boolean updateTaskStatistics(FindGoodsTaskDto findGoodsTaskDto) {
    JyBizTaskFindGoodsDetailQueryDto shouldFindParams =assembleShouldFindParams(findGoodsTaskDto);
    Integer shouldFindCount =jyBizTaskFindGoodsDetailDao.countInventoryDetail(shouldFindParams);
    JyBizTaskFindGoodsDetailQueryDto haveFindParams =assembleHaveFindParams(findGoodsTaskDto);
    Integer haveFindCount =jyBizTaskFindGoodsDetailDao.countInventoryDetail(haveFindParams);

    JyBizTaskFindGoods jyBizTaskFindGoods =new JyBizTaskFindGoods();
    jyBizTaskFindGoods.setId(findGoodsTaskDto.getId());
    jyBizTaskFindGoods.setWaitFindCount(null == shouldFindCount?Constants.NUMBER_ZERO:shouldFindCount);
    jyBizTaskFindGoods.setHaveFindCount(null == haveFindCount?Constants.NUMBER_ZERO:haveFindCount);
    jyBizTaskFindGoods.setUpdateUserErp(findGoodsTaskDto.getUpdateUserErp());
    jyBizTaskFindGoods.setUpdateUserName(findGoodsTaskDto.getUpdateUserName());
    jyBizTaskFindGoods.setUpdateTime(new Date());
    int rs =jyBizTaskFindGoodsDao.updateByPrimaryKeySelective(jyBizTaskFindGoods);
    if (rs > 0){
      return true;
    }
    return false;
  }

  private JyBizTaskFindGoodsDetailQueryDto assembleHaveFindParams(FindGoodsTaskDto findGoodsTaskDto) {
    JyBizTaskFindGoodsDetailQueryDto dto =new JyBizTaskFindGoodsDetailQueryDto();
    dto.setFindGoodsTaskBizId(findGoodsTaskDto.getBizId());
    List<Integer> statusList =new ArrayList<>();
    statusList.add(InventoryDetailStatusEnum.FIND_GOOD.getCode());
    statusList.add(InventoryDetailStatusEnum.PDA_REAL_OPERATE.getCode());
    dto.setStatusList(statusList);
    return dto;
  }

  private JyBizTaskFindGoodsDetailQueryDto assembleShouldFindParams(FindGoodsTaskDto findGoodsTaskDto) {
    JyBizTaskFindGoodsDetailQueryDto dto =new JyBizTaskFindGoodsDetailQueryDto();
    dto.setFindGoodsTaskBizId(findGoodsTaskDto.getBizId());
    return dto;
  }


  @Override
  public boolean updateWaitFindPackage(UpdateWaitFindPackageStatusDto dto,
      FindGoodsTaskDto findGoodsTaskDto) {
    return false;
  }
}
