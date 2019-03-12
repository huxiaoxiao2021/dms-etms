package com.jd.bluedragon.distribution.newseal.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.bluedragon.distribution.newseal.dao.SealVehiclesDao;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;

/**
 *
 * @ClassName: SealVehiclesServiceImpl
 * @Description: 封车数据表--Service接口实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Service("sealVehiclesService")
public class SealVehiclesServiceImpl extends BaseService<SealVehicles> implements SealVehiclesService {

	@Autowired
	@Qualifier("sealVehiclesDao")
	private SealVehiclesDao sealVehiclesDao;

	@Override
	public Dao<SealVehicles> getDao() {
		return this.sealVehiclesDao;
	}

}
