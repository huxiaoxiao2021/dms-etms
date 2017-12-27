package com.jd.bluedragon.distribution.transport.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.transport.domain.ArBookingSpace;
import com.jd.bluedragon.distribution.transport.dao.ArBookingSpaceDao;
import com.jd.bluedragon.distribution.transport.service.ArBookingSpaceService;

/**
 *
 * @ClassName: ArBookingSpaceServiceImpl
 * @Description: 空铁项目-订舱表--Service接口实现
 * @author wuyoude
 * @date 2017年12月28日 09:46:12
 *
 */
@Service("arBookingSpaceService")
public class ArBookingSpaceServiceImpl extends BaseService<ArBookingSpace> implements ArBookingSpaceService {

	@Autowired
	@Qualifier("arBookingSpaceDao")
	private ArBookingSpaceDao arBookingSpaceDao;

	@Override
	public Dao<ArBookingSpace> getDao() {
		return this.arBookingSpaceDao;
	}

}
