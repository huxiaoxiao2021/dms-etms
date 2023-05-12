package com.jd.bluedragon.dbrouter;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.businessIntercept.constants.Constant;
import com.jd.bluedragon.enums.ReadWriteTypeEnum;
import com.jd.bluedragon.utils.BeanUtils;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jmq.common.message.Message;
import java.lang.reflect.Field;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
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

  @Autowired
  UccPropertyConfiguration ucc;

  @Value("${jmq4.dmsWeb.app}")
  protected String jmq4GroupWeb;

  @Pointcut("@within(com.jd.bluedragon.dbrouter.NeedChangeDataSources)")
  public void pointCut() {
  }

  @Before("pointCut()")
  public void before(JoinPoint point) {
    try {
      if (ReadWriteTypeEnum.READ.getType().equals(readWriteType)) {
        logger.info("===================AggsChooseDataSourceAspect read=========================");
        if (ObjectHelper.isNotNull(ucc.getAggsDataSource())) {
          logger.info("===================AggsChooseDataSourceAspect read {} =========================",ucc.getAggsDataSource());
          DynamicDataSourceType dataSourceType = DynamicDataSourceHolders.getDataSources(ucc.getAggsDataSource());
          if (ObjectHelper.isNotNull(dataSourceType)) {
            logger.info("===================AggsChooseDataSourceAspect read dataSourceType {} =========================",dataSourceType);
            DynamicDataSourceHolders.putDataSource(dataSourceType);
          }
        }
      }
      else if (ReadWriteTypeEnum.WRITE.getType().equals(readWriteType)){
        Object target = point.getTarget();
        if (ObjectHelper.isNotNull(target)){
          if (!BeanUtils.hasField(target,"dataSourceType")){
            return;
          }
          Field field =target.getClass().getDeclaredField("dataSourceType");
          field.setAccessible(true);
          String dataSource = (String)field.get(target);
          logger.info("==========AggsChooseDataSourceAspect write dataSource============ {}",dataSource);
          if (ObjectHelper.isNotNull(dataSource)){
            DynamicDataSourceType dataSourceType = DynamicDataSourceHolders.getDataSources(dataSource);
            if (ObjectHelper.isNotNull(dataSourceType)){
              logger.info("==========AggsChooseDataSourceAspect write dataSourceType============ {}",dataSourceType);
              DynamicDataSourceHolders.putDataSource(dataSourceType);
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("aggsDataSourceAspect Choose DataSource error", e);
    }
  }

  @After("pointCut()")
  public void after(JoinPoint point) {
    if (ReadWriteTypeEnum.READ.getType().equals(readWriteType)){
      DynamicDataSourceHolders.clearDataSource();
    }
  }

}
