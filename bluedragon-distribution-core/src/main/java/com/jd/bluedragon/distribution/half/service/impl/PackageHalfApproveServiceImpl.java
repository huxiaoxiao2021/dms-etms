package com.jd.bluedragon.distribution.half.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.half.domain.PackageHalfApprove;
import com.jd.bluedragon.distribution.half.dao.PackageHalfApproveDao;
import com.jd.bluedragon.distribution.half.service.PackageHalfApproveService;

/**
 *
 * @ClassName: PackageHalfApproveServiceImpl
 * @Description: 包裹半收协商再投终端提交业务表--Service接口实现
 * @author wuyoude
 * @date 2018年04月11日 18:45:19
 *
 */
@Service("packageHalfApproveService")
public class PackageHalfApproveServiceImpl extends BaseService<PackageHalfApprove> implements PackageHalfApproveService {

	@Autowired
	@Qualifier("packageHalfApproveDao")
	private PackageHalfApproveDao packageHalfApproveDao;

	@Override
	public Dao<PackageHalfApprove> getDao() {
		return this.packageHalfApproveDao;
	}

}
