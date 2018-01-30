package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.List;

import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.JsonHelper;

public class WaybillPrintOperateHandler extends InterceptHandler<WaybillPrintContext,String>{
	private List<Handler<WaybillPrintContext,InterceptResult<String>>> preHandlers;
	private List<Handler<WaybillPrintContext,String>> handlers;
	private List<Handler<WaybillPrintContext,String>> afterHandlers;
	
	@Override
	public InterceptResult<String> preHandle(WaybillPrintContext context) {
		InterceptResult<String> interceptResult = new InterceptResult<String>();
		if(preHandlers != null && !preHandlers.isEmpty()){
			for(Handler<WaybillPrintContext,InterceptResult<String>> handler:preHandlers){
				if(handler instanceof InterceptHandler){
					InterceptResult<String> InterceptResult = handler.handle(context);
					if(InterceptResult != null && InterceptResult.isPassed()){
						if(InterceptResult.isWeakPassed()){
							context.appendMessage(InterceptResult.getMessage());
						}
					}else{
						return InterceptResult;
					}
				}else{
					handler.handle(context);
				}
			}
		}
		return interceptResult;
	}
	@Override
	public InterceptResult<String> doHandle(WaybillPrintContext context) {
		InterceptResult<String> interceptResult = new InterceptResult<String>();
		if(handlers != null && !handlers.isEmpty()){
			for(Handler<WaybillPrintContext,String> handler:handlers){
				if(handler instanceof InterceptHandler){
					InterceptResult<String> InterceptResult = ((InterceptHandler)handler).handle(context);
					if(InterceptResult != null && InterceptResult.isPassed()){
						if(InterceptResult.isWeakPassed()){
							context.appendMessage(InterceptResult.getMessage());
						}
					}else{
						return InterceptResult;
					}
				}else{
					handler.handle(context);
				}
			}
		}
		interceptResult.setData(JsonHelper.toJson(context.getResponse()));
		interceptResult.toSuccess();
		return interceptResult;
	}
	@Override
	public void afterHandle(WaybillPrintContext context) {
		if(afterHandlers != null && !afterHandlers.isEmpty()){
			for(Handler<WaybillPrintContext,String> handler:afterHandlers){
				handler.handle(context);
			}
		}
	}
	/**
	 * @return the preHandlers
	 */
	public List<Handler<WaybillPrintContext,InterceptResult<String>>> getPreHandlers() {
		return preHandlers;
	}
	/**
	 * @param preHandlers the preHandlers to set
	 */
	public void setPreHandlers(
			List<Handler<WaybillPrintContext,InterceptResult<String>>> preHandlers) {
		this.preHandlers = preHandlers;
	}
	/**
	 * @return the handlers
	 */
	public List<Handler<WaybillPrintContext, String>> getHandlers() {
		return handlers;
	}
	/**
	 * @param handlers the handlers to set
	 */
	public void setHandlers(List<Handler<WaybillPrintContext, String>> handlers) {
		this.handlers = handlers;
	}
	/**
	 * @return the afterHandlers
	 */
	public List<Handler<WaybillPrintContext, String>> getAfterHandlers() {
		return afterHandlers;
	}
	/**
	 * @param afterHandlers the afterHandlers to set
	 */
	public void setAfterHandlers(
			List<Handler<WaybillPrintContext, String>> afterHandlers) {
		this.afterHandlers = afterHandlers;
	}
}
