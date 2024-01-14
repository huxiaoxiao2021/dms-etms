package com.jd.bluedragon.distribution.jy.service.picking;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodScanCacheDto;
import com.jd.jim.cli.Cluster;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 空铁提货发货缓存服务
 */
@Service
public class JyAviationRailwayPickingGoodsCacheService {
    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwayPickingGoodsCacheService.class);

    private static final String DEFAULT_VALUE_1 = "1";

    private static final String CACHE_PICKING_SCAN_PRE = "cache:picking:scan:%s:%s:%s";
    private static final Integer CACHE_PICKING_SCAN_TIMEOUT_DAYS = 3;

    private static final String LOCK_PICKING_SCAN_PRE = "lock:picking:scan:%s:%s";
    private static final Integer LOCK_PICKING_SCAN_TIMEOUT_SECONDS = 120;

    private static final String LOCK_PICKING_TASK_BIZ_SITE_PRE = "lock:picking:task:bizId:siteId:%s:%s";
    private static final Integer LOCK_PICKING_TASK_BIZ_SITE_PRE_TIMEOUT_MINUTES = 5;

    private static final String LOCK_PICKING_DETAIL_RECORD_INIT_PRE = "lock:picking:detail:init:%s:%s:%s";
    private static final Integer LOCK_PICKING_DETAIL_RECORD_INIT_PRE_TIMEOUT_SECONDS = 120;


    @Autowired
    private JimDbLock jimDbLock;
    @Autowired
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


    /**
     * 提货扫描缓存保存
     */
    public void saveCachePickingGoodScan(String barCode, String bizId, Integer siteId) {
        String cacheKey = this.getCachePickingGoodScanKey(barCode, bizId, siteId);
        redisClientOfJy.setEx(cacheKey, DEFAULT_VALUE_1, CACHE_PICKING_SCAN_TIMEOUT_DAYS, TimeUnit.DAYS);
    }
    /**
     * 是否提货扫描
     * true: 是  false: 否
     */
    public boolean getCachePickingGoodScanValue(String barCode, String bizId, Integer siteId) {
        String cacheKey = this.getCachePickingGoodScanKey(barCode, bizId, siteId);
        String value = redisClientOfJy.get(cacheKey);
        return StringUtils.isNotBlank(value);
    }
    //提货扫描缓存key生成
    private String getCachePickingGoodScanKey(String barCode, String bizId, Integer siteId) {
        return String.format(JyAviationRailwayPickingGoodsCacheService.CACHE_PICKING_SCAN_PRE, barCode, bizId, siteId);
    }

    /**
     * 提货扫描并发锁
     * @param barCode
     * @param siteId
     * @return
     */
    public boolean lockPickingGoodScan(String barCode, Integer siteId) {
        String lockKey = this.getLockKeyPickingGoodScan(barCode, siteId);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, LOCK_PICKING_SCAN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    public void unlockPickingGoodScan(String barCode, Integer siteId) {
        String lockKey = this.getLockKeyPickingGoodScan(barCode, siteId);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    //提货扫描加锁
    private String getLockKeyPickingGoodScan(String barCode, Integer siteId) {
        return String.format(LOCK_PICKING_SCAN_PRE, barCode, siteId);
    }




    /**
     * 提货场地+任务锁
     * @param bizId
     * @param siteId
     * @return
     */
    public boolean lockPickingGoodBizIdSiteId(String bizId, Long siteId) {
        String lockKey = this.getLockKeyPickingGoodBizIdSiteId(bizId, siteId);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, LOCK_PICKING_TASK_BIZ_SITE_PRE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }
    public void unlockPickingGoodBizIdSiteId(String bizId, Long siteId) {
        String lockKey = this.getLockKeyPickingGoodBizIdSiteId(bizId, siteId);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    private String getLockKeyPickingGoodBizIdSiteId(String bizId, Long siteId) {
        return String.format(LOCK_PICKING_TASK_BIZ_SITE_PRE, bizId, siteId);
    }

    /**
     * 提货明细初始化锁 场地+bizId+barCode
     * @param siteId
     * @param bizId
     * @param barCode
     * @return
     */
    public boolean lockPickingGoodDetailRecordInit(Long siteId, String bizId, String barCode) {
        String lockKey = this.getLockKeyPickingGoodDetailRecordInit(siteId, bizId, barCode);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, LOCK_PICKING_DETAIL_RECORD_INIT_PRE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    public void unlockPickingGoodDetailRecordInit(Long siteId, String bizId, String barCode) {
        String lockKey = this.getLockKeyPickingGoodDetailRecordInit(siteId, bizId, barCode);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    private String getLockKeyPickingGoodDetailRecordInit(Long siteId, String bizId, String barCode) {
        return String.format(LOCK_PICKING_DETAIL_RECORD_INIT_PRE, siteId, bizId, barCode);
    }


}
