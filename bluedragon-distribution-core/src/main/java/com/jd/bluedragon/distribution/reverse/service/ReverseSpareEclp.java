package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.reverse.domain.BdInboundECLPDto;
import com.jd.bluedragon.distribution.send.domain.SendDetail;

public interface ReverseSpareEclp {

    BdInboundECLPDto makeEclpMessage(String waybillCode,SendDetail sendDetail);
}
