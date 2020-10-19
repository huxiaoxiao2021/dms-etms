package com.jd.bluedragon.utils;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.jim.cli.Cluster;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @program: bluedragon-distribution
 * @description: 查询间隔工具类
 * @author: liuduo8
 * @create: 2020-10-12 15:56
 **/
@Component
public class QueryGapTimeUtil {

    private static Logger logger = LoggerFactory.getLogger(QueryGapTimeUtil.class);

    @Resource
    private UccPropertyConfiguration uccPropertyConfiguration;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    private String REDIS_KEY = "gap:%s:%s";

    public static String INSPECTION_RESOURCE_GET = "1";

    public static String SEND_PRINT_RESOURCE_SOP_PRINT_QUERY = "2";

    public static String INSPECTION_RESOURCE_RETURN_WAREHOUSE = "3";
    /**
     * 检查是否允许通过
     * @param jsonStr
     * @param methodName
     * @return
     */
    @JProfiler(jKey = "DMSWEB.QueryGapTimeUtil.checkPass",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public boolean checkPass(String jsonStr,String methodName){

        int gapTime = uccPropertyConfiguration.getClientPrintQueryGapTime();

        if(gapTime <= 0){
            return true;
        }
        try{
            String key = String.format(REDIS_KEY,methodName,Md5Helper.encode(jsonStr));
            if(!jimdbCacheService.setNx(key,1,gapTime, TimeUnit.SECONDS)){
                logger.warn("查询间隔时间未到，{}  {}",methodName, methodName);
                return false;
            }
        }catch (Exception e){
            logger.error("查询间隔时间处理异常，{}  {}  {}",methodName, methodName,e.getMessage(),e);
        }
        return true;
    }




}
