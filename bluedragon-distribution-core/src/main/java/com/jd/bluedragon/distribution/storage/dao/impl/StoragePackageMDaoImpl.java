package com.jd.bluedragon.distribution.storage.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageMDao;
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
    public int updateStoragePackageMStatusForSendOfPerformanceCode(String performanceCode) {
        return sqlSession.update(this.nameSpace+".updateStoragePackageMStatusForSendOfPerformanceCode", performanceCode);
    }

    @Override
    public int updateStoragePackageMStatusForSendOfWaybill(String waybillCode) {
        return sqlSession.update(this.nameSpace+".updateStoragePackageMStatusForSendOfWaybill", waybillCode);
    }

    @Override
    public int updateStoragePackageMStatusForSendOfPackage(String waybillCode) {
        return sqlSession.update(this.nameSpace+".updateStoragePackageMStatusForSendOfPackage", waybillCode);
    }


}
