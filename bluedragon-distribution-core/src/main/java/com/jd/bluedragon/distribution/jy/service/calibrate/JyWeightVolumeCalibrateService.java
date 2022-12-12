package com.jd.bluedragon.distribution.jy.service.calibrate;

import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateDetail;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateRequest;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateTaskResult;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.dto.calibrate.DwsMachineCalibrateMQ;

import java.util.List;

/**
 * 拣运称重量方校准服务
 *
 * @author hujiping
 * @date 2022/12/5 10:26 PM
 */
public interface JyWeightVolumeCalibrateService {

    /**
     * 设备校准任务扫描
     *
     * @param request
     * @return
     */
    InvokeResult<DwsWeightVolumeCalibrateTaskResult> machineCalibrateScan(DwsWeightVolumeCalibrateRequest request);

    /**
     * 获取设备校准明细
     *
     * @param request
     * @return
     */
    InvokeResult<List<DwsWeightVolumeCalibrateDetail>> getMachineCalibrateDetail(DwsWeightVolumeCalibrateRequest request);

    /**
     * 关闭设备校准任务
     *
     * @param request
     * @return
     */
    InvokeResult<Void> closeMachineCalibrateTask(DwsWeightVolumeCalibrateRequest request);

    /**
     * 根据设备校准数据处理校准任务
     *
     * @param dwsMachineCalibrateMQ
     * @return
     */
    InvokeResult<Boolean> dealCalibrateTask(DwsMachineCalibrateMQ dwsMachineCalibrateMQ) throws Exception;
}
