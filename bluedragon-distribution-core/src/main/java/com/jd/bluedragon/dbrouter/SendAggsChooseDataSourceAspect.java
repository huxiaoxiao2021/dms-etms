package com.jd.bluedragon.dbrouter;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.distribution.jy.manager.JySendOrUnloadDataReadDuccConfigManager;
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

import com.jd.jmq.common.message.Message;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/7/18 16:43
 * @Description: 波次待扫数据源切面类
 */

@Component
@Aspect
@Order(-100)
public class SendAggsChooseDataSourceAspect {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AggsChooseDataSourceAspect.class);

    @Value("${jy.aggs.readWrite.type}")
    protected String readWriteType;

    @Value("${jmq.topic.jy.send.predict.aggs}")
    protected String snedAggsMain;

    @Value("${jmq.topic.jy.send.predict.aggs.bak}")
    protected String snedAggsBak;


    @Autowired
    private JySendOrUnloadDataReadDuccConfigManager jyDuccConfigManager;

    @Pointcut("@within(com.jd.bluedragon.dbrouter.SendAggsChangeDataSources)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void before(JoinPoint point) {
        try{

            logger.info("===================SendAggsChooseDataSourceAspect readWriteType {}  =========================",readWriteType);
            if (ReadWriteTypeEnum.READ.getType().equals(readWriteType)) {
                logger.info("===================SendAggsChooseDataSourceAspect read=========================");
                String dataSwitch = null;
                if(jyDuccConfigManager.getJySendAggsDataReadSwitchInfo()){
                    dataSwitch = Constants.sendPredictaggstopic2DataSource.get(snedAggsBak);
                }else {
                    dataSwitch = Constants.sendPredictaggstopic2DataSource.get(snedAggsMain);
                }
                if (ObjectHelper.isNotNull(dataSwitch)) {
                    logger.info("===================SendAggsChooseDataSourceAspect read {} =========================",dataSwitch);
                    DynamicDataSourceType dataSourceType = DynamicDataSourceHolders.getDataSources(dataSwitch);
                    if (ObjectHelper.isNotNull(dataSourceType)) {
                        logger.info("===================SendAggsChooseDataSourceAspect read dataSourceType {} =========================",dataSourceType);
                        DynamicDataSourceHolders.putDataSource(dataSourceType);
                    }
                }
            }else if (ReadWriteTypeEnum.WRITE.getType().equals(readWriteType)) {
                Object[] args = point.getArgs();
                if (ObjectHelper.isNotNull(args) && args.length > 0) {
                    if((args[0].getClass().getName()).equals("com.jd.jmq.common.message.Message")){
                        Message message = (Message) args[0];
                        if (ObjectHelper.isNotNull(message.getTopic())) {
                            String dateSourceKey = message.getTopic();
                            String dateSourceName = Constants.sendPredictaggstopic2DataSource.get(dateSourceKey);
                            DynamicDataSourceType dataSourceType = DynamicDataSourceHolders.getDataSources(dateSourceName);
                            DynamicDataSourceHolders.putDataSource(dataSourceType);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("SendAggsChooseDataSourceAspect Choose DataSource error", e);
        }
    }

    @After("pointCut()")
    public void after(JoinPoint point) {
        if (ReadWriteTypeEnum.READ.getType().equals(readWriteType)){
            DynamicDataSourceHolders.clearDataSource();
        }
    }

}
