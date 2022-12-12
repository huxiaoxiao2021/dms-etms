package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateDetail;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateRequest;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateTaskResult;
import com.jd.bluedragon.common.dto.operation.workbench.enums.CalibrateDetailStatusEnum;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateDetailEntity;
import com.jd.bluedragon.distribution.jy.calibrate.JyBizTaskMachineCalibrateEntity;
import com.jd.bluedragon.distribution.jy.dto.calibrate.DwsMachineCalibrateMQ;
import com.jd.bluedragon.distribution.jy.dto.calibrate.JyBizTaskMachineCalibrateQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 设备称重量方校准服务实现
 *
 * @author hujiping
 * @date 2022/12/7 9:00 PM
 */
@Service("jyWeightVolumeCalibrateService")
public class JyWeightVolumeCalibrateServiceImpl implements JyWeightVolumeCalibrateService{

    @Autowired
    private JyBizTaskMachineCalibrateService jyBizTaskMachineCalibrateService;

    @Autowired
    private JyBizTaskMachineCalibrateDetailService jyBizTaskMachineCalibrateDetailService;

    @Override
    public InvokeResult<DwsWeightVolumeCalibrateTaskResult> machineCalibrateScan(DwsWeightVolumeCalibrateRequest request) {
        // todo

        return null;
    }

    @Override
    public InvokeResult<List<DwsWeightVolumeCalibrateDetail>> getMachineCalibrateDetail(DwsWeightVolumeCalibrateRequest request) {
        // todo

        return null;
    }

    @Override
    public InvokeResult<Void> closeMachineCalibrateTask(DwsWeightVolumeCalibrateRequest request) {
        InvokeResult<Void> result = new InvokeResult<>();
        JyBizTaskMachineCalibrateEntity entity = new JyBizTaskMachineCalibrateEntity();
        entity.setMachineCode(request.getMachineCode());
        entity.setCalibrateTaskStartTime(request.getCalibrateTaskStartTime());
        entity.setCalibrateTaskCloseTime(new Date());
        entity.setUpdateTime(new Date());
        if (jyBizTaskMachineCalibrateService.closeMachineCalibrateTask(entity) == 1){
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
    public InvokeResult<Boolean> dealCalibrateTask(DwsMachineCalibrateMQ dwsMachineCalibrateMQ) throws Exception {
        JyBizTaskMachineCalibrateQuery query = new JyBizTaskMachineCalibrateQuery();
        query.setMachineCode(dwsMachineCalibrateMQ.getMachineCode());
        query.setCalibrateTime(new Date(dwsMachineCalibrateMQ.getCalibrateTime()));
        JyBizTaskMachineCalibrateDetailEntity taskDetail = jyBizTaskMachineCalibrateDetailService.queryCurrentTaskDetail(query);
        taskDetail.setMachineStatus(dwsMachineCalibrateMQ.getMachineStatus());
        if (taskDetail == null){
            throw new Exception("找不到设备编码为【" + dwsMachineCalibrateMQ.getMachineCode() + "】的待处理任务");
        }
        if (Constants.CALIBRATE_WEIGHT.equals(dwsMachineCalibrateMQ.getCalibrateType())){
            taskDetail.setWeightCalibrateStatus(dwsMachineCalibrateMQ.getCalibrateStatus());
            taskDetail.setWeightCalibrateTime(new Date(dwsMachineCalibrateMQ.getCalibrateTime()));
        }
        if (Constants.CALIBRATE_VOLUME.equals(dwsMachineCalibrateMQ.getCalibrateType())){
            taskDetail.setVolumeCalibrateStatus(dwsMachineCalibrateMQ.getCalibrateStatus());
            taskDetail.setVolumeCalibrateTime(new Date(dwsMachineCalibrateMQ.getCalibrateTime()));
        }

        Boolean isFinished = taskDetail.getWeightCalibrateStatus() != null && taskDetail.getVolumeCalibrateStatus() != null;
        if(isFinished){
            taskDetail.setCalibrateFinishTime(new Date(dwsMachineCalibrateMQ.getCalibrateTime()));
            taskDetail.setTaskStatus(CalibrateDetailStatusEnum.SOLVED.getStatusCode());
            jyBizTaskMachineCalibrateDetailService.update(taskDetail);
            // todo 创建新任务
        }


        return null;
    }
}
