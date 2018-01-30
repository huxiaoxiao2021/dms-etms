package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
@Service
public class TemplateSelectorWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(TemplateSelectorWaybillHandler.class);

	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("模板选择处理");
		context.getResponse().setTemplateName("nopaper15");
		return new JdResult<String>();
	}
}
