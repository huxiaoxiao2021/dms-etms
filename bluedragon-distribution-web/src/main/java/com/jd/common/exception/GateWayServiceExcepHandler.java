package com.jd.common.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.ResponseCodeMapping;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class GateWayServiceExcepHandler {
    @Around("@within(com.jd.bluedragon.distribution.external.gateway.service.impl.*) && @annotation(com.jd.bluedragon.common.UnifiedExceptionProcess)")
    public JdCResponse serviceExcepHandler(ProceedingJoinPoint proceedingJoinPoint) {
        log.info("invoke start..");
        JdCResponse  jdCResponse= new JdCResponse<>();
        try {
            jdCResponse = (JdCResponse) proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("ServiceExcepHandler  failed", throwable);
            return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
        }
        log.info("invoke end..");
        return jdCResponse;
    }
}
