package com.jd.bluedragon.distribution.jy.service.inspection;

import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.jim.cli.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author zhengchengfa
 * @Date 2024/3/8 13:56
 * @Description
 */
@Service
public class JyTrustHandoverAutoInspectionCacheService {

    private static final Logger log = LoggerFactory.getLogger(JyTrustHandoverAutoInspectionCacheService.class);

    public static final String DEFAULT_VALUE_1 = "1";
    public static final Integer DEFAULT_LOCK_TIMEOUT_SECONDS = 300;
    /**
     * 整车验货包裹缓存时间
     */
    public static final Integer DEFAULT_CACHE_TIMEOUT_MINUTES = 10;



    //场地+包裹号+操作时间
    public static final String LOCK_PACKAGE_ARRIVE_CAR_AUTO_INSPECTION = "lock:package:arrive:car:auto:inspection:%s:%s:%s";
    public static final String CACHE_PACKAGE_ARRIVE_CAR_AUTO_INSPECTION = "cache:package:arrive:car:auto:inspection:%s:%s:%s";
    //场地+物资编码+操作时间
    public static final String LOCK_RECYCLE_MATERIAL_ENTER_SITE = "lock:recycle:material:enter:site:%s:%s:%s";
    public static final String CACHE_RECYCLE_MATERIAL_ENTER_SITE = "cache:recycle:material:enter:site:%s:%s:%s";
    //场地+箱号+操作时间
    public static final String LOCK_RECYCLE_MATERIAL_ENTER_SITE_BOX_INSPECTION = "lock:recycle:material:enter:site:box:inspection:%s:%s:%s";
    public static final String CACHE_RECYCLE_MATERIAL_ENTER_SITE_BOX_INSPECTION = "cache:recycle:material:enter:site:box:inspection:%s:%s:%s";
    //场地+包裹号+操作时间
    public static final String LOCK_RECYCLE_MATERIAL_ENTER_SITE_PACKAGE_INSPECTION = "lock:recycle:material:enter:site:package:inspection:%s:%s:%s";
    public static final String CACHE_RECYCLE_MATERIAL_ENTER_SITE_PACKAGE_INSPECTION = "cache:recycle:material:enter:site:package:inspection:%s:%s:%s";


    @Autowired
    private JimDbLock jimDbLock;
    @Autowired
    @Qualifier("redisClientOfJy")
    private Cluster redisClientOfJy;


    /**
     * 包裹自动验货加锁
     * @param siteId
     * @param packageCode
     * @param operateTime
     * @return
     */
    public boolean lockPackageArriveCarAutoInspection(Integer siteId, String packageCode, Long operateTime) {
        String lockKey = this.getLockKeyPackageArriveCarAutoInspection(siteId, packageCode, operateTime);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, DEFAULT_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    public void unlockPackageArriveCarAutoInspection(Integer siteId, String packageCode, Long operateTime) {
        String lockKey = this.getLockKeyPackageArriveCarAutoInspection(siteId, packageCode, operateTime);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    public String getLockKeyPackageArriveCarAutoInspection(Integer siteId, String packageCode, Long operateTime) {
        return String.format(LOCK_PACKAGE_ARRIVE_CAR_AUTO_INSPECTION, packageCode, siteId, operateTime);
    }


    /**
     * 包裹自动验货防重
     * @param siteId
     * @param packageCode
     * @param operateTime
     * @return
     */
    public void saveCachePackageArriveCarAutoInspection(Integer siteId, String packageCode, Long operateTime) {
        String lockKey = this.getCacheKeyPackageArriveCarAutoInspection(siteId, packageCode, operateTime);
        redisClientOfJy.setEx(lockKey, DEFAULT_VALUE_1, DEFAULT_CACHE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }
    public Boolean existCachePackageArriveCarAutoInspection(Integer siteId, String packageCode, Long operateTime) {
        String lockKey = this.getCacheKeyPackageArriveCarAutoInspection(siteId, packageCode, operateTime);
        return redisClientOfJy.exists(lockKey);
    }
    public String getCacheKeyPackageArriveCarAutoInspection(Integer siteId, String packageCode, Long operateTime) {
        return String.format(CACHE_PACKAGE_ARRIVE_CAR_AUTO_INSPECTION, packageCode, siteId, operateTime);
    }


    /**
     * 循环物资进场消息加锁
     * @param siteId
     * @param materialCode
     * @param operateTime
     * @return
     */
    public boolean lockRecycleMaterialEnterSite(Integer siteId, String materialCode, Long operateTime) {
        String lockKey = this.getLockKeyRecycleMaterialEnterSite(siteId, materialCode, operateTime);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, DEFAULT_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    public void unlockRecycleMaterialEnterSite(Integer siteId, String materialCode, Long operateTime) {
        String lockKey = this.getLockKeyRecycleMaterialEnterSite(siteId, materialCode, operateTime);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    public String getLockKeyRecycleMaterialEnterSite(Integer siteId, String materialCode, Long operateTime) {
        return String.format(LOCK_RECYCLE_MATERIAL_ENTER_SITE, materialCode, siteId, operateTime);
    }


    /**
     * 循环物资进场消息防重
     * @param siteId
     * @param materialCode
     * @param operateTime
     * @return
     */
    public void saveCacheRecycleMaterialEnterSite(Integer siteId, String materialCode, Long operateTime) {
        String lockKey = this.getCacheKeyRecycleMaterialEnterSite(siteId, materialCode, operateTime);
        redisClientOfJy.setEx(lockKey, DEFAULT_VALUE_1, DEFAULT_CACHE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }
    public Boolean existCacheRecycleMaterialEnterSite(Integer siteId, String materialCode, Long operateTime) {
        String lockKey = this.getCacheKeyRecycleMaterialEnterSite(siteId, materialCode, operateTime);
        return redisClientOfJy.exists(lockKey);
    }
    public String getCacheKeyRecycleMaterialEnterSite(Integer siteId, String materialCode, Long operateTime) {
        return String.format(CACHE_RECYCLE_MATERIAL_ENTER_SITE, materialCode, siteId, operateTime);
    }



    /**
     * 循环物资进场箱自动验货消息加锁
     * @param siteId
     * @param boxCode
     * @param operateTime
     * @return
     */
    public boolean lockRecycleMaterialEnterSiteBoxInspection(Integer siteId, String boxCode, Long operateTime) {
        String lockKey = this.getLockKeyRecycleMaterialEnterSiteBoxInspection(siteId, boxCode, operateTime);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, DEFAULT_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    public void unlockRecycleMaterialEnterSiteBoxInspection(Integer siteId, String boxCode, Long operateTime) {
        String lockKey = this.getLockKeyRecycleMaterialEnterSiteBoxInspection(siteId, boxCode, operateTime);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    public String getLockKeyRecycleMaterialEnterSiteBoxInspection(Integer siteId, String boxCode, Long operateTime) {
        return String.format(LOCK_RECYCLE_MATERIAL_ENTER_SITE_BOX_INSPECTION, boxCode, siteId, operateTime);
    }


    /**
     * 循环物资进场箱自动验货消息防重
     * @param siteId
     * @param boxCode
     * @param operateTime
     * @return
     */
    public void saveCacheRecycleMaterialEnterSiteBoxInspection(Integer siteId, String boxCode, Long operateTime) {
        String lockKey = this.getCacheKeyRecycleMaterialEnterSiteBoxInspection(siteId, boxCode, operateTime);
        redisClientOfJy.setEx(lockKey, DEFAULT_VALUE_1, DEFAULT_CACHE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }
    public Boolean existCacheRecycleMaterialEnterSiteBoxInspection(Integer siteId, String boxCode, Long operateTime) {
        String lockKey = this.getCacheKeyRecycleMaterialEnterSiteBoxInspection(siteId, boxCode, operateTime);
        return redisClientOfJy.exists(lockKey);
    }
    public String getCacheKeyRecycleMaterialEnterSiteBoxInspection(Integer siteId, String boxCode, Long operateTime) {
        return String.format(CACHE_RECYCLE_MATERIAL_ENTER_SITE_BOX_INSPECTION, boxCode, siteId, operateTime);
    }


    /**
     * 循环物资进场箱内包裹自动验货消息加锁
     * @param siteId
     * @param packageCode
     * @param operateTime
     * @return
     */
    public boolean lockRecycleMaterialEnterSitePackageInspection(Integer siteId, String packageCode, Long operateTime) {
        String lockKey = this.getLockKeyRecycleMaterialEnterSitePackageInspection(siteId, packageCode, operateTime);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, DEFAULT_LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
    public void unlockRecycleMaterialEnterSitePackageInspection(Integer siteId, String packageCode, Long operateTime) {
        String lockKey = this.getLockKeyRecycleMaterialEnterSitePackageInspection(siteId, packageCode, operateTime);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    public String getLockKeyRecycleMaterialEnterSitePackageInspection(Integer siteId, String packageCode, Long operateTime) {
        return String.format(LOCK_RECYCLE_MATERIAL_ENTER_SITE_PACKAGE_INSPECTION, packageCode, siteId, operateTime);
    }


    /**
     * 循环物资进场箱内包裹自动验货消息防重
     * @param siteId
     * @param packageCode
     * @param operateTime
     * @return
     */
    public void saveCacheRecycleMaterialEnterSitePackageInspection(Integer siteId, String packageCode, Long operateTime) {
        String lockKey = this.getCacheKeyRecycleMaterialEnterSitePackageInspection(siteId, packageCode, operateTime);
        redisClientOfJy.setEx(lockKey, DEFAULT_VALUE_1, DEFAULT_CACHE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
    }
    public Boolean existCacheRecycleMaterialEnterSitePackageInspection(Integer siteId, String packageCode, Long operateTime) {
        String lockKey = this.getCacheKeyRecycleMaterialEnterSitePackageInspection(siteId, packageCode, operateTime);
        return redisClientOfJy.exists(lockKey);
    }
    public String getCacheKeyRecycleMaterialEnterSitePackageInspection(Integer siteId, String packageCode, Long operateTime) {
        return String.format(CACHE_RECYCLE_MATERIAL_ENTER_SITE_PACKAGE_INSPECTION, packageCode, siteId, operateTime);
    }


}
