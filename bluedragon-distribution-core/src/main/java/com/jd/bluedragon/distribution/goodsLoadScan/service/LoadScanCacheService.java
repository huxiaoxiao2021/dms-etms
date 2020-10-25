package com.jd.bluedragon.distribution.goodsLoadScan.service;

import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;

/**
 * @program: bluedragon-distribution
 * @description: 装车扫描相关缓存
 * @author: zhengchengfa
 * @create: 2020-10-25 12:35
 */
public interface LoadScanCacheService {

    public boolean setWaybillCacheKey(Long taskId, String waybillCode);

    public GoodsLoadScan getWaybillCacheValue(Long taskId, String waybillCode);

    public boolean setPackageCacheKey(Long taskId, String waybillCode, String packageCode);

    public GoodsLoadScanRecord getPackageCacheValue(Long taskId, String waybillCode, String packageCode);


}
