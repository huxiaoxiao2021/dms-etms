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
 * @ClassName: InterceptWaybillHandler
 * @Description: 取消锁定拦截处理
 * @author: wuyoude
 * @date: 2018年1月30日 上午9:18:31
 */
@Service
public class InterceptWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(InterceptWaybillHandler.class);
	
	@Autowired
	@Qualifier("interceptComposeService")
	private ComposeService interceptComposeService;
	
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("包裹标签打印-取消锁定拦截处理");
		Integer dmsCode = context.getRequest().getDmsSiteCode();
		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
		interceptComposeService.handle(context.getResponse(), dmsCode, targetSiteCode);
		return context.getResult();
	}
}
