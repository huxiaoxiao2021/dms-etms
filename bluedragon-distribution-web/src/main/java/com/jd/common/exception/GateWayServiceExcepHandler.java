package com.jd.common.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.ResponseCodeMapping;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class GateWayServiceExcepHandler {
    private static final Logger log = LoggerFactory.getLogger(GateWayServiceExcepHandler.class);

    @Around("execution(* com.jd.bluedragon.distribution.external.gateway.service.impl..*.*(..)) && @within(com.jd.bluedragon.common.UnifiedExceptionProcess)")
    public JdCResponse serviceExceptionHandler(ProceedingJoinPoint proceedingJoinPoint) {
        log.info("invoke start..");
        JdCResponse jdCResponse = new JdCResponse<>();
        try {
            jdCResponse = (JdCResponse) proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("serviceExceptionHandler异常处理...", throwable);
            //在这做异常处理封装
            return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
        }
        log.info("invoke end..");
        return jdCResponse;
    }
}
