package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.WaybillPrintResponse;
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
		InterceptResult<String> interceptResult = new InterceptResult<String>();
		logger.info("加载运单基本信息");
		Integer dmsCode = context.getRequest().getDmsSiteCode();
		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
		WaybillPrintResponse waybillPrintResponse = waybillPrintService.loadBasicWaybillInfo(dmsCode, context.getRequest().getBarCode(), targetSiteCode);
		if(waybillPrintResponse!=null){
			context.setResponse(waybillPrintResponse);
			interceptResult.toSuccess();
		}else{
			interceptResult.toFail(WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.getMsgCode(),WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.formatMsg());
		}
		return interceptResult;
	}
	@Override
	public InterceptResult<String> doHandle(WaybillPrintContext context) {
		return new InterceptResult<String>();
	}
	@Override
	public void afterHandle(WaybillPrintContext context) {
		
	}
}
