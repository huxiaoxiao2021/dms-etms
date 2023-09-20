package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.common.dto.operation.workbench.enums.TmsDistributeVehicleStatusEnum;
import com.jd.bluedragon.common.dto.send.request.*;
import com.jd.bluedragon.common.dto.send.response.*;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.PdaSorterApiManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.delivery.IDeliveryOperationService;
import com.jd.bluedragon.distribution.jy.comboard.JyGroupSortCrossDetailEntity;
import com.jd.bluedragon.distribution.jy.dto.send.BindVehicleResp;
import com.jd.bluedragon.distribution.jy.dto.send.JySendCodeDto;
import com.jd.bluedragon.distribution.jy.dto.send.TransferVehicleResp;
import com.jd.bluedragon.distribution.jy.dto.send.VehicleSendRelationDto;
import com.jd.bluedragon.distribution.jy.dto.tms.SameDestinationSendTaskDto;
import com.jd.bluedragon.distribution.jy.dto.tms.TmsUrgeVehicleMq;
import com.jd.bluedragon.distribution.jy.enums.*;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.manager.IJySendVehicleJsfManager;
import com.jd.bluedragon.distribution.jy.manager.JyScheduleTaskManager;
import com.jd.bluedragon.distribution.jy.manager.JyTransportManager;
import com.jd.bluedragon.distribution.jy.send.JySendCodeEntity;
import com.jd.bluedragon.distribution.jy.service.comboard.JyGroupSortCrossDetailService;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleDetailService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskSendVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleDetailQueryEntity;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskSendVehicleEntity;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.ThreeDeliveryResponse;
import com.jd.bluedragon.distribution.send.domain.dto.SendDetailDto;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.enums.SendStatusEnum;
import com.jd.bluedragon.utils.*;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.workbench.dto.*;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskReq;
import com.jdl.jy.schedule.dto.task.JyScheduleTaskResp;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskDistributionTypeEnum;
import com.jdl.jy.schedule.enums.task.JyScheduleTaskTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.*;
import static com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum.JY_APP;
import static com.jd.bluedragon.utils.TimeUtils.yyyyMMdd;

@Service
@Slf4j
public class JyNoTaskSendServiceImpl implements JyNoTaskSendService {
    @Autowired
    JyTransportManager jyTransportManager;
    @Autowired
    DeliveryService deliveryService;
    @Autowired
    GroupBoardManager groupBoardManager;
    @Autowired
    SendMService sendMService;
    @Autowired
    JyVehicleSendRelationService jyVehicleSendRelationService;
    @Autowired
    SendCodeService sendCodeService;
    @Autowired
    JyBizTaskSendVehicleDetailService jyBizTaskSendVehicleDetailService;
    @Autowired
    private IDeliveryOperationService deliveryOperationService;
    @Autowired
    JyBizTaskSendVehicleService jyBizTaskSendVehicleService;
    @Autowired
    @Qualifier("redisClientCache")
    protected Cluster redisClientCache;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    @Qualifier("redisJySendBizIdSequenceGen")
    private JimdbSequenceGen redisJyBizIdSequenceGen;
    @Autowired
    @Qualifier("jySendVehicleService")
    IJySendVehicleService iJySendVehicleService;
    @Autowired
    private SendDetailService sendDetailService;
    @Autowired
    SortingService sortingService;
    @Autowired
    @Qualifier("jySendVehicleService")
    private IJySendVehicleService jySendVehicleService;
    @Autowired
    BaseMajorManager baseMajorManager;
    @Autowired
    JySendTransferLogService jySendTransferLogService;
    @Autowired
    private IJySendService jySendService;
    @Autowired
    private NewSealVehicleService newsealVehicleService;
    @Autowired
    private JyScheduleTaskManager jyScheduleTaskManager;
    @Autowired
    private UccPropertyConfiguration uccConfig;
    @Autowired
    @Qualifier("jyTaskGroupMemberService")
    private JyTaskGroupMemberService taskGroupMemberService;
    private static final int SEND_SCAN_BAR_EXPIRE = 6;

    @Autowired
    private IJySendVehicleJsfManager sendVehicleJsfManager;

    @Autowired
    private JyGroupSortCrossDetailService jyGroupSortCrossDetailService;

    @Autowired
    @Qualifier("redisJyNoTaskSendDetailBizIdSequenceGen")
    private JimdbSequenceGen redisJyNoTaskSendDetailBizIdSequenceGen;

    @Autowired
    private SendVehicleTransactionManager sendVehicleTransactionManager;

    @Autowired
    private PdaSorterApiManager pdaSorterApiManager;

    @Autowired
    private DefaultJMQProducer jySendTmsUrgeVehicleProducer;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.listVehicleType", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<VehicleSpecResp>> listVehicleType() {
        CommonDto<List<BasicVehicleTypeDto>> rs = jyTransportManager.getVehicleTypeList();
        if (null != rs && rs.getCode() == Constants.RESULT_SUCCESS) {
            //按照车长做groupBy
            Map<String, List<VehicleTypeDto>> groupByVehicleLength = new HashMap<>();
            for (BasicVehicleTypeDto basicVehicleTypeDto : rs.getData()) {
                String vehicleLength = basicVehicleTypeDto.getVehicleLength();
                if (ObjectHelper.isNotNull(vehicleLength)) {
                    VehicleTypeDto vehicleTypeDto = BeanUtils.copy(basicVehicleTypeDto, VehicleTypeDto.class);
                    final BigDecimal vehicleLengthVal = new BigDecimal(vehicleLength);
                    final BigDecimal vehicleLengthGroupVal = vehicleLengthVal.divide(new BigDecimal(100), 1, RoundingMode.DOWN);
                    if (groupByVehicleLength.containsKey(vehicleLengthGroupVal.toString())) {
                        groupByVehicleLength.get(vehicleLengthGroupVal.toString()).add(vehicleTypeDto);
                    } else {
                        List<VehicleTypeDto> vehicleTypeDtoList = new ArrayList<>();
                        vehicleTypeDtoList.add(vehicleTypeDto);
                        groupByVehicleLength.put(vehicleLengthGroupVal.toString(), vehicleTypeDtoList);
                    }
                }
            }
            //封装树形结构响应体
            List<VehicleSpecResp> vehicleSpecRespList = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("###.0");
            for (Map.Entry<String, List<VehicleTypeDto>> entry : groupByVehicleLength.entrySet()) {
                String key = entry.getKey();
                List<VehicleTypeDto> value = entry.getValue();
                VehicleSpecResp vehicleSpecResp = new VehicleSpecResp();
                vehicleSpecResp.setVehicleLength(new BigDecimal(key).multiply(new BigDecimal(10)).intValue());
                vehicleSpecResp.setName(new BigDecimal(key) + "米");
                vehicleSpecResp.setVehicleTypeDtoList(value);
                vehicleSpecRespList.add(vehicleSpecResp);
            }
            Collections.sort(vehicleSpecRespList, new VehicleTypeComparator());
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, vehicleSpecRespList);
        }
        return new InvokeResult(RESULT_NODATA_GETCARTYPE_CODE, RESULT_NODATA_GETCARTYPE_MESSAGE);
    }

    class VehicleTypeComparator implements Comparator<VehicleSpecResp> {
        @Override
        public int compare(VehicleSpecResp o1, VehicleSpecResp o2) {
            return o1.getVehicleLength() - o2.getVehicleLength();
        }
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.createVehicleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InvokeResult<CreateVehicleTaskResp> createVehicleTask(CreateVehicleTaskReq createVehicleTaskReq) {
        InvokeResult<CreateVehicleTaskResp> result = new InvokeResult<>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
        CreateVehicleTaskResp createVehicleTaskResp = new CreateVehicleTaskResp();
        result.setData(createVehicleTaskResp);
        try {

            // 查询是否有相同流向的运输任务，有则提示 是否跳转到该发货任务，无则提示是否跳转至加车申请页面
            Result<SameDestinationSendTaskDto> checkResult = null;
            if(createVehicleTaskReq.getDestinationSiteId() != null) {
                createVehicleTaskResp.setHasSameDestinationTask(false);
                createVehicleTaskResp.setHasSameDestinationTaskOfTms(false);
                checkResult = this.checkHasSameDestinationTmsTask(createVehicleTaskReq);
                if (checkResult.getData() != null) {
                    if(checkResult.getData().getJyBizTaskSendVehicleEntity() != null){
                        createVehicleTaskResp.setHasSameDestinationTask(true);
                    }
                    if(checkResult.getData().getTmsTransJobBillDto() != null){
                        createVehicleTaskResp.setHasSameDestinationTaskOfTms(true);
                    }
                }
                if (!Objects.equals(createVehicleTaskReq.getConfirmCreate(), true)) {
                    if(createVehicleTaskResp.getHasSameDestinationTask() || (!createVehicleTaskResp.getHasSameDestinationTask() && !createVehicleTaskResp.getHasSameDestinationTaskOfTms())){
                        return result;
                    }
                }
            }

            JyBizTaskSendVehicleEntity jyBizTaskSendVehicleEntity = initJyBizTaskSendVehicle(createVehicleTaskReq);
            jyBizTaskSendVehicleService.saveSendVehicleTask(jyBizTaskSendVehicleEntity);

            if(createVehicleTaskReq.getDestinationSiteId() != null){
                final JyBizTaskSendVehicleDetailEntity jyBizTaskSendVehicleDetailEntity = this.initJyBizTaskSendVehicleDetail(createVehicleTaskReq, jyBizTaskSendVehicleEntity);
                jyBizTaskSendVehicleDetailService.saveTaskSendDetail(jyBizTaskSendVehicleDetailEntity);
            }

            createVehicleTaskResp.setBizId(jyBizTaskSendVehicleEntity.getBizId());
            createVehicleTaskResp.setBizNo(jyBizTaskSendVehicleEntity.getBizNo());
            createVehicleTaskResp.setTaskName(jyBizTaskSendVehicleEntity.getTaskName());
            createVehicleTaskResp.setCreateUserErp(createVehicleTaskReq.getUser().getUserErp());
            // 创建发货调度任务
            if (uccConfig.getSyncScheduleTaskSwitch() && !createSendScheduleTask(jyBizTaskSendVehicleEntity)){
                log.error("创建发货调度任务失败！bizId:{}",jyBizTaskSendVehicleEntity.getBizId());
                result.error("创建任务失败！");
            }

            if(createVehicleTaskReq.getDestinationSiteId() != null) {
                if(checkResult == null){
                    checkResult = this.checkHasSameDestinationTmsTask(createVehicleTaskReq);
                }
                // 发送催派任务
                if (checkResult.getData() != null && checkResult.getData().getTmsTransJobBillDto() != null) {
                    this.sendTmsUrgeVehicleMq(createVehicleTaskReq, jyBizTaskSendVehicleEntity, checkResult.getData().getTmsTransJobBillDto());
                }
            }
        } catch (JyBizException e) {
            log.error("JyNoTaskSendServiceImpl createVehicleTask ", e);
            result.error("系统异常");
        }

        return result;
    }

    private Result<SameDestinationSendTaskDto> checkHasSameDestinationTmsTask(CreateVehicleTaskReq createVehicleTaskReq) {
        final SameDestinationSendTaskDto sameDestinationSendTaskDto = new SameDestinationSendTaskDto();
        Result<SameDestinationSendTaskDto> result = Result.success(sameDestinationSendTaskDto);

        final User user = createVehicleTaskReq.getUser();
        final CurrentOperate currentOperate = createVehicleTaskReq.getCurrentOperate();
        // 先查分拣自己的待发货任务
        JyBizTaskSendVehicleDetailQueryEntity detailQueryEntity = new JyBizTaskSendVehicleDetailQueryEntity();
        detailQueryEntity.setLastPlanDepartTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -uccConfig.getJySendTaskPlanTimeBeginDay()));
        detailQueryEntity.setStartSiteId((long)currentOperate.getSiteCode());
        detailQueryEntity.setEndSiteId(createVehicleTaskReq.getDestinationSiteId());
        detailQueryEntity.setLastPlanDepartTimeEnd(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), uccConfig.getJySendTaskPlanTimeEndDay()));
        detailQueryEntity.setCreateTimeBegin(DateHelper.addDate(DateHelper.getCurrentDayWithOutTimes(), -uccConfig.getJySendTaskCreateTimeBeginDay()));
        List<Integer> lineTypeList = Arrays.asList(JyLineTypeEnum.TRUNK_LINE.getCode(), JyLineTypeEnum.BRANCH_LINE.getCode());
        detailQueryEntity.setLineTypeList(lineTypeList);
        List<Integer> statusList = new ArrayList<>(Arrays.asList(JyBizTaskSendStatusEnum.TO_SEND.getCode()));
        final List<JyBizTaskSendVehicleEntity> existSendTaskList = jyBizTaskSendVehicleService.findSendTaskByDestAndStatusesWithPage(detailQueryEntity, statusList, 1, 1);

        // JyBizTaskSendVehicleDetailEntity detailQ = new JyBizTaskSendVehicleDetailEntity((long)currentOperate.getSiteCode(), createVehicleTaskReq.getDestinationSiteId());
        // final List<JyBizTaskSendVehicleDetailEntity> sendVehicleDetailExistList = jyBizTaskSendVehicleDetailService.findBySiteAndStatus(detailQ, statusList);
        if (CollectionUtils.isNotEmpty(existSendTaskList)) {
            sameDestinationSendTaskDto.setJyBizTaskSendVehicleEntity(existSendTaskList.get(0));
            return result;
        }
        // 再查运输的任务
        AccountDto accountDto = getAccountDto(user.getUserErp(), user.getUserName());
        TransJobPdaQueryDto queryDto = getTransJobPdaQueryDto(createVehicleTaskReq, result);
        if(queryDto == null || result.isFail()){
            return result;
        }

        // 暂定一次查50条
        PageDto<TmsTransJobBillDto> pageDto = new PageDto<>();
        pageDto.setCurrentPage(1);
        pageDto.setPageSize(50);

        com.jd.tms.workbench.dto.CommonDto<PageDto<TmsTransJobBillDto>> commonDto = pdaSorterApiManager.queryTransJobPage(accountDto, queryDto, pageDto);
        /*commonDto = new com.jd.tms.workbench.dto.CommonDto<>();
        final PageDto<TmsTransJobBillDto> tmsTransJobBillDtoPageDto = new PageDto<>();
        commonDto.setData(tmsTransJobBillDtoPageDto);
        tmsTransJobBillDtoPageDto.setTotalRow(1);
        final TmsTransJobBillDto tmsTransJobBillDto = new TmsTransJobBillDto();
        tmsTransJobBillDto.setAccountCode("wuyoude");
        tmsTransJobBillDto.setTransportCode("testTransportCode");
        tmsTransJobBillDto.setTransJobCode("testTransJobCode");
        List<TmsTransJobBillDto> tmsTransJobBillDtos = new ArrayList<TmsTransJobBillDto>(){{
            add(tmsTransJobBillDto);
        }};
        tmsTransJobBillDtoPageDto.setResult(tmsTransJobBillDtos);*/
        if (commonDto == null) {
            log.error("checkHasSameDestinationTmsTask call queryTransJobPage empty {} {} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(queryDto), JsonHelper.toJson(pageDto));
            return result.toFail("获取待派车列表异常！");
        }
        if (!Objects.equals(commonDto.getCode(), com.jd.tms.workbench.dto.CommonDto.CODE_SUCCESS)) {
            log.error("checkHasSameDestinationTmsTask call queryTransJobPage fail {} {} {} {}", JsonHelper.toJson(commonDto), JsonHelper.toJson(accountDto), JsonHelper.toJson(queryDto), JsonHelper.toJson(pageDto));
            return result.toFail("获取待派车列表失败！");
        }
        final PageDto<TmsTransJobBillDto> pageData = commonDto.getData();
        if (pageData != null && CollectionUtils.isNotEmpty(pageData.getResult())) {
            sameDestinationSendTaskDto.setTmsTransJobBillDto(pageData.getResult().get(0));
        }
        return result;
    }

    private AccountDto getAccountDto(String userErp, String userName) {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountCode(userErp);
        accountDto.setAccountName(userName);
        accountDto.setAccountType(Constants.TMS_INTERNAL_ERP_ACCOUNT_TYPE);
        return accountDto;
    }

    private TransJobPdaQueryDto getTransJobPdaQueryDto(CreateVehicleTaskReq createVehicleTaskReq, Result<SameDestinationSendTaskDto> result) {
        TransJobPdaQueryDto queryDto = new TransJobPdaQueryDto();
        final CurrentOperate currentOperate = createVehicleTaskReq.getCurrentOperate();
        BaseStaffSiteOrgDto sourceSite = baseMajorManager.getBaseSiteBySiteId(currentOperate.getSiteCode());
        if (sourceSite == null) {
            log.error("getTransJobPdaQueryDto get sourceSite is null{}", JsonHelper.toJson(createVehicleTaskReq));
            result.toFail(String.format("根据操作场地%s未查询到场地信息", currentOperate.getSiteCode()));
            return null;
        }
        Date now = new Date();
        queryDto.setBeginNodeCode(sourceSite.getDmsSiteCode());
        queryDto.setPlanDepartTimeBegin(new Date());
        queryDto.setPlanDepartTimeEnd(DateUtils.addHours(now, uccConfig.getFetchCarDistributionTimeRange() != null ? uccConfig.getFetchCarDistributionTimeRange(): 48));
        queryDto.setTransTypeList(new ArrayList<>(Arrays.asList(TmsLineTypeEnum.TRUNK_LINE.getCode(), TmsLineTypeEnum.BRANCH_LINE.getCode())));

        // 目的网点非必填
        if (createVehicleTaskReq.getDestinationSiteId() != null) {
            BaseStaffSiteOrgDto destSite = baseMajorManager.getBaseSiteBySiteId(createVehicleTaskReq.getDestinationSiteId().intValue());
            if (destSite == null) {
                log.error("getTransJobPdaQueryDto get sourceSite is null{}", JsonHelper.toJson(createVehicleTaskReq));
                result.toFail(String.format("根据目的场地%s未查询到场地信息", createVehicleTaskReq.getDestinationSiteId()));
                return null;
            }
            queryDto.setEndNodeCode(destSite.getDmsSiteCode());
        }

        List<Integer> statusList = new ArrayList<>();
        statusList.add(TmsDistributeVehicleStatusEnum.INIT.getCode());
        statusList.add(TmsDistributeVehicleStatusEnum.CONFIRMED.getCode());
        queryDto.setStatusList(statusList);
        return queryDto;
    }

    /**
     * 发送催派消息
     * @param createVehicleTaskReq 创建任务请求入参
     * @param jyBizTaskSendVehicleEntity
     * @param tmsTransJobBillDto
     */
    private void sendTmsUrgeVehicleMq(CreateVehicleTaskReq createVehicleTaskReq, JyBizTaskSendVehicleEntity jyBizTaskSendVehicleEntity, TmsTransJobBillDto tmsTransJobBillDto) {
        try {
            final User user = createVehicleTaskReq.getUser();
            final CurrentOperate currentOperate = createVehicleTaskReq.getCurrentOperate();
            final TmsUrgeVehicleMq tmsUrgeVehicleMq = new TmsUrgeVehicleMq();
            tmsUrgeVehicleMq.setUserCode(user.getUserErp());
            tmsUrgeVehicleMq.setUserId((long)user.getUserCode());
            tmsUrgeVehicleMq.setUserName(user.getUserName());
            tmsUrgeVehicleMq.setOperateTime(System.currentTimeMillis());

            tmsUrgeVehicleMq.setSiteCode(currentOperate.getSiteCode());
            tmsUrgeVehicleMq.setSiteName(currentOperate.getSiteName());

            tmsUrgeVehicleMq.setTransJobCode(tmsTransJobBillDto.getTransJobCode());
            if (CollectionUtils.isNotEmpty(tmsTransJobBillDto.getTransJobItemDtoList())) {
                final TmsTransJobItemDto tmsTransJobItemDto = tmsTransJobBillDto.getTransJobItemDtoList().get(0);
                tmsUrgeVehicleMq.setTransportCode(tmsTransJobItemDto.getTransportCode());
            }
            tmsUrgeVehicleMq.setBizId(jyBizTaskSendVehicleEntity.getBizId());

            jySendTmsUrgeVehicleProducer.sendOnFailPersistent(jyBizTaskSendVehicleEntity.getBizId(), JsonHelper.toJson(tmsUrgeVehicleMq));
        } catch (Exception e) {
            log.error("sendTmsUrgeVehicleMq exception {} {}", JsonHelper.toJson(jyBizTaskSendVehicleEntity), JsonHelper.toJson(tmsTransJobBillDto), e);
        }
    }

    /**
     * 创建发货调度任务
     * @param sendVehicleEntity
     * @return
     */
    public boolean createSendScheduleTask(JyBizTaskSendVehicleEntity sendVehicleEntity){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(sendVehicleEntity.getBizId());
        req.setTaskType(JyScheduleTaskTypeEnum.SEND.getCode());
        req.setOpeUser(sendVehicleEntity.getCreateUserErp());
        req.setOpeUserName(sendVehicleEntity.getCreateUserName());
        req.setOpeTime(new Date());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.createScheduleTask(req);
        return jyScheduleTaskResp != null;
    }

    /**
     * 关闭调度任务
     * @param entity
     * @return
     */
    private boolean closeScheduleTask(JyBizTaskSendVehicleEntity entity){
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(entity.getBizId());
        req.setTaskType(String.valueOf(JyScheduleTaskTypeEnum.SEND.getCode()));
        req.setOpeUser(entity.getUpdateUserErp());
        req.setOpeUserName(entity.getUpdateUserName());
        req.setOpeTime(new Date());
        JyScheduleTaskResp jyScheduleTaskResp = jyScheduleTaskManager.closeScheduleTask(req);
        return jyScheduleTaskResp != null;
    }

    private void finishTaskGroup(JyBizTaskSendVehicleDetailEntity sendStatusQ) {
        String taskId = getJyScheduleTaskId(sendStatusQ.getSendVehicleBizId());
        if (StringUtils.isBlank(taskId)) {
            return;
        }
        JyTaskGroupMemberEntity endData = new JyTaskGroupMemberEntity();
        endData.setRefTaskId(taskId);
        endData.setUpdateUser(sendStatusQ.getUpdateUserErp());
        endData.setUpdateUserName(sendStatusQ.getUpdateUserName());
        endData.setUpdateTime(sendStatusQ.getUpdateTime());
        Result<Boolean> result = taskGroupMemberService.endTask(endData);

        if (log.isInfoEnabled()){
            log.info("封车完成关闭小组任务. data:{}, result:{}", JsonHelper.toJson(endData), JsonHelper.toJson(result));
        }
    }
    private JyBizTaskSendVehicleEntity initJyBizTaskSendVehicle(CreateVehicleTaskReq createVehicleTaskReq) {
        JyBizTaskSendVehicleEntity entity = new JyBizTaskSendVehicleEntity();
        entity.setBizId(genMainTaskBizId());
        String bizNo = genSendVehicleTaskBizNo(createVehicleTaskReq);
        entity.setBizNo(bizNo);
        final User user = createVehicleTaskReq.getUser();
        String userName = StringUtils.isNotBlank(user.getUserName()) ?
                (user.getUserName().length() > 4 ? user.getUserName().substring(0, 4) : user.getUserName()) : "";
        entity.setTaskName(userName + "自建" + entity.getBizNo());
        entity.setStartSiteId(Long.valueOf(createVehicleTaskReq.getCurrentOperate().getSiteCode()));
        entity.setManualCreatedFlag(1);
        entity.setVehicleType(createVehicleTaskReq.getVehicleType());
        entity.setVehicleTypeName(createVehicleTaskReq.getVehicleTypeName());
        entity.setCreateUserErp(createVehicleTaskReq.getUser().getUserErp());
        entity.setCreateUserName(createVehicleTaskReq.getUser().getUserName());
        entity.setYn(0);
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        if(createVehicleTaskReq.getDestinationSiteId() != null) {
            entity.setYn(Constants.YN_YES);
        }
        return entity;
    }

    private JyBizTaskSendVehicleDetailEntity initJyBizTaskSendVehicleDetail(CreateVehicleTaskReq createVehicleTaskReq, JyBizTaskSendVehicleEntity jyBizTaskSendVehicleEntity) {
        JyBizTaskSendVehicleDetailEntity noTaskDetail = new JyBizTaskSendVehicleDetailEntity();
        noTaskDetail.setSendVehicleBizId(jyBizTaskSendVehicleEntity.getBizId());
        noTaskDetail.setBizId(genNoTaskBizId());
        noTaskDetail.setTransWorkItemCode(StringUtils.EMPTY);
        noTaskDetail.setVehicleStatus(JyBizTaskSendDetailStatusEnum.TO_SEND.getCode());
        noTaskDetail.setStartSiteId(jyBizTaskSendVehicleEntity.getStartSiteId());

        BaseStaffSiteOrgDto startSite = baseMajorManager.getBaseSiteBySiteId(jyBizTaskSendVehicleEntity.getStartSiteId().intValue());
        noTaskDetail.setStartSiteName(startSite == null ? StringUtils.EMPTY : startSite.getSiteName());

        noTaskDetail.setEndSiteId(createVehicleTaskReq.getDestinationSiteId());
        BaseStaffSiteOrgDto endSite = baseMajorManager.getBaseSiteBySiteId(noTaskDetail.getEndSiteId().intValue());
        noTaskDetail.setEndSiteName(endSite == null ? StringUtils.EMPTY : endSite.getSiteName());

        Date noTaskPlanDate = new Date();
        noTaskDetail.setPlanDepartTime(noTaskPlanDate);
        noTaskDetail.setCreateUserErp("sys.dms");
        noTaskDetail.setCreateUserName("sys.dms");
        return noTaskDetail;
    }

    private String genNoTaskBizId() {
        String ownerKey = String.format(JyBizTaskSendVehicleDetailEntity.NO_TASK_BIZ_PREFIX, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyNoTaskSendDetailBizIdSequenceGen.gen(ownerKey));
    }

    private boolean distributeAndStartScheduleTask(BindVehicleDetailTaskReq request) {
        JyScheduleTaskReq req = new JyScheduleTaskReq();
        req.setBizId(request.getToSendVehicleBizId());
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


    private boolean taskSendFirstScan(BindVehicleDetailTaskReq request) {
        boolean firstScanned = false;
        String mutexKey = getSendTaskBizCacheKey(request.getToSendVehicleBizId());
        String requestId =UUID.randomUUID().toString().replace("-","");
        if (redisClientOfJy.set(mutexKey,requestId , SEND_SCAN_BAR_EXPIRE, TimeUnit.HOURS, false)) {
            firstScanned = true;
            if (log.isInfoEnabled()){
                log.info("单号是发车任务的首次扫描. [{}], {}", mutexKey, JsonHelper.toJson(request));
            }
        }
        return firstScanned;
    }

    public String getSendTaskBizCacheKey(String sendVehicleBiz) {
        return String.format(CacheKeyConstants.JY_SEND_TASK_FIRST_SCAN_KEY, sendVehicleBiz);
    }
    private void recordTaskMembers(BindVehicleDetailTaskReq request) {
        JyTaskGroupMemberEntity startData = new JyTaskGroupMemberEntity();
        startData.setRefGroupCode(request.getGroupCode());
        startData.setRefTaskId(getJyScheduleTaskId(request.getToSendVehicleBizId()));

        startData.setSiteCode(request.getCurrentOperate().getSiteCode());
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(startData.getSiteCode());
        startData.setOrgCode(baseSite != null ? baseSite.getOrgId() : -1);
        startData.setProvinceAgencyCode(baseSite == null ? null : baseSite.getProvinceAgencyCode());
        startData.setAreaHubCode(baseSite == null ? null : baseSite.getAreaCode());

        startData.setCreateUser(request.getUser().getUserErp());
        startData.setCreateUserName(request.getUser().getUserName());

        Result<Boolean> startTask = taskGroupMemberService.startTask(startData);
        if (log.isInfoEnabled()) {
            log.info("发货任务首次扫描记录组员. {}, {}", request.getToSendVehicleBizId(), JsonHelper.toJson(startTask));}
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

    private String genSendVehicleTaskBizNo(CreateVehicleTaskReq createVehicleTaskReq) {
        String bizNoKey = "bizNo:" + createVehicleTaskReq.getCurrentOperate().getSiteCode() + ":" + TimeUtils.date2string(new Date(), yyyyMMdd + ":");
        long bizNo = 0;
        if (!ObjectHelper.isNotNull(redisClientCache.get(bizNoKey))) {
            redisClientCache.set(bizNoKey, "0", 24 * 60, TimeUnit.MINUTES, false);
        }
        try {
            bizNo = redisClientCache.incr(bizNoKey);
        } catch (Exception e) {
            return "";
        }
        return String.valueOf(bizNo);
    }

    private String genMainTaskBizId() {
        String ownerKey = String.format(JyBizTaskSendVehicleEntity.BIZ_PREFIX_NOTASK, DateHelper.formatDate(new Date(), DateHelper.DATE_FORMATE_yyMMdd));
        return ownerKey + StringHelper.padZero(redisJyBizIdSequenceGen.gen(ownerKey));
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.deleteVehicleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InvokeResult deleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        log.info("删除自建任务,deleteVehicleTaskReq:{}", JsonHelper.toJson(deleteVehicleTaskReq));
        JyBizTaskSendVehicleEntity task = jyBizTaskSendVehicleService.findByBizId(deleteVehicleTaskReq.getBizId());
        if (task.hasBeenBindedOrDeleted()) {
            return new InvokeResult(NO_RE_DETELE_TASK_CODE, NO_RE_DETELE_TASK_MESSAGE);
        }
        //删除主任务
        JyBizTaskSendVehicleEntity entity = new JyBizTaskSendVehicleEntity();
        entity.setBizId(deleteVehicleTaskReq.getBizId());
        entity.setYn(0);
        Date now = new Date();
        entity.setUpdateTime(now);
        entity.setUpdateUserErp(deleteVehicleTaskReq.getUser().getUserErp());
        entity.setUpdateUserName(deleteVehicleTaskReq.getUser().getUserName());
        jyBizTaskSendVehicleService.updateSendVehicleTask(entity);
        //删除子任务
        JyBizTaskSendVehicleDetailEntity detailEntity = new JyBizTaskSendVehicleDetailEntity();
        detailEntity.setSendVehicleBizId(deleteVehicleTaskReq.getBizId());
        detailEntity.setYn(0);
        detailEntity.setUpdateTime(now);
        detailEntity.setUpdateUserErp(deleteVehicleTaskReq.getUser().getUserErp());
        detailEntity.setUpdateUserName(deleteVehicleTaskReq.getUser().getUserName());
        jyBizTaskSendVehicleDetailService.updateDateilTaskByVehicleBizId(detailEntity);
        //关闭调度任务
        if (uccConfig.getSyncScheduleTaskSwitch()){
            if (!closeScheduleTask(entity)){
                log.error("删除自建任务-同步关闭发货调度任务失败！bizId:{}",entity.getBizId());
                throw new JyBizException("删除任务失败！");
            }
            finishTaskGroup(detailEntity);
        }
        //删除任务-发货绑定关系+取消发货
        List<String> sendCodeList = jyVehicleSendRelationService.querySendCodesByVehicleBizId(deleteVehicleTaskReq.getBizId());
        if (ObjectHelper.isNotNull(sendCodeList) && sendCodeList.size() > 0) {
            JySendCodeDto dto = new JySendCodeDto();
            dto.setSendVehicleBizId(deleteVehicleTaskReq.getBizId());
            dto.setUpdateUserErp(deleteVehicleTaskReq.getUser().getUserErp());
            dto.setUpdateUserName(deleteVehicleTaskReq.getUser().getUserName());
            jyVehicleSendRelationService.deleteVehicleSendRelationByVehicleBizId(dto);

            for (String sendCode : sendCodeList) {
                SendM sendM = new SendM();
                sendM.setSendCode(sendCode);
                sendM.setCreateSiteCode(deleteVehicleTaskReq.getCurrentOperate().getSiteCode());
                sendM.setUpdateTime(new Date());
                sendM.setUpdaterUser(deleteVehicleTaskReq.getUser().getUserName());
                sendM.setUpdateUserCode(deleteVehicleTaskReq.getUser().getUserCode());
                //如果已封车的批次不触发取消发货
                if (!newsealVehicleService.newCheckSendCodeSealed(sendCode, new StringBuffer())) {
                    ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
                    if (!tDResponse.getCode().equals(RESULT_SUCCESS_CODE)) {
                        return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage());
                    }
                }

            }
        }
        return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
    }
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.checkBeforeDeleteVehicleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<DeleteVehicleTaskCheckResponse> checkBeforeDeleteVehicleTask(DeleteVehicleTaskReq deleteVehicleTaskReq) {
        log.info("删除自建任务前校验,deleteVehicleTaskReq:{}",JsonHelper.toJson(deleteVehicleTaskReq));
        JyBizTaskSendVehicleEntity task =jyBizTaskSendVehicleService.findByBizId(deleteVehicleTaskReq.getBizId());
        if (task.hasBeenBindedOrDeleted()){
            return new InvokeResult<DeleteVehicleTaskCheckResponse>(NO_RE_DETELE_TASK_CODE, NO_RE_DETELE_TASK_MESSAGE);
        }
        InvokeResult<DeleteVehicleTaskCheckResponse> result = new InvokeResult<DeleteVehicleTaskCheckResponse>(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE);
        DeleteVehicleTaskCheckResponse resultData = new DeleteVehicleTaskCheckResponse();
        resultData.setNeedCheckPassword(Boolean.FALSE);
        result.setData(resultData);
        //查询任务下的批次
        List<String> sendCodeList = jyVehicleSendRelationService.querySendCodesByVehicleBizId(deleteVehicleTaskReq.getBizId());
        if (ObjectHelper.isNotNull(sendCodeList) && sendCodeList.size() > 0) {
            for (String sendCode : sendCodeList) {
                SendM sendM = new SendM();
                sendM.setSendCode(sendCode);
                sendM.setCreateSiteCode(deleteVehicleTaskReq.getCurrentOperate().getSiteCode());
                sendM.setUpdateTime(new Date());
                sendM.setUpdaterUser(deleteVehicleTaskReq.getUser().getUserName());
                sendM.setUpdateUserCode(deleteVehicleTaskReq.getUser().getUserCode());
                //如果存在批次未封车，需要输入密码才能删除
                if (!newsealVehicleService.newCheckSendCodeSealed(sendCode, new StringBuffer())) {
                	resultData.setNeedCheckPassword(Boolean.TRUE);
                	return result;
                }

            }
        }
        return result;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.listVehicleTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> listVehicleTask(VehicleTaskReq vehicleTaskReq) {
        return jySendVehicleService.fetchSendTaskForBinding(vehicleTaskReq);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.listVehicleTaskSupportTransfer", mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<VehicleTaskResp> listVehicleTaskSupportTransfer(TransferVehicleTaskReq transferVehicleTaskReq) {
        if (ObjectHelper.isNotNull(transferVehicleTaskReq.getBarCode())){
            return jySendVehicleService.fetchSendTaskForTransferV2(transferVehicleTaskReq);
        }
        return jySendVehicleService.fetchSendTaskForTransfer(transferVehicleTaskReq);
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.bindVehicleDetailTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InvokeResult bindVehicleDetailTask(BindVehicleDetailTaskReq bindVehicleDetailTaskReq) {
        log.info("自建任务绑定运输任务,bindVehicleDetailTaskReq:{}", JsonHelper.toJson(bindVehicleDetailTaskReq));
        JyBizTaskSendVehicleEntity task = jyBizTaskSendVehicleService.findByBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
        if (task.hasBeenBindedOrDeleted()) {
            return new InvokeResult(NO_RE_BIND_TASK_CODE, NO_RE_BIND_TASK_MESSAGE);
        }
        //更新任务与发货批次的关联关系
        List<String> sendCodeList = jyVehicleSendRelationService.querySendCodesByVehicleBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
        if (ObjectHelper.isNotNull(sendCodeList) && sendCodeList.size() > 0) {
            JyBizTaskSendVehicleDetailEntity queryFromDetailTaskParams = new JyBizTaskSendVehicleDetailEntity();
            queryFromDetailTaskParams.setSendVehicleBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
            queryFromDetailTaskParams.setStartSiteId(Long.valueOf(bindVehicleDetailTaskReq.getCurrentOperate().getSiteCode()));
            JyBizTaskSendVehicleDetailEntity fromSvdTask = jyBizTaskSendVehicleDetailService.findSendDetail(queryFromDetailTaskParams);
            if (!ObjectHelper.isNotNull(fromSvdTask)) {
                return new InvokeResult(DETAIL_TASK_NO_FOUND_BY_MAIN_TASK_CODE, DETAIL_TASK_NO_FOUND_BY_MAIN_TASK_MESSAGE);
            }
            if (jySendVehicleService.checkIfSealed(fromSvdTask)) {
                return new InvokeResult(FORBID_BIND_FOR_SEALED_DETAIL_CODE, FORBID_BIND_FOR_SEALED_DETAIL_MESSAGE);
            }

            JyBizTaskSendVehicleDetailEntity queryToDetailTaskParams = new JyBizTaskSendVehicleDetailEntity();
            queryToDetailTaskParams.setSendVehicleBizId(bindVehicleDetailTaskReq.getToSendVehicleBizId());
            queryToDetailTaskParams.setStartSiteId(Long.valueOf(bindVehicleDetailTaskReq.getCurrentOperate().getSiteCode()));
            queryToDetailTaskParams.setEndSiteId(fromSvdTask.getEndSiteId());
            List<JyBizTaskSendVehicleDetailEntity> taskSendDetails = jyBizTaskSendVehicleDetailService.findEffectiveSendVehicleDetail(queryToDetailTaskParams);
            JyBizTaskSendVehicleDetailEntity toSvdTask =jySendVehicleService.pickUpOneUnSealedDetail(taskSendDetails,fromSvdTask.getEndSiteId());
            if (!ObjectHelper.isNotNull(toSvdTask)) {
                return new InvokeResult(FORBID_BIND_TO_SEALED_DETAIL_CODE, FORBID_BIND_TO_SEALED_DETAIL_MESSAGE);
            }
            VehicleSendRelationDto dto = BeanUtils.copy(bindVehicleDetailTaskReq, VehicleSendRelationDto.class);
            dto.setFromSendVehicleDetailBizId(fromSvdTask.getBizId());
            dto.setToSendVehicleDetailBizId(toSvdTask.getBizId());
            dto.setSendCodes(sendCodeList);
            dto.setUpdateUserErp(bindVehicleDetailTaskReq.getUser().getUserErp());
            dto.setUpdateUserName(bindVehicleDetailTaskReq.getUser().getUserName());
            dto.setCreateSiteId(Long.valueOf(bindVehicleDetailTaskReq.getCurrentOperate().getSiteCode()));
            dto.setSameWayFlag(true);
            dto.setBindFlag(true);
            dto.setSource(TransferLogTypeEnum.SAME_WAY_BIND.getCode());
            jyVehicleSendRelationService.updateVehicleSendRelation(dto);
            jySendTransferLogService.saveTransferLog(dto);
            jySendService.updateTransferProperBySendCode(dto);

            //更新流向任务和主任的状态-为发货状态
            JyBizTaskSendVehicleEntity toSvTask = new JyBizTaskSendVehicleEntity();
            toSvTask.setBizId(dto.getToSendVehicleBizId());
            toSvTask.setVehicleStatus(JyBizTaskSendStatusEnum.SENDING.getCode());
            toSvTask.setPreVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
            jyBizTaskSendVehicleService.updateBizTaskSendStatus(toSvTask);

            JyBizTaskSendVehicleDetailEntity toSvDetailTask = new JyBizTaskSendVehicleDetailEntity();
            toSvDetailTask.setBizId(dto.getToSendVehicleDetailBizId());
            toSvDetailTask.setVehicleStatus(JyBizTaskSendStatusEnum.SENDING.getCode());
            toSvDetailTask.setPreVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
            jyBizTaskSendVehicleDetailService.updateBizTaskSendDetailStatus(toSvDetailTask);

            //删除自建主任务
            JyBizTaskSendVehicleEntity fromSvTask = new JyBizTaskSendVehicleEntity();
            fromSvTask.setBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
            fromSvTask.setYn(Constants.YN_NO);
            fromSvTask.setBindFlag(Constants.YN_YES);
            Date now = new Date();
            fromSvTask.setUpdateTime(now);
            fromSvTask.setUpdateUserErp(bindVehicleDetailTaskReq.getUser().getUserErp());
            fromSvTask.setUpdateUserName(bindVehicleDetailTaskReq.getUser().getUserName());
            jyBizTaskSendVehicleService.updateSendVehicleTask(fromSvTask);
            //删除自建子任务
            JyBizTaskSendVehicleDetailEntity fromSvDetailTask = new JyBizTaskSendVehicleDetailEntity();
            fromSvDetailTask.setSendVehicleBizId(bindVehicleDetailTaskReq.getFromSendVehicleBizId());
            fromSvDetailTask.setYn(Constants.YN_NO);
            fromSvDetailTask.setUpdateTime(now);
            fromSvDetailTask.setUpdateUserErp(bindVehicleDetailTaskReq.getUser().getUserErp());
            fromSvDetailTask.setUpdateUserName(bindVehicleDetailTaskReq.getUser().getUserName());
            jyBizTaskSendVehicleDetailService.updateDateilTaskByVehicleBizId(fromSvDetailTask);

            //接货仓发货岗任务绑定or迁移时，被迁移流向从混扫任务中删除
            if(JyFuncCodeEnum.WAREHOUSE_SEND_POSITION.getCode().equals(bindVehicleDetailTaskReq.getPost())) {
                JyGroupSortCrossDetailEntity sortCrossDetailEntity = new JyGroupSortCrossDetailEntity();
                sortCrossDetailEntity.setStartSiteId((long)bindVehicleDetailTaskReq.getCurrentOperate().getSiteCode());
                sortCrossDetailEntity.setFuncType(bindVehicleDetailTaskReq.getPost());
                sortCrossDetailEntity.setGroupCode(bindVehicleDetailTaskReq.getGroupCode());
                sortCrossDetailEntity.setSendVehicleDetailBizId(fromSvdTask.getBizId());
                sortCrossDetailEntity.setUpdateTime(now);
                sortCrossDetailEntity.setUpdateUserErp("system");//修改人system  区分是服务端逻辑系统删除（绑定或迁移触发）非操作人指定删除
                if(log.isInfoEnabled()) {
                    log.info("任务绑定时，将被绑定流向【{}】从【接货仓发货岗混扫任务】中删除，request={}", fromSvdTask.getBizId(), JsonHelper.toJson(bindVehicleDetailTaskReq));
                }
                jyGroupSortCrossDetailService.deleteBySiteAndBizId(sortCrossDetailEntity);
            }

            //关闭调度任务
            if (uccConfig.getSyncScheduleTaskSwitch()){
                if (!closeScheduleTask(fromSvTask)){
                    log.error("绑定-同步关闭发货调度任务失败！bizId:{}",fromSvTask.getBizId());
                    throw new JyBizException("绑定运输任务失败！");
                }
                finishTaskGroup(fromSvDetailTask);
                if (ObjectHelper.isNotNull(bindVehicleDetailTaskReq.getGroupCode()) && taskSendFirstScan(bindVehicleDetailTaskReq)){
                    if (distributeAndStartScheduleTask(bindVehicleDetailTaskReq)) {
                        recordTaskMembers(bindVehicleDetailTaskReq);
                    }
                    else {
                        log.error("绑定-同步给运输任务分配调度任务失败！bizId:{}", toSvTask.getBizId());
                        //throw new JyBizException("绑定运输任务失败！");
                    }
                }
            }

            BindVehicleResp bindVehicleResp = new BindVehicleResp();
            bindVehicleResp.setFromSendVehicleDetailBizId(fromSvdTask.getBizId());
            bindVehicleResp.setToSendVehicleDetailBizId(toSvdTask.getBizId());
            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, bindVehicleResp);
        }
        return new InvokeResult(NO_SEND_DATA_UNDER_TASK_CODE, NO_SEND_DATA_UNDER_TASK_MESSAGE);
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.JyNoTaskSendServiceImpl.transferSendTask", mState = {JProEnum.TP, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public InvokeResult transferSendTask(TransferSendTaskReq transferSendTaskReq) {
        log.info("任务迁移,transferSendTaskReq:{}", JsonHelper.toJson(transferSendTaskReq));
        //查询要迁移的批次信息-sendCodes
        JyBizTaskSendVehicleDetailEntity fromSvd = jyBizTaskSendVehicleDetailService.findByBizId(transferSendTaskReq.getFromSendVehicleDetailBizId());
        if (jySendVehicleService.checkIfSealed(fromSvd)) {
            return new InvokeResult(FORBID_TRANS_FRROM_SEALED_DETAIL_CODE, FORBID_TRANS_FRROM_SEALED_DETAIL_MESSAGE);
        }
        JyBizTaskSendVehicleDetailEntity toSvd = jyBizTaskSendVehicleDetailService.findByBizId(transferSendTaskReq.getToSendVehicleDetailBizId());
        if (jySendVehicleService.checkIfSealed(toSvd)) {
            return new InvokeResult(FORBID_TRANS_TO_SEALED_DETAIL_CODE, FORBID_TRANS_TO_SEALED_DETAIL_MESSAGE);
        }
        List<String> sendCodeList = (!transferSendTaskReq.getTotalTransFlag() && transferSendTaskReq.getSendCodeList() != null) ?
                transferSendTaskReq.getSendCodeList() : jyVehicleSendRelationService.querySendCodesByVehicleDetailBizId(transferSendTaskReq.getFromSendVehicleDetailBizId());
        if (ObjectHelper.isNotNull(sendCodeList) && sendCodeList.size() > 0) {
            sendCodeList = filterEmptySendCode(transferSendTaskReq.getCurrentOperate().getSiteCode(), sendCodeList);
            if (sendCodeList.size() <= 0) {
                return new InvokeResult(FORBID_TRANS_FOR_EMPTY_BATCH_CODE, FORBID_TRANS_FOR_EMPTY_BATCH_MESSAGE);
            }
            if (log.isInfoEnabled()){
                log.info("迁出任务{}待迁移批次：{}",transferSendTaskReq.getFromSendVehicleDetailBizId(),sendCodeList);
            }
            VehicleSendRelationDto dto = BeanUtils.copy(transferSendTaskReq, VehicleSendRelationDto.class);
            dto.setSendCodes(sendCodeList);
            dto.setUpdateUserErp(transferSendTaskReq.getUser().getUserErp());
            dto.setUpdateUserName(transferSendTaskReq.getUser().getUserName());
            dto.setUpdateUserCode(transferSendTaskReq.getUser().getUserCode());
            dto.setCreateSiteId(Long.valueOf(transferSendTaskReq.getCurrentOperate().getSiteCode()));

            TransferVehicleResp transferVehicleResp = new TransferVehicleResp();
            transferVehicleResp.setFromSendCodes(sendCodeList);
            if (ObjectHelper.isTrue(transferSendTaskReq.getSameWayFlag())) {
                //同流向--直接变更绑定关系
                dto.setSource(TransferLogTypeEnum.SAME_WAY_TRANSFER.getCode());
                jyVehicleSendRelationService.updateVehicleSendRelation(dto);
                jySendTransferLogService.saveTransferLog(dto);
                jySendService.updateTransferProperBySendCode(dto);
            } else {
                //删除原绑定关系
                jyVehicleSendRelationService.deleteVehicleSendRelation(dto);
                //增加新流向绑定关系
                String newSendCode = generateSendCode(toSvd, transferSendTaskReq.getUser().getUserErp());
                JySendCodeEntity jySendCodeEntity = initJySendCodeEntity(transferSendTaskReq, newSendCode);
                jyVehicleSendRelationService.add(jySendCodeEntity);
                dto.setNewSendCode(newSendCode);
                jySendTransferLogService.saveTransferLog(dto);
                jySendService.updateTransferProperBySendCode(dto);
                //生成迁移任务，异步执行迁移逻辑
                for (String sendCode : sendCodeList) {
                    List<SendM> sendMList = sendMService.selectBySiteAndSendCode(transferSendTaskReq.getCurrentOperate().getSiteCode(), sendCode);
                    deliveryOperationService.asyncHandleTransfer(sendMList, dto);
                }
                List<String> toSendCodes = new ArrayList<>();
                toSendCodes.add(newSendCode);
                transferVehicleResp.setToSendCodes(toSendCodes);
            }
            transferVehicleResp.setEndSiteId(toSvd.getEndSiteId());
            transferVehicleResp.setEndSiteName(toSvd.getEndSiteName());

            JyBizTaskSendVehicleEntity toSvTask = new JyBizTaskSendVehicleEntity();
            toSvTask.setBizId(dto.getToSendVehicleBizId());
            toSvTask.setVehicleStatus(JyBizTaskSendStatusEnum.SENDING.getCode());
            toSvTask.setPreVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
            jyBizTaskSendVehicleService.updateBizTaskSendStatus(toSvTask);

            JyBizTaskSendVehicleDetailEntity toSvDetailTask = new JyBizTaskSendVehicleDetailEntity();
            toSvDetailTask.setBizId(dto.getToSendVehicleDetailBizId());
            toSvDetailTask.setVehicleStatus(JyBizTaskSendStatusEnum.SENDING.getCode());
            toSvDetailTask.setPreVehicleStatus(JyBizTaskSendStatusEnum.TO_SEND.getCode());
            jyBizTaskSendVehicleDetailService.updateBizTaskSendDetailStatus(toSvDetailTask);

            //迁移完毕，判断迁出的流向任务是否有被打cancel的label，有执行作废
            doCancelForLabelCanceldTask(transferSendTaskReq.getFromSendVehicleDetailBizId());

            return new InvokeResult(RESULT_SUCCESS_CODE, RESULT_SUCCESS_MESSAGE, transferVehicleResp);
        }
        return new InvokeResult(NO_SEND_DATA_UNDER_TASK_CODE, NO_SEND_DATA_UNDER_TASK_MESSAGE);
    }

    private List<String> filterEmptySendCode(int siteCode, List<String> sendCodeList) {
        List<String> notEmptyList = new ArrayList<>();
        for (String sendCode : sendCodeList) {
            List<SendM> sendMList = sendMService.selectBySiteAndSendCode(siteCode, sendCode);
            if (ObjectHelper.isNotNull(sendMList) && sendMList.size() > 0) {
                notEmptyList.add(sendCode);
            }
        }
        return notEmptyList;
    }

    private void doCancelForLabelCanceldTask(String fromSendVehicleDetailBizId) {
        JyBizTaskSendVehicleDetailEntity detailEntity = jyBizTaskSendVehicleDetailService.findByBizId(fromSendVehicleDetailBizId);
        if (ObjectHelper.isNotNull(detailEntity)
                && ObjectHelper.isNotNull(detailEntity.getExcepLabel())
                && SendTaskExcepLabelEnum.CANCEL.getCode().equals(detailEntity.getExcepLabel())) {
            try {
                jyBizTaskSendVehicleDetailService.cancelDetailTaskAndMainTask(detailEntity);
            } catch (Exception e) {
                log.error("对打标cancel的流向任务执行取消异常", e);
            }
        }
    }

    private JySendCodeEntity initJySendCodeEntity(TransferSendTaskReq transferSendTaskReq, String sendCode) {
        JySendCodeEntity jySendCodeEntity = new JySendCodeEntity();
        jySendCodeEntity.setSendCode(sendCode);
        jySendCodeEntity.setSendVehicleBizId(transferSendTaskReq.getToSendVehicleBizId());
        jySendCodeEntity.setSendDetailBizId(transferSendTaskReq.getToSendVehicleDetailBizId());
        jySendCodeEntity.setSource(TransferLogTypeEnum.NOT_SAME_WAY_TRANSFER_NEW_BATCH.getCode());
        Date now = new Date();
        jySendCodeEntity.setCreateTime(now);
        jySendCodeEntity.setUpdateTime(now);
        jySendCodeEntity.setCreateUserErp(transferSendTaskReq.getUser().getUserErp());
        jySendCodeEntity.setCreateUserName(transferSendTaskReq.getUser().getUserName());
        return jySendCodeEntity;
    }

    private String generateSendCode(JyBizTaskSendVehicleDetailEntity sendVehicleDetail, String createUser) {
        Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap = new HashMap<>();
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.from_site_code, String.valueOf(sendVehicleDetail.getStartSiteId()));
        attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.to_site_code, String.valueOf(sendVehicleDetail.getEndSiteId()));
        //attributeKeyEnumObjectMap.put(BusinessCodeAttributeKey.SendCodeAttributeKeyEnum.is_fresh, "false");
        return sendCodeService.createSendCode(attributeKeyEnumObjectMap, JY_APP, createUser);
    }

    @Override
    public InvokeResult<CancelSendTaskResp> cancelSendTask(CancelSendTaskReq request) {
        log.info("jy取消发货，按{}进行取消,扫描号码：{}", CancelSendTypeEnum.getReportTypeName(request.getType()), request.getCode());
        validateCancelReq(request);
        SendM sendM = toSendM(request);
        CancelSendTaskResp cancelSendTaskResp = new CancelSendTaskResp();
        cancelSendTaskResp.setCancelCode(request.getCode());

        //按运单
        if (CancelSendTypeEnum.WAYBILL_CODE.getCode().equals(request.getType())) {
            String wayBillCode = WaybillUtil.isPackageCode(request.getCode()) ? WaybillUtil.getWaybillCode(request.getCode()) : request.getCode();
            sendM.setBoxCode(wayBillCode);
            cancelSendTaskResp.setCancelCode(wayBillCode);
        }
        //按箱
        if (CancelSendTypeEnum.BOX_CODE.getCode().equals(request.getType())) {
            String boxCode = request.getCode();
            if (WaybillUtil.isPackageCode(request.getCode())) {
                //寻找箱号
                List<Sorting> sortingList = sortingService.findByPackageCode(request.getCurrentOperate().getSiteCode(), request.getCode());
                if (!ObjectHelper.isNotNull(sortingList) || sortingList.size() != 1) {
                    log.info("按箱号取消，根据包裹号{}查询箱号数据异常", request.getCode());
                    return new InvokeResult(BOX_NO_FOUND_BY_PACKAGE_CODE, BOX_NO_FOUND_BY_PACKAGE_MESSAGE);
                }
                boxCode = sortingList.get(0).getBoxCode();
            }
            sendM.setBoxCode(boxCode);
            cancelSendTaskResp.setCancelCode(boxCode);
        }
        //按板
        if (CancelSendTypeEnum.BOARD_NO.getCode().equals(request.getType())) {
            String boardCode = request.getCode();
            if (WaybillUtil.isPackageCode(request.getCode()) || BusinessUtil.isBoxcode(request.getCode())) {
                Response<Board> boardResponse = groupBoardManager.getBoardByBoxCode(request.getCode(), request.getCurrentOperate().getSiteCode());
                if (!JdCResponse.CODE_SUCCESS.equals(boardResponse.getCode())) {
                    log.info("按板取消发货-未根据箱号/包裹号找到匹配的板号！");
                    return new InvokeResult(MSCodeMapping.NO_BOARD_BY_PACKAGECODE.getCode(), MSCodeMapping.NO_BOARD_BY_PACKAGECODE.getMessage());
                }
                log.info("============按板取消发货，扫描的包裹号/箱号：{}，板号：{}", request.getCode(), boardResponse.getData().getCode());
                boardCode = boardResponse.getData().getCode();
            }
            sendM.setBoxCode(boardCode);
            cancelSendTaskResp.setCancelCode(boardCode);
        }

        if (CancelSendTypeEnum.BOARD_NO.getCode().equals(request.getType()) && BusinessUtil.isBoardCode(sendM.getBoxCode())) {
            //查询组板信息
            Response<List<String>> response = groupBoardManager.getBoxesByBoardCode(sendM.getBoxCode());
            if (!(JdCResponse.CODE_SUCCESS.equals(response.getCode()) && null != response.getData() && response.getData().size() > 0)) {
                log.info("根据板号：{}未查到包裹/箱号信息", sendM.getBoxCode());
                return new InvokeResult(RESULT_NO_FOUND_BY_BOARD_CODE, RESULT_NO_FOUND_BY_BOARD_MESSAGE);
            }
            List<String> packOrBoxCodes = response.getData();
            List<String> packageCodes = getPackageCodesFromPackOrBoxCodes(packOrBoxCodes, request.getCurrentOperate().getSiteCode());
            cancelSendTaskResp.setCanclePackageCount(packageCodes.size());
            cancelSendTaskResp.setPackageCodes(packageCodes);

            //查询一下sendCode信息
            SendM sendMDto = sendMService.selectSendByBoardCode(request.getCurrentOperate().getSiteCode(), sendM.getBoxCode(), 1);
            if (sendMDto == null) {
                log.info("按板取消发货==========没有找到按板的sendM(发货)记录");
                return new InvokeResult(NO_SEND_DATA_UNDER_BOARD_CODE, NO_SEND_DATA_UNDER_BOARD_MESSAGE);
            }
            sendM.setSendCode(sendMDto.getSendCode());
        }
        //执行取消发货
        ThreeDeliveryResponse tDResponse = deliveryService.dellCancelDeliveryMessageWithServerTime(sendM, true);
        if (ObjectHelper.isNotNull(tDResponse) && JdCResponse.CODE_SUCCESS.equals(tDResponse.getCode())) {
            if (cancelSendTaskResp.getCanclePackageCount() == null) {
                cancelSendTaskResp.setCanclePackageCount(sendM.getCancelPackageCount());
            }
            String sendCode = getSendCodeByScanCode(request.getCode(), request.getCurrentOperate().getSiteCode());
            if (BusinessUtil.isSendCode(sendCode)) {
                BaseSiteInfoDto baseSiteInfoDto = baseMajorManager.getBaseSiteInfoBySiteId(BusinessUtil.getReceiveSiteCodeFromSendCode(sendCode));
                if (ObjectHelper.isNotNull(baseSiteInfoDto) && ObjectHelper.isNotNull(baseSiteInfoDto.getSiteName())) {
                    cancelSendTaskResp.setEndSiteName(baseSiteInfoDto.getSiteName());
                }
            } else {
                log.info("jy取消发货-根据扫描code获取sendCode异常！");
            }
            return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage(), cancelSendTaskResp);
        }
        return new InvokeResult(tDResponse.getCode(), tDResponse.getMessage());
    }

    private String getSendCodeByScanCode(String code, int createSiteCode) {
        /**
         * 包裹 运单 查sendD  箱号或者板号 查sendM
         */
        if (WaybillUtil.isPackageCode(code)) {
            List<SendDetail> sendDetailList = sendDetailService.findByWaybillCodeOrPackageCode(createSiteCode, null, code);
            if (ObjectHelper.isNotNull(sendDetailList) && sendDetailList.size() > 0) {
                return sendDetailList.get(0).getSendCode();
            }
        } else if (WaybillUtil.isWaybillCode(code)) {
            List<SendDetail> sendDetailList = sendDetailService.findByWaybillCodeOrPackageCode(createSiteCode, code, null);
            if (ObjectHelper.isNotNull(sendDetailList) && sendDetailList.size() > 0) {
                return sendDetailList.get(0).getSendCode();
            }
        } else if (BusinessUtil.isBoxcode(code)) {
            List<SendM> sendMList = sendMService.findDeliveryRecord(createSiteCode, code);
            if (ObjectHelper.isNotNull(sendMList) && sendMList.size() > 0) {
                return sendMList.get(0).getSendCode();
            }
        } else if (BusinessUtil.isBoardCode(code)) {
            SendM sendM = sendMService.selectSendByBoardCode(createSiteCode, code, SendStatusEnum.HAS_BEEN_SENDED.getCode());
            if (ObjectHelper.isNotNull(sendM)) {
                return sendM.getSendCode();
            }
        }
        return null;
    }

    private void validateCancelReq(CancelSendTaskReq request) {
        if (request.getType() == null) {
            throw new JyBizException("取消扫描类型不能为空！");
        }
        if (CancelSendTypeEnum.getReportTypeName(request.getType()) == null) {
            throw new JyBizException("不支持该扫描类型！");
        }
        //按运单-支持运单和包裹
        if (CancelSendTypeEnum.WAYBILL_CODE.getCode().equals(request.getType())) {
            if (!(WaybillUtil.isPackageCode(request.getCode()) || WaybillUtil.isWaybillCode(request.getCode()))) {
                throw new JyBizException("无效条码，请扫描运单号或者包裹号！");
            }
        }
        //按箱-支持箱和包裹
        else if (CancelSendTypeEnum.BOX_CODE.getCode().equals(request.getType())) {
            if (!(BusinessUtil.isBoxcode(request.getCode()) || WaybillUtil.isPackageCode(request.getCode()))) {
                throw new JyBizException("无效条码，请扫描箱号或者包裹号！");
            }
        }
        //按板-支持板 箱 包裹
        else if (CancelSendTypeEnum.BOARD_NO.getCode().equals(request.getType())) {
            if (!(BusinessUtil.isBoardCode(request.getCode()) || BusinessUtil.isBoxcode(request.getCode()) || WaybillUtil.isPackageCode(request.getCode()))) {
                throw new JyBizException("无效条码，请扫描板号、箱号或者包裹号！");
            }
        } else {
            if (!WaybillUtil.isPackageCode(request.getCode())) {
                throw new JyBizException("无效条码，请扫描包裹号！");
            }
        }
    }

    private List<String> getPackageCodesFromPackOrBoxCodes(List<String> packOrBoxCodes, Integer siteCode) {
        List<String> packageCodes = new ArrayList<>();
        for (String code : packOrBoxCodes) {
            if (BusinessUtil.isBoxcode(code)) {
                log.info("=====getPackageCodesFromPackOrBoxCodes=======根据箱号获取集包包裹 {}", code);
                List<String> pCodes = getPackageCodesByBoxCodeOrSendCode(code, siteCode);
                if (pCodes != null && pCodes.size() > 0) {
                    log.info("======getPackageCodesFromPackOrBoxCodes======根据sendD找到包裹信息{}", pCodes.toString());
                    packageCodes.addAll(pCodes);
                }
            } else {
                packageCodes.add(code);
            }
        }
        return packageCodes;
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

    private List<String> getPackageCodesByBoxCodeOrSendCode(String boxOrSendCode, Integer siteCode) {
        //构建查询sendDetail的查询参数
        SendDetailDto sendDetail = initSendDetail(boxOrSendCode, siteCode);
        if (BusinessUtil.isBoxcode(boxOrSendCode)) {
            return sendDetailService.queryPackageCodeByboxCode(sendDetail);
        }
        return sendDetailService.queryPackageCodeBySendCode(sendDetail);
    }

    private SendM toSendM(CancelSendTaskReq request) {
        SendM sendM = new SendM();
        sendM.setBoxCode(request.getCode());
        sendM.setCreateSiteCode(request.getCurrentOperate().getSiteCode());
        sendM.setUpdaterUser(request.getUser().getUserName());
        sendM.setSendType(10);
        sendM.setUpdateUserCode(request.getUser().getUserCode());
        Date now = new Date();
        sendM.setOperateTime(now);
        sendM.setUpdateTime(now);
        sendM.setYn(0);
        return sendM;
    }

    /**
     * 运输催派
     *
     * @param tmsUrgeVehicleMq 催派报文
     * @return 催派执行结果
     * @author fanggang7
     * @time 2023-09-15 10:46:29 周五
     */
    @Override
    public com.jd.dms.java.utils.sdk.base.Result<Boolean> remindTransJob(TmsUrgeVehicleMq tmsUrgeVehicleMq) {
        log.info("JyNoTaskSendServiceImpl.remindTransJob param {}", JsonHelper.toJson(tmsUrgeVehicleMq));
        com.jd.dms.java.utils.sdk.base.Result<Boolean> result = com.jd.dms.java.utils.sdk.base.Result.success();
        try {
            AccountDto accountDto = this.getAccountDto(tmsUrgeVehicleMq.getUserCode(), tmsUrgeVehicleMq.getUserName());

            RemindTransJobRequestDTO remindTransJobRequestDTO = new RemindTransJobRequestDTO();
            remindTransJobRequestDTO.setTransJobCode(tmsUrgeVehicleMq.getTransJobCode());
            remindTransJobRequestDTO.setTransportCode(tmsUrgeVehicleMq.getTransportCode());
            remindTransJobRequestDTO.setUpdateUserCode(tmsUrgeVehicleMq.getUserCode());
            remindTransJobRequestDTO.setUpdateUserName(tmsUrgeVehicleMq.getUserName());
            remindTransJobRequestDTO.setUpdateTime(new Date(tmsUrgeVehicleMq.getOperateTime()));

            log.info("JyNoTaskSendServiceImpl.remindTransJob pdaSorterApiManager.remindTransJob param {} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(remindTransJobRequestDTO));
            final com.jd.tms.workbench.dto.CommonDto<RemindTransJobReponseDTO> remoteResult = pdaSorterApiManager.remindTransJob(accountDto, remindTransJobRequestDTO);
            log.info("JyNoTaskSendServiceImpl.remindTransJob pdaSorterApiManager.remindTransJob result {}", JsonHelper.toJson(remoteResult));
            if (remoteResult == null) {
                log.error("JyNoTaskSendServiceImpl.remindTransJob call remindTransJob empty {} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(remindTransJobRequestDTO));
                return result.toFail("催派接口调用失败！");
            }
            if (Objects.equals(remoteResult.getCode(), com.jd.tms.workbench.dto.CommonDto.CODE_SUCCESS)) {
                log.error("JyNoTaskSendServiceImpl.remindTransJob call remindTransJob fail {} {}", JsonHelper.toJson(accountDto), JsonHelper.toJson(remindTransJobRequestDTO));
                return result.toFail("催派接口调用失败！");
            }
        } catch (Exception e) {
            log.error("JyNoTaskSendServiceImpl.remindTransJob exception {}", JsonHelper.toJson(tmsUrgeVehicleMq), e);
            result.toFail("系统异常");
        }
        return result;
    }
}
