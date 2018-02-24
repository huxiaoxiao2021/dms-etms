package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
@Service
public class BasicWaybillPrintHandler implements InterceptHandler<WaybillPrintContext,String>{
	private static final Log logger= LogFactory.getLog(BasicWaybillPrintHandler.class);
    @Autowired
    private WaybillPrintService waybillPrintService;
    
	@Override
	public InterceptResult<String> handle(WaybillPrintContext context) {
		return waybillPrintService.loadBasicWaybillInfo(context);
	}
}
