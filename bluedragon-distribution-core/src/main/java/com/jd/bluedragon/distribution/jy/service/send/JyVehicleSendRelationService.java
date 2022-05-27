package com.jd.bluedragon.distribution.jy.service.send;

import java.util.List;

public interface JyVehicleSendRelationService {
    List<String> querySendCodesByVehicleDetailBizId(String vehicleDetailBizId);
}
