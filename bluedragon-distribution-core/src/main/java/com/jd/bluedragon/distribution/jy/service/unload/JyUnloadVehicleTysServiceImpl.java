package com.jd.bluedragon.distribution.jy.service.unload;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.UnifiedExceptionProcess;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.unload.request.UnloadCompleteRequest;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BoardCommonManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.external.enums.AppVersionEnums;
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
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadDto;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.distribution.jy.unload.JyBizTaskUnloadVehicleStageEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadVehicleBoardEntity;
import com.jd.bluedragon.distribution.loadAndUnload.exception.LoadIllegalException;
import com.jd.bluedragon.distribution.loadAndUnload.exception.UnloadPackageBoardException;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.distribution.router.domain.dto.RouteNextDto;
import com.jd.bluedragon.distribution.transfer.service.TransferService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jsf.gd.util.JsonUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
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
import org.springframework.beans.factory.annotation.Qualifier;
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
    /**
     * 卸车数据完成同步数据触发节点： 1：交班；2：卸车任务完成
     */
    public static final Integer OPERATE_NODE_HANDOVER_COMPLETE = 1;
    public static final Integer OPERATE_NODE_TASK_COMPLETE = 2;
    public static final String PACKAGE_ILLEGAL="该包裹号不存在，可能修改过包裹数，需要按运单号重新补打面单";


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
    @Autowired
    private IJyUnloadVehicleService unloadVehicleService;
    @Autowired
    private TransferService transferService;
    @Autowired
    @Qualifier("jyUnloadCarPostTaskCompleteProducer")
    private DefaultJMQProducer jyUnloadCarPostTaskCompleteProducer;
    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration ;

    @Autowired
    private WaybillService waybillService;
    @Autowired
    private RouterService routerService;

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.listUnloadVehicleTask",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<UnloadVehicleTaskRespDto> listUnloadVehicleTask(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        if(log.isInfoEnabled()) {
            log.info("JyUnloadVehicleTysServiceImpl.listUnloadVehicleTask--req={}", JsonUtils.toJSONString(unloadVehicleTaskReqDto));
        }
        if (ObjectHelper.isNotNull(unloadVehicleTaskReqDto.getPackageCode())) {
            return queryUnloadVehicleTaskByVehicleNumOrPackage(unloadVehicleTaskReqDto);
        }
        //查询状态统计数据(按状态分组聚合)
        JyBizTaskUnloadStatusEnum[] statusEnums = {UN_LOADING, UN_LOAD_DONE};
        JyBizTaskUnloadVehicleEntity statusStatisticsQueryParams = assembleQueryStatusStatisticsCondition(unloadVehicleTaskReqDto);
        List<JyBizTaskUnloadCountDto> unloadCountDtos = jyBizTaskUnloadVehicleService.findStatusCountByCondition4Status(statusStatisticsQueryParams, null, statusEnums);
        if(CollectionUtils.isEmpty(unloadCountDtos)) {
            unloadCountDtos = new ArrayList<>();
        }
        //待扫状态单独计算，待扫要过滤到达时间，其他状态不过滤时间
        JyBizTaskUnloadStatusEnum[] waitStatusEnums = {WAIT_UN_LOAD};
        JyBizTaskUnloadVehicleEntity waitStatusStatisticsQueryParams = assembleQueryStatusStatisticsCondition(unloadVehicleTaskReqDto);
        waitStatusStatisticsQueryParams.setActualArriveStartTime(waitUnloadQueryTimeRange());
        List<JyBizTaskUnloadCountDto> waitStatusUnloadCountDtos = jyBizTaskUnloadVehicleService.findStatusCountByCondition4Status(waitStatusStatisticsQueryParams, null, waitStatusEnums);
        unloadCountDtos.addAll(waitStatusUnloadCountDtos);

        if (!CollectionUtils.isNotEmpty(unloadCountDtos)) {
            return new InvokeResult<>(RESULT_SUCCESS_CODE, TASK_NO_FOUND_BY_STATUS_MESSAGE);
        }
        UnloadVehicleTaskRespDto respDto = new UnloadVehicleTaskRespDto();
        initCountToResp(respDto, unloadCountDtos);
        //查询卸车任务列表
        PageHelper.startPage(unloadVehicleTaskReqDto.getPageNo(), unloadVehicleTaskReqDto.getPageSize());
        JyBizTaskUnloadVehicleEntity unloadTaskQueryParams = assembleQueryTaskCondition(unloadVehicleTaskReqDto);
        List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = jyBizTaskUnloadVehicleService.listUnloadVehicleTask(unloadTaskQueryParams);
        respDto.setUnloadVehicleTaskDtoList(unloadVehicleTaskDtoList);

        statusStatisticsQueryParams.setVehicleStatus(unloadVehicleTaskReqDto.getVehicleStatus());
        if(WAIT_UN_LOAD.getCode().equals(unloadVehicleTaskReqDto.getVehicleStatus())) {
            statusStatisticsQueryParams.setActualArriveStartTime(waitUnloadQueryTimeRange());
        }
        List<LineTypeStatisDto> lineTypeStatisDtoList = calculationLineTypeStatis(statusStatisticsQueryParams);
        respDto.setLineTypeStatisDtoList(lineTypeStatisDtoList);

        return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, respDto);
    }

    /**
     * 待卸车装车过滤时间范围
     * */
    private Date waitUnloadQueryTimeRange() {
        return DateHelper.getZeroFromDay(new Date(), uccPropertyConfiguration.getJyUnloadCarListQueryDayFilter());
    }

//    /**
//     * 根据包裹号从ES获得封车编码
//     * @param request
//     * @return
//     */
//    private List<String> getSealCarCodeFromEs(UnloadVehicleTaskReqDto request) {
//        JyVehicleTaskUnloadDetail query = new JyVehicleTaskUnloadDetail();
//        query.setPackageCode(request.getPackageCode());
//        query.setEndSiteId(request.getCurrentOperate().getSiteCode());
//        List<JyVehicleTaskUnloadDetail> unloadDetails = iJyUnloadVehicleManager.findUnloadDetail(query);
//
//        Set<String> sealCarCodes = new HashSet<>();
//        if (CollectionUtils.isNotEmpty(unloadDetails)) {
//            for (JyVehicleTaskUnloadDetail unloadDetail : unloadDetails) {
//                sealCarCodes.add(unloadDetail.getSealCarCode());
//            }
//            return new ArrayList<>(sealCarCodes);
//        }
//
//        return null;
//    }

    private JyBizTaskUnloadVehicleEntity assembleQueryTaskCondition(UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        JyBizTaskUnloadVehicleEntity condition = new JyBizTaskUnloadVehicleEntity();
        condition.setVehicleStatus(unloadVehicleTaskReqDto.getVehicleStatus());
        condition.setEndSiteId(Long.valueOf(unloadVehicleTaskReqDto.getCurrentOperate().getSiteCode()));
        condition.setLineType(unloadVehicleTaskReqDto.getLineType());
        condition.setFuzzyVehicleNumber(unloadVehicleTaskReqDto.getVehicleNumber());
        if(WAIT_UN_LOAD.getCode().equals(unloadVehicleTaskReqDto.getVehicleStatus())) {
            condition.setActualArriveStartTime(waitUnloadQueryTimeRange());
        }
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
        //请求中的线路类型仅是查列表的过滤条件，线路类型为筛选条件，要展示全部
        JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
        org.springframework.beans.BeanUtils.copyProperties(condition,entity);
        entity.setLineType(null);
        List<JyBizTaskUnloadCountDto> lineTypeAgg = jyBizTaskUnloadVehicleService.findStatusCountByCondition4StatusAndLine(entity, null, statusEnum);
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
        String methodDesc = "JyUnloadVehicleTysServiceImpl.queryUnloadVehicleTaskByVehicleNumOrPackage--扫描包裹号查询任务信息--";
        try{
            log.info("{}请求信息={}", methodDesc, JsonUtils.toJSONString(queryUnloadTaskDto));
            UnloadVehicleTaskRespDto unloadVehicleTaskRespDto = new UnloadVehicleTaskRespDto();
            if (ObjectHelper.isNotNull(queryUnloadTaskDto.getPackageCode()) && WaybillUtil.isPackageCode(queryUnloadTaskDto.getPackageCode())) {
                JyVehicleTaskUnloadDetail detail = new JyVehicleTaskUnloadDetail();
                detail.setPackageCode(queryUnloadTaskDto.getPackageCode());
                detail.setEndSiteId(queryUnloadTaskDto.getCurrentOperate().getSiteCode());
                List<JyVehicleTaskUnloadDetail> unloadDetailList = iJyUnloadVehicleManager.findUnloadDetail(detail);
                if (CollectionUtils.isNotEmpty(unloadDetailList)) {
//                    List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = convertUnloadVehicleTaskDto(unloadDetailList);
                    List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = new ArrayList<>();
                    JyBizTaskUnloadVehicleEntity entity = jyBizTaskUnloadVehicleService.findByBizId(unloadDetailList.get(0).getBizId());
                    if(entity == null || (!WAIT_UN_LOAD.getCode().equals(entity.getVehicleStatus())
                            && !UN_LOADING.getCode().equals(entity.getVehicleStatus())
                            && !UN_LOAD_DONE.getCode().equals(entity.getVehicleStatus()))) {
                        return new InvokeResult(RESULT_SUCCESS_CODE, TASK_NO_FOUND_BY_PARAMS_MESSAGE);
                    }
                    unloadVehicleTaskDtoList.add(jyBizTaskUnloadVehicleService.entityConvertDto(entity));
                    calculationCount(unloadVehicleTaskRespDto, unloadVehicleTaskDtoList, queryUnloadTaskDto);
                    return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadVehicleTaskRespDto);
                }else {
                    log.info("{}未查到包裹号对应任务信息；请求信息={}", methodDesc, JsonUtils.toJSONString(queryUnloadTaskDto));
                    return new InvokeResult(RESULT_SUCCESS_CODE, TASK_NO_FOUND_BY_PARAMS_MESSAGE);
                }
            } else if (ObjectHelper.isNotNull(queryUnloadTaskDto.getVehicleNumber())) {
                JyBizTaskUnloadVehicleEntity entity = new JyBizTaskUnloadVehicleEntity();
                entity.setFuzzyVehicleNumber(queryUnloadTaskDto.getVehicleNumber());
                entity.setEndSiteId(Long.valueOf(queryUnloadTaskDto.getCurrentOperate().getSiteCode()));
                List<Integer> statusList = Arrays.asList(WAIT_UN_LOAD.getCode(),UN_LOADING.getCode(), UN_LOAD_DONE.getCode());
                entity.setStatusCodeList(statusList);
                List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = jyBizTaskUnloadVehicleService.queryByFuzzyVehicleNumberAndStatus(entity);
                calculationCount(unloadVehicleTaskRespDto, unloadVehicleTaskDtoList, queryUnloadTaskDto);
                return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, unloadVehicleTaskRespDto);
            }else {
                log.info("{}参数缺失：该接口包裹号或车牌号不能全部为空；请求信息={}", methodDesc, JsonUtils.toJSONString(queryUnloadTaskDto));
                return new InvokeResult(RESULT_PARAMETER_ERROR_CODE, "该接口包裹号或车牌号不能全部为空");
            }
        }catch (Exception e) {
            log.error("{}服务异常；请求信息={}， 错误={}", methodDesc, JsonUtils.toJSONString(queryUnloadTaskDto), e.getMessage(), e);
            String errMsg = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "扫描包裹号查询任务信息服务异常";
            return new InvokeResult(SERVER_ERROR_CODE, errMsg);
        }
    }

    private void calculationCount(UnloadVehicleTaskRespDto unloadVehicleTaskRespDto, List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList, UnloadVehicleTaskReqDto unloadVehicleTaskReqDto) {
        int waitUnloadCount = 0;
        //卸车中数量
        int unloadingCount = 0;
        //已完成卸车数量
        int unloadedCount = 0;
        List<UnloadVehicleTaskDto> queryStatusList = new ArrayList<>();

        for (UnloadVehicleTaskDto unloadVehicleTaskDto : unloadVehicleTaskDtoList) {
            if(unloadVehicleTaskReqDto.getVehicleStatus() != null && unloadVehicleTaskDto.getVehicleStatus() != null
                    && unloadVehicleTaskReqDto.getVehicleStatus().equals(unloadVehicleTaskDto.getVehicleStatus())) {
                queryStatusList.add(unloadVehicleTaskDto);
            }

            switch (unloadVehicleTaskDto.getVehicleStatus()) {
                case 3:
                    waitUnloadCount++;
                    continue;
                case 4:
                    unloadingCount++;
                    continue;
                case 5:
                    unloadedCount++;
                    continue;
                default:
                    log.info("");
            }
        }
        unloadVehicleTaskRespDto.setUnloadVehicleTaskDtoList(queryStatusList);

        unloadVehicleTaskRespDto.setWaitUnloadCount(waitUnloadCount);
        unloadVehicleTaskRespDto.setUnloadingCount(unloadingCount);
        unloadVehicleTaskRespDto.setUnloadedCount(unloadedCount);
    }

//    private List<UnloadVehicleTaskDto> convertUnloadVehicleTaskDto(List<JyVehicleTaskUnloadDetail> unloadDetailList) {
//        List<UnloadVehicleTaskDto> unloadVehicleTaskDtoList = new ArrayList<>();
//        JyVehicleTaskUnloadDetail detail = unloadDetailList.get(0);
//        JyBizTaskUnloadVehicleEntity entity = jyBizTaskUnloadVehicleService.findByBizId(detail.getBizId());
//        UnloadVehicleTaskDto unloadVehicleTaskDto = jyBizTaskUnloadVehicleService.entityConvertDto(entity);
//        unloadVehicleTaskDtoList.add(unloadVehicleTaskDto);
//        return unloadVehicleTaskDtoList;
//    }

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
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
        }
        return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.completeUnloadTask",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Boolean> completeUnloadTask(UnloadCompleteDto request) {
        String methodDesc = "JyUnloadVehicleTysServiceImpl.completeUnloadTask--完成任务--";
        //子任务完成
        JyBizTaskUnloadVehicleStageEntity doingChildTask = jyBizTaskUnloadVehicleStageService.selectUnloadDoingStageTask(request.getBizId());
        if (doingChildTask != null) {
            JyBizTaskUnloadVehicleStageEntity stageEntity = new JyBizTaskUnloadVehicleStageEntity();
            stageEntity.setUnloadVehicleBizId(request.getBizId());
            stageEntity.setStatus(JyBizTaskStageStatusEnum.COMPLETE.getCode());
            stageEntity.setEndTime(new Date());
            stageEntity.setUpdateTime(new Date());
            stageEntity.setUpdateUserErp(request.getUser().getUserErp());
            stageEntity.setUpdateUserName(request.getUser().getUserName());
            stageEntity.setBizId(doingChildTask.getBizId());
            jyBizTaskUnloadVehicleStageService.updateStatusByUnloadVehicleBizId(stageEntity);
        }
        //主任务完成
        UnloadCompleteRequest unloadCompleteRequest = new UnloadCompleteRequest();
        unloadCompleteRequest.setTaskId(request.getTaskId());
        unloadCompleteRequest.setAbnormalFlag(request.getAbnormalFlag());
        unloadCompleteRequest.setBizId(request.getBizId());
        unloadCompleteRequest.setSealCarCode(request.getSealCarCode());
        unloadCompleteRequest.setUser(copyUser(request.getUser()));
        unloadCompleteRequest.setCurrentOperate(copyCurrentOperate(request.getCurrentOperate()));
        unloadCompleteRequest.setToScanCount(request.getToScanCount());
        unloadCompleteRequest.setMoreScanLocalCount(request.getMoreScanLocalCount());
        unloadCompleteRequest.setMoreScanOutCount(request.getMoreScanOutCount());
        InvokeResult<Boolean> result = unloadVehicleService.submitUnloadCompletion(unloadCompleteRequest);
        if (RESULT_SUCCESS_CODE != result.getCode() || !Boolean.TRUE.equals(result.getData())) {
            log.warn("{}完成失败,req={}", methodDesc, JsonHelper.toJson(request));
            return result;
        }
        sendTaskCompleteMqHandler(doingChildTask, request);
        return result;

    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.previewBeforeUnloadComplete",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<UnloadPreviewRespDto> previewBeforeUnloadComplete(UnloadPreviewDto request) {
        InvokeResult<UnloadPreviewRespDto> result = new InvokeResult<>();
        try {
            UnloadPreviewRespDto previewData = new UnloadPreviewRespDto();
            // 默认正常
            previewData.setAbnormalFlag(Constants.NUMBER_ZERO.byteValue());
            // 查询异常统计数据
            QueryUnloadDetailDto queryUnloadDetailDto = new QueryUnloadDetailDto();
            queryUnloadDetailDto.setExpFlag(Boolean.TRUE);
            queryUnloadDetailDto.setExpType(request.getExceptionType());
            queryUnloadDetailDto.setBizId(request.getBizId());
            queryUnloadDetailDto.setCurrentOperate(request.getCurrentOperate());
            queryUnloadDetailDto.setUser(request.getUser());
            queryUnloadDetailDto.setVehicleNumber(request.getVehicleNumber());
            queryUnloadDetailDto.setPageNo(request.getPageNumber());
            queryUnloadDetailDto.setPageSize(request.getPageSize());
            InvokeResult<ScanStatisticsInnerDto> invokeResult = queryUnloadDetailByDiffDimension(queryUnloadDetailDto);
            if (RESULT_SUCCESS_CODE != invokeResult.getCode()) {
                log.warn("previewBeforeUnloadComplete|卸车完成前预览获取异常统计数据返回失败:request={},statisticsData={}", JsonHelper.toJson(request), JsonHelper.toJson(invokeResult));
                result.customMessage(invokeResult.getCode(), invokeResult.getMessage());
                return result;
            }
            ScanStatisticsInnerDto statisticsData = invokeResult.getData();
            if (statisticsData == null) {
                log.warn("previewBeforeUnloadComplete|卸车完成前预览获取异常统计数据返回空:request={},statisticsData={}", JsonHelper.toJson(request), JsonHelper.toJson(invokeResult));
                return result;
            }
            previewData.setExceptScanDtoList(statisticsData.getExcepScanDtoList());
            previewData.setUnloadWaybillDtoList(statisticsData.getUnloadWaybillDtoList());
            // 查询待扫、本场地/非本场地多扫
            JyUnloadAggsEntity aggsEntity = jyUnloadAggsDao.queryToScanAndMoreScanStatistics(request.getBizId());
            if (aggsEntity != null) {
                previewData.setToScanCount(aggsEntity.getShouldScanCount() - aggsEntity.getActualScanCount());
                previewData.setMoreScanLocalCount(aggsEntity.getMoreScanLocalCount());
                previewData.setMoreScanOutCount(aggsEntity.getMoreScanOutCount());
            }
            result.setData(previewData);
        } catch (Exception ex) {
            log.error("previewBeforeUnloadComplete|卸车完成预览数据发生异常. {}", JsonHelper.toJson(request), ex);
            result.error("校验卸车完成发生异常，请咚咚联系分拣小秘！");
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryStatisticsByDiffDimension",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ScanStatisticsDto> queryStatisticsByDiffDimension(DimensionQueryDto dto) {
        if(log.isInfoEnabled()) {
            log.info("JyUnloadVehicleTysServiceImpl.queryStatisticsByDiffDimension--req={}", JsonUtils.toJSONString(dto));
        }
        if (UnloadStatisticsQueryTypeEnum.PACKAGE.getCode().equals(dto.getType()) || UnloadStatisticsQueryTypeEnum.WAYBILL.getCode().equals(dto.getType())) {
            ScanStatisticsDto scanStatisticsDto = jyBizTaskUnloadVehicleService.queryStatisticsByDiffDimension(dto);
            return new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, scanStatisticsDto);
        }
        return new InvokeResult(NOT_SUPPORT_TYPE_QUERY_CODE, NOT_SUPPORT_TYPE_QUERY_MESSAGE);
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.queryStatistics",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<StatisticsDto> queryStatistics(DimensionQueryDto dto) {
        try{
            return jyBizTaskUnloadVehicleService.queryStatistics(dto);
        }catch (Exception e) {
            log.info("JyUnloadVehicleTysServiceImpl.queryStatistics--异常--errMsg={},req={}", e.getMessage(), JsonUtils.toJSONString(dto), e);
            return new InvokeResult<>(RESULT_PARAMETER_ERROR_CODE, e.getMessage());
        }
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
        log.info("invoking jy scanAndComBoard,params: {}", JsonHelper.toJson(scanPackageDto));
        try {
            // 非空任务才需要互斥
            if (StringUtils.isNotBlank(scanPackageDto.getSealCarCode())) {
                // 新老版本互斥
                InvokeResult<Boolean> permissionResult = transferService.saveOperatePdaVersion(scanPackageDto.getSealCarCode(), AppVersionEnums.PDA_GUIDED.getVersion());
                if (permissionResult.getCode() != RESULT_SUCCESS_CODE) {
                    log.warn("作业app版本校验失败，该任务已经在老PDA版本领取:request={},permissionResult={}", JSON.toJSONString(scanPackageDto), JSON.toJSONString(permissionResult));
                    invokeResult.customMessage(RESULT_INTERCEPT_CODE, permissionResult.getMessage());
                    return invokeResult;
                }
                if (Boolean.FALSE.equals(permissionResult.getData())) {
                    log.warn("作业app版本校验失败，该任务已经在老PDA版本领取:request={},permissionResult={}", JSON.toJSONString(scanPackageDto), JSON.toJSONString(permissionResult));
                    invokeResult.customMessage(RESULT_INTERCEPT_CODE, "该任务已经在老版PDA领取，请前往老版PDA继续操作");
                    return invokeResult;
                }
            }
            return startScanningProcess(scanPackageDto, invokeResult);
        } catch (JyBizException | LoadIllegalException e) {
            invokeResult.customMessage(RESULT_INTERCEPT_CODE, e.getMessage());
            log.error("人工卸车扫描接口服务异常：req={}，errMsg={}", JsonUtils.toJSONString(scanPackageDto), e.getMessage());
            return invokeResult;
        } catch (Exception e) {
            if (e instanceof UnloadPackageBoardException) {
                invokeResult.customMessage(JdCResponse.CODE_CONFIRM, e.getMessage());
                return invokeResult;
            }
            log.error("人工卸车扫描接口发生异常：req={}，errMsg={}，e=", JsonUtils.toJSONString(scanPackageDto), e.getMessage(), e);
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
        scanResult.setConfirmMsg(new HashMap<String, String>(3));
        return scanResult;
    }

    private InvokeResult<ScanPackageRespDto> packageScan(ScanPackageDto scanPackageDto, JyBizTaskUnloadVehicleEntity unloadVehicleEntity,
                                                         InvokeResult<ScanPackageRespDto> invokeResult) {
        String barCode = scanPackageDto.getScanCode();
        String bizId = scanPackageDto.getBizId();
        Integer operateSiteCode = scanPackageDto.getCurrentOperate().getSiteCode();
        ScanPackageRespDto scanPackageRespDto = invokeResult.getData();
        DeliveryPackageD packageD = waybillPackageManager.getPackageInfoByPackageCode(barCode);
        if (packageD == null) {
                invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, PACKAGE_ILLEGAL);
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
        // 包裹是否扫描成功以及是否组板成功
        jyUnloadVehicleCheckTysService.packageIsScan(scanPackageDto);

        //易冻品校验
        checkEasyFreezeResult(waybillCode,scanPackageDto.getCurrentOperate().getSiteCode(),scanPackageRespDto);

        // 是否强制组板
        if (!scanPackageDto.getIsForceCombination()) {
            UnloadScanDto unloadScanDto = createUnloadDto(scanPackageDto, unloadVehicleEntity);
            // 加盟商余额校验 + 推验货任务
            jyUnloadVehicleCheckTysService.inspectionIntercept(barCode, waybill, unloadScanDto);
            // 组装返回数据
            jyUnloadVehicleCheckTysService.assembleReturnData(scanPackageDto, scanPackageRespDto, unloadVehicleEntity, unloadScanDto);
            // 无任务设置上游站点
            jyUnloadVehicleCheckTysService.setStartSiteForJyUnloadVehicle(scanPackageDto, scanPackageRespDto, unloadVehicleEntity);
            // 货区校验
            String checkResult = jyUnloadVehicleCheckTysService.checkGoodsArea(scanPackageDto, scanPackageRespDto);
            if (StringUtils.isNotBlank(checkResult)) {
                invokeResult.customMessage(InvokeResult.CODE_SPECIAL_INTERCEPT, checkResult);
                return invokeResult;
            }
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
                // 板上包裹数限制
                jyUnloadVehicleCheckTysService.packageCountCheck(scanPackageDto);
                // 是否发货校验
                jyUnloadVehicleCheckTysService.isSendCheck(scanPackageDto);
                // 运单超重校验
                jyUnloadVehicleCheckTysService.checkWaybillOverWeight(waybill);
                // ver组板拦截校验
                String boardCheckStr = jyUnloadVehicleCheckTysService.boardCombinationCheck(scanPackageDto);
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
                invokeResult.customMessage(InvokeResult.RESULT_INTERCEPT_CODE, PACKAGE_ILLEGAL);
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
        //易冻损校验
        checkEasyFreezeResult(waybillCode,scanPackageDto.getCurrentOperate().getSiteCode(),scanPackageRespDto);
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
        // 货区校验
        String checkResult = jyUnloadVehicleCheckTysService.checkGoodsArea(scanPackageDto, scanPackageRespDto);
        if (StringUtils.isNotBlank(checkResult)) {
            invokeResult.customMessage(InvokeResult.CODE_SPECIAL_INTERCEPT, checkResult);
            return invokeResult;
        }
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


    private void checkEasyFreezeResult(String barCode, Integer siteCode,ScanPackageRespDto scanPackageRespDto){
        //易冻品校验
        InvokeResult<Boolean> easyFreezeCheckResult
                = waybillService.checkEasyFreeze(barCode, new Date(), siteCode);
        log.info("checkEasyFreezeResult-易冻品校验结果-{}",JSON.toJSONString(easyFreezeCheckResult));
        if(easyFreezeCheckResult != null && easyFreezeCheckResult.getData()){
            Map<String, String> confirmMsg = scanPackageRespDto.getConfirmMsg();
            confirmMsg.put(easyFreezeCheckResult.getCode()+"",easyFreezeCheckResult.getMessage());
            scanPackageRespDto.setConfirmMsg(confirmMsg);
        }
    }



    private void checkScan(ScanPackageDto scanPackageDto, JyBizTaskUnloadVehicleEntity unloadVehicleEntity) {
        if (BusinessUtil.isBoxcode(scanPackageDto.getScanCode())) {
            throw new JyBizException("暂不支持箱号！");
        }
        String scanCode = scanPackageDto.getScanCode();
        if (WaybillUtil.isPackageCode(scanCode)) {
            scanCode = WaybillUtil.getWaybillCode(scanCode);
        }
        RouteNextDto routeNextDto = routerService.matchNextNodeAndLastNodeByRouter(scanPackageDto.getCurrentOperate().getSiteCode(), scanCode, null);
        scanPackageDto.setNextSiteCode(routeNextDto.getFirstNextSiteId());
        if(routeNextDto.getFirstNextSiteId() != null){
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(routeNextDto.getFirstNextSiteId());
            if (baseSite != null) {
                scanPackageDto.setNextSiteName(baseSite.getSiteName());
            }
        }
        if (Constants.START_SITE_INITIAL_VALUE.equals(unloadVehicleEntity.getStartSiteId())) {
            scanPackageDto.setPrevSiteCode(routeNextDto.getFirstLastSiteId());
        } else {
            scanPackageDto.setPrevSiteCode(unloadVehicleEntity.getStartSiteId().intValue());
            scanPackageDto.setPrevSiteName(unloadVehicleEntity.getStartSiteName());
        }
        if (JyBizTaskUnloadStatusEnum.UN_LOAD_DONE.getCode().equals(unloadVehicleEntity.getVehicleStatus())) {
            scanPackageDto.setTaskFinish(Boolean.TRUE);
        }
        if (StringUtils.isBlank(scanPackageDto.getBoardCode())) {
            scanPackageDto.setCreateNewBoard(Boolean.TRUE);
        }
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
        // 设置子阶段bizId
        InvokeResult<Boolean> invokeResult = jyUnloadVehicleCheckTysService.setStageBizId(request, unloadScanDto);
        if(!invokeResult.codeSuccess()) {
            throw new JyBizException(invokeResult.getMessage());
        }
        return unloadScanDto;
    }


    private InvokeResult<ScanPackageRespDto> startScanningProcess(ScanPackageDto scanPackageDto, InvokeResult<ScanPackageRespDto> invokeResult) {
        // 校验任务
        JyBizTaskUnloadVehicleEntity unloadVehicleEntity = jyBizTaskUnloadVehicleService.findByBizId(scanPackageDto.getBizId());
        if (unloadVehicleEntity == null) {
            invokeResult.customMessage(RESULT_INTERCEPT_CODE, TASK_NO_FOUND_BY_PARAMS_MESSAGE);
            return invokeResult;
        }
        //补扫校验
        InvokeResult<Void> invokeResultRes = scanSupplementCheck(unloadVehicleEntity);
        if(!invokeResultRes.codeSuccess()) {
            invokeResult.customMessage(RESULT_INTERCEPT_CODE, invokeResultRes.getMessage());
            return invokeResult;
        }
        // 通用校验
        checkScan(scanPackageDto, unloadVehicleEntity);
//        // 校验跨场地支援权限
        if (!unloadVehicleEntity.getEndSiteId().equals((long) scanPackageDto.getCurrentOperate().getSiteCode())) {
            log.warn("任务流向与request流向不一致:bizId={},erp={}", scanPackageDto.getBizId(), scanPackageDto.getUser().getUserErp());
            invokeResult.customMessage(RESULT_INTERCEPT_CODE, "任务流向与request流向不一致");
            return invokeResult;
        }
        ScanPackageRespDto scanPackageRespDto = convertToScanResult(scanPackageDto);
        invokeResult.setData(scanPackageRespDto);
        // 按件扫描
        if (ScanTypeEnum.SCAN_ONE.getCode().equals(scanPackageDto.getType())) {
            // 包裹号
            if (WaybillUtil.isPackageCode(scanPackageDto.getScanCode())) {
                return packageScan(scanPackageDto, unloadVehicleEntity, invokeResult);
                // 箱号
            } else if (BusinessUtil.isBoxcode(scanPackageDto.getScanCode())) {
                invokeResult.customMessage(RESULT_INTERCEPT_CODE, "暂不支持箱号");
                return invokeResult;
            }
            // 按单扫描
        } else if (ScanTypeEnum.SCAN_WAYBILL.getCode().equals(scanPackageDto.getType())) {
            return waybillScan(scanPackageDto, unloadVehicleEntity, invokeResult);
        }
        return invokeResult;
    }

    /**
     * 转运卸车任务补扫校验
     * @param unloadVehicleEntity
     * @return
     */
    private InvokeResult<Void> scanSupplementCheck(JyBizTaskUnloadVehicleEntity unloadVehicleEntity) {
        InvokeResult<Void> res = new InvokeResult<>();
        res.success();
        if(JyBizTaskUnloadStatusEnum.UN_LOAD_DONE.getCode().equals(unloadVehicleEntity.getVehicleStatus())) {
            //自建任务禁止补扫
            if(unloadVehicleEntity != null && unloadVehicleEntity.getManualCreatedFlag().equals(1)) {
                res.customMessage(RESULT_INTERCEPT_CODE, "自建任务结束后禁止补扫，请重新创建自建任务进行扫描");
                return res;
            }
            //完成时间过久禁止补扫
            Long completeTime = unloadVehicleEntity.getUnloadFinishTime().getTime();
            Long limitTime = uccPropertyConfiguration.getTysUnloadTaskSupplementScanLimitHours() * 3600l * 1000l;
            if(System.currentTimeMillis() - completeTime - limitTime > 0) {
                String msg = String.format("该任务已结束%s小时，禁止补扫，可自建任务扫描", uccPropertyConfiguration.getTysUnloadTaskHandoverMaxSize());
                res.customMessage(RESULT_INTERCEPT_CODE, msg);
                return res;
            }

        }
        return res;
    }


    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.scanForPipelining",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ScanPackageRespDto> scanForPipelining(ScanPackageDto scanPackageDto) {
        InvokeResult<ScanPackageRespDto> invokeResult = new InvokeResult<>();
        invokeResult.success();
        log.info("invoking jy scanForPipelining,params: {}", JsonHelper.toJson(scanPackageDto));
        try {
            // 非空任务才需要互斥
            if (StringUtils.isNotBlank(scanPackageDto.getSealCarCode())) {
                // 新老版本互斥
                InvokeResult<Boolean> permissionResult = transferService.saveOperatePdaVersion(scanPackageDto.getSealCarCode(), AppVersionEnums.PDA_GUIDED.getVersion());
                if (permissionResult.getCode() != RESULT_SUCCESS_CODE) {
                    log.warn("流水线扫描新版本获取锁失败或卸车任务已在老版本操作:request={},permissionResult={}", JSON.toJSONString(scanPackageDto), JSON.toJSONString(permissionResult));
                    invokeResult.customMessage(RESULT_INTERCEPT_CODE, permissionResult.getMessage());
                    return invokeResult;
                }
                if (Boolean.FALSE.equals(permissionResult.getData())) {
                    log.warn("流水线扫描新版本获取锁失败或卸车任务已在老版本操作:request={},permissionResult={}", JSON.toJSONString(scanPackageDto), JSON.toJSONString(permissionResult));
                    invokeResult.customMessage(RESULT_INTERCEPT_CODE, "该任务已经在老版PDA领取，请前往老版PDA继续操作");
                    return invokeResult;
                }
            }
            return startScanningProcess(scanPackageDto, invokeResult);
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
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.createUnloadTask",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<UnloadNoTaskRespDto> createUnloadTask(UnloadNoTaskDto request) {
        InvokeResult<UnloadNoTaskRespDto> result = new InvokeResult<>();
        result.success();
        try {
            JyBizTaskUnloadDto dto = new JyBizTaskUnloadDto();
            dto.setManualCreatedFlag(Constants.CONSTANT_NUMBER_ONE);
            dto.setVehicleNumber(request.getVehicleNumber());
            dto.setOperateSiteId(request.getOperateSiteId());
            dto.setOperateSiteName(request.getOperateSiteName());
            dto.setOperateUserErp(request.getUser().getUserErp());
            dto.setOperateUserName(request.getUser().getUserName());
            JyBizTaskUnloadDto noTaskUnloadDto = unloadVehicleService.createUnloadTask(dto);
            if (noTaskUnloadDto == null) {
                log.warn("createUnloadTask|创建无任务卸车返回空,req={}", JsonHelper.toJson(request));
                result.customMessage(RESULT_INTERCEPT_CODE, "创建无任务卸车失败");
                return result;
            }
            UnloadNoTaskRespDto unloadNoTaskResponse = new UnloadNoTaskRespDto();
            unloadNoTaskResponse.setOperateSiteId(noTaskUnloadDto.getOperateSiteId());
            unloadNoTaskResponse.setOperateSiteName(noTaskUnloadDto.getOperateSiteName());
            unloadNoTaskResponse.setBizId(noTaskUnloadDto.getBizId());
            unloadNoTaskResponse.setTaskId(noTaskUnloadDto.getTaskId());
            unloadNoTaskResponse.setSealCarCode(noTaskUnloadDto.getSealCarCode());
            unloadNoTaskResponse.setVehicleNumber(noTaskUnloadDto.getVehicleNumber());
            result.setData(unloadNoTaskResponse);
        } catch (Exception e) {
            log.error("createUnloadTask|创建无任务卸车服务异常,req={},errMsg=", JsonHelper.toJson(request), e);
            result.error("创建无任务卸车服务异常");
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "JyUnloadVehicleTysServiceImpl.handoverTask",jAppName= Constants.UMP_APP_NAME_DMSWEB,mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<Void> handoverTask(UnloadVehicleTaskDto unloadVehicleTask) {
        InvokeResult<Void> result = new InvokeResult<>();
        result.success();
        try {
            if(log.isInfoEnabled()) {
                log.info("JyUnloadVehicleTysServiceImpl.handoverTask--交接班请求={}", JsonHelper.toJson(unloadVehicleTask));
            }
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
            if(taskUnloadVehicle.getManualCreatedFlag() != null && taskUnloadVehicle.getManualCreatedFlag().equals(1)) {
                result.error("自建任务不支持交班,扫描结束请直接完成任务！");
                return result;
            }
            //最大交班次数限制
            JyBizTaskUnloadVehicleStageEntity entityQuery = new JyBizTaskUnloadVehicleStageEntity();
            entityQuery.setUnloadVehicleBizId(unloadVehicleTask.getBizId());
            entityQuery.setType(JyBizTaskStageTypeEnum.HANDOVER.getCode());
            int normalTaskNum = jyBizTaskUnloadVehicleStageService.getTaskCount(entityQuery);
            if(normalTaskNum >= uccPropertyConfiguration.getTysUnloadTaskHandoverMaxSize() + 1) {
                if(log.isInfoEnabled()) {
                    log.info("JyUnloadVehicleTysServiceImpl.handoverTask--限制交接班此时，当前查到子任务数为{}，限制最大交接次数为{}，请求={}",
                            normalTaskNum, uccPropertyConfiguration.getTysUnloadTaskHandoverMaxSize(), JsonHelper.toJson(unloadVehicleTask));
                }
                result.error("当前任务已经达到最大交班次数，扫描结束请直接完成任务");
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

            sendTaskHandoverMqHandler(unloadVehicleTask, entity);
        } catch (Exception e) {
            log.error("handoverTask|交班服务异常,req={},errMsg=", JsonHelper.toJson(unloadVehicleTask), e);
            result.error("交班服务异常");
        }
        return result;
    }

    private void sendTaskHandoverMqHandler(UnloadVehicleTaskDto request, JyBizTaskUnloadVehicleStageEntity childTask) {
        JyUnloadCarTaskCompleteDto mqDto = new JyUnloadCarTaskCompleteDto();
        mqDto.setMasterTaskBizId(request.getBizId());
        mqDto.setMasterTaskTaskId(request.getTaskId());
        mqDto.setSealCarCode(request.getSealCarCode());
        mqDto.setChildTaskBizId(childTask.getBizId());
        mqDto.setOperateNode(OPERATE_NODE_HANDOVER_COMPLETE);
        mqDto.setOperatorTime(System.currentTimeMillis());
        mqDto.setUser(request.getUser());
        mqDto.setCurrentOperate(request.getCurrentOperate());
        String businessId = mqDto.getMasterTaskBizId() + "-" + mqDto.getChildTaskBizId() + "-" + mqDto.getOperateNode();
        sendUnloadCarPostTaskCompleteMq(businessId, JsonHelper.toJson(mqDto));
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
        if(log.isInfoEnabled()) {
            log.info("JyUnloadVehicleTysServiceImpl.queryUnloadDetailByDiffDimension-下载明细查询-param={}",JsonUtils.toJSONString(queryUnloadDetailDto));
        }
        //统计数据查询
        ScanStatisticsInnerDto scanStatisticsInnerDto = new ScanStatisticsInnerDto();
        JyUnloadAggsEntity condition = new JyUnloadAggsEntity();
        condition.setBizId(queryUnloadDetailDto.getBizId());
        condition.setBoardCode(queryUnloadDetailDto.getBoardCode());

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
        condition.setBoardCode(queryUnloadDetailDto.getBoardCode());
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
            condition.setScannedFlag(0);
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
                ComBoardAggDto aggDto = new ComBoardAggDto();
                aggDto.setBoardCode(boardCode);
                if(jyaggs != null) {
                    aggDto.setHaveScanCount(jyaggs.getActualScanCount());
                    aggDto.setExtraScanCount(jyaggs.getMoreScanTotalCount());
                    extraScanCount = extraScanCount + jyaggs.getMoreScanTotalCount();
                    haveScanCount = haveScanCount + jyaggs.getActualScanCount();
                }else {
                    aggDto.setHaveScanCount(0);
                    aggDto.setExtraScanCount(0);
                }
                comBoardDtoList.add(aggDto);
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
        RouteNextDto routeNextDto = routerService.matchNextNodeAndLastNodeByRouter(startSiteId, waybillCode, null);
        return routeNextDto.getFirstNextSiteId();
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
            UnloadMasterTaskDto masterTask = new UnloadMasterTaskDto();
            org.springframework.beans.BeanUtils.copyProperties(jyMasterTask, masterTask);
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
    public InvokeResult<UnloadChildTaskDto> queryChildTaskInfoByBizId(String childBizId) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.queryChildTaskInfoByBizId--根据子任务BizId查询子任务服务--";
        InvokeResult<UnloadChildTaskDto> res = new InvokeResult<>();
        res.success();
        try{
            if(StringUtils.isBlank(childBizId)) {
                res.error("参数缺失：必传任务bizId");
                return res;
            }
            JyBizTaskUnloadVehicleStageEntity jyBizTaskUnloadVehicleStageEntity = jyBizTaskUnloadVehicleStageDao.queryByBizId(childBizId);
            if(jyBizTaskUnloadVehicleStageEntity == null) {
                res.setMessage("未查到数据");
                return res;
            }
            UnloadChildTaskDto resData = new UnloadChildTaskDto();
            org.springframework.beans.BeanUtils.copyProperties(jyBizTaskUnloadVehicleStageEntity,resData);
            res.setData(resData);
            return res;
        }catch (Exception e) {
            log.error("{}服务异常，childBizId={}，errMsg={}", methodDesc, childBizId, e.getMessage(), e);
            res.error("根据子BizId查询子任务服务异常 " + e.getMessage());
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
                UnloadBoardRespDto taskBoardInfo = new UnloadBoardRespDto();
                org.springframework.beans.BeanUtils.copyProperties(entity, taskBoardInfo);
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
    public InvokeResult<UnloadBoardRespDto> queryTaskBoardInfoByChildTaskBizId(String childTaskBizId, String boardCode) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.queryTaskBoardInfoByChildTaskBizId--根据子BizId查询任务板关系服务--";
        InvokeResult<UnloadBoardRespDto> res = new InvokeResult<>();
        res.success();
        try{
            if(StringUtils.isBlank(childTaskBizId) || StringUtils.isBlank(boardCode)) {
                res.error("参数缺失");
                return res;
            }
            JyUnloadVehicleBoardEntity param = new JyUnloadVehicleBoardEntity();
            param.setUnloadVehicleStageBizId(childTaskBizId);
            param.setBoardCode(boardCode);
            JyUnloadVehicleBoardEntity jyMasterTask = jyUnloadVehicleBoardDao.getTaskBoardInfoByChildTaskBizId(param);
            if(jyMasterTask == null) {
                res.setMessage("查询数据为空");
                return res;
            }
            UnloadBoardRespDto resData = new UnloadBoardRespDto();
            org.springframework.beans.BeanUtils.copyProperties(jyMasterTask,resData);

            res.setData(resData);
            return res;
        }catch (Exception e) {
            log.error("{}服务异常，childTaskBizId={}，boardCode={}, errMsg={}", methodDesc, childTaskBizId, boardCode, e.getMessage(), e);
            res.error("根据子BizId查询任务板关系服务异常 " + e.getMessage());
            return  res;
        }
    }

    @Override
    public InvokeResult<UnloadBoardRespDto> queryTaskBoardInfoByBizIdAndBoardCode(String bizId, String boardCode) {
        final String methodDesc = "JyUnloadVehicleTysServiceImpl.queryTaskBoardInfoByBizIdAndBoardCode--根据BizId查询任务板关系服务--";
        InvokeResult<UnloadBoardRespDto> res = new InvokeResult<>();
        res.success();
        try{
            if(StringUtils.isBlank(bizId) || StringUtils.isBlank(boardCode)) {
                res.error("参数缺失");
                return res;
            }
            JyUnloadVehicleBoardEntity param = new JyUnloadVehicleBoardEntity();
            param.setUnloadVehicleBizId(bizId);
            param.setBoardCode(boardCode);
            JyUnloadVehicleBoardEntity entityRes = jyUnloadVehicleBoardDao.selectByBizIdAndBoardCode(param);

            if(entityRes == null) {
                res.setMessage("查询数据为空");
                return res;
            }
            UnloadBoardRespDto resData = new UnloadBoardRespDto();
            org.springframework.beans.BeanUtils.copyProperties(entityRes,resData);

            res.setData(resData);
            return res;
        }catch (Exception e) {
            log.error("{}服务异常，bizId={}，boardCode={}, errMsg={}", methodDesc, bizId, boardCode, e.getMessage(), e);
            res.error("根据BizId查询任务板关系服务异常 " + e.getMessage());
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
            if (null == boardResponse) {
                log.warn("{}--查询包裹所在板异常--packageCode={},siteCode={}", methodDesc, flowBoardDto.getPackageCode(), flowBoardDto.getCurrentOperate().getSiteCode());
                response.error("查询包裹所在板异常");
                return response;
            }
            if (boardResponse.getCode() != 200) {
                log.warn("{}--查询包裹所在板异常--packageCode={},siteCode={}", methodDesc, flowBoardDto.getPackageCode(), flowBoardDto.getCurrentOperate().getSiteCode());
                response.error(boardResponse.getMesseage());
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
                response.error("未查询到板上聚合数据");
                return response;
            }
            ComBoardAggDto aggDto = new ComBoardAggDto();
            aggDto.setBoardCode(boardResponse.getData().getCode());
            aggDto.setHaveScanCount(jyaggs.getActualScanCount());
            aggDto.setExtraScanCount(jyaggs.getMoreScanTotalCount());
            res.setComBoardAggDto(aggDto);
            //查流向
            String waybillCode = WaybillUtil.getWaybillCode(flowBoardDto.getPackageCode());
            RouteNextDto routeNextDto = routerService.matchNextNodeAndLastNodeByRouter(flowBoardDto.getCurrentOperate().getSiteCode(), waybillCode, null);
            Integer nextSiteCode = routeNextDto.getFirstNextSiteId();
            if(nextSiteCode == null) {
                log.warn("{}--包裹未查到路由信息--packageCode={},routeNextDto={}", methodDesc, waybillCode, JsonHelper.toJson(routeNextDto));
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
    private com.jd.bluedragon.common.dto.base.request.User copyUser(com.jd.bluedragon.distribution.jy.dto.User userParam) {
        com.jd.bluedragon.common.dto.base.request.User user = new com.jd.bluedragon.common.dto.base.request.User();
        user.setUserCode(userParam.getUserCode());
        user.setUserName(userParam.getUserName());
        user.setUserErp(userParam.getUserErp());
        return user;
    }

    private com.jd.bluedragon.common.dto.base.request.CurrentOperate copyCurrentOperate(com.jd.bluedragon.distribution.jy.dto.CurrentOperate currentOperateParam) {
        com.jd.bluedragon.common.dto.base.request.CurrentOperate currentOperate = new com.jd.bluedragon.common.dto.base.request.CurrentOperate();
        currentOperate.setSiteCode(currentOperateParam.getSiteCode());
        currentOperate.setSiteName(currentOperateParam.getSiteName());
        return currentOperate;
    }


    private void sendTaskCompleteMqHandler(JyBizTaskUnloadVehicleStageEntity doingChildTask, UnloadCompleteDto request) {
        JyUnloadCarTaskCompleteDto mqDto = new JyUnloadCarTaskCompleteDto();
        mqDto.setMasterTaskBizId(request.getBizId());
        mqDto.setMasterTaskTaskId(request.getTaskId());
        mqDto.setSealCarCode(request.getSealCarCode());
        if (doingChildTask != null) {
            mqDto.setChildTaskBizId(doingChildTask.getBizId());
        }
        mqDto.setOperateNode(OPERATE_NODE_TASK_COMPLETE);
        mqDto.setOperatorTime(System.currentTimeMillis());
        mqDto.setUser(request.getUser());
        mqDto.setCurrentOperate(request.getCurrentOperate());
        String businessId = mqDto.getMasterTaskBizId() + "-" + mqDto.getChildTaskBizId() + "-" + mqDto.getOperateNode();
        sendUnloadCarPostTaskCompleteMq(businessId, JsonHelper.toJson(mqDto));
    }

    void sendUnloadCarPostTaskCompleteMq(String businessId, String msg){
        String methodDesc = "JyUnloadVehicleTysServiceImpl.sendUnloadCarPostTaskCompleteMq--转运卸车岗发送任务完成MQ--";
        try{
            log.info("{}卸车岗任务完成发送MQ-start--MQ发送;businessId={}，msg={}", methodDesc, businessId, msg);
            jyUnloadCarPostTaskCompleteProducer.sendOnFailPersistent(businessId, msg);
        }catch (Exception e) {
            log.error("{}--MQ发送异常businessId={}，msg={},errMsg={}", methodDesc, businessId, msg, e.getMessage(), e);
        }
    }

}
