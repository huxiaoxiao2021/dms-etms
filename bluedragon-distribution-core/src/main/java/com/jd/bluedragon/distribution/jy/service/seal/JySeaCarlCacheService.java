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
    public static final int LOCK_SEND_TASK_OUT_BOOKING_WEIGHT_TIMEOUT_SECONDS = 7;

    public static final String OUT_WEIGHT_TASK_PREFIX = "aviation.out.weight:%s";

    @Autowired
    private JimDbLock jimDbLock;

    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisDao;


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

    /**
     * 场景：保存任务超载提示
     * @param bizId  任务bizId
     */
    public boolean outBookingWeightTaskCheck(String bizId) {
        try {
            String key = String.format(OUT_WEIGHT_TASK_PREFIX,bizId);
            if (redisDao.exists(key)) {
                return false;
            }
            redisDao.sAdd(key, DEFAULT_VALUE_1);
            redisDao.expire(key, LOCK_SEND_TASK_OUT_BOOKING_WEIGHT_TIMEOUT_SECONDS, TimeUnit.DAYS);
            return true;
        }catch (Exception e) {
            log.error("lockOutBookingWeightTask:超载任务加锁失败 :bizId={},errMsg={}", bizId, e.getMessage(), e);
            throw new JyBizException("超载任务加锁失败");
        }
    }





}
