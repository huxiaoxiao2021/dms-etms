package com.jd.common.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.MSCodeMapping;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.ministore.exception.MiniStoreBizException;
import com.jd.bluedragon.enums.EnvEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import java.lang.reflect.Method;
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
      jdCResponse = (JdCResponse) jp.proceed();
    } catch (Throwable throwable) {
      Map<String, Object> params =getRequestArgs(jp);
      log.error("UnifiedExceptionProcess catch exception：{}，requestArgs：{}", throwable,JsonHelper.toJson(params));
            if (throwable instanceof MiniStoreBizException) {
                MiniStoreBizException exception = (MiniStoreBizException) throwable;
                return new JdCResponse(exception.getCode(), exception.getMessage());
            }
            if (throwable instanceof JyBizException) {
                JyBizException exception = (JyBizException) throwable;
                if (ObjectHelper.isNotNull(exception.getCode())){
                    return new JdCResponse(exception.getCode(), exception.getMessage());
                }
                return new JdCResponse(CODE_ERROR, exception.getMessage());
            }
            if (EnvEnum.TEST.getCode().equals(env) && throwable instanceof Exception){
                return new JdCResponse(CODE_ERROR, throwable.getMessage());
            }
            return new JdCResponse(MSCodeMapping.UNKNOW_ERROR.getCode(), MSCodeMapping.UNKNOW_ERROR.getMessage());
        }
        return jdCResponse;
    }

  private Map<String, Object> getRequestArgs(ProceedingJoinPoint jp) {
    Map<String, Object> params = new HashMap();
    try {
      MethodSignature ms = (MethodSignature) jp.getSignature();
      Method method =ms.getMethod();
      params.put("method",method.getName());
      //Class<?> targetClass = jp.getTarget().getClass();
      //Method targetMethod = targetClass.getDeclaredMethod(ms.getName(), ms.getParameterTypes());
      Object[] args = jp.getArgs();
      String[] names = ((CodeSignature) jp.getSignature()).getParameterNames();
      for (int i = 0; i < names.length; i++) {
        params.put(names[i], args[i]);
      }
    } catch (Exception e) {
      log.error("统一异常处理获取请求参数异常",e);
    }
    return params;
  }
}
