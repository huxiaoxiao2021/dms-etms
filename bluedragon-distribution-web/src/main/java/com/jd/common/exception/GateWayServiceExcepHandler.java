package com.jd.common.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.ResponseCodeMapping;
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
        log.info("invoke start..");
        JdCResponse jdCResponse = new JdCResponse<>();
        try {
            jdCResponse = (JdCResponse) proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("serviceExceptionHandler异常处理...", throwable);
            //在这做异常mapping处理封装
            if (throwable instanceof ValidationException){
                ValidationException exception =(ValidationException) throwable;
                return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR.getCode(),exception.getMessage());
            }
            if (throwable instanceof ConstraintViolationException) {
                Set<ConstraintViolation<?>> exceptionSet = ((ConstraintViolationException) throwable).getConstraintViolations();
                StringBuilder sb = new StringBuilder();
                if (!CollectionUtils.isEmpty(exceptionSet)) {
                    for (ConstraintViolation<?> set : exceptionSet) {
                        if (sb.length() != 0) {
                            sb.append("，");
                        }

                        sb.append(set.getMessageTemplate());
                    }
                }
                return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR.getCode(),sb.toString());
            }
            if (throwable instanceof JsonMappingException) {
                return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR.getCode(),"JSON格式错误, " + throwable.getLocalizedMessage());
            }
            if (throwable instanceof HttpMessageNotReadableException) {
                return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR.getCode(),"请求体格式错误, " + throwable.getLocalizedMessage());
            }
            if (throwable instanceof MissingServletRequestParameterException) {
                String paramName = ((MissingServletRequestParameterException) throwable).getParameterName();
                return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR.getCode(),paramName + " 不能为空");
            }
            return JdCResponse.errorResponse(ResponseCodeMapping.UNKNOW_ERROR);
        }
        log.info("invoke end..");
        return jdCResponse;
    }
}
