package com.jd.bluedragon.dbrouter;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.businessIntercept.constants.Constant;
import com.jd.bluedragon.enums.ReadWriteTypeEnum;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jmq.common.message.Message;
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
        Object[] args =point.getArgs();
        if (ObjectHelper.isNotNull(args) && args.length>0){
          Message message =(Message)args[0];
          if (ObjectHelper.isNotNull(message.getTopic())){
            logger.info("==========AggsChooseDataSourceAspect write getTopic============ {}",message.getTopic());
            String group =message.getApp();
            if (ObjectHelper.isNotNull(group) && group.equals(jmq4GroupWeb)){
              logger.info("AggsChooseDataSourceAspect write group {}",group);
              logger.info("==========AggsChooseDataSourceAspect write dataSourceType============ JY_CORE");
              DynamicDataSourceHolders.putDataSource(DynamicDataSourceType.JY_CORE);
              return;
            }
            String dateSourceKey = Constants.topic2DataSource.get(message.getTopic());
            DynamicDataSourceType dataSourceType = DynamicDataSourceHolders.getDataSources(dateSourceKey);
            if (ObjectHelper.isNotNull(dataSourceType)){
              logger.info("==========AggsChooseDataSourceAspect write dataSourceType============ {}",dataSourceType);
              DynamicDataSourceHolders.putDataSource(dataSourceType);
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error(String.format("aggsDataSourceAspect Choose DataSource error", e));
    }
  }

  @After("pointCut()")
  public void after(JoinPoint point) {
    if (ReadWriteTypeEnum.READ.getType().equals(readWriteType)){
      DynamicDataSourceHolders.clearDataSource();
    }
  }

}
