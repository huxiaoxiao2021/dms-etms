package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.service.ComposeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
/**
 * 
 * @ClassName: InterceptWaybillHandler
 * @Description: 取消锁定拦截处理
 * @author: wuyoude
 * @date: 2018年1月30日 上午9:18:31
 */
@Service
public class InterceptWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Logger log = LoggerFactory.getLogger(InterceptWaybillHandler.class);
	
	@Autowired
	@Qualifier("interceptComposeService")
	private ComposeService interceptComposeService;
	
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		log.debug("包裹标签打印-取消锁定拦截处理");
		Integer dmsCode = context.getRequest().getDmsSiteCode();
		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
		interceptComposeService.handle(context.getResponse(), dmsCode, targetSiteCode);
		return context.getResult();
	}
}
