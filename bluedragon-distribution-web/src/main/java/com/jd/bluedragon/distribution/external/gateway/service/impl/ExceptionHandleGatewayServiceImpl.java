package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.RedeliveryCheckRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.qualityControl.domain.RedeliveryMode;
import com.jd.bluedragon.distribution.rest.QualityControl.QualityControlResource;
import com.jd.bluedragon.external.gateway.service.ExceptionHandleGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ExceptionHandleGatewayServiceImpl
 * @date 2019/9/16
 */
public class ExceptionHandleGatewayServiceImpl implements ExceptionHandleGatewayService {

    @Autowired
    @Qualifier("qualityControlResource")
    private QualityControlResource qualityControlResource;

    @Override
    public InvokeResult<RedeliveryMode> reDeliveryCheck(RedeliveryCheckRequest request) {
        return qualityControlResource.redeliveryCheckNew(request);
    }

    @Override
    public QualityControlResponse exceptionSubmit(QualityControlRequest request) {
        return qualityControlResource.exceptionInfo(request);
    }

}
