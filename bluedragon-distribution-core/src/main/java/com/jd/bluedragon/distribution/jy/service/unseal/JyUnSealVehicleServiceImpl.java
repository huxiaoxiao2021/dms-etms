package com.jd.bluedragon.distribution.jy.service.unseal;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealCodeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.*;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadOrderTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyUnSealStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.SpotCheckTypeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.IJyUnSealVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnSealDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.JyUnloadTaskSignConstants;
import com.jd.bluedragon.utils.*;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.enums.seal.LineTypeEnum;
import com.jdl.jy.realtime.enums.seal.VehicleStatusEnum;
import com.jdl.jy.realtime.model.es.seal.SealCarMonitor;
import com.jdl.jy.realtime.model.query.seal.SealVehicleTaskQuery;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @ClassName JyUnSealVehicleServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/3/11 14:30
 **/
@Service
public class JyUnSealVehicleServiceImpl implements IJyUnSealVehicleService {

    private static final Logger log = LoggerFactory.getLogger(JyUnSealVehicleServiceImpl.class);

    /**
     * 查询几天内的带解任务（负数）
     * */
    @Value("${newSealVehicleResource.rollBackDay:-7}")
    private int rollBackDay;

    private static final int STATUS = 10;
    private static final int VEHICLE_NUMBER_FOUR = 4;

    @Autowired
    @Qualifier("jyUnSealVehicleManager")
    private IJyUnSealVehicleManager jySealVehicleManager;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Autowired
    private UccPropertyConfiguration uccConfig;

    /**
     * Hystrix 配置参考
     * @see https://cf.jd.com/pages/viewpage.action?pageId=575172590
     * @see https://github.com/Netflix/Hystrix/wiki/Configuration
     * @param request
     * @return
     */
    @HystrixCommand(
            commandKey = "fetchSealTask",
            fallbackMethod = "sealTaskFallback",
            threadPoolKey = "fetchSealTaskThread", // 不同command线程池配置隔离
            commandProperties = {
                    // 是否开启降级逻辑，默认是开启。
                    @HystrixProperty(name = HystrixPropertiesManager.FALLBACK_ENABLED, value = "true"),
                    // fallBack方法执行时的最大并发数，默认是10，如果大量请求的fallback方法被执行时，超出此并发数的请求会抛出REJECTED_SEMAPHORE_FALLBACK异常。
                    // 如果并发数达到该设置值，请求会被拒绝和抛出异常并且fallback不会被调用
                    @HystrixProperty(name = HystrixPropertiesManager.FALLBACK_ISOLATION_SEMAPHORE_MAX_CONCURRENT_REQUESTS, value = "100"),
                    // run方法执行时间超过此时间以后将执行fallback逻辑。如果超时不降级，将此值配置大点
                    @HystrixProperty(name = HystrixPropertiesManager.EXECUTION_ISOLATION_THREAD_TIMEOUT_IN_MILLISECONDS, value = "10000"),

                    // 是否开启断路器，默认开启。推荐开启
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ENABLED, value = "true"),
                    // 启用熔断器功能窗口时间内的最小请求数。默认值20. 这个参数非常重要，熔断器是否打开首先要满足这个条件
                    // 意思是至少有20个请求才进行errorThresholdPercentage错误百分比计算。比如一段时间（10s）内有19个请求全部失败了。错误百分比是100%，但熔断器不会打开，因为requestVolumeThreshold的值是20.
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_REQUEST_VOLUME_THRESHOLD, value = "50"),
                    // 此属性设置错误百分比，等于或高于该百分比时，执行fallback降级逻辑
                    @HystrixProperty(name = HystrixPropertiesManager.CIRCUIT_BREAKER_ERROR_THRESHOLD_PERCENTAGE, value = "50"),

                    // 此属性设置滚动窗口的大小，默认10秒
                    @HystrixProperty(name = HystrixPropertiesManager.METRICS_ROLLING_STATS_TIME_IN_MILLISECONDS, value = "10000"),
            },
            threadPoolProperties = {
                    // 核心线程池的大小，默认值是 10
                    @HystrixProperty(name = HystrixPropertiesManager.CORE_SIZE, value = "20"),

                    // 线程池中线程的最大数量，默认值是10，此配置项单独配置时并不会生效，需要启用allowMaximumSizeToDivergeFromCoreSize项
                    @HystrixProperty(name = HystrixPropertiesManager.MAXIMUM_SIZE, value = "50"),

                    // 设置线程池队列大小。作业队列的最大值，默认值为-1，设置为此值时，队列会使用SynchronousQueue，此时其size为0，Hystrix 不会向队列内存放作业。
                    // 如果此值设置为一个正的int型，队列会使用一个固定size的LinkedBlockingQueue，此时在核心线程池内的线程都在忙碌时，会将作业暂时存放在此队列内，但超出此队列的请求依然会被拒绝
                    @HystrixProperty(name = HystrixPropertiesManager.MAX_QUEUE_SIZE, value = "5"),

                    // 是否允许线程池扩展到最大线程池数量，默认为 false。
                    @HystrixProperty(name = HystrixPropertiesManager.ALLOW_MAXIMUM_SIZE_TO_DIVERGE_FROM_CORE_SIZE, value = "true")
            }
    )
    @Override
    public InvokeResult<SealVehicleTaskResponse> fetchSealTask(SealVehicleTaskRequest request) {
        InvokeResult<SealVehicleTaskResponse> result = new InvokeResult<>();

        SealVehicleTaskQuery query = assembleCommonCondition(request);
        if (isSearch(request)) {
            // 根据封签号或批次号查询，从运输获得封车编码
            if (queryFromSealCode(request.getBarCode())
                    || queryFromBatchCode(request.getBarCode())) {
                List<String> sealCarCodeList = getSealCarCodeFromVos(result, request);
                if (!result.codeSuccess()) {
                    return result;
                }
                query.setSealCarCode(sealCarCodeList);
            }
            else {
                query.setSearchKeyword(request.getBarCode());
            }
        }
        else {
            // 查询最近6小时的待解封车任务
            query.setQueryTimeStart(DateHelper.newTimeRangeHoursAgo(new Date(), 6));
        }
        query.setVehicleStatus(request.getVehicleStatus());
        query.setLineType(request.getLineType());

        Pager<SealVehicleTaskQuery> pager = new Pager<>();
        pager.setPageNo(request.getPageNumber());
        pager.setPageSize(request.getPageSize());
        pager.setSearchVo(query);

        com.jdl.jy.realtime.model.vo.seal.SealVehicleTaskResponse serviceResult = jySealVehicleManager.querySealTask(pager);
        if (serviceResult == null) {
            log.warn("未查询到待解封车任务. {}", JsonHelper.toJson(pager));
            return result;
        }

        try {
            SealVehicleTaskResponse taskResponse = JsonHelper.fromJson(JsonHelper.toJson(serviceResult), SealVehicleTaskResponse.class);

            result.setData(taskResponse);
        }
        catch (Exception e) {
            log.error("copy SealVehicleTaskResponse error.", e);
            result.error("服务器异常，请联系分拣小秘");
        }

        return result;
    }

    @Override
    public InvokeResult<SealVehicleTaskResponse> fetchUnSealTask(SealVehicleTaskRequest request) {

        logInfo("拉取拣运到车任务. {}", JsonHelper.toJson(request));

        InvokeResult<SealVehicleTaskResponse> result = new InvokeResult<>();

        try {
            JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
            condition.setEndSiteId(request.getEndSiteCode().longValue());

            if (isSearch(request)) {
                // 按封车编码查询
                if (queryFromSealCarCode(request.getBarCode())) {
                    condition.setSealCarCode(request.getBarCode());
                }
                // 根据封签号或批次号查询，从运输获得封车编码
                else if (queryFromSealCode(request.getBarCode())
                        || queryFromBatchCode(request.getBarCode())) {
                    List<String> sealCarCodeList = getSealCarCodeFromVos(result, request);
                    if (!result.codeSuccess()) {
                        return result;
                    }
                    condition.setSealCarCode(sealCarCodeList.get(0));
                }
                else {
                    if (NumberHelper.isPositiveNumber(request.getBarCode())) {
                        condition.setStartSiteId(Long.valueOf(request.getBarCode()));
                    }
                    if (request.getBarCode().length() == VEHICLE_NUMBER_FOUR) {
                        condition.setFuzzyVehicleNumber(request.getBarCode());
                    }
                    if (StringUtils.isBlank(condition.getFuzzyVehicleNumber()) && !NumberHelper.gt0(condition.getStartSiteId())) {
                        result.error("请输入正确的车牌号后四位或上游场地！");
                        return result;
                    }
                }
            }
            else {
                // 查询最近6小时的待解封车任务
                condition.setSortTime(DateHelper.newTimeRangeHoursAgo(new Date(), 6));
            }

            List<JyBizTaskUnloadCountDto> vehicleStatusAggList =
                    jyBizTaskUnloadVehicleService.findStatusCountByCondition4Status(condition, null, JyBizTaskUnloadStatusEnum.UNSEAL_STATUS_OPTIONS.toArray(new JyBizTaskUnloadStatusEnum[JyBizTaskUnloadStatusEnum.UNSEAL_STATUS_OPTIONS.size()]));
            if (CollectionUtils.isEmpty(vehicleStatusAggList)) {
                return result;
            }

            SealVehicleTaskResponse response = new SealVehicleTaskResponse();
            result.setData(response);

            // 按状态统计到车任务
            assembleUnSealVehicleStatusAgg(vehicleStatusAggList, response);

            // 按状态组装到车任务数据
            assembleUnSealVehicle(request, condition, response);

            // 按封车编码查不到数据，再兜底查运输
            if (StringUtils.isNotBlank(condition.getSealCarCode()) && response.responseDataIsNull()) {

                logInfo("从运输获得待解封车任务列表. {}", JsonHelper.toJson(condition));

                SealCarDto sealCarQuery = queryVosUnSealTaskUsingSealCarCode(request, condition);
                PageDto<SealCarDto> queryPageDto = getSealCarDtoPageDto(request.getPageNumber(), request.getPageSize());
                PageDto<SealCarDto> pageDto = getSealTaskFromVos(result, sealCarQuery, queryPageDto);
                if (!result.codeSuccess()) {
                    return result;
                }

                result.setData(makeSealResponseUsingVos(request, pageDto, false));
            }
        }
        catch (Exception e) {
            log.error("查询到车任务服务器异常. {}", JsonHelper.toJson(request), e);
            result.error("获取到车任务服务器异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    /**
     * 根据封车编码查运输待解任务
     * @param request
     * @param condition
     * @return
     */
    private SealCarDto queryVosUnSealTaskUsingSealCarCode(SealVehicleTaskRequest request, JyBizTaskUnloadVehicleEntity condition) {
        SealCarDto sealCarQuery = new SealCarDto();
        // 查询封车任务
        sealCarQuery.setStatus(STATUS);
        sealCarQuery.setEndSiteId(request.getEndSiteCode());
        sealCarQuery.setSealCarCode(condition.getSealCarCode());
        return sealCarQuery;
    }

    /**
     * 按状态统计车辆数量
     * @param vehicleStatusAggList
     * @param response
     */
    private void assembleUnSealVehicleStatusAgg(List<JyBizTaskUnloadCountDto> vehicleStatusAggList, SealVehicleTaskResponse response) {
        List<VehicleStatusStatis> statusAgg = Lists.newArrayListWithCapacity(vehicleStatusAggList.size());
        response.setStatusStatis(statusAgg);

        for (JyBizTaskUnloadCountDto countDto : vehicleStatusAggList) {
            VehicleStatusStatis item = new VehicleStatusStatis();
            item.setVehicleStatus(countDto.getVehicleStatus());
            item.setVehicleStatusName(JyBizTaskUnloadStatusEnum.getNameByCode(item.getVehicleStatus()));
            item.setTotal(countDto.getSum().longValue());
            statusAgg.add(item);
        }
    }

    /**
     * 按车辆状态组装车辆数据
     * @param request
     * @param condition
     * @param response
     */
    private void assembleUnSealVehicle(SealVehicleTaskRequest request, JyBizTaskUnloadVehicleEntity condition, SealVehicleTaskResponse response) {
        JyBizTaskUnloadStatusEnum curQueryStatus = JyBizTaskUnloadStatusEnum.getEnumByCode(request.getVehicleStatus());
        List<LineTypeStatis> lineTypeList = getVehicleLineTypeList(condition, curQueryStatus);
        UnSealCarData unSealCarData = new UnSealCarData();
        unSealCarData.setVehicleStatus(curQueryStatus.getCode());
        unSealCarData.setLineStatistics(lineTypeList);

        // 按车辆状态组装
        makeVehicleList(condition, request, curQueryStatus, unSealCarData);

        switch (curQueryStatus) {
            case WAIT_UN_SEAL:
                response.setToSealCarData(unSealCarData);
                break;
            case WAIT_UN_LOAD:
                response.setToUnloadCarData(unSealCarData);
                break;
            case UN_LOADING:
                response.setUnloadCarData(unSealCarData);
                break;
            case ON_WAY:
                response.setDrivingData(unSealCarData);
                break;
        }
    }

    private JyBizTaskUnloadOrderTypeEnum setTaskOrderType(JyBizTaskUnloadStatusEnum curQueryStatus) {
        switch (curQueryStatus) {
            case WAIT_UN_SEAL:
                if (Objects.equals(Constants.CONSTANT_NUMBER_ONE, uccConfig.getJyUnSealTaskOrderByIntegral())) {
                    return JyBizTaskUnloadOrderTypeEnum.RANKING;
                }
                else {
                    return JyBizTaskUnloadOrderTypeEnum.ORDER_TIME;
                }
            case WAIT_UN_LOAD:
            case UN_LOADING:
            case ON_WAY:
                return JyBizTaskUnloadOrderTypeEnum.ORDER_TIME;
            default:
                return null;
        }
    }

    private void makeVehicleList(JyBizTaskUnloadVehicleEntity condition, SealVehicleTaskRequest request,
                                 JyBizTaskUnloadStatusEnum curQueryStatus, UnSealCarData unSealCarData) {
        List<VehicleBaseInfo> vehicleList = Lists.newArrayList();
        unSealCarData.setData(vehicleList);

        // 列表查询条件
        condition.setLineType(request.getLineType());
        condition.setVehicleStatus(request.getVehicleStatus());

        JyBizTaskUnloadOrderTypeEnum orderTypeEnum = setTaskOrderType(curQueryStatus);

        List<JyBizTaskUnloadVehicleEntity> vehiclePageList = jyBizTaskUnloadVehicleService.findByConditionOfPage(condition, orderTypeEnum, request.getPageNumber(), request.getPageSize(), null);
        if (CollectionUtils.isEmpty(vehiclePageList)) {
            return;
        }

        for (JyBizTaskUnloadVehicleEntity entity : vehiclePageList) {
            // 初始化基础字段
            VehicleBaseInfo vehicleBaseInfo = assembleVehicleBase(curQueryStatus, entity);

            // 按卸车状态设置个性化属性
            switch (curQueryStatus) {
                case WAIT_UN_SEAL:
                    ToSealCarInfo toSealCarInfo = (ToSealCarInfo) vehicleBaseInfo;
                    toSealCarInfo.setActualArriveTime(entity.getActualArriveTime());

                    vehicleList.add(toSealCarInfo);
                    break;
                case WAIT_UN_LOAD:
                    ToUnloadCarInfo toUnloadCarInfo = (ToUnloadCarInfo) vehicleBaseInfo;
                    toUnloadCarInfo.setDeSealCarTime(entity.getDesealCarTime());

                    vehicleList.add(toUnloadCarInfo);
                    break;
                case UN_LOADING:
                    UnloadCarInfo unloadCarInfo = (UnloadCarInfo) vehicleBaseInfo;
                    unloadCarInfo.setUnloadProgress(this.setUnloadProgress(entity));

                    vehicleList.add(unloadCarInfo);
                    break;
                case ON_WAY:
                    DrivingCarInfo drivingCarInfo = (DrivingCarInfo) vehicleBaseInfo;
                    drivingCarInfo.setPredictionArriveTime(entity.getPredictionArriveTime());

                    vehicleList.add(drivingCarInfo);
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

    private VehicleBaseInfo assembleVehicleBase(JyBizTaskUnloadStatusEnum curQueryStatus, JyBizTaskUnloadVehicleEntity entity) {
        VehicleBaseInfo vehicleBaseInfo = null;
        switch (curQueryStatus) {
            case WAIT_UN_SEAL:
                vehicleBaseInfo = new ToSealCarInfo();
                break;
            case WAIT_UN_LOAD:
                vehicleBaseInfo = new ToUnloadCarInfo();
                break;
            case UN_LOADING:
                vehicleBaseInfo = new UnloadCarInfo();
                break;
            case ON_WAY:
                vehicleBaseInfo = new DrivingCarInfo();
        }

        vehicleBaseInfo.setSealCarCode(entity.getSealCarCode());
        vehicleBaseInfo.setVehicleNumber(entity.getVehicleNumber());
        vehicleBaseInfo.setLineType(entity.getLineType());
        vehicleBaseInfo.setLineTypeName(entity.getLineTypeName());
        
        if (BusinessUtil.isSignChar(entity.getTagsSign(),JyUnloadTaskSignConstants.POSITION_1,JyUnloadTaskSignConstants.CHAR_1_1)) {
            vehicleBaseInfo.setSpotCheck(true);
            vehicleBaseInfo.setSpotCheckType(SpotCheckTypeEnum.DIRECT.getCode());
        }else if (BusinessUtil.isSignChar(entity.getTagsSign(),JyUnloadTaskSignConstants.POSITION_1,JyUnloadTaskSignConstants.CHAR_1_2)) {
            vehicleBaseInfo.setSpotCheck(true);
            vehicleBaseInfo.setSpotCheckType(SpotCheckTypeEnum.RANDOM.getCode());
        }

        vehicleBaseInfo.setStarSiteId(entity.getStartSiteId().intValue());
        vehicleBaseInfo.setStartSiteName(entity.getStartSiteName());

        return vehicleBaseInfo;
    }

    private List<LineTypeStatis> getVehicleLineTypeList(JyBizTaskUnloadVehicleEntity condition, JyBizTaskUnloadStatusEnum curQueryStatus) {
        List<LineTypeStatis> lineTypeList = new ArrayList<>();
        List<JyBizTaskUnloadCountDto> lineTypeAgg = jyBizTaskUnloadVehicleService.findStatusCountByCondition4StatusAndLine(condition, null, curQueryStatus);
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

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    private boolean isRefresh(SealVehicleTaskRequest request) {
        return SealVehicleTaskQuery.FETCH_TYPE_REFRESH.equals(request.getFetchType());
    }

    private boolean isSearch(SealVehicleTaskRequest request) {
        return StringUtils.isNotBlank(request.getBarCode());
    }

    private SealVehicleTaskQuery assembleCommonCondition(SealVehicleTaskRequest request) {
        SealVehicleTaskQuery query = new SealVehicleTaskQuery();

        query.setEndSiteId(request.getEndSiteCode());
        query.setFetchType(request.getFetchType());

        return query;
    }

    @Override
    public InvokeResult<SealTaskInfo> taskInfo(SealTaskInfoRequest request) {

        InvokeResult<SealTaskInfo> response = new InvokeResult<>();

        SealCarMonitor sealCarMonitor = jySealVehicleManager.querySealTaskInfo(request);
        if (null == sealCarMonitor) {
            response.error("查询任务明细异常，请联系分拣小秘！");
            return response;
        }

        response.setData(makeTaskInfo(sealCarMonitor));
        return response;
    }

    /**
     * Hystrix降级任务
     * 解封车任务降级逻辑
     * @param request
     * @param throwable
     * @return
     */
    public InvokeResult<SealVehicleTaskResponse> sealTaskFallback(SealVehicleTaskRequest request, Throwable throwable) {
        InvokeResult<SealVehicleTaskResponse> invokeResult = new InvokeResult<>();

        CallerInfo ump = ProfilerHelper.registerInfo("dms.web.IJySealVehicleService.sealTaskFallback");
        try {

            StringBuilder msgConcat = new StringBuilder();
            msgConcat.append("获取解封车任务进入降级逻辑");

            if (throwable != null) {
                log.error("获取解封车任务进入降级逻辑. request:{}", JsonHelper.toJson(request), throwable);
                msgConcat.append("异常信息:[").append(throwable.getMessage()).append("]");

                Profiler.businessAlarm("dms.web.IJySealVehicleService.fallback", msgConcat.toString());
            }

            SealCarDto sealCarQuery = getSealCarDto(request);
            PageDto<SealCarDto> queryPageDto = getSealCarDtoPageDto(request.getPageNumber(), request.getPageSize());
            PageDto<SealCarDto> pageDto = getSealTaskFromVos(invokeResult, sealCarQuery, queryPageDto);
            if (!invokeResult.codeSuccess()) {
                return invokeResult;
            }

            SealVehicleTaskResponse response = makeSealResponseUsingVos(request, pageDto, true);

            invokeResult.setData(response);
        }
        catch (Exception e) {
            log.error("从运输拉取解封车任务异常. {}", JsonHelper.toJson(request), e);
            invokeResult.error("服务器异常，请联系分拣小秘");
            Profiler.functionError(ump);
        }
        finally {
            Profiler.registerInfoEnd(ump);
        }

        return invokeResult;
    }

    /**
     * 转换VOS解封车结果
     * @param request
     * @param pageDto
     * @param needFilter 是否需要根据搜索项过滤运输的数据
     * @return
     */
    private SealVehicleTaskResponse makeSealResponseUsingVos(SealVehicleTaskRequest request, PageDto<SealCarDto> pageDto, Boolean needFilter) {
        SealVehicleTaskResponse response = new SealVehicleTaskResponse();
        List<VehicleStatusStatis> statusStatisList = new ArrayList<>();
        response.setStatusStatis(statusStatisList);
        VehicleStatusStatis statusStatis = new VehicleStatusStatis();
        statusStatisList.add(statusStatis);
        statusStatis.setVehicleStatus(JyUnSealStatusEnum.WAIT_UN_SEAL.getCode());
        statusStatis.setVehicleStatusName(JyUnSealStatusEnum.WAIT_UN_SEAL.getName());

        List<SealCarDto> filterList = new ArrayList<>();
        if (needFilter && isSearch(request)) {
            for (SealCarDto sealCar : pageDto.getResult()) {
                if (filterBySealCode(request.getBarCode(), sealCar) || filterByVehicleNumber(request.getBarCode(), sealCar)) {
                    filterList.add(sealCar);
                }
            }
            statusStatis.setTotal((long)filterList.size());
        }
        else {
            filterList = pageDto.getResult();
            statusStatis.setTotal((long) pageDto.getTotalRow());
        }

        sealCarDtoToResponse(response, filterList);

        return response;
    }

    private SealCarDto querySealCarCodeCondition(SealVehicleTaskRequest request) {
        SealCarDto sealCarDto = new SealCarDto();
        // 查询封车任务
        sealCarDto.setStatus(STATUS);
        sealCarDto.setEndSiteId(request.getEndSiteCode());
        // 封签号
        if (queryFromSealCode(request.getBarCode())) {
            sealCarDto.setSealCode(request.getBarCode());
        }
        // 批次号
        else if (queryFromBatchCode(request.getBarCode())) {
            sealCarDto.setBatchCode(request.getBarCode());
        }

        return sealCarDto;
    }

    private boolean queryFromBatchCode(String inputCode) {
        return BusinessHelper.isSendCode(inputCode)
                || BusinessUtil.isTerminalSendCode(inputCode);
    }

    private boolean queryFromSealCode(String inputCode) {
        return BusinessUtil.isSealBoxNo(inputCode);
    }

    private boolean queryFromSealCarCode(String inputCode) {
        return BusinessUtil.isSealCarCode(inputCode);
    }

    /**
     * 根据封签号获得封车编码
     * @param result
     * @param request
     * @return
     */
    private List<String> getSealCarCodeFromVos(InvokeResult<SealVehicleTaskResponse> result, SealVehicleTaskRequest request) {
        SealCarDto sealCarQuery = querySealCarCodeCondition(request);

        List<String> sealCarCodeList = getSealCarCodeFromVos(request, sealCarQuery);
        if (CollectionUtils.isEmpty(sealCarCodeList)) {
            result.error("没有待解封车任务！");
            return Lists.newArrayList();
        }

        return sealCarCodeList;
    }

    /**
     * 查询运输接口，获取封车编码
     * @param request
     * @param queryDto
     * @return
     */
    private List<String> getSealCarCodeFromVos(SealVehicleTaskRequest request, SealCarDto queryDto) {
        Set<String> sealCarCodeSet = new HashSet<>();

        int pageNumber = 1, pageSize = 100;
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        PageDto<SealCarDto> queryPageDto = getSealCarDtoPageDto(pageNumber, pageSize);
        PageDto<SealCarDto> sealTaskFromVos = getSealTaskFromVos(invokeResult, queryDto, queryPageDto);

        if (sealTaskFromVos != null && CollectionUtils.isNotEmpty(sealTaskFromVos.getResult())) {

            boolean queryFromSealCode = queryFromSealCode(request.getBarCode());

            boolean queryFromBatchCode = queryFromBatchCode(request.getBarCode());

            for (SealCarDto sealCarDto : sealTaskFromVos.getResult()) {
                if (queryFromSealCode) {
                    if (filterBySealCode(request.getBarCode(), sealCarDto)) {
                        sealCarCodeSet.add(sealCarDto.getSealCarCode());
                        if (log.isInfoEnabled()) {
                            log.info("根据封签号{}从运输获取封车编码{}.", JsonHelper.toJson(request), sealCarCodeSet);
                        }
                    }
                }
                else if (queryFromBatchCode) {
                    sealCarCodeSet.add(sealCarDto.getSealCarCode());
                    if (log.isInfoEnabled()) {
                        log.info("根据批次号{}从运输获取封车编码{}.", JsonHelper.toJson(request), sealCarCodeSet);
                    }
                }
            }
        }

        return new ArrayList<>(sealCarCodeSet);
    }

    /**
     * 根据查询条件从运输获得封车任务
     * @param invokeResult
     * @param sealCarDto
     * @param pageDto
     * @return
     */
    private <E> PageDto<SealCarDto> getSealTaskFromVos(InvokeResult<E> invokeResult, SealCarDto sealCarDto, PageDto<SealCarDto> pageDto) {
        try {
            CommonDto<PageDto<SealCarDto>> returnCommonDto = newSealVehicleService.findSealInfo(sealCarDto, pageDto);
            if (returnCommonDto != null) {
                if (Constants.RESULT_SUCCESS == returnCommonDto.getCode()) {
                    if (returnCommonDto.getData() != null && CollectionUtils.isNotEmpty(returnCommonDto.getData().getResult())) {
                        return returnCommonDto.getData();
                    }
                    else {
                        invokeResult.setCode(JdResponse.CODE_OK_NULL);
                        invokeResult.setMessage(JdResponse.MESSAGE_OK_NULL);
                    }
                }
                else {
                    invokeResult.setCode(NewSealVehicleResponse.CODE_EXCUTE_ERROR);
                    invokeResult.setMessage("[" + returnCommonDto.getCode() + ":" + returnCommonDto.getMessage() + "]");
                }
            }
        }
        catch (Exception e) {
            log.error("从运输拉取解封车任务异常. {}", JsonHelper.toJson(sealCarDto), e);
            invokeResult.error("服务器异常，请联系分拣小秘！");
        }

        log.warn("查询运输接口获取解封车任务失败. sealCarDto:{}-{}, response:{}", JsonHelper.toJson(sealCarDto), JsonHelper.toJson(pageDto), JsonHelper.toJson(invokeResult));

        return null;
    }

    /**
     * 按搜索条件过滤封车任务
     * @param barCode
     * @param sealCar
     * @return
     */
    private boolean filterBySealCode(String barCode, SealCarDto sealCar) {
        if (CollectionUtils.isNotEmpty(sealCar.getSealCodes()) && sealCar.getSealCodes().contains(barCode)) {
            return true;
        }

        return false;
    }

    private boolean filterByVehicleNumber(String barCode, SealCarDto sealCar) {
        if (!queryFromSealCode(barCode)) {
            if (StringUtils.isNotBlank(sealCar.getVehicleNumber())
                    && sealCar.getVehicleNumber().length() > VEHICLE_NUMBER_FOUR && barCode.equals(StringUtils.substring(sealCar.getVehicleNumber(), - VEHICLE_NUMBER_FOUR))) {
                return true;
            }
        }

        return false;
    }

    private void sealCarDtoToResponse(SealVehicleTaskResponse response, List<SealCarDto> sealCarDtos) {
        if (CollectionUtils.isEmpty(sealCarDtos)) {
            return;
        }

        List<ToSealCarInfo> sealCarInfList = new ArrayList<>();
        for (SealCarDto sealCarDto : sealCarDtos) {
            ToSealCarInfo sealCarInfo = new ToSealCarInfo();
            sealCarInfo.setActualArriveTime(null);
            sealCarInfo.setSealCarCode(sealCarDto.getSealCarCode());
            sealCarInfo.setVehicleNumber(sealCarDto.getVehicleNumber());
            sealCarInfo.setStarSiteId(sealCarDto.getStartSiteId());
            sealCarInfo.setStartSiteName(sealCarDto.getStartSiteName());
            sealCarInfo.setLineType(null);
            sealCarInfo.setLineTypeName(null);
            sealCarInfo.setSpotCheck(null);
            sealCarInfList.add(sealCarInfo);
        }

        UnSealCarData<ToSealCarInfo> toSealCarData = new UnSealCarData<>();
        toSealCarData.setData(sealCarInfList);
        response.setToSealCarData(toSealCarData);
    }

    private PageDto<SealCarDto> getSealCarDtoPageDto(Integer pageNumber, Integer pageSize) {
        PageDto<SealCarDto> pageDto = new PageDto<>();
        pageDto.setCurrentPage(pageNumber);
        pageDto.setPageSize(pageSize);
        return pageDto;
    }

    private SealCarDto getSealCarDto(SealVehicleTaskRequest request) {
        SealCarDto sealCarDto = new SealCarDto();
        // 查询封车任务
        sealCarDto.setStatus(STATUS);
        // 查询15天内的待解任务
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, rollBackDay);
        sealCarDto.setSealCarTimeBegin(c.getTime());
        sealCarDto.setEndSiteId(request.getEndSiteCode());
        return sealCarDto;
    }

    private SealTaskInfo makeTaskInfo(SealCarMonitor sealCarMonitor) {
        SealTaskInfo taskInfo = new SealTaskInfo();
        taskInfo.setVehicleNumber(sealCarMonitor.getVehicleNumber());
        taskInfo.setCarModel(sealCarMonitor.getCarModel());
        taskInfo.setVehicleStatus(sealCarMonitor.getVehicleStatus());
        taskInfo.setVehicleStatusName(ValueNameEnumUtils.getNameByValue(VehicleStatusEnum.class, taskInfo.getVehicleStatus()));
        taskInfo.setStartSiteId(sealCarMonitor.getStartSiteId());
        taskInfo.setStartSiteName(sealCarMonitor.getStartSiteName());
        taskInfo.setLineType(sealCarMonitor.getLineType());
        taskInfo.setLineTypeName(ValueNameEnumUtils.getNameByValue(LineTypeEnum.class, taskInfo.getLineType()));
        taskInfo.setTransportCode(sealCarMonitor.getTransportCode());
        taskInfo.setTransBookCode(sealCarMonitor.getTransBookCode());
        if (StringUtils.isNotBlank(sealCarMonitor.getSendCodeList())) {
            try {
                taskInfo.setBatchCodeList(Arrays.asList(sealCarMonitor.getSendCodeList().split(Constants.SEPARATOR_COMMA)));
            }
            catch (Exception e) {
                log.error("transfer sendCodeList from es error.", e);
            }
        }
        taskInfo.setTotalCount(sealCarMonitor.getTotalCount());
        taskInfo.setLocalCount(sealCarMonitor.getLocalCount());
        taskInfo.setExternalCount(sealCarMonitor.getExternalCount());
        taskInfo.setUnloadCount(sealCarMonitor.getUnloadCount());
        taskInfo.setSealCarTime(sealCarMonitor.getSealCarTime());
        taskInfo.setPredictionArriveTime(sealCarMonitor.getPredictionArriveTime());
        taskInfo.setActualArriveTime(sealCarMonitor.getActualArriveTime());

        return taskInfo;
    }

    @Override
    public InvokeResult<SealCodeResponse> sealCodeList(SealCodeRequest request) {
        InvokeResult<SealCodeResponse> result = new InvokeResult<>();

        SealCarDto sealCarQuery = new SealCarDto();
        sealCarQuery.setSealCarCode(request.getSealCarCode());
        Integer pageNumber = 1, pageSize = 10;
        PageDto<SealCarDto> queryPageDto = getSealCarDtoPageDto(pageNumber, pageSize);

        PageDto<SealCarDto> pageDto = getSealTaskFromVos(result, sealCarQuery, queryPageDto);
        if (!result.codeSuccess()) {
            return result;
        }

        // TODO 增加无任务解封车逻辑
        for (SealCarDto sealCarDto : pageDto.getResult()) {
            if (filterBySealCarCode(request, sealCarDto)) {
                result.setData(makeSealCodeResponse(sealCarDto));
                break;
            }
        }

        return result;
    }

    /**
     * 创建解封车任务
     *
     * @param dto
     * @return
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnSealVehicleService.createUnSealTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean createUnSealTask(JyBizTaskUnSealDto dto) {
        //保存业务任务数据
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
        entity.setBizId(dto.getBizId());
        entity.setSealCarCode(dto.getSealCarCode());
        entity.setVehicleNumber(dto.getVehicleNumber());
        entity.setTransWorkItemCode(dto.getTransWorkItemCode());
        entity.setStartSiteId(dto.getStartSiteId());
        entity.setStartSiteName(dto.getStartSiteName());
        entity.setEndSiteId(dto.getEndSiteId());
        entity.setEndSiteName(dto.getEndSiteName());
        entity.setCreateUserName(dto.getOperateUserName());
        entity.setCreateUserErp(dto.getOperateUserErp());
        entity.setCreateTime(dto.getOperateTime());
        entity.setUpdateUserName(dto.getOperateUserName());
        entity.setUpdateUserErp(dto.getOperateUserErp());
        entity.setUpdateTime(dto.getOperateTime());
        //优先已上游调用者传入的状态为准，默认在途状态
        if(dto.getVehicleStatus() == null){
            entity.setVehicleStatus(JyBizTaskUnloadStatusEnum.ON_WAY.getCode());
        }else{
            entity.setVehicleStatus(dto.getVehicleStatus());
        }
        if(jyBizTaskUnloadVehicleService.saveOrUpdateOfBaseInfo(entity)){
            //创建调度任务
            if(!createUnSealScheduleTask(dto)){
                throw new JyBizException("创建调度任务失败");
            }else{
                return true;
            }
        }else{
            log.error("IJyUnSealVehicleService.createUnSealTask fail dto:{}",JsonHelper.toJson(dto));
            return false;
        }

    }

    /**
     * 取消解封车任务
     *
     * @param dto
     * @return
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnSealVehicleService.cancelUnSealTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean cancelUnSealTask(JyBizTaskUnSealDto dto) {
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
        entity.setBizId(dto.getBizId());
        entity.setVehicleStatus(JyBizTaskUnloadStatusEnum.CANCEL.getCode());
        entity.setUpdateTime(dto.getOperateTime());
        entity.setUpdateUserErp(dto.getOperateUserErp());
        entity.setUpdateUserName(dto.getOperateUserName());
        if(jyBizTaskUnloadVehicleService.changeStatus(entity)){
            //关闭调度任务
            if(!closeUnSealScheduleTask(dto)){
                throw new JyBizException("关闭调度任务失败");
            }else{
                return true;
            }
        }else{
            log.error("IJyUnSealVehicleService.cancelUnSealTask fail dto:{}",JsonHelper.toJson(dto));
            return false;
        }
    }

    /**
     * 创建卸车专用调度任务
     * @param dto
     * @return
     */
    private boolean createUnSealScheduleTask(JyBizTaskUnSealDto dto){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(dto.getBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.UNSEAL.getCode());
        req.setOpeUser(dto.getOperateUserErp());
        req.setOpeUserName(dto.getOperateUserName());
        req.setOpeTime(dto.getOperateTime());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.createScheduleTask(req);
        return jyScheduleTaskResp != null;
    }

    /**
     * 关闭卸车专用调度任务
     * @param dto
     * @return
     */
    private boolean closeUnSealScheduleTask(JyBizTaskUnSealDto dto){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(dto.getBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.UNSEAL.getCode());
        req.setOpeUser(dto.getOperateUserErp());
        req.setOpeUserName(dto.getOperateUserName());
        req.setOpeTime(dto.getOperateTime());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.closeScheduleTask(req);
        return jyScheduleTaskResp != null;
    }

    private SealCodeResponse makeSealCodeResponse(SealCarDto sealCarDto) {
        SealCodeResponse response = new SealCodeResponse();
        response.setSealCodes(sealCarDto.getSealCodes());
        response.setVehicleNumber(sealCarDto.getVehicleNumber());
        response.setBillCode(sealCarDto.getBillCode());
        return response;
    }

    private boolean filterBySealCarCode(SealCodeRequest request, SealCarDto sealCarDto) {
        return StringUtils.isNotBlank(sealCarDto.getSealCarCode()) && Objects.equals(request.getSealCarCode(), sealCarDto.getSealCarCode());
    }
}
