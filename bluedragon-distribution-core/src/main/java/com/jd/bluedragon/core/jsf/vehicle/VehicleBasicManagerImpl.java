package com.jd.bluedragon.core.jsf.vehicle;

import com.jdl.basic.api.domain.vehicle.VehicleVolumeDicReq;
import com.jdl.basic.api.domain.vehicle.VehicleVolumeDicResp;
import com.jdl.basic.api.service.vehicle.VehicleVolumeDicJsfService;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VehicleBasicManagerImpl implements VehicleBasicManager{

  @Autowired
  VehicleVolumeDicJsfService vehicleVolumeDicJsfService;


  @Override
  public VehicleVolumeDicResp queryVolumeByVehicleType(VehicleVolumeDicReq req) {
    try {
      Result<VehicleVolumeDicResp> result= vehicleVolumeDicJsfService.queryVolumeByVehicleType(req);
      if (result != null && result.isSuccess()){
        return result.getData();
      }
    } catch (Exception e) {
      log.error("调用jy基础资料获取车型容量数据异常",e);
    }
    return null;
  }
}
