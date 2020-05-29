package com.jd.bluedragon.distribution.storage.dao.impl;

import com.jd.bluedragon.distribution.storage.dao.StoragePackageMDao;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageMCondition;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: StoragePackageMDaoImpl
 * @Description: 储位包裹主表--Dao接口实现
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */

public class StoragePackageMDaoImpl extends BaseDao<StoragePackageM> implements StoragePackageMDao {


    @Override
    public int updateForceSendByPerformanceCodes(List<String> performanceCodes) {
        if(performanceCodes != null && performanceCodes.size() > 0){
            return sqlSession.update(this.nameSpace+".updateForceSendByPerformanceCodes", performanceCodes);
        }
        return 0;
    }

    @Override
    public StoragePackageM queryByWaybillCode(String waybillCode) {
        return sqlSession.selectOne(this.nameSpace+".queryByWaybillCode", waybillCode);
    }

    @Override
    public List<StoragePackageM> queryByPerformanceCode(String performanceCode) {
        return sqlSession.selectList(this.nameSpace+".queryByPerformanceCode", performanceCode);
    }


    @Override
    public int updatePutawayPackageSum(StoragePackageM storagePackageM) {
        return sqlSession.update(this.nameSpace+".updatePutawayPackageSum", storagePackageM);
    }

    @Override
    public int updateStoragePackageMStatusForCanSendOfPerformanceCode(String performanceCode) {
        return sqlSession.update(this.nameSpace+".updateStoragePackageMStatusForSendOfPerformanceCode", performanceCode);
    }

    @Override
    public int updateStoragePackageMStatusForCanSendOfWaybill(String waybillCode) {
        return sqlSession.update(this.nameSpace+".updateStoragePackageMStatusForSendOfWaybill", waybillCode);
    }

    @Override
    public int updateStoragePackageMStatusForCanSendOfPackage(String waybillCode) {
        return sqlSession.update(this.nameSpace+".updateStoragePackageMStatusForSendOfPackage", waybillCode);
    }

    @Override
    public int updateStoragePackageMStatusForBeSendOfPWaybill(String waybillCode) {
        return sqlSession.update(this.nameSpace+".updateStoragePackageMStatusForBeSendOfPWaybill", waybillCode);
    }

    @Override
    public List<StoragePackageM> queryExportByCondition(StoragePackageMCondition condition) {
        return sqlSession.selectList(this.nameSpace+".queryExportByCondition", condition);
    }

    @Override
    public int updateKYStorageCode(StoragePackageM storagePackageM) {
        return sqlSession.update(this.nameSpace+".updateKYStorageCode", storagePackageM);
    }

    @Override
    public int updateDownAwayTimeByWaybillCode(String waybillCode) {
        return sqlSession.update(this.nameSpace+".updateDownAwayTimeByWaybillCode", waybillCode);
    }
}
