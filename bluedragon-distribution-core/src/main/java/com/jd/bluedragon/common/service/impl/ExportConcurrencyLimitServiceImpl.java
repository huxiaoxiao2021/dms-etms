package com.jd.bluedragon.common.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/1 16:19
 */
@Service("exportConcurrencyLimit")
public class ExportConcurrencyLimitServiceImpl implements ExportConcurrencyLimitService {

    private static final Logger log = LoggerFactory.getLogger(ExportConcurrencyLimitServiceImpl.class);

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    /**
     * 导出最大限制
     */
    private static final int EXPORT_MAX_SIZE = 10000;

    /**
     * 判断是否需要限制并发导出操作
     * @param key
     * @return
     */
    @Override
    @JProfiler(jKey = "com.jd.bluedragon.distribution.web.ExportConcurrencyLimitServiceImpl.checkConcurrencyLimit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public boolean checkConcurrencyLimit(String key) {
            try {
                // 一天一个key
                String redisKey = getRedisDateKey(key);
                Integer concurrencyLimit = Constants.CONCURRENCY_EXPORT_LIMIT;
                if(uccPropertyConfiguration.getExportConcurrencyLimitNum()!=null){
                    concurrencyLimit = uccPropertyConfiguration.getExportConcurrencyLimitNum();
                }

                //1.用于系统降级,限制导出
                if(concurrencyLimit  <= 0){
                    return false;
                }

                Integer timeOut = Constants.EXPORT_REDIS_KEY_TIME_OUT;

                //2.当并发大时,限制导出
                String value = redisClientCache.get(redisKey);
                if(StringUtils.isEmpty(value)){
                    redisClientCache.setEx(redisKey,"0",timeOut,TimeUnit.DAYS);
                    return true;
                }

                if(Long.valueOf(value) <= concurrencyLimit){
                    return true;
                }
            }catch (Exception e){
                log.error("并发限制检查异常",e);
                return  false;
            }
            return false;
    }

    /**
     *  进行中导出并发数量+1
     * @param key
     */
    @Override
    @JProfiler(jKey = "com.jd.bluedragon.common.service.impl.ExportConcurrencyLimitServiceImpl.incrKey", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public void incrKey(String key){
        try{
            String redisKey = getRedisDateKey(key);
            redisClientCache.incrBy(redisKey,1);
        }catch (Exception e){
            log.error("增加导出并发限制+1 异常",e);
        }
    }

    /**
     * 进行中导出并发数量 -1
     * @param key
     */
    @Override
    @JProfiler(jKey = "com.jd.bluedragon.common.service.impl.ExportConcurrencyLimitServiceImpl.decrKey", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public void decrKey(String key){
        try{
            String redisKey = getRedisDateKey(key);
            redisClientCache.decrBy(redisKey,1);
        }catch (Exception e){
            log.error("减少导出并发限制-1 异常",e);
        }
    }

    private String getRedisDateKey(String key){
       return key + DateHelper.formatDate(new Date(),DateHelper.DATE_FORMATE_yyyyMMdd);
    }

    /**
     * 获取导出数量限制
     * @return
     */
    @Override
    @JProfiler(jKey = "com.jd.bluedragon.distribution.web.ExportConcurrencyLimitServiceImpl.uccSpotCheckMaxSize", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public Integer uccSpotCheckMaxSize(){
        // 设置总导出数据
        Integer uccSpotCheckMaxSize = uccPropertyConfiguration.getExportSpotCheckMaxSize();
        if(uccPropertyConfiguration.getExportSpotCheckMaxSize() == null){
            uccSpotCheckMaxSize = EXPORT_MAX_SIZE;
        }
        return uccSpotCheckMaxSize;
    }

    /**
     * 获取导出单次查询数据库条数限制
     * @return
     */
    @Override
    @JProfiler(jKey = "com.jd.bluedragon.distribution.web.ExportConcurrencyLimitServiceImpl.uccSpotCheckMaxSize", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public Integer getOneQuerySizeLimit(){
        return uccPropertyConfiguration.getOneQuerySize();
    }


    @Override
    @JProfiler(jKey = "com.jd.bluedragon.common.service.impl.ExportConcurrencyLimitServiceImpl.addBusinessLog", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public void addBusinessLog(String requestParam,String methodName,long time,Integer count){
        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_EXPORT_OPERATE);
        businessLogProfiler.setBizType(Constants.BUSINESS_LOG_EXPORT_OPERATE);
        businessLogProfiler.setOperateType(Constants.OPERATE_TYPE_REPORT_OPERATE);
        businessLogProfiler.setOperateRequest(requestParam);
        businessLogProfiler.setOperateResponse("runTime:"+time+"ms");
        businessLogProfiler.setRemark("导出数量:"+count);
        businessLogProfiler.setTimeStamp(System.currentTimeMillis());
        businessLogProfiler.setMethodName(methodName);
        BusinessLogWriter.writeLog(businessLogProfiler);
    }
}
    
