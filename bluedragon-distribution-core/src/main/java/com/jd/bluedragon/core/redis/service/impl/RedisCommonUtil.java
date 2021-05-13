package com.jd.bluedragon.core.redis.service.impl;

import com.jd.bluedragon.core.redis.service.RedisCommonAbstractUtil;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import static com.google.common.base.Charsets.UTF_8;

/**
 * Created by zhanglei51 on 2016/11/14.
 *
 * Cachem Redis数据缓存工具类
 *
 */
public class RedisCommonUtil extends RedisCommonAbstractUtil<Integer> {
    private static Logger log = LoggerFactory.getLogger(RedisCommonUtil.class);

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
            log.error("缓存数据出错,key = {}",key,e);
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
            log.error("从Redis中获取信息出错,key = {}",key,e);
            return null;
        }
    }

    /**
     * 删除数据
     * @param key
     * @return
     */
    public void del(String key){
        try {
            redisClient.del(key);
        } catch (Exception e) {
            log.error("从Redis中删除数据出错,key = {}",key,e);
        }
    }

    /**
     * 缓存数据 没有过期时间
     * @param key key 存入redis时会拼接
     * @param data
     * @return
     */
    public boolean cacheIntegerNx(String key,Integer data){
        try {
            redisClient.setNX(key, data.toString());
            return true;
        }catch (Exception e){
            log.error("缓存数据出错,key = {} ",key,e);
            return false;
        }
    };

    /**
     * 缓存数据的值-1
     * @param key
     * @return
     */
    public Long decrVal(String key){
        try{
            return redisClient.decr(key);
        }catch (Exception e){
            log.error("缓存数据减一出错,key = {}",key,e);
            return -1L;
        }
    }
}
