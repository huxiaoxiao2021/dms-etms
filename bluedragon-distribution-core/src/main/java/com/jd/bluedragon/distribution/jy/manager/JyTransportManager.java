package com.jd.bluedragon.distribution.jy.manager;

import com.jd.tms.basic.dto.BasicDictDto;
import com.jd.tms.basic.dto.BasicVehicleTypeDto;
import com.jd.tms.basic.dto.CommonDto;

import java.util.List;

public interface JyTransportManager {
    CommonDto<List<BasicDictDto>> getVehicleTypeByType(String owner, Integer type);

    CommonDto<BasicVehicleTypeDto> getVehicleTypeByVehicleNum(String vehicleNum) throws Exception;

    CommonDto<List<BasicVehicleTypeDto>> getVehicleTypeList();

}
