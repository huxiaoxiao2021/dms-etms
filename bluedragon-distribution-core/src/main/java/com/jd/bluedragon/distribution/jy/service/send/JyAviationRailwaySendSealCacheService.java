package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.jim.cli.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * 空铁发货场景缓存服务
 */

@Service
public class JyAviationRailwaySendSealCacheService {

    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwaySendSealCacheService.class);

    public static final String DEFAULT_VALUE_1 = "1";




    /**
     * K:bizId V：erp
     */
    public static final String LOCK_AVIATION_RAILWAY_TASK_SEAL_BIZID = "lock:aviationRailwayTaskSeal:bizId:%s";
    public static final int LOCK_AVIATION_RAILWAY_TASK_SEAL_BIZID_TIMEOUT_SECONDS = 30;


    @Autowired
    private JimDbLock jimDbLock;
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;


    /**
     * 并发锁
     * 场景：摆渡任务绑定传摆任务
     * @param bizId  任务bizId
     */
    public boolean lockTaskSealCarBizId(String bizId) {
        try {
            String lockKey = this.getLockKeyTaskSealCarBizId(bizId);
            return jimDbLock.lock(lockKey,
                    DEFAULT_VALUE_1,
                    JyAviationRailwaySendSealCacheService.LOCK_AVIATION_RAILWAY_TASK_SEAL_BIZID_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS);
        }catch (Exception e) {
            log.error("lockTaskBindBizId:任务绑定加锁失败:bizId={},errMsg={}", bizId, e.getMessage(), e);
            throw new JyBizException("任务绑定加锁失败");
        }
    }
    public void unlockTaskSealCarBizId(String bizId) {
        String lockKey = this.getLockKeyTaskSealCarBizId(bizId);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    private String getLockKeyTaskSealCarBizId(String bizId) {
        return String.format(JyAviationRailwaySendSealCacheService.LOCK_AVIATION_RAILWAY_TASK_SEAL_BIZID, bizId);
    }




}
