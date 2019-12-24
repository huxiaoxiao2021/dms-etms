package com.jd.bluedragon.distribution.batch.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.batch.dao.BatchInfoDao;
import com.jd.bluedragon.distribution.batch.domain.BatchInfo;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 获取返单条码
 */
@Service("batchInfoService")
class BatchInfoServiceImpl implements BatchInfoService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final int timeout = 172800;

    @Autowired
    private BatchInfoDao batchInfoDao;

    @Autowired
    private IGenerateObjectId genObjectId;

    @Autowired
    RedisManager redisManager;

    @Autowired
    BaseMinorManager baseMinorManager;

    @JProfiler(jKey = "DMSWEB.BatchInfoService.addBatchInfo", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer add(BatchInfo batchInfo) {
        Assert.notNull(batchInfo, "batchInfo must not be null");

        BatchInfo maxBatchInfo=null;
        List<BatchInfo> tempList=batchInfoDao.findMaxCreateTimeBatchInfo(batchInfo);
        if(tempList.size()>0){
            maxBatchInfo=tempList.get(0);
        }

        String batchCode=batchInfo.getCreateSiteCode()+new SimpleDateFormat("yyyyMMdd").format(new Date())
                + StringHelper.padZero(this.genObjectId.getObjectId(this.generateKey(batchInfo)));
        batchInfo.setBatchCode(batchCode);
        batchInfo.setUpdateTime(new Date(0));
        Integer value= this.batchInfoDao.add(BatchInfoDao.namespace, batchInfo);
        if (value == 1) {
            BatchInfo temp = this.batchInfoDao.findBatchInfoByCode(batchInfo.getBatchCode());
            batchInfo.setCreateTime(temp.getCreateTime());//客户端使用
            if(maxBatchInfo!=null){
                maxBatchInfo.setUpdateTime(temp.getCreateTime());
                batchInfoDao.updateBatchInfoTime(maxBatchInfo);
            }
            try {
                redisManager.setex(getRedisKey(temp), timeout,
                        JsonHelper.toJson(temp));
            } catch (Exception e) {
                this.log.error("创建波次号写入缓存失败",e);
            }
        }

        return  value;
    }

    private String generateKey(BatchInfo batchInfo) {
        return BatchInfo.class.getName() + batchInfo.getCreateSiteCode();
    }

    public BatchInfo findBatchInfoByCode(String code) {
        Assert.notNull(code, "code must not be null");

        return this.batchInfoDao.findBatchInfoByCode(code);
    }

    @JProfiler(jKey=  "DMSWEB.BatchInfoService.findMaxCreqteTimeBatchInfo", mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.NOT_SUPPORTED)
    public List<BatchInfo> findMaxCreateTimeBatchInfo(BatchInfo batchInfo) {
        Assert.notNull(batchInfo.getCreateSiteCode(), "createSiteCode must not be null");
        String maxbatchinfocode=getRedisKey(batchInfo);
        try {
            // 取出缓存
            // key箱号
            List<BatchInfo> lst=Lists.newArrayList();
            String batchInfoJson = redisManager.getCache(maxbatchinfocode);
            BatchInfo batchCache = null;

            if (batchInfoJson != null && !batchInfoJson.isEmpty()) {
                batchCache = JsonHelper.fromJson(batchInfoJson, BatchInfo.class);
                if (batchCache != null) {
                    this.log.info("findMaxCreateTimeBatchInfo缓存命中波次为:{}" , maxbatchinfocode);
                    //如果箱号 目的地 始发地不为空的时候
                    if (batchCache != null&&batchCache.getBatchCode() != null && batchCache.getCreateSiteCode() != null) {
                        lst.add(batchCache);
                        return lst;
                    }
                } else {
                    this.log.info("findMaxCreateTimeBatchInfo没有缓存波次号:{}" , maxbatchinfocode);
                }
            } else {
                this.log.info("findMaxCreateTimeBatchInfo缓存命中,但是消息为null,波次号为{}" , maxbatchinfocode);
            }


        } catch (Exception e) {
            this.log.error("findMaxCreateTimeBatchInfo获取缓存波次号失败，波次号为:{}" , maxbatchinfocode, e);
        }

        return batchInfoDao.findMaxCreateTimeBatchInfo(batchInfo);
    }


    private String getRedisKey(BatchInfo batchInfo){
        return batchInfo.getCreateSiteCode()+"maxbatchinfocode";
    }


    @Override
    public BatchInfo findCurrentBatchInfo(Integer sortingCenterId, Date operateTime) {
        return batchInfoDao.findCurrent(sortingCenterId,operateTime);
    }


    public BatchInfo findBatchInfoByBatchInfoCode(BatchInfo batchInfo) {
        Assert.notNull(batchInfo, "batchInfo must not be null");
        return this.batchInfoDao.findBatchInfoByBatchInfoCode(batchInfo);
    }

    public List<BatchInfo> findBatchInfoes(BatchInfo batchInfo) {
        Assert.notNull(batchInfo, "batchInfo must not be null");
        return this.batchInfoDao.findBatchInfoesBySite(batchInfo);
    }

    @Override
    public List<BatchInfo> findBatchInfo(BatchInfo batchInfo) {
        return batchInfoDao.findBatchInfo(batchInfo);
    }

    @Override
    public List<BatchInfo> findAllBatchInfo(BatchInfo batchInfo) {
        return batchInfoDao.findAllBatchInfo(batchInfo);
    }
}