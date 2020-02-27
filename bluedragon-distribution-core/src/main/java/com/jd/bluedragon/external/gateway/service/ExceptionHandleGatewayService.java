package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.api.request.RedeliveryCheckRequest;
import com.jd.bluedragon.distribution.api.response.QualityControlResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.qualityControl.domain.RedeliveryMode;

/**
 * 异常相关
 * 发布到物流网关 由安卓调用
 * @author lixin39
 * @Description TODO
 * @ClassName ExceptionHandleGatewayService
 * @date 2019/9/16
 */
public interface ExceptionHandleGatewayService {

    /**
     * 协商再投校验
     *
     * @param request
     * @return
     */
    InvokeResult<RedeliveryMode> reDeliveryCheck(RedeliveryCheckRequest request);

    /**
     * 异常提交
     *
     * @param request
     * @return
     */
    QualityControlResponse exceptionSubmit(QualityControlRequest request);

}
