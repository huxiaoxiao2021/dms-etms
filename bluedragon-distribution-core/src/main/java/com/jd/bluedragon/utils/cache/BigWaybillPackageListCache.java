package com.jd.bluedragon.utils.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author lixin39
 * @Description 大运单对象包裹列表本地缓存
 * @ClassName BigWaybillPackageListCache
 * @date 2019/3/5
 */
public class BigWaybillPackageListCache {

    private static final Logger log = LoggerFactory.getLogger(BigWaybillPackageListCache.class);

    /**
     * 大包裹运单限制，如果超过该值需进行缓存
     */
    public final static int BIG_WAYBILL_PACKAGE_LIMIT = 1000;

    /**
     * 对象缓存超时时间 - 300s
     */
    private static long EXPIRE_TIME_SECOND = 300;

    /**
     * 最多缓存对象个数
     */
    private static int MAXIMUM_SIZE = 64;

    /**
     * 本地内存缓存，如果缓存不存在则直接
     */
    private static LoadingCache<String, List<DeliveryPackageD>> localCache = CacheBuilder.newBuilder()
            .expireAfterAccess(EXPIRE_TIME_SECOND, TimeUnit.SECONDS)
            .maximumSize(MAXIMUM_SIZE)
            .concurrencyLevel(10)
            .softValues()
            .build(new CacheLoader<String, List<DeliveryPackageD>>() {
                @Override
                public List<DeliveryPackageD> load(String key) throws Exception {
                    log.info("大运单包裹缓存[{}]，未命中或已过期，调用运单接口，缓存当前大小:{}",key, localCache.size());
                    WaybillPackageManager waybillPackageManager = (WaybillPackageManager) SpringHelper.getBean("waybillPackageManager");
                    if (waybillPackageManager != null) {
                        // 根据运单号获取包裹信息
                        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCode(key);
                        if (baseEntity.getResultCode() == 1) {
                            return baseEntity.getData();
                        } else {
                            log.warn("[大包裹运单缓存]调用运单waybillPackageManager.getPackListByWaybillCode接口获取包裹信息失败[ResultCode:{}，Message:{}]",baseEntity.getResultCode(), baseEntity.getMessage());
                            throw new RuntimeException("[大包裹运单缓存]调用运单waybillPackageManager.getPackListByWaybillCode接口获取包裹信息失败[ResultCode:" + baseEntity.getResultCode() + "，Message:" + baseEntity.getMessage() + "]");
                        }
                    } else {
                        log.error("[大包裹运单缓存]获取WaybillPackageManager的Spring bean对象为空，无法获取包裹信息，缓存失败");
                        throw new RuntimeException("[大包裹运单缓存]获取WaybillPackageManager的Spring bean对象为空，无法获取包裹信息，缓存失败");
                    }
                }
            });

    /**
     * 根据运单号从缓存中获取包裹信息
     *
     * @param waybillCode
     * @return
     * @throws ExecutionException
     */
    public static List<DeliveryPackageD> getPackageListFromCache(String waybillCode) throws ExecutionException {
        CallerInfo info = Profiler.registerInfo("DMSWORKER.BigWaybillPackageListCache.getPackageListFromCache", Constants.UMP_APP_NAME_DMSWORKER, false, true);
        log.info("大运单包裹缓存[{}]，取内存缓存，缓存当前大小:{}" ,waybillCode, localCache.size());
        List<DeliveryPackageD> packageList = localCache.get(waybillCode);
        Profiler.registerInfoEnd(info);
        return packageList;
    }
}
