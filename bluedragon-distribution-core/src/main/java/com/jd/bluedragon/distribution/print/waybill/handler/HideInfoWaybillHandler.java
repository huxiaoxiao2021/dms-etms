package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.print.service.HideInfoService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
/**
 * 
 * @ClassName: HideInfoWaybillHandler
 * @Description: 包裹标签打印-微笑面单处理逻辑
 * @author: wuyoude
 * @date: 2018年1月30日 上午9:18:31
 */
@Service
public class HideInfoWaybillHandler implements Handler<WaybillPrintContext,JdResult<String>>{
	private static final Log logger= LogFactory.getLog(HideInfoWaybillHandler.class);
	@Autowired
	@Qualifier("hideInfoService")
	private HideInfoService hideInfoService;

	@Override
	public JdResult<String> handle(WaybillPrintContext context) {
		logger.info("包裹标签打印-微笑面单-隐藏电话和地址");
		String waybillSign = "";
		if(context.getBigWaybillDto() !=null && context.getBigWaybillDto().getWaybill()!=null){
			waybillSign=context.getBigWaybillDto().getWaybill().getWaybillSign();
		}
		//hideInfoService.setHideInfo(waybillSign,context.getResponse());
		return context.getResult();
	}
}
