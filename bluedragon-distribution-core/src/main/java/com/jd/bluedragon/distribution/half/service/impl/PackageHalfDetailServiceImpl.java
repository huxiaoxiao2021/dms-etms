package com.jd.bluedragon.distribution.half.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.half.domain.PackageHalfDetail;
import com.jd.bluedragon.distribution.half.dao.PackageHalfDetailDao;
import com.jd.bluedragon.distribution.half.service.PackageHalfDetailService;

/**
 *
 * @ClassName: PackageHalfDetailServiceImpl
 * @Description: 包裹半收操作明细表--Service接口实现
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
@Service("packageHalfDetailService")
public class PackageHalfDetailServiceImpl extends BaseService<PackageHalfDetail> implements PackageHalfDetailService {

	@Autowired
	@Qualifier("packageHalfDetailDao")
	private PackageHalfDetailDao packageHalfDetailDao;

	@Override
	public Dao<PackageHalfDetail> getDao() {
		return this.packageHalfDetailDao;
	}

}
