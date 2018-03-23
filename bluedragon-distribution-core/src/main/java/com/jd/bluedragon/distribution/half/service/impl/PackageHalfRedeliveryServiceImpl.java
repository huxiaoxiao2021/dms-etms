package com.jd.bluedragon.distribution.half.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.half.domain.PackageHalfRedelivery;
import com.jd.bluedragon.distribution.half.dao.PackageHalfRedeliveryDao;
import com.jd.bluedragon.distribution.half.service.PackageHalfRedeliveryService;

/**
 *
 * @ClassName: PackageHalfRedeliveryServiceImpl
 * @Description: 包裹半收协商再投业务表--Service接口实现
 * @author wuyoude
 * @date 2018年03月23日 17:40:03
 *
 */
@Service("packageHalfRedeliveryService")
public class PackageHalfRedeliveryServiceImpl extends BaseService<PackageHalfRedelivery> implements PackageHalfRedeliveryService {

	@Autowired
	@Qualifier("packageHalfRedeliveryDao")
	private PackageHalfRedeliveryDao packageHalfRedeliveryDao;

	@Override
	public Dao<PackageHalfRedelivery> getDao() {
		return this.packageHalfRedeliveryDao;
	}

}
