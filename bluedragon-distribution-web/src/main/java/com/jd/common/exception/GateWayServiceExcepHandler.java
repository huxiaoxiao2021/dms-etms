package com.jd.common.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.ResponseCodeMapping;
import com.jd.bluedragon.distribution.ministore.exception.MiniStoreBizException;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;

@Component
@Aspect
public class GateWayServiceExcepHandler {
    private static final Logger log = LoggerFactory.getLogger(GateWayServiceExcepHandler.class);

    @Around("execution(* com.jd.bluedragon.distribution.external.gateway.service.impl..*.*(..)) && @within(com.jd.bluedragon.common.UnifiedExceptionProcess)")
    public JdCResponse serviceExceptionHandler(ProceedingJoinPoint proceedingJoinPoint) {
        JdCResponse jdCResponse;
        try {
            jdCResponse = (JdCResponse) proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {

            if (throwable instanceof MiniStoreBizException) {
                MiniStoreBizException exception = (MiniStoreBizException) throwable;
                return JdCResponse.errorResponse(exception.getCode(), exception.getMessage());
            }
            return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
        }
        return jdCResponse;
    }
}
