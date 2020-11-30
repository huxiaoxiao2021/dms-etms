package com.jd.bluedragon.core.base;

import com.jd.tms.basic.dto.TransportResourceDto;

import java.util.List;

public interface BasicSelectWsManager {

     List<TransportResourceDto> queryPageTransportResourceWithNodeId(TransportResourceDto transportResourceDto);

}
