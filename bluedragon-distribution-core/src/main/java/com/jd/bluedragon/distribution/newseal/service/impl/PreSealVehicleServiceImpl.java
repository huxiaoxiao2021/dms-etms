package com.jd.bluedragon.distribution.newseal.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.dao.PreSealVehicleDao;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;

/**
 *
 * @ClassName: PreSealVehicleServiceImpl
 * @Description: 预封车数据表--Service接口实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Service("preSealVehicleService")
public class PreSealVehicleServiceImpl extends BaseService<PreSealVehicle> implements PreSealVehicleService {

	@Autowired
	@Qualifier("preSealVehicleDao")
	private PreSealVehicleDao preSealVehicleDao;

	@Override
	public Dao<PreSealVehicle> getDao() {
		return this.preSealVehicleDao;
	}

}
