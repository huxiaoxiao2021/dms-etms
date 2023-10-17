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
public class SimpleComplexSwitchRestAspect {

    @Autowired
    private SimpleComplexSwitchExecutor simpleComplexSwitchExecutor;
    
    // easyrest接口切入点
    @Pointcut("execution(* com.jd.bluedragon.distribution.rest..*.*(..))")
    public void restMethod() {}
    
    // 环绕通知
    @Around("restMethod()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // 入参繁to简
        complexToSimple(pjp);
        // 业务逻辑
        Object result = pjp.proceed();
        // 返回值简to繁
        simpleToComplex(result);
        return result;
    }

    private void complexToSimple(ProceedingJoinPoint pjp) {
        if(SimpleComplexSwitchContext.getRestThreadInfo() != null 
                && Objects.equals(SimpleComplexSwitchContext.getRestThreadInfo().getSimpleComplexFlag(), SimpleComplexSwitchContext.COMPLEX)
                && pjp.getArgs() != null){
            for (Object arg : pjp.getArgs()) {
                simpleComplexSwitchExecutor.recursiveDeal(arg, SimpleComplexSwitchContext.SIMPLE_TYPE);
            }
        }
    }

    private void simpleToComplex(Object result) {
        if(SimpleComplexSwitchContext.getRestThreadInfo() != null 
                && Objects.equals(SimpleComplexSwitchContext.getRestThreadInfo().getSimpleComplexFlag(), SimpleComplexSwitchContext.COMPLEX)){
            simpleComplexSwitchExecutor.recursiveDeal(result, SimpleComplexSwitchContext.COMPLEX_TYPE);
        }
    }

}
