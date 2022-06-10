package com.jd.bluedragon.distribution.jy.service.send;
import java.util.Date;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendAbnormalEnum;
import com.jd.bluedragon.common.dto.operation.workbench.enums.SendVehicleLabelOptionEnum;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
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
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.delivery.constants.SendKeyTypeEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jsf.domain.SortingCheck;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.dto.send.QueryTaskSendDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JySendVehicleStatusEnum;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendAttachmentEntity;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.send.JySendEntity;
import com.jd.bluedragon.distribution.jy.service.seal.JySendSealCodeService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
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
import com.jd.etms.waybill.domain.Waybill;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.JY_APP;

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

    /**
     * 运单路由字段使用的分隔符
     */
    private static final String WAYBILL_ROUTER_SPLIT = "\\|";

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

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
        if (basicVehicleType == null) {
            log.warn("从运输基础资料获取车型失败. {}", vehicleType);
            return BigDecimal.ZERO;
        }

        return this.dealLoadRate(sendAgg.getTotalScannedWeight(), BigDecimal.valueOf(basicVehicleType.getWeight()));

    }

    /**
     * 设置发货流向数据
     * @param queryTaskSendDto
     * @param curQueryStatus
     * @param entity
     * @return
     */
    private List<SendVehicleDetail> getSendVehicleDetail(QueryTaskSendDto queryTaskSendDto, JyBizTaskSendStatusEnum curQueryStatus, JyBizTaskSendVehicleEntity entity) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(queryTaskSendDto.getStartSiteId(), queryTaskSendDto.getEndSiteId(),  entity.getBizId());
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(detailQ);
        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            return Lists.newArrayList();
        }
        List<SendVehicleDetail> sendDestList = Lists.newArrayListWithCapacity(vehicleDetailList.size());
        for (JyBizTaskSendVehicleDetailEntity vehicleDetail : vehicleDetailList) {
            SendVehicleDetail detailVo = new SendVehicleDetail();
            detailVo.setItemStatus(vehicleDetail.getVehicleStatus());
            detailVo.setItemStatusDesc(this.setItemStatusDesc(vehicleDetail, curQueryStatus));
            detailVo.setPlanDepartTime(vehicleDetail.getPlanDepartTime());
            detailVo.setEndSiteId(vehicleDetail.getEndSiteId());
            detailVo.setEndSiteName(vehicleDetail.getEndSiteName());

            sendDestList.add(detailVo);
        }

        return sendDestList;
    }

    /**
     * 设置发货流向状态的描述
     * @param vehicleDetail
     * @param curStatus
     * @return
     */
    private String setItemStatusDesc(JyBizTaskSendVehicleDetailEntity vehicleDetail, JyBizTaskSendStatusEnum curStatus) {
        String fmtDesc;
        switch (curStatus) {
            case TO_SEND:
                fmtDesc = JyBizTaskSendStatusEnum.getNameByCode(vehicleDetail.getVehicleStatus());
                break;
            case SENDING:
                if (JyBizTaskSendStatusEnum.TO_SEND.getCode().equals(vehicleDetail.getVehicleStatus())) {
                    fmtDesc = "未扫";
                }
                else {
                    fmtDesc = "已扫";
                }
                break;
            case TO_SEAL:
                if (JyBizTaskSendStatusEnum.SEALED.getCode().equals(vehicleDetail.getVehicleStatus())) {
                    fmtDesc = "已封车";
                }
                else {
                    fmtDesc = "待封车";
                }
                break;
            case SEALED:
                if (vehicleDetail.getSealCarTime() != null) {
                    String formatTime = DateHelper.formatDate(vehicleDetail.getSealCarTime(), DateHelper.DATE_FORMAT_HHmm);
                    fmtDesc = formatTime + "封";
                }
                fmtDesc = "-";
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

        // FIXME 补全任务标签
        baseSendVehicle.setTags(Lists.<LabelOption>newArrayList());
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
        long endSiteId = -1;

        // 取当前操作网点的路由下一节点
        if (WaybillUtil.isPackageCode(queryTaskSendDto.getKeyword())) {
            endSiteId = getWaybillNextRouter(WaybillUtil.getWaybillCode(queryTaskSendDto.getKeyword()), startSiteId);
        }

        if (endSiteId == -1) {
            return null;
        }

        // 根据路由下一节点查询发货流向的任务
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(startSiteId, endSiteId);
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findBySiteAndStatus(detailQ, queryTaskSendDto.getVehicleStatuses());
        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            String msg = String.format("该包裹没有路由下一站[%s]的发货任务", endSiteId);
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
    private long getWaybillNextRouter(String waybillCode, long startSiteId) {
        String routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
        if (StringUtils.isNotBlank(routerStr)) {
            String [] routerNodes = routerStr.split(WAYBILL_ROUTER_SPLIT);
            for (int i = 0; i < routerNodes.length - 1; i++) {
                int curNode = Integer.parseInt(routerNodes[i]);
                int nextNode = Integer.parseInt(routerNodes[i + 1]);
                if (curNode == startSiteId) {
                    return nextNode;
                }
            }
        }

        return -1;
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

    /**
     * 组装发车任务查询条件
     * @param request
     * @return
     */
    private JyBizTaskSendVehicleEntity makeFetchCondition(SendVehicleTaskRequest request) {
        JyBizTaskSendVehicleEntity condition = new JyBizTaskSendVehicleEntity();
        condition.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
        if (request.getLineType() != null) {
            condition.setLineType(request.getLineType());
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

        QueryTaskSendDto queryTaskSendDto = makeSendTaskQuery(vehicleTaskReq);

        try {
            // 根据包裹号未查到发货流向的任务
            List<String> sendVehicleBizList = resolveSearchKeyword(result, queryTaskSendDto);
            if (!result.codeSuccess()) {
                return result;
            }
            queryTaskSendDto.setSendVehicleBizList(sendVehicleBizList);

            JyBizTaskSendVehicleEntity queryCondition = makeFetchCondition(queryTaskSendDto);

            // 默认按预计发车时间排序
            JyBizTaskSendSortTypeEnum orderTypeEnum = JyBizTaskSendSortTypeEnum.PLAN_DEPART_TIME;
            List<JyBizTaskSendVehicleEntity> vehiclePageList = taskSendVehicleService.querySendTaskOfPage(queryCondition, queryTaskSendDto.getSendVehicleBizList(), orderTypeEnum,
                    queryTaskSendDto.getPageNumber(), queryTaskSendDto.getPageSize(), queryTaskSendDto.getVehicleStatuses());

            VehicleTaskResp taskResp = new VehicleTaskResp();
            result.setData(taskResp);
            List<VehicleTaskDto> vehicleTaskList = new ArrayList<>();
            taskResp.setVehicleTaskDtoList(vehicleTaskList);

            if (CollectionUtils.isEmpty(vehiclePageList)) {
                return result;
            }

            // 按目的地检索同流向的任务，同流向封车了，需要剔除发车任务
            Set<String> needToTwiceRemoveTask = new HashSet<>();

            for (JyBizTaskSendVehicleEntity sendVehicleEntity : vehiclePageList) {
                // 组装发车任务
                VehicleTaskDto vehicleTaskDto = this.initVehicleTaskDto(sendVehicleEntity);

                List<VehicleDetailTaskDto> vdList = new ArrayList<>();
                vehicleTaskDto.setVehicleDetailTaskDtoList(vdList);

                // 组装发车任务流向明细
                this.initVehicleTaskDetails(queryTaskSendDto, sendVehicleEntity, vdList, needToTwiceRemoveTask);

                if (!needToTwiceRemoveTask.contains(sendVehicleEntity.getBizId())) {
                    vehicleTaskList.add(vehicleTaskDto);
                }
            }
        }
        catch (Exception e) {
            log.error("查询发车任务异常. {}", JsonHelper.toJson(vehicleTaskReq), e);
            result.error("查询发车任务异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    private void initVehicleTaskDetails(QueryTaskSendDto queryTaskSendDto, JyBizTaskSendVehicleEntity sendVehicleEntity,
                                        List<VehicleDetailTaskDto> vdList, Set<String> needToRemoveTask) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity(queryTaskSendDto.getStartSiteId(), queryTaskSendDto.getEndSiteId(), sendVehicleEntity.getBizId());
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(detailQ);
        if (CollectionUtils.isNotEmpty(vehicleDetailList)) {
            for (JyBizTaskSendVehicleDetailEntity detailEntity : vehicleDetailList) {

                // 根据目的地匹配的发货流向已封车，发车任务需要剔除掉
                if (detailEntity.getVehicleStatus().equals(JyBizTaskSendDetailStatusEnum.SEALED.getCode()) && detailEntity.getEndSiteId().equals(queryTaskSendDto.getEndSiteId())) {
                    needToRemoveTask.add(detailEntity.getSendVehicleBizId());
                }

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
        
        return vehicleTaskDto;
    }

    private QueryTaskSendDto makeSendTaskQuery(VehicleTaskReq vehicleTaskReq) {
        QueryTaskSendDto queryTaskSendDto = new QueryTaskSendDto();
        queryTaskSendDto.setPageNumber(vehicleTaskReq.getPageNumber());
        queryTaskSendDto.setPageSize(vehicleTaskReq.getPageSize());
        queryTaskSendDto.setVehicleStatuses(JyBizTaskSendStatusEnum.UN_SEALED_STATUS);
        queryTaskSendDto.setStartSiteId(vehicleTaskReq.getStartSiteId());
        queryTaskSendDto.setEndSiteId(vehicleTaskReq.getEndSiteId());
        queryTaskSendDto.setKeyword(vehicleTaskReq.getPackageCode());
        return queryTaskSendDto;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.sendScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<SendScanResponse> sendScan(SendScanRequest request) {

        logInfo("拣运发货扫描:{}", JsonHelper.toJson(request));

        JdVerifyResponse<SendScanResponse> result = new JdVerifyResponse<>();

        // 基础校验
        if (!sendRequestBaseCheck(result, request)) {
            return result;
        }

        JyBizTaskSendVehicleEntity taskSend = taskSendVehicleService.findByBizId(request.getSendVehicleBizId());
        if (taskSend == null) {
            result.toFail("发货任务不存在！");
            return result;
        }

        // 本次扫描的目的地
        long destSiteId = getNextRouter(request);
        List<JyBizTaskSendVehicleDetailEntity> taskSendDetails = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));

        // 业务场景校验
        if (!sendRequestBizCheck(result, request, taskSend)) {
            return result;
        }

        String barCode = request.getBarCode();
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

        SendScanResponse sendScanResponse = new SendScanResponse();
        result.setData(sendScanResponse);

        sendScanResponse.setScanPackCount(this.calculateScanPackageCount(request, sendType));

        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId((int) destSiteId);
        sendScanResponse.setCurScanDestId(destSiteId);
        sendScanResponse.setCurScanDestName(baseSite.getSiteName());

        String sendCode = null;

        // 发货目的地
        long sendDestId = destSiteId;

        // 无任务首次发货确认目的地信息
        if (taskSend.manualCreatedTask()) {
            // 未确认目的地信息
            if (CollectionUtils.isEmpty(taskSendDetails)) {
                if (!request.getNoTaskConfirmDest()) {
                    result.setCode(SendScanResponse.CODE_CONFIRM_DEST);
                    result.addConfirmBox(0, "请确认目的地！");
                    return result;
                }
                else {
                    if (!NumberHelper.gt0(request.getConfirmSendDestId())) {
                        result.toBizError();
                        result.addInterceptBox(0, "无任务发货时请确认发货目的地！");
                        return result;
                    }
                    // FIXME 插入sendVehicleDetail
                }
            }
            else {
                String detailBiz = null;
                for (JyBizTaskSendVehicleDetailEntity sendDetail : taskSendDetails) {
                    if (sendDetail.getEndSiteId().equals(destSiteId)) {
                        detailBiz = sendDetail.getBizId();
                        break;
                    }
                }
                if (StringUtils.isNotBlank(detailBiz)) {
                    // 非同流向迁移会生成新批次，一个流向不止一个批次
                    String curDestSendCode = jySendCodeService.findEarliestSendCode(detailBiz);
                    if (StringUtils.isBlank(curDestSendCode)) {
                        // 首次扫描生成批次
                        sendCode = generateSendCode((long) request.getCurrentOperate().getSiteCode(), destSiteId, request.getUser().getUserErp());

                        this.saveSendCode(request, sendCode, detailBiz);
                    }
                    else {
                        sendCode = curDestSendCode;
                    }
                }
                else {
                    if (!request.getForceSubmit()) {
                        result.setCode(SendScanResponse.CODE_CONFIRM_DEST);
                        result.addConfirmBox(0, "没有该流向的任务，确认要强制发货？");
                        return result;
                    }
                    else {
                        if (!NumberHelper.gt0(request.getConfirmSendDestId())) {
                            result.toBizError();
                            result.addInterceptBox(0, "强制发货时请确认发货目的地！");
                            return result;
                        }
                        sendDestId = request.getConfirmSendDestId();
                        // 错发的流向匹配流向里的一个目的地
                        sendCode = generateSendCode((long) request.getCurrentOperate().getSiteCode(), request.getConfirmSendDestId(), request.getUser().getUserErp());

                        this.saveSendCode(request, sendCode, detailBiz);
                    }
                }
            }
        }
        else {
            if (CollectionUtils.isEmpty(taskSendDetails)) {
                result.toFail("发货流向都已作废！");
                return result;
            }

            String detailBiz = null;
            for (JyBizTaskSendVehicleDetailEntity sendDetail : taskSendDetails) {
                if (sendDetail.getEndSiteId().equals(destSiteId)) {
                    detailBiz = sendDetail.getBizId();
                    break;
                }
            }
            if (StringUtils.isNotBlank(detailBiz)) {
                // 非同流向迁移会生成新批次，一个流向不止一个批次
                String curDestSendCode = jySendCodeService.findEarliestSendCode(detailBiz);
                if (StringUtils.isBlank(curDestSendCode)) {
                    // 首次扫描生成批次
                    sendCode = generateSendCode((long) request.getCurrentOperate().getSiteCode(), destSiteId, request.getUser().getUserErp());

                    this.saveSendCode(request, sendCode, detailBiz);
                }
                else {
                    sendCode = curDestSendCode;
                }
            }
            else {
                if (!request.getForceSubmit()) {
                    result.setCode(SendScanResponse.CODE_CONFIRM_DEST);
                    result.addConfirmBox(0, "没有该流向的任务，确认要强制发货？");
                    return result;
                }
                else {
                    if (!NumberHelper.gt0(request.getConfirmSendDestId())) {
                        result.toBizError();
                        result.addInterceptBox(0, "强制发货时请确认发货目的地！");
                        return result;
                    }
                    sendDestId = request.getConfirmSendDestId();
                    // 错发的流向匹配流向里的一个目的地
                    sendCode = generateSendCode((long) request.getCurrentOperate().getSiteCode(), request.getConfirmSendDestId(), request.getUser().getUserErp());

                    this.saveSendCode(request, sendCode, detailBiz);
                }
            }
        }

        try {
            SendM sendM = toSendMDomain(request, sendDestId, sendCode);
            sendM.setBoxCode(barCode);

            if (deliveryService.isSendByWaybillProcessing(sendM)) {
                result.toBizError();
                result.addInterceptBox(0, HintService.getHint(HintCodeConstants.SEND_BY_WAYBILL_PROCESSING));
                return result;
            }

            // 执行发货前前置校验逻辑
            boolean oldForceSend = true; // 跳过原有拦截校验，使用新的校验逻辑
            boolean cancelLastSend = true;
            SendResult sendResult = new SendResult(SendResult.CODE_OK, SendResult.MESSAGE_OK);

            if (!request.getForceSubmit()) {
                // FIXME 抽出拣运发货的拦截链
                SortingCheck sortingCheck = deliveryService.getSortingCheck(sendM);
                FilterChain filterChain = sortingCheckService.matchJyDeliveryFilterChain(sendType);
                SortingJsfResponse chainResp = sortingCheckService.doSingleSendCheckWithChain(sortingCheck, true, filterChain);
                if (!chainResp.getCode().equals(JdResponse.CODE_OK)) {
                    if (chainResp.getCode() >= SendResult.RESPONSE_CODE_MAPPING_CONFIRM) {
                        sendResult.init(SendResult.CODE_CONFIRM, chainResp.getMessage(), chainResp.getCode(), null);
                    }
                    else {
                        sendResult.init(SendResult.CODE_SENDED, chainResp.getMessage(), chainResp.getCode(), null);
                    }
                }
                if (!sendResultToJdResp(result, sendResult)) {
                    // 拦截时保存拦截记录
                    JySendEntity sendEntity = this.saveJySendRecord(request, sendDestId, sendCode, barCode);
                    sendEntity.setForceSendFlag(0);
                    sendEntity.setInterceptFlag(1);
                    jySendService.save(sendEntity);
                    return result;
                }
            }
            if (request.getForceSubmit()) {
                JySendEntity sendEntity = this.saveJySendRecord(request, sendDestId, sendCode, barCode);
                sendEntity.setForceSendFlag(1);
                jySendService.save(sendEntity);
            }
            else {
                JySendEntity sendEntity = this.saveJySendRecord(request, sendDestId, sendCode, barCode);
                sendEntity.setForceSendFlag(0);
                jySendService.save(sendEntity);
            }

            switch (sendType) {
                case BY_WAYBILL:
                    sendResult = deliveryService.packageSendByWaybill(sendM, oldForceSend, cancelLastSend);
                case BY_PACKAGE:
                    sendResult = deliveryService.packageSend(SendBizSourceEnum.JY_APP_SEND, sendM, oldForceSend, cancelLastSend);
                case BY_BOX:
                    sendResult = deliveryService.packageSend(SendBizSourceEnum.JY_APP_SEND, sendM, oldForceSend, cancelLastSend);
                case BY_BOARD:
                    // TODO 支持扫描板号
            }

            if (!sendResultToJdResp(result, sendResult)) {
                return result;
            }

            // FIXME 绑定集包袋逻辑
            if (!request.getForceSubmit() && StringUtils.isNotBlank(request.getMaterialCode())) {

            }


            // FIXME 记录扫描进度

            // FIXME 修改任务状态
            if (judgeBarCodeIsFirstScanFromTaskDest(request, sendDestId)) {
                JyBizTaskSendVehicleDetailEntity curSendDetail = null;
                for (JyBizTaskSendVehicleDetailEntity sendDetail : taskSendDetails) {
                    if (sendDetail.getEndSiteId().equals(sendDestId)) {
                        curSendDetail = sendDetail;
                        break;
                    }
                }

                // 首次扫描更新发货流向状态为“已发货”
                JyBizTaskSendVehicleDetailEntity statusQ = new JyBizTaskSendVehicleDetailEntity(taskSend.getStartSiteId(), sendDestId, taskSend.getBizId());
                statusQ.setVehicleStatus(JyBizTaskSendDetailStatusEnum.SENDING.getCode());
                taskSendVehicleDetailService.updateStatus(statusQ, curSendDetail.getVehicleStatus());

                Map<Integer, Integer> statusCountMapping = new HashMap<>();
                for (JyBizTaskSendVehicleDetailEntity sendDetail : taskSendDetails) {
                    if (statusCountMapping.containsKey(sendDetail.getVehicleStatus())) {
                        Integer count = statusCountMapping.get(sendDetail.getVehicleStatus());
                        statusCountMapping.put(sendDetail.getVehicleStatus(), count + 1);
                    }
                    else {
                        statusCountMapping.put(sendDetail.getVehicleStatus(), 1);
                    }
                }

                JySendVehicleStatusEnum sendTaskStatus = JySendVehicleStatusEnum.TO_SEND;
                if (statusCountMapping.containsKey(JyBizTaskSendDetailStatusEnum.TO_SEND.getCode())) {
                    sendTaskStatus = JySendVehicleStatusEnum.TO_SEND;
                }
                else if (statusCountMapping.containsKey(JyBizTaskSendDetailStatusEnum.SENDING.getCode())) {
                    sendTaskStatus = JySendVehicleStatusEnum.SENDING;
                }

                JyBizTaskSendVehicleEntity sendStatusQ = new JyBizTaskSendVehicleEntity();
                sendStatusQ.setBizId(taskSend.getBizId());
                sendStatusQ.setVehicleStatus(sendTaskStatus.getCode());
                sendStatusQ.setStartSiteId(taskSend.getStartSiteId());

                taskSendVehicleService.updateStatus(sendStatusQ, taskSend.getVehicleStatus());
            }
            
            // FIXME 任务首次扫描记录组员信息
        }
        catch (Exception ex) {
            log.error("发车扫描失败. {}", JsonHelper.toJson(request), ex);
            result.toError("服务器异常，发车扫描失败，请咚咚联系分拣小秘！");

            redisClientOfJy.del(getBizBarCodeCacheKey(barCode, request.getCurrentOperate().getSiteCode()));
        }

        return result;
    }

    private JySendEntity saveJySendRecord(SendScanRequest request, long destSiteId, String sendCode, String barCode) {
        JySendEntity sendEntity = new JySendEntity();
        sendEntity.setSendVehicleBizId(request.getSendVehicleBizId());
        sendEntity.setCreateSiteId((long) request.getCurrentOperate().getSiteCode());
        sendEntity.setReceiveSiteId(destSiteId);
        sendEntity.setBarCode(barCode);
        sendEntity.setSendCode(sendCode);
        sendEntity.setOperateTime(request.getCurrentOperate().getOperateTime());
        sendEntity.setCreateUserErp(request.getUser().getUserErp());
        sendEntity.setCreateUserName(request.getUser().getUserName());

        return sendEntity;
//
//        jySendService.save(sendEntity);
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
            case BY_WAYBILL:
                Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(barCode);
                if (waybill != null && NumberHelper.gt0(waybill.getGoodNumber())) {
                    scanCount = waybill.getGoodNumber();
                }
            case BY_BOX:
                List<SendDetail> list = deliveryService.getCancelSendByBox(barCode);
                if (CollectionUtils.isNotEmpty(list)) {
                    scanCount = list.size();
                }
            case BY_BOARD:
        }

        return scanCount;
    }

    /**
     * 判断是否是发车任务流向的第一次扫描
     * @param request
     * @param sendDestId
     * @return
     */
    private boolean judgeBarCodeIsFirstScanFromTaskDest(SendScanRequest request, Long sendDestId) {
        boolean firstScanned = false;
        String mutexKey = getSendBizCacheKey(request.getSendVehicleBizId(), (long) request.getCurrentOperate().getSiteCode(), sendDestId);
        if (redisClientOfJy.set(mutexKey, request.getBarCode(), SEND_SCAN_BAR_EXPIRE, TimeUnit.HOURS, false)) {
            JySendEntity queryDb = new JySendEntity(request.getSendVehicleBizId(), (long) request.getCurrentOperate().getSiteCode(),  sendDestId);
            if (jySendService.findByBizId(queryDb) == null) {

                logInfo("单号是发车任务流向的首次扫描. [{}], {}", mutexKey, JsonHelper.toJson(request));

                firstScanned = true;
            }
        }

        return firstScanned;
    }

    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }

    /**
     *
     * @param result
     * @param sendResult
     * @return
     */
    private Boolean sendResultToJdResp(JdVerifyResponse<SendScanResponse> result, SendResult sendResult) {
        if (sendResult.getKey().equals(SendResult.CODE_OK)) {
            result.toSuccess();
            return true;
        }
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
        return sendCodeService.createSendCode(attributeKeyEnumObjectMap, JY_APP, createUser);
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

    /**
     * 发货扫描业务场景校验
     * <h2>客户端弹窗提示类型</h2>
     * <ul>
     *     <li>绑定集包袋：{@link com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum.INTERCEPT}</li>
     *     <li>无任务首次扫描确认目的地：{@link com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum.CONFIRM}</li>
     *     <li>装载率超标：{@link com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum.WARNING}</li>
     *     <li>拦截链：{@link com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum.INTERCEPT}</li>
     *     <li>重复扫描：{@link com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum.PROMPT}</li>
     *     <li>与上一单流向不一致：{@link com.jd.bluedragon.common.dto.base.response.MsgBoxTypeEnum.WARNING}</li>
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

        // 校验是否已经扫描过
        if (checkBarScannedAlready(barCode, siteCode)) {
            response.toBizError();
            response.addPromptBox(0, "扫描重复！");
            return false;
        }

        // 校验集包袋绑定
        if (BusinessHelper.isBoxcode(barCode)) {
            Box box = boxService.findBoxByCode(barCode);
            if (BusinessHelper.isBCBoxType(box.getType())) {
                boolean needBindMaterialBag = funcSwitchConfigService.getBcBoxFilterStatus(FuncSwitchConfigEnum.FUNCTION_BC_BOX_FILTER.getCode(), siteCode);
                if (needBindMaterialBag && !request.getForceSubmit() && StringUtils.isBlank(request.getMaterialCode())) {
                    response.setCode(SendScanResponse.CODE_CONFIRM_MATERIAL);
                    response.addInterceptBox(0, "请扫描集包袋！");
                    return false;
                }
            }
        }

        // FIXME 校验发货流向是否已经封车


        // 校验满载率超标
        BigDecimal loadRate = this.dealLoadRate(taskSend);
        if (loadRate.compareTo(BigDecimal.valueOf(uccConfig.getJySendTaskLoadRateUpperLimit())) > 0) {
            response.toBizError();
            response.addWarningBox(0, "满载率已达到" + uccConfig.getJySendTaskLoadRateUpperLimit());
            return false;
        }

        if (taskSend.getStartSiteId().intValue() != siteCode) {
            response.toFail("当前发车任务始发ID与岗位所属单位ID不一致!");
            return false;
        }

        return true;
    }

    private Long getNextRouter(SendScanRequest request) {
        String barCode = request.getBarCode();
        int siteCode = request.getCurrentOperate().getSiteCode();

        if (WaybillUtil.isPackageCode(barCode)) {
            return getWaybillNextRouter(WaybillUtil.getWaybillCode(barCode), siteCode);
        }
        if (WaybillUtil.isWaybillCode(barCode)) {
            return getWaybillNextRouter(barCode, siteCode);
        }
        if (BusinessHelper.isBoxcode(barCode)) {

        }
        if (BusinessUtil.isBoardCode(barCode)) {

        }

        // FIXME 完善获取目的地逻辑

        return -1L;
    }

    /**
     * 校验是否已经扫描过该单号
     * @param barCode 包裹、运单、箱号、板号
     * @param siteCode 操作场地
     * @return true：扫描过
     */
    private boolean checkBarScannedAlready(String barCode, int siteCode) {
        boolean alreadyScanned = false;
        // 同场地一个单号只能扫描一次
        String mutexKey = getBizBarCodeCacheKey(barCode, siteCode);
        if (redisClientOfJy.set(mutexKey, String.valueOf(System.currentTimeMillis()), SEND_SCAN_BAR_EXPIRE, TimeUnit.HOURS, false)) {
            if (jySendService.queryByCodeAndSite(new JySendEntity(barCode, (long) siteCode)) != null) {
                alreadyScanned = true;
            }
        }
        else {
            alreadyScanned = true;
        }

        return alreadyScanned;
    }

    private String getSendBizCacheKey(String sendVehicleBiz, Long startSiteId, Long sendDestId) {
        return String.format(CacheKeyConstants.JY_SEND_TASK_FIRST_SCAN_KEY, sendVehicleBiz, startSiteId, sendDestId);
    }

    private String getBizBarCodeCacheKey(String barCode, int siteCode) {
        return String.format(CacheKeyConstants.JY_SEND_SCAN_KEY, barCode, siteCode);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.uploadPhoto",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<Boolean> uploadPhoto(SendPhotoRequest request) {
        return null;
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

        JyBizTaskSendVehicleEntity sendVehicleEntity = taskSendVehicleService.findByBizId(request.getSendVehicleBizId());
        if (sendVehicleEntity == null) {
            invokeResult.hintMessage("发货任务不存在！");
            return invokeResult;
        }

        SendVehicleInfo sendVehicleInfo = new SendVehicleInfo();
        invokeResult.setData(sendVehicleInfo);

        if (!setSendVehicleBaseInfo(request, invokeResult, sendVehicleEntity, sendVehicleInfo)) {
            return invokeResult;
        }

        // 设置目的地信息
        setSendVehicleDestInfo(request, sendVehicleInfo);

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
            sendVehicleInfo.setSendDetailBizId(oneTargetItem.getSendVehicleBizId());
            sendVehicleInfo.setPlanDepartTime(oneTargetItem.getPlanDepartTime());
            sendVehicleInfo.setEndSiteId(oneTargetItem.getEndSiteId().intValue());
            sendVehicleInfo.setEndSiteName(oneTargetItem.getEndSiteName());
        }
    }

    private Boolean setSendVehicleBaseInfo(SendVehicleInfoRequest request, InvokeResult<SendVehicleInfo> invokeResult,
                                           JyBizTaskSendVehicleEntity sendVehicleEntity, SendVehicleInfo sendVehicleInfo) {
        TransWorkBillDto transWorkBillDto = jdiQueryWSManager.queryTransWork(sendVehicleEntity.getTransWorkCode());
        if (transWorkBillDto == null) {
            invokeResult.hintMessage("派车单不存在！");
            return false;
        }

        sendVehicleInfo.setManualCreated(sendVehicleEntity.manualCreatedTask());
        sendVehicleInfo.setPhoto(sendAttachmentService.sendVehicleTakePhoto(new JySendAttachmentEntity(request.getSendVehicleBizId())));
        sendVehicleInfo.setVehicleNumber(transWorkBillDto.getVehicleNumber());
        sendVehicleInfo.setLineTypeShortName(sendVehicleEntity.getLineTypeName());
        sendVehicleInfo.setDriverName(transWorkBillDto.getCarrierDriverName());
        sendVehicleInfo.setDriverPhone(transWorkBillDto.getCarrierDriverPhone());
        sendVehicleInfo.setCarLengthStr(this.setCarLength(sendVehicleEntity));
        sendVehicleInfo.setNoTaskBindVehicle(sendVehicleEntity.noTaskBindVehicle());

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
                long carLength = Long.parseLong(basicVehicleType.getVehicleLength());
                carLengthStr = String.format(SendVehicleLabelOptionEnum.CAR_LENGTH.getName(), carLength / 100);
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

        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
        if (CollectionUtils.isEmpty(vehicleDetailList)) {
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

        return invokeResult;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.loadProgress",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendVehicleProgress> loadProgress(SendVehicleProgressRequest request) {
        InvokeResult<SendVehicleProgress> invokeResult = new InvokeResult<>();
        if (StringUtils.isBlank(request.getSendVehicleBizId()) || StringUtils.isBlank(request.getVehicleNumber())) {
            invokeResult.parameterError();
            return invokeResult;
        }

        JyBizTaskSendVehicleEntity taskSend = taskSendVehicleService.findByBizId(request.getSendVehicleBizId());
        if (taskSend == null) {
            invokeResult.hintMessage("发车任务不存在！");
            return invokeResult;
        }

        SendVehicleProgress progress = new SendVehicleProgress();
        invokeResult.setData(progress);

        setSendProgressData(taskSend, progress);

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
            progress.setLoadRate(this.dealLoadRate(sendAgg.getTotalScannedWeight(), BigDecimal.valueOf(basicVehicleType.getWeight())));
            progress.setLoadVolume(sendAgg.getTotalScannedVolume());
            progress.setLoadWeight(sendAgg.getTotalScannedWeight());
            progress.setToScanCount(this.dealMinus(sendAgg.getTotalShouldScanCount(), sendAgg.getTotalScannedCount()));
            progress.setScannedPackCount(sendAgg.getTotalShouldScanCount().longValue());
            progress.setScannedBoxCount(sendAgg.getTotalScannedBoxCodeCount().longValue());
            progress.setInterceptedPackCount(sendAgg.getTotalInterceptCount().longValue());
            progress.setForceSendPackCount(sendAgg.getTotalForceSendCount().longValue());
        }

        progress.setDestTotal(this.getDestTotal(taskSend.getBizId()));
        progress.setSealedTotal(this.getSealedDestTotal(taskSend.getBizId()));
    }

    private Integer getDestTotal(String sendVehicleBizId) {
        JyBizTaskSendVehicleDetailEntity totalQ = new JyBizTaskSendVehicleDetailEntity();
        totalQ.setSendVehicleBizId(sendVehicleBizId);
        return taskSendVehicleDetailService.countByStatus(totalQ);
    }

    private Integer getSealedDestTotal(String sendVehicleBizId) {
        JyBizTaskSendVehicleDetailEntity sealedQ = new JyBizTaskSendVehicleDetailEntity();
        sealedQ.setSendVehicleBizId(sendVehicleBizId);
        sealedQ.setVehicleStatus(JyBizTaskSendDetailStatusEnum.SEALED.getCode());
        return taskSendVehicleDetailService.countByStatus(sealedQ);
    }

    private BigDecimal dealLoadRate(BigDecimal loadWeight, BigDecimal standardWeight) {
        if (!NumberHelper.gt0(standardWeight)) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(BigDecimalHelper.div(loadWeight, standardWeight, 6) * 100);
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.checkSendVehicleNormalStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendAbnormalResponse> checkSendVehicleNormalStatus(SendAbnormalRequest request) {
        InvokeResult<SendAbnormalResponse> invokeResult = new InvokeResult<>();
        if (StringUtils.isBlank(request.getSendDetailBizId()) || StringUtils.isBlank(request.getVehicleNumber())) {
            invokeResult.parameterError();
            return invokeResult;
        }
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
            JyBizTaskSendVehicleDetailEntity statusQ = new JyBizTaskSendVehicleDetailEntity(taskDetail.getStartSiteId(), taskDetail.getEndSiteId(), taskDetail.getSendVehicleBizId());
            statusQ.setVehicleStatus(JyBizTaskSendDetailStatusEnum.TO_SEAL.getCode());
            taskSendVehicleDetailService.updateStatus(statusQ, taskDetail.getVehicleStatus());

            JyBizTaskSendVehicleEntity taskSend = taskSendVehicleService.findByBizId(taskDetail.getSendVehicleBizId());
            JyBizTaskSendVehicleEntity sendStatusQ = new JyBizTaskSendVehicleEntity();
            sendStatusQ.setBizId(taskSend.getBizId());
            sendStatusQ.setVehicleStatus(JyBizTaskSendDetailStatusEnum.TO_SEAL.getCode());
            sendStatusQ.setStartSiteId(taskSend.getStartSiteId());
            taskSendVehicleService.updateStatus(sendStatusQ, taskSend.getVehicleStatus());
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
            if (JyBizTaskSendDetailStatusEnum.SEALED.getCode().equals(detailEntity.getVehicleStatus())) {
                if (Objects.equals(detailEntity.getBizId(), request.getSendDetailBizId())) {
                    invokeResult.hintMessage("该发货流向已封车！");
                    return false;
                }
                sealedDestCount ++;
            }
        }

        // 无任务发货校验是否绑定了发车任务
        JyBizTaskSendVehicleEntity taskSend = taskSendVehicleService.findByBizId(sendVehicleBizId);
        if (taskSend.manualCreatedTask() && !taskSend.noTaskBindVehicle()) {
            invokeResult.hintMessage("无任务发货未绑定任务！");
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
        return null;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.forceSendBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendAbnormalBarCode> forceSendBarCodeDetail(SendAbnormalPackRequest request) {
        return null;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.abnormalSendBarCodeDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendAbnormalBarCode> abnormalSendBarCodeDetail(SendAbnormalPackRequest request) {
        return null;
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
