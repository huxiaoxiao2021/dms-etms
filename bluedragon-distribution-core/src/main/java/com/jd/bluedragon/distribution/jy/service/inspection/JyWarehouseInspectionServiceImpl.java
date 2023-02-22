package com.jd.bluedragon.distribution.jy.service.inspection;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.JyBaseReq;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.inspection.request.InspectionRequest;
import com.jd.bluedragon.common.dto.inspection.response.InspectionCheckResultDto;
import com.jd.bluedragon.common.dto.operation.workbench.enums.UnloadBarCodeScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.UnloadScanTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.UnloadScanDetail;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionCommonRequest;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionFinishSubmitRequest;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionNoTaskRequest;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.request.InspectionScanRequest;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response.InspectionFinishPreviewData;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response.InspectionInterceptDto;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response.InspectionScanBarCode;
import com.jd.bluedragon.common.dto.operation.workbench.warehouse.inpection.response.InspectionTaskDetail;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.jy.constants.JyCacheKeyConstants;
import com.jd.bluedragon.distribution.jy.constants.RedisHashKeyConstants;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadDao;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadDetailCache;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadScanDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadTaskCompleteDto;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyDemotionException;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.service.common.UnloadRenderUtils;
import com.jd.bluedragon.distribution.jy.service.config.JyDemotionService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.unload.IJyUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.service.unload.JyUnloadAggsService;
import com.jd.bluedragon.distribution.jy.service.unload.UnloadVehicleTransactionManager;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.dms.workbench.utils.sdk.base.Result;
import com.jd.dms.workbench.utils.sdk.constants.ResultCodeConstant;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.ql.dms.common.constants.CodeConstants;
import com.jd.ql.dms.common.constants.JyConstants;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 接货仓验货服务接口
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-10-19 16:46:21 周三
 */
@Service
public class JyWarehouseInspectionServiceImpl implements JyWarehouseInspectionService{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Resource
    private JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Resource
    private IJyUnloadVehicleService jyUnloadVehicleService;

    @Resource
    private JyUnloadAggsService jyUnloadAggsService;

    @Resource
    private JyDemotionService jyDemotionService;

    @Resource
    private IJyUnloadVehicleManager unloadVehicleManager;

    @Resource
    private UnloadVehicleTransactionManager unloadVehicleTransactionManager;

    @Resource
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Resource
    private WaybillQueryManager waybillQueryManager;

    @Resource
    private DeliveryService deliveryService;

    @Resource
    private JyUnloadDao jyUnloadDao;
    
    @Resource
    @Qualifier("jyUnloadScanProducer")
    private DefaultJMQProducer unloadScanProducer;

    @Resource
    @Qualifier("jyUnloadTaskCompleteProducer")
    private DefaultJMQProducer unloadTaskCompleteProducer;

    @Resource
    private InspectionService inspectionService;

    @Autowired
    private JimDbLock jimDbLock;

    /**
     * 创建无任务验货
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:23:19 周日
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseInspectionServiceImpl.createNoTaskInspectionTask", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<InspectionTaskDetail> createNoTaskInspectionTask(InspectionNoTaskRequest request) {
        logInfo("JyWarehouseInspectionServiceImpl.createNoTaskInspectionTask param {}", JsonHelper.toJson(request));
        Result<InspectionTaskDetail> result = Result.success();

        // 0. base condition check
        if (!checkParamCommon(request, result)) {
            return result;
        }
        String cacheKey = this.genNewUnloadTaskKey(request.getGroupCode());
        try {
            if (!jimDbLock.lock(cacheKey, Constants.STRING_FLG_TRUE, JyCacheKeyConstants.JY_WAREHOUSE_INSPECTION_CREATE_LOCK_EXPIRED, JyCacheKeyConstants.JY_WAREHOUSE_INSPECTION_CREATE_LOCK_EXPIRED_UNIT)) {
                return result.toFail("任务创建中，请不要重复提交");
            }
            // 查询是否已有未完成任务，如果有，直接返回
            final InspectionCommonRequest inspectionCommonRequest = new InspectionCommonRequest();
            inspectionCommonRequest.setGroupCode(request.getGroupCode());
            inspectionCommonRequest.setUser(request.getUser());
            inspectionCommonRequest.setCurrentOperate(request.getCurrentOperate());
            InspectionTaskDetail inspectionTaskDetail = this.getInspectionTaskDetail(inspectionCommonRequest);
            if (inspectionTaskDetail != null) {
                return result.setData(inspectionTaskDetail);
            }
            // 没有，则创建
            final CurrentOperate currentOperate = request.getCurrentOperate();
            JyBizTaskUnloadDto dto = new JyBizTaskUnloadDto();
            dto.setManualCreatedFlag(Constants.CONSTANT_NUMBER_ONE);
            dto.setGroupCode(request.getGroupCode());
            dto.setVehicleNumber(request.getGroupCode());
            dto.setOperateSiteId(currentOperate.getSiteCode());
            dto.setOperateSiteName(currentOperate.getSiteName());
            dto.setTaskType(JyBizTaskUnloadTaskTypeEnum.UNLOAD_TASK_CATEGORY_WAREHOUSE_RECEIVE.getCode());
            JyBizTaskUnloadDto noTaskUnloadDto = jyUnloadVehicleService.createUnloadTask(dto);

            inspectionTaskDetail = new InspectionTaskDetail();
            inspectionTaskDetail.setTaskId(noTaskUnloadDto.getTaskId());
            inspectionTaskDetail.setBizId(noTaskUnloadDto.getBizId());
            inspectionTaskDetail.setScanCount(0L);
            inspectionTaskDetail.setInterceptScanCount(0L);
            result.setData(inspectionTaskDetail);

        } catch (Exception e) {
            log.error("JyWarehouseInspectionServiceImpl.createNoTaskInspectionTask error ",  e);
            result.toFail("接口异常");
        }finally {
            jimDbLock.releaseLock(cacheKey, Constants.STRING_FLG_TRUE);
        }
        return result;
    }

    private String genNewUnloadTaskKey(String groupCode){
        return String.format(JyCacheKeyConstants.JY_WAREHOUSE_INSPECTION_CREATE_LOCK, groupCode);
    }

    private String getJyScheduleTaskId(String bizId) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(bizId);
        req.setTaskType(JyScheduleTaskTypeEnum.UNLOAD.getCode());
        JyScheduleTaskResp scheduleTask = jyScheduleTaskManager.findScheduleTaskByBizId(req);
        return null != scheduleTask ? scheduleTask.getTaskId() : StringUtils.EMPTY;
    }

    private Result<Void> checkParamCommon(JyBaseReq baseReq) {
        Result<Void> result = Result.success();
        if(baseReq == null){
            return result.toFail("参数错误，参数不能为空", ResultCodeConstant.NULL_ARGUMENT);
        }
        final CurrentOperate currentOperate = baseReq.getCurrentOperate();
        if(currentOperate == null){
            return result.toFail("参数错误，currentOperate不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(currentOperate.getSiteCode() <= 0){
            return result.toFail("参数错误，siteCode不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(currentOperate.getSiteName() == null){
            return result.toFail("参数错误，siteName不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        final User user = baseReq.getUser();
        if(user == null){
            return result.toFail("参数错误，user不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(StringUtils.isBlank(user.getUserErp())){
            return result.toFail("参数错误，userErp不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(StringUtils.isBlank(user.getUserName())){
            return result.toFail("参数错误，userName不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        if(StringUtils.isBlank(baseReq.getGroupCode())){
            return result.toFail("参数错误，groupCode不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
        }
        return result;
    }

    private boolean checkParamCommon(JyBaseReq baseReq, Result<?> result) {
        if(baseReq == null){
            result.toFail("参数错误，参数不能为空", ResultCodeConstant.NULL_ARGUMENT);
            return false;
        }
        final CurrentOperate currentOperate = baseReq.getCurrentOperate();
        if(currentOperate == null){
            result.toFail("参数错误，currentOperate不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        if(currentOperate.getSiteCode() <= 0){
            result.toFail("参数错误，siteCode不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        if(currentOperate.getSiteName() == null){
            result.toFail("参数错误，siteName不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        final User user = baseReq.getUser();
        if(user == null){
            result.toFail("参数错误，user不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        if(StringUtils.isBlank(user.getUserErp())){
            result.toFail("参数错误，userErp不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        if(StringUtils.isBlank(user.getUserName())){
            result.toFail("参数错误，userName不合法", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        if(StringUtils.isBlank(baseReq.getGroupCode())){
            result.toFail("参数错误，groupCode不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        return true;
    }

    /**
     * 验货任务明细
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:23:19 周日
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseInspectionServiceImpl.inspectionTaskDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<InspectionTaskDetail> inspectionTaskDetail(InspectionCommonRequest request) {
        logInfo("JyWarehouseInspectionServiceImpl.inspectionTaskDetail param {}", JsonHelper.toJson(request));
        Result<InspectionTaskDetail> result = Result.success();

        try {
            final boolean checkParamFlag = checkParamCommon(request, result);
            if (!checkParamFlag) {
                return result;
            }
            // 区分入参是否包含bizId
            if (StringUtils.isBlank(request.getBizId())) {
                // 没有有bizId则为首次查询，则去查询是否有最新的未完成的任务
                final InspectionTaskDetail inspectionTaskDetail = this.getInspectionTaskDetail(request);
                result.setData(inspectionTaskDetail);
            } else {
                final JyBizTaskUnloadVehicleEntity taskUnloadVehicleExist = jyBizTaskUnloadVehicleService.findByBizId(request.getBizId());
                if (taskUnloadVehicleExist == null) {
                    return result.toFail(String.format("未查询到bizId为%s的任务数据", request.getBizId()));
                }
                final InspectionTaskDetail inspectionTaskDetail = getInspectionTaskDetailByExistUnloadTask(taskUnloadVehicleExist);
                result.setData(inspectionTaskDetail);
            }

            if(jyDemotionService.checkIsDemotion(JyConstants.JY_FLINK_UNLOAD_IS_DEMOTION)){
                throw new JyDemotionException("卸车进度不准，flink降级!");
            }
        } catch (JyDemotionException e){
            result.toFail(HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_UNLOAD_PROCESS_NOT_ACCURATE, false), CodeConstants.JY_DEMOTION_CODE);
        } catch (Exception e) {
            log.error("JyWarehouseInspectionServiceImpl.inspectionTaskDetail error ",  e);
            result.toFail("接口异常");
        }
        return result;
    }

    private String genUnloadProcessCacheKey(String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_PROCESS_KEY, bizId);
    }

    private InspectionTaskDetail getInspectionTaskDetail(InspectionCommonRequest request) {
        try {
            // 查询是否有最新的未完成的任务
            final JyBizTaskUnloadVehicleEntity jyBizTaskUnloadVehicleEntityQuery = new JyBizTaskUnloadVehicleEntity();
            jyBizTaskUnloadVehicleEntityQuery.setEndSiteId((long)request.getCurrentOperate().getSiteCode());
            jyBizTaskUnloadVehicleEntityQuery.setRefGroupCode(request.getGroupCode());
            jyBizTaskUnloadVehicleEntityQuery.setVehicleStatus(JyBizTaskUnloadStatusEnum.UN_LOADING.getCode());
            List<JyBizTaskUnloadVehicleEntity> vehiclePageList = jyBizTaskUnloadVehicleService.findByConditionOfPage(jyBizTaskUnloadVehicleEntityQuery, JyBizTaskUnloadOrderTypeEnum.ORDER_TIME, 1, 1, new ArrayList<String>());
            if (CollectionUtils.isEmpty(vehiclePageList)) {
                return null;
            }
            final JyBizTaskUnloadVehicleEntity taskUnloadVehicleExist = vehiclePageList.get(0);
            return getInspectionTaskDetailByExistUnloadTask(taskUnloadVehicleExist);
        } catch (Exception e) {
            log.error("getInspectionTaskDetail exception ", e);
            throw new RuntimeException(e);
        }
    }

    private InspectionTaskDetail getInspectionTaskDetailByExistUnloadTask(JyBizTaskUnloadVehicleEntity taskUnloadVehicleExist) {
        final InspectionTaskDetail inspectionTaskDetail = new InspectionTaskDetail();
        inspectionTaskDetail.setTaskId(this.getJyScheduleTaskId(taskUnloadVehicleExist.getBizId()));
        inspectionTaskDetail.setBizId(taskUnloadVehicleExist.getBizId());
        inspectionTaskDetail.setScanCount(0L);
        inspectionTaskDetail.setInterceptScanCount(0L);

        jyUnloadVehicleService.refreshUnloadAggCache(taskUnloadVehicleExist.getBizId());
        String progressCacheKey = genUnloadProcessCacheKey(taskUnloadVehicleExist.getBizId());
        Map<String, String> hashRedisMap = redisClientOfJy.hGetAll(progressCacheKey);
        if (hashRedisMap == null || hashRedisMap.size() == 0) {
            return inspectionTaskDetail;
        }

        try {
            UnloadDetailCache redisCache = RedisHashUtils.mapConvertBean(hashRedisMap, UnloadDetailCache.class);
            UnloadScanDetail scanProgress = JsonHelper.fromJson(JsonHelper.toJson(redisCache), UnloadScanDetail.class);

            if (scanProgress != null) {
                inspectionTaskDetail.setScanCount(scanProgress.getUnloadCount() != null ? scanProgress.getUnloadCount() : 0L);
                inspectionTaskDetail.setInterceptScanCount(scanProgress.getInterceptActualScanCount() != null ? scanProgress.getInterceptActualScanCount() : 0L);
            }
        } catch (Exception e) {
            log.error("getInspectionTaskDetailByExistUnloadTask exception ", e);
            throw new RuntimeException(e);
        }
        return inspectionTaskDetail;
    }

    /**
     * 验货扫描
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseInspectionServiceImpl.inspectionScan", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<Integer> inspectionScan(InspectionScanRequest request) {
        logInfo("JyWarehouseInspectionServiceImpl.inspectionScan param {}", JsonHelper.toJson(request));
        JdVerifyResponse<Integer> result = new JdVerifyResponse<>();
        result.toSuccess("验货成功");

        try {
            final Result<Void> checkParamCommonResult = this.checkParamCommon(request);
            if (!checkParamCommonResult.isSuccess()) {
                result.toFail(checkParamCommonResult.getMessage());
                return result;
            }
            if(!this.checkParam4InspectionScan(request, result)){
                return result;
            }

            JyBizTaskUnloadVehicleEntity taskUnloadVehicle = jyBizTaskUnloadVehicleService.findByBizId(request.getBizId());
            if (taskUnloadVehicle == null) {
                result.toFail("验货任务不存在，请重新进入验货页面！");
                return result;
            }

            // 卸车扫描前置校验
            if (!checkBeforeScan(result, request)) {
                return result;
            }

            try {
                // 保存扫描记录，发运单全程跟踪。首次扫描分配卸车任务
                UnloadScanDto unloadScanDto = UnloadRenderUtils.createUnloadDto(request, taskUnloadVehicle);
                unloadScanProducer.sendOnFailPersistent(unloadScanDto.getBarCode(), JsonHelper.toJson(unloadScanDto));

                // 统计本次扫描的包裹数
                result.setData(calculateScanPackageCount(request));

                // 记录卸车任务扫描进度
                recordUnloadProgress(result.getData(), request, taskUnloadVehicle);
            } catch (Exception ex) {
                log.error("JyWarehouseInspectionServiceImpl.inspectionScan exception. {}", JsonHelper.toJson(request), ex);
                result.toFail("服务器异常，验货扫描失败，请咚咚联系分拣小秘！");

                redisClientOfJy.del(getBizBarCodeCacheKey(request.getBarCode(), request.getCurrentOperate().getSiteCode(), request.getBizId()));
            }
        } catch (Exception e) {
            log.error("JyWarehouseInspectionServiceImpl.inspectionScan error ",  e);
            result.toFail("接口异常");
        }
        return result;
    }

    private boolean checkParam4InspectionScan(InspectionScanRequest request, JdVerifyResponse<?> result) {
        if (request.getForceSubmit() == null) {
            request.setForceSubmit(false);
        }
        if (StringUtils.isBlank(request.getBizId())) {
            result.toFail("参数错误，bizId不能为空");
            return false;
        }
        if (StringUtils.isBlank(request.getTaskId())) {
            result.toFail("参数错误，taskId不能为空");
            return false;
        }

        String barCode = request.getBarCode();
        if (StringUtils.isBlank(barCode)) {
            result.toFail("请扫描单号！");
            return false;
        }
        if (!BusinessHelper.isBoxcode(barCode)
                && !WaybillUtil.isWaybillCode(barCode)
                && !WaybillUtil.isPackageCode(barCode)) {
            result.toFail("扫描单号非法！");
            return false;
        }

        // 设置默认扫描方式
        if(request.getScanType() == null){
            request.setScanType(UnloadScanTypeEnum.SCAN_ONE.getCode());
        }
        final BarCodeType barCodeType = BusinessUtil.getBarCodeType(request.getBarCode());
        if(barCodeType == null) {
            result.toFail("请扫描正确的条码！");
            return false;
        }
        if(Objects.equals(UnloadScanTypeEnum.SCAN_ONE.getCode(), request.getScanType()) &&
                (!Objects.equals(BarCodeType.PACKAGE_CODE.getCode(), barCodeType.getCode()) && !Objects.equals(BarCodeType.BOX_CODE.getCode(), barCodeType.getCode()))){
            result.toFail("请扫描包裹号或箱号！");
            return false;
        }
        if(Objects.equals(UnloadScanTypeEnum.SCAN_WAYBILL.getCode(), request.getScanType()) &&
                (!Objects.equals(BarCodeType.PACKAGE_CODE.getCode(), barCodeType.getCode()) && !Objects.equals(BarCodeType.WAYBILL_CODE.getCode(), barCodeType.getCode()))){
            result.toFail("请扫描包裹号或运单号！");
            return false;
        }
        return true;
    }

    /**
     * 扫描前校验
     * @param result
     * @param request
     * @return
     */
    private boolean checkBeforeScan(JdVerifyResponse<Integer> result, InspectionScanRequest request) {
        if(!checkBarInterceptResult(result, request)){
            return false;
        }

        // 一个单号只能扫描一次
        if (checkBarScannedAlready(request)) {
            result.toFail("单号已扫描！");
            return false;
        }

        return true;
    }

    /**
     * 校验卸车是否已经扫描过该单号，同一个任务只能扫描一次
     * @return true：扫描过
     */
    private boolean checkBarScannedAlready(InspectionScanRequest request) {
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

    /**
     * 调用验货拦截链
     * @param response
     * @param request
     * @return
     */
    private boolean checkBarInterceptResult(JdVerifyResponse<Integer> response, InspectionScanRequest request) {
        // 非强制提交，校验拦截
        if (!request.getForceSubmit()) {
            InspectionRequest inspectionRequest = new InspectionRequest();
            inspectionRequest.setBarCode(request.getBarCode());
            if(Objects.equals(UnloadScanTypeEnum.SCAN_WAYBILL.getCode(), request.getScanType())){
                inspectionRequest.setBarCode(WaybillUtil.getWaybillCode(request.getBarCode()));
            }
            inspectionRequest.setBusinessType(10);
            inspectionRequest.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
            inspectionRequest.setCreateSiteName(request.getCurrentOperate().getSiteName());
            inspectionRequest.setOperateTime(DateHelper.formatDateTime(new Date()));
            inspectionRequest.setOperateType(2);
            inspectionRequest.setOperateUserCode(request.getUser().getUserCode());
            inspectionRequest.setOperateUserName(request.getUser().getUserName());
            JdVerifyResponse<InspectionCheckResultDto> verifyResponse = inspectionService.checkBeforeInspection(inspectionRequest);
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

    /**
     * 统计本次扫描的包裹数量
     * @param request
     */
    private Integer calculateScanPackageCount(InspectionScanRequest request) {
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

    private static final int UNLOAD_CACHE_EXPIRE = 12;

    private static final int UNLOAD_SCAN_BAR_EXPIRE = 6;
    /**
     * 更新PDA验货扫描进度
     * @param pdaUnloadCount
     * @param request
     * @param taskUnloadVehicle
     */
    private void recordUnloadProgress(Integer pdaUnloadCount, InspectionScanRequest request, JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
        String pdaOpeCacheKey = genPdaUnloadProgressCacheKey(request.getBizId());
        if (redisClientOfJy.exists(pdaOpeCacheKey)) {

            redisClientOfJy.hIncrBy(pdaOpeCacheKey, RedisHashKeyConstants.UNLOAD_COUNT, pdaUnloadCount);
            redisClientOfJy.expire(pdaOpeCacheKey, UNLOAD_CACHE_EXPIRE, TimeUnit.HOURS);

            logInfo("更新PDA本地验货扫描进度. {}-{}", JsonHelper.toJson(request), pdaUnloadCount);
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

    private String genPdaUnloadProgressCacheKey(String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_PDA_PROCESS_KEY, bizId);
    }

    private String getBizBarCodeCacheKey(String barCode, int siteCode, String bizId) {
        return String.format(CacheKeyConstants.JY_UNLOAD_SCAN_KEY, barCode, siteCode, bizId);
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
     * 拦截单号明细
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseInspectionServiceImpl.interceptBarCodeDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<InspectionInterceptDto> interceptBarCodeDetail(InspectionCommonRequest request) {
        logInfo("JyWarehouseInspectionServiceImpl.interceptBarCodeDetail param {}", JsonHelper.toJson(request));
        Result<InspectionInterceptDto> result = Result.success();
        InspectionInterceptDto interceptScanBarCode = new InspectionInterceptDto();
        interceptScanBarCode.setInterceptScanCount(0L);
        interceptScanBarCode.setBarcodeList(new ArrayList<InspectionScanBarCode>());
        result.setData(interceptScanBarCode);

        try {
            if (!checkParamCommon(request, result) || !this.checkParam4InterceptBarCodeDetail(request, result)) {
                return result;
            }

            Pager<JyVehicleTaskUnloadDetail> pager = getInterceptBarCodeCondition(request);
            Pager<JyVehicleTaskUnloadDetail> retPager = unloadVehicleManager.queryInterceptBarCodeDetail(pager);
            if (retPager == null || CollectionUtils.isEmpty(retPager.getData())) {
                return result;
            }
            interceptScanBarCode.setBarcodeList(this.getUnloadScanBarCodeList(UnloadBarCodeQueryEntranceEnum.INTERCEPT, retPager.getData()));
            if (CollectionUtils.isNotEmpty(interceptScanBarCode.getBarcodeList())) {
                interceptScanBarCode.setInterceptScanCount((long)interceptScanBarCode.getBarcodeList().size());
            }

            result.setData(interceptScanBarCode);
        } catch (JyDemotionException e) {
            result.toFail(HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_UNLOAD_INTERCEPT, false), CodeConstants.JY_DEMOTION_CODE);
        } catch (Exception e) {
            log.error("JyWarehouseInspectionServiceImpl.interceptBarCodeDetail error ",  e);
            result.toFail("接口异常");
        }
        return result;
    }

    private boolean checkParam4InterceptBarCodeDetail(InspectionCommonRequest request, Result<?> result) {
        if (StringUtils.isBlank(request.getBizId())) {
            result.toFail("参数错误，bizId不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        if (StringUtils.isBlank(request.getTaskId())) {
            result.toFail("参数错误，taskId不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        return true;
    }

    private Pager<JyVehicleTaskUnloadDetail> getInterceptBarCodeCondition(InspectionCommonRequest request) {
        Pager<JyVehicleTaskUnloadDetail> pager = new Pager<>();
        pager.setPageNo(request.getPageNumber());
        pager.setPageSize(request.getPageSize());

        JyVehicleTaskUnloadDetail searchVo = new JyVehicleTaskUnloadDetail();
        pager.setSearchVo(searchVo);

        searchVo.setEndSiteId(request.getCurrentOperate().getSiteCode());
        searchVo.setBizId(request.getBizId());
        searchVo.setInterceptFlag(Constants.CONSTANT_NUMBER_ONE); // 拦截
        return pager;
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

    private List<InspectionScanBarCode> getUnloadScanBarCodeList(UnloadBarCodeQueryEntranceEnum queryEntranceEnum, List<JyVehicleTaskUnloadDetail> unloadDetailList) {
        List<InspectionScanBarCode> barCodeList = Lists.newArrayList();
        for (JyVehicleTaskUnloadDetail unloadDetail : unloadDetailList) {
            InspectionScanBarCode inspectionScanBarCode = new InspectionScanBarCode();
            inspectionScanBarCode.setBarcode(unloadDetail.getPackageCode());
            inspectionScanBarCode.setProductType(UnloadProductTypeEnum.getNameByCode(unloadDetail.getProductType()));
            inspectionScanBarCode.setTags(UnloadRenderUtils.resolveBarCodeLabel(queryEntranceEnum, unloadDetail));

            UnloadBarCodeScanTypeEnum scanTypeEnum = UnloadRenderUtils.resolveScanTypeDesc(unloadDetail);
            inspectionScanBarCode.setScanType(scanTypeEnum.getCode());
            inspectionScanBarCode.setScanTypeDesc(scanTypeEnum.getName());

            barCodeList.add(inspectionScanBarCode);
        }
        return barCodeList;
    }

    /**
     * 验货完成前预览是否有异常数据
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseInspectionServiceImpl.inspectionFinishPreview", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<InspectionFinishPreviewData> inspectionFinishPreview(InspectionCommonRequest request) {
        logInfo("JyWarehouseInspectionServiceImpl.inspectionFinishPreview param {}", JsonHelper.toJson(request));
        Result<InspectionFinishPreviewData> result = Result.success();
        final InspectionFinishPreviewData inspectionFinishPreviewData = new InspectionFinishPreviewData();
        inspectionFinishPreviewData.setAbnormalFlag(false);
        result.setData(inspectionFinishPreviewData);

        try {

            if (!checkParamCommon(request, result) || !this.checkParam4SubmitInspectionCompletion(request, result)) {
                return result;
            }

            List<JyUnloadAggsEntity> unloadAggList = jyUnloadAggsService.queryByBizId(new JyUnloadAggsEntity(request.getBizId()));
            if (CollectionUtils.isEmpty(unloadAggList)) {
                log.warn("判断卸车任务是否完成查询AGG为空.{}", JsonHelper.toJson(request));
                return result;
            }
            final Boolean taskNormal = this.judgeUnloadTaskNormal(inspectionFinishPreviewData, unloadAggList);

            // 任务正常，直接返回
            if (taskNormal) {
                return result;
            }
            inspectionFinishPreviewData.setAbnormalFlag(!taskNormal);

            // 获取拦截明细列表
            Pager<JyVehicleTaskUnloadDetail> interceptQueryPager = getInterceptBarCodeCondition(request);
            Pager<JyVehicleTaskUnloadDetail> retPager = unloadVehicleManager.queryInterceptBarCodeDetail(interceptQueryPager);
            if (retPager == null || CollectionUtils.isEmpty(retPager.getData())) {
                return result;
            }

            inspectionFinishPreviewData.setBarCodeList(this.getUnloadScanBarCodeList(UnloadBarCodeQueryEntranceEnum.INTERCEPT, retPager.getData()));
        } catch (Exception e) {
            log.error("JyWarehouseInspectionServiceImpl.inspectionFinishPreview error ",  e);
            result.toFail("接口异常");
        }
        return result;
    }

    private Boolean judgeUnloadTaskNormal(InspectionFinishPreviewData previewData, List<JyUnloadAggsEntity> unloadAggList) {
        long existToScanRows = 0;
        long existLocalMoreScanRows = 0;
        long existOutMoreScanRows = 0;
        long interceptActualScanCount = 0;
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
            if (NumberHelper.gt0(aggEntity.getInterceptActualScanCount())) {
                interceptActualScanCount ++;
            }
        }
        boolean unloadDone = existToScanRows == 0 && existLocalMoreScanRows == 0 && existOutMoreScanRows == 0;

        JyUnloadAggsEntity oneUnloadAgg = unloadAggList.get(0);
        logInfo("judgeUnloadTaskNormal. biz: {}, progress: {}-{}-{}", oneUnloadAgg.getBizId(), existToScanRows, existLocalMoreScanRows, existOutMoreScanRows);

        if (!unloadDone) {
            previewData.setTotalScan(oneUnloadAgg.getTotalScannedPackageCount().longValue());
            previewData.setInterceptScanCount(interceptActualScanCount);
            final long toScanCount = dealMinus(oneUnloadAgg.getTotalSealPackageCount(), oneUnloadAgg.getTotalScannedPackageCount());
            previewData.setAbnormalCount(interceptActualScanCount + toScanCount);
        }

        return unloadDone;
    }

    private long dealMinus(Number a, Number b) {
        if(a == null){
            a = 0L;
        }
        if(b == null){
            b = 0L;
        }
        return NumberHelper.gt(a, b) ? a.longValue() - b.longValue() : 0L;
    }

    /**
     * 验货完成
     *
     * @param request 请求参数
     * @return 返回结果
     * @author fanggang7
     * @time 2022-10-09 14:28:59 周日
     */
    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWarehouseInspectionServiceImpl.submitInspectionCompletion", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public Result<Boolean> submitInspectionCompletion(InspectionCommonRequest request) {
        logInfo("JyWarehouseInspectionServiceImpl.submitInspectionCompletion param {}", JsonHelper.toJson(request));
        Result<Boolean> result = Result.success("操作成功");

        try {
            // 0. base condition check
            if (!checkParamCommon(request, result) || !this.checkParam4SubmitInspectionCompletion(request, result)) {
                return result;
            }
            // 1. check business condition
            final JyBizTaskUnloadVehicleEntity taskUnloadVehicleExist = jyBizTaskUnloadVehicleService.findByBizId(request.getBizId());
            if(JyBizTaskUnloadStatusEnum.UN_LOAD_DONE.getCode().equals(taskUnloadVehicleExist.getVehicleStatus())){
                return result.toFail("该任务已经操作验货完成，无需重复操作");
            }
            // 2. redo check abnormal status and intercept count
            List<JyUnloadAggsEntity> unloadAggList = jyUnloadAggsService.queryByBizId(new JyUnloadAggsEntity(request.getBizId()));
            boolean taskNormal = true;
            if (CollectionUtils.isEmpty(unloadAggList)) {
                log.warn("submitInspectionCompletion queryByBizId empty {}", JsonHelper.toJson(request));
            } else {
                final InspectionFinishPreviewData inspectionFinishPreviewData = new InspectionFinishPreviewData();
                taskNormal = this.judgeUnloadTaskNormal(inspectionFinishPreviewData, unloadAggList);
            }

            final InspectionFinishSubmitRequest inspectionFinishSubmitRequest = new InspectionFinishSubmitRequest();
            inspectionFinishSubmitRequest.setTaskId(request.getTaskId());
            inspectionFinishSubmitRequest.setBizId(request.getBizId());
            inspectionFinishSubmitRequest.setCurrentOperate(request.getCurrentOperate());
            inspectionFinishSubmitRequest.setUser(request.getUser());
            inspectionFinishSubmitRequest.setAbnormalFlag(!taskNormal);
            this.recordUnloadCompleteStatus(inspectionFinishSubmitRequest);
            result.setData(this.completeTask(inspectionFinishSubmitRequest));

            // todo 更新任务缓存，当前岗位码已无未完成的验货任务

        } catch (Exception e) {
            log.error("JyWarehouseInspectionServiceImpl.submitInspectionCompletion error ",  e);
            result.toFail("接口异常");
        }
        return result;
    }

    private boolean checkParam4SubmitInspectionCompletion(InspectionCommonRequest request, Result<?> result) {
        if (StringUtils.isBlank(request.getBizId())) {
            result.toFail("参数错误，bizId不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        if (StringUtils.isBlank(request.getTaskId())) {
            result.toFail("参数错误，taskId不能为空", ResultCodeConstant.ILLEGAL_ARGUMENT);
            return false;
        }
        return true;
    }

    private void recordUnloadCompleteStatus(InspectionFinishSubmitRequest request) {
        UnloadTaskCompleteDto completeDto = new UnloadTaskCompleteDto();
        completeDto.setTaskId(request.getTaskId());
        completeDto.setBizId(request.getBizId());
        completeDto.setAbnormalFlag(request.getAbnormalFlag() ? (byte)1 : (byte)0);

        completeDto.setMoreScanCount(0L);
        completeDto.setToScanCount(0L);
        completeDto.setOperateTime(new Date());
        completeDto.setOperateUserErp(request.getUser().getUserErp());
        completeDto.setOperateUserName(request.getUser().getUserName());
        unloadTaskCompleteProducer.sendOnFailPersistent(completeDto.getBizId(), JsonHelper.toJson(completeDto));
    }

    private boolean completeTask(InspectionFinishSubmitRequest request) {
        JyBizTaskUnloadDto completeDto = new JyBizTaskUnloadDto();
        completeDto.setBizId(request.getBizId());
        completeDto.setOperateUserErp(request.getUser().getUserErp());
        completeDto.setOperateUserName(request.getUser().getUserName());
        completeDto.setOperateTime(new Date());
        return unloadVehicleTransactionManager.completeUnloadTask(completeDto);
    }

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
}
