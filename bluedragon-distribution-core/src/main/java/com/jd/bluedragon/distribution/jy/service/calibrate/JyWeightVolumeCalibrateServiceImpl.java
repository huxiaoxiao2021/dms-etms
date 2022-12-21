package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DWSCheckRequest;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckRecord;
import com.jd.bd.dms.automatic.sdk.modules.dwsCheck.dto.DwsCheckResponse;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.*;
import com.jd.bluedragon.common.dto.operation.workbench.enums.*;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.DWSCheckManager;
import com.jd.bluedragon.core.base.HrUserManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateCondition;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateDetailEntity;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateEntity;
import com.jd.bluedragon.distribution.jy.dto.calibrate.DwsMachineCalibrateMQ;
import com.jd.bluedragon.distribution.jy.dto.calibrate.JyBizTaskMachineCalibrateMessage;
import com.jd.bluedragon.distribution.jy.dto.calibrate.JyBizTaskMachineCalibrateQuery;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.NoticeUtils;
import com.jd.ql.dms.common.cache.CacheService;
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

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
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
        String redisKey = machineCode + Constants.SEPARATOR_HYPHEN + userErp;
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
            machineTaskCondition.setCreateUserErp(request.getUser().getUserErp());
            JyBizTaskMachineCalibrateDetailEntity machineTaskEntity = jyBizTaskMachineCalibrateDetailService.selectLatelyOneByCondition(machineTaskCondition);

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
                // 设备任务已关闭
                Long intervalTime = uccPropertyConfiguration.getMachineCalibrateTaskForceCreateIntervalTime();
                if(System.currentTimeMillis() - machineRecord.getCalibrateTaskCloseTime().getTime() < intervalTime){
                    // explain：如果设备抽检任务关闭后，在2h内继续扫描设备编码，则进行友好提示（客户端可强制确定来创建新任务）
                    result.customMessage(30001, JyBizTaskMachineCalibrateMessage.MACHINE_CALIBRATE_TASK_CLOSED_AND_NOT_OVER_2_HINT);
                    return;
                }
                isCreateTaskFlag = true;
            }else {
                // 设备任务未关闭
                if(machineTaskEntity == null){
                    result.error(JyBizTaskMachineCalibrateMessage.MACHINE_CALIBRATE_TASK_NOT_FIND_HINT);
                    return;
                }
                String createUserErp = machineTaskEntity.getCreateUserErp();
                if(!Objects.equals(userErp, createUserErp)){
                    result.error(String.format(JyBizTaskMachineCalibrateMessage.MACHINE_CALIBRATE_TASK_CREATED_WITH_OTHER_HINT, createUserErp));
                    return;
                }
                if(!Objects.equals(machineTaskEntity.getTaskStatus(), JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_TODO.getCode())){
                    result.error(JyBizTaskMachineCalibrateMessage.MACHINE_CALIBRATE_STATUS_ERROR_HINT);
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
        result.getData().setSystemTime(System.currentTimeMillis());
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
        if(entity.getVolumeCalibrateStatus() == null){
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

    @Override
    public InvokeResult<DwsWeightVolumeCalibrateDetailResult> getMachineCalibrateDetail(DwsWeightVolumeCalibrateRequest request) {
        InvokeResult<DwsWeightVolumeCalibrateDetailResult> result = new InvokeResult();
        DwsWeightVolumeCalibrateDetailResult detailResult = new DwsWeightVolumeCalibrateDetailResult();
        if (request.getMachineCode() == null || request.getCalibrateTaskStartTime() == null || request.getCalibrateTaskEndTime() == null) {
            logger.error("查询设备校验细节出错，入参{}", request);
            result.error("查询设备校验细节出错，请联系分拣小秘进行处理！");
            return result;
        }
        DWSCheckRequest checkRequest = new DWSCheckRequest();
        checkRequest.setMachineCode(request.getMachineCode());
        checkRequest.setQueryStartTime(request.getCalibrateTaskStartTime());
        checkRequest.setQueryEndTime(request.getCalibrateTaskEndTime());
        DwsCheckResponse response = dwsCheckManager.getLastDwsCheckByTime(checkRequest);
        if (response == null){
            result.error("查询设备校验细节出错，请联系分拣小秘进行处理！");
            logger.error("自动化接口调用response为空，入参{}", checkRequest);
            return result;
        }
        List<DwsWeightVolumeCalibrateDetail> detailList = new ArrayList<>();
        if (response.getDetailList() != null) {
            Long calibrateFishTime = Long.MIN_VALUE;
            for (DwsCheckRecord record : response.getDetailList()) {
                DwsWeightVolumeCalibrateDetail detail = new DwsWeightVolumeCalibrateDetail();
                detail.setCalibrateType(record.getCalibrateType());
                detail.setMachineCode(record.getMachineCode());
                detail.setFarmarCode(record.getFarmarCode());
                detail.setFarmarWeight(String.valueOf(record.getFarmarWeight()));
                detail.setFarmarLength(String.valueOf(record.getFarmarLength()));
                detail.setFarmarWidth(String.valueOf(record.getFarmarWidth()));
                detail.setFarmarHigh(String.valueOf(record.getFarmarHigh()));
                detail.setActualWeight(String.valueOf(record.getActualWeight()));
                detail.setActualLength(String.valueOf(record.getActualLength()));
                detail.setActualWidth(String.valueOf(record.getActualWidth()));
                detail.setActualHigh(String.valueOf(record.getActualHigh()));
                detail.setCalibrateStatus(record.getCalibrateStatus());
                detail.setCalibrateTime(detail.getCalibrateTime());
                detail.setErrorRange(record.getErrorRange());
                detailList.add(detail);

                calibrateFishTime = Math.max(calibrateFishTime, record.getCalibrateTime());
            }
            detailResult.setCalibrateFinishTime(calibrateFishTime);
        }
        detailResult.setDetailList(detailList);
        detailResult.setPreviousMachineEligibleTime(response.getPreviousMachineEligibleTime());
        detailResult.setMachineCode(request.getMachineCode());
        detailResult.setMachineStatus(response.getMachineStatus());

        JyBizTaskMachineCalibrateQuery query = new JyBizTaskMachineCalibrateQuery();
        query.setMachineCode(request.getMachineCode());
        query.setTaskCreateTime(new Date(request.getCalibrateTaskStartTime()));
        query.setTaskEndTime(new Date(request.getCalibrateTaskEndTime()));
        detailResult.setTaskCreateTime(request.getCalibrateTaskStartTime());
        detailResult.setTaskEndTime(request.getCalibrateTaskEndTime());
        result.setData(detailResult);
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public InvokeResult<Void> closeMachineCalibrateTask(DwsWeightVolumeCalibrateRequest request) {
        InvokeResult<Void> result = new InvokeResult<>();
        JyBizTaskMachineCalibrateEntity entity = new JyBizTaskMachineCalibrateEntity();
        entity.setMachineCode(request.getMachineCode());
        entity.setCalibrateTaskStartTime(new Date(request.getCalibrateTaskStartTime()));
        entity.setCalibrateTaskCloseTime(new Date());
        entity.setUpdateTime(new Date());
        if (jyBizTaskMachineCalibrateService.closeMachineCalibrateTask(entity) == Constants.CONSTANT_NUMBER_ONE){
            JyBizTaskMachineCalibrateDetailEntity deleteEntity = new JyBizTaskMachineCalibrateDetailEntity();
            deleteEntity.setMachineCode(request.getMachineCode());
            deleteEntity.setUpdateUserErp(request.getUser().getUserErp());
            deleteEntity.setUpdateTime(new Date());
            //设备关闭废弃当前的最新的待处理任务
            jyBizTaskMachineCalibrateDetailService.duplicateNewestTaskDetail(deleteEntity);
            result.customMessage(InvokeResult.RESULT_SUCCESS_CODE, "任务关闭成功！");
        }else {
            result.customMessage(InvokeResult.SERVER_ERROR_CODE, "任务关闭失败！");
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public InvokeResult<Boolean> dealCalibrateTask(DwsMachineCalibrateMQ dwsMachineCalibrateMQ) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        JyBizTaskMachineCalibrateQuery query = new JyBizTaskMachineCalibrateQuery();
        query.setMachineCode(dwsMachineCalibrateMQ.getMachineCode());
        query.setCalibrateTime(new Date(dwsMachineCalibrateMQ.getCalibrateTime()));
        JyBizTaskMachineCalibrateDetailEntity taskDetail = jyBizTaskMachineCalibrateDetailService.queryCurrentTaskDetail(query);
        if (taskDetail == null){
            logger.warn("找不到设备编码为:{}的待处理任务!", dwsMachineCalibrateMQ.getMachineCode());
            result.error("设备" + dwsMachineCalibrateMQ.getMachineCode() + "未生成待处理任务，请联系分拣小秘排查!");
            return result;
        }
        taskDetail.setMachineStatus(dwsMachineCalibrateMQ.getMachineStatus());
        if (Objects.equals(JyBizTaskMachineCalibrateTypeEnum.CALIBRATE_TYPE_W.getCode(), dwsMachineCalibrateMQ.getCalibrateType())){
            taskDetail.setWeightCalibrateStatus(dwsMachineCalibrateMQ.getCalibrateStatus());
            taskDetail.setWeightCalibrateTime(new Date(dwsMachineCalibrateMQ.getCalibrateTime()));
        }
        if (Objects.equals(JyBizTaskMachineCalibrateTypeEnum.CALIBRATE_TYPE_V.getCode(), dwsMachineCalibrateMQ.getCalibrateType())){
            taskDetail.setVolumeCalibrateStatus(dwsMachineCalibrateMQ.getCalibrateStatus());
            taskDetail.setVolumeCalibrateTime(new Date(dwsMachineCalibrateMQ.getCalibrateTime()));
        }

        boolean isFinished = !Objects.equals(taskDetail.getWeightCalibrateStatus(), JyBizTaskMachineWeightCalibrateStatusEnum.NO_CALIBRATE.getCode())
                && !Objects.equals(taskDetail.getVolumeCalibrateStatus(), JyBizTaskMachineVolumeCalibrateStatusEnum.NO_CALIBRATE.getCode());
        if(isFinished){
            Date calibrateFinishTime = taskDetail.getWeightCalibrateTime().getTime() > taskDetail.getVolumeCalibrateTime().getTime() ?
                    taskDetail.getWeightCalibrateTime() : taskDetail.getVolumeCalibrateTime();
            taskDetail.setCalibrateFinishTime(calibrateFinishTime);
            taskDetail.setTaskStatus(JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_COMPLETE.getCode());
            // 任务完成后创建新任务
            createNewTaskAfterCompleteTask(dwsMachineCalibrateMQ, taskDetail);
        }
        jyBizTaskMachineCalibrateDetailService.update(taskDetail);
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
        jyBizTaskMachineCalibrateDetailService.insert(entity);
    }

    @Override
    public InvokeResult<Boolean> regularScanCalibrateTask() {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();
        JyBizTaskMachineCalibrateCondition condition = new JyBizTaskMachineCalibrateCondition();
        condition.setTaskStatus(JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_TODO.getCode());
        condition.setTaskEndTime(new Date());
        // 超时任务
        List<JyBizTaskMachineCalibrateDetailEntity> list = jyBizTaskMachineCalibrateDetailService.selectByCondition(condition);
        if(CollectionUtils.isEmpty(list)){
            logger.warn("待处理任务未超过限定时长，无需处理!");
            return result;
        }
        List<Long> ids = Lists.newArrayList();
        for (JyBizTaskMachineCalibrateDetailEntity entity : list) {
            ids.add(entity.getId());
        }
        // 任务状态批量更新为'超时'
        jyBizTaskMachineCalibrateDetailService.batchUpdateStatus(ids, JyBizTaskMachineCalibrateTaskStatusEnum.TASK_STATUS_OVERTIME.getCode());
        // 超时推送咚咚
        noticeToDD(list);
        return result;
    }

    private void noticeToDD(List<JyBizTaskMachineCalibrateDetailEntity> list) {
        String title = "设备校准任务已超时";
        String content;
        String template = "设备编码:%s在时间%s-%s内的校准任务未处理，已超时，请及时处理!";
        // 最外层是key: erp, 里层key: machineCode
        Map<String, Map<String, JyBizTaskMachineCalibrateDetailEntity>> erpMap = Maps.newHashMap();
        for (JyBizTaskMachineCalibrateDetailEntity entity : list) {
            String createUserErp = entity.getCreateUserErp();
            String machineCode = entity.getMachineCode();
            if(erpMap.containsKey(createUserErp)){
                erpMap.get(createUserErp).put(machineCode, entity);
            }else {
                // key: machineCode
                Map<String, JyBizTaskMachineCalibrateDetailEntity> machineMap = Maps.newHashMap();
                machineMap.put(machineCode, entity);
                erpMap.put(createUserErp, machineMap);
            }
        }
        for (Map.Entry<String, Map<String, JyBizTaskMachineCalibrateDetailEntity>> erpEntry : erpMap.entrySet()) {
            String createUserErp = erpEntry.getKey();
            for (Map.Entry<String, JyBizTaskMachineCalibrateDetailEntity> machineEntry : erpEntry.getValue().entrySet()) {
                String machineCode = machineEntry.getKey();
                String leaderErp = hrUserManager.getSuperiorErp(createUserErp);
                List<String> erpList = Lists.newArrayList();
                erpList.add(createUserErp);
                if(StringUtils.isNotEmpty(leaderErp)){
                    erpList.add(leaderErp);
                }
                if(CollectionUtils.isEmpty(erpList)){
                    logger.warn("设备编码:{}的任务未维护创建人!", machineCode);
                    continue;
                }
                JyBizTaskMachineCalibrateDetailEntity entity = machineEntry.getValue();
                content = String.format(template, entity.getMachineCode(),
                        DateHelper.formatDate(entity.getCreateTime(), Constants.DATE_TIME_FORMAT),
                        DateHelper.formatDate(entity.getTaskEndTime(), Constants.DATE_TIME_FORMAT));
                NoticeUtils.noticeToTimelineWithNoUrl(title, content, erpList);
            }
        }
    }
}
