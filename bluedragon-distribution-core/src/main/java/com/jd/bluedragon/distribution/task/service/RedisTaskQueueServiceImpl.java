package com.jd.bluedragon.distribution.task.service;

import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wangtingwei on 2015/10/28.
 */
@Service("RedisTaskQueueService")
public class RedisTaskQueueServiceImpl implements RedisTaskQueueService {

    private static final Logger log = LoggerFactory.getLogger(RedisTaskQueueServiceImpl.class);

    @Autowired
    RedisManager redisManager;

    @Cache(key = "RedisTaskQueueServiceImpl.getQueueLength@args0", memoryEnable = true, memoryExpiredTime = 30 * 1000,redisEnable = false)
    @Override
    public long getQueueLength(String cacheName,String queueName) {
        try {
            return redisManager.llen(queueName);
        }catch (Exception ex){
            if(log.isErrorEnabled()){
                log.error("获取队列长度：{}", queueName,ex);
            }
        }
        return 0;
    }
}
