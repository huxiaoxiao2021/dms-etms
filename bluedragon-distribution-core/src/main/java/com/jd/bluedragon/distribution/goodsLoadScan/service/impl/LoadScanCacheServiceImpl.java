package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanCacheService;
import com.jd.ql.dms.common.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @program: bluedragon-distribution
 * @description: 装车扫描缓存相关服务
 * @author: zhengchengfa
 * @create: 2020-10-25 12:50
 */
public class LoadScanCacheServiceImpl implements LoadScanCacheService {

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    public boolean setWaybillCacheKey(Long taskId, String waybillCode) {
        return false;
    }

    @Override
    public GoodsLoadScan getWaybillCacheValue(Long taskId, String waybillCode) {
        String key = GoodsLoadScanConstants.CACHE_KEY_WAYBILL + taskId.toString() + waybillCode;
        return jimdbCacheService.get(key, GoodsLoadScan.class);
    }

    @Override
    public boolean setPackageCacheKey(Long taskId, String waybillCode, String packageCode) {
        return false;
    }

    @Override
    public GoodsLoadScanRecord getPackageCacheValue(Long taskId, String waybillCode, String packageCode) {
        String key = GoodsLoadScanConstants.CACHE_KEY_PACKAGE + taskId.toString() + waybillCode + packageCode;
        return jimdbCacheService.get(key, GoodsLoadScanRecord.class);
    }
}
