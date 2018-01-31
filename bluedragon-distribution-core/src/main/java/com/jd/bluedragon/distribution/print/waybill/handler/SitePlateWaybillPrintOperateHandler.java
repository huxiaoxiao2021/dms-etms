package com.jd.bluedragon.distribution.print.waybill.handler;

import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
/**
 * 
 * @ClassName: SitePlateWaybillPrintOperateHandler
 * @Description: 站点平台打印操作处理逻辑
 * @author: wuyoude
 * @date: 2018年1月31日 下午5:40:04
 *
 */
public class SitePlateWaybillPrintOperateHandler implements Handler<WaybillPrintContext,InterceptResult<String>>{

	@Override
	public InterceptResult<String> handle(WaybillPrintContext target) {
		InterceptResult<String> interceptResult = new InterceptResult<String>();
		interceptResult.toSuccess();
		return interceptResult;
	}
}
