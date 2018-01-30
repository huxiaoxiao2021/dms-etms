package com.jd.bluedragon.distribution.print.waybill.handler;

import java.util.List;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.InterceptHandler;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.utils.JsonHelper;

public class WaybillPrintOperateHandler extends InterceptHandler<WaybillPrintContext,String>{
	private List<Handler<WaybillPrintContext,JdResult<String>>> preHandlers;
	private List<Handler<WaybillPrintContext,JdResult<String>>> handlers;
	private List<Handler<WaybillPrintContext,JdResult<String>>> afterHandlers;
	
	@Override
	public InterceptResult<String> preHandle(WaybillPrintContext context) {
		InterceptResult<String> interceptResult = new InterceptResult<String>();
		if(preHandlers != null && !preHandlers.isEmpty()){
			for(Handler<WaybillPrintContext,JdResult<String>> handler:preHandlers){
				if(handler instanceof InterceptHandler){
					InterceptResult<String> interceptResult0 = ((InterceptHandler)handler).handle(context);
					if(interceptResult0 != null && interceptResult0.isPassed()){
						if(interceptResult0.isWeakPassed()){
							context.appendMessage(interceptResult0.getMessage());
							context.setStatus(interceptResult0.getStatus());
						}
					}else{
						return interceptResult0;
					}
				}else{
					JdResult<String> jdResult = handler.handle(context);
					if(jdResult == null || jdResult.getCode()!=JdResult.CODE_SUC){
						context.appendMessage(jdResult.getMessage());
						context.setStatus(InterceptResult.STATUS_NO_PASSED);
						interceptResult.toFail(jdResult.getMessageCode(), jdResult.getMessage());
						return interceptResult;
					}
				}
			}
		}
		return interceptResult;
	}
	@Override
	public InterceptResult<String> doHandle(WaybillPrintContext context) {
		InterceptResult<String> interceptResult = new InterceptResult<String>();
		if(handlers != null && !handlers.isEmpty()){
			for(Handler<WaybillPrintContext,JdResult<String>> handler:handlers){
				if(handler instanceof InterceptHandler){
					InterceptResult<String> InterceptResult = ((InterceptHandler)handler).handle(context);
					if(InterceptResult != null && InterceptResult.isPassed()){
						if(InterceptResult.isWeakPassed()){
							context.appendMessage(InterceptResult.getMessage());
							context.setStatus(InterceptResult.getStatus());
						}
					}else{
						return InterceptResult;
					}
				}else{
					JdResult<String> jdResult = handler.handle(context);
					if(jdResult == null || jdResult.getCode()!=JdResult.CODE_SUC){
						context.appendMessage(jdResult.getMessage());
						context.setStatus(InterceptResult.STATUS_NO_PASSED);
						interceptResult.toFail(jdResult.getMessageCode(), jdResult.getMessage());
						return interceptResult;
					}
				}
			}
		}
		interceptResult.toSuccess();
		interceptResult.setData(JsonHelper.toJson(context.getResponse()));
		if(context.getStatus()==InterceptResult.STATUS_WEAK_PASSED){
			interceptResult.setStatus(context.getStatus());
			interceptResult.setMessage(context.getMessages().get(0));
			interceptResult.setCode(JdResult.CODE_SUC);
		}
		return interceptResult;
	}
	@Override
	public void afterHandle(WaybillPrintContext context) {
		if(afterHandlers != null && !afterHandlers.isEmpty()){
			for(Handler<WaybillPrintContext,JdResult<String>> handler:afterHandlers){
				handler.handle(context);
			}
		}
	}
	/**
	 * @return the preHandlers
	 */
	public List<Handler<WaybillPrintContext, JdResult<String>>> getPreHandlers() {
		return preHandlers;
	}
	/**
	 * @param preHandlers the preHandlers to set
	 */
	public void setPreHandlers(
			List<Handler<WaybillPrintContext, JdResult<String>>> preHandlers) {
		this.preHandlers = preHandlers;
	}
	/**
	 * @return the handlers
	 */
	public List<Handler<WaybillPrintContext, JdResult<String>>> getHandlers() {
		return handlers;
	}
	/**
	 * @param handlers the handlers to set
	 */
	public void setHandlers(
			List<Handler<WaybillPrintContext, JdResult<String>>> handlers) {
		this.handlers = handlers;
	}
	/**
	 * @return the afterHandlers
	 */
	public List<Handler<WaybillPrintContext, JdResult<String>>> getAfterHandlers() {
		return afterHandlers;
	}
	/**
	 * @param afterHandlers the afterHandlers to set
	 */
	public void setAfterHandlers(
			List<Handler<WaybillPrintContext, JdResult<String>>> afterHandlers) {
		this.afterHandlers = afterHandlers;
	}
}
