package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.Map;

import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.command.JsonCommandHandlerMapping;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.utils.JsonHelper;

/**
 * 
 * @ClassName: WaybillPrintHandler
 * @Description: 包裹标签打印处理接口
 * @author: wuyoude
 * @date: 2018年1月25日 下午10:55:59
 *
 */
public class WaybillPrintHandlerMapping implements JsonCommandHandlerMapping<Integer,WaybillPrintContext,String>{
	private Map<Integer,Handler<WaybillPrintContext,String>> handlerMap;
	
	@Override
	public WaybillPrintContext parserCommand(String jsonStr) {
		WaybillPrintContext context = new WaybillPrintContext();
		context.setRequest(JsonHelper.fromJson(jsonStr, WaybillPrintRequest.class));
		return context;
	}
	@Override
	public Handler<WaybillPrintContext, String> getHandler(Integer k) {
		return handlerMap.get(k);
	}
	/**
	 * @return the handlerMap
	 */
	public Map<Integer, Handler<WaybillPrintContext, String>> getHandlerMap() {
		return handlerMap;
	}
	/**
	 * @param handlerMap the handlerMap to set
	 */
	public void setHandlerMap(
			Map<Integer, Handler<WaybillPrintContext, String>> handlerMap) {
		this.handlerMap = handlerMap;
	}
	
}
