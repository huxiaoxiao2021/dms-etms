package com.jd.bluedragon.core.redis.service.impl;

import com.jd.bluedragon.core.redis.service.RedisCommonAbstractUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by zhanglei51 on 2016/11/14.
 *
 * Cachem Redis数据缓存工具类
 *
 */
public class RedisCommonUtil extends RedisCommonAbstractUtil<Integer> {
    private static Log logger = LogFactory.getLog(RedisCommonUtil.class);

    /**
     * 缓存数据
     * @param key
     * @param data
     * @param expireTime
     * @return
     */
    @Override
    public boolean cacheDataEx(String key, Integer data, long expireTime){
        try{
            redisClient.setEx(key,data.toString(),expireTime,TimeUnit.MILLISECONDS);
            return true;
        }catch (Exception e){
            logger.error("缓存数据出错,key = "+key+" 错误信息为："+e.getMessage());
            return false;
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public Integer getData(String key){
        try {
            String value = redisClient.get(key);
            if(value == null){
                return 0;
            }
            return Integer.parseInt(value);
        } catch (Exception e) {
            logger.error("从Redis中获取信息出错,key = "+key+" 错误信息为:"+e.getMessage());
            return null;
        }
    }
}
