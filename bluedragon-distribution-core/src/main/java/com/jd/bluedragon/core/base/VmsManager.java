package com.jd.bluedragon.core.base;

import com.jd.etms.vehicle.manager.domain.Vehicle;

/**
 * @author huangliang
 *车辆管理系统（VMS）: 测试分组：VMS-TEST，线上分组： VMS-JSF
 */
public interface VmsManager {

	/**
	 * 
	 * @param vehicle
	 * @return
	 * @throws Exception
	 */
	public abstract Vehicle getVehicleInfoByNumber(Vehicle vehicle) throws Exception;

	/**
	 * 
	 * @param vehicle
	 * @return
	 * @throws Exception
	 */
	public abstract Vehicle getVehicleInfoByCode(Vehicle vehicle) throws Exception;

}
