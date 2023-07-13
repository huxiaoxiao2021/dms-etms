package com.jd.bluedragon.core.jsf.vehicle;

import com.jdl.basic.api.domain.vehicle.VehicleVolumeDicReq;
import com.jdl.basic.api.domain.vehicle.VehicleVolumeDicResp;
import java.math.BigDecimal;

public interface VehicleBasicManager {

  VehicleVolumeDicResp queryVolumeByVehicleType(VehicleVolumeDicReq req);

}
