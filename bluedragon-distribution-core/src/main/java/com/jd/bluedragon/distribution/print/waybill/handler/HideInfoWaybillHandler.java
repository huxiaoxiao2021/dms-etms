package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.service.ComposeService;
@Service
public class HideInfoWaybillHandler implements Handler<WaybillPrintContext,String>{
	private static final Log logger= LogFactory.getLog(HideInfoWaybillHandler.class);
	@Autowired
	@Qualifier("hideInfoComposeService")
	private ComposeService hideInfoComposeService;
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("微笑面单-隐藏电话和地址");
		Integer dmsCode = context.getRequest().getDmsSiteCode();
		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
		hideInfoComposeService.handle(context.getResponse(), dmsCode, targetSiteCode);
		return new JdResult<String>();
	}
}
