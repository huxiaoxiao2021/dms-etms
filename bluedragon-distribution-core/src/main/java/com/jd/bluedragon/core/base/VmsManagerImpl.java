package com.jd.bluedragon.core.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.etms.vehicle.manager.domain.Vehicle;
import com.jd.etms.vehicle.manager.service.saf.VehicleManagerService;

@Service("vmsManager")
public class VmsManagerImpl implements VmsManager{

    @Autowired
    @Qualifier("vehicleManagerService")
    VehicleManagerService vehicleManagerService;
	
	@Override
	public Vehicle getVehicleInfoByNumber(Vehicle vehicle) throws Exception{
		return vehicleManagerService.getVehicleInfoByNumber(vehicle);
	}

	@Override
	public Vehicle getVehicleInfoByCode(Vehicle vehicle) throws Exception{
		return vehicleManagerService.getVehicleInfoByCode(vehicle);
	}
}
