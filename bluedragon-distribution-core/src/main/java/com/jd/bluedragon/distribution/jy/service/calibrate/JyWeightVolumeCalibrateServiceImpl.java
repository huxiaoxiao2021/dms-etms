package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateDetail;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateRequest;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateTaskResult;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.calibrate.DwsMachineCalibrateMQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
        // todo

        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public InvokeResult<Boolean> dealCalibrateTask(DwsMachineCalibrateMQ dwsMachineCalibrateMQ) {
        // todo

        return null;
    }
}
