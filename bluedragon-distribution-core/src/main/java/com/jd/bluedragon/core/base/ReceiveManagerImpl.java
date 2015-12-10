package com.jd.bluedragon.core.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.etms.receive.api.request.GrossReturnRequest;
import com.jd.etms.receive.api.response.GrossReturnResponse;
import com.jd.etms.receive.api.saf.GrossReturnSaf;

@Service("receiveManager")
public class ReceiveManagerImpl implements ReceiveManager{

	/**
     * 接货中心换单接口
     */
    @Autowired
    private GrossReturnSaf receiveExchangeService;
	
	@Override
	public GrossReturnResponse queryDeliveryIdByFcode(String fCode) throws Exception{
		return receiveExchangeService.queryDeliveryIdByFcode(fCode);
	}

	@Override
	public GrossReturnResponse generateGrossReturnWaybill(GrossReturnRequest grossReturnRequest) throws Exception{
		return receiveExchangeService.generateGrossReturnWaybill(grossReturnRequest);
	}
}
