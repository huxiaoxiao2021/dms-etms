package com.jd.bluedragon.distribution.ver.util;

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
public class RedisStringUtil extends RedisCommonAbstractUtil<String> {
    private static Log logger = LogFactory.getLog(RedisStringUtil.class);

    /**
     * 缓存数据
     * @param key
     * @param data
     * @param expireTime
     * @return
     */
    @Override
    public boolean cacheDataEx(String key, String data, long expireTime){
        try{
            redisClient.setEx(key,data,expireTime, TimeUnit.MILLISECONDS);
            return true;
        }catch (Exception e){
            logger.error("缓存数据出错,key = "+key+" 错误信息为："+e.getMessage());
            return false;
        }
    }

    /**
     * 缓存数据 如果数据已经存在 则缓存不成功 返回false 如果不存在返回成功
     * @param key
     * @param data
     * @return
     */
    public boolean cacheDataNX(String key, String data){
        try{
            return redisClient.setNX(key,data);
        }catch (Exception e){
            logger.error("缓存数据出错,key = "+key+" 错误信息为："+e.getMessage());
            return false;
        }
    }

    /**
     * 设置过期时间
     * @param key
     * @return
     */
    public boolean expire(String key, long timeout, TimeUnit unit){
        try{
            return redisClient.expire(key,timeout,unit);
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
    public String getData(String key){
        try {
            return redisClient.get(key);
        } catch (Exception e) {
            logger.error("从Redis中获取信息出错,key = "+key+" 错误信息为:"+e.getMessage());
            return null;
        }
    }

    public boolean existsKey(String key){
        try {
            return redisClient.exists(key);
        } catch (Exception e) {
            logger.error("从Redis中获取信息出错,key = "+key+" 错误信息为:"+e.getMessage());
            return false;
        }
    }
}
