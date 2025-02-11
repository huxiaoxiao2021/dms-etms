package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.reverse.domain.BdInboundECLPDto;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundOrder;

public interface ReverseSpareEclp {



    InboundOrder makeInboundOrder(String waybillCode, SendDetail sendDetail);

    boolean checkIsPureMatch(String waybillCode,String waybillSign,InvokeResult<Boolean> result);
}
