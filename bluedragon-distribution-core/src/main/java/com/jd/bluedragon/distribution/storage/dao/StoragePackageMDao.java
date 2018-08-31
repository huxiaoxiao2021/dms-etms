package com.jd.bluedragon.distribution.storage.dao;

import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
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

    int updateStoragePackageMStatusForSendOfPerformanceCode(String performanceCode);

    int updateStoragePackageMStatusForSendOfWaybill(String waybillCode);

    int updateStoragePackageMStatusForSendOfPackage(String waybillCode);




}
