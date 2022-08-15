package com.jd.bluedragon.distribution.jy.service.unload;

import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.jy.api.JyUnloadVehicleTysService;
import com.jd.bluedragon.distribution.jy.dao.task.JyBizTaskUnloadVehicleDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyBizTaskUnloadVehicleStageDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadAggsDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadVehicleBoardDao;
import com.jd.bluedragon.distribution.jy.dto.task.JyBizTaskUnloadCountDto;
import com.jd.bluedragon.distribution.jy.dto.unload.*;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.manager.IJyUnloadVehicleManager;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyBizTaskUnloadVehicleStageEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadVehicleBoardEntity;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.exception.UnloadPackageBoardException;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.transboard.api.dto.*;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JyUnloadTaskWaybillAgg;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.jd.bluedragon.Constants.WAYBILL_ROUTER_SPLIT;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum.*;


/**
 * @author weixiaofeng12
 * @date 2022-07-01
 * 转运卸车岗相关服务实现
 */

@Service("jyUnloadVehicleTysService")
@UnifiedExceptionProcess
public class JyUnloadVehicleTysServiceImpl implements JyUnloadVehicleTysService {

    private static final Logger log = LoggerFactory.getLogger(JyUnloadVehicleTysServiceImpl.class);

    @Autowired
    JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;
    @Autowired
    IJyUnloadVehicleManager iJyUnloadVehicleManager;
    @Autowired
    GroupBoardManager groupBoardManager;
    @Autowired
    BoardCommonManager boardCommonManager;
    @Autowired
    private WaybillCacheService waybillCacheService;
    @Autowired
    IJyUnloadVehicleManager jyUnloadVehicleManager;
    @Autowired
    private JyUnloadAggsService jyUnloadAggsService;
    @Autowired
    JyUnloadVehicleBoardDao jyUnloadVehicleBoardDao;
    @Autowired
    BaseMajorManager baseMajorManager;
    @Autowired
    private JyUnloadAggsDao jyUnloadAggsDao;
    @Autowired
    JyBizTaskUnloadVehicleStageService jyBizTaskUnloadVehicleStageService;
    @Autowired
    private JyBizTaskUnloadVehicleStageDao jyBizTaskUnloadVehicleStageDao;
    @Autowired
    private JyBizTaskUnloadVehicleDao jyBizTaskUnloadVehicleDao;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private WaybillPackageManager waybillPackageManager;
    @Autowired
    private JyUnloadVehicleCheckTysService jyUnloadVehicleCheckTysService;
    @Autowired
    private BoardCombinationService boardCombinationService;






    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.listUnloadVehicleTask",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<UnloadVehicleTaskRespDto> listUnloadVehicleTask(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        if (ObjectHelper.isNotNull(unloadVehicleTaskReqDto.getPackageCode())) {
            return queryUnloadVehicleTaskByVehicleNumOrPackage(unloadVehicleTaskReqDto);
        }
        //查询状态统计数据(按状态分组聚合)
        JyBizTaskUnloadStatusEnum[] statusEnums = {WAIT_UN_LOAD, UN_LOADING, UN_LOAD_DONE};
        JyBizTaskUnloadVehicleEntity statusStatisticsQueryParams = assembleQueryStatusStatisticsCondition(unloadVehicleTaskReqDto);
        List<JyBizTaskUnloadCountDto> unloadCountDtos = jyBizTaskUnloadVehicleService.findStatusCountByCondition4Status(statusStatisticsQueryParams, null, statusEnums);
        if (!ObjectHelper.isNotNull(unloadCountDtos)) {
            return new InvokeResult(TASK_NO_FOUND_BY_STATUS_CODE, TASK_NO_FOUND_BY_STATUS_MESSAGE);
        }
        UnloadVehicleTaskRespDto respDto = new UnloadVehicleTaskRespDto();
        initCountToResp(respDto, unloadCountDtos);
        //查询卸车任务列表
        PageHelper.startPage(unloadVehicleTaskReqDto.getPageNo(), unloadVehicleTaskReqDto.getPageSize());
        JyBizTaskUnloadVehicleEntity unloadTaskQueryParams = assembleQueryTaskCondition(unloadVehicleTaskReqDto);
        List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = jyBizTaskUnloadVehicleService.listUnloadVehicleTask(unloadTaskQueryParams);
        respDto.setUnloadVehicleTaskDtoList(unloadVehicleTaskDtoList);

        statusStatisticsQueryParams.setVehicleStatus(unloadVehicleTaskReqDto.getVehicleStatus());
        List<LineTypeStatisDto> lineTypeStatisDtoList = calculationLineTypeStatis(statusStatisticsQueryParams);
        respDto.setLineTypeStatisDtoList(lineTypeStatisDtoList);

        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, respDto);
    }

    private JyBizTaskUnloadVehicleEntity assembleQueryTaskCondition(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
        condition.setVehicleStatus(unloadVehicleTaskReqDto.getVehicleStatus());
        condition.setEndSiteId(Long.valueOf(unloadVehicleTaskReqDto.getCurrentOperate().getSiteCode()));
        condition.setLineType(unloadVehicleTaskReqDto.getLineType());
        condition.setFuzzyVehicleNumber(unloadVehicleTaskReqDto.getVehicleNumber());
        return condition;
    }

    private JyBizTaskUnloadVehicleEntity assembleQueryStatusStatisticsCondition(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
        condition.setLineType(unloadVehicleTaskReqDto.getLineType());
        condition.setEndSiteId(Long.valueOf(unloadVehicleTaskReqDto.getCurrentOperate().getSiteCode()));
        condition.setFuzzyVehicleNumber(unloadVehicleTaskReqDto.getVehicleNumber());
        return condition;
    }

    private List<LineTypeStatisDto> calculationLineTypeStatis(JyBizTaskUnloadVehicleEntity condition) {
        List<LineTypeStatisDto> lineTypeList = new ArrayList<>();
        JyBizTaskUnloadStatusEnum statusEnum = JyBizTaskUnloadStatusEnum.getEnumByCode(condition.getVehicleStatus());
        List<JyBizTaskUnloadCountDto> lineTypeAgg = jyBizTaskUnloadVehicleService.findStatusCountByCondition4StatusAndLine(condition, null, statusEnum);
        if (CollectionUtils.isNotEmpty(lineTypeAgg)) {
            for (JyBizTaskUnloadCountDto countDto : lineTypeAgg) {
                LineTypeStatisDto lineTypeStatis = convertLineTypeDto(countDto);
                lineTypeList.add(lineTypeStatis);
            }
        }
        return lineTypeList;
    }

    private LineTypeStatisDto convertLineTypeDto(JyBizTaskUnloadCountDto countDto) {
        LineTypeStatisDto lineTypeStatis = new LineTypeStatisDto();
        lineTypeStatis.setLineType(countDto.getLineType());
        lineTypeStatis.setLineTypeName(JyLineTypeEnum.getNameByCode(countDto.getLineType()));
        lineTypeStatis.setCount(countDto.getSum());
        return lineTypeStatis;
    }


    private void initCountToResp(UnloadVehicleTaskRespDto unloadVehicleTaskRespDto, List<JyBizTaskUnloadCountDto> unloadCountDtos) {
        for (JyBizTaskUnloadCountDto unloadCountDto : unloadCountDtos) {
            switch (unloadCountDto.getVehicleStatus()) {
                case 3:
                    unloadVehicleTaskRespDto.setWaitUnloadCount(unloadCountDto.getSum());
                    break;
                case 4:
                    unloadVehicleTaskRespDto.setUnloadingCount(unloadCountDto.getSum());
                    break;
                case 5:
                    unloadVehicleTaskRespDto.setUnloadedCount(unloadCountDto.getSum());
                    break;
                default:
                    log.info("");
            }
        }
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryUnloadVehicleTaskByVehicleNumOrPackage",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<UnloadVehicleTaskRespDto> queryUnloadVehicleTaskByVehicleNumOrPackage(UnloadVehicleTaskReqDto queryUnloadTaskDto) {
        UnloadVehicleTaskRespDto unloadVehicleTaskRespDto = new UnloadVehicleTaskRespDto();
        if (ObjectHelper.isNotNull(queryUnloadTaskDto.getPackageCode()) && WaybillUtil.isPackageCode(queryUnloadTaskDto.getPackageCode())) {
            JyVehicleTaskUnloadDetail detail = new JyVehicleTaskUnloadDetail();
            detail.setPackageCode(queryUnloadTaskDto.getPackageCode());
            detail.setEndSiteId(queryUnloadTaskDto.getCurrentOperate().getSiteCode());
            List<JyVehicleTaskUnloadDetail> unloadDetailList = iJyUnloadVehicleManager.findUnloadDetail(detail);
            if (ObjectHelper.isNotNull(unloadDetailList)) {
                List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = convertUnloadVehicleTaskDto(unloadDetailList);
                calculationCount(unloadVehicleTaskRespDto, unloadVehicleTaskDtoList);
                return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadVehicleTaskRespDto);
            }
        } else if (ObjectHelper.isNotNull(queryUnloadTaskDto.getVehicleNumber())) {
            JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
            entity.setFuzzyVehicleNumber(queryUnloadTaskDto.getVehicleNumber());
            entity.setEndSiteId(Long.valueOf(queryUnloadTaskDto.getCurrentOperate().getSiteCode()));
            List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = jyBizTaskUnloadVehicleService.listUnloadVehicleTask(entity);
            calculationCount(unloadVehicleTaskRespDto, unloadVehicleTaskDtoList);
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadVehicleTaskRespDto);
        }
        return new InvokeResult(TASK_NO_FOUND_BY_PARAMS_CODE, TASK_NO_FOUND_BY_PARAMS_MESSAGE);
    }

    private void calculationCount(UnloadVehicleTaskRespDto unloadVehicleTaskRespDto, List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList) {
        unloadVehicleTaskRespDto.setUnloadVehicleTaskDtoList(unloadVehicleTaskDtoList);
        int waitUnloadCount = 0;
        //卸车中数量
        int unloadingCount = 0;
        //已完成卸车数量
        int unloadedCount = 0;
        for (UnloadVehicleTaskDto unloadVehicleTaskDto : unloadVehicleTaskDtoList) {
            switch (unloadVehicleTaskDto.getVehicleStatus()) {
                case 3:
                    waitUnloadCount++;
                    break;
                case 4:
                    unloadingCount++;
                    break;
                case 5:
                    unloadedCount++;
                    break;
                default:
                    log.info("");
            }
        }
        unloadVehicleTaskRespDto.setWaitUnloadCount(waitUnloadCount);
        unloadVehicleTaskRespDto.setUnloadingCount(unloadingCount);
        unloadVehicleTaskRespDto.setUnloadedCount(unloadedCount);
    }

    private List<UnloadVehicleTaskDto> convertUnloadVehicleTaskDto(List<JyVehicleTaskUnloadDetail> unloadDetailList) {
        List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = new ArrayList<>();
        JyVehicleTaskUnloadDetail detail = unloadDetailList.get(0);
        JyBizTaskUnloadVehicleEntity entity = jyBizTaskUnloadVehicleService.findByBizId(detail.getBizId());
        UnloadVehicleTaskDto unloadVehicleTaskDto = jyBizTaskUnloadVehicleService.entityConvertDto(entity);
        unloadVehicleTaskDtoList.add(unloadVehicleTaskDto);
        return unloadVehicleTaskDtoList;
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.updateUnloadVehicleTaskProperty",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult updateUnloadVehicleTaskProperty(UnloadVehicleTaskDto unloadVehicleTask) {
        log.info("JyUnloadVehicleTysServiceImpl.updateUnloadVehicleTaskProperty--请求参数={}", JsonUtils.toJSONString(unloadVehicleTask));
        if(unloadVehicleTask.getUser() == null || StringUtils.isBlank(unloadVehicleTask.getUser().getUserErp())) {
            return new InvokeResult(RESULT_PARAMETER_ERROR_CODE, "操作人为空");
        }
        if(unloadVehicleTask.getCurrentOperate() == null || unloadVehicleTask.getCurrentOperate().getSiteCode() <= 0) {
            return new InvokeResult(RESULT_PARAMETER_ERROR_CODE, "操作场地为空");
        }
        if (StringUtils.isBlank(unloadVehicleTask.getBizId())) {
            return new InvokeResult(RESULT_PARAMETER_ERROR_CODE, "操作任务BizId为空");
        }
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
        org.springframework.beans.BeanUtils.copyProperties(unloadVehicleTask, entity);

        Boolean success = jyBizTaskUnloadVehicleService.saveOrUpdateOfBusinessInfo(entity);
        if (success) {
            // 如果本次是卸车完成动作
            if (JyBizTaskUnloadStatusEnum.UN_LOAD_DONE.getCode().equals(unloadVehicleTask.getVehicleStatus())) {
                // 将此卸车任务的子阶段结束
                JyBizTaskUnloadVehicleStageEntity stageEntity = new JyBizTaskUnloadVehicleStageEntity();
                stageEntity.setUnloadVehicleBizId(unloadVehicleTask.getBizId());
                stageEntity.setStatus(JyBizTaskStageStatusEnum.COMPLETE.getCode());
                stageEntity.setEndTime(new Date());
                stageEntity.setUpdateTime(new Date());
                stageEntity.setUpdateUserErp(unloadVehicleTask.getUser().getUserErp());
                stageEntity.setUpdateUserName(unloadVehicleTask.getUser().getUserName());
                jyBizTaskUnloadVehicleStageService.updateStatusByUnloadVehicleBizId(stageEntity);
            }
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryStatisticsByDiffDimension",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ScanStatisticsDto> queryStatisticsByDiffDimension(DimensionQueryDto dto) {
        if (UnloadStatisticsQueryTypeEnum.PACKAGE.getCode().equals(dto.getType()) || UnloadStatisticsQueryTypeEnum.WAYBILL.getCode().equals(dto.getType())) {
            ScanStatisticsDto scanStatisticsDto = jyBizTaskUnloadVehicleService.queryStatisticsByDiffDimension(dto);
            return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, scanStatisticsDto);
        }
        return new InvokeResult(NOT_SUPPORT_TYPE_QUERY_CODE, NOT_SUPPORT_TYPE_QUERY_MESSAGE);
    }

    @Override
    public InvokeResult<StatisticsDto> queryStatistics(DimensionQueryDto dto) {
        StatisticsDto statisticsDto = jyBizTaskUnloadVehicleService.queryStatistics(dto);
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, statisticsDto);
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryTaskDataByBizId",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<UnloadVehicleTaskDto> queryTaskDataByBizId(String bizId) {
        UnloadVehicleTaskDto unloadVehicleTaskDto = jyBizTaskUnloadVehicleService.queryTaskDataByBizId(bizId);
        if (ObjectHelper.isNotNull(unloadVehicleTaskDto)) {
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadVehicleTaskDto);
        }
        return new InvokeResult(TASK_NO_FOUND_BY_PARAMS_CODE, TASK_NO_FOUND_BY_PARAMS_MESSAGE);
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.scan",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ScanPackageRespDto> scan(ScanPackageDto scanPackageDto) {
        InvokeResult<ScanPackageRespDto> invokeResult = new InvokeResult<>();
        invokeResult.success();
        ScanPackageRespDto scanPackageRespDto = convertToScanResult(scanPackageDto);
        invokeResult.setData(scanPackageRespDto);

        log.info("invoking jy scanAndComBoard,params: {}", JsonHelper.toJson(scanPackageDto));
        try {
            // 校验任务
            JyBizTaskUnloadVehicleEntity unloadVehicleEntity = jyBizTaskUnloadVehicleService.findByBizId(scanPackageDto.getBizId());
            if (!ObjectHelper.isNotNull(unloadVehicleEntity)) {
                return new InvokeResult<>(TASK_NO_FOUND_BY_PARAMS_CODE, TASK_NO_FOUND_BY_PARAMS_MESSAGE);
            }
            // 通用校验
            checkScan(scanPackageDto, unloadVehicleEntity);
            // 校验跨场地支援权限
            if (!unloadVehicleEntity.getEndSiteId().equals((long) scanPackageDto.getCurrentOperate().getSiteCode())) {
                log.warn("支援人员无需操作:bizId={},erp={}", scanPackageDto.getBizId(), scanPackageDto.getUser().getUserErp());
                invokeResult.customMessage(RESULT_PARAMETER_ERROR_CODE, "支援人员无需操作该任务，待任务完成后该任务会自动清除");
                return invokeResult;
            }
            // 按包裹扫描
            if (ScanTypeEnum.PACKAGE.getCode().equals(scanPackageDto.getType())) {
                return packageScan(scanPackageDto, unloadVehicleEntity, invokeResult);
                // 按运单扫描
            } else if (ScanTypeEnum.WAYBILL.getCode().equals(scanPackageDto.getType())) {
                return waybillScan(scanPackageDto, unloadVehicleEntity, invokeResult);
                // 按箱扫描
            } else if (ScanTypeEnum.BOX.getCode().equals(scanPackageDto.getType())) {

            }
        } catch (JyBizException | LoadIllegalException e) {
            invokeResult.customMessage(RESULT_INTERCEPT_CODE, e.getMessage());
            return invokeResult;
        } catch (Exception e) {
            if (e instanceof UnloadPackageBoardException) {
                scanPackageRespDto.getConfirmMsg().put(e.getMessage(), e.getMessage());
                invokeResult.customMessage(JdCResponse.CODE_CONFIRM, e.getMessage());
                return invokeResult;
            }
            log.error("人工卸车扫描接口发生异常：e=", e);
            invokeResult.customMessage(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
        }
        return invokeResult;
    }

    /**
     * 设置扫描返回对象
     */
    private ScanPackageRespDto convertToScanResult(ScanPackageDto request) {
        ScanPackageRespDto scanResult = new ScanPackageRespDto();
        scanResult.setBizId(request.getBizId());
        scanResult.setBoardCode(request.getBoardCode());
        scanResult.setGoodsAreaCode(request.getGoodsAreaCode());
        scanResult.setBarCode(request.getScanCode());
        if (request.getPrevSiteCode() != null) {
            scanResult.setPrevSiteId(Long.valueOf(request.getPrevSiteCode()));
        }
        scanResult.setPrevSiteName(request.getPrevSiteName());
        if (request.getNextSiteCode() != null) {
            scanResult.setEndSiteId(Long.valueOf(request.getNextSiteCode()));
        }
        scanResult.setEndSiteName(request.getNextSiteName());
        scanResult.setWarnMsg(new HashMap<String, String>(5));
        scanResult.setConfirmMsg(new HashMap<String, String>(1));
        return scanResult;
    }

    private InvokeResult<ScanPackageRespDto> packageScan(ScanPackageDto scanPackageDto, JyBizTaskUnloadVehicleEntity unloadVehicleEntity,
                                                         InvokeResult<ScanPackageRespDto> invokeResult) {
        String barCode = scanPackageDto.getScanCode();
        String bizId = scanPackageDto.getBizId();
        Integer operateSiteCode = scanPackageDto.getCurrentOperate().getSiteCode();
        String boardCode = scanPackageDto.getBoardCode();
        ScanPackageRespDto scanPackageRespDto = invokeResult.getData();
        DeliveryPackageD packageD = waybillPackageManager.getPackageInfoByPackageCode(barCode);
        if (packageD == null) {
            invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "该包裹号不存在，请检查包裹号是否正确！");
            return invokeResult;
        }
        String waybillCode = WaybillUtil.getWaybillCode(scanPackageDto.getScanCode());
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        if (waybill == null) {
            invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "该运单号不存在，请检查运单号是否正确！");
            return invokeResult;
        }
        // 判断是否是跨越的取消订单
        String kyCancelCheckStr = jyUnloadVehicleCheckTysService.kyExpressCancelCheck(operateSiteCode, waybill);
        if (StringUtils.isNotBlank(kyCancelCheckStr)) {
            invokeResult.customMessage(RESULT_PARAMETER_ERROR_CODE, kyCancelCheckStr);
            return invokeResult;
        }
        // 包裹超重校验
        jyUnloadVehicleCheckTysService.checkPackageOverWeight(packageD, waybill, scanPackageRespDto);
        // 包裹是否扫描成功
        jyUnloadVehicleCheckTysService.packageIsScanBoard(bizId, barCode, boardCode);
        if (!scanPackageDto.getIsForceCombination()) {
            UnloadScanDto unloadScanDto = createUnloadDto(scanPackageDto, unloadVehicleEntity);
            // 验货校验
            jyUnloadVehicleCheckTysService.inspectionIntercept(barCode, waybill, unloadScanDto);
            // 组装返回数据
            jyUnloadVehicleCheckTysService.assembleReturnData(scanPackageDto, scanPackageRespDto, unloadVehicleEntity, unloadScanDto);
            // 无任务设置上游站点
            jyUnloadVehicleCheckTysService.setStartSiteForJyUnloadVehicle(scanPackageDto, scanPackageRespDto, unloadVehicleEntity);
            // B网快运发货规则校验
            String interceptResult = jyUnloadVehicleCheckTysService.interceptValidateUnloadCar(waybill, packageD, scanPackageRespDto, barCode);
            if (StringUtils.isNotBlank(interceptResult)) {
                invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult);
                return invokeResult;
            }
            // 专网校验
            boolean privateNetworkFlag = jyUnloadVehicleCheckTysService.privateNetworkCheck(waybill, scanPackageRespDto);
            if (privateNetworkFlag) {
                return invokeResult;
            }
            // 跨越目的转运中心自提校验
            String kyResult = jyUnloadVehicleCheckTysService.kyExpressCheck(waybill, operateSiteCode);
            if (StringUtils.isNotBlank(kyResult)) {
                invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, kyResult);
                return invokeResult;
            }
            // 人工卸车模式组板校验
            if (UnloadCarTypeEnum.MANUAL_TYPE.getCode().equals(scanPackageDto.getWorkType())) {
                // 路由校验、生成板号
                boolean routerCheckResult = jyUnloadVehicleCheckTysService.routerCheck(scanPackageRespDto, scanPackageDto);
                if (!routerCheckResult) {
                    log.info("packageCodeScanNew--路由校验失败：该包裹流向与当前板号流向不一致, req=【{}】,res=【{}】", JsonUtils.toJSONString(scanPackageDto), JsonUtils.toJSONString(invokeResult));
                    return invokeResult;
                }
                // 是否发货校验
                jyUnloadVehicleCheckTysService.isSendCheck(scanPackageDto);
                // 板上包裹数限制
                jyUnloadVehicleCheckTysService.packageCountCheck(scanPackageDto);
                // ver组板拦截校验
                String boardCheckStr = jyUnloadVehicleCheckTysService.boardCombinationCheck(scanPackageDto, scanPackageRespDto);
                if (StringUtils.isNotBlank(boardCheckStr)) {
                    invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, boardCheckStr);
                    return invokeResult;
                }
            }
        }
        // 人工卸车模式组板
        if (UnloadCarTypeEnum.MANUAL_TYPE.getCode().equals(scanPackageDto.getWorkType())) {
            // 卸车处理并回传TC组板关系
            jyUnloadVehicleCheckTysService.dealUnloadAndBoxToBoard(scanPackageDto, scanPackageRespDto);
        }
        // 设置拦截缓存
        jyUnloadVehicleCheckTysService.setCacheOfSealCarAndPackageIntercept(bizId, barCode);
        return invokeResult;
    }

    private InvokeResult<ScanPackageRespDto> waybillScan(ScanPackageDto scanPackageDto, JyBizTaskUnloadVehicleEntity unloadVehicleEntity,
                                                         InvokeResult<ScanPackageRespDto> invokeResult) {
        String barCode = scanPackageDto.getScanCode();
        String bizId = scanPackageDto.getBizId();
        ScanPackageRespDto scanPackageRespDto = invokeResult.getData();
        DeliveryPackageD packageD = null;
        String waybillCode = scanPackageDto.getScanCode();
        if (WaybillUtil.isPackageCode(barCode)) {
            packageD = waybillPackageManager.getPackageInfoByPackageCode(barCode);
            if (packageD == null) {
                invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "该包裹号不存在，请检查包裹号是否正确！");
                return invokeResult;
            }
            waybillCode = WaybillUtil.getWaybillCode(scanPackageDto.getScanCode());
            scanPackageDto.setScanCode(waybillCode);
            barCode = waybillCode;
        }
        Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        if (waybill == null) {
            invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "该运单号不存在，请检查运单号是否正确！");
            return invokeResult;
        }
        scanPackageDto.setGoodsNumber(waybill.getGoodNumber());
        // 校验是否达到大宗使用标准
        String checkStr = jyUnloadVehicleCheckTysService.checkIsMeetWaybillStandard(waybill);
        if (StringUtils.isNotBlank(checkStr)) {
            invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, checkStr);
            return invokeResult;
        }
        // 校验是否为KA运单
        boolean isKaWaybill = jyUnloadVehicleCheckTysService.checkIsKaWaybill(waybill);
        if (isKaWaybill) {
            log.warn("此包裹为大件KA运单,不支持大宗按单操作,请逐包裹扫描！运单号={}", waybillCode);
            invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "此包裹为大件KA运单,不支持大宗按单操作,请逐包裹扫描!");
            return invokeResult;
        }
        boolean isRepeatScan = jyUnloadVehicleCheckTysService.checkWaybillScanIsRepeat(bizId, waybillCode);
        // 运单是否扫描成功
        if (isRepeatScan) {
            log.warn("运单卸车扫描--该运单已经扫描过，请勿重复扫:bizId={},waybillCode={}", bizId, waybillCode);
            invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, "该运单已经扫描过，请勿重复扫！");
            return invokeResult;
        }
        UnloadScanDto unloadScanDto = createUnloadDto(scanPackageDto, unloadVehicleEntity);
        // 验货校验
        jyUnloadVehicleCheckTysService.inspectionIntercept(barCode, waybill, unloadScanDto);
        // 组装返回数据
        jyUnloadVehicleCheckTysService.assembleReturnData(scanPackageDto, scanPackageRespDto, unloadVehicleEntity, unloadScanDto);
        // 设置拦截缓存
        jyUnloadVehicleCheckTysService.waybillInspectSuccessAfter(bizId, waybillCode);
        // 无任务设置上游站点
        jyUnloadVehicleCheckTysService.setStartSiteForJyUnloadVehicle(scanPackageDto, scanPackageRespDto, unloadVehicleEntity);
        // 专网校验
        boolean privateNetworkFlag = jyUnloadVehicleCheckTysService.privateNetworkCheck(waybill, scanPackageRespDto);
        if (privateNetworkFlag) {
            return invokeResult;
        }
        // B网快运发货规则校验
        String interceptResult = jyUnloadVehicleCheckTysService.interceptValidateUnloadCar(waybill, packageD, scanPackageRespDto, barCode);
        if (StringUtils.isNotBlank(interceptResult)) {
            invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, interceptResult);
            return invokeResult;
        }
        return invokeResult;
    }





    private void checkScan(ScanPackageDto scanPackageDto, JyBizTaskUnloadVehicleEntity unloadVehicleEntity) {
        if (BusinessUtil.isBoxcode(scanPackageDto.getScanCode())) {
            return;
        }
        String scanCode = scanPackageDto.getScanCode();
        if (WaybillUtil.isPackageCode(scanCode)) {
            scanCode = WaybillUtil.getWaybillCode(scanCode);
        }
        String routerStr = waybillCacheService.getRouterByWaybillCode(scanCode);
        Integer nextSiteCode = getRouteNextSite(scanPackageDto.getCurrentOperate().getSiteCode(), routerStr);
        scanPackageDto.setNextSiteCode(nextSiteCode);
        if (StringUtils.isBlank(unloadVehicleEntity.getStartSiteName())) {
            Integer prevSiteCode = getPrevSiteCodeByRouter(routerStr, scanPackageDto.getCurrentOperate().getSiteCode());
            scanPackageDto.setPrevSiteCode(prevSiteCode);
        } else {
            scanPackageDto.setPrevSiteCode(unloadVehicleEntity.getStartSiteId().intValue());
            scanPackageDto.setPrevSiteName(unloadVehicleEntity.getStartSiteName());
        }
        String goodsAreaCode = getGoodsAreaCode(scanPackageDto.getCurrentOperate().getSiteCode(), nextSiteCode);
        if (ObjectHelper.isNotNull(scanPackageDto.getGoodsAreaCode()) && ObjectHelper.isNotNull(goodsAreaCode)) {
            if (!goodsAreaCode.equals(scanPackageDto.getGoodsAreaCode())) {
                throw new JyBizException("扫描包裹非本货区，请移除本区！");
            }
        }
        scanPackageDto.setGoodsAreaCode(goodsAreaCode);
    }


    private UnloadScanDto createUnloadDto(ScanPackageDto request, JyBizTaskUnloadVehicleEntity taskUnloadVehicle) {
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
        unloadScanDto.setBarCode(request.getScanCode());
        unloadScanDto.setOperateTime(operateTime);
        unloadScanDto.setCreateUserErp(request.getUser().getUserErp());
        unloadScanDto.setCreateUserName(request.getUser().getUserName());
        unloadScanDto.setUpdateUserErp(request.getUser().getUserErp());
        unloadScanDto.setUpdateUserName(request.getUser().getUserName());
        unloadScanDto.setCreateTime(operateTime);
        unloadScanDto.setUpdateTime(operateTime);
        // 只有已完成的卸车任务，才算补扫
        if (JyBizTaskUnloadStatusEnum.UN_LOAD_DONE.getCode().equals(taskUnloadVehicle.getVehicleStatus())) {
            unloadScanDto.setSupplementary(Boolean.TRUE);
        }
        unloadScanDto.setGroupCode(request.getGroupCode());
        unloadScanDto.setTaskId(request.getTaskId());
        unloadScanDto.setTaskType(JyBizTaskSourceTypeEnum.TRANSPORT.getCode());
        return unloadScanDto;
    }




    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.scanForPipelining",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ScanPackageRespDto> scanForPipelining(ScanPackageDto scanPackageDto) {
        InvokeResult<ScanPackageRespDto> invokeResult = new InvokeResult<>();
        invokeResult.success();
        ScanPackageRespDto scanPackageRespDto = convertToScanResult(scanPackageDto);
        invokeResult.setData(scanPackageRespDto);

        log.info("invoking jy scanAndComBoard,params: {}", JsonHelper.toJson(scanPackageDto));
        try {
            // 校验任务
            JyBizTaskUnloadVehicleEntity unloadVehicleEntity = jyBizTaskUnloadVehicleService.findByBizId(scanPackageDto.getBizId());
            if (!ObjectHelper.isNotNull(unloadVehicleEntity)) {
                return new InvokeResult<>(TASK_NO_FOUND_BY_PARAMS_CODE, TASK_NO_FOUND_BY_PARAMS_MESSAGE);
            }
            // 通用校验
            checkScan(scanPackageDto, unloadVehicleEntity);
            // 校验跨场地支援权限
            if (!unloadVehicleEntity.getEndSiteId().equals((long) scanPackageDto.getCurrentOperate().getSiteCode())) {
                log.warn("支援人员无需操作:bizId={},erp={}", scanPackageDto.getBizId(), scanPackageDto.getUser().getUserErp());
                invokeResult.customMessage(RESULT_PARAMETER_ERROR_CODE, "支援人员无需操作该任务，待任务完成后该任务会自动清除");
                return invokeResult;
            }
            // 按包裹扫描
            if (ScanTypeEnum.PACKAGE.getCode().equals(scanPackageDto.getType())) {
                return packageScan(scanPackageDto, unloadVehicleEntity, invokeResult);
                // 按运单扫描
            } else if (ScanTypeEnum.WAYBILL.getCode().equals(scanPackageDto.getType())) {
                return waybillScan(scanPackageDto, unloadVehicleEntity, invokeResult);
                // 按箱扫描
            } else if (ScanTypeEnum.BOX.getCode().equals(scanPackageDto.getType())) {

            }
        } catch (JyBizException | LoadIllegalException e) {
            invokeResult.customMessage(RESULT_INTERCEPT_CODE, e.getMessage());
            return invokeResult;
        } catch (Exception e) {
            if (e instanceof UnloadPackageBoardException) {
                invokeResult.customMessage(JdCResponse.CODE_CONFIRM, e.getMessage());
                return invokeResult;
            }
            log.error("流水线卸车扫描接口发生异常：e=", e);
            invokeResult.customMessage(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
        }
        return invokeResult;
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryComBoardDataByBoardCode",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ComBoardDto> queryComBoardDataByBoardCode(String boardCode) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.queryComBoardDataByBoardCode--根据板号查询板号信息--";
        InvokeResult<ComBoardDto> res = new InvokeResult<>();
        res.success();
        ComBoardDto resData = new ComBoardDto();
        try{

            Response<Board> response = groupBoardManager.getBoard(boardCode);
            if(response != null && response.getCode() == ResponseEnum.SUCCESS.getIndex()) {
                if(response.getData() == null) {
                    res.setMessage("查询板信息为空");
                    return res;
                }
                resData.setBoardCode(boardCode);
                resData.setEndSiteId(Long.valueOf(response.getData().getDestinationId()));
                resData.setEndSiteName(response.getData().getDestination());
            }else {
                log.error("{}查询失败，板号={}，res={}", methodDesc, boardCode, JsonHelper.toJson(response));
                String errMsg = res == null || StringUtils.isBlank(res.getMessage()) ? "根据板号查询板信息异常" : res.getMessage();
                res.error(errMsg);
                return res;
            }
            JyUnloadVehicleBoardEntity jyUnloadVehicleBoardEntity = jyUnloadVehicleBoardDao.selectByBoardCode(boardCode);
            if(jyUnloadVehicleBoardEntity != null) {
                resData.setGoodsAreaCode(jyUnloadVehicleBoardEntity.getGoodsAreaCode());
            }else {
                //实操非核心展示：不做强拦截
                log.error("{}--根据板号{}查任务板关系表中货区编码为空", methodDesc, boardCode);
            }
            res.setData(resData);
            return res;
        }catch (Exception e) {
            log.error("{}服务异常，请求={}，errMsg={}", methodDesc, boardCode, e.getMessage(), e);
            res.setMessage("根据板号查询板号信息服务异常");
            return res;
        }
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.handoverTask",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Void> handoverTask(UnloadVehicleTaskDto unloadVehicleTask) {
        InvokeResult<Void> result = new InvokeResult<>();
        result.success();
        try {
            JyBizTaskUnloadVehicleEntity taskUnloadVehicle = jyBizTaskUnloadVehicleService.findByBizId(unloadVehicleTask.getBizId());
            if (taskUnloadVehicle == null) {
                result.error("任务不存在，交班失败！");
                return result;
            }
            // 只有处于卸车状态的任务才能交班
            if (!JyBizTaskUnloadStatusEnum.UN_LOADING.getCode().equals(taskUnloadVehicle.getVehicleStatus())) {
                result.error("当前任务状态不支持交班！");
                return result;
            }
            // 查询子任务bizId
            JyBizTaskUnloadVehicleStageEntity condition = new JyBizTaskUnloadVehicleStageEntity();
            condition.setUnloadVehicleBizId(unloadVehicleTask.getBizId());
            condition.setStatus(JyBizTaskStageStatusEnum.DOING.getCode());
            JyBizTaskUnloadVehicleStageEntity entity = jyBizTaskUnloadVehicleStageService.queryCurrentStage(condition);
            if (entity == null) {
                result.error("当前任务状态不支持交班！");
                return result;
            }
            entity.setUpdateUserErp(unloadVehicleTask.getUser().getUserErp());
            entity.setUpdateUserName(unloadVehicleTask.getUser().getUserName());
            entity.setUpdateTime(new Date());
            entity.setEndTime(new Date());
            entity.setStatus(JyBizTaskStageStatusEnum.COMPLETE.getCode());
            jyBizTaskUnloadVehicleStageService.updateByPrimaryKeySelective(entity);
        } catch (Exception e) {
            log.error("handoverTask|交班服务异常,req={},errMsg=", JsonHelper.toJson(unloadVehicleTask), e);
            result.error("交班服务异常");
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryGoodsCategoryByDiffDimension",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<GoodsCategoryDto>> queryGoodsCategoryByDiffDimension(QueryGoodsCategory queryGoodsCategory) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.queryGoodsCategoryByDiffDimension--查询货物分类服务--";
        InvokeResult<List<GoodsCategoryDto>> res = new InvokeResult<>();
        res.success();
        try{
            if(queryGoodsCategory == null) {
                res.error("请求参数为空");
                return  res;
            }

            JyUnloadAggsEntity entity = new JyUnloadAggsEntity();
            entity.setBizId(queryGoodsCategory.getBizId());
            entity.setBoardCode(queryGoodsCategory.getBoardCode());
            List<GoodsCategoryDto> goodsCategoryDtoList = jyUnloadAggsService.queryGoodsCategoryStatistics(entity);

            res.setData(goodsCategoryDtoList);
            return res;
        }catch (Exception e) {
            log.error("{}服务异常, req={}, errMsg={}", methodDesc, JsonHelper.toJson(queryGoodsCategory), e.getMessage(), e);
            res.error("查询货物分类服务服务异常");
            return res;
        }
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryUnloadDetailByDiffDimension",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ScanStatisticsInnerDto> queryUnloadDetailByDiffDimension(QueryUnloadDetailDto queryUnloadDetailDto) {
        //统计数据查询
        ScanStatisticsInnerDto scanStatisticsInnerDto = new ScanStatisticsInnerDto();
        JyUnloadAggsEntity condition = new JyUnloadAggsEntity();
        condition.setBizId(queryUnloadDetailDto.getBizId());

        if (!queryUnloadDetailDto.getExpFlag()) {
            List<GoodsCategoryDto> goodsCategoryDtoList = jyUnloadAggsService.queryGoodsCategoryStatistics(condition);
            scanStatisticsInnerDto.setGoodsCategoryDtoList(goodsCategoryDtoList);
        } else {
            List<ExcepScanDto> excepScanDtoList = jyUnloadAggsService.queryExcepScanStatistics(condition);
            scanStatisticsInnerDto.setExcepScanDtoList(excepScanDtoList);
        }
        //运单列表查询
        Pager<JyVehicleTaskUnloadDetail> query = assembleQueryUnloadTaskWaybillCondition(queryUnloadDetailDto);
        Pager<JyUnloadTaskWaybillAgg> waybillAggPager = jyUnloadVehicleManager.queryUnloadTaskWaybill(query);
        if (ObjectHelper.isNotNull(waybillAggPager) && CollectionUtils.isNotEmpty(waybillAggPager.getData())) {
            List<UnloadWaybillDto> unloadWaybillDtoList = new ArrayList<>();
            for (JyUnloadTaskWaybillAgg data : waybillAggPager.getData()) {
                UnloadWaybillDto unloadWaybillDto = new UnloadWaybillDto();
                unloadWaybillDto.setWaybillCode(data.getWaybillCode());
                unloadWaybillDto.setPackageCount(data.getPackageCount());
                unloadWaybillDtoList.add(unloadWaybillDto);
            }
            scanStatisticsInnerDto.setUnloadWaybillDtoList(unloadWaybillDtoList);
        }
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, scanStatisticsInnerDto);
    }

    private Pager<JyVehicleTaskUnloadDetail> assembleQueryUnloadTaskWaybillCondition(QueryUnloadDetailDto queryUnloadDetailDto) {
        JyVehicleTaskUnloadDetail condition = new JyVehicleTaskUnloadDetail();
        condition.setEndSiteId(queryUnloadDetailDto.getCurrentOperate().getSiteCode());
        condition.setBizId(queryUnloadDetailDto.getBizId());
        if (queryUnloadDetailDto.getExpFlag()) {
            switch (queryUnloadDetailDto.getExpType()) {
                case 1:
                    condition.setScannedFlag(0);
                    break;
                case 2:
                    condition.setInterceptFlag(1);
                    break;
                case 3:
                    condition.setMoreScanFlag(1);
                    break;
                default:
                    log.info("");
            }
        } else {
            condition.setProductType(queryUnloadDetailDto.getGoodsType());
        }
        Pager<JyVehicleTaskUnloadDetail> pager = new Pager<>();
        pager.setPageNo(queryUnloadDetailDto.getPageNo());
        pager.setPageSize(queryUnloadDetailDto.getPageSize());
        pager.setSearchVo(condition);
        return pager;
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryUnloadTaskFlow",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<UnloadTaskFlowDto>> queryUnloadTaskFlow(String bizId) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.queryUnloadTaskFlow--查询任务内流向统计服务--";
        InvokeResult<List<UnloadTaskFlowDto>> res = new InvokeResult<>();
        res.success();
        try{
            if(StringUtils.isBlank(bizId)) {
                res.error("参数缺失：bizId为空");
                return  res;
            }

            List<UnloadTaskFlowDto> resData = new ArrayList<>();
            List<JyUnloadVehicleBoardEntity> jyUnloadVehicleBoardEntityList = jyUnloadVehicleBoardDao.getFlowStatisticsByBizId(bizId);
            if(CollectionUtils.isNotEmpty(jyUnloadVehicleBoardEntityList)) {
                for(JyUnloadVehicleBoardEntity entity : jyUnloadVehicleBoardEntityList) {
                    UnloadTaskFlowDto unloadTaskFlowDto = new UnloadTaskFlowDto();
                    unloadTaskFlowDto.setGoodsAreaCode(entity.getGoodsAreaCode());
                    unloadTaskFlowDto.setEndSiteId(entity.getEndSiteId());
                    unloadTaskFlowDto.setEndSiteName(entity.getEndSiteName());
                    unloadTaskFlowDto.setComBoardCount(entity.getBoardCodeNum());
                    resData.add(unloadTaskFlowDto);
                }
            }else {
                res.setMessage("查询数据为空");
            }
            res.setData(resData);
            return res;
        }catch (Exception e) {
            log.error("{}服务异常, req={}, errMsg={}", methodDesc, bizId, e.getMessage(), e);
            res.error("查询任务内流向统计服务异常");
            return res;
        }
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryComBoarUnderTaskFlow",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<TaskFlowComBoardDto> queryComBoarUnderTaskFlow(TaskFlowDto taskFlowDto) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.queryComBoarUnderTaskFlow--查询流向内板维度统计数据服务--";
        InvokeResult<TaskFlowComBoardDto> res = new InvokeResult<>();
        res.success();
        try{
            if(taskFlowDto == null) {
                res.error("请求对象为空");
                return  res;
            }
            if(StringUtils.isBlank(taskFlowDto.getBizId())) {
                res.error("参数缺失：bizId为空");
                return  res;
            }
            if(taskFlowDto.getEndSiteId() == null) {
                res.error("参数缺失：endSiteId为空");
                return  res;
            }

            TaskFlowComBoardDto resData = new TaskFlowComBoardDto();
            List<ComBoardAggDto> comBoardDtoList = new ArrayList<>();
            Integer extraScanCount = 0;
            Integer haveScanCount = 0;

            JyUnloadVehicleBoardEntity entityParam = new JyUnloadVehicleBoardEntity();
            entityParam.setUnloadVehicleBizId(taskFlowDto.getBizId());
            entityParam.setEndSiteId(taskFlowDto.getEndSiteId());
            List<String> boardCodeList = jyUnloadVehicleBoardDao.getBoardCodeList(entityParam);

            if(CollectionUtils.isEmpty(boardCodeList)) {
                res.setMessage("查询为空");
                return  res;
            }
            for(String boardCode : boardCodeList) {
                DimensionQueryDto aggsQueryParams = new DimensionQueryDto();
                aggsQueryParams.setBizId(taskFlowDto.getBizId());
                aggsQueryParams.setBoardCode(boardCode);
                JyUnloadAggsEntity jyaggs = jyUnloadAggsDao.queryBoardStatistics(aggsQueryParams);
                if(jyaggs != null) {
                    ComBoardAggDto aggDto = new ComBoardAggDto();
                    aggDto.setBoardCode(boardCode);
                    aggDto.setHaveScanCount(jyaggs.getActualScanCount());
                    aggDto.setExtraScanCount(jyaggs.getMoreScanTotalCount());
                    comBoardDtoList.add(aggDto);
                    extraScanCount = extraScanCount + jyaggs.getMoreScanTotalCount();
                    haveScanCount = haveScanCount + jyaggs.getActualScanCount();
                }
            }
            resData.setComBoardDtoList(comBoardDtoList);
            resData.setExtraScanCount(extraScanCount);
            resData.setHaveScanCount(haveScanCount);
            res.setData(resData);
            return res;
        }catch (Exception e) {
            log.error("{}服务异常, req={}, errMsg={}", methodDesc, JsonHelper.toJson(taskFlowDto), e.getMessage(), e);
            res.error("查询流向内板维度统计数据服务异常");
            return res;
        }
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryWaybillUnderBoard",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<UnloadWaybillDto>> queryWaybillUnderBoard(QueryBoardDto queryBoardDto) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.queryWaybillUnderBoard--查询板上运单信息服务--";
        InvokeResult<List<UnloadWaybillDto>> res = new InvokeResult<>();
        res.success();
        try{
            if(queryBoardDto == null) {
                res.error("请求对象为空");
                return  res;
            }
            if(StringUtils.isBlank(queryBoardDto.getBoardCode())) {
                res.error("参数缺失：板号为空");
                return  res;
            }
            Response<BoardBoxStatisticsResDto> boardBoxRes = groupBoardManager.getBoardStatisticsByBoardCode(queryBoardDto.getBoardCode());
            if(boardBoxRes == null || boardBoxRes.getCode() != JdCResponse.CODE_SUCCESS || boardBoxRes.getData() == null) {
                log.error("{}，查询箱内数据失败，接口参数={}，异常返回结果={}", methodDesc, JsonHelper.toJson(queryBoardDto), JsonHelper.toJson(boardBoxRes));
                res.error("查询箱内数据失败");
                return  res;
            }

            List<UnloadWaybillDto> resData = new ArrayList<>();
            List<BoardWaybillStatisticsDto> boardWaybillStatisticsDtoList = boardBoxRes.getData().getWaybillStatisticsDtoList();
            if(CollectionUtils.isNotEmpty(boardWaybillStatisticsDtoList)) {
                for(BoardWaybillStatisticsDto bwsd : boardWaybillStatisticsDtoList) {
                    UnloadWaybillDto unloadWaybillDto = new UnloadWaybillDto();
                    unloadWaybillDto.setWaybillCode(bwsd.getWaybillCode());
                    int packageCount = CollectionUtils.isEmpty(bwsd.getPackageCodeList()) ? 0 : bwsd.getPackageCodeList().size();
                    unloadWaybillDto.setPackageCount(packageCount);
                    resData.add(unloadWaybillDto);
                }
            }
            res.setData(resData);
            return res;
        }catch (Exception e) {
            log.error("{}服务异常, req={}, errMsg={}", methodDesc, JsonHelper.toJson(queryBoardDto), e.getMessage(), e);
            res.error("查询板上运单信息服务异常");
            return res;
        }
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryPackageUnderWaybill",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<UnloadPackageDto>> queryPackageUnderWaybill(QueryWaybillDto queryWaybillDto) {
        //板内查询做实操取消发货，明细取DB
        if (ObjectHelper.isNotNull(queryWaybillDto.getBoardCode())) {
            //search DB
            List<PackageDto> packageDtos = groupBoardManager.getPackageCodeUnderComBoard(queryWaybillDto.getBoardCode(), queryWaybillDto.getWaybillCode());
            List<UnloadPackageDto> unloadPackageDtoList = BeanUtils.copy(packageDtos, UnloadPackageDto.class);
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadPackageDtoList);
        } else {
         //任务内查询仅做展示，查ES
            Pager<JyVehicleTaskUnloadDetail> condition = assembleScanQueryCondition(queryWaybillDto);
            Pager<JyVehicleTaskUnloadDetail> pagerResp = null;
            if (UnloadBarCodeQueryEntranceEnum.TO_SCAN.getCode().equals(queryWaybillDto.getExpType())) {
                pagerResp = iJyUnloadVehicleManager.queryToScanBarCodeDetail(condition);
            } else if (UnloadBarCodeQueryEntranceEnum.MORE_SCAN.getCode().equals(queryWaybillDto.getExpType())) {
                pagerResp = iJyUnloadVehicleManager.queryMoreScanBarCodeDetail(condition);
            } else if (UnloadBarCodeQueryEntranceEnum.INTERCEPT.getCode().equals(queryWaybillDto.getExpType())) {
                pagerResp = iJyUnloadVehicleManager.queryInterceptBarCodeDetail(condition);
            }
            List<UnloadPackageDto> unloadPackageDtoList = new ArrayList<>();
            if (ObjectHelper.isNotNull(pagerResp.getData())) {
                List<JyVehicleTaskUnloadDetail> unloadDetailList = pagerResp.getData();
                for (JyVehicleTaskUnloadDetail unloadDetail : unloadDetailList) {
                    UnloadPackageDto unloadPackageDto = new UnloadPackageDto();
                    unloadPackageDto.setPackageCode(unloadDetail.getPackageCode());
                    unloadPackageDtoList.add(unloadPackageDto);
                }
            }
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadPackageDtoList);
        }
    }

    private Pager<JyVehicleTaskUnloadDetail> assembleScanQueryCondition(QueryWaybillDto queryWaybillDto) {
        JyVehicleTaskUnloadDetail condition = new JyVehicleTaskUnloadDetail();
        condition.setBizId(queryWaybillDto.getBizId());
        condition.setEndSiteId(queryWaybillDto.getCurrentOperate().getSiteCode());
        condition.setWaybillCode(queryWaybillDto.getWaybillCode());
        if (ObjectHelper.isNotNull(queryWaybillDto.getGoodsType())) {
            condition.setProductType(queryWaybillDto.getGoodsType());
        } else {
            switch (queryWaybillDto.getExpType()) {
                case 1:
                    condition.setScannedFlag(0);
                    break;
                case 2:
                    condition.setInterceptFlag(1);
                    break;
                case 3:
                    condition.setMoreScanFlag(1);
                    break;
                default:
                    log.info("");
            }
        }
        Pager<JyVehicleTaskUnloadDetail> pager = new Pager<>();
        pager.setPageNo(queryWaybillDto.getPageNo());
        pager.setPageSize(queryWaybillDto.getPageSize());
        pager.setSearchVo(condition);
        return pager;
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.cancelComBoard",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult cancelComBoard(CancelComBoardDto cancelComBoardDto) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.cancelComBoard--批量取消组板服务--";
        InvokeResult<List<UnloadWaybillDto>> res = new InvokeResult<>();
        res.success();
        try{
            if(cancelComBoardDto == null) {
                res.error("请求对象为空");
                return  res;
            }
            if(StringUtils.isBlank(cancelComBoardDto.getBoardCode()) || CollectionUtils.isEmpty(cancelComBoardDto.getPackageCodeList())) {
                res.error("参数缺失：板号或包裹号为空");
                return  res;
            }
            if(cancelComBoardDto.getUser() == null || StringUtils.isBlank(cancelComBoardDto.getUser().getUserErp())) {
                res.error("参数缺失：操作人erp为空");
                return  res;
            }
            if(cancelComBoardDto.getCurrentOperate() == null ) {
                res.error("参数缺失：操作人场地为空");
                return  res;
            }

            RemoveBoardBoxDto removeBoardBoxDto = new RemoveBoardBoxDto();
            removeBoardBoxDto.setBoardCode(cancelComBoardDto.getBoardCode());
            removeBoardBoxDto.setBoxCodeList(cancelComBoardDto.getPackageCodeList());
            removeBoardBoxDto.setOperatorErp(cancelComBoardDto.getUser().getUserErp());
            removeBoardBoxDto.setOperatorName(cancelComBoardDto.getUser().getUserName());
            removeBoardBoxDto.setSiteCode(cancelComBoardDto.getCurrentOperate().getSiteCode());

            Response removeBoardBoxRes = groupBoardManager.batchRemoveBardBoxByBoxCodes(removeBoardBoxDto);
            if(removeBoardBoxRes == null || removeBoardBoxRes.getCode() != JdCResponse.CODE_SUCCESS) {
               log.error("{}，操作失败，接口参数={}，异常返回结果={}", methodDesc, JsonHelper.toJson(removeBoardBoxDto), JsonHelper.toJson(removeBoardBoxRes));
               String errMsg =  (removeBoardBoxRes == null || StringUtils.isBlank(removeBoardBoxRes.getMesseage())) ? "批量取消组板服务失败" : removeBoardBoxRes.getMesseage();
               res.error(errMsg);
               return res;
            }
            return res;
        }catch (Exception e) {
            log.error("{}服务异常, req={}, errMsg={}", methodDesc, JsonHelper.toJson(cancelComBoardDto), e.getMessage(), e);
            res.error("批量取消组板服务异常");
            return res;
        }
    }

    @Override
    public InvokeResult<Void> comBoardComplete(String boardCode) {
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        if (StringUtils.isBlank(boardCode)) {
            res.parameterError("板号不能为空");
            return res;
        }
        try {
            Response<Boolean> closeBoardResponse = boardCombinationService.closeBoard(boardCode);
            // 关板失败
            if (InvokeResult.RESULT_SUCCESS_CODE != closeBoardResponse.getCode() || !closeBoardResponse.getData()) {
                log.warn("组板完成调用TC关板失败,板号：{}，关板结果：{}", boardCode, JsonHelper.toJson(closeBoardResponse));
                res.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, closeBoardResponse.getMesseage());
                return res;
            }
        } catch (Exception e) {
            log.error("组板完成调用TC关板异常：板号={}" , boardCode, e);
            res.error("组板完成发生异常");
        }
        return res;
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.getWaybillNextRouter",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public Integer getWaybillNextRouter(String waybillCode, Integer startSiteId) {
        String routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
        return getRouteNextSite(startSiteId, routerStr);
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.getGoodsAreaCode",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public String getGoodsAreaCode(Integer currentSiteCode, Integer nextSiteCode) {
        if (currentSiteCode == null || nextSiteCode == null) {
            log.warn("jy转运卸车岗查询货区编码参数错误，currentSiteCode={}不可为空，nextSiteCode={}不可为空", currentSiteCode, nextSiteCode);
        }
        return jyUnloadVehicleManager.getGoodsAreaCode(currentSiteCode, nextSiteCode);
    }

    private Integer getRouteNextSite(Integer startSiteId, String routerStr) {
        if (StringUtils.isNotBlank(routerStr)) {
            String[] routerNodes = routerStr.split(WAYBILL_ROUTER_SPLIT);
            for (int i = 0; i < routerNodes.length - 1; i++) {
                Integer curNode = Integer.valueOf(routerNodes[i]);
                Integer nextNode = Integer.valueOf(routerNodes[i + 1]);
                if (startSiteId.equals(curNode)) {
                    return nextNode;
                }
            }
        }
        return null;
    }

    /**
     * 根据当前站点ID从路由链路中筛选出上一路由
     * @param router 路由链路 10186|364605|910......
     * @param createSiteCode 当前站点ID
     * @return 上游站点ID
     */
    public static Integer getPrevSiteCodeByRouter(String router, int createSiteCode) {
        if (org.apache.commons.lang.StringUtils.isBlank(router)) {
            return null;
        }
        Integer prevSiteCode = null;
        String[] routerNodes = router.split("\\|");
        for (int i = 0; i < routerNodes.length - 1; i ++) {
            int curNode = Integer.parseInt(routerNodes[i]);
            // 如果该网点ID等于当前操作站点ID
            if (curNode == createSiteCode) {
                // 如果当前路由节点不是第一个
                if (i != 0) {
                    // 获取上一个
                    prevSiteCode = Integer.parseInt(routerNodes[i - 1]);
                }
            }
        }
        return prevSiteCode;
    }

    @Override
    public InvokeResult<UnloadMasterChildTaskRespDto> queryMasterChildTaskInfoByBizId(String masterBizId, Boolean queryChildTaskFlag) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.queryMasterChildTaskInfoByBizId--根据主BizId查询主子任务服务--";
        InvokeResult<UnloadMasterChildTaskRespDto> res = new InvokeResult<>();
        try{
            if(StringUtils.isBlank(masterBizId)) {
                res.error("参数缺失：必传任务bizId");
                return res;
            }
            UnloadMasterChildTaskRespDto resData = new UnloadMasterChildTaskRespDto();
            //主任务
            JyBizTaskUnloadVehicleEntity jyMasterTask = jyBizTaskUnloadVehicleDao.findByBizId(masterBizId);
            UnloadMasterTaskDto masterTask = BeanUtils.convert(jyMasterTask, UnloadMasterTaskDto.class);
            resData.setUnloadMasterTaskDto(masterTask);
            if(queryChildTaskFlag != null && queryChildTaskFlag) {
                //子任务
                List<UnloadChildTaskDto> unloadChildTaskDtoList = new ArrayList<>();
                List<JyBizTaskUnloadVehicleStageEntity> jyChildTaskList = jyBizTaskUnloadVehicleStageDao.queryByParentBizId(masterBizId);
                if(CollectionUtils.isNotEmpty(jyChildTaskList)) {
                    for (JyBizTaskUnloadVehicleStageEntity childTaskInfo : jyChildTaskList) {
                        unloadChildTaskDtoList.add(BeanUtils.convert(childTaskInfo, UnloadChildTaskDto.class));
                    }
                }
                resData.setUnloadChildTaskDtoList(unloadChildTaskDtoList);
            }
            res.setData(resData);
            return res;
        }catch (Exception e) {
            log.error("{}服务异常，masterBizId={}，errMsg={}", methodDesc, masterBizId, e.getMessage(), e);
            res.error("根据主BizId查询主子任务服务异常 " + e.getMessage());
            return  res;
        }
    }

    @Override
    public InvokeResult<List<UnloadBoardRespDto>> queryTaskBoardInfoByBizId(String masterBizId) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.queryTaskBoardInfoByBizId--根据主BizId查询任务板关系服务--";
        InvokeResult<List<UnloadBoardRespDto>> res = new InvokeResult<>();
        res.success();
        try{
            //主任务
            JyUnloadVehicleBoardEntity param = new JyUnloadVehicleBoardEntity();
            param.setUnloadVehicleBizId(masterBizId);
            List<JyUnloadVehicleBoardEntity> jyMasterTask = jyUnloadVehicleBoardDao.getTaskBoardInfoList(param);
            if(CollectionUtils.isEmpty(jyMasterTask)) {
                res.setMessage("查询数据为空");
                return res;
            }
            List<UnloadBoardRespDto> resDataList = new ArrayList<>();
            for(JyUnloadVehicleBoardEntity entity : jyMasterTask) {
                UnloadBoardRespDto taskBoardInfo = BeanUtils.convert(entity, UnloadBoardRespDto.class);
                resDataList.add(taskBoardInfo);
            }
            res.setData(resDataList);
            return res;
        }catch (Exception e) {
            log.error("{}服务异常，masterBizId={}，errMsg={}", methodDesc, masterBizId, e.getMessage(), e);
            res.error("根据主BizId查询任务板关系服务异常 " + e.getMessage());
            return  res;
        }
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.getTaskFlowBoardInfoByPackageCode",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<FlowBoardDto> getTaskFlowBoardInfoByPackageCode(FlowBoardDto flowBoardDto) {
        InvokeResult<FlowBoardDto> response = new InvokeResult<>();
        response.success();
        FlowBoardDto res = new FlowBoardDto();
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.getTaskFlowBoardInfoByPackageCode--根据包裹号查询任务下已经组板的流向板数据--";
        if(flowBoardDto == null) {
            response.error("参数为空");
            return response;
        }
        try{
            log.info("{}start--参数={}", methodDesc, JsonUtils.toJSONString(flowBoardDto));
            if(flowBoardDto.getUser() == null || com.jd.jsf.gd.util.StringUtils.isBlank(flowBoardDto.getUser().getUserErp())) {
                response.error("请求操作人erp为空");
                return response;
            }
            if(flowBoardDto.getCurrentOperate() == null || flowBoardDto.getCurrentOperate().getSiteCode() < 0) {
                response.error("请求场地编码为空");
                return response;
            }
            if(com.jd.jsf.gd.util.StringUtils.isBlank(flowBoardDto.getBizId())){
                response.error("任务编码为空");
                return response;
            }
            if(com.jd.jsf.gd.util.StringUtils.isBlank(flowBoardDto.getPackageCode())){
                response.error("包裹号为空");
                return response;
            }
            if(!WaybillUtil.isPackageCode(flowBoardDto.getPackageCode())) {
                response.error("扫描非包裹号");
                return response;
            }
            //查询板号
            Response<Board>  boardResponse = groupBoardManager.getBoardByBoxCode(flowBoardDto.getPackageCode(), flowBoardDto.getCurrentOperate().getSiteCode());
            if (null == boardResponse || boardResponse.getCode() != 200) {
                log.warn("{}--查询包裹所在板异常--packageCode={},siteCode={}", methodDesc, flowBoardDto.getPackageCode(), flowBoardDto.getCurrentOperate().getSiteCode());
                response.error("查询包裹所在板异常");
                return response;
            }
            if(boardResponse.getData() == null) {
                response.success();
                response.setMessage("未查到该包裹组板信息");
                return response;
            }
            //查询任务流向下板数据
            DimensionQueryDto aggsQueryParams = new DimensionQueryDto();
            aggsQueryParams.setBizId(flowBoardDto.getBizId());
            aggsQueryParams.setBoardCode(boardResponse.getData().getCode());
            JyUnloadAggsEntity jyaggs = jyUnloadAggsDao.queryBoardStatistics(aggsQueryParams);
            if(jyaggs == null) {
                log.warn("{}，查到该包裹已组板，但是jyUnloadAggs没有生成板上聚合数据，参数={}", methodDesc, JsonUtils.toJSONString(flowBoardDto));
                response.error("查询板上包裹数据异常");
                return response;
            }
            ComBoardAggDto aggDto = new ComBoardAggDto();
            aggDto.setBoardCode(boardResponse.getData().getCode());
            aggDto.setHaveScanCount(jyaggs.getActualScanCount());
            aggDto.setExtraScanCount(jyaggs.getMoreScanTotalCount());
            res.setComBoardAggDto(aggDto);
            //查流向
            String waybillCode = WaybillUtil.getWaybillCode(flowBoardDto.getPackageCode());
            String routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
            Integer nextSiteCode = getRouteNextSite(flowBoardDto.getCurrentOperate().getSiteCode(), routerStr);
            if(nextSiteCode == null) {
                log.warn("{}--包裹未查到路由信息--packageCode={},route={}", methodDesc, waybillCode, routerStr);
                response.error("未查到该包裹流向信息");
                return response;
            }
            //查询任务流向
            JyUnloadVehicleBoardEntity param = new JyUnloadVehicleBoardEntity();
            param.setUnloadVehicleBizId(flowBoardDto.getBizId());
            param.setStartSiteId((long)flowBoardDto.getCurrentOperate().getSiteCode());
            param.setEndSiteId(nextSiteCode.longValue());
            List<JyUnloadVehicleBoardEntity> jyUnloadVehicleBoardEntityList = jyUnloadVehicleBoardDao.getFlowStatisticsByFlow(param);
            if(CollectionUtils.isEmpty(jyUnloadVehicleBoardEntityList)) {
                log.warn("{}，查到该包裹已组板，但是jyUnloadVehicleBoard 任务板关系没有查到流向数据，参数={}", methodDesc, JsonUtils.toJSONString(flowBoardDto));
                response.error("该任务下未查到该包裹同流向信息");
                return response;
            }
            JyUnloadVehicleBoardEntity entity = jyUnloadVehicleBoardEntityList.get(0);
            UnloadTaskFlowDto unloadTaskFlowDto = new UnloadTaskFlowDto();
            unloadTaskFlowDto.setGoodsAreaCode(entity.getGoodsAreaCode());
            unloadTaskFlowDto.setEndSiteId(entity.getEndSiteId());
            unloadTaskFlowDto.setEndSiteName(entity.getEndSiteName());
            unloadTaskFlowDto.setComBoardCount(entity.getBoardCodeNum());
            res.setUnloadTaskFlowDto(unloadTaskFlowDto);

            response.setData(res);
        }catch (Exception e) {
            log.error("{} 服务异常，请求={},error={}：", methodDesc, JsonUtils.toJSONString(flowBoardDto), e.getMessage(), e);
            response.error("根据包裹号查询任务下已经组板的流向板数据服务异常");
            return response;
        }
        return response;
    }

}
