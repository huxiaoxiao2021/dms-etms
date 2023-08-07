package com.jd.bluedragon.distribution.jy.service.unseal;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.ProductTypeAgg;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealCodeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.*;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.base.JdiTransWorkWSManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.exception.JyDemotionException;
import com.jd.bluedragon.distribution.jy.manager.IJyUnSealVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.task.autoRefresh.enums.ClientAutoRefreshBusinessTypeEnum;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadAggsService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnSealDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.JyUnloadTaskSignConstants;
import com.jd.bluedragon.utils.*;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.dms.common.constants.CodeConstants;
import com.jd.tms.jdi.dto.BigQueryOption;
import com.jd.tms.jdi.dto.BigTransWorkItemDto;
import com.jd.tms.jdi.dto.TransWorkBillDto;
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
import org.springframework.beans.BeanUtils;
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
    private JdiTransWorkWSManager jdiTransWorkWSManager;

    @Autowired
    private JdiQueryWSManager jdiQueryWSManager;

    @Autowired
    private UccPropertyConfiguration uccConfig;

    @Autowired
    private JyUnloadAggsService jyUnloadAggsService;

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

        if (log.isInfoEnabled()) {
            log.info("查询ES返回待解封车任务serviceResult. {}", JsonHelper.toJson(serviceResult));
        }

        try {
            SealVehicleTaskResponse taskResponse = JsonHelper.fromJson(JsonHelper.toJson(serviceResult), SealVehicleTaskResponse.class);

            if (log.isInfoEnabled()) {
                log.info("返回待解封车任务. {}", JsonHelper.toJson(taskResponse));
            }

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
                        try{
                            condition.setStartSiteId(Long.valueOf(request.getBarCode()));
                        }catch (Exception e) {
                            log.error("JyUnSealVehicleServiceImpl.fetchUnSealTask:barCode输入错误，应输入场地编码或车牌后四位，转Long异常，req={]", JsonUtils.toJSONString(request));
                            result.error("请输入正确的车牌号后四位或上游场地编码！");
                            return result;
                        }
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
                Long lastHour = uccConfig.getJyUnSealTaskLastHourTime();
                if(lastHour != null && lastHour > Constants.LONG_ZERO){
                    condition.setSortTime(DateHelper.newTimeRangeHoursAgo(new Date(), lastHour.intValue()));
                }
            }

            SealVehicleTaskResponse response = new SealVehicleTaskResponse();
            result.setData(response);

            // 增加刷新间隔配置
            response.setClientAutoRefreshConfig(uccConfig.getJyWorkAppAutoRefreshConfigByBusinessType(ClientAutoRefreshBusinessTypeEnum.UNSEAL_TASK_LIST.name()));

            List<JyBizTaskUnloadCountDto> vehicleStatusAggList =
                    jyBizTaskUnloadVehicleService.findStatusCountByCondition4Status(condition, null, JyBizTaskUnloadStatusEnum.UNSEAL_STATUS_OPTIONS.toArray(new JyBizTaskUnloadStatusEnum[JyBizTaskUnloadStatusEnum.UNSEAL_STATUS_OPTIONS.size()]));
            if (CollectionUtils.isEmpty(vehicleStatusAggList)) {
                return result;
            }

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
     * 记录实际解封车顺序
     * 1、根据业务主键获取对应场地信息
     * 2、获取当前场地到车任务积分排名
     * 3、快照保存当前解封车顺序
     * @param bizId
     * @return
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnSealVehicleService.saveRealUnSealRanking",
           jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public void saveRealUnSealRanking(String bizId,Date unSealTime){
        try {
            JyBizTaskUnloadVehicleEntity unloadVehicle = jyBizTaskUnloadVehicleService.findByBizId(bizId);
            if(unloadVehicle == null){
                log.error("saveRealUnSealRanking not find task! {}",bizId);
                return;
            }
            JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
            condition.setEndSiteId(unloadVehicle.getEndSiteId());
            // 查询以解封车时间为准前默认时间内的待解封车任务中所在的顺序
            Long lastHour = uccConfig.getJyUnSealTaskLastHourTime();
            if(lastHour != null && lastHour > Constants.LONG_ZERO) {
                condition.setSortTime(DateHelper.newTimeRangeHoursAgo(unSealTime, lastHour.intValue()));
            }
            condition.setVehicleStatus(JyBizTaskUnloadStatusEnum.WAIT_UN_SEAL.getCode());
            condition.setBizId(bizId);
            JyBizTaskUnloadVehicleEntity realRankingResult = jyBizTaskUnloadVehicleService.findRealRankingByBizId(condition);
            if(log.isInfoEnabled()){
                log.info("saveRealUnSealRanking realRankingResult, req:{},resp:{}",JsonHelper.toJson(condition),JsonHelper.toJson(realRankingResult));
            }
            if(realRankingResult == null){
                log.error("saveRealUnSealRanking not find task by condition! {}",bizId);
                return;
            }
            JyBizTaskUnloadVehicleEntity RealUnSealRankingUpdateParam = new JyBizTaskUnloadVehicleEntity();
            RealUnSealRankingUpdateParam.setBizId(bizId);
            RealUnSealRankingUpdateParam.setRealRanking(realRankingResult.getRealRanking());
            if(jyBizTaskUnloadVehicleService.saveOrUpdateOfBusinessInfo(RealUnSealRankingUpdateParam)){
                if(log.isInfoEnabled()){
                    log.info("saveRealUnSealRanking success!, bizId:{},realRanking:{}",bizId,realRankingResult.getRealRanking());
                }
            }else{
                log.error("saveRealUnSealRanking fail!, bizId:{},realRanking:{}",bizId,realRankingResult.getRealRanking());
            }


        }catch (Exception e){
            log.error("saveRealUnSealRanking error! {},{}",bizId,unSealTime,e);
        }

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

        // 如果是待解状态，先查询优先待解封任务列表
        if (Constants.NUMBER_ONE.equals(request.getVersion()) && JyBizTaskUnloadStatusEnum.WAIT_UN_SEAL.equals(curQueryStatus)) {
            setPriorityVehicleList(condition, request, curQueryStatus, unSealCarData);
            // 查询普通列表时，不能带优先标识
            condition.setPriorityFlag(Constants.NUMBER_ZERO);
        }

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

    private void setPriorityVehicleList(JyBizTaskUnloadVehicleEntity condition, SealVehicleTaskRequest request,
                      JyBizTaskUnloadStatusEnum curQueryStatus, UnSealCarData unSealCarData) {
        List<VehicleBaseInfo> priorityVehicleList = Lists.newArrayList();
        unSealCarData.setPriorityData(priorityVehicleList);
        // 组装优先列表查询参数
        JyBizTaskUnloadVehicleEntity priorityCondition = new JyBizTaskUnloadVehicleEntity();
        BeanUtils.copyProperties(condition, priorityCondition);
        // 必须带有优先标识
        priorityCondition.setPriorityFlag(Constants.NUMBER_ONE);
        // 分页查询
        List<JyBizTaskUnloadVehicleEntity> priorityVehiclePageList = jyBizTaskUnloadVehicleService.findByConditionOfPage(priorityCondition, JyBizTaskUnloadOrderTypeEnum.PRIORITY_FRACTION, request.getPageNumber(), request.getPageSize(), null);
        if (CollectionUtils.isNotEmpty(priorityVehiclePageList)) {
            // 批量查询各个产品类型的应卸总数 <bizId, 产品类型聚合列表>
            Map<String, List<ProductTypeAgg>> productTypeAggMap = getProductTypeAggMap(priorityVehiclePageList);
            for (JyBizTaskUnloadVehicleEntity entity : priorityVehiclePageList) {
                // 初始化基础字段
                VehicleBaseInfo vehicleBaseInfo = assembleVehicleBase(curQueryStatus, entity);
                ToSealCarInfo toSealCarInfo = (ToSealCarInfo) vehicleBaseInfo;
                // 实际到达时间
                toSealCarInfo.setActualArriveTime(entity.getActualArriveTime());
                // 各产品类型的应卸总数
                List<ProductTypeAgg> productTypeAggList = productTypeAggMap.get(entity.getBizId());
                toSealCarInfo.setProductTypeAggList(productTypeAggList == null ? new ArrayList<>() : productTypeAggList);
                priorityVehicleList.add(toSealCarInfo);
            }
        }
    }

    private Map<String, List<ProductTypeAgg>> getProductTypeAggMap(List<JyBizTaskUnloadVehicleEntity> priorityVehiclePageList) {
        // <bizId, 产品类型聚合列表>
        Map<String, List<ProductTypeAgg>> productTypeAggMap = new HashMap<>(priorityVehiclePageList.size());
        // bizId列表
        List<String> bizIds = new ArrayList<>(priorityVehiclePageList.size());
        for (JyBizTaskUnloadVehicleEntity entity : priorityVehiclePageList) {
            bizIds.add(entity.getBizId());
        }
        // 批量查询各个产品类型的应卸总数
        List<JyUnloadAggsEntity> unloadAggList = jyUnloadAggsService.queryShouldScanByBizIds(bizIds);
        if (CollectionUtils.isEmpty(unloadAggList)) {
            return productTypeAggMap;
        }
        for (JyUnloadAggsEntity aggEntity : unloadAggList) {
            // bizId
            String bizId = aggEntity.getBizId();
            // 转换产品结果对象
            ProductTypeAgg productTypeAgg = convertAggEntity(aggEntity);
            // 产品聚合对象列表
            List<ProductTypeAgg> productTypeAggList = productTypeAggMap.get(bizId);
            if (productTypeAggList == null) {
                productTypeAggList = new ArrayList<>();
            }
            productTypeAggList.add(productTypeAgg);
            productTypeAggMap.put(bizId, productTypeAggList);
        }
        return productTypeAggMap;
    }

    private ProductTypeAgg convertAggEntity(JyUnloadAggsEntity aggEntity) {
        ProductTypeAgg item = new ProductTypeAgg();
        item.setProductType(aggEntity.getProductType());
        item.setProductTypeName(UnloadProductTypeEnum.getNameByCode(item.getProductType()));
        item.setCount(aggEntity.getShouldScanCount().longValue());
        return item;
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
        vehicleBaseInfo.setBizId(entity.getBizId());
        vehicleBaseInfo.setVehicleNumber(entity.getVehicleNumber());
        vehicleBaseInfo.setLineType(entity.getLineType());
        vehicleBaseInfo.setLineTypeName(entity.getLineTypeName());
        vehicleBaseInfo.setTags(resolveTagSign(entity.getTagsSign()));
        
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

        try {
            SealCarMonitor sealCarMonitor = jySealVehicleManager.querySealTaskInfo(request);
            if (null == sealCarMonitor) {
                response.error("查询任务明细异常，请联系分拣小秘！");
                return response;
            }
            response.setData(makeTaskInfo(sealCarMonitor));
        }catch (JyDemotionException e){
            response.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_SEAL_DETAIL, false));
        }

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

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnSealVehicleService.getSealTaskInfo",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SealTaskInfo> getSealTaskInfo(SealTaskInfoRequest request) {
        InvokeResult<SealTaskInfo> result = new InvokeResult<>();
        if(request == null && StringUtils.isBlank(request.getSealCarCode())){
            result.error("入参不能为空!");
            return result;
        }
        try{
            SealCarMonitor sealCarMonitor = jySealVehicleManager.querySealCarData(request.getSealCarCode());
            if(sealCarMonitor == null){
                result.error("获取待解封车信息为空!");
                return result;
            }
            SealTaskInfo taskInfo = new SealTaskInfo();
            taskInfo.setStartSiteName(sealCarMonitor.getStartSiteName());
            taskInfo.setLocalCount(sealCarMonitor.getLocalCount());
            taskInfo.setExternalCount(sealCarMonitor.getExternalCount());
            taskInfo.setVehicleNumber(sealCarMonitor.getVehicleNumber());
            taskInfo.setTransportCode(sealCarMonitor.getTransportCode());
            result.setData(taskInfo);
            result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
        }catch (JyDemotionException e){
            result.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_SEAL_DETAIL, false));
        } catch (Exception e){
            log.error("获取待解封车信息异常-{}",e.getMessage(),e);
            result.error("获取待解封车信息异常!");
        }
        return result;
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

    /**
     * 获取待解封车数据详情
     *
     * @param request 请求参数
     * @return 详情
     * @author fanggang7
     * @time 2023-03-02 21:37:32 周四
     */
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyUnSealVehicleServiceImpl.getUnSealTaskInfo", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public Result<SealTaskInfo> getUnSealTaskInfo(SealTaskInfoRequest request) {
        logInfo("JyUnSealVehicleServiceImpl.getUnSealTaskInfo param: {}", JsonHelper.toJson(request));
        Result<SealTaskInfo> result = Result.success();

        try {
            final Result<Void> checkResult = checkParam4getSealTaskDetail(request);
            if (!checkResult.isSuccess()) {
                return result.toFail(checkResult.getMessage(), checkResult.getCode());
            }
            JyBizTaskUnloadVehicleEntity unloadVehicle = jyBizTaskUnloadVehicleService.findByBizId(request.getBizId());
            if(unloadVehicle == null){
                log.error("saveRealUnSealRanking not find task! {}", request.getSealCarCode());
                return result.toFail("未获取到任务数据");
            }

            final SealTaskInfo sealTaskInfo = new SealTaskInfo();
            result.setData(sealTaskInfo);

            sealTaskInfo.setSealCarCode(unloadVehicle.getSealCarCode());
            sealTaskInfo.setVehicleNumber(unloadVehicle.getVehicleNumber());
            sealTaskInfo.setVehicleStatus(unloadVehicle.getVehicleStatus());
            sealTaskInfo.setVehicleStatusName(ValueNameEnumUtils.getNameByValue(VehicleStatusEnum.class, sealTaskInfo.getVehicleStatus()));
            sealTaskInfo.setTransportCode(unloadVehicle.getTransWorkItemCode());

            // 查询积分排序
            if (request.getQueryRankOrder()) {
                final Result<Void> queryEsSealCarMonitorResult = queryRankOrderIndex(request, unloadVehicle.getEndSiteId(), sealTaskInfo);
                if (!queryEsSealCarMonitorResult.isSuccess()) {
                    return result.toFail(queryEsSealCarMonitorResult.getMessage(), queryEsSealCarMonitorResult.getCode());
                }
            }

            // 查询ES
            if (request.getQueryEsSealCarMonitor()) {
                final Result<Void> queryEsSealCarMonitorResult = queryEsSealCarMonitor(request, sealTaskInfo);
                if (!queryEsSealCarMonitorResult.isSuccess()) {
                    return result.toFail(queryEsSealCarMonitorResult.getMessage(), queryEsSealCarMonitorResult.getCode());
                }
            }

            // 查询封签号列表
            if (request.getQuerySealCode()) {
                final Result<Void> querySealCodeListResult = querySealCodeList(request, sealTaskInfo);
                if (!querySealCodeListResult.isSuccess()) {
                    return result.toFail(querySealCodeListResult.getMessage(), querySealCodeListResult.getCode());
                }
            }

            // 查询司机信息
            if(request.getQueryDriverInfo()){
                this.queryDriverInfo(unloadVehicle.getTransWorkItemCode(), sealTaskInfo);
            }

        } catch (Exception e) {
            log.error("JyUnSealVehicleServiceImpl.getUnSealTaskInfo param: {} ", JsonHelper.toJson(request), e);
            result.toFail("系统异常");
        }
        return result;
    }

    private Result<Void> checkParam4getSealTaskDetail(SealTaskInfoRequest request){
        Result<Void> result = Result.success();
        if(request == null){
            return result.toFail("参数错误，参数不能为空");
        }
        if(StringUtils.isBlank(request.getBizId())){
            return result.toFail("参数错误，bizId不能为空");
        }
        return result;
    }

    private void queryDriverInfo(String transWorkItemCode, SealTaskInfo sealTaskInfo) {
        if (StringUtils.isNotBlank(transWorkItemCode)) {
            BigQueryOption bigQueryOption = new BigQueryOption();
            bigQueryOption.setQueryTransWorkItemDto(Boolean.TRUE);
            BigTransWorkItemDto bigTransWorkItemDto = jdiTransWorkWSManager.queryTransWorkItemByOptionWithRead(transWorkItemCode, bigQueryOption);
            if (bigTransWorkItemDto != null && bigTransWorkItemDto.getTransWorkItemDto() != null) {
                String transWorkCode = bigTransWorkItemDto.getTransWorkItemDto().getTransWorkCode();
                if (StringUtils.isNotBlank(transWorkCode)) {
                    TransWorkBillDto transWorkBillDto = jdiQueryWSManager.queryTransWork(transWorkCode);
                    if (transWorkBillDto != null) {
                        sealTaskInfo.setDriverName(transWorkBillDto.getCarrierDriverName());
                        sealTaskInfo.setDriverPhone(transWorkBillDto.getCarrierDriverPhone());
                    }
                }
            }
        }
    }

    /**
     * 查询es统计相关信息
     */
    private Result<Void> queryEsSealCarMonitor(SealTaskInfoRequest request, SealTaskInfo sealTaskInfo) {
        Result<Void> result = Result.success();
        SealCarMonitor sealCarMonitor = jySealVehicleManager.querySealCarData(request.getSealCarCode());
        if(sealCarMonitor == null){
            return result.toFail("获取待解封车信息为空!");
        }
        sealTaskInfo.setCarModel(sealCarMonitor.getCarModel());
        sealTaskInfo.setStartSiteId(sealCarMonitor.getStartSiteId());
        sealTaskInfo.setStartSiteName(sealCarMonitor.getStartSiteName());
        sealTaskInfo.setLineType(sealCarMonitor.getLineType());
        sealTaskInfo.setLineTypeName(ValueNameEnumUtils.getNameByValue(LineTypeEnum.class, sealTaskInfo.getLineType()));
        sealTaskInfo.setTransBookCode(sealCarMonitor.getTransBookCode());
        if (StringUtils.isNotBlank(sealCarMonitor.getSendCodeList())) {
            try {
                sealTaskInfo.setBatchCodeList(Arrays.asList(sealCarMonitor.getSendCodeList().split(Constants.SEPARATOR_COMMA)));
            }
            catch (Exception e) {
                log.error("transfer sendCodeList from es error.", e);
            }
        }
        sealTaskInfo.setTotalCount(sealCarMonitor.getTotalCount());
        sealTaskInfo.setLocalCount(sealCarMonitor.getLocalCount());
        sealTaskInfo.setExternalCount(sealCarMonitor.getExternalCount());
        sealTaskInfo.setUnloadCount(sealCarMonitor.getUnloadCount());
        sealTaskInfo.setSealCarTime(sealCarMonitor.getSealCarTime());
        sealTaskInfo.setPredictionArriveTime(sealCarMonitor.getPredictionArriveTime());
        sealTaskInfo.setActualArriveTime(sealCarMonitor.getActualArriveTime());
        return result;
    }

    /**
     * 查询封签号列表
     */
    private Result<Void> querySealCodeList(SealTaskInfoRequest request, SealTaskInfo sealTaskInfo) {
        Result<Void> result = Result.success();

        SealCarDto sealCarDtoQuery = new SealCarDto();
        sealCarDtoQuery.setSealCarCode(request.getSealCarCode());
        Integer pageNumber = 1, pageSize = 10;
        PageDto<SealCarDto> queryPageDto = getSealCarDtoPageDto(pageNumber, pageSize);

        try {
            CommonDto<PageDto<SealCarDto>> sealInfoResult = newSealVehicleService.findSealInfo(sealCarDtoQuery, queryPageDto);
            if (sealInfoResult == null) {
                log.warn("querySealCodeList newSealVehicleService.findSealInfo return null {} {}", JsonHelper.toJson(sealCarDtoQuery), JsonHelper.toJson(queryPageDto));
                result.toFail("查询运输任务数据为空");
                return result;
            }
            if (!Objects.equals(sealInfoResult.getCode(), Constants.RESULT_SUCCESS)) {
                log.warn("querySealCodeList newSealVehicleService.findSealInfo fail {} {}", JsonHelper.toJson(sealCarDtoQuery), JsonHelper.toJson(queryPageDto));
                result.toFail("查询运输任务数据失败");
                return result;
            }
            final PageDto<SealCarDto> pageDto = sealInfoResult.getData();
            for (SealCarDto sealCarDto : pageDto.getResult()) {
                if (StringUtils.isNotBlank(sealCarDto.getSealCarCode()) && Objects.equals(request.getSealCarCode(), sealCarDto.getSealCarCode())) {
                    sealTaskInfo.setSealCodes(sealCarDto.getSealCodes());
                    sealTaskInfo.setBillCode(sealCarDto.getBillCode());
                    break;
                }
            }
        } catch (Exception e) {
            log.error("querySealCodeList newSealVehicleService.findSealInfo exception {} {}", JsonHelper.toJson(sealCarDtoQuery), JsonHelper.toJson(queryPageDto));
            return result.toFail("查询运输数据异常");
        }

        return result;
    }

    /**
     * 查询es统计相关信息
     */
    private Result<Void> queryRankOrderIndex(SealTaskInfoRequest request, Long endSiteId, SealTaskInfo sealTaskInfo) {
        Result<Void> result = Result.success();
        JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
        condition.setEndSiteId(endSiteId);
        // 查询以解封车时间为准前默认时间内的待解封车任务中所在的顺序
        Date currentDate = new Date();
        Long lastHour = uccConfig.getJyUnSealTaskLastHourTime();
        if(lastHour != null && lastHour > Constants.LONG_ZERO) {
            condition.setSortTime(DateHelper.newTimeRangeHoursAgo(currentDate, lastHour.intValue()));
        }
        condition.setVehicleStatus(JyBizTaskUnloadStatusEnum.WAIT_UN_SEAL.getCode());
        condition.setBizId(request.getBizId());
        JyBizTaskUnloadVehicleEntity realRankingResult = jyBizTaskUnloadVehicleService.findRealRankingByBizId(condition);
        logInfo("queryRankOrderIndex realRankingResult, req:{},resp:{}", JsonHelper.toJson(condition),JsonHelper.toJson(realRankingResult));
        // 可能存在未找到排序的数据
        if(realRankingResult == null){
            log.error("queryRankOrderIndex find rank null! {}", request.getBizId());
            sealTaskInfo.setOrderIndex(-1);
            return result;
        }
        sealTaskInfo.setOrderIndex(realRankingResult.getRealRanking());
        sealTaskInfo.setWrongOrderMessage(HintService.getHint(HintCodeConstants.JY_UNSEAL_WRONG_ORDER_MESSAGE));
        return result;
    }
}
