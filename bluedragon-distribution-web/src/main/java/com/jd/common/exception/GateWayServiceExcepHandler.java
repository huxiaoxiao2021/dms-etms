package com.jd.common.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.ministore.exception.MiniStoreBizException;
import com.jd.bluedragon.enums.EnvEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.jd.bluedragon.common.dto.base.response.JdCResponse.CODE_ERROR;

@Component
@Aspect
public class GateWayServiceExcepHandler {
    private static final Logger log = LoggerFactory.getLogger(GateWayServiceExcepHandler.class);

    @Value("${app.config.runningMode}")
    protected String env;

    @Around("execution(* com.jd.bluedragon.distribution.external.gateway.service.impl..*.*(..)) && @within(com.jd.bluedragon.common.UnifiedExceptionProcess)")
    public JdCResponse serviceExceptionHandler(ProceedingJoinPoint proceedingJoinPoint) {
        JdCResponse jdCResponse;
        try {
            jdCResponse = (JdCResponse) proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {

            if (throwable instanceof MiniStoreBizException) {
                MiniStoreBizException exception = (MiniStoreBizException) throwable;
                return new JdCResponse(exception.getCode(), exception.getMessage());
            }
            if (throwable instanceof JyBizException) {
                JyBizException exception = (JyBizException) throwable;
                return new JdCResponse(CODE_ERROR, exception.getMessage());
            }
            if (EnvEnum.TEST.getCode().equals(env) && throwable instanceof Exception){
                return new JdCResponse(CODE_ERROR, throwable.getMessage());
            }
            return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
        }
        return jdCResponse;
    }
}
