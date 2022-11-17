package com.jd.bluedragon.common.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.enums.EnvEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.jd.bluedragon.common.dto.base.response.JdCResponse.CODE_ERROR;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_MESSAGE;

@Component
@Aspect
public class UnifiedExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(UnifiedExceptionHandler.class);

    @Value("${app.config.runningMode}")
    protected String env;

    @Around("execution(* com.jd.bluedragon.distribution.jy.service..*.*(..)) && @within(com.jd.bluedragon.common.UnifiedExceptionProcess)")
    public InvokeResult serviceExceptionHandler(ProceedingJoinPoint proceedingJoinPoint) {
        InvokeResult invokeResult;
        try {
            invokeResult = (InvokeResult) proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {

            if (throwable instanceof JyBizException) {
                JyBizException exception = (JyBizException) throwable;
                return new InvokeResult(CODE_ERROR, exception.getMessage());
            }
            if (EnvEnum.TEST.getCode().equals(env) && throwable instanceof Exception) {
                log.info("jy服务调用发生异常",throwable);
                return new InvokeResult(CODE_ERROR, throwable.getMessage());
            }
            log.error("UnifiedExceptionHandler检测到jy服务调用发生异常",throwable);
            return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
        }
        return invokeResult;
    }
}
