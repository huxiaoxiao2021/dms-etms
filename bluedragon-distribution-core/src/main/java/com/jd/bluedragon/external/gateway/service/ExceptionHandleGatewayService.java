package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName ExceptionHandleGatewayService
 * @date 2019/9/16
 */
public interface ExceptionHandleGatewayService {

    /**
     * 异常提交
     *
     * @param request
     * @return
     */
    QualityControlResponse exceptionSubmit(QualityControlRequest request);

}
