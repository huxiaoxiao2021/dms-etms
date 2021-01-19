package com.jd.bluedragon.core.base;

import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.TransportResourceDto;

public interface CarrierQueryWSManager {

    CommonDto<TransportResourceDto> getTransportResourceByTransCode(String capacityCode);
}
