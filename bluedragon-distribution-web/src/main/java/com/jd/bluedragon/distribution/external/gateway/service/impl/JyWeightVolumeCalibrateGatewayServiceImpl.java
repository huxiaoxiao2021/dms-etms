package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateDetailResult;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateRequest;
import com.jd.bluedragon.common.dto.operation.workbench.calibrate.DwsWeightVolumeCalibrateTaskResult;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.service.calibrate.JyWeightVolumeCalibrateService;
import com.jd.bluedragon.external.gateway.service.JyWeightVolumeCalibrateGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 拣运设备称重重量校准网关服务实现
 *
 * @author hujiping
 * @date 2022/12/7 7:45 PM
 */
@Service("jyWeightVolumeCalibrateGatewayService")
public class JyWeightVolumeCalibrateGatewayServiceImpl implements JyWeightVolumeCalibrateGatewayService {

    @Autowired
    private JyWeightVolumeCalibrateService jyWeightVolumeCalibrateService;

    private <T> JdCResponse<T> retJdCResponse(InvokeResult<T> invokeResult) {
        return new JdCResponse<>(invokeResult.getCode(), invokeResult.getMessage(), invokeResult.getData());
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWeightVolumeCalibrateGatewayService.machineCalibrateScan",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<DwsWeightVolumeCalibrateTaskResult> machineCalibrateScan(DwsWeightVolumeCalibrateRequest request) {
        return retJdCResponse(jyWeightVolumeCalibrateService.machineCalibrateScan(request));
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWeightVolumeCalibrateGatewayService.getMachineCalibrateDetail",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<DwsWeightVolumeCalibrateDetailResult> getMachineCalibrateDetail(DwsWeightVolumeCalibrateRequest request) {
        return retJdCResponse(jyWeightVolumeCalibrateService.getMachineCalibrateDetail(request));
    }

    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JyWeightVolumeCalibrateGatewayService.closeMachineCalibrateTask",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    @Override
    public JdCResponse<Void> closeMachineCalibrateTask(DwsWeightVolumeCalibrateRequest request) {
        return retJdCResponse(jyWeightVolumeCalibrateService.closeMachineCalibrateTask(request));
    }
}
