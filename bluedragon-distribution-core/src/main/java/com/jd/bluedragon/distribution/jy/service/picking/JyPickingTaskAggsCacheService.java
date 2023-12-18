package com.jd.bluedragon.distribution.jy.service.picking;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodAggsDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingSendGoodAggsDto;
import com.jd.bluedragon.utils.JsonHelper;
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
public class JyPickingTaskAggsCacheService {
    private static final Logger log = LoggerFactory.getLogger(JyPickingTaskAggsCacheService.class);

    private static final String DEFAULT_VALUE_1 = "1";

    private static final String CACHE_TASK_PICKING_AGG = "cache:task:picking:agg:%s:%s";
    private static final Integer CACHE_TASK_PICKING_AGG_TIMEOUT_DAYS = 3;

    private static final String CACHE_TASK_PICKING_SEND_AGG = "cache:task:picking:send:agg:%s:%s:%s";
    private static final Integer CACHE_TASK_PICKING_SEND_AGG_TIMEOUT_DAYS = 3;

    private static final String LOCK_PICKING_GOOD_TASK = "lock:picking:good:task:%s";
    private static final Integer LOCK_PICKING_GOOD_TASK_TIMEOUT_SECONDS = 300;

    /**
     * 实际提货的待提包裹数
     */
    private static final String CACHE_REAL_SCAN_WAIT_PICKING_PACKAGE = "cache:real:scan:wait:picking:package:%s:%s";
    private static final Integer CACHE_REAL_SCAN_WAIT_PICKING_PACKAGE_TIMEOUT_DAYS = 15;
    /**
     * 实际提货的待提箱数
     */
    private static final String CACHE_REAL_SCAN_WAIT_PICKING_BOX = "cache:real:scan:wait:pinking:box:%s:%s";
    private static final Integer CACHE_REAL_SCAN_WAIT_PICKING_BOX_TIMEOUT_DAYS = 15;
    /**
     * 实际提货的多提包裹数
     */
    private static final String CACHE_REAL_SCAN_MORE_PICKING_PACKAGE = "cache:real:scan:more:pinking:package:%s:%s";
    private static final Integer CACHE_REAL_SCAN_MORE_PICKING_PACKAGE_TIMEOUT_DAYS = 15;
    /**
     * 实际提货的多提箱数
     */
    private static final String CACHE_REAL_SCAN_MORE_PICKING_BOX = "cache:real:scan:more:pinking:box:%s:%s";
    private static final Integer CACHE_REAL_SCAN_MORE_PICKING_BOX_TIMEOUT_DAYS = 15;
    /**
     * 实际提发货的待提包裹数
     */
    private static final String CACHE_REAL_SCAN_WAIT_SEND_PACKAGE = "cache:real:scan:wait:send:package:%s:%s";
    private static final Integer CACHE_REAL_SCAN_WAIT_SEND_PACKAGE_TIMEOUT_DAYS = 15;
    /**
     * 实际提发货的待提箱数
     */
    private static final String CACHE_REAL_SCAN_WAIT_SEND_BOX = "cache:real:scan:wait:send:box:%s:%s";
    private static final Integer CACHE_REAL_SCAN_WAIT_SEND_BOX_TIMEOUT_DAYS = 15;
    /**
     * 实际提发货的多提包裹数
     */
    private static final String CACHE_REAL_SCAN_MORE_SEND_PACKAGE = "cache:real:scan:more:send:package:%s:%s";
    private static final Integer CACHE_REAL_SCAN_MORE_SEND_PACKAGE_TIMEOUT_DAYS = 15;
    /**
     * 实际提发货的多提箱数
     */
    private static final String CACHE_REAL_SCAN_MORE_SEND_BOX = "cache:real:scan:more:send:box:%s:%s";
    private static final Integer CACHE_REAL_SCAN_MORE_SEND_BOX_TIMEOUT_DAYS = 15;

    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    private JimDbLock jimDbLock;

    /**
     * 提货任务统计缓存处理
     */
    public void saveCacheTaskPickingAgg(Integer siteId, String bizId, PickingGoodAggsDto value) {
        if(Objects.isNull(siteId) || StringUtils.isBlank(bizId) || Objects.isNull(value)) {
            return;
        }
        String cacheKey = this.getCacheKeyTaskPickingAgg(siteId, bizId);
        redisClientOfJy.setEx(cacheKey, JsonHelper.toJson(value), CACHE_TASK_PICKING_AGG_TIMEOUT_DAYS, TimeUnit.DAYS);
    }

    public PickingGoodAggsDto getCacheTaskPickingAgg(Integer siteId, String bizId) {
        String cacheKey = this.getCacheKeyTaskPickingAgg(siteId, bizId);
        String cacheValue = redisClientOfJy.get(cacheKey);
        if(StringUtils.isNotBlank(cacheKey)) {
            PickingGoodAggsDto aggsDto = JSONObject.parseObject(cacheValue, PickingGoodAggsDto.class);
            return aggsDto;
        }
        return null;
    }

    private String getCacheKeyTaskPickingAgg(Integer siteId, String bizId) {
        return String.format(CACHE_TASK_PICKING_AGG, siteId, bizId);
    }

    /**
     * 发货流向提货任务统计缓存处理
     * * 待发数据需要在发货明细初始化完成之后统计待发
     */
    public void saveCacheTaskPickingSendAgg(Integer siteId, Integer nextSiteId, String bizId, PickingSendGoodAggsDto value) {
        if(Objects.isNull(siteId) || Objects.isNull(nextSiteId) || StringUtils.isBlank(bizId) || Objects.isNull(value)) {
            return;
        }
        String cacheKey = this.getCacheKeyTaskPickingSendAgg(siteId, nextSiteId, bizId);
        redisClientOfJy.setEx(cacheKey, JsonHelper.toJson(value), CACHE_TASK_PICKING_SEND_AGG_TIMEOUT_DAYS, TimeUnit.DAYS);
    }
    public PickingSendGoodAggsDto getCacheTaskPickingSendAgg(Integer siteId, Integer nextSiteId, String bizId) {
        String cacheKey = this.getCacheKeyTaskPickingSendAgg(siteId, nextSiteId, bizId);
        String cacheValue = redisClientOfJy.get(cacheKey);
        if(StringUtils.isNotBlank(cacheKey)) {
            PickingSendGoodAggsDto aggsDto = JSONObject.parseObject(cacheValue, PickingSendGoodAggsDto.class);
            return aggsDto;
        }
        return null;
    }
    private String getCacheKeyTaskPickingSendAgg(Integer siteId, Integer nextSiteId, String bizId) {
        return String.format(CACHE_TASK_PICKING_SEND_AGG, siteId, nextSiteId, bizId);
    }


    /**
     * 提货任务维度锁 bizId
     * 使用场景说明
     * 1、按任务维度回刷统计数据
     * 2、
     */
    public boolean saveLockPickingGoodTask(String bizId) {
        String cacheKey = this.getLockKeyPickingGoodTask(bizId);
        return jimDbLock.lock(cacheKey, DEFAULT_VALUE_1, LOCK_PICKING_GOOD_TASK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    public void unlockLockPickingGoodTask(String bizId) {
        String cacheKey = this.getLockKeyPickingGoodTask(bizId);
        jimDbLock.releaseLock(cacheKey, DEFAULT_VALUE_1);
    }
    private String getLockKeyPickingGoodTask(String bizId) {
        return String.format(LOCK_PICKING_GOOD_TASK, bizId);
    }


    /**
     * 实际提货的待提包裹数
     * @param bizId
     * @param siteId
     * @return
     */
    public Integer incrRealScanWaitPickingPackageNum(String bizId, Long siteId){
        String key = this.getCacheKeyRealScanWaitPickingPackageNum(bizId, siteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_WAIT_PICKING_PACKAGE_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }
    public Integer getValueRealScanWaitPickingPackageNum(String bizId, Long siteId){
        String key = this.getCacheKeyRealScanWaitPickingPackageNum(bizId, siteId);
        String numStr = redisClientOfJy.get(key);
        if(StringUtils.isBlank(numStr)) {
            return 0;
        }
        return Integer.valueOf(numStr);
    }
    private String getCacheKeyRealScanWaitPickingPackageNum(String bizId, Long siteId){
        return String.format(CACHE_REAL_SCAN_WAIT_PICKING_PACKAGE, bizId, siteId);
    }


    /**
     * 实际提货的待提箱数
     */
    public Integer incrRealScanWaitPickingBoxNum(String bizId, Long siteId){
        String key = this.getCacheKeyRealScanWaitPickingBoxNum(bizId, siteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_WAIT_PICKING_BOX_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }
    public Integer getValueRealScanWaitPickingBoxNum(String bizId, Long siteId){
        String key = this.getCacheKeyRealScanWaitPickingBoxNum(bizId, siteId);
        String numStr = redisClientOfJy.get(key);
        if(StringUtils.isBlank(numStr)) {
            return 0;
        }
        return Integer.valueOf(numStr);
    }
    private String getCacheKeyRealScanWaitPickingBoxNum(String bizId, Long siteId){
        return String.format(CACHE_REAL_SCAN_WAIT_PICKING_BOX, bizId, siteId);
    }

    /**
     * 实际提货的多提包裹数
     */
    public Integer incrRealScanMorePickingPackageNum(String bizId, Long siteId){
        String key = this.getCacheKeyRealScanMorePickingPackageNum(bizId, siteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_MORE_PICKING_PACKAGE_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }
    public Integer getValueRealScanMorePickingPackageNum(String bizId, Long siteId){
        String key = this.getCacheKeyRealScanMorePickingPackageNum(bizId, siteId);
        String numStr = redisClientOfJy.get(key);
        if(StringUtils.isBlank(numStr)) {
            return 0;
        }
        return Integer.valueOf(numStr);
    }
    private String getCacheKeyRealScanMorePickingPackageNum(String bizId, Long siteId){
        return String.format(CACHE_REAL_SCAN_MORE_PICKING_PACKAGE, bizId, siteId);
    }

    /**
     * 实际提货的多提箱数
     */
    public Integer incrRealScanMorePickingBoxNum(String bizId, Long siteId){
        String key = this.getCacheKeyRealScanMorePickingBoxNum(bizId, siteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_MORE_PICKING_BOX_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }
    public Integer getValueRealScanMorePickingBoxNum(String bizId, Long siteId){
        String key = this.getCacheKeyRealScanMorePickingBoxNum(bizId, siteId);
        String numStr = redisClientOfJy.get(key);
        if(StringUtils.isBlank(numStr)) {
            return 0;
        }
        return Integer.valueOf(numStr);
    }
    private String getCacheKeyRealScanMorePickingBoxNum(String bizId, Long siteId){
        return String.format(CACHE_REAL_SCAN_MORE_PICKING_BOX, bizId, siteId);
    }

    //
    public Integer incrRealScanWaitSendPackageNum(String bizId, Long siteId){
        String key = String.format(CACHE_REAL_SCAN_WAIT_SEND_PACKAGE, bizId, siteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_WAIT_SEND_PACKAGE_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }

    public Integer incrRealScanWaitSendBoxNum(String bizId, Long siteId){
        String key = String.format(CACHE_REAL_SCAN_WAIT_SEND_BOX, bizId, siteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_WAIT_SEND_BOX_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }

    public Integer incrRealScanMoreSendPackageNum(String bizId, Long siteId){
        String key = String.format(CACHE_REAL_SCAN_MORE_SEND_PACKAGE, bizId, siteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_MORE_SEND_PACKAGE_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }

    public Integer incrRealScanMoreSendBoxNum(String bizId, Long siteId){
        String key = String.format(CACHE_REAL_SCAN_MORE_SEND_BOX, bizId, siteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_MORE_SEND_BOX_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }
}
