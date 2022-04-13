package com.jd.bluedragon.distribution.jy.service.unload;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.BarCodeLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.UnloadBarCodeScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.LineTypeStatis;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleBaseInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.constants.RedisHashKeyConstants;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadAggsDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadDetailCache;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadTaskCompleteDto;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.JyUnloadTaskSignConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
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
    private JyUnloadAggsDao unloadAggDao;

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
        if (WaybillUtil.isPackageCode(request.getBarCode())) {
            String sealCode = getSealCarCodeFromEs(request);
            if (StringUtils.isBlank(sealCode)) {
                result.parameterError("该包裹号不存在关联的卸车任务！");
                return result;
            }
        }

        try {
            List<JyBizTaskUnloadCountDto> vehicleStatusAggList =
                    unloadVehicleService.findStatusCountByCondition4Status(condition, JyBizTaskUnloadStatusEnum.UNLOAD_STATUS_OPTIONS.toArray(new JyBizTaskUnloadStatusEnum[JyBizTaskUnloadStatusEnum.UNLOAD_STATUS_OPTIONS.size()]));
            if (CollectionUtils.isEmpty(vehicleStatusAggList)) {
                return result;
            }

            UnloadVehicleTaskResponse response = new UnloadVehicleTaskResponse();

            // 封车状态数量统计
            assembleUnloadStatusAgg(vehicleStatusAggList, response);

            // 按卸车状态组装车辆数据
            assembleUnloadVehicleData(request, condition, response);

            result.setData(response);
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
     * @param condition
     * @param response
     */
    private void assembleUnloadVehicleData(UnloadVehicleTaskRequest request, JyBizTaskUnloadVehicleEntity condition, UnloadVehicleTaskResponse response) {
        JyBizTaskUnloadStatusEnum curQueryStatus = JyBizTaskUnloadStatusEnum.getEnumByCode(request.getVehicleStatus());
        List<LineTypeStatis> lineTypeList = getVehicleLineTypeList(condition, curQueryStatus);
        UnloadVehicleData unloadVehicleData = new UnloadVehicleData();
        unloadVehicleData.setVehicleStatus(curQueryStatus.getCode());
        unloadVehicleData.setLineStatistics(lineTypeList);

        // 按车辆状态组装
        makeVehicleList(condition, request, curQueryStatus, unloadVehicleData);

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
     */
    private void makeVehicleList(JyBizTaskUnloadVehicleEntity condition, UnloadVehicleTaskRequest request,
                                 JyBizTaskUnloadStatusEnum curQueryStatus, UnloadVehicleData unloadVehicleData) {
        List<VehicleBaseInfo> vehicleList = Lists.newArrayList();
        unloadVehicleData.setData(vehicleList);

        JyBizTaskUnloadOrderTypeEnum orderTypeEnum = setTaskOrderType(curQueryStatus);
        List<JyBizTaskUnloadVehicleEntity> vehiclePageList = unloadVehicleService.findByConditionOfPage(condition, orderTypeEnum, request.getPageNumber(), request.getPageSize());
        if (CollectionUtils.isEmpty(vehiclePageList)) {
            return;
        }

        for (JyBizTaskUnloadVehicleEntity entity : vehiclePageList) {
            VehicleBaseInfo vehicleBaseInfo = assembleVehicleBase(curQueryStatus, entity);

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
                    unloadVehicleInfo.setUnloadProgress(entity.getUnloadProgress());
                    unloadVehicleInfo.setTaskId(getJyScheduleTaskId(entity.getBizId()));
                    unloadVehicleInfo.setBizId(entity.getBizId());
                    vehicleList.add(unloadVehicleInfo);
                    break;
                case UN_LOAD_DONE:
                    UnloadCompleteVehicle completeVehicle = (UnloadCompleteVehicle) vehicleBaseInfo;
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
        if (BusinessUtil.isSignY(tagSign, JyUnloadTaskSignConstants.POSITION_1)) {
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

        return tagList;
    }

    private List<LineTypeStatis> getVehicleLineTypeList(JyBizTaskUnloadVehicleEntity condition, JyBizTaskUnloadStatusEnum curQueryStatus) {
        List<LineTypeStatis> lineTypeList = new ArrayList<>();
        List<JyBizTaskUnloadCountDto> lineTypeAgg = unloadVehicleService.findStatusCountByCondition4StatusAndLine(condition, curQueryStatus);
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

        return condition;
    }

    /**
     * 根据包裹号从ES获得封车编码
     * @param request
     * @return
     */
    private String getSealCarCodeFromEs(UnloadVehicleTaskRequest request) {
        JyVehicleTaskUnloadDetail query = new JyVehicleTaskUnloadDetail();
        query.setPackageCode(request.getBarCode());
        JyVehicleTaskUnloadDetail unloadDetail = unloadVehicleManager.findOneUnloadDetail(query);
        if (unloadDetail != null) {
            return unloadDetail.getSealCarCode();
        }

        return null;
    }

    private boolean checkBeforeFetchTask(UnloadVehicleTaskRequest request, InvokeResult<UnloadVehicleTaskResponse> result) {
        if (!NumberHelper.gt0(request.getVehicleStatus())) {
            result.parameterError("请选择车辆状态");
            return false;
        }
        if (!NumberHelper.gt0(request.getPageSize()) || !NumberHelper.gt0(request.getPageSize())) {
            result.parameterError("缺少分页参数");
            return false;
        }
        if (!NumberHelper.gt0(request.getEndSiteCode())) {
            result.parameterError("缺少目的场地");
            return false;
        }

        return true;
    }

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJyUnloadVehicleService.unloadScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Integer> unloadScan(UnloadScanRequest request) {

        logInfo("开始卸车扫描. {}", JsonHelper.toJson(request));

        InvokeResult<Integer> result = new InvokeResult<>();

        JyBizTaskUnloadVehicleEntity taskUnloadVehicle = unloadVehicleService.findByBizId(request.getBizId());
        if (taskUnloadVehicle == null) {
            result.hintMessage("卸车任务不存在，请刷新卸车任务列表后再扫描！");
            return result;
        }

        // 卸车扫描前置校验
        if (!checkBeforeScan(result, request)) {
            return result;
        }


        try {
            // 保存扫描记录，发运单全程跟踪。首次扫描分配卸车任务
            UnloadScanDto unloadScanDto = createUnloadDto(request, taskUnloadVehicle);
            unloadScanProducer.sendOnFailPersistent(unloadScanDto.getBarCode(), JsonHelper.toJson(unloadScanDto));

            // 统计本次扫描的包裹数
            result.setData(calculateScanPackageCount(request));

            // 记录卸车任务扫描进度
            recordUnloadProgress(result.getData(), request, taskUnloadVehicle);
        }
        catch (Exception ex) {
            log.error("卸车扫描失败. {}", JsonHelper.toJson(request), ex);
            result.error("服务器异常，卸车扫描失败，请咚咚联系分拣小秘！");
            return result;
        }

        return result;
    }

    /**
     * 更新PDA卸车扫描进度
     * @param pdaUnloadCount
     * @param request
     * @param taskUnloadVehicle
     */
    private void recordUnloadProgress(Integer pdaUnloadCount, UnloadScanRequest request, JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
        String pdaOpeCacheKey = genPdaUnloadProgressCacheKey(request.getBizId());
        if (redisClientOfJy.exists(pdaOpeCacheKey)) {

            redisClientOfJy.hIncrBy(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_COUNT, pdaUnloadCount);
            redisClientOfJy.expire(pdaOpeCacheKey, UNLOAD_CACHE_EXPIRE, TimeUnit.HOURS);

            logInfo("更新PDA本地卸车扫描进度. {}-{}", JsonHelper.toJson(request), pdaUnloadCount);
        }
        else {
            // 长时间未扫描，本地扫描记录过期，以agg为准
            JyUnloadAggsEntity unloadAggEntity = sumUnloadAgg(request.getBizId());
            Integer unloadCount = calculateRightUnloadCount(pdaUnloadCount, unloadAggEntity);

            Map<String, String> redisHashMap = new HashMap<>();
            redisHashMap.put(RedisHashKeyConstants.UNLOAD_TOTAL_COUNT, String.valueOf(taskUnloadVehicle.getTotalCount()));
            redisHashMap.put(RedisHashKeyConstants.UNLOAD_COUNT, String.valueOf(unloadCount));

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
        JyUnloadAggsEntity retAgg = new JyUnloadAggsEntity();

        List<JyUnloadAggsEntity> aggList = unloadAggDao.queryByBizId(new JyUnloadAggsEntity(bizId));
        if (CollectionUtils.isEmpty(aggList)) {
            return retAgg;
        }

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
     * @param result
     * @param request
     * @return
     */
    private boolean checkBeforeScan(InvokeResult<Integer> result, UnloadScanRequest request) {
        String barCode = request.getBarCode();
        int siteCode = request.getCurrentOperate().getSiteCode();

        // 一个单号只能扫描一次
        if (checkBarScannedAlready(barCode, siteCode)) {
            result.hintMessage("单号已扫描！");
            return false;
        }

        return true;
    }

    /**
     * 统计本次扫描的包裹数量
     * @param request
     */
    private Integer calculateScanPackageCount(UnloadScanRequest request) {
        String barCode = request.getBarCode();
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
        unloadScanDto.setOperateTime(operateTime);
        unloadScanDto.setCreateUserErp(request.getUser().getUserErp());
        unloadScanDto.setCreateUserName(request.getUser().getUserName());
        unloadScanDto.setUpdateUserErp(request.getUser().getUserErp());
        unloadScanDto.setUpdateUserName(request.getUser().getUserName());
        unloadScanDto.setCreateTime(operateTime);
        unloadScanDto.setUpdateTime(operateTime);

        unloadScanDto.setGroupCode(request.getGroupCode());
        unloadScanDto.setTaskId(request.getTaskId());

        return unloadScanDto;
    }

    /**
     * 校验卸车是否已经扫描过该单号
     * @param barCode 包裹、运单、箱号
     * @param siteCode 操作场地
     * @return true：扫描过
     */
    private boolean checkBarScannedAlready(String barCode, int siteCode) {
        boolean alreadyScanned = false;
        // 同场地一个单号只能扫描一次
        String mutexKey = String.format(CacheKeyConstants.JY_UNLOAD_SCAN_KEY, barCode, siteCode);
        if (redisClientOfJy.set(mutexKey, "1", UNLOAD_SCAN_BAR_EXPIRE, TimeUnit.HOURS, false)) {
            JyUnloadEntity queryDb = new JyUnloadEntity(barCode, (long) siteCode);
            if (jyUnloadDao.queryByCodeAndSite(queryDb) != null) {
                alreadyScanned = true;
            }
        }
        else {
            alreadyScanned = true;
        }

        return alreadyScanned;
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

        // 暂时去掉缓存读数据库。需要在接收agg时写入缓存。
        Map<String, String> hashRedisMap = Maps.newHashMap();

        if (MapUtils.isEmpty(hashRedisMap)) {
            log.warn("卸车进度缓存不存在主动刷新. {}", JsonHelper.toJson(request));
            refreshUnloadAggCache(request.getBizId());
        }
        String progressCacheKey = genUnloadDetailCacheKey(request.getBizId());
        hashRedisMap = redisClientOfJy.hGetAll(progressCacheKey);

        try {
            UnloadDetailCache redisCache = RedisHashUtils.mapConvertBean(hashRedisMap, UnloadDetailCache.class);

            UnloadScanDetail scanProgress = JsonHelper.fromJson(JsonHelper.toJson(redisCache), UnloadScanDetail.class);
            result.setData(scanProgress);
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

        // 比较PDA扫描的进度和Flink计算出的进度
        String pdaOpeCacheKey = genPdaUnloadProgressCacheKey(bizId);
        if (redisClientOfJy.exists(pdaOpeCacheKey)) {
            String redisVal = redisClientOfJy.hGet(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_COUNT);
            if (StringUtils.isNotBlank(redisVal) && NumberHelper.isNumber(redisVal)) {
                Integer pdaScannedPackageCount = Integer.valueOf(redisVal);
                // PDA进度快以PDA为准
                Integer rightUnloadCount = calculateRightUnloadCount(pdaScannedPackageCount, unloadAggEntity);

                unloadAggEntity.setTotalScannedPackageCount(rightUnloadCount);
            }
        }

        logInfo("init unload progress from unload agg. {}", JsonHelper.toJson(unloadAggEntity));

        return initScanDetailCacheUsingUnloadAgg(unloadAggEntity);
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
            UnloadDetailCache cacheEntity = new UnloadDetailCache();
            cacheEntity.setBizId(unloadAggEntity.getBizId());
            cacheEntity.setInterceptShouldScanCount(unloadAggEntity.getInterceptShouldScanCount());
            cacheEntity.setInterceptActualScanCount(unloadAggEntity.getInterceptActualScanCount());
            cacheEntity.setMoreScanLocalCount(unloadAggEntity.getMoreScanLocalCount());
            cacheEntity.setMoreScanOutCount(unloadAggEntity.getMoreScanOutCount());
            cacheEntity.setTotalCount(unloadAggEntity.getTotalSealPackageCount());
            cacheEntity.setUnloadCount(unloadAggEntity.getTotalScannedPackageCount());

            Map<String, String> redisHashMap = RedisHashUtils.objConvertToMap(cacheEntity);
            String unloadDetailCacheKey = genUnloadDetailCacheKey(unloadAggEntity.getBizId());
            redisClientOfJy.hMSet(unloadDetailCacheKey, redisHashMap);
            redisClientOfJy.expire(unloadDetailCacheKey, UNLOAD_CACHE_EXPIRE, TimeUnit.HOURS);
        }
        catch (Exception ex) {
            log.error("初始化卸车进度缓存异常. {}", JsonHelper.toJson(unloadAggEntity), ex);
            return false;
        }

        return true;
    }

    private String genUnloadDetailCacheKey(String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_DETAIL_KEY, bizId);
    }

    private String genPdaUnloadProgressCacheKey(String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_PDA_AGG_KEY, bizId);
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
            List<JyUnloadAggsEntity> unloadAggList = unloadAggDao.queryByBizId(new JyUnloadAggsEntity(request.getBizId()));
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
            List<JyUnloadAggsEntity> unloadAggList = unloadAggDao.queryByBizId(new JyUnloadAggsEntity(request.getBizId()));
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
            item.setCount(dealMinus(aggEntity.getShouldScanCount(), aggEntity.getActualScanCount()));
            productTypeList.add(item);
        }
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
            if (unloadDetail.getScannedFlag() == Constants.CONSTANT_NUMBER_ONE) {
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

            List<JyUnloadAggsEntity> unloadAggList = unloadAggDao.queryByBizId(new JyUnloadAggsEntity(request.getBizId()));
            if (CollectionUtils.isEmpty(unloadAggList)) {
                log.warn("判断卸车任务是否完成查询AGG为空.{}", JsonHelper.toJson(request));
                return result;
            }

            // 判断卸车任务是否异常.
            Boolean unloadTaskNormal = judgeUnloadTaskNormal(previewData, unloadAggList);

            // 卸车任务正常，直接返回
            if (unloadTaskNormal) {
                previewData.setAbnormalFlag(Constants.NUMBER_ZERO.byteValue());
                return result;
            }

            Pager<JyVehicleTaskUnloadDetail> query = getMoreScanAndToScanBarCodeCondition(request);
            Pager<JyVehicleTaskUnloadDetail> retPager = unloadVehicleManager.queryMoreScanAndToScanBarCodeDetail(query);
            if (retPager == null || CollectionUtils.isEmpty(retPager.getData())) {
                return result;
            }

            previewData.setBarCodeList(getUnloadScanBarCodeList(UnloadBarCodeQueryEntranceEnum.COMPLETE_PREVIEW, retPager.getData()));
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
     * 判断卸车任务是否正常。同时满足一下三个条件为正常
     * <ul>
     *     <li>待扫包裹数==0</li>
     *     <li>本场地多扫==0</li>
     *     <li>非本场地多扫==0</li>
     * </ul>
     * @param previewData
     * @param unloadAggList
     * @return true:正常
     */
    private Boolean judgeUnloadTaskNormal(UnloadPreviewData previewData, List<JyUnloadAggsEntity> unloadAggList) {
        long existToScanRows = 0;
        long existLocalMoreScanRows = 0;
        long existOutMoreScanRows = 0;
        for (JyUnloadAggsEntity aggEntity : unloadAggList) {
            if (!Objects.equals(aggEntity.getTotalScannedPackageCount(), aggEntity.getTotalSealPackageCount())) {
                existToScanRows ++;
            }
            if (NumberHelper.gt0(aggEntity.getMoreScanLocalCount())) {
                existLocalMoreScanRows ++;
            }
            if (NumberHelper.gt0(aggEntity.getMoreScanOutCount())) {
                existOutMoreScanRows ++;
            }
        }
        boolean unloadDone = existToScanRows == 0 && existLocalMoreScanRows == 0 && existOutMoreScanRows == 0;

        JyUnloadAggsEntity oneUnloadAgg = unloadAggList.get(0);
        logInfo("卸车任务卸车进度. biz: {}, progress: {}-{}-{}", oneUnloadAgg.getBizId(), existToScanRows, existLocalMoreScanRows, existOutMoreScanRows);

        if (!unloadDone) {
            previewData.setTotalScan(oneUnloadAgg.getTotalScannedPackageCount().longValue());
            previewData.setMoreScanCount(existOutMoreScanRows + existLocalMoreScanRows);
            previewData.setShouldScanCount(dealMinus(oneUnloadAgg.getTotalSealPackageCount(), oneUnloadAgg.getTotalScannedPackageCount()));
            previewData.setAbnormalCount(previewData.getMoreScanCount() + previewData.getShouldScanCount());
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
                && null == request.getShouldScanCount()
                && null == request.getMoreScanCount()) {
            invokeResult.parameterError("卸车任务异常时需要异常数量");
            return invokeResult;
        }

        try {
            // 保存卸车任务是否异常，卸车完成时间，关闭卸车小组 etc.
            recordUnloadCompleteStatus(request);

            // 同步请求关闭卸车任务
            invokeResult.setData(completeTask(request));
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
        completeDto.setMoreScanCount(request.getMoreScanCount());
        completeDto.setShouldScanCount(request.getShouldScanCount());
        completeDto.setOperateTime(new Date());
        completeDto.setOperateUserErp(request.getUser().getUserErp());
        completeDto.setOperateUserName(request.getUser().getUserName());
        unloadTaskCompleteProducer.sendOnFailPersistent(completeDto.getBizId(), JsonHelper.toJson(completeDto));
    }

    private boolean completeTask(UnloadCompleteRequest request) {
        JyBizTaskUnloadDto completeDto = new JyBizTaskUnloadDto();
        completeDto.setBizId(request.getBizId());
        completeDto.setOperateUserErp(request.getUser().getUserErp());
        completeDto.setOperateUserName(request.getUser().getUserName());
        completeDto.setOperateTime(new Date());
        return transactionManager.completeUnloadTask(completeDto);
    }
}
