package com.jd.bluedragon.distribution.jy.service.send;
import java.security.interfaces.ECKey;
import java.util.Date;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.send.request.*;
import com.jd.bluedragon.common.dto.operation.workbench.send.response.*;
import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import com.jd.bluedragon.common.dto.operation.workbench.unseal.response.VehicleStatusStatis;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAttachmentDao;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.send.JySendAggsEntity;
import com.jd.bluedragon.distribution.jy.send.JySendAttachmentEntity;
import com.jd.bluedragon.distribution.jy.service.seal.JySendSealCodeService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName JySendVehicleServiceImpl
 * @Description 发货岗网关逻辑加工服务
 * @Author wyh
 * @Date 2022/5/29 14:31
 **/
@Service
public class JySendVehicleServiceImpl implements IJySendVehicleService{

    private static final Logger log = LoggerFactory.getLogger(JySendVehicleServiceImpl.class);

    /**
     * 运单路由字段使用的分隔符
     */
    private static final String WAYBILL_ROUTER_SPLITER = "\\|";

    @Autowired
    private JyBizTaskSendVehicleService taskSendVehicleService;

    @Autowired
    private JyBizTaskSendVehicleDetailService taskSendVehicleDetailService;

    @Autowired
    private JySendAggsService sendAggsService;

    @Autowired
    private JySendSealCodeService sendSealCodeService;

    @Autowired
    private IJySendAttachmentService sendAttachmentService;

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;

    @Autowired
    private JdiQueryWSManager jdiQueryWSManager;

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

            JyBizTaskSendVehicleEntity condition = makeFetchCondition(request);

            // 根据包裹号未查到发货流向的任务
            List<String> sendVehicleBizList = resolveSearchKeyword(result, request);
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

            // 按任务状态组装车辆数据
            assembleSendVehicleData(response, request, condition, sendVehicleBizList);

            result.setData(response);
        }
        catch (Exception e) {
            log.error("查询发车任务异常. {}", JsonHelper.toJson(request), e);
            result.error("查询发车任务异常，请咚咚联系分拣小秘！");
        }

        return result;
    }

    /**
     * 按状态组装车辆数据
     * @param response
     * @param request
     * @param condition
     * @param sendVehicleBizList
     */
    private void assembleSendVehicleData(SendVehicleTaskResponse response, SendVehicleTaskRequest request,
                                         JyBizTaskSendVehicleEntity condition, List<String> sendVehicleBizList) {
        JyBizTaskSendStatusEnum curQueryStatus = JyBizTaskSendStatusEnum.getEnumByCode(request.getVehicleStatus());
        SendVehicleData sendVehicleData = new SendVehicleData();
        sendVehicleData.setVehicleStatus(curQueryStatus.getCode());

        // 按车辆状态组装
        makeVehicleList(sendVehicleData, request, condition, curQueryStatus, sendVehicleBizList);

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
     * @param request
     * @param condition
     * @param curQueryStatus
     * @param sendVehicleBizList
     */
    private void makeVehicleList(SendVehicleData sendVehicleData, SendVehicleTaskRequest request,
                                 JyBizTaskSendVehicleEntity condition, JyBizTaskSendStatusEnum curQueryStatus,
                                 List<String> sendVehicleBizList) {
        List<BaseSendVehicle> vehicleList = Lists.newArrayList();
        sendVehicleData.setData(vehicleList);

        // 设置排序方式
        JyBizTaskSendSortTypeEnum orderTypeEnum = setTaskOrderType(curQueryStatus);

        List<JyBizTaskSendVehicleEntity> vehiclePageList = taskSendVehicleService.querySendTaskOfPage(condition, sendVehicleBizList, orderTypeEnum, request.getPageNumber(), request.getPageSize());
        if (CollectionUtils.isEmpty(vehiclePageList)) {
            return;
        }

        for (JyBizTaskSendVehicleEntity entity : vehiclePageList) {
            // 初始化基础字段
            BaseSendVehicle baseSendVehicle = assembleBaseSendVehicle(curQueryStatus, entity);

            // 设置个性化字段
            switch (curQueryStatus) {
                case TO_SEND:
                    ToSendVehicle toSendVehicle = (ToSendVehicle) baseSendVehicle;
                    toSendVehicle.setSendDestList(this.getSendVehicleDetail(curQueryStatus, request, entity));

                    vehicleList.add(toSendVehicle);
                    break;
                case SENDING:
                    SendingVehicle sendingVehicle = (SendingVehicle) baseSendVehicle;
                    sendingVehicle.setLoadRate(this.dealLoadRate(entity));
                    sendingVehicle.setSendDestList(this.getSendVehicleDetail(curQueryStatus, request, entity));

                    vehicleList.add(sendingVehicle);
                    break;
                case TO_SEAL:
                    ToSealVehicle toSealVehicle = (ToSealVehicle) baseSendVehicle;
                    toSealVehicle.setSendDestList(this.getSendVehicleDetail(curQueryStatus, request, entity));

                    vehicleList.add(toSealVehicle);
                    break;
                case SEALED:
                    SealedVehicle sealedVehicle = (SealedVehicle) baseSendVehicle;
                    sealedVehicle.setSealCodeCount(sendSealCodeService.countByBiz(entity.getBizId()));
                    sealedVehicle.setSendDestList(this.getSendVehicleDetail(curQueryStatus, request, entity));

                    vehicleList.add(sealedVehicle);
                    break;
            }
        }
    }

    /**
     * 按体积计算车辆装载率
     * @param entity
     * @return
     */
    private BigDecimal dealLoadRate(JyBizTaskSendVehicleEntity entity) {
        // FIXME 补全setLoadRate
        JySendAggsEntity sendAgg = sendAggsService.getVehicleSendStatistics(entity.getBizId());
        if (sendAgg != null && NumberHelper.gt0(sendAgg.getTotalScannedWeight())) {
            Integer vehicleType = entity.getVehicleType();
            // 根据车型从运输获得车型满载体积
        }

        return BigDecimal.ZERO;
    }

    /**
     * 设置发货流向数据
     * @param curQueryStatus
     * @param request
     * @param entity
     * @return
     */
    private List<SendVehicleDetail> getSendVehicleDetail(JyBizTaskSendStatusEnum curQueryStatus, SendVehicleTaskRequest request, JyBizTaskSendVehicleEntity entity) {
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity((long)request.getCurrentOperate().getSiteCode(), request.getEndSiteId(),  entity.getBizId());
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

        TransWorkBillDto transWorkBillDto = jdiQueryWSManager.queryTransWork(entity.getTransWorkCode());
        baseSendVehicle.setVehicleNumber(transWorkBillDto == null ? StringUtils.EMPTY : transWorkBillDto.getVehicleNumber());
        baseSendVehicle.setManualCreatedFlag(entity.manualCreatedTask());
        baseSendVehicle.setNoTaskBindVehicle(entity.noTaskBindVehicle());
        baseSendVehicle.setSendVehicleBizId(entity.getBizId());
        baseSendVehicle.setTaskId(getJyScheduleTaskId(entity.getBizId()));
        baseSendVehicle.setTransWorkCode(entity.getTransWorkCode());

        // FIXME 补全任务标签
        baseSendVehicle.setTags(Lists.<LabelOption>newArrayList());

        return baseSendVehicle;
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
     * @param request
     * @return
     */
    private List<String> resolveSearchKeyword(InvokeResult<SendVehicleTaskResponse> result, SendVehicleTaskRequest request) {
        if (StringUtils.isBlank(request.getKeyword())) {
            return null;
        }

        int startSiteId = request.getCurrentOperate().getSiteCode();
        int receiveSiteCode = -1;

        // 取当前操作网点的路由下一节点
        if (WaybillUtil.isPackageCode(request.getKeyword())) {
            String waybillCode = WaybillUtil.getWaybillCode(request.getKeyword());
            String routerStr = waybillCacheService.getRouterByWaybillCode(waybillCode);
            if (StringUtils.isBlank(routerStr)) {
                return null;
            }

            String [] routerNodes = routerStr.split(WAYBILL_ROUTER_SPLITER);
            for (int i = 0; i < routerNodes.length - 1; i++) {
                int curNode = Integer.parseInt(routerNodes[i]);
                int nextNode = Integer.parseInt(routerNodes[i + 1]);
                if (curNode == startSiteId) {
                    receiveSiteCode = nextNode;
                    break;
                }
            }
        }

        if (receiveSiteCode == -1) {
            return null;
        }

        // 根据路由下一节点查询发货流向的任务
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity((long) startSiteId, (long) receiveSiteCode, request.getVehicleStatus());
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findSendVehicleDetail(detailQ);
        if (CollectionUtils.isEmpty(vehicleDetailList)) {
            String msg = String.format("该包裹没有路由下一站[%s]的发货任务", receiveSiteCode);
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
     * 组装发车任务查询条件
     * @param request
     * @return
     */
    private JyBizTaskSendVehicleEntity makeFetchCondition(SendVehicleTaskRequest request) {
        JyBizTaskSendVehicleEntity condition = new JyBizTaskSendVehicleEntity();
        condition.setStartSiteId((long) request.getCurrentOperate().getSiteCode());
        condition.setVehicleStatus(request.getVehicleStatus());
        if (NumberHelper.gt0(request.getLineType())) {
            condition.setLineType(request.getLineType());
        }

        return condition;
    }

    private boolean checkBeforeFetchTask(SendVehicleTaskRequest request, InvokeResult<SendVehicleTaskResponse> result) {
        if (!NumberHelper.gt0(request.getVehicleStatus())) {
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
        if (!NumberHelper.gt0(request.getLineType())) {
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

    private Boolean setSendVehicleBaseInfo(SendVehicleInfoRequest request, InvokeResult<SendVehicleInfo> invokeResult, JyBizTaskSendVehicleEntity sendVehicleEntity, SendVehicleInfo sendVehicleInfo) {
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
        // FIXME 车长
        sendVehicleInfo.setCarLengthStr("");

        return true;
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

        List<JySendAggsEntity> sendAggList = sendAggsService.findBySendVehicleBiz(request.getSendVehicleBizId());
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

            if (sendAggMap.containsKey(detailEntity.getTransWorkItemCode())) {
                JySendAggsEntity itemAgg = sendAggMap.get(detailEntity.getTransWorkItemCode());
                sendDestDetail.setToScanPackCount(dealMinus(itemAgg.getShouldScanCount(), itemAgg.getActualScanCount()));
                sendDestDetail.setScannedPackCount(itemAgg.getActualScanCount().longValue());
            }
            sendDestDetail.setPlanDepartTime(detailEntity.getPlanDepartTime());

            sendDestDetails.add(sendDestDetail);
        }

        return invokeResult;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.loadProgress",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendVehicleProgress> loadProgress(SendVehicleProgressRequest request) {
        InvokeResult<SendVehicleProgress> invokeResult = new InvokeResult<>();

        SendVehicleProgress progress = new SendVehicleProgress();
        progress.setLoadRate(new BigDecimal("0"));
        progress.setVolume(new BigDecimal("0"));
        progress.setWeight(new BigDecimal("0"));
        progress.setLoadVolume(new BigDecimal("0"));
        progress.setLoadWeight(new BigDecimal("0"));
        progress.setToScanCount(0L);
        progress.setScannedPackCount(0L);
        progress.setScannedBoxCount(0L);
        progress.setInterceptedPackCount(0L);
        progress.setForceSendPackCount(0L);
        progress.setSealedTotal(0);
        progress.setDestTotal(0);


        return invokeResult;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.checkSendVehicleNormalStatus",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult<SendAbnormalResponse> checkSendVehicleNormalStatus(SendAbnormalRequest request) {
        return null;
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
        return null;
    }
}
