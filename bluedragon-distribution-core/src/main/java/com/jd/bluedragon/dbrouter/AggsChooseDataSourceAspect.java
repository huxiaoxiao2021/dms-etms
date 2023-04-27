package com.jd.bluedragon.dbrouter;

import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.enums.ReadWriteTypeEnum;
import com.jd.bluedragon.utils.ObjectHelper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(1)
public class AggsChooseDataSourceAspect {

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AggsChooseDataSourceAspect.class);

  @Value("${jy.aggs.readWrite.type}")
  protected String readWriteType;

  @Value("${jy.aggs.dataSource}")
  protected String aggsDataSource;

  @Autowired
  UccPropertyConfiguration ucc;

  @Pointcut("execution(* com.jd.bluedragon.distribution.jy.service.comboard..*.*(..)) ")
  public void pointCut(){};

  @Before("pointCut()")
  public void before(JoinPoint point) {
    try {
      if (ReadWriteTypeEnum.READ.getType().equals(readWriteType)) {
        if (ObjectHelper.isNotNull(ucc.getAggsDataSource())) {
          DynamicDataSourceType dataSourceType = DynamicDataSourceHolders.getDataSources(ucc.getAggsDataSource());
          if (ObjectHelper.isNotNull(dataSourceType)) {
            DynamicDataSourceHolders.putDataSource(dataSourceType);
          }
        } else if (ObjectHelper.isNotNull(aggsDataSource)) {
          DynamicDataSourceType dataSourceType = DynamicDataSourceHolders.getDataSources(aggsDataSource);
          if (ObjectHelper.isNotNull(dataSourceType)) {
            DynamicDataSourceHolders.putDataSource(dataSourceType);
          }
        }
      } else if (ReadWriteTypeEnum.WRITE.getType().equals(readWriteType)) {
        if (ObjectHelper.isNotNull(aggsDataSource)) {
          DynamicDataSourceType dataSourceType = DynamicDataSourceHolders.getDataSources(aggsDataSource);
          if (ObjectHelper.isNotNull(dataSourceType)) {
            DynamicDataSourceHolders.putDataSource(dataSourceType);
          }
        }
      }
    } catch (Exception e) {
      logger.error(String.format("Choose DataSource error", e));
    }
  }

  @After("pointCut()")
  public void after(JoinPoint point) {
    DynamicDataSourceHolders.clearDataSource();
  }

}
