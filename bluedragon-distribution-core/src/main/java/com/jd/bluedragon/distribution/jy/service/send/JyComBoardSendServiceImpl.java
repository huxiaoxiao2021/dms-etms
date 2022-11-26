package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.board.BizSourceEnum;
import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BoardCommonManagerImpl;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jsf.cross.SortCrossJsfManager;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey.SendCodeAttributeKeyEnum;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.delivery.constants.SendKeyTypeEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyGroupSortCrossDetailDao;
import com.jd.bluedragon.distribution.jy.enums.ComboardBarCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadProductTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.ComboardStatusEnum;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardService;
import com.jd.bluedragon.distribution.middleend.sorting.dao.DynamicSortingQueryDao;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.ConfirmMsgBox;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.DmsConstants;
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
import com.jd.transboard.api.dto.*;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.cross.*;

import java.util.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.etms.waybill.domain.Waybill;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.JY_APP;
import static com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE;

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
  SendCodeService sendCodeService;
  @Autowired
  GroupBoardManager groupBoardManager;

  @Autowired
  @Qualifier("redisJySendBizIdSequenceGen")
  private JimdbSequenceGen redisJyBizIdSequenceGen;
  @Autowired
  private JyComboardService jyComboardService;
  @Autowired
  private DeliveryService deliveryService;
  @Autowired
  private NewSealVehicleService newSealVehicleService;
  @Autowired
  @Qualifier("redisClientOfJy")
  protected Cluster redisClientCache;

  private static final Integer BOX_TYPE = 1;

  private static final Integer PACKAGE_TYPE = 2;

  private static final Integer WAYBILL_TYPE = 3;

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
    } else {
      log.info("获取滑道笼车信息异常:{}", JsonHelper.toJson(query));
      return new InvokeResult<>(SEND_FLOE_CTT_CODE, SEND_FLOE_CTT_MESSAGE);
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
    if (cttGroupDataResp == null || CollectionUtils
        .isEmpty(cttGroupDataResp.getCttGroupDtolist())) {
      log.info("获取混扫任务信息异常： {}", JsonHelper.toJson(request));
      return new InvokeResult<>(SEND_FLOE_CTT_GROUP_CODE, SEND_FLOE_CTT_GROUP_MESSAGE);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, cttGroupDataResp);
  }

  @Override
  public InvokeResult<SendFlowDataResp> listSendFlowUnderCTTGroup(SendFlowDataReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getTemplateCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    SendFlowDataResp resp = new SendFlowDataResp();
    List<SendFlowDto> sendFlowDtoList = new ArrayList<>();
    resp.setSendFlowDtoList(sendFlowDtoList);
    Integer startSiteCode = request.getCurrentOperate().getSiteCode();
    try {
      log.info("开始获取混扫任务下流向的详细信息：{}", JsonHelper.toJson(request));
      JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
      entity.setGroupCode(request.getGroupCode());
      entity.setStartSiteId(Long.valueOf(startSiteCode));
      entity.setTemplateCode(request.getTemplateCode());
      // 获取当前网格码内扫描的人数
      int userCount = jyComboardService.queryUserCountByStartSiteCode(Long.valueOf(startSiteCode));
      resp.setScanUserCount(userCount);
      // 获取混扫任务下的流向信息
      List<JyGroupSortCrossDetailEntity> sendFlowList = jyGroupSortCrossDetailService
          .listSendFlowByTemplateCode(entity);
      // 获取目的地
      List<Integer> endSiteCodeList = getEndSiteCodeListBySendFlowList(sendFlowList);
      // 获取当前混扫任务下多个流向的组板数量和待扫统计
      List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsService
          .queryComboardAggs(startSiteCode, endSiteCodeList);
      HashMap<Long, JyComboardAggsEntity> sendFlowMap = getSendFlowMap(jyComboardAggsEntities);
      // 获取当前流向执行中的板号
      List<JyBizTaskComboardEntity> boardList = jyBizTaskComboardService
          .queryInProcessBoardListBySendFlowList(startSiteCode, endSiteCodeList);
      List<String> boardCodeList = getBoardCodeList(boardList);
      // 获取当前板号的扫描信息
      List<JyComboardAggsEntity> boardScanInfoList = jyComboardAggsService
          .queryComboardAggs(boardCodeList);
      // 当前板的扫描信息
      HashMap<Long, JyComboardAggsEntity> boardFlowMap = getBoardFlowMap(boardScanInfoList,boardList);
      getSendFlowDtoList(sendFlowList, boardFlowMap, sendFlowMap, sendFlowDtoList);
    } catch (Exception e) {
      log.info("获取混扫任务下的流向详情信息失败：{}", JsonHelper.toJson(request), e);
      return new InvokeResult<>(SEND_FLOW_UNDER_CTTGROUP_CODE, SEND_FLOW_UNDER_CTTGROUP_MESSAGE);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  private List<String> getBoardCodeList(List<JyBizTaskComboardEntity> boardList) {
    List<String> boardCodeList = new ArrayList<>();
    for (JyBizTaskComboardEntity board : boardList) {
      boardCodeList.add(board.getBoardCode());
    }
    return boardCodeList;
  }

  private HashMap<Long, JyComboardAggsEntity> getBoardFlowMap(
          List<JyComboardAggsEntity> boardScanInfoList, List<JyBizTaskComboardEntity> boardList) {
    HashMap<Long, JyComboardAggsEntity> boardFlowMap = new HashMap<>();
    HashMap<String, Long> boardMap = new HashMap<>();
    for (JyBizTaskComboardEntity boardScanInfo : boardList) {
      boardMap.put(boardScanInfo.getBoardCode(), boardScanInfo.getEndSiteId());
    }
    for (JyComboardAggsEntity entity : boardScanInfoList) {
      String boardCode = entity.getBoardCode();
      Long endSiteId = boardMap.get(boardCode);
      boardFlowMap.put(endSiteId,entity);
    }
    return boardFlowMap;
  }

  private void getSendFlowDtoList(List<JyGroupSortCrossDetailEntity> sendFlowList,
      HashMap<Long, JyComboardAggsEntity> boardFlowMap,
      HashMap<Long, JyComboardAggsEntity> sendFlowMap,
      List<SendFlowDto> sendFlowDtoList) {
    for (JyGroupSortCrossDetailEntity entity : sendFlowList) {
      Long endSiteCode = entity.getEndSiteId();
      SendFlowDto sendFlowDto = new SendFlowDto();
      sendFlowDto.setCrossCode(entity.getCrossCode());
      sendFlowDto.setTableTrolleyCode(entity.getTabletrolleyCode());
      sendFlowDto.setEndSiteId(entity.getEndSiteId().intValue());
      sendFlowDto.setEndSiteName(entity.getEndSiteName());
      JyComboardAggsEntity sendFlow = sendFlowMap.get(endSiteCode);
      if (sendFlow != null) {
        sendFlowDto.setBoardCount(sendFlow.getBoardCount());
        sendFlowDto.setWaitScanCount(sendFlow.getWaitScanCount());
      }

      // 获取当前板号的扫描信息
      JyComboardAggsEntity boardFlow = boardFlowMap.get(endSiteCode);
      if (boardFlow != null) {
        BoardDto boardDto = new BoardDto();
        sendFlowDto.setCurrentBoardDto(boardDto);
        boardDto.setBoardCode(boardFlow.getBoardCode());
        boardDto.setBoxHaveScanCount(boardFlow.getBoxScannedCount());
        boardDto.setPackageHaveScanCount(boardFlow.getPackageScannedCount());
        boardDto.setInterceptCount(boardFlow.getInterceptCount());
        // 已扫比例
        int scanCount = boardFlow.getPackageScannedCount() + boardFlow.getBoxScannedCount();
        int scanProgress = (int) ((scanCount * 1.00 / ucc.getJyComboardCountLimit()) * 100);
        boardDto.setProgress(scanProgress + "%");
      }
      sendFlowDtoList.add(sendFlowDto);
    }
  }

  private HashMap<Long, JyComboardAggsEntity> getSendFlowMap(
      List<JyComboardAggsEntity> jyComboardAggsEntities) {
    HashMap<Long, JyComboardAggsEntity> sendFlowMap = new HashMap<>();
    for (JyComboardAggsEntity entity : jyComboardAggsEntities) {
      sendFlowMap.put(entity.getReceiveSiteId().longValue(), entity);
    }
    return sendFlowMap;
  }

  private List<Integer> getEndSiteCodeListBySendFlowList(
      List<JyGroupSortCrossDetailEntity> sendFlowList) {
    List<Integer> endSitecodeList = new ArrayList<>();
    for (JyGroupSortCrossDetailEntity entity : sendFlowList) {
      endSitecodeList.add(entity.getEndSiteId().intValue());
    }
    return endSitecodeList;
  }

  @Override
  public InvokeResult<SendFlowDetailResp> querySendFlowDetail(SendFlowDetailReq request) {
    return null;
  }

  @Override
  public InvokeResult<BoardResp> queryBoardDetail(BoardReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBoardCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    BoardResp resp = new BoardResp();
    List<GoodsCategoryDto> goodsCategoryList = new ArrayList<>();
    Integer startSIteId = request.getCurrentOperate().getSiteCode();
    resp.setGoodsCategoryList(goodsCategoryList);
    String boardCode = request.getBoardCode();
    Integer endSiteId = request.getEndSiteId();
    try {
      // 获取组板包裹号箱号信息
      JyComboardAggsEntity boardInfo = jyComboardAggsService.queryComboardAggs(boardCode);
      resp.setPackageHaveScanCount(boardInfo.getPackageScannedCount());
      resp.setBoxHaveScanCount(boardInfo.getBoxScannedCount());
      // 获取当前流向的滑道号和笼车号
      TableTrolleyQuery query = new TableTrolleyQuery();
      query.setSiteCode(endSiteId);
      query.setDmsId(startSIteId);
      TableTrolleyJsfResp sendFlowInfo = sortCrossJsfManager.queryCTTByStartEndSiteCode(query);
      if (sendFlowInfo != null && !CollectionUtils
          .isEmpty(sendFlowInfo.getTableTrolleyDtoJsfList())) {
        TableTrolleyJsfDto tableTrolleyJsfDto = sendFlowInfo.getTableTrolleyDtoJsfList().get(0);
        resp.setEndSiteId(endSiteId);
        resp.setEndSiteName(tableTrolleyJsfDto.getEndSiteName());
        resp.setCrossCode(tableTrolleyJsfDto.getCrossCode());
        resp.setTableTrolleyCode(tableTrolleyJsfDto.getTableTrolleyCode());
      } else {
        log.info("获取滑道笼车信息异常:{}", JsonHelper.toJson(query));
        return new InvokeResult<>(SEND_FLOE_CTT_CODE, SEND_FLOE_CTT_MESSAGE);
      }
      // 获取当前板号的产品类型扫描信息
      List<JyComboardAggsEntity> productTypeList = jyComboardAggsService.
          queryComboardAggs(boardCode, UnloadProductTypeEnum.values());
      getGoodsCategoryList(goodsCategoryList, productTypeList);
    } catch (Exception e) {
      log.info("获取板的详情信息失败：{}", JsonHelper.toJson(request), e);
      return new InvokeResult<>(BOARD_INFO_CODE, BOARD_INFO_MESSAGE);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  private void getGoodsCategoryList(List<GoodsCategoryDto> goodsCategoryList,
      List<JyComboardAggsEntity> productTypeList) {
    for (JyComboardAggsEntity entity : productTypeList) {
      GoodsCategoryDto goodsCategoryDto = new GoodsCategoryDto();
      goodsCategoryDto.setCount(entity.getScannedCount());
      goodsCategoryDto.setName(UnloadProductTypeEnum.getNameByCode(entity.getProductType()));
      goodsCategoryDto.setType(entity.getProductType());
      goodsCategoryList.add(goodsCategoryDto);
    }
  }

  @Override
  public InvokeResult finishBoard(BoardReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBoardCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    Integer startSiteId = request.getCurrentOperate().getSiteCode();
    String boardCode = request.getBoardCode();
    // 查询当前板号状态
    JyBizTaskComboardEntity entity = new JyBizTaskComboardEntity();
    entity.setStartSiteId(startSiteId.longValue());
    entity.setBoardCode(boardCode);
    entity.setStatus(1);
    try {
      JyBizTaskComboardEntity board = jyBizTaskComboardService.queryBizTaskByBoardCode(entity);
      if (board == null) {
        log.info("该板已完结: {}", boardCode);
        return new InvokeResult(FINISH_BOARD_AGAIN_CODE, FINISH_BOARD_AGAIN_MESSAGE);
      }
      if (!jyBizTaskComboardService.finishBoard(boardCode)){
        log.info("完结板异常：{}",boardCode);
        return new InvokeResult(FINISH_BOARD_CODE, FINISH_BOARD_MESSAGE);
      }
    } catch (Exception e) {
      log.info("完结板异常：{}",boardCode);
      return new InvokeResult(FINISH_BOARD_CODE, FINISH_BOARD_MESSAGE);
    }
    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
  }

  @Override
  public InvokeResult finishBoardsUnderCTTGroup(CTTGroupReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getTemplateCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    Integer startSiteId = request.getCurrentOperate().getSiteCode();
    JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
    entity.setStartSiteId(Long.valueOf(startSiteId));
    entity.setTemplateCode(request.getTemplateCode());
    entity.setGroupCode(request.getGroupCode());
    try{
      
      // 获取当前混扫任务下的流向信息
      List<JyGroupSortCrossDetailEntity> sendFlowList = jyGroupSortCrossDetailService
              .listSendFlowByTemplateCode(entity);
      // 获取目的地
      List<Integer> endSiteCodeList = getEndSiteCodeListBySendFlowList(sendFlowList);
      // 根据流向批量结束组板
      if (!jyBizTaskComboardService.batchFinishBoardBySendFLowList(startSiteId, endSiteCodeList)) {
        log.info("完结板失败，混扫任务编号：{}",request.getTemplateCode());
        return new InvokeResult(FINISH_BOARD_CODE, FINISH_BOARD_MESSAGE);
      }
    } catch (Exception e) {
      log.info("完结板异常，混扫任务编号：{}",request.getTemplateCode());
      return new InvokeResult(FINISH_BOARD_CODE, FINISH_BOARD_MESSAGE);
    }
    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.comboardScan", mState = {JProEnum.TP, JProEnum.FunctionError})
  @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public InvokeResult<ComboardScanResp> comboardScan(ComboardScanReq request) {
    baseCheck(request);
    comboardCheck(request);
    getOrCreateBoardCode(request);
    sendCheck(request);

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
    String boardLockKey = String.format(Constants.JY_COMBOARD_BOARD_LOCK_PREFIX, request.getBoardCode());
    if (!jimDbLock.lock(boardLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
      throw new JyBizException("当前系统繁忙,请稍后再试！");
    }
    try {
      JyBizTaskComboardEntity condition = new JyBizTaskComboardEntity();
      condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
      condition.setBoardCode(request.getBoardCode());
      JyBizTaskComboardEntity entity = jyBizTaskComboardService.queryBizTaskByBoardCode(condition);
      if (!entity.getBulkFlag() && entity.getCount() < ucc.getJyComboardCountLimit()) {
        Date now = new Date();
        if (WaybillUtil.isWaybillCode(request.getBarCode())) {
          //更新大宗标识
          JyBizTaskComboardEntity comboardEntity = new JyBizTaskComboardEntity();
          comboardEntity.setId(entity.getId());
          comboardEntity.setBulkFlag(true);
          comboardEntity.setUpdateTime(now);
          comboardEntity.setUpdateUserErp(request.getUser().getUserErp());
          comboardEntity.setUpdateUserName(request.getUser().getUserName());
          jyBizTaskComboardService.updateBizTaskById(comboardEntity);
          log.info("扫描大宗运单，走异步租板逻辑");
          return;
        }
        AddBoardBox addBoardBox = assembleComboardParam(request);
        Response<Integer> comboardResp = groupBoardManager.addBoxToBoard(addBoardBox);
        if (comboardResp.getCode() != ResponseEnum.SUCCESS.getIndex()) {
          throw new JyBizException(comboardResp.getMesseage()!=null?comboardResp.getMesseage():BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
        }
        JyBizTaskComboardEntity bizTaskComboardEntity = new JyBizTaskComboardEntity();
        bizTaskComboardEntity.setId(entity.getId());
        bizTaskComboardEntity.setCount(entity.getCount() + Constants.CONSTANT_NUMBER_ONE);
        bizTaskComboardEntity.setUpdateTime(now);
        bizTaskComboardEntity.setUpdateUserErp(request.getUser().getUserErp());
        bizTaskComboardEntity.setUpdateUserName(request.getUser().getUserName());
        jyBizTaskComboardService.updateBizTaskById(bizTaskComboardEntity);
        JyComboardEntity comboardEntity = createJyComboardRecord(request);
        jyComboardService.save(comboardEntity);
        //发送组板全程跟踪
        sendComboardWaybillTrace(request);
      } else {
        throw new JyBizException("已到上限，需要换新板");
      }
    } finally {
      jimDbLock.releaseLock(boardLockKey, request.getRequestId());
    }
  }

  private void sendComboardWaybillTrace(ComboardScanReq request) {
    OperatorInfo operatorInfo = assembleComboardOperatorInfo(request);
    virtualBoardService.sendWaybillTrace(request.getBarCode(), operatorInfo, request.getBoardCode(),
        request.getEndSiteName(), WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION,
        BizSourceEnum.PDA.getValue());
  }

  private OperatorInfo assembleComboardOperatorInfo(ComboardScanReq request) {
    OperatorInfo operatorInfo = new OperatorInfo();
    operatorInfo.setSiteCode(request.getCurrentOperate().getSiteCode());
    operatorInfo.setSiteName(request.getCurrentOperate().getSiteName());
    operatorInfo.setUserCode(request.getUser().getUserCode());
    operatorInfo.setOperateTime(new Date());
    return operatorInfo;
  }

  public  int getComboardBarCodeType(String barCode) {
    if (BusinessUtil.isBoxcode(barCode)) {
      return ComboardBarCodeTypeEnum.BOX.getCode();
    } else if (WaybillUtil.isPackageCode(barCode)) {
      return ComboardBarCodeTypeEnum.PACKAGE.getCode();
    } else if (WaybillUtil.isWaybillCode(barCode)) {
      return ComboardBarCodeTypeEnum.WAYBILL.getCode();
    }  return 0;
  }

  private AddBoardBox assembleComboardParam(ComboardScanReq request) {
    AddBoardBox addBoardBox = new AddBoardBox();
    addBoardBox.setBoardCode(request.getBoardCode());
    addBoardBox.setBoxCode(request.getBarCode());
    addBoardBox.setBarcodeType(getComboardBarCodeType(request.getBarCode()));
    addBoardBox.setOperatorErp(request.getUser().getUserErp());
    addBoardBox.setOperatorName(request.getUser().getUserName());
    addBoardBox.setSiteCode(request.getCurrentOperate().getSiteCode());
    addBoardBox.setSiteName(request.getCurrentOperate().getSiteName());
    addBoardBox.setSiteType(BoardCommonManagerImpl.BOARD_COMBINATION_SITE_TYPE);
    addBoardBox.setBizSource(BizSourceEnum.PDA.getValue());
    return addBoardBox;
  }

  /**
   * 执行发货
   */
  private void execSend(ComboardScanReq request) {
    if (WaybillUtil.isWaybillCode(request.getBarCode())) {
      return;
    }
    SendKeyTypeEnum sendType = getSendType(request.getBarCode());
    SendM sendM = toSendMDomain(request);
    boolean oldForceSend = true;
    SendResult sendResult = new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
    switch (sendType) {
      case BY_WAYBILL:
        deliveryService.packageSendByRealWaybill(sendM, request.getCancelLastSend(), sendResult);
        break;
      case BY_PACKAGE:
        sendResult = deliveryService.packageSend(SendBizSourceEnum.JY_APP_SEND, sendM, oldForceSend,
            request.getCancelLastSend());
        break;
      case BY_BOX:
        sendResult = deliveryService.packageSend(SendBizSourceEnum.JY_APP_SEND, sendM, oldForceSend,
            request.getCancelLastSend());
        break;
      case BY_BOARD:
        break;
    }
    if (!Objects.equals(sendResult.getKey(), SendResult.CODE_OK)) {
      throw new JyBizException(sendResult.getValue());
    }
  }

  private SendResult execPackageSend(SendKeyTypeEnum sendType, SendResult sendResult, SendM sendM) {
    boolean oldForceSend = true; // 跳过原有拦截校验，使用新校验逻辑
    boolean cancelLastSend = ConfirmMsgBox.CODE_CONFIRM_CANCEL_LAST_SEND
        .equals(sendResult.getInterceptCode());
    switch (sendType) {
      case BY_WAYBILL:
        deliveryService.packageSendByRealWaybill(sendM, cancelLastSend, sendResult);
        break;
      case BY_PACKAGE:
        sendResult = deliveryService
            .packageSend(SendBizSourceEnum.JY_APP_SEND, sendM, oldForceSend, cancelLastSend);
        break;
      case BY_BOX:
        sendResult = deliveryService
            .packageSend(SendBizSourceEnum.JY_APP_SEND, sendM, oldForceSend, cancelLastSend);
        break;
      case BY_BOARD:
        // TODO 支持扫描板号
        break;
    }

    return sendResult;
  }

  private void getOrCreateBoardCode(ComboardScanReq request) {
    //从板详情进入补扫
    if (ObjectHelper.isNotNull(request.getBoardCode())) {
      JyBizTaskComboardEntity condition = new JyBizTaskComboardEntity();
      condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
      condition.setBoardCode(request.getBoardCode());
      JyBizTaskComboardEntity entity = jyBizTaskComboardService.queryBizTaskByBoardCode(condition);
      if (ComboardStatusEnum.SEALED.getCode() == entity.getStatus()) {
        throw new JyBizException("已封车禁止继续扫描！");
      }
      return;
    }
    /**
     * 流向加锁
     */
    String sendFlowLockKey = String
        .format(Constants.JY_COMBOARD_SENDFLOW_LOCK_PREFIX, request.getEndSiteId());
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
        //生成新板任务，lock主要是lock的这个点
        generateNewBoard(request);
        //存储jy_task
        JyBizTaskComboardEntity record = assembleJyBizTaskComboardParam(request);
        //空板是不确定是大宗还是非大宗，组板扫描成功后再确定
        jyBizTaskComboardService.save(record);
      }
    } finally {
      jimDbLock.releaseLock(sendFlowLockKey, request.getRequestId());
    }
  }

  private void generateNewBoard(ComboardScanReq request) {
    AddBoardRequest addBoardRequest = new AddBoardRequest();
    addBoardRequest.setBoardCount(1);
    addBoardRequest.setDestinationId(request.getEndSiteId());
    addBoardRequest.setDestination(request.getEndSiteName());
    addBoardRequest.setOperatorErp(request.getUser().getUserErp());
    addBoardRequest.setOperatorName(request.getUser().getUserName());
    addBoardRequest.setSiteCode(request.getCurrentOperate().getSiteCode());
    addBoardRequest.setSiteName(request.getCurrentOperate().getSiteName());
    addBoardRequest.setBizSource(BizSourceEnum.PDA.getValue());
    Response<List<Board>> response = groupBoardManager.createBoards(addBoardRequest);
    if (response == null || response.getCode() != ResponseEnum.SUCCESS.getIndex() || response
        .getData().isEmpty()) {
      throw new JyBizException("创建新板异常！");
    }
    request.setBoardCode(response.getData().get(0).getCode());
  }

  private String genTaskBizId(ComboardScanReq request) {
    String ownerKey = String.format(JyBizTaskComboardEntity.BIZ_PREFIX,
        DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
    String bizId = ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    request.setBizId(bizId);
    return bizId;
  }

  private JyBizTaskComboardEntity assembleJyBizTaskComboardParam(ComboardScanReq request) {
    JyBizTaskComboardEntity record = new JyBizTaskComboardEntity();
    record.setBoardCode(request.getBoardCode());
    record.setBizId(genTaskBizId(request));
    record.setSendCode(genSendCode(request));
    record.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
    record.setEndSiteId(Long.valueOf(request.getEndSiteId()));
    record.setStatus(ComboardStatusEnum.PROCESSING.getCode());
    Date now = new Date();
    record.setCreateTime(now);
    record.setUpdateTime(now);
    record.setCreateUserErp(request.getUser().getUserErp());
    record.setCreateUserName(request.getUser().getUserName());
    record.setUpdateUserErp(request.getUser().getUserErp());
    record.setUpdateUserName(request.getUser().getUserName());
    return record;
  }

  private String genSendCode(ComboardScanReq request) {
    Map<SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
    attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code,
        String.valueOf(request.getCurrentOperate().getSiteCode()));
    attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code,
        String.valueOf(request.getEndSiteId()));
    String sendCode = sendCodeService
        .createSendCode(attributeKeyEnumObjectMap, JY_APP, request.getUser().getUserErp());
    request.setSendCode(sendCode);
    return sendCode;
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

  /**
   * 租板相关校验
   */
  private void comboardCheck(ComboardScanReq request) {
    String barCode = request.getBarCode();
    if (WaybillUtil.isPackageCode(barCode) || WaybillUtil.isWaybillCode(barCode)) {
      Waybill waybill = waybillQueryManager
          .getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCodeByPackCode(barCode));
      if (waybill == null) {
        throw new JyBizException("未查找到运单数据");
      }
      if (waybill.getOldSiteId() == null) {
        throw new JyBizException("运单对应的预分拣站点为空");
      }
      if (WaybillUtil.isWaybillCode(barCode) && waybill.getGoodNumber() < ucc
          .getBulkScanPackageMinCount()) {
        throw new JyBizException("大宗扫描：运单包裹数量不得低于100！");
      }
      request.setEndSiteId(waybill.getOldSiteId());
      //匹流向
      matchDestinationCheck(request);
      /**
       * 已集包校验
       */
      sortingCheck(request);
      /**
       * 通用拦截链
       */
      comboardCheckChain(request);
    } else if (BusinessHelper.isBoxcode(barCode)) {
      final Box box = boxService.findBoxByCode(barCode);
      if (box == null) {
        throw new JyBizException("未找到对应箱号，请检查");
      }
      request.setEndSiteId(box.getReceiveSiteCode());
      //匹配流向
      matchDestinationCheck(request);
      if (!cycleBagBindCheck(barCode, request.getCurrentOperate().getSiteCode(), box)) {
        throw new JyBizException(BoxResponse.MESSAGE_BC_NO_BINDING);
      }
    }
    BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService.getSiteBySiteID(request.getEndSiteId());
    if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())) {
      request.setEndSiteName(baseStaffSiteOrgDto.getSiteName());
    }

    //TODO 利用缓存判断是否已经租过板了
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
  private void sendCheck(ComboardScanReq request) {
    SendM sendM = toSendMDomain(request);
    SendKeyTypeEnum sendType = getSendType(request.getBarCode());
    SendResult sendResult = new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
    sendStatusCheck(request, sendType, sendResult, sendM);
    sendInterceptChain(request, sendM, sendType);
  }

  private void sendStatusCheck(ComboardScanReq request, SendKeyTypeEnum sendType,
      SendResult sendResult, SendM sendM) {
    if (SendKeyTypeEnum.BY_PACKAGE.equals(sendType) && deliveryService
        .isSendByWaybillProcessing(sendM)) {
      throw new JyBizException(HintService.getHint(HintCodeConstants.SEND_BY_WAYBILL_PROCESSING));
    }
    // 校验是否已经发货
    deliveryService.multiSendVerification(sendM, sendResult);
    if (Objects.equals(sendResult.getKey(), SendResult.CODE_SENDED)) {
      throw new JyBizException(sendResult.getValue());
    }
    // 校验发货批次号状态
    StringBuffer customMsg = new StringBuffer()
        .append(HintService.getHint(HintCodeConstants.SEND_CODE_SEALED_TIPS_SECOND));
    if (newSealVehicleService.newCheckSendCodeSealed(sendM.getSendCode(), customMsg)) {
      throw new JyBizException(customMsg.toString());
    }
    if (ConfirmMsgBox.CODE_CONFIRM_CANCEL_LAST_SEND.equals(sendResult.getInterceptCode())) {
      request.setCancelLastSend(true);
    }
  }


  private void sendInterceptChain(ComboardScanReq request, SendM sendM, SendKeyTypeEnum sendType) {
    if (!BusinessHelper.isBoxcode(request.getBarCode())) {
      SortingCheck sortingCheck = deliveryService.getSortingCheck(sendM);
        /*if (request.getValidateIgnore() != null) {
          sortingCheck.setValidateIgnore(this.convertValidateIgnore(request.getValidateIgnore()));
        }*/
      FilterChain filterChain = sortingCheckService.matchJyDeliveryFilterChain(sendType);
      SortingJsfResponse chainResp = sortingCheckService
          .doSingleSendCheckWithChain(sortingCheck, true, filterChain);
      if (!chainResp.getCode().equals(JdResponse.CODE_OK)) {
        if (!(JdResponse.CODE_SERVICE_ERROR.equals(chainResp.getCode())
            || chainResp.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM)) {
          // 拦截时保存拦截记录
          JyComboardEntity comboardEntity = createJyComboardRecord(request);
          comboardEntity.setInterceptFlag(true);
          jyComboardService.save(comboardEntity);
        }
        throw new JyBizException(chainResp.getMessage());
      }
    }
  }

  private SendKeyTypeEnum getSendType(String barCode) {
    SendKeyTypeEnum sendType = null;
    if (WaybillUtil.isWaybillCode(barCode)) {
      sendType = SendKeyTypeEnum.BY_WAYBILL;
    } else if (BusinessUtil.isBoardCode(barCode)) {
      sendType = SendKeyTypeEnum.BY_BOARD;
    } else if (WaybillUtil.isPackageCode(barCode)) {
      sendType = SendKeyTypeEnum.BY_PACKAGE;
    } else if (BusinessHelper.isBoxcode(barCode)) {
      sendType = SendKeyTypeEnum.BY_BOX;
    }
    return sendType;
  }

  private JyComboardEntity createJyComboardRecord(ComboardScanReq request) {
    JyComboardEntity comboardEntity = new JyComboardEntity();
    Date now = new Date();
    comboardEntity.setBizId(request.getBizId());
    comboardEntity.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
    comboardEntity.setStartSiteName(request.getCurrentOperate().getSiteName());
    comboardEntity.setEndSiteId(Long.valueOf(request.getEndSiteId()));
    comboardEntity.setEndSiteName(request.getEndSiteName());
    comboardEntity.setBarCode(request.getBarCode());
    comboardEntity.setBarCodeType(getBarCodeType(request.getBarCode()));
    comboardEntity.setSendCode(request.getSendCode());
    comboardEntity.setBoardCode(request.getBoardCode());
    comboardEntity.setCreateUserErp(request.getUser().getUserErp());
    comboardEntity.setCreateUserName(request.getUser().getUserName());
    comboardEntity.setUpdateUserErp(request.getUser().getUserErp());
    comboardEntity.setUpdateUserName(request.getUser().getUserName());
    comboardEntity.setCreateTime(now);
    comboardEntity.setUpdateTime(now);
    return comboardEntity;
  }

  private Integer getBarCodeType(String barCode) {
    if (WaybillUtil.isWaybillCode(barCode)) {
      return ComboardBarCodeTypeEnum.WAYBILL.getCode();
    } else if (WaybillUtil.isPackageCode(barCode)) {
      return ComboardBarCodeTypeEnum.PACKAGE.getCode();
    } else {
      return ComboardBarCodeTypeEnum.BOX.getCode();
    }
  }

  private SendM toSendMDomain(ComboardScanReq request) {
    SendM domain = new SendM();
    domain.setReceiveSiteCode(request.getEndSiteId());
    domain.setSendCode(request.getSendCode());
    domain.setBoxCode(request.getBarCode());
    domain.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
    domain.setCreateUser(request.getUser().getUserName());
    domain.setCreateUserCode(request.getUser().getUserCode());
    domain.setSendType(DmsConstants.BUSSINESS_TYPE_POSITIVE);
    domain.setBizSource(SendBizSourceEnum.JY_APP_SEND.getCode());
    domain.setYn(1);
    domain.setCreateTime(request.getCurrentOperate().getOperateTime());
    domain.setOperateTime(request.getCurrentOperate().getOperateTime());
    return domain;
  }

  /**
   * 已经集包的包裹不允许租板
   */
  private void sortingCheck(ComboardScanReq request) {
    Sorting sorting = new Sorting();
    sorting.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
    if (WaybillUtil.isPackageCode(request.getBarCode())) {
      sorting.setPackageCode(request.getBarCode());
    } else if (WaybillUtil.isWaybillCode(request.getBarCode())) {
      sorting.setWaybillCode(request.getBarCode());
    }
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

  public boolean cycleBagBindCheck(String boxCode, Integer siteCode, Box box) {
    // 不是BC类型的不拦截
    if (!BusinessHelper.isBCBoxType(box.getType())) {
      return true;
    }
    // 开关关闭不拦截
    if (!funcSwitchConfigService
        .getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(), siteCode)) {
      return true;
    }
    // 获取循环集包袋绑定信息
    String materialCode = cycleBoxService.getBoxMaterialRelation(boxCode);
    //有集包袋不拦截
    if (!StringUtils.isEmpty(materialCode)) {
      return true;
    }
    return false;
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
      //TODO 把这个链放在获取板号后面，持久化扫描拦截记录
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
      if (entity.getEndSiteId().intValue()== request.getEndSiteId()) {
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

  @Override
  public InvokeResult<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(
      BoardStatisticsReq request) {
    //按照流向查询 已扫 待扫 拦截指标
    //查询流向下 7人内未封车的板 list
    //查询板list的统计数据：已扫和拦截指标
    return null;
  }

  @Override
  public InvokeResult<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(
      HaveScanStatisticsReq request) {
    //
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

  @Override
  public InvokeResult<ComboardDetailResp> listPackageOrBoxUnderBoard(BoardReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBoardCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    ComboardDetailResp resp = new ComboardDetailResp();
    String boardCode = request.getBoardCode();
    List<ComboardDetailDto> comboardDetailDtoList = new ArrayList<>();
    resp.setComboardDetailDtoList(comboardDetailDtoList);
    // 判断是否为运单
    if (request.isBulkFlag()) {
      // 获取运单号
      String wayBillCode = jyComboardService.queryWayBillCodeByBoardCode(boardCode);
      if (!StringUtils.isEmpty(wayBillCode)){
        resp.setBulkFlag(Boolean.TRUE);
        ComboardDetailDto comboardDetailDto = new ComboardDetailDto();
        comboardDetailDto.setType(WAYBILL_TYPE);
        comboardDetailDto.setBarCode(wayBillCode);
        comboardDetailDtoList.add(comboardDetailDto);
      }else {
        log.info("根据板号获取运单号异常：{}",boardCode);
        return new InvokeResult<>(PACKAGE_OR_BOX_UNDER_BOARD_CODE,PACKAGE_OR_BOX_UNDER_BOARD_MESSAGE);
      }
    }else {
      Response<BoardBoxCountDto> boardScanInfo = groupBoardManager.getBoxCountInfoByBoardCode(boardCode);
      if (boardScanInfo == null || boardScanInfo.getData() == null ) {
        log.info("根据板号获取包裹号箱号异常：{}",boardCode);
        return new InvokeResult<>(PACKAGE_OR_BOX_UNDER_BOARD_CODE,PACKAGE_OR_BOX_UNDER_BOARD_MESSAGE);
      }
      resp.setBulkFlag(Boolean.FALSE);
      BoardBoxCountDto boardInfoDto = boardScanInfo.getData();
      resp.setBoxCount(boardInfoDto.getBoxCount());
      resp.setPackageCount(boardInfoDto.getPackageCodeCount());
      getComboardDetailDtoList(boardInfoDto.getBarCodeList(),boardInfoDto.getBoxCount(),comboardDetailDtoList);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  @Override
  public InvokeResult cancelComboard(CancelBoardReq request) {
    if (!checkBaseRequest(request) 
            || StringUtils.isEmpty(request.getBoardCode()) 
            ||CollectionUtils.isEmpty(request.getCancelList())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    List<ComboardDetailDto> cancelList = request.getCancelList();
    RemoveBoardBoxDto removeBoardBoxDto = new RemoveBoardBoxDto();
    removeBoardBoxDto.setSiteCode(request.getCurrentOperate().getSiteCode());
    removeBoardBoxDto.setOperatorErp(request.getUser().getUserErp());
    removeBoardBoxDto.setOperatorName(request.getUser().getUserName());
    removeBoardBoxDto.setBoardCode(request.getBoardCode());
    try {
      if (request.isBulkFlag()) {
        // 运单号
        if (request.getCancelList().get(0) != null
            && request.getCancelList().get(0).getBarCode() != null) {
          removeBoardBoxDto.setWaybillCode(request.getCancelList().get(0).getBarCode());
          groupBoardManager.removeBardBoxByWaybillCode(removeBoardBoxDto);
          // todo 异步发送全程跟踪
        }
      } else {
        // 包裹号
        List<String> boxCodeList = new ArrayList<>();
        removeBoardBoxDto.setBoxCodeList(boxCodeList);
        for (ComboardDetailDto comboardDetailDto : cancelList) {
          boxCodeList.add(comboardDetailDto.getBarCode());
        }
        groupBoardManager.batchRemoveBardBoxByBoxCodes(removeBoardBoxDto);
      }
    } catch (Exception e) {
      log.error("取消组板失败：{}", JsonHelper.toJson(removeBoardBoxDto));
      return new InvokeResult<>(CANCEL_COM_BOARD_CODE, CANCEL_COM_BOARD_MESSAGE);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
  }

  @Override
  public InvokeResult<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBarCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    QueryBelongBoardResp resp = new QueryBelongBoardResp();
    try{
      BoardBoxInfoDto boardBoxInfoDto = groupBoardManager
              .getBoardBoxInfo(request.getBarCode(),request.getCurrentOperate().getSiteCode());
      if (boardBoxInfoDto != null ) {
        resp.setBoardCode(boardBoxInfoDto.getCode());
        resp.setEndSiteId(boardBoxInfoDto.getDestinationId());
      }else {
        log.error("获取板号信息失败：{}", JsonHelper.toJson(request.getBarCode()));
        return new InvokeResult<>(BOARD_INFO_CODE, BOARD_INFO_MESSAGE);
      }
    }catch (Exception e) {
      log.error("获取板号信息失败：{}", JsonHelper.toJson(request.getBarCode()));
      return new InvokeResult<>(BOARD_INFO_CODE, BOARD_INFO_MESSAGE);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,resp);
  }

  private void getComboardDetailDtoList(List<String> barCodeList, Integer boxCount, 
                                        List<ComboardDetailDto> comboardDetailDtoList) {
    int index = 0;
    for (String barCode : barCodeList) {
      ComboardDetailDto dto = new ComboardDetailDto();
      dto.setBarCode(barCode);
      if (index <= boxCount) {
        dto.setType(BOX_TYPE);
      } else {
        dto.setType(PACKAGE_TYPE);
      }
      comboardDetailDtoList.add(dto);
    }
  }
}
