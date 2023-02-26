package com.jd.bluedragon.dbrouter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * ChooseDataSourceMPRAspect:ChooseDataSourceManyTimesPerRequestAspect
 * 一次请求-响应：要选择/切换多个数据源进行数据读写
 */
public class ChooseDataSourceMPRAspect {
  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ChooseDataSourceMPRAspect.class);
  private String preDataSources =new String("");

  public void pointCut(){};

  public void before(JoinPoint point)
  {
    Object target = point.getTarget();
    String methodName = point.getSignature().getName();
    Class<?>[] clazz = target.getClass().getInterfaces();
    Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();

    try {
      Method method = clazz[0].getMethod(methodName, parameterTypes);
      //先判断下 ThreadLocal是否有值 有 使用，没有 执行切面逻辑
      if (null != DynamicDataSourceHolders.getDataSource()){
        if (method != null && method.isAnnotationPresent(DataSources.class)) {
          DataSources data = method.getAnnotation(DataSources.class);
          DynamicDataSourceHolders.putDataSource(data.value());
        }
        preDataSources = DynamicDataSourceHolders.getDataSource().name();
      }
      else {
        if (method != null && method.isAnnotationPresent(DataSources.class)) {
          DataSources data = method.getAnnotation(DataSources.class);
          DynamicDataSourceHolders.putDataSource(data.value());
        }
      }

    } catch (Exception e) {
      logger.error("Choose DataSource error",e);
      logger.error(String.format("Choose DataSource error, method:%s, msg:%s", methodName, e.getMessage()));
    }
  }
  public void after(JoinPoint point) {//还原数据源
    if ("".equals(preDataSources)){
      DynamicDataSourceHolders.clearDataSource();
    }
    else {
      DynamicDataSourceHolders.clearDataSource();
      DynamicDataSourceHolders.putDataSource(DynamicDataSourceHolders.getDataSources(preDataSources));
    }
  }

}
