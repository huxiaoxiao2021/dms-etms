package com.jd.bluedragon.common.service;

import com.jd.bluedragon.common.BoxMsgResult;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.InvokeWithMsgBoxResult;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.enums.EnvEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_CODE;
import static com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_MESSAGE;

@Component
@Aspect
public class UnifiedExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(UnifiedExceptionHandler.class);

    @Value("${app.config.runningMode}")
    protected String env;

    @Around("execution(* com.jd.bluedragon.distribution.jy.service..*.*(..)) && @within(com.jd.bluedragon.common.UnifiedExceptionProcess)")
    public Object serviceExceptionHandler(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            Method method = methodSignature.getMethod();
            BoxMsgResult boxMsgResult = method.getAnnotation(BoxMsgResult.class);

            if (boxMsgResult != null) {
                return new InvokeWithMsgBoxResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
            }
            if (throwable instanceof JyBizException) {
                JyBizException exception = (JyBizException) throwable;
                return new InvokeResult(SERVER_ERROR_CODE, exception.getMessage());
            }
            if (EnvEnum.TEST.getCode().equals(env) && throwable instanceof Exception) {
                log.info("jy服务调用发生异常", throwable);
                return new InvokeResult(SERVER_ERROR_CODE, throwable.getMessage());
            }
            log.error("UnifiedExceptionHandler检测到jy服务调用发生异常", throwable);
            return new InvokeResult(SERVER_ERROR_CODE, SERVER_ERROR_MESSAGE);
        }
    }
}
