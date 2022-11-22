package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.jsf.cross.SortCrossJsfManager;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyGroupSortCrossDetailDao;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.distribution.middleend.sorting.dao.DynamicSortingQueryDao;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptOnlineStatusEnum;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.cross.*;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import com.jd.etms.waybill.domain.Waybill;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.utils.TimeUtils.yyyyMMdd;

@Service
@Slf4j
public class JyComBoardSendServiceImpl implements JyComBoardSendService {

  @Autowired
  private SortCrossJsfManager sortCrossJsfManager;

  @Autowired
  private WaybillQueryManager waybillQueryManager;
  @Autowired
  private SortingCheckService sortingCheckService;
  @Autowired
  private BoxService boxService;
  @Autowired
  private VirtualBoardService virtualBoardService;
  @Autowired
  private BaseService baseService;
  @Resource
  private CycleBoxService cycleBoxService;
  @Resource
  private FuncSwitchConfigService funcSwitchConfigService;
  @Autowired
  private DynamicSortingQueryDao dynamicSortingQueryDao;
  @Autowired
  JyGroupSortCrossDetailDao jyGroupSortCrossDetailDao;

  @Autowired
  private JyGroupSortCrossDetailService jyGroupSortCrossDetailService;
  @Autowired
  JyComboardAggsService jyComboardAggsService;
  @Autowired
  JyBizTaskComboardService jyBizTaskComboardService;
  @Autowired
  JimDbLock jimDbLock;
  @Autowired
  UccPropertyConfiguration ucc;

  @Autowired
  @Qualifier("redisClientOfJy")
  protected Cluster redisClientCache;

  @Override
  public InvokeResult<CrossDataResp> listCrossData(CrossDataReq request) {
    log.info("开始获取场地滑道信息：{}", JsonHelper.toJson(request));
    CrossPageQuery query = new CrossPageQuery();
    if (!checkBaseRequest(request)) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    InvokeResult<CrossDataResp> result = new InvokeResult<>();
    CrossDataResp crossDataResp = new CrossDataResp();
    result.setData(crossDataResp);
    query.setDmsId(request.getCurrentOperate().getSiteCode());
    query.setPageNumber(request.getPageNo());
    query.setLimit(request.getPageSize());
    CrossDataJsfResp crossDataJsfResp = sortCrossJsfManager.queryCrossDataByDmsCode(query);
    if (crossDataJsfResp != null) {
      crossDataResp.setCrossCodeList(crossDataJsfResp.getCrossCodeList());
      crossDataResp.setTotalPage(crossDataJsfResp.getTotalPage());
    }
    return result;
  }

  @Override
  public InvokeResult<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request) {
    log.info("开始获取笼车营业部信息：{}", JsonHelper.toJson(request));
    if (!checkBaseRequest(request)) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    InvokeResult<TableTrolleyResp> result = new InvokeResult<>();
    TableTrolleyResp tableTrolleyResp = new TableTrolleyResp();
    result.setData(tableTrolleyResp);

    TableTrolleyQuery query = new TableTrolleyQuery();
    query.setDmsId(request.getCurrentOperate().getSiteCode());
    query.setPageNumber(request.getPageNo());
    query.setLimit(request.getPageSize());
    TableTrolleyJsfResp tableTrolleyJsfResp;
    if (!StringUtils.isEmpty(request.getCrossCode())) {
      // 根据滑道查询笼车信息
      query.setCrossCode(request.getCrossCode());
      tableTrolleyJsfResp = sortCrossJsfManager.queryTableTrolleyListByCrossCode(query);
    } else {
      // 根据场地查询笼车信息
      tableTrolleyJsfResp = sortCrossJsfManager.queryTableTrolleyListByDmsId(query);
    }
    if (tableTrolleyJsfResp != null && !CollectionUtils
        .isEmpty(tableTrolleyJsfResp.getTableTrolleyDtoJsfList())) {
      tableTrolleyResp.setTableTrolleyDtoList(
          getTableTrolleyDto(tableTrolleyJsfResp.getTableTrolleyDtoJsfList()));
      tableTrolleyResp.setTotalPage(tableTrolleyJsfResp.getTotalPage());

      if (request.getNeedMatchGroupCTT() && ObjectHelper.isNotNull(request.getTemplateCode())) {
        JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
        condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        condition.setTemplateCode(request.getTemplateCode());
        List<JyGroupSortCrossDetailEntity> groupSortCrossDetailList = jyGroupSortCrossDetailService
            .listSendFlowByTemplateCode(condition);

        if (ObjectHelper.isNotNull(groupSortCrossDetailList)) {
          for (JyGroupSortCrossDetailEntity entity : groupSortCrossDetailList) {
            for (TableTrolleyDto dto : tableTrolleyResp.getTableTrolleyDtoList()) {
              if (entity.getEndSiteId().intValue() == dto.getEndSiteId()) {
                dto.setSelectedFlag(true);
              }
            }
          }
        }
      }
    }
    return result;
  }

  public static void main(String[] args) {
    Long a = 1L;
    Integer b = 1;
    System.out.println(a.intValue() == b);
  }

  private List<TableTrolleyDto> getTableTrolleyDto(
      List<TableTrolleyJsfDto> tableTrolleyDtoJsfList) {
    List<TableTrolleyDto> tableTrolleyDtoList = new ArrayList<>();
    for (TableTrolleyJsfDto tableTrolleyJsfDto : tableTrolleyDtoJsfList) {
      TableTrolleyDto tableTrolleyDto = new TableTrolleyDto();
      tableTrolleyDto.setTableTrolleyCode(tableTrolleyJsfDto.getTableTrolleyCode());
      tableTrolleyDto.setCrossCode(tableTrolleyJsfDto.getCrossCode());
      tableTrolleyDto.setEndSiteId(tableTrolleyJsfDto.getEndSiteId());
      tableTrolleyDto.setEndSiteName(tableTrolleyJsfDto.getEndSiteName());
      tableTrolleyDtoList.add(tableTrolleyDto);
    }
    return tableTrolleyDtoList;
  }

  @Override
  public InvokeResult<CreateGroupCTTResp> createGroupCTTData(CreateGroupCTTReq request) {
    if (!checkBaseRequest(request)) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    log.info("开始保存本场地常用的笼车集合：{}", JsonHelper.toJson(request));
    CreateGroupCTTResp resp = jyGroupSortCrossDetailService.batchInsert(request);
    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  private String getGroupCTTName(BaseReq req) {
    String templateNoKey =
        "CTT:" + req.getCurrentOperate().getSiteCode() + ":" + req.getGroupCode();
    long templateNo = 0;
    if (!ObjectHelper.isNotNull(redisClientCache.get(templateNoKey))) {
      redisClientCache.set(templateNoKey, "0", false);
    }
    try {
      templateNo = redisClientCache.incr(templateNoKey);
    } catch (Exception e) {
      return "";
    }
    return String.valueOf(templateNo);
  }

  @Override
  public InvokeResult<CreateGroupCTTResp> getDefaultGroupCTTName(BaseReq request) {
    CreateGroupCTTResp createGroupCTTResp = new CreateGroupCTTResp();
    String groupName = "混扫" + getGroupCTTName(request);
    createGroupCTTResp.setTemplateName(groupName);
    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, createGroupCTTResp);
  }

  @Override
  public InvokeResult<TableTrolleyResp> querySendFlowByBarCode(QuerySendFlowReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBarCode()) || !checkBarCode(
        request.getBarCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    log.info("开始根据包裹号,箱号或滑道-笼车号获取流向信息：{}", JsonHelper.toJson(request));
    TableTrolleyResp tableTrolleyResp = new TableTrolleyResp();
    TableTrolleyJsfResp tableTrolleyJsfResp = null;
    String barCode = request.getBarCode();
    TableTrolleyQuery query = new TableTrolleyQuery();
    query.setDmsId(request.getCurrentOperate().getSiteCode());
    if (WaybillUtil.isPackageCode(barCode)) {
      final Waybill waybill = waybillQueryManager
          .getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCodeByPackCode(barCode));
      if (waybill == null) {
        throw new JyBizException("未查找到运单数据");
      }
      if (waybill.getOldSiteId() == null) {
        throw new JyBizException("运单对应的预分拣站点为空");
      }
      query.setDmsId(request.getCurrentOperate().getSiteCode());
      query.setSiteCode(waybill.getOldSiteId());
      tableTrolleyJsfResp = sortCrossJsfManager.queryCTTByStartEndSiteCode(query);
    } else if (BusinessHelper.isBoxcode(barCode)) {
      final Box box = boxService.findBoxByCode(barCode);
      if (box == null) {
        throw new JyBizException("未找到对应箱号，请检查");
      }
      query.setSiteCode(box.getReceiveSiteCode());
      tableTrolleyJsfResp = sortCrossJsfManager.queryCTTByStartEndSiteCode(query);
    } else if (checkCTTCode(barCode)) {
      int index = barCode.indexOf("-");
      query.setCrossCode(barCode.substring(0, index));
      query.setTabletrolleyCode(barCode.substring(index + 1));
      tableTrolleyJsfResp = sortCrossJsfManager.queryCTTByCTTCode(query);
    }
    if (tableTrolleyJsfResp != null && !CollectionUtils
        .isEmpty(tableTrolleyJsfResp.getTableTrolleyDtoJsfList())) {
      tableTrolleyResp.setTableTrolleyDtoList(
          getTableTrolleyDto(tableTrolleyJsfResp.getTableTrolleyDtoJsfList()));
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, tableTrolleyResp);
  }

  /**
   * 校验barCode格式
   */
  private boolean checkBarCode(String barCode) {
    if (WaybillUtil.isPackageCode(barCode) || BusinessHelper.isBoxcode(barCode) || checkCTTCode(
        barCode)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  /**
   * 校验是否为滑道-笼车号
   */
  private boolean checkCTTCode(String barCode) {
    if (barCode.contains("-")) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  @Override
  public InvokeResult addCTT2Group(AddCTTReq request) {
    if (!checkBaseRequest(request)) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    log.info("开始更新常用滑道笼车流向集合：{}", JsonHelper.toJson(request));
    if (jyGroupSortCrossDetailService.addCTTGroup(request)) {
      return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    } else {
      return new InvokeResult(UPDATE_CTT_GROUP_LIST_CODE, UPDATE_CTT_GROUP_LIST_MESSAGE);
    }
  }

  private Boolean checkBaseRequest(BaseReq request) {
    if (request == null || request.getCurrentOperate() == null
        || request.getCurrentOperate().getSiteCode() < 0 ||
        request.getUser() == null || StringUtils.isEmpty(request.getUser().getUserErp())
        || StringUtils.isEmpty(request.getUser().getUserName())) {
      return Boolean.FALSE;
    }
    return Boolean.TRUE;
  }

  @Override
  public InvokeResult removeCTTFromGroup(RemoveCTTReq request) {
    if (!checkBaseRequest(request)) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    log.info("开始更新常用滑道笼车流向集合：{}", JsonHelper.toJson(request));
    if (jyGroupSortCrossDetailService.removeCTTFromGroup(request)) {
      return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    } else {
      return new InvokeResult(UPDATE_CTT_GROUP_LIST_CODE, UPDATE_CTT_GROUP_LIST_MESSAGE);
    }
  }

  @Override
  public InvokeResult<CTTGroupDataResp> listCTTGroupData(CTTGroupDataReq request) {
    if (!checkBaseRequest(request)) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    log.info("开始查询常用滑道笼车流向集合：{}", JsonHelper.toJson(request));
    CTTGroupDataResp resp = jyGroupSortCrossDetailService
        .queryCTTGroupDataByGroupOrSiteCode(request);
    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  @Override
  public InvokeResult<CTTGroupDataResp> queryCTTGroupByBarCode(QueryCTTGroupReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBarCode()) || !checkBarCode(
        request.getBarCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    log.info("开始根据包裹号,箱号或滑道-笼车号获取混扫任务信息：{}", JsonHelper.toJson(request));
    CTTGroupDataResp cttGroupDataResp = new CTTGroupDataResp();
    String barCode = request.getBarCode();
    JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
    entity.setGroupCode(request.getGroupCode());
    entity.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
    if (WaybillUtil.isPackageCode(barCode)) {
      final Waybill waybill = waybillQueryManager
          .getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCodeByPackCode(barCode));
      if (waybill == null) {
        throw new JyBizException("未查找到运单数据");
      }
      if (waybill.getOldSiteId() == null) {
        throw new JyBizException("运单对应的预分拣站点为空");
      }
      entity.setEndSiteId(waybill.getOldSiteId().longValue());
      cttGroupDataResp = jyGroupSortCrossDetailService.listGroupByEndSiteCodeOrCTTCode(entity);
    } else if (BusinessHelper.isBoxcode(barCode)) {
      final Box box = boxService.findBoxByCode(barCode);
      if (box == null) {
        throw new JyBizException("未找到对应箱号，请检查");
      }
      entity.setEndSiteId(box.getReceiveSiteCode().longValue());
      cttGroupDataResp = jyGroupSortCrossDetailService.listGroupByEndSiteCodeOrCTTCode(entity);
    } else if (checkCTTCode(barCode)) {
      int index = barCode.indexOf("-");
      entity.setCrossCode(barCode.substring(0, index));
      entity.setTabletrolleyCode(barCode.substring(index + 1));
      cttGroupDataResp = jyGroupSortCrossDetailService.listGroupByEndSiteCodeOrCTTCode(entity);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, cttGroupDataResp);
  }

  @Override
  public InvokeResult<SendFlowDataResp> listSendFlowUnderCTTGroup(SendFlowDataReq request) {
    return null;
  }

  @Override
  public InvokeResult<SendFlowDetailResp> querySendFlowDetail(SendFlowDetailReq request) {
    return null;
  }

  @Override
  public InvokeResult<BoardResp> queryBoardDetail(BoardReq request) {
    return null;
  }

  @Override
  public InvokeResult finishBoard(BoardReq request) {
    return null;
  }

  @Override
  public InvokeResult finishBoardsUnderCTTGroup(CTTGroupReq request) {
    return null;
  }

  @Override
  public InvokeResult<ComboardScanResp> comboardScan(ComboardScanReq request) {
    baseCheck(request);
    comboardCheck(request);
    getOrCreateBoardCode(request);
    sendSealCheck(request);

    execComboard(request);
    execSend(request);

    ComboardScanResp resp = new ComboardScanResp();
    resp.setEndSiteId(request.getEndSiteId());
    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  /**
   * 执行租板
   */
  private void execComboard(ComboardScanReq request) {
    //板加锁
  }

  /**
   * 执行发货
   */
  private void execSend(ComboardScanReq request) {
  }

  private void getOrCreateBoardCode(ComboardScanReq request) {
    /**
     * 流向加锁
     */
    String sendFlowLockKey = String.format(Constants.JY_COMBOARD_SENDFLOW_LOCK_PREFIX, request.getEndSiteId());
    if (!jimDbLock.lock(sendFlowLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
      throw new JyBizException("当前系统繁忙,请稍后再试！");
    }
    try {
      //查询当前流向是否存在进行中的板
      SendFlowDto sendFlowDto = new SendFlowDto();
      sendFlowDto.setStartSiteId(request.getCurrentOperate().getSiteCode());
      sendFlowDto.setEndSiteId(request.getEndSiteId());
      BoardDto boardDto = jyBizTaskComboardService.queryInProcessBoard(sendFlowDto);
      if (ObjectHelper.isNotNull(boardDto)) {
        if (!boardDto.getBulkFlag() && boardDto.getCount() < ucc.getJyComboardCountLimit()) {
          request.setBoardCode(boardDto.getBoardCode());
        } else {
          throw new JyBizException("已到上限，需要换新板");
        }
      } else {
        //生成新板任务
        generateNewBoard(request);
        //存储jy_task
        JyBizTaskComboardEntity record = assembleJyBizTaskComboardParam(request);
        //空板是不确定是大宗还是非大宗，组板成功后再确定
        jyBizTaskComboardService.save(record);
      }
    } finally {
      jimDbLock.releaseLock(sendFlowLockKey, request.getRequestId());
    }
  }

  private void generateNewBoard(ComboardScanReq request) {
  }

  private JyBizTaskComboardEntity assembleJyBizTaskComboardParam(ComboardScanReq request) {
    JyBizTaskComboardEntity record = new JyBizTaskComboardEntity();
    record.setBoardCode(request.getBoardCode());
    record.setBizId("");
    record.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
    record.setEndSiteId(Long.valueOf(request.getEndSiteId()));
    record.setStatus(1);
    return record;
  }

  private void baseCheck(ComboardScanReq request) {
    /**
     * 参数校验
     */
    if (!ObjectHelper.isNotNull(request.getTemplateCode())) {
      throw new JyBizException("参数错误：缺少混扫任务编号！");
    }
    if (!ObjectHelper.isNotNull(request.getScanType())) {
      throw new JyBizException("参数错误：请选择扫货方式！");
    }

    String barCode = request.getBarCode();
    if (!BusinessHelper.isBoxcode(barCode) && !WaybillUtil.isWaybillCode(barCode)
        && !WaybillUtil.isPackageCode(barCode)) {
      throw new JyBizException("请扫描正确的条形码：只支持扫描包裹、运单、箱号！");
    }
    int siteCode = request.getCurrentOperate().getSiteCode();
    if (!NumberHelper.gt0(siteCode)) {
      throw new JyBizException("缺少操作场地！");
    }

    if (StringUtils.isBlank(request.getGroupCode())) {
      throw new JyBizException("扫描前请绑定小组！");
    }
    // 扫描方式与扫描条码是否匹配校验
    if (request.getScanType() == null) {
      request.setScanType(SendVehicleScanTypeEnum.SCAN_ONE.getCode());
    }
    final BarCodeType barCodeType = BusinessUtil.getBarCodeType(request.getBarCode());
    if (barCodeType == null) {
      throw new JyBizException("请扫描正确的条码！");
    }
    if (Objects.equals(SendVehicleScanTypeEnum.SCAN_ONE.getCode(), request.getScanType()) &&
        (!Objects.equals(BarCodeType.PACKAGE_CODE.getCode(), barCodeType.getCode()) && !Objects
            .equals(BarCodeType.BOX_CODE.getCode(), barCodeType.getCode()))) {
      throw new JyBizException("请扫描包裹号或箱号！");
    }
    if (Objects.equals(SendVehicleScanTypeEnum.SCAN_WAYBILL.getCode(), request.getScanType())) {
      if (!Objects.equals(BarCodeType.PACKAGE_CODE.getCode(), barCodeType.getCode()) && !Objects
          .equals(BarCodeType.WAYBILL_CODE.getCode(), barCodeType.getCode())) {
        throw new JyBizException("请扫描包裹号或运单号！");
      }
      request.setBarCode(WaybillUtil.getWaybillCode(request.getBarCode()));
    }
  }

  private void bizCheck(ComboardScanReq request) {
    /**
     * 组板相关校验
     */
    comboardCheck(request);
    /**
     * 发货封车相关校验
     */
    sendSealCheck(request);
  }

  /**
   * 租板相关校验
   */
  private void comboardCheck(ComboardScanReq request) {
    String barCode = request.getBarCode();
    if (WaybillUtil.isPackageCode(barCode)) {
      final Waybill waybill = waybillQueryManager
          .getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCodeByPackCode(barCode));
      if (waybill == null) {
        throw new JyBizException("未查找到运单数据");
      }
      if (waybill.getOldSiteId() == null) {
        throw new JyBizException("运单对应的预分拣站点为空");
      }
      request.setEndSiteId(waybill.getOldSiteId());
      //匹流向
      matchDestinationCheck(request);
      /**
       * 通用拦截链
       */
      comboardCheckChain(request);
      /**
       * 已集包校验
       */
      sortingCheck(request);

    } else if (BusinessHelper.isBoxcode(barCode)) {
      final Box box = boxService.findBoxByCode(barCode);
      if (box == null) {
        throw new JyBizException("未找到对应箱号，请检查");
      }
      request.setEndSiteId(box.getReceiveSiteCode());
      //匹配流向
      matchDestinationCheck(request);
      if (!cycleBagBindCheck(barCode, null, request.getCurrentOperate().getSiteCode(), box)) {
        throw new JyBizException(BoxResponse.MESSAGE_BC_NO_BINDING);
      }

    } else {
      //TODO 运单
    }

    // 已在同场地发货，不可再组板
    final SendM recentSendMByParam = virtualBoardService
        .getRecentSendMByParam(barCode, request.getCurrentOperate().getSiteCode(), null, null);
    if (recentSendMByParam != null) {
      //三小时内禁止再次发货，返调度再次发货问题处理
      Date sendTime = recentSendMByParam.getOperateTime();
      if (sendTime != null
          && System.currentTimeMillis() - sendTime.getTime() <= 3L * 3600L * 1000L) {
        throw new JyBizException("该包裹已发货");
      }
    }
  }

  /**
   * 发货相关校验
   */
  private void sendSealCheck(ComboardScanReq request) {
  }

  /**
   * 已经集包的包裹不允许租板
   */
  private void sortingCheck(ComboardScanReq request) {
    Sorting sorting = new Sorting();
    sorting.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
    sorting.setPackageCode(request.getBarCode());
    List<Sorting> sortingList = dynamicSortingQueryDao.findByWaybillCodeOrPackageCode(sorting);
    if (!CollectionUtils.isEmpty(sortingList)) {
      for (Sorting sortingTemp : sortingList) {
        final BarCodeType barCodeType = BusinessUtil.getBarCodeType(sortingTemp.getBoxCode());
        if (barCodeType != null && Objects.equals(barCodeType, BarCodeType.BOX_CODE)) {
          log.info("分拣传站租板校验--包裹【{}】 已经集包【{}】", request.getBarCode(),
              JsonHelper.toJson(sortingTemp));
          throw new JyBizException(BoxResponse.MESSAGE_CODE_PACKAGE_BOX);
        }
      }
    }
  }

  public boolean cycleBagBindCheck(String boxCode, Integer operateType, Integer siteCode, Box box) {
    if (Constants.OPERATE_TYPE_SORTING.equals(operateType) || Constants.OPERATE_TYPE_INSPECTION
        .equals(operateType)) {
      BaseStaffSiteOrgDto dto = baseService
          .queryDmsBaseSiteByCode(box.getReceiveSiteCode().toString());
      if (dto == null) {
        log.info("boxes/validation :{} baseService.queryDmsBaseSiteByCode 获取目的地信息 NULL",
            box.getReceiveSiteCode().toString());
        return false;
      }
      // 获取循环集包袋绑定信息
      String materialCode = cycleBoxService.getBoxMaterialRelation(boxCode);
      // 决定是否绑定循环集包袋
      // 不是BC类型的不拦截
      if (!BusinessHelper.isBCBoxType(box.getType())) {
        return true;
      }
      // 开关关闭不拦截
      if (!funcSwitchConfigService
          .getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(), siteCode)) {
        return true;
      }
      //有集包袋不拦截
      if (!StringUtils.isEmpty(materialCode)) {
        return true;
      }
      return false;
    }
    return true;
  }

  private void comboardCheckChain(ComboardScanReq request) {
    final PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
    pdaOperateRequest.setPackageCode(request.getBarCode());
    pdaOperateRequest.setBoxCode(request.getBarCode());
    pdaOperateRequest.setReceiveSiteCode(request.getEndSiteId());
    pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
    pdaOperateRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
    pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
    pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
    pdaOperateRequest.setOnlineStatus(BusinessInterceptOnlineStatusEnum.ONLINE.getCode());
    BoardCombinationJsfResponse interceptResult = sortingCheckService
        .virtualBoardCombinationCheck(pdaOperateRequest);
    if (!interceptResult.getCode().equals(200)) {
      throw new JyBizException(interceptResult.getMessage());
    }
  }

  /**
   * 校验当前barCode的流向 是否在当前混扫任务的流向范围内
   */
  private void matchDestinationCheck(ComboardScanReq request) {
    JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
    condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
    condition.setGroupCode(request.getGroupCode());
    condition.setTemplateCode(request.getTemplateCode());

    List<JyGroupSortCrossDetailEntity> groupSortCrossDetailEntityList = jyGroupSortCrossDetailDao
        .listSendFlowByTemplateCode(condition);

    boolean hasMatchDestinationIdFlag = false;
    for (JyGroupSortCrossDetailEntity entity : groupSortCrossDetailEntityList) {
      if (Objects.equals(entity.getEndSiteId(), request.getEndSiteId())) {
        hasMatchDestinationIdFlag = true;
        break;
      }
    }
    // 如果获取不到匹配流向，则获取大小站
    if (!hasMatchDestinationIdFlag) {
      final Integer parentSiteId = baseService.getMappingSite(request.getEndSiteId());
      if (parentSiteId != null) {
        if (Objects.equals(parentSiteId, request.getEndSiteId())) {
          hasMatchDestinationIdFlag = true;
        }
      }
    }
    if (!hasMatchDestinationIdFlag) {
      throw new JyBizException("扫描包裹/箱号所属流向不在当前混扫任务范畴内！");
    }
  }

  private Boolean hasMatchDestinationIdFromBoardList(
      List<com.jd.transboard.api.dto.VirtualBoardResultDto> virtualBoardDtoList,
      Integer destinationId) {
    boolean hasMatchDestinationIdFlag = false;
    for (com.jd.transboard.api.dto.VirtualBoardResultDto virtualBoardResultDtoQueryDatum : virtualBoardDtoList) {
      if (Objects.equals(virtualBoardResultDtoQueryDatum.getDestinationId(), destinationId)) {
        hasMatchDestinationIdFlag = true;
        break;
      }
    }
    return hasMatchDestinationIdFlag;
  }

  @Override
  public InvokeResult<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(
      BoardStatisticsReq request) {
    return null;
  }

  @Override
  public InvokeResult<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(
      HaveScanStatisticsReq request) {
    return null;
  }

  @Override
  public InvokeResult<PackageDetailResp> listPackageDetailRespUnderBox(BoxQueryReq request) {
    return null;
  }

  @Override
  public InvokeResult<WaitScanStatisticsResp> queryWaitScanStatisticsUnderSendFlow(
      WaitScanStatisticsReq request) {
    return null;
  }

  @Override
  public InvokeResult<PackageDetailResp> listPackageDetailRespUnderSendFlow(
      SendFlowQueryReq request) {
    return null;
  }

  @Override
  public InvokeResult<BoardExcepStatisticsResp> queryExcepScanStatisticsUnderBoard(
      BoardExcepStatisticsReq request) {
    return null;
  }

  @Override
  public InvokeResult<SendFlowExcepStatisticsResp> queryExcepScanStatisticsUnderCTTGroup(
      SendFlowExcepStatisticsReq request) {
    return null;
  }
}
