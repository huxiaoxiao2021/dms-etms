package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealCodeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealTaskInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.request.SealVehicleTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.*;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.IJySealVehicleManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.NewSealVehicleResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BeanCopyUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ValueNameEnumUtils;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.PageDto;
import com.jd.etms.vos.dto.SealCarDto;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.enums.seal.LineTypeEnum;
import com.jdl.jy.realtime.enums.seal.VehicleStatusEnum;
import com.jdl.jy.realtime.model.es.seal.SealCarMonitor;
import com.jdl.jy.realtime.model.query.seal.SealVehicleTaskQuery;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.contrib.javanica.conf.HystrixPropertiesManager;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.record.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * @ClassName JySealVehicleServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/3/11 14:30
 **/
@Service
public class JySealVehicleServiceImpl implements IJySealVehicleService {

    private static final Logger log = LoggerFactory.getLogger(JySealVehicleServiceImpl.class);

    /**
     * 查询几天内的带解任务（负数）
     * */
    @Value("${newSealVehicleResource.rollBackDay:-7}")
    private int rollBackDay;

    private static final int STATUS = 10;

    @Autowired
    @Qualifier("jySealVehicleManager")
    private IJySealVehicleManager jySealVehicleManager;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

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

        SealVehicleTaskQuery query = assembleCommandCondition(request);

        if (isSearch(request)) {
            if (BusinessUtil.isSealBoxNo(request.getBarCode())) {
                List<String> sealCarCodeList = getSealCarCodeFromVos(result, request);
                if (!result.codeSuccess()) {
                    return result;
                }
                query.setSealCarCode(sealCarCodeList);
            }
            else {
                query.setVehicleNumberLastFour(request.getBarCode());
            }
        }
        else if (isRefresh(request)) {
            query.setVehicleStatus(request.getVehicleStatus());
            query.setLineType(request.getLineType());
            // 查询最近6小时的待解封车任务
            query.setQueryTimeStart(DateHelper.newTimeRangeHoursAgo(new Date(), 6));
        }

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
            StopWatch stopWatch = new StopWatch("CV-SealVehicleTaskResponse");
            stopWatch.start();
            SealVehicleTaskResponse taskResponse = JsonHelper.fromJson(JsonHelper.toJson(serviceResult), SealVehicleTaskResponse.class);
            stopWatch.stop();
            log.info(stopWatch.prettyPrint());

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

    private boolean isRefresh(SealVehicleTaskRequest request) {
        return SealVehicleTaskQuery.FETCH_TYPE_REFRESH.equals(request.getFetchType());
    }

    private boolean isSearch(SealVehicleTaskRequest request) {
        return SealVehicleTaskQuery.FETCH_TYPE_SEARCH.equals(request.getFetchType());
    }

    private SealVehicleTaskQuery assembleCommandCondition(SealVehicleTaskRequest request) {
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
            }

            Profiler.businessAlarm("dms.web.IJySealVehicleService.fallback", msgConcat.toString());

            SealCarDto sealCarQuery = getSealCarDto(request);
            PageDto<SealCarDto> queryPageDto = getSealCarDtoPageDto(request.getPageNumber(), request.getPageSize());
            PageDto<SealCarDto> pageDto = getSealTaskFromVos(invokeResult, sealCarQuery, queryPageDto);
            if (!invokeResult.codeSuccess()) {
                return invokeResult;
            }

            SealVehicleTaskResponse response = makeSealResponseUsingVos(request, pageDto);

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
     * @return
     */
    private SealVehicleTaskResponse makeSealResponseUsingVos(SealVehicleTaskRequest request, PageDto<SealCarDto> pageDto) {
        SealVehicleTaskResponse response = new SealVehicleTaskResponse();
        List<VehicleStatusStatis> statusStatisList = new ArrayList<>();
        response.setStatusStatis(statusStatisList);
        VehicleStatusStatis statusStatis = new VehicleStatusStatis();
        statusStatisList.add(statusStatis);
        statusStatis.setVehicleStatus(VehicleStatusEnum.TO_SEAL.getCode());
        statusStatis.setVehicleStatusName(VehicleStatusEnum.TO_SEAL.getName());

        List<SealCarDto> filterList = new ArrayList<>();
        if (isSearch(request)) {
            for (SealCarDto sealCar : pageDto.getResult()) {
                if (filterBySealCode(request, sealCar) || filterByVehicleNumber(request, sealCar)) {
                    filterList.add(sealCar);
                }
            }
            statusStatis.setTotal((long)filterList.size());
        }
        else if (isRefresh(request)) {
            filterList = pageDto.getResult();
            statusStatis.setTotal((long) pageDto.getTotalRow());
        }

        sealCarDtoToResponse(response, filterList);

        return response;
    }

    /**
     * 根据封签号获得封车编码
     * @param result
     * @param request
     * @return
     */
    private List<String> getSealCarCodeFromVos(InvokeResult<SealVehicleTaskResponse> result, SealVehicleTaskRequest request) {
        SealCarDto sealCarQuery = getSealCarDto(request);
        PageDto<SealCarDto> queryPageDto = getSealCarDtoPageDto(request.getPageNumber(), request.getPageSize());
        PageDto<SealCarDto> pageDto = getSealTaskFromVos(result, sealCarQuery, queryPageDto);
        if (!result.codeSuccess()) {
            return null;
        }
        List<SealCarDto> filterList = new ArrayList<>();
        for (SealCarDto sealCar : pageDto.getResult()) {
            if (filterBySealCode(request, sealCar)) {
                filterList.add(sealCar);
            }
        }
        if (CollectionUtils.isEmpty(filterList)) {
            result.error("该封签号没有待解封车任务");
            return null;
        }

        Set<String> sealCarCodeSet = new HashSet<>();
        for (SealCarDto sealCarDto : filterList) {
            sealCarCodeSet.add(sealCarDto.getSealCarCode());
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

        return null;
    }

    /**
     * 按搜索条件过滤封车任务
     * @param request
     * @param sealCar
     * @return
     */
    private boolean filterBySealCode(SealVehicleTaskRequest request, SealCarDto sealCar) {
        if (CollectionUtils.isNotEmpty(sealCar.getSealCodes()) && sealCar.getSealCodes().contains(request.getBarCode())) {
            return true;
        }

        return false;
    }

    private boolean filterByVehicleNumber(SealVehicleTaskRequest request, SealCarDto sealCar) {
        if (!BusinessUtil.isSealBoxNo(request.getBarCode())) {
            if (StringUtils.isNotBlank(sealCar.getVehicleNumber())
                    && sealCar.getVehicleNumber().length() > 4 && request.getBarCode().equals(StringUtils.substring(sealCar.getVehicleNumber(), - 4))) {
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
