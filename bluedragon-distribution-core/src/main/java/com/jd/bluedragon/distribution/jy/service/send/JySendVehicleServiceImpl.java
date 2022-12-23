package com.jd.bluedragon.distribution.jy.service.send;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.CheckSendCodeRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.SendVehicleInfoRequest;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.BaseSendVehicle;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SealedVehicle;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendAbnormalBarCode;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendScanBarCode;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendTaskInfo;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendTaskItemDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleData;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleInfo;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendVehicleProgress;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.SendingVehicle;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestAgg;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealDestDetail;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSealVehicle;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.ToSendVehicle;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.common.dto.send.request.SendBatchReq;
import com.jd.bluedragon.common.dto.send.request.BindVehicleDetailTaskReq;
import com.jd.bluedragon.common.dto.send.request.CancelSendTaskReq;
import com.jd.bluedragon.common.dto.send.request.CreateVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.request.DeleteVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.request.TransferSendTaskReq;
import com.jd.bluedragon.common.dto.send.request.TransferVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.request.VehicleTaskReq;
import com.jd.bluedragon.common.dto.send.response.*;
import com.jd.bluedragon.common.dto.send.response.CancelSendTaskResp;
import com.jd.bluedragon.common.dto.send.response.CreateVehicleTaskResp;
import com.jd.bluedragon.common.dto.send.response.SendBatchResp;
import com.jd.bluedragon.common.dto.send.response.SendCodeDto;
import com.jd.bluedragon.common.dto.send.response.VehicleDetailTaskDto;
import com.jd.bluedragon.common.dto.send.response.VehicleSpecResp;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskDto;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskResp;
import com.jd.bluedragon.common.dto.sysConfig.request.MenuUsageConfigRequestDto;
import com.jd.bluedragon.common.dto.sysConfig.response.MenuUsageProcessDto;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.*;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.cyclebox.CycleBoxService;
import com.jd.bluedragon.distribution.delivery.constants.SendKeyTypeEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jsf.domain.ValidateIgnore;
import com.jd.bluedragon.distribution.jy.dto.JyLineTypeDto;
import com.jd.bluedragon.distribution.jy.dto.send.*;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyDemotionException;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.manager.IJySendVehicleJsfManager;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.send.*;
import com.jd.bluedragon.distribution.jy.service.config.JyDemotionService;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.seal.JySendSealCodeService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailServiceImpl;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.service.transfer.manager.JYTransferConfigProxy;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.sealVehicle.domain.TransTypeEnum;
import com.jd.bluedragon.distribution.send.domain.ConfirmMsgBox;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BarCodeType;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.constants.CodeConstants;
import com.jd.ql.dms.common.constants.JyConstants;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import com.jd.tms.jdi.dto.TransWorkFuzzyQueryParam;
import com.jd.tms.jdi.dto.TransWorkItemDto;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.transboard.api.enums.ResponseEnum;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.basic.api.domain.transferDp.ConfigTransferDpSite;
import com.jdl.basic.api.dto.transferDp.ConfigTransferDpSiteMatchQo;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.query.send.SendVehiclePackageDetailQuery;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.send.SendBarCodeDetailVo;
import com.jdl.jy.realtime.model.vo.send.SendVehiclePackageDetailVo;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskDistributionTypeEnum;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;


/**
 * @ClassName JySendVehicleServiceImpl
 * @Description 发货岗网关逻辑加工服务
 * @Author wyh
 * @Date 2022/5/29 14:31
 **/
@Service("jySendVehicleService")
public class JySendVehicleServiceImpl implements IJySendVehicleService {

    private static final Logger log = LoggerFactory.getLogger(JySendVehicleServiceImpl.class);

    private static final int SEND_SCAN_BAR_EXPIRE = 6;

    private static final int TRANS_BILL_STATUS_CONFIRM = 15;
    private static final int TRANS_BILL_WORK_STATUS = 20;

    /**
     * 运单路由字段使用的分隔符
     */
    private static final String WAYBILL_ROUTER_SPLIT = "\\|";
    private static final int VEHICLE_NUMBER_FOUR = 4;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    @Qualifier("redisJyNoTaskSendDetailBizIdSequenceGen")
    private JimdbSequenceGen redisJyNoTaskSendDetailBizIdSequenceGen;

    @Autowired
    private UccPropertyConfiguration uccConfig;

    @Autowired
    private JyBizTaskSendVehicleService taskSendVehicleService;

    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;

    @Autowired
    private JySendAggsService sendAggService;

    @Autowired
    private JySendSealCodeService sendSealCodeService;

    @Autowired
    private IJySendAttachmentService sendAttachmentService;

    @Autowired
    private IJySendService jySendService;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Autowired
    private JdiQueryWSManager jdiQueryWSManager;

    @Autowired
    private BasicQueryWSManager basicQueryWSManager;

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    @Autowired
    private BoxService boxService;

    @Autowired
    private JyVehicleSendRelationService jySendCodeService;

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Autowired
    private SortingCheckService sortingCheckService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private SendVehicleTransactionManager transactionManager;

    @Autowired
    private CycleBoxService cycleBoxService;

    @Autowired
    private IJySendVehicleJsfManager sendVehicleJsfManager;

    @Autowired
    @Qualifier("jyTaskGroupMemberService")
    private JyTaskGroupMemberService taskGroupMemberService;

    @Autowired
    @Qualifier("sendCarArriveStatusProducer")
    private DefaultJMQProducer sendCarArriveStatusProducer;

    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;

    @Autowired
    private BaseService baseService;
    @Autowired
    private SealVehiclesService sealVehiclesService;



    @Autowired
    private BasicSelectWsManager basicSelectWsManager;
    @Autowired
    SendDetailService sendDetailService;

    @Autowired
    private JyDemotionService jyDemotionService;
    @Autowired
    JyNoTaskSendService jyNoTaskSendService;
    @Autowired
    private GroupBoardManager groupBoardManager;

    @Autowired
    private JySendProductAggsService jySendProductAggsService;

    @Autowired
    private JYTransferConfigProxy jyTransferConfigProxy;

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.fetchSendVehicleTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendVehicleTaskResponse> fetchSendVehicleTask(SendVehicleTaskRequest request) {
        InvokeResult<SendVehicleTaskResponse> result = new InvokeResult<>();

        // 参数校验
        if (!checkBeforeFetchTask(request, result)) {
            return result;
        }

        try {
            SendVehicleTaskResponse response = new SendVehicleTaskResponse();
            result.setData(response);

            QueryTaskSendDto queryTaskSendDto = setQueryTaskSendDto(request);

            JyBizTaskSendVehicleEntity condition = makeFetchCondition(queryTaskSendDto);

            // 根据包裹号未查到发货流向的任务
            List<String> sendVehicleBizList = resolveSearchKeyword(result, queryTaskSendDto);
            if (!result.codeSuccess()) {
                return result;
            }

            response.setSendVehicleBizList(sendVehicleBizList);

            List<JyBizTaskSendCountDto> vehicleStatusAggList =
                    taskSendVehicleService.sumTaskByVehicleStatus(condition, sendVehicleBizList);
            if (CollectionUtils.isEmpty(vehicleStatusAggList)) {
                return result;
            }

            // 按任务状态统计发货任务数量
            assembleSendVehicleStatusAgg(vehicleStatusAggList, response);

            queryTaskSendDto.setSendVehicleBizList(sendVehicleBizList);

            // 按任务状态组装车辆数据
            assembleSendVehicleData(response, queryTaskSendDto);

            result.setData(response);
        } catch (Exception e) {
            log.error("查询发车任务异常. {}", JsonHelper.toJson(request), e);
            result.error("查询发车任务异常，请咚咚联系分拣小秘！");
        }

        return result;
    }


    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.findSendVehicleLineTypeAgg",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public List<JyLineTypeDto> findSendVehicleLineTypeAgg(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> invokeResult) {
            QueryTaskSendDto queryTaskSendDto = setQueryTaskSendDto(request);
            JyBizTaskSendVehicleEntity condition = makeFetchCondition(queryTaskSendDto);
            condition.setVehicleStatus(request.getVehicleStatus());
            // 根据包裹号未查到发货流向的任务
            List<String> sendVehicleBizList = invokeResult.getData().getSendVehicleBizList();
            List<JyBizTaskSendLineTypeCountDto> lineTypeAggList =
                    taskSendVehicleService.sumTaskByLineType(condition, sendVehicleBizList);
            return transformLineTypeAgg(lineTypeAggList);
    }

    /**
     * 按线路类型统计发货任务数
     * @param lineTypeAggList
     */
    private List<JyLineTypeDto> transformLineTypeAgg(List<JyBizTaskSendLineTypeCountDto> lineTypeAggList) {
        List<JyLineTypeDto> lineTypeAgg = Lists.newArrayListWithCapacity(lineTypeAggList.size());
        for (JyBizTaskSendLineTypeCountDto countDto : lineTypeAggList) {
            JyLineTypeDto item = new JyLineTypeDto();
            item.setLineType(countDto.getLineType());
            item.setLineTypeName(JyLineTypeEnum.getNameByCode(item.getLineType()));
            item.setTotal(countDto.getTotal() == null ? 0 : countDto.getTotal().intValue());
            lineTypeAgg.add(item);
        }
        return lineTypeAgg;
    }

    private QueryTaskSendDto setQueryTaskSendDto(SendVehicleTaskRequest request) {
        QueryTaskSendDto queryTaskSendDto = new QueryTaskSendDto();
        queryTaskSendDto.setPageNumber(request.getPageNumber());
        queryTaskSendDto.setPageSize(request.getPageSize());
        queryTaskSendDto.setVehicleStatuses(Collections.singletonList(request.getVehicleStatus()));
        queryTaskSendDto.setLineType(request.getLineType());
        queryTaskSendDto.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
        queryTaskSendDto.setEndSiteId(request.getEndSiteId());
        queryTaskSendDto.setKeyword(request.getKeyword());
        //设置默认预计发货时间查询范围
        try {
            if (ObjectHelper.isNotNull(request.getLastPlanDepartTimeBegin())) {
                queryTaskSendDto.setLastPlanDepartTimeBegin(request.getLastPlanDepartTimeBegin());
            } else {
                queryTaskSendDto.setLastPlanDepartTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -uccConfig.getJySendTaskPlanTimeBeginDay()));
            }
            if (ObjectHelper.isNotNull(request.getLastPlanDepartTimeEnd())) {
                queryTaskSendDto.setLastPlanDepartTimeEnd(request.getLastPlanDepartTimeEnd());
            } else {
                queryTaskSendDto.setLastPlanDepartTimeEnd(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), uccConfig.getJySendTaskPlanTimeEndDay()));
            }
            queryTaskSendDto.setCreateTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -uccConfig.getJySendTaskCreateTimeBeginDay()));

        } catch (Exception e) {
            log.error("查询发货任务设置默认查询条件异常，入参{}", JsonHelper.toJson(request), e.getMessage(), e);
        }
        return queryTaskSendDto;
    }

    /**
     * 按状态组装车辆数据
     * @param response
     * @param queryTaskSendDto
     */
    private void assembleSendVehicleData(SendVehicleTaskResponse response, QueryTaskSendDto queryTaskSendDto) {
        JyBizTaskSendStatusEnum curQueryStatus = JyBizTaskSendStatusEnum.getEnumByCode(queryTaskSendDto.getVehicleStatuses().get(0));
        SendVehicleData sendVehicleData = new SendVehicleData();
        sendVehicleData.setVehicleStatus(curQueryStatus.getCode());

        // 按车辆状态组装
        makeVehicleList(sendVehicleData, queryTaskSendDto);

        switch (curQueryStatus) {
            case TO_SEND:
                response.setToSendVehicleData(sendVehicleData);
                break;
            case SENDING:
                response.setSendingVehicleData(sendVehicleData);
                break;
            case TO_SEAL:
                response.setToSealVehicleData(sendVehicleData);
                break;
            case SEALED:
                response.setSealedVehicleData(sendVehicleData);
                break;
        }
    }

    /**
     * 按车辆状态组装车辆列表
     * @param sendVehicleData
     * @param queryTaskSendDto
     */
    private void makeVehicleList(SendVehicleData sendVehicleData, QueryTaskSendDto queryTaskSendDto) {
        List<BaseSendVehicle> vehicleList = Lists.newArrayList();
        sendVehicleData.setData(vehicleList);

        JyBizTaskSendStatusEnum curQueryStatus = JyBizTaskSendStatusEnum.getEnumByCode(queryTaskSendDto.getVehicleStatuses().get(0));

        // 设置排序方式
        JyBizTaskSendSortTypeEnum orderTypeEnum = setTaskOrderType(curQueryStatus);

        JyBizTaskSendVehicleEntity queryCondition = makeFetchCondition(queryTaskSendDto);

        List<JyBizTaskSendVehicleEntity> vehiclePageList = taskSendVehicleService.querySendTaskOfPage(queryCondition, queryTaskSendDto.getSendVehicleBizList(), orderTypeEnum,
                queryTaskSendDto.getPageNumber(), queryTaskSendDto.getPageSize(), queryTaskSendDto.getVehicleStatuses());
        if (CollectionUtils.isEmpty(vehiclePageList)) {
            return;
        }

        assemblePageSendTaskList(queryTaskSendDto, vehicleList, curQueryStatus, vehiclePageList);
    }

    private void assemblePageSendTaskList(QueryTaskSendDto queryTaskSendDto, List<BaseSendVehicle> vehicleList,
                                          JyBizTaskSendStatusEnum curQueryStatus, List<JyBizTaskSendVehicleEntity> vehiclePageList) {
        for (JyBizTaskSendVehicleEntity entity : vehiclePageList) {
            // 初始化基础字段
            BaseSendVehicle baseSendVehicle = assembleBaseSendVehicle(curQueryStatus, entity);

            // 设置个性化字段
            switch (curQueryStatus) {
                case TO_SEND:
                    ToSendVehicle toSendVehicle = (ToSendVehicle) baseSendVehicle;
                    toSendVehicle.setSendDestList(this.getSendVehicleDetail(queryTaskSendDto, curQueryStatus, entity));

                    vehicleList.add(toSendVehicle);
                    break;
                case SENDING:
                    SendingVehicle sendingVehicle = (SendingVehicle) baseSendVehicle;
                    sendingVehicle.setLoadRate(this.dealLoadRate(entity));
                    sendingVehicle.setSendDestList(this.getSendVehicleDetail(queryTaskSendDto, curQueryStatus, entity));

                    vehicleList.add(sendingVehicle);
                    break;
                case TO_SEAL:
                    ToSealVehicle toSealVehicle = (ToSealVehicle) baseSendVehicle;
                    toSealVehicle.setSendDestList(this.getSendVehicleDetail(queryTaskSendDto, curQueryStatus, entity));

                    vehicleList.add(toSealVehicle);
                    break;
                case SEALED:
                    SealedVehicle sealedVehicle = (SealedVehicle) baseSendVehicle;
                    sealedVehicle.setSealCodeCount(sendSealCodeService.countByBiz(entity.getBizId()));
                    sealedVehicle.setSendDestList(this.getSendVehicleDetail(queryTaskSendDto, curQueryStatus, entity));

                    vehicleList.add(sealedVehicle);
                    break;
            }
        }
    }

    /**
     * 按重量计算车辆装载率
     * @param entity
     * @return
     */
    private BigDecimal dealLoadRate(JyBizTaskSendVehicleEntity entity) {
        JySendAggsEntity sendAgg = sendAggService.getVehicleSendStatistics(entity.getBizId());
        if (sendAgg == null || !NumberHelper.gt0(sendAgg.getTotalScannedWeight())) {
            return BigDecimal.ZERO;
        }

        Integer vehicleType = entity.getVehicleType();
        if (!NumberHelper.gt0(vehicleType)) {
            return BigDecimal.ZERO;
        }
        // 根据车型从运输获得车型满载体积
        BasicVehicleTypeDto basicVehicleType = basicQueryWSManager.getVehicleTypeByVehicleType(vehicleType);
        if (basicVehicleType == null || basicVehicleType.getWeight() == null) {
            log.warn("从运输基础资料获取车型失败. {}", vehicleType);
            return BigDecimal.ZERO;
        }

        return this.dealLoadRate(sendAgg.getTotalScannedWeight(), this.convertTonToKg(BigDecimal.valueOf(basicVehicleType.getWeight())));
    }

    /**
     * 重量单位转为KG
     * @param weightOfTon
     * @return
     */
    private BigDecimal convertTonToKg(BigDecimal weightOfTon) {
        return weightOfTon.multiply(BigDecimal.valueOf(1000));
    }

    /**
     * 设置发货流向数据
     * @param queryTaskSendDto
     * @param curQueryStatus
     * @param entity
     * @return
     */
    private List<SendVehicleDetail> getSendVehicleDetail(QueryTaskSendDto queryTaskSendDto, JyBizTaskSendStatusEnum curQueryStatus, JyBizTaskSendVehicleEntity entity) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(queryTaskSendDto.getStartSiteId(), queryTaskSendDto.getEndSiteId(), entity.getBizId());
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(detailQ);
        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            return Lists.newArrayList();
        }
        List<SendVehicleDetail> sendDestList = Lists.newArrayListWithCapacity(vehicleDetailList.size());
        for (JyBizTaskSendVehicleDetailEntity vehicleDetail : vehicleDetailList) {
            SendVehicleDetail detailVo = new SendVehicleDetail();
            detailVo.setItemStatus(vehicleDetail.getVehicleStatus());
            detailVo.setItemStatusDesc(this.setDetailStatusShowDesc(vehicleDetail));
            detailVo.setPlanDepartTime(vehicleDetail.getPlanDepartTime());
            detailVo.setEndSiteId(vehicleDetail.getEndSiteId());
            detailVo.setEndSiteName(vehicleDetail.getEndSiteName());
            detailVo.setSendDetailBizId(vehicleDetail.getBizId());

            sendDestList.add(detailVo);
        }

        return sendDestList;
    }

    /**
     * 设置发货流向状态的描述
     * @param vehicleDetail
     * @return
     */
    private String setDetailStatusShowDesc(JyBizTaskSendVehicleDetailEntity vehicleDetail) {
        JyBizTaskSendDetailStatusEnum detailStatus = JyBizTaskSendDetailStatusEnum.getEnumByCode(vehicleDetail.getVehicleStatus());
        String fmtDesc;
        switch (detailStatus) {
            case TO_SEND:
                fmtDesc = "未扫";
                break;
            case SENDING:
                fmtDesc = "已扫";
                break;
            case TO_SEAL:
                fmtDesc = "待封车";
                break;
            case SEALED:
                if (vehicleDetail.getSealCarTime() != null) {
                    String formatTime = DateHelper.formatDate(vehicleDetail.getSealCarTime(), DateHelper.DATE_FORMAT_HHmm);
                    fmtDesc = formatTime + "封";
                } else {
                    fmtDesc = "已封车";
                }
                break;
            default:
                return "未知";
        }
        return fmtDesc;
    }

    /**
     * 组装发车任务基础数据
     * @param curQueryStatus
     * @param entity
     * @return
     */
    private BaseSendVehicle assembleBaseSendVehicle(JyBizTaskSendStatusEnum curQueryStatus, JyBizTaskSendVehicleEntity entity) {
        BaseSendVehicle baseSendVehicle = null;

        switch (curQueryStatus) {
            case TO_SEND:
                baseSendVehicle = new ToSendVehicle();
                break;
            case SENDING:
                baseSendVehicle = new SendingVehicle();
                break;
            case TO_SEAL:
                baseSendVehicle = new ToSealVehicle();
                break;
            case SEALED:
                baseSendVehicle = new SealedVehicle();
                break;
        }

        setBaseSendVehicle(entity, baseSendVehicle);

        return baseSendVehicle;
    }

    /**
     * 设置发货任务通用属性
     * @param entity
     * @param baseSendVehicle
     */
    private void setBaseSendVehicle(JyBizTaskSendVehicleEntity entity, BaseSendVehicle baseSendVehicle) {
        TransWorkBillDto transWorkBillDto = jdiQueryWSManager.queryTransWork(entity.getTransWorkCode());
        baseSendVehicle.setBizNo(entity.getBizNo());
        baseSendVehicle.setTaskName(entity.getTaskName());
        baseSendVehicle.setVehicleNumber(transWorkBillDto == null ? StringUtils.EMPTY : transWorkBillDto.getVehicleNumber());
        baseSendVehicle.setManualCreatedFlag(entity.manualCreatedTask());
        baseSendVehicle.setNoTaskBindVehicle(entity.noTaskBindVehicle());
        baseSendVehicle.setSendVehicleBizId(entity.getBizId());
        baseSendVehicle.setTaskId(getJyScheduleTaskId(entity.getBizId()));
        baseSendVehicle.setTransWorkCode(entity.getTransWorkCode());
        baseSendVehicle.setLineType(entity.getLineType());
        baseSendVehicle.setLineTypeName(entity.getLineTypeName());
        baseSendVehicle.setCreateUserErp(entity.getCreateUserErp());

        // 任务标签
        baseSendVehicle.setTags(resolveTaskTag(entity, transWorkBillDto));
    }

    /**
     * 解析任务标签
     *
     * @param entity
     * @param transWorkBillDto
     * @return
     */
    private List<LabelOption> resolveTaskTag(JyBizTaskSendVehicleEntity entity, TransWorkBillDto transWorkBillDto) {
        List<LabelOption> tagList = new ArrayList<>();

        // 司机是否领取任务
        if (transWorkBillDto != null) {
            // work_status = 20(已开始), status > 15(待接受)
            if (Objects.equals(TRANS_BILL_WORK_STATUS, transWorkBillDto.getWorkStatus()) && NumberHelper.gt(transWorkBillDto.getStatus(), TRANS_BILL_STATUS_CONFIRM)) {
                SendVehicleLabelOptionEnum driverRecvTaskTag = SendVehicleLabelOptionEnum.DRIVER_RECEIVE;
                tagList.add(new LabelOption(driverRecvTaskTag.getCode(), driverRecvTaskTag.getName(), driverRecvTaskTag.getDisplayOrder()));
            }
        }

        // 车长
        String carLengthDesc = setCarLength(entity);
        SendVehicleLabelOptionEnum carLengthTag = SendVehicleLabelOptionEnum.CAR_LENGTH;
        tagList.add(new LabelOption(carLengthTag.getCode(), carLengthDesc, carLengthTag.getDisplayOrder()));

        return tagList;
    }

    /**
     * 查询调度任务ID
     *
     * @param bizId
     * @return
     */
    private String getJyScheduleTaskId(String bizId) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(bizId);
        req.setTaskType(JyScheduleTaskTypeEnum.SEND.getCode());
        JyScheduleTaskResp scheduleTask = jyScheduleTaskManager.findScheduleTaskByBizId(req);
        return null != scheduleTask ? scheduleTask.getTaskId() : StringUtils.EMPTY;
    }

    private JyBizTaskSendSortTypeEnum setTaskOrderType(JyBizTaskSendStatusEnum curQueryStatus) {
        switch (curQueryStatus) {
            case TO_SEND:
            case SENDING:
            case TO_SEAL:
                return JyBizTaskSendSortTypeEnum.PLAN_DEPART_TIME;
            case SEALED:
                return JyBizTaskSendSortTypeEnum.SEAL_CAR_TIME;
            default:
                return null;
        }
    }

    /**
     * 按状态统计发货任务数
     *
     * @param vehicleStatusAggList
     * @param response
     */
    private void assembleSendVehicleStatusAgg(List<JyBizTaskSendCountDto> vehicleStatusAggList, SendVehicleTaskResponse response) {
        List<VehicleStatusStatis> statusAgg = Lists.newArrayListWithCapacity(vehicleStatusAggList.size());
        response.setStatusAgg(statusAgg);

        for (JyBizTaskSendCountDto countDto : vehicleStatusAggList) {
            VehicleStatusStatis item = new VehicleStatusStatis();
            item.setVehicleStatus(countDto.getVehicleStatus());
            item.setVehicleStatusName(JyBizTaskSendStatusEnum.getNameByCode(item.getVehicleStatus()));
            item.setTotal(countDto.getTotal());
            statusAgg.add(item);
        }
    }

    /**
     * 根据包裹号查询路由下一跳的发货任务
     * 取当前操作机构的下一跳作为发货目的地查询发货流向任务
     * @param result
     * @param queryTaskSendDto
     * @return
     */
    private <T> List<String> resolveSearchKeyword(InvokeResult<T> result, QueryTaskSendDto queryTaskSendDto) {
        if (StringUtils.isBlank(queryTaskSendDto.getKeyword())) {
            return null;
        }

        long startSiteId = queryTaskSendDto.getStartSiteId();
        Long endSiteId = null;

        // 取当前操作网点的路由下一节点
        if (WaybillUtil.isPackageCode(queryTaskSendDto.getKeyword())) {
            endSiteId = getWaybillNextRouter(WaybillUtil.getWaybillCode(queryTaskSendDto.getKeyword()), startSiteId);
        } else if (BusinessUtil.isSendCode(queryTaskSendDto.getKeyword())) {
            endSiteId = Long.valueOf(BusinessUtil.getReceiveSiteCodeFromSendCode(queryTaskSendDto.getKeyword()));
        } else if (BusinessUtil.isTaskSimpleCode(queryTaskSendDto.getKeyword())) {
            List<String> sendVehicleBizList = querySendVehicleBizIdByTaskSimpleCode(queryTaskSendDto);
            if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
                return sendVehicleBizList;
            }
            result.hintMessage("未检索到相应的发货任务数据！");
            return null;
        } else {
            //车牌号后四位检索
            if (queryTaskSendDto.getKeyword().length() == VEHICLE_NUMBER_FOUR) {
                List<String> sendVehicleBizList = querySendVehicleBizIdByVehicleFuzzy(queryTaskSendDto);
                if (ObjectHelper.isNotNull(sendVehicleBizList) && sendVehicleBizList.size() > 0) {
                    return sendVehicleBizList;
                }
                result.hintMessage("未检索到相应的发货任务数据！");
            } else {
                result.hintMessage("输入位数错误，未检索到发货任务数据！");
            }
            return null;
        }

        if (endSiteId == null) {
            result.hintMessage("运单的路由没有当前场地！");
            return null;
        }

        // 根据路由下一节点查询发货流向的任务
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(startSiteId, endSiteId);
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findBySiteAndStatus(detailQ, null);
        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            String msg = String.format("该包裹没有路由下一站[%s]的发货任务！", endSiteId);
            result.hintMessage(msg);
            return null;
        }
        Set<String> sendVehicleBizSet = new HashSet<>();
        for (JyBizTaskSendVehicleDetailEntity entity : vehicleDetailList) {
            sendVehicleBizSet.add(entity.getSendVehicleBizId());
        }

        return new ArrayList<>(sendVehicleBizSet);
    }

    private List<String> querySendVehicleBizIdByTaskSimpleCode(QueryTaskSendDto queryTaskSendDto) {
        com.jd.tms.jdi.dto.CommonDto<TransWorkItemDto> transWorkItemResp = jdiQueryWSManager.queryTransWorkItemBySimpleCode(queryTaskSendDto.getKeyword());
        if (ObjectHelper.isNotNull(transWorkItemResp) && Constants.RESULT_SUCCESS == transWorkItemResp.getCode()) {
            TransWorkItemDto transWorkItemDto = transWorkItemResp.getData();
            if (log.isInfoEnabled()){
                log.info("根据任务简码查询TransWorkItemDto信息：{}",JsonHelper.toJson(transWorkItemDto));
            }
            if (ObjectHelper.isNotNull(transWorkItemDto) && ObjectHelper.isNotNull(transWorkItemDto.getTransWorkCode())) {
                List<String> tranWorkCodes = new ArrayList<>();
                tranWorkCodes.add(transWorkItemDto.getTransWorkCode());
                List<JyBizTaskSendVehicleEntity> entityList = taskSendVehicleService.findSendTaskByTransWorkCode(tranWorkCodes, queryTaskSendDto.getStartSiteId());
                if (ObjectHelper.isNotNull(entityList) && entityList.size() > 0) {
                    if (log.isInfoEnabled()){
                        log.info("根据派车单号查询任务bizId：{}",JsonHelper.toJson(transWorkItemDto));
                    }
                    List<String> bizIdList = new ArrayList<>();
                    for (JyBizTaskSendVehicleEntity entity : entityList) {
                        bizIdList.add(entity.getBizId());
                    }
                    return bizIdList;
                }
            }
        }
        return null;
    }

    private List<String> querySendVehicleBizIdByVehicleFuzzy(QueryTaskSendDto queryTaskSendDto) {
        TransWorkFuzzyQueryParam param = new TransWorkFuzzyQueryParam();
        BaseStaffSiteOrgDto baseStaffSiteOrgDto;
        try {
            baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(queryTaskSendDto.getStartSiteId().intValue());
            if (ObjectHelper.isEmpty(baseStaffSiteOrgDto) || ObjectHelper.isEmpty(baseStaffSiteOrgDto.getDmsSiteCode())) {
                log.info("getBaseSiteBySiteId未获取到" + queryTaskSendDto.getStartSiteId() + "站点信息");
                return null;
            }
        } catch (Exception e) {
            log.error("getBaseSiteBySiteId获取站点信息异常", e);
            return null;
        }

        param.setBeginNodeCode(baseStaffSiteOrgDto.getDmsSiteCode());
        param.setVehicleNumber(queryTaskSendDto.getKeyword());
        List<String> tranWorkCodes = jdiQueryWSManager.listTranWorkCodesByVehicleFuzzy(param);
        if (ObjectHelper.isNotNull(tranWorkCodes) && tranWorkCodes.size() > 0) {
            List<JyBizTaskSendVehicleEntity> entityList = taskSendVehicleService.findSendTaskByTransWorkCode(tranWorkCodes, queryTaskSendDto.getStartSiteId());
            if (ObjectHelper.isNotNull(entityList) && entityList.size() > 0) {
                List<String> bizIdList = new ArrayList<>();
                for (JyBizTaskSendVehicleEntity entity : entityList) {
                    bizIdList.add(entity.getBizId());
                }
                return bizIdList;
            }
        }
        return null;
    }

    private Long getWaybillNextRouterWithTransferConfig(String waybillCode, Integer startSiteId, Boolean manualCreatedFlag, JdVerifyResponse<SendScanResponse> response) {
        Long matchDestIdByPack = getWaybillNextRouter(waybillCode, Long.valueOf(startSiteId));
        Waybill waybill1 = waybillQueryManager.queryWaybillByWaybillCode(waybillCode);
        if (waybill1 != null && BusinessHelper.isDPWaybill1_2(waybill1.getWaybillSign()) && manualCreatedFlag) {
            ConfigTransferDpSiteMatchQo matchQo = new ConfigTransferDpSiteMatchQo();
            matchQo.setHandoverSiteCode(startSiteId);
            matchQo.setPreSortSiteCode(waybill1.getOldSiteId());
            ConfigTransferDpSite resultCof = jyTransferConfigProxy.queryMatchConditionRecord(matchQo);
            if (jyTransferConfigProxy.isMatchConfig(resultCof, waybill1.getWaybillSign())) {
                matchDestIdByPack = null;
                response.setCode(SendScanResponse.CODE_CONFIRM_DEST);
                response.addWarningBox(101, "您扫描的" + waybillCode + "订单是转德邦订单，需手动选择下游目的地，谢谢。");
            }
        }
        return matchDestIdByPack;
    }

    /**
     * 获取运单路由下一节点
     * @param waybillCode
     * @param startSiteId
     * @return
     */
    private Long getWaybillNextRouter(String waybillCode, Long startSiteId) {
        String routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
        return getRouteNextSite(startSiteId, routerStr);
    }

    /**
     * 从路由里解析下一站
     * @param startSiteId
     * @param routerStr
     * @return
     */
    private Long getRouteNextSite(Long startSiteId, String routerStr) {
        if (StringUtils.isNotBlank(routerStr)) {
            String[] routerNodes = routerStr.split(WAYBILL_ROUTER_SPLIT);
            for (int i = 0; i < routerNodes.length - 1; i++) {
                long curNode = Long.parseLong(routerNodes[i]);
                long nextNode = Long.parseLong(routerNodes[i + 1]);
                if (curNode == startSiteId) {
                    return nextNode;
                }
            }
        }

        return null;
    }

    /**
     * 组装发车任务查询条件
     * @param queryTaskSendDto
     * @return
     */
    private JyBizTaskSendVehicleEntity makeFetchCondition(QueryTaskSendDto queryTaskSendDto) {
        JyBizTaskSendVehicleEntity condition = new JyBizTaskSendVehicleEntity();
        condition.setStartSiteId(queryTaskSendDto.getStartSiteId());
        if (queryTaskSendDto.getLineType() != null) {
            condition.setLineType(queryTaskSendDto.getLineType());
        }
        condition.setLastPlanDepartTimeBegin(queryTaskSendDto.getLastPlanDepartTimeBegin());
        condition.setLastPlanDepartTimeEnd(queryTaskSendDto.getLastPlanDepartTimeEnd());
        if (ObjectHelper.isNotNull(queryTaskSendDto.getCreateTimeBegin())) {
            condition.setCreateTimeBegin(queryTaskSendDto.getCreateTimeBegin());
        }
        return condition;
    }

    public boolean checkBeforeFetchTask(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> result) {
        if (request.getVehicleStatus() == null) {
            result.parameterError("请选择车辆状态！");
            return false;
        }
        if (!NumberHelper.gt0(request.getPageSize()) || !NumberHelper.gt0(request.getPageNumber())) {
            result.parameterError("缺少分页参数！");
            return false;
        }
        if (request.getCurrentOperate() == null || !NumberHelper.gt0(request.getCurrentOperate().getSiteCode())) {
            result.parameterError("缺少当前场地信息！");
            return false;
        }
        if (request.getLineType() == null) {
            result.parameterError("请选择线路类型！");
            return false;
        }
        return true;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.fetchSendTaskForBinding",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> fetchSendTaskForBinding(VehicleTaskReq vehicleTaskReq) {
        InvokeResult<VehicleTaskResp> result = new InvokeResult<>();
        if (StringUtils.isBlank(vehicleTaskReq.getBizId())) {
            result.parameterError();
            return result;
        }

        // 无任务发货只有一个流向
        JyBizTaskSendVehicleDetailEntity sendDestQ = new JyBizTaskSendVehicleDetailEntity();
        sendDestQ.setSendVehicleBizId(vehicleTaskReq.getBizId());
        sendDestQ.setStartSiteId((long) vehicleTaskReq.getCurrentOperate().getSiteCode());
        JyBizTaskSendVehicleDetailEntity curSendDest = taskSendVehicleDetailService.findSendDetail(sendDestQ);
        if (curSendDest == null) {
            result.hintMessage("无任务绑定之前必须发货！");
            return result;
        }
        try {
            curSendDest.setLastPlanDepartTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -uccConfig.getJySendTaskPlanTimeBeginDay()));
            curSendDest.setLastPlanDepartTimeEnd(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), uccConfig.getJySendTaskPlanTimeEndDay()));
            curSendDest.setCreateTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -uccConfig.getJySendTaskCreateTimeBeginDay()));
            // 统计发货任务数量
            Integer taskCount = taskSendVehicleService.countSendTaskByDest(curSendDest);
            VehicleTaskResp taskResp = new VehicleTaskResp();
            result.setData(taskResp);
            taskResp.setCount(taskCount);
            List<VehicleTaskDto> vehicleTaskList = new ArrayList<>();
            taskResp.setVehicleTaskDtoList(vehicleTaskList);
            if (taskResp.getCount() == 0) {
                return result;
            }

            // 默认按预计发车时间排序
            curSendDest.setSendVehicleBizId(null);
            curSendDest.setTransferFlag(TransFlagEnum.IN.getCode());
            List<JyBizTaskSendVehicleEntity> vehiclePageList = taskSendVehicleService.findSendTaskByDestOfPage(curSendDest,
                    vehicleTaskReq.getPageNumber(), vehicleTaskReq.getPageSize());

            if (CollectionUtils.isEmpty(vehiclePageList)) {
                result.setMessage(HintService.getHint(HintCodeConstants.NOT_FOUND_BINDING_TASK_DATA_MSG));
                return result;
            }

            for (JyBizTaskSendVehicleEntity sendVehicleEntity : vehiclePageList) {
                // 组装发车任务
                VehicleTaskDto vehicleTaskDto = this.initVehicleTaskDto(sendVehicleEntity);

                List<VehicleDetailTaskDto> vdList = new ArrayList<>();
                vehicleTaskDto.setVehicleDetailTaskDtoList(vdList);
                vehicleTaskList.add(vehicleTaskDto);

                // 组装发车任务流向明细
                this.initVehicleTaskDetails(sendVehicleEntity, vdList);
            }
        } catch (Exception e) {
            log.error("查询发车任务异常. {}", JsonHelper.toJson(vehicleTaskReq), e);
            result.error("查询发车任务异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    private void initVehicleTaskDetails(JyBizTaskSendVehicleEntity sendVehicleEntity, List<VehicleDetailTaskDto> vdList) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(sendVehicleEntity.getStartSiteId(), sendVehicleEntity.getBizId());
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findNoSealCarSendVehicleDetail(detailQ);
        for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {
            VehicleDetailTaskDto detailTaskDto = new VehicleDetailTaskDto();
            detailTaskDto.setBizId(detailEntity.getBizId());
            detailTaskDto.setTransWorkItemCode(detailEntity.getTransWorkItemCode());
            detailTaskDto.setVehicleStatus(detailEntity.getVehicleStatus());
            detailTaskDto.setStartSiteId(detailEntity.getStartSiteId());
            detailTaskDto.setStartSiteName(detailEntity.getStartSiteName());
            detailTaskDto.setEndSiteId(detailEntity.getEndSiteId());
            detailTaskDto.setEndSiteName(detailEntity.getEndSiteName());
            detailTaskDto.setPlanDepartTime(detailEntity.getPlanDepartTime());

            vdList.add(detailTaskDto);
        }
    }

    private void initVehicleTaskDetails(QueryTaskSendDto queryTaskSendDto, JyBizTaskSendVehicleEntity sendVehicleEntity,
                                        List<VehicleDetailTaskDto> vdList, Set<String> needToRemoveTask) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(queryTaskSendDto.getStartSiteId(), queryTaskSendDto.getEndSiteId(), sendVehicleEntity.getBizId());
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(detailQ);

        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            needToRemoveTask.add(sendVehicleEntity.getBizId());
            return;
        }

        for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {

            // 根据目的地匹配的发货流向已封车，发车任务需要剔除掉
            this.needToRemoveSendTask(queryTaskSendDto, needToRemoveTask, detailEntity);

            VehicleDetailTaskDto detailTaskDto = new VehicleDetailTaskDto();
            detailTaskDto.setBizId(detailEntity.getBizId());
            detailTaskDto.setTransWorkItemCode(detailEntity.getTransWorkItemCode());
            detailTaskDto.setVehicleStatus(detailEntity.getVehicleStatus());
            detailTaskDto.setStartSiteId(detailEntity.getStartSiteId());
            detailTaskDto.setStartSiteName(detailEntity.getStartSiteName());
            detailTaskDto.setEndSiteId(detailEntity.getEndSiteId());
            detailTaskDto.setEndSiteName(detailEntity.getEndSiteName());
            detailTaskDto.setPlanDepartTime(detailEntity.getPlanDepartTime());

            vdList.add(detailTaskDto);
        }
    }

    private void needToRemoveSendTask(QueryTaskSendDto queryTaskSendDto, Set<String> needToRemoveTask, JyBizTaskSendVehicleDetailEntity detailEntity) {
        if (detailEntity.getVehicleStatus().equals(JyBizTaskSendDetailStatusEnum.SEALED.getCode()) && detailEntity.getEndSiteId().equals(queryTaskSendDto.getEndSiteId())) {
            needToRemoveTask.add(detailEntity.getSendVehicleBizId());
        }
    }

    private VehicleTaskDto initVehicleTaskDto(JyBizTaskSendVehicleEntity sendVehicleEntity) {
        VehicleTaskDto vehicleTaskDto = new VehicleTaskDto();
        vehicleTaskDto.setBizId(sendVehicleEntity.getBizId());
        vehicleTaskDto.setTransWorkCode(sendVehicleEntity.getTransWorkCode());

        TransWorkBillDto transWorkBillDto = jdiQueryWSManager.queryTransWork(sendVehicleEntity.getTransWorkCode());
        vehicleTaskDto.setVehicleNumber(transWorkBillDto == null ? StringUtils.EMPTY : transWorkBillDto.getVehicleNumber());
        vehicleTaskDto.setVehicleStatus(sendVehicleEntity.getVehicleStatus());
        vehicleTaskDto.setTransWay(sendVehicleEntity.getTransWay());
        vehicleTaskDto.setTransWayName(sendVehicleEntity.getTransWayName());
        vehicleTaskDto.setVehicleType(sendVehicleEntity.getVehicleType());
        vehicleTaskDto.setVehicleTypeName(sendVehicleEntity.getVehicleTypeName());
        vehicleTaskDto.setLineType(sendVehicleEntity.getLineType());
        vehicleTaskDto.setLineTypeName(sendVehicleEntity.getLineTypeName());
        vehicleTaskDto.setLoadRate(this.dealLoadRate(sendVehicleEntity));

        return vehicleTaskDto;
    }

    private QueryTaskSendDto makeSendTaskQuery(VehicleTaskReq vehicleTaskReq) {
        QueryTaskSendDto queryTaskSendDto = new QueryTaskSendDto();
        queryTaskSendDto.setPageNumber(vehicleTaskReq.getPageNumber());
        queryTaskSendDto.setPageSize(vehicleTaskReq.getPageSize());
        queryTaskSendDto.setVehicleStatuses(JyBizTaskSendStatusEnum.UN_SEALED_STATUS);
        queryTaskSendDto.setStartSiteId((long) vehicleTaskReq.getCurrentOperate().getSiteCode());
        return queryTaskSendDto;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.fetchSendTaskForTransfer",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> fetchSendTaskForTransfer(TransferVehicleTaskReq vehicleTaskReq) {
        InvokeResult<VehicleTaskResp> result = new InvokeResult<>();
        if (vehicleTaskReq.getCurrentOperate() == null || vehicleTaskReq.getCurrentOperate().getSiteCode() < 0) {
            result.parameterError("缺少场地信息");
            return result;
        }

        try {
            Long startSiteId = (long) vehicleTaskReq.getCurrentOperate().getSiteCode();
            JyBizTaskSendVehicleDetailEntity queryDetail = new JyBizTaskSendVehicleDetailEntity();
            queryDetail.setStartSiteId(startSiteId);
            // 选择转出任务，转出按包裹扫描记录匹配发货任务
            if (Objects.equals(Constants.CONSTANT_NUMBER_ONE, vehicleTaskReq.getTransferFlag())) {
                if (!getSendTaskByPackage(vehicleTaskReq, result, queryDetail)) {
                    return result;
                }
            }
            // 选择转入任务，包裹按路由目的地匹配发货任务
            else {
                if (WaybillUtil.isPackageCode(vehicleTaskReq.getPackageCode())) {
                    Long nextRouter = getWaybillNextRouter(WaybillUtil.getWaybillCode(vehicleTaskReq.getPackageCode()), startSiteId);
                    if (nextRouter == null) {
                        result.hintMessage("运单路由里没有当前场地！");
                        return result;
                    }
                    queryDetail.setEndSiteId(nextRouter);
                }
            }
            if (NumberHelper.gt0(vehicleTaskReq.getEndSiteId())) {
                queryDetail.setEndSiteId(vehicleTaskReq.getEndSiteId());
            }

            VehicleTaskResp taskResp = new VehicleTaskResp();
            result.setData(taskResp);
            List<VehicleTaskDto> vehicleTaskList = new ArrayList<>();
            taskResp.setVehicleTaskDtoList(vehicleTaskList);

            queryDetail.setLastPlanDepartTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(),-uccConfig.getJySendTaskPlanTimeBeginDay()));
            queryDetail.setLastPlanDepartTimeEnd(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(),uccConfig.getJySendTaskPlanTimeEndDay()));
            queryDetail.setCreateTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(),-uccConfig.getJySendTaskCreateTimeBeginDay()));
            List<JyBizTaskSendVehicleEntity> vehiclePageList = taskSendVehicleService.findSendTaskByDestOfPage(queryDetail,
                    vehicleTaskReq.getPageNumber(), vehicleTaskReq.getPageSize());

            if (CollectionUtils.isEmpty(vehiclePageList)) {
                result.setMessage(HintService.getHint(HintCodeConstants.NOT_FOUND_TRANSFER_TASK_DATA_MSG));
                return result;
            }

            for (JyBizTaskSendVehicleEntity sendVehicleEntity : vehiclePageList) {
                // 组装发车任务
                VehicleTaskDto vehicleTaskDto = this.initVehicleTaskDto(sendVehicleEntity);

                List<VehicleDetailTaskDto> vdList = new ArrayList<>();
                vehicleTaskDto.setVehicleDetailTaskDtoList(vdList);
                vehicleTaskList.add(vehicleTaskDto);

                // 组装发车任务流向明细
                this.initVehicleTaskDetails(sendVehicleEntity, vdList);
            }
        }
        catch (Exception e) {
            log.error("查询发车任务异常. {}", JsonHelper.toJson(vehicleTaskReq), e);
            result.error("查询发车任务异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.fetchSendTaskForTransferV2",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> fetchSendTaskForTransferV2(TransferVehicleTaskReq vehicleTaskReq) {
        InvokeResult<VehicleTaskResp> result = new InvokeResult<>();
        if (vehicleTaskReq.getCurrentOperate() == null || vehicleTaskReq.getCurrentOperate().getSiteCode() < 0) {
            result.parameterError("缺少场地信息");
            return result;
        }

        try {
            String barCode =vehicleTaskReq.getBarCode();
            Long startSiteId = (long) vehicleTaskReq.getCurrentOperate().getSiteCode();
            JyBizTaskSendVehicleDetailEntity queryDetail = new JyBizTaskSendVehicleDetailEntity();
            queryDetail.setStartSiteId(startSiteId);
            //包裹号
            if (WaybillUtil.isPackageCode(barCode)){
                if (Objects.equals(Constants.CONSTANT_NUMBER_ONE, vehicleTaskReq.getTransferFlag())) {
                    if (!getSendTaskByPackage(vehicleTaskReq, result, queryDetail)) {
                        String boardCode = getBoardCodeByPackageCode(barCode, startSiteId.intValue());
                        if (boardCode == null) {
                            return result;
                        }
                        // 当包裹号存在对应的板号时，再次根据板号查询
                        vehicleTaskReq.setBarCode(boardCode);
                        if (!getSendTaskByPackage(vehicleTaskReq, result, queryDetail)) {
                            return result;
                        }
                        result.success();
                    }
                }
                else {
                    Long nextRouter = getWaybillNextRouter(WaybillUtil.getWaybillCode(vehicleTaskReq.getPackageCode()), startSiteId);
                    if (nextRouter == null) {
                        result.hintMessage("运单路由里没有当前场地！");
                        return result;
                    }
                    queryDetail.setEndSiteId(nextRouter);
                }

            }
            //批次号
            else if (BusinessUtil.isSendCode(barCode)){
                queryDetail.setEndSiteId(Long.valueOf(BusinessUtil.getReceiveSiteCodeFromSendCode(barCode)));
            }
            //任务简码
            else if (BusinessUtil.isTaskSimpleCode(barCode)){
                String transWorkItemCode =getTransWorkItemCodeBySimpleCode(barCode);
                if (null==transWorkItemCode){
                    result.setMessage(HintService.getHint(HintCodeConstants.NOT_FOUND_TRANSFER_TASK_DATA_MSG));
                    return result;
                }
                queryDetail.setTransWorkItemCode(transWorkItemCode);
            }
            //不是纯数字--并且是4位-按车牌号
            else if (!NumberHelper.gt0(barCode) && VEHICLE_NUMBER_FOUR ==barCode.length()){
                List<String> transWorkCodeList =getTransWorkCodeByVehicleNum(vehicleTaskReq);
                if (null==transWorkCodeList){
                    result.setMessage(HintService.getHint(HintCodeConstants.NOT_FOUND_TRANSFER_TASK_DATA_MSG));
                    return result;
                }
                queryDetail.setTransWorkCodeList(transWorkCodeList);
            }
            //纯数字
            else if (NumberHelper.gt0(barCode)){
                //四位 优先按车牌号
                if (VEHICLE_NUMBER_FOUR ==barCode.length()){
                    //没查到再按目的站点匹配
                    List<String> transWorkCodeList =getTransWorkCodeByVehicleNum(vehicleTaskReq);
                    if (ObjectHelper.isNotNull(transWorkCodeList) && findExitTaskRecordByTransWorkCode(transWorkCodeList,startSiteId)){
                        queryDetail.setTransWorkCodeList(transWorkCodeList);
                    }
                    else {
                        queryDetail.setEndSiteId(Long.valueOf(barCode));
                    }
                }
                //按站点匹配
                else {
                    queryDetail.setEndSiteId(Long.valueOf(barCode));
                }
            }
            else {
                //不支持该类型
                result.parameterError("暂不支持该类型条码！");
                return result;
            }

            VehicleTaskResp taskResp = new VehicleTaskResp();
            result.setData(taskResp);
            List<VehicleTaskDto> vehicleTaskList = new ArrayList<>();
            taskResp.setVehicleTaskDtoList(vehicleTaskList);

            if (Objects.equals(TransFlagEnum.IN.getCode(), vehicleTaskReq.getTransferFlag())){
                queryDetail.setTransferFlag(TransFlagEnum.IN.getCode());
            }
            queryDetail.setLastPlanDepartTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(),-uccConfig.getJySendTaskPlanTimeBeginDay()));
            queryDetail.setLastPlanDepartTimeEnd(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(),uccConfig.getJySendTaskPlanTimeEndDay()));
            queryDetail.setCreateTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(),-uccConfig.getJySendTaskCreateTimeBeginDay()));
            List<JyBizTaskSendVehicleEntity> vehiclePageList = taskSendVehicleService.findSendTaskByDestOfPage(queryDetail,
                    vehicleTaskReq.getPageNumber(), vehicleTaskReq.getPageSize());

            if (CollectionUtils.isEmpty(vehiclePageList)) {
                result.setMessage(HintService.getHint(HintCodeConstants.NOT_FOUND_TRANSFER_TASK_DATA_MSG));
                return result;
            }

            for (JyBizTaskSendVehicleEntity sendVehicleEntity : vehiclePageList) {
                // 组装发车任务
                VehicleTaskDto vehicleTaskDto = this.initVehicleTaskDto(sendVehicleEntity);

                List<VehicleDetailTaskDto> vdList = new ArrayList<>();
                vehicleTaskDto.setVehicleDetailTaskDtoList(vdList);
                vehicleTaskList.add(vehicleTaskDto);

                // 组装发车任务流向明细
                this.initVehicleTaskDetails(sendVehicleEntity, vdList);
            }
        }
        catch (Exception e) {
            log.error("查询迁移任务列表异常. {}", JsonHelper.toJson(vehicleTaskReq), e);
            result.error("查询发车任务异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    private String getBoardCodeByPackageCode(String packageCode, int createSiteCode) {
        Response<Board> boardResponse = groupBoardManager.getBoardByBoxCode(packageCode, createSiteCode);
        if (!JdCResponse.CODE_SUCCESS.equals(boardResponse.getCode())) {
            log.warn("fetchSendTaskForTransferV2|未根据包裹号找到匹配的板号,barCode={},response={}", packageCode, JsonHelper.toJson(boardResponse));
            return null;
        }
        Board board = boardResponse.getData();
        if (board != null) {
            return board.getCode();
        }
        return null;
    }

    private boolean findExitTaskRecordByTransWorkCode(List<String> transWorkCodeList,Long startSiteId) {
        List<JyBizTaskSendVehicleEntity> taskSendVehicleEntities =taskSendVehicleService.findSendTaskByTransWorkCode(transWorkCodeList,startSiteId);
        if (ObjectHelper.isNotNull(taskSendVehicleEntities) && taskSendVehicleEntities.size()>0){
            return true;
        }
        return false;
    }

    private String getTransWorkItemCodeBySimpleCode(String barCode) {
        com.jd.tms.jdi.dto.CommonDto<TransWorkItemDto> transWorkItemResp = jdiQueryWSManager.queryTransWorkItemBySimpleCode(barCode);
        if (ObjectHelper.isNotNull(transWorkItemResp) && Constants.RESULT_SUCCESS == transWorkItemResp.getCode()) {
            TransWorkItemDto transWorkItemDto = transWorkItemResp.getData();
            if (ObjectHelper.isNotNull(transWorkItemDto) && ObjectHelper.isNotNull(transWorkItemDto.getTransWorkItemCode())) {
                return transWorkItemDto.getTransWorkItemCode();
            }
        }
        return null;
    }

    private List<String> getTransWorkCodeByVehicleNum(TransferVehicleTaskReq vehicleTaskReq) {
        TransWorkFuzzyQueryParam param = new TransWorkFuzzyQueryParam();
        BaseStaffSiteOrgDto baseStaffSiteOrgDto;
        try {
            baseStaffSiteOrgDto = baseMajorManager.getBaseSiteBySiteId(vehicleTaskReq.getCurrentOperate().getSiteCode());
            if (ObjectHelper.isEmpty(baseStaffSiteOrgDto) || ObjectHelper.isEmpty(baseStaffSiteOrgDto.getDmsSiteCode())) {
                log.info("getBaseSiteBySiteId未获取到" + vehicleTaskReq.getCurrentOperate().getSiteCode() + "站点信息");
                return null;
            }
        } catch (Exception e) {
            log.error("getBaseSiteBySiteId获取站点信息异常", e);
            return null;
        }

        param.setBeginNodeCode(baseStaffSiteOrgDto.getDmsSiteCode());
        param.setVehicleNumber(vehicleTaskReq.getBarCode());
        List<String> tranWorkCodes = jdiQueryWSManager.listTranWorkCodesByVehicleFuzzy(param);
        if (ObjectHelper.isNotNull(tranWorkCodes) && tranWorkCodes.size() > 0) {
            return tranWorkCodes;
        }
        return null;
    }

    /**
     * 根据发货目的地查发货任务
     * @param result
     * @param queryTaskSendDto
     * @param sendVehicleBizList
     * @param nextRouter
     * @return
     */
    private boolean getSendTaskByDestId(InvokeResult<VehicleTaskResp> result, QueryTaskSendDto queryTaskSendDto,
                                        List<String> sendVehicleBizList, Long nextRouter, boolean queryByRoute) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(queryTaskSendDto.getStartSiteId(), nextRouter);
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findBySiteAndStatus(detailQ, queryTaskSendDto.getVehicleStatuses());
        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            if (queryByRoute) {
                result.hintMessage("没有路由下一站的发货记录!");
            } else {
                result.hintMessage("没有该目的地的发货记录!");
            }
            return false;
        }
        for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {
            sendVehicleBizList.add(detailEntity.getSendVehicleBizId());
        }

        return true;
    }

    /**
     * 根据包裹号查发货任务
     * @param vehicleTaskReq
     * @param result
     * @param queryDetail
     * @return
     */
    private boolean getSendTaskByPackage(TransferVehicleTaskReq vehicleTaskReq, InvokeResult<VehicleTaskResp> result, JyBizTaskSendVehicleDetailEntity queryDetail) {
        if (vehicleTaskReq.getBarCode()==null && vehicleTaskReq.getPackageCode()!=null){
            vehicleTaskReq.setBarCode(vehicleTaskReq.getPackageCode());
        }
        if (WaybillUtil.isPackageCode(vehicleTaskReq.getBarCode()) || BusinessUtil.isBoardCode(vehicleTaskReq.getBarCode())) {
            Long startSiteId = (long) vehicleTaskReq.getCurrentOperate().getSiteCode();
            JySendEntity sendEntity = jySendService.queryByCodeAndSite(new JySendEntity(vehicleTaskReq.getBarCode(), startSiteId));
            if (sendEntity == null) {
                result.hintMessage("没有该包裹的发货记录!");
                return false;
            }

            JyBizTaskSendVehicleDetailEntity searchExistRecord = new JyBizTaskSendVehicleDetailEntity();
            searchExistRecord.setSendVehicleBizId(sendEntity.getSendVehicleBizId());
            searchExistRecord.setStartSiteId(sendEntity.getCreateSiteId());
            searchExistRecord.setEndSiteId(sendEntity.getReceiveSiteId());
            JyBizTaskSendVehicleDetailEntity sendDetail = taskSendVehicleDetailService.findSendDetail(searchExistRecord);
            if (sendDetail != null && JyBizTaskSendDetailStatusEnum.SEALED.getCode().equals(sendDetail.getVehicleStatus())) {
                result.hintMessage("该包裹匹配的发货流向已经封车，不允许转移!");
                return false;
            }

            queryDetail.setSendVehicleBizId(sendEntity.getSendVehicleBizId());
            queryDetail.setEndSiteId(sendEntity.getReceiveSiteId());
        }

        return true;
    }

    private QueryTaskSendDto makeSendTaskQuery(TransferVehicleTaskReq vehicleTaskReq) {
        QueryTaskSendDto queryTaskSendDto = new QueryTaskSendDto();
        queryTaskSendDto.setPageNumber(vehicleTaskReq.getPageNumber());
        queryTaskSendDto.setPageSize(vehicleTaskReq.getPageSize());
        queryTaskSendDto.setVehicleStatuses(JyBizTaskSendStatusEnum.UN_SEALED_STATUS);
        queryTaskSendDto.setStartSiteId((long) vehicleTaskReq.getCurrentOperate().getSiteCode());
        return queryTaskSendDto;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.sendScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<SendScanResponse> sendScan(SendScanRequest request) {

        logInfo("拣运发货扫描:{}", JsonHelper.toJson(request));

        JdVerifyResponse<SendScanResponse> result = new JdVerifyResponse<>();
        result.toSuccess();

        // 基础校验
        if (!sendRequestBaseCheck(result, request)) {
            return result;
        }
        JyBizTaskSendVehicleEntity taskSend = taskSendVehicleService.findByBizId(request.getSendVehicleBizId());
        if (taskSend == null) {
            result.toFail("发货任务不存在！");
            return result;
        }
        if (taskSend.manualCreatedTask() && taskSend.noTaskBindVehicle()) {
            result.toFail(NO_SCAN_AFTER_BIND_TASK_MESSAGE);
            return result;
        }
        // 业务场景校验
        if (!sendRequestBizCheck(result, request, taskSend)) {
            return result;
        }

        String barCode = request.getBarCode();
        SendKeyTypeEnum sendType = getSendType(barCode);
        // 获取本次扫描匹配的发货目的地
        List<JyBizTaskSendVehicleDetailEntity> taskSendDetails = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
        Set<Long> allDestId = new HashSet<>();
        for (JyBizTaskSendVehicleDetailEntity sendDetail : taskSendDetails) {
            allDestId.add(sendDetail.getEndSiteId());
        }
        boolean singleDestFlag = true;
        if (allDestId.size() > 1) {
            singleDestFlag = false;
        }
        // 根据发货流向匹配出来的发货目的地
        SendFindDestInfoDto sendFindDestInfoDto = this.matchSendDest(request, sendType, taskSend, allDestId, result);
        logInfo("拣运发货匹配的目的地为: {}-{}-{}-{}", request.getBarCode(), taskSend.getStartSiteId(), sendFindDestInfoDto.getMatchSendDestId(), sendFindDestInfoDto.getRouterNextSiteId());
        if (result.getCode() != JdVerifyResponse.CODE_SUCCESS) {
            return result;
        }
        if (sendFindDestInfoDto.getMatchSendDestId() == null && !NumberHelper.gt0(request.getConfirmSendDestId())) {
            if (singleDestFlag) {
                if (sendFindDestInfoDto.getRouterNextSiteId() != null) {
                    sendFindDestInfoDto.setMatchSendDestId(new ArrayList<>(allDestId).get(0));
                } else {
                    result.setCode(SendScanResponse.CODE_CONFIRM_DEST);
                    result.addWarningBox(0, "未匹配到发货下一站，请手动选择！");
                    return result;
                }
            } else {
                result.setCode(SendScanResponse.CODE_CONFIRM_DEST);
                result.addWarningBox(0, "未匹配到发货下一站，请手动选择！");
                return result;
            }
        }
        // 实际发货目的地
        Long sendDestId = sendFindDestInfoDto.getMatchSendDestId();
        if (NumberHelper.gt0(request.getConfirmSendDestId())) {
            sendDestId = request.getConfirmSendDestId();
        }
        String detailBizId = request.getSendVehicleDetailBizId();

        try {
            JyBizTaskSendVehicleDetailEntity curSendDetail = null;
            if (ObjectHelper.isNotNull(detailBizId)) {
                curSendDetail = pickUpOneDetailByBizId(taskSendDetails, detailBizId);
            } else {
                curSendDetail = pickUpOneUnSealedDetail(taskSendDetails, sendDestId);
            }

            if (curSendDetail == null) {
                result.toBizError();
                result.addInterceptBox(0, "该发货流向已封车！");
                return result;
            }
            if (curSendDetail != null && SendTaskExcepLabelEnum.CANCEL.getCode().equals(curSendDetail.getExcepLabel())) {
                result.toFail("该流向已被取消，请勿继续扫描，已扫货物请迁移！");
                return result;
            }
            SendResult sendResult = new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
            String sendCode = this.getOrCreateSendCode(request, curSendDetail);
            SendM sendM = toSendMDomain(request, curSendDetail.getEndSiteId(), sendCode);
            sendM.setBoxCode(barCode);

            // 发货状态校验
            if (!doSendStatusVerify(result, sendType, sendResult, sendM)) {
                return result;
            }

            // 执行发货前前置校验逻辑
            if (!execSendInterceptChain(request, result, sendType, sendResult, sendM, sendFindDestInfoDto)) {
                return result;
            }

            if (Boolean.TRUE.equals(request.getForceSubmit())) {
                JySendEntity sendEntity = this.createJySendRecord(request, curSendDetail.getEndSiteId(), sendCode, barCode);
                sendEntity.setForceSendFlag(1);
                jySendService.save(sendEntity);
            } else {
                JySendEntity sendEntity = this.createJySendRecord(request, curSendDetail.getEndSiteId(), sendCode, barCode);
                sendEntity.setForceSendFlag(0);
                jySendService.save(sendEntity);
            }

            // 绑定集包袋逻辑
            if (StringUtils.isNotBlank(request.getMaterialCode()) && BusinessHelper.isBoxcode(barCode)) {
                BoxMaterialRelationRequest req = getBoxMaterialRelationRequest(request, barCode);
                InvokeResult bindMaterialRet = cycleBoxService.boxMaterialRelationAlter(req);
                if (!bindMaterialRet.codeSuccess()) {
                    result.toFail("绑定失败：" + bindMaterialRet.getMessage());
                    return result;
                }
            }

            // 执行一车一单发货逻辑
            sendResult = this.execPackageSend(sendType, sendResult, sendM);
            if (!sendResultToJdResp(result, sendResult)) {
                return result;
            }

            // 包裹首次扫描逻辑
            boolean firstScanFlag = this.dealTaskFirstScan(request, taskSend, curSendDetail);

            SendScanResponse sendScanResponse = new SendScanResponse();
            result.setData(sendScanResponse);
            sendScanResponse.setSendCode(sendCode);
            sendScanResponse.setFirstScan(firstScanFlag);
            sendScanResponse.setSendDetailBizId(curSendDetail.getBizId());
            sendScanResponse.setCreateTime(curSendDetail.getCreateTime());
            sendScanResponse.setScanPackCount(this.calculateScanPackageCount(request, sendType));
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(curSendDetail.getEndSiteId().intValue());
            sendScanResponse.setCurScanDestId(curSendDetail.getEndSiteId());
            sendScanResponse.setCurScanDestName(baseSite.getSiteName());
        } catch (Exception ex) {
            log.error("发货任务扫描失败. {}", JsonHelper.toJson(request), ex);
            result.toError("服务器异常，发货任务扫描失败，请咚咚联系分拣小秘！");
        }

        return result;
    }

    @Override
    public JyBizTaskSendVehicleDetailEntity pickUpOneUnSealedDetail(List<JyBizTaskSendVehicleDetailEntity> taskSendDetails, Long sendDestId) {
        List<JyBizTaskSendVehicleDetailEntity> sameDirections = new ArrayList<>();
        for (JyBizTaskSendVehicleDetailEntity sendDetail : taskSendDetails) {
            if (sendDetail.getEndSiteId().equals(sendDestId)) {
                sameDirections.add(sendDetail);
            }
        }
        if (sameDirections.size() > 0) {
            //1.过滤已封车的流向
            Iterator it = sameDirections.iterator();
            while (it.hasNext()) {
                JyBizTaskSendVehicleDetailEntity detail = (JyBizTaskSendVehicleDetailEntity) it.next();
                if (checkIfSealed(detail)) {
                    it.remove();
                }
            }
            //2.按时间倒排（过滤完封车状态后仍然有多个同流向的话 就选择最晚那个）
            if (sameDirections.size() > 1) {
                Collections.sort(sameDirections, new JyBizTaskSendVehicleDetailEntity.DetailComparatorByTime());
            }
        }
        return sameDirections.size() > 0 ? sameDirections.get(0) : null;
    }

    @Override
    public boolean checkIfSealed(JyBizTaskSendVehicleDetailEntity detail) {
        if (JyBizTaskSendDetailStatusEnum.SEALED.getCode().equals(detail.getVehicleStatus())) {
            return true;
        }
        String originSendCode = jySendCodeService.findEarliestSendCode(detail.getBizId());
        if (ObjectHelper.isNotNull(originSendCode) && newSealVehicleService.newCheckSendCodeSealed(originSendCode, null)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkIfSealedByAllSendCode(JyBizTaskSendVehicleDetailEntity detail) {
        if (JyBizTaskSendDetailStatusEnum.SEALED.getCode().equals(detail.getVehicleStatus())) {
            return true;
        }
        List<String> sendCodes = jySendCodeService.querySendCodesByVehicleDetailBizId(detail.getBizId());
        for (String sendCode : sendCodes) {
            if (sendDetailService.checkSendIsExist(sendCode) && !newSealVehicleService.newCheckSendCodeSealed(sendCode, null)) {
                return false;
            }
        }
        return true;
    }

    private JyBizTaskSendVehicleDetailEntity pickUpOneDetailByBizId(
            List<JyBizTaskSendVehicleDetailEntity> taskSendDetails, String detailBizId) {
        for (JyBizTaskSendVehicleDetailEntity sendDetail : taskSendDetails) {
            if (sendDetail.getBizId().equals(detailBizId) && !checkIfSealed(sendDetail)) {
                return sendDetail;
            }
        }
        return null;
    }

    private ValidateIgnore convertValidateIgnore(com.jd.bluedragon.common.dto.operation.workbench.send.request.ValidateIgnore validateIgnoreSource) {
        ValidateIgnore validateIgnoreTarget = new ValidateIgnore();
        final com.jd.bluedragon.distribution.jsf.domain.ValidateIgnoreRouterCondition validateIgnoreRouterCondition = new com.jd.bluedragon.distribution.jsf.domain.ValidateIgnoreRouterCondition();
        BeanCopyUtil.copy(validateIgnoreSource.getValidateIgnoreRouterCondition(), validateIgnoreRouterCondition);
        validateIgnoreTarget.setValidateIgnoreRouterCondition(validateIgnoreRouterCondition);
        return validateIgnoreTarget;
    }

    /**
     * 执行发货拦截链
     * @param request
     * @param result
     * @param sendType
     * @param sendResult
     * @param sendM
     * @return
     */
    private boolean execSendInterceptChain(SendScanRequest request, JdVerifyResponse<SendScanResponse> result, SendKeyTypeEnum sendType, SendResult sendResult, SendM sendM, SendFindDestInfoDto sendFindDestInfoDto) {
        if (Boolean.FALSE.equals(request.getForceSubmit())) {
            if (!BusinessHelper.isBoxcode(request.getBarCode())) {
                SortingCheck sortingCheck = deliveryService.getSortingCheck(sendM);
                sortingCheck.setBoard(sendFindDestInfoDto.getBoard());
                if (request.getValidateIgnore() != null) {
                    sortingCheck.setValidateIgnore(this.convertValidateIgnore(request.getValidateIgnore()));
                }
                FilterChain filterChain = sortingCheckService.matchJyDeliveryFilterChain(sendType);
                SortingJsfResponse chainResp = sortingCheckService.doSingleSendCheckWithChain(sortingCheck, true, filterChain);
                if (!chainResp.getCode().equals(JdResponse.CODE_OK)) {
                    if (JdResponse.CODE_SERVICE_ERROR.equals(chainResp.getCode())) {
                        result.toBizError();
                        result.addInterceptBox(chainResp.getCode(), chainResp.getMessage());
                        return false;
                    } else if (chainResp.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM) {
                        result.toBizError();
                        if (Objects.equals(chainResp.getCode(), SortingResponse.CODE_CROUTER_ERROR)) {
                            final JdVerifyResponse.MsgBox msgBox = new JdVerifyResponse.MsgBox(MsgBoxTypeEnum.CONFIRM, chainResp.getCode(), chainResp.getMessage());
                            final RouterValidateData routerValidateData = new RouterValidateData();
                            routerValidateData.setRouterNextSiteId(sendFindDestInfoDto.getRouterNextSiteId());
                            msgBox.setData(routerValidateData);
                            result.addBox(msgBox);
                        } else {
                            result.addConfirmBox(chainResp.getCode(), chainResp.getMessage());
                        }
                        return false;
                    } else {
                        // 拦截时保存拦截记录
                        JySendEntity sendEntity = this.createJySendRecord(request, sendM.getReceiveSiteCode(), sendM.getSendCode(), request.getBarCode());
                        sendEntity.setForceSendFlag(0);
                        sendEntity.setInterceptFlag(1);
                        jySendService.save(sendEntity);
                        result.toBizError();
                        result.addInterceptBox(chainResp.getCode(), chainResp.getMessage());
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean doSendStatusVerify(JdVerifyResponse<SendScanResponse> result, SendKeyTypeEnum sendType, SendResult sendResult, SendM sendM) {
        if (SendKeyTypeEnum.BY_PACKAGE.equals(sendType) && deliveryService.isSendByWaybillProcessing(sendM)) {
            result.toBizError();
            result.addInterceptBox(0, HintService.getHint(HintCodeConstants.SEND_BY_WAYBILL_PROCESSING));
            return false;
        }

        // 校验是否已经发货
        deliveryService.multiSendVerification(sendM, sendResult);
        if (Objects.equals(sendResult.getKey(), SendResult.CODE_SENDED)) {
            result.toBizError();
            result.addInterceptBox(sendResult.getKey(), sendResult.getValue());
            return false;
        }

        // 校验发货批次号状态
        StringBuffer customMsg = new StringBuffer().append(HintService.getHint(HintCodeConstants.SEND_CODE_SEALED_TIPS_SECOND));
        if (newSealVehicleService.newCheckSendCodeSealed(sendM.getSendCode(), customMsg)) {
            result.toBizError();
            result.addInterceptBox(0, customMsg.toString());
            return false;
        }

        return true;
    }

    private SendResult execPackageSend(SendKeyTypeEnum sendType, SendResult sendResult, SendM sendM) {
        boolean oldForceSend = true; // 跳过原有拦截校验，使用新校验逻辑
        boolean cancelLastSend = ConfirmMsgBox.CODE_CONFIRM_CANCEL_LAST_SEND.equals(sendResult.getInterceptCode());
        switch (sendType) {
            case BY_WAYBILL:
                deliveryService.packageSendByRealWaybill(sendM, cancelLastSend, sendResult);
                break;
            case BY_PACKAGE:
                sendResult = deliveryService.packageSend(SendBizSourceEnum.JY_APP_SEND, sendM, oldForceSend, cancelLastSend);
                break;
            case BY_BOX:
                sendResult = deliveryService.packageSend(SendBizSourceEnum.JY_APP_SEND, sendM, oldForceSend, cancelLastSend);
                break;
            case BY_BOARD:
                if (sendM.getBoardCode() == null) {
                    sendM.setBoardCode(sendM.getBoxCode());
                }
                sendResult = deliveryService.boardSend(sendM, oldForceSend);
                break;
        }

        return sendResult;
    }

    private boolean dealTaskFirstScan(SendScanRequest request, JyBizTaskSendVehicleEntity taskSend, JyBizTaskSendVehicleDetailEntity curSendDetail) {
        boolean firstScanFlag = false;
        // 发货流向首次扫描
        if (taskSendDestFirstScan(request, curSendDetail.getBizId())) {
            logInfo("发货任务流向[{}-{}]首次扫描, 任务状态变为“发货中”. {}", request.getSendVehicleBizId(), curSendDetail.getEndSiteId(),
                    JsonHelper.toJson(request));
            firstScanFlag = true;
            updateSendVehicleStatus(request, taskSend, curSendDetail);
            if (ObjectHelper.isNotNull(request.getTaskName())) {
                JyBizTaskSendVehicleEntity sendVehicleTask = new JyBizTaskSendVehicleEntity();
                //sendVehicleTask.setId(taskSend.getId());
                sendVehicleTask.setBizId(taskSend.getBizId());
                sendVehicleTask.setTaskName(request.getTaskName());
                sendVehicleTask.setUpdateTime(new Date());
                sendVehicleTask.setUpdateUserErp(request.getUser().getUserErp());
                sendVehicleTask.setUpdateUserName(request.getUser().getUserName());
                taskSendVehicleService.updateSendVehicleTask(sendVehicleTask);
            }
        }

        // 发货任务首次扫描记录组员信息
        if (taskSendFirstScan(request)) {
            logInfo("发货任务[{}]首次扫描, 任务状态变为“发货中”. {}", request.getSendVehicleBizId(), JsonHelper.toJson(request));

            distributeAndStartScheduleTask(request);

            recordTaskMembers(request);
        }
        return firstScanFlag;
    }

    private BoxMaterialRelationRequest getBoxMaterialRelationRequest(SendScanRequest request, String barCode) {
        BoxMaterialRelationRequest req = new BoxMaterialRelationRequest();
        req.setUserCode(request.getUser().getUserCode());
        req.setUserName(request.getUser().getUserName());
        req.setOperatorERP(request.getUser().getUserErp());
        req.setSiteCode(request.getCurrentOperate().getSiteCode());
        req.setSiteName(request.getCurrentOperate().getSiteName());
        req.setBoxCode(barCode);
        req.setMaterialCode(request.getMaterialCode());
        req.setBindFlag(Constants.CONSTANT_NUMBER_ONE); // 绑定
        return req;
    }

    private void updateSendVehicleStatus(SendScanRequest request, JyBizTaskSendVehicleEntity taskSend, JyBizTaskSendVehicleDetailEntity curSendDetail) {
        taskSend.setUpdateTime(new Date());
        taskSend.setUpdateUserErp(request.getUser().getUserErp());
        taskSend.setUpdateUserName(request.getUser().getUserName());
        curSendDetail.setUpdateTime(taskSend.getUpdateTime());
        curSendDetail.setUpdateUserErp(taskSend.getUpdateUserErp());
        curSendDetail.setUpdateUserName(taskSend.getUpdateUserName());

        sendVehicleTransactionManager.updateTaskStatus(taskSend, curSendDetail, JyBizTaskSendDetailStatusEnum.SENDING);
    }

    private void recordTaskMembers(SendScanRequest request) {
        JyTaskGroupMemberEntity startData = new JyTaskGroupMemberEntity();
        startData.setRefGroupCode(request.getGroupCode());
        startData.setRefTaskId(getJyScheduleTaskId(request.getSendVehicleBizId()));

        startData.setSiteCode(request.getCurrentOperate().getSiteCode());
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(startData.getSiteCode());
        startData.setOrgCode(baseSite != null ? baseSite.getOrgId() : -1);

        startData.setCreateUser(request.getUser().getUserErp());
        startData.setCreateUserName(request.getUser().getUserName());

        Result<Boolean> startTask = taskGroupMemberService.startTask(startData);

        logInfo("发货任务[{}-{}]首次扫描记录组员.", request.getSendVehicleBizId(), request.getVehicleNumber());
        logInfo("卸车任务首次扫描记录组员. {}, {}", request.getSendVehicleBizId(), JsonHelper.toJson(startTask));
    }

    /**
     * 分配发货调度任务
     * @param request
     * @return
     */
    private boolean distributeAndStartScheduleTask(SendScanRequest request) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(request.getSendVehicleBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.SEND.getCode());
        req.setDistributionType(JyScheduleTaskDistributionTypeEnum.GROUP.getCode());
        req.setDistributionTarget(request.getGroupCode());
        req.setDistributionTime(request.getCurrentOperate().getOperateTime());
        req.setOpeUser(request.getUser().getUserErp());
        req.setOpeUserName(request.getUser().getUserName());
        req.setOpeTime(req.getDistributionTime());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.distributeAndStartScheduleTask(req);
        return jyScheduleTaskResp != null;
    }

    private SendKeyTypeEnum getSendType(String barCode) {
        SendKeyTypeEnum sendType = null;
        if (WaybillUtil.isWaybillCode(barCode)) {
            sendType = SendKeyTypeEnum.BY_WAYBILL;
        } else if (BusinessUtil.isBoardCode(barCode)) {
            // TODO 支持扫描板号
            sendType = SendKeyTypeEnum.BY_BOARD;
        } else if (WaybillUtil.isPackageCode(barCode)) {
            sendType = SendKeyTypeEnum.BY_PACKAGE;
        } else if (BusinessHelper.isBoxcode(barCode)) {
            sendType = SendKeyTypeEnum.BY_BOX;
        }
        return sendType;
    }

    /**
     * 根据发货流向查询批次
     * @param request
     * @param detail
     * @return
     */
    private String getOrCreateSendCode(SendScanRequest request, JyBizTaskSendVehicleDetailEntity detail) {
        // 非同流向迁移会生成新批次，一个流向不止一个批次
        String curDestSendCode = jySendCodeService.findEarliestSendCode(detail.getBizId());

        String sendCode;
        if (StringUtils.isBlank(curDestSendCode)) {
            Profiler.businessAlarm("dms.web.JySendVehicleService.getOrCreateSendCode", "[拣运APP]发货匹配发货批次失败，将新建批次！");
            logWarn("发货流向获取批次号为空! {}", detail.getBizId());
            // 首次扫描生成批次
            sendCode = generateSendCode((long) request.getCurrentOperate().getSiteCode(), detail.getEndSiteId(), request.getUser().getUserErp());

            this.saveSendCode(request, sendCode, detail.getBizId());
        } else {
            sendCode = curDestSendCode;
        }

        return sendCode;
    }

    /**
     * 根据发货流向匹配发货的目的地
     * <ul>
     *     <li>运单、包裹：根据路由匹配</li>
     *     <li>箱号先根据目的地匹配，未匹配再出来再从箱里取三单根据路由匹配</li>
     *     <li>匹配出来的目的地不在发货流向里，需要用户确认</li>
     * </ul>
     * @param request
     * @param sendType
     * @param taskSend
     * @param allDestId
     * @return
     */
    private SendFindDestInfoDto matchSendDest(SendScanRequest request, SendKeyTypeEnum sendType,
                                              JyBizTaskSendVehicleEntity taskSend, Set<Long> allDestId, JdVerifyResponse<SendScanResponse> response) {
        String barCode = request.getBarCode();
        long siteCode = request.getCurrentOperate().getSiteCode();
        // 根据发货流向匹配出来的发货目的地
        Long destSiteId = null;
        final SendFindDestInfoDto sendFindDestInfoDto = new SendFindDestInfoDto();

        switch (sendType) {
            case BY_WAYBILL:
                Long matchDestId = getWaybillNextRouterWithTransferConfig(barCode, request.getCurrentOperate().getSiteCode(),taskSend.manualCreatedTask(), response);
                if (allDestId.contains(matchDestId)) {
                    destSiteId = matchDestId;
                }
                sendFindDestInfoDto.setRouterNextSiteId(matchDestId);
                break;
            case BY_PACKAGE:
                Long matchDestIdByPack = getWaybillNextRouterWithTransferConfig(WaybillUtil.getWaybillCode(barCode), request.getCurrentOperate().getSiteCode(),taskSend.manualCreatedTask(), response);
                if (allDestId.contains(matchDestIdByPack)) {
                    destSiteId = matchDestIdByPack;
                }
                sendFindDestInfoDto.setRouterNextSiteId(matchDestIdByPack);
                break;
            case BY_BOX:
                // 先根据箱号目的地取，再从箱号里取三个运单，根据路由匹配发货流向，需要弹窗提示 fixme 解封版后使用抽出的子方法 getBoxMatchDestId
                Box box = boxService.findBoxByCode(barCode);
                if (box != null) {
                    if (allDestId.contains(box.getReceiveSiteCode().longValue())) {
                        destSiteId = box.getReceiveSiteCode().longValue();
                    } else {
                        List<String> waybillCodes = deliveryService.getWaybillCodesByBoxCodeAndFetchNum(barCode, 3);
                        // 获取运单对应的路由
                        String routerStr = null;
                        String waybillForVerify = null;
                        Long boxRouteDest = null;
                        if (CollectionUtils.isNotEmpty(waybillCodes)) {
                            for (String waybillCode : waybillCodes) {
                                routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
                                if (StringHelper.isNotEmpty(routerStr)) {
                                    waybillForVerify = waybillCode;
                                    boxRouteDest = getRouteNextSite(taskSend.getStartSiteId(), routerStr);
                                    break;
                                }
                            }
                            if (StringUtils.isBlank(routerStr)) {
                                logWarn("拣运发货根据箱号未获取到路由. 箱号{}, 取到的运单为{}, 操作站点为{}.",
                                        barCode, waybillCodes, taskSend.getStartSiteId());
                            }
                        }
                        if (boxRouteDest != null) {
                            logInfo("拣运发货根据箱号匹配路由【成功】, 箱号{}, 取到的运单为{}," +
                                            " 进行检验的运单为{}, 运单的路由为{}, 操作站点为{}.",
                                    barCode, waybillCodes, waybillForVerify, routerStr, taskSend.getStartSiteId());
                        } else {
                            logWarn("拣运发货根据箱号匹配路由【失败】, 箱号{}, 取到的运单为{}," +
                                            " 进行检验的运单为{}, 运单的路由为{}, 操作站点为{}.",
                                    barCode, waybillCodes, waybillForVerify, routerStr, taskSend.getStartSiteId());
                        }
                        if (allDestId.contains(boxRouteDest)) {
                            destSiteId = boxRouteDest;
                        }
                    }
                }
                break;
            case BY_BOARD:
                Long matchDestIdByBoard = null;
                Response<Board> boardResponse = groupBoardManager.getBoard(barCode);
                if (boardResponse.getCode() == ResponseEnum.SUCCESS.getIndex() && boardResponse.getData() != null) {
                    Board board = boardResponse.getData();
                    com.jd.bluedragon.distribution.board.domain.Board sortingBoard = new com.jd.bluedragon.distribution.board.domain.Board();
                    BeanUtils.copyProperties(board, sortingBoard);
                    sendFindDestInfoDto.setBoard(sortingBoard);
                    if (board.getDestinationId() != null) {
                        matchDestIdByBoard = board.getDestinationId().longValue();
                    }
                }
                if (allDestId.contains(matchDestIdByBoard)) {
                    destSiteId = matchDestIdByBoard;
                }
                sendFindDestInfoDto.setRouterNextSiteId(matchDestIdByBoard);
                break;
        }

        sendFindDestInfoDto.setMatchSendDestId(destSiteId);
        return sendFindDestInfoDto;
    }

    /**
     * 获取箱号路由
     * @param barCode 箱号
     * @param startSiteId 起始目的地
     * @param allDestId 指定范围的匹配目的地，如果此值传空对象，则会返回获取到的路由目的地
     * @return 匹配的箱号目的地
     * @author fanggang7
     * @time 2022-10-21 16:40:19 周五
     */
    private Long getBoxMatchDestId(String barCode, Long startSiteId, Set<Long> allDestId) {
        // 先根据箱号目的地取，再从箱号里取三个运单，根据路由匹配发货流向，需要弹窗提示
        Long destSiteId = null;
        Box box = boxService.findBoxByCode(barCode);
        if (box != null) {
            if (CollectionUtils.isNotEmpty(allDestId) && allDestId.contains(box.getReceiveSiteCode().longValue())) {
                destSiteId = box.getReceiveSiteCode().longValue();
            } else {
                List<String> waybillCodes = deliveryService.getWaybillCodesByBoxCodeAndFetchNum(barCode, 3);
                // 获取运单对应的路由
                String routerStr = null;
                String waybillForVerify = null;
                Long boxRouteDest = null;
                if (CollectionUtils.isNotEmpty(waybillCodes)) {
                    for (String waybillCode : waybillCodes) {
                        routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
                        if (StringHelper.isNotEmpty(routerStr)) {
                            waybillForVerify = waybillCode;
                            boxRouteDest = getRouteNextSite(startSiteId, routerStr);
                            break;
                        }
                    }
                    if (StringUtils.isBlank(routerStr)) {
                        logWarn("拣运发货根据箱号未获取到路由. 箱号{}, 取到的运单为{}, 操作站点为{}.", barCode, waybillCodes, startSiteId);
                    }
                }
                if (boxRouteDest != null) {
                    logInfo("拣运发货根据箱号匹配路由【成功】, 箱号{}, 取到的运单为{}," +
                                    " 进行检验的运单为{}, 运单的路由为{}, 操作站点为{}.", barCode, waybillCodes, waybillForVerify, routerStr, startSiteId);
                } else {
                    logWarn("拣运发货根据箱号匹配路由【失败】, 箱号{}, 取到的运单为{}," +
                                    " 进行检验的运单为{}, 运单的路由为{}, 操作站点为{}.", barCode, waybillCodes, waybillForVerify, routerStr, startSiteId);
                }
                if (CollectionUtils.isEmpty(allDestId) || allDestId.contains(boxRouteDest)) {
                    destSiteId = boxRouteDest;
                }
            }
        }
        return destSiteId;
    }

    private JySendEntity createJySendRecord(SendScanRequest request, long destSiteId, String sendCode, String barCode) {
        JySendEntity sendEntity = new JySendEntity();

        sendEntity.setSendVehicleBizId(request.getSendVehicleBizId());
        sendEntity.setCreateSiteId((long) request.getCurrentOperate().getSiteCode());
        sendEntity.setReceiveSiteId(destSiteId);
        sendEntity.setBarCode(barCode);
        sendEntity.setSendCode(sendCode);
        sendEntity.setOperateTime(request.getCurrentOperate().getOperateTime());
        sendEntity.setCreateUserErp(request.getUser().getUserErp());
        sendEntity.setCreateUserName(request.getUser().getUserName());
        sendEntity.setCreateTime(new Date());
        sendEntity.setUpdateUserErp(request.getUser().getUserErp());
        sendEntity.setUpdateUserName(request.getUser().getUserName());
        sendEntity.setUpdateTime(new Date());

        return sendEntity;
    }

    /**
     * 统计本次扫描的包裹数量
     *
     * @param request
     * @param sendType
     */
    private Integer calculateScanPackageCount(SendScanRequest request, SendKeyTypeEnum sendType) {
        String barCode = request.getBarCode();
        Integer scanCount = 0;
        switch (sendType) {
            case BY_PACKAGE:
                scanCount = 1;
                break;
            case BY_WAYBILL:
                Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(barCode);
                if (waybill != null && NumberHelper.gt0(waybill.getGoodNumber())) {
                    scanCount = waybill.getGoodNumber();
                }
                break;
            case BY_BOX:
                List<SendDetail> list = deliveryService.getCancelSendByBox(barCode);
                if (CollectionUtils.isNotEmpty(list)) {
                    scanCount = list.size();
                }
                break;
            case BY_BOARD:
                Response<List<String>> tcResponse = groupBoardManager.getBoxesByBoardCode(barCode);
                if (ResponseEnum.SUCCESS.getIndex() == tcResponse.getCode() && CollectionUtils.isNotEmpty(tcResponse.getData())) {
                    scanCount = getPackageNumFromPackOrBoxCodes(tcResponse.getData(), request.getCurrentOperate().getSiteCode());
                }
                break;
        }

        return scanCount;
    }

    private int getPackageNumFromPackOrBoxCodes(List<String> packOrBoxCodes, Integer siteCode) {
        int count = 0;
        for (String code : packOrBoxCodes) {
            if (BusinessUtil.isBoxcode(code)) {
                log.info("=====getPackageNumFromPackOrBoxCodes=======根据箱号获取集包包裹 {}", code);
                List<String> pCodes = getPackageCodesByBoxCodeOrSendCode(code, siteCode);
                if (pCodes != null && pCodes.size() > 0) {
                    count = count + pCodes.size();
                }
            } else {
                count = count + 1;
            }
        }
        return count;
    }

    private List<String> getPackageCodesByBoxCodeOrSendCode(String boxOrSendCode, Integer siteCode) {
        //构建查询sendDetail的查询参数
        SendDetailDto sendDetail = initSendDetail(boxOrSendCode, siteCode);
        if (BusinessUtil.isBoxcode(boxOrSendCode)) {
            return sendDetailService.queryPackageCodeByboxCode(sendDetail);
        }
        return sendDetailService.queryPackageCodeBySendCode(sendDetail);
    }

    private SendDetailDto initSendDetail(String barcode, int createSiteCode) {
        SendDetailDto sendDetail = new SendDetailDto();
        sendDetail.setIsCancel(0);
        sendDetail.setCreateSiteCode(createSiteCode);
        if (BusinessUtil.isBoxcode(barcode)) {
            sendDetail.setBoxCode(barcode);
        }
        if (BusinessUtil.isSendCode(barcode)) {
            sendDetail.setSendCode(barcode);
        }
        return sendDetail;
    }

    /**
     * 判断是否是发车任务流向的第一次扫描
     * @param request
     * @param sendDetailBizId
     * @return
     */
    private boolean taskSendDestFirstScan(SendScanRequest request, String sendDetailBizId) {
        boolean firstScanned = false;
        String mutexKey = getSendDetailBizCacheKey(request.getSendVehicleBizId(), sendDetailBizId);
        if (redisClientOfJy.set(mutexKey, request.getBarCode(), SEND_SCAN_BAR_EXPIRE, TimeUnit.HOURS, false)) {

            firstScanned = true;

            logInfo("单号是发车任务流向的首次扫描. [{}], {}", mutexKey, JsonHelper.toJson(request));
        }

        return firstScanned;
    }

    /**
     * 判断是否是发车任务的第一次扫描
     * @param request
     * @return
     */
    private boolean taskSendFirstScan(SendScanRequest request) {
        boolean firstScanned = false;
        String mutexKey = getSendTaskBizCacheKey(request.getSendVehicleBizId());
        if (redisClientOfJy.set(mutexKey, request.getBarCode(), SEND_SCAN_BAR_EXPIRE, TimeUnit.HOURS, false)) {
            firstScanned = true;
            logInfo("单号是发车任务的首次扫描. [{}], {}", mutexKey, JsonHelper.toJson(request));
        }

        return firstScanned;
    }

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    private void logWarn(String message, Object... objects) {
        if (log.isWarnEnabled()) {
            log.warn(message, objects);
        }
    }

    /**
     * @param result
     * @param sendResult
     * @return
     */
    private Boolean sendResultToJdResp(JdVerifyResponse<SendScanResponse> result, SendResult sendResult) {
        if (Objects.equals(sendResult.getKey(), SendResult.CODE_WARN)) {
            result.toBizError();
            result.addWarningBox(sendResult.getKey(), sendResult.getValue());
            return false;
        }

        if (Objects.equals(sendResult.getKey(), SendResult.CODE_SENDED)) {
            result.toBizError();
            result.addInterceptBox(sendResult.getKey(), sendResult.getValue());
            return false;
        }

        if (Objects.equals(sendResult.getKey(), SendResult.CODE_CONFIRM)) {
            result.toBizError();
            result.addConfirmBox(sendResult.getKey(), sendResult.getValue());
            return false;
        }

        return true;
    }


    /**
     * 请求拼装SendM发货对象
     *
     * @param request
     * @return
     */
    private SendM toSendMDomain(SendScanRequest request, long destSiteId, String sendCode) {
        SendM domain = new SendM();
        domain.setReceiveSiteCode((int) destSiteId);
        domain.setSendCode(sendCode);
        domain.setCreateSiteCode(request.getCurrentOperate().getSiteCode());

        domain.setCreateUser(request.getUser().getUserName());
        domain.setCreateUserCode(request.getUser().getUserCode());
        domain.setSendType(DmsConstants.BUSSINESS_TYPE_POSITIVE);
        domain.setBizSource(SendBizSourceEnum.JY_APP_SEND.getCode());
        domain.setYn(1);
        domain.setCreateTime(request.getCurrentOperate().getOperateTime());
        domain.setOperateTime(request.getCurrentOperate().getOperateTime());
        domain.setOperatorTypeCode(request.getCurrentOperate().getOperatorTypeCode());
        domain.setOperatorId(request.getCurrentOperate().getOperatorId());
        return domain;
    }

    private void saveSendCode(SendScanRequest request, String sendCode, String detailBiz) {
        JySendCodeEntity sendCodeEntity = new JySendCodeEntity();
        sendCodeEntity.setSendVehicleBizId(request.getSendVehicleBizId());
        sendCodeEntity.setSendDetailBizId(detailBiz);
        sendCodeEntity.setSendCode(sendCode);
        sendCodeEntity.setCreateUserErp(request.getUser().getUserErp());
        sendCodeEntity.setCreateUserName(request.getUser().getUserName());
        sendCodeEntity.setUpdateUserErp(sendCodeEntity.getCreateUserErp());
        sendCodeEntity.setUpdateUserName(sendCodeEntity.getCreateUserName());
        sendCodeEntity.setSource(0);
        Date now = new Date();
        sendCodeEntity.setCreateTime(now);
        sendCodeEntity.setUpdateTime(now);

        jySendCodeService.add(sendCodeEntity);
    }

    private String generateSendCode(Long startSiteId, Long destSiteId, String createUser) {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(startSiteId));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, String.valueOf(destSiteId));
        return sendCodeService.createSendCode(attributeKeyEnumObjectMap, BusinessCodeFromSourceEnum.JY_APP, createUser);
    }


    /**
     * 发货扫描基础校验，校验只返回fail类型
     * @param response
     * @param request
     * @return
     */
    private boolean sendRequestBaseCheck(JdVerifyResponse<SendScanResponse> response, SendScanRequest request) {
        String barCode = request.getBarCode();
        if (!BusinessHelper.isBoxcode(barCode)
                && !WaybillUtil.isWaybillCode(barCode)
                && !WaybillUtil.isPackageCode(barCode)
                && !BusinessUtil.isBoardCode(barCode)) {
            // FIXME 按功能配置动态校验
            response.toFail("只支持扫描包裹、运单、箱号和板号！");
            return false;
        }

        int siteCode = request.getCurrentOperate().getSiteCode();
        if (!NumberHelper.gt0(siteCode)) {
            response.toFail("缺少操作场地！");
            return false;
        }

        if (StringUtils.isBlank(request.getGroupCode())) {
            response.toFail("扫描前请绑定小组！");
            return false;
        }

        if (StringUtils.isBlank(request.getSendVehicleBizId())) {
            response.toFail("请选择发车任务！");
            return false;
        }

        // 设置默认扫描方式
        if (request.getBarCodeType() == null) {
            request.setBarCodeType(SendVehicleScanTypeEnum.SCAN_ONE.getCode());
        }
        final BarCodeType barCodeType = BusinessUtil.getBarCodeType(request.getBarCode());
        if (barCodeType == null) {
            response.toFail("请扫描正确的条码！");
            return false;
        }
        if (Objects.equals(SendVehicleScanTypeEnum.SCAN_ONE.getCode(), request.getBarCodeType()) &&
                (!Objects.equals(BarCodeType.PACKAGE_CODE.getCode(), barCodeType.getCode()) && !Objects.equals(BarCodeType.BOX_CODE.getCode(), barCodeType.getCode()))) {
            response.toFail("请扫描包裹号或箱号！");
            return false;
        }
        if (Objects.equals(SendVehicleScanTypeEnum.SCAN_WAYBILL.getCode(), request.getBarCodeType())) {
            if (!Objects.equals(BarCodeType.PACKAGE_CODE.getCode(), barCodeType.getCode()) && !Objects.equals(BarCodeType.WAYBILL_CODE.getCode(), barCodeType.getCode())) {
                response.toFail("请扫描包裹号或运单号！");
                return false;
            }
            // @mark 注意此处，按运单号扫描时，如果是扫的包裹号，则将包裹号转成运单号
            request.setBarCode(WaybillUtil.getWaybillCode(request.getBarCode()));
        }

        return true;
    }

    private String genNoTaskBizId() {
        String ownerKey = String.format(JyBizTaskSendVehicleDetailEntity.NO_TASK_BIZ_PREFIX, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyNoTaskSendDetailBizIdSequenceGen.gen(ownerKey));
    }

    /**
     * 发货扫描业务场景校验
     * <h2>客户端弹窗提示类型</h2>
     * <ul>
     *     <li>绑定集包袋：{@link MsgBoxTypeEnum.INTERCEPT}</li>
     *     <li>无任务首次扫描确认目的地：{@link MsgBoxTypeEnum.CONFIRM}</li>
     *     <li>拦截链：{@link MsgBoxTypeEnum.INTERCEPT}</li>
     * </ul>
     * @param response
     * @param request
     * @param taskSend
     * @return
     */
    private boolean sendRequestBizCheck(JdVerifyResponse<SendScanResponse> response, SendScanRequest request,
                                        JyBizTaskSendVehicleEntity taskSend) {
        String barCode = request.getBarCode();
        int siteCode = request.getCurrentOperate().getSiteCode();

        if (taskSend.getStartSiteId().intValue() != siteCode) {
            response.toFail("当前发车任务始发ID与岗位所属单位ID不一致!");
            return false;
        }

        Integer existSendDetail = taskSendVehicleDetailService.countByCondition(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
        if (!NumberHelper.gt0(existSendDetail)) {
            // 无任务发货未确认目的地信息
            if (taskSend.manualCreatedTask()) {
                if (Boolean.FALSE.equals(request.getNoTaskConfirmDest()) || !NumberHelper.gt0(request.getConfirmSendDestId())) {
                    if (!BusinessUtil.isBoxcode(barCode) && !WaybillUtil.isPackageCode(barCode) && !WaybillUtil.isWaybillCode(barCode)) {
                        response.toBizError();
                        response.addInterceptBox(0, "无任务首次扫描只能是运单或包裹或箱号！");
                        return false;
                    }
                    // 无任务首次扫描返回目的地
                    Long matchDest = null;
                    response.setCode(SendScanResponse.CODE_NO_TASK_CONFIRM_DEST);
                    response.addConfirmBox(0, "无任务发货请确认发货流向");
                    if (BusinessUtil.isBoxcode(barCode)) {
                        matchDest = getBoxMatchDestId(barCode, taskSend.getStartSiteId(), new HashSet<Long>());
                    } else {
                        matchDest = getWaybillNextRouter(WaybillUtil.getWaybillCode(barCode), taskSend.getStartSiteId());
                    }
                    if (matchDest == null) {
                        return false;
                    }

                    SendScanResponse sendScanResponse = new SendScanResponse();
                    response.setData(sendScanResponse);
                    BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(matchDest.intValue());
                    sendScanResponse.setCurScanDestId(matchDest);
                    sendScanResponse.setCurScanDestName(baseSite.getSiteName());

                    return false;
                } else {
                    // 客户端确认流向后保存无任务的发货流向 fixme 等发货成功后才可记录已确认流向？
                    JyBizTaskSendVehicleDetailEntity noTaskDetail = makeNoTaskSendDetail(request, taskSend);
                    logInfo("初始化无任务发货明细. {}", JsonHelper.toJson(noTaskDetail));
                    transactionManager.saveTaskSendAndDetail(null, noTaskDetail);

                    logInfo("启用无任务发货任务. {}", JsonHelper.toJson(taskSend));
                    this.enableNoTask(taskSend);

                    // 保存无任务发货备注
                    //saveNoTaskRemark(request);
                }
            } else {
                response.toBizError();
                response.addInterceptBox(0, "发货流向都已作废！");
                return false;
            }
        }

        // 校验箱号是否绑定集包袋
        if (BusinessHelper.isBoxcode(barCode)) {
            Box box = boxService.findBoxByCode(barCode);
            if (box == null) {
                response.toBizError();
                response.addPromptBox(0, "未查找到对应的箱号数据，请扫描或输入正确的箱号！");
                return false;
            }
            if (BusinessHelper.isBCBoxType(box.getType())) {
                boolean needBindMaterialBag = funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(), siteCode);
                if (needBindMaterialBag) {
                    // 箱号未绑定集包袋
                    if (StringUtils.isBlank(cycleBoxService.getBoxMaterialRelation(barCode))) {
                        if (!BusinessUtil.isCollectionBag(request.getMaterialCode())) {
                            response.setCode(SendScanResponse.CODE_CONFIRM_MATERIAL);
                            response.addInterceptBox(0, "请扫描或输入正确的集包袋！");
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private void saveNoTaskRemark(SendScanRequest request) {
        if (StringUtils.isNotBlank(request.getNoTaskRemark())) {
            JySendAttachmentEntity attachment = new JySendAttachmentEntity();
            attachment.setSendVehicleBizId(request.getSendVehicleBizId());
            attachment.setOperateSiteId((long) request.getCurrentOperate().getSiteCode());
            attachment.setOperateTime(request.getCurrentOperate().getOperateTime());
            attachment.setCreateTime(request.getCurrentOperate().getOperateTime());
            attachment.setCreateUserErp(request.getUser().getUserErp());
            attachment.setCreateUserName(request.getUser().getUserName());
            attachment.setUpdateTime(request.getCurrentOperate().getOperateTime());
            attachment.setUpdateUserErp(attachment.getCreateUserErp());
            attachment.setUpdateUserName(attachment.getCreateUserName());
            attachment.setRemark(request.getNoTaskRemark());
            sendAttachmentService.saveAttachment(attachment);
        }
    }

    /**
     * 启用无任务发货
     * @param taskSend
     */
    private void enableNoTask(JyBizTaskSendVehicleEntity taskSend) {
        JyBizTaskSendVehicleEntity enableNoTask = new JyBizTaskSendVehicleEntity();
        enableNoTask.setBizId(taskSend.getBizId());
        enableNoTask.setYn(Constants.CONSTANT_NUMBER_ONE);
        enableNoTask.setUpdateTime(new Date());
        taskSendVehicleService.updateSendVehicleTask(enableNoTask);
    }

    private JyBizTaskSendVehicleDetailEntity makeNoTaskSendDetail(SendScanRequest request, JyBizTaskSendVehicleEntity taskSend) {
        JyBizTaskSendVehicleDetailEntity noTaskDetail = new JyBizTaskSendVehicleDetailEntity();
        noTaskDetail.setSendVehicleBizId(taskSend.getBizId());
        noTaskDetail.setBizId(genNoTaskBizId());
        noTaskDetail.setTransWorkItemCode(StringUtils.EMPTY);
        noTaskDetail.setVehicleStatus(JyBizTaskSendDetailStatusEnum.TO_SEND.getCode());
        noTaskDetail.setStartSiteId(taskSend.getStartSiteId());

        BaseStaffSiteOrgDto startSite = baseMajorManager.getBaseSiteBySiteId(taskSend.getStartSiteId().intValue());
        noTaskDetail.setStartSiteName(startSite == null ? StringUtils.EMPTY : startSite.getSiteName());
        noTaskDetail.setEndSiteId(request.getConfirmSendDestId());
        BaseStaffSiteOrgDto endSite = baseMajorManager.getBaseSiteBySiteId(noTaskDetail.getEndSiteId().intValue());
        noTaskDetail.setEndSiteName(endSite == null ? StringUtils.EMPTY : endSite.getSiteName());
        // TODO 指定自建任务的预计发车时间
        Date noTaskPlanDate = new Date();
        noTaskDetail.setPlanDepartTime(noTaskPlanDate);
        noTaskDetail.setCreateUserErp("sys.dms");
        noTaskDetail.setCreateUserName("sys.dms");
        return noTaskDetail;
    }

    private String getSendDetailBizCacheKey(String sendVehicleBiz, String sendDetailBizId) {
        return String.format(CacheKeyConstants.JY_SEND_TASK_DETAIL_FIRST_SCAN_KEY, sendVehicleBiz, sendDetailBizId);
    }

    public String getSendTaskBizCacheKey(String sendVehicleBiz) {
        return String.format(CacheKeyConstants.JY_SEND_TASK_FIRST_SCAN_KEY, sendVehicleBiz);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.uploadPhoto",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Boolean> uploadPhoto(SendPhotoRequest request) {
        InvokeResult<Boolean> invokeResult = new InvokeResult<>();
        if (StringUtils.isBlank(request.getSendVehicleBizId())
                || request.getVehicleArrived() == null) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            JySendAttachmentEntity attachment = genSendAttachment(request);
            sendAttachmentService.saveAttachment(attachment);
            sendCarArriveStatus(attachment, request);
        } catch (Exception ex) {
            log.error("发货拍照上传失败. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，拍照上传异常，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    private void sendCarArriveStatus(JySendAttachmentEntity attachment, SendPhotoRequest request) {
        JyBizTaskSendVehicleEntity jyBizTaskSendVehicle = new JyBizTaskSendVehicleEntity();
        try {
            JySendArriveStatusDto jySendArriveStatusDto = new JySendArriveStatusDto();
            jySendArriveStatusDto.setOperateTime(attachment.getOperateTime().getTime());
            jySendArriveStatusDto.setVehicleArrived(attachment.getVehicleArrived());
            jySendArriveStatusDto.setOperateSiteId(attachment.getOperateSiteId());
            jyBizTaskSendVehicle = taskSendVehicleService.findByBizId(attachment.getSendVehicleBizId());
            if (jyBizTaskSendVehicle != null) {
                String transWorkCode = jyBizTaskSendVehicle.getTransWorkCode();
                jySendArriveStatusDto.setTransWorkCode(transWorkCode);
            }
            jySendArriveStatusDto.setOperateUserErp(attachment.getCreateUserErp());
            jySendArriveStatusDto.setOperateUserName(attachment.getCreateUserName());
            if (CollectionUtils.isNotEmpty(request.getImgList())) {
                jySendArriveStatusDto.setImgList(request.getImgList());
            }
            sendCarArriveStatusProducer.send(jySendArriveStatusDto.getTransWorkCode(), JsonHelper.toJson(jySendArriveStatusDto));
            log.info("推送MQ数据为topic:{}->body:{}", "sendCarArriveStatusProducer", JsonHelper.toJson(jySendArriveStatusDto));
        } catch (Exception e) {
            log.error("拣运发货任务车辆拍照MQ发送失败,派车单号:{} :  ", jyBizTaskSendVehicle.getTransWorkCode(), e);
        }
    }

    private JySendAttachmentEntity genSendAttachment(SendPhotoRequest request) {
        JySendAttachmentEntity attachment = new JySendAttachmentEntity();
        attachment.setSendVehicleBizId(request.getSendVehicleBizId());
        attachment.setOperateSiteId((long) request.getCurrentOperate().getSiteCode());
        attachment.setVehicleArrived(request.getVehicleArrived());
        String url = Constants.EMPTY_FILL;
        if(CollectionUtils.isNotEmpty(request.getImgList())){
            url = Joiner.on(Constants.SEPARATOR_COMMA).join(request.getImgList());
        }
        if (request.getType() == null || SendImageTypeEnum.SEND_IMAGE.getCode().equals(request.getType())) {
            attachment.setImgUrl(url);
        } else if (SendImageTypeEnum.SEAL_IMAGE.getCode().equals(request.getType())) {
            attachment.setSealImgUrl(url);
        }
        attachment.setOperateTime(request.getCurrentOperate().getOperateTime());
        attachment.setCreateTime(request.getCurrentOperate().getOperateTime());
        attachment.setCreateUserErp(request.getUser().getUserErp());
        attachment.setCreateUserName(request.getUser().getUserName());
        attachment.setUpdateTime(request.getCurrentOperate().getOperateTime());
        attachment.setUpdateUserErp(attachment.getCreateUserErp());
        attachment.setUpdateUserName(attachment.getCreateUserName());
        return attachment;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.sendVehicleInfo",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendVehicleInfo> sendVehicleInfo(SendVehicleInfoRequest request) {
        InvokeResult<SendVehicleInfo> invokeResult = new InvokeResult<>();
        if (request.getCurrentOperate() == null
                || request.getCurrentOperate().getSiteCode() <= 0
                || StringUtils.isBlank(request.getSendVehicleBizId())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            JyBizTaskSendVehicleEntity sendVehicleEntity = taskSendVehicleService.findByBizId(request.getSendVehicleBizId());
            if (sendVehicleEntity == null) {
                invokeResult.hintMessage("发货任务不存在！");
                return invokeResult;
            }

            SendVehicleInfo sendVehicleInfo = new SendVehicleInfo();
            invokeResult.setData(sendVehicleInfo);

            if (!setSendVehicleBaseInfo(request, sendVehicleEntity, sendVehicleInfo)) {
                return invokeResult;
            }

            // 设置目的地信息
            setSendVehicleDestInfo(request, sendVehicleInfo);
        } catch (Exception ex) {
            log.error("查询发车任务详情失败. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，查询发车任务详情异常，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    private void setSendVehicleDestInfo(SendVehicleInfoRequest request, SendVehicleInfo sendVehicleInfo) {
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            sendVehicleInfo.setDestCount(Constants.NUMBER_ZERO);
            return;
        }

        sendVehicleInfo.setDestCount(vehicleDetailList.size());

        // 单流向
        if (Objects.equals(sendVehicleInfo.getDestCount(), Constants.CONSTANT_NUMBER_ONE)) {
            JyBizTaskSendVehicleDetailEntity oneTargetItem = vehicleDetailList.get(0);
            sendVehicleInfo.setSendDetailBizId(oneTargetItem.getBizId());
            sendVehicleInfo.setPlanDepartTime(oneTargetItem.getPlanDepartTime());
            sendVehicleInfo.setEndSiteId(oneTargetItem.getEndSiteId().intValue());
            sendVehicleInfo.setEndSiteName(oneTargetItem.getEndSiteName());
        }
    }

    private Boolean setSendVehicleBaseInfo(SendVehicleInfoRequest request,
                                           JyBizTaskSendVehicleEntity sendVehicleEntity, SendVehicleInfo sendVehicleInfo) {
        sendVehicleInfo.setManualCreated(sendVehicleEntity.manualCreatedTask());
        JySendAttachmentEntity attachmentEntity = sendAttachmentService.selectBySendVehicleBizId(new JySendAttachmentEntity(request.getSendVehicleBizId()));
        // 发货前拍照标识
        boolean sendPhotoFlag = false;
        // 封车前拍照标识
        boolean sealPhotoFlag = false;
        if (attachmentEntity != null) {
            sendPhotoFlag = StringUtils.isNotBlank(attachmentEntity.getImgUrl());
            sealPhotoFlag = StringUtils.isNotBlank(attachmentEntity.getSealImgUrl());
        }
        sendVehicleInfo.setPhoto(sendPhotoFlag);
        sendVehicleInfo.setSealPhoto(sealPhotoFlag);
        // 无任务不需拍照
        if (sendVehicleInfo.getManualCreated()) {
            sendVehicleInfo.setPhoto(Boolean.TRUE);
        }
        sendVehicleInfo.setLineTypeShortName(sendVehicleEntity.getLineTypeName());
        sendVehicleInfo.setCarLengthStr(this.setCarLength(sendVehicleEntity));
        sendVehicleInfo.setNoTaskBindVehicle(sendVehicleEntity.noTaskBindVehicle());
        TransWorkBillDto transWorkBillDto = jdiQueryWSManager.queryTransWork(sendVehicleEntity.getTransWorkCode());
        if (transWorkBillDto != null) {
            sendVehicleInfo.setVehicleNumber(transWorkBillDto.getVehicleNumber());
            sendVehicleInfo.setDriverName(transWorkBillDto.getCarrierDriverName());
            sendVehicleInfo.setDriverPhone(transWorkBillDto.getCarrierDriverPhone());
        }

        return true;
    }

    /**
     * 设置车长描述
     * @param sendVehicleEntity
     * @return
     */
    private String setCarLength(JyBizTaskSendVehicleEntity sendVehicleEntity) {
        String carLengthStr = StringUtils.EMPTY;
        BasicVehicleTypeDto basicVehicleType = basicQueryWSManager.getVehicleTypeByVehicleType(sendVehicleEntity.getVehicleType());
        if (basicVehicleType != null && StringUtils.isNotBlank(basicVehicleType.getVehicleLength())) {
            try {
                double carLength = Double.parseDouble(basicVehicleType.getVehicleLength());
                DecimalFormat df = new DecimalFormat("###.0");
                carLengthStr = String.format(SendVehicleLabelOptionEnum.CAR_LENGTH.getName(), df.format(carLength / 100));
            } catch (NumberFormatException e) {
                log.error("解析车长失败. {}", JsonHelper.toJson(basicVehicleType));
            }
        }

        return carLengthStr;
    }

    private Long dealMinus(Number a, Number b) {
        return NumberHelper.gt(a, b) ? a.longValue() - b.longValue() : 0L;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.sendDestDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<List<SendDestDetail>> sendDestDetail(SendDetailRequest request) {
        InvokeResult<List<SendDestDetail>> invokeResult = new InvokeResult<>();

        if (request.getCurrentOperate() == null
                || request.getCurrentOperate().getSiteCode() <= 0
                || StringUtils.isBlank(request.getSendVehicleBizId())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
            if (CollectionUtils.isEmpty(vehicleDetailList)) {
                invokeResult.hintMessage("发货流向为空");
                return invokeResult;
            }
            List<SendDestDetail> sendDestDetails = new ArrayList<>();
            invokeResult.setData(sendDestDetails);

            List<JySendAggsEntity> sendAggList = sendAggService.findBySendVehicleBiz(request.getSendVehicleBizId());
            Map<String, JySendAggsEntity> sendAggMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(sendAggList)) {
                for (JySendAggsEntity aggEntity : sendAggList) {
                    sendAggMap.put(aggEntity.getTransWorkItemCode(), aggEntity);
                }
            }

            for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {
                SendDestDetail sendDestDetail = new SendDestDetail();
                sendDestDetail.setEndSiteId(detailEntity.getEndSiteId().intValue());
                sendDestDetail.setEndSiteName(detailEntity.getEndSiteName());
                sendDestDetail.setPlanDepartTime(detailEntity.getPlanDepartTime());
                sendDestDetail.setVehicleStatus(detailEntity.getVehicleStatus());

                if (sendAggMap.containsKey(detailEntity.getTransWorkItemCode())) {
                    JySendAggsEntity itemAgg = sendAggMap.get(detailEntity.getTransWorkItemCode());
                    sendDestDetail.setToScanPackCount(dealMinus(itemAgg.getShouldScanCount(), itemAgg.getActualScanCount()));
                    sendDestDetail.setScannedPackCount(itemAgg.getActualScanCount().longValue());
                }

                sendDestDetails.add(sendDestDetail);
            }
        } catch (Exception ex) {
            log.error("查询发货任务流向失败. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，查询发货任务流向异常，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.loadProgress",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendVehicleProgress> loadProgress(SendVehicleProgressRequest request) {
        InvokeResult<SendVehicleProgress> invokeResult = new InvokeResult<>();
        if (StringUtils.isBlank(request.getSendVehicleBizId())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            JyBizTaskSendVehicleEntity taskSend = taskSendVehicleService.findByBizId(request.getSendVehicleBizId());
            if (taskSend == null) {
                invokeResult.hintMessage("发车任务不存在！");
                return invokeResult;
            }

            SendVehicleProgress progress = new SendVehicleProgress();
            invokeResult.setData(progress);

            setSendProgressData(taskSend, progress);
            // flink积压导致进度不准，提示前台友好提示
            if(jyDemotionService.checkIsDemotion(JyConstants.JY_FLINK_SEND_IS_DEMOTION)){
                throw new JyDemotionException("进度数据不准，flink降级!");
            }
        }
        catch (JyDemotionException e){
            invokeResult.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_SEND_PROCESS_NOT_ACCURATE, false));
        }
        catch (Exception ex) {
            log.error("查询发货进度失败. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，查询发货进度异常，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    /**
     * 设置发货进度
     * @param taskSend
     * @param progress
     */
    private void setSendProgressData(JyBizTaskSendVehicleEntity taskSend, SendVehicleProgress progress) {
        JySendAggsEntity sendAgg = sendAggService.getVehicleSendStatistics(taskSend.getBizId());

        BasicVehicleTypeDto basicVehicleType = basicQueryWSManager.getVehicleTypeByVehicleType(taskSend.getVehicleType());
        if (basicVehicleType != null) {
            progress.setVolume(BigDecimal.valueOf(basicVehicleType.getVolume()));
            progress.setWeight(BigDecimal.valueOf(basicVehicleType.getWeight()));
        }

        if (sendAgg != null && basicVehicleType != null) {
            progress.setLoadRate(this.dealLoadRate(sendAgg.getTotalScannedWeight(), this.convertTonToKg(BigDecimal.valueOf(basicVehicleType.getWeight()))));
            progress.setLoadVolume(sendAgg.getTotalScannedVolume());
            progress.setLoadWeight(sendAgg.getTotalScannedWeight());
            //progress.setToScanCount(this.dealMinus(sendAgg.getTotalShouldScanCount(), sendAgg.getTotalScannedCount()));
            progress.setScannedPackCount(sendAgg.getTotalScannedCount().longValue());
            progress.setScannedBoxCount(sendAgg.getTotalScannedBoxCodeCount().longValue());
            progress.setInterceptedPackCount(sendAgg.getTotalInterceptCount().longValue());
            progress.setForceSendPackCount(sendAgg.getTotalForceSendCount().longValue());
            progress.setScannedWaybillCount(sendAgg.getTotalScannedWaybillCount().longValue());
            progress.setIncompleteWaybillCount(sendAgg.getTotalIncompleteWaybillCount().longValue());
        }
        log.info("获取待扫数据入参--{}",taskSend.getBizId());
        Long toScanCountSum = jySendProductAggsService.getToScanCountSum(taskSend.getBizId());
        log.info("获取待扫数据--{}",toScanCountSum);
        progress.setToScanCount(toScanCountSum);

        progress.setDestTotal(this.getDestTotal(taskSend.getBizId()));
        progress.setSealedTotal(this.getSealedDestTotal(taskSend.getBizId()));
        progress.setLoadRateUpperLimit(uccConfig.getJySendTaskLoadRateUpperLimit());
    }

    private Integer getDestTotal(String sendVehicleBizId) {
        JyBizTaskSendVehicleDetailEntity totalQ = new JyBizTaskSendVehicleDetailEntity();
        totalQ.setSendVehicleBizId(sendVehicleBizId);
        return taskSendVehicleDetailService.countByCondition(totalQ);
    }

    private Integer getSealedDestTotal(String sendVehicleBizId) {
        JyBizTaskSendVehicleDetailEntity sealedQ = new JyBizTaskSendVehicleDetailEntity();
        sealedQ.setSendVehicleBizId(sendVehicleBizId);
        sealedQ.setVehicleStatus(JyBizTaskSendDetailStatusEnum.SEALED.getCode());
        return taskSendVehicleDetailService.countByCondition(sealedQ);
    }

    private BigDecimal dealLoadRate(BigDecimal loadWeight, BigDecimal standardWeight) {
        if (!NumberHelper.gt0(standardWeight)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(BigDecimalHelper.div(loadWeight, standardWeight, 6)).multiply(new BigDecimal(100)).setScale(6, RoundingMode.HALF_UP);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.checkSendVehicleNormalStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendAbnormalResponse> checkSendVehicleNormalStatus(SendAbnormalRequest request) {
        InvokeResult<SendAbnormalResponse> invokeResult = new InvokeResult<>();
        if (StringUtils.isBlank(request.getSendDetailBizId())) {
            invokeResult.parameterError();
            return invokeResult;
        }
        try {
            JyBizTaskSendVehicleDetailEntity taskDetail = taskSendVehicleDetailService.findByBizId(request.getSendDetailBizId());
            if (taskDetail == null) {
                invokeResult.hintMessage("发货流向已作废！");
                return invokeResult;
            }

            SendAbnormalResponse response = new SendAbnormalResponse();
            response.setNormalFlag(Boolean.TRUE); // 默认正常
            invokeResult.setData(response);

            if (!getSendTaskNormalStatus(request, invokeResult, taskDetail, response)) {
                return invokeResult;
            }

            // 强制前往封车或者任务状态正常，任务状态变为待封车
            if (request.getForceGoToSeal() || response.getNormalFlag()) {
                JyBizTaskSendVehicleEntity taskSend = taskSendVehicleService.findByBizId(taskDetail.getSendVehicleBizId());
                taskDetail.setUpdateTime(new Date());
                taskDetail.setUpdateUserErp(request.getUser().getUserErp());
                taskDetail.setUpdateUserName(request.getUser().getUserName());
                taskSend.setUpdateTime(taskDetail.getUpdateTime());
                taskSend.setUpdateUserErp(taskDetail.getUpdateUserErp());
                taskSend.setUpdateUserName(taskDetail.getUpdateUserName());
                sendVehicleTransactionManager.updateTaskStatus(taskSend, taskDetail, JyBizTaskSendDetailStatusEnum.TO_SEAL);
            }
        } catch (Exception ex) {
            log.error("判断发货异常状态失败. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    /**
     * 校验发货任务是否异常
     * <ul>
     *     <li>无任务发货必须绑定任务才能封车</li>
     *     <li>单流向任务直接校验，多流向只在最后一个流向封车时校验</li>
     *     <li>拦截&强扫或装载率不足，两者都满足时异常优先</li>
     * </ul>
     * @param request
     * @param invokeResult
     * @param taskDetail
     * @param response
     * @return
     */
    private boolean getSendTaskNormalStatus(SendAbnormalRequest request, InvokeResult<SendAbnormalResponse> invokeResult,
                                            JyBizTaskSendVehicleDetailEntity taskDetail, SendAbnormalResponse response) {
        // 强制前往封车跳过校验
        if (request.getForceGoToSeal()) {
            return true;
        }

        String sendVehicleBizId = taskDetail.getSendVehicleBizId();

        List<JyBizTaskSendVehicleDetailEntity> destList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity(taskDetail.getStartSiteId(), sendVehicleBizId));
        int sealedDestCount = 0;
        for (JyBizTaskSendVehicleDetailEntity detailEntity : destList) {
            if (Objects.equals(detailEntity.getBizId(), request.getSendDetailBizId())) {
                if (JyBizTaskSendDetailStatusEnum.SEALED.getCode().equals(detailEntity.getVehicleStatus())
                        || JyBizTaskSendDetailStatusEnum.TO_SEAL.getCode().equals(detailEntity.getVehicleStatus())) {
                    response.setNormalFlag(Boolean.TRUE);
                    return true;
                }
            }
            if (JyBizTaskSendDetailStatusEnum.SEALED.getCode().equals(detailEntity.getVehicleStatus())) {
                sealedDestCount = sealedDestCount + 1;
            }
        }

        // 无任务发货校验是否绑定了发车任务
        JyBizTaskSendVehicleEntity taskSend = taskSendVehicleService.findByBizId(sendVehicleBizId);
        if (taskSend.manualCreatedTask()) {
            invokeResult.hintMessage("无任务不允许封车！");
            return false;
        }

        // 当前封车流向是最后一个
        if (sealedDestCount == destList.size() - 1) {
            boolean existAbnormal = jySendService.findSendRecordExistAbnormal(taskSend.getStartSiteId(), sendVehicleBizId);
            if (existAbnormal) {
                response.setNormalFlag(Boolean.FALSE);
                response.setAbnormalType(SendAbnormalEnum.EXIST_ABNORMAL_PACK);
                return true;
            } else {
                BigDecimal loadRate = this.dealLoadRate(taskSend);
                if (BigDecimal.valueOf(uccConfig.getJySendTaskLoadRateLowerLimit()).compareTo(loadRate) > 0) {
                    response.setNormalFlag(Boolean.FALSE);
                    response.setAbnormalType(SendAbnormalEnum.LOW_LOADING_RATE);
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.interceptedBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendAbnormalBarCode> interceptedBarCodeDetail(SendAbnormalPackRequest request) {
        InvokeResult<SendAbnormalBarCode> invokeResult = new InvokeResult<>();
        if (!NumberHelper.gt0(request.getPageNumber())
                || !NumberHelper.gt0(request.getPageSize())
                || StringUtils.isBlank(request.getSendVehicleBizId())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            querySendBarCodeList(invokeResult, request, SendBarCodeQueryEntranceEnum.INTERCEPT, SendVehicleTaskQuery.QUERY_INTERCEPT);
        }
        catch (JyDemotionException e){
            invokeResult.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_SEND_INTERCEPT, false));
        }
        catch (Exception ex) {
            log.error("查询发车拦截包裹记录异常. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，查询拦截包裹记录失败，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    private void querySendBarCodeList(InvokeResult<SendAbnormalBarCode> invokeResult, SendAbnormalPackRequest request,
                                      SendBarCodeQueryEntranceEnum entranceEnum, Integer queryFlag) {
        Pager<SendVehicleTaskQuery> queryPager = this.getInterceptOrForceBarCodeQuery(request);
        queryPager.getSearchVo().setQueryBarCodeFlag(queryFlag);

        SendAbnormalBarCode barCodeVo = new SendAbnormalBarCode();
        barCodeVo.setTotal(0L);
        List<SendScanBarCode> barCodeList = new ArrayList<>();
        barCodeVo.setBarCodeList(barCodeList);
        invokeResult.setData(barCodeVo);

        Pager<SendBarCodeDetailVo> retPager = sendVehicleJsfManager.queryByCondition(queryPager);
        if (retPager != null && CollectionUtils.isNotEmpty(retPager.getData())) {
            barCodeVo.setTotal(retPager.getTotal());
            for (SendBarCodeDetailVo detailVo : retPager.getData()) {
                SendScanBarCode barCodeItem = new SendScanBarCode();
                barCodeItem.setBarCode(detailVo.getPackageCode());
                barCodeItem.setTags(this.resolveBarCodeTag(entranceEnum, detailVo));
                barCodeList.add(barCodeItem);
            }
        }
    }

    /**
     * 处理包裹展示标签
     * @param entranceEnum
     * @param detailVo
     * @return
     */
    private List<LabelOption> resolveBarCodeTag(SendBarCodeQueryEntranceEnum entranceEnum, SendBarCodeDetailVo detailVo) {
        List<LabelOption> tags = new ArrayList<>();

        switch (entranceEnum) {
            case INTERCEPT:
                if (detailVo.getInterceptFlag() == Constants.CONSTANT_NUMBER_ONE) {
                    tags.add(new LabelOption(BarCodeLabelOptionEnum.INTERCEPT.getCode(), BarCodeLabelOptionEnum.INTERCEPT.getName()));
                }
                break;
            case FORCE_SEND:
                if (detailVo.getForceSendFlag() == Constants.CONSTANT_NUMBER_ONE) {
                    tags.add(new LabelOption(BarCodeLabelOptionEnum.SEND_FORCE_SEND.getCode(), BarCodeLabelOptionEnum.SEND_FORCE_SEND.getName()));
                }
                break;
            case GO_TO_SEAL_PREVIEW:
                if (detailVo.getForceSendFlag() != Constants.CONSTANT_NUMBER_ONE) {
                    if (detailVo.getInterceptFlag() == Constants.CONSTANT_NUMBER_ONE) {
                        tags.add(new LabelOption(BarCodeLabelOptionEnum.INTERCEPT.getCode(), BarCodeLabelOptionEnum.INTERCEPT.getName()));
                    }
                } else {
                    tags.add(new LabelOption(BarCodeLabelOptionEnum.SEND_FORCE_SEND.getCode(), BarCodeLabelOptionEnum.SEND_FORCE_SEND.getName()));
                }
                break;
        }

        return tags;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.forceSendBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendAbnormalBarCode> forceSendBarCodeDetail(SendAbnormalPackRequest request) {
        InvokeResult<SendAbnormalBarCode> invokeResult = new InvokeResult<>();
        if (!NumberHelper.gt0(request.getPageNumber())
                || !NumberHelper.gt0(request.getPageSize())
                || StringUtils.isBlank(request.getSendVehicleBizId())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            querySendBarCodeList(invokeResult, request, SendBarCodeQueryEntranceEnum.FORCE_SEND, SendVehicleTaskQuery.QUERY_FORCE_SEND);
        }
        catch (JyDemotionException e){
            invokeResult.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_SEND_FORCE, false));
        }
        catch (Exception ex) {
            log.error("查询发车强制发货包裹记录异常. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，查询强制发货包裹记录失败，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.abnormalSendBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendAbnormalBarCode> abnormalSendBarCodeDetail(SendAbnormalPackRequest request) {
        InvokeResult<SendAbnormalBarCode> invokeResult = new InvokeResult<>();
        if (!NumberHelper.gt0(request.getPageNumber())
                || !NumberHelper.gt0(request.getPageSize())
                || StringUtils.isBlank(request.getSendVehicleBizId())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            querySendBarCodeList(invokeResult, request, SendBarCodeQueryEntranceEnum.GO_TO_SEAL_PREVIEW, SendVehicleTaskQuery.QUERY_BOTH_INTERCEPT_AND_FORCE_SEND);
        }
        catch (JyDemotionException e){
            invokeResult.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_SEND_ABNORMAL, false));
        }
        catch (Exception ex) {
            log.error("查询发车异常包裹记录异常. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，查询异常包裹记录失败，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    private Pager<SendVehicleTaskQuery> getInterceptOrForceBarCodeQuery(SendAbnormalPackRequest request) {
        Pager<SendVehicleTaskQuery> pager = new Pager<>();
        pager.setPageNo(request.getPageNumber());
        pager.setPageSize(request.getPageSize());

        SendVehicleTaskQuery searchVo = new SendVehicleTaskQuery();
        pager.setSearchVo(searchVo);
        searchVo.setSendVehicleBizId(request.getSendVehicleBizId());
        return pager;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.selectSealDest",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<ToSealDestAgg> selectSealDest(SelectSealDestRequest request) {
        InvokeResult<ToSealDestAgg> invokeResult = new InvokeResult<>();
        if (request.getCurrentOperate() == null
                || request.getCurrentOperate().getSiteCode() <= 0
                || StringUtils.isBlank(request.getSendVehicleBizId())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
            if (CollectionUtils.isEmpty(vehicleDetailList)) {
                invokeResult.hintMessage("发货流向已作废！");
                return invokeResult;
            }

            ToSealDestAgg toSealDestAgg = new ToSealDestAgg();
            toSealDestAgg.setSealedTotal(this.getSealedDestTotal(request.getSendVehicleBizId()));
            toSealDestAgg.setDestTotal(this.getDestTotal(request.getSendVehicleBizId()));

            // 设置发货流向
            toSealDestAgg.setDestList(this.setSendDestDetail(request, vehicleDetailList));
            invokeResult.setData(toSealDestAgg);
        } catch (Exception ex) {
            log.error("获取发车流向失败. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，获取发车流向失败，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    @Override
    public InvokeResult checkMainLineSendTask(CheckSendCodeRequest request) {
        log.info("jy checkMainLineSendTask request:{}", JsonHelper.toJson(request));
        if (ObjectHelper.isNotNull(request.getBizSource()) && uccConfig.needValidateMainLine(request.getBizSource())) {
            try {
                Integer[] sites = BusinessUtil.getSiteCodeBySendCode(request.getSendCode());
                Integer createSite = sites[0];
                Integer receiveSite = sites[1];
                BaseStaffSiteOrgDto receiveSiteDto = baseService.queryDmsBaseSiteByCode(String.valueOf(receiveSite));
                BaseStaffSiteOrgDto createSiteDto = baseService.queryDmsBaseSiteByCode(String.valueOf(createSite));
                MenuUsageConfigRequestDto menuUsageConfigRequestDto = new MenuUsageConfigRequestDto();
                menuUsageConfigRequestDto.setMenuCode(Constants.MENU_CODE_SEND_GZ);
                menuUsageConfigRequestDto.setCurrentOperate(request.getCurrentOperate());
                menuUsageConfigRequestDto.setUser(request.getUser());
                MenuUsageProcessDto menuUsageProcessDto = baseService.getClientMenuUsageConfig(menuUsageConfigRequestDto);
                if (menuUsageProcessDto != null && Constants.FLAG_OPRATE_OFF.equals(menuUsageProcessDto.getCanUse())) {
                    Long endSiteId = new Long(BusinessUtil.getReceiveSiteCodeFromSendCode(request.getSendCode()));
                    Long startSiteId = new Long(request.getCurrentOperate().getSiteCode());
                    boolean isTrunkOrBranch = sendVehicleTransactionManager.isTrunkOrBranchLine(startSiteId, endSiteId);
                    if (isTrunkOrBranch) {
                        boolean needIntercept = Boolean.TRUE;
                        //补充判断运力的运输方式是否包含铁路或者航空
                        if (receiveSiteDto != null && createSiteDto != null) {
                            TransportResourceDto transportResourceDto = new TransportResourceDto();
                            // 始发区域
                            transportResourceDto.setStartOrgCode(String.valueOf(createSiteDto.getOrgId()));
                            // 始发站
                            transportResourceDto.setStartNodeId(createSite);
                            // 目的区域
                            transportResourceDto.setEndOrgCode(String.valueOf(receiveSiteDto.getOrgId()));
                            // 目的站
                            transportResourceDto.setEndNodeId(receiveSite);
                            List<TransportResourceDto> transportResourceDtos = basicSelectWsManager.queryPageTransportResourceWithNodeId(transportResourceDto);
                            if (transportResourceDtos != null) {
                                for (TransportResourceDto trd : transportResourceDtos) {
                                    if (uccConfig.notValidateTransType(trd.getTransWay())) {
                                        needIntercept = Boolean.FALSE;
                                        break;
                                    }
                                }
                            }
                        }
                        if (needIntercept) {
                            return new InvokeResult(NOT_SUPPORT_MAIN_LINE_TASK_CODE, menuUsageProcessDto.getMsg());

                        }
                    }
                }
            } catch (Exception e) {
                log.error("checkMainLineSendTask-校验异常", e);
            }
        }
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    }

    @Override
    public InvokeResult<List<VehicleSpecResp>> listVehicleType() {
        return jyNoTaskSendService.listVehicleType();
    }

    @Override
    public InvokeResult<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        return jyNoTaskSendService.createVehicleTask(createVehicleTaskReq);
    }

    @Override
    public InvokeResult deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        return jyNoTaskSendService.deleteVehicleTask(deleteVehicleTaskReq);
    }

    @Override
    public InvokeResult<DeleteVehicleTaskCheckResponse> checkBeforeDeleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        return jyNoTaskSendService.checkBeforeDeleteVehicleTask(deleteVehicleTaskReq);
    }

    @Override
    public InvokeResult<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        return jyNoTaskSendService.listVehicleTask(vehicleTaskReq);
    }

    @Override
    public InvokeResult<VehicleTaskResp> listVehicleTaskSupportTransfer(TransferVehicleTaskReq transferVehicleTaskReq) {
        return jyNoTaskSendService.listVehicleTaskSupportTransfer(transferVehicleTaskReq);
    }

    @Override
    public InvokeResult bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        return jyNoTaskSendService.bindVehicleDetailTask(bindVehicleDetailTaskReq);
    }

    @Override
    public InvokeResult transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        return jyNoTaskSendService.transferSendTask(transferSendTaskReq);
    }

    @Override
    public InvokeResult<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq cancelSendTaskReq) {
        return jyNoTaskSendService.cancelSendTask(cancelSendTaskReq);
    }

    /**
     * 设置发货流向数据
     * @param request
     * @param vehicleDetailList
     */
    private List<ToSealDestDetail> setSendDestDetail(SelectSealDestRequest request, List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList) {
        List<ToSealDestDetail> sendDestDetails = new ArrayList<>();
        List<JySendAggsEntity> sendAggList = sendAggService.findBySendVehicleBiz(request.getSendVehicleBizId());
        Map<String, JySendAggsEntity> sendAggMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sendAggList)) {
            for (JySendAggsEntity aggEntity : sendAggList) {
                sendAggMap.put(aggEntity.getTransWorkItemCode(), aggEntity);
            }
        }

        for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {
            ToSealDestDetail sendDestDetail = new ToSealDestDetail();
            sendDestDetail.setSendDetailBizId(detailEntity.getBizId());
            sendDestDetail.setItemStatus(detailEntity.getVehicleStatus());
            sendDestDetail.setItemStatusDesc(JyBizTaskSendDetailStatusEnum.getNameByCode(detailEntity.getVehicleStatus()));

            sendDestDetail.setEndSiteId(detailEntity.getEndSiteId().intValue());
            sendDestDetail.setEndSiteName(detailEntity.getEndSiteName());
            sendDestDetail.setPlanDepartTime(detailEntity.getPlanDepartTime());

            if (sendAggMap.containsKey(detailEntity.getTransWorkItemCode())) {
                JySendAggsEntity itemAgg = sendAggMap.get(detailEntity.getTransWorkItemCode());
                sendDestDetail.setToScanPackCount(this.dealMinus(itemAgg.getShouldScanCount(), itemAgg.getActualScanCount()));
                sendDestDetail.setScannedPackCount(itemAgg.getActualScanCount().longValue());
            }

            sendDestDetails.add(sendDestDetail);
        }

        return sendDestDetails;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.sendTaskDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendTaskInfo> sendTaskDetail(SendVehicleInfoRequest request) {
        InvokeResult<SendTaskInfo> invokeResult = new InvokeResult<>();
        if (request.getCurrentOperate() == null
                || request.getCurrentOperate().getSiteCode() <= 0
                || StringUtils.isBlank(request.getSendVehicleBizId())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            final SendTaskInfo sendTaskInfo = new SendTaskInfo();
            // 查询主任务数据
            JyBizTaskSendVehicleEntity sendVehicleEntity = taskSendVehicleService.findByBizId(request.getSendVehicleBizId());
            if (sendVehicleEntity == null) {
                invokeResult.hintMessage("发货任务不存在！");
                return invokeResult;
            }
            this.fillSendTaskInfo(sendTaskInfo, sendVehicleEntity);

            // 查询明细列表
            List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
            if (CollectionUtils.isEmpty(vehicleDetailList)) {
                invokeResult.hintMessage("发货流向为空");
                return invokeResult;
            }
            List<SendTaskItemDetail> sendTaskItemDetails = new ArrayList<>();
            for (JyBizTaskSendVehicleDetailEntity jyBizTaskSendVehicleDetailEntity : vehicleDetailList) {
                sendTaskItemDetails.add(convertSendVehicleDetail2SendDestDetail(jyBizTaskSendVehicleDetailEntity));
            }
            sendTaskInfo.setDetailList(sendTaskItemDetails);

            // 查询发货进度
            SendVehicleProgress progress = new SendVehicleProgress();
            setSendProgressData(sendVehicleEntity, progress);
            sendTaskInfo.setSendVehicleProgress(progress);

            // 查询批次
            final List<JySendCodeEntity> sendCodeEntityList = jySendCodeService.queryByVehicleBizId(request.getSendVehicleBizId());
            if (CollectionUtils.isNotEmpty(sendCodeEntityList)) {
                Map<String, List<String>> sendCodeEntityMapGbDetailIdMap = new HashMap<>();
                for (JySendCodeEntity jySendCodeEntity : sendCodeEntityList) {
                    List<String> sendCodeEntityListExist = sendCodeEntityMapGbDetailIdMap.get(jySendCodeEntity.getSendDetailBizId());
                    if (sendCodeEntityListExist == null) {
                        sendCodeEntityListExist = new ArrayList<>();
                        sendCodeEntityListExist.add(jySendCodeEntity.getSendCode());
                        sendCodeEntityMapGbDetailIdMap.put(jySendCodeEntity.getSendDetailBizId(), sendCodeEntityListExist);
                    } else {
                        sendCodeEntityListExist.add(jySendCodeEntity.getSendCode());
                    }
                }

                for (SendTaskItemDetail sendTaskItemDetail : sendTaskItemDetails) {
                    final List<String> sendCodeEntityListExist = sendCodeEntityMapGbDetailIdMap.get(sendTaskItemDetail.getBizId());
                    if (CollectionUtils.isNotEmpty(sendCodeEntityListExist)) {
                        sendTaskItemDetail.setBatchCodeList(sendCodeEntityListExist);
                    }
                }
            }
            invokeResult.setData(sendTaskInfo);
        } catch (Exception ex) {
            log.error("查询发车任务详情失败. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，查询发车任务详情异常，请咚咚联系分拣小秘！");
        }

        return invokeResult;
    }

    @Override
    public InvokeResult<SendBatchResp> listSendBatchByTaskDetail(SendBatchReq request) {
        List<JySendCodeEntity> sendCodeEntityList = jySendCodeService.queryByVehicleDetailBizId(request.getSendVehicleDetailBizId());
        SendBatchResp sendBatchResp = new SendBatchResp();
        if (ObjectHelper.isNotNull(sendCodeEntityList)) {
            List<SendCodeDto> sendCodeDtos = assembleSendCodeDto(sendCodeEntityList);
            sendBatchResp.setSendCodeList(sendCodeDtos);
        }
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, sendBatchResp);
    }


    @Override
    public InvokeResult<List<SendVehicleProductTypeAgg>> sendVehicleToScanAggByProduct(SendVehicleCommonRequest request) {
        InvokeResult<List<SendVehicleProductTypeAgg>> result = new InvokeResult<>();
        if (StringUtils.isBlank(request.getSendVehicleBizId())) {
            result.parameterError("请选择发车任务！");
            return result;
        }
        try {
            List<SendVehicleProductTypeAgg> productTypeList = Lists.newArrayList();
            result.setData(productTypeList);
            log.info("统计待扫产品类型和包裹总数入参-{}", JSON.toJSONString(request));
            List<JySendVehicleProductType> sendVehicleProductTypeList = jySendProductAggsService.getSendVehicleProductTypeList(request.getSendVehicleBizId());
            log.info("统计待扫产品类型和包裹总数结果-{}",JSON.toJSONString(sendVehicleProductTypeList));
            if (CollectionUtils.isEmpty(sendVehicleProductTypeList)) {
                return result;
            }
            makeToScanCountAggByProduct(productTypeList, sendVehicleProductTypeList);
        }
        catch (Exception ex) {
            log.error("按产品类型统计待扫包裹总数异常. {}-{}", JsonHelper.toJson(request), ex.getMessage(),ex);
            result.error("查询待扫包裹数据服务器异常，请咚咚联系分拣小秘！");
        }
        return result;
    }

    @Override
    public InvokeResult<SendVehicleToScanPackageDetailResponse> sendVehicleToScanPackageDetail(SendVehicleToScanPackageDetailRequest  request) {
        log.info("JySendVehicleServiceImpl.SendVehicleToScanPackageDetail-发车岗按产品类型查询待扫包裹信息入参-{}", JSON.toJSONString(request));
        InvokeResult<SendVehicleToScanPackageDetailResponse> invokeResult = new InvokeResult<>();
        if(request == null || StringUtils.isBlank(request.getSendVehicleBizId()) || StringUtils.isBlank(request.getProductType())){
            invokeResult.parameterError("查询参数不能为空！");
            return invokeResult;
        }
        if (!NumberHelper.gt0(request.getPageSize()) || !NumberHelper.gt0(request.getPageNumber())) {
            invokeResult.parameterError("查询分页信息不能为空！");
        }
        try {
            JyBizTaskSendVehicleDetailEntity entity = new JyBizTaskSendVehicleDetailEntity();
            entity.setSendVehicleBizId(request.getSendVehicleBizId());

            List<Long> receiveIds = taskSendVehicleDetailService.getAllSendDest(entity);

            log.info("发车岗按产品类型查询待扫包裹获取目的地-{}  结果-{}",JSON.toJSONString(entity),JSON.toJSONString(receiveIds));

            Pager<SendVehiclePackageDetailQuery> queryPager = new Pager<>();
            SendVehiclePackageDetailQuery query = new SendVehiclePackageDetailQuery();
            //query.setSendVehicleBizId(request.getSendVehicleBizId());
            query.setProductType(request.getProductType());
            query.setOperateSiteId(request.getCurrentOperate().getSiteCode());
            query.setReceiveSiteIds(receiveIds);

            queryPager.setPageNo(request.getPageNumber());
            queryPager.setPageSize(request.getPageSize());
            queryPager.setSearchVo(query);

            Pager<SendVehiclePackageDetailVo> pagerResult = sendVehicleJsfManager.querySendVehicleToScanPackageDetail(queryPager);
            if (pagerResult != null && CollectionUtils.isNotEmpty(pagerResult.getData())){
                SendVehicleToScanPackageDetailResponse response =new SendVehicleToScanPackageDetailResponse();
                response.setPackageCount(pagerResult.getTotal());
                response.setProductType(request.getProductType());
                response.setProductTypeName(JySendVehicleProductTypeEnum.getNameByCode(request.getProductType()));
                List<SendVehicleToScanPackage> toScanPackages = new ArrayList<>();
                for(SendVehiclePackageDetailVo vo : pagerResult.getData()){
                    SendVehicleToScanPackage toScanPackage =  new SendVehicleToScanPackage();
                    toScanPackage.setPackageCode(vo.getPackageCode());
                    toScanPackage.setProductType(vo.getProductType());
                    toScanPackages.add(toScanPackage);
                }
                response.setPackageCodeList(toScanPackages);
                invokeResult.setData(response);
            }
        }catch (JyDemotionException e){
            invokeResult.customMessage(CodeConstants.JY_DEMOTION_CODE, HintService.getHint(HintCodeConstants.JY_DEMOTION_MSG_SEND_INTERCEPT, false));
        }catch (Exception e){
            log.error("SendVehicleToScanPackageDetail-发车岗按产品类型查询待扫包裹信息异常! 入参-{},{}",JSON.toJSONString(request),e.getMessage(),e);
            invokeResult.error("发车岗按产品类型查询待扫包裹信息异常!");
        }
        return invokeResult;
    }

    private void makeToScanCountAggByProduct(List<SendVehicleProductTypeAgg> productTypeList, List<JySendVehicleProductType> unloadAggList) {
        for (JySendVehicleProductType aggEntity : unloadAggList) {
            SendVehicleProductTypeAgg item = new SendVehicleProductTypeAgg();
            item.setProductType(aggEntity.getProductType());
            item.setProductTypeName(UnloadProductTypeEnum.getNameByCode(item.getProductType()));
            item.setOrder(UnloadProductTypeEnum.getOrderByCode(item.getProductType()));
            item.setCount(new Long(aggEntity.getProductwaitScanCount()));
            productTypeList.add(item);
        }
        Collections.sort(productTypeList, new SendVehicleProductTypeAgg.OrderComparator());
    }

    private List<SendCodeDto> assembleSendCodeDto(List<JySendCodeEntity> sendCodeEntityList) {
        List<SendCodeDto> sendCodeDtos = new ArrayList<>();
        for (JySendCodeEntity jySendCodeEntity : sendCodeEntityList) {
            SendCodeDto sendCodeDto = new SendCodeDto();
            sendCodeDto.setSendCode(jySendCodeEntity.getSendCode());
            sendCodeDto.setSource(jySendCodeEntity.getSource());
            sendCodeDto.setCreateTime(jySendCodeEntity.getUpdateTime());
            sendCodeDto.setCreateUserErp(jySendCodeEntity.getUpdateUserErp());
            sendCodeDto.setCreateUserName(jySendCodeEntity.getUpdateUserName());
            sendCodeDtos.add(sendCodeDto);
        }
        return sendCodeDtos;
    }

    private void fillSendTaskInfo(SendTaskInfo sendTaskInfo, JyBizTaskSendVehicleEntity sendVehicleEntity) {
        sendTaskInfo.setVehicleNumber(sendVehicleEntity.getVehicleNumber());
        sendTaskInfo.setBizId(sendVehicleEntity.getBizId());
        sendTaskInfo.setBizNo(sendVehicleEntity.getBizNo());
        sendTaskInfo.setTransWorkCode(sendVehicleEntity.getTransWorkCode());
        sendTaskInfo.setStartSiteId(sendVehicleEntity.getStartSiteId());
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(sendVehicleEntity.getStartSiteId().intValue());
        if (baseSite != null) {
            sendTaskInfo.setStartSiteName(baseSite.getSiteName());
        }
        sendTaskInfo.setLastPlanDepartTime(sendVehicleEntity.getLastPlanDepartTime());
        sendTaskInfo.setLastSealCarTime(sendVehicleEntity.getLastSealCarTime());
        sendTaskInfo.setVehicleStatus(sendVehicleEntity.getVehicleStatus());
        sendTaskInfo.setVehicleStatusName(JySendVehicleStatusEnum.getNameByCode(sendVehicleEntity.getVehicleStatus()));
        sendTaskInfo.setManualCreatedFlag(sendVehicleEntity.getManualCreatedFlag());
        sendTaskInfo.setAbnormalFlag(sendVehicleEntity.getAbnormalFlag());
        sendTaskInfo.setTransWay(sendVehicleEntity.getTransWay());
        sendTaskInfo.setTransWayName(sendVehicleEntity.getTransWayName());
        if (sendVehicleEntity.getTransWay() != null && StringUtils.isBlank(sendVehicleEntity.getTransWayName())) {
            final TransTypeEnum transTypeEnum = TransTypeEnum.getEnum(sendVehicleEntity.getTransWay());
            if (transTypeEnum != null) {
                sendTaskInfo.setTransWayName(transTypeEnum.getName());
            }
        }
        sendTaskInfo.setVehicleType(sendVehicleEntity.getVehicleType());
        sendTaskInfo.setVehicleTypeName(sendVehicleEntity.getVehicleTypeName());
        sendTaskInfo.setLineType(sendVehicleEntity.getLineType());
        sendTaskInfo.setLineTypeName(sendVehicleEntity.getLineTypeName());
    }

    private SendTaskItemDetail convertSendVehicleDetail2SendDestDetail(JyBizTaskSendVehicleDetailEntity sendVehicleDetailEntity) {
        final SendTaskItemDetail sendTaskItemDetail = new SendTaskItemDetail();
        sendTaskItemDetail.setBizId(sendVehicleDetailEntity.getBizId());
        sendTaskItemDetail.setSendVehicleBizId(sendVehicleDetailEntity.getSendVehicleBizId());
        sendTaskItemDetail.setVehicleStatus(sendVehicleDetailEntity.getVehicleStatus());
        sendTaskItemDetail.setVehicleStatusName(JySendVehicleStatusEnum.getNameByCode(sendVehicleDetailEntity.getVehicleStatus()));
        sendTaskItemDetail.setTransWorkItemCode(sendVehicleDetailEntity.getTransWorkItemCode());
        sendTaskItemDetail.setStartSiteId(sendVehicleDetailEntity.getStartSiteId());
        sendTaskItemDetail.setStartSiteName(sendVehicleDetailEntity.getStartSiteName());
        sendTaskItemDetail.setEndSiteId(sendVehicleDetailEntity.getEndSiteId());
        sendTaskItemDetail.setEndSiteName(sendVehicleDetailEntity.getEndSiteName());
        sendTaskItemDetail.setPlanDepartTime(sendVehicleDetailEntity.getPlanDepartTime());
        sendTaskItemDetail.setSealCarTime(sendVehicleDetailEntity.getSealCarTime());
        sendTaskItemDetail.setExcepLabel(sendVehicleDetailEntity.getExcepLabel());
        return sendTaskItemDetail;
    }
}
