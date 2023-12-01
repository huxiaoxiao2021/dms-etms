package com.jd.bluedragon.distribution.jy.service.findgoods;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.inventory.enums.InventoryTaskStatusEnum;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.core.base.HrUserManager;
import com.jd.bluedragon.core.base.MspClientProxy;
import com.jd.bluedragon.distribution.api.request.SingleSiteWaveQuery;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JyFindGoodsJsfService;
import com.jd.bluedragon.distribution.jy.dto.findgoods.CreateFindGoodsTask;
import com.jd.bluedragon.distribution.jy.dto.findgoods.DistributPackageDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.FindGoodsTaskDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.FindGoodsTaskQueryDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.SingleSiteWaveDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.UpdateWaitFindPackageStatusDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.WaitFindPackageDto;
import com.jd.bluedragon.distribution.jy.dto.findgoods.WaitFindPackageQueryDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.findgoods.JyBizTaskFindGoods;
import com.jd.bluedragon.distribution.jy.manager.JyFindGoodsManager;
import com.jd.bluedragon.distribution.station.dao.UserSignRecordDao;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.dms.workbench.utils.core.common.DateUtil;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.udata.query.api.dto.ApiDataQueryRequest;
import com.jd.udata.query.api.dto.ApiDataQueryResult;
import com.jd.udata.query.api.service.ApiQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;

@Slf4j
@Service("jyFindGoodsJsfService")
public class JyFindGoodsJsfServiceImpl implements JyFindGoodsJsfService {

  @Autowired
  JyFindGoodsManager jyFindGoodsManager;
  @Autowired
  JyFindGoodsService jyFindGoodsService;
  @Autowired
  JimDbLock jimDbLock;
  @Autowired
  @Qualifier("redisJySendBizIdSequenceGen")
  private JimdbSequenceGen redisJyBizIdSequenceGen;
  
  @Autowired
  private UserSignRecordDao userSignRecordDao;
  
  @Autowired
  private HrUserManager hrUserManager;

  @Autowired
  @Qualifier("jimdbCacheService")
  private CacheService cacheService;

  @Autowired
  private ApiQueryService apiQueryService;
  
  @Value("${sortingClean.udata.appToken}")
  private String udataAppToken;

  @Value("${sortingClean.udata.apiGroupName}")
  private String apiGroupName;

  @Value("${sortingClean.udata.apiName.sortingCleanSingleSiteWave}")
  private String sortingCleanSingleSiteWave;
  @Autowired
  private MspClientProxy mspClientProxy;
  
  @Value("${find.good.send.message.default.erp:}")
  private String findGoodSendMessageDErp;
    

  @Override
  public InvokeResult<PagerResult<WaitFindPackageDto>> listWaitFindPackage(
      WaitFindPackageQueryDto waitFindPackageQueryDto) {
    PagerResult<WaitFindPackageDto> rs = jyFindGoodsManager
        .listWaitFindPackage(waitFindPackageQueryDto);
    if (ObjectHelper.isNotNull(rs)) {
      return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, rs);
    }
    return new InvokeResult<>(NO_WAITFIND_DATA_CODE, NO_WAITFIND_DATA_MESSAGE);
  }

  @Override
  public InvokeResult<FindGoodsTaskDto> createFindGoodsTask(CreateFindGoodsTask dto) {
    checkCreateTaskDto(dto);
    //按场地+日期+波次
    String createFindGoodsTaskLock = String
        .format(Constants.JY_CREATE_FINDGOODS_TASK_LOCK_PREFIX, dto.getWorkGridKey(), dto.getDate(),
            dto.getWaveStartTime(), dto.getWaveEndTime());
    if (!jimDbLock.lock(createFindGoodsTaskLock, dto.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
      throw new JyBizException("创建找货任务繁忙,请稍后再试！");
    }
    try {
      FindGoodsTaskQueryDto findGoodsTaskQueryDto = assembleFindGoodsTaskQueryDto(dto);
      InvokeResult<FindGoodsTaskDto> result = jyFindGoodsService.findWaveTask(findGoodsTaskQueryDto);
      if (ObjectHelper.isNotNull(result) && result.codeSuccess()) {
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, result.getData());
      }
      FindGoodsTaskDto findGoodsTaskDto = assembleFindGoodsTaskDto(dto);
      int rs = jyFindGoodsService.saveFindGoodsTask(findGoodsTaskDto);
      if (rs > 0) {
        findGoodsTaskDto.setNewCreateFlag(true);
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, findGoodsTaskDto);
      }
      return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    } finally {
      jimDbLock.releaseLock(createFindGoodsTaskLock,dto.getRequestId());
    }
  }

  private String genFindGoodsTaskBizId() {
    String ownerKey = String.format(
        JyBizTaskFindGoods.BIZ_PREFIX,
        DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
    return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
  }

  private FindGoodsTaskDto assembleFindGoodsTaskDto(CreateFindGoodsTask dto) {
    FindGoodsTaskDto findGoodsTaskDto = new FindGoodsTaskDto();
    Date today = new Date();
    findGoodsTaskDto.setBizId(genFindGoodsTaskBizId());
    findGoodsTaskDto.setSiteCode(Long.valueOf(dto.getSiteCode()));
    findGoodsTaskDto.setWorkGridKey(dto.getWorkGridKey());
    findGoodsTaskDto.setTaskDate(DateHelper.getDateOfyyMMdd2());
    findGoodsTaskDto.setWaveStartTime(dto.getWaveStartTime());
    findGoodsTaskDto.setWaveEndTime(dto.getWaveEndTime());
    findGoodsTaskDto.setTaskStatus(InventoryTaskStatusEnum.ONGOING.getCode());
    findGoodsTaskDto.setCreateUserErp("sys");
    findGoodsTaskDto.setCreateUserName("系统分配");
    findGoodsTaskDto.setUpdateUserErp("sys");
    findGoodsTaskDto.setUpdateUserName("系统分配");
    findGoodsTaskDto.setCreateTime(today);
    findGoodsTaskDto.setUpdateTime(today);
    return findGoodsTaskDto;
  }

  private FindGoodsTaskQueryDto assembleFindGoodsTaskQueryDto(CreateFindGoodsTask dto) {
    FindGoodsTaskQueryDto findGoodsTaskQueryDto = BeanUtils.copy(dto, FindGoodsTaskQueryDto.class);
    return findGoodsTaskQueryDto;
  }

  @Override
  @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public InvokeResult distributWaitFindPackage(DistributPackageDto dto) {
    checkDistributPackageDto(dto);
    FindGoodsTaskDto findGoodsTaskDto =jyFindGoodsService.findTaskByBizId(dto.getFindGoodsTaskBizId());
    if (ObjectHelper.isEmpty(findGoodsTaskDto)){
      throw new JyBizException("未找到对应的找货任务！");
    }
    if (InventoryTaskStatusEnum.COMPLETE.getCode().equals(findGoodsTaskDto.getTaskStatus())){
      throw new JyBizException("任务已结束，不允许再分配包裹！");
    }
    String findGoodsTaskLock = String.format(Constants.JY_FINDGOODS_TASK_LOCK_PREFIX, dto.getFindGoodsTaskBizId());
    if (!jimDbLock.lock(findGoodsTaskLock, dto.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
      throw new JyBizException("任务分配找货明细繁忙,请稍后再试！");
    }
    try {
      jyFindGoodsService.distributWaitFindPackage(dto,findGoodsTaskDto);
      jyFindGoodsService.updateTaskStatistics(findGoodsTaskDto);
      return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    } finally {
      jimDbLock.releaseLock(findGoodsTaskLock,dto.getRequestId());
    }
  }

  private void checkDistributPackageDto(DistributPackageDto dto) {
    if (ObjectHelper.isEmpty(dto.getFindGoodsTaskBizId())){
      throw new JyBizException("参数错误：缺失任务bizId");
    }
    if (CollectionUtils.isEmpty(dto.getWaitFindPackageDtoList())){
      throw new JyBizException("参数错误：缺失找货任务列表");
    }
  }

  private void checkCreateTaskDto(CreateFindGoodsTask dto) {
    if (ObjectHelper.isEmpty(dto.getSiteCode())) {
      throw new JyBizException("参数错误：缺失场地编码！");
    }
    if (ObjectHelper.isEmpty(dto.getWorkGridKey())) {
      throw new JyBizException("参数错误：缺失网格key！");
    }
    if (ObjectHelper.isEmpty(dto.getDate())) {
      throw new JyBizException("参数错误：缺失日期参数！");
    }
    if (ObjectHelper.isEmpty(dto.getWaveStartTime())) {
      throw new JyBizException("参数错误：缺失波次起始时间！");
    }
    if (ObjectHelper.isEmpty(dto.getWaveEndTime())) {
      throw new JyBizException("参数错误：缺失波次结束时间！");
    }
  }

  @Override
  public InvokeResult updateWaitFindPackageStatus(UpdateWaitFindPackageStatusDto dto) {
    checkUpdateWaitFindPackageStatus(dto);
    FindGoodsTaskDto findGoodsTaskDto =jyFindGoodsService.findTaskByBizId(dto.getFindGoodsTaskBizId());
    if (ObjectHelper.isEmpty(findGoodsTaskDto)){
      throw new JyBizException("未找到对应的找货任务！");
    }
    if (InventoryTaskStatusEnum.COMPLETE.getCode().equals(findGoodsTaskDto.getTaskStatus())){
      throw new JyBizException("任务已结束，不允许再更改包裹状态！");
    }
    String findGoodsTaskLock = String.format(Constants.JY_FINDGOODS_TASK_LOCK_PREFIX, dto.getFindGoodsTaskBizId());
    if (!jimDbLock.lock(findGoodsTaskLock, dto.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
      throw new JyBizException("任务更新找货明细繁忙,请稍后再试！");
    }
    try {
      jyFindGoodsService.updateWaitFindPackage(dto,findGoodsTaskDto);
      jyFindGoodsService.updateTaskStatistics(findGoodsTaskDto);
      return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    } finally {
      jimDbLock.releaseLock(findGoodsTaskLock,dto.getRequestId());
    }
  }

  private void checkUpdateWaitFindPackageStatus(UpdateWaitFindPackageStatusDto dto) {
    if (ObjectHelper.isEmpty(dto.getFindGoodsTaskBizId())){
      throw new JyBizException("参数错误：缺失任务bizId！");
    }
    if (CollectionUtils.isEmpty(dto.getWaitFindPackageDtoList())){
      throw new JyBizException("参数错误：缺失待更新包裹列表！");
    }
  }

  @Override
  public InvokeResult updateFindGoodsStatus(FindGoodsTaskDto dto) {
    checkUpdateFindGoodsTaskDto(dto);
    FindGoodsTaskDto findGoodsTaskDto =jyFindGoodsService.findTaskByBizId(dto.getBizId());
    if (ObjectHelper.isEmpty(findGoodsTaskDto)){
      throw new JyBizException("未找到对应的找货任务！");
    }
    if (InventoryTaskStatusEnum.COMPLETE.getCode().equals(findGoodsTaskDto.getTaskStatus())){
      throw new JyBizException("任务已结束，不允许再更改包裹状态！");
    }
    dto.setId(findGoodsTaskDto.getId());
    boolean success =jyFindGoodsService.updateFindGoodsStatus(dto);
    if (success){
      return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE);
    }
    return new InvokeResult(SERVER_ERROR_CODE,SERVER_ERROR_MESSAGE);
  }
  private void checkUpdateFindGoodsTaskDto(FindGoodsTaskDto findGoodsTaskDto) {
  }


  @Override
  public void findGoodsNotice(FindGoodsTaskDto findGoodsTaskDto) {
    try {
      log.info("JyFindGoodsJsfServiceImpl.FindGoodsNotice findGoodsTaskDto:{}", JSON.toJSONString(findGoodsTaskDto));
      if (StringUtils.isBlank(cacheService.get(getFindGoodsNoticeCacheKey(findGoodsTaskDto)))) {
          String sendErp = getLeaderErp(findGoodsTaskDto);
          if (StringUtils.isNotBlank(findGoodSendMessageDErp)) {
            sendErp = findGoodSendMessageDErp;
          }
          log.info("JyFindGoodsJsfServiceImpl.FindGoodsNotice sendErp:{}", sendErp);
          if (StringUtils.isNotBlank(sendErp)) {
              // send message
              this.sendFindGoodsMessage(findGoodsTaskDto, sendErp);
              // record cache
              cacheService.setEx(getFindGoodsNoticeCacheKey(findGoodsTaskDto),"1",1L,TimeUnit.DAYS);
          }
      }
    } catch (Exception e) {
      log.info("JyFindGoodsJsfServiceImpl.sendFindGoodsMessage error:{}", e.getMessage());
    } finally {
      log.info("findGoodsNotice run finish!");
    }
  }

  private void sendFindGoodsMessage(FindGoodsTaskDto findGoodsTaskDto, String sendErp) {
    SingleSiteWaveDto siteWaveDto = querySingleSiteWave(findGoodsTaskDto);
    if (siteWaveDto == null) {
      log.error("JyFindGoodsJsfServiceImpl.sendFindGoodsMessage  siteWaveDto is null");
      cacheService.del(getFindGoodsNoticeCacheKey(findGoodsTaskDto));
    }
    String content = String.format(Constants.FIND_GOODS_NOTICE_CONTENT,
            siteWaveDto.getSortingSiteName(),
            DateUtil.format(new Date(),DateUtil.FORMATE_MONTH_DAY),
            findGoodsTaskDto.getWaveStartTime(),
            findGoodsTaskDto.getWaveEndTime(),
            siteWaveDto.getWaitFindCount(),
            siteWaveDto.getHaveFindCount(),
            siteWaveDto.getNotFindCount(),
            siteWaveDto.getNotFindHighValueCount(),
            siteWaveDto.getNotFindExpressCount(),
            siteWaveDto.getNotFindFreshCount()
            );
    Set<String> pins = new HashSet<>();
    pins.add(sendErp);
    log.info("JyFindGoodsJsfServiceImpl.sendFindGoodsMessage sendErp:{},content:{}", sendErp, content);
    mspClientProxy.sendTimeline(Constants.FIND_GOODS_NOTICE_TITLE, content, null, pins, false);
  }
  
  private SingleSiteWaveDto querySingleSiteWave(FindGoodsTaskDto findGoodsTaskDto) {
    SingleSiteWaveQuery query = new SingleSiteWaveQuery();
    query.setSortingSiteCode(findGoodsTaskDto.getSiteCode());
    query.setWaveDate(DateUtil.formatDate(new Date()));
    query.setWaveBeginTm(findGoodsTaskDto.getWaveStartTime());
    query.setWaveEndTm(findGoodsTaskDto.getWaveEndTime());
    Map<String, Object> params = ObjectMapHelper.convertObject2Map(query);
    ApiDataQueryRequest request = new ApiDataQueryRequest();
    request.setApiName(sortingCleanSingleSiteWave);
    request.setAppToken(udataAppToken);
    request.setApiGroupName(apiGroupName);
    request.setParams(params);
    log.info("JyFindGoodsJsfServiceImpl.querySingleSiteWave request:{}", JSON.toJSONString(request));
    ApiDataQueryResult apiDataQueryResult = apiQueryService.apiDataQuery(request);
    log.info("JyFindGoodsJsfServiceImpl.querySingleSiteWave apiDataQueryResult:{}", JSON.toJSONString(apiDataQueryResult));
    if (apiDataQueryResult.getCode() == null || !apiDataQueryResult.getCode().equals(200)) {
      return null;
    }
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.convertValue(apiDataQueryResult.getData().get(0), SingleSiteWaveDto.class);
  }

  private String getFindGoodsNoticeCacheKey(FindGoodsTaskDto findGoodsTaskDto){
    return String.format(Constants.FIND_GOODS_NOTICE_CACHE_KEY, findGoodsTaskDto.getSiteCode(), DateUtil.formatDate(new Date()),
            findGoodsTaskDto.getWaveStartTime(), findGoodsTaskDto.getWaveEndTime());
  }
  private String getLeaderErp(FindGoodsTaskDto findGoodsTaskDto) {
    UserSignRecord userSignRecord = null;
    try {
      UserSignRecordQuery query = new UserSignRecordQuery();
      query.setSignDateStr(DateUtil.formatDate(new Date()));
      query.setSiteCode(Integer.valueOf(findGoodsTaskDto.getSiteCode().toString()));
      userSignRecord = userSignRecordDao.queryFirstExistGridRecord(query);
    } catch (Exception e) {
      log.error("JyFindGoodsJsfServiceImpl.leaderErp  queryFirstExistGridRecord error:{}",e.getMessage());
    }
    if (userSignRecord == null) {
      return null;
    }
    return hrUserManager.getSuperiorErp(userSignRecord.getUserCode());
  }
}
