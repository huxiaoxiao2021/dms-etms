package com.jd.bluedragon.distribution.jy.service.unload;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.LineTypeStatis;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleBaseInfo;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.common.dto.select.SelectOption;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.coldChain.domain.InspectionVO;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.inspection.service.InspectionJsfService;
import com.jd.bluedragon.distribution.jy.constants.RedisHashKeyConstants;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadAggsDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadDetailCache;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadOrderTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyLineTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.UnloadProductTypeEnum;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskDistributionTypeEnum;
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
 * @ClassName UnloadVehicleServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/4/2 17:04
 **/
@Service
public class JyUnloadVehicleServiceImpl implements IJyUnloadVehicleService {

    private static final Logger log = LoggerFactory.getLogger(JyUnloadVehicleServiceImpl.class);

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    private JyUnloadDao jyUnloadDao;

    @Autowired
    private JyBizTaskUnloadVehicleService unloadVehicleService;

    @Autowired
    private JyUnloadAggsDao unloadAggDao;

    @Qualifier("inspectionJsfServiceProvider")
    @Autowired
    private InspectionJsfService inspectionService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.fetchUnloadTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<UnloadVehicleTaskResponse> fetchUnloadTask(UnloadVehicleTaskRequest request) {
        InvokeResult<UnloadVehicleTaskResponse> result = new InvokeResult<>();

        if (!checkBeforeFetchTask(request, result)) {
            return result;
        }

        logInfo("拉取卸车任务. {}", JsonHelper.toJson(request));

        JyBizTaskUnloadVehicleEntity condition = makeFetchCondition(request);
        if (WaybillUtil.isPackageCode(request.getBarCode())) {
            // TODO get sealCarCode from es
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
            case UN_LOADING:
                response.setUnloadVehicleData(unloadVehicleData);
            case UN_LOAD_DONE:
                response.setUnloadCompletedData(unloadVehicleData);
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
                    vehicleList.add(toUnloadVehicle);
                case UN_LOADING:
                    UnloadVehicleInfo unloadVehicleInfo = (UnloadVehicleInfo) vehicleBaseInfo;
                    unloadVehicleInfo.setManualCreatedTask(entity.unloadWithoutTask());
                    unloadVehicleInfo.setTags(resolveTagSign(entity.getTagsSign()));
                    unloadVehicleInfo.setUnloadProgress(entity.getUnloadProgress());
                    unloadVehicleInfo.setTaskId(getJyScheduleTaskId(entity.getBizId()));
                    vehicleList.add(unloadVehicleInfo);
                case UN_LOAD_DONE:
                    UnloadCompleteVehicle completeVehicle = (UnloadCompleteVehicle) vehicleBaseInfo;
                    completeVehicle.setLessCount(entity.getLessCount());
                    completeVehicle.setMoreCount(entity.getMoreCount());
                    completeVehicle.setManualCreatedTask(entity.unloadWithoutTask());
                    completeVehicle.setAbnormalFlag(entity.unloadAbnormal());
                    completeVehicle.setUnloadFinishTime(entity.getUnloadFinishTime());
                    completeVehicle.setTaskId(getJyScheduleTaskId(entity.getBizId()));
                    vehicleList.add(completeVehicle);
            }
        }
    }

    /**
     * 查询调度任务ID
     * @param bizId
     * @return
     */
    private String getJyScheduleTaskId(String bizId) {
        // TODO 查询调度任务ID
        String taskId = StringUtils.EMPTY;

        return taskId;
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
            case UN_LOADING:
                vehicleBaseInfo = new UnloadVehicleInfo();
            case UN_LOAD_DONE:
                vehicleBaseInfo = new UnloadCompleteVehicle();
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
    private List<SelectOption> resolveTagSign(String tagSign) {
        // TODO 解析tagSign
        List<SelectOption> tagList = new ArrayList<>();

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
        if (!WaybillUtil.isPackageCode(request.getBarCode())) {
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
        String sealCarCode = null;

        return sealCarCode;
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.unloadScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Integer> unloadScan(UnloadScanRequest request) {

        logInfo("开始卸车扫描. {}", JsonHelper.toJson(request));

        InvokeResult<Integer> result = new InvokeResult<>();
        // 卸车扫描前置校验
        if (checkBeforeScan(result, request)) {
            return result;
        }

        JyBizTaskUnloadVehicleEntity taskUnloadVehicle = unloadVehicleService.findByBizId(request.getBizId());
        if (taskUnloadVehicle == null) {
            result.hintMessage("卸车任务不存在，请刷新卸车任务列表后再扫描！");
            return result;
        }

        JyUnloadEntity dbEntity = createUnloadEntity(request, taskUnloadVehicle);
        try {
            // 保存扫描记录，发运单全程跟踪
            scanCoreLogic(request, dbEntity);

            // 统计本次扫描的包裹数
            calculateScanCount(result, request);

            // 记录卸车任务扫描进度
            recordUnloadProgress(result, request, taskUnloadVehicle);
        }
        // TODO catch specific exception
//        catch () {
//
//        }
        catch (Exception ex) {
            log.error("卸车扫描失败. {}", JsonHelper.toJson(request), ex);
            result.error("服务器异常，卸车扫描失败，请咚咚联系分拣小秘！");
            return result;
        }

        return result;
    }

    /**
     * 更新PDA卸车扫描进度
     * @param result
     * @param request
     * @param taskUnloadVehicle
     */
    private void recordUnloadProgress(InvokeResult<Integer> result, UnloadScanRequest request, JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
        String pdaOpeCacheKey = genPdaUnloadProgressCacheKey(request.getBizId());
        if (redisClientOfJy.exists(pdaOpeCacheKey)) {
            redisClientOfJy.hIncrBy(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_COUNT, result.getData());
            redisClientOfJy.expire(pdaOpeCacheKey, 10, TimeUnit.HOURS);

            logInfo("更新PDA本地卸车扫描进度. {}-{}", JsonHelper.toJson(request), JsonHelper.toJson(result));
        }
        else {
            JyUnloadAggsEntity queryAgg = new JyUnloadAggsEntity(request.getBizId());
            JyUnloadAggsEntity unloadAggEntity = unloadAggDao.aggByBiz(queryAgg);
            Integer unloadCount = calculateRightUnloadCount(result, unloadAggEntity);

            Map<String, String> redisHashMap = new HashMap<>();
            redisHashMap.put(RedisHashKeyConstants.UNLOAD_TOTAL_COUNT, String.valueOf(taskUnloadVehicle.getTotalCount()));
            redisHashMap.put(RedisHashKeyConstants.UNLOAD_COUNT, String.valueOf(unloadCount));

            redisClientOfJy.hMSet(pdaOpeCacheKey, redisHashMap);
            redisClientOfJy.expire(pdaOpeCacheKey, 10, TimeUnit.HOURS);

            logInfo("初始化PDA本地卸车扫描进度. {}-{}", JsonHelper.toJson(request), JsonHelper.toJson(result));
        }
    }

    /**
     *
     * @param result
     * @param unloadAggEntity
     * @return
     */
    private Integer calculateRightUnloadCount(InvokeResult<Integer> result, JyUnloadAggsEntity unloadAggEntity) {
        Integer unloadCount = result.getData();
        if (unloadAggEntity != null && NumberHelper.gt0(unloadAggEntity.getTotalScannedPackageCount())) {
            if (NumberHelper.gt(unloadAggEntity.getTotalScannedPackageCount(), unloadCount)) {
                unloadCount = unloadAggEntity.getTotalScannedPackageCount();

                logInfo("卸车已扫描数量从jy_unload_agg获取. {}-{}", JsonHelper.toJson(result), JsonHelper.toJson(unloadAggEntity));
            }
        }

        return unloadCount;
    }

    /**
     * 保存扫描记录，发运单全程跟踪
     * @param request
     * @param dbEntity
     */
    private void scanCoreLogic(UnloadScanRequest request, JyUnloadEntity dbEntity) {
        boolean firstScanFromTask = judgeBarCodeIsFirstScanFromTask(request);
        if (jyUnloadDao.insert(dbEntity) > 0) {

            // 插入验货或收货任务，发运单全程跟踪
            com.jd.bluedragon.distribution.jsf.domain.InvokeResult<Boolean> taskResult = addInspectionTask(request, dbEntity);
            if (!taskResult.codeSuccess()) {
                Profiler.businessAlarm("dms.web.IJyUnloadVehicleService.unloadScan.addInspectionTask", "卸车扫描插入验货任务失败，将重试");
                log.warn("卸车扫描插入验货任务失败，将重试. {}，{}", JsonHelper.toJson(request), JsonHelper.toJson(taskResult));

                // 再次尝试插入任务
                try {
                    Thread.sleep(100);
                    addInspectionTask(request, dbEntity);
                }
                catch (InterruptedException e) {
                    log.error("再次插入卸车验货任务异常. {}", JsonHelper.toJson(request), e);
                }
            }

            // 首次扫描变更任务状态，该小组绑定任务
            if (firstScanFromTask) {
                logInfo("任务[{}]首次开始扫描，修改任务状态，锁定任务。{}", request.getBizId(), request.getBarCode());

                // TODO liuduo8 创建任务，分配任务  request.getGroupCode()
                // TODO 更新状态抛异常删除扫描缓存  JY_UNLOAD_BIZ_KEY
            }
        }
    }

    /**
     * 扫描前校验
     * @param result
     * @param request
     * @return
     */
    private boolean checkBeforeScan(InvokeResult<Integer> result, UnloadScanRequest request) {
        String barCode = request.getBarCode();
        if (StringUtils.isBlank(barCode)) {
            result.parameterError("请扫描单号！");
            return false;
        }
        if (!BusinessHelper.isBoxcode(barCode)
                && !WaybillUtil.isWaybillCode(barCode)
                && !WaybillUtil.isPackageCode(barCode)) {
            result.parameterError("扫描单号非法！");
            return false;
        }

        if (StringUtils.isBlank(request.getBizId()) || StringUtils.isBlank(request.getSealCarCode())) {
            result.parameterError("请选择卸车任务！");
            return false;
        }

        int siteCode = request.getCurrentOperate().getSiteCode();
        if (!NumberHelper.gt0(siteCode)) {
            result.parameterError("缺少操作场地！");
            return false;
        }

        // 一个单号只能扫描一次
        if (checkBarScannedAlready(barCode, siteCode)) {
            result.hintMessage("单号已扫描！");
            return false;
        }

        if (StringUtils.isBlank(request.getGroupCode())) {
            result.hintMessage("扫描前请绑定小组！");
            return false;
        }

        return true;
    }

    /**
     * 统计本次扫描的数量
     * @param result
     * @param request
     */
    private void calculateScanCount(InvokeResult<Integer> result, UnloadScanRequest request) {
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

        result.setData(scanCount);
    }

    /**
     * 插入验货或收货任务
     * @param request
     * @param dbEntity
     * @return
     */
    private com.jd.bluedragon.distribution.jsf.domain.InvokeResult<Boolean> addInspectionTask(UnloadScanRequest request, JyUnloadEntity dbEntity) {
        InspectionVO inspectionVO = new InspectionVO();
        inspectionVO.setBarCodes(Collections.singletonList(request.getBarCode()));
        inspectionVO.setSiteCode(request.getCurrentOperate().getSiteCode());
        inspectionVO.setSiteName(request.getCurrentOperate().getSiteName());
        inspectionVO.setUserCode(request.getUser().getUserCode());
        inspectionVO.setUserName(request.getUser().getUserName());
        inspectionVO.setOperateTime(DateHelper.formatDateTime(dbEntity.getOperateTime()));
        return inspectionService.inspection(inspectionVO, InspectionBizSourceEnum.JY_UNLOAD_INSPECTION);
    }

    /**
     * 判断该单号是否是本次卸车任务扫描的第一单
     * @param request
     * @return
     */
    private boolean judgeBarCodeIsFirstScanFromTask(UnloadScanRequest request) {
        boolean firstScanned = false;
        String mutexKey = String.format(CacheKeyConstants.JY_UNLOAD_BIZ_KEY, request.getBizId());
        if (redisClientOfJy.set(mutexKey, "1", 5, TimeUnit.MINUTES, false)) {
            JyUnloadEntity queryDb = new JyUnloadEntity(request.getBizId());
            if (jyUnloadDao.findByBizId(queryDb) == null) {
                firstScanned = true;
            }
        }

        return firstScanned;
    }

    private JyUnloadEntity createUnloadEntity(UnloadScanRequest request, JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
        Date operateTime = new Date();
        JyUnloadEntity unloadEntity = new JyUnloadEntity();
        unloadEntity.setBizId(request.getBizId());
        unloadEntity.setSealCarCode(request.getSealCarCode());
        unloadEntity.setVehicleNumber(taskUnloadVehicle.getVehicleNumber());
        unloadEntity.setStartSiteId(taskUnloadVehicle.getStartSiteId());
        unloadEntity.setEndSiteId(taskUnloadVehicle.getEndSiteId());
        unloadEntity.setManualCreatedFlag(taskUnloadVehicle.getManualCreatedFlag());
        unloadEntity.setOperateSiteId((long) request.getCurrentOperate().getSiteCode());
        unloadEntity.setBarCode(request.getBarCode());
        unloadEntity.setOperateTime(operateTime);
        unloadEntity.setCreateUserErp(request.getUser().getUserErp());
        unloadEntity.setCreateUserName(request.getUser().getUserName());
        unloadEntity.setUpdateUserErp(request.getUser().getUserErp());
        unloadEntity.setUpdateUserName(request.getUser().getUserName());
        unloadEntity.setCreateTime(operateTime);
        unloadEntity.setUpdateTime(operateTime);

        return unloadEntity;
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
        if (redisClientOfJy.set(mutexKey, "1", 6, TimeUnit.HOURS, false)) {
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.unloadDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<UnloadScanDetail> unloadDetail(UnloadCommonRequest request) {
        InvokeResult<UnloadScanDetail> result = new InvokeResult<>();
        if (StringUtils.isBlank(request.getBizId())) {
            result.parameterError("加载卸车进度缺少任务编码!");
            return result;
        }

        String progressCacheKey = genUnloadDetailCacheKey(request.getBizId());
        Map<String, String> hashRedisMap = redisClientOfJy.hGetAll(progressCacheKey);

        if (MapUtils.isEmpty(hashRedisMap)) {
            log.warn("卸车进度缓存不存在手动刷新. {}", JsonHelper.toJson(request));
            refreshUnloadAggCache(request.getBizId());
        }

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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.createUnloadTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean createUnloadTask(JyBizTaskUnloadDto dto){

        if(dto.getManualCreatedFlag() != null && Integer.valueOf(1).equals(dto.getManualCreatedFlag())){
            //无任务模式
            //计算生成BIZ_ID
            JyBizTaskUnloadVehicleEntity taskUnloadVehicleEntity = jyBizTaskUnloadVehicleService.initTaskByNoTask(dto);
            if(taskUnloadVehicleEntity == null){
                //初始化失败
                throw new JyBizException(String.format("初始业务任务基础数据异常！无任务模式 车牌:%s",dto.getVehicleNumber()));
            }
        }else{
            //防止上线初期运输数据未全部接入时 增加补数逻辑
            Long id = jyBizTaskUnloadVehicleService.findIdByBizId(dto.getBizId());
            if(id == null || id <=0 ){
                JyBizTaskUnloadVehicleEntity unloadVehicleEntity = jyBizTaskUnloadVehicleService.initTaskByTms(dto.getSealCarCode());
                if(unloadVehicleEntity == null){
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
        boolean createFlag = createUnLoadScheduleTask(dto);
        if(!createFlag){
            throw new JyBizException(String.format("关闭原解封车调度任务失败！bizId:%s",dto.getBizId()));
        }

        return true;
    }

    /**
     * 卸车任务领取和分配
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.drawUnloadTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public boolean drawUnloadTask(JyBizTaskUnloadDto dto) {
        //更新状态
        JyBizTaskUnloadVehicleEntity changeStatusParam = new JyBizTaskUnloadVehicleEntity();
        changeStatusParam.setBizId(dto.getBizId());
        changeStatusParam.setUpdateUserErp(dto.getOperateUserErp());
        changeStatusParam.setUpdateUserName(dto.getOperateUserName());
        changeStatusParam.setUpdateTime(dto.getOperateTime());
        changeStatusParam.setVehicleStatus(JyBizTaskUnloadStatusEnum.UN_LOADING.getCode());
        if(!jyBizTaskUnloadVehicleService.changeStatus(changeStatusParam)){
            throw new JyBizException(String.format("更新任务状态异常！bizId:%s",dto.getBizId()));
        }
        //分配调度任务
        if(!distributeScheduleTask(dto)){
            throw new JyBizException(String.format("分配调度任务失败！bizId:%s",dto.getBizId()));
        }
        return true;
    }

    /**
     * 卸车任务完成
     *
     * @param dto
     * @return
     */
    @Override
    @Transactional(value = "tm_jy_core",propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.completeUnloadTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public boolean completeUnloadTask(JyBizTaskUnloadDto dto) {
        //修改业务任务状态
        JyBizTaskUnloadVehicleEntity changeStatusParam = new JyBizTaskUnloadVehicleEntity();
        changeStatusParam.setBizId(dto.getBizId());
        changeStatusParam.setUpdateUserErp(dto.getOperateUserErp());
        changeStatusParam.setUpdateUserName(dto.getOperateUserName());
        changeStatusParam.setUpdateTime(dto.getOperateTime());
        changeStatusParam.setVehicleStatus(JyBizTaskUnloadStatusEnum.UN_LOAD_DONE.getCode());
        if(!jyBizTaskUnloadVehicleService.changeStatus(changeStatusParam)){
            throw new JyBizException(String.format("更新任务状态异常！bizId:%s",dto.getBizId()));
        }
        //关闭调度任务
        if(!closeScheduleTask(dto,JyScheduleTaskTypeEnum.UNLOAD)){
            throw new JyBizException(String.format("关闭调度任务失败！bizId:%s",dto.getBizId()));
        }
        return true;
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
     * 分配卸车调度任务
     * @param dto
     * @return
     */
    private boolean distributeScheduleTask(JyBizTaskUnloadDto dto){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(dto.getBizId());
        req.setTaskType(String.valueOf(JyScheduleTaskTypeEnum.UNLOAD.getCode()));
        req.setDistributionType(JyScheduleTaskDistributionTypeEnum.GROUP.getCode());
        req.setDistributionTarget(dto.getGroupCode());
        req.setDistributionTime(dto.getOperateTime());
        req.setOpeUser(dto.getOperateUserErp());
        req.setOpeUserName(dto.getOperateUserName());
        req.setOpeTime(dto.getOperateTime());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.distributeScheduleTask(req);
        return jyScheduleTaskResp != null;
    }

    /**
     * 创建卸车专用调度任务
     * @param dto
     * @return
     */
    private boolean createUnLoadScheduleTask(JyBizTaskUnloadDto dto){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(dto.getBizId());
        req.setTaskType(String.valueOf(JyScheduleTaskTypeEnum.UNLOAD.getCode()));
        req.setOpeUser(dto.getOperateUserErp());
        req.setOpeUserName(dto.getOperateUserName());
        req.setOpeTime(dto.getOperateTime());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.createScheduleTask(req);
        return jyScheduleTaskResp != null;
    }

    /**
     * 根据卸车业务主键更新卸车进度
     * @param bizId
     * @return
     */
    private Boolean refreshUnloadAggCache(String bizId) {

        // TODO jy_unload_agg flink增加版本，防止进度变小

        JyUnloadAggsEntity queryAgg = new JyUnloadAggsEntity(bizId);

        // FIXME maybe exist performance problems because of agg. calculate agg from redis when receiving mq.
        JyUnloadAggsEntity unloadAggEntity = unloadAggDao.aggByBiz(queryAgg);

        logInfo("触发更新卸车扫描进度. {}", JsonHelper.toJson(unloadAggEntity));

        // 比较PDA扫描的进度和Flink计算出的进度大小，进度小于PDA则不更新
        String pdaOpeCacheKey = genPdaUnloadProgressCacheKey(bizId);
        if (redisClientOfJy.exists(pdaOpeCacheKey)) {
            String redisVal = redisClientOfJy.hGet(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_COUNT);
            if (StringUtils.isNotBlank(redisVal)) {
                Long scannedPackageCount = Long.valueOf(redisVal);
                if (null == unloadAggEntity) {
                    log.warn("没有卸车进度数据已本地扫描为准. {}", bizId);
                    return true;
                }
                if (NumberHelper.gt(unloadAggEntity.getTotalScannedPackageCount(), scannedPackageCount)) {
                    String unloadDetailCacheKey = genUnloadDetailCacheKey(bizId);
                    redisClientOfJy.hSet(unloadDetailCacheKey, RedisHashKeyConstants.UNLOAD_COUNT, String.valueOf(unloadAggEntity.getTotalSealPackageCount()));

                    logInfo("根据unload_agg更新卸车扫描进度成功. {}", JsonHelper.toJson(unloadAggEntity));
                }
            }
        }
        // PDA卸车扫描数据失效，卸车进度则以unload_agg为准
        else {
            logInfo("init unload progress from unload agg. {}", JsonHelper.toJson(unloadAggEntity));

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
            UnloadDetailCache cacheEntity = JsonHelper.fromJson(JsonHelper.toJson(unloadAggEntity), UnloadDetailCache.class);
            if (cacheEntity != null) {
                Map<String, String> redisHashMap = RedisHashUtils.objConvertToMap(cacheEntity);
                String unloadDetailCacheKey = genUnloadDetailCacheKey(unloadAggEntity.getBizId());
                redisClientOfJy.hMSet(unloadDetailCacheKey, redisHashMap);
            }
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.unloadGoodsDetail",
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.toScanAggByProduct",
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IUnloadVehicleService.toScanBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<ToScanDetailByProductType> toScanBarCodeDetail(UnloadProductTypeRequest request) {

        // scaned_flag null or 0 是待扫

        InvokeResult<ToScanDetailByProductType> result = new InvokeResult<>();

        return result;
    }
}
