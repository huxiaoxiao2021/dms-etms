package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.coldchain.fulfillment.ot.api.dto.waybill.ColdChainReverseRequest;
import com.jd.coldchain.fulfillment.ot.api.dto.waybill.ColdChainReverseResult;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResult;

/**
 * @author : caozhixing
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.core.base
 * @Description: 冷链逆向单接口
 * @date Date : 2021年12月22日 14:30
 */
public interface ColdChainReverseManager {
    boolean checkColdReverseProductType(String waybillCode);
    ColdChainReverseRequest makeColdChainReverseRequest(ExchangeWaybillDto exchangeWaybillDto);
    WaybillReverseResult createReverseWbOrder(ColdChainReverseRequest coldChainReverseRequest,StringBuilder errorMessage);
}
