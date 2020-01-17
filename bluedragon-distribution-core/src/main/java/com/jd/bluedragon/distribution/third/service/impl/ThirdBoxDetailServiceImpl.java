package com.jd.bluedragon.distribution.third.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.dao.ThirdBoxDetailDao;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 * @ClassName: ThirdBoxDetailServiceImpl
 * @Description: 三方装箱明细表--Service接口实现
 * @author wuyoude
 * @date 2020年01月07日 16:34:04
 *
 */
@Service("thirdBoxDetailService")
public class ThirdBoxDetailServiceImpl extends BaseService<ThirdBoxDetail> implements ThirdBoxDetailService {

	@Autowired
	@Qualifier("thirdBoxDetailDao")
	private ThirdBoxDetailDao thirdBoxDetailDao;

	@Override
	public Dao<ThirdBoxDetail> getDao() {
		return this.thirdBoxDetailDao;
	}


	/**
	 * 建立箱-包关系
	 *
	 * @param detail 箱-包关系
	 * @return 结果
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public boolean sorting(ThirdBoxDetail detail) {
		return thirdBoxDetailDao.insert(detail);
	}

	/**
	 * 取消某一包裹的绑定关系
	 *
	 * @param detail 明细
	 * @return 结果
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	@Override
	public boolean cancel(ThirdBoxDetail detail) {
		return thirdBoxDetailDao.cancel(detail);
	}

	/**
	 * 查询箱子明细
	 *
	 * @param tenantCode 租户编码
	 * @param startSiteId 始发站点
	 * @param boxCode     箱号
	 * @return 结果集
	 */
	@Override
	public List<ThirdBoxDetail> queryByBoxCode(String tenantCode, Integer startSiteId, String boxCode) {
		return thirdBoxDetailDao.queryByBoxCode(tenantCode, startSiteId, boxCode);
	}
}
