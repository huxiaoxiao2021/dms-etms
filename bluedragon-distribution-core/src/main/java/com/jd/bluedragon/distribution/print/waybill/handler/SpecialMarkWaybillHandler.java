package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.service.ComposeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
@Service
public class SpecialMarkWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Logger log = LoggerFactory.getLogger(SpecialMarkWaybillHandler.class);
	@Autowired
	@Qualifier("specialMarkComposeService")
	private ComposeService specialMarkComposeService;
	
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		log.debug("包裹标签打印-特殊标记合成");
		Integer dmsCode = context.getRequest().getDmsSiteCode();
		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
		specialMarkComposeService.handle(context.getResponse(), dmsCode, targetSiteCode);
		return context.getResult();
	}
}
