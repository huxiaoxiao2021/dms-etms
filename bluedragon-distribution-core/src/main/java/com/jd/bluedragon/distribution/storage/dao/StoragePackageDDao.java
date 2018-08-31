package com.jd.bluedragon.distribution.storage.dao;

import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: StoragePackageDDao
 * @Description: 储位包裹明细表--Dao接口
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
public interface StoragePackageDDao extends Dao<StoragePackageD> {

    /**
     * 通过运单号获取最近一条暂存明细数据
     * @param waybillCode
     * @return
     */
    StoragePackageD findLastStoragePackageDByWaybillCode(String waybillCode);

    /**
     * 通过包裹号获取最近一条暂存明细数据
     * @param waybillCode
     * @return
     */
    StoragePackageD findLastStoragePackageDByPackageCode(String packageCode);

    /**
     * 通过履约单号获取最近一条暂存明细数据
     * @param waybillCode
     * @return
     */
    StoragePackageD findLastStoragePackageDByPerformanceCode(String performanceCode);


    List<StoragePackageD> findByWaybill(String waybillCode);

}
