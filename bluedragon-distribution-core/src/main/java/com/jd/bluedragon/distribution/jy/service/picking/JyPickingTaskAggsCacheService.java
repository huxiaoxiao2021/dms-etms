package com.jd.bluedragon.distribution.jy.service.picking;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodAggsDto;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingSendGoodAggsDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;


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
}
