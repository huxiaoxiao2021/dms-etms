package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.service.WayBillPrintRedundanceService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @ClassName: SitePlateWaybillPrintOperateHandler
 * @Description: 站点平台打印操作处理逻辑
 * @author: wuyoude
 * @date: 2018年1月31日 下午5:40:04
 *
 */
public class SitePlateWaybillPrintOperateHandler implements Handler<WaybillPrintContext,InterceptResult<String>>{

	@Autowired
	private WayBillPrintRedundanceService wayBillPrintRedundanceService;

	@Override
	public InterceptResult<String> handle(WaybillPrintContext context) {
		return wayBillPrintRedundanceService.getWaybillPack(context);
	}
}
