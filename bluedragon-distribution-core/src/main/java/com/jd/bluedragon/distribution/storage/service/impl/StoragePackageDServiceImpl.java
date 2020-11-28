package com.jd.bluedragon.distribution.storage.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.bluedragon.distribution.storage.dao.StoragePackageDDao;
import com.jd.bluedragon.distribution.storage.service.StoragePackageDService;

import java.util.List;

/**
 *
 * @ClassName: StoragePackageDServiceImpl
 * @Description: 储位包裹明细表--Service接口实现
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
@Service("storagePackageDService")
public class StoragePackageDServiceImpl extends BaseService<StoragePackageD> implements StoragePackageDService {

	@Autowired
	@Qualifier("storagePackageDDao")
	private StoragePackageDDao storagePackageDDao;

	@Override
	public Dao<StoragePackageD> getDao() {
		return this.storagePackageDDao;
	}

	@Override
	public List<StoragePackageD> queryByWaybill(String waybillCode) {
		return storagePackageDDao.findByWaybill(waybillCode);
	}

	@Override
	public int cancelPutaway(String waybillCode) {
		return storagePackageDDao.cancelPutaway(waybillCode);
	}

	@Override
	public List<String> queryStorageCodeByWaybillCodeAndSiteCode(String waybillCode, Long destDmsSiteCode) {
		StoragePackageD storagePackageD=new StoragePackageD();
		storagePackageD.setWaybillCode(waybillCode);
		storagePackageD.setCreateSiteCode(destDmsSiteCode);
		return storagePackageDDao.findStorageCodeByWaybillCodeAndSiteCode(storagePackageD);
	}


}
