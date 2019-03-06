package com.jd.bluedragon.utils.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.utils.SpringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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

    private static final Log logger = LogFactory.getLog(BigWaybillPackageListCache.class);

    /**
     * 大包裹运单限制，如果超过该值需进行缓存
     */
    public final static int BIG_WAYBILL_PACKAGE_LIMIT = 1000;

    /**
     * 对象缓存超时时间 - 180s
     */
    private static long EXPIRE_TIME_SECOND = 180;

    /**
     * 最多缓存对象个数
     */
    private static int MAXIMUM_SIZE = 256;

    /**
     * 本地内存缓存，如果缓存不存在则直接
     */
    private static LoadingCache<String, List<DeliveryPackageD>> localCache = CacheBuilder.newBuilder()
            .maximumSize(MAXIMUM_SIZE).expireAfterAccess(EXPIRE_TIME_SECOND, TimeUnit.SECONDS)
            .build(new CacheLoader<String, List<DeliveryPackageD>>() {
                @Override
                public List<DeliveryPackageD> load(String key) throws Exception {
                    WaybillPackageManager waybillPackageManager = (WaybillPackageManager) SpringHelper.getBean("waybillPackageManager");
                    if (waybillPackageManager != null) {
                        // 根据运单号获取包裹信息
                        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCode(key);
                        if (baseEntity.getResultCode() == 1) {
                            return baseEntity.getData();
                        } else {
                            logger.error("[大包裹运单缓存]调用运单waybillPackageManager.getPackListByWaybillCode接口获取包裹信息失败[ResultCode:" + baseEntity.getResultCode() + "，Message:" + baseEntity.getMessage() + "]");
                            throw new RuntimeException("[大包裹运单缓存]调用运单waybillPackageManager.getPackListByWaybillCode接口获取包裹信息失败[ResultCode:" + baseEntity.getResultCode() + "，Message:" + baseEntity.getMessage() + "]");
                        }
                    } else {
                        logger.error("[大包裹运单缓存]获取WaybillPackageManager的Spring bean对象为空，无法获取包裹信息，缓存失败");
                        throw new RuntimeException("[大包裹运单缓存]获取WaybillPackageManager的Spring bean对象为空，无法获取包裹信息，缓存失败");
                    }
                }
            });

    /**
     * 根据
     *
     * @param waybillCode
     * @return
     * @throws ExecutionException
     */
    public static List<DeliveryPackageD> getPackageListFromCache(String waybillCode) throws ExecutionException {
        return localCache.get(waybillCode);
    }
}
