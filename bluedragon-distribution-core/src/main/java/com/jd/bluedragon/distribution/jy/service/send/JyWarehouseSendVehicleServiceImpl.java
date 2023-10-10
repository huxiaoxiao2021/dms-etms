package com.jd.bluedragon.distribution.jy.service.send;

import com.google.common.collect.Lists;
import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.areadest.AreaDestJsfService;
import com.jd.bd.dms.automatic.sdk.modules.areadest.dto.AreaDestJsfRequest;
import com.jd.bd.dms.automatic.sdk.modules.areadest.dto.AreaDestJsfVo;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.device.enums.DeviceTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JySendFlowConfigEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.enums.FocusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.send.*;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.cross.SortCrossJsfManager;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.busineCode.jqCode.JQCodeService;
import com.jd.bluedragon.distribution.businessCode.dao.BusinessCodeDao;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordCondition;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordDetailCondition;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordDetailPo;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordStatistics;
import com.jd.bluedragon.distribution.collectNew.service.JyScanCollectService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.delivery.constants.SendKeyTypeEnum;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntityQueryDto;
import com.jd.bluedragon.distribution.jy.constants.JyMixScanTaskCompleteEnum;
import com.jd.bluedragon.distribution.jy.constants.JyCollectScanCodeTypeEnum;
import com.jd.bluedragon.distribution.jy.constants.WaybillCustomTypeEnum;
import com.jd.bluedragon.distribution.jy.dao.send.JySendCodeDao;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.send.JySendCancelScanDto;
import com.jd.bluedragon.distribution.jy.dto.send.QueryTaskSendDto;
import com.jd.bluedragon.distribution.jy.dto.send.SendFindDestInfoDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.service.collectNew.strategy.JyScanCollectStrategy;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailQueryEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.common.annotation.CacheMethod;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.erp.util.BeanUtils;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.cross.TableTrolleyJsfDto;
import com.jdl.basic.api.domain.cross.TableTrolleyJsfResp;
import com.jdl.basic.api.domain.cross.TableTrolleyQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.jd.bluedragon.Constants.LONG_ZERO;

@Service("jyWarehouseSendVehicleServiceImpl")
public class JyWarehouseSendVehicleServiceImpl extends JySendVehicleServiceImpl implements JyWarehouseSendVehicleService{

    private static final Logger log = LoggerFactory.getLogger(JyWarehouseSendVehicleServiceImpl.class);

    /**
     * DB不做分页时，查询limit最大数量限制
     */
    public static final Integer DB_LIMIT_DEFAULT_MAX = 100;
    /**
     * DB不做分页时，查询limit默认数量
     */
    public static final Integer DB_LIMIT_DEFAULT = 30;
    /**
     * 混扫任务默认流向数量
     */
    public static final Integer MIX_SCAN_TASK_DEFAULT_FLOW_NUM = 10;

    public static final String OPERATE_SOURCE_PDA = "pdaScan";
    public static final String OPERATE_SOURCE_MQ = "allSelectMqSplit";


    @Value("${jyWarehouseSendTaskPlanTimeBeginDay:1}")
    private Integer jyWarehouseSendTaskPlanTimeBeginDay;
    @Value("${jyWarehouseSendTaskPlanTimeEndDay:2}")
    private Integer jyWarehouseSendTaskPlanTimeEndDay;



    @Autowired
    private SortCrossJsfManager sortCrossJsfManager;
    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;
    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration ;
    @Autowired
    private JyGroupSortCrossDetailService jyGroupSortCrossDetailService;
    @Autowired
    private JdiQueryWSManager jdiQueryWSManager;
    /* 运单查询 */
    @Autowired
    private WaybillCommonService waybillCommonService;
    @Autowired
    private RouterService routerService;
    @Autowired
    private BaseService baseService;
    @Autowired
    private AreaDestJsfService areaDestJsfService;
    @Autowired
    private BoxService boxService;
    @Autowired
    private JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    private JQCodeService jqCodeService;
    @Autowired
    private KvIndexDao kvIndexDao;
    @Autowired
    private JyScanCollectService jyScanCollectService;
    @Autowired
    private JySendCodeDao jySendCodeDao;
    @Autowired
    private BusinessCodeDao businessCodeDao;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private JyScanCollectStrategy jyScanCollectStrategy;
    @Autowired
    @Qualifier("jyCancelScanProducer")
    private DefaultJMQProducer jyCancelScanProducer;
    @Autowired
    private JyWarehouseSendVehicleCacheService jyWarehouseSendVehicleCacheService;
    @Autowired
    private JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendVehicleServiceImpl.fetchSendVehicleTaskPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request) {

        this.beforeQueryParamHandler(request);

        InvokeResult<SendVehicleTaskResponse> invokeResult = super.fetchSendVehicleTask(request);
        if(Objects.isNull(invokeResult.getData())) {
            SendVehicleTaskResponse resData = new SendVehicleTaskResponse();
            invokeResult.setData(resData);
        }

        fillMustField(invokeResult.getData());
        //车辆状态差异化查询
        if(JyBizTaskSendStatusEnum.TO_SEND.getCode().equals(request.getVehicleStatus())) {

            this.setMixScanTaskSiteFlowMaxNum(request, invokeResult);

            this.fillWareHouseFocusField(request, invokeResult);
        }

        return invokeResult;
    }

    private void beforeQueryParamHandler(SendVehicleTaskRequest request) {
        if (ObjectHelper.isNotNull(request.getLastPlanDepartTimeBegin())) {
            request.setLastPlanDepartTimeBegin(request.getLastPlanDepartTimeBegin());
        } else {
            request.setLastPlanDepartTimeBegin(this.defaultTaskPlanTimeBegin());
        }
        if (ObjectHelper.isNotNull(request.getLastPlanDepartTimeEnd())) {
            request.setLastPlanDepartTimeEnd(request.getLastPlanDepartTimeEnd());
        } else {
            request.setLastPlanDepartTimeEnd(this.defaultTaskPlanTimeEnd());
        }
    }

    private Date defaultTaskPlanTimeBegin() {
        return DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -jyWarehouseSendTaskPlanTimeBeginDay);
    }

    private Date defaultTaskPlanTimeEnd() {
        return DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), jyWarehouseSendTaskPlanTimeEndDay);
    }

    private void fillMustField(SendVehicleTaskResponse response) {

        List<VehicleStatusStatis> statusAggList = new ArrayList<>();
        VehicleStatusStatis toSend = new VehicleStatusStatis();
        toSend.setVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
        toSend.setVehicleStatusName(JyBizTaskSendStatusEnum.TO_SEND.getName());
        toSend.setTotal(LONG_ZERO);
        statusAggList.add(toSend);

        VehicleStatusStatis sending = new VehicleStatusStatis();
        sending.setVehicleStatus(JyBizTaskSendStatusEnum.SENDING.getCode());
        sending.setVehicleStatusName(JyBizTaskSendStatusEnum.SENDING.getName());
        sending.setTotal(LONG_ZERO);
        statusAggList.add(sending);

        VehicleStatusStatis toSeal = new VehicleStatusStatis();
        toSeal.setVehicleStatus(JyBizTaskSendStatusEnum.TO_SEAL.getCode());
        toSeal.setVehicleStatusName(JyBizTaskSendStatusEnum.TO_SEAL.getName());
        toSeal.setTotal(LONG_ZERO);
        statusAggList.add(toSeal);

        VehicleStatusStatis sealed = new VehicleStatusStatis();
        sealed.setVehicleStatus(JyBizTaskSendStatusEnum.SEALED.getCode());
        sealed.setVehicleStatusName(JyBizTaskSendStatusEnum.SEALED.getName());
        sealed.setTotal(LONG_ZERO);
        statusAggList.add(sealed);

        if(CollectionUtils.isEmpty(response.getStatusAgg())) {
            response.setStatusAgg(statusAggList);
        }else {
            //返回状态list补全，否则状态更新为0时PDA数据不会更新
            Map<Integer, VehicleStatusStatis> map = new HashMap<>();
            for (VehicleStatusStatis statusAgg : response.getStatusAgg()) {
                map.put(statusAgg.getVehicleStatus(), statusAgg);
            }
            for (VehicleStatusStatis statistics : statusAggList) {
               if(Objects.isNull(map.get(statistics.getVehicleStatus()))) {
                   response.getStatusAgg().add(statistics);
               }
            }
        }
    }


    @Override
    public boolean checkBeforeFetchTask(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> result) {
        if (request.getVehicleStatus() == null) {
            result.parameterError("请选择车辆状态！");
            return false;
        }
        if (!NumberHelper.gt0(request.getPageSize()) || !NumberHelper.gt0(request.getPageNumber())) {
            result.parameterError("缺少分页参数！");
            return false;
        }
        if (request.getCurrentOperate() == null || !NumberHelper.gt0(request.getCurrentOperate().getSiteCode())) {
            result.parameterError("缺少当前场地信息！");
            return false;
        }
        return true;
    }


    /**
     * 混扫任务内配置最大流向数量
     * @param invokeResult
     */
    private void setMixScanTaskSiteFlowMaxNum(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> invokeResult) {

        if(Objects.isNull(invokeResult.getData().getToSendVehicleData())) {
            SendVehicleData<ToSendVehicle> toSendVehicleData  = new SendVehicleData<>();
            invokeResult.getData().setToSendVehicleData(toSendVehicleData);
        }
        invokeResult.getData().getToSendVehicleData().setMixScanTaskSiteFlowMaxNum(this.getFlowMaxBySiteCode(request.getCurrentOperate().getSiteCode()));
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendVehicleServiceImpl.getFlowMaxBySiteCode",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Integer getFlowMaxBySiteCode(Integer siteCode) {
        int mixScanTaskFlowNum = MIX_SCAN_TASK_DEFAULT_FLOW_NUM;
        String mixScanTaskFlowNumConfig = uccPropertyConfiguration.getJyWarehouseSendVehicleMixScanTaskFlowNumConfig();

        String configKey = String.format("%s%s%s", Constants.SEPARATOR_COMMA, siteCode, Constants.SEPARATOR_COLON);
        if(StringUtils.isNotBlank(mixScanTaskFlowNumConfig) && mixScanTaskFlowNumConfig.contains(configKey)) {
            String[] configArr = mixScanTaskFlowNumConfig.split(Constants.SEPARATOR_COMMA);
            for(String conf : configArr) {
                String[] keyValue = conf.split(Constants.SEPARATOR_COLON);
                if(siteCode.toString().equals(keyValue[0]) && StringUtils.isNotBlank(keyValue[1])) {
                    mixScanTaskFlowNum = Integer.valueOf(keyValue[1]);
                    break;
                }
            }
        }
        return mixScanTaskFlowNum;
    }
    /**
     * 填充接货仓发货岗关注字段
     * @param request
     * @param invokeResult
     */
    private void fillWareHouseFocusField(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> invokeResult) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.JyWarehouseSendVehicleServiceImpl.fillWareHouseFocusField", false, true);

        if(!Objects.isNull(invokeResult)
                && !Objects.isNull(invokeResult.getData())
                && !Objects.isNull(invokeResult.getData().getToSendVehicleData())
                && CollectionUtils.isNotEmpty(invokeResult.getData().getToSendVehicleData().getData())) {

            List<ToSendVehicle> toSendVehicleList = invokeResult.getData().getToSendVehicleData().getData();

            Set<Integer> allQueryNextSiteCodeSet = new HashSet<>();
            //派车任务
            for(ToSendVehicle toSendVehicle : toSendVehicleList) {
                String bizId = toSendVehicle.getSendVehicleBizId();
                if(Objects.isNull(toSendVehicle) || CollectionUtils.isEmpty(toSendVehicle.getSendDestList())) {
                    continue;
                }
                //派车任务明细
                for(SendVehicleDetail sendVehicleDetail : toSendVehicle.getSendDestList()) {
                    //派车任务bizId
                    if(StringUtils.isBlank(sendVehicleDetail.getBizId())) {
                        sendVehicleDetail.setBizId(bizId);
                    }
                    allQueryNextSiteCodeSet.add(sendVehicleDetail.getEndSiteId().intValue());
                }
            }

            if(CollectionUtils.isNotEmpty(allQueryNextSiteCodeSet)) {
                TableTrolleyQuery tableTrolleyQuery = new TableTrolleyQuery();
                tableTrolleyQuery.setDmsId(request.getCurrentOperate().getSiteCode());
                List<Integer> allQueryNextSiteCodeList= allQueryNextSiteCodeSet.stream().collect(Collectors.toList());
                tableTrolleyQuery.setSiteCodeList(allQueryNextSiteCodeList);
                JdResult<TableTrolleyJsfResp> tableTrolleyRes = sortCrossJsfManager.queryCrossCodeTableTrolleyBySiteFlowList(tableTrolleyQuery);


                if(!Objects.isNull(tableTrolleyRes) && tableTrolleyRes.isSucceed() && !Objects.isNull(tableTrolleyRes.getData()) && CollectionUtils.isNotEmpty(tableTrolleyRes.getData().getTableTrolleyDtoJsfList())) {
                    Map<Integer, TableTrolleyJsfDto> nextSiteTableTrolleyMap = new HashMap<>();
                    tableTrolleyRes.getData().getTableTrolleyDtoJsfList().forEach(tableTrolleyDto -> {
                        if(!Objects.isNull(tableTrolleyDto.getEndSiteId())) {
                            nextSiteTableTrolleyMap.put(tableTrolleyDto.getEndSiteId(), tableTrolleyDto);
                        }
                    });
                    //发货任务遍历
                    toSendVehicleList.forEach(toSendList -> {
                        if(CollectionUtils.isNotEmpty(toSendList.getSendDestList())) {
                            toSendList.getSendDestList().forEach(detailEntityDto -> {
                                TableTrolleyJsfDto tableTrolleyJsfDto = nextSiteTableTrolleyMap.get(detailEntityDto.getEndSiteId().intValue());
                                if(!Objects.isNull(tableTrolleyJsfDto)) {
                                    detailEntityDto.setCrossCode(tableTrolleyJsfDto.getCrossCode());
                                    detailEntityDto.setTableTrolleyCode(tableTrolleyJsfDto.getTableTrolleyCode());
                                    detailEntityDto.setCrossTableTrolley(String.format("%s-%s", tableTrolleyJsfDto.getCrossCode(), tableTrolleyJsfDto.getTableTrolleyCode()));
                                }else {
                                    detailEntityDto.setCrossTableTrolley(StringUtils.EMPTY);
                                }
                            });
                        }
                    });
                }else {
                    log.error("fillWareHouseFocusField:滑道笼车批量查询为空或者查询失败，request={},滑道笼车查询参数={}，响应={}", JsonHelper.toJson(request), JsonHelper.toJson(tableTrolleyQuery), JsonHelper.toJson(tableTrolleyRes));
                }
            }
        }

        Profiler.registerInfoEnd(info);
    }

    /**
     * 获取滑道笼车号
     * 返回值要么正确，要么null, null由调用方自己处理默认值
     * @return
     */
    public JdResult<TableTrolleyJsfResp> fetchCrossTableTrolley(Integer currentSiteCode, Integer nextSiteCode) {
        String methodDesc = "JyWarehouseSendVehicleServiceImpl.fetchCrossTableTrolley:获取滑道笼车号：";
        try{
            TableTrolleyQuery tableTrolleyQuery = new TableTrolleyQuery();
            tableTrolleyQuery.setDmsId(currentSiteCode);
            tableTrolleyQuery.setSiteCode(nextSiteCode);
            JdResult<TableTrolleyJsfResp>  res = sortCrossJsfManager.queryCrossCodeTableTrolleyBySiteFlow(tableTrolleyQuery);
            if(log.isInfoEnabled()) {
                log.info("{}.currentSiteCode={},nextSiteCode={},res={}", methodDesc, currentSiteCode, nextSiteCode, JsonHelper.toJson(res));
            }
            return res;
        }catch (Exception e) {
            log.error("{}服务异常，errMsg={},接货仓发货岗派车任务主页展示滑道笼车号，不卡实操流程，request={]|{}",
                    methodDesc, e.getMessage(), currentSiteCode, nextSiteCode);
        }
        return null;
    }
    //默认滑道笼车号
    private String unknownCrossTableTrolley(Integer currentSiteCode, Integer nextSiteCode) {
        if(Objects.isNull(currentSiteCode) || Objects.isNull(nextSiteCode)) {
            return "未知滑道笼车号";
        }
        if(log.isInfoEnabled()) {
            log.info("接货仓发货岗查询滑道笼车号逻辑：{}", String.format("%s-%s未知滑道笼车号", currentSiteCode, nextSiteCode));
        }
//        return String.format("%s-%s未知滑道笼车号", currentSiteCode, nextSiteCode);
        return "未知滑道笼车号";
    }

    /**
     * 根据包裹号查询路由下一跳的发货任务
     * 取当前操作机构的下一跳作为发货目的地查询发货流向任务
     *
     * @param result
     * @param queryTaskSendDto
     * @return
     */
    @Override
    public  <T> List<String> resolveSearchKeyword(InvokeResult<T> result, QueryTaskSendDto queryTaskSendDto) {
        if (StringUtils.isBlank(queryTaskSendDto.getKeyword())) {
            return null;
        }

        long startSiteId = queryTaskSendDto.getStartSiteId();
        Long endSiteId = null;

        // 取当前操作网点的路由下一节点
        if (WaybillUtil.isPackageCode(queryTaskSendDto.getKeyword())) {
            endSiteId = getWaybillNextRouter(WaybillUtil.getWaybillCode(queryTaskSendDto.getKeyword()), startSiteId);
        }
//        else if (BusinessUtil.isBoxcode(queryTaskSendDto.getKeyword())
//                && JyComboardLineTypeEnum.TRANSFER.getCode().equals(queryTaskSendDto.getLineType())){
//            endSiteId = getBoxEndSiteId(queryTaskSendDto.getKeyword());
//        }
        else if (BusinessUtil.isSendCode(queryTaskSendDto.getKeyword())) {
            endSiteId = Long.valueOf(BusinessUtil.getReceiveSiteCodeFromSendCode(queryTaskSendDto.getKeyword()));
        }  else {
            //车牌号后四位检索
            if (queryTaskSendDto.getKeyword().length() == VEHICLE_NUMBER_FOUR) {
                List<String> sendVehicleBizList = querySendVehicleBizIdByVehicleFuzzy(queryTaskSendDto);
                if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
                    return sendVehicleBizList;
                }
                result.hintMessage("未检索到相应的发货任务数据！");
            } else if(queryTaskSendDto.getKeyword().contains(Constants.SEPARATOR_HYPHEN)
                    && queryTaskSendDto.getKeyword().split(Constants.SEPARATOR_HYPHEN).length == 2)  {
                List<Long> nextSiteIdList = this.getSiteFlowByCrossCodeTableTrolley(queryTaskSendDto);
                if(CollectionUtils.isEmpty(nextSiteIdList)) {
                    result.hintMessage("根据滑道笼车号未检索到流向！");
                    return null;
                }
                List<String> bizIdList = this.getSendVehicleBizIdList(startSiteId, nextSiteIdList);
                if(CollectionUtils.isEmpty(bizIdList)) {
                    result.hintMessage("根据滑道笼车号未检索到相应的发货任务数据！");
                    return null;
                }
                return bizIdList;

            } else {
                log.info("接货仓发货岗派车任务页按条件查询keyword={},输入错误。请求信息={}", queryTaskSendDto.getKeyword(), JsonHelper.toJson(queryTaskSendDto));
                result.hintMessage("输入位数错误，未检索到发货任务数据！");
            }
            return null;
        }

        if (endSiteId == null) {
            result.hintMessage("运单的路由没有当前场地！");
            return null;
        }

        // 根据路由下一节点查询发货流向的任务
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(startSiteId, endSiteId);
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findBySiteAndStatus(detailQ, null);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(vehicleDetailList)) {
            String msg = String.format("该包裹没有路由下一站[%s]的发货任务！", endSiteId);
            result.hintMessage(msg);
            return null;
        }
        Set<String> sendVehicleBizSet = new HashSet<>();
        for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
            sendVehicleBizSet.add(entity.getSendVehicleBizId());
        }

        return new ArrayList<>(sendVehicleBizSet);
    }

    /**
     * 按照滑道笼车号查询，必须查当前场地
     * @param queryTaskSendDto
     * @return
     */
    private List<Long> getSiteFlowByCrossCodeTableTrolley(QueryTaskSendDto queryTaskSendDto) {
        final String methodDesc = "JyWarehouseSendVehicleServiceImpl.getSiteFlowByCrossCodeTableTrolley根据滑道笼车号查询流向：";
        String[] keyword = queryTaskSendDto.getKeyword().split(Constants.SEPARATOR_HYPHEN);

        TableTrolleyQuery query = new TableTrolleyQuery();
        query.setDmsId(queryTaskSendDto.getStartSiteId().intValue());
        query.setCrossCode(keyword[0]);
        query.setTabletrolleyCode(keyword[1]);
        JdResult<TableTrolleyJsfResp> jsfRes = sortCrossJsfManager.querySiteFlowByCrossCodeTableTrolley(query);

        if(!jsfRes.isSucceed()) {
            log.error("{}服务异常，参数={}，异常返回={}", methodDesc, JsonHelper.toJson(queryTaskSendDto), JsonHelper.toJson(jsfRes));
            throw new JyBizException("根据滑道笼车号查询流向异常");
        }
        if(Objects.isNull(jsfRes.getData()) || CollectionUtils.isEmpty(jsfRes.getData().getTableTrolleyDtoJsfList())) {
            if(log.isInfoEnabled()) {
                log.info("{}查询为空，参数={}，返回={}", methodDesc, JsonHelper.toJson(queryTaskSendDto), JsonHelper.toJson(jsfRes));
            }
            return null;
        }
        List<Long> res = new ArrayList<>();
        for(TableTrolleyJsfDto dto : jsfRes.getData().getTableTrolleyDtoJsfList()) {
            res.add(dto.getEndSiteId().longValue());
        }
        return res;
    }

    /**
     * 根据流向获取派车任务Id list
     * 接货仓发货岗根据滑道笼车号可能查出多个流向，按照流向list查避免查询结果过多，服务端风险规避，limit数量限制   按关键字查询此处暂时不考虑分页
     * @param startSiteId
     * @param nextSiteIdList
     * @return
     */
    private List<String> getSendVehicleBizIdList(long startSiteId, List<Long> nextSiteIdList) {
        Integer pageSize = DB_LIMIT_DEFAULT;
        Integer defaultLimitSize = uccPropertyConfiguration.getJyWarehouseSendVehicleDetailQueryDefaultLimitSize();
        if(!Objects.isNull(defaultLimitSize) && defaultLimitSize > 0 && defaultLimitSize <= DB_LIMIT_DEFAULT_MAX) {
            pageSize = defaultLimitSize;
        }

        JyBizTaskSendVehicleDetailQueryEntity queryEntity = new JyBizTaskSendVehicleDetailQueryEntity();
        queryEntity.setStartSiteId(startSiteId);
        queryEntity.setEndSiteIdList(nextSiteIdList);
        queryEntity.setPageSize(pageSize);
        List<String> bizIds = taskSendVehicleDetailService.findBizIdsBySiteFlows(queryEntity);
        return bizIds;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendVehicleServiceImpl.getMixScanTaskDetailList",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<MixScanTaskDetailRes> getMixScanTaskDetailList(MixScanTaskQueryReq request) {
        InvokeResult<MixScanTaskDetailRes> res = new InvokeResult<>();
        res.success();
        JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
        entity.setGroupCode(request.getGroupCode());
        entity.setTemplateCode(request.getTemplateCode());
        entity.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
        List<JyGroupSortCrossDetailEntity> entityList = jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(entity);
        if(CollectionUtils.isEmpty(entityList)) {
            res.setMessage("查询为空");
            return res;
        }
        List<MixScanTaskDetailDto> sendVehicleDetailDtoList = new ArrayList<>();
        entityList.forEach(en -> {
            MixScanTaskDetailDto dto = new MixScanTaskDetailDto();
            dto.setStartSiteId(en.getStartSiteId());
            dto.setStartSiteName(en.getStartSiteName());
            dto.setCrossCode(en.getCrossCode());
            dto.setTabletrolleyCode(en.getTabletrolleyCode());
            dto.setTemplateName(en.getTemplateName());
            dto.setTemplateCode(en.getTemplateCode());
            dto.setEndSiteId(en.getEndSiteId());
            dto.setEndSiteName(en.getEndSiteName());
            dto.setSendVehicleDetailBizId(en.getSendVehicleDetailBizId());
            dto.setFocus(en.getFocus());
            sendVehicleDetailDtoList.add(dto);
        });
        MixScanTaskDetailRes resData = new MixScanTaskDetailRes();
        resData.setMixScanTaskDetailDtoList(sendVehicleDetailDtoList);
        res.setData(resData);
        return res;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendVehicleServiceImpl.fetchToSendAndSendingTaskPage",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<AppendSendVehicleTaskQueryRes> fetchToSendAndSendingTaskPage(AppendSendVehicleTaskQueryReq request) {
        InvokeResult<AppendSendVehicleTaskQueryRes> result = new InvokeResult<>();
        try {
            QueryTaskSendDto queryTaskSendDto = new QueryTaskSendDto();
            queryTaskSendDto.setKeyword(request.getKeyword());
            queryTaskSendDto.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
            //条件查询
            List<String> sendVehicleBizList = resolveSearchKeyword(result, queryTaskSendDto);
            if (!result.codeSuccess()) {
                return result;
            }
            List<JyBizTaskSendVehicleEntity> vehiclePageList = getToSendAndSendingSendVehiclePage(request, sendVehicleBizList);

            if (CollectionUtils.isEmpty(vehiclePageList)) {
                result.setMessage("查询数据为空");
                return result;
            }
            List<SendVehicleDto> sendVehicleDtoList = new ArrayList<>();
            AppendSendVehicleTaskQueryRes resData = new AppendSendVehicleTaskQueryRes();
            resData.setSendVehicleDtoList(sendVehicleDtoList);
            result.setData(resData);

            CallerInfo info0 = Profiler.registerInfo("DMSWEB.JyWarehouseSendVehicleServiceImpl.fetchToSendAndSendingTaskPage.0", false, true);

           Set<Integer> allQueryNextSiteCodeSet = new HashSet<>();

            vehiclePageList.forEach(entity -> {
                SendVehicleDto dto = new SendVehicleDto();
                dto.setSendVehicleBizId(entity.getBizId());
                dto.setVehicleStatus(entity.getVehicleStatus());
                //获取车牌号
                if(!Objects.isNull(entity.getManualCreatedFlag()) && entity.getManualCreatedFlag().equals(1)) {
                    dto.setManualCreatedFlag(true);
                    dto.setBizNo(entity.getBizNo());
                    dto.setTaskName(entity.getTaskName());
                }else {
                    dto.setManualCreatedFlag(false);
                    dto.setVehicleNumber(this.getVehicleNumber(entity));
                }
                //获取明细
                dto.setSendVehicleDetailDtoList(this.getSendVehicleDetailDto(entity, request, allQueryNextSiteCodeSet));
                sendVehicleDtoList.add(dto);
            });

            this.fillFieldCrossTableTrolley(request, sendVehicleDtoList, allQueryNextSiteCodeSet);

            //最大流向配置
            resData.setMixScanTaskSiteFlowMaxNum(this.getFlowMaxBySiteCode(request.getCurrentOperate().getSiteCode()));

            //已添加流向配置
            JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
            entity.setGroupCode(request.getGroupCode());
            entity.setTemplateCode(request.getMixScanTaskCode());
            entity.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
            List<JyGroupSortCrossDetailEntity> entityList = jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(entity);
            resData.setMixScanTaskSiteFlowNum(CollectionUtils.isEmpty(entityList) ? 0 : entityList.size());

            Profiler.registerInfoEnd(info0);

        } catch (Exception e) {
            log.error("查询发车任务异常. {}", JsonHelper.toJson(request), e);
            result.error("查询发车任务异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    private void fillFieldCrossTableTrolley(AppendSendVehicleTaskQueryReq request, List<SendVehicleDto> sendVehicleDtoList, Set<Integer> allQueryNextSiteCodeSet) {

        TableTrolleyQuery tableTrolleyQuery = new TableTrolleyQuery();
        tableTrolleyQuery.setDmsId(request.getCurrentOperate().getSiteCode());
        List<Integer> allQueryNextSiteCodeList= allQueryNextSiteCodeSet.stream().collect(Collectors.toList());
        tableTrolleyQuery.setSiteCodeList(allQueryNextSiteCodeList);
        JdResult<TableTrolleyJsfResp> tableTrolleyRes = sortCrossJsfManager.queryCrossCodeTableTrolleyBySiteFlowList(tableTrolleyQuery);

        if(!Objects.isNull(tableTrolleyRes) && tableTrolleyRes.isSucceed() && !Objects.isNull(tableTrolleyRes.getData()) && CollectionUtils.isNotEmpty(tableTrolleyRes.getData().getTableTrolleyDtoJsfList())) {
            Map<Integer, TableTrolleyJsfDto> nextSiteTableTrolleyMap = new HashMap<>();
            tableTrolleyRes.getData().getTableTrolleyDtoJsfList().forEach(tableTrolleyDto -> {
                if(!Objects.isNull(tableTrolleyDto.getEndSiteId())) {
                    nextSiteTableTrolleyMap.put(tableTrolleyDto.getEndSiteId(), tableTrolleyDto);
                }
            });
            //遍历发货任务下的明细任务，组装滑道笼车号
            sendVehicleDtoList.forEach(sendVehicleEntityList -> {
                if(CollectionUtils.isNotEmpty(sendVehicleEntityList.getSendVehicleDetailDtoList())) {
                    sendVehicleEntityList.getSendVehicleDetailDtoList().forEach(detailEntityDto -> {
                        TableTrolleyJsfDto tableTrolleyJsfDto = nextSiteTableTrolleyMap.get(detailEntityDto.getEndSiteId().intValue());
                        if(!Objects.isNull(tableTrolleyJsfDto)) {
                            String crossCode = tableTrolleyJsfDto.getCrossCode();
                            String tableTrolleyCode = tableTrolleyJsfDto.getTableTrolleyCode();
                            detailEntityDto.setCrossCode(crossCode);
                            detailEntityDto.setTableTrolleyCode(tableTrolleyCode);
                            detailEntityDto.setCrossTableTrolley(String.format("%s-%s", crossCode, tableTrolleyCode));
                        }else {
//                            String crossTableTrolley = unknownCrossTableTrolley(request.getCurrentOperate().getSiteCode(), detailEntityDto.getEndSiteId().intValue());
//                            if(log.isInfoEnabled()) {
//                                log.info("fillFieldCrossTableTrolley:滑道笼车查询为空或者查询失败，设置默认滑道笼车号={}，request={}", crossTableTrolley, JsonHelper.toJson(request));
//                            }
                            detailEntityDto.setCrossTableTrolley(StringUtils.EMPTY);
                        }
                    });
                }
            });
        }else {
            log.error("fillWareHouseFocusField:滑道笼车批量查询为空或者查询失败，request={},滑道笼车查询参数={}，响应={}", JsonHelper.toJson(request), JsonHelper.toJson(tableTrolleyQuery), JsonHelper.toJson(tableTrolleyRes));
        }
    }


    private List<SendVehicleDetailDto> getSendVehicleDetailDto(JyBizTaskSendVehicleEntity entity,
                                                               AppendSendVehicleTaskQueryReq request,
                                                               Set<Integer> allQueryNextSiteCodeSet) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.JyWarehouseSendVehicleServiceImpl.getSendVehicleDetailDto", false, true);

        // 根据路由下一节点查询发货流向的任务
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(entity.getStartSiteId(), entity.getBizId());

        List<Integer> vehicleStatuses = Arrays.asList(JyBizTaskSendStatusEnum.TO_SEND.getCode(), JyBizTaskSendStatusEnum.SENDING.getCode());

        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findBySiteAndStatus(detailQ, vehicleStatuses);
        List<SendVehicleDetailDto> res = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(vehicleDetailList)) {
            vehicleDetailList.forEach(detailEntity -> {
                SendVehicleDetailDto dto = new SendVehicleDetailDto();
                dto.setBizId(entity.getBizId());
                dto.setSendDetailBizId(detailEntity.getBizId());
                dto.setEndSiteId(detailEntity.getEndSiteId());
                dto.setEndSiteName(detailEntity.getEndSiteName());
                dto.setItemStatus(detailEntity.getVehicleStatus());
                dto.setItemStatusDesc(JyBizTaskSendStatusEnum.getNameByCode(detailEntity.getVehicleStatus()));
                allQueryNextSiteCodeSet.add(detailEntity.getEndSiteId().intValue());
                //是否已添加到混扫任务中
                dto.setMixScanTaskProcess(this.isMixScanProcess(detailEntity, request));
                res.add(dto);
            });
        }
        Profiler.registerInfoEnd(info);
        return res;
    }

    /**
     * 是否混扫任务进行中
     * @param entity
     * @param request
     * @return
     */
    private Boolean isMixScanProcess(JyBizTaskSendVehicleDetailEntity entity, AppendSendVehicleTaskQueryReq request) {
        JyGroupSortCrossDetailEntity jyGroupSortCrossDetailEntity = new JyGroupSortCrossDetailEntity();
        jyGroupSortCrossDetailEntity.setGroupCode(request.getGroupCode());
        jyGroupSortCrossDetailEntity.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
        jyGroupSortCrossDetailEntity.setTemplateCode(request.getMixScanTaskCode());
        jyGroupSortCrossDetailEntity.setSendVehicleDetailBizId(entity.getBizId());
        return jyGroupSortCrossDetailService.isMixScanProcess(jyGroupSortCrossDetailEntity);

    }
    

    private List<JyBizTaskSendVehicleEntity> getToSendAndSendingSendVehiclePage(AppendSendVehicleTaskQueryReq request, List<String> sendVehicleBizList){
        JyBizTaskSendVehicleEntity queryEntity = new JyBizTaskSendVehicleEntity();
        queryEntity.setStartSiteId((long) request.getCurrentOperate().getSiteCode());

        List<Integer> vehicleStatuses = Arrays.asList(JyBizTaskSendStatusEnum.TO_SEND.getCode(),
                JyBizTaskSendStatusEnum.SENDING.getCode());

        //设置默认预计发货时间查询范围
        try {
            if (ObjectHelper.isNotNull(request.getLastPlanDepartTimeBegin())) {
                queryEntity.setLastPlanDepartTimeBegin(request.getLastPlanDepartTimeBegin());
            } else {
                queryEntity.setLastPlanDepartTimeBegin(this.defaultTaskPlanTimeBegin());
            }
            if (ObjectHelper.isNotNull(request.getLastPlanDepartTimeEnd())) {
                queryEntity.setLastPlanDepartTimeEnd(request.getLastPlanDepartTimeEnd());
            } else {
                queryEntity.setLastPlanDepartTimeEnd(this.defaultTaskPlanTimeEnd());
            }
            queryEntity.setCreateTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -uccPropertyConfiguration.getJySendTaskCreateTimeBeginDay()));

        } catch (Exception e) {
            log.error("查询发货任务设置默认查询条件异常，入参{}", JsonHelper.toJson(request), e.getMessage(), e);
        }


         return taskSendVehicleService.querySendTaskOfPage(
                queryEntity, sendVehicleBizList, null, request.getPageNo(), request.getPageSize(), vehicleStatuses);
    }


    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseSendVehicleServiceImpl.scan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<SendScanRes> scan(SendScanReq request, JdVerifyResponse<SendScanRes> response) {
        //发货前执行逻辑
        warehouseSendScanBeforeHandler(request, response);
        if(!response.codeSuccess()) {
            return response;
        }
        SendScanRequest sendScanRequest = new SendScanRequest();
        BeanUtils.copyProperties(request, sendScanRequest);
        JdVerifyResponse<SendScanResponse> scanRes = super.sendScan(sendScanRequest);
        if(log.isInfoEnabled()) {
            log.info("JyWarehouseSendVehicleServiceImpl.sendScan，req={},res={}", JsonHelper.toJson(request), JsonHelper.toJson(scanRes));
        }

        response.setCode(scanRes.getCode());
        response.setMessage(scanRes.getMessage());
        response.setMsgBoxes(scanRes.getMsgBoxes());
        SendScanRes resData = new SendScanRes();
        if(!Objects.isNull(scanRes.getData())) {
            BeanUtils.copyProperties(scanRes.getData(), resData);
            //调用发货后接货仓发货岗单独逻辑处理
        }
        warehouseSendScanResponseHandler(request, response, resData);
        response.setData(resData);
        return response;
    }

    @Override
    public SendFindDestInfoDto matchSendDest(SendScanRequest request, SendKeyTypeEnum sendType,
                                              JyBizTaskSendVehicleEntity taskSend, Set<Long> allDestId, JdVerifyResponse<SendScanResponse> response) {
        SendFindDestInfoDto sendFindDestInfoDto = new SendFindDestInfoDto();

        sendFindDestInfoDto.setRouterNextSiteId(request.getPreNextSiteCode());
        if (allDestId.contains(request.getPreNextSiteCode())) {
            sendFindDestInfoDto.setMatchSendDestId(request.getPreNextSiteCode());
        }
        return sendFindDestInfoDto;
    }

    /**
     * 接货仓发货岗发货前逻辑
     * @param request
     * @param response
     */
    private void warehouseSendScanBeforeHandler(SendScanReq request, JdVerifyResponse<SendScanRes> response) {
        //混扫任务状态校验
        if(mixScanTaskStatusComplete(request)) {
            response.toBizError();
            response.addInterceptBox(0, "当前混扫任务已经结束，请重新选择混扫任务");
            return ;
        }

        if(!Objects.isNull(request.getUnfocusedFlowForceSend()) && request.getUnfocusedFlowForceSend()) {
            //强发不走龙门架匹配，直接指定任务发货
            if (log.isInfoEnabled()) {
                log.info("warehouseSendScanBeforeHandler:接货仓发货岗强发时指定发货任务信息,req={}", JsonHelper.toJson(request));
            }
            if(StringUtils.isBlank(request.getTaskId())) {
                request.setTaskId(super.getJyScheduleTaskId(request.getSendVehicleBizId()));
            }

            if(StringUtils.isBlank(request.getSendVehicleDetailBizId())) {
                throw new JyBizException("强发任务为空");
            }
            //避免页面停留过久强发任务失效
            JyBizTaskSendVehicleDetailEntity entity = jyBizTaskSendVehicleDetailService.findByBizId(request.getSendVehicleDetailBizId());
            if(Objects.isNull(entity) || Objects.isNull(entity.getEndSiteId())) {
                throw new JyBizException("强发任务不存在");
            }
            if(Objects.isNull(request.getPreNextSiteCode())) {
                request.setPreNextSiteCode(entity.getEndSiteId());
            }
        }else {
            //扫描单据关联派车任务查询
            fetchBarCodeBindSendVehicleTask(request, response);
        }

    }


    /**
     * 接货仓发货岗发货前校验逻辑
     * @param request
     * @return true: 已完成
     */
    private boolean mixScanTaskStatusComplete(SendScanReq request) {
        JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();
        queryDto.setGroupCode(request.getGroupCode());
        queryDto.setTemplateCode(request.getMixScanTaskCode());
        queryDto.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
        queryDto.setFuncType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
        queryDto.setCompleteStatus(JyMixScanTaskCompleteEnum.COMPLETE.getCode());
        return jyGroupSortCrossDetailService.mixScanTaskStatusComplete(queryDto);
    }

    /**
     * 1、走龙门架配置获取可能下游流向
     * 2、根据流向匹配混扫任务中已添加流向
     * 3、如果匹配到混扫任务且关注，request中填充任务信息
     * 4、强发校验（1）未匹配到混扫任务、（2）匹配到混扫任务未关注
     * @param request
     * @param response
     */
    private void fetchBarCodeBindSendVehicleTask(SendScanReq request, JdVerifyResponse<SendScanRes> response) {
        String methodDesc = "JyWarehouseSendVehicleServiceImpl.fetchBarCodeBindSendVehicleTask:接货仓发货岗扫描前查找发货流向及混扫任务信息：";
        CallerInfo info = Profiler.registerInfo("DMSWEB.jsf.JyWarehouseSendVehicleServiceImpl.fetchBarCodeBindSendVehicleTask", Constants.UMP_APP_NAME_DMSWEB,false, true);
        //根据发货方式获取流向
        InvokeResult<List<Integer>> nextSiteCodeRes = this.fetchNextSiteId(request);
        if(!nextSiteCodeRes.codeSuccess()) {
            log.warn("接货仓发货岗查询扫描下一流向失败：request={}，流向res={}", JsonHelper.toJson(request), JsonHelper.toJson(nextSiteCodeRes));
            String customMsg = StringUtils.isBlank(nextSiteCodeRes.getMessage()) ? "获取扫描下一流向失败" : nextSiteCodeRes.getMessage();
            response.setCode(SendScanRes.DEFAULT_FAIL);
            response.setMessage(customMsg);
            return;
        }
        if(log.isInfoEnabled()) {
            log.info("{}，当前扫描单号可能流向为{}，request={}", methodDesc, JsonHelper.toJson(nextSiteCodeRes), JsonHelper.toJson(request));
        }
        List<Long> endSiteIdList = new ArrayList<>();
        nextSiteCodeRes.getData().forEach(siteId ->{
            endSiteIdList.add((long)siteId);
        });
        //这是一行测试代码
//        endSiteIdList.add(910l); endSiteIdList.add(10186l);
        //根据发货流向匹配混扫任务明细，获取派车任务信息
        JyGroupSortCrossDetailEntityQueryDto entityQueryParam = new JyGroupSortCrossDetailEntityQueryDto();
        entityQueryParam.setGroupCode(request.getGroupCode());
        entityQueryParam.setTemplateCode(request.getMixScanTaskCode());
        entityQueryParam.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
        entityQueryParam.setEndSiteIdList(endSiteIdList);
        entityQueryParam.setFuncType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
        List<JyGroupSortCrossDetailEntity> resEntityList = jyGroupSortCrossDetailService.selectByCondition(entityQueryParam);
        if(CollectionUtils.isEmpty(resEntityList)) {
            log.warn("接货仓发货岗该单据{}匹配龙门架流向为{}，未匹配到混扫任务,request={},派车信息={}", request.getBarCode(), JsonHelper.toJson(endSiteIdList),
                    JsonHelper.toJson(request), JsonHelper.toJson(resEntityList));
            //
            String getNextSiteName = this.getImpossibleNextSiteName(request, endSiteIdList);
            response.setCode(SendScanRes.CODE_NULL_FLOW_FORCE_SEND);
            response.setMessage(String.format(SendScanRes.MSG_NULL_FLOW_FORCE_SEND, getNextSiteName));
            return;
        }
        JyGroupSortCrossDetailEntity jyGroupSortCrossDetailEntity = this.getDetailSendVehicleByReceiveSiteCodes(resEntityList, endSiteIdList);
        if(log.isInfoEnabled()) {
            log.info("接货仓发货岗按流向{}匹配混扫任务明细,request={},混扫明细={}", JsonHelper.toJson(endSiteIdList),
                    JsonHelper.toJson(request), JsonHelper.toJson(jyGroupSortCrossDetailEntity));
        }
        request.setSendVehicleDetailBizId(jyGroupSortCrossDetailEntity.getSendVehicleDetailBizId());
        JyBizTaskSendVehicleDetailEntity entity = jyBizTaskSendVehicleDetailService.findByBizId(jyGroupSortCrossDetailEntity.getSendVehicleDetailBizId());
        if(Objects.isNull(entity)) {
            //正常场景不会有异常，异常数据可能会存在NPE，比如自建任务绑定后自建任务无效，但是混扫任务没有删除该无效数据，解决办法，找到这个任务，在PDA上删除
            response.setCode(SendScanRes.DEFAULT_FAIL);
            response.setMessage("混扫任务中添加的流向任务错误");
            return;
        }
        request.setSendVehicleBizId(entity.getSendVehicleBizId());
        request.setPreNextSiteCode(entity.getEndSiteId());
        request.setTaskId(super.getJyScheduleTaskId(entity.getSendVehicleBizId()));
        //确定是否强发
        if(Objects.isNull(request.getUnfocusedFlowForceSend()) || !request.getUnfocusedFlowForceSend()) {
            if(!Objects.isNull(jyGroupSortCrossDetailEntity.getFocus()) && FocusEnum.FOCUS.getCode() != jyGroupSortCrossDetailEntity.getFocus()) {
                SendScanRes resData = response.getData();
                resData.setUnfocusedDetailBizId(jyGroupSortCrossDetailEntity.getSendVehicleDetailBizId());
                resData.setUnfocusedBizId(entity.getSendVehicleBizId());
                resData.setUnfocusedNextSiteCode(entity.getEndSiteId());
                resData.setUnfocusedNextSiteName(entity.getEndSiteName());
                response.setCode(SendScanRes.CODE_UNFOCUSED_FLOW_FORCE_SEND);
                response.setMessage(String.format(SendScanRes.MSG_UNFOCUSED_FLOW_FORCE_SEND, jyGroupSortCrossDetailEntity.getEndSiteName()));
                return;
            }
        }

        Profiler.registerInfoEnd(info);
    }

    private JyGroupSortCrossDetailEntity getDetailSendVehicleByReceiveSiteCodes(List<JyGroupSortCrossDetailEntity> detailEntityList, List<Long> endSiteIdList) {
        if(detailEntityList.size() == 1) {
            return detailEntityList.get(0);
        }

        // K-流向 V-混扫任务中该流向发货任务    根据龙门架流向list和混扫任务list匹配结果可能多个，按照龙门架发货流向取前面的
        Map<Long, JyGroupSortCrossDetailEntity> nextSiteCodeMap = new HashMap<>();
        for(JyGroupSortCrossDetailEntity entity : detailEntityList) {
            nextSiteCodeMap.put(entity.getEndSiteId(), entity);
        }

        for(Long receiveSiteCode : endSiteIdList) {
            if(!Objects.isNull(nextSiteCodeMap.get(receiveSiteCode))) {
                return nextSiteCodeMap.get(receiveSiteCode);
            }
        }
        //上游detailEntityList 是根据endSiteIdList匹配出来的，一定会if成功返回
        return null;
    }

    /**
     * 根据发货规则匹配可能的流向，获取可能的流向场地名称
     * @param endSiteIdList
     * @return
     */
    private String getImpossibleNextSiteName(SendScanReq request, List<Long> endSiteIdList) {
        if(StringUtils.isBlank(request.getBarCode()) || CollectionUtils.isEmpty(endSiteIdList)) {
            return "";
        }

        if (BusinessUtil.isBoxcode(request.getBarCode())) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(endSiteIdList.get(0).intValue());
            return baseStaffSiteOrgDto.getSiteName();
        }else if (WaybillUtil.isPackageCode(request.getBarCode()) || WaybillUtil.isWaybillCode(request.getBarCode())) {
            BaseStaffSiteOrgDto baseStaffSiteOrgDto;
            //按单按包裹扫描，流向get(0)是运单的预分拣站点，其他的是发货规则配置的可能分拣中心流向
            if(endSiteIdList.size() == 1) {
                baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(endSiteIdList.get(0).intValue());
            }else {
                baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(endSiteIdList.get(1).intValue());
            }
            return baseStaffSiteOrgDto.getSiteName();
        }else {
            return "";
        }
    }


    /**
     * 查询下一流向
     * 按箱查询时，返回是箱号下一流向
     * 按包裹或运单查询时，返回get(0)是预分拣站点，其他为流向
     * @param request
     * @return
     */
    @Override
    public InvokeResult<List<Integer>> fetchNextSiteId(SendScanReq request) {

        if (BusinessUtil.isBoxcode(request.getBarCode())) {
            return fetchNextSiteIdByBox(request);
        }else if (WaybillUtil.isPackageCode(request.getBarCode()) || WaybillUtil.isWaybillCode(request.getBarCode())) {
            return fetchNextSiteIdByWaybillOrPackage(request);
        }else {
            return new InvokeResult<>(InvokeResult.RESULT_PARAMETER_ERROR_CODE, "只支持扫描包裹、运单和箱号");
        }
    }

    @Override
    public InvokeResult<BuQiWaybillRes> findByQiWaybillPage(BuQiWaybillReq request) {
        String methodDesc = "JyWarehouseSendVehicleServiceImpl.findByQiWaybillPage:接货仓查询不齐运单(B网)信息：";

        InvokeResult<BuQiWaybillRes> res = new InvokeResult<>();
        res.success();

        String sendVehicleBizId = request.getSendVehicleBizId();

        Integer siteId = request.getCurrentOperate().getSiteCode();
        List<String> sendCodes = jySendCodeDao.querySendCodesByVehicleBizId(sendVehicleBizId);
        if(CollectionUtils.isEmpty(sendCodes)) {
            res.parameterError("未查到该任务发货批次数据");
            return res;
        }
        List<String> collectionCodeList = this.getTaskAllCollectionCode(siteId, sendCodes);
        if(CollectionUtils.isEmpty(collectionCodeList)) {
            log.warn("{}根据condition没有查到集齐collectionCode信息，req={}", methodDesc, JsonHelper.toJson(request));
            res.setMessage("未查到该任务扫描信息");
            return res;
        }
        if(log.isInfoEnabled()) {
            log.info("{}获取场地{}任务{}下所有批次{}对应的collectionCode={}",
                    methodDesc, siteId, sendVehicleBizId, JsonHelper.toJson(sendCodes), JsonHelper.toJson(collectionCodeList));
        }

        JyCollectRecordCondition jqQueryParam = new JyCollectRecordCondition();
        jqQueryParam.setCollectionCodeList(collectionCodeList);
        jqQueryParam.setSiteId(siteId.longValue());
        jqQueryParam.setCustomType(WaybillCustomTypeEnum.TO_B.getCode());//B网
        jqQueryParam.setPageSize(request.getPageSize());
        int offset = (request.getPageNo() - 1) * request.getPageSize();
        jqQueryParam.setOffset(offset);

        if(log.isInfoEnabled()) {
            log.info("{}不齐运单查询参数={}", methodDesc, JsonHelper.toJson(jqQueryParam));
        }
        List<JyCollectRecordStatistics> collectionRecordPoList = jyScanCollectService.findBuQiWaybillByCollectionCodes(jqQueryParam);
        if(CollectionUtils.isEmpty(collectionRecordPoList)) {
            res.setMessage("未查到任务下不齐数据");
            return res;
        }

        BuQiWaybillRes resData = new BuQiWaybillRes();
        resData.setSendVehicleBizId(sendVehicleBizId);
//        resData.setBuQiWaybillTotalSum(collectionRecordPoList.size());
        List<BuQiWaybillDto> buQiWaybillDtoList = new ArrayList<>();
        collectionRecordPoList.forEach(collectionRecordPo -> {
            BuQiWaybillDto buQiWaybillDto = new BuQiWaybillDto();
            buQiWaybillDto.setWaybillCode(collectionRecordPo.getAggCode());
            buQiWaybillDto.setTotalNum(collectionRecordPo.getShouldCollectTotalNum());
            buQiWaybillDto.setScanNum(collectionRecordPo.getRealCollectTotalNum());
            buQiWaybillDtoList.add(buQiWaybillDto);
        });
        resData.setBuQiWaybillDtoList(buQiWaybillDtoList);
        res.setData(resData);
        return res;
    }


    /**
     * 获取任务下可能的所有collectionCode
     * @param siteCode
     * @param sendCodes
     * @return
     */
    private List<String> getTaskAllCollectionCode(Integer siteCode, List<String> sendCodes) {
        //当前场地直接
        Set<String> collectionCodeNewList = new HashSet<>();
        //跨流向任务迁移时会生成新批次作为当前批次，原批次删除，集齐扫描关系不变还在原批次上，需要找到当前批次被跨流向迁移之前的批次
        Set<String> collectionCodeOldList = new HashSet<>();

        for(String sendCode : sendCodes) {
            String jqCondition = jqCodeService.getJyScanSendCodeCollectionCondition(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION, sendCode);
            String collectionCode = kvIndexDao.queryRecentOneByKeyword(jqCondition);
            if(StringUtils.isBlank(collectionCode)) {
                log.warn("接货仓发货岗根据批次号condition查collectionCode为空，场地={}，jqCondition={}", siteCode, jqCondition);
                continue;
            }
            collectionCodeNewList.add(collectionCode);
            //todo 下面几行代码未来会使用，本期暂时不执行：：当处理错流向任务迁移时，会删除原批次，转移生成新批次，需要在新批次上维护原批次信息。保证新批次查询时能关联到已经删除的批次下的操作数据
//            List<String> relationCollectionCodeList = businessCodeDao.findAttributeValueByCodeAndPossibleKey(collectionCode, JQCodeServiceImpl.ATTRIBUTE_COLLECTION_CODE);
//            if(CollectionUtils.isNotEmpty(relationCollectionCodeList)) {
//                collectionCodeOldList.addAll(relationCollectionCodeList);
//            }
        }
        collectionCodeNewList.addAll(collectionCodeOldList);
        return collectionCodeNewList.stream().collect(Collectors.toList());
    }

    @Override
    public InvokeResult<BuQiPackageRes> findByQiPackagePage(BuQiWaybillReq request) {
        String methodDesc = "JyWarehouseSendVehicleServiceImpl.findByQiPackagePage:接货仓查询不齐运单(B网)明细信息：";

        InvokeResult<BuQiPackageRes> res = new InvokeResult<>();
        res.success();
        String sendVehicleBizId = request.getSendVehicleBizId();
        List<String> sendCodes = jySendCodeDao.querySendCodesByVehicleBizId(sendVehicleBizId);
        if(CollectionUtils.isEmpty(sendCodes)) {
            res.parameterError("未查到该任务发货批次数据");
            return res;
        }
        Integer siteId = request.getCurrentOperate().getSiteCode();
        List<String> collectionCodeList = this.getTaskAllCollectionCode(siteId, sendCodes);
        if(CollectionUtils.isEmpty(collectionCodeList)) {
            log.warn("{}根据condition没有查到集齐collectionCode信息，req={}", methodDesc, JsonHelper.toJson(request));
            res.setMessage("未查到该任务扫描信息");
            return res;
        }
        if(log.isInfoEnabled()) {
            log.info("{}获取场地{}任务{}下所有批次{}对应的collectionCode={}",
                    methodDesc, siteId, sendVehicleBizId, JsonHelper.toJson(sendCodes), JsonHelper.toJson(collectionCodeList));
        }

        JyCollectRecordDetailCondition jqDetailQueryParam = new JyCollectRecordDetailCondition();
        jqDetailQueryParam.setSiteId(siteId.longValue());
        jqDetailQueryParam.setCollectionCodeList(collectionCodeList);
        jqDetailQueryParam.setAggCode(request.getWaybillCode());
        jqDetailQueryParam.setPageSize(request.getPageSize());
        int offset = (request.getPageNo() - 1) * request.getPageSize();
        jqDetailQueryParam.setOffset(offset);

        if(log.isInfoEnabled()) {
            log.info("{}不齐包裹查询参数={}", methodDesc, JsonHelper.toJson(jqDetailQueryParam));
        }
        List<JyCollectRecordDetailPo> collectionRecordPoList = jyScanCollectService.findByCollectionCodesAndAggCode(jqDetailQueryParam);
        if(CollectionUtils.isEmpty(collectionRecordPoList)) {
            res.setMessage("未查到运单数据");
            return res;
        }
        List<BuQiPackageDto> buQiPackageDtoList = new ArrayList<>();
        BuQiPackageRes resData = new BuQiPackageRes();
        collectionRecordPoList.forEach(collectionRecordPo -> {
            BuQiPackageDto buQiPackageDto = new BuQiPackageDto();
            buQiPackageDto.setWaybillCode(request.getWaybillCode());
            buQiPackageDto.setPackageCode(collectionRecordPo.getScanCode());
            buQiPackageDtoList.add(buQiPackageDto);
        });
        resData.setBuQiPackageDtoList(buQiPackageDtoList);
        res.setData(resData);
        return res;
    }

    /**
     * 处理不齐实操
     * （底层单个取消发货tp99在200ms上下，发货岗没有最大发货数量校验，避免全选或者选择包裹过多，拆分异步处理）
     * @param request
     * @return
     */
    @Override
    public InvokeResult<Void> buQiCancelSendScan(BuQiCancelSendScanReq request) {
        String methodDesc = "JyWarehouseSendVehicleServiceImpl.buQiCancelSendScan:不齐取消处理：";
        if(log.isInfoEnabled()) {
            log.info("{},start:参数={}", methodDesc, JsonHelper.toJson(request));
        }
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        //混扫任务状态校验
        JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();
        queryDto.setGroupCode(request.getGroupCode());
        queryDto.setTemplateCode(request.getMixScanTaskCode());
        queryDto.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
        queryDto.setFuncType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
        queryDto.setCompleteStatus(JyMixScanTaskCompleteEnum.COMPLETE.getCode());
        if(jyGroupSortCrossDetailService.mixScanTaskStatusComplete(queryDto)) {
            res.parameterError("当前混扫任务已完成");
            return res;
        }
        //发车任务状态校验
        JyBizTaskSendVehicleEntity entity = jyBizTaskSendVehicleService.findByBizId(request.getSendVehicleBizId());
        if(Objects.isNull(entity)) {
            res.parameterError("未找到当前派车任务");
            return res;
        }

        //按包裹或运单 异步取消 todo 按单按包取消发货tp99 150ms上下，异步处理（存在问题，异步取消失败，PDA无法感知，后面改造）
        sendCancelScanMq(request);
        return res;

    }


    /**
     * 接货仓发货岗取消扫描包裹或运单
     * @param request
     */
    private void sendCancelScanMq(BuQiCancelSendScanReq request) {
        String methodDesc = "JyWarehouseSendVehicleServiceImpl:sendCancelScanMq:接货仓发货岗取消发货MQ发送：";
        JySendCancelScanDto mqDto = new JySendCancelScanDto();
        mqDto.setOperatorName(request.getUser().getUserName());
        mqDto.setOperatorErp(request.getUser().getUserErp());
        mqDto.setOperateSiteId(request.getCurrentOperate().getSiteCode());
        mqDto.setOperateSiteName(request.getCurrentOperate().getSiteName());
        mqDto.setOperateTime(request.getCurrentOperate().getOperateTime().getTime());
        mqDto.setJyPostType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
        mqDto.setBizSource(JyWarehouseSendVehicleServiceImpl.OPERATE_SOURCE_PDA);
        //
        mqDto.setMainTaskBizId(request.getSendVehicleBizId());

        mqDto.setBuQiAllSelectFlag(false);
        List<Message> messageList = new ArrayList<>();
        //包裹取消
        if(CollectionUtils.isNotEmpty(request.getPackList())) {
            for(String packageCode : request.getPackList()) {
                mqDto.setBarCode(packageCode);
                mqDto.setBarCodeType(JyCollectScanCodeTypeEnum.PACKAGE.getCode());
                String businessId = String.format("%s:%s:%s:%s", packageCode, mqDto.getMainTaskBizId(), mqDto.getJyPostType(), mqDto.getBizSource());
                String msg = JsonHelper.toJson(mqDto);
                if(log.isInfoEnabled()) {
                    log.info("{}按包裹取消：businessId={},msx={}", methodDesc, businessId, msg);
                }
                messageList.add(new Message(jyCancelScanProducer.getTopic(), msg, businessId));
            }
        }
        //运单取消
        if(CollectionUtils.isNotEmpty(request.getWaybillCodeList())) {
            for(String waybillCode : request.getWaybillCodeList()) {
                mqDto.setBarCode(waybillCode);
                mqDto.setBarCodeType(JyCollectScanCodeTypeEnum.WAYBILL.getCode());
                String businessId = String.format("%s:%s:%s:%s", waybillCode, mqDto.getMainTaskBizId(), mqDto.getJyPostType(), mqDto.getBizSource());
                String msg = JsonHelper.toJson(mqDto);
                if(log.isInfoEnabled()) {
                    log.info("{}按运单取消：businessId={},msx={}", methodDesc, businessId, msg);
                }
                messageList.add(new Message(jyCancelScanProducer.getTopic(), msg, businessId));
            }
        }
        jyCancelScanProducer.batchSendOnFailPersistent(messageList);
    }


    /**
     * 获取箱号下一流向
     * @param request
     * @return
     */
    private InvokeResult<List<Integer>> fetchNextSiteIdByBox(SendScanReq request) {
        String methodDesc = "JyWarehouseSendVehicleServiceImpl.fetchNextSiteIdByBox:获取箱号下一流向：";
        InvokeResult<List<Integer>> result = new InvokeResult<List<Integer>>();
        if(log.isInfoEnabled()) {
            log.info("{},参数={}", methodDesc, JsonHelper.toJson(request));
        }
        Box box = boxService.findBoxByCode(request.getBarCode());
        if (box == null) {
            result.setMessage(HintService.getHint(HintCodeConstants.BOX_NOT_EXIST));
            result.setCode(BoxResponse.CODE_BOX_NOT_FOUND);
            return result;
        }
        result.setData(Arrays.asList(box.getReceiveSiteCode()));
        return result;
    }

    /**
     * 扫描包裹或运单获取预分拣站点及下一流向
     * @param request
     * @return
     */
    private InvokeResult<List<Integer>> fetchNextSiteIdByWaybillOrPackage(SendScanReq request) {
        String methodDesc = "JyWarehouseSendVehicleServiceImpl.fetchNextSiteIdByWaybillOrPackage:获取运单可能下一流向：";
        CallerInfo info = Profiler.registerInfo("DMSWEB.jsf.JyWarehouseSendVehicleServiceImpl.fetchNextSiteIdByWaybillOrPackage", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            InvokeResult<List<Integer>> result = new InvokeResult<List<Integer>>();
            if (log.isInfoEnabled()) {
                log.info("{},参数={}", methodDesc, JsonHelper.toJson(request));
            }
            String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
            Integer operateSiteCode = request.getCurrentOperate().getSiteCode();
            long operateTime = request.getCurrentOperate().getOperateTime().getTime();
            /* 获取预分拣站点 */
            InvokeResult<Integer> oldSiteIdRes = waybillCommonService.fetchOldSiteId(waybillCode);
            if (!oldSiteIdRes.codeSuccess()) {
                result.error(oldSiteIdRes.getMessage());
                return result;
            }

            Integer siteCode = oldSiteIdRes.getData();

            /* 路由站点关系 index:0 表示预分拣站点；index:>0 表示可能的下一跳的路由 */
            List<Integer> siteRouters = new ArrayList<Integer>();
            Set<Integer> nextRouters = new HashSet<Integer>();
            siteRouters.add(siteCode);

            /* 根据operateType判断是否走路由，还是走分拣的配置 */
            if (request.getOperateType().equals(JySendFlowConfigEnum.ROUTER.getCode())) {
                /* 通过路由调用 */
                result = this.getSiteRoutersFromRouterJsf(operateSiteCode, waybillCode, nextRouters);
                if (CollectionUtils.isEmpty(nextRouters)) {
                    result.error("获取路由流向数据失败");
                }
            } else {
                /* 按龙门架时需要先获取大小站逻辑 */
                Integer receiveSite = siteCode;
                Integer selfSite = baseService.getMappingSite(siteCode);
                if (selfSite != null) {
                    receiveSite = selfSite;
                }
                /* 通过发货配置jsf接口调用 */
                result = getSiteRoutersFromDMSAutoJsf(request.getMachineCode(), operateSiteCode, receiveSite, operateTime, waybillCode, nextRouters);
            }
            siteRouters.addAll(nextRouters);
            result.setData(siteRouters);
            return result;
        }catch (Exception e) {
            Profiler.functionError(info);
            log.info("{},请求={}", methodDesc, JsonHelper.toJson(request), e);
            throw new JyBizException("查询发货流向服务异常");
        }finally {
            Profiler.registerInfoEnd(info);
        }
    }



    /**
     * 按路由获取下一流向
     */
    private InvokeResult<List<Integer>> getSiteRoutersFromRouterJsf
    (Integer operateSiteCode, String waybillCode,Set<Integer> nextRouters) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.jsf.JyWarehouseSendVehicleServiceImpl.getSiteRoutersFromRouterJsf", Constants.UMP_APP_NAME_DMSWEB,false, true);

        InvokeResult<List<Integer>> result = new InvokeResult<List<Integer>>();
        try {
            RouteNextDto routeNextDto = routerService.matchRouterNextNode(operateSiteCode,waybillCode);
            if(routeNextDto.isRoutExistCurrentSite()){
                nextRouters.add(routeNextDto.getFirstNextSiteId());
            }
        } catch (Exception e) {
            log.error("JyWarehouseSendVehicleServiceImpl.getSiteRoutersFromRouterJsf-->路由接口调用异常,单号为：{}" , waybillCode,e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(HintService.getHint(HintCodeConstants.GET_ROUTER_BY_WAYBILL_ERROR));
            return result;
        }
        Profiler.registerInfoEnd(info);
        return  result;
    }

    /**
     * 按分拣龙门架配置获取下一流向
     * @return
     */
    private InvokeResult<List<Integer>> getSiteRoutersFromDMSAutoJsf
    (String machineCode, Integer operateSiteCode, Integer destinationSiteCode,Long operateTime,String waybillCode,Set<Integer> nextRouters) {

        InvokeResult<List<Integer>> result = new InvokeResult<List<Integer>>();

        AreaDestJsfRequest jsfRequest = new AreaDestJsfRequest();
        jsfRequest.setOriginalSiteCode(operateSiteCode);
        jsfRequest.setDestinationSiteCode(destinationSiteCode);
        jsfRequest.setOperateTime(operateTime);
        jsfRequest.setMachineId(machineCode);
        jsfRequest.setDeviceType(DeviceTypeEnum.GANTRY.getTypeCode());
        BaseDmsAutoJsfResponse<List<AreaDestJsfVo>> jsfResponse;

        CallerInfo info = Profiler.registerInfo("DMSWEB.jsf.getSiteRoutersFromDMSAutoJsf.areaDestJsfService.findAreaDest", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            /* 调用发货配置jsf接口 */
            jsfResponse = areaDestJsfService.findAreaDest(jsfRequest);
        } catch (Exception e) {
            Profiler.functionError(info);
            log.error("JyWarehouseSendVehicleServiceImpl.getBarCodeAllRouters-->配置接口调用异常,单号为：{}, 请求={}" , waybillCode, JsonHelper.toJson(jsfRequest), e);
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(HintService.getHint(HintCodeConstants.GET_AREA_DEST_ERROR));
            return result;
        }finally {
            Profiler.registerInfoEnd(info);
        }

        if (null == jsfResponse || jsfResponse.getStatusCode() != BaseDmsAutoJsfResponse.SUCCESS_CODE
                || jsfResponse.getData() == null) {
            if (log.isWarnEnabled()) {
                log.warn("JyWarehouseSendVehicleServiceImpl.getBarCodeAllRouters-->从分拣的发货配置关系中没有获取到有效的路由配置，req={}，返回值为:{}"
                        , JsonHelper.toJson(jsfRequest), JsonHelper.toJson(jsfResponse));
            }
            result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
            result.setMessage("龙门架配置未配置发货关系");
            return result;
        }
        for (AreaDestJsfVo areaDestJsfVo : jsfResponse.getData()) {
            if (!operateSiteCode.equals(areaDestJsfVo.getCreateSiteCode())) {
                log.warn("JyWarehouseSendVehicleServiceImpl.getBarCodeAllRouters-->areaDestJsfService接口数据获取错误");
                continue;
            }
            /*
             * 如果中转场编号为空，或者为0，或者为当前分拣中心，则选receiveSiteCode作为下一跳
             * 否则直接将中转场编号作为下一跳
             * 如果返回的记录中有多条符合则拼接到siteRouters的后面
             */
            if (areaDestJsfVo.getTransferSiteCode() == null
                    || operateSiteCode.equals(areaDestJsfVo.getTransferSiteCode())
                    || areaDestJsfVo.getTransferSiteCode() <= 0) {
                if (null != areaDestJsfVo.getReceiveSiteCode() && areaDestJsfVo.getReceiveSiteCode() > 0) {
                    nextRouters.add(areaDestJsfVo.getReceiveSiteCode());
                }
            } else {
                nextRouters.add(areaDestJsfVo.getTransferSiteCode());
            }
        }
        return result;
    }

    /**
     * 接货仓发货岗扫描结果集逻辑处理
     * @param resData
     */
    private void warehouseSendScanResponseHandler(SendScanReq request, JdVerifyResponse<SendScanRes> response, SendScanRes resData) {
        if(Objects.isNull(resData.getNextSiteCode())) {
            resData.setNextSiteCode(request.getPreNextSiteCode());
        }
        if(Objects.isNull(resData.getSendDetailBizId())) {
            resData.setSendDetailBizId(request.getSendVehicleDetailBizId());
        }

        if(response.codeSuccess() && !Objects.isNull(request.getLastNextSiteCode()) && !Objects.isNull(request.getPreNextSiteCode())) {
            if(!request.getLastNextSiteCode().trim().equals(request.getPreNextSiteCode().toString().trim())) {
                response.setCode(SendScanRes.CODE_FOCUS_FLOW_DIFFER);
                response.setMessage(SendScanRes.String_FOCUS_FLOW_DIFFER);
                return;
            }
        }
    }

    /**
     * 查询混扫任务下的封车流向数据
     * @param request
     * @param list
     * @return
     */
    public MixScanTaskToSealDestAgg selectMixScanTaskSealDest(SelectMixScanTaskSealDestReq request, List<JyGroupSortCrossDetailEntity> list) {
        MixScanTaskToSealDestAgg destAgg = new MixScanTaskToSealDestAgg();
        List<String> bizIds = new ArrayList<>();
        for (JyGroupSortCrossDetailEntity entity : list) {
            bizIds.add(entity.getSendVehicleDetailBizId());
        }
        
        // 根据BizIds批量获取任务
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService
                .findSendVehicleDetailByBizIds(request.getCurrentOperate().getSiteCode(),bizIds);

        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            throw new JyBizException("发货流向已作废！");
        }
        
        destAgg.setDestTotal(vehicleDetailList.size());
        destAgg.setSealedTotal(getSealedTotal(vehicleDetailList));
        destAgg.setCarToSealList(getCarToSealList(vehicleDetailList, bizIds));
        return destAgg;
    }

    /**
     * 获取流向信息详情
     * @param vehicleDetailList
     * @param bizIds
     * @return
     */
    private List<CarToSealDetail> getCarToSealList(List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList, List<String> bizIds) {
        List<CarToSealDetail> carToSealList = new ArrayList<>();
        
        HashSet<String> sendVehicleBizIds = new HashSet<>();
        for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
            sendVehicleBizIds.add(entity.getSendVehicleBizId());
        }
        // 批量获取车牌号
        List<JyBizTaskSendVehicleEntity> sendTaskList = taskSendVehicleService.findSendTaskByBizIds(new ArrayList<>(sendVehicleBizIds));
        HashMap<String,JyBizTaskSendVehicleEntity> sendTaskMap = getSendTaskMap(sendTaskList);
        
        // 批量获取统计数据
        List<JySendAggsEntity> sendAggs = sendAggService.findBySendVehicleDetailBizs(bizIds);
        HashMap<String, JySendAggsEntity> aggsMap = getAggsMag(sendAggs);
        
        for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
            CarToSealDetail detail = new CarToSealDetail();
            detail.setSendVehicleBizId(entity.getSendVehicleBizId());
            // 查询车牌
            JyBizTaskSendVehicleEntity bizTask = sendTaskMap.get(entity.getSendVehicleBizId());
            if (bizTask != null) {
                detail.setVehicleNumber(getVehicleNumber(bizTask));
            }
            detail.setSendDetailBizId(entity.getBizId());
            detail.setEndSiteId(entity.getEndSiteId().intValue());
            detail.setEndSiteName(entity.getEndSiteName());
            detail.setPlanDepartTime(entity.getPlanDepartTime());
            detail.setItemStatusDesc(JyBizTaskSendDetailStatusEnum.getNameByCode(entity.getVehicleStatus()));
            detail.setItemStatus(entity.getVehicleStatus());

            JySendAggsEntity aggs = aggsMap.get(entity.getBizId());
            if (aggs != null) {
                detail.setToScanPackCount(dealMinus(aggs.getShouldScanCount(), aggs.getActualScanCount()));
                detail.setScannedPackCount(aggs.getActualScanCount().longValue());            
            }
            carToSealList.add(detail);
        }
        return carToSealList;
    }

    private HashMap<String, JyBizTaskSendVehicleEntity> getSendTaskMap(List<JyBizTaskSendVehicleEntity> sendTaskList) {
        HashMap<String,JyBizTaskSendVehicleEntity> map = new HashMap<>();
        for (JyBizTaskSendVehicleEntity entity : sendTaskList) {
            map.put(entity.getBizId(),entity);
        }
        return map;
    }

    /**
     * 获取已封车状态的信息
     * @param vehicleDetailList
     * @return
     */
    private Integer getSealedTotal(List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList) {
        int toatl = 0;
        for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
            if (JyBizTaskSendDetailStatusEnum.SEALED.getCode().equals(entity.getVehicleStatus())) {
                toatl++;
            }
        }
        return toatl;
    }

    /**
     * 查询混扫任务详情及统计数据
     * @param mixScanTaskFlowReq
     * @return
     */
    public MixScanTaskFlowDetailRes getMixScanTaskFlowDetailList(MixScanTaskFlowDetailReq mixScanTaskFlowReq) {
        MixScanTaskFlowDetailRes res = new MixScanTaskFlowDetailRes();
        // 获取混扫任务信息
        MixScanTaskQueryReq req = new MixScanTaskQueryReq();
        req.setTemplateCode(mixScanTaskFlowReq.getTemplateCode());
        req.setCurrentOperate(mixScanTaskFlowReq.getCurrentOperate());
        req.setGroupCode(mixScanTaskFlowReq.getGroupCode());
        InvokeResult<MixScanTaskDetailRes> invokeResult = getMixScanTaskDetailList(req);
        if (invokeResult==null 
                || invokeResult.getData()==null 
                || CollectionUtils.isEmpty(invokeResult.getData().getMixScanTaskDetailDtoList())) {
            throw new JyBizException("未获取到流向信息!");
        }
        setMixScanTaskSendProgressData(res, invokeResult.getData().getMixScanTaskDetailDtoList(), mixScanTaskFlowReq);
        return res;
    }



    @Override
    public InvokeResult<Void> checkBeforeSealCar(SealCarCheckDtoReq request) {
        InvokeResult<Void> res = new InvokeResult<>();
        //混扫任务状态校验
        JyGroupSortCrossDetailEntityQueryDto queryDto = new JyGroupSortCrossDetailEntityQueryDto();
        queryDto.setGroupCode(request.getGroupCode());
        queryDto.setTemplateCode(request.getMixScanTaskCode());
        queryDto.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
        queryDto.setFuncType(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode());
        queryDto.setCompleteStatus(JyMixScanTaskCompleteEnum.COMPLETE.getCode());
        if(jyGroupSortCrossDetailService.mixScanTaskStatusComplete(queryDto)) {
            res.error("当前混扫任务已经结束，请刷新页面重新选择混扫任务");
            return res;
        }

        JyGroupSortCrossDetailEntity entity = new JyGroupSortCrossDetailEntity();
        entity.setGroupCode(request.getGroupCode());
        entity.setTemplateCode(request.getMixScanTaskCode());
        entity.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
        List<JyGroupSortCrossDetailEntity> entityList = jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(entity);
        if(CollectionUtils.isEmpty(entityList)) {
            res.setMessage("查询混扫任务流向为空");
            return res;
        }

        List<String> detailBizList = entityList.stream().map(JyGroupSortCrossDetailEntity::getSendVehicleDetailBizId).collect(Collectors.toList());
        List<JyBizTaskSendVehicleDetailEntity> jyBizTaskSendVehicleDetailEntityList = jyBizTaskSendVehicleDetailService.findByDetailVehicleBiz(detailBizList, request.getCurrentOperate().getSiteCode());
        if(CollectionUtils.isEmpty(detailBizList)) {
            res.setMessage("查询发货明细任务为空");
            return res;
        }

        HashSet<String> bizIdSet = new HashSet<>();
        jyBizTaskSendVehicleDetailEntityList.forEach(detailEntity -> {
            bizIdSet.add(detailEntity.getSendVehicleBizId());
        });

        List<String> bizIds = bizIdSet.stream().collect(Collectors.toList());
        List<JyBizTaskSendVehicleEntity> jyBizTaskSendVehicleEntityList = jyBizTaskSendVehicleService.findSendTaskByBizIds(bizIds);
        if(CollectionUtils.isEmpty(jyBizTaskSendVehicleEntityList)) {
            res.setMessage("查询发货任务为空");
            return res;
        }
        //自建任务&未绑定  禁止前往封车
        for (JyBizTaskSendVehicleEntity entity1 : jyBizTaskSendVehicleEntityList) {
            if(Constants.NUMBER_ONE.equals(entity1.getManualCreatedFlag()) && !Constants.NUMBER_ONE.equals(entity1.getBindFlag())) {
                res.error("存在自建任务未绑定发货任务，禁止前往封车");
                return res;
            }
        }
        return res;
    }

    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean mixScanTaskComplete(MixScanTaskCompleteReq request) {
        try {
            // 完成混扫任务
            JyGroupSortCrossDetailEntity updateInfo = new JyGroupSortCrossDetailEntity();
            updateInfo.setGroupCode(request.getGroupCode());
            updateInfo.setTemplateCode(request.getTemplateCode());
            updateInfo.setStartSiteId((long)request.getCurrentOperate().getSiteCode());
            updateInfo.setUpdateUserErp(request.getUser().getUserErp());
            updateInfo.setUpdateUserName(request.getUser().getUserName());
            if (!jyGroupSortCrossDetailService.mixScanTaskComplete(updateInfo)) {
                return false;
            }
            // 获取混扫任务下的流向信息
            JyGroupSortCrossDetailEntity condition = new JyGroupSortCrossDetailEntity();
            condition.setStartSiteId(Long.valueOf(request.getCurrentOperate().getSiteCode()));
            condition.setTemplateCode(request.getTemplateCode());
            condition.setGroupCode(request.getGroupCode());
            List<JyGroupSortCrossDetailEntity> entities = jyGroupSortCrossDetailService.listSendFlowByTemplateCodeOrEndSiteCode(condition);
            List<String> detailBizIds = new ArrayList<>();
            for (JyGroupSortCrossDetailEntity entity : entities) {
                detailBizIds.add(entity.getSendVehicleDetailBizId());
            }

            // 更新车辆状态
            if (!updateStatusByDetailBizIds(request, detailBizIds)) {
                throw new JyBizException("完成混扫任务失败!");
            }
        }catch (Exception e) {
            log.warn("完成混扫任务异常: {}",JsonHelper.toJson(request), e);
            throw new JyBizException("完成混扫任务异常!");
        }
        return true;
    }
}
