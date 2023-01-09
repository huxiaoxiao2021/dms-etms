package com.jd.common.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.ministore.exception.MiniStoreBizException;
import com.jd.bluedragon.enums.EnvEnum;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.fastjson.JSONObject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.jd.bluedragon.common.dto.base.response.JdCResponse.CODE_ERROR;

@Component
@Aspect
@Order(-1)
public class GateWayServiceExcepHandler {

  private static final Logger log = LoggerFactory.getLogger(GateWayServiceExcepHandler.class);

  @Value("${app.config.runningMode}")
  protected String env;

  @Around("execution(* com.jd.bluedragon.distribution.external.gateway.service.impl..*.*(..)) && @within(com.jd.bluedragon.common.UnifiedExceptionProcess)")
  public JdCResponse serviceExceptionHandler(ProceedingJoinPoint jp) {
    JdCResponse jdCResponse;
    try {
      Class<?> targetClass = jp.getTarget().getClass();
      MethodSignature ms = (MethodSignature) jp.getSignature();
      Method method =ms.getMethod();
      log.info("GateWayServiceExcepHandler===method："+method+"===");
      String targetClassName = targetClass.getName();
      log.info("GateWayServiceExcepHandler===targetClassName："+targetClassName+"===");
      Method targetMethod = targetClass.getDeclaredMethod(ms.getName(), ms.getParameterTypes());
      log.info("GateWayServiceExcepHandler===targetMethod："+targetMethod+"===");
      Object[] args = jp.getArgs();
      String[] names = ((CodeSignature) jp.getSignature()).getParameterNames();
      Map<String, Object> params = new HashMap();
      for (int i = 0; i < names.length; i++) {
        params.put(names[i], args[i]);
      }

      log.info("GateWayServiceExcepHandler请求参数：{}",JSONObject.toJSONString(params));

      jdCResponse = (JdCResponse) jp.proceed();
    } catch (Throwable throwable) {
      log.error("GateWayServiceExcepHandler catch exception:", throwable);

      if (throwable instanceof MiniStoreBizException) {
        MiniStoreBizException exception = (MiniStoreBizException) throwable;
        return new JdCResponse(exception.getCode(), exception.getMessage());
      }
      if (throwable instanceof JyBizException) {
        JyBizException exception = (JyBizException) throwable;
        if (ObjectHelper.isNotNull(exception.getCode())) {
          return new JdCResponse(exception.getCode(), exception.getMessage());
        }
        return new JdCResponse(CODE_ERROR, exception.getMessage());
      }
      if (EnvEnum.TEST.getCode().equals(env) && throwable instanceof Exception) {
        return new JdCResponse(CODE_ERROR, throwable.getMessage());
      }
      return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(),
          MSCodeMapping.UNKNOW_ERROR.getMessage());
    }
    return jdCResponse;
  }
}
