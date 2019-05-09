package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.VehicleManagerService.getVehicleInfoByNumber",mState={JProEnum.TP,JProEnum.FunctionError})
	public Vehicle getVehicleInfoByNumber(Vehicle vehicle) throws Exception{
		return vehicleManagerService.getVehicleInfoByNumber(vehicle);
	}

	@Override
	@JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.VehicleManagerService.getVehicleInfoByCode",mState={JProEnum.TP,JProEnum.FunctionError})
	public Vehicle getVehicleInfoByCode(Vehicle vehicle) throws Exception{
		return vehicleManagerService.getVehicleInfoByCode(vehicle);
	}
}
