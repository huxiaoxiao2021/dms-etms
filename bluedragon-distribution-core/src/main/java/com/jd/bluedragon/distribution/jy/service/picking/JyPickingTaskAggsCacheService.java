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
     * 按发货流向维度：该流向实际扫描的交接包裹数
     */
    private static final String CACHE_REAL_SCAN_FLOW_WAIT_SEND_PACKAGE = "cache:real:scan:flow:wait:send:package:%s:%s:%s";
    private static final Integer CACHE_REAL_SCAN_FLOW_WAIT_SEND_PACKAGE_TIMEOUT_DAYS = 15;
    /**
     * 按发货流向维度：该流向实际扫描的交接箱数
     */
    private static final String CACHE_REAL_SCAN_FLOW_WAIT_SEND_BOX = "cache:real:scan:flow:wait:send:box:%s:%s:%s";
    private static final Integer CACHE_REAL_SCAN_FLOW_WAIT_SEND_BOX_TIMEOUT_DAYS = 15;
    /**
     * 按发货流向维度：该流向实际提发货的多提包裹数
     */
    private static final String CACHE_REAL_SCAN_FLOW_MORE_SEND_PACKAGE = "cache:real:scan:flow:more:send:package:%s:%s:%s";
    private static final Integer CACHE_REAL_SCAN_FLOW_MORE_SEND_PACKAGE_TIMEOUT_DAYS = 15;
    /**
     * 按发货流向维度：该流向实际提发货的多提箱数
     */
    private static final String CACHE_REAL_SCAN_FLOW_MORE_SEND_BOX = "cache:real:scan:flow:more:send:box:%s:%s:%s";
    private static final Integer CACHE_REAL_SCAN_FLOW_MORE_SEND_BOX_TIMEOUT_DAYS = 15;

    /**
     * 实际扫描统计数据加工，防重cache
     */
    private static final String CACHE_REAL_PICKING_SCAN_STATISTICS = "cache:real:picking:scan:statistics:%s:%s:%s:%s";
    private static final Integer CACHE_REAL_PICKING_SCAN_STATISTICS_TIMEOUT_DAYS = 15;



    /**
     * 提货任务初始化后的待提总数
     */
    private static final String CACHE_INIT_WAIT_PICKING_TOTAL_NUM = "cache:init:wait:picking:total:num:%s:%s";
    //todo zcf 扫描续期
    private static final Integer CACHE_INIT_WAIT_PICKING_TOTAL_NUM_TIMEOUT_SECOND = 60;
    /**
     * 提货任务初始化后按流向的待发总数
     */
    private static final String CACHE_INIT_WAIT_SEND_TOTAL_NUM = "cache:init:wait:send:total:num:%s:%s:%s";
    private static final Integer CACHE_INIT_WAIT_SEND_TOTAL_NUM_TIMEOUT_SECOND = 5;


    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;
    @Autowired
    private JimDbLock jimDbLock;

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



    /**
     * 按发货流向维度：该流向实际提发货的需要待发的包裹数
     * @param bizId
     * @param siteId
     * @param nextSiteId
     * @return
     */
    public Integer incrRealScanFlowWaitSendPackageNum(String bizId, Long siteId, Long nextSiteId){
        String key = this.getCacheKeyRealScanFlowWaitSendPackageNum(bizId, siteId, nextSiteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_FLOW_WAIT_SEND_PACKAGE_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }
    public Integer getValueRealScanFlowWaitSendPackageNum(String bizId, Long siteId, Long nextSiteId){
        String key = this.getCacheKeyRealScanFlowWaitSendPackageNum(bizId, siteId, nextSiteId);
        String numStr = redisClientOfJy.get(key);
        if(StringUtils.isBlank(numStr)) {
            return 0;
        }
        return Integer.valueOf(numStr);
    }
    private String getCacheKeyRealScanFlowWaitSendPackageNum(String bizId, Long siteId, Long nextSiteId){
        return String.format(CACHE_REAL_SCAN_FLOW_WAIT_SEND_PACKAGE, bizId, siteId, nextSiteId);
    }

    /**
     * 按发货流向维度：该流向实际提发货的需要待提的箱数
     * @param bizId
     * @param siteId
     * @param nextSiteId
     * @return
     */
    public Integer incrRealScanFlowWaitSendBoxNum(String bizId, Long siteId, Long nextSiteId){
        String key = this.getCacheKeyRealScanFlowWaitSendBoxNum(bizId, siteId, nextSiteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_FLOW_WAIT_SEND_BOX_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }
    public Integer getValueRealScanFlowWaitSendBoxNum(String bizId, Long siteId, Long nextSiteId){
        String key = this.getCacheKeyRealScanFlowWaitSendBoxNum(bizId, siteId, nextSiteId);
        String numStr = redisClientOfJy.get(key);
        if(StringUtils.isBlank(numStr)) {
            return 0;
        }
        return Integer.valueOf(numStr);
    }
    private String getCacheKeyRealScanFlowWaitSendBoxNum(String bizId, Long siteId, Long nextSiteId){
        return String.format(CACHE_REAL_SCAN_FLOW_WAIT_SEND_BOX, bizId, siteId, nextSiteId);
    }
    /**
     * 按发货流向维度：该流向实际提发货的多发包裹数
     * @param bizId
     * @param siteId
     * @param nextSiteId
     * @return
     */
    public Integer incrRealScanFlowMoreSendPackageNum(String bizId, Long siteId, Long nextSiteId){
        String key = this.getCacheKeyRealScanFlowMoreSendPackageNum(bizId, siteId, nextSiteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_FLOW_MORE_SEND_PACKAGE_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }
    public Integer getValueRealScanFlowMoreSendPackageNum(String bizId, Long siteId, Long nextSiteId){
        String key = this.getCacheKeyRealScanFlowMoreSendPackageNum(bizId, siteId, nextSiteId);
        String numStr = redisClientOfJy.get(key);
        if(StringUtils.isBlank(numStr)) {
            return 0;
        }
        return Integer.valueOf(numStr);
    }
    private String getCacheKeyRealScanFlowMoreSendPackageNum(String bizId, Long siteId, Long nextSiteId){
        return String.format(CACHE_REAL_SCAN_FLOW_MORE_SEND_PACKAGE, bizId, siteId, nextSiteId);
    }
    /**
     * 按发货流向维度：该流向实际提发货的多发箱数
     * @param bizId
     * @param siteId
     * @param nextSiteId
     * @return
     */
    public Integer incrRealScanFlowMoreSendBoxNum(String bizId, Long siteId, Long nextSiteId){
        String key = this.getCacheKeyRealScanFlowMoreSendBoxNum(bizId, siteId, nextSiteId);
        Long num = redisClientOfJy.incr(key);
        redisClientOfJy.expire(key, CACHE_REAL_SCAN_FLOW_MORE_SEND_BOX_TIMEOUT_DAYS, TimeUnit.DAYS);
        return num.intValue();
    }
    public Integer getValueRealScanFlowMoreSendBoxNum(String bizId, Long siteId, Long nextSiteId){
        String key = this.getCacheKeyRealScanFlowMoreSendBoxNum(bizId, siteId, nextSiteId);
        String numStr = redisClientOfJy.get(key);
        if(StringUtils.isBlank(numStr)) {
            return 0;
        }
        return Integer.valueOf(numStr);
    }
    private String getCacheKeyRealScanFlowMoreSendBoxNum(String bizId, Long siteId, Long nextSiteId){
        return String.format(CACHE_REAL_SCAN_FLOW_MORE_SEND_BOX, bizId, siteId, nextSiteId);
    }



    /**
     * 任务初始化的待提数量
     */
    public void saveCacheInitWaitPickingTotalItemNum(String bizId, Long siteId, Integer value) {
        String cacheKey = this.getCacheKeyInitWaitPickingTotalItemNum(bizId, siteId);
        redisClientOfJy.setEx(cacheKey, value.toString(), CACHE_INIT_WAIT_PICKING_TOTAL_NUM_TIMEOUT_SECOND, TimeUnit.SECONDS);
    }
    public Integer getCacheInitWaitPickingTotalItemNum(String bizId, Long siteId) {
        String key = this.getCacheKeyInitWaitPickingTotalItemNum(bizId, siteId);
        String value = redisClientOfJy.get(key);
        if(StringUtils.isBlank(value)) {
            return 0;
        }
        return Integer.valueOf(value);
    }
    private String getCacheKeyInitWaitPickingTotalItemNum(String bizId, Long siteId){
        return String.format(CACHE_INIT_WAIT_PICKING_TOTAL_NUM, bizId, siteId);
    }



    /**
     * 该流向任务初始化的待提数量
     */
    public void saveCacheInitWaitSendTotalItemNum(String bizId, Long siteId, Long nextSiteId, Integer value) {
        String cacheKey = this.getCacheKeyInitWaitSendTotalItemNum(bizId, siteId, nextSiteId);
        redisClientOfJy.setEx(cacheKey, value.toString(), CACHE_INIT_WAIT_SEND_TOTAL_NUM_TIMEOUT_SECOND, TimeUnit.SECONDS);
    }
    public Integer getCacheInitWaitSendTotalItemNum(String bizId, Long siteId, Long nextSiteId) {
        String key = this.getCacheKeyInitWaitSendTotalItemNum(bizId, siteId, nextSiteId);
        String value = redisClientOfJy.get(key);
        if(StringUtils.isBlank(value)) {
            return 0;
        }
        return Integer.valueOf(value);
    }
    private String getCacheKeyInitWaitSendTotalItemNum(String bizId, Long siteId, Long nextSiteId){
        return String.format(CACHE_INIT_WAIT_SEND_TOTAL_NUM, bizId, siteId, nextSiteId);
    }


    /**
     * 统计提货任务数据防重缓存
     * @param barCode 必填
     * @param bizId 必填
     * @param siteId 必填
     * @param nextSiteId 选填： 非空是为提发统计缓存，为空时是仅提货缓存
     */
    public void saveCacheRealPickingScanStatistics(String barCode, String bizId, Long siteId, Long nextSiteId) {
        String cacheKey = this.getCacheKeyRealPickingScanStatistics(barCode, bizId, siteId, nextSiteId);
        redisClientOfJy.setEx(cacheKey, DEFAULT_VALUE_1, CACHE_REAL_PICKING_SCAN_STATISTICS_TIMEOUT_DAYS, TimeUnit.DAYS);
    }
    public boolean existCacheRealPickingScanStatistics(String barCode, String bizId, Long siteId, Long nextSiteId) {
        String key = this.getCacheKeyRealPickingScanStatistics(barCode, bizId, siteId, nextSiteId);
        if(Objects.isNull(redisClientOfJy.get(key))) {
            return false;
        }
        return true;

    }
    private String getCacheKeyRealPickingScanStatistics(String barCode, String bizId, Long siteId, Long nextSiteId){
        nextSiteId = Objects.isNull(nextSiteId) ? -1 : nextSiteId;
        return String.format(CACHE_REAL_PICKING_SCAN_STATISTICS, barCode, bizId, siteId, nextSiteId);
    }
}
