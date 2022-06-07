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
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.BasicQueryWSManager;
import com.jd.bluedragon.core.base.JdiQueryWSManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.jy.dto.send.JyBizTaskSendCountDto;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendDetailStatusEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendSortTypeEnum;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskSendStatusEnum;
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
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
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
    private BoardCombinationService boardCombinationService;

    @Autowired
    private JyVehicleSendRelationService jySendCodeService;

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private BaseMajorManager baseMajorManager;



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
        long receiveSiteCode = -1;

        // 取当前操作网点的路由下一节点
        if (WaybillUtil.isPackageCode(request.getKeyword())) {
            receiveSiteCode = getWaybillNextRouter(WaybillUtil.getWaybillCode(request.getKeyword()), startSiteId);
        }

        if (receiveSiteCode == -1) {
            return null;
        }

        // 根据路由下一节点查询发货流向的任务
        JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity((long) startSiteId, receiveSiteCode, request.getVehicleStatus());
        List<JyBizTaskSendVehicleDetailEntity> vehicleDetailList = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(detailQ);
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
     * 获取运单路由下一节点
     * @param waybillCode
     * @param startSiteId
     * @return
     */
    private long getWaybillNextRouter(String waybillCode, int startSiteId) {
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
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "IJySendVehicleService.sendScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public JdVerifyResponse<SendScanResponse> sendScan(SendScanRequest request) {
        JdVerifyResponse<SendScanResponse> result = new JdVerifyResponse<>();

        // 校验
        if (!sendRequestBaseCheck(result, request)) {
            return result;
        }

        JyBizTaskSendVehicleEntity taskSend = taskSendVehicleService.findByBizId(request.getSendVehicleBizId());
        if (taskSend == null) {
            result.toFail("发货任务不存在！");
            return result;
        }

        if (!sendRequestBizCheck(result, request, taskSend)) {
            return result;
        }


        int siteCode = request.getCurrentOperate().getSiteCode();
        if (taskSend.getStartSiteId().intValue() != siteCode) {
            result.toFail("当前发车任务始发ID与岗位所属单位ID不一致!");
            return result;
        }

        // FIXME 调用拦截逻辑，增加类似RouterFilter，获取运单路由判断是否在车辆任务所有流向明细里，不包含则提示强发，并且生成新批次

        // 判断首次发货
        // 2. 保存批次信息
        // 3. 无任务发货生成发货流向，已经生成流向不允许再次确认流向信息
        // 4. 首次发货必须是包裹或者运单

        // FIXME 通知客户端用新目的地

        SendScanResponse response = new SendScanResponse();
        result.setData(response);

        response.setScanPackCount(0);

        // 本次扫描的目的地
        long destSiteId = getNextRouter(request);

        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId((int) destSiteId);
        response.setCurScanDestId(destSiteId);
        response.setCurScanDestName(baseSite.getSiteName());

        String sendCode = null;

        // 无任务首次发货确认目的地信息
        if (taskSend.manualCreatedTask()) {
            List<JyBizTaskSendVehicleDetailEntity> taskSendDetails = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity(taskSend.getStartSiteId(), taskSend.getBizId()));
            // 未确认目的地信息
            if (CollectionUtils.isEmpty(taskSendDetails)) {
                if (!request.getNoTaskConfirmDest()) {
                    result.toBizError();
                    result.addConfirmBox(0, "请确认目的地！");
                    return result;
                }
                else {
                    // FIXME 插入sendVehicleDetail
                }
            }
            else {
                String detailBiz = taskSendDetails.get(0).getBizId();
                // 非同流向迁移会生成新批次，一个流向不止一个批次
                List<String> sendCodeList = jySendCodeService.querySendCodesByVehicleDetailBizId(detailBiz);
                if (CollectionUtils.isEmpty(sendCodeList)) {
                    // 首次扫描生成批次
                    sendCode = generateSendCode((long) request.getCurrentOperate().getSiteCode(), destSiteId, request.getUser().getUserErp());

                    this.saveSendCode(request, sendCode, detailBiz);
                }
                else {
                    sendCode = sendCodeList.get(0);
                }
            }
        }
        else {
            List<JyBizTaskSendVehicleDetailEntity> taskSendDetails = taskSendVehicleDetailService.findEffectiveSendVehicleDetail(new JyBizTaskSendVehicleDetailEntity((long) request.getCurrentOperate().getSiteCode(), request.getSendVehicleBizId()));
            if (CollectionUtils.isEmpty(taskSendDetails)) {
                result.toFail("发货流向已作废！");
                return result;
            }
            String detailBiz = taskSendDetails.get(0).getBizId();
            // 非同流向迁移会生成新批次，一个流向不止一个批次
            List<String> sendCodeList = jySendCodeService.querySendCodesByVehicleDetailBizId(detailBiz);
            if (CollectionUtils.isEmpty(sendCodeList)) {
                // 首次扫描生成批次
                sendCode = generateSendCode((long) request.getCurrentOperate().getSiteCode(), destSiteId, request.getUser().getUserErp());

                this.saveSendCode(request, sendCode, detailBiz);
            }
            else {
                sendCode = sendCodeList.get(0);
            }
        }


        try {
            JySendEntity sendEntity = new JySendEntity();
            sendEntity.setSendVehicleBizId(request.getSendVehicleBizId());
            sendEntity.setCreateSiteId((long) siteCode);
            sendEntity.setReceiveSiteId(destSiteId);
            sendEntity.setBarCode(request.getBarCode());
            sendEntity.setSendCode(sendCode);
            sendEntity.setOperateTime(request.getCurrentOperate().getOperateTime());
            sendEntity.setInterceptFlag(0); // FIXME 拦截标识
            sendEntity.setForceSendFlag(request.getForceSubmit() ? 1 : 0);
            sendEntity.setCreateUserErp(request.getUser().getUserErp());
            sendEntity.setCreateUserName(request.getUser().getUserName());

            jySendService.add(sendEntity);


            SendM sendM = toSendMDomain(request, destSiteId, sendCode);


            // FIXME 记录扫描进度

            // FIXME 修改任务状态

        }
        catch (Exception ex) {
            log.error("发车扫描失败. {}", JsonHelper.toJson(request), ex);
            result.toError("服务器异常，发车扫描失败，请咚咚联系分拣小秘！");

            redisClientOfJy.del(getBizBarCodeCacheKey(request.getBarCode(), request.getCurrentOperate().getSiteCode()));
        }

        return result;
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
    private boolean sendRequestBizCheck(JdVerifyResponse<SendScanResponse> response, SendScanRequest request, JyBizTaskSendVehicleEntity taskSend) {
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
                if (needBindMaterialBag && StringUtils.isBlank(request.getMaterialCode())) {
                    response.toBizError();
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

        // FIXME 强制前往封车或者任务状态正常，任务状态变为待封车
        if (request.getForceGoToSeal() || response.getNormalFlag()) {

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
            response.setNormalFlag(Boolean.FALSE);
            response.setAbnormalType(SendAbnormalEnum.MANUAL_TASK_NO_BIND);
            return true;
        }

        // 当前封车流向是最后一个
        if (sealedDestCount == destList.size() - 1) {
            JySendEntity existAbnormal = jySendService.findSendRecordExistAbnormal(new JySendEntity(sendVehicleBizId));
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
