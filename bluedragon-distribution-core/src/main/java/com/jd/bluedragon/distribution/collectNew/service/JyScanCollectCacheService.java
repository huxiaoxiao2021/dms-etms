package com.jd.bluedragon.distribution.collectNew.service;

import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.jim.cli.Cluster;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    public static final String DEFAULT_VALUE = Strings.EMPTY;

    public static final String LOCK_UP_INSERT_JY_SCAN_COLLECTION_RECORD = "lock:upInsert:collectionRecord:%s:%s";
    public static final int LOCK_UP_INSERT_JY_SCAN_COLLECTION_RECORD_TIMEOUT_SECONDS = 120;

    public static final String LOCK_INSERT_JY_SCAN_COLLECTION_RECORD_DETAIL = "lock:insert:collectionRecordDetail:%s:%s";
    public static final int LOCK_INSERT_JY_SCAN_COLLECTION_RECORD_DETAIL_TIMEOUT_SECONDS = 120;

    public static final String CACHE_JY_SCAN_PACKAGE_COLLECT = "cache:jyScan:packageCollect:%s:%s";
    public static final int CACHE_JY_SCAN_PACKAGE_COLLECT_TIMEOUT_MINUTES = 30;

    @Autowired
    private JimDbLock jimDbLock;
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;


    /**
     * 集齐主表插入数据加锁
     * @param collectionCode
     * @param aggCode
     * @return
     */
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

    /**
     * 集齐主表删除
     * @param collectionCode
     * @param aggCode
     */
    public void delLockUpInsertCollectRecord(String collectionCode, String aggCode) {
        if(StringUtils.isBlank(collectionCode) || StringUtils.isBlank(aggCode)) {
            log.error("delLockUpInsertCollectRecord加锁参数缺失:collectionCode={}, aggCode={}", collectionCode, aggCode);
            throw new JyBizException("delLockUpInsertCollectRecord加锁参数缺失");
        }
        String lockKey = getLockUpInsertCollectRecordKey(collectionCode, aggCode);
        jimDbLock.releaseLock(lockKey, JyScanCollectCacheService.DEFAULT_VALUE);
    }

    /**
     * 集齐主表添加锁 获取key
     * @param collectionCode
     * @param aggCode
     * @return
     */
    private String getLockUpInsertCollectRecordKey(String collectionCode, String aggCode) {
        return String.format(JyScanCollectCacheService.LOCK_UP_INSERT_JY_SCAN_COLLECTION_RECORD, collectionCode, aggCode);
    }

    /**
     * 集齐明细表插入锁
     * @param collectionCode
     * @param scanCode
     * @return
     */
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

    /**
     * 集齐明细表删除锁
     * @param collectionCode
     * @param scanCode
     */
    public void delLockInsertCollectRecordDetail(String collectionCode, String scanCode) {
        if(StringUtils.isBlank(collectionCode) || StringUtils.isBlank(scanCode)) {
            log.error("lockInsertCollectRecordDetail加锁参数缺失:collectionCode={}, 包裹号={}", collectionCode, scanCode);
            throw new JyBizException("lockInsertCollectRecordDetail加锁参数缺失");
        }
        String lockKey = getLockInsertCollectRecordDetailKey(collectionCode, scanCode);
        jimDbLock.releaseLock(lockKey, JyScanCollectCacheService.DEFAULT_VALUE);
    }

    /**
     * 集齐明细表获取key
     * @param collectionCode
     * @param scanCode
     * @return
     */
    private String getLockInsertCollectRecordDetailKey(String collectionCode, String scanCode) {
        return String.format(JyScanCollectCacheService.LOCK_INSERT_JY_SCAN_COLLECTION_RECORD_DETAIL, collectionCode, scanCode);
    }

    /**
     * 集齐包裹防重缓存
     * @param collectionCode
     * @param packageCode
     */
    public void saveCacheScanPackageCollectDeal(String collectionCode, String packageCode) {
        if(StringUtils.isBlank(collectionCode) || StringUtils.isBlank(packageCode)) {
            log.error("saveCacheScanPackageCollectDeal保存防重缓存参数缺失:collectionCode={}, 包裹号={}", collectionCode, packageCode);
            throw new JyBizException("saveCacheScanPackageCollectDeal保存防重缓存参数缺失");
        }
        try {
            String cacheKey = this.getCacheKeyScanPackageCollectDeal(collectionCode, packageCode);
            redisClientOfJy.setEx(cacheKey, "1",
                    JyScanCollectCacheService.CACHE_JY_SCAN_PACKAGE_COLLECT_TIMEOUT_MINUTES,
                    TimeUnit.MINUTES);
        }catch (Exception e) {
            log.error("saveCacheScanPackageCollectDeal，collectionCode={}，packageCode={}", collectionCode, packageCode, e);
        }
    }

    /**
     * 校验集齐包裹防重缓存是否存在  true: 存在，已扫描  false: 不存在，未扫描
     * @param collectionCode
     * @param packageCode
     * @return
     */
    public boolean existCacheScanPackageCollectDeal(String collectionCode, String packageCode) {
        if(StringUtils.isBlank(collectionCode) || StringUtils.isBlank(packageCode)) {
            log.error("existCacheScanPackageCollectDeal校验防重缓存参数缺失:collectionCode={}, 包裹号={}", collectionCode, packageCode);
            throw new JyBizException("existCacheScanPackageCollectDeal校验防重缓存参数缺失");
        }
        try {
            String cacheKey = getCacheKeyScanPackageCollectDeal(collectionCode, packageCode);
            if (StringUtils.isBlank(redisClientOfJy.get(cacheKey))) {
                return false;
            }
            return true;
        }catch (Exception e) {
            log.error("existCacheScanPackageCollectDeal，collectionCode={}，packageCode={}", collectionCode, packageCode, e);
            return false;
        }
    }

    /**
     * 获取集齐包裹防重缓存key
     * @param collectionCode
     * @param packageCode
     * @return
     */
    public String getCacheKeyScanPackageCollectDeal(String collectionCode, String packageCode) {
        return String.format(CACHE_JY_SCAN_PACKAGE_COLLECT, collectionCode, packageCode);
    }

}
