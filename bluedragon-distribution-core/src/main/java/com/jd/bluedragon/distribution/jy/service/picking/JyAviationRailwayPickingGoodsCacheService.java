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
 * @Author zhengchengfa
 * @Date 2023/12/4 13:29
 * @Description 空铁提货发货缓存服务
 */

@Service
public class JyAviationRailwayPickingGoodsCacheService {
    private static final Logger log = LoggerFactory.getLogger(JyAviationRailwayPickingGoodsCacheService.class);

    private static final String DEFAULT_VALUE_1 = "1";

    private static final String CACHE_PICKING_SCAN_PRE = "cache:picking:scan:%s:%s:%s";
    private static final Integer CACHE_PICKING_SCAN_TIMEOUT_DAYS = 15;

    private static final String LOCK_PICKING_SCAN_PRE = "lock:picking:scan:%s:%s";
    private static final Integer LOCK_PICKING_SCAN_TIMEOUT_SECONDS = 120;

    private static final String LOCK_PICKING_TASK_BIZ_SITE_PRE = "lock:picking:task:bizId:siteId:%s:%s";
    private static final Integer LOCK_PICKING_TASK_BIZ_SITE_PRE_TIMEOUT_MINUTES = 5;

    private static final String LOCK_PICKING_DETAIL_RECORD_INIT_PRE = "lock:picking:detail:init:%s:%s:%s";
    private static final Integer LOCK_PICKING_DETAIL_RECORD_INIT_PRE_TIMEOUT_SECONDS = 120;
    //发货完成
    private static final String LOCK_PICKING_SEND_COMPLETE_PRE = "lock:picking:send:complete:%s:%s";
    private static final Integer LOCK_PICKING_SEND_COMPLETE_TIMEOUT_SECONDS = 120;

    //批次删除
    private static final String LOCK_PICKING_SEND_BATCH_CODE_DELETE_PRE = "lock:picking:send:batch:code:delete:%s:%s";
    private static final Integer LOCK_PICKING_SEND_BATCH_CODE_DELETE_TIMEOUT_SECONDS = 120;
    //异常上报
    private static final String CACHE_PICKING_EXCEPTION_SUBMIT_PRE = "cache:picking:exception:submit:%s";
    private static final Integer CACHE_PICKING_EXCEPTION_SUBMIT_TIMEOUT_HOURS = 24;


    @Autowired
    private JimDbLock jimDbLock;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;


    /**
     * 提货扫描防重复缓存
     */
    public void saveCachePickingGoodScan(String barCode, String bizId, Integer siteId) {
        String cacheKey = this.getCachePickingGoodScanKey(barCode, bizId, siteId);
        redisClientOfJy.setEx(cacheKey, DEFAULT_VALUE_1, CACHE_PICKING_SCAN_TIMEOUT_DAYS, TimeUnit.DAYS);
    }
    public boolean getCachePickingGoodScanValue(String barCode, String bizId, Integer siteId) {
        String cacheKey = this.getCachePickingGoodScanKey(barCode, bizId, siteId);
        String value = redisClientOfJy.get(cacheKey);
        return StringUtils.isNotBlank(value);
    }
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
     * 加锁场景 （1）提货计划消费
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
     * 提货明细包裹维度初始化锁 场地+bizId+barCode
     * 加锁场景 （1）包裹维度消费
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

    /**
     * 提货并发货批次完成加锁
     * @param siteId
     * @param nextSite
     * @return
     */
    public boolean lockPickingSendTaskComplete(Integer siteId, Integer nextSite) {
        String lockKey = this.getLockKeyPickingSendTaskComplete(siteId, nextSite);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, LOCK_PICKING_SEND_COMPLETE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    //释放锁
    public void unlockPickingSendTaskComplete(Integer siteId, Integer nextSite) {
        String lockKey = this.getLockKeyPickingSendTaskComplete(siteId, nextSite);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    //获取key
    private String getLockKeyPickingSendTaskComplete(Integer siteId, Integer nextSite) {
        return String.format(LOCK_PICKING_SEND_COMPLETE_PRE, siteId, nextSite);
    }


    /**
     * 提发批次数据删除加锁
     * @param siteCode
     * @param nextSiteId
     * @return
     */
    public boolean lockPickingSendBatchCodeDel(int siteCode, Integer nextSiteId) {
        String lockKey = this.getLockKeyPickingSendBatchCodeDel(siteCode, nextSiteId);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, LOCK_PICKING_SEND_BATCH_CODE_DELETE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    //释放锁
    public void unlockPickingSendBatchCodeDel(int siteCode, Integer nextSiteId) {
        String lockKey = this.getLockKeyPickingSendBatchCodeDel(siteCode, nextSiteId);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    //获取key
    private String getLockKeyPickingSendBatchCodeDel(int siteCode, Integer nextSiteId) {
        return String.format(LOCK_PICKING_SEND_BATCH_CODE_DELETE_PRE, siteCode, nextSiteId);
    }

    /**
     * 提发异常上报缓存
     * @return
     */
    public void saveCachePickingExceptionSubmit(String bizId) {
        String cacheKey = this.getCachePickingExceptionSubmitKey(bizId);
        redisClientOfJy.setEx(cacheKey, DEFAULT_VALUE_1, CACHE_PICKING_EXCEPTION_SUBMIT_TIMEOUT_HOURS, TimeUnit.HOURS);
    }
    public boolean existCachePickingExceptionSubmitValue(String bizId) {
        String cacheKey = this.getCachePickingExceptionSubmitKey(bizId);
        String value = redisClientOfJy.get(cacheKey);
        return StringUtils.isNotBlank(value);
    }
    private String getCachePickingExceptionSubmitKey(String bizId) {
        return String.format(JyAviationRailwayPickingGoodsCacheService.CACHE_PICKING_EXCEPTION_SUBMIT_PRE, bizId);
    }

}
