package com.jd.bluedragon.distribution.jy.service.send;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.BarCodeLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendAbnormalEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.common.dto.send.request.TransferVehicleTaskReq;
import com.jd.bluedragon.common.dto.send.request.VehicleTaskReq;
import com.jd.bluedragon.common.dto.send.response.VehicleDetailTaskDto;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskDto;
import com.jd.bluedragon.common.dto.send.response.VehicleTaskResp;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.BoxMaterialRelationRequest;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
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
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.send.JySendArriveStatusDto;
import com.jd.bluedragon.distribution.jy.dto.send.QueryTaskSendDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.SendBarCodeQueryEntranceEnum;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.manager.IJySendVehicleJsfManager;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendAttachmentEntity;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.send.JySendEntity;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.seal.JySendSealCodeService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.send.domain.ConfirmMsgBox;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.ver.filter.FilterChain;
import com.jd.bluedragon.distribution.ver.service.SortingCheckService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.jy.realtime.base.Pager;
import com.jdl.jy.realtime.model.query.send.SendVehicleTaskQuery;
import com.jdl.jy.realtime.model.vo.send.SendBarCodeDetailVo;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskDistributionTypeEnum;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName JySendVehicleServiceImpl
 * @Description 发货岗网关逻辑加工服务
 * @Author wyh
 * @Date 2022/5/29 14:31
 **/
@Service
public class JySendVehicleServiceImpl implements IJySendVehicleService{

    private static final Logger log = LoggerFactory.getLogger(JySendVehicleServiceImpl.class);

    private static final int SEND_SCAN_BAR_EXPIRE = 6;

    private static final int TRANS_BILL_STATUS_CONFIRM = 15;
    private static final int TRANS_BILL_WORK_STATUS = 20;

    /**
     * 运单路由字段使用的分隔符
     */
    private static final String WAYBILL_ROUTER_SPLIT = "\\|";

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
        }
        catch (Exception e) {
            log.error("查询发车任务异常. {}", JsonHelper.toJson(request), e);
            result.error("查询发车任务异常，请咚咚联系分拣小秘！");
        }

        return result;
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
    private void makeVehicleList(SendVehicleData sendVehicleData,  QueryTaskSendDto queryTaskSendDto) {
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
                }
                else {
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
        baseSendVehicle.setVehicleNumber(transWorkBillDto == null ? StringUtils.EMPTY : transWorkBillDto.getVehicleNumber());
        baseSendVehicle.setManualCreatedFlag(entity.manualCreatedTask());
        baseSendVehicle.setNoTaskBindVehicle(entity.noTaskBindVehicle());
        baseSendVehicle.setSendVehicleBizId(entity.getBizId());
        baseSendVehicle.setTaskId(getJyScheduleTaskId(entity.getBizId()));
        baseSendVehicle.setTransWorkCode(entity.getTransWorkCode());
        baseSendVehicle.setLineType(entity.getLineType());
        baseSendVehicle.setLineTypeName(entity.getLineTypeName());

        // 任务标签
        baseSendVehicle.setTags(resolveTaskTag(entity, transWorkBillDto));
    }

    /**
     * 解析任务标签
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
            String [] routerNodes = routerStr.split(WAYBILL_ROUTER_SPLIT);
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

        return condition;
    }

    private boolean checkBeforeFetchTask(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> result) {
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
        if (StringUtils.isNotBlank(request.getKeyword())) {
            if (!WaybillUtil.isPackageCode(request.getKeyword())) {
                result.parameterError("请扫描正确的包裹号！");
                return false;
            }
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
            List<JyBizTaskSendVehicleEntity> vehiclePageList = taskSendVehicleService.findSendTaskByDestOfPage(curSendDest,
                    vehicleTaskReq.getPageNumber(), vehicleTaskReq.getPageSize());

            if (CollectionUtils.isEmpty(vehiclePageList)) {
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

    private void initVehicleTaskDetails(JyBizTaskSendVehicleEntity sendVehicleEntity, List<VehicleDetailTaskDto> vdList) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(sendVehicleEntity.getStartSiteId(), sendVehicleEntity.getBizId());
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(detailQ);
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

            List<JyBizTaskSendVehicleEntity> vehiclePageList = taskSendVehicleService.findSendTaskByDestOfPage(queryDetail,
                    vehicleTaskReq.getPageNumber(), vehicleTaskReq.getPageSize());

            if (CollectionUtils.isEmpty(vehiclePageList)) {
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
            }
            else {
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
        if (WaybillUtil.isPackageCode(vehicleTaskReq.getPackageCode())) {
            Long startSiteId = (long) vehicleTaskReq.getCurrentOperate().getSiteCode();
            JySendEntity sendEntity = jySendService.queryByCodeAndSite(new JySendEntity(vehicleTaskReq.getPackageCode(), startSiteId));
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

        // 业务场景校验
        if (!sendRequestBizCheck(result, request, taskSend)) {
            return result;
        }

        String barCode = request.getBarCode();
        SendKeyTypeEnum sendType = getSendType(barCode);

        // 1. 获取本次扫描匹配的发货目的地
        List<JyBizTaskSendVehicleDetailEntity> taskSendDetails = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
        Set<Long> allDestId = new HashSet<>();
        for (JyBizTaskSendVehicleDetailEntity sendDetail : taskSendDetails) {
            allDestId.add(sendDetail.getEndSiteId());
        }

        // 根据发货流向匹配出来的发货目的地
        Long matchSendDestId = this.matchSendDest(request, sendType, taskSend, allDestId);

        logInfo("拣运发货匹配的目的地为: {}-{}-{}", request.getBarCode(), taskSend.getStartSiteId(), matchSendDestId);

        if (matchSendDestId == null && !NumberHelper.gt0(request.getConfirmSendDestId())) {
            result.setCode(SendScanResponse.CODE_CONFIRM_DEST);
            result.addWarningBox(0, "未匹配到发货下一站，请手动选择！");
            return result;
        }
        // 实际发货目的地
        Long sendDestId = matchSendDestId;
        if (NumberHelper.gt0(request.getConfirmSendDestId())) {
            sendDestId = request.getConfirmSendDestId();
        }

        try {
            JyBizTaskSendVehicleDetailEntity curSendDetail = null;
            for (JyBizTaskSendVehicleDetailEntity sendDetail : taskSendDetails) {
                if (sendDetail.getEndSiteId().equals(sendDestId)) {
                    curSendDetail = sendDetail;
                    break;
                }
            }
            if (curSendDetail != null && JyBizTaskSendDetailStatusEnum.SEALED.getCode().equals(curSendDetail.getVehicleStatus())) {
                result.toBizError();
                result.addInterceptBox(0, "该发货流向已封车！");
                return result;
            }

            SendResult sendResult = new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);
            String sendCode = getOrCreateSendCode(request, sendDestId, taskSendDetails);
            SendM sendM = toSendMDomain(request, sendDestId, sendCode);
            sendM.setBoxCode(barCode);

            if (SendKeyTypeEnum.BY_PACKAGE.equals(sendType) && deliveryService.isSendByWaybillProcessing(sendM)) {
                result.toBizError();
                result.addInterceptBox(0, HintService.getHint(HintCodeConstants.SEND_BY_WAYBILL_PROCESSING));
                return result;
            }

            // 校验是否已经发货
            deliveryService.multiSendVerification(sendM, sendResult);
            if (Objects.equals(sendResult.getKey(), SendResult.CODE_SENDED)) {
                result.toBizError();
                result.addInterceptBox(sendResult.getKey(), sendResult.getValue());
                return result;
            }

            // 执行发货前前置校验逻辑
            boolean oldForceSend = true; // 跳过原有拦截校验，使用新的校验逻辑
            boolean cancelLastSend = ConfirmMsgBox.CODE_CONFIRM_CANCEL_LAST_SEND.equals(sendResult.getInterceptCode());
            if (Boolean.FALSE.equals(request.getForceSubmit())) {
                if (!BusinessHelper.isBoxcode(barCode)) {
                    SortingCheck sortingCheck = deliveryService.getSortingCheck(sendM);
                    FilterChain filterChain = sortingCheckService.matchJyDeliveryFilterChain(sendType);
                    SortingJsfResponse chainResp = sortingCheckService.doSingleSendCheckWithChain(sortingCheck, true, filterChain);
                    if (!chainResp.getCode().equals(JdResponse.CODE_OK)) {
                        if (JdResponse.CODE_SERVICE_ERROR.equals(chainResp.getCode())) {
                            sendResult.init(SendResult.CODE_SENDED, chainResp.getMessage(), chainResp.getCode(), null);
                        }
                        else if (chainResp.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM) {
                            sendResult.init(SendResult.CODE_CONFIRM, chainResp.getMessage(), chainResp.getCode(), null);
                        }
                        else {
                            sendResult.init(SendResult.CODE_SENDED, chainResp.getMessage(), chainResp.getCode(), null);
                            // 拦截时保存拦截记录
                            JySendEntity sendEntity = this.createJySendRecord(request, sendDestId, sendCode, barCode);
                            sendEntity.setForceSendFlag(0);
                            sendEntity.setInterceptFlag(1);
                            jySendService.save(sendEntity);
                        }
                    }
                }
                if (!sendResultToJdResp(result, sendResult)) {
                    return result;
                }
            }
            if (Boolean.TRUE.equals(request.getForceSubmit())) {
                JySendEntity sendEntity = this.createJySendRecord(request, sendDestId, sendCode, barCode);
                sendEntity.setForceSendFlag(1);
                jySendService.save(sendEntity);
            }
            else {
                JySendEntity sendEntity = this.createJySendRecord(request, sendDestId, sendCode, barCode);
                sendEntity.setForceSendFlag(0);
                jySendService.save(sendEntity);
            }

            // 绑定集包袋逻辑
            if (StringUtils.isNotBlank(request.getMaterialCode()) && BusinessHelper.isBoxcode(barCode)) {
                BoxMaterialRelationRequest req = new BoxMaterialRelationRequest();
                InvokeResult bindMaterialRet = cycleBoxService.boxMaterialRelationAlter(req);
                if (!bindMaterialRet.codeSuccess()) {
                    result.toFail("绑定失败：" + bindMaterialRet.getMessage());
                    return result;
                }
            }

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
                    // TODO 支持扫描板号
                    break;
            }

            if (!sendResultToJdResp(result, sendResult)) {
                return result;
            }

            // 发货流向首次扫描
            if (taskSendDestFirstScan(request, sendDestId)) {
                logInfo("发货任务流向[{}-{}]首次扫描, 任务状态变为“发货中”. {}", request.getSendVehicleBizId(), sendDestId,
                        JsonHelper.toJson(request));
                updateSendVehicleStatus(request, taskSend, curSendDetail);
            }

            // 发货任务首次扫描记录组员信息
            if (taskSendFirstScan(request)) {
                logInfo("发货任务[{}]首次扫描, 任务状态变为“发货中”. {}", request.getSendVehicleBizId(), JsonHelper.toJson(request));

                distributeAndStartScheduleTask(request);

                recordTaskMembers(request);
            }

            SendScanResponse sendScanResponse = new SendScanResponse();
            result.setData(sendScanResponse);

            sendScanResponse.setScanPackCount(this.calculateScanPackageCount(request, sendType));
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(sendDestId.intValue());
            sendScanResponse.setCurScanDestId(sendDestId);
            sendScanResponse.setCurScanDestName(baseSite.getSiteName());
        }
        catch (Exception ex) {
            log.error("发货任务扫描失败. {}", JsonHelper.toJson(request), ex);
            result.toError("服务器异常，发货任务扫描失败，请咚咚联系分拣小秘！");
        }

        return result;
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
        }
        else if (BusinessUtil.isBoardCode(barCode)) {
            // TODO 支持扫描板号
            sendType = SendKeyTypeEnum.BY_BOARD;
        }
        else if (WaybillUtil.isPackageCode(barCode)) {
            sendType = SendKeyTypeEnum.BY_PACKAGE;
        }
        else if (BusinessHelper.isBoxcode(barCode)) {
            sendType = SendKeyTypeEnum.BY_BOX;
        }
        return sendType;
    }

    /**
     * 根据发货流向查询批次
     * @param request
     * @param destSiteId
     * @param taskSendDetails
     * @return
     */
    private String getOrCreateSendCode(SendScanRequest request, Long destSiteId, List<JyBizTaskSendVehicleDetailEntity> taskSendDetails) {
        String detailBiz = null;
        for (JyBizTaskSendVehicleDetailEntity sendDetail : taskSendDetails) {
            if (sendDetail.getEndSiteId().equals(destSiteId)) {
                detailBiz = sendDetail.getBizId();
                break;
            }
        }
        // 非同流向迁移会生成新批次，一个流向不止一个批次
        String curDestSendCode = jySendCodeService.findEarliestSendCode(detailBiz);

        String sendCode;
        if (StringUtils.isBlank(curDestSendCode)) {
            Profiler.businessAlarm("dms.web.JySendVehicleService.getOrCreateSendCode", "[拣运APP]发货匹配发货批次失败，将新建批次！");
            logWarn("发货流向获取批次号为空! {}", detailBiz);
            // 首次扫描生成批次
            sendCode = generateSendCode((long) request.getCurrentOperate().getSiteCode(), destSiteId, request.getUser().getUserErp());

            this.saveSendCode(request, sendCode, detailBiz);
        }
        else {
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
    private Long matchSendDest(SendScanRequest request, SendKeyTypeEnum sendType,
                               JyBizTaskSendVehicleEntity taskSend, Set<Long> allDestId) {
        String barCode = request.getBarCode();
        long siteCode = request.getCurrentOperate().getSiteCode();
        // 根据发货流向匹配出来的发货目的地
        Long destSiteId = null;

        switch (sendType) {
            case BY_WAYBILL:
                Long matchDestId = getWaybillNextRouter(barCode, siteCode);
                if (allDestId.contains(matchDestId)) {
                    destSiteId = matchDestId;
                }
                break;
            case BY_PACKAGE:
                Long matchDestIdByPack = getWaybillNextRouter(WaybillUtil.getWaybillCode(barCode), siteCode);
                if (allDestId.contains(matchDestIdByPack)) {
                    destSiteId = matchDestIdByPack;
                }
                break;
            case BY_BOX:
                // 先根据箱号目的地取，再从箱号里取三个运单，根据路由匹配发货流向，需要弹窗提示
                Box box = boxService.findBoxByCode(barCode);
                if (box != null) {
                    if (allDestId.contains(box.getReceiveSiteCode().longValue())) {
                        destSiteId = box.getReceiveSiteCode().longValue();
                    }
                    else {
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
                        }
                        else {
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
                // TODO 支持扫描板号
                break;
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
                break;
        }

        return scanCount;
    }

    /**
     * 判断是否是发车任务流向的第一次扫描
     * @param request
     * @param sendDestId
     * @return
     */
    private boolean taskSendDestFirstScan(SendScanRequest request, Long sendDestId) {
        boolean firstScanned = false;
        String mutexKey = getSendDetailBizCacheKey(request.getSendVehicleBizId(), (long) request.getCurrentOperate().getSiteCode(), sendDestId);
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
     *
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
     *     <li>绑定集包袋：{@link com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum.INTERCEPT}</li>
     *     <li>无任务首次扫描确认目的地：{@link com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum.CONFIRM}</li>
     *     <li>拦截链：{@link com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum.INTERCEPT}</li>
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
                    if (!WaybillUtil.isPackageCode(barCode) && !WaybillUtil.isWaybillCode(barCode)) {
                        response.toBizError();
                        response.addInterceptBox(0, "无任务首次扫描只能是运单或包裹！");
                        return false;
                    }

                    // 无任务首次扫描返回目的地
                    Long routeNextSite = getWaybillNextRouter(WaybillUtil.getWaybillCode(barCode), taskSend.getStartSiteId());
                    if (routeNextSite == null) {
                        response.toBizError();
                        response.addInterceptBox(0, "运单的路由没有当前场地！");
                        return false;
                    }

                    response.setCode(SendScanResponse.CODE_NO_TASK_CONFIRM_DEST);
                    response.addConfirmBox(0, "无任务发货请确认发货流向！");

                    SendScanResponse sendScanResponse = new SendScanResponse();
                    response.setData(sendScanResponse);
                    BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(routeNextSite.intValue());
                    sendScanResponse.setCurScanDestId(routeNextSite);
                    sendScanResponse.setCurScanDestName(baseSite.getSiteName());

                    return false;
                }
                else {
                    // 客户端确认流向后保存无任务的发货流向
                    JyBizTaskSendVehicleDetailEntity noTaskDetail = makeNoTaskSendDetail(request, taskSend);
                    logInfo("初始化无任务发货明细. {}", JsonHelper.toJson(noTaskDetail));
                    transactionManager.saveTaskSendAndDetail(null, noTaskDetail);

                    logInfo("启用无任务发货任务. {}", JsonHelper.toJson(taskSend));
                    this.enableNoTask(taskSend);

                    // 保存无任务发货备注
                    saveNoTaskRemark(request);
                }
            }
            else {
                response.toBizError();
                response.addInterceptBox(0, "发货流向都已作废！");
                return false;
            }
        }

        // 校验箱号是否绑定集包袋
        if (BusinessHelper.isBoxcode(barCode)) {
            Box box = boxService.findBoxByCode(barCode);
            if (BusinessHelper.isBCBoxType(box.getType())) {
                boolean needBindMaterialBag = funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(), siteCode);
                if (needBindMaterialBag) {
                    // 箱号未绑定集包袋
                    if (StringUtils.isBlank(cycleBoxService.getBoxMaterialRelation(barCode))) {
                        if (StringUtils.isBlank(request.getMaterialCode())) {
                            response.setCode(SendScanResponse.CODE_CONFIRM_MATERIAL);
                            response.addInterceptBox(0, "请扫描集包袋！");
                            return false;
                        }
                    }
                }
            }
        }

        // 已经绑定的无任务不允许再发货
        if (taskSend.manualCreatedTask() && taskSend.noTaskBindVehicle()) {
            response.toBizError();
            response.addInterceptBox(0, "无任务已经绑定，不能继续操作发货！");
            return false;
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

    private String getSendDetailBizCacheKey(String sendVehicleBiz, Long startSiteId, Long sendDestId) {
        return String.format(CacheKeyConstants.JY_SEND_TASK_DETAIL_FIRST_SCAN_KEY, sendVehicleBiz, startSiteId, sendDestId);
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
                || request.getVehicleArrived() == null
                || CollectionUtils.isEmpty(request.getImgList())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        try {
            JySendAttachmentEntity attachment = genSendAttachment(request);
            sendAttachmentService.saveAttachment(attachment);
            sendCarArriveStatus(attachment,request);
        }
        catch (Exception ex) {
            log.error("发货拍照上传失败. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，拍照上传异常，请咚咚联系分拣小秘！");;
        }

        return invokeResult;
    }

    private void sendCarArriveStatus(JySendAttachmentEntity attachment,SendPhotoRequest request) {
        JyBizTaskSendVehicleEntity jyBizTaskSendVehicle = new JyBizTaskSendVehicleEntity();
        try{
            JySendArriveStatusDto jySendArriveStatusDto = new JySendArriveStatusDto();
            jySendArriveStatusDto.setOperateTime(attachment.getOperateTime().getTime());
            jySendArriveStatusDto.setVehicleArrived(attachment.getVehicleArrived());
            jySendArriveStatusDto.setOperateSiteId(attachment.getOperateSiteId());
            jyBizTaskSendVehicle = taskSendVehicleService.findByBizId(attachment.getSendVehicleBizId());
            if (jyBizTaskSendVehicle!=null){
                String transWorkCode = jyBizTaskSendVehicle.getTransWorkCode();
                jySendArriveStatusDto.setTransWorkCode(transWorkCode);
            }
            jySendArriveStatusDto.setOperateUserErp(attachment.getCreateUserErp());
            jySendArriveStatusDto.setOperateUserName(attachment.getCreateUserName());
            if (CollectionUtils.isNotEmpty(request.getImgList())){
                jySendArriveStatusDto.setImgList(request.getImgList());
            }
            sendCarArriveStatusProducer.send(jySendArriveStatusDto.getTransWorkCode(),JsonHelper.toJson(jySendArriveStatusDto));
            log.info("推送MQ数据为topic:{}->body:{}", "sendCarArriveStatusProducer", JsonHelper.toJson(jySendArriveStatusDto));
        }catch (Exception e) {
            log.error("拣运发货任务车辆拍照MQ发送失败,派车单号:{} :  ",jyBizTaskSendVehicle.getTransWorkCode(),e);
        }
    }

    private JySendAttachmentEntity genSendAttachment(SendPhotoRequest request) {
        JySendAttachmentEntity attachment = new JySendAttachmentEntity();
        attachment.setSendVehicleBizId(request.getSendVehicleBizId());
        attachment.setOperateSiteId((long) request.getCurrentOperate().getSiteCode());
        attachment.setVehicleArrived(request.getVehicleArrived());
        attachment.setImgUrl(Joiner.on(Constants.SEPARATOR_COMMA).join(request.getImgList()));
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
        }
        catch (Exception ex) {
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
        sendVehicleInfo.setPhoto(sendAttachmentService.sendVehicleTakePhoto(new JySendAttachmentEntity(request.getSendVehicleBizId())));
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
            }
            catch (NumberFormatException e) {
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

                if (sendAggMap.containsKey(detailEntity.getTransWorkItemCode())) {
                    JySendAggsEntity itemAgg = sendAggMap.get(detailEntity.getTransWorkItemCode());
                    sendDestDetail.setToScanPackCount(dealMinus(itemAgg.getShouldScanCount(), itemAgg.getActualScanCount()));
                    sendDestDetail.setScannedPackCount(itemAgg.getActualScanCount().longValue());
                }

                sendDestDetails.add(sendDestDetail);
            }
        }
        catch (Exception ex) {
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
            progress.setToScanCount(this.dealMinus(sendAgg.getTotalShouldScanCount(), sendAgg.getTotalScannedCount()));
            progress.setScannedPackCount(sendAgg.getTotalScannedCount().longValue());
            progress.setScannedBoxCount(sendAgg.getTotalScannedBoxCodeCount().longValue());
            progress.setInterceptedPackCount(sendAgg.getTotalInterceptCount().longValue());
            progress.setForceSendPackCount(sendAgg.getTotalForceSendCount().longValue());
        }

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
        }
        catch (Exception ex) {
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
        }

        // 无任务发货校验是否绑定了发车任务
        JyBizTaskSendVehicleEntity taskSend = taskSendVehicleService.findByBizId(sendVehicleBizId);
        if (taskSend.manualCreatedTask()) {
            invokeResult.hintMessage("无任务不允许封车！");
            return false;
        }

        // 当前封车流向是最后一个
        if (sealedDestCount == destList.size() - 1) {
            JySendEntity existAbnormal = jySendService.findSendRecordExistAbnormal(new JySendEntity(taskSend.getStartSiteId(), sendVehicleBizId));
            if (existAbnormal != null) {
                response.setNormalFlag(Boolean.FALSE);
                response.setAbnormalType(SendAbnormalEnum.EXIST_ABNORMAL_PACK);
                return true;
            }
            else {
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
                }
                else {
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
        }
        catch (Exception ex) {
            log.error("获取发车流向失败. {}", JsonHelper.toJson(request), ex);
            invokeResult.error("服务器异常，获取发车流向失败，请咚咚联系分拣小秘！");
        }

        return invokeResult;
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
}
