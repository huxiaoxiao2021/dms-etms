package com.jd.bluedragon.core.base;

import com.jd.tms.basic.dto.BasicDictDto;
import com.jd.tms.basic.dto.CarrierDto;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.TransportResourceDto;

import java.util.List;

public interface BasicSelectWsManager {

     List<TransportResourceDto> queryPageTransportResourceWithNodeId(TransportResourceDto transportResourceDto);

     List<CarrierDto> getCarrierInfoList(CarrierDto carrierDto);

}
