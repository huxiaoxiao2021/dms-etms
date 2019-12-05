package com.jd.bluedragon.core.redis.service;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
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
public abstract class RedisCommonAbstractUtil<T> {
    protected static Logger log = LoggerFactory.getLogger(RedisCommonAbstractUtil.class);

    protected Cluster redisClient;

    /**
     * 缓存数据 没有过期时间 这种可能不会用到
     * @param key key 存入redis时会拼接
     * @param data
     * @return
     */
    public boolean cacheData(String key,T data){
        try {
            redisClient.set((key).getBytes(), JsonHelper.toJsonUseGson(data).getBytes(UTF_8));
            return true;
        }catch (Exception e){
            log.error("缓存数据出错,key = {} ",key,e);
            return false;
        }
    };

    /**
     * 缓存数据
     * @param key
     * @param data
     * @param expireTime
     * @return
     */
    public boolean cacheDataEx(String key, T data, long expireTime){
        try{
            redisClient.setEx(key.getBytes(), JsonHelper.toJsonUseGson(data).getBytes(UTF_8),expireTime, TimeUnit.MILLISECONDS);
            return true;
        }catch (Exception e){
            log.error("缓存数据出错,key ={}",key,e);
            return false;
        }
    }

    /**
     * 缓存数据
     * @param key
     * @param data
     * @param expireTime
     * @return
     */
    public boolean cacheStringEx(String key, String data, long expireTime){
        try{
            redisClient.setEx(key.getBytes(), data.getBytes(UTF_8), expireTime, TimeUnit.MILLISECONDS);
            return true;
        }catch (Exception e){
            log.error("缓存数据出错,key = {}",key,e);
            return false;
        }
    }

    /**
     * 缓存数据的值-1
     * @param key
     * @return
     */
    public boolean decr(String key){
        try{
            redisClient.decr(key.getBytes());
            return true;
        }catch (Exception e){
            log.error("缓存数据减一出错,key = {}",key,e);
            return false;
        }
    }

    /**
     * 缓存数据的值+1
     * @param key
     * @return
     */
    public boolean incr(String key){
        try{
            redisClient.incr(key.getBytes());
            return true;
        }catch (Exception e){
            log.error("缓存数据加一出错,key = {}",key,e);
            return false;
        }
    }
    /**
     *
     * @param key
     * @return
     */
    public abstract T getData(String key);

    public Cluster getRedisClient() {
        return redisClient;
    }

    public void setRedisClient(Cluster redisClient) {
        this.redisClient = redisClient;
    }
}
