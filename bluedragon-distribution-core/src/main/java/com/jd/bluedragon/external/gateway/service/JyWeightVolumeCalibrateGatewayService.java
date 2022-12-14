package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateDetailResult;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateRequest;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateTaskResult;

/**
 * 拣运设备称重重量校准网关服务
 *
 * @author hujiping
 * @date 2022/12/7 7:41 PM
 */
public interface JyWeightVolumeCalibrateGatewayService {

    /**
     * 设备称重量方校准扫描
     *
     * @param request
     * @return
     */
    JdCResponse<DwsWeightVolumeCalibrateTaskResult> machineCalibrateScan(DwsWeightVolumeCalibrateRequest request);

    /**
     * 获取设备校准任务明细
     *
     * @param request
     * @return
     */
    JdCResponse<DwsWeightVolumeCalibrateDetailResult> getMachineCalibrateDetail(DwsWeightVolumeCalibrateRequest request);

    /**
     * 关闭设备校准任务
     *
     * @param request
     * @return
     */
    JdCResponse<Void> closeMachineCalibrateTask(DwsWeightVolumeCalibrateRequest request);
}
