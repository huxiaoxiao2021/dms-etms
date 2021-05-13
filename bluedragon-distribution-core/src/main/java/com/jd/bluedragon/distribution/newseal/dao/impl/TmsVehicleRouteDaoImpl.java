package com.jd.bluedragon.distribution.newseal.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.newseal.dao.TmsVehicleRouteDao;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRoute;

/**
 * @ClassName: TmsVehicleRouteDaoImpl
 * @Description: 运输任务线路表--Dao接口实现
 * @author wuyoude
 * @date 2020年12月31日 21:24:16
 *
 */
@Repository("tmsVehicleRouteDao")
public class TmsVehicleRouteDaoImpl extends BaseDao<TmsVehicleRoute> implements TmsVehicleRouteDao {
	
	public static final String namespace = TmsVehicleRouteDao.class.getName();
	@Override
	public int insert(TmsVehicleRoute tmsVehicleRoute) {
		return this.getSqlSession().insert(namespace + ".insert", tmsVehicleRoute);
	}

	@Override
	public int update(TmsVehicleRoute tmsVehicleRoute) {
		return this.getSqlSession().insert(namespace + ".update", tmsVehicleRoute);
	}

	@Override
	public int logicalDeleteById(Long id) {
		return this.getSqlSession().update(namespace + ".logicalDeleteById", id);
	}

	@Override
	public TmsVehicleRoute queryByVehicleRouteCode(String vehicleRouteCode) {
		return this.getSqlSession().selectOne(namespace + ".queryByVehicleRouteCode", vehicleRouteCode);
	}
}
