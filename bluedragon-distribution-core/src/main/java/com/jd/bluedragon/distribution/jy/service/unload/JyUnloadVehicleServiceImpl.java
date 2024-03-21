package com.jd.bluedragon.distribution.jy.service.unload;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.inspection.response.InspectionCheckResultDto;
import com.jd.bluedragon.common.dto.operation.workbench.enums.BarCodeLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.UnloadBarCodeScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.UnloadScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.LineTypeStatis;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleBaseInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionScanRequest;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.common.utils.SdkConvertAndroidUtil;
import com.jd.bluedragon.configuration.DmsConfigManager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillRouteLinkQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.easyFreezeSite.EasyFreezeSiteManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.economic.domain.EconomicNetException;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.jy.constants.RedisHashKeyConstants;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.*;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.exception.JyDemotionException;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.JyCallbackJsfManager;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.config.JyDemotionService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.autoRefresh.enums.ClientAutoRefreshBusinessTypeEnum;
import com.jd.bluedragon.distribution.jy.service.transfer.manager.JYTransferConfigProxy;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;

import com.jd.bluedragon.distribution.seal.manager.SealCarManager;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.waybill.enums.WaybillVasEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.JyUnloadTaskSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.vos.dto.StopoverInfoDto;
import com.jd.etms.vos.dto.StopoverQueryDto;
import com.jd.bluedragon.utils.converter.BeanConverter;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.util.WaybillCodeRuleValidateUtil;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.constants.CodeConstants;
import com.jd.ql.dms.common.constants.JyConstants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.transferDp.ConfigTransferDpSite;
import com.jdl.basic.api.enums.TenantEnum;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import com.jdl.sorting.tech.tenant.core.context.TenantContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName JyUnloadVehicleServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/4/2 17:04
 **/
@Service
public class JyUnloadVehicleServiceImpl implements IJyUnloadVehicleService {

    private static final Logger log = LoggerFactory.getLogger(JyUnloadVehicleServiceImpl.class);

    private static final int UNLOAD_CACHE_EXPIRE = 12;

    private static final int UNLOAD_SCAN_BAR_EXPIRE = 6;

    private static final int TEAN_PACKAGE_FLAG = 1;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    @Qualifier("jyUnloadScanProducer")
    private DefaultJMQProducer unloadScanProducer;

    @Autowired
    @Qualifier("jyUnloadTaskCompleteProducer")
    private DefaultJMQProducer unloadTaskCompleteProducer;

    @Autowired
    private JyUnloadDao jyUnloadDao;

    @Autowired
    private JyBizTaskUnloadVehicleService unloadVehicleService;

    @Autowired
    private JyUnloadAggsService jyUnloadAggsService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Autowired
    private IJyUnloadVehicleManager unloadVehicleManager;

    @Autowired
    private UnloadVehicleTransactionManager transactionManager;

    @Autowired
    private JyDemotionService jyDemotionService;


    @Autowired
    private WaybillService waybillService;

    @Autowired
    private WaybillRouteLinkQueryManager waybillRouteManager;

    @Autowired
    private EasyFreezeSiteManager easyFreezeSiteManager;

    @Autowired
    private DmsConfigManager dmsConfigManager;

    @Autowired
    private JYTransferConfigProxy jyTransferConfigProxy;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private BaseMinorManager baseMinorManager;

    @Autowired
    private SealCarManager sealCarManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    @Qualifier("jyCallbackJsfManager")
    private JyCallbackJsfManager jyCallbackJsfManager;

    @Autowired
    private InspectionService inspectionService;

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.fetchUnloadTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<UnloadVehicleTaskResponse> fetchUnloadTask(UnloadVehicleTaskRequest request) {
        InvokeResult<UnloadVehicleTaskResponse> result = new InvokeResult<>();

        if (!checkBeforeFetchTask(request, result)) {
            return result;
        }

        logInfo("拉取卸车任务. {}", JsonHelper.toJson(request));

        JyBizTaskUnloadVehicleEntity condition = makeFetchCondition(request);

        try {
            List<String> sealCarCodes = new ArrayList<>();
            if (WaybillUtil.isPackageCode(request.getBarCode())) {
                sealCarCodes = getSealCarCodeFromEs(request);
                if (CollectionUtils.isEmpty(sealCarCodes)) {
                    result.parameterError("该包裹号不存在关联的卸车任务！");
                    return result;
                }
            }

            List<JyBizTaskUnloadCountDto> vehicleStatusAggList =
                    unloadVehicleService.findStatusCountByCondition4Status(condition, sealCarCodes, JyBizTaskUnloadStatusEnum.UNLOAD_STATUS_OPTIONS.toArray(new JyBizTaskUnloadStatusEnum[JyBizTaskUnloadStatusEnum.UNLOAD_STATUS_OPTIONS.size()]));
            if (CollectionUtils.isEmpty(vehicleStatusAggList)) {
                return result;
            }

            UnloadVehicleTaskResponse response = new UnloadVehicleTaskResponse();
            // 增加刷新间隔配置
            response.setClientAutoRefreshConfig(dmsConfigManager.getPropertyConfig().getJyWorkAppAutoRefreshConfigByBusinessType(ClientAutoRefreshBusinessTypeEnum.UNLOAD_TASK_LIST.name()));

            // 封车状态数量统计
            assembleUnloadStatusAgg(vehicleStatusAggList, response);

            // 按卸车状态组装车辆数据
            assembleUnloadVehicleData(request, response, condition, sealCarCodes);

            result.setData(response);
        }
        catch (JyDemotionException e){
            result.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_NO_SEAL_CAR_CODE, false));
        }
        catch (Exception e) {
            log.error("查询卸车任务异常. {}", JsonHelper.toJson(request), e);
            result.error("查询卸车任务异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    /**
     * 按卸车状态组装车辆数据
     * @param request
     * @param response
     * @param condition
     * @param sealCarCodes
     */
    private void assembleUnloadVehicleData(UnloadVehicleTaskRequest request, UnloadVehicleTaskResponse response,
                                           JyBizTaskUnloadVehicleEntity condition, List<String> sealCarCodes) {
        JyBizTaskUnloadStatusEnum curQueryStatus = JyBizTaskUnloadStatusEnum.getEnumByCode(request.getVehicleStatus());
        List<LineTypeStatis> lineTypeList = this.getVehicleLineTypeList(condition, curQueryStatus, sealCarCodes);
        Long teanCount = unloadVehicleService.findStatusCountByCondition4StatusAndLineOfTEAN(condition, sealCarCodes, curQueryStatus);
        UnloadVehicleData unloadVehicleData = new UnloadVehicleData();
        unloadVehicleData.setVehicleStatus(curQueryStatus.getCode());
        unloadVehicleData.setLineStatistics(lineTypeList);
        unloadVehicleData.setTotalOfTEAN(teanCount);

        // 按车辆状态组装
        makeVehicleList(condition, request, curQueryStatus, unloadVehicleData, sealCarCodes);

        switch (curQueryStatus) {
            case WAIT_UN_LOAD:
                response.setToUnloadVehicleData(unloadVehicleData);
                break;
            case UN_LOADING:
                response.setUnloadVehicleData(unloadVehicleData);
                break;
            case UN_LOAD_DONE:
                response.setUnloadCompletedData(unloadVehicleData);
                break;
        }
    }

    /**
     *
     * @param vehicleStatusAggList
     * @param response
     */
    private void assembleUnloadStatusAgg(List<JyBizTaskUnloadCountDto> vehicleStatusAggList, UnloadVehicleTaskResponse response) {
        List<VehicleStatusStatis> statusAgg = Lists.newArrayListWithCapacity(vehicleStatusAggList.size());
        response.setStatusAgg(statusAgg);

        for (JyBizTaskUnloadCountDto countDto : vehicleStatusAggList) {
            VehicleStatusStatis item = new VehicleStatusStatis();
            item.setVehicleStatus(countDto.getVehicleStatus());
            item.setVehicleStatusName(JyBizTaskUnloadStatusEnum.getNameByCode(item.getVehicleStatus()));
            item.setTotal(countDto.getSum().longValue());
            statusAgg.add(item);
        }
    }

    /**
     * 按车辆状态组装车辆列表
     * @param condition
     * @param request
     * @param curQueryStatus
     * @param unloadVehicleData
     * @param sealCarCodes
     */
    private void makeVehicleList(JyBizTaskUnloadVehicleEntity condition, UnloadVehicleTaskRequest request,
                                 JyBizTaskUnloadStatusEnum curQueryStatus, UnloadVehicleData unloadVehicleData, List<String> sealCarCodes) {
        List<VehicleBaseInfo> vehicleList = Lists.newArrayList();
        unloadVehicleData.setData(vehicleList);

        JyBizTaskUnloadOrderTypeEnum orderTypeEnum = setTaskOrderType(curQueryStatus);
        List<JyBizTaskUnloadVehicleEntity> vehiclePageList = unloadVehicleService.findByConditionOfPage(condition, orderTypeEnum, request.getPageNumber(), request.getPageSize(), sealCarCodes);
        if (CollectionUtils.isEmpty(vehiclePageList)) {
            return;
        }

        for (JyBizTaskUnloadVehicleEntity entity : vehiclePageList) {
            // 初始化基础字段
            VehicleBaseInfo vehicleBaseInfo = assembleVehicleBase(curQueryStatus, entity);

            // 设置个性化字段
            switch (curQueryStatus) {
                case WAIT_UN_LOAD:
                    ToUnloadVehicle toUnloadVehicle = (ToUnloadVehicle) vehicleBaseInfo;
                    toUnloadVehicle.setDeSealCarTime(entity.getDesealCarTime());
                    toUnloadVehicle.setManualCreatedTask(entity.unloadWithoutTask());
                    toUnloadVehicle.setTags(resolveTagSign(entity.getTagsSign()));
                    toUnloadVehicle.setTaskId(getJyScheduleTaskId(entity.getBizId()));
                    toUnloadVehicle.setBizId(entity.getBizId());
                    vehicleList.add(toUnloadVehicle);
                    break;
                case UN_LOADING:
                    UnloadVehicleInfo unloadVehicleInfo = (UnloadVehicleInfo) vehicleBaseInfo;
                    unloadVehicleInfo.setManualCreatedTask(entity.unloadWithoutTask());
                    unloadVehicleInfo.setTags(resolveTagSign(entity.getTagsSign()));
                    unloadVehicleInfo.setUnloadProgress(this.setUnloadProgress(entity));
                    unloadVehicleInfo.setTaskId(getJyScheduleTaskId(entity.getBizId()));
                    unloadVehicleInfo.setBizId(entity.getBizId());
                    vehicleList.add(unloadVehicleInfo);
                    break;
                case UN_LOAD_DONE:
                    UnloadCompleteVehicle completeVehicle = (UnloadCompleteVehicle) vehicleBaseInfo;
                    completeVehicle.setTags(resolveTagSign(entity.getTagsSign()));
                    completeVehicle.setLessCount(entity.getLessCount());
                    completeVehicle.setMoreCount(entity.getMoreCount());
                    completeVehicle.setManualCreatedTask(entity.unloadWithoutTask());
                    completeVehicle.setAbnormalFlag(entity.unloadAbnormal());
                    completeVehicle.setUnloadFinishTime(entity.getUnloadFinishTime());
                    completeVehicle.setTaskId(getJyScheduleTaskId(entity.getBizId()));
                    completeVehicle.setBizId(entity.getBizId());
                    vehicleList.add(completeVehicle);
                    break;
            }
        }
    }

    private BigDecimal setUnloadProgress(JyBizTaskUnloadVehicleEntity entity) {
        if (NumberHelper.gt0(entity.getUnloadProgress())) {
            return entity.getUnloadProgress().multiply(new BigDecimal(100)).setScale(6, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    /**
     * 查询调度任务ID
     * @param bizId
     * @return
     */
    private String getJyScheduleTaskId(String bizId) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(bizId);
        req.setTaskType(JyScheduleTaskTypeEnum.UNLOAD.getCode());
        JyScheduleTaskResp scheduleTask = jyScheduleTaskManager.findScheduleTaskByBizId(req);
        return null != scheduleTask ? scheduleTask.getTaskId() : StringUtils.EMPTY;
    }

    /**
     * 组装卸车基础数据
     * @param curQueryStatus
     * @param entity
     * @return
     */
    private VehicleBaseInfo assembleVehicleBase(JyBizTaskUnloadStatusEnum curQueryStatus, JyBizTaskUnloadVehicleEntity entity) {
        VehicleBaseInfo vehicleBaseInfo = null;
        switch (curQueryStatus) {
            case WAIT_UN_LOAD:
                vehicleBaseInfo = new ToUnloadVehicle();
                break;
            case UN_LOADING:
                vehicleBaseInfo = new UnloadVehicleInfo();
                break;
            case UN_LOAD_DONE:
                vehicleBaseInfo = new UnloadCompleteVehicle();
                break;
        }

        vehicleBaseInfo.setSealCarCode(entity.getSealCarCode());
        vehicleBaseInfo.setVehicleNumber(entity.getVehicleNumber());
        vehicleBaseInfo.setLineType(entity.getLineType());
        vehicleBaseInfo.setLineTypeName(entity.getLineTypeName());
        vehicleBaseInfo.setStarSiteId(entity.getStartSiteId().intValue());
        vehicleBaseInfo.setStartSiteName(entity.getStartSiteName());

        return vehicleBaseInfo;
    }

    /**
     * 解析任务标签
     * @param tagSign
     * @return
     */
    private List<LabelOption> resolveTagSign(String tagSign) {
        List<LabelOption> tagList = new ArrayList<>();

        // 是否抽检
        if (BusinessUtil.needSpotCheck(tagSign)) {
            UnloadTaskLabelEnum spotCheck = UnloadTaskLabelEnum.SPOT_CHECK;
            tagList.add(new LabelOption(spotCheck.getCode(), spotCheck.getName(), spotCheck.getDisplayOrder()));
        }

        // 逐单卸
        if (BusinessUtil.isSignY(tagSign, JyUnloadTaskSignConstants.POSITION_2)) {
            UnloadTaskLabelEnum unloadSingleBill = UnloadTaskLabelEnum.UNLOAD_SINGLE_BILL;
            tagList.add(new LabelOption(unloadSingleBill.getCode(), unloadSingleBill.getName(), unloadSingleBill.getDisplayOrder()));
        }

        // 半车卸
        if (BusinessUtil.isSignY(tagSign, JyUnloadTaskSignConstants.POSITION_3)) {
            UnloadTaskLabelEnum unloadHalfCar = UnloadTaskLabelEnum.UNLOAD_HALF_CAR;
            tagList.add(new LabelOption(unloadHalfCar.getCode(), unloadHalfCar.getName(), unloadHalfCar.getDisplayOrder()));
        }
        // 特安服务
        if (BusinessUtil.isSignY(tagSign, JyUnloadTaskSignConstants.POSITION_4)) {
            UnloadTaskLabelEnum unloadHalfCar = UnloadTaskLabelEnum.TE_AN_SERVICE;
            tagList.add(new LabelOption(unloadHalfCar.getCode(), unloadHalfCar.getName(), unloadHalfCar.getDisplayOrder()));
        }

        return tagList;
    }

    private List<LineTypeStatis> getVehicleLineTypeList(JyBizTaskUnloadVehicleEntity condition, JyBizTaskUnloadStatusEnum curQueryStatus, List<String> sealCarCodes) {
        List<LineTypeStatis> lineTypeList = new ArrayList<>();
        List<JyBizTaskUnloadCountDto> lineTypeAgg = unloadVehicleService.findStatusCountByCondition4StatusAndLine(condition, sealCarCodes, curQueryStatus);
        if (CollectionUtils.isNotEmpty(lineTypeAgg)) {
            for (JyBizTaskUnloadCountDto countDto : lineTypeAgg) {
                LineTypeStatis lineTypeStatis = createLineTypeAgg(countDto);
                lineTypeList.add(lineTypeStatis);
            }
        }

        return lineTypeList;
    }


    private LineTypeStatis createLineTypeAgg(JyBizTaskUnloadCountDto countDto) {
        LineTypeStatis lineTypeStatis = new LineTypeStatis();
        lineTypeStatis.setLineType(countDto.getLineType());
        lineTypeStatis.setLineTypeName(JyLineTypeEnum.getNameByCode(countDto.getLineType()));
        lineTypeStatis.setTotal(countDto.getSum().longValue());
        return lineTypeStatis;
    }

    private JyBizTaskUnloadOrderTypeEnum setTaskOrderType(JyBizTaskUnloadStatusEnum curQueryStatus) {
        switch (curQueryStatus) {
            case WAIT_UN_LOAD:
                return JyBizTaskUnloadOrderTypeEnum.DESEAL_CAR_TIME;
            case UN_LOADING:
                return JyBizTaskUnloadOrderTypeEnum.UNLOAD_PROGRESS;
            case UN_LOAD_DONE:
                return JyBizTaskUnloadOrderTypeEnum.ORDER_TIME_ABNORMAL;
            default:
                return null;
        }
    }

    /**
     * 卸车任务查询条件
     * @param request
     * @return
     */
    private JyBizTaskUnloadVehicleEntity makeFetchCondition(UnloadVehicleTaskRequest request) {
        JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
        condition.setEndSiteId(request.getEndSiteCode().longValue());
        condition.setVehicleStatus(request.getVehicleStatus());
        condition.setLineType(request.getLineType());
        if (StringUtils.isNotBlank(request.getBarCode()) && !WaybillUtil.isPackageCode(request.getBarCode())) {
            condition.setFuzzyVehicleNumber(request.getBarCode());
        }
        condition.setTaskType(request.getTaskType());
        if(Objects.equals(request.getTeanFlag(),TEAN_PACKAGE_FLAG)){
            condition.setTeanFlag(request.getTeanFlag());
        }

        return condition;
    }

    /**
     * 根据包裹号从ES获得封车编码
     * @param request
     * @return
     */
    private List<String> getSealCarCodeFromEs(UnloadVehicleTaskRequest request) {
        JyVehicleTaskUnloadDetail query = new JyVehicleTaskUnloadDetail();
        query.setPackageCode(request.getBarCode());
        query.setEndSiteId(request.getEndSiteCode());
        List<JyVehicleTaskUnloadDetail> unloadDetails = unloadVehicleManager.findUnloadDetail(query);

        Set<String> sealCarCodes = new HashSet<>();
        if (CollectionUtils.isNotEmpty(unloadDetails)) {
            for (JyVehicleTaskUnloadDetail unloadDetail : unloadDetails) {
                sealCarCodes.add(unloadDetail.getSealCarCode());
            }
            return new ArrayList<>(sealCarCodes);
        }

        return null;
    }

    private boolean checkBeforeFetchTask(UnloadVehicleTaskRequest request, InvokeResult<UnloadVehicleTaskResponse> result) {
        if (!NumberHelper.gt0(request.getVehicleStatus())) {
            result.parameterError("请选择车辆状态");
            return false;
        }
        if (!NumberHelper.gt0(request.getPageSize()) || !NumberHelper.gt0(request.getPageNumber())) {
            result.parameterError("缺少分页参数");
            return false;
        }
        if (!NumberHelper.gt0(request.getEndSiteCode())) {
            result.parameterError("缺少目的场地");
            return false;
        }

        if (request.getTaskType() == null) {
            request.setTaskType(JyBizTaskUnloadTaskTypeEnum.UNLOAD_TASK_CATEGORY_DMS.getCode());
        }
        return true;
    }

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    /**
     * 执行卸车扫描操作
     * @param request 卸车扫描请求对
     * @return 返回带有校验结果的卸车扫描响应对象
     * @throws EconomicNetException 经济网异常
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.unloadScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<UnLoadScanResponse> unloadScan(UnloadScanRequest request) {

        logInfo("开始卸车扫描. {}", JsonHelper.toJson(request));

        JdVerifyResponse<UnLoadScanResponse> result = new JdVerifyResponse<>();
        result.toSuccess();

        JyBizTaskUnloadVehicleEntity taskUnloadVehicle = unloadVehicleService.findByBizId(request.getBizId());
        if (taskUnloadVehicle == null) {
            result.toCustomError(InvokeResult.CODE_HINT, "卸车任务不存在，请刷新卸车任务列表后再扫描！");
            return result;
        }else {
            if(Boolean.TRUE.equals(dmsConfigManager.getUccPropertyConfiguration().getPdaUnloadAndAutoInspectionRejectSwitch())) {
                // 判断是否触发围栏到车自动验货，如果已经开启则不能手动验
                if(Boolean.TRUE.equals(request.getRejectAutoInspectionSwitch()) && Constants.NUMBER_ONE.equals(taskUnloadVehicle.getAutoInspectionFlag())) {
                    result.toCustomError(InvokeResult.CODE_HINT, "该任务包裹已围栏到车自动验货，无需手动验货");
                    return result;
                }
            }
        }

        // 卸车扫描前置校验
        if (!checkBeforeScan(result, request)) {
            return result;
        }

        // 扫描前校验拦截结果
        if (!checkBarInterceptResult(result, request)) {
            // 失败直接返回
            return result;
        }

        try {
            // 上下文数据
            final UnloadScanContextDto unloadScanContextDto = new UnloadScanContextDto();
            unloadScanContextDto.setUnloadScanRequest(request);

            // 保存扫描记录，发运单全程跟踪。首次扫描分配卸车任务
            UnloadScanDto unloadScanDto = createUnloadDto(request, taskUnloadVehicle);

            unloadScanProducer.sendOnFailPersistent(unloadScanDto.getBarCode(), JsonHelper.toJson(unloadScanDto));

            // 判断是否本场地单子
            this.handleMoreLocalOrOutScan(request, unloadScanDto, result);

            // 统计本次扫描的包裹数
            this.calculateScanPackageCount(request, result, unloadScanContextDto);

            // 德邦场地提示
            this.handleDepponMergeCondition(request, result, unloadScanContextDto);

            // 记录卸车任务扫描进度
            this.recordUnloadProgress(result.getData(), unloadScanDto, request, taskUnloadVehicle);

            // 处理特殊产品类型提示音
            this.handleSpecialProductType(request, result, unloadScanContextDto);

            // 执行回调
            this.unloadScanOfCallback(result,request);
        }
        catch (EconomicNetException e) {
            log.error("发货任务扫描失败. 三方箱号未准备完成{}", JsonHelper.toJson(request), e);
            result.toFail(e.getMessage());
            redisClientOfJy.del(getBizBarCodeCacheKey(request.getBarCode(), request.getCurrentOperate().getSiteCode(), request.getBizId()));
        }
        catch (Exception ex) {
            log.error("卸车扫描失败. {}", JsonHelper.toJson(request), ex);
            result.toFail("服务器异常，卸车扫描失败，请咚咚联系分拣小秘！");

            redisClientOfJy.del(getBizBarCodeCacheKey(request.getBarCode(), request.getCurrentOperate().getSiteCode(), request.getBizId()));
        }

        return result;
    }

    /**
     * 调用验货拦截链
     * @param response
     * @param request
     * @return
     */
    private boolean checkBarInterceptResult(JdVerifyResponse<UnLoadScanResponse> response, UnloadScanRequest request) {
        // 非强制提交，校验拦截
        if (!request.getForceSubmit()) {
            final InspectionScanRequest inspectionScanRequest = new InspectionScanRequest();
            BeanHelper.copyProperties(inspectionScanRequest, request);
            JdVerifyResponse<InspectionCheckResultDto> verifyResponse = inspectionService.checkBeforeInspection(inspectionScanRequest);
            if (verifyResponse.getCode() != JdVerifyResponse.CODE_SUCCESS) {
                response.setCode(verifyResponse.getCode());
                response.setMessage(verifyResponse.getMessage());
                return false;
            } else {
                if (CollectionUtils.isNotEmpty(verifyResponse.getMsgBoxes())) {
                    response.setCode(verifyResponse.getCode());
                    response.setMessage(verifyResponse.getMessage());
                    response.setMsgBoxes(verifyResponse.getMsgBoxes());
                    return true;
                }
            }
        }

        return true;
    }

    private void handleMoreLocalOrOutScan(UnloadScanRequest request, UnloadScanDto unloadScanDto, JdVerifyResponse<UnLoadScanResponse> result) {
        // 降级开关
        if (!sysConfigService.getConfigByName(Constants.MORE_OUT_SCAN_NOTIFY_SWITCH)) {
            log.info("handleMoreLocalOrOutScan|卸车扫描非本场地多扫弱提醒开关已关闭");
            return;
        }
        String barCode = request.getBarCode();
        int siteCode = request.getCurrentOperate().getSiteCode();
        String waybillCode = null;
        if (WaybillUtil.isPackageCode(barCode)) {
            waybillCode = WaybillUtil.getWaybillCode(barCode);
        } else if (WaybillUtil.isWaybillCode(barCode)) {
            waybillCode = barCode;
        } else if (BusinessHelper.isBoxcode(barCode)) {
            CallerInfo inlineUmp = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleService.unloadScan.getCancelSendByBox");
            List<SendDetail> list = deliveryService.getCancelSendByBox(barCode);
            Profiler.registerInfoEnd(inlineUmp);
            if (CollectionUtils.isNotEmpty(list)) {
                waybillCode = list.get(0).getWaybillCode();
            }
        }
        if (waybillCode == null) {
            log.warn("handleMoreLocalOrOutScan|运单号为空:barCode={}", JsonHelper.toJson(request));
            return;
        }
        // 判断是否本场地
        if (!hasCurrentNodeInRouteLink(siteCode, waybillCode)) {
            unloadScanDto.setMoreFlag(Constants.MORE_OUT_SCAN);
            result.addPromptBox(InvokeResult.CODE_MORE_OUT_SCAN, InvokeResult.CODE_MORE_OUT_SCAN_MESSAGE);
        }
    }

    private boolean hasCurrentNodeInRouteLink(int operateSiteCode, String waybillCode) {
        // 根据运单号查询waybill表router字段
        String routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
        // 如果路由链路为空，则默认是本场地
        if (StringUtils.isBlank(routerStr)) {
            return true;
        }
        String[] routerArray = routerStr.split(Constants.WAYBILL_ROUTER_SPLIT);
        for (String node : routerArray) {
            if (node.equals(String.valueOf(operateSiteCode))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 卸车包裹是否应扫查询条件
     * @param request
     * @return
     */
    private Pager<JyVehicleTaskUnloadDetail> getIsShouldScanCondition(UnloadScanRequest request, String barCode) {
        Pager<JyVehicleTaskUnloadDetail> pager = new Pager<>();
        JyVehicleTaskUnloadDetail searchVo = new JyVehicleTaskUnloadDetail();
        pager.setSearchVo(searchVo);
        searchVo.setEndSiteId(request.getCurrentOperate().getSiteCode());
        searchVo.setBizId(request.getBizId());
        if (UnloadScanTypeEnum.SCAN_WAYBILL.getCode().equals(request.getScanType())) {
            searchVo.setWaybillCode(WaybillUtil.getWaybillCode(barCode));
        } else {
            searchVo.setPackageCode(barCode);
        }
        return pager;
    }

    private void handleDepponMergeCondition(UnloadScanRequest request, JdVerifyResponse<UnLoadScanResponse> result, UnloadScanContextDto unloadScanContextDto) {
        // 只处理包裹号或运单号
        if(!WaybillUtil.isWaybillCode(request.getBarCode()) && WaybillUtil.isPackageCode(request.getBarCode())){
            return;
        }
        String waybillCode = WaybillUtil.getWaybillCode(request.getBarCode());
        // 必须是德邦运单
        if (!WaybillCodeRuleValidateUtil.isDpWaybillCode(waybillCode)) {
            return;
        }
        // 找到第一个分拣中心和最后一个分拣中心
        BaseSiteInfoDto firstSortingSite = null;
        BaseSiteInfoDto lastSortingSite = null;
        final String routersStr = waybillService.getRouterByWaybillCode(waybillCode);
        log.info("JyUnloadVehicleServiceImpl.handleDepponMergeCondition getRouterByWaybillCode {}", routersStr);
        final boolean hasRouter = StringUtils.isNotBlank(routersStr);
        if(hasRouter){
            String[] routersArr = routersStr.split(Constants.WAYBILL_ROUTER_SPLIT);
            final List<String> routerStrList = Arrays.asList(routersArr);
            // 先判断是否有相同的路由节点，再判断是否为分拣中心
            boolean hasSameRouteNode = false;
            Map<String, Integer> routersCountMap = new HashMap<>();
            for (String routerStr : routerStrList) {
                Integer existCount = routersCountMap.get(routerStr);
                if(existCount == null){
                    existCount = 0;
                } else {
                    hasSameRouteNode = true;
                }
                routersCountMap.put(routerStr, existCount + 1);
            }
            // 找到第一个为大于1的节点，表名路由上有相同的节点
            if(!hasSameRouteNode){
                return;
            }
            for (int i = 0; i < routerStrList.size(); i++) {
                if(NumberHelper.isPositiveNumber(routerStrList.get(i))){
                    Integer siteCode = Integer.parseInt(routerStrList.get(i));
                    final BaseSiteInfoDto siteInfo = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
                    if(siteInfo == null){
                        continue;
                    }
                    if(this.matchSortingSite(siteInfo.getSiteType(), siteInfo.getSortType(), siteInfo.getSortSubType(), siteInfo.getSortThirdType())){
                        firstSortingSite = siteInfo;
                        break;
                    }
                }
            }
            for (int i = routerStrList.size() - 1; i >= 0; i--) {
                if(NumberHelper.isPositiveNumber(routerStrList.get(i))){
                    Integer siteCode = Integer.parseInt(routerStrList.get(i));
                    final BaseSiteInfoDto siteInfo = baseMajorManager.getBaseSiteInfoBySiteId(siteCode);
                    if(siteInfo == null){
                        continue;
                    }
                    if(this.matchSortingSite(siteInfo.getSiteType(), siteInfo.getSortType(), siteInfo.getSortSubType(), siteInfo.getSortThirdType())){
                        lastSortingSite = siteInfo;
                        break;
                    }
                }
            }
        } else {
            // 取不到路由，则取操作场地和运单预分拣站点
            final Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
            if(waybill != null){
                lastSortingSite = baseMajorManager.getBaseSiteInfoBySiteId(waybill.getOldSiteId());
                firstSortingSite = baseMajorManager.getBaseSiteInfoBySiteId(request.getCurrentOperate().getSiteCode());
            }
        }
        if(firstSortingSite == null && lastSortingSite == null){
            return;
        }
        String message = null;
        if(firstSortingSite != null && lastSortingSite != null){
            // 如果第一个和最后一个一致，则增加滑道提醒
            if(Objects.equals(firstSortingSite.getSiteCode(), lastSortingSite.getSiteCode())){
                BaseDmsStore baseDmsStore = new BaseDmsStore();
                // 查询滑道信息
                BaseEntity<BigWaybillDto> waybillDataForPrintResult = waybillQueryManager.getWaybillDataForPrint(waybillCode);
                if(waybillDataForPrintResult == null || Constants.RESULT_SUCCESS != waybillDataForPrintResult.getResultCode()){
                    log.error("handleDepponMergeCondition getWaybillDataForPrint fail : {}, {}", waybillCode, JsonHelper.toJson(waybillDataForPrintResult));
                    return;
                }
                final BigWaybillDto bigWaybillDto = waybillDataForPrintResult.getData();
                WaybillManageDomain waybillManageDomain = bigWaybillDto.getWaybillState();
                baseDmsStore.setStoreId(waybillManageDomain.getStoreId());//库房编号

                final Waybill waybill = bigWaybillDto.getWaybill();
                baseDmsStore.setCky2(waybill.getCky2());//cky2
                // baseDmsStore.setOrgId(printWaybill.getOrgId());//机构编号
                baseDmsStore.setDmsId(lastSortingSite.getSiteCode());//分拣中心编号
                final Integer originalCrossType = BusinessUtil.getOriginalCrossType(waybill.getWaybillSign(), waybill.getSendPay());
                JdResult<CrossPackageTagNew> queryCrossPackageTagForPrintResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore, waybill.getOldSiteId(), lastSortingSite.getSiteCode(), originalCrossType);
                if(!queryCrossPackageTagForPrintResult.isSucceed()) {
                    log.error("handleDepponMergeCondition queryCrossPackageTagForPrint: {},{},{},{}, {}", JsonHelper.toJson(baseDmsStore), waybill.getOldSiteId(), lastSortingSite.getSiteCode(), originalCrossType, JsonHelper.toJson(queryCrossPackageTagForPrintResult));
                }else{
                    CrossPackageTagNew crossPackageTagNew = queryCrossPackageTagForPrintResult.getData();
                    message = String.format("%s %s-%s", lastSortingSite.getSiteName(), crossPackageTagNew.getOriginalCrossCode(), crossPackageTagNew.getOriginalTabletrolleyCode());
                }
            } else {
                // 提醒最后一个分拣中心三位短码
                if(hasRouter){
                    log.info("handleDepponMergeCondition firstSortingSite or lastSortingSite not null and hasRouter");
                    message = this.getDepponAlarmSiteDmsCode(lastSortingSite);
                }
            }
        }
        if(firstSortingSite == null || lastSortingSite == null){
            log.info("handleDepponMergeCondition firstSortingSite or lastSortingSite null");
            // 提醒第一个分拣中心三位短码
            BaseSiteInfoDto alarmSite = firstSortingSite != null ? firstSortingSite : lastSortingSite;
            message = getDepponAlarmSiteDmsCode(alarmSite);
        }
        // 添加toast提示
        result.toCustomError(InvokeResult.CODE_HINT, message);
    }

    private final Integer sorting_sortType = 12351;
    private final Integer sorting_sortSubType = 123511;
    private final List<Integer> sorting_sortThirdTypeList = Arrays.asList(1235111, 1235112, 1235113, 1235114, 1235115);
    private boolean matchSortingSite(Integer siteType, Integer sortType, Integer sortSubType, Integer sortThirdType){
        if(Constants.DMS_SITE_TYPE.equals(siteType)
            || (Objects.equals(sorting_sortType, sortType) && Objects.equals(sorting_sortSubType, sortSubType) && sorting_sortThirdTypeList.contains(sortThirdType))){
            return true;
        }
        return false;
    }

    private String getDepponAlarmSiteDmsCode(BaseSiteInfoDto alarmSite) {
        if (alarmSite == null) {
            return "";
        }
        return String.format("%s(%s)", alarmSite.getSiteName(), alarmSite.getDmsCode());
    }

    /**
     * 更新PDA卸车扫描进度
     * @param dataResult
     * @param request
     * @param taskUnloadVehicle
     */
    private void recordUnloadProgress(UnLoadScanResponse dataResult, UnloadScanDto unloadScanDto, UnloadScanRequest request, JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
        Integer pdaUnloadCount = dataResult.getScanPackCount();
        String pdaOpeCacheKey = genPdaUnloadProgressCacheKey(request.getBizId());
        if (redisClientOfJy.exists(pdaOpeCacheKey)) {

            redisClientOfJy.hIncrBy(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_COUNT, pdaUnloadCount);
            redisClientOfJy.expire(pdaOpeCacheKey, UNLOAD_CACHE_EXPIRE, TimeUnit.HOURS);
            // 如果是本场地多扫
            if (Constants.MORE_LOCAL_SCAN.equals(unloadScanDto.getMoreFlag())) {
                redisClientOfJy.hIncrBy(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_MORE_SCAN_LOCAL_COUNT, pdaUnloadCount);
                // 如果是本场地部分多扫
            } else if (Constants.MORE_LOCAL_PART_SCAN.equals(unloadScanDto.getMoreFlag())) {
                redisClientOfJy.hIncrBy(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_MORE_SCAN_LOCAL_COUNT, unloadScanDto.getMoreLocalPartCount());
                // 如果是非本场地多扫
            } else if (Constants.MORE_OUT_SCAN.equals(unloadScanDto.getMoreFlag())) {
                redisClientOfJy.hIncrBy(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_MORE_SCAN_OUT_COUNT, pdaUnloadCount);
            }
            logInfo("更新PDA本地卸车扫描进度. {}-{}", JsonHelper.toJson(request), pdaUnloadCount);
        }
        else {
            // 长时间未扫描，本地扫描记录过期，以agg为准
            JyUnloadAggsEntity unloadAggEntity = sumUnloadAgg(request.getBizId());
            Integer unloadCount = calculateRightUnloadCount(pdaUnloadCount, unloadAggEntity);

            Map<String, String> redisHashMap = new HashMap<>();
            redisHashMap.put(RedisHashKeyConstants.UNLOAD_TOTAL_COUNT, String.valueOf(taskUnloadVehicle.getTotalCount()));
            redisHashMap.put(RedisHashKeyConstants.UNLOAD_COUNT, String.valueOf(unloadCount));
            // 如果是本场地多扫
            if (Constants.MORE_LOCAL_SCAN.equals(unloadScanDto.getMoreFlag())) {
                redisHashMap.put(RedisHashKeyConstants.UNLOAD_MORE_SCAN_LOCAL_COUNT, String.valueOf(pdaUnloadCount));
                // 如果是本场地部分多扫
            } else if (Constants.MORE_LOCAL_PART_SCAN.equals(unloadScanDto.getMoreFlag())) {
                redisHashMap.put(RedisHashKeyConstants.UNLOAD_MORE_SCAN_LOCAL_COUNT, String.valueOf(unloadScanDto.getMoreLocalPartCount()));
                // 如果是非本场地多扫
            }  else if (Constants.MORE_OUT_SCAN.equals(unloadScanDto.getMoreFlag())) {
                redisHashMap.put(RedisHashKeyConstants.UNLOAD_MORE_SCAN_OUT_COUNT, String.valueOf(pdaUnloadCount));
            }
            redisClientOfJy.hMSet(pdaOpeCacheKey, redisHashMap);
            redisClientOfJy.expire(pdaOpeCacheKey, UNLOAD_CACHE_EXPIRE, TimeUnit.HOURS);

            logInfo("初始化PDA本地卸车扫描进度. {}-{}", JsonHelper.toJson(request), pdaUnloadCount);
        }
    }

    /**
     * 统计卸车进度
     * @param bizId
     * @return
     */
    private JyUnloadAggsEntity sumUnloadAgg(String bizId) {
        List<JyUnloadAggsEntity> aggList = jyUnloadAggsService.queryByBizId(new JyUnloadAggsEntity(bizId));
        if (CollectionUtils.isEmpty(aggList)) {
            log.warn("卸车任务[{}]不存在进度数据.", bizId);
            return null;
        }

        JyUnloadAggsEntity retAgg = new JyUnloadAggsEntity();
        retAgg.setBizId(bizId);
        retAgg.setTotalScannedPackageCount(aggList.get(0).getTotalScannedPackageCount());
        retAgg.setTotalSealPackageCount(aggList.get(0).getTotalSealPackageCount());

        int interceptShouldScanCount = 0;
        int interceptActualScanCount = 0;
        int moreScanLocalCount = 0;
        int moreScanOutCount = 0;
        for (JyUnloadAggsEntity aggEntity : aggList) {
            interceptShouldScanCount += aggEntity.getInterceptShouldScanCount();
            interceptActualScanCount += aggEntity.getInterceptActualScanCount();
            moreScanLocalCount += aggEntity.getMoreScanLocalCount();
            moreScanOutCount += aggEntity.getMoreScanOutCount();
        }
        retAgg.setInterceptShouldScanCount(interceptShouldScanCount);
        retAgg.setInterceptActualScanCount(interceptActualScanCount);
        retAgg.setMoreScanLocalCount(moreScanLocalCount);
        retAgg.setMoreScanOutCount(moreScanOutCount);

        return retAgg;
    }

    /**
     * 比较PDA本地卸车进度和卸车统计的进度大小。
     * @param pdaUnloadCount
     * @param unloadAggEntity
     * @return
     */
    private Integer calculateRightUnloadCount(Integer pdaUnloadCount, JyUnloadAggsEntity unloadAggEntity) {
        Integer unloadCount = pdaUnloadCount;
        if (unloadAggEntity != null && NumberHelper.gt0(unloadAggEntity.getTotalScannedPackageCount())) {
            if (NumberHelper.gt(unloadAggEntity.getTotalScannedPackageCount(), unloadCount)) {
                unloadCount = unloadAggEntity.getTotalScannedPackageCount();

                logInfo("卸车已扫描数量从jy_unload_agg获取. {}", JsonHelper.toJson(unloadAggEntity));
            }
            else {
                logInfo("根据PDA更新卸车扫描进度成功. {}", JsonHelper.toJson(unloadAggEntity));
            }
        }

        return unloadCount;
    }

    /**
     * 扫描前校验
     * @param result JdVerifyResponse对象，用于存储校验结果
     * @param request UnloadScanRequest对象，表示卸货扫描请求
     * @return 若满足条件则返回true，否则返回false
     */
    private boolean checkBeforeScan(JdVerifyResponse<UnLoadScanResponse> result, UnloadScanRequest request) {
        //回调
        unloadScanCheckOfCallback(result, request);
        if (!result.codeSuccess()) {
            return false;
        }
        // 一个单号只能扫描一次
        if (checkBarScannedAlready(request)) {
            result.toCustomError(InvokeResult.CODE_HINT, "单号已扫描！");
            return false;
        }

        return true;
    }

    /**
     * 卸车校验回调
     * @param result
     * @param request
     * @return
     */
    private void unloadScanCheckOfCallback(JdVerifyResponse<UnLoadScanResponse> result,UnloadScanRequest request){
        //需要判断当非拣运租户时在触发回调
        String tenantCode = TenantContext.getTenantCode();
        if(StringUtils.isNotBlank(tenantCode) && !TenantEnum.TENANT_JY.getCode().equals(tenantCode)) {
            String barCode = request.getBarCode();
            InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> callbackResult = jyCallbackJsfManager.unloadScanCheckOfCallback(transferDto(request));
            //返回 code 非成功时需要阻断服务，不运行继续执行
            if (!callbackResult.isSuccess()) {
                //返回个性服务标识
                result.setSelfDomFlag(Boolean.TRUE);
                result.setCode(callbackResult.getCode());
                result.setMessage(callbackResult.getMessage());
                //提示类的提示语不放在这个，放在执行回调,这里保留代码。实际不返回
                result.addBox(SdkConvertAndroidUtil.convertMsg(callbackResult.getMsgBoxes(),Boolean.TRUE));
            }
        }
    }

    /**
     * 卸车扫描回调
     * @param result JdVerifyResponse对象，包含UnLoadScanResponse类型的响应
     * @param request UnloadScanRequest对象，卸载扫描请求
     * @return 无
     */
    private void unloadScanOfCallback(JdVerifyResponse<UnLoadScanResponse> result,UnloadScanRequest request){
        //需要判断当非拣运租户时在触发回调
        String tenantCode = TenantContext.getTenantCode();
        if (StringUtils.isNotBlank(tenantCode) && !TenantEnum.TENANT_JY.getCode().equals(tenantCode)) {
            String barCode = request.getBarCode();
            InvokeWithMsgBoxResult<UnloadScanCallbackRespDto> callbackResult = jyCallbackJsfManager.unloadScanOfCallback(transferDto(request));
            //返回 code 成功继续执行,不成功时不要阻断，不处理
            if (callbackResult.isSuccess()) {
                //返回个性服务标识
                result.setSelfDomFlag(Boolean.TRUE);
                result.setCode(callbackResult.getCode());
                result.setMessage(callbackResult.getMessage());
                result.addBox(SdkConvertAndroidUtil.convertMsg(callbackResult.getMsgBoxes(), Boolean.TRUE));
                //data暂时没有
                //result.setData(callbackResult.getData());
            }
        }
    }
    /**
     * 将UnloadScanRequest转换为UnloadScanCallbackReqDto
     * @param request 载入扫描请求对象
     * @return callbackReqDto 卸载扫描回调请求对象
     * @throws NullPointerException 如果request为空时
     */
    private UnloadScanCallbackReqDto transferDto(UnloadScanRequest request){
        UnloadScanCallbackReqDto callbackReqDto = new UnloadScanCallbackReqDto();
        callbackReqDto.setBarCode(request.getBarCode());
        callbackReqDto.setScanType(request.getScanType());
        callbackReqDto.setForceSubmit(request.getForceSubmit());
        callbackReqDto.setSiteCode(request.getCurrentOperate().getSiteCode());
        callbackReqDto.setSiteName(request.getCurrentOperate().getSiteName());
        if (request.getUser() != null) {
            callbackReqDto.setUserCode(request.getUser().getUserCode());
            callbackReqDto.setUserName(request.getUser().getUserName());
        }
        callbackReqDto.setOperateTime(new Date());
        return callbackReqDto;
    }

    /**
     * 统计本次扫描的包裹数量
     * @param request
     */
    private Integer calculateScanPackageCount(UnloadScanRequest request) {
        String barCode = request.getBarCode();
        if(Objects.equals(UnloadScanTypeEnum.SCAN_WAYBILL.getCode(), request.getScanType())){
            barCode = WaybillUtil.getWaybillCode(request.getBarCode());
        }
        Integer scanCount = 0;
        if (WaybillUtil.isPackageCode(barCode)) {
            scanCount = 1;
        }
        else if (WaybillUtil.isWaybillCode(barCode)) {
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(barCode);
            if (waybill != null && NumberHelper.gt0(waybill.getGoodNumber())) {
                scanCount = waybill.getGoodNumber();
            }
        }
        else if (BusinessHelper.isBoxcode(barCode)) {
            CallerInfo inlineUmp = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleService.unloadScan.getCancelSendByBox");
            List<SendDetail> list = deliveryService.getCancelSendByBox(barCode);
            Profiler.registerInfoEnd(inlineUmp);
            if (CollectionUtils.isNotEmpty(list)) {
                scanCount = list.size();
            }
        }

        return scanCount;
    }

    private void calculateScanPackageCount(UnloadScanRequest request , JdVerifyResponse<UnLoadScanResponse> result, UnloadScanContextDto unloadScanContextDto) {
        UnLoadScanResponse dataResult = result.getData() == null? new UnLoadScanResponse() : result.getData();

        String barCode = request.getBarCode();
        if(Objects.equals(UnloadScanTypeEnum.SCAN_WAYBILL.getCode(), request.getScanType())){
            barCode = WaybillUtil.getWaybillCode(request.getBarCode());
        }

        Waybill waybill = null;
        Integer scanCount = 0;
        if (WaybillUtil.isPackageCode(barCode)) {
            waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCode(barCode));
            scanCount = 1;
            //运单包裹数
            if (waybill != null && NumberHelper.gt0(waybill.getGoodNumber())) {
                dataResult.setScanWaybillPackSum(waybill.getGoodNumber());
            }
        }
        else if (WaybillUtil.isWaybillCode(barCode)) {
            waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(barCode);
            if (waybill != null && NumberHelper.gt0(waybill.getGoodNumber())) {
                scanCount = waybill.getGoodNumber();
                //运单包裹数
                dataResult.setScanWaybillPackSum(waybill.getGoodNumber());
            }
        }
        else if (BusinessHelper.isBoxcode(barCode)) {
            CallerInfo inlineUmp = ProfilerHelper.registerInfo("dms.web.IJyUnloadVehicleService.unloadScan.getCancelSendByBox");
            List<SendDetail> list = deliveryService.getCancelSendByBox(barCode);
            Profiler.registerInfoEnd(inlineUmp);
            if (CollectionUtils.isNotEmpty(list)) {
                scanCount = list.size();
            }
        }
        unloadScanContextDto.setWaybill(waybill);
        dataResult.setScanPackCount(scanCount);
        result.setData(dataResult);

        //春节项目的特殊逻辑
        if (WaybillUtil.isPackageCode(barCode) || WaybillUtil.isWaybillCode(barCode)) {
            if (waybill != null) {
                ConfigTransferDpSite configTransferDpSite = jyTransferConfigProxy.queryMatchConditionRecord(request.getCurrentOperate().getSiteCode(), waybill.getOldSiteId());
                if (jyTransferConfigProxy.isMatchConfig(configTransferDpSite, waybill.getWaybillSign())) {
                    result.setCode(InvokeResult.DP_SPECIAL_CODE);
                    result.setMessage(MessageFormat.format(InvokeResult.DP_SPECIAL_HINT_MESSAGE, barCode));
                }
            }
        }


    }

    private UnloadScanDto createUnloadDto(UnloadScanRequest request, JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
        Date operateTime = new Date();
        UnloadScanDto unloadScanDto = new UnloadScanDto();
        unloadScanDto.setBizId(request.getBizId());
        // 无任务场景下没有sealCarCode
        unloadScanDto.setSealCarCode(StringUtils.isBlank(request.getSealCarCode()) ? StringUtils.EMPTY : request.getSealCarCode());
        unloadScanDto.setVehicleNumber(taskUnloadVehicle.getVehicleNumber());
        unloadScanDto.setStartSiteId(taskUnloadVehicle.getStartSiteId());
        unloadScanDto.setEndSiteId(taskUnloadVehicle.getEndSiteId());
        unloadScanDto.setManualCreatedFlag(taskUnloadVehicle.getManualCreatedFlag());
        unloadScanDto.setOperateSiteId((long) request.getCurrentOperate().getSiteCode());
        unloadScanDto.setBarCode(request.getBarCode());
        if(Objects.equals(UnloadScanTypeEnum.SCAN_WAYBILL.getCode(), request.getScanType())){
            unloadScanDto.setBarCode(WaybillUtil.getWaybillCode(request.getBarCode()));
        }
        unloadScanDto.setOperateTime(operateTime);
        unloadScanDto.setCreateUserId(request.getUser().getUserCode());
        unloadScanDto.setCreateUserErp(request.getUser().getUserErp());
        unloadScanDto.setCreateUserName(request.getUser().getUserName());
        unloadScanDto.setUpdateUserErp(request.getUser().getUserErp());
        unloadScanDto.setUpdateUserName(request.getUser().getUserName());
        unloadScanDto.setCreateTime(operateTime);
        unloadScanDto.setUpdateTime(operateTime);

        unloadScanDto.setGroupCode(request.getGroupCode());
        unloadScanDto.setTaskId(request.getTaskId());
        unloadScanDto.setOperatorData(BeanConverter.convertToOperatorData(request.getCurrentOperate()));
        unloadScanDto.setMoreFlag(Constants.NUMBER_ZERO);
        return unloadScanDto;
    }

    /**
     * 校验卸车是否已经扫描过该单号，同一个任务只能扫描一次
     * @return true：扫描过
     */
    private boolean checkBarScannedAlready(UnloadScanRequest request) {
        String barCode = request.getBarCode();
        int siteCode = request.getCurrentOperate().getSiteCode();
        boolean alreadyScanned = false;
        if(Objects.equals(UnloadScanTypeEnum.SCAN_WAYBILL.getCode(), request.getScanType())){
            barCode = WaybillUtil.getWaybillCode(request.getBarCode());
        }
        String mutexKey = getBizBarCodeCacheKey(barCode, siteCode, request.getBizId());
        if (redisClientOfJy.set(mutexKey, String.valueOf(System.currentTimeMillis()), UNLOAD_SCAN_BAR_EXPIRE, TimeUnit.HOURS, false)) {
            JyUnloadEntity queryDb = new JyUnloadEntity(barCode, (long) siteCode, request.getBizId());
            if (jyUnloadDao.queryByCodeAndSite(queryDb) != null) {
                alreadyScanned = true;
            }
        }
        else {
            alreadyScanned = true;
        }

        return alreadyScanned;
    }

    private String getBizBarCodeCacheKey(String barCode, int siteCode, String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_SCAN_KEY, barCode, siteCode, bizId);
    }

    private void handleSpecialProductType(UnloadScanRequest request, JdVerifyResponse<UnLoadScanResponse> result, UnloadScanContextDto unloadScanContextDto){
        try {
            final BarCodeType barCodeType = BusinessUtil.getBarCodeType(request.getBarCode());
            if (!Objects.equals(barCodeType, BarCodeType.WAYBILL_CODE) && !Objects.equals(barCodeType, BarCodeType.PACKAGE_CODE)) {
                return;
            }
            final Waybill waybill = unloadScanContextDto.getWaybill();
            if (waybill == null) {
                return;
            }
            // 判断产品类型
            // 1. 航空件类型
            if(BusinessUtil.isAirLineMode(waybill.getWaybillSign())){
                result.addPromptBox(InvokeResult.CODE_AIR_TRANSPORT, "航空件");
            }
            // 2. 生鲜特保类型
            if (!BusinessUtil.hasWaybillVas(waybill.getWaybillSign())) {
                return;
            }
            // 无增值服务不需要调接口查询
            final Result<Boolean> checkWaybillVasResult = waybillCommonService.checkWaybillVas(waybill.getWaybillCode(), WaybillVasEnum.WAYBILL_VAS_SPECIAL_SAFEGUARD_COLD_FRESH_OPERATION);
            if(!checkWaybillVasResult.isSuccess()){
                return;
            }
            if(checkWaybillVasResult.getData()){
                result.addPromptBox(InvokeResult.CODE_FRESH_SPECIAL, "生鲜特保件");
                return;
            }
        } catch (Exception e) {
            log.error("handleSpecialProductType exception {}", JsonHelper.toJson(request), e);
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.unloadDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<UnloadScanDetail> unloadDetail(UnloadCommonRequest request) {
        InvokeResult<UnloadScanDetail> result = new InvokeResult<>();
        if (StringUtils.isBlank(request.getBizId())) {
            result.parameterError("加载卸车进度缺少任务编码!");
            return result;
        }

        try {
            // 暂时去掉缓存读数据库。需要在接收agg时写入缓存。
            Map<String, String> hashRedisMap = Maps.newHashMap();

            if (MapUtils.isEmpty(hashRedisMap)) {
                log.warn("卸车进度缓存不存在主动刷新. {}", JsonHelper.toJson(request));
                refreshUnloadAggCache(request.getBizId());
            }

            String progressCacheKey = genUnloadProcessCacheKey(request.getBizId());
            hashRedisMap = redisClientOfJy.hGetAll(progressCacheKey);

            UnloadDetailCache redisCache = RedisHashUtils.mapConvertBean(hashRedisMap, UnloadDetailCache.class);

            UnloadScanDetail scanProgress = JsonHelper.fromJson(JsonHelper.toJson(redisCache), UnloadScanDetail.class);
            // 增加刷新间隔配置
            if (scanProgress != null) {
                scanProgress.setClientAutoRefreshConfig(dmsConfigManager.getPropertyConfig().getJyWorkAppAutoRefreshConfigByBusinessType(ClientAutoRefreshBusinessTypeEnum.UNLOAD_PROGRESS.name()));
            }
            result.setData(scanProgress);
            if(jyDemotionService.checkIsDemotion(JyConstants.JY_FLINK_UNLOAD_IS_DEMOTION)){
                throw new JyDemotionException("卸车进度不准，flink降级!");
            }
        }
        catch (JyDemotionException e){
            result.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_UNLOAD_PROCESS_NOT_ACCURATE, false));
        }
        catch (Exception ex) {
            log.error("加载卸车进度异常. {}", JsonHelper.toJson(request), ex);
            result.error("加载卸车进度异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    /**
     * 创建卸车任务 解封车操作成功使用
     *
     * @param dto
     * @return
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.createUnloadTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public JyBizTaskUnloadDto createUnloadTask(JyBizTaskUnloadDto dto){
        //初始生成业务任务实体
        JyBizTaskUnloadVehicleEntity taskUnloadVehicleEntity = null;
        //创建调度任务实体
        JyBizTaskUnloadDto createScheduleTask = new JyBizTaskUnloadDto();
        BeanCopyUtil.copy(dto,createScheduleTask);

        if(dto.getManualCreatedFlag() != null && Integer.valueOf(1).equals(dto.getManualCreatedFlag())){
            //无任务模式
            //计算生成BIZ_ID
            taskUnloadVehicleEntity = jyBizTaskUnloadVehicleService.initTaskByNoTask(dto);
            if(taskUnloadVehicleEntity == null){
                //初始化失败
                throw new JyBizException(String.format("初始业务任务基础数据异常！无任务模式 车牌:%s",dto.getVehicleNumber()));
            }
            //无任务模式补充BIZ_ID
            createScheduleTask.setBizId(taskUnloadVehicleEntity.getBizId());
        }else{
            //防止上线初期运输数据未全部接入时 增加补数逻辑
            Long id = jyBizTaskUnloadVehicleService.findIdByBizId(dto.getBizId());
            if(id == null || id <=0 ){
                taskUnloadVehicleEntity = jyBizTaskUnloadVehicleService.initTaskByTms(dto.getSealCarCode());
                if(taskUnloadVehicleEntity == null){
                    throw new JyBizException(String.format("初始业务任务基础数据异常！bizId:%s",dto.getBizId()));
                }
            }

            // 处理只卸不装属性

            //更新状态
            JyBizTaskUnloadVehicleEntity changeStatusParam = new JyBizTaskUnloadVehicleEntity();
            changeStatusParam.setBizId(dto.getBizId());
            changeStatusParam.setUpdateUserErp(dto.getOperateUserErp());
            changeStatusParam.setUpdateUserName(dto.getOperateUserName());
            changeStatusParam.setUpdateTime(dto.getOperateTime());
            changeStatusParam.setVehicleStatus(JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode());
            if(!jyBizTaskUnloadVehicleService.changeStatus(changeStatusParam)){
                throw new JyBizException(String.format("更新任务状态异常！bizId:%s",dto.getBizId()));
            }
            //关闭原解封车任务
            if(!closeScheduleTask(dto,JyScheduleTaskTypeEnum.UNSEAL)){
                throw new JyBizException(String.format("关闭原解封车调度任务失败！bizId:%s",dto.getBizId()));
            }

        }

        //创建卸车任务
        JyScheduleTaskResp scheduleTaskResp =  createUnLoadScheduleTask(createScheduleTask);
        boolean createFlag = scheduleTaskResp != null;
        if(!createFlag){
            throw new JyBizException(String.format("创建新卸车调度任务失败！bizId:%s",createScheduleTask.getBizId()));
        }
        JyBizTaskUnloadDto result = new JyBizTaskUnloadDto();
        result.setTaskId(scheduleTaskResp.getTaskId());
        result.setBizId(createScheduleTask.getBizId());
        result.setVehicleNumber(createScheduleTask.getVehicleNumber());
        return result;
    }

    private void handleOnlyUnloadNotLoad(JyBizTaskUnloadDto dto, JyBizTaskUnloadVehicleEntity jyBizTaskUnloadVehicle) {
        BaseStaffSiteOrgDto operateSite = baseMajorManager.getBaseSiteBySiteId(dto.getOperateSiteId());
        StopoverQueryDto stopoverQueryDto = new StopoverQueryDto();
        stopoverQueryDto.setSiteCode(operateSite == null ? null : operateSite.getDmsSiteCode());
        // stopoverQueryDto.setTransWorkCode(transWorkCode);
        stopoverQueryDto.setSealCarCode(dto.getSealCarCode());
        // stopoverQueryDto.setSimpleCode(simpleCode);
        stopoverQueryDto.setVehicleNumber(dto.getVehicleNumber());
        StopoverInfoDto stopoverInfoDto = sealCarManager.queryStopoverInfo(stopoverQueryDto);
        if(stopoverInfoDto == null){
        }
        // 站点类型1:始发;2:经停;3:目的
        // 站点类型=经停 且 装车计数＞0  且 卸车计数=0
        if(Objects.equals(stopoverInfoDto.getSiteType(), 2)
                && NumberHelper.isPositiveNumber(stopoverInfoDto.getLoadCount())
                && Objects.equals(stopoverInfoDto.getUnloadCount(), 0)){
            // return ImmutablePair.of(JdExtraMessageResponse.CODE_HINT, "途径本场地只装不卸，请先操作无任务解封签!");
        }
        // 站点类型=经停  且 装车计数=0  且 卸车计数＞0
        if(Objects.equals(stopoverInfoDto.getSiteType(), 2)
                && Objects.equals(stopoverInfoDto.getLoadCount(), 0)
                && NumberHelper.isPositiveNumber(stopoverInfoDto.getUnloadCount())){
            // return ImmutablePair.of(JdExtraMessageResponse.CODE_HINT, "途径本场地只卸不装，卸车完成后请操作无货上封签!");
        }
    }

    /**
     * 关闭调度任务
     * @param dto
     * @param typeEnum
     * @return
     */
    private boolean closeScheduleTask(JyBizTaskUnloadDto dto,JyScheduleTaskTypeEnum typeEnum){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(dto.getBizId());
        req.setTaskType(String.valueOf(typeEnum.getCode()));
        req.setOpeUser(dto.getOperateUserErp());
        req.setOpeUserName(dto.getOperateUserName());
        req.setOpeTime(dto.getOperateTime());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.closeScheduleTask(req);
        return jyScheduleTaskResp != null;
    }

    /**
     * 创建卸车专用调度任务
     * @param dto
     * @return
     */
    private JyScheduleTaskResp createUnLoadScheduleTask(JyBizTaskUnloadDto dto){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(dto.getBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.UNLOAD.getCode());
        req.setOpeUser(dto.getOperateUserErp());
        req.setOpeUserName(dto.getOperateUserName());
        req.setOpeTime(dto.getOperateTime());
        return jyScheduleTaskManager.createScheduleTask(req);
    }

    /**
     * 根据卸车业务主键更新卸车进度缓存
     * @param bizId
     * @return
     */
    public Boolean refreshUnloadAggCache(String bizId) {
        JyUnloadAggsEntity unloadAggEntity = sumUnloadAgg(bizId);

        boolean unloadAggSumExist = true;
        boolean pdaUnloadProgressExist = true;

        if (null == unloadAggEntity) {
            unloadAggSumExist = false;
        }

        // 比较PDA扫描的进度和Flink计算出的进度
        String pdaOpeCacheKey = genPdaUnloadProgressCacheKey(bizId);
        if (redisClientOfJy.exists(pdaOpeCacheKey)) {

            if (!unloadAggSumExist) {
                unloadAggEntity = new JyUnloadAggsEntity();
                unloadAggEntity.setBizId(bizId);
            }

            String redisVal = redisClientOfJy.hGet(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_COUNT);
            if (StringUtils.isNotBlank(redisVal) && NumberHelper.isNumber(redisVal)) {
                Integer pdaScannedPackageCount = Integer.valueOf(redisVal);
                // PDA进度快以PDA为准
                Integer rightUnloadCount = calculateRightUnloadCount(pdaScannedPackageCount, unloadAggEntity);

                unloadAggEntity.setTotalScannedPackageCount(rightUnloadCount);
            }
            // 本场地多扫：比较redis与数据库的值，取最大者
            String moreScanLocalCount = redisClientOfJy.hGet(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_MORE_SCAN_LOCAL_COUNT);
            if (StringUtils.isNotBlank(moreScanLocalCount) && NumberHelper.isNumber(moreScanLocalCount)) {
                Integer rightMoreScanLocalCount = Integer.valueOf(moreScanLocalCount);
                // PDA进度快以PDA为准
                if (unloadAggEntity.getMoreScanLocalCount() != null && NumberHelper.gt(unloadAggEntity.getMoreScanLocalCount(), rightMoreScanLocalCount)) {
                    rightMoreScanLocalCount = unloadAggEntity.getMoreScanLocalCount();
                }
                unloadAggEntity.setMoreScanLocalCount(rightMoreScanLocalCount);
            }
            // 非本场地多扫：比较redis与数据库的值，取最大者
            String moreScanOutCount = redisClientOfJy.hGet(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_MORE_SCAN_OUT_COUNT);
            if (StringUtils.isNotBlank(moreScanOutCount) && NumberHelper.isNumber(moreScanOutCount)) {
                Integer rightMoreScanOutCount = Integer.valueOf(moreScanOutCount);
                // PDA进度快以PDA为准
                if (unloadAggEntity.getMoreScanOutCount() != null && NumberHelper.gt(unloadAggEntity.getMoreScanOutCount(), rightMoreScanOutCount)) {
                    rightMoreScanOutCount = unloadAggEntity.getMoreScanOutCount();
                }
                unloadAggEntity.setMoreScanOutCount(rightMoreScanOutCount);
            }
        }
        else {
            pdaUnloadProgressExist = false;
        }

        if (unloadAggSumExist || pdaUnloadProgressExist) {
            return initScanDetailCacheUsingUnloadAgg(unloadAggEntity);
        }
        return false;

    }

    /**
     * 根据unload_agg数据初始化卸车进度
     * @param unloadAggEntity
     * @return
     */
    private boolean initScanDetailCacheUsingUnloadAgg(JyUnloadAggsEntity unloadAggEntity) {
        if (null == unloadAggEntity) {
            return true;
        }

        try {
            logInfo("init unload progress from unload agg. {}", JsonHelper.toJson(unloadAggEntity));

            UnloadDetailCache cacheEntity = unloadAggToCacheDomain(unloadAggEntity);
            Map<String, String> redisHashMap = RedisHashUtils.objConvertToMap(cacheEntity);
            if (MapUtils.isEmpty(redisHashMap)) {
                log.warn("unload entity to redis hashMap error. {}", JsonHelper.toJson(unloadAggEntity));
                return false;
            }
            String unloadDetailCacheKey = genUnloadProcessCacheKey(unloadAggEntity.getBizId());
            redisClientOfJy.hMSet(unloadDetailCacheKey, redisHashMap);
            redisClientOfJy.expire(unloadDetailCacheKey, UNLOAD_CACHE_EXPIRE, TimeUnit.HOURS);
        }
        catch (Exception ex) {
            log.error("初始化卸车进度缓存异常. {}", JsonHelper.toJson(unloadAggEntity), ex);
            return false;
        }

        return true;
    }

    private UnloadDetailCache unloadAggToCacheDomain(JyUnloadAggsEntity unloadAggEntity) {
        UnloadDetailCache cacheEntity = new UnloadDetailCache();
        cacheEntity.setBizId(unloadAggEntity.getBizId());
        cacheEntity.setInterceptShouldScanCount(unloadAggEntity.getInterceptShouldScanCount());
        cacheEntity.setInterceptActualScanCount(unloadAggEntity.getInterceptActualScanCount());
        cacheEntity.setMoreScanLocalCount(unloadAggEntity.getMoreScanLocalCount());
        cacheEntity.setMoreScanOutCount(unloadAggEntity.getMoreScanOutCount());
        cacheEntity.setTotalCount(unloadAggEntity.getTotalSealPackageCount());
        cacheEntity.setUnloadCount(unloadAggEntity.getTotalScannedPackageCount());
        return cacheEntity;
    }

    /**
     * 卸车进度缓存
     * @param bizId
     * @return
     */
    private String genUnloadProcessCacheKey(String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_PROCESS_KEY, bizId);
    }

    /**
     * PDA本地扫描进度缓存
     * @param bizId
     * @return
     */
    private String genPdaUnloadProgressCacheKey(String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_PDA_PROCESS_KEY, bizId);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.unloadGoodsDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<List<UnloadScanAggByProductType>> unloadGoodsDetail(UnloadGoodsRequest request) {
        InvokeResult<List<UnloadScanAggByProductType>> result = new InvokeResult<>();
        if (StringUtils.isBlank(request.getBizId())) {
            result.parameterError("请选择卸车任务！");
            return result;
        }

        try {
            List<UnloadScanAggByProductType> productTypeList = Lists.newArrayList();
            result.setData(productTypeList);
            List<JyUnloadAggsEntity> unloadAggList = jyUnloadAggsService.queryByBizId(new JyUnloadAggsEntity(request.getBizId()));
            if (CollectionUtils.isEmpty(unloadAggList)) {
                return result;
            }

            convertAggEntityToPage(productTypeList, unloadAggList);
        }
        catch (Exception ex) {
            log.error("查询货物明细服务器异常. {}", JsonHelper.toJson(request), ex);
            result.error("查询货物明细服务器异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    private void convertAggEntityToPage(List<UnloadScanAggByProductType> productTypeList, List<JyUnloadAggsEntity> unloadAggList) {


        for (JyUnloadAggsEntity aggEntity : unloadAggList) {
            UnloadScanAggByProductType item = new UnloadScanAggByProductType();
            item.setProductType(aggEntity.getProductType());
            item.setProductTypeName(UnloadProductTypeEnum.getNameByCode(item.getProductType()));
            item.setShouldScanCount(aggEntity.getShouldScanCount().longValue());
            item.setActualScanCount(aggEntity.getActualScanCount().longValue());
            productTypeList.add(item);
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.toScanAggByProduct",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<List<ProductTypeAgg>> toScanAggByProduct(UnloadCommonRequest request) {
        InvokeResult<List<ProductTypeAgg>> result = new InvokeResult<>();
        if (StringUtils.isBlank(request.getBizId())) {
            result.parameterError("请选择卸车任务！");
            return result;
        }

        try {
            List<ProductTypeAgg> productTypeList = Lists.newArrayList();
            result.setData(productTypeList);
            List<JyUnloadAggsEntity> unloadAggList = jyUnloadAggsService.queryByBizId(new JyUnloadAggsEntity(request.getBizId()));
            if (CollectionUtils.isEmpty(unloadAggList)) {
                return result;
            }

            makeToScanCountAggByProduct(productTypeList, unloadAggList);
        }
        catch (Exception ex) {
            log.error("按产品类型统计待扫包裹总数异常. {}", JsonHelper.toJson(request), ex);
            result.error("查询待扫包裹数据服务器异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    private void makeToScanCountAggByProduct(List<ProductTypeAgg> productTypeList, List<JyUnloadAggsEntity> unloadAggList) {
        for (JyUnloadAggsEntity aggEntity : unloadAggList) {
            ProductTypeAgg item = new ProductTypeAgg();
            item.setProductType(aggEntity.getProductType());
            item.setProductTypeName(UnloadProductTypeEnum.getNameByCode(item.getProductType()));
            item.setOrder(UnloadProductTypeEnum.getOrderByCode(item.getProductType()));
            item.setCount(dealMinus(aggEntity.getShouldScanCount(), aggEntity.getActualScanCount()));
            productTypeList.add(item);
        }
        Collections.sort(productTypeList,new ProductTypeAgg.OrderComparator());
    }

    private Long dealMinus(Number a, Number b) {
        return NumberHelper.gt(a, b) ? a.longValue() - b.longValue() : 0L;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.toScanBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<ToScanDetailByProductType> toScanBarCodeDetail(UnloadProductTypeRequest request) {
        InvokeResult<ToScanDetailByProductType> result = new InvokeResult<>();
        if (StringUtils.isBlank(request.getBizId())
                || StringUtils.isBlank(request.getProductType())
                || !NumberHelper.gt0(request.getCurrentOperate().getSiteCode())
                || !NumberHelper.gt0(request.getPageNumber())
                || !NumberHelper.gt0(request.getPageSize())) {
            result.parameterError();
            return result;
        }

        try {
            Pager<JyVehicleTaskUnloadDetail> pager = getToScanBarCodeCondition(request);
            Pager<JyVehicleTaskUnloadDetail> retPager = unloadVehicleManager.queryToScanBarCodeDetail(pager);
            if (retPager == null || CollectionUtils.isEmpty(retPager.getData())) {
                return result;
            }

            ToScanDetailByProductType toScanDetailByProductType = setToScanBarCodeList(request, retPager);
            result.setData(toScanDetailByProductType);
        }
        catch (JyDemotionException e){
            result.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_UNLOAD_TOSCAN, false));
        }
        catch (Exception ex) {
            log.error("按产品类型查询待扫包裹数据异常. {}", JsonHelper.toJson(request), ex);
            result.error("按产品类型查询待扫包裹数据，请咚咚联系分拣小秘！");
        }

        return result;
    }

    /**
     * 待扫包裹明细，按产品类型统计
     * @param request
     * @param retPager
     * @return
     */
    private ToScanDetailByProductType setToScanBarCodeList(UnloadProductTypeRequest request, Pager<JyVehicleTaskUnloadDetail> retPager) {
        ToScanDetailByProductType toScanDetailByProductType = new ToScanDetailByProductType();
        toScanDetailByProductType.setBarCodeCount(retPager.getTotal());
        toScanDetailByProductType.setProductType(request.getProductType());
        toScanDetailByProductType.setProductTypeName(UnloadProductTypeEnum.getNameByCode(request.getProductType()));

        toScanDetailByProductType.setBarCodeList(getUnloadScanBarCodeList(UnloadBarCodeQueryEntranceEnum.TO_SCAN, retPager.getData()));

        return toScanDetailByProductType;
    }

    /**
     * 卸车待扫包裹查询条件
     * @param request
     * @return
     */
    private Pager<JyVehicleTaskUnloadDetail> getToScanBarCodeCondition(UnloadProductTypeRequest request) {
        Pager<JyVehicleTaskUnloadDetail> pager = new Pager<>();
        pager.setPageNo(request.getPageNumber());
        pager.setPageSize(request.getPageSize());

        JyVehicleTaskUnloadDetail searchVo = new JyVehicleTaskUnloadDetail();
        pager.setSearchVo(searchVo);

        searchVo.setEndSiteId(request.getCurrentOperate().getSiteCode());
        searchVo.setBizId(request.getBizId());
        searchVo.setSealCarCode(request.getSealCarCode());
        searchVo.setProductType(request.getProductType());
        searchVo.setScannedFlag(Constants.NUMBER_ZERO); // 待扫
        return pager;
    }

    /**
     * 解析扫描类型。待扫、多扫 etc.
     * @param unloadDetail
     * @return
     */
    private UnloadBarCodeScanTypeEnum resolveScanTypeDesc(JyVehicleTaskUnloadDetail unloadDetail) {
        if (NumberHelper.gt0(unloadDetail.getMoreScanFlag())) {
            if (NumberHelper.gt0(unloadDetail.getLocalSiteFlag())) {
                return UnloadBarCodeScanTypeEnum.LOCAL_MORE_SCAN;
            }
            else {
                return UnloadBarCodeScanTypeEnum.OUT_MORE_SCAN;
            }
        }
        else {
            if (!NumberHelper.gt0(unloadDetail.getScannedFlag())) {
                return UnloadBarCodeScanTypeEnum.TO_SCAN;
            }
            else {
                return UnloadBarCodeScanTypeEnum.SCANNED;
            }
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.interceptBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<InterceptScanBarCode> interceptBarCodeDetail(UnloadCommonRequest request) {
        InvokeResult<InterceptScanBarCode> result = new InvokeResult<>();

        if (!checkPageQueryParam(request, result)) {
            return result;
        }

        try {
            Pager<JyVehicleTaskUnloadDetail> pager = getInterceptBarCodeCondition(request);
            Pager<JyVehicleTaskUnloadDetail> retPager = unloadVehicleManager.queryInterceptBarCodeDetail(pager);
            if (retPager == null || CollectionUtils.isEmpty(retPager.getData())) {
                return result;
            }

            InterceptScanBarCode interceptScanBarCode = setInterceptBarCodeList(retPager);
            result.setData(interceptScanBarCode);
        }
        catch (JyDemotionException e){
            result.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_UNLOAD_INTERCEPT, false));
        }
        catch (Exception ex) {
            log.error("查询卸车拦截包裹数据异常. {}", JsonHelper.toJson(request), ex);
            result.error("查询卸车拦截包裹数据异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    private InterceptScanBarCode setInterceptBarCodeList(Pager<JyVehicleTaskUnloadDetail> retPager) {
        InterceptScanBarCode interceptScanBarCode = new InterceptScanBarCode();
        interceptScanBarCode.setActualScanCount(calculateInterceptShouldScanCount(retPager.getData()));
        interceptScanBarCode.setShouldScanCount(retPager.getTotal());

        interceptScanBarCode.setBarCodeList(getUnloadScanBarCodeList(UnloadBarCodeQueryEntranceEnum.INTERCEPT, retPager.getData()));

        return interceptScanBarCode;
    }

    private List<UnloadScanBarCode> getUnloadScanBarCodeList(UnloadBarCodeQueryEntranceEnum queryEntranceEnum, List<JyVehicleTaskUnloadDetail> unloadDetailList) {
        List<UnloadScanBarCode> barCodeList = Lists.newArrayList();
        for (JyVehicleTaskUnloadDetail unloadDetail : unloadDetailList) {
            UnloadScanBarCode barCode = new UnloadScanBarCode();
            barCode.setBarCode(unloadDetail.getPackageCode());
            barCode.setProductType(UnloadProductTypeEnum.getNameByCode(unloadDetail.getProductType()));
            barCode.setTags(resolveBarCodeLabel(queryEntranceEnum, unloadDetail));

            UnloadBarCodeScanTypeEnum scanTypeEnum = resolveScanTypeDesc(unloadDetail);
            barCode.setScanType(scanTypeEnum.getCode());
            barCode.setScanTypeDesc(scanTypeEnum.getName());

            barCodeList.add(barCode);
        }
        return barCodeList;
    }

    private Long calculateInterceptShouldScanCount(List<JyVehicleTaskUnloadDetail> unloadDetails) {
        Long shouldScanCount = 0L;
        for (JyVehicleTaskUnloadDetail unloadDetail : unloadDetails) {
            if (Objects.equals(Constants.CONSTANT_NUMBER_ONE, unloadDetail.getScannedFlag())) {
                shouldScanCount ++;
            }
        }
        return shouldScanCount;
    }

    private Pager<JyVehicleTaskUnloadDetail> getInterceptBarCodeCondition(UnloadCommonRequest request) {
        Pager<JyVehicleTaskUnloadDetail> pager = new Pager<>();
        pager.setPageNo(request.getPageNumber());
        pager.setPageSize(request.getPageSize());

        JyVehicleTaskUnloadDetail searchVo = new JyVehicleTaskUnloadDetail();
        pager.setSearchVo(searchVo);

        searchVo.setEndSiteId(request.getCurrentOperate().getSiteCode());
        searchVo.setSealCarCode(request.getSealCarCode());
        searchVo.setBizId(request.getBizId());
        searchVo.setInterceptFlag(Constants.CONSTANT_NUMBER_ONE); // 拦截
        return pager;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.moreScanBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<MoreScanBarCode> moreScanBarCodeDetail(UnloadCommonRequest request) {
        InvokeResult<MoreScanBarCode> result = new InvokeResult<>();

        if (!checkPageQueryParam(request, result)) {
            return result;
        }

        try {
            Pager<JyVehicleTaskUnloadDetail> pager = getMoreScanBarCodeCondition(request);
            Pager<JyVehicleTaskUnloadDetail> retPager = unloadVehicleManager.queryMoreScanBarCodeDetail(pager);
            if (retPager == null || CollectionUtils.isEmpty(retPager.getData())) {
                return result;
            }

            MoreScanBarCode moreScanBarCode = setMoreScanBarCodeList(retPager);
            result.setData(moreScanBarCode);
        }
        catch (JyDemotionException e){
            result.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_UNLOAD_MORE_SCAN, false));
        }
        catch (Exception ex) {
            log.error("查询卸车多扫包裹数据异常. {}", JsonHelper.toJson(request), ex);
            result.error("查询卸车多扫包裹数据异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    private <E> boolean checkPageQueryParam(UnloadCommonRequest request, InvokeResult<E> invokeResult) {
        if (StringUtils.isBlank(request.getBizId())
                || !NumberHelper.gt0(request.getCurrentOperate().getSiteCode())
                || !NumberHelper.gt0(request.getPageNumber())
                || !NumberHelper.gt0(request.getPageSize())) {
            invokeResult.parameterError();
            return false;
        }

        return true;
    }

    private Pager<JyVehicleTaskUnloadDetail> getMoreScanBarCodeCondition(UnloadCommonRequest request) {
        Pager<JyVehicleTaskUnloadDetail> pager = new Pager<>();
        pager.setPageNo(request.getPageNumber());
        pager.setPageSize(request.getPageSize());

        JyVehicleTaskUnloadDetail searchVo = new JyVehicleTaskUnloadDetail();
        pager.setSearchVo(searchVo);

        // 多扫按操作场地查
        searchVo.setOperateSiteId(request.getCurrentOperate().getSiteCode());
        searchVo.setSealCarCode(request.getSealCarCode());
        searchVo.setBizId(request.getBizId());
        searchVo.setMoreScanFlag(Constants.CONSTANT_NUMBER_ONE); // 多扫
        return pager;
    }

    private MoreScanBarCode setMoreScanBarCodeList(Pager<JyVehicleTaskUnloadDetail> retPager) {
        MoreScanBarCode moreScanBarCode = new MoreScanBarCode();

        // 设置多扫总数
        setMoreScanAggCount(moreScanBarCode, retPager.getData());

        moreScanBarCode.setBarCodeList(getUnloadScanBarCodeList(UnloadBarCodeQueryEntranceEnum.MORE_SCAN, retPager.getData()));

        return moreScanBarCode;
    }

    private void setMoreScanAggCount(MoreScanBarCode moreScanBarCode, List<JyVehicleTaskUnloadDetail> unloadDetails) {
        long moreScanLocalCount = 0L;
        long moreScanOutCount = 0L;
        for (JyVehicleTaskUnloadDetail unloadDetail : unloadDetails) {
            // 本场地多扫
            if (unloadDetail.getLocalSiteFlag() == Constants.CONSTANT_NUMBER_ONE) {
                moreScanLocalCount ++;
            }
            else {
                moreScanOutCount ++;
            }
        }

        moreScanBarCode.setMoreScanLocalCount(moreScanLocalCount);
        moreScanBarCode.setMoreScanOutCount(moreScanOutCount);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.unloadPreviewDashboard",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<UnloadPreviewData> unloadPreviewDashboard(UnloadCommonRequest request) {
        InvokeResult<UnloadPreviewData> result = new InvokeResult<>();

        if (!checkPageQueryParam(request, result)) {
            return result;
        }

        try {
            UnloadPreviewData previewData = new UnloadPreviewData();
            result.setData(previewData);

            // 默认正常
            previewData.setAbnormalFlag(Constants.NUMBER_ZERO.byteValue());

            List<JyUnloadAggsEntity> unloadAggList = jyUnloadAggsService.queryByBizId(new JyUnloadAggsEntity(request.getBizId()));
            if (CollectionUtils.isEmpty(unloadAggList)) {
                log.warn("判断卸车任务是否完成查询AGG为空.{}", JsonHelper.toJson(request));
                return result;
            }

            // 判断卸车任务是否异常.
            Boolean unloadTaskNormal = judgeUnloadTaskNormal(previewData, unloadAggList);
            previewData.setAbnormalFlag(unloadTaskNormal ? Constants.NUMBER_ZERO.byteValue() : Constants.CONSTANT_NUMBER_ONE);

            // 卸车任务正常，直接返回
            if (unloadTaskNormal) {
                return result;
            }

            Pager<JyVehicleTaskUnloadDetail> query = getMoreScanAndToScanBarCodeCondition(request);
            Pager<JyVehicleTaskUnloadDetail> retPager = unloadVehicleManager.queryMoreScanAndToScanBarCodeDetail(query);
            if (retPager == null || CollectionUtils.isEmpty(retPager.getData())) {
                return result;
            }

            previewData.setBarCodeList(getUnloadScanBarCodeList(UnloadBarCodeQueryEntranceEnum.COMPLETE_PREVIEW, retPager.getData()));
        }
        catch (JyDemotionException e){
            result.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_UNLOAD_MORE_AND_TOSCAN, false));
        }
        catch (Exception ex) {
            log.error("卸车完成预览数据发生异常. {}", JsonHelper.toJson(request), ex);
            result.error("校验卸车完成发生异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    /**
     * 解析包裹标签。产品类型、拦截等
     * @param queryEntranceEnum
     * @param unloadDetail
     * @return
     */
    private List<LabelOption> resolveBarCodeLabel(UnloadBarCodeQueryEntranceEnum queryEntranceEnum, JyVehicleTaskUnloadDetail unloadDetail) {
        List<LabelOption> labelList = new ArrayList<>();

        // 标签展示的顺序
        int displayOrder = 0;

        // 待扫包裹不显示产品类型
        if (!Objects.equals(UnloadBarCodeQueryEntranceEnum.TO_SCAN, queryEntranceEnum)) {
            if (StringUtils.isNotBlank(unloadDetail.getProductType())) {
                displayOrder ++;
                labelList.add(new LabelOption(BarCodeLabelOptionEnum.PRODUCT_TYPE.getCode(), UnloadProductTypeEnum.getNameByCode(unloadDetail.getProductType()), displayOrder));
            }
        }

        if (NumberHelper.gt0(unloadDetail.getInterceptFlag())) {
            displayOrder ++;
            labelList.add(new LabelOption(BarCodeLabelOptionEnum.INTERCEPT.getCode(), BarCodeLabelOptionEnum.INTERCEPT.getName(), displayOrder));
        }

        return labelList;
    }

    private Pager<JyVehicleTaskUnloadDetail> getMoreScanAndToScanBarCodeCondition(UnloadCommonRequest request) {
        Pager<JyVehicleTaskUnloadDetail> pager = new Pager<>();
        pager.setPageNo(request.getPageNumber());
        pager.setPageSize(request.getPageSize());

        JyVehicleTaskUnloadDetail searchVo = new JyVehicleTaskUnloadDetail();
        pager.setSearchVo(searchVo);

        searchVo.setBizId(request.getBizId());
        searchVo.setSealCarCode(request.getSealCarCode());

        // 多扫查询条件
        searchVo.setOperateSiteId(request.getCurrentOperate().getSiteCode());
        searchVo.setMoreScanFlag(Constants.CONSTANT_NUMBER_ONE); // 多扫

        // 待扫查询条件
        searchVo.setScannedFlag(Constants.NUMBER_ZERO); // 待扫
        searchVo.setEndSiteId(request.getCurrentOperate().getSiteCode());
        return pager;
    }

    /**
     * 判断卸车任务是否正常。同时满足以下三个条件为正常
     * <ul>
     *     <li>待扫包裹数==0</li>
     *     <li>本场地多扫==0</li>
     *     <li>非本场地多扫==0</li>
     * </ul>
     * @param previewData
     * @param unloadAggList
     * @return true:正常
     */
    public boolean judgeUnloadTaskNormal(UnloadPreviewData previewData, List<JyUnloadAggsEntity> unloadAggList) {
        long existToScanRows = 0;
        long existLocalMoreScanRows = 0;
        long existOutMoreScanRows = 0;
        long moreScanOutCount = 0;
        long moreScanLocalCount = 0;
        long interceptNotScanCount = 0;
        long interceptActualScanCount = 0;
        for (JyUnloadAggsEntity aggEntity : unloadAggList) {
            if (!Objects.equals(aggEntity.getTotalScannedPackageCount(), aggEntity.getTotalSealPackageCount())) {
                existToScanRows ++;
            }
            if (NumberHelper.gt0(aggEntity.getMoreScanLocalCount())) {
                existLocalMoreScanRows ++;
                moreScanLocalCount += aggEntity.getMoreScanLocalCount();
            }
            if (NumberHelper.gt0(aggEntity.getMoreScanOutCount())) {
                existOutMoreScanRows ++;
                moreScanOutCount += aggEntity.getMoreScanOutCount();
            }
            if (NumberHelper.gt0(aggEntity.getInterceptShouldScanCount())) {
                interceptNotScanCount ++;
            }
            if (NumberHelper.gt0(aggEntity.getInterceptActualScanCount())) {
                interceptActualScanCount ++;
            }
        }
        boolean unloadDone = existToScanRows == 0 && existLocalMoreScanRows == 0 && existOutMoreScanRows == 0;

        JyUnloadAggsEntity oneUnloadAgg = unloadAggList.get(0);
        logInfo("卸车任务卸车进度. biz: {}, progress: {}-{}-{}", oneUnloadAgg.getBizId(), existToScanRows, existLocalMoreScanRows, existOutMoreScanRows);

        if (!unloadDone) {
            previewData.setTotalScan(oneUnloadAgg.getTotalScannedPackageCount().longValue());
            previewData.setInterceptNotScanCount(interceptNotScanCount);
            previewData.setInterceptActualScanCount(interceptActualScanCount);
            previewData.setMoreScanLocalCount(moreScanLocalCount);
            previewData.setMoreScanOutCount(moreScanOutCount);
            previewData.setToScanCount(dealMinus(oneUnloadAgg.getTotalSealPackageCount(), oneUnloadAgg.getTotalScannedPackageCount()));
            previewData.setAbnormalCount(previewData.getMoreScanLocalCount() + previewData.getMoreScanOutCount() + previewData.getToScanCount());
        }

        return unloadDone;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.submitUnloadCompletion",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Boolean> submitUnloadCompletion(UnloadCompleteRequest request) {
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();

        if (StringUtils.isBlank(request.getBizId())
                || StringUtils.isBlank(request.getTaskId())) {
            invokeResult.parameterError("请选择卸车任务！");
            return invokeResult;
        }
        if (null == request.getAbnormalFlag()) {
            invokeResult.parameterError("缺少卸车完成标识");
            return invokeResult;
        }
        if (NumberHelper.gt0(request.getAbnormalFlag())
                && null == request.getToScanCount()
                && null == request.getMoreScanOutCount()) {
            invokeResult.parameterError("卸车任务异常时需要异常数量");
            return invokeResult;
        }

        try {
            // 保存卸车任务是否异常，卸车完成时间，关闭卸车小组 etc.
            recordUnloadCompleteStatus(request);

            // 同步请求关闭卸车任务
            invokeResult.setData(this.completeTask(request));
        }
        catch (JyBizException bizException) {
            log.error("卸车完成关闭任务发生异常. {}", JsonHelper.toJson(request), bizException);
            invokeResult.error("关闭卸车任务发生异常，请咚咚联系分拣小秘！");
        }
        catch (Exception ex) {
            log.error("卸车完成发生异常. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("卸车完成发生异常，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    private void recordUnloadCompleteStatus(UnloadCompleteRequest request) {
        UnloadTaskCompleteDto completeDto = new UnloadTaskCompleteDto();
        completeDto.setTaskId(request.getTaskId());
        completeDto.setBizId(request.getBizId());
        completeDto.setSealCarCode(request.getSealCarCode());
        completeDto.setAbnormalFlag(request.getAbnormalFlag());

        // 设置多扫数量
        completeDto.setMoreScanCount(this.calcRightMoreScanCount(request));

        completeDto.setToScanCount(request.getToScanCount());
        completeDto.setOperateTime(new Date());
        completeDto.setOperateUserErp(request.getUser().getUserErp());
        completeDto.setOperateUserName(request.getUser().getUserName());
        unloadTaskCompleteProducer.sendOnFailPersistent(completeDto.getBizId(), JsonHelper.toJson(completeDto));
    }

    /**
     * 计算正确的多扫数量
     * <ul>
     *     <li>无任务卸车只有非本场地多扫</li>
     *     <li>正常卸车任务包含非本场地多扫+本场地多扫</li>
     * </ul>
     * @param request
     * @return
     */
    private Long calcRightMoreScanCount(UnloadCompleteRequest request) {
        JyBizTaskUnloadVehicleEntity unloadTask = jyBizTaskUnloadVehicleService.findByBizId(request.getBizId());
        // 无任务模式，只统计非本场地多扫
        if (unloadTask != null && unloadTask.unloadWithoutTask()) {
            return request.getMoreScanOutCount();
        }

        if (request.getMoreScanLocalCount() != null && request.getMoreScanOutCount() != null) {
            return request.getMoreScanLocalCount() + request.getMoreScanOutCount();
        }

        return 0L;
    }

    private boolean completeTask(UnloadCompleteRequest request) {
        JyBizTaskUnloadDto completeDto = new JyBizTaskUnloadDto();
        completeDto.setBizId(request.getBizId());
        completeDto.setOperateUserErp(request.getUser().getUserErp());
        completeDto.setOperateUserName(request.getUser().getUserName());
        completeDto.setOperateTime(new Date());
        return transactionManager.completeUnloadTask(completeDto);
    }

	@Override
	public InvokeResult<Long> countByVehicleNumberAndStatus(UnsealVehicleTaskRequest request) {
		InvokeResult<Long> invokeResult = new InvokeResult<>();
        if (StringUtils.isBlank(request.getVehicleNumber())) {
            invokeResult.parameterError("车牌号不能为空！");
            return invokeResult;
        }
        if (!NumberHelper.gt0(request.getEndSiteCode())) {
            invokeResult.parameterError("缺少目的场地！");
            return invokeResult;
        }
        JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
        condition.setStatusCodeList(request.getStatusCodeList());
        condition.setVehicleNumber(request.getVehicleNumber());
        condition.setEndSiteId(request.getEndSiteCode().longValue());
        invokeResult.setData(unloadVehicleService.countByVehicleNumberAndStatus(condition));
		return invokeResult;
	}
}
