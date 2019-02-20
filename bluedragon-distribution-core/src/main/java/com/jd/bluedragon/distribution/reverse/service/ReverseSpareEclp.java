package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.reverse.domain.BdInboundECLPDto;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundOrder;

public interface ReverseSpareEclp {

    BdInboundECLPDto makeEclpMessage(String waybillCode,SendDetail sendDetail);

    InboundOrder createInboundOrder(String waybillCode, SendDetail sendDetail);
}
