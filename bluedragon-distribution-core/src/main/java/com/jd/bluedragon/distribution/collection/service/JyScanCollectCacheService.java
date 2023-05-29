package com.jd.bluedragon.distribution.collection.service;

import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author zhengchengfa
 * @Date 2023/5/29 15:11
 * @Description
 */
@Service
@Slf4j
public class JyScanCollectCacheService {

    private static final String DEFAULT_VALUE = Strings.EMPTY;

    private static final String LOCK_UP_INSERT_JY_SCAN_COLLECTION_RECORD = "lock:upInsert:collectionRecord:%s:%s";
    private static final int LOCK_UP_INSERT_JY_SCAN_COLLECTION_RECORD_TIMEOUT_SECONDS = 120;

    private static final String LOCK_INSERT_JY_SCAN_COLLECTION_RECORD_DETAIL = "lock:insert:collectionRecordDetail:%s:%s";
    private static final int LOCK_INSERT_JY_SCAN_COLLECTION_RECORD_DETAIL_TIMEOUT_SECONDS = 120;

    @Autowired
    private JimDbLock jimDbLock;

    public boolean lockUpInsertCollectRecord(String collectionCode, String aggCode) {
        if(StringUtils.isBlank(collectionCode) || StringUtils.isBlank(aggCode)) {
            log.error("lockUpInsertCollectRecord加锁参数缺失:collectionCode={}, aggCode={}", collectionCode, aggCode);
            throw new JyBizException("lockUpInsertCollectRecord加锁参数缺失");
        }
        String lockKey = getLockUpInsertCollectRecordKey(collectionCode, aggCode);
        return jimDbLock.lock(lockKey,
                JyScanCollectCacheService.DEFAULT_VALUE,
                JyScanCollectCacheService.LOCK_UP_INSERT_JY_SCAN_COLLECTION_RECORD_TIMEOUT_SECONDS,
                TimeUnit.SECONDS);
    }

    public void delLockUpInsertCollectRecord(String collectionCode, String aggCode) {
        if(StringUtils.isBlank(collectionCode) || StringUtils.isBlank(aggCode)) {
            log.error("delLockUpInsertCollectRecord加锁参数缺失:collectionCode={}, aggCode={}", collectionCode, aggCode);
            throw new JyBizException("delLockUpInsertCollectRecord加锁参数缺失");
        }
        String lockKey = getLockUpInsertCollectRecordKey(collectionCode, aggCode);
        jimDbLock.releaseLock(lockKey, JyScanCollectCacheService.DEFAULT_VALUE);
    }

    private String getLockUpInsertCollectRecordKey(String collectionCode, String aggCode) {
        return String.format(JyScanCollectCacheService.LOCK_UP_INSERT_JY_SCAN_COLLECTION_RECORD, collectionCode, aggCode);
    }

    public boolean lockInsertCollectRecordDetail(String collectionCode, String scanCode) {
        if(StringUtils.isBlank(collectionCode) || StringUtils.isBlank(scanCode)) {
            log.error("lockInsertCollectRecordDetail加锁参数缺失:collectionCode={}, 包裹号={}", collectionCode, scanCode);
            throw new JyBizException("lockInsertCollectRecordDetail加锁参数缺失");
        }
        String lockKey = getLockInsertCollectRecordDetailKey(collectionCode, scanCode);
        return jimDbLock.lock(lockKey,
                JyScanCollectCacheService.DEFAULT_VALUE,
                JyScanCollectCacheService.LOCK_INSERT_JY_SCAN_COLLECTION_RECORD_DETAIL_TIMEOUT_SECONDS,
                TimeUnit.SECONDS);
    }

    public void delLockInsertCollectRecordDetail(String collectionCode, String scanCode) {
        if(StringUtils.isBlank(collectionCode) || StringUtils.isBlank(scanCode)) {
            log.error("lockInsertCollectRecordDetail加锁参数缺失:collectionCode={}, 包裹号={}", collectionCode, scanCode);
            throw new JyBizException("lockInsertCollectRecordDetail加锁参数缺失");
        }
        String lockKey = getLockInsertCollectRecordDetailKey(collectionCode, scanCode);
        jimDbLock.releaseLock(lockKey, JyScanCollectCacheService.DEFAULT_VALUE);
    }

    private String getLockInsertCollectRecordDetailKey(String collectionCode, String scanCode) {
        return String.format(JyScanCollectCacheService.LOCK_INSERT_JY_SCAN_COLLECTION_RECORD_DETAIL, collectionCode, scanCode);
    }
}
