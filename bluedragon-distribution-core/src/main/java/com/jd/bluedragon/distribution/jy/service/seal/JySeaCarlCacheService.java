package com.jd.bluedragon.distribution.jy.service.seal;

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
public class JySeaCarlCacheService {

    private static final Logger log = LoggerFactory.getLogger(JySeaCarlCacheService.class);

    public static final String DEFAULT_VALUE_1 = "1";




    /**
     * K:bizId V：erp
     */
    public static final String LOCK_SEND_TASK_SEAL = "lock:send:seal:%s:%s";
    public static final int LOCK_SEND_TASK_SEAL_TIMEOUT_SECONDS = 60;


    @Autowired
    private JimDbLock jimDbLock;
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;


    /**
     * 并发锁
     * 场景：摆渡任务封车、摆渡任务绑定、解绑航空任务
     * @param bizId  任务bizId
     */
    public boolean lockSendTaskSeal(String bizId, Integer taskType) {
        try {
            String lockKey = this.getLockKeyShuttleTaskSealCarBizId(bizId, taskType);
            return jimDbLock.lock(lockKey,
                    DEFAULT_VALUE_1,
                    JySeaCarlCacheService.LOCK_SEND_TASK_SEAL_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS);
        }catch (Exception e) {
            log.error("lockTaskBindBizId:任务绑定加锁失败:bizId={},errMsg={}", bizId, e.getMessage(), e);
            throw new JyBizException("任务绑定加锁失败");
        }
    }
    public void unlockSendTaskSeal(String bizId, Integer taskType) {
        String lockKey = this.getLockKeyShuttleTaskSealCarBizId(bizId, taskType);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    private String getLockKeyShuttleTaskSealCarBizId(String bizId, Integer taskType) {
        return String.format(JySeaCarlCacheService.LOCK_SEND_TASK_SEAL, bizId, taskType);
    }







}
