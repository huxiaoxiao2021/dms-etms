package com.jd.bluedragon.distribution.half.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.half.domain.PackageHalf;
import com.jd.bluedragon.distribution.half.dao.PackageHalfDao;
import com.jd.bluedragon.distribution.half.service.PackageHalfService;

/**
 *
 * @ClassName: PackageHalfServiceImpl
 * @Description: 包裹半收操作--Service接口实现
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
@Service("packageHalfService")
public class PackageHalfServiceImpl extends BaseService<PackageHalf> implements PackageHalfService {

	@Autowired
	@Qualifier("packageHalfDao")
	private PackageHalfDao packageHalfDao;

	@Override
	public Dao<PackageHalf> getDao() {
		return this.packageHalfDao;
	}

}
