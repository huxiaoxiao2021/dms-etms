package com.jd.bluedragon.common.aspect;

import com.jd.bluedragon.core.simpleComplex.SimpleComplexSwitchContext;
import com.jd.bluedragon.core.simpleComplex.SimpleComplexSwitchExecutor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * resteasy接口-简繁体切换切片
 *
 * @author hujiping
 * @date 2023/7/26 10:42 AM
 */
@Aspect
@Component
public class SimpleComplexSwitchAspect {

    @Autowired
    private SimpleComplexSwitchExecutor simpleComplexSwitchExecutor;
    
    // easyrest接口切入点
    @Pointcut("execution(* com.jd.bluedragon.distribution.rest..*.*(..))")
    public void restMethod() {}

    // gateway-jsf接口切入点
    @Pointcut("execution(* com.jd.bluedragon.external.gateway.service.*.*(..))")
    public void gatewayJsfMethod() {}
    
    // 环绕通知
    @Around("restMethod() || gatewayJsfMethod()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        boolean isComplex = queryIsSwitchToComplex();
        // 入参繁to简
        if(isComplex && pjp.getArgs() != null){
            for (Object arg : pjp.getArgs()) {
                simpleComplexSwitchExecutor.recursiveDeal(arg, SimpleComplexSwitchContext.SIMPLE_TYPE);
            }
        }
        // 业务逻辑
        Object result = pjp.proceed();
        Object dest = null;
        if(isComplex){
            // new object    
            dest = simpleComplexSwitchExecutor.copyAndReturnNewInstance(result);
            if(dest != null){
                // 返回值简to繁
                simpleComplexSwitchExecutor.recursiveDeal(dest, SimpleComplexSwitchContext.COMPLEX_TYPE);    
            }
        }
        return dest == null ? result : dest;
    }

    private boolean queryIsSwitchToComplex() {
        return (SimpleComplexSwitchContext.getRestThreadInfo() != null
                && Objects.equals(SimpleComplexSwitchContext.getRestThreadInfo().getSimpleComplexFlag(), SimpleComplexSwitchContext.COMPLEX)) 
                || SimpleComplexSwitchContext.getJsfThreadInfo() != null
                && Objects.equals(SimpleComplexSwitchContext.getJsfThreadInfo().getSimpleComplexFlag(), SimpleComplexSwitchContext.COMPLEX);
    }

}
