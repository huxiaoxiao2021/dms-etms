package com.jd.bluedragon.distribution.newseal.service;

import java.util.List;

import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelation;
import com.jd.bluedragon.distribution.newseal.entity.DmsSendRelationCondition;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRoute;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRouteCondition;

/**
 * @ClassName: TmsVehicleRouteService
 * @Description: 运输任务线路表--Service接口
 * @author wuyoude
 * @date 2020年12月31日 21:24:16
 *
 */
public interface TmsVehicleRouteService {
	/**
	 * 插入一条数据
	 * @param tmsVehicleRoute
	 * @return
	 */
	boolean insert(TmsVehicleRoute tmsVehicleRoute);
	/**
	 * 更新一条数据
	 * @param tmsVehicleRoute
	 * @return
	 */
	boolean update(TmsVehicleRoute tmsVehicleRoute);
	/**
	 * 逻辑删除一条数据
	 * @param tmsVehicleRoute
	 * @return
	 */
	boolean logicalDeleteById(Long id);
	/**
	 * 根据车次任务编码查询数据
	 * @param vehicleRouteCode
	 * @return
	 */
	TmsVehicleRoute queryByVehicleRouteCode(String vehicleRouteCode);
	/**
	 * 同步数据
	 * @param tmsVehicleRoute
	 */
	boolean syncToDb(TmsVehicleRoute tmsVehicleRoute);
	/**
	 * 查询满足条件的数据
	 * @param dmsSendRelation
	 * @return
	 */
	List<TmsVehicleRoute> queryByCondition(TmsVehicleRouteCondition condition);
}
