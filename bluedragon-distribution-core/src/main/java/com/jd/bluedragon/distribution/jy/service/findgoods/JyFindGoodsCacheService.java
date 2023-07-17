package com.jd.bluedragon.distribution.jy.service.findgoods;

import com.jd.bluedragon.common.lock.redis.JimDbLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author zhengchengfa
 * @Date 2023/7/17 15:44
 * @Description
 */
@Service
public class JyFindGoodsCacheService {



    public static final String DEFAULT_VALUE = "1";
    public static final String PHOTOGRAPH_LOCK_PRE = "lock:find:goods:bizId:%s";
    public static final Integer PHOTOGRAPH_LOCK_TIMEOUT_SECONDS = 30;



    @Autowired
    private JimDbLock jimDbLock;


    /**
     * 找货任务Biz维度锁
     * @param bizId
     * @return
     */
    public boolean lockTaskByBizId(String bizId) {
        String lockKey = this.getLockKeyTaskByBizId(bizId);
        return jimDbLock.lock(lockKey, JyFindGoodsCacheService.DEFAULT_VALUE, PHOTOGRAPH_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    public void unlockTaskByBizId(String bizId) {
        jimDbLock.releaseLock(bizId, JyFindGoodsCacheService.DEFAULT_VALUE);
    }
    public String getLockKeyTaskByBizId(String bizId) {
        return String.format(JyFindGoodsCacheService.PHOTOGRAPH_LOCK_PRE, bizId);
    }

    /**
     *
     */



}
