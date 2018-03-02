package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.service.WayBillPrintRedundanceService;
import com.jd.bluedragon.utils.JsonHelper;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @ClassName: SitePlatePrintResultHandler
 * @Description: 站点平台打印-结果处理
 * @author: wuyoude
 * @date: 2018年2月6日 上午9:03:59
 *
 */
public class SitePlatePrintResultHandler implements Handler<WaybillPrintContext,InterceptResult<String>>{

	@Autowired
	private WayBillPrintRedundanceService wayBillPrintRedundanceService;

	@Override
	public InterceptResult<String> handle(WaybillPrintContext context) {
		InterceptResult<String> result = context.getResult();
		if(result.isSucceed()){
			result.setData(JsonHelper.toJson(context.getWaybill()));
		}
		return result;
	}
}
