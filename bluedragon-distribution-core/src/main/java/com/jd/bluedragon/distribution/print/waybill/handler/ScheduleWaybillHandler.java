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
 * @ClassName: ScheduleWaybillHandler
 * @Description: 包裹标签打印-重新调度处理
 * @author: wuyoude
 * @date: 2018年2月5日 下午5:10:42
 *
 */
@Service
public class ScheduleWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(ScheduleWaybillHandler.class);
	
	@Autowired
	@Qualifier("scheduleComposeService")
	private ComposeService scheduleComposeService;
	
	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("包裹标签打印-重新调度处理");
//		Integer dmsCode = context.getRequest().getDmsSiteCode();
//		Integer targetSiteCode = context.getRequest().getTargetSiteCode();
//		scheduleComposeService.handle(context.getResponse(), dmsCode, targetSiteCode);
		return context.getResult();
	}
}
