package com.jd.bluedragon.distribution.jy.service.task;

import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.jim.cli.Cluster;
import org.apache.commons.lang3.StringUtils;
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
public class JyBizTaskSendAviationPlanCacheService {

    private static final Logger log = LoggerFactory.getLogger(JyBizTaskSendAviationPlanCacheService.class);

    public static final String DEFAULT_VALUE_1 = "1";




    /**
     * K:bizId V：erp
     */
    public static final String CACHE_AVIATION_PLAN_CANCEL = "cache:aviationPlan:cancel::%s";
    public static final int CACHE_AVIATION_PLAN_CANCEL_TIMEOUT_HOURS = 24;


    @Autowired
    private JimDbLock jimDbLock;
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;


    /**
     * 航空计划取消
     */
    public void saveCacheAviationPlanCancel(String bizId) {
        if(StringUtils.isBlank(bizId)) {
            return;
        }
        try {
            String cacheKey = this.getCacheKeyAviationPlanCancel(bizId);
            redisClientOfJy.setEx(cacheKey,
                    DEFAULT_VALUE_1,
                    JyBizTaskSendAviationPlanCacheService.CACHE_AVIATION_PLAN_CANCEL_TIMEOUT_HOURS,
                    TimeUnit.HOURS);
        }catch (Exception e) {
            log.error("lockTaskBindBizId:任务绑定加锁失败:bizId={},errMsg={}", bizId, e.getMessage(), e);
            throw new JyBizException("任务绑定加锁失败");
        }
    }
    /**
     * 返回true: 航空计划被取消
     * @param bizId
     * @return
     */
    public boolean existCacheAviationPlanCancel(String bizId) {
        if(StringUtils.isBlank(bizId)) {
            return false;
        }
        return StringUtils.isNotBlank(redisClientOfJy.get(this.getCacheKeyAviationPlanCancel(bizId)));
    }
    public String getCacheKeyAviationPlanCancel(String bizId) {
        return String.format(JyBizTaskSendAviationPlanCacheService.CACHE_AVIATION_PLAN_CANCEL, bizId);

    }




}
