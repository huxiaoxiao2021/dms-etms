package com.jd.bluedragon.distribution.jy.service.collect;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dto.collect.BatchUpdateCollectStatusDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectDto;
import com.jd.bluedragon.distribution.jy.dto.collect.InitCollectSplitDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.collect.constant.CollectCacheConstant;
import com.jd.bluedragon.distribution.jy.service.collect.emuns.CollectInitNodeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
@Service
public class JyCollectCacheService {
    private Logger log = LoggerFactory.getLogger(JyCollectCacheService.class);

    @Resource
    private Cluster redisClientCache;


    /**
     * 空任务扫描运单，按运单批修改集齐状态: cache 保存
     */
    public void cacheSaveTaskNullWaybillUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
        String methodDesc = "cacheSaveTaskNullWaybillUpdateCollectStatus：空任务按单扫描修改集齐状态添加redis防重缓存:";

        try {
            String cacheKey = getCacheKeyTaskNullWaybillUpdateCollectStatus(paramDto);
            redisClientCache.setEx(cacheKey, StringUtils.EMPTY, CollectCacheConstant.CACHE_TASK_NULL_WAYBILL_UPDATE_COLLECT_STATUS_TIMEOUT, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("集齐按单批量修改状态redis防重缓存添加失败");
        }
    }
    //空任务扫描运单，按运单批修改集齐状态: cache 是否存在
    public Boolean cacheExistTaskNullWaybillUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
        String methodDesc = "cacheExistTaskNullWaybillUpdateCollectStatus：空任务按单扫描修改集齐状态redis防重缓存校验是否存在:";
        try {
            String cacheKey = getCacheKeyTaskNullWaybillUpdateCollectStatus(paramDto);
            return redisClientCache.exists(cacheKey);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("集齐按单批量修改状态redis防重缓存校验是否存在失败");
        }
    }
    //空任务扫描运单，按运单批修改集齐状态: get cache key
    public String getCacheKeyTaskNullWaybillUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
            StringBuilder sb = new StringBuilder();
            sb.append(CollectCacheConstant.CACHE_TASK_NULL_WAYBILL_UPDATE_COLLECT_STATUS)
                    .append(paramDto.getBizId())
                    .append(Constants.SEPARATOR_COLON)
                    .append(WaybillUtil.getWaybillCode(paramDto.getScanCode()))
                    .append(Constants.SEPARATOR_COLON)
                    .append(paramDto.getPageNo())
                    .append(Constants.SEPARATOR_COLON)
                    .append(paramDto.getPageSize());
            return sb.toString();
    }


    /**
     * 空任务扫描运单，按运单批修改集齐状态: cache 保存
     */
    public void cacheSaveTaskNullWaybillSplitUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
        String methodDesc = "cacheSaveTaskNullWaybillSplitUpdateCollectStatus：集齐批量修改状态按单拆分添加redis防重缓存:";

        try {
            String cacheKey = getCacheKeyTaskNullWaybillSplitUpdateCollectStatus(paramDto);
            redisClientCache.setEx(cacheKey, StringUtils.EMPTY, CollectCacheConstant.CACHE_TASK_NULL_WAYBILL_SPLIT_UPDATE_COLLECT_STATUS_TIMEOUT, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("集齐批量修改状态按单拆分redis防重缓存添加失败");
        }
    }
    //空任务扫描运单，按运单批修改集齐状态: cache 是否存在
    public Boolean cacheExistTaskNullWaybillSplitUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
        String methodDesc = "cacheExistTaskNullWaybillSplitUpdateCollectStatus：集齐批量修改状态按单拆分redis防重缓存校验是否存在:";
        try {
            String cacheKey = getCacheKeyTaskNullWaybillSplitUpdateCollectStatus(paramDto);
            return redisClientCache.exists(cacheKey);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("集齐批量修改状态按单拆分redis防重缓存校验是否存在失败");
        }
    }
    //空任务扫描运单，按运单批修改集齐状态: get cache key
    public String getCacheKeyTaskNullWaybillSplitUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
        StringBuilder sb = new StringBuilder();
        sb.append(CollectCacheConstant.CACHE_TASK_NULL_WAYBILL_SPLIT_UPDATE_COLLECT_STATUS)
                .append(paramDto.getBizId())
                .append(Constants.SEPARATOR_COLON)
                .append(WaybillUtil.getWaybillCode(paramDto.getScanCode()));
        return sb.toString();
    }

    /**
     * 空任务扫描运单，按运单批修改集齐状态: lock 保存
     */
    public Boolean lockSaveTaskNullWaybillUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
        String methodDesc = "lockSaveTaskNullWaybillUpdateCollectStatus：空任务按单扫描修改集齐状态添加redis并发锁:";
        try {
            String lockKey = getLockKeyTaskNullWaybillUpdateCollectStatus(paramDto);
            return redisClientCache.set(lockKey,StringUtils.EMPTY,CollectCacheConstant.LOCK_TASK_NULL_WAYBILL_UPDATE_COLLECT_STATUS_TIMEOUT, TimeUnit.MINUTES,false);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("集齐按单批量修改状态并发锁添加失败");
        }
    }
    //空任务扫描运单，按运单批修改集齐状态: lock 删除
    public void lockDelTaskNullWaybillUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
        String methodDesc = "lockDelTaskNullWaybillUpdateCollectStatus：空任务按单扫描修改集齐状态删除redis并发锁:";
        try {
            redisClientCache.del(getLockKeyTaskNullWaybillUpdateCollectStatus(paramDto));
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("集齐按单批量修改状态并发锁删除失败");
        }
    }
    //空任务扫描运单，按运单批修改集齐状态: get lock key
    public String getLockKeyTaskNullWaybillUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
            StringBuilder sb = new StringBuilder();
            sb.append(CollectCacheConstant.LOCK_TASK_NULL_WAYBILL_UPDATE_COLLECT_STATUS)
                    .append(paramDto.getBizId())
                    .append(Constants.SEPARATOR_COLON)
                    .append(WaybillUtil.getWaybillCode(paramDto.getScanCode()))
                    .append(Constants.SEPARATOR_COLON)
                    .append(paramDto.getPageNo())
                    .append(Constants.SEPARATOR_COLON)
                    .append(paramDto.getPageSize());
            return sb.toString();
    }


    /**
     * 空任务扫描运单，按运单批修改集齐状态: lock 保存
     */
    public Boolean lockSaveTaskNullWaybillSplitUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
        String methodDesc = "lockSaveTaskNullWaybillSplitUpdateCollectStatus：集齐批量修改状态按单拆分添加redis并发锁:";
        try {
            String lockKey = getLockKeyTaskNullWaybillSplitUpdateCollectStatus(paramDto);
            return redisClientCache.set(lockKey,StringUtils.EMPTY,CollectCacheConstant.LOCK_TASK_NULL_WAYBILL_SPLIT_UPDATE_COLLECT_STATUS_TIMEOUT, TimeUnit.MINUTES,false);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("集齐按单批量修改状态并发锁添加失败");
        }
    }
    //空任务扫描运单，按运单批修改集齐状态: lock 删除
    public void lockDelTaskNullWaybillSplitUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
        String methodDesc = "lockDelTaskNullWaybillSplitUpdateCollectStatus：集齐批量修改状态按单拆分删除redis并发锁:";
        try {
            redisClientCache.del(getLockKeyTaskNullWaybillSplitUpdateCollectStatus(paramDto));
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("集齐按单批量修改状态并发锁删除失败");
        }
    }
    //空任务扫描运单，按运单批修改集齐状态: get lock key
    public String getLockKeyTaskNullWaybillSplitUpdateCollectStatus(BatchUpdateCollectStatusDto paramDto){
        StringBuilder sb = new StringBuilder();
        sb.append(CollectCacheConstant.LOCK_TASK_NULL_WAYBILL_SPLIT_UPDATE_COLLECT_STATUS)
                .append(paramDto.getBizId())
                .append(Constants.SEPARATOR_COLON)
                .append(WaybillUtil.getWaybillCode(paramDto.getScanCode()));
        return sb.toString();
    }


    /**
     * 按运单进行集齐初始化前的拆分逻辑: cache 保存
     */
    public void cacheSaveWaybillCollectSplitBeforeInit(InitCollectDto paramDto, Integer nodeType){
        String methodDesc = "cacheSaveWaybillCollectSplitBeforeInit：按运单进行集齐初始化前的拆分逻辑:添加redis防重缓存:";

        try {
            String cacheKey = getCacheKeyWaybillCollectSplitBeforeInit(paramDto, nodeType);
            redisClientCache.setEx(cacheKey, StringUtils.EMPTY, CollectCacheConstant.CACHE_WAYBILL_COLLECT_SPLIT_BEFORE_INIT_TIMEOUT, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("按运单进行集齐初始化前拆分逻辑防重缓存添加异常");
        }
    }
    //按运单进行集齐初始化前的拆分逻辑: cache 是否存在
    public Boolean cacheExistWaybillCollectSplitBeforeInit(InitCollectDto paramDto, Integer nodeType){
        String methodDesc = "cacheExistWaybillCollectSplitBeforeInit：按运单进行集齐初始化前的拆分逻辑:防重缓存校验是否存在:";
        try {
            String cacheKey = getCacheKeyWaybillCollectSplitBeforeInit(paramDto, nodeType);
            return redisClientCache.exists(cacheKey);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("按运单进行集齐初始化前拆分逻辑防重缓存校验是否存在异常");
        }
    }
    //按运单进行集齐初始化前的拆分逻辑: get cache key
    public String getCacheKeyWaybillCollectSplitBeforeInit(InitCollectDto paramDto, Integer nodeType){
        StringBuilder sb = new StringBuilder();
        sb.append(CollectCacheConstant.CACHE_WAYBILL_COLLECT_SPLIT_BEFORE_INIT)
                .append(paramDto.getBizId())
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getWaybillCode());
        if(CollectInitNodeEnum.NULL_TASK_INIT.getCode() == nodeType) {
            //无任务扫描运单初始化分两种，扫包裹运单只做初始化，扫运单该运单初始化同时更改集齐状态
            sb.append(Constants.SEPARATOR_COLON).append(paramDto.getTaskNullScanCodeType());
        }
        return sb.toString();
    }

    /**
     * 按运单进行集齐初始化前的拆分逻辑: lock 保存
     */
    public Boolean lockSaveWaybillCollectSplitBeforeInit(InitCollectDto paramDto, Integer nodeType){
        String methodDesc = "lockSaveWaybillCollectSplitBeforeInit：按运单进行集齐初始化前的拆分逻辑:添加redis并发锁:";
        try {
            String lockKey = getLockKeyWaybillCollectSplitBeforeInit(paramDto, nodeType);
            return redisClientCache.set(lockKey,StringUtils.EMPTY,CollectCacheConstant.LOCK_WAYBILL_COLLECT_SPLIT_BEFORE_INIT_TIMEOUT, TimeUnit.MINUTES,false);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("按运单进行集齐初始化前的拆分逻辑并发锁添加异常");
        }
    }
    //按运单进行集齐初始化前的拆分逻辑: lock 删除
    public void lockDelWaybillCollectSplitBeforeInit(InitCollectDto paramDto, Integer nodeType){
        String methodDesc = "lockDelWaybillCollectSplitBeforeInit：按运单进行集齐初始化前的拆分逻辑:删除redis并发锁:";
        try {
            redisClientCache.del(getLockKeyWaybillCollectSplitBeforeInit(paramDto, nodeType));
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("按运单进行集齐初始化前的拆分逻辑并发锁删除异常");
        }
    }
    //按运单进行集齐初始化前的拆分逻辑: get lock key
    public String getLockKeyWaybillCollectSplitBeforeInit(InitCollectDto paramDto, Integer nodeType){
        StringBuilder sb = new StringBuilder();
        sb.append(CollectCacheConstant.LOCK_WAYBILL_COLLECT_SPLIT_BEFORE_INIT)
                .append(paramDto.getBizId())
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getWaybillCode());
        if(CollectInitNodeEnum.NULL_TASK_INIT.getCode() == nodeType) {
            //无任务扫描运单初始化分两种，扫包裹运单只做初始化，扫运单该运单初始化同时更改集齐状态
            sb.append(Constants.SEPARATOR_COLON).append(paramDto.getTaskNullScanCodeType());
        }
        return sb.toString();
    }

    /**
     * 空任务扫描节点:按运单进行集齐拆分后的初始化逻辑: cache 保存
     */
    public void cacheSaveTaskNullWaybillCollectInitAfterSplit(InitCollectSplitDto paramDto){
        String methodDesc = "cacheSaveTaskNullWaybillInitAfterSplit：空任务扫描节点:按运单进行集齐拆分后的初始化逻辑:添加redis防重缓存:";

        try {
            String cacheKey = getCacheKeyTaskNullWaybillCollectInitAfterSplit(paramDto);
            redisClientCache.setEx(cacheKey, StringUtils.EMPTY, CollectCacheConstant.CACHE_TASK_NULL_WAYBILL_INIT_AFTER_SPLIT_TIMEOUT, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("按运单进行集齐拆分后的初始化逻辑防重缓存添加异常");
        }
    }
    //空任务扫描节点:按运单进行集齐拆分后的初始化逻辑: cache 是否存在
    public Boolean cacheExistTaskNullWaybillCollectInitAfterSplit(InitCollectSplitDto paramDto){
        String methodDesc = "cacheExistTaskNullWaybillInitAfterSplit：空任务扫描节点:按运单进行集齐拆分后的初始化逻辑:防重缓存校验是否存在:";
        try {
            String cacheKey = getCacheKeyTaskNullWaybillCollectInitAfterSplit(paramDto);
            return redisClientCache.exists(cacheKey);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("按运单进行集齐拆分后的初始化逻辑防重缓存校验是否存在异常");
        }
    }
    //空任务扫描节点:按运单进行集齐拆分后的初始化逻辑: get cache key
    public String getCacheKeyTaskNullWaybillCollectInitAfterSplit(InitCollectSplitDto paramDto){
        StringBuilder sb = new StringBuilder();
        sb.append(CollectCacheConstant.CACHE_TASK_NULL_WAYBILL_INIT_AFTER_SPLIT)
                .append(paramDto.getBizId())
                .append(Constants.SEPARATOR_COLON)
                .append(WaybillUtil.getWaybillCode(paramDto.getTaskNullScanCode()))
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getTaskNullScanCodeType())//运单初始化分两种，扫包裹运单只做初始化，扫运单该运单初始化同时更改集齐状态
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getPageNo())
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getPageSize());
        return sb.toString();
    }

    /**
     * 封车节点集齐拆分后初始化: cache 保存
     */
    public void cacheSaveSealCarCollectInitAfterSplit(InitCollectSplitDto paramDto){
        String methodDesc = "cacheSaveSealCarCollectInitAfterSplit：封车节点集齐拆分后初始化:添加redis防重缓存:";

        try {
            String cacheKey = getCacheKeySealCarCollectInitAfterSplit(paramDto);
            redisClientCache.setEx(cacheKey, StringUtils.EMPTY, CollectCacheConstant.CACHE_SEAL_COLLECT_INIT_AFTER_SPLIT_TIMEOUT, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("封车节点集齐拆分后初始化逻辑防重缓存添加异常");
        }
    }
    //封车节点集齐拆分后初始化: cache 是否存在
    public Boolean cacheExistSealCarCollectInitAfterSplit(InitCollectSplitDto paramDto){
        String methodDesc = "cacheExistSealCarCollectInitAfterSplit：封车节点集齐拆分后初始化:防重缓存校验是否存在:";
        try {
            String cacheKey = getCacheKeySealCarCollectInitAfterSplit(paramDto);
            return redisClientCache.exists(cacheKey);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("封车节点集齐拆分后初始化逻辑防重缓存校验是否存在异常");
        }
    }
    //封车节点集齐拆分后初始化: get cache key
    public String getCacheKeySealCarCollectInitAfterSplit(InitCollectSplitDto paramDto){
        StringBuilder sb = new StringBuilder();
        sb.append(CollectCacheConstant.CACHE_SEAL_COLLECT_INIT_AFTER_SPLIT)
                .append(paramDto.getBizId())
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getSealBatchCode())
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getPageNo())
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getPageSize());
        return sb.toString();
    }

    /**
     * 封车集齐数据初始化前拆分逻辑: cache 保存
     */
    public void cacheSaveSealCarCollectSplitBeforeInit(InitCollectDto paramDto){
        String methodDesc = "cacheSaveSealCarCollectSplitBeforeInit：封车集齐数据初始化前拆分逻辑:添加redis防重缓存:";

        try {
            String cacheKey = getCacheKeySealCarCollectSplitBeforeInit(paramDto);
            redisClientCache.setEx(cacheKey, StringUtils.EMPTY, CollectCacheConstant.CACHE_SEAL_COLLECT_SPLIT_BEFORE_INIT_TIMEOUT, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("封车集齐数据初始化前拆分逻辑防重缓存添加异常");
        }
    }
    //封车集齐数据初始化前拆分逻辑: cache 是否存在
    public Boolean cacheExistSealCarCollectSplitBeforeInit(InitCollectDto paramDto){
        String methodDesc = "cacheExistSealCarCollectSplitBeforeInit：封车集齐数据初始化前拆分逻辑:防重缓存校验是否存在:";
        try {
            String cacheKey = getCacheKeySealCarCollectSplitBeforeInit(paramDto);
            return redisClientCache.exists(cacheKey);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("封车集齐数据初始化前拆分逻辑防重缓存校验是否存在异常");
        }
    }
    //封车集齐数据初始化前拆分逻辑: get cache key
    public String getCacheKeySealCarCollectSplitBeforeInit(InitCollectDto paramDto){
        StringBuilder sb = new StringBuilder();
        sb.append(CollectCacheConstant.CACHE_SEAL_COLLECT_SPLIT_BEFORE_INIT)
                .append(paramDto.getBizId());
        return sb.toString();
    }





    /**
     * 按运单进行集齐拆分后的初始化逻辑: cache 保存
     */
    public void cacheSaveSealWaybillCollectInitAfterSplit(InitCollectSplitDto paramDto){
        String methodDesc = "cacheSaveSealWaybillCollectInitAfterSplit：按运单进行集齐拆分后的初始化逻辑:添加redis防重缓存:";

        try {
            String cacheKey = getCacheKeySealWaybillCollectInitAfterSplit(paramDto);
            redisClientCache.setEx(cacheKey, StringUtils.EMPTY, CollectCacheConstant.CACHE_SEAL_WAYBILL_COLLECT_INIT_AFTER_SPLIT_TIMEOUT, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("按运单进行集齐拆分后的初始化逻辑防重缓存添加异常");
        }
    }
    //按运单进行集齐拆分后的初始化逻辑: cache 是否存在
    public Boolean cacheExistSealWaybillCollectInitAfterSplit(InitCollectSplitDto paramDto){
        String methodDesc = "cacheExistSealWaybillCollectInitAfterSplit：按运单进行集齐拆分后的初始化逻辑:防重缓存校验是否存在:";
        try {
            String cacheKey = getCacheKeySealWaybillCollectInitAfterSplit(paramDto);
            return redisClientCache.exists(cacheKey);
        } catch (Exception e) {
            log.error("{}异常,参数bizId={}, errMsg={}", methodDesc, JsonHelper.toJson(paramDto), e.getMessage(), e);
            throw new JyBizException("按运单进行集齐拆分后的初始化逻辑防重缓存校验是否存在异常");
        }
    }
    //按运单进行集齐拆分后的初始化逻辑: get cache key
    public String getCacheKeySealWaybillCollectInitAfterSplit(InitCollectSplitDto paramDto){
        StringBuilder sb = new StringBuilder();
        sb.append(CollectCacheConstant.CACHE_SEAL_WAYBILL_COLLECT_INIT_AFTER_SPLIT)
                .append(paramDto.getBizId())
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getWaybillCode())
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getPageNo())
                .append(Constants.SEPARATOR_COLON)
                .append(paramDto.getPageSize());
        return sb.toString();
    }
}
