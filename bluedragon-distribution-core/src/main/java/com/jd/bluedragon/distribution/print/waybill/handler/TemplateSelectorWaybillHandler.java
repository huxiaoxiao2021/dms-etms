package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.print.service.TemplateSelectService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
@Service
public class TemplateSelectorWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(TemplateSelectorWaybillHandler.class);
	@Autowired
	@Qualifier("templateSelectService")
	private TemplateSelectService templateSelectService;

	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("获取包裹标签版本");
		String templateName = templateSelectService.handle(context);
		context.getBasePrintWaybill().setTemplateName(templateName);
		return context.getResult();
	}
}
