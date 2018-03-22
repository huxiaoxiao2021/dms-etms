package com.jd.bluedragon.distribution.base.service.impl;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.b2bRouter.domain.ErpUserClient;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.dao.DmsStorageAreaDao;
import com.jd.bluedragon.distribution.base.service.DmsStorageAreaService;

import java.util.Date;

/**
 *
 * @ClassName: DmsStorageAreaServiceImpl
 * @Description: 分拣中心库位--Service接口实现
 * @author wuyoude
 * @date 2018年03月13日 16:25:45
 *
 */
@Service("dmsStorageAreaService")
public class DmsStorageAreaServiceImpl extends BaseService<DmsStorageArea> implements DmsStorageAreaService {

	@Autowired
	@Qualifier("dmsStorageAreaDao")
	private DmsStorageAreaDao dmsStorageAreaDao;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Override
	public Dao<DmsStorageArea> getDao() {
		return this.dmsStorageAreaDao;
	}

	public DmsStorageArea findByProAndCity(DmsStorageArea dmsStorageArea){

		return dmsStorageAreaDao.findByProAndCity(dmsStorageArea);
	}

	public  DmsStorageArea getUserInfo(DmsStorageArea dmsStorageArea){
		//获得登陆人信息
		ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
		BaseStaffSiteOrgDto baseStaffByErpNoCache = baseMajorManager.getBaseStaffByErpNoCache(erpUser.getUserCode());
		dmsStorageArea.setCreateUserName(erpUser.getUserName());
		dmsStorageArea.setDmsSiteCode(baseStaffByErpNoCache.getSiteCode());
		dmsStorageArea.setDmsSiteName(baseStaffByErpNoCache.getSiteName());
		dmsStorageArea.setCreateTime(new Date());
		return dmsStorageArea;
	}


}
