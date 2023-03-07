package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DWSCheckRequest;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckRecord;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckResponse;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.DWSCheckManager;
import com.jd.bluedragon.core.base.DeviceConfigInfoJsfServiceManager;
import com.jd.bluedragon.core.base.HrUserManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateCondition;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateDetailEntity;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateEntity;
import com.jd.bluedragon.distribution.jy.dto.calibrate.DwsMachineCalibrateMQ;
import com.jd.bluedragon.distribution.jy.dto.calibrate.JyBizTaskMachineCalibrateMessage;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NoticeUtils;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 设备称重量方校准服务实现
 *
 * @author hujiping
 * @date 2022/12/7 9:00 PM
 */
@Service("jyWeightVolumeCalibrateService")
public class JyWeightVolumeCalibrateServiceImpl implements JyWeightVolumeCalibrateService{

    private final Logger logger = LoggerFactory.getLogger(JyWeightVolumeCalibrateServiceImpl.class);

    @Autowired
    private JyBizTaskMachineCalibrateService jyBizTaskMachineCalibrateService;

    @Autowired
    private JyBizTaskMachineCalibrateDetailService jyBizTaskMachineCalibrateDetailService;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    private HrUserManager hrUserManager;

    @Autowired
    @Qualifier("dwsCheckManager")
    private DWSCheckManager dwsCheckManager;

    @Autowired
    @Qualifier("dwsCalibratePushDDProducer")
    private DefaultJMQProducer dwsCalibratePushDDProducer;

    @Autowired
    private DeviceConfigInfoJsfServiceManager deviceConfigInfoJsfServiceManager;

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWeightVolumeCalibrateService.machineCalibrateScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public InvokeResult<DwsWeightVolumeCalibrateTaskResult> machineCalibrateScan(DwsWeightVolumeCalibrateRequest request) {
        InvokeResult<DwsWeightVolumeCalibrateTaskResult> result = new InvokeResult<>();
        result.success();
        DwsWeightVolumeCalibrateTaskResult dwsWeightVolumeCalibrateTaskResult = new DwsWeightVolumeCalibrateTaskResult();
        result.setData(dwsWeightVolumeCalibrateTaskResult);
        if(!baseParamsCheckIsSuc(request, result)){
            return result;
        }
        if(StringUtils.isNotEmpty(request.getMachineCode())){
            // 扫描设备编码处理
            machineScanDeal(request, result);
            if(!result.codeSuccess()){
                return result;
            }
        }
        // 根据erp获取校准任务明细
        queryAndAssembleResult(buildQueryCondition(request), result);
        return result;
    }

    /**
     * 构建查询条件
     *
     * @param request
     * @return
     */
    private JyBizTaskMachineCalibrateCondition buildQueryCondition(DwsWeightVolumeCalibrateRequest request) {
        JyBizTaskMachineCalibrateCondition condition = new JyBizTaskMachineCalibrateCondition();
        condition.setCreateUserErp(request.getUser().getUserErp());
        // 查询时间范围，默认：48h
        Long machineCalibrateTaskQueryRange = uccPropertyConfiguration.getMachineCalibrateTaskQueryRange();
        Date queryEndTime = new Date();
        Date queryStartTime = new Date(queryEndTime.getTime() - machineCalibrateTaskQueryRange);
        condition.setQueryEndTime(queryEndTime);
        condition.setQueryStartTime(queryStartTime);
        List<Integer> statusList = Lists.newArrayList();
        statusList.add(JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_TODO.getCode());
        statusList.add(JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_COMPLETE.getCode());
        statusList.add(JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_OVERTIME.getCode());
        condition.setTaskStatusList(statusList);
        return condition;
    }

    private void machineScanDeal(DwsWeightVolumeCalibrateRequest request, InvokeResult<DwsWeightVolumeCalibrateTaskResult> result) {
        String machineCode = request.getMachineCode();
        String userErp = request.getUser().getUserErp();
        String redisKey = String.format(CacheKeyConstants.CACHE_KEY_DWS_CALIBRATE_SCAN, machineCode, userErp);
        if(!cacheMachineKey(redisKey)){
            result.error(String.format(JyBizTaskMachineCalibrateMessage.MACHINE_IS_DEAL_HINT, machineCode));
            return;
        }
        try {
            // 设备维度记录
            JyBizTaskMachineCalibrateEntity machineCondition = new JyBizTaskMachineCalibrateEntity();
            machineCondition.setMachineCode(machineCode);
            JyBizTaskMachineCalibrateEntity machineRecord = jyBizTaskMachineCalibrateService.queryOneByCondition(machineCondition);
            // 设备任务维度记录
            JyBizTaskMachineCalibrateCondition machineTaskCondition = new JyBizTaskMachineCalibrateCondition();
            machineTaskCondition.setMachineCode(machineCode);
            JyBizTaskMachineCalibrateDetailEntity machineTaskEntity = jyBizTaskMachineCalibrateDetailService.selectLatelyOneByMachineCode(machineTaskCondition);

            boolean isCreateTaskFlag = false; // 是否创建任务标识
            if(Objects.equals(request.getForceCreateTask(), true)){
                // 强制创建任务
                if(machineRecord != null && machineRecord.getCalibrateTaskCloseTime() == null){
                    result.error(JyBizTaskMachineCalibrateMessage.MACHINE_CALIBRATE_TASK_CREATED_HINT);
                    return;
                }
                isCreateTaskFlag = true;
            }else if(machineRecord == null){
                // 设备维度记录不存在（第一次扫描设备编码）
                isCreateTaskFlag = true;
            }else if(machineRecord.getCalibrateTaskCloseTime() != null){
                // 设备已关闭
                Long intervalTime = uccPropertyConfiguration.getMachineCalibrateTaskForceCreateIntervalTime();
                if(System.currentTimeMillis() - machineRecord.getCalibrateTaskCloseTime().getTime() < intervalTime){
                    // explain：如果设备抽检任务关闭后，在2h内继续扫描设备编码，则进行友好提示（客户端可强制确定来创建新任务）
                    result.customMessage(InvokeResult.CODE_CONFIRM, HintService.getHint(HintCodeConstants.JY_MACHINE_CALIBRATE_TASK_CLOSED_AND_NOT_OVER_2_HINT));
                    return;
                }
                isCreateTaskFlag = true;
            }else {
                // 设备未关闭
                if(machineTaskEntity == null){
                    String errorMessage = String.format(JyBizTaskMachineCalibrateMessage.MACHINE_CALIBRATE_TASK_NOT_FIND_HINT, machineCode);
                    logger.error(errorMessage);
                    result.error(errorMessage);
                    return;
                }
                String createUserErp = machineTaskEntity.getCreateUserErp();
                if(!Objects.equals(userErp, createUserErp)){
                    result.error(String.format(JyBizTaskMachineCalibrateMessage.MACHINE_CALIBRATE_TASK_CREATED_WITH_OTHER_HINT, createUserErp));
                    return;
                }
                if(!Objects.equals(machineTaskEntity.getTaskStatus(), JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_TODO.getCode())){
                    String errorMessage = String.format(JyBizTaskMachineCalibrateMessage.MACHINE_CALIBRATE_STATUS_ERROR_HINT, machineCode);
                    logger.error(errorMessage);
                    result.error(errorMessage);
                    return;
                }
            }

            if(isCreateTaskFlag){
                // 创建设备维度数据
                JyBizTaskMachineCalibrateEntity machineEntity = new JyBizTaskMachineCalibrateEntity();
                machineEntity.setMachineCode(machineCode);
                machineEntity.setCalibrateTaskStartTime(new Date());
                machineEntity.setCreateUserErp(userErp);
                jyBizTaskMachineCalibrateService.insert(machineEntity);
                // 创建设备任务维度数据
                JyBizTaskMachineCalibrateDetailEntity entity = initBaseData();
                entity.setRefMachineKey(machineEntity.getId());
                entity.setMachineCode(request.getMachineCode());
                entity.setCreateUserErp(request.getUser().getUserErp());
                jyBizTaskMachineCalibrateDetailService.insert(entity);
            }
        }catch (Exception e){
            logger.error("扫描设备编码:{}创建校准任务异常!", machineCode);
            throw e;
        }finally {
            deleteMachineKey(redisKey);
        }
    }

    private void deleteMachineKey(String redisKey) {
        try {
            jimdbCacheService.del(redisKey);
        }catch (Exception e){
            logger.error("删除缓存key:{}异常!", redisKey);
        }
    }

    private boolean cacheMachineKey(String redisKey) {
        try {
            return jimdbCacheService.setNx(redisKey, 1, 5, TimeUnit.SECONDS);
        }catch (Exception e){
            logger.error("设置缓存key:{}异常!", redisKey);
        }
        return false;
    }

    private void queryAndAssembleResult(JyBizTaskMachineCalibrateCondition condition, InvokeResult<DwsWeightVolumeCalibrateTaskResult> result) {
        result.getData().setSystemTime(System.currentTimeMillis());
        List<JyBizTaskMachineCalibrateDetailEntity> list = jyBizTaskMachineCalibrateDetailService.selectByCondition(condition);
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        List<JyBizTaskMachineCalibrateDetailEntity> todoList = Lists.newArrayList();
        List<JyBizTaskMachineCalibrateDetailEntity> doneList = Lists.newArrayList();
        List<JyBizTaskMachineCalibrateDetailEntity> overtimeList = Lists.newArrayList();
        for (JyBizTaskMachineCalibrateDetailEntity entity : list) {
            if(Objects.equals(entity.getTaskStatus(), JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_TODO.getCode())){
                todoList.add(entity);
            }
            if(Objects.equals(entity.getTaskStatus(), JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_COMPLETE.getCode())){
                doneList.add(entity);
            }
            if(Objects.equals(entity.getTaskStatus(), JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_OVERTIME.getCode())){
                overtimeList.add(entity);
            }
        }
        convert2Result(todoList, result.getData().getTodoTaskList());
        doneConvert2Result(doneList, result.getData().getDoneTaskList());
        convert2Result(overtimeList, result.getData().getOvertimeTaskList());
    }

    private void doneConvert2Result(List<JyBizTaskMachineCalibrateDetailEntity> doneList, List<DwsWeightVolumeCalibrateTaskDetail> doneTaskList) {
        List<DwsWeightVolumeCalibrateTaskDetail> notEligibleList = Lists.newArrayList();
        List<DwsWeightVolumeCalibrateTaskDetail> eligibleList = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(doneList)){
            for (JyBizTaskMachineCalibrateDetailEntity entity : doneList) {
                DwsWeightVolumeCalibrateTaskDetail taskDetail = convert2VO(entity);
                if(Objects.equals(entity.getMachineStatus(), JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode())){
                    eligibleList.add(taskDetail);
                }else {
                    notEligibleList.add(taskDetail);
                }
            }
        }
        //按照校验完成时间降序排列
        Collections.sort(notEligibleList, new Comparator<DwsWeightVolumeCalibrateTaskDetail>() {
            @Override
            public int compare(DwsWeightVolumeCalibrateTaskDetail v1, DwsWeightVolumeCalibrateTaskDetail v2) {
                return v2.getCalibrateFinishTime().compareTo(v1.getCalibrateFinishTime());
            }
        });
        doneTaskList.addAll(notEligibleList);
        doneTaskList.addAll(eligibleList);
    }

    private void convert2Result(List<JyBizTaskMachineCalibrateDetailEntity> dataList, List<DwsWeightVolumeCalibrateTaskDetail> resultList) {
        if(CollectionUtils.isNotEmpty(dataList)){
            for (JyBizTaskMachineCalibrateDetailEntity entity : dataList) {
                resultList.add(convert2VO(entity));
            }
        }
        //按照任务创建升序排列
        Collections.sort(resultList, new Comparator<DwsWeightVolumeCalibrateTaskDetail>() {
            @Override
            public int compare(DwsWeightVolumeCalibrateTaskDetail v1, DwsWeightVolumeCalibrateTaskDetail v2) {
                return v1.getTaskCreateTime().compareTo(v2.getTaskCreateTime());
            }
        });
    }

    private DwsWeightVolumeCalibrateTaskDetail convert2VO(JyBizTaskMachineCalibrateDetailEntity entity) {
        DwsWeightVolumeCalibrateTaskDetail taskDetail = new DwsWeightVolumeCalibrateTaskDetail();
        BeanUtils.copyProperties(entity, taskDetail);
        taskDetail.setMachineTaskId(entity.getId());
        taskDetail.setCalibrateHint(judgeCalibrateHint(entity));
        taskDetail.setTaskCreateTime(entity.getTaskCreateTime().getTime());
        taskDetail.setTaskEndTime(entity.getTaskEndTime().getTime());
        taskDetail.setCalibrateFinishTime(entity.getCalibrateFinishTime() == null ? null : entity.getCalibrateFinishTime().getTime());
        return taskDetail;
    }

    private Integer judgeCalibrateHint(JyBizTaskMachineCalibrateDetailEntity entity) {
        if(entity.getWeightCalibrateTime() == null && entity.getVolumeCalibrateTime() == null){
            return JyBizTaskMachineCalibrateHintEnum.CALIBRATE_NOT_START.getCode();
        }
        if(entity.getWeightCalibrateTime() == null){
            return JyBizTaskMachineCalibrateHintEnum.WEIGHT_NOT_CALIBRATE.getCode();
        }
        if(entity.getVolumeCalibrateTime() == null){
            return JyBizTaskMachineCalibrateHintEnum.VOLUME_NOT_CALIBRATE.getCode();
        }
        return JyBizTaskMachineCalibrateHintEnum.CALIBRATE_COMPLETE.getCode();
    }

    private boolean baseParamsCheckIsSuc(DwsWeightVolumeCalibrateRequest request, InvokeResult<DwsWeightVolumeCalibrateTaskResult> result) {
        if(request == null || request.getUser() == null
                || StringUtils.isEmpty(request.getUser().getUserErp())){
            result.parameterError();
            return false;
        }
        String machineCode = request.getMachineCode();
        if(StringUtils.isNotEmpty(machineCode)){
            // 设备编码存在性校验
            DeviceConfigDto deviceConfigDto = deviceConfigInfoJsfServiceManager.findOneDeviceConfigByMachineCode(machineCode);
            if(deviceConfigDto == null || StringUtils.isEmpty(deviceConfigDto.getMachineCode())){
                result.parameterError("设备编码不存在,请重新扫描!");
                return false;
            }
        }
        return true;
    }

    /**
     * 初始化基础数据
     *
     * @return
     */
    private JyBizTaskMachineCalibrateDetailEntity initBaseData() {
        JyBizTaskMachineCalibrateDetailEntity entity = new JyBizTaskMachineCalibrateDetailEntity();
        // 任务时间设置
        Date current = new Date();
        Long machineCalibrateTaskDuration = uccPropertyConfiguration.getMachineCalibrateTaskDuration(); // 任务时长
        entity.setTaskCreateTime(current);
        entity.setTaskEndTime(new Date(current.getTime() + machineCalibrateTaskDuration));
        // 状态
        entity.setTaskStatus(JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_TODO.getCode());
        entity.setMachineStatus(JyBizTaskMachineCalibrateStatusEnum.NO_CALIBRATE.getCode());
        entity.setWeightCalibrateStatus(JyBizTaskMachineWeightCalibrateStatusEnum.NO_CALIBRATE.getCode());
        entity.setVolumeCalibrateStatus(JyBizTaskMachineVolumeCalibrateStatusEnum.NO_CALIBRATE.getCode());
        return entity;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWeightVolumeCalibrateService.getMachineCalibrateDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public InvokeResult<DwsWeightVolumeCalibrateDetailResult> getMachineCalibrateDetail(DwsWeightVolumeCalibrateRequest request) {
        InvokeResult<DwsWeightVolumeCalibrateDetailResult> result = new InvokeResult<>();
        result.success();
        DwsWeightVolumeCalibrateDetailResult detailResult = new DwsWeightVolumeCalibrateDetailResult();
        if (request.getMachineCode() == null || request.getMachineTaskId() == null) {
            logger.error("查询设备校验细节出错，入参{}", JsonHelper.toJson(request));
            result.error("查询设备校验细节出错，请联系分拣小秘进行处理！");
            return result;
        }
        JyBizTaskMachineCalibrateDetailEntity taskDetail = jyBizTaskMachineCalibrateDetailService.selectById(request.getMachineTaskId());
        if (taskDetail == null) {
            logger.error("查询设备校验细节为空，入参{}", JsonHelper.toJson(request));
            result.error("查询设备校验细节出错，请联系分拣小秘进行处理！");
            return result;
        }

        DWSCheckRequest checkRequest = new DWSCheckRequest();
        checkRequest.setMachineCode(request.getMachineCode());
        //已完成状态的任务明细，需要根据体积和重量校准时间去获取校准记录
        //防止短时间内校准重量体积多次，查询到多个任务的校准记录
        if (Objects.equals(taskDetail.getTaskStatus(), JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_COMPLETE.getCode())) {
            Long queryStartTime = Math.min(taskDetail.getWeightCalibrateTime().getTime(), taskDetail.getVolumeCalibrateTime().getTime());
            Long queryEndTime = Math.max(taskDetail.getWeightCalibrateTime().getTime(), taskDetail.getVolumeCalibrateTime().getTime());
            checkRequest.setQueryStartTime(queryStartTime);
            checkRequest.setQueryEndTime(queryEndTime);
        } else { // 待处理、超时状态的任务根据任务创建时间以及截止时间去获取即可
            checkRequest.setQueryStartTime(taskDetail.getTaskCreateTime().getTime());
            checkRequest.setQueryEndTime(taskDetail.getTaskEndTime().getTime());
        }
        DwsCheckResponse response = dwsCheckManager.getLastDwsCheckByTime(checkRequest);
        if (response == null){
            result.error("查询设备校验细节出错，请联系分拣小秘进行处理！");
            logger.error("自动化接口调用response为空，入参{}", JsonHelper.toJson(checkRequest));
            return result;
        }
        List<DwsWeightVolumeCalibrateDetail> detailList = new ArrayList<>();
        if (response.getDetailList() != null) {
            long calibrateFishTime = Long.MIN_VALUE;
            //四舍五入保留2位小数
            NumberFormat formatter = NumberFormat.getNumberInstance();
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
            formatter.setRoundingMode(RoundingMode.HALF_UP);
            Integer weightStatus = JyBizTaskMachineWeightCalibrateStatusEnum.NO_CALIBRATE.getCode();
            Integer volumeStatus = JyBizTaskMachineVolumeCalibrateStatusEnum.NO_CALIBRATE.getCode();
            for (DwsCheckRecord record : response.getDetailList()) {
                DwsWeightVolumeCalibrateDetail detail = new DwsWeightVolumeCalibrateDetail();
                detail.setCalibrateType(record.getCalibrateType());
                detail.setMachineCode(record.getMachineCode());
                detail.setFarmarCode(record.getFarmarCode());
                if (record.getFarmarWeight() != null) {
                    detail.setFarmarWeight(formatter.format(record.getFarmarWeight()));
                }
                if (record.getFarmarLength() != null) {
                    detail.setFarmarLength(formatter.format(record.getFarmarLength()));
                }
                if (record.getFarmarWidth() != null) {
                    detail.setFarmarWidth(formatter.format(record.getFarmarWidth()));
                }
                if (record.getFarmarHigh() != null) {
                    detail.setFarmarHigh(formatter.format(record.getFarmarHigh()));
                }
                if (record.getActualWeight() != null) {
                    detail.setActualWeight(formatter.format(record.getActualWeight()));
                }
                if (record.getActualLength() != null) {
                    detail.setActualLength(formatter.format(record.getActualLength()));
                }
                if (record.getActualWidth() != null) {
                    detail.setActualWidth(formatter.format(record.getActualWidth()));
                }
                if (record.getActualHigh() != null) {
                    detail.setActualHigh(formatter.format(record.getActualHigh()));
                }
                if (Objects.equals(record.getCalibrateType(), JyBizTaskMachineCalibrateTypeEnum.CALIBRATE_TYPE_W.getCode())) {
                    weightStatus = record.getCalibrateStatus();
                }
                if (Objects.equals(record.getCalibrateType(), JyBizTaskMachineCalibrateTypeEnum.CALIBRATE_TYPE_V.getCode())) {
                    volumeStatus = record.getCalibrateStatus();
                }
                detail.setCalibrateStatus(record.getCalibrateStatus() == null
                        ? JyBizTaskMachineWeightCalibrateStatusEnum.NO_CALIBRATE.getCode() : record.getCalibrateStatus());
                detail.setCalibrateTime(record.getCalibrateTime());
                detail.setErrorRange(record.getErrorRange());
                detailList.add(detail);

                if (record.getCalibrateTime() != null) {
                    calibrateFishTime = Math.max(calibrateFishTime, record.getCalibrateTime());
                }
            }
            detailResult.setCalibrateFinishTime(calibrateFishTime == Long.MIN_VALUE ? null : calibrateFishTime);
            // 重量校准或体积校准有一个不合格，设备不合格
            if (Objects.equals(weightStatus, JyBizTaskMachineWeightCalibrateStatusEnum.UN_ELIGIBLE.getCode())
                    || Objects.equals(volumeStatus, JyBizTaskMachineVolumeCalibrateStatusEnum.UN_ELIGIBLE.getCode())) {
                detailResult.setMachineStatus(JyBizTaskMachineCalibrateStatusEnum.UN_ELIGIBLE.getCode());
            }
            // 重量校准合格，体积校准不等于不合格（体积校准合格或者未校准），设备合格
            if (Objects.equals(weightStatus, JyBizTaskMachineWeightCalibrateStatusEnum.ELIGIBLE.getCode())
                    && !Objects.equals(volumeStatus, JyBizTaskMachineVolumeCalibrateStatusEnum.UN_ELIGIBLE.getCode())) {
                detailResult.setMachineStatus(JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode());
            }
            // 体积校准合格，重量校准不等于不合格（重量校准合格或者未校准），设备合格
            if (Objects.equals(volumeStatus, JyBizTaskMachineVolumeCalibrateStatusEnum.ELIGIBLE.getCode())
                    && !Objects.equals(weightStatus, JyBizTaskMachineWeightCalibrateStatusEnum.UN_ELIGIBLE.getCode())) {
                detailResult.setMachineStatus(JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode());
            }
            // 重量、体积都未校准，设备状态为未校准
            if (Objects.equals(weightStatus, JyBizTaskMachineWeightCalibrateStatusEnum.NO_CALIBRATE.getCode())
                    && Objects.equals(volumeStatus, JyBizTaskMachineVolumeCalibrateStatusEnum.NO_CALIBRATE.getCode())) {
                detailResult.setMachineStatus(JyBizTaskMachineCalibrateStatusEnum.NO_CALIBRATE.getCode());
            }
        }
        detailResult.setDetailList(detailList);
        detailResult.setPreviousMachineEligibleTime(response.getPreviousMachineEligibleTime());
        detailResult.setMachineCode(request.getMachineCode());
        detailResult.setTaskCreateTime(request.getCalibrateTaskStartTime());
        detailResult.setTaskEndTime(request.getCalibrateTaskEndTime());
        result.setData(detailResult);
        return result;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWeightVolumeCalibrateService.closeMachineCalibrateTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public InvokeResult<Void> closeMachineCalibrateTask(DwsWeightVolumeCalibrateRequest request) {
        InvokeResult<Void> result = new InvokeResult<>();

        if (request.getMachineCode() == null || request.getCalibrateTaskStartTime() == null
                || request.getUser() == null || request.getMachineTaskId() == null) {
            result.error(JyBizTaskMachineCalibrateMessage.MACHINE_CALIBRATE_REQUEST_ERROR);
            return result;
        }
        JyBizTaskMachineCalibrateDetailEntity closeDetailTask = jyBizTaskMachineCalibrateDetailService.selectById(request.getMachineTaskId());
        if (closeDetailTask == null || closeDetailTask.getRefMachineKey() == null) {
            logger.error("dws承重量方查询任务明细出错！入参：{}", JsonHelper.toJson(request));
            result.error(String.format(JyBizTaskMachineCalibrateMessage.MACHINE_CALIBRATE_TASK_NOT_FIND_HINT, request.getMachineCode()));
            return result;
        }
        JyBizTaskMachineCalibrateEntity closeMainTask = new JyBizTaskMachineCalibrateEntity();
        closeMainTask.setId(closeDetailTask.getRefMachineKey());
        closeMainTask.setCalibrateTaskCloseTime(new Date());
        closeMainTask.setUpdateUserErp(request.getUser().getUserErp());
        closeMainTask.setUpdateTime(new Date());
        //设备关闭后废弃当前的最新的处于待处理状态的任务
        if (jyBizTaskMachineCalibrateService.closeMachineCalibrateTask(closeMainTask) == Constants.CONSTANT_NUMBER_ONE){
            closeDetailTask.setTaskStatus(JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_CLOSE.getCode());
            closeDetailTask.setUpdateUserErp(request.getUser().getUserErp());
            closeDetailTask.setUpdateTime(new Date());
            jyBizTaskMachineCalibrateDetailService.closeCalibrateDetailById(closeDetailTask);
            result.customMessage(InvokeResult.RESULT_SUCCESS_CODE, "任务关闭成功！");
        }else {
            logger.error("closeMachineCalibrateTask关闭任务失败，入参:{}", JsonHelper.toJson(request));
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, "任务关闭失败！");
        }

        return result;
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWeightVolumeCalibrateService.dealCalibrateTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public InvokeResult<Boolean> dealCalibrateTask(DwsMachineCalibrateMQ dwsMachineCalibrateMQ) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        JyBizTaskMachineCalibrateCondition condition = new JyBizTaskMachineCalibrateCondition();
        condition.setMachineCode(dwsMachineCalibrateMQ.getMachineCode());
        condition.setCalibrateTime(new Date(dwsMachineCalibrateMQ.getCalibrateTime()));
        //只有待处理状态的任务才会更新
        condition.setCalibrateTaskStatus(JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_TODO.getCode());
        List<JyBizTaskMachineCalibrateDetailEntity> detailList = jyBizTaskMachineCalibrateDetailService.queryCurrentTaskDetail(condition);
        if (CollectionUtils.isEmpty(detailList) || detailList.size() > Constants.CONSTANT_NUMBER_ONE){
            logger.error("获取设备编码为:{}的待处理任务出现异常!", dwsMachineCalibrateMQ.getMachineCode());
            result.error("设备" + dwsMachineCalibrateMQ.getMachineCode() + "待处理任务获取异常，请联系分拣小秘排查!");
            return result;
        }
        JyBizTaskMachineCalibrateDetailEntity taskDetail = detailList.get(0);
        if (Objects.equals(JyBizTaskMachineCalibrateTypeEnum.CALIBRATE_TYPE_W.getCode(), dwsMachineCalibrateMQ.getCalibrateType())){
            taskDetail.setWeightCalibrateStatus(dwsMachineCalibrateMQ.getCalibrateStatus());
            taskDetail.setWeightCalibrateTime(new Date(dwsMachineCalibrateMQ.getCalibrateTime()));
        }
        if (Objects.equals(JyBizTaskMachineCalibrateTypeEnum.CALIBRATE_TYPE_V.getCode(), dwsMachineCalibrateMQ.getCalibrateType())){
            taskDetail.setVolumeCalibrateStatus(dwsMachineCalibrateMQ.getCalibrateStatus());
            taskDetail.setVolumeCalibrateTime(new Date(dwsMachineCalibrateMQ.getCalibrateTime()));
        }
        // 重量、体积都操作校准了，校准任务结束
        boolean isFinished = !Objects.equals(taskDetail.getWeightCalibrateStatus(), JyBizTaskMachineWeightCalibrateStatusEnum.NO_CALIBRATE.getCode())
                && !Objects.equals(taskDetail.getVolumeCalibrateStatus(), JyBizTaskMachineVolumeCalibrateStatusEnum.NO_CALIBRATE.getCode());
        if(isFinished){
            //体积、重量校准时间靠后的为校准任务明细完成时间
            Date calibrateFinishTime = taskDetail.getWeightCalibrateTime().getTime() > taskDetail.getVolumeCalibrateTime().getTime() ?
                    taskDetail.getWeightCalibrateTime() : taskDetail.getVolumeCalibrateTime();
            taskDetail.setCalibrateFinishTime(calibrateFinishTime);
            taskDetail.setTaskStatus(JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_COMPLETE.getCode());
            //体积重量校准都合格时，设备状态才是合格的
            Integer machineStatus = Objects.equals(taskDetail.getWeightCalibrateStatus(), JyBizTaskMachineWeightCalibrateStatusEnum.ELIGIBLE.getCode())
                    && Objects.equals(taskDetail.getVolumeCalibrateStatus(), JyBizTaskMachineVolumeCalibrateStatusEnum.ELIGIBLE.getCode()) ?
                    JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode() : JyBizTaskMachineCalibrateStatusEnum.UN_ELIGIBLE.getCode();
            taskDetail.setMachineStatus(machineStatus);
        }
        jyBizTaskMachineCalibrateDetailService.update(taskDetail);
        // 任务完成后并且设备合格，创建新任务明细
        if (isFinished && Objects.equals(taskDetail.getMachineStatus(), JyBizTaskMachineCalibrateStatusEnum.ELIGIBLE.getCode())) {
            createNewTaskAfterCompleteTask(dwsMachineCalibrateMQ, taskDetail);
        }
        // 任务完成后并且设备不合格，关闭设备主表任务
        if (isFinished && Objects.equals(taskDetail.getMachineStatus(), JyBizTaskMachineCalibrateStatusEnum.UN_ELIGIBLE.getCode())) {
            JyBizTaskMachineCalibrateEntity mainTask = new JyBizTaskMachineCalibrateEntity();
            mainTask.setId(taskDetail.getRefMachineKey());
            mainTask.setCalibrateTaskCloseTime(new Date());
            mainTask.setUpdateTime(new Date());
            jyBizTaskMachineCalibrateService.closeMachineCalibrateTask(mainTask);
        }
        return result;
    }

    /**
     * 任务完成后创建新任务
     *
     * @param dwsMachineCalibrateMQ
     * @param completeTaskDetail
     */
    private void createNewTaskAfterCompleteTask(DwsMachineCalibrateMQ dwsMachineCalibrateMQ, JyBizTaskMachineCalibrateDetailEntity completeTaskDetail) {
        JyBizTaskMachineCalibrateDetailEntity entity = initBaseData();;
        entity.setMachineCode(dwsMachineCalibrateMQ.getMachineCode());
        entity.setCreateUserErp(completeTaskDetail.getCreateUserErp());
        entity.setRefMachineKey(completeTaskDetail.getRefMachineKey());
        jyBizTaskMachineCalibrateDetailService.insert(entity);
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWeightVolumeCalibrateService.regularScanCalibrateTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Transactional(value = "tm_jy_core", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public InvokeResult<Boolean> regularScanCalibrateTask() {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        JyBizTaskMachineCalibrateCondition condition = new JyBizTaskMachineCalibrateCondition();
        condition.setTaskStatus(JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_TODO.getCode());
        condition.setTaskEndTime(new Date());
        // 超时任务
        int total = jyBizTaskMachineCalibrateDetailService.selectCountForTask(condition);
        if(total == 0){
            logger.warn("待处理任务未超过限定时长，无需处理!");
            return result;
        }
        // 待更新id集合
        List<Long> ids = Lists.newArrayList();
        Set<Long> refMachineIds = Sets.newHashSet();

        int offSet = 0;
        int pageSize = 100;
        int loop = total / pageSize + 1; // 总共需循环次数
        int count = 1; // 当前循环次数
        while (count <= loop){
            condition.setPageSize(pageSize);
            condition.setOffSet(offSet);
            List<JyBizTaskMachineCalibrateDetailEntity> list = jyBizTaskMachineCalibrateDetailService.selectByConditionForTask(condition);
            if(CollectionUtils.isEmpty(list)){
                logger.warn("待处理任务未超过限定时长，无需处理!");
                break;
            }
            for (JyBizTaskMachineCalibrateDetailEntity entity : list) {
                ids.add(entity.getId());
                refMachineIds.add(entity.getRefMachineKey());
            }
            // 批量发消息推送咚咚
            batchSendPushDD(list);

            offSet += pageSize;
            count ++;
            // 防止死循环
            if(count > 100){
                logger.warn("设备超时任务总共:{}条并且超过循环次数限制!", total);
                break;
            }
        }
        // 批量关闭设备任务
        batchCloseMachine(Lists.newArrayList(refMachineIds));
        // 任务状态批量更新为'超时'
        batchUpdateStatus(ids);
        return result;
    }

    private void batchCloseMachine(List<Long> refMachineIds) {
        for (List<Long> singleList : Lists.partition(refMachineIds, 100)) {
            // 100一批分批更新状态
            jyBizTaskMachineCalibrateService.batchCloseByIds(singleList);
        }
    }

    private void batchSendPushDD(List<JyBizTaskMachineCalibrateDetailEntity> list) {
        List<Message> messageList = Lists.newArrayList();
        for (JyBizTaskMachineCalibrateDetailEntity entity : list) {
            Message message = new Message();
            message.setTopic(dwsCalibratePushDDProducer.getTopic());
            message.setText(JsonHelper.toJson(entity));
            message.setBusinessId(entity.getMachineCode() + entity.getId());
            messageList.add(message);
        }
        dwsCalibratePushDDProducer.batchSendOnFailPersistent(messageList);
    }

    private void batchUpdateStatus(List<Long> ids) {
        for (List<Long> singleList : Lists.partition(ids, 100)) {
            // 100一批分批更新状态
            jyBizTaskMachineCalibrateDetailService.batchUpdateStatus(singleList, JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_OVERTIME.getCode());
        }
    }

    @Override
    public void noticeToDD(JyBizTaskMachineCalibrateDetailEntity machineCalibrateDetail) {
        String title = "设备校准任务已超时";
        String content;
        String template = "设备编码:%s在时间%s-%s内的校准任务未处理，已超时，请及时处理!";
        String machineCode = machineCalibrateDetail.getMachineCode();
        String createUserErp = machineCalibrateDetail.getCreateUserErp();
        String leaderErp = hrUserManager.getSuperiorErp(createUserErp);
        List<String> erpList = Lists.newArrayList();
        erpList.add(createUserErp);
        if(StringUtils.isNotEmpty(leaderErp)){
            erpList.add(leaderErp);
        }
        if(CollectionUtils.isEmpty(erpList)){
            logger.warn("设备编码:{}的任务未维护创建人!", machineCode);
            return;
        }
        content = String.format(template, machineCode,
                DateHelper.formatDate(machineCalibrateDetail.getTaskCreateTime(), Constants.DATE_TIME_FORMAT),
                DateHelper.formatDate(machineCalibrateDetail.getTaskEndTime(), Constants.DATE_TIME_FORMAT));
        NoticeUtils.noticeToTimelineWithNoUrl(title, content, erpList);
    }
}
