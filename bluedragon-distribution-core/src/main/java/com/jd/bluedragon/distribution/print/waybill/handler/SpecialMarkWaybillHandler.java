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
public class SpecialMarkWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(SpecialMarkWaybillHandler.class);
	@Autowired
	@Qualifier("specialMarkComposeService")
	private ComposeService specialMarkComposeService;
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		JdResult<String> jdResult = new JdResult<String>() ;
		logger.info("运单特殊标记合成");
		Integer dmsCode = context.getRequest().getDmsSiteCode();
		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
		specialMarkComposeService.handle(context.getResponse(), dmsCode, targetSiteCode);
		jdResult.toSuccess();
		return jdResult;
	}
}
