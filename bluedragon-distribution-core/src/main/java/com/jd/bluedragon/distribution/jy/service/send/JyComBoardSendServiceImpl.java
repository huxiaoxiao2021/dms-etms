package com.jd.bluedragon.distribution.jy.service.send;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.OperatorInfo;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.board.BizSourceEnum;
import com.jd.bluedragon.common.dto.comboard.request.*;
import com.jd.bluedragon.common.dto.comboard.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.UnloadScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BoardCommonManagerImpl;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.cross.SortCrossJsfManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.domain.OperatorData;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.request.SortingPageRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.board.service.VirtualBoardService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey.SendCodeAttributeKeyEnum;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptOnlineStatusEnum;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.delivery.IDeliveryOperationService;
import com.jd.bluedragon.distribution.delivery.constants.SendKeyTypeEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jsf.domain.BoardCombinationJsfResponse;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyGroupSortCrossDetailDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.*;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.IJyComboardJsfManager;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardService;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.middleend.sorting.dao.DynamicSortingQueryDao;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.ConfirmMsgBox;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.enums.WaybillVasEnum;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.WaybillVasDto;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.transboard.api.dto.*;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.transboard.api.service.GroupBoardService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.cross.*;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.comboard.ComboardScanedDto;
import com.jdl.jy.realtime.model.es.comboard.JyComboardPackageDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;
import static com.jd.bluedragon.Constants.SUCCESS_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum.COMBOARD_SEND_POSITION;
import static com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException.BOARD_TOTC_FAIL_INTERCEPT_MESSAGE;

@Service
@Slf4j
public class JyComBoardSendServiceImpl implements JyComBoardSendService {

  @Autowired
  private SortCrossJsfManager sortCrossJsfManager;

  @Autowired
  private WaybillQueryManager waybillQueryManager;
  @Autowired
  private WaybillCommonService waybillCommonService;
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
  DmsConfigManager dmsConfigManager;
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

  @Autowired
  private SortingService sortingService;

  @Autowired
  SendMService sendMService;
  @Autowired
  IDeliveryOperationService deliveryOperationService;
  @Autowired
  IJyComboardJsfManager comboardJsfManager;

  @Autowired
  @Qualifier("waybillCancelComboardProducer")
  private DefaultJMQProducer waybillCancelComboardProducer;


  @Autowired
  @Qualifier("waybillComboardProducer")
  private DefaultJMQProducer waybillComboardProducer;


  @Autowired
  @Qualifier("cancelComboardSendProducer")
  private DefaultJMQProducer cancelComboardSendProducer;

  @Autowired
  private IGenerateObjectId genObjectId;

  @Autowired
  private GroupBoardService groupBoardService;

  @Autowired
  private NewSealVehicleService newsealVehicleService;

  @Autowired
  @Qualifier("jyComboardTaskFirstSaveProducer")
  private DefaultJMQProducer jyComboardTaskFirstSaveProducer;

  private static final Integer BOX_TYPE = 1;

  private static final Integer PACKAGE_TYPE = 2;

  private static final Integer WAYBILL_TYPE = 3;

  final static int COMBOARD_SPLIT_NUM = 1024;

  private static final String GROUP_NAME_PREFIX= "混扫%s";

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.listCrossData", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<CrossDataResp> listCrossData(CrossDataReq request) {
    if (log.isInfoEnabled()) {
      log.info("开始获取场地滑道信息：{}", JsonHelper.toJson(request));
    }
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
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.listTableTrolleyUnderCross", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<TableTrolleyResp> listTableTrolleyUnderCross(TableTrolleyReq request) {
    if (log.isInfoEnabled()) {
      log.info("开始获取笼车营业部信息：{}", JsonHelper.toJson(request));
    }
    if (!checkBaseRequest(request)) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    InvokeResult<TableTrolleyResp> result = new InvokeResult<>();
    TableTrolleyResp tableTrolleyResp = new TableTrolleyResp();
    tableTrolleyResp.setSendFlowCountLimitUnderCtt(Constants.SEND_FLOW_COUNT_LIMIT_DEFAULT);
    if (ObjectHelper.isNotNull(dmsConfigManager.getPropertyConfig().getCttGroupSendFLowLimit()) && dmsConfigManager.getPropertyConfig().getCttGroupSendFLowLimit()>0){
      tableTrolleyResp.setSendFlowCountLimitUnderCtt(dmsConfigManager.getPropertyConfig().getCttGroupSendFLowLimit());
    }
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
      query.setLimit(dmsConfigManager.getPropertyConfig().getJyComboardSiteCTTPageSize());
      tableTrolleyJsfResp = sortCrossJsfManager.queryTableTrolleyListByDmsId(query);
    }
    if (tableTrolleyJsfResp != null && !CollectionUtils
        .isEmpty(tableTrolleyJsfResp.getTableTrolleyDtoJsfList())) {
      log.info("<===========sortCrossJsfManager.queryTableTrolleyListByDmsId============>:{}",tableTrolleyJsfResp.getTableTrolleyDtoJsfList().toString());
      tableTrolleyResp.setTableTrolleyDtoList(
          getTableTrolleyDto(tableTrolleyJsfResp.getTableTrolleyDtoJsfList()));
      tableTrolleyResp.setTotalPage(tableTrolleyJsfResp.getTotalPage());

      if (request.getNeedMatchGroupCTT() && ObjectHelper.isNotNull(request.getTemplateCode())) {
        JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
        condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
        condition.setTemplateCode(request.getTemplateCode());
        condition.setGroupCode(request.getGroupCode());
        List<JyGroupSortCrossDetailEntity> groupSortCrossDetailList = jyGroupSortCrossDetailService
            .listSendFlowByTemplateCodeOrEndSiteCode(condition);

        if (ObjectHelper.isNotNull(groupSortCrossDetailList)) {
          for (TableTrolleyDto dto : tableTrolleyResp.getTableTrolleyDtoList()) {
            for (JyGroupSortCrossDetailEntity entity : groupSortCrossDetailList) {
              if (entity.getEndSiteId().intValue() == dto.getEndSiteId()) {
                dto.setSelectedFlag(true);
              }
            }
          }
        }
      }
      if (request.getNeedSendFlowStatistics() && ObjectHelper.isNotNull(request.getCrossCode())) {
        List<Integer> endSiteIdList = new ArrayList<>();
        for (TableTrolleyDto dto : tableTrolleyResp.getTableTrolleyDtoList()) {
          endSiteIdList.add(dto.getEndSiteId());
        }
        try {
          BoardCountReq boardCountReq = new BoardCountReq();
          Date queryTime = DateHelper.addHoursByDay(new Date(), -Double.valueOf(dmsConfigManager.getPropertyConfig().getJyComboardTaskCreateTimeBeginDay()));
          boardCountReq.setCreateTime(queryTime);
          boardCountReq.setEndSiteIdList(endSiteIdList);
          boardCountReq.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
          List<Integer> comboardSourceList = new ArrayList<>();
          comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
          boardCountReq.setComboardSourceList(comboardSourceList);
          List<Integer> statusList = new ArrayList<>();
          statusList.add(ComboardStatusEnum.SEALED.getCode());
          statusList.add(ComboardStatusEnum.FINISHED.getCode());
          statusList.add(ComboardStatusEnum.CANCEL_SEAL.getCode());
          boardCountReq.setStatusList(statusList);
          List<BoardCountDto> finishBoardCount = jyBizTaskComboardService.boardCountTaskBySendFlowList(boardCountReq);

          List<Integer> inProcessBoardStatus = new ArrayList<>();
          inProcessBoardStatus.add(ComboardStatusEnum.PROCESSING.getCode());
          boardCountReq.setStatusList(inProcessBoardStatus);
          List<BoardCountDto> inProcessBoardCount = jyBizTaskComboardService.boardCountTaskBySendFlowList(boardCountReq);

          HashMap<Long,Integer> finishBoardMap = getMapByBoardCountDto(finishBoardCount);
          HashMap<Long,Integer> inProcessBoardMap = getMapByBoardCountDto(inProcessBoardCount);
          for (TableTrolleyDto dto : tableTrolleyResp.getTableTrolleyDtoList()) {
            Integer finishCount = finishBoardMap.getOrDefault(dto.getEndSiteId().longValue(),0);
            Integer inProcessCount = inProcessBoardMap.getOrDefault(dto.getEndSiteId().longValue(),0);
            dto.setFinishBoardCount(finishCount);
            dto.setInProcessBoardCount(inProcessCount);
            dto.setBoardCount(finishCount + inProcessCount);
          }
        } catch (Exception e) {
          log.error("listTableTrolleyUnderCross 查询流向统计异常", e);
        }
      }
    }
    return result;
  }

  private HashMap<Long, Integer> getMapByBoardCountDto(List<BoardCountDto> inProcessBoardCount) {
    HashMap<Long, Integer> map = new HashMap<>();
    for (BoardCountDto boardCountDto : inProcessBoardCount) {
      map.put(boardCountDto.getEndSiteId(),boardCountDto.getBoardCount());
    }
    return map;
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
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.createGroupCTTData", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<CreateGroupCTTResp> createGroupCTTData(CreateGroupCTTReq request) {
    if (!checkBaseRequest(request)) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    if (CollectionUtils.isEmpty(request.getTableTrolleyDtoList())) {
      throw new JyBizException("参数错误: 无流向信息！");
    }
    if (log.isInfoEnabled()) {
      log.info("开始保存本场地常用的笼车集合：{}", JsonHelper.toJson(request));
    }
    if (request.getTableTrolleyDtoList().size() > dmsConfigManager.getPropertyConfig().getCttGroupSendFLowLimit()) {
      throw new JyBizException("混扫任务流向不能超过"+ dmsConfigManager.getPropertyConfig().getCttGroupSendFLowLimit()+"个！");
    }

    String templateCode = jyGroupSortCrossDetailService.createGroup(request);
    CreateGroupCTTResp resp = new CreateGroupCTTResp();
    if (templateCode == null ) {
      return new InvokeResult(CREATE_GROUP_CTT_DATA_CODE, CREATE_GROUP_CTT_DATA_MESSAGE);
    }else {
      resp.setTemplateCode(templateCode);
    }

    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.getDefaultGroupCTTName", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<CreateGroupCTTResp> getDefaultGroupCTTName(BaseReq request) {
    CreateGroupCTTResp createGroupCTTResp = new CreateGroupCTTResp();
    String groupName = jyGroupSortCrossDetailService.getMixScanTaskDefaultName(GROUP_NAME_PREFIX);
    createGroupCTTResp.setTemplateName(groupName);
    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, createGroupCTTResp);
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.querySendFlowByBarCode", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<TableTrolleyResp> querySendFlowByBarCode(QuerySendFlowReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBarCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    if (!checkBarCode(request.getBarCode())) {
      return new InvokeResult<>(CHECK_BARCODE_CODE, CHECK_BARCODE_MESSAGE);
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
    }else if (SerialRuleUtil.isMatchNumeric(barCode)) {
      // 目的地站点
      query.setSiteCode(Integer.valueOf(barCode));
      tableTrolleyJsfResp = sortCrossJsfManager.queryCTTByCTTCode(query);
    }

    if (tableTrolleyJsfResp != null && !CollectionUtils
        .isEmpty(tableTrolleyJsfResp.getTableTrolleyDtoJsfList())) {
      tableTrolleyResp.setTableTrolleyDtoList(
          getTableTrolleyDto(tableTrolleyJsfResp.getTableTrolleyDtoJsfList()));
    } else {
      if (log.isInfoEnabled()) {
        log.info("获取滑道笼车信息异常:{}", JsonHelper.toJson(query));
      }
      return new InvokeResult<>(NOT_FIND_CTT_CODE, NOT_FIND_CTT_MESSAGE);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, tableTrolleyResp);
  }

  /**
   * 校验barCode格式
   */
  private boolean checkBarCode(String barCode) {
    if (WaybillUtil.isPackageCode(barCode) || BusinessHelper.isBoxcode(barCode) || checkCTTCode(
        barCode) || SerialRuleUtil.isMatchNumeric(barCode)) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  /**
   * 校验是否为滑道-笼车号
   */
  public static boolean checkCTTCode(String barCode) {
    if (barCode.contains("-")) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.addCTT2Group", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult addCTT2Group(AddCTTReq request) {
    if (!checkBaseRequest(request)) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    if (CollectionUtils.isEmpty(request.getTableTrolleyDtoList())) {
      throw new JyBizException("参数错误: 无流向信息！");
    }
    log.info("开始更新常用滑道笼车流向集合：{}", JsonHelper.toJson(request));
    // 校验是否包含当前流向
    for (TableTrolleyDto tableTrolleyDto : request.getTableTrolleyDtoList()) {
      JyGroupSortCrossDetailEntity query = new JyGroupSortCrossDetailEntity();
      query.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
      query.setGroupCode(request.getGroupCode());
      query.setTemplateCode(request.getTemplateCode());
      query.setEndSiteId(tableTrolleyDto.getEndSiteId().longValue());
      query.setFuncType(COMBOARD_SEND_POSITION.getCode());
      JyGroupSortCrossDetailEntity entity = jyGroupSortCrossDetailService.selectOneByFlowAndTemplateCode(query);
      if (entity != null) {
        log.error("已经存在流向，无法新增：{}", JsonHelper.toJson(entity));
        return new InvokeResult(HAVE_SEND_FLOW_UNDER_GROUP_CODE, HAVE_SEND_FLOW_UNDER_GROUP_MESSAGE);
      }
    }

    // 获取混扫任务下的流向信息
    JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
    condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
    condition.setTemplateCode(request.getTemplateCode());
    condition.setGroupCode(request.getGroupCode());
    List<JyGroupSortCrossDetailEntity> sendFlowList =
            jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(condition);
    if (!CollectionUtils.isEmpty(sendFlowList)) {
      Integer sendFlowSize = sendFlowList.size() + request.getTableTrolleyDtoList().size();
      if (sendFlowSize > dmsConfigManager.getPropertyConfig().getCttGroupSendFLowLimit()) {
        return new InvokeResult( UPDATE_CTT_GROUP_LIST_CODE, "混扫任务流向不能超过"+ dmsConfigManager.getPropertyConfig().getCttGroupSendFLowLimit()+"个");
      }
    }

    try {
      if (jyGroupSortCrossDetailService.addCTTGroup(request)) {
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
      }
    }catch (JyBizException e) {
      return new InvokeResult(UPDATE_CTT_GROUP_LIST_CODE, e.getMessage());
    }
    return new InvokeResult(UPDATE_CTT_GROUP_LIST_CODE, UPDATE_CTT_GROUP_LIST_MESSAGE);
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
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.removeCTTFromGroup", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult removeCTTFromGroup(RemoveCTTReq request) {
    if (!checkBaseRequest(request)
            || CollectionUtils.isEmpty(request.getTableTrolleyDtoList())
            || StringUtils.isEmpty(request.getTemplateCode())
    ) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    log.info("开始更新常用滑道笼车流向集合：{}", JsonHelper.toJson(request));
    //查询当前流向是否存在进行中的板
    List<Integer> endSiteCodeList = new ArrayList<>();
    for (TableTrolleyDto tableTrolleyDto : request.getTableTrolleyDtoList()) {
      endSiteCodeList.add(tableTrolleyDto.getEndSiteId());
    }
    List<JyBizTaskComboardEntity> boardListBySendFlowList = jyBizTaskComboardService
            .queryInProcessBoardListBySendFlowList(request.getCurrentOperate().getSiteCode(), endSiteCodeList,request.getGroupCode());
    if (!CollectionUtils.isEmpty(boardListBySendFlowList) && boardListBySendFlowList.size()>0) {
      return new InvokeResult(HAVE_IN_HAND_BOARD_CODE, HAVE_IN_HAND_BOARD_MESSAGE);
    }
    if (jyGroupSortCrossDetailService.removeCTTFromGroup(request)) {
      return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    } else {
      return new InvokeResult(UPDATE_CTT_GROUP_LIST_CODE, UPDATE_CTT_GROUP_LIST_MESSAGE);
    }
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.listCTTGroupData", mState = {JProEnum.TP, JProEnum.FunctionError})
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
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.queryCTTGroupByBarCode", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<CTTGroupDataResp> queryCTTGroupByBarCode(QueryCTTGroupReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBarCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    if (!checkBarCode(request.getBarCode())) {
      return new InvokeResult<>(CHECK_BARCODE_CODE, CHECK_BARCODE_MESSAGE);
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
    }else if (SerialRuleUtil.isMatchNumeric(barCode)) {
      // 目的地站点
      entity.setEndSiteId(Long.valueOf(barCode));
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
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.listSendFlowUnderCTTGroup", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<SendFlowDataResp> listSendFlowUnderCTTGroup(SendFlowDataReq request) {
    if (!checkBaseRequest(request) ||
            (StringUtils.isEmpty(request.getTemplateCode()) && request.getEndSiteId() == null)) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    SendFlowDataResp resp = new SendFlowDataResp();
    List<SendFlowDto> sendFlowDtoList = new ArrayList<>();
    resp.setSendFlowDtoList(sendFlowDtoList);
    List<Integer> endSiteCodeList = new ArrayList<>();
    List<JyGroupSortCrossDetailEntity> sendFlowList = new ArrayList<>();
    Integer startSiteCode = request.getCurrentOperate().getSiteCode();
    log.info("开始获取混扫任务下流向的详细信息：{}", JsonHelper.toJson(request));
    JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
    entity.setGroupCode(request.getGroupCode());
    entity.setStartSiteId(Long.valueOf(startSiteCode));
    // 获取当前场地扫描人员信息
    JyComboardEntity userQuery = new JyComboardEntity();
    userQuery.setGroupCode(request.getGroupCode());
    userQuery.setStartSiteId(Long.valueOf(startSiteCode));
    Date time = DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -dmsConfigManager.getPropertyConfig().getJyComboardScanUserBeginDay());
    userQuery.setCreateTime(time);
    List<User> userList = jyComboardService.queryUserByStartSiteCode(userQuery);
    resp.setScanUserList(userList);
    entity.setTemplateCode(request.getTemplateCode());
    // 获取混扫任务下的流向信息
    sendFlowList = jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(entity);
    if (CollectionUtils.isEmpty(sendFlowList)) {
      return new InvokeResult<>(SEND_FLOW_UNDER_GROUP_CODE, SEND_FLOW_UNDER_GROUP_MESSAGE);
    }
    // 获取目的地
    endSiteCodeList = getEndSiteCodeListBySendFlowList(sendFlowList);
    // 获取当前混扫任务下多个流向的组板数量和待扫统计
    List<JyComboardAggsEntity> jyComboardAggsEntities = null;
    try {
      jyComboardAggsEntities = jyComboardAggsService.queryComboardAggs(startSiteCode, endSiteCodeList);
    } catch (Exception e) {
      log.info("获取当前混扫任务下待扫统计失败: {}", JsonHelper.toJson(request),e);
    }
    HashMap<Long, JyComboardAggsEntity> sendFlowMap = getSendFlowMap(jyComboardAggsEntities);
    //查询多个流向下n天内未封车的板数量
    BoardCountReq boardCountReq = new BoardCountReq();
    Date queryTime = DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -dmsConfigManager.getPropertyConfig().getJyComboardTaskCreateTimeBeginDay());
    boardCountReq.setCreateTime(queryTime);
    boardCountReq.setEndSiteIdList(endSiteCodeList);
    boardCountReq.setStartSiteId(startSiteCode.longValue());
    List<Integer> comboardSourceList = new ArrayList<>();
    comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
    boardCountReq.setComboardSourceList(comboardSourceList);
    List<Integer> statusList = new ArrayList<>();
    statusList.add(ComboardStatusEnum.PROCESSING.getCode());
    statusList.add(ComboardStatusEnum.FINISHED.getCode());
    statusList.add(ComboardStatusEnum.CANCEL_SEAL.getCode());
    boardCountReq.setStatusList(statusList);
    List<BoardCountDto> entityList = jyBizTaskComboardService.boardCountTaskBySendFlowList(boardCountReq);
    HashMap<Long, Integer> boardCountMap = getBoardCountMap(entityList);
    // 获取当前流向执行中的板号
    List<JyBizTaskComboardEntity> boardList = jyBizTaskComboardService
            .queryInProcessBoardListBySendFlowList(startSiteCode, endSiteCodeList,request.getGroupCode());
    List<String> boardCodeList = getBoardCodeList(boardList);
    // 获取当前板号的扫描信息
    List<JyComboardAggsEntity> boardScanInfoList = null;
    try {
      boardScanInfoList = jyComboardAggsService.queryComboardAggs(boardCodeList);
    } catch (Exception e) {
      log.info("获取当前板号的扫描信息失败：{}", JsonHelper.toJson(request),e);
    }
    // 当前板的扫描信息
    HashMap<Long, JyComboardAggsEntity> boardFlowMap = getBoardFlowMap(boardScanInfoList,
            boardList);
    getSendFlowDtoList(sendFlowList, boardFlowMap, sendFlowMap, boardCountMap, sendFlowDtoList);
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  private JyGroupSortCrossDetailEntity getSendFlowList(TableTrolleyJsfDto tableTrolleyJsfDto) {
    JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
    entity.setEndSiteId(Long.valueOf(tableTrolleyJsfDto.getEndSiteId()));
    entity.setEndSiteName(tableTrolleyJsfDto.getEndSiteName());
    entity.setCrossCode(tableTrolleyJsfDto.getCrossCode());
    entity.setTabletrolleyCode(tableTrolleyJsfDto.getTableTrolleyCode());
    return entity;
  }

  private HashMap<Long, Integer> getBoardCountMap(List<BoardCountDto> entityList) {
    HashMap<Long, Integer> boardCountMap = new HashMap<>();
    for (BoardCountDto boardCountDto : entityList) {
      boardCountMap.put(boardCountDto.getEndSiteId(),boardCountDto.getBoardCount());
    }
    return boardCountMap;
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
    HashMap<String, JyComboardAggsEntity> boardMap = new HashMap<>();
    if (CollectionUtils.isEmpty(boardList) && CollectionUtils.isEmpty(boardScanInfoList)) {
      return boardFlowMap;
    }
    // 如果没有统计数据
    if (CollectionUtils.isEmpty(boardScanInfoList)) {
      for (JyBizTaskComboardEntity boardScanInfo : boardList) {
        JyComboardAggsEntity aggsEntity = new JyComboardAggsEntity();
        aggsEntity.setBoardCode(boardScanInfo.getBoardCode());
        boardFlowMap.put(boardScanInfo.getEndSiteId(),aggsEntity);
      }
      return boardFlowMap;
    }
    for (JyComboardAggsEntity aggsEntity : boardScanInfoList) {
      boardMap.put(aggsEntity.getBoardCode(),aggsEntity);
    }
    for (JyBizTaskComboardEntity boardScanInfo : boardList) {
      JyComboardAggsEntity aggsEntity = boardMap.get(boardScanInfo.getBoardCode());
      if (aggsEntity == null ) {
        JyComboardAggsEntity jyComboardAggsEntity = new JyComboardAggsEntity();
        jyComboardAggsEntity.setBoardCode(boardScanInfo.getBoardCode());
        boardFlowMap.put(boardScanInfo.getEndSiteId(),jyComboardAggsEntity);
      }else {
        boardFlowMap.put(boardScanInfo.getEndSiteId(),aggsEntity);
      }
    }
    return boardFlowMap;
  }

  private void getSendFlowDtoList(List<JyGroupSortCrossDetailEntity> sendFlowList,
                                  HashMap<Long, JyComboardAggsEntity> boardFlowMap,
                                  HashMap<Long, JyComboardAggsEntity> sendFlowMap,
                                  HashMap<Long, Integer> boardCountMap,
                                  List<SendFlowDto> sendFlowDtoList) {
    for (JyGroupSortCrossDetailEntity entity : sendFlowList) {
      Long endSiteCode = entity.getEndSiteId();
      SendFlowDto sendFlowDto = new SendFlowDto();
      sendFlowDto.setCrossCode(entity.getCrossCode());
      sendFlowDto.setTableTrolleyCode(entity.getTabletrolleyCode());
      sendFlowDto.setEndSiteId(entity.getEndSiteId().intValue());
      sendFlowDto.setEndSiteName(entity.getEndSiteName());
      JyComboardAggsEntity sendFlow = sendFlowMap.get(endSiteCode);
      if (boardCountMap!=null) {
        sendFlowDto.setBoardCount(boardCountMap.get(endSiteCode));
      }
      if (sendFlow != null) {
        sendFlowDto.setWaitScanCount(sendFlow.getWaitScanCount());
      }

      // 获取当前板号的扫描信息
      JyComboardAggsEntity boardFlow = boardFlowMap.get(endSiteCode);
      if (boardFlow != null) {
        BoardDto boardDto = new BoardDto();
        sendFlowDto.setCurrentBoardDto(boardDto);
        boardDto.setStatus(1);
        boardDto.setBoardCode(boardFlow.getBoardCode());
        boardDto.setBoxHaveScanCount(boardFlow.getBoxScannedCount());
        boardDto.setPackageHaveScanCount(boardFlow.getPackageScannedCount());
        boardDto.setInterceptCount(boardFlow.getInterceptCount());
        boardDto.setBoardScanLimit(dmsConfigManager.getPropertyConfig().getJyComboardCountLimit());
        // 已扫比例
        int scanCount = 0;
        if (boardFlow.getPackageScannedCount()!=null) {
          scanCount += boardFlow.getPackageScannedCount();
        }
        if ( boardFlow.getBoxScannedCount()!=null ) {
          scanCount += boardFlow.getBoxScannedCount();
        }
        int scanProgress = (int) ((scanCount * 1.00 / dmsConfigManager.getPropertyConfig().getJyComboardCountLimit()) * 100);
        boardDto.setProgress(String.valueOf(scanProgress));
      }
      sendFlowDtoList.add(sendFlowDto);
    }
  }

  private HashMap<Long, JyComboardAggsEntity> getSendFlowMap(
      List<JyComboardAggsEntity> jyComboardAggsEntities) {
    HashMap<Long, JyComboardAggsEntity> sendFlowMap = new HashMap<>();
    if (CollectionUtils.isEmpty(jyComboardAggsEntities)){
      return sendFlowMap;
    }
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
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.querySendFlowDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<SendFlowDetailResp> querySendFlowDetail(SendFlowDetailReq request) {
    if (!checkBaseRequest(request)
            || StringUtils.isEmpty(request.getBoardCode())
            || request.getEndSiteId() == null
            || request.getEndSiteId() < 0) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    SendFlowDetailResp resp = new SendFlowDetailResp();
    SendFlowDto sendFlowDto = new SendFlowDto();
    resp.setSendFlowDto(sendFlowDto);
    Integer startSiteCode = request.getCurrentOperate().getSiteCode();
    if (log.isInfoEnabled()) {
      log.info("开始流向下板的详细信息：{}", JsonHelper.toJson(request));
    }
    // 获取当前网格码内扫描人员信息
    JyComboardEntity userQuery = new JyComboardEntity();
    userQuery.setGroupCode(request.getGroupCode());
    userQuery.setStartSiteId(Long.valueOf(startSiteCode));
    Date time = DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -dmsConfigManager.getPropertyConfig().getJyComboardScanUserBeginDay());
    userQuery.setCreateTime(time);
    List<User> userList = jyComboardService.queryUserByStartSiteCode(userQuery);
    resp.setScanUserList(userList);
    // 获取当前流向
    TableTrolleyQuery query = new TableTrolleyQuery();
    query.setSiteCode(request.getEndSiteId());
    query.setDmsId(startSiteCode);
    TableTrolleyJsfResp tableTrolleyJsfResp = sortCrossJsfManager.queryCTTByStartEndSiteCode(query);
    if (tableTrolleyJsfResp != null && !CollectionUtils.isEmpty(tableTrolleyJsfResp.getTableTrolleyDtoJsfList())) {
      JyGroupSortCrossDetailEntity sendFlow = getSendFlowList(tableTrolleyJsfResp.getTableTrolleyDtoJsfList().get(0));
      sendFlowDto.setEndSiteId(sendFlow.getEndSiteId().intValue());
      sendFlowDto.setEndSiteName(sendFlow.getEndSiteName());
      sendFlowDto.setCrossCode(sendFlow.getCrossCode());
      sendFlowDto.setTableTrolleyCode(sendFlow.getTabletrolleyCode());
    } else {
      return new InvokeResult<>(SEND_FLOE_CTT_CODE, SEND_FLOE_CTT_MESSAGE, resp);
    }
    // 获取当前流向下的待扫统计
    JyComboardAggsEntity aggsEntity = null;
    try {
      aggsEntity = jyComboardAggsService.queryComboardAggs(startSiteCode, request.getEndSiteId());
    } catch (Exception e) {
      if (log.isInfoEnabled()) {
        log.info("获取流向下带扫数据失败：{}", JsonHelper.toJson(request));
      }
    }
    if (aggsEntity != null) {
      sendFlowDto.setWaitScanCount(aggsEntity.getWaitScanCount());
    }

    //查询流向下7天内未封车的板
    SendFlowDto sendFlow = new SendFlowDto();
    Date queryTime = DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -dmsConfigManager.getPropertyConfig().getJyComboardTaskCreateTimeBeginDay());
    sendFlow.setQueryTimeBegin(queryTime);
    sendFlow.setEndSiteId(request.getEndSiteId());
    sendFlow.setStartSiteId(startSiteCode);
    List<Integer> comboardSourceList = new ArrayList<>();
    comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
    sendFlow.setComboardSourceList(comboardSourceList);
    List<Integer> statusList = new ArrayList<>();
    statusList.add(ComboardStatusEnum.PROCESSING.getCode());
    statusList.add(ComboardStatusEnum.FINISHED.getCode());
    statusList.add(ComboardStatusEnum.CANCEL_SEAL.getCode());
    sendFlow.setStatusList(statusList);
    List<JyBizTaskComboardEntity> entities = jyBizTaskComboardService.listBoardTaskBySendFlow(sendFlow);
    sendFlowDto.setBoardCount(entities.size());
    BoardDto boardDto = new BoardDto();
    boardDto.setBoardScanLimit(dmsConfigManager.getPropertyConfig().getJyComboardCountLimit());

    // 查询当前板状态
    JyBizTaskComboardEntity queryBoard = new JyBizTaskComboardEntity();
    queryBoard.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
    queryBoard.setBoardCode(request.getBoardCode());
    JyBizTaskComboardEntity entity = jyBizTaskComboardService.queryBizTaskByBoardCode(queryBoard);
    boardDto.setStatus(entity.getBoardStatus());
    boardDto.setBoardCode(request.getBoardCode());
    sendFlowDto.setCurrentBoardDto(boardDto);
    // 获取当前板号的扫描信息
    JyComboardAggsEntity boardScanInfo = null;
    try {
      boardScanInfo = jyComboardAggsService.queryComboardAggs(request.getBoardCode());
    } catch (Exception e) {
      log.info("获取当前板号的扫描信息失败：{}", JsonHelper.toJson(request));
    }
    if (boardScanInfo != null) {
      boardDto.setBoxHaveScanCount(boardScanInfo.getBoxScannedCount());
      boardDto.setPackageHaveScanCount(boardScanInfo.getPackageScannedCount());
      boardDto.setInterceptCount(boardScanInfo.getInterceptCount());
      // 已扫比例
      int scanCount = 0;
      if (boardScanInfo.getPackageScannedCount() != null) {
        scanCount += boardScanInfo.getPackageScannedCount();
      }
      if (boardScanInfo.getBoxScannedCount() != null) {
        scanCount += boardScanInfo.getBoxScannedCount();
      }
      int scanProgress = (int) ((scanCount * 1.00 / dmsConfigManager.getPropertyConfig().getJyComboardCountLimit()) * 100);
      boardDto.setProgress(String.valueOf(scanProgress));
    }

    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.queryBoardDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
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
    // 查询是否是运单
    JyBizTaskComboardEntity queryBoard = new JyBizTaskComboardEntity();
    queryBoard.setStartSiteId(Long.valueOf(startSIteId));
    queryBoard.setBoardCode(boardCode);
    JyBizTaskComboardEntity boardTaskInfo = jyBizTaskComboardService
            .queryBizTaskByBoardCode(queryBoard);
    if (boardTaskInfo == null) {
      log.info("未获取到该板号的任务{}", JsonHelper.toJson(request));
      return new InvokeResult<>(BOARD_INFO_CODE, BOARD_INFO_MESSAGE);
    }
    resp.setBulkFlag(boardTaskInfo.getBulkFlag());
    resp.setEndSiteName(boardTaskInfo.getEndSiteName());

    // 获取组板包裹号箱号信息
    JyComboardAggsEntity boardInfo = null;
    try {
      boardInfo = jyComboardAggsService.queryComboardAggs(boardCode);
    } catch (Exception e) {
      log.info("获取板的详情信息失败：{}", JsonHelper.toJson(request), e);
      return new InvokeResult<>(BOARD_INFO_CODE, BOARD_INFO_MESSAGE);
    }
    if (boardInfo != null) {
      resp.setPackageHaveScanCount(boardInfo.getPackageScannedCount());
      resp.setBoxHaveScanCount(boardInfo.getBoxScannedCount());
    }
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
    List<JyComboardAggsEntity> productTypeList = null;
    try {
      productTypeList = jyComboardAggsService.
              queryComboardAggs(boardCode, UnloadProductTypeEnum.values());
    } catch (Exception e) {
      log.info("获取板的详情信息失败：{}", JsonHelper.toJson(request), e);
    }
    getGoodsCategoryList(goodsCategoryList, productTypeList);
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  private void getGoodsCategoryList(List<GoodsCategoryDto> goodsCategoryList,
      List<JyComboardAggsEntity> productTypeList) {
    if (CollectionUtils.isEmpty(productTypeList)){
      return;
    }
    for (JyComboardAggsEntity entity : productTypeList) {
      GoodsCategoryDto goodsCategoryDto = new GoodsCategoryDto();
      goodsCategoryDto.setCount(entity.getScannedCount());
      goodsCategoryDto.setName(UnloadProductTypeEnum.getNameByCode(entity.getProductType()));
      goodsCategoryDto.setType(entity.getProductType());
      goodsCategoryList.add(goodsCategoryDto);
    }
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.finishBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<String> finishBoard(BoardReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBoardCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    Integer startSiteId = request.getCurrentOperate().getSiteCode();
    String boardCode = request.getBoardCode();
    // 查询当前板号状态
    JyBizTaskComboardEntity entity = new JyBizTaskComboardEntity();
    entity.setStartSiteId(startSiteId.longValue());
    entity.setBoardCode(boardCode);
    entity.setBoardStatus(ComboardStatusEnum.PROCESSING.getCode());
    try {
      JyBizTaskComboardEntity board = jyBizTaskComboardService.queryBizTaskByBoardCode(entity);
      if (board == null) {
        if (log.isInfoEnabled()) {
          log.warn("该板已完结: {}", boardCode);
        }
        return new InvokeResult(FINISH_BOARD_AGAIN_CODE, FINISH_BOARD_AGAIN_MESSAGE,request.getBoardCode());
      }
      JyBizTaskComboardReq jyBizTaskComboardReq = new JyBizTaskComboardReq();
      jyBizTaskComboardReq.setBoardCode(boardCode);
      jyBizTaskComboardReq.setUpdateUserErp(request.getUser().getUserErp());
      jyBizTaskComboardReq.setUpdateUserName(request.getUser().getUserName());
      if (!jyBizTaskComboardService.finishBoard(jyBizTaskComboardReq)) {
        log.info("完结板异常：{}", boardCode);
        return new InvokeResult(FINISH_BOARD_CODE, FINISH_BOARD_MESSAGE);
      }
    } catch (Exception e) {
      log.info("完结板异常：{}", boardCode);
      return new InvokeResult(FINISH_BOARD_CODE, FINISH_BOARD_MESSAGE);
    }
    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE,request.getBoardCode());
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.finishBoardsUnderCTTGroup", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult finishBoardsUnderCTTGroup(CTTGroupReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getTemplateCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    Integer startSiteId = request.getCurrentOperate().getSiteCode();
    JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
    entity.setStartSiteId(Long.valueOf(startSiteId));
    entity.setTemplateCode(request.getTemplateCode());
    entity.setGroupCode(request.getGroupCode());
    try {

      // 获取当前混扫任务下的流向信息
      List<JyGroupSortCrossDetailEntity> sendFlowList = jyGroupSortCrossDetailService
          .listSendFlowByTemplateCodeOrEndSiteCode(entity);
      // 获取目的地
      List<Integer> endSiteCodeList = getEndSiteCodeListBySendFlowList(sendFlowList);
      if (CollectionUtils.isEmpty(endSiteCodeList)) {
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
      }
      List<JyBizTaskComboardEntity> boardInProcess = jyBizTaskComboardService
              .queryInProcessBoardListBySendFlowList(startSiteId, endSiteCodeList,request.getGroupCode());
      if (CollectionUtils.isEmpty(boardInProcess)){
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
      }
      // 根据流向批量结束组板
      JyBizTaskComboardReq req = new JyBizTaskComboardReq();
      req.setEndSiteCodeList(endSiteCodeList);
      req.setUpdateUserName(request.getUser().getUserName());
      req.setUpdateUserErp(request.getUser().getUserErp());
      req.setStartSiteId(startSiteId);
      List<Integer> comboardSourceList = new ArrayList<>();
      comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
      req.setComboardSourceList(comboardSourceList);
      if (!dmsConfigManager.getPropertyConfig().getCreateBoardBySendFlowSwitch()) {
        req.setGroupCode(request.getGroupCode());
      }
      if (!jyBizTaskComboardService.batchFinishBoardBySendFLowList(req)) {
        log.info("完结板失败，混扫任务编号：{}", request.getTemplateCode());
        return new InvokeResult(FINISH_BOARD_CODE, FINISH_BOARD_MESSAGE);
      }
    } catch (Exception e) {
      log.error("完结板异常，混扫任务编号：{}", request.getTemplateCode(),e);
      return new InvokeResult(FINISH_BOARD_CODE, FINISH_BOARD_MESSAGE);
    }
    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.comboardScan", mState = {JProEnum.TP, JProEnum.FunctionError})
  public JdVerifyResponse<ComboardScanResp> comboardScan(ComboardScanReq request) {
    JdVerifyResponse<ComboardScanResp> result = new JdVerifyResponse<>();
    result.toSuccess();
    try {
      baseCheck(request);
      comboardCheck(request);
      getOrCreateBoardCode(request);
      comboardCheckChain(request);
      sendCheck(request);

      execComboard(request);
      execSend(request);
      businessTips(request, result);
    } catch (NullPointerException e){
      log.error("JyComBoardSendServiceImpl comboardScan NullPointerException", e);
      result.toError("服务器开小差，请联系分拣小秘！");
      return result;
    } catch(JyBizException e) {
      ComboardScanResp resp = assembleComboardResp(request);
      resp.setInterceptFlag(checkIntercept(request));
      if (ObjectHelper.isNotNull(e.getCode())){
        if (BOARD_HAS_BEEN_FULL_CODE==e.getCode()){
          result.toCustomError(e.getCode(), e.getMessage());
          result.setData(resp);
          return result;
        }
        result.toCustomError(e.getCode(), e.getMessage());
        result.setData(resp);
        return result;
      }
      result.setData(resp);
      result.toError(e.getMessage());
      return result;
    }
    ComboardScanResp resp = assembleComboardResp(request);
    if (dmsConfigManager.getPropertyConfig().getSupportMutilScan() && request.getNeedSkipSendFlowCheck()){
      result.toCustomError(NOT_CONSISTENT_WHIT_CUR_SENDFLOW_CODE, NOT_CONSISTENT_WHIT_CUR_SENDFLOW_MESSAGE);
      result.setData(resp);
      return result;
    }
    result.setData(resp);
    return result;
  }

  //是否需要强拦截提醒
  private boolean checkIntercept(ComboardScanReq request) {
    if ((dmsConfigManager.getPropertyConfig().getInterceptBlackList().equals(Constants.TOTAL_URL_INTERCEPTOR) || checkContainsCurrentSite(request)) && request.getNeedIntercept()){
      return true;
    }
    return false;
  }

  private boolean checkContainsCurrentSite(ComboardScanReq request) {
    if (ObjectHelper.isNotNull(request.getCurrentOperate().getSiteCode())){
      List<String> siteList =new ArrayList<>();
      if (dmsConfigManager.getPropertyConfig().getInterceptBlackList().contains(Constants.SEPARATOR_COMMA)){
        siteList =Arrays.asList(dmsConfigManager.getPropertyConfig().getInterceptBlackList().split(Constants.SEPARATOR_COMMA));
      }
      else {
        siteList.add(dmsConfigManager.getPropertyConfig().getInterceptBlackList());
      }
      if (!CollectionUtils.isEmpty(siteList) && siteList.contains(String.valueOf(request.getCurrentOperate().getSiteCode()))){
        return true;
      }
    }
    return false;
  }

  private ComboardScanResp assembleComboardResp(ComboardScanReq request) {
    ComboardScanResp resp = new ComboardScanResp();
    resp.setEndSiteId(request.getDestinationId());
    resp.setBarCode(request.getBarCode());
    resp.setBarCodeType(request.getBarCodeType());
    resp.setScanDetailCount(request.getScanDetailCount());
    return resp;
  }

  @Override
  public InvokeResult<ComboardScanResp> sortMachineComboard(ComboardScanReq request) {
    if (!Objects.equals(request.getBizSource(),BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS.name())){
      throw new JyBizException("非自动化系统调用");
    }
    if (WaybillUtil.isWaybillCode(request.getBarCode()) && !WaybillUtil.isPackageCode(request.getBarCode())){
      throw new JyBizException("自动化系统不支持按运单组板发货");
    }
    CallerInfo info = Profiler.registerInfo("DMSWEB.JyComBoardSendServiceImpl.comboardScanForSortMachine", false, true);
    try{
      execSortMachineComboard(request);
      execSend(request);

      ComboardScanResp resp = new ComboardScanResp();
      resp.setEndSiteId(request.getDestinationId());
      resp.setBarCode(request.getBarCode());
      resp.setBarCodeType(request.getBarCodeType());
      resp.setScanDetailCount(request.getScanDetailCount());
      return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
    }catch (Exception e){
      Profiler.functionError(info);
      throw e;
    } finally {
      Profiler.registerInfoEnd(info);
    }
  }

  private void execSortMachineComboard(ComboardScanReq request) {
    String boardLockKey = String.format(Constants.JY_COMBOARD_BOARD_LOCK_PREFIX, request.getBoardCode());
    if (!jimDbLock.lock(boardLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
      throw new JyBizException("当前系统繁忙,请稍后再试！");
    }
    try {
      JyBizTaskComboardEntity entity = jyBizTaskComboardService.queryBizTaskByBoardCode(request.getCurrentOperate().getSiteCode(), request.getBoardCode());
      if (entity == null) {
        //存储jy_task
        JyBizTaskComboardEntity record = assembleJyBizTaskComboardParam(request);
        record.setHaveScanCount(1);
        record.setComboardSource(2);
        record.setCreateTime(request.getCurrentOperate().getOperateTime());
        record.setUnsealTime(request.getCurrentOperate().getOperateTime());
        jyBizTaskComboardService.save(record);
        // 板创建成功后，发送延时消息，处理两小时没有操作过组板的板号
        pushDelayDeleteBoardMQ(record);
        request.setBizId(record.getBizId());
      }else{
        request.setBizId(entity.getBizId());
      }
      AddBoardBox addBoardBox = assembleComboardParam(request);
      addBoardBox.setOperatorTime(request.getCurrentOperate().getOperateTime());
      Response<Integer> comboardResp = groupBoardManager.addBoxToBoard(addBoardBox);
      if (comboardResp.getCode() != ResponseEnum.SUCCESS.getIndex()) {
        throw new JyBizException(comboardResp.getMesseage()!=null?comboardResp.getMesseage():BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
      }

      JyComboardEntity comboardEntity = createJyComboardRecord(request);
      comboardEntity.setCreateTime(request.getCurrentOperate().getOperateTime());
      comboardEntity.setUpdateTime(request.getCurrentOperate().getOperateTime());
      jyComboardService.save(comboardEntity);
      //发送组板全程跟踪
      sendComboardWaybillTrace(request,WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);

    } finally {
      jimDbLock.releaseLock(boardLockKey, request.getRequestId());
    }
  }

  private void pushDelayDeleteBoardMQ(JyBizTaskComboardEntity record) {
    try {
      jyComboardTaskFirstSaveProducer.send(record.getBoardCode(), JsonHelper.toJson(record));
    } catch (Exception e) {
      log.info("首次保存组板任务发送jmq消息异常{}", JsonHelper.toJson(record));
    }
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
      
      if (ObjectHelper.isEmpty(entity)) {
        throw new JyBizException("该板以被清理，请重新扫描！");
      }
      
      if (!entity.getBulkFlag() && entity.getHaveScanCount() < dmsConfigManager.getPropertyConfig().getJyComboardCountLimit()) {
        Date now = new Date();
        if (entity.getHaveScanCount()<= Constants.NO_MATCH_DATA && WaybillUtil.isWaybillCode(request.getBarCode()) && !WaybillUtil.isPackageCode(request.getBarCode())) {
          //更新大宗标识
          JyBizTaskComboardEntity comboardEntity = new JyBizTaskComboardEntity();
          comboardEntity.setId(entity.getId());
          comboardEntity.setBulkFlag(true);
          comboardEntity.setHaveScanCount(request.getScanDetailCount());
          comboardEntity.setUpdateTime(now);
          comboardEntity.setUpdateUserErp(request.getUser().getUserErp());
          comboardEntity.setUpdateUserName(request.getUser().getUserName());
          jyBizTaskComboardService.updateBizTaskById(comboardEntity);
          //存一下jy_comboard
          JyComboardEntity jyComboardRecord = createJyComboardRecord(request);
          jyComboardService.save(jyComboardRecord);
          log.info("扫描大宗运单，走异步租板逻辑");
          asyncExecComboard(request);
          return;
        }
        AddBoardBox addBoardBox = assembleComboardParam(request);
        Response<Integer> comboardResp = groupBoardManager.addBoxToBoardV2(addBoardBox);
        if (comboardResp.getCode() != ResponseEnum.SUCCESS.getIndex()) {
          throw new JyBizException(comboardResp.getMesseage()!=null?comboardResp.getMesseage():BOARD_TOTC_FAIL_INTERCEPT_MESSAGE);
        }
        JyBizTaskComboardEntity bizTaskComboardEntity = new JyBizTaskComboardEntity();
        bizTaskComboardEntity.setId(entity.getId());
        bizTaskComboardEntity.setHaveScanCount(entity.getHaveScanCount() + request.getScanDetailCount());
        bizTaskComboardEntity.setUpdateTime(now);
        bizTaskComboardEntity.setUpdateUserErp(request.getUser().getUserErp());
        bizTaskComboardEntity.setUpdateUserName(request.getUser().getUserName());
        jyBizTaskComboardService.updateBizTaskById(bizTaskComboardEntity);
        JyComboardEntity comboardEntity = createJyComboardRecord(request);
        jyComboardService.save(comboardEntity);
        //发送组板全程跟踪
        sendComboardWaybillTrace(request,WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
      } else {
        throw new JyBizException("已到上限，需要换新板");
      }
    } finally {
      jimDbLock.releaseLock(boardLockKey, request.getRequestId());
    }
  }

  private void asyncExecComboard(ComboardScanReq request) {
    //分批次生成组板任务
    ComboardTaskDto dto =new ComboardTaskDto();
    dto.setStartSiteId(request.getCurrentOperate().getSiteCode());
    dto.setStartSiteName(request.getCurrentOperate().getSiteName());
    dto.setEndSiteId(request.getDestinationId());
    dto.setEndSiteName(request.getEndSiteName());
    dto.setBoardCode(request.getBoardCode());
    dto.setWaybillCode(request.getBarCode());
    dto.setUserErp(request.getUser().getUserErp());
    dto.setUserName(request.getUser().getUserName());
    dto.setUserCode(request.getUser().getUserCode());
    dto.setOperateTime(new Date());
    dto.setOperatorData(BeanConverter.convertToOperatorData(request.getCurrentOperate()));
    // 获取运单包裹数
    Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(dto.getWaybillCode());
    if (waybill == null || waybill.getGoodNumber() == null) {
      log.error("[异步组板任务]获取运单包裹数失败! code:{}, sendM:{}", dto.getWaybillCode(), JsonHelper.toJson(dto));
      return;
    }

    int totalNum = waybill.getGoodNumber();
    int onePageSize = dmsConfigManager.getPropertyConfig().getWaybillSplitPageSize() == 0 ? COMBOARD_SPLIT_NUM : dmsConfigManager.getPropertyConfig().getWaybillSplitPageSize();
    int pageTotal = (totalNum % onePageSize) == 0 ? (totalNum / onePageSize) : (totalNum / onePageSize) + 1;
    dto.setTotalPage(pageTotal);

    // 插入分页任务
    for (int i = 0; i < pageTotal; i++) {
      dto.setPageNo(i+1);
      dto.setPageSize(onePageSize);
      try {
        waybillComboardProducer.send(dto.getWaybillCode() + "_" + i+1, JsonHelper.toJson(dto));
        log.info("JyComBoardSendServiceImpl asyncExecComboard : {}", JsonHelper.toJson(dto));
      } catch (Exception e) {
        log.error("JyComBoardSendServiceImpl asyncExecComboard exception {}", e.getMessage(), e);
        throw new JyBizException("异步发送全程跟踪失败");
      }
    }
    log.info("====================成功生成大宗运单异步组板任务============================");
  }

  private BizSourceEnum getBizSourceEnum(ComboardScanReq request) {
    if (request.getBizSource().equals(BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS.name())){
      return BizSourceEnum.SORTING_MACHINE;
    }
    return BizSourceEnum.PDA;
  }

  private BizSourceEnum getBizSourceEnum(CancelBoardReq request) {
    if (request.getBizSource().equals(BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS.name())){
      return BizSourceEnum.SORTING_MACHINE;
    }
    return BizSourceEnum.PDA;
  }

  private SendBizSourceEnum getSendBizSourceEnumEnum(ComboardScanReq request) {
    int bizSource = 0;
    if (request.getBizSource().equals(BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS.name())){
      return SendBizSourceEnum.SORT_MACHINE_SEND;
    }
    return SendBizSourceEnum.JY_APP_SEND;
  }


  private void sendComboardWaybillTrace(ComboardScanReq request, Integer waybillTrackBoardCombination) {

    OperatorInfo operatorInfo = assembleComboardOperatorInfo(request);
    OperatorData operatorData = BeanConverter.convertToOperatorData(request.getCurrentOperate());
    virtualBoardService.sendWaybillTrace(request.getBarCode(), operatorInfo,operatorData, request.getBoardCode(),
        request.getEndSiteName(), waybillTrackBoardCombination,getBizSourceEnum(request).getValue());
  }

  private OperatorInfo assembleComboardOperatorInfo(ComboardScanReq request) {
    OperatorInfo operatorInfo = new OperatorInfo();
    operatorInfo.setSiteCode(request.getCurrentOperate().getSiteCode());
    operatorInfo.setSiteName(request.getCurrentOperate().getSiteName());
    operatorInfo.setUserCode(request.getUser().getUserCode());
    if(request.getCurrentOperate().getOperateTime() == null){
        operatorInfo.setOperateTime(new Date());
    }else{
      operatorInfo.setOperateTime(request.getCurrentOperate().getOperateTime());
    }
    operatorInfo.setOperatorTypeCode(request.getCurrentOperate().getOperatorTypeCode());
    operatorInfo.setOperatorId(request.getCurrentOperate().getOperatorId());
    return operatorInfo;
  }

  private AddBoardBox assembleComboardParam(ComboardScanReq request) {
    AddBoardBox addBoardBox = new AddBoardBox();
    addBoardBox.setBoardCode(request.getBoardCode());
    addBoardBox.setBarcodeType(getBarCodeType(request.getBarCode()));
    addBoardBox.setBoxCode(request.getBarCode());
    addBoardBox.setOperatorErp(request.getUser().getUserErp());
    addBoardBox.setOperatorName(request.getUser().getUserName());
    addBoardBox.setSiteCode(request.getCurrentOperate().getSiteCode());
    addBoardBox.setSiteName(request.getCurrentOperate().getSiteName());
    addBoardBox.setSiteType(BoardCommonManagerImpl.BOARD_COMBINATION_SITE_TYPE);
    addBoardBox.setBizSource(getBizSourceEnum(request).getValue());
    return addBoardBox;
  }

  /**
   * 执行发货
   */
  private void execSend(ComboardScanReq request) {
    SendKeyTypeEnum sendType = getSendType(request.getBarCode());
    SendM sendM = toSendMDomain(request);
    boolean oldForceSend = true;
    SendResult sendResult = new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
    switch (sendType) {
      case BY_WAYBILL:
        deliveryService.packageSendByRealWaybill(sendM, request.getCancelLastSend(), sendResult);
        break;
      case BY_PACKAGE:
        sendResult = deliveryService.packageSend(getSendBizSourceEnumEnum(request), sendM, oldForceSend,
            request.getCancelLastSend());
        break;
      case BY_BOX:
        sendResult = deliveryService.packageSend(getSendBizSourceEnumEnum(request), sendM, oldForceSend,
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
    if (!request.getSupportMutilSendFlow() && ObjectHelper.isNotNull(request.getBoardCode())) {
      JyBizTaskComboardEntity condition = new JyBizTaskComboardEntity();
      condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
      condition.setBoardCode(request.getBoardCode());
      JyBizTaskComboardEntity entity = jyBizTaskComboardService.queryBizTaskByBoardCode(condition);
      if (ComboardStatusEnum.SEALED.getCode() == entity.getBoardStatus()) {
        throw new JyBizException("已封车禁止继续扫描！");
      }
      if (ObjectHelper.isNotNull(entity.getBulkFlag()) && entity.getBulkFlag()){
        throw new JyBizException(NOT_SUPPORT_BULK_SCAN_FOR_REPLENISH_SCAN_CODE,NOT_SUPPORT_REPLENISH_SCAN_FOR_BULK_MESSAGE);
      }
      if (entity.getHaveScanCount()>Constants.NO_MATCH_DATA && WaybillUtil.isWaybillCode(request.getBarCode()) && !WaybillUtil.isPackageCode(request.getBarCode())){
        throw new JyBizException(NOT_SUPPORT_BULK_SCAN_FOR_REPLENISH_SCAN_CODE,NOT_SUPPORT_BULK_SCAN_FOR_REPLENISH_SCAN_MESSAGE);
      }
      request.setBizId(entity.getBizId());
      request.setSendCode(entity.getSendCode());
      return;
    }

    /**
     * 流向加锁
     */
    String sendFlowLockKey = String.format(Constants.JY_COMBOARD_SENDFLOW_LOCK_PREFIX, request.getDestinationId());
    if (dmsConfigManager.getPropertyConfig().getCreateBoardBySendFlowSwitch() && !jimDbLock.lock(sendFlowLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
      throw new JyBizException("当前系统繁忙,请稍后再试！");
    }

    String sendFlowAndGroupLockKey = String.format(Constants.JY_COMBOARD_SENDFLOW_GROUP_LOCK_PREFIX, request.getCurrentOperate().getSiteCode(), request.getDestinationId(), request.getGroupCode());
    if (!dmsConfigManager.getPropertyConfig().getCreateBoardBySendFlowSwitch() && !jimDbLock.lock(sendFlowAndGroupLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
      throw new JyBizException("当前系统繁忙,请稍后再试！");
    }
    try {
      //查询当前流向是否存在进行中的板
      SendFlowDto sendFlowDto = new SendFlowDto();
      sendFlowDto.setStartSiteId(request.getCurrentOperate().getSiteCode());
      sendFlowDto.setEndSiteId(request.getDestinationId());
      List<Integer> comboardSourceList = new ArrayList<>();
      comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
      sendFlowDto.setComboardSourceList(comboardSourceList);
      sendFlowDto.setGroupCode(request.getGroupCode());
      BoardDto boardDto = jyBizTaskComboardService.queryInProcessBoard(sendFlowDto);
      if (ObjectHelper.isNotNull(boardDto)) {
        if (boardDto.getCount()>Constants.NO_MATCH_DATA && WaybillUtil.isWaybillCode(request.getBarCode()) && !WaybillUtil.isPackageCode(request.getBarCode())){
          throw new JyBizException(BOARD_HAS_BEEN_FULL_CODE,BOARD_HAS_BEEN_FULL_MESSAGE);
        }
        if (!boardDto.getBulkFlag() && boardDto.getCount() < dmsConfigManager.getPropertyConfig().getJyComboardCountLimit()) {
          request.setBoardCode(boardDto.getBoardCode());
          request.setBizId(boardDto.getBizId());
          request.setSendCode(boardDto.getSendCode());
        } else {
          throw new JyBizException(BOARD_HAS_BEEN_FULL_CODE,BOARD_HAS_BEEN_FULL_MESSAGE);
        }
      } else {
        //生成新板任务，lock主要是lock的这个点
        generateNewBoard(request);
        //存储jy_task
        JyBizTaskComboardEntity record = assembleJyBizTaskComboardParam(request);
        //空板是不确定-是大宗还是非大宗，组板扫描成功后再确定
        jyBizTaskComboardService.save(record);
        // 板创建成功后，发送延时消息，删除两小时没有操作过组板的板号
        pushDelayDeleteBoardMQ(record);
      }
    } finally {
      if (dmsConfigManager.getPropertyConfig().getCreateBoardBySendFlowSwitch()){
        jimDbLock.releaseLock(sendFlowLockKey, request.getRequestId());
      }
      if (!dmsConfigManager.getPropertyConfig().getCreateBoardBySendFlowSwitch()) {
        jimDbLock.releaseLock(sendFlowAndGroupLockKey, request.getRequestId());
      }
    }
  }

  private void generateNewBoard(ComboardScanReq request) {
    AddBoardRequest addBoardRequest = new AddBoardRequest();
    addBoardRequest.setBoardCount(1);
    addBoardRequest.setDestinationId(request.getDestinationId());
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
    String ownerKey = String.format(JyBizTaskComboardEntity.BIZ_PREFIX, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
    String bizId = ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    request.setBizId(bizId);
    return bizId;
  }

  private JyBizTaskComboardEntity assembleJyBizTaskComboardParam(ComboardScanReq request) {
    JyBizTaskComboardEntity record = new JyBizTaskComboardEntity();
    record.setBoardCode(request.getBoardCode());
    record.setBizId(genTaskBizId(request));
    if (Objects.equals(getBizSourceEnum(request) ,BizSourceEnum.PDA)) {
      record.setSendCode(genSendCode(request));
    } else {
      record.setSendCode(request.getSendCode());
    }
    record.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
    record.setEndSiteId(Long.valueOf(request.getDestinationId()));
    record.setBoardStatus(ComboardStatusEnum.PROCESSING.getCode());
    Date now = new Date();
    record.setCreateTime(now);
    record.setUpdateTime(now);
    record.setCreateUserErp(request.getUser().getUserErp());
    record.setCreateUserName(request.getUser().getUserName());
    record.setUpdateUserErp(request.getUser().getUserErp());
    record.setUpdateUserName(request.getUser().getUserName());
    record.setGroupCode(request.getGroupCode());
    return record;
  }

  private String genSendCode(ComboardScanReq request) {
    Map<SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
    attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code,
        String.valueOf(request.getCurrentOperate().getSiteCode()));
    attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code,
        String.valueOf(request.getDestinationId()));
    String sendCode = sendCodeService
        .createSendCode(attributeKeyEnumObjectMap, BusinessCodeFromSourceEnum.getFromName(request.getBizSource()), request.getUser().getUserErp());
    request.setSendCode(sendCode);
    return sendCode;
  }

  private void baseCheck(ComboardScanReq request) {
    if (!ObjectHelper.isNotNull(request.getBarCode())){
      throw new JyBizException("参数错误：缺少包裹号、箱号、运单号！");
    }
    request.setBarCode(request.getBarCode().trim());
    /**
     * 参数校验
     */
    /*if (!ObjectHelper.isNotNull(request.getTemplateCode())) {
      throw new JyBizException("参数错误：缺少混扫任务编号！");
    }*/
    if (!ObjectHelper.isNotNull(request.getScanType())) {
      throw new JyBizException("参数错误：缺少扫货方式！");
    }
    if (!ObjectHelper.isNotNull(request.getEndSiteId())) {
      throw new JyBizException("参数错误：缺少目的地id！");
    }
    if (!request.getSupportMutilSendFlow() && !ObjectHelper.isNotNull(request.getBoardCode())){
      throw new JyBizException("参数错误：缺少板号！");
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
    if ((Objects.equals(SendVehicleScanTypeEnum.SCAN_ONE.getCode(), request.getScanType())
        ||Objects.equals(UnloadScanTypeEnum.SCAN_ONE.getCode(), request.getScanType())) &&
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
    request.setBarCodeType(barCodeType.getCode());
    request.setScanDetailCount(Constants.YN_YES);
  }

  /**
   * 租板相关校验
   */
  private void comboardCheck(ComboardScanReq request) {
    String barCode = request.getBarCode();
    if (WaybillUtil.isPackageCode(barCode) || WaybillUtil.isWaybillCode(barCode)) {
      Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCode(barCode));
      if (waybill == null) {
        throw new JyBizException("未查找到运单数据");
      }
      if (waybill.getOldSiteId() == null) {
        throw new JyBizException("运单对应的预分拣站点为空");
      }
      if (WaybillUtil.isWaybillCode(barCode) && !WaybillUtil.isPackageCode(request.getBarCode()) && waybill.getGoodNumber() < dmsConfigManager.getPropertyConfig().getBulkScanPackageMinCount()) {
        throw new JyBizException("大宗扫描：运单包裹数量不得低于100！");
      }
      if (Objects.equals(SendVehicleScanTypeEnum.SCAN_WAYBILL.getCode(), request.getScanType())){
        request.setScanDetailCount(waybill.getGoodNumber());
      }
      request.setDestinationId(waybill.getOldSiteId());
      //匹流向
      matchDestinationCheck(request);
      /**
       * 已集包校验
       */
      sortingCheck(request);

    } else if (BusinessHelper.isBoxcode(barCode)) {
      final Box box = boxService.findBoxByCode(barCode);
      if (box == null) {
        throw new JyBizException("未找到对应箱号，请检查");
      }
      request.setDestinationId(box.getReceiveSiteCode());

      if (StringUtils.isNotBlank(request.getMaterialCode()) && BusinessHelper.isBoxcode(barCode)) {
        BoxMaterialRelationRequest req = getBoxMaterialRelationRequest(request, barCode);
        //暂时忽略弱校验 等待晓峰修改后在支持
        req.setForceFlag(Boolean.TRUE);
        InvokeResult bindMaterialRet = cycleBoxService.boxMaterialRelationAlter(req);
        if (!bindMaterialRet.codeSuccess()) {
          throw new JyBizException("绑定集包袋失败：" + bindMaterialRet.getMessage());
        }
      }
      if (!cycleBagBindCheck(barCode, request.getCurrentOperate().getSiteCode(), box)) {
        throw new JyBizException(BoxResponse.CODE_BC_BOX_NO_BINDING,BoxResponse.MESSAGE_BC_NO_BINDING);
      }
      //匹配流向
      matchDestinationCheck(request);
    }
    JyComboardEntity condition = new JyComboardEntity();
    condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
    condition.setBarCode(barCode);
    JyComboardEntity entity = jyComboardService.queryIfScaned(condition);
    if (ObjectHelper.isNotNull(entity)) {
      Date comboardTime = entity.getCreateTime();
      if (comboardTime != null && System.currentTimeMillis() - comboardTime.getTime() <=  dmsConfigManager.getPropertyConfig().getReComboardTimeLimit() * 3600L * 1000L) {
        log.warn("组板失败：该单号以及组过板，{}", JsonHelper.toJson(entity));
        throw new JyBizException("该单号已组过板");
      }
    }
    BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseService.getSiteBySiteID(request.getDestinationId());
    if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper
        .isNotNull(baseStaffSiteOrgDto.getSiteName())) {
      request.setEndSiteName(baseStaffSiteOrgDto.getSiteName());
    }
    // 已在同场地发货，不可再组板
    final SendM recentSendMByParam = virtualBoardService
        .getRecentSendMByParam(barCode, request.getCurrentOperate().getSiteCode(), null, null);
    if (recentSendMByParam != null) {
      //三小时内禁止再次发货，返调度再次发货问题处理
      Date sendTime = recentSendMByParam.getOperateTime();
      if (sendTime != null
          && System.currentTimeMillis() - sendTime.getTime() <= dmsConfigManager.getPropertyConfig().getReComboardTimeLimit() * 3600L * 1000L) {
        throw new JyBizException("该单号已发货");
      }
    }
  }

  private BoxMaterialRelationRequest getBoxMaterialRelationRequest(ComboardScanReq request, String barCode) {
    BoxMaterialRelationRequest req = new BoxMaterialRelationRequest();
    req.setUserCode(request.getUser().getUserCode());
    req.setUserName(request.getUser().getUserName());
    req.setOperatorERP(request.getUser().getUserErp());
    req.setSiteCode(request.getCurrentOperate().getSiteCode());
    req.setSiteName(request.getCurrentOperate().getSiteName());
    req.setBoxCode(barCode);
    req.setMaterialCode(request.getMaterialCode());
    req.setBindFlag(Constants.CONSTANT_NUMBER_ONE); // 绑定
    return req;
  }

  /**
   * 发货相关校验
   */
  private void sendCheck(ComboardScanReq request) {
    SendM sendM = toSendMDomain(request);
    SendKeyTypeEnum sendType = getSendType(request.getBarCode());
    SendResult sendResult = new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
    sendStatusCheck(request, sendType, sendResult, sendM);
    if (request.getForceSendFlag() || request.getNeedSkipWeakIntercept()){
      return;
    }
    sendInterceptChain(request, sendM, sendType);
  }

  private void sendStatusCheck(ComboardScanReq request, SendKeyTypeEnum sendType,
      SendResult sendResult, SendM sendM) {
    if (SendKeyTypeEnum.BY_PACKAGE.equals(sendType) && deliveryService
        .isSendByWaybillProcessing(sendM)) {
      throw new JyBizException(HintService.getHint(HintCodeConstants.SEND_BY_WAYBILL_PROCESSING));
    }
    // 校验是否已经发货
    //deliveryService.multiSendVerification(sendM, sendResult);
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
      FilterChain filterChain = sortingCheckService.matchJyDeliveryFilterChain(sendType);
      SortingJsfResponse chainResp = sortingCheckService
          .doSingleSendCheckWithChain(sortingCheck, true, filterChain);
      if (!chainResp.getCode().equals(JdResponse.CODE_OK)) {
        if (!(JdResponse.CODE_SERVICE_ERROR.equals(chainResp.getCode())
            || chainResp.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM)) {
          // 拦截时保存拦截记录
          JyComboardEntity comboardEntity = createJyComboardRecord(request);
          comboardEntity.setInterceptFlag(true);
          comboardEntity.setInterceptTime(new Date());
          jyComboardService.save(comboardEntity);
          //弱拦截提示
          if (chainResp.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM) {
            throw new JyBizException(InvokeResult.COMBOARD_SCAN_WEAK_INTECEPTER_CODE,chainResp.getMessage());
          }
        }
        request.setNeedIntercept(true);
        throw new JyBizException(chainResp.getMessage());
      }
    }
  }

  private SendKeyTypeEnum getSendType(String barCode) {
    SendKeyTypeEnum sendType = null;
    if (WaybillUtil.isPackageCode(barCode)) {
      sendType = SendKeyTypeEnum.BY_PACKAGE;
    } else if (WaybillUtil.isWaybillCode(barCode)) {
      sendType = SendKeyTypeEnum.BY_WAYBILL;
    } else if (BusinessUtil.isBoardCode(barCode)) {
      sendType = SendKeyTypeEnum.BY_BOARD;
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
    comboardEntity.setEndSiteId(Long.valueOf(request.getDestinationId()));
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
    comboardEntity.setGroupCode(request.getGroupCode());
    if (request.getForceSendFlag()){
      comboardEntity.setForceSendFlag(true);
    }
    return comboardEntity;
  }

  private Integer getBarCodeType(String barCode) {
    if (WaybillUtil.isPackageCode(barCode)) {
      return ComboardBarCodeTypeEnum.PACKAGE.getCode();
    } else if (WaybillUtil.isWaybillCode(barCode)) {
      return ComboardBarCodeTypeEnum.WAYBILL.getCode();
    } else {
      return ComboardBarCodeTypeEnum.BOX.getCode();
    }
  }

  private SendM toSendMDomain(ComboardScanReq request) {
    SendM domain = new SendM();
    domain.setReceiveSiteCode(request.getDestinationId());
    domain.setSendCode(request.getSendCode());
    domain.setBoxCode(request.getBarCode());
    domain.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
    domain.setCreateUser(request.getUser().getUserName());
    domain.setCreateUserCode(request.getUser().getUserCode());
    domain.setSendType(DmsConstants.BUSSINESS_TYPE_POSITIVE);
    domain.setBizSource(getSendBizSourceEnumEnum(request).getCode());
    domain.setBoardCode(request.getBoardCode());
    domain.setYn(1);
    if (Objects.equals(request.getBizSource(), BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS.name())){
      Date date = DateUtils.addMilliseconds(request.getCurrentOperate().getOperateTime(), Constants.COMBOARD_SEND_DELAY_TIME);
      domain.setCreateTime(date);//固定加1秒
      domain.setOperateTime(date);//固定加1秒
    }else{
      domain.setCreateTime(new Date(System.currentTimeMillis()+ Constants.COMBOARD_SEND_DELAY_TIME));//固定加1秒
      domain.setOperateTime(new Date(System.currentTimeMillis()+ Constants.COMBOARD_SEND_DELAY_TIME));//固定加1秒
    }
    if(request.getCurrentOperate() != null) {
    	OperatorData operatorData = BeanConverter.convertToOperatorData(request.getCurrentOperate());
		domain.setOperatorTypeCode(operatorData.getOperatorTypeCode());
		domain.setOperatorId(operatorData.getOperatorId());
    	domain.setOperatorData(operatorData);
    }
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
        if (barCodeType != null && Objects.equals(barCodeType, BarCodeType.BOX_CODE) && !checkReComboard(sortingTemp)) {
          log.info("分拣传站租板校验--包裹【{}】 已经集包【{}】", request.getBarCode(), JsonHelper.toJson(sortingTemp));
          throw new JyBizException(BoxResponse.MESSAGE_CODE_PACKAGE_BOX);
        }
      }
    }
  }

  private boolean checkReComboard(Sorting sorting) {
    if (ObjectHelper.isNotNull(sorting)){
      Date sortingCreateTime = sorting.getCreateTime();
      if (sortingCreateTime != null && System.currentTimeMillis() - sortingCreateTime.getTime() >  dmsConfigManager.getPropertyConfig().getReComboardTimeLimit() * 3600L * 1000L) {
        return true;
      }
    }
    return false;
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
    if (request.getForceSendFlag() || request.getNeedSkipWeakIntercept()){
      return;
    }
    if (WaybillUtil.isPackageCode(request.getBarCode()) || WaybillUtil.isWaybillCode(request.getBarCode())) {
      final PdaOperateRequest pdaOperateRequest = new PdaOperateRequest();
      pdaOperateRequest.setPackageCode(request.getBarCode());
      pdaOperateRequest.setBoxCode(request.getBarCode());
      pdaOperateRequest.setReceiveSiteCode(request.getDestinationId());
      pdaOperateRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
      pdaOperateRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
      pdaOperateRequest.setOperateUserCode(request.getUser().getUserCode());
      pdaOperateRequest.setOperateUserName(request.getUser().getUserName());
      pdaOperateRequest.setOnlineStatus(BusinessInterceptOnlineStatusEnum.ONLINE.getCode());
      final CurrentOperate currentOperate = request.getCurrentOperate();
      final com.jd.bluedragon.common.dto.base.request.OperatorData operatorData = currentOperate.getOperatorData();
      if (operatorData != null) {
        pdaOperateRequest.setWorkGridKey(operatorData.getWorkGridKey());
        pdaOperateRequest.setWorkStationGridKey(operatorData.getWorkStationGridKey());
        pdaOperateRequest.setPositionCode(operatorData.getPositionCode());
      }
      BoardCombinationJsfResponse interceptResult = sortingCheckService.virtualBoardCombinationCheck(pdaOperateRequest);
      if (!interceptResult.getCode().equals(200)) {
        JyComboardEntity comboardEntity = createJyComboardRecord(request);
        comboardEntity.setInterceptFlag(true);
        comboardEntity.setInterceptTime(new Date());
        jyComboardService.save(comboardEntity);
        //弱拦截提示
        if (interceptResult.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM) {
          throw new JyBizException(InvokeResult.COMBOARD_SCAN_WEAK_INTECEPTER_CODE,interceptResult.getMessage());
        }
        request.setNeedIntercept(true);
        throw new JyBizException(interceptResult.getMessage());
      }
    }
  }


  boolean needForceSend(Integer siteId){
    if (dmsConfigManager.getPropertyConfig().getForceSendSiteList().equals(Constants.TOTAL_URL_INTERCEPTOR) || dmsConfigManager.getPropertyConfig().getForceSendSiteList().contains(String.valueOf(siteId))){
      return true;
    }
    return false;
  }
  /**
   * 校验当前barCode的流向 是否在当前混扫任务的流向范围内
   */
  private void matchDestinationCheck(ComboardScanReq request) {
    if (!request.getSupportMutilSendFlow()){
      if (!request.getEndSiteId().equals(request.getDestinationId())){
        final Integer parentSiteId = baseService.getMappingSite(request.getDestinationId());
        if (parentSiteId==null || !request.getEndSiteId().equals(parentSiteId)){
          if (request.getForceSendFlag()){
            request.setDestinationId(request.getEndSiteId());
            return;
          }
          if (!BusinessUtil.isBoxcode(request.getBarCode()) && needForceSend(request.getCurrentOperate().getSiteCode())){
            BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseService.getSiteBySiteID(request.getEndSiteId());
            if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())){
              throw new JyBizException(COMBOARD_SCAN_FORCE_SEND_WARNING,"非本流向货物，是否强制发往【"+baseStaffSiteOrgDto.getSiteName()+"】？");
            }
          }
          throw new JyBizException(NOT_BELONG_THIS_SENDFLOW_CODE,NOT_BELONG_THIS_SENDFLOW_MESSAGE);
        }
        request.setDestinationId(parentSiteId);
      }
      return;
    }
    if (!ObjectHelper.isNotNull(request.getTemplateCode())) {
      throw new JyBizException("参数错误：缺少混扫任务编号！");
    }

    JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
    condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
    condition.setGroupCode(request.getGroupCode());
    condition.setTemplateCode(request.getTemplateCode());

    List<JyGroupSortCrossDetailEntity> groupSortCrossDetailEntityList = jyGroupSortCrossDetailDao
        .listSendFlowByTemplateCodeOrEndSiteCode(condition);

    boolean hasMatchDestinationIdFlag = false;
    for (JyGroupSortCrossDetailEntity entity : groupSortCrossDetailEntityList) {
      if (entity.getEndSiteId().intValue() == request.getDestinationId()) {
        hasMatchDestinationIdFlag = true;
        break;
      }
    }
    // 如果获取不到匹配流向，则获取大小站
    if (!hasMatchDestinationIdFlag) {
      final Integer parentSiteId = baseService.getMappingSite(request.getDestinationId());
      if (parentSiteId != null) {
        for (JyGroupSortCrossDetailEntity entity : groupSortCrossDetailEntityList) {
          if (entity.getEndSiteId().intValue() == parentSiteId) {
            hasMatchDestinationIdFlag = true;
            request.setDestinationId(parentSiteId);
            break;
          }
        }
      }
    }
    //不在混扫的流向内
    if (!hasMatchDestinationIdFlag) {
      if (request.getForceSendFlag()){
        request.setDestinationId(request.getEndSiteId());
        return;
      }
      if (!BusinessUtil.isBoxcode(request.getBarCode()) && needForceSend(request.getCurrentOperate().getSiteCode())){
        BaseStaffSiteOrgDto baseStaffSiteOrgDto =baseService.getSiteBySiteID(request.getEndSiteId());
        if (ObjectHelper.isNotNull(baseStaffSiteOrgDto) && ObjectHelper.isNotNull(baseStaffSiteOrgDto.getSiteName())){
          throw new JyBizException(COMBOARD_SCAN_FORCE_SEND_WARNING,"非本混扫任务流向货物，是否强制发往【"+baseStaffSiteOrgDto.getSiteName()+"】？");
        }
      }
      throw new JyBizException(NOT_BELONG_THIS_HUNSAO_TASK_CODE,NOT_BELONG_THIS_HUNSAO_TASK_MESSAGE);
    }
    //混扫切换流向
    if (!request.getEndSiteId().equals(request.getDestinationId()) && !request.getNeedSkipSendFlowCheck()){
      if (dmsConfigManager.getPropertyConfig().getSupportMutilScan()){
        request.setNeedSkipSendFlowCheck(true);
      }
      else {
        throw new JyBizException(NOT_CONSISTENT_WHIT_LAST_SENDFLOW_CODE,NOT_CONSISTENT_WHIT_LAST_SENDFLOW_MESSAGE);
      }
    }
  }

  private void businessTips(ComboardScanReq request, JdVerifyResponse<ComboardScanResp> response){
    try {
      // 只处理包裹号或运单号
      if(!WaybillUtil.isWaybillCode(request.getBarCode()) && !WaybillUtil.isPackageCode(request.getBarCode())){
        log.warn("此单非包裹或运单号");
        return;
      }
      String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());

      Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(waybillCode);
      // 1. 航空件类型
      if(BusinessUtil.isAirLineMode(waybill.getWaybillSign())){
        response.addPromptBox(InvokeResult.CODE_AIR_TRANSPORT, InvokeResult.CODE_AIR_TRANSPORT_MESSAGE);
      }

      // 2. 增值服务-特安
      final List<WaybillVasDto> waybillVasList = waybillCommonService.getWaybillVasList(waybillCode);
      if(org.apache.commons.collections4.CollectionUtils.isEmpty(waybillVasList)){
        return;
      }
      final com.jd.dms.java.utils.sdk.base.Result<Boolean> specialSafetyCheckResult = waybillCommonService.checkWaybillVas(waybillCode, WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFETY, waybillVasList);
      if(specialSafetyCheckResult.isSuccess() && specialSafetyCheckResult.getData()){
        response.addWarningBox(InvokeResult.REVOKE_TEAN_CODE, InvokeResult.REVOKE_TEAN_MESSAGE);
      }
      // 3. 增值服务-生鲜特保
      final com.jd.dms.java.utils.sdk.base.Result<Boolean> specialSafeColdFreshCheckResult = waybillCommonService.checkWaybillVas(waybillCode, WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFEGUARD_COLD_FRESH_OPERATION, waybillVasList);
      if(specialSafeColdFreshCheckResult.isSuccess() && specialSafeColdFreshCheckResult.getData()){
        response.addPromptBox(InvokeResult.CODE_FRESH_SPECIAL, InvokeResult.CODE_FRESH_SPECIAL_MESSAGE);
      }
    } catch (Exception e) {
      log.error("businessTips param {}", JsonHelper.toJson(request), e);
    }
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.queryBoardStatisticsUnderSendFlow", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<BoardStatisticsResp> queryBoardStatisticsUnderSendFlow(
      BoardStatisticsReq request) {
    BoardStatisticsResp resp =BeanUtils.copy(request,BoardStatisticsResp.class);
    try {
      //填充流向统计数据
      JyComboardAggsEntity aggsEntity =jyComboardAggsService.queryComboardAggs(request.getCurrentOperate().getSiteCode(),request.getEndSiteId());
      if (ObjectHelper.isNotNull(aggsEntity)){
        resp.setInterceptCount(aggsEntity.getInterceptCount());
        resp.setWaitScanCount(aggsEntity.getWaitScanCount());
        resp.setPackageHaveScanCount(aggsEntity.getPackageScannedCount());
        resp.setBoxHaveScanCount(aggsEntity.getBoxScannedCount());
      }
    } catch (Exception e) {
      log.error("queryBoardStatisticsUnderSendFlow 查询流向统计数据异常",e);
    }
    //查询流向下7天内未封车的板 和两日内已封车的板
    SendFlowDto sendFlowDto = assemblySendFlowParams(request);
    Page page =PageHelper.startPage(request.getPageNo(),request.getPageSize());
    List<JyBizTaskComboardEntity> entityList = jyBizTaskComboardService.listSealOrUnSealedBoardTaskBySendFlow(sendFlowDto);
    if (ObjectHelper.isNotNull(entityList) && entityList.size() > 0) {
      List<String> boardCodeList = new ArrayList<>();
      List<BoardDto> boardDtoList = new ArrayList<>();
      resp.setBoardDtoList(boardDtoList);
      resp.setTotalBoardCount((int)page.getTotal());
      for (JyBizTaskComboardEntity entity : entityList) {
        boardCodeList.add(entity.getBoardCode());
        BoardDto boardDto =new BoardDto();
        boardDto.setBoardCode(entity.getBoardCode());
        boardDto.setStatus(entity.getBoardStatus());
        boardDto.setStatusDesc(ComboardStatusEnum.getStatusDesc(entity.getBoardStatus()));
        boardDtoList.add(boardDto);
      }
      try {
        //填充板的统计数据
        List<JyComboardAggsEntity> aggsEntityList = jyComboardAggsService.queryComboardAggs(boardCodeList);
        if (ObjectHelper.isNotNull(aggsEntityList) && aggsEntityList.size() > 0) {
          for (BoardDto boardDto:boardDtoList){
            for (JyComboardAggsEntity aggsEntity:aggsEntityList){
              if (boardDto.getBoardCode().equals(aggsEntity.getBoardCode())){
                boardDto.setInterceptCount(aggsEntity.getInterceptCount());
                boardDto.setBoxHaveScanCount(aggsEntity.getBoxScannedCount());
                boardDto.setPackageHaveScanCount(aggsEntity.getPackageScannedCount());
              }
            }
          }
        }
      } catch (Exception e) {
        log.error("queryBoardStatisticsUnderSendFlow 查询板列表统计数据异常", e);
      }
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,resp);
  }



  private SendFlowDto assemblySendFlowParams(BoardStatisticsReq request) {
    SendFlowDto sendFlowDto =new SendFlowDto();
    sendFlowDto.setStartSiteId(request.getCurrentOperate().getSiteCode());
    sendFlowDto.setEndSiteId(request.getEndSiteId());
    sendFlowDto.setQueryTimeBegin(DateHelper.addHoursByDay(new Date(), -Double.valueOf(dmsConfigManager.getPropertyConfig().getJyComboardTaskCreateTimeBeginDay())));
    sendFlowDto.setQuerySealTimeBegin(DateHelper.addHoursByDay(new Date(), -Double.valueOf(dmsConfigManager.getPropertyConfig().getJyComboardTaskSealTimeBeginDay())));
    List<Integer> comboardSourceList = new ArrayList<>();
    comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
    sendFlowDto.setComboardSourceList(comboardSourceList);
    return sendFlowDto;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.queryHaveScanStatisticsUnderBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<HaveScanStatisticsResp> queryHaveScanStatisticsUnderBoard(HaveScanStatisticsReq request) {
    checkHaveScanStatisticsParams(request);
    HaveScanStatisticsResp haveScanStatisticsResp =new HaveScanStatisticsResp();
    Pager<JyComboardPackageDetail> query = assembleQueryHaveScan(request);
    Pager<ComboardScanedDto> comboardScanedDtoPager =comboardJsfManager.queryScannedDetail(query);
    if (ObjectHelper.isNotNull(comboardScanedDtoPager) && ObjectHelper.isNotNull(comboardScanedDtoPager.getData()) ){
      List<ComboardScanedDto> comboardScanedDtoLis = comboardScanedDtoPager.getData();
      List<HaveScanDto> haveScanDtoList =new ArrayList<>();
      for (ComboardScanedDto comboardScanedDto:comboardScanedDtoLis){
        HaveScanDto haveScanDto =new HaveScanDto();
        haveScanDto.setBarCode(comboardScanedDto.getBarCode());
        haveScanDto.setType(comboardScanedDto.getBarCodeType());
        haveScanDto.setPackageCount(comboardScanedDto.getPackageCount());
        haveScanDto.setLabelOptionList(resolveProductTag(comboardScanedDto));
        haveScanDtoList.add(haveScanDto);
      }
      haveScanStatisticsResp.setHaveScanDtoList(haveScanDtoList);
    }
    return new InvokeResult<>(SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,haveScanStatisticsResp);
  }

  private List<LabelOption> resolveProductTag(ComboardScanedDto comboardScanedDto) {
    List<LabelOption> tagList = new ArrayList<>();
    tagList.add(new LabelOption(Constants.NO_MATCH_DATA,UnloadProductTypeEnum.getNameByCode(comboardScanedDto.getProductType())));
    return tagList;
  }

  private Pager<JyComboardPackageDetail> assembleQueryHaveScan(HaveScanStatisticsReq request) {
    Pager pager =new Pager();
    JyComboardPackageDetail condition =new JyComboardPackageDetail();
    condition.setBoardCode(request.getBoardCode());
    condition.setScannedFlag(Constants.YN_YES);
    condition.setOperateSiteId(request.getCurrentOperate().getSiteCode());
    condition.setNeedQueryBox(true);
    pager.setPageNo(request.getPageNo());
    pager.setPageSize(request.getPageSize());
    if (ObjectHelper.isEmpty(request.getPageNo())){
      pager.setPageNo(Constants.YN_YES);
    }
    if (ObjectHelper.isEmpty(request.getPageSize())){
      pager.setPageSize(Constants.COMBOARD_LIMIT);
    }
    pager.setSearchVo(condition);
    return pager;
  }

  private void checkHaveScanStatisticsParams(HaveScanStatisticsReq request) {
    if (!ObjectHelper.isNotNull(request.getBoardCode())){
      throw new JyBizException("参数错误：缺失板号");
    }
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.listPackageDetailRespUnderBox", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<PackageDetailResp> listPackageDetailRespUnderBox(BoxQueryReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBoxCode())
        || request.getPageNo() < 1 || request.getPageSize() < 1) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    PackageDetailResp resp = new PackageDetailResp();
    List<PackageScanDto> packageCodeList = new ArrayList<>();
    resp.setPackageCodeList(packageCodeList);
    SortingPageRequest sortingPageRequest = new SortingPageRequest();
    sortingPageRequest.setOffset((request.getPageNo() - 1) * request.getPageSize());
    sortingPageRequest.setLimit(request.getPageSize());
    sortingPageRequest.setBoxCode(request.getBoxCode());
    sortingPageRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
    List<String> codeList = sortingService.getPagePackageNoByBoxCode(sortingPageRequest);
    log.info("获取包裹号结果：{}", JsonHelper.toJson(codeList));
    for (String packageCode : codeList) {
      PackageScanDto packageScanDto = new PackageScanDto();
      packageScanDto.setPackageCode(packageCode);
      packageCodeList.add(packageScanDto);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.queryWaitScanStatisticsUnderSendFlow", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<WaitScanStatisticsResp> queryWaitScanStatisticsUnderSendFlow(WaitScanStatisticsReq request) {
    checkWaitScanParams(request);
    WaitScanStatisticsResp waitScanStatisticsResp =new WaitScanStatisticsResp();
    try {
      List<JyComboardAggsEntity> comboardAggsEntityList =jyComboardAggsService.queryComboardAggs(request.getCurrentOperate().getSiteCode(),request.getEndSiteId(),UnloadProductTypeEnum.values());
      if (ObjectHelper.isNotNull(comboardAggsEntityList)){
        List<GoodsCategoryDto> goodsCategoryDtoList =new ArrayList<>();
        for (JyComboardAggsEntity jyComboardAggsEntity:comboardAggsEntityList){
          GoodsCategoryDto goodsCategoryDto =new GoodsCategoryDto();
          goodsCategoryDto.setType(jyComboardAggsEntity.getProductType());
          goodsCategoryDto.setName(UnloadProductTypeEnum.getNameByCode(jyComboardAggsEntity.getProductType()));
          goodsCategoryDto.setCount(jyComboardAggsEntity.getWaitScanCount());
          goodsCategoryDtoList.add(goodsCategoryDto);
        }
        waitScanStatisticsResp.setGoodsCategoryDtoList(goodsCategoryDtoList);

        Pager<JyComboardPackageDetail> query =assembleQueryWaitScan(request);
        Pager<ComboardScanedDto> pager =comboardJsfManager.queryWaitScanDetail(query);
        if (ObjectHelper.isNotNull(pager) && ObjectHelper.isNotNull(pager.getData())){
          List<ComboardScanedDto> comboardScanedDtoList =pager.getData();
          List<WaitScanDto> waitScanDtoList =new ArrayList<>();
          for (ComboardScanedDto comboardScanedDto:comboardScanedDtoList){
            WaitScanDto waitScanDto =new WaitScanDto();
            waitScanDto.setBarCode(comboardScanedDto.getBarCode());
            //waitScanDto.setType();
            waitScanDtoList.add(waitScanDto);
          }
          waitScanStatisticsResp.setWaitScanDtoList(waitScanDtoList);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      log.error("queryWaitScanStatisticsUnderSendFlow 查询流向待扫数据异常",e);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE,waitScanStatisticsResp);
  }

  private Pager<JyComboardPackageDetail> assembleQueryWaitScan(WaitScanStatisticsReq request) {
    Pager<JyComboardPackageDetail> pager = new Pager<>();
    JyComboardPackageDetail con =new JyComboardPackageDetail();
    con.setOperateSiteId(request.getCurrentOperate().getSiteCode());
    con.setReceiveSiteId(String.valueOf(request.getEndSiteId()));
    con.setProductType(request.getGoodsType());
    con.setScannedFlag(Constants.YN_NO);
    pager.setSearchVo(con);
    pager.setPageNo(request.getPageNo());
    pager.setPageSize(request.getPageSize());
    return pager;
  }

  private void checkWaitScanParams(WaitScanStatisticsReq request) {
    if (ObjectHelper.isEmpty(request.getEndSiteId())){
      throw  new JyBizException("参数错误:缺少endSiteId");
    }
    if(ObjectHelper.isEmpty(request.getGoodsType())){
      request.setGoodsType(UnloadProductTypeEnum.FAST.getCode());
    }
  }

  @Override
  public InvokeResult<PackageDetailResp> listPackageDetailRespUnderSendFlow(
      SendFlowQueryReq request) {
    return null;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.queryExcepScanStatisticsUnderBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<BoardExcepStatisticsResp> queryExcepScanStatisticsUnderBoard(BoardExcepStatisticsReq request) {
    checkExcepScanParams(request);
    BoardExcepStatisticsResp resp =new BoardExcepStatisticsResp();
    try {
      JyComboardAggsEntity jyComboardAggsEntity =jyComboardAggsService.queryComboardAggs(request.getBoardCode());
      if (ObjectHelper.isNotNull(jyComboardAggsEntity) && checkIfExcep(jyComboardAggsEntity)){
        List<ExcepScanDto> excepScanDtoList = assembleExcepScanDtoList(jyComboardAggsEntity);
        resp.setExcepScanDtoList(excepScanDtoList);

        Pager<JyComboardPackageDetail> query =assembleQueryExcepScan(request);
        log.info("请求参数：{}", JsonHelper.toJson(query));
        Pager<ComboardScanedDto> pager =comboardJsfManager.queryInterceptDetail(query);
        log.info("返回结果：{}",JsonHelper.toJson(pager));
        if (ObjectHelper.isNotNull(pager) && ObjectHelper.isNotNull(pager.getData())){
          List<ComboardScanedDto> comboardScanedDtoList =pager.getData();
          List<PackageScanDto> packageScanDtoList =new ArrayList<>();
          for (ComboardScanedDto comboardScanedDto:comboardScanedDtoList){
            PackageScanDto packageScanDto =new PackageScanDto();
            packageScanDto.setPackageCode(comboardScanedDto.getBarCode());
            packageScanDtoList.add(packageScanDto);
          }
          resp.setPackageCodeList(packageScanDtoList);
        }
      }
    } catch (Exception e) {
      log.error("queryExcepScanStatisticsUnderBoard 查询流向待扫数据异常",e);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,resp);
  }

  private boolean checkIfExcep(JyComboardAggsEntity jyComboardAggsEntity) {
    if (ObjectHelper.isNotNull(jyComboardAggsEntity.getInterceptCount())
        && jyComboardAggsEntity.getInterceptCount()>0){
      return true;
    }
    return false;
  }

  private Pager<JyComboardPackageDetail> assembleQueryExcepScan(BoardExcepStatisticsReq request) {
    Pager<JyComboardPackageDetail> pager = new Pager<>();
    JyComboardPackageDetail con =new JyComboardPackageDetail();
    con.setOperateSiteId(request.getCurrentOperate().getSiteCode());
    con.setBoardCode(request.getBoardCode());
    con.setInterceptFlag(Constants.YN_YES);
    pager.setSearchVo(con);
    pager.setPageNo(request.getPageNo());
    pager.setPageSize(request.getPageSize());
    return pager;
  }

  private List<ExcepScanDto> assembleExcepScanDtoList(JyComboardAggsEntity jyComboardAggsEntity) {
    List<ExcepScanDto> excepScanDtoList =new ArrayList<>();
    ExcepScanDto excepScanDto =new ExcepScanDto();
    excepScanDto.setType(ExcepScanTypeEnum.INTERCEPTED.getCode());
    excepScanDto.setName(ExcepScanTypeEnum.INTERCEPTED.getName());
    excepScanDto.setCount(jyComboardAggsEntity.getInterceptCount());
    excepScanDtoList.add(excepScanDto);
    return excepScanDtoList;
  }

  private void checkExcepScanParams(BoardExcepStatisticsReq request) {
    if (ObjectHelper.isEmpty(request.getBoardCode())){
      throw new JyBizException("参数错误：缺失板号信息！");
    }
    if (ObjectHelper.isEmpty(request.getExcepType())){
      request.setExcepType(ExcepScanTypeEnum.INTERCEPTED.getCode());
    }
    if (ObjectHelper.isNotNull(request.getExcepType()) && !ExcepScanTypeEnum.INTERCEPTED.getCode().equals(request.getExcepType())){
      throw  new JyBizException("暂不支持该异常类型的查询！");
    }

  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.queryExcepScanStatisticsUnderCTTGroup", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<SendFlowExcepStatisticsResp> queryExcepScanStatisticsUnderCTTGroup(
      SendFlowExcepStatisticsReq request) {
    checkExcepScanUnderCTTParams(request);
    JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
    entity.setGroupCode(request.getGroupCode());
    entity.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
    entity.setTemplateCode(request.getTemplateCode());
    // 获取混扫任务下的流向信息
    List<JyGroupSortCrossDetailEntity>  groupSortCrossDetailList= jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(entity);
    if (!ObjectHelper.isNotNull(groupSortCrossDetailList) || groupSortCrossDetailList.size()==0){
      throw new JyBizException("未查询到该混扫任务下的流向信息！");
    }
    SendFlowExcepStatisticsResp resp =new SendFlowExcepStatisticsResp();
    //查询多个流向下的拦截数据
    List<Integer> endSiteIdList =assembleEndSiteIdList(groupSortCrossDetailList);
    try {
      List<JyComboardAggsEntity> aggsEntityList =jyComboardAggsService.queryComboardAggs(request.getCurrentOperate().getSiteCode(),endSiteIdList);
      if (ObjectHelper.isNotNull(aggsEntityList) && aggsEntityList.size()>0){
        List<ExcepScanDto> excepScanDtoList = assembleExcepScanUnderCTTList(aggsEntityList);
        List<SendFlowDto> sendFlowDtoList =assembleSendFlowList(groupSortCrossDetailList,aggsEntityList);
        resp.setExcepScanDtoList(excepScanDtoList);
        resp.setSendFlowDtoList(sendFlowDtoList);
      }
    } catch (Exception e) {
      log.error("查询混扫任务下 异常统计数据异常");
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,resp);
  }

  private List<SendFlowDto> assembleSendFlowList(List<JyGroupSortCrossDetailEntity> groupSortCrossDetailList, List<JyComboardAggsEntity> aggsEntityList) {
    List<SendFlowDto> sendFlowDtoList =new ArrayList<>();
    for (JyGroupSortCrossDetailEntity sortCrossDetailEntity:groupSortCrossDetailList){
      SendFlowDto sendFlowDto =new SendFlowDto();
      sendFlowDto.setCrossCode(sortCrossDetailEntity.getCrossCode());
      sendFlowDto.setTableTrolleyCode(sortCrossDetailEntity.getTabletrolleyCode());
      sendFlowDto.setEndSiteId(sortCrossDetailEntity.getEndSiteId().intValue());
      sendFlowDto.setEndSiteName(sortCrossDetailEntity.getEndSiteName());
      for (JyComboardAggsEntity aggsEntity:aggsEntityList){
        if (sortCrossDetailEntity.getEndSiteId().intValue() == aggsEntity.getReceiveSiteId()){
          sendFlowDto.setInterceptCount(aggsEntity.getInterceptCount());
          break;
        }
      }
      sendFlowDtoList.add(sendFlowDto);
    }
    return sendFlowDtoList;
  }

  private List<ExcepScanDto> assembleExcepScanUnderCTTList(List<JyComboardAggsEntity> aggsEntityList) {
    List<ExcepScanDto> excepScanDtoList =new ArrayList<>();
    ExcepScanDto excepScanDto =new ExcepScanDto();
    excepScanDto.setType(ExcepScanTypeEnum.INTERCEPTED.getCode());
    excepScanDto.setName(ExcepScanTypeEnum.INTERCEPTED.getName());
    int count =0;
    for (JyComboardAggsEntity entity:aggsEntityList){
      count =count+entity.getInterceptCount();
    }
    excepScanDto.setCount(count);
    excepScanDtoList.add(excepScanDto);
    return excepScanDtoList;
  }

  private List<Integer> assembleEndSiteIdList(List<JyGroupSortCrossDetailEntity> groupSortCrossDetailList) {
    List<Integer> endSiteIdList =new ArrayList<>();
    for (JyGroupSortCrossDetailEntity entity:groupSortCrossDetailList){
      endSiteIdList.add(entity.getEndSiteId().intValue());
    }
    return endSiteIdList;
  }

  private void checkExcepScanUnderCTTParams(SendFlowExcepStatisticsReq request) {
    if (ObjectHelper.isEmpty(request.getTemplateCode())){
      throw new JyBizException("参数错误：缺失混扫任务编号！");
    }
    if (ObjectHelper.isEmpty(request.getType())){
      request.setType(ExcepScanTypeEnum.INTERCEPTED.getCode());
    }
    if (ObjectHelper.isNotNull(request.getType()) && !ExcepScanTypeEnum.INTERCEPTED.getCode().equals(request.getType())){
      throw  new JyBizException("暂不支持该异常类型的查询！");
    }

  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.listPackageOrBoxUnderBoard", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<ComboardDetailResp> listPackageOrBoxUnderBoard(BoardReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBoardCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    ComboardDetailResp resp = new ComboardDetailResp();
    String boardCode = request.getBoardCode();
    List<ComboardDetailDto> comboardDetailDtoList = new ArrayList<>();
    resp.setComboardDetailDtoList(comboardDetailDtoList);
    // 判断是否为运单
    if (request.getBulkFlag()) {
      // 获取运单号
      JyComboardEntity entity = new JyComboardEntity();
      entity.setBoardCode(boardCode);
      entity.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
      String wayBillCode = jyComboardService.queryWayBillCodeByBoardCode(entity);
      if (!StringUtils.isEmpty(wayBillCode)) {
        resp.setBulkFlag(Boolean.TRUE);
        ComboardDetailDto comboardDetailDto = new ComboardDetailDto();
        comboardDetailDto.setType(WAYBILL_TYPE);
        comboardDetailDto.setBarCode(wayBillCode);
        comboardDetailDtoList.add(comboardDetailDto);
      }
    } else {
      Response<BoardBoxCountDto> boardScanInfo = groupBoardManager
          .getBoxCountInfoByBoardCode(boardCode);
      if (boardScanInfo == null || boardScanInfo.getData() == null) {
        log.info("根据板号获取包裹号箱号异常：{}", boardCode);
        return new InvokeResult<>(PACKAGE_OR_BOX_UNDER_BOARD_CODE,
            PACKAGE_OR_BOX_UNDER_BOARD_MESSAGE);
      }
      resp.setBulkFlag(Boolean.FALSE);
      BoardBoxCountDto boardInfoDto = boardScanInfo.getData();
      resp.setBoxCount(boardInfoDto.getBoxCount());
      resp.setPackageCount(boardInfoDto.getPackageCodeCount());
      getComboardDetailDtoList(boardInfoDto.getBarCodeList(), boardInfoDto.getBoxCount(),
          comboardDetailDtoList);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.cancelComboard", mState = {JProEnum.TP, JProEnum.FunctionError})
  @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
  public InvokeResult<Void> cancelComboard(CancelBoardReq request) {
    try {
      if (!checkBaseRequest(request)
              || StringUtils.isEmpty(request.getBoardCode())) {
        return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
      }
      List<ComboardDetailDto> cancelList = request.getCancelList();
      RemoveBoardBoxDto removeBoardBoxDto = new RemoveBoardBoxDto();
      removeBoardBoxDto.setSiteCode(request.getCurrentOperate().getSiteCode());
      removeBoardBoxDto.setOperatorErp(request.getUser().getUserErp());
      removeBoardBoxDto.setOperatorName(request.getUser().getUserName());
      removeBoardBoxDto.setBoardCode(request.getBoardCode());
      BatchUpdateCancelReq batchUpdateCancelReq = new BatchUpdateCancelReq();
      batchUpdateCancelReq.setBoardCode(request.getBoardCode());
      batchUpdateCancelReq.setUpdateUserErp(request.getUser().getUserErp());
      batchUpdateCancelReq.setUpdateUserName(request.getUser().getUserName());
      List<String> barCodeList = new ArrayList<>();
      batchUpdateCancelReq.setBarCodeList(barCodeList);
      removeBoardBoxDto.setBoxCodeList(barCodeList);
      batchUpdateCancelReq.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
      JyBizTaskComboardEntity query = new JyBizTaskComboardEntity();
      query.setBoardCode(request.getBoardCode());
      batchUpdateCancelReq.setCancelFlag(Boolean.TRUE);
      query.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
      JyBizTaskComboardEntity comboardEntity = jyBizTaskComboardService.queryBizTaskByBoardCode(query);

      if (comboardEntity == null) {
        return new InvokeResult<>(RESULT_PARAMETER_ERROR_CODE, "板号:" + request.getBoardCode() +"已被删除！");
      }

      //如果已封车的批次不触发取消组板发货
      if (checkSealTime(comboardEntity) && newsealVehicleService.newCheckSendCodeSealed(comboardEntity.getSendCode(), new StringBuffer())) {
        return new InvokeResult<>(BOARD_HAVE_SEAL_CAR_CODE, BOARD_HAVE_SEAL_CAR_MESSAGE);
      }

      String boardLockKey = String.format(Constants.JY_COMBOARD_BOARD_LOCK_PREFIX, request.getBoardCode());
      if (!jimDbLock.lock(boardLockKey, request.getRequestId(), LOCK_EXPIRE, TimeUnit.SECONDS)) {
        throw new JyBizException("当前系统繁忙,请稍后再试！");
      }

      try {
        if (request.isBulkFlag()) {
          // 获取运单号
          JyComboardEntity entity = new JyComboardEntity();
          entity.setBoardCode(request.getBoardCode());
          entity.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
          String waybillCode = jyComboardService.queryWayBillCodeByBoardCode(entity);
          if (StringUtils.isEmpty(waybillCode)) {
            log.error("运单取消组板失败：{}", waybillCode);
            return new InvokeResult<>(CANCEL_COM_BOARD_CODE, CANCEL_COM_BOARD_MESSAGE);
          }
          removeBoardBoxDto.setWaybillCode(waybillCode);
          barCodeList.add(waybillCode);
          if (!jyComboardService.batchUpdateCancelFlag(batchUpdateCancelReq)) {
            log.error("运单取消组板失败：{}", waybillCode);
            return new InvokeResult<>(CANCEL_COM_BOARD_CODE, CANCEL_COM_BOARD_MESSAGE);
          }

          JyBizTaskComboardEntity jyBizTaskComboardEntity = new JyBizTaskComboardEntity();
          jyBizTaskComboardEntity.setBizId(comboardEntity.getBizId());
          jyBizTaskComboardEntity.setHaveScanCount(Constants.NUMBER_ZERO);
          if (jyBizTaskComboardService.updateBizTaskById(jyBizTaskComboardEntity) < 0) {
            log.error("更新组板任务表失败：{}", JsonHelper.toJson(jyBizTaskComboardEntity));
            throw new JyBizException("更新板任务失败");
          }

          Response removeBoardBoxRes = groupBoardManager.removeBoardBoxByWaybillCode(removeBoardBoxDto);
          if (removeBoardBoxRes == null || removeBoardBoxRes.getCode() != JdCResponse.CODE_SUCCESS) {
            log.error("取消组板操作失败，接口参数：{}，异常返回结果：{}", JsonHelper.toJson(removeBoardBoxDto), JsonHelper.toJson(removeBoardBoxRes));
            throw new JyBizException("取消组板失败");
          }
        } else {
          // 如果为全选
          if (request.isSelectAll()) {
            BoardReq boardReq = new BoardReq();
            boardReq.setBulkFlag(request.isBulkFlag());
            boardReq.setBoardCode(request.getBoardCode());
            boardReq.setGroupCode(request.getGroupCode());
            boardReq.setCurrentOperate(request.getCurrentOperate());
            boardReq.setUser(request.getUser());
            InvokeResult<ComboardDetailResp> boxOrPackCodeList = listPackageOrBoxUnderBoard(boardReq);
            if (boxOrPackCodeList != null
                    && boxOrPackCodeList.getData() != null
                    && !CollectionUtils.isEmpty(boxOrPackCodeList.getData().getComboardDetailDtoList())) {
              for (ComboardDetailDto dto : boxOrPackCodeList.getData().getComboardDetailDtoList()) {
                barCodeList.add(dto.getBarCode());
              }
            }
            // 全部取消，发送延时删除板消息
            pushDelayDeleteBoardMQ(comboardEntity);
          } else {
            // 包裹号或箱号
            for (ComboardDetailDto comboardDetailDto : cancelList) {
              barCodeList.add(comboardDetailDto.getBarCode());
            }
          }
          if (!jyComboardService.batchUpdateCancelFlag(batchUpdateCancelReq)) {
            log.error("按包裹箱取消组板失败：{}", JsonHelper.toJson(batchUpdateCancelReq));
            return new InvokeResult<>(CANCEL_COM_BOARD_CODE, CANCEL_COM_BOARD_MESSAGE);
          }

          JyBizTaskComboardEntity jyBizTaskComboardEntity = new JyBizTaskComboardEntity();
          jyBizTaskComboardEntity.setId(comboardEntity.getId());
          jyBizTaskComboardEntity.setHaveScanCount(comboardEntity.getHaveScanCount() - barCodeList.size());
          if (jyBizTaskComboardService.updateBizTaskById(jyBizTaskComboardEntity) < 0) {
            log.error("更新组板任务表失败：{}", JsonHelper.toJson(jyBizTaskComboardEntity));
            throw new JyBizException("更新板任务失败");
          }

          Response removeBoardBoxRes = groupBoardManager.batchRemoveBardBoxByBoxCodes(removeBoardBoxDto);
          if (removeBoardBoxRes == null || removeBoardBoxRes.getCode() != JdCResponse.CODE_SUCCESS) {
            log.error("取消组板操作失败，接口参数：{}，异常返回结果：{}", JsonHelper.toJson(removeBoardBoxDto), JsonHelper.toJson(removeBoardBoxRes));
            throw new JyBizException("取消组板失败");
          }
        }
        // 异步发送取消组板和发货的全程跟踪
        asyncSendCancelComboardAndSend(request, barCodeList);
      }finally {
        jimDbLock.releaseLock(boardLockKey, request.getRequestId());
      }

    }catch (Exception e ) {
      if (log.isErrorEnabled()) {
        log.error("取消组板异常：{}",JsonHelper.toJson(request),e);
      }
      throw new JyBizException("取消组板异常");
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
  }

  private boolean checkSealTime(JyBizTaskComboardEntity comboardEntity) {
    if (comboardEntity.getSealTime() == null) {
      return false;
    }
    if ( System.currentTimeMillis() - comboardEntity.getSealTime().getTime() >  dmsConfigManager.getPropertyConfig().getReComboardTimeLimit() * 3600L * 1000L) {
      return false;
    }
    return true;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.cancelSortMachineComboard", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<Void> cancelSortMachineComboard(CancelBoardReq request) {
    try {
      List<String> barCodeList = new ArrayList<>();
      List<ComboardDetailDto> cancelList = request.getCancelList();
      // 包裹号或箱号
      for (ComboardDetailDto comboardDetailDto : cancelList) {
        barCodeList.add(comboardDetailDto.getBarCode());
      }

      RemoveBoardBoxDto removeBoardBoxDto = new RemoveBoardBoxDto();
      removeBoardBoxDto.setSiteCode(request.getCurrentOperate().getSiteCode());
      removeBoardBoxDto.setOperatorErp(request.getUser().getUserErp());
      removeBoardBoxDto.setOperatorName(request.getUser().getUserName());
      removeBoardBoxDto.setBoardCode(request.getBoardCode());
      removeBoardBoxDto.setBoxCodeList(barCodeList);

      JyBizTaskComboardEntity query = new JyBizTaskComboardEntity();
      query.setBoardCode(request.getBoardCode());
      query.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
      JyBizTaskComboardEntity comboardEntity = jyBizTaskComboardService.queryBizTaskByBoardCode(query);
      if (comboardEntity == null) {
         //只取消组板，兼容旧的组板逻辑,组板发货岗推广后，可删除
        for (String barCode : barCodeList) {
          BoardBoxRequest cancelBoardBox = new BoardBoxRequest();
          cancelBoardBox.setBoardCode(request.getBoardCode());
          cancelBoardBox.setBoxCode(barCode);
          cancelBoardBox.setSiteCode(request.getCurrentOperate().getSiteCode());
          cancelBoardBox.setOperatorErp(request.getUser().getUserErp());
          cancelBoardBox.setOperatorName(request.getUser().getUserName());
          groupBoardService.removeBoardBox(cancelBoardBox);
        }
      }else{

        //如果已封车的批次不触发取消组板发货
        if (checkSealTime(comboardEntity) && newsealVehicleService.newCheckSendCodeSealed(comboardEntity.getSendCode(), new StringBuffer())) {
          return new InvokeResult<>(BOARD_HAVE_SEAL_CAR_CODE, BOARD_HAVE_SEAL_CAR_MESSAGE);
        }
        BatchUpdateCancelReq batchUpdateCancelReq = new BatchUpdateCancelReq();
        batchUpdateCancelReq.setBoardCode(request.getBoardCode());
        batchUpdateCancelReq.setUpdateUserErp(request.getUser().getUserErp());
        batchUpdateCancelReq.setUpdateUserName(request.getUser().getUserName());
        batchUpdateCancelReq.setBarCodeList(barCodeList);
        batchUpdateCancelReq.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
        batchUpdateCancelReq.setCancelFlag(Boolean.TRUE);
        if (!jyComboardService.batchUpdateCancelFlag(batchUpdateCancelReq)) {
          log.error("按包裹箱取消组板失败：{}", JsonHelper.toJson(batchUpdateCancelReq));
          return new InvokeResult<>(CANCEL_COM_BOARD_CODE, CANCEL_COM_BOARD_MESSAGE);
        }
      }

      Response removeBoardBoxRes = groupBoardManager.batchRemoveBardBoxByBoxCodes(removeBoardBoxDto);
      if (removeBoardBoxRes == null || removeBoardBoxRes.getCode() != JdCResponse.CODE_SUCCESS) {
        log.error("取消组板操作失败，接口参数：{}，异常返回结果：{}", JsonHelper.toJson(removeBoardBoxDto), JsonHelper.toJson(removeBoardBoxRes));
        throw new JyBizException("取消组板失败");
      }


      // 发送取消组板全程跟踪
      OperatorInfo operatorInfo = new OperatorInfo();
      operatorInfo.setSiteCode(request.getCurrentOperate().getSiteCode());
      operatorInfo.setSiteName(request.getCurrentOperate().getSiteName());
      operatorInfo.setUserCode(request.getUser().getUserCode());
      if(request.getCurrentOperate().getOperateTime() == null){
        operatorInfo.setOperateTime(new Date());
      }else{
        operatorInfo.setOperateTime(request.getCurrentOperate().getOperateTime());
      }
      operatorInfo.setOperatorTypeCode(request.getCurrentOperate().getOperatorTypeCode());
      operatorInfo.setOperatorId(request.getCurrentOperate().getOperatorId());
      if (!CollectionUtils.isEmpty(barCodeList)) {
        String barCode = barCodeList.get(0);
        OperatorData operatorData = BeanConverter.convertToOperatorData(request.getCurrentOperate());
        virtualBoardService.sendWaybillTrace(barCode, operatorInfo,operatorData, request.getBoardCode(),
                request.getEndSiteName(), WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION_CANCEL,
                getBizSourceEnum(request).getValue());
        // 取消发货
        if(!cancelSend(request,barCodeList)){
          log.error("取消发货操作失败，接口参数：{}", JsonHelper.toJson(barCodeList));
        }
      }

      // 异步发送取消组板和发货的全程跟踪
      // asyncSendCancelComboardAndSend(request, barCodeList);
    }catch (Exception e ) {
      if (log.isErrorEnabled()) {
        log.error("取消组板异常：{}",JsonHelper.toJson(request),e);
      }
      throw new JyBizException("取消组板异常");
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
  }

  private void asyncSendCancelComboardAndSend(CancelBoardReq request, List<String> barCodeList) {
    CancelComboardSendTaskDto taskDto = new CancelComboardSendTaskDto();
    taskDto.setBoardCode(request.getBoardCode());
    taskDto.setBarCodeList(barCodeList);
    taskDto.setEndSiteName(request.getEndSiteName());
    taskDto.setSiteCode(request.getCurrentOperate().getSiteCode());
    taskDto.setUserErp(request.getUser().getUserErp());
    taskDto.setUserName(request.getUser().getUserName());
    taskDto.setSiteName(request.getCurrentOperate().getSiteName());
    taskDto.setUserCode(request.getUser().getUserCode());
    taskDto.setBizSource(getBizSourceEnum(request));
    if(request.getCurrentOperate() != null) {
    	OperatorData operatorData = BeanConverter.convertToOperatorData(request.getCurrentOperate());
    	taskDto.setOperatorTypeCode(operatorData.getOperatorTypeCode());
    	taskDto.setOperatorId(operatorData.getOperatorId());
    	taskDto.setOperatorData(operatorData);
    }
    try {
      cancelComboardSendProducer.send(request.getBoardCode(), JsonHelper.toJson(taskDto));
      log.info("JyComBoardSendServiceImpl asyncSendCancelComboardAndSend : {}", JsonHelper.toJson(taskDto));
    } catch (Exception e) {
      log.error("JyComBoardSendServiceImpl asyncSendCancelComboardAndSend exception {}", e.getMessage(), e);
      throw new JyBizException("异步发送取消组板取消发货全程跟踪失败");
    }
  }

  private void asyncSendComboardWaybillTrace(CancelBoardReq request, String waybillCode) {
    // 获取运单包裹数
    Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
    if (waybill == null || waybill.getGoodNumber() == null) {
      log.error("[异步取消组板任务]获取运单包裹数失败! {}", waybillCode);
      return;
    }

    int totalNum = waybill.getGoodNumber();
    int onePageSize = dmsConfigManager.getPropertyConfig().getWaybillSplitPageSize() == 0 ? COMBOARD_SPLIT_NUM : dmsConfigManager.getPropertyConfig().getWaybillSplitPageSize();
    int pageTotal = (totalNum % onePageSize) == 0 ? (totalNum / onePageSize) : (totalNum / onePageSize) + 1;
    // 插入分页任务
    CancelComboardTaskDto taskDto = new CancelComboardTaskDto();
    taskDto.setBoardCode(request.getBoardCode());
    taskDto.setWaybillCode(waybillCode);
    taskDto.setEndSiteName(request.getEndSiteName());
    taskDto.setUserErp(request.getUser().getUserErp());
    taskDto.setUserName(request.getUser().getUserName());
    taskDto.setUserCode(request.getUser().getUserCode());
    if(request.getCurrentOperate() != null) {
        taskDto.setSiteName(request.getCurrentOperate().getSiteName());
        taskDto.setSiteCode(request.getCurrentOperate().getSiteCode());
        OperatorData operatorData = BeanConverter.convertToOperatorData(request.getCurrentOperate());
    	taskDto.setOperatorTypeCode(operatorData.getOperatorTypeCode());
    	taskDto.setOperatorId(operatorData.getOperatorId());
    	taskDto.setOperatorData(operatorData);
    }
    for (int i = 0; i < pageTotal; i++) {
      taskDto.setPageNo(i + 1);
      taskDto.setPageSize(onePageSize);
      try {
        waybillCancelComboardProducer.send(waybillCode + "_" + i+1, JsonHelper.toJson(taskDto));
        log.info("JyComBoardSendServiceImpl asyncSendComboardWaybillTrace : {}", JsonHelper.toJson(taskDto));
      } catch (Exception e) {
        log.error("JyComBoardSendServiceImpl asyncSendComboardWaybillTrace exception {}", e.getMessage(), e);
        throw new JyBizException("异步发送全程跟踪失败");
      }
    }
  }

  private boolean cancelSend(CancelBoardReq request, List<String> barCodeList) {
    SendM sendM = toSendM(request);
    sendM.setBoxCode(barCodeList.get(0));
    ThreeDeliveryResponse tDeliveryResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
    if (!ObjectUtils.equals(JdResponse.CODE_OK, tDeliveryResponse.getCode())) {
      return false;
    }
    return true;
  }
  private SendM toSendM(CancelBoardReq request) {
    SendM sendM = new SendM();
    sendM.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
    sendM.setUpdaterUser(request.getUser().getUserName());
    sendM.setSendType(10);
    sendM.setUpdateUserCode(request.getUser().getUserCode());
    Date now = new Date();
    sendM.setOperateTime(now);
    sendM.setUpdateTime(now);
    sendM.setYn(0);
    if(request.getCurrentOperate() != null) {
    	OperatorData operatorData = BeanConverter.convertToOperatorData(request.getCurrentOperate());
    	sendM.setOperatorTypeCode(operatorData.getOperatorTypeCode());
    	sendM.setOperatorId(operatorData.getOperatorId());
    	sendM.setOperatorData(operatorData);
    }
    return sendM;
  }
  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.queryBelongBoardByBarCode", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<QueryBelongBoardResp> queryBelongBoardByBarCode(QueryBelongBoardReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getBarCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    QueryBelongBoardResp resp = new QueryBelongBoardResp();
    try {
      BoardBoxInfoDto boardBoxInfoDto = groupBoardManager.getBoardBoxInfo(request.getBarCode(), request.getCurrentOperate().getSiteCode());
      if (boardBoxInfoDto != null) {
        resp.setBoardCode(boardBoxInfoDto.getCode());
        resp.setEndSiteId(boardBoxInfoDto.getDestinationId());
      } else {
        log.error("未找到对应的板信息：{}", JsonHelper.toJson(request.getBarCode()));
        return new InvokeResult<>(NOT_FIND_BOARD_INFO_CODE, NOT_FIND_BOARD_INFO_MESSAGE);
      }
    } catch (Exception e) {
      log.error("获取板号信息失败：{}", JsonHelper.toJson(request.getBarCode()));
      return new InvokeResult<>(BOARD_INFO_CODE, BOARD_INFO_MESSAGE);
    }
    return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, resp);
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.listPackageDetailUnderSendFlow", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<PackageDetailResp> listPackageDetailUnderSendFlow(SendFlowQueryReq request) {
    checkSendFlowQueryParams(request);
    Pager<JyComboardPackageDetail> query =assembleQuerySendFlowExcepScan(request);
    Pager<ComboardScanedDto> pager =comboardJsfManager.queryInterceptDetail(query);
    PackageDetailResp resp =new PackageDetailResp();
    if (ObjectHelper.isNotNull(pager) && ObjectHelper.isNotNull(pager.getData())){
      List<ComboardScanedDto> comboardScanedDtoList =pager.getData();
      List<PackageScanDto> packageScanDtoList =new ArrayList<>();
      for (ComboardScanedDto comboardScanedDto:comboardScanedDtoList){
        PackageScanDto packageScanDto =new PackageScanDto();
        packageScanDto.setPackageCode(comboardScanedDto.getBarCode());
        packageScanDto.setLabelOptionList(resolveProductTag(comboardScanedDto));
        packageScanDtoList.add(packageScanDto);
      }
      resp.setPackageCodeList(packageScanDtoList);
    }
    return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,resp);
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.deleteCTTGroup", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<String> deleteCTTGroup(DeleteCTTGroupReq request) {
    if (!checkBaseRequest(request) || StringUtils.isEmpty(request.getTemplateCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }

    // 获取混扫任务下的流向信息
    JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
    condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
    condition.setTemplateCode(request.getTemplateCode());
    condition.setGroupCode(request.getGroupCode());
    List<JyGroupSortCrossDetailEntity> entityList = jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(condition);

    if (CollectionUtils.isEmpty(entityList)) {
      throw new JyBizException("任务已删除,请刷新页面！");
    }

    List<Long> ids = new ArrayList<>();
    for (JyGroupSortCrossDetailEntity entity : entityList) {
      ids.add(entity.getId());
    }
    JyCTTGroupUpdateReq updateReq = new JyCTTGroupUpdateReq();
    updateReq.setIds(ids);
    updateReq.setUpdateUserErp(request.getUser().getUserErp());
    updateReq.setUpdateUserName(request.getUser().getUserName());
    updateReq.setUpdateTime(new Date());
    if (!jyGroupSortCrossDetailService.deleteByIds(updateReq)) {
      return new InvokeResult(RESULT_PARAMETER_ERROR_CODE,"删除混扫任务失败！");
    }
    return new InvokeResult(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,request.getTemplateCode());
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.queryGoodsCategoryByBoardCode", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<List<GoodsCategoryDto>> queryGoodsCategoryByBoardCode(BoardReq boardReq) {
    if (StringUtils.isEmpty(boardReq.getBoardCode())) {
      return new InvokeResult<>(RESULT_THIRD_ERROR_CODE, PARAM_ERROR);
    }
    List<GoodsCategoryDto> goodsCategoryList = new ArrayList<>();

    // 获取当前板号的产品类型扫描信息
    List<JyComboardAggsEntity> productTypeList = null;
    try {
      productTypeList = jyComboardAggsService.
              queryComboardAggs(boardReq.getBoardCode(), UnloadProductTypeEnum.values());
    } catch (Exception e) {
      log.info("获取板的详情信息失败：{}", JsonHelper.toJson(boardReq), e);
    }
    getGoodsCategoryList(goodsCategoryList, productTypeList);
    return new InvokeResult<>(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,goodsCategoryList);
  }

  private Pager<JyComboardPackageDetail> assembleQuerySendFlowExcepScan(SendFlowQueryReq request) {
    Pager<JyComboardPackageDetail> pager = new Pager<>();
    JyComboardPackageDetail con =new JyComboardPackageDetail();
    con.setOperateSiteId(request.getCurrentOperate().getSiteCode());
    con.setReceiveSiteId(String.valueOf(request.getEndSiteId()));
    con.setInterceptFlag(Constants.YN_YES);
    pager.setSearchVo(con);
    pager.setPageNo(request.getPageNo());
    pager.setPageSize(request.getPageSize());
    return pager;
  }

  private void checkSendFlowQueryParams(SendFlowQueryReq request) {
    if (ObjectHelper.isEmpty(request.getEndSiteId())){
      throw new JyBizException("参数错误：缺失目的地信息");
    }
    if (ObjectHelper.isEmpty(request.getPageNo())){
      throw new JyBizException("参数错误:缺失页码");
    }
    if (ObjectHelper.isEmpty(request.getPageSize())){
      throw new JyBizException("参数错误:缺失分页大小");
    }
    if (ObjectHelper.isEmpty(request.getType())){
      request.setType(ExcepScanTypeEnum.INTERCEPTED.getCode());
    }
    if (ObjectHelper.isNotNull(request.getType()) && !ExcepScanTypeEnum.INTERCEPTED.getCode().equals(request.getType())){
      throw  new JyBizException("暂不支持该异常类型的查询！");
    }
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.listComboardBySendFlow", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<BoardQueryResp> listComboardBySendFlow(BoardQueryReq request) {
    InvokeResult<BoardQueryResp> invokeResult = new InvokeResult<>();
    if (request == null || request.getEndSiteId() < 0 || request.getCurrentOperate() == null) {
      invokeResult.setCode(NO_SEND_FLOW_CODE);
      invokeResult.setMessage(NO_SEND_FLOW_MESSAGE);
      return invokeResult;
    }
    BoardQueryResp boardQueryResp = new BoardQueryResp();
    List<BoardDto> boardDtos = new ArrayList<>();
    boardQueryResp.setBoardDtoList(boardDtos);
    invokeResult.setData(boardQueryResp);
    boardQueryResp.setBoardLimit(dmsConfigManager.getPropertyConfig().getJyComboardSealBoardListLimit());
    boardQueryResp.setBoardSelectLimit(dmsConfigManager.getPropertyConfig().getJyComboardSealBoardListSelectLimit());

    // 获取当前场地未封车的板号
    SendFlowDto sendFlow = new SendFlowDto();
    sendFlow.setEndSiteId(request.getEndSiteId());
    sendFlow.setStartSiteId(request.getCurrentOperate().getSiteCode());
    Date time = DateHelper.addHoursByDay(new Date(), -Double.valueOf(dmsConfigManager.getPropertyConfig().getJyComboardSealQueryBoardListTime()));
    log.info("组板列表查询创建时间ucc配置：{}", dmsConfigManager.getPropertyConfig().getJyComboardSealQueryBoardListTime());
    sendFlow.setQueryTimeBegin(time);
    List<Integer> comboardSourceList = new ArrayList<>();
    comboardSourceList.add(JyBizTaskComboardSourceEnum.ARTIFICIAL.getCode());
    comboardSourceList.add(JyBizTaskComboardSourceEnum.AUTOMATION.getCode());
    sendFlow.setComboardSourceList(comboardSourceList);
    Page page = PageHelper.startPage(request.getPageNo(), request.getPageSize());
    List<Integer> statusList = new ArrayList<>();
    statusList.add(ComboardStatusEnum.PROCESSING.getCode());
    statusList.add(ComboardStatusEnum.FINISHED.getCode());
    statusList.add(ComboardStatusEnum.CANCEL_SEAL.getCode());
    sendFlow.setStatusList(statusList);
    if (ObjectHelper.isNotNull(request.getGroupCode()) && needIsolateBoardByGroupCode(request.getCurrentOperate())){
      sendFlow.setGroupCode(request.getGroupCode());
    }
    List<JyBizTaskComboardEntity> boardList = jyBizTaskComboardService.listBoardTaskBySendFlow(sendFlow);

    if (com.jd.dbs.util.CollectionUtils.isEmpty(boardList)) {
      invokeResult.setCode(RESULT_SUCCESS_CODE);
      invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
      return invokeResult;
    }

    if (ObjectHelper.isNotNull(page)) {
      boardQueryResp.setBoardTotal(page.getTotal());
    }

    // 获取板号扫描数量统计数据
    List<String> boardCodeList = getboardCodeList(boardList);
    List<JyComboardAggsEntity> boardScanCountList = new ArrayList<>();
    try {
      boardScanCountList = jyComboardAggsService.queryComboardAggs(boardCodeList);
    } catch (Exception e) {
      log.error("获取板号统计信息失败：{}", JsonHelper.toJson(boardCodeList), e);
    }
    HashMap<String, JyComboardAggsEntity> boardScanCountMap = getBoardScanCountMap(boardScanCountList);


    for (JyBizTaskComboardEntity board : boardList) {
      BoardDto boardDto = new BoardDto();
      boardDto.setSendCode(board.getSendCode());
      boardDto.setBoardCode(board.getBoardCode());
      boardDto.setComboardSource(JyBizTaskComboardSourceEnum.getNameByCode(board.getComboardSource()));
      boardDto.setStatus(board.getBoardStatus());
      boardDto.setStatusDesc(ComboardStatusEnum.getStatusDesc(board.getBoardStatus()));
      boardDto.setBoardCreateTime(board.getCreateTime());
      if (ObjectHelper.isNotNull(request.getGroupCode()) && !request.getGroupCode().equals(board.getGroupCode())){
        boardDto.setNotMyGroup(true);
      }

      if (boardScanCountMap.containsKey(board.getBoardCode())) {
        JyComboardAggsEntity aggsEntity = boardScanCountMap.get(board.getBoardCode());
        boardDto.setBoxHaveScanCount(aggsEntity.getBoxScannedCount());
        boardDto.setPackageHaveScanCount(aggsEntity.getPackageScannedCount());
        if (aggsEntity.getWeight() != null) {
          boardDto.setWeight(aggsEntity.getWeight().toString());
        }
        if (aggsEntity.getVolume() != null) {
          boardDto.setVolume(aggsEntity.getVolume().toString());
        }
      }

      boardDtos.add(boardDto);
    }
    invokeResult.setCode(RESULT_SUCCESS_CODE);
    invokeResult.setMessage(RESULT_SUCCESS_MESSAGE);
    return invokeResult;
  }

  private boolean needIsolateBoardByGroupCode(CurrentOperate currentOperate) {
    if (dmsConfigManager.getPropertyConfig().getNeedIsolateBoardByGroupCodeSiteList().contains(String.valueOf(currentOperate.getSiteCode()))){
      return true;
    }
    return false;
  }

  @Override
  @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyComBoardSendServiceImpl.queryScanUser", mState = {JProEnum.TP, JProEnum.FunctionError})
  public InvokeResult<SendFlowDataResp> queryScanUser(SendFlowQueryReq request) {
    SendFlowDataResp resp = new SendFlowDataResp();
    List<User> userList = new ArrayList<>();
    resp.setScanUserList(userList);
    try{
      // 获取当前场地扫描人员信息
      JyComboardEntity userQuery = new JyComboardEntity();
      userQuery.setGroupCode(request.getGroupCode());
      userQuery.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
      Date time = DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -dmsConfigManager.getPropertyConfig().getJyComboardScanUserBeginDay());
      userQuery.setCreateTime(time);
      userList = jyComboardService.queryUserByStartSiteCode(userQuery);
    }catch (Exception e) {
      log.error("获取扫描人员信息失败{}",JsonHelper.toJson(request));
    }
    resp.setScanUserList(userList);
    resp.setTimerInterval(dmsConfigManager.getPropertyConfig().getJyComboardRefreshTimerInterval());
    return new InvokeResult<>(RESULT_SUCCESS_CODE,RESULT_SUCCESS_MESSAGE,resp);
  }

  private HashMap<String, JyComboardAggsEntity> getBoardScanCountMap(List<JyComboardAggsEntity> boardScanCountList) {
    HashMap<String, JyComboardAggsEntity> boardScanCountMap = new HashMap<>();
    for (JyComboardAggsEntity aggsEntity : boardScanCountList) {
      boardScanCountMap.put(aggsEntity.getBoardCode(),aggsEntity);
    }
    return boardScanCountMap;
  }


  private List<String> getboardCodeList(List<JyBizTaskComboardEntity> boardList) {
    List<String> boardCodeList = new ArrayList<>();
    for (JyBizTaskComboardEntity boardInfo : boardList) {
      boardCodeList.add(boardInfo.getBoardCode());
    }
    return boardCodeList;
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
