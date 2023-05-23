package com.jd.bluedragon.distribution.jy.service.comboard.impl;

import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.jim.cli.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class JyGroupSortCrossDetailCacheService {

    public static final String DEFAULT_VALUE = "";
    /**
     * 混扫任务更新key: 前缀：岗位组：混扫任务编码
     */
    public static final String LOCK_MIX_SCAN_TASK_KEY = "lock:mixScanTask:%s:%s";
    public static final int  LOCK_MIX_SCAN_TASK_TIMEOUT_SECOND  = 3;

    /**
     * 混扫完成redis:前缀：岗位组：混扫任务编码
     */
    public static final String CACHE_MIX_SCAN_TASK_KEY = "cache:mixScanTask:%s:%s";
    public static final int  CACHE_MIX_SCAN_TASK_TIMEOUT_HOUR  = 12;



    @Autowired
    private JimDbLock jimDbLock;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;



    //
    public boolean getMixScanTaskCompleteLock(String groupCode, String templateCode) {
        String key = getMixScanTaskCompleteLockKey(groupCode, templateCode);
        return jimDbLock.lock(key, JyGroupSortCrossDetailCacheService.DEFAULT_VALUE, JyGroupSortCrossDetailCacheService.LOCK_MIX_SCAN_TASK_TIMEOUT_SECOND, TimeUnit.SECONDS);
    }

    public void delMixScanTaskCompleteLock(String groupCode, String templateCode) {
        String key = getMixScanTaskCompleteLockKey(groupCode, templateCode);
        jimDbLock.releaseLock(key, JyGroupSortCrossDetailCacheService.DEFAULT_VALUE);
    }

    private String getMixScanTaskCompleteLockKey(String groupCode, String templateCode) {
        return String.format(JyGroupSortCrossDetailCacheService.LOCK_MIX_SCAN_TASK_KEY, groupCode, templateCode);
    }

    //
    public void saveMixScanTaskCompleteCache(String groupCode, String templateCode) {
            String key = getMixScanTaskCompleteCacheKey(groupCode, templateCode);
            redisClientOfJy.setEx(key, "1",
                    JyGroupSortCrossDetailCacheService.CACHE_MIX_SCAN_TASK_TIMEOUT_HOUR,
                    TimeUnit.HOURS);
    }

    public boolean existMixScanTaskCompleteCache(String groupCode, String templateCode) {
        String key = getMixScanTaskCompleteCacheKey(groupCode, templateCode);
        return !Objects.isNull(redisClientOfJy.get(key));
    }
    private String getMixScanTaskCompleteCacheKey(String groupCode, String templateCode) {
        return String.format(JyGroupSortCrossDetailCacheService.CACHE_MIX_SCAN_TASK_KEY, groupCode, templateCode);
    }
}
