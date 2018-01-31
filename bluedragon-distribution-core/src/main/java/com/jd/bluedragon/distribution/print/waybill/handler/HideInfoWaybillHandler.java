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
 * @ClassName: HideInfoWaybillHandler
 * @Description: 微笑面单处理逻辑
 * @author: wuyoude
 * @date: 2018年1月30日 上午9:18:31
 */
@Service
public class HideInfoWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(HideInfoWaybillHandler.class);
	@Autowired
	@Qualifier("hideInfoComposeService")
	private ComposeService hideInfoComposeService;
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("微笑面单-隐藏电话和地址");
		JdResult<String> jdResult = new JdResult<String>() ;
		Integer dmsCode = context.getRequest().getDmsSiteCode();
		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
		hideInfoComposeService.handle(context.getResponse(), dmsCode, targetSiteCode);
		jdResult.toSuccess();
		return jdResult;
	}
}
