package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.jim.cli.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 空铁提货发货缓存服务
 */
@Service
public class JyAviationRailwayPickingGoodsCacheService {
    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwayPickingGoodsCacheService.class);

    public static final String DEFAULT_VALUE_1 = "1";


    @Autowired
    private JimDbLock jimDbLock;
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;


    public boolean lockXXX(String keyword) {
        String lockKey = this.getLockKey(keyword);
        return jimDbLock.lock(lockKey,
                DEFAULT_VALUE_1,
                10,
                TimeUnit.SECONDS);
    }

    public void unlockXXX(String keyword) {
        String lockKey = this.getLockKey(keyword);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }

    public String getLockKey(String keyword){
        return keyword;
    }


}
