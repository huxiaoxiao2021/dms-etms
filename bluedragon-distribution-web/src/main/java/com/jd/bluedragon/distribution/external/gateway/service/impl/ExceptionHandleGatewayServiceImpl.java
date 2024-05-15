package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.distribution.abnormal.domain.RedeliveryMode;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.RedeliveryCheckRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.qualityControl.AbnormalBizSourceEnum;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.external.gateway.service.ExceptionHandleGatewayService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import com.jd.bluedragon.common.dto.jyexpection.request.AbnormalCallbackRequest;
import com.jd.bluedragon.common.dto.jyexpection.response.AbnormalCallbackResponse;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ExceptionHandleGatewayServiceImpl
 * @date 2019/9/16
 */
public class ExceptionHandleGatewayServiceImpl implements ExceptionHandleGatewayService {

    @Autowired
    private QualityControlService qualityControlService;

    @Override
    @JProfiler(jKey = "DMSWEB.ExceptionHandleGatewayServiceImpl.reDeliveryCheck",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<RedeliveryMode> reDeliveryCheck(RedeliveryCheckRequest request) {
        return qualityControlService.redeliveryCheck(request);
    }

    @Override
    @JProfiler(jKey = "DMSWEB.ExceptionHandleGatewayServiceImpl.exceptionSubmit",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public QualityControlResponse exceptionSubmit(QualityControlRequest request) {
        QualityControlResponse response = new QualityControlResponse();
        // 设置菜单来源
        request.setBizSource(AbnormalBizSourceEnum.ABNORMAL_HANDLE.getType());
        InvokeResult<Boolean> result = qualityControlService.exceptionSubmit(request);
        response.setCode(result.getCode());
        response.setMessage(result.getMessage());
        return response;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.ExceptionHandleGatewayServiceImpl.abnormalH5Callback",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdVerifyResponse<AbnormalCallbackResponse> abnormalH5Callback(AbnormalCallbackRequest request) {
        return qualityControlService.abnormalH5Callback(request);
    }

}
