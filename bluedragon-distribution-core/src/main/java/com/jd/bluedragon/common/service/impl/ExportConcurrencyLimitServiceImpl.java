package com.jd.bluedragon.common.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.jim.cli.Cluster;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2021/4/1 16:19
 */
@Service("exportConcurrencyLimit")
public class ExportConcurrencyLimitServiceImpl implements ExportConcurrencyLimitService {

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
     * @param redisKey
     * @return
     */
    @Override
    @JProfiler(jKey = "com.jd.bluedragon.distribution.web.ExportConcurrencyLimitServiceImpl.checkConcurrencyLimit", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public boolean checkConcurrencyLimit(String redisKey) {
            Integer concurrencyLimit = Constants.CONCURRENCY_EXPORT_LIMIT;
            if(uccPropertyConfiguration.getExportConcurrencyLimitNum()!=null){
                concurrencyLimit = uccPropertyConfiguration.getExportConcurrencyLimitNum();
            }

            Integer timeOut = Constants.EXPORT_REDIS_KEY_TIME_OUT;
            if(uccPropertyConfiguration.getExportRedisKeyTimeOut()!=null){
                timeOut = uccPropertyConfiguration.getExportRedisKeyTimeOut();
            }

            long count  = redisClientCache.incrBy(redisKey,1);

            //小于调用阀值时，设置失效时间
            if( count <= concurrencyLimit){
                redisClientCache.expire(redisKey, timeOut, TimeUnit.SECONDS);
                return true;
            }
            return false;
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

}
    
