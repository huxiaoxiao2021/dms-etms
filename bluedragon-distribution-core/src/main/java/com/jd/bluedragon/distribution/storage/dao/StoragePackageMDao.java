package com.jd.bluedragon.distribution.storage.dao;

import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageMCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: StoragePackageMDao
 * @Description: 储位包裹主表--Dao接口
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
public interface StoragePackageMDao extends Dao<StoragePackageM> {

    int updateForceSendByPerformanceCodes(List<String> ids);

    StoragePackageM queryByWaybillCode(String waybillCode);

    List<StoragePackageM> queryByPerformanceCode(String performanceCode);

    int updatePutawayPackageSum(StoragePackageM storagePackageM);
    /**
     * 更新运单的暂存状态为可发货
     * @param waybillCode
     * @return
     */
    int updateStoragePackageMStatusForCanSendOfPerformanceCode(String performanceCode);
    /**
     * 更新运单的暂存状态为可发货
     * @param waybillCode
     * @return
     */
    int updateStoragePackageMStatusForCanSendOfWaybill(String waybillCode);
    /**
     * 更新运单的暂存状态为可发货
     * @param waybillCode
     * @return
     */
    int updateStoragePackageMStatusForCanSendOfPackage(String waybillCode);

    /**
     * 更新运单的暂存状态为已发货
     * @param waybillCode
     * @return
     */
    int updateStoragePackageMStatusForBeSendOfPWaybill(String waybillCode);

    /**
     * 根据条件导出
     * @param condition
     * @return
     */
    List<StoragePackageM> queryExportByCondition(StoragePackageMCondition condition);

    /**
     * 更新储位号
     *  只针对快运暂存
     * @param storagePackageM
     * @return
     */
    int updateKYStorageCode(StoragePackageM storagePackageM);

    /**
     * 更新全部下架时间
     * @param waybillCode
     * @return
     */
    int updateDownAwayTimeByWaybillCode(String waybillCode);
}
