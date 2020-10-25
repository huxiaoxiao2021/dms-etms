package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanCacheService;
import com.jd.ql.dms.common.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @program: bluedragon-distribution
 * @description: 装车扫描缓存相关服务
 * @author: zhengchengfa
 * @create: 2020-10-25 12:50
 */
@Service("loadScanCacheService")
public class LoadScanCacheServiceImpl implements LoadScanCacheService {

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    public boolean setWaybillLoadScan(Long taskId, String waybillCode, GoodsLoadScan goodsLoadScan) {
        String key = GoodsLoadScanConstants.CACHE_KEY_WAYBILL + taskId.toString() + waybillCode;
        return jimdbCacheService.setEx(key, goodsLoadScan, 1, TimeUnit.DAYS);
    }

    @Override
    public GoodsLoadScan getWaybillLoadScan(Long taskId, String waybillCode) {
        String key = GoodsLoadScanConstants.CACHE_KEY_WAYBILL + taskId.toString() + waybillCode;
        return jimdbCacheService.get(key, GoodsLoadScan.class);
    }

    @Override
    public boolean setWaybillLoadScanRecord(Long taskId, String waybillCode, String packageCode, GoodsLoadScanRecord goodsLoadScanRecord) {
        String key = GoodsLoadScanConstants.CACHE_KEY_PACKAGE + taskId.toString() + waybillCode + packageCode;
        return jimdbCacheService.setEx(key, goodsLoadScanRecord, 1, TimeUnit.DAYS);
    }

    @Override
    public GoodsLoadScanRecord getWaybillLoadScanRecord(Long taskId, String waybillCode, String packageCode) {
        String key = GoodsLoadScanConstants.CACHE_KEY_PACKAGE + taskId.toString() + waybillCode + packageCode;
        return jimdbCacheService.get(key, GoodsLoadScanRecord.class);
    }

    @Override
    public boolean lock(String key, long exTime) {
        String lock = GoodsLoadScanConstants.LOCK_KEY + key;
        return jimdbCacheService.setNx(lock, "1",exTime, TimeUnit.MINUTES);
    }

    @Override
    public boolean unLock(String key) {
        String lock =  GoodsLoadScanConstants.LOCK_KEY + key;
        return jimdbCacheService.del(lock);
    }

    @Override
    public boolean delWaybillLoadScan(Long taskId, String waybillCode) {
        String key = GoodsLoadScanConstants.CACHE_KEY_WAYBILL + taskId.toString() + waybillCode;
        return jimdbCacheService.del(key);
    }

    @Override
    public boolean delWaybillLoadScanRecord(Long taskId, String waybillCode, String packageCode) {
        String key = GoodsLoadScanConstants.CACHE_KEY_PACKAGE + taskId.toString() + waybillCode + packageCode;
        return jimdbCacheService.del(key);
    }


}
