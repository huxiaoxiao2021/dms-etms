package com.jd.bluedragon.distribution.jy.manager;

import com.jdl.basic.api.domain.vehicle.VehicleIntegralConfig;

public interface VehicleIntegralConfigJsfManager {

    VehicleIntegralConfig findConfigByVehicleType(Integer vehicleType);

}
