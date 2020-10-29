package com.jd.bluedragon.distribution.goodsLoadScan.service;

import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScanRecord;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;

/**
 * @program: bluedragon-distribution
 * @description: 装车扫描相关缓存
 * @author: zhengchengfa
 * @create: 2020-10-25 12:35
 */
public interface LoadScanCacheService {

    public boolean setWaybillLoadScan(Long taskId, String waybillCode, GoodsLoadScan goodsLoadScan);

    public GoodsLoadScan getWaybillLoadScan(Long taskId, String waybillCode);

    public boolean setWaybillLoadScanRecord(Long taskId, String waybillCode, String packageCode, GoodsLoadScanRecord goodsLoadScanRecord);

    public GoodsLoadScanRecord getWaybillLoadScanRecord(Long taskId, String waybillCode, String packageCode);

    public boolean lock(String key, long exTime);

    public boolean unLock(String key);

    public boolean delWaybillLoadScan(Long taskId, String waybillCode);

    public boolean delWaybillLoadScanRecord(Long taskId, String waybillCode, String packageCode);

    public boolean setTaskLoadScan(LoadCar loadCar);

    public LoadCar getTaskLoadScan(Long taskId);

    public boolean delTaskLoadScan(Long taskId);
}
