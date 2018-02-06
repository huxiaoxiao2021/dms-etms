package com.jd.bluedragon.distribution.command;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.command.handler.AbstractJsonCommandHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintContext;
import com.jd.bluedragon.utils.JsonHelper;

/**
 * 
 * @ClassName: WaybillPrintCommandHandler
 * @Description: 包裹标签打印-指令处理逻辑单元
 * @author: wuyoude
 * @date: 2018年1月25日 下午10:55:59
 *
 */
public class WaybillPrintCommandHandler extends AbstractJsonCommandHandler<WaybillPrintContext,InterceptResult<String>>{
	/**
	 * 将json请求内容转换为WaybillPrintContext对象
	 */
	@Override
	public WaybillPrintContext fromJson(String jsonData) {
		WaybillPrintContext context = new WaybillPrintContext();
		//初始化请求信息和返回结果信息
		InterceptResult<String> result = new InterceptResult<String>();
		result.toSuccess();
		context.setRequest(JsonHelper.fromJson(jsonData, WaybillPrintRequest.class));
		context.setResult(result);
		return context;
	}
}
