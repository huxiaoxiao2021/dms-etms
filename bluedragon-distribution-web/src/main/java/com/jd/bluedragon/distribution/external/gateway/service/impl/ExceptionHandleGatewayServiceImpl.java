package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.distribution.abnormal.domain.RedeliveryMode;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.RedeliveryCheckRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.external.gateway.service.ExceptionHandleGatewayService;
import org.springframework.beans.factory.annotation.Autowired;

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
    public InvokeResult<RedeliveryMode> reDeliveryCheck(RedeliveryCheckRequest request) {
        return qualityControlService.redeliveryCheck(request);
    }

    @Override
    public QualityControlResponse exceptionSubmit(QualityControlRequest request) {
        QualityControlResponse response = new QualityControlResponse();
        InvokeResult<Boolean> result = qualityControlService.exceptionSubmit(request);
        response.setCode(result.getCode());
        response.setMessage(result.getMessage());
        return response;
    }

}
