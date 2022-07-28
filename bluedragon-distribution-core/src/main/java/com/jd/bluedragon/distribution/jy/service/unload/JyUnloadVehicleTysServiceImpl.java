package com.jd.bluedragon.distribution.jy.service.unload;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.request.BoardCommonRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JyUnloadVehicleTysService;
import com.jd.bluedragon.distribution.jy.constants.UnloadCarPostConstants;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadAggsDao;
import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadDao;
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
import com.jd.bluedragon.distribution.jy.unload.JyUnloadEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadVehicleBoardEntity;
import com.jd.bluedragon.distribution.loadAndUnload.neum.UnloadCarWarnEnum;
import com.jd.bluedragon.distribution.ministore.enums.SiteTypeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.transboard.api.dto.*;
import com.jd.transboard.api.enums.BizSourceEnum;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.es.unload.JyUnloadTaskWaybillAgg;
import com.jdl.jy.realtime.model.es.unload.JyVehicleTaskUnloadDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.WAYBILL_ROUTER_SPLIT;
import static com.jd.bluedragon.core.base.BoardCommonManagerImpl.BOARD_COMBINATION_SITE_TYPE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum.*;


/**
 * @author weixiaofeng12
 * @date 2022-07-01
 * 转运卸车岗相关服务实现
 */
@Slf4j
@Service("jyUnloadVehicleTysService")
@UnifiedExceptionProcess
public class JyUnloadVehicleTysServiceImpl implements JyUnloadVehicleTysService {
    @Autowired
    JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;
    @Autowired
    IJyUnloadVehicleManager iJyUnloadVehicleManager;
    @Autowired
    GroupBoardManager groupBoardManager;
    @Autowired
    BoardCommonManager boardCommonManager;
    @Autowired
    @Qualifier("jyUnloadScanProducer")
    private DefaultJMQProducer unloadScanProducer;
    @Autowired
    private WaybillCacheService waybillCacheService;
    @Autowired
    IJyUnloadVehicleManager jyUnloadVehicleManager;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    private JyUnloadDao jyUnloadDao;
    @Autowired
    private JyUnloadAggsService jyUnloadAggsService;
    @Autowired
    JyUnloadVehicleBoardDao jyUnloadVehicleBoardDao;
    @Autowired
    BaseMajorManager baseMajorManager;

    private static final int SCAN_EXPIRE_TIME_HOUR = 6;
    @Autowired
    private JyUnloadAggsDao jyUnloadAggsDao;

    @Autowired
    JyBizTaskUnloadVehicleStageService jyBizTaskUnloadVehicleStageService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private JyUnloadVehicleCheckTysService jyUnloadVehicleCheckTysService;

    /**
     * 包裹重量上限值，单位kg
     */
    @Value("${packageWeightLimit:2000}")
    private String packageWeightLimit;


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
        JyBizTaskUnloadVehicleEntity entity = BeanUtils.convert(unloadVehicleTask, JyBizTaskUnloadVehicleEntity.class);
        Boolean success = jyBizTaskUnloadVehicleService.saveOrUpdateOfBusinessInfo(entity);
        if (success) {
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
        ScanPackageRespDto scanPackageRespDto = new ScanPackageRespDto();
        scanPackageRespDto.setWarnMsg(new HashMap<String, String>(5));
        invokeResult.setData(scanPackageRespDto);

        Integer operateSiteCode = scanPackageDto.getCurrentOperate().getSiteCode();
        String barCode = scanPackageDto.getScanCode();
        String bizId = scanPackageDto.getBizId();
        String boardCode = scanPackageDto.getBoardCode();
        log.info("invoking jy scanAndComBoard,params: {}", JsonHelper.toJson(scanPackageDto));
        checkScan(scanPackageDto);

        JyBizTaskUnloadVehicleEntity unloadVehicleEntity = jyBizTaskUnloadVehicleService.findByBizId(scanPackageDto.getBizId());
        if (!ObjectHelper.isNotNull(unloadVehicleEntity)) {
            return new InvokeResult<>(TASK_NO_FOUND_BY_PARAMS_CODE, TASK_NO_FOUND_BY_PARAMS_MESSAGE);
        }
        // 校验跨场地支援权限
        if (!unloadVehicleEntity.getEndSiteId().equals((long) scanPackageDto.getCurrentOperate().getSiteCode())) {
            log.warn("支援人员无需操作:bizId={},erp={}", scanPackageDto.getBizId(), scanPackageDto.getUser().getUserErp());
            invokeResult.customMessage(RESULT_PARAMETER_ERROR_CODE, "支援人员无需操作该任务，待任务完成后该任务会自动清除");
            return invokeResult;
        }
        if (ScanTypeEnum.PACKAGE.getCode().equals(scanPackageDto.getType())) {
            DeliveryPackageD packageD = waybillPackageManager.getPackageInfoByPackageCode(barCode);
            if (packageD == null) {
                return null;
            }
            String waybillCode = WaybillUtil.getWaybillCode(scanPackageDto.getScanCode());
            Waybill waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
            if (waybill == null) {
                return null;
            }
            // 判断是否是跨越的取消订单
            String kyCancelCheckStr = jyUnloadVehicleCheckTysService.kyExpressCancelCheck(operateSiteCode, waybill);
            if (StringUtils.isNotBlank(kyCancelCheckStr)) {
                invokeResult.customMessage(RESULT_PARAMETER_ERROR_CODE, kyCancelCheckStr);
                return invokeResult;
            }
            // 包裹超重校验
            BigDecimal packageWeight = jyUnloadVehicleCheckTysService.getPackageWeight(packageD, waybill);
            if (packageWeight != null && packageWeight.compareTo(new BigDecimal(packageWeightLimit)) > 0) {
                log.info("包裹超重:packageCode={},weight={},limit={}", barCode, packageWeight.toPlainString(), packageWeightLimit);
                Map<String, String> warnMsg = invokeResult.getData().getWarnMsg();
                warnMsg.put(UnloadCarWarnEnum.PACKAGE_OVER_WEIGHT_MESSAGE.getLevel(), String.format(UnloadCarWarnEnum.PACKAGE_OVER_WEIGHT_MESSAGE.getDesc(), packageWeight.toPlainString()));
            }
            // 包裹是否扫描成功
            jyUnloadVehicleCheckTysService.packageIsScanBoard(bizId, barCode, boardCode);
            if (!scanPackageDto.getIsForceCombination()) {
                UnloadScanDto unloadScanDto = createUnloadDto(scanPackageDto, unloadVehicleEntity);
                // 验货校验
                jyUnloadVehicleCheckTysService.inspectionIntercept(barCode, waybill, unloadScanDto);
                // 拦截校验
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
                    return invokeResult;
                }
                // 卸车处理并回传TC组板关系
                jyUnloadVehicleCheckTysService.dealUnloadAndBoxToBoard(scanPackageDto, scanPackageRespDto);
                return invokeResult;
            }
        } else if (ScanTypeEnum.WAYBILL.getCode().equals(scanPackageDto.getType())) {

        }
        return invokeResult;
    }

    private Integer comBoard(ScanPackageDto scanPackageDto) {
        Integer comBoardCount = 0;
        if (ObjectHelper.isNotNull(scanPackageDto.getBoardCode())) {
            comBoardCount = doComBoard(scanPackageDto.getBoardCode(), scanPackageDto);
        } else {
            String boardCode = generateBoard(scanPackageDto);
            scanPackageDto.setBoardCode(boardCode);
            if (ObjectHelper.isNotNull(boardCode)) {
                comBoardCount = doComBoard(boardCode, scanPackageDto);
            }
            JyUnloadVehicleBoardEntity entity = convertUnloadVehicleBoard(scanPackageDto);
            jyUnloadVehicleBoardDao.insertSelective(entity);
        }
        return comBoardCount;
    }

    private JyUnloadVehicleBoardEntity convertUnloadVehicleBoard(ScanPackageDto scanPackageDto) {
        Date now = new Date();
        JyUnloadVehicleBoardEntity entity = new JyUnloadVehicleBoardEntity();
        entity.setUnloadVehicleBizId(scanPackageDto.getBizId());
        entity.setBoardCode(scanPackageDto.getBoardCode());
        entity.setEndSiteId(Long.valueOf(scanPackageDto.getNextSiteCode()));
        entity.setGoodsAreaCode(scanPackageDto.getGoodsAreaCode());
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setCreateUserErp(scanPackageDto.getUser().getUserErp());
        entity.setCreateUserName(scanPackageDto.getUser().getUserName());
        return entity;
    }

    private void checkScan(ScanPackageDto scanPackageDto) {
        if (checkBarCodeScannedAlready(scanPackageDto)) {
            throw new JyBizException("单号已扫描！");
        }
        String scanCode = scanPackageDto.getScanCode();
        if (WaybillUtil.isPackageCode(scanCode)) {
            scanCode = WaybillUtil.getWaybillCode(scanCode);
        }
        Integer nextSiteCode = getWaybillNextRouter(scanCode, scanPackageDto.getCurrentOperate().getSiteCode());
        if (!ObjectHelper.isNotNull(nextSiteCode)) {
            throw new JyBizException("未获取到该运单" + scanCode + "的下游站点信息,当前站点为" + scanPackageDto.getCurrentOperate().getSiteCode());
        }
        String goodsAreaCode = getGoodsAreaCode(scanPackageDto.getCurrentOperate().getSiteCode(), nextSiteCode);
        if (!ObjectHelper.isNotNull(goodsAreaCode)) {
            throw new JyBizException("未获取到该运单" + scanCode + "对应的货区编码信息");
        }
        if (ObjectHelper.isNotNull(scanPackageDto.getGoodsAreaCode()) && !goodsAreaCode.equals(scanPackageDto.getGoodsAreaCode())) {
            throw new JyBizException("扫描包裹非本货区，请移除本区！");
        }
        scanPackageDto.setGoodsAreaCode(goodsAreaCode);
        scanPackageDto.setNextSiteCode(nextSiteCode);
    }

    private boolean checkBarCodeScannedAlready(ScanPackageDto request) {
        String barCode = request.getScanCode();
        int siteCode = request.getCurrentOperate().getSiteCode();
        boolean alreadyScanned = false;
        String mutexKey = String.format(CacheKeyConstants.JY_UNLOAD_SCAN_KEY, barCode, siteCode, request.getBizId());
        if (redisClientOfJy.set(mutexKey, String.valueOf(System.currentTimeMillis()), SCAN_EXPIRE_TIME_HOUR, TimeUnit.HOURS, false)) {
            JyUnloadEntity queryDb = new JyUnloadEntity(barCode, (long) siteCode, request.getBizId());
            if (jyUnloadDao.queryByCodeAndSite(queryDb) != null) {
                alreadyScanned = true;
            }
        } else {
            alreadyScanned = true;
        }
        return alreadyScanned;
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
        //unloadScanDto.setGroupCode(request.getGroupCode());
        unloadScanDto.setTaskId(request.getTaskId());
        unloadScanDto.setTaskType(2);
        return unloadScanDto;
    }


    private Integer doComBoard(String boardCode, ScanPackageDto scanPackageDto) {
        Integer count = 0;
        AddBoardBox addBoardBox = new AddBoardBox();
        addBoardBox.setBoardCode(boardCode);
        addBoardBox.setBoxCode(scanPackageDto.getScanCode());
        addBoardBox.setOperatorErp(scanPackageDto.getUser().getUserErp());
        addBoardBox.setOperatorName(scanPackageDto.getUser().getUserName());
        addBoardBox.setSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
        addBoardBox.setSiteName(scanPackageDto.getCurrentOperate().getSiteName());
        addBoardBox.setSiteType(BOARD_COMBINATION_SITE_TYPE);
        addBoardBox.setBizSource(BizSourceEnum.PDA.getValue());
        String waybillCode = WaybillUtil.isPackageCode(scanPackageDto.getScanCode()) ? WaybillUtil.getWaybillCode(scanPackageDto.getScanCode()) : scanPackageDto.getScanCode();
        addBoardBox.setWaybillCode(waybillCode);

        Response<Integer> rs = groupBoardManager.addBoxToBoard(addBoardBox);
        if (ObjectHelper.isNotNull(rs) && ResponseEnum.SUCCESS.getIndex() == rs.getCode()) {
            BoardCommonRequest boardCommonRequest = new BoardCommonRequest();
            boardCommonRequest.setBarCode(scanPackageDto.getScanCode());
            boardCommonRequest.setOperateSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
            boardCommonRequest.setOperateSiteName(scanPackageDto.getCurrentOperate().getSiteName());
            boardCommonRequest.setOperateUserCode(scanPackageDto.getUser().getUserCode());
            boardCommonRequest.setOperateUserName(scanPackageDto.getUser().getUserName());
            boardCommonManager.sendWaybillTrace(boardCommonRequest, WaybillStatus.WAYBILL_TRACK_BOARD_COMBINATION);
            count = rs.getData();
        }
        return count;
    }

    private String generateBoard(ScanPackageDto scanPackageDto) {
        AddBoardRequest addBoardRequest = new AddBoardRequest();
        addBoardRequest.setBoardCount(1);
        addBoardRequest.setDestinationId(1);
        addBoardRequest.setDestination("");
        addBoardRequest.setOperatorErp(scanPackageDto.getUser().getUserErp());
        addBoardRequest.setOperatorName(scanPackageDto.getUser().getUserName());
        addBoardRequest.setSiteCode(scanPackageDto.getCurrentOperate().getSiteCode());
        addBoardRequest.setSiteName(scanPackageDto.getCurrentOperate().getSiteName());
        addBoardRequest.setBizSource(1);
        Response<List<Board>> createBoardResp = groupBoardManager.createBoards(addBoardRequest);
        if (ObjectHelper.isNotNull(createBoardResp) && ResponseEnum.SUCCESS.getIndex() == createBoardResp.getCode()) {
            Board board = createBoardResp.getData().get(0);
            return board.getCode();
        }
        log.warn("jy卸车岗扫描创建新板异常{}", JsonHelper.toJson(createBoardResp));
        return null;
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.scanForPipelining",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ScanPackageRespDto> scanForPipelining(ScanPackageDto scanPackageDto) {
        return null;
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
                if(response.getData() != null) {
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
            //查询子任务bizId
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
        List<UnloadWaybillDto> unloadWaybillDtoList;
        Pager<JyVehicleTaskUnloadDetail> query = assembleQueryUnloadTaskWaybillCondition(queryUnloadDetailDto);
        Pager<JyUnloadTaskWaybillAgg> waybillAggPager = jyUnloadVehicleManager.queryUnloadTaskWaybill(query);
        if (ObjectHelper.isNotNull(waybillAggPager) && ObjectHelper.isNotNull(waybillAggPager.getData())) {
            unloadWaybillDtoList = BeanUtils.copy(waybillAggPager.getData(), UnloadWaybillDto.class);
            scanStatisticsInnerDto.setUnloadWaybillDtoList(unloadWaybillDtoList);
        }
        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, scanStatisticsInnerDto);
    }

    private Pager<JyVehicleTaskUnloadDetail> assembleQueryUnloadTaskWaybillCondition(QueryUnloadDetailDto queryUnloadDetailDto) {
        JyVehicleTaskUnloadDetail condition = new JyVehicleTaskUnloadDetail();
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
                    condition.setManualCreatedFlag(1);
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
            if (ObjectHelper.isNotNull(pagerResp.getData())) {
                List<UnloadPackageDto> unloadPackageDtoList = new ArrayList<>();
                List<JyVehicleTaskUnloadDetail> unloadDetailList = pagerResp.getData();
                for (JyVehicleTaskUnloadDetail unloadDetail : unloadDetailList) {
                    UnloadPackageDto unloadPackageDto = new UnloadPackageDto();
                    unloadPackageDto.setPackageCode(unloadDetail.getPackageCode());
                    unloadPackageDtoList.add(unloadPackageDto);
                }
                return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadPackageDtoList);
            }
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    private Pager<JyVehicleTaskUnloadDetail> assembleScanQueryCondition(QueryWaybillDto queryWaybillDto) {
        JyVehicleTaskUnloadDetail condition = new JyVehicleTaskUnloadDetail();
        condition.setBizId(queryWaybillDto.getBizId());
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
                    condition.setManualCreatedFlag(1);
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

}
