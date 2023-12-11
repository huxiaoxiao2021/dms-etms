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

    private static final String CACHE_PICKING_SCAN_PRE = "cache:picking:scan:%s:%s";
    private static final Integer CACHE_PICKING_SCAN_TIMEOUT = 15;

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


    /**
     * 提货扫描缓存保存
     * @param cacheDto
     */
    public void saveCachePickingGoodScan(PickingGoodScanCacheDto cacheDto) {
        if(Objects.isNull(cacheDto) || StringUtils.isBlank(cacheDto.getBarCode()) || StringUtils.isBlank(cacheDto.getBizId())) {
            return;
        }
        String cacheKey = this.getCachePickingGoodScanKey(cacheDto.getBarCode(), cacheDto.getBizId());
        redisClientOfJy.setEx(cacheKey, "", JyAviationRailwayPickingGoodsCacheService.CACHE_PICKING_SCAN_TIMEOUT, TimeUnit.DAYS);
    }
    //提货扫描缓存查找
    public PickingGoodScanCacheDto getCachePickingGoodScanValue(String barCode, String bizId) {
        String cacheKey = this.getCachePickingGoodScanKey(barCode, bizId);
        String value = redisClientOfJy.get(cacheKey);
        if(StringUtils.isNotBlank(value)) {
            PickingGoodScanCacheDto cache = JSONObject.parseObject(value, PickingGoodScanCacheDto.class);
            return cache;
        }
        return null;
    }
    //提货扫描缓存key生成
    public String getCachePickingGoodScanKey(String barCode, String bizId) {
        return String.format(JyAviationRailwayPickingGoodsCacheService.CACHE_PICKING_SCAN_PRE, barCode, bizId);
    }




}
