package com.jd.bluedragon.distribution.newseal.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRoute;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRouteCondition;

/**
 * @ClassName: TmsVehicleRouteDao
 * @Description: 运输任务线路表--Dao接口
 * @author wuyoude
 * @date 2020年12月31日 21:24:16
 *
 */
@Repository("tmsVehicleRouteDao")
public interface TmsVehicleRouteDao {

	int insert(TmsVehicleRoute tmsVehicleRoute);

	int update(TmsVehicleRoute tmsVehicleRoute);

	int logicalDeleteById(Long id);

	TmsVehicleRoute queryByVehicleRouteCode(String vehicleRouteCode);

	List<TmsVehicleRoute> queryByCondition(TmsVehicleRouteCondition condition);
}
