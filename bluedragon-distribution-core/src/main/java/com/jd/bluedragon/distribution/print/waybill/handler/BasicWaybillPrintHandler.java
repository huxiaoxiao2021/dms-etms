package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.service.WaybillPrintService;
@Service
public class BasicWaybillPrintHandler extends InterceptHandler<WaybillPrintContext,String>{
	private static final Log logger= LogFactory.getLog(BasicWaybillPrintHandler.class);
    @Autowired
    private WaybillPrintService waybillPrintService;
	@Override
	public InterceptResult<String> preHandle(WaybillPrintContext context) {
		return waybillPrintService.loadBasicWaybillInfo(context);
	}
	@Override
	public InterceptResult<String> doHandle(WaybillPrintContext context) {
		InterceptResult<String> jdResult = new InterceptResult<String>() ;
		jdResult.toSuccess();
		return jdResult;
	}
	@Override
	public void afterHandle(WaybillPrintContext context) {
		
	}
}
