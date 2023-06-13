package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.jim.cli.Cluster;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class JyWarehouseSendVehicleCacheService {
    private static final Logger log = LoggerFactory.getLogger(JyWarehouseSendVehicleCacheService.class);

    public static final String DEFAULT_VALUE_1 = "1";

    /**
     * K:bizId V：erp
     */
    public static final String LOCK_CANCEL_SCAN_ALL_SELECT_PROTECT = "lock:warehouse:cancelScan:allSelect:%s";
    public static final int LOCK_CANCEL_SCAN_ALL_SELECT_PROTECT_TIMEOUT_SECONDS = 2;


    @Autowired
    private JimDbLock jimDbLock;
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;


    /**
     * 接货仓发货岗取消扫描全选防刷保护
     * @param bizId  任务bizId
     */
    public boolean lockCacheCancelScanAllSelectProtect(String bizId, String erp) {
        try {
            String lockKey = this.getLockKeyCancelScanAllSelectProtect(bizId);
            return jimDbLock.lock(lockKey,
                    erp,
                    JyWarehouseSendVehicleCacheService.LOCK_CANCEL_SCAN_ALL_SELECT_PROTECT_TIMEOUT_SECONDS,
                    TimeUnit.SECONDS);
        }catch (Exception e) {
            log.error("lockCacheCancelScanAllSelectProtect，bizId={}", bizId, e);
        }
        //该锁用作全选防刷，加锁失败不拦截
        return true;
    }


    /**
     * 获取接货仓发货岗取消扫描全选防刷保护key
     * @param bizId
     * @return
     */
    private String getLockKeyCancelScanAllSelectProtect(String bizId) {
        return String.format(LOCK_CANCEL_SCAN_ALL_SELECT_PROTECT, bizId);
    }



}
