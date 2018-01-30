package com.jd.bluedragon.distribution.print.waybill.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.service.ComposeService;
/**
 * 
 * @ClassName: PromiseWaybillHandler
 * @Description: 时效处理逻辑单元
 * @author: wuyoude
 * @date: 2018年1月29日 上午10:54:45
 *
 */
@Service
public class PromiseWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(PromiseWaybillHandler.class);
	@Autowired
	@Qualifier("promiseComposeService")
	private ComposeService promiseComposeService;
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("获取时效信息");
		JdResult<String> jdResult = new JdResult<String>() ;
		Integer dmsCode = context.getRequest().getDmsSiteCode();
		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
		promiseComposeService.handle(context.getResponse(), dmsCode, targetSiteCode);
		jdResult.toSuccess();
		return jdResult;
	}
}
