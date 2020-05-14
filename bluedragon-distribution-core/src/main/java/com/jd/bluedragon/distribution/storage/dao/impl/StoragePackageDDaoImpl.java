package com.jd.bluedragon.distribution.storage.dao.impl;

import com.jd.bluedragon.distribution.storage.dao.StoragePackageDDao;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: StoragePackageDDaoImpl
 * @Description: 储位包裹明细表--Dao接口实现
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */

public class StoragePackageDDaoImpl extends BaseDao<StoragePackageD> implements StoragePackageDDao {


    @Override
    public StoragePackageD findLastStoragePackageDByWaybillCode(String waybillCode) {
        return sqlSession.selectOne(this.nameSpace+".findLastStoragePackageDByWaybillCode", waybillCode);
    }

    @Override
    public StoragePackageD findLastStoragePackageDByPackageCode(String packageCode) {
        return sqlSession.selectOne(this.nameSpace+".findLastStoragePackageDByPackageCode", packageCode);
    }

    @Override
    public StoragePackageD findLastStoragePackageDByPerformanceCode(String performanceCode) {
        return sqlSession.selectOne(this.nameSpace+".findLastStoragePackageDByPerformanceCode", performanceCode);
    }

    @Override
    public List<StoragePackageD> findByWaybill(String waybillCode) {
        return sqlSession.selectList(this.nameSpace+".findByWaybill", waybillCode);
    }

    @Override
    public int cancelPutaway(String waybillCode) {
        return sqlSession.update(this.nameSpace+".cancelPutaway", waybillCode);
    }

    @Override
    public int updateKYStorageCodeByPackageCode(StoragePackageD storagePackageD) {
        return sqlSession.update(this.nameSpace+".updateKYStorageCodeByPackageCode", storagePackageD);
    }

    @Override
    public int updateKYStorageCodeByWaybillCode(StoragePackageD storagePackageD) {
        return sqlSession.update(this.nameSpace+".updateKYStorageCodeByWaybillCode", storagePackageD);
    }
}
