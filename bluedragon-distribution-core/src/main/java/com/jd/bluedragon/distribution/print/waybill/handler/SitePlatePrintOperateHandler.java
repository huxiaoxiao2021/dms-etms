package com.jd.bluedragon.distribution.print.waybill.handler;

import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.service.WayBillPrintRedundanceService;
import com.jd.bluedragon.utils.JsonHelper;

/**
 * 
 * @ClassName: SitePlateWaybillPrintOperateHandler
 * @Description: 站点平台打印操作处理逻辑
 * @author: wuyoude
 * @date: 2018年1月31日 下午5:40:04
 *
 */
public class SitePlatePrintOperateHandler implements Handler<WaybillPrintContext,InterceptResult<String>>{

	@Autowired
	private WayBillPrintRedundanceService wayBillPrintRedundanceService;

	@Override
	public InterceptResult<String> handle(WaybillPrintContext context) {
		InterceptResult<String> result = wayBillPrintRedundanceService.getWaybillPack(context);
		if(result.isSucceed()){
			result.setData(JsonHelper.toJson(context.getWaybill()));
			if(InterceptResult.STATUS_WEAK_PASSED.equals(context.getStatus())){
				result.setStatus(context.getStatus());
				result.setMessage(context.getMessages().get(0));
			}
		}
		return result;
	}
}
