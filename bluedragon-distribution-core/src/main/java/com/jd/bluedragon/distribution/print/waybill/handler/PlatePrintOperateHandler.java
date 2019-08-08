package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.utils.JsonHelper;

/**
 * @ClassName: PlatePrintOperateHandler
 * @Description: 补打、平台打印、换单打印、批量补打等操作
 * @author: wuyoude
 * @date: 2018年2月6日 上午9:10:00
 */
public class PlatePrintOperateHandler extends AbstractPrintOperateHandler{
	/**
	 * 处理打印结果，返回 context.getResponse()的json格式
	 * @param context
	 * @return
	 */
	@Override
	public String dealPrintResult(WaybillPrintContext context) {
		return JsonHelper.toJson(context.getResponse());
	}
}
