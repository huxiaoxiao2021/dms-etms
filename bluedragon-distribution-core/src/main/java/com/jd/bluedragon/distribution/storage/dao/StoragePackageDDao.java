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


    int cancelPutaway(String waybillCode);

    /**
     * 通过包裹号更新储位号
     *  只针对快运暂存
     * @param storagePackageD
     * @return
     */
    int updateKYStorageCodeByPackageCode(StoragePackageD storagePackageD);

    /**
     * 通过运单号更新储位号
     *  只针对快运暂存
     * @param storagePackageD
     * @return
     */
    int updateKYStorageCodeByWaybillCode(StoragePackageD storagePackageD);

    /**
     * 通过运单号和分拣中心号查询储位信息
     * @param storagePackageD
     * @return
     */
    List<String> findStorageCodeByWaybillCodeAndSiteCode(StoragePackageD storagePackageD);

    /**
     * 查询未发货包裹暂存明细
     * @param waybillCode
     * @return
     */
    List<StoragePackageD> queryUnSendByWaybill(String waybillCode);

    /**
     * 更新包裹发货时间
     * @param packageCode
     * @return
     */
    int updateSendTimeByPackageCode(String packageCode);
}
